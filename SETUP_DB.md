# Theo doi cau hinh ket noi SQL Server - QLBanSach

Ngay cap nhat: 2026-04-05

## Da thuc hien
- Tao datasource GlassFish trong project: `web/WEB-INF/glassfish-resources.xml`
- Tao lop ket noi SQL Server: `src/java/com/project/db/SqlServerConnection.java`
- Tao endpoint test ket noi: `src/java/com/project/web/DbHealthServlet.java` voi URL `/db-check`
- Cap nhat trang chu de mo nhanh endpoint test.

## Cach test sau khi deploy
1. Deploy project len GlassFish.
2. Mo URL: `http://localhost:8080/Project/db-check`
3. Ket qua dung khi thay:
   - `Connection OK`
   - `Current database: QLBanSach`
   - `Match: true`

## Ghi chu
- Tai khoan dang cau hinh: `sa / 1`
- Neu may chua co driver SQL Server JDBC trong GlassFish, can bo sung file jar driver.
