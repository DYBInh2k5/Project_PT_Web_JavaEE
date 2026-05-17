package com.project.dao;

import com.project.model.Book;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

public class BookDAO {

    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("bookstorePU");

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (ENTITY_MANAGER_FACTORY != null && ENTITY_MANAGER_FACTORY.isOpen()) {
                ENTITY_MANAGER_FACTORY.close();
            }
        }));
    }

    public List<Book> findAll(String keyword) throws SQLException {
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        try {
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
            entityManager.close();
        }
    }

    public Book findById(int maSach) throws SQLException {
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        try {
            return entityManager.find(Book.class, Integer.valueOf(maSach));
        } finally {
            entityManager.close();
        }
    }

    public void insert(Book book) throws SQLException {
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(book);
            entityManager.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new SQLException("Khong them duoc sach: " + ex.getMessage(), ex);
        } finally {
            entityManager.close();
        }
    }

    public void update(Book book) throws SQLException {
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(book);
            entityManager.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new SQLException("Khong cap nhat duoc sach: " + ex.getMessage(), ex);
        } finally {
            entityManager.close();
        }
    }

    public void delete(int maSach) throws SQLException {
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Book existing = entityManager.find(Book.class, Integer.valueOf(maSach));
            if (existing != null) {
                entityManager.remove(existing);
            }
            entityManager.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new SQLException("Khong xoa duoc sach: " + ex.getMessage(), ex);
        } finally {
            entityManager.close();
        }
    }
}
