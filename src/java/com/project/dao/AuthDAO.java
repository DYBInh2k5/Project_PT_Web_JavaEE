package com.project.dao;

import com.project.db.SqlServerConnection;
import com.project.model.AuthUser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthDAO {

    public AuthUser login(String username, String password) throws SQLException {
        String sql = "SELECT TOP 1 MaNV, HoTen, TaiKhoan, VaiTro "
                + "FROM dbo.NhanVien "
                + "WHERE TaiKhoan = ? AND MatKhau = ?";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AuthUser user = new AuthUser();
                    user.setMaNV(rs.getString("MaNV"));
                    user.setHoTen(rs.getString("HoTen"));
                    user.setTaiKhoan(rs.getString("TaiKhoan"));
                    user.setVaiTro(rs.getString("VaiTro"));
                    return user;
                }
            }
        }

        return null;
    }
}
