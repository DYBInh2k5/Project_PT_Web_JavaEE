package com.project.dao;

import com.project.db.SqlServerConnection;
import com.project.model.Book;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public List<Book> findAll(String keyword) throws SQLException {
        List<Book> books = new ArrayList<Book>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT MaSach, TenSach, TacGia, TheLoai, DonGia, SoLuong, AnhBia ");
        sql.append("FROM dbo.Sach ");

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        if (hasKeyword) {
            sql.append("WHERE TenSach LIKE ? OR TacGia LIKE ? OR TheLoai LIKE ? ");
        }

        sql.append("ORDER BY MaSach DESC");

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            if (hasKeyword) {
                String q = "%" + keyword.trim() + "%";
                ps.setString(1, q);
                ps.setString(2, q);
                ps.setString(3, q);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(mapRow(rs));
                }
            }
        }

        return books;
    }

    public Book findById(int maSach) throws SQLException {
        String sql = "SELECT MaSach, TenSach, TacGia, TheLoai, DonGia, SoLuong, AnhBia FROM dbo.Sach WHERE MaSach = ?";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maSach);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }

        return null;
    }

    public void insert(Book book) throws SQLException {
        String sql = "INSERT INTO dbo.Sach (TenSach, TacGia, TheLoai, DonGia, SoLuong, AnhBia) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            bindCommonFields(ps, book);
            ps.executeUpdate();
        }
    }

    public void update(Book book) throws SQLException {
        String sql = "UPDATE dbo.Sach SET TenSach = ?, TacGia = ?, TheLoai = ?, DonGia = ?, SoLuong = ?, AnhBia = ? WHERE MaSach = ?";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            bindCommonFields(ps, book);
            ps.setInt(7, book.getMaSach());
            ps.executeUpdate();
        }
    }

    public void delete(int maSach) throws SQLException {
        String sql = "DELETE FROM dbo.Sach WHERE MaSach = ?";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maSach);
            ps.executeUpdate();
        }
    }

    private void bindCommonFields(PreparedStatement ps, Book book) throws SQLException {
        ps.setString(1, emptyToNull(book.getTenSach()));
        ps.setString(2, emptyToNull(book.getTacGia()));
        ps.setString(3, emptyToNull(book.getTheLoai()));

        if (book.getDonGia() == null) {
            ps.setNull(4, java.sql.Types.DECIMAL);
        } else {
            ps.setBigDecimal(4, book.getDonGia());
        }

        if (book.getSoLuong() == null) {
            ps.setNull(5, java.sql.Types.INTEGER);
        } else {
            ps.setInt(5, book.getSoLuong());
        }

        ps.setString(6, emptyToNull(book.getAnhBia()));
    }

    private Book mapRow(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setMaSach(rs.getInt("MaSach"));
        book.setTenSach(rs.getString("TenSach"));
        book.setTacGia(rs.getString("TacGia"));
        book.setTheLoai(rs.getString("TheLoai"));

        BigDecimal donGia = rs.getBigDecimal("DonGia");
        book.setDonGia(donGia);

        int soLuong = rs.getInt("SoLuong");
        if (rs.wasNull()) {
            book.setSoLuong(null);
        } else {
            book.setSoLuong(soLuong);
        }

        book.setAnhBia(rs.getString("AnhBia"));
        return book;
    }

    private String emptyToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
