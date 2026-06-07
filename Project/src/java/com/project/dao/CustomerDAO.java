// ===== DAO Khách hàng (CustomerDAO) — CRUD cho bảng KhachHang =====
// Dùng Native SQL (câu SQL thuần) thay vì JPQL vì bảng không phải là Entity JPA
package com.project.dao;

import com.project.model.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    // Lấy danh sách khách hàng, có tìm kiếm theo tên/SĐT/email
    public List<Customer> findAll(String keyword) {
        // Xây dựng câu SQL động: nếu có keyword thì thêm WHERE với LIKE
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT MaKH, TenKH, DienThoai, Email, DiaChi, TaiKhoan, MatKhau FROM dbo.KhachHang ");

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        if (hasKeyword) {
            sql.append("WHERE TenKH LIKE ? OR DienThoai LIKE ? OR Email LIKE ? ");
        }

        sql.append("ORDER BY MaKH DESC");

        EntityManager em = JpaSupport.createEntityManager();
        try {
            var query = em.createNativeQuery(sql.toString());
            if (hasKeyword) {
                // Gán giá trị LIKE với ký tự đại diện % cho cả 3 tham số
                String q = "%" + keyword.trim() + "%";
                query.setParameter(1, q);
                query.setParameter(2, q);
                query.setParameter(3, q);
            }

            // Ánh xạ từng dòng kết quả sang đối tượng Customer
            List<Object[]> rows = query.getResultList();
            List<Customer> customers = new ArrayList<Customer>(rows.size());
            for (Object[] row : rows) {
                customers.add(mapRow(row));
            }
            return customers;
        } finally {
            em.close();
        }
    }

    // Tìm khách hàng theo mã (MaKH)
    public Customer findById(int maKH) {
        String sql = "SELECT MaKH, TenKH, DienThoai, Email, DiaChi, TaiKhoan, MatKhau FROM dbo.KhachHang WHERE MaKH = ?";

        EntityManager em = JpaSupport.createEntityManager();
        try {
            List<Object[]> rows = em.createNativeQuery(sql)
                    .setParameter(1, maKH)
                    .getResultList();
            return rows.isEmpty() ? null : mapRow(rows.get(0));
        } finally {
            em.close();
        }
    }

    // Tìm khách hàng theo số điện thoại HOẶC email, ưu tiên bản ghi mới nhất (TOP 1)
    public Customer findByPhoneOrEmail(String phone, String email) {
        boolean hasPhone = phone != null && !phone.trim().isEmpty();
        boolean hasEmail = email != null && !email.trim().isEmpty();
        if (!hasPhone && !hasEmail) {
            return null;
        }

        // Xây dựng mệnh đề WHERE linh hoạt theo tham số đầu vào
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT TOP 1 MaKH, TenKH, DienThoai, Email, DiaChi, TaiKhoan, MatKhau FROM dbo.KhachHang WHERE ");
        if (hasPhone && hasEmail) {
            sql.append("DienThoai = ? OR Email = ?");
        } else if (hasPhone) {
            sql.append("DienThoai = ?");
        } else {
            sql.append("Email = ?");
        }
        sql.append(" ORDER BY MaKH DESC");

        EntityManager em = JpaSupport.createEntityManager();
        try {
            var query = em.createNativeQuery(sql.toString());
            if (hasPhone && hasEmail) {
                query.setParameter(1, phone.trim());
                query.setParameter(2, email.trim());
            } else if (hasPhone) {
                query.setParameter(1, phone.trim());
            } else {
                query.setParameter(1, email.trim());
            }

            List<Object[]> rows = query.getResultList();
            return rows.isEmpty() ? null : mapRow(rows.get(0));
        } finally {
            em.close();
        }
    }

    // Thêm mới khách hàng, không trả về mã
    public void insert(Customer customer) {
        String sql = "INSERT INTO dbo.KhachHang (TenKH, DienThoai, Email, DiaChi, TaiKhoan, MatKhau) VALUES (?, ?, ?, ?, ?, ?)";

        EntityManager em = JpaSupport.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery(sql)
                    .setParameter(1, emptyToNull(customer.getTenKH()))
                    .setParameter(2, emptyToNull(customer.getDienThoai()))
                    .setParameter(3, emptyToNull(customer.getEmail()))
                    .setParameter(4, emptyToNull(customer.getDiaChi()))
                    .setParameter(5, emptyToNull(customer.getTaiKhoan()))
                    .setParameter(6, emptyToNull(customer.getMatKhau()))
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

    // Thêm mới khách hàng và trả về MaKH vừa sinh (dùng OUTPUT INSERTED.MaKH của SQL Server)
    public int insertAndGetId(Customer customer) {
        String sql = "INSERT INTO dbo.KhachHang (TenKH, DienThoai, Email, DiaChi, TaiKhoan, MatKhau) OUTPUT INSERTED.MaKH VALUES (?, ?, ?, ?, ?, ?)";

        EntityManager em = JpaSupport.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // getSingleResult trả về cột MaKH do OUTPUT INSERTED.MaKH tạo ra
            Number generatedId = (Number) em.createNativeQuery(sql)
                    .setParameter(1, emptyToNull(customer.getTenKH()))
                    .setParameter(2, emptyToNull(customer.getDienThoai()))
                    .setParameter(3, emptyToNull(customer.getEmail()))
                    .setParameter(4, emptyToNull(customer.getDiaChi()))
                    .setParameter(5, emptyToNull(customer.getTaiKhoan()))
                    .setParameter(6, emptyToNull(customer.getMatKhau()))
                    .getSingleResult();
            tx.commit();
            return generatedId == null ? 0 : generatedId.intValue();
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    // Cập nhật thông tin khách hàng dựa vào MaKH
    public void update(Customer customer) {
        String sql = "UPDATE dbo.KhachHang SET TenKH = ?, DienThoai = ?, Email = ?, DiaChi = ?, TaiKhoan = ?, MatKhau = ? WHERE MaKH = ?";

        EntityManager em = JpaSupport.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery(sql)
                    .setParameter(1, emptyToNull(customer.getTenKH()))
                    .setParameter(2, emptyToNull(customer.getDienThoai()))
                    .setParameter(3, emptyToNull(customer.getEmail()))
                    .setParameter(4, emptyToNull(customer.getDiaChi()))
                    .setParameter(5, emptyToNull(customer.getTaiKhoan()))
                    .setParameter(6, emptyToNull(customer.getMatKhau()))
                    .setParameter(7, customer.getMaKH())
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

    // Xoá khách hàng theo mã
    public void delete(int maKH) {
        String sql = "DELETE FROM dbo.KhachHang WHERE MaKH = ?";

        EntityManager em = JpaSupport.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery(sql)
                    .setParameter(1, maKH)
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

    // Ánh xạ một dòng dữ liệu từ ResultSet (Object[]) sang đối tượng Customer
    private Customer mapRow(Object[] row) {
        Customer customer = new Customer();
        customer.setMaKH(toInt(row[0]));
        customer.setTenKH(toString(row[1]));
        customer.setDienThoai(toString(row[2]));
        customer.setEmail(toString(row[3]));
        customer.setDiaChi(toString(row[4]));
        // TaiKhoan và MatKhau có thể không có (câu SELECT cũ), cần kiểm tra độ dài mảng
        customer.setTaiKhoan(row.length > 5 ? toString(row[5]) : null);
        customer.setMatKhau(row.length > 6 ? toString(row[6]) : null);
        return customer;
    }

    // Đăng nhập: tìm khách hàng theo tài khoản/email/SĐT và mật khẩu
    public Customer loginCustomer(String username, String password) {
        boolean hasUsername = username != null && !username.trim().isEmpty();
        boolean hasPassword = password != null && !password.trim().isEmpty();
        if (!hasUsername || !hasPassword) {
            return null;
        }

        String sql = "SELECT TOP 1 MaKH, TenKH, DienThoai, Email, DiaChi, TaiKhoan, MatKhau FROM dbo.KhachHang "
                + "WHERE (TaiKhoan = ? OR Email = ? OR DienThoai = ?) AND MatKhau = ?";

        EntityManager em = JpaSupport.createEntityManager();
        try {
            List<Object[]> rows = em.createNativeQuery(sql)
                    .setParameter(1, username.trim())
                    .setParameter(2, username.trim())
                    .setParameter(3, username.trim())
                    .setParameter(4, password)
                    .getResultList();
            return rows.isEmpty() ? null : mapRow(rows.get(0));
        } finally {
            em.close();
        }
    }

    // Tìm tài khoản theo tên đăng nhập, email hoặc SĐT (dùng cho kiểm tra trùng)
    public Customer findByAccount(String username) {
        boolean hasUsername = username != null && !username.trim().isEmpty();
        if (!hasUsername) {
            return null;
        }

        String sql = "SELECT TOP 1 MaKH, TenKH, DienThoai, Email, DiaChi, TaiKhoan, MatKhau FROM dbo.KhachHang "
                + "WHERE TaiKhoan = ? OR Email = ? OR DienThoai = ? ORDER BY MaKH DESC";

        EntityManager em = JpaSupport.createEntityManager();
        try {
            List<Object[]> rows = em.createNativeQuery(sql)
                    .setParameter(1, username.trim())
                    .setParameter(2, username.trim())
                    .setParameter(3, username.trim())
                    .getResultList();
            return rows.isEmpty() ? null : mapRow(rows.get(0));
        } finally {
            em.close();
        }
    }

    // Chuyển đối tượng thành số nguyên (int), trả về 0 nếu null
    private int toInt(Object value) {
        return value == null ? 0 : ((Number) value).intValue();
    }

    // Chuyển đối tượng thành chuỗi (String), trả về null nếu value null
    private String toString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    // Chuyển chuỗi rỗng thành null trước khi lưu vào DB (chuẩn hoá dữ liệu)
    private String emptyToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
