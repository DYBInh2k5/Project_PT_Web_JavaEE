# Huong dan cau hinh SQL Server - QLBanSach

Tai lieu nay mo ta cach cau hinh SQL Server de chay voi Spring Boot + JPA trong project.

## Thong tin mac dinh

- Database: `QLBanSach`
- Host: `localhost`
- Port: `1433`
- User: `sa`
- Password: `1`

## File can dong bo

Khi doi thong tin database, can cap nhat dong bo ca 2 file sau:

- [src/main/resources/application.properties](src/main/resources/application.properties)
- [src/main/resources/META-INF/persistence.xml](src/main/resources/META-INF/persistence.xml)

## Cach tao database

1. Mo SQL Server Management Studio.
2. Dang nhap bang tai khoan co quyen tao database.
3. Tao database moi ten `QLBanSach`.
4. Chay file schema/data neu ban da co script khoi tao.

## Neu dung tai khoan khac

Neu may ban khong dung `sa / 1`, hay sua 4 gia tri sau:

- `spring.datasource.username`
- `spring.datasource.password`
- `jakarta.persistence.jdbc.user`
- `jakarta.persistence.jdbc.password`

## Cach kiem tra ket noi

Sau khi chay ung dung, mo:

```text
http://localhost:8080/Project/api/db-check
```

Ket qua hop le thuong se co:

- `Connection OK`
- `Current database: QLBanSach`
- `Match: true`

## Loi thuong gap

- SQL Server chua bat `TCP/IP`.
- Port `1433` bi chan boi firewall.
- Sai user/password.
- Chua tao database `QLBanSach`.
- Driver `mssql-jdbc` chua duoc tai len du an.

## Ghi chu

File `web/WEB-INF/glassfish-resources.xml` chi con la tai lieu cu; project hien tai chay theo Spring Boot va cau hinh trong `application.properties` + `persistence.xml`.
