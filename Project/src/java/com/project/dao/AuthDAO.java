package com.project.dao;

import com.project.model.AuthUser;
import jakarta.persistence.EntityManager;

public class AuthDAO {

    public AuthUser login(String username, String password) {
        String sql = "SELECT TOP 1 MaNV, HoTen, TaiKhoan, VaiTro "
                + "FROM dbo.NhanVien "
                + "WHERE TaiKhoan = ? AND MatKhau = ?";

        EntityManager em = JpaSupport.createEntityManager();
        try {
            var rows = em.createNativeQuery(sql)
                    .setParameter(1, username)
                    .setParameter(2, password)
                    .getResultList();

            if (rows.isEmpty()) {
                return null;
            }

            Object[] row = (Object[]) rows.get(0);
            AuthUser user = new AuthUser();
            user.setMaNV(toString(row[0]));
            user.setHoTen(toString(row[1]));
            user.setTaiKhoan(toString(row[2]));
            user.setVaiTro(toString(row[3]));
            return user;
        } finally {
            em.close();
        }
    }

    private String toString(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
