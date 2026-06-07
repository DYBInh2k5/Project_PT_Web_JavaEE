// ===== DAO Hóa đơn (InvoiceDAO) — CRUD cho HoaDon và ChiTietHoaDon =====
// Quan trọng: có xử lý kho (trừ số lượng tồn) và tính tổng tiền
package com.project.dao;

import com.project.model.Invoice;
import com.project.model.InvoiceItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InvoiceDAO {

    // === Lớp NewInvoiceItem: đại diện cho một dòng sản phẩm sẽ thêm vào hóa đơn mới ===
    // Mỗi đối tượng giữ mã sách (maSach) và số lượng mua (soLuong)
    public static class NewInvoiceItem {
        private final int maSach;
        private final int soLuong;

        public NewInvoiceItem(int maSach, int soLuong) {
            this.maSach = maSach;
            this.soLuong = soLuong;
        }

        public int getMaSach() {
            return maSach;
        }

        public int getSoLuong() {
            return soLuong;
        }
    }

    // === findAll: Lấy danh sách tất cả hóa đơn (JOIN với KhachHang để lấy tên khách) ===
    // Sắp xếp theo MaHD giảm dần (hóa đơn mới nhất lên đầu)
    public List<Invoice> findAll() {
        String sql = "SELECT hd.MaHD, hd.MaNV, hd.MaKH, hd.NgayLap, hd.TongTien, hd.GiamGia, hd.ThueVAT, hd.TrangThai, kh.TenKH "
                + "FROM dbo.HoaDon hd "
                + "LEFT JOIN dbo.KhachHang kh ON hd.MaKH = kh.MaKH "
                + "ORDER BY hd.MaHD DESC";

        EntityManager em = JpaSupport.createEntityManager();
        try {
            List<Object[]> rows = em.createNativeQuery(sql).getResultList();
            List<Invoice> invoices = new ArrayList<Invoice>(rows.size());
            for (Object[] row : rows) {
                invoices.add(mapInvoiceRow(row));
            }
            return invoices;
        } finally {
            em.close();
        }
    }

    // === findById: Tìm một hóa đơn theo mã (MaHD) ===
    // Trả về null nếu không tìm thấy
    public Invoice findById(int maHD) {
        String sql = "SELECT hd.MaHD, hd.MaNV, hd.MaKH, hd.NgayLap, hd.TongTien, hd.GiamGia, hd.ThueVAT, hd.TrangThai, kh.TenKH "
                + "FROM dbo.HoaDon hd "
                + "LEFT JOIN dbo.KhachHang kh ON hd.MaKH = kh.MaKH "
                + "WHERE hd.MaHD = ?";

        EntityManager em = JpaSupport.createEntityManager();
        try {
            List<Object[]> rows = em.createNativeQuery(sql)
                    .setParameter(1, maHD)
                    .getResultList();
            return rows.isEmpty() ? null : mapInvoiceRow(rows.get(0));
        } finally {
            em.close();
        }
    }

    // === findByCustomerId: Lấy danh sách hóa đơn theo mã khách hàng (MaKH) ===
    // Sắp xếp giảm dần theo MaHD
    public List<Invoice> findByCustomerId(int maKH) {
        String sql = "SELECT hd.MaHD, hd.MaNV, hd.MaKH, hd.NgayLap, hd.TongTien, hd.GiamGia, hd.ThueVAT, hd.TrangThai, kh.TenKH "
                + "FROM dbo.HoaDon hd "
                + "LEFT JOIN dbo.KhachHang kh ON hd.MaKH = kh.MaKH "
                + "WHERE hd.MaKH = ? "
                + "ORDER BY hd.MaHD DESC";

        EntityManager em = JpaSupport.createEntityManager();
        try {
            List<Object[]> rows = em.createNativeQuery(sql)
                    .setParameter(1, maKH)
                    .getResultList();
            List<Invoice> invoices = new ArrayList<>();
            for (Object[] row : rows) {
                invoices.add(mapInvoiceRow(row));
            }
            return invoices;
        } finally {
            em.close();
        }
    }

    // === findByIdForCustomerLookup: Tra cứu hóa đơn an toàn có xác thực SĐT/Email ===
    // Chỉ trả về hóa đơn nếu SĐT hoặc Email khớp với thông tin khách hàng
    // Dùng để ngăn người lạ xem hóa đơn của người khác
    public Invoice findByIdForCustomerLookup(int maHD, String phone, String email) {
        boolean hasPhone = phone != null && !phone.trim().isEmpty();
        boolean hasEmail = email != null && !email.trim().isEmpty();
        if (!hasPhone && !hasEmail) {
            return null;
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT hd.MaHD, hd.MaNV, hd.MaKH, hd.NgayLap, hd.TongTien, hd.GiamGia, hd.ThueVAT, hd.TrangThai, ");
        sql.append("kh.TenKH, kh.DienThoai, kh.Email, kh.DiaChi ");
        sql.append("FROM dbo.HoaDon hd ");
        sql.append("LEFT JOIN dbo.KhachHang kh ON hd.MaKH = kh.MaKH ");
        sql.append("WHERE hd.MaHD = ? AND hd.MaKH IS NOT NULL AND (");
        if (hasPhone && hasEmail) {
            sql.append("kh.DienThoai = ? OR kh.Email = ?");
        } else if (hasPhone) {
            sql.append("kh.DienThoai = ?");
        } else {
            sql.append("kh.Email = ?");
        }
        sql.append(")");

        EntityManager em = JpaSupport.createEntityManager();
        try {
            var query = em.createNativeQuery(sql.toString());
            query.setParameter(1, maHD);
            if (hasPhone && hasEmail) {
                query.setParameter(2, phone.trim());
                query.setParameter(3, email.trim());
            } else if (hasPhone) {
                query.setParameter(2, phone.trim());
            } else {
                query.setParameter(2, email.trim());
            }

            List<Object[]> rows = query.getResultList();
            return rows.isEmpty() ? null : mapInvoiceRow(rows.get(0));
        } finally {
            em.close();
        }
    }

    // === findByIds: Lấy nhiều hóa đơn cùng lúc theo danh sách mã (IN query) ===
    // Giữ nguyên thứ tự theo danh sách đầu vào (dùng LinkedHashMap)
    public List<Invoice> findByIds(List<Integer> invoiceIds) {
        if (invoiceIds == null || invoiceIds.isEmpty()) {
            return Collections.emptyList();
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT hd.MaHD, hd.MaNV, hd.MaKH, hd.NgayLap, hd.TongTien, hd.GiamGia, hd.ThueVAT, hd.TrangThai, ");
        sql.append("kh.TenKH, kh.DienThoai, kh.Email, kh.DiaChi ");
        sql.append("FROM dbo.HoaDon hd ");
        sql.append("LEFT JOIN dbo.KhachHang kh ON hd.MaKH = kh.MaKH ");
        sql.append("WHERE hd.MaHD IN (");
        for (int i = 0; i < invoiceIds.size(); i++) {
            if (i > 0) {
                sql.append(",");
            }
            sql.append("?");
        }
        sql.append(")");

        EntityManager em = JpaSupport.createEntityManager();
        try {
            var query = em.createNativeQuery(sql.toString());
            int idx = 1;
            for (Integer id : invoiceIds) {
                query.setParameter(idx++, id == null ? -1 : id);
            }

            List<Object[]> rows = query.getResultList();
            Map<Integer, Invoice> byId = new LinkedHashMap<Integer, Invoice>();
            for (Object[] row : rows) {
                Invoice invoice = mapInvoiceRow(row);
                byId.put(invoice.getMaHD(), invoice);
            }

            List<Invoice> ordered = new ArrayList<Invoice>();
            for (Integer id : invoiceIds) {
                if (id != null && byId.containsKey(id)) {
                    ordered.add(byId.get(id));
                }
            }
            return ordered;
        } finally {
            em.close();
        }
    }

    // === findItemsByInvoiceId: Lấy danh sách chi tiết (dòng) của một hóa đơn ===
    // JOIN với Sach để lấy tên sách, sắp xếp theo MaCT
    public List<InvoiceItem> findItemsByInvoiceId(int maHD) {
        List<InvoiceItem> items = new ArrayList<InvoiceItem>();
        String sql = "SELECT ct.MaCT, ct.MaHD, ct.MaSach, ct.SoLuong, ct.DonGia, ct.ThanhTien, s.TenSach "
                + "FROM dbo.ChiTietHoaDon ct "
                + "LEFT JOIN dbo.Sach s ON ct.MaSach = s.MaSach "
                + "WHERE ct.MaHD = ? ORDER BY ct.MaCT";

        EntityManager em = JpaSupport.createEntityManager();
        try {
            List<Object[]> rows = em.createNativeQuery(sql)
                    .setParameter(1, maHD)
                    .getResultList();
            for (Object[] row : rows) {
                items.add(mapInvoiceItemRow(row));
            }
            return items;
        } finally {
            em.close();
        }
    }

    // === createInvoice: Phương thức chính tạo hóa đơn trong một transaction ===
    // 1. Chèn header hóa đơn -> lấy MaHD
    // 2. Với mỗi dòng: kiểm tra tồn kho, chèn chi tiết, cập nhật số lượng tồn
    // 3. Tính tổng tiền (subTotal - discount + VAT) và cập nhật vào hóa đơn
    // Nếu lỗi -> rollback toàn bộ
    public int createInvoice(Integer maKH, String maNV, BigDecimal giamGia, BigDecimal thueVat, List<NewInvoiceItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Hoa don phai co it nhat 1 dong chi tiet.");
        }

        EntityManager em = JpaSupport.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            int maHD = insertInvoiceHeader(em, maKH, maNV, giamGia, thueVat, "NEW");
            BigDecimal subTotal = BigDecimal.ZERO;

            for (NewInvoiceItem item : items) {
                if (item.getSoLuong() <= 0) {
                    throw new IllegalArgumentException("So luong phai lon hon 0.");
                }

                StockInfo stock = getStockInfoForUpdate(em, item.getMaSach());
                if (stock == null) {
                    throw new IllegalArgumentException("Khong tim thay sach ma " + item.getMaSach());
                }

                if (stock.getSoLuongTon() < item.getSoLuong()) {
                    throw new IllegalArgumentException("So luong ton khong du cho sach ma " + item.getMaSach());
                }

                BigDecimal thanhTien = stock.getDonGia().multiply(BigDecimal.valueOf(item.getSoLuong()));
                insertInvoiceItem(em, maHD, item, stock.getDonGia(), thanhTien);
                updateBookStock(em, item.getMaSach(), stock.getSoLuongTon() - item.getSoLuong());
                subTotal = subTotal.add(thanhTien);
            }

            BigDecimal discount = giamGia == null ? BigDecimal.ZERO : giamGia;
            if (discount.compareTo(subTotal) > 0) {
                discount = subTotal;
            }
            BigDecimal vat = thueVat == null ? BigDecimal.ZERO : thueVat;
            BigDecimal total = subTotal.subtract(discount).add(vat);
            if (total.compareTo(BigDecimal.ZERO) < 0) {
                total = BigDecimal.ZERO;
            }

            updateInvoiceTotals(em, maHD, total, discount, vat);
            tx.commit();
            return maHD;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    // === insertInvoiceHeader: Chèn dòng header vào bảng HoaDon, trả về MaHD tự sinh ===
    // Dùng OUTPUT INSERTED.MaHD để lấy ID vừa tạo từ SQL Server
    private int insertInvoiceHeader(EntityManager em, Integer maKH, String maNV, BigDecimal giamGia, BigDecimal thueVat, String trangThai) {
        String sql = "INSERT INTO dbo.HoaDon (MaNV, MaKH, NgayLap, TongTien, GiamGia, ThueVAT, TrangThai) "
                + "OUTPUT INSERTED.MaHD VALUES (?, ?, GETDATE(), ?, ?, ?, ?)";

        Number generated = (Number) em.createNativeQuery(sql)
                .setParameter(1, maNV == null || maNV.trim().isEmpty() ? null : maNV.trim())
                .setParameter(2, maKH)
                .setParameter(3, BigDecimal.ZERO)
                .setParameter(4, giamGia == null ? BigDecimal.ZERO : giamGia)
                .setParameter(5, thueVat == null ? BigDecimal.ZERO : thueVat)
                .setParameter(6, trangThai == null || trangThai.trim().isEmpty() ? "NEW" : trangThai.trim())
                .getSingleResult();

        if (generated == null) {
            throw new IllegalArgumentException("Khong tao duoc hoa don.");
        }
        return generated.intValue();
    }

    // === updateInvoiceTotals: Cập nhật tổng tiền, giảm giá và thuế sau khi đã tính toán ===
    private void updateInvoiceTotals(EntityManager em, int maHD, BigDecimal tongTien, BigDecimal giamGia, BigDecimal thueVat) {
        String sql = "UPDATE dbo.HoaDon SET TongTien = ?, GiamGia = ?, ThueVAT = ? WHERE MaHD = ?";
        em.createNativeQuery(sql)
                .setParameter(1, tongTien)
                .setParameter(2, giamGia)
                .setParameter(3, thueVat)
                .setParameter(4, maHD)
                .executeUpdate();
    }

    // === updateStatus: Cập nhật trạng thái đơn hàng (NEW / SHIPPED / PAID) ===
    // Chuẩn hóa trạng thái trước khi ghi vào DB
    public void updateStatus(int maHD, String trangThai) {
        String normalized = normalizeStatus(trangThai);
        String sql = "UPDATE dbo.HoaDon SET TrangThai = ? WHERE MaHD = ?";

        EntityManager em = JpaSupport.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery(sql)
                    .setParameter(1, normalized)
                    .setParameter(2, maHD)
                    .executeUpdate();
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    // === normalizeStatus: Chuẩn hóa và kiểm tra trạng thái hóa đơn ===
    // Chỉ chấp nhận: NEW (mới), SHIPPED (đã giao), PAID (đã thanh toán)
    // Mặc định là "NEW" nếu giá trị null hoặc rỗng
    public String normalizeStatus(String trangThai) {
        if (trangThai == null) {
            return "NEW";
        }

        String normalized = trangThai.trim().toUpperCase();
        if (normalized.isEmpty()) {
            return "NEW";
        }
        if ("NEW".equals(normalized) || "SHIPPED".equals(normalized) || "PAID".equals(normalized)) {
            return normalized;
        }
        throw new IllegalArgumentException("Trang thai khong hop le. Chi chap nhan: NEW, SHIPPED, PAID.");
    }

    // === getStockInfoForUpdate: Lấy đơn giá và số lượng tồn của sách (có khóa bi lạc quan) ===
    // Dùng UPDLOCK + ROWLOCK để tránh tranh chấp dữ liệu khi nhiều giao dịch cùng lúc
    private StockInfo getStockInfoForUpdate(EntityManager em, int maSach) {
        String sql = "SELECT DonGia, SoLuong FROM dbo.Sach WITH (UPDLOCK, ROWLOCK) WHERE MaSach = ?";
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter(1, maSach)
                .getResultList();
        if (rows.isEmpty()) {
            return null;
        }

        Object[] row = rows.get(0);
        BigDecimal donGia = row[0] == null ? BigDecimal.ZERO : (BigDecimal) row[0];
        int soLuong = row[1] == null ? 0 : ((Number) row[1]).intValue();
        return new StockInfo(donGia, soLuong);
    }

    // === insertInvoiceItem: Chèn một dòng chi tiết vào bảng ChiTietHoaDon ===
    private void insertInvoiceItem(EntityManager em, int maHD, NewInvoiceItem item, BigDecimal donGia, BigDecimal thanhTien) {
        String sql = "INSERT INTO dbo.ChiTietHoaDon (MaHD, MaSach, SoLuong, DonGia, ThanhTien) VALUES (?, ?, ?, ?, ?)";
        em.createNativeQuery(sql)
                .setParameter(1, maHD)
                .setParameter(2, item.getMaSach())
                .setParameter(3, item.getSoLuong())
                .setParameter(4, donGia)
                .setParameter(5, thanhTien)
                .executeUpdate();
    }

    // === updateBookStock: Cập nhật số lượng tồn kho sau khi bán hàng ===
    private void updateBookStock(EntityManager em, int maSach, int newStock) {
        String sql = "UPDATE dbo.Sach SET SoLuong = ? WHERE MaSach = ?";
        em.createNativeQuery(sql)
                .setParameter(1, newStock)
                .setParameter(2, maSach)
                .executeUpdate();
    }

    // === mapInvoiceRow: Ánh xạ một dòng kết quả truy vấn (Object[]) thành đối tượng Invoice ===
    // Hỗ trợ cả truy vấn ngắn (9 cột) và truy vấn dài (12 cột có thêm SĐT, Email, Địa chỉ)
    private Invoice mapInvoiceRow(Object[] row) {
        Invoice invoice = new Invoice();
        invoice.setMaHD(toInt(row[0]));
        invoice.setMaNV(toString(row[1]));
        invoice.setMaKH(row[2] == null ? null : toInt(row[2]));
        invoice.setNgayLap(row[3] == null ? null : (java.sql.Timestamp) row[3]);
        invoice.setTongTien(row[4] == null ? BigDecimal.ZERO : (BigDecimal) row[4]);
        invoice.setGiamGia(row[5] == null ? BigDecimal.ZERO : (BigDecimal) row[5]);
        invoice.setThueVAT(row[6] == null ? BigDecimal.ZERO : (BigDecimal) row[6]);
        invoice.setTrangThai(row[7] == null ? "NEW" : toString(row[7]));
        invoice.setTenKH(toString(row[8]));

        if (row.length > 9) {
            invoice.setDienThoaiKH(toString(row[9]));
        }
        if (row.length > 10) {
            invoice.setEmailKH(toString(row[10]));
        }
        if (row.length > 11) {
            invoice.setDiaChiKH(toString(row[11]));
        }

        return invoice;
    }

    // === mapInvoiceItemRow: Ánh xạ một dòng kết quả thành đối tượng InvoiceItem ===
    // Gồm: MaCT, MaHD, MaSach, SoLuong, DonGia, ThanhTien, TenSach
    private InvoiceItem mapInvoiceItemRow(Object[] row) {
        InvoiceItem item = new InvoiceItem();
        item.setMaCT(toInt(row[0]));
        item.setMaHD(toInt(row[1]));
        item.setMaSach(toInt(row[2]));
        item.setSoLuong(toInt(row[3]));
        item.setDonGia(row[4] == null ? BigDecimal.ZERO : (BigDecimal) row[4]);
        item.setThanhTien(row[5] == null ? BigDecimal.ZERO : (BigDecimal) row[5]);
        item.setTenSach(toString(row[6]));
        return item;
    }

    // === toInt: Chuyển Object thành int (nếu null thì trả về 0) ===
    private int toInt(Object value) {
        return value == null ? 0 : ((Number) value).intValue();
    }

    // === toString: Chuyển Object thành String (nếu null thì trả về null) ===
    private String toString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    // === Lớp StockInfo: Lưu thông tin đơn giá và số lượng tồn của một cuốn sách ===
    // Dùng nội bộ để truyền dữ liệu giữa getStockInfoForUpdate và các bước xử lý khác
    private static final class StockInfo {
        private final BigDecimal donGia;
        private final int soLuongTon;

        private StockInfo(BigDecimal donGia, int soLuongTon) {
            this.donGia = donGia;
            this.soLuongTon = soLuongTon;
        }

        public BigDecimal getDonGia() {
            return donGia;
        }

        public int getSoLuongTon() {
            return soLuongTon;
        }
    }
}
