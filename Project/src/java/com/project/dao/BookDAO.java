// ===== DAO Sách (BookDAO) — xử lý truy vấn CRUD cho bảng Sach =====
// Dùng JPA (Hibernate) với EntityManager để thao tác dữ liệu
package com.project.dao;

import com.project.model.Book;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class BookDAO {

    // Tìm tất cả sách, có hỗ trợ tìm kiếm theo từ khóa (tên, tác giả, thể loại)
    public List<Book> findAll(String keyword) {
        EntityManager entityManager = JpaSupport.createEntityManager();
        try {
            // Xây dựng câu JPQL động — nếu có keyword thì thêm WHERE với LIKE
            StringBuilder jpql = new StringBuilder("SELECT b FROM Book b");
            boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
            if (hasKeyword) {
                jpql.append(" WHERE LOWER(b.tenSach) LIKE :keyword OR LOWER(b.tacGia) LIKE :keyword OR LOWER(b.theLoai) LIKE :keyword");
            }
            jpql.append(" ORDER BY b.maSach DESC");

            TypedQuery<Book> query = entityManager.createQuery(jpql.toString(), Book.class);
            if (hasKeyword) {
                query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
            }

            return new ArrayList<Book>(query.getResultList());
        } finally {
            entityManager.close(); // Luôn đóng EntityManager để tránh rò rỉ kết nối
        }
    }

    // Tìm sách theo mã (khóa chính)
    public Book findById(int maSach) {
        EntityManager entityManager = JpaSupport.createEntityManager();
        try {
            return entityManager.find(Book.class, Integer.valueOf(maSach));
        } finally {
            entityManager.close();
        }
    }

    // Thêm sách mới
    public void insert(Book book) {
        EntityManager entityManager = JpaSupport.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(book); // persist = INSERT vào DB
            entityManager.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback(); // Rollback nếu có lỗi
            }
            throw new RuntimeException("Khong them duoc sach: " + ex.getMessage(), ex);
        } finally {
            entityManager.close();
        }
    }

    // Cập nhật thông tin sách
    public void update(Book book) {
        EntityManager entityManager = JpaSupport.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(book); // merge = UPDATE nếu đã tồn tại
            entityManager.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Khong cap nhat duoc sach: " + ex.getMessage(), ex);
        } finally {
            entityManager.close();
        }
    }

    // Xóa sách theo mã
    public void delete(int maSach) {
        EntityManager entityManager = JpaSupport.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Book existing = entityManager.find(Book.class, Integer.valueOf(maSach));
            if (existing != null) {
                entityManager.remove(existing); // remove = DELETE
            }
            entityManager.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Khong xoa duoc sach: " + ex.getMessage(), ex);
        } finally {
            entityManager.close();
        }
    }
}
