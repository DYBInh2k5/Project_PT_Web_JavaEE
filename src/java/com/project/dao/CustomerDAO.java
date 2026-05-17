package com.project.dao;

import com.project.model.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public List<Customer> findAll(String keyword) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT MaKH, TenKH, DienThoai, Email, DiaChi FROM dbo.KhachHang ");

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        if (hasKeyword) {
            sql.append("WHERE TenKH LIKE ? OR DienThoai LIKE ? OR Email LIKE ? ");
        }

        sql.append("ORDER BY MaKH DESC");

        EntityManager em = JpaSupport.createEntityManager();
        try {
            var query = em.createNativeQuery(sql.toString());
            if (hasKeyword) {
                String q = "%" + keyword.trim() + "%";
                query.setParameter(1, q);
                query.setParameter(2, q);
                query.setParameter(3, q);
            }

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

    public Customer findById(int maKH) {
        String sql = "SELECT MaKH, TenKH, DienThoai, Email, DiaChi FROM dbo.KhachHang WHERE MaKH = ?";

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

    public Customer findByPhoneOrEmail(String phone, String email) {
        boolean hasPhone = phone != null && !phone.trim().isEmpty();
        boolean hasEmail = email != null && !email.trim().isEmpty();
        if (!hasPhone && !hasEmail) {
            return null;
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT TOP 1 MaKH, TenKH, DienThoai, Email, DiaChi FROM dbo.KhachHang WHERE ");
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

    public void insert(Customer customer) {
        String sql = "INSERT INTO dbo.KhachHang (TenKH, DienThoai, Email, DiaChi) VALUES (?, ?, ?, ?)";

        EntityManager em = JpaSupport.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery(sql)
                    .setParameter(1, emptyToNull(customer.getTenKH()))
                    .setParameter(2, emptyToNull(customer.getDienThoai()))
                    .setParameter(3, emptyToNull(customer.getEmail()))
                    .setParameter(4, emptyToNull(customer.getDiaChi()))
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

    public int insertAndGetId(Customer customer) {
        String sql = "INSERT INTO dbo.KhachHang (TenKH, DienThoai, Email, DiaChi) OUTPUT INSERTED.MaKH VALUES (?, ?, ?, ?)";

        EntityManager em = JpaSupport.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Number generatedId = (Number) em.createNativeQuery(sql)
                    .setParameter(1, emptyToNull(customer.getTenKH()))
                    .setParameter(2, emptyToNull(customer.getDienThoai()))
                    .setParameter(3, emptyToNull(customer.getEmail()))
                    .setParameter(4, emptyToNull(customer.getDiaChi()))
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

    public void update(Customer customer) {
        String sql = "UPDATE dbo.KhachHang SET TenKH = ?, DienThoai = ?, Email = ?, DiaChi = ? WHERE MaKH = ?";

        EntityManager em = JpaSupport.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery(sql)
                    .setParameter(1, emptyToNull(customer.getTenKH()))
                    .setParameter(2, emptyToNull(customer.getDienThoai()))
                    .setParameter(3, emptyToNull(customer.getEmail()))
                    .setParameter(4, emptyToNull(customer.getDiaChi()))
                    .setParameter(5, customer.getMaKH())
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

    private Customer mapRow(Object[] row) {
        Customer customer = new Customer();
        customer.setMaKH(toInt(row[0]));
        customer.setTenKH(toString(row[1]));
        customer.setDienThoai(toString(row[2]));
        customer.setEmail(toString(row[3]));
        customer.setDiaChi(toString(row[4]));
        return customer;
    }

    private int toInt(Object value) {
        return value == null ? 0 : ((Number) value).intValue();
    }

    private String toString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private String emptyToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
