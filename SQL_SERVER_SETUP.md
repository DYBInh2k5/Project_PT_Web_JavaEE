# Huong dan ket noi SQL Server

Tai lieu nay cho biet dung cau hinh nao de project ket noi SQL Server thanh cong.

## Cau hinh mac dinh

| Muc | Gia tri |
|---|---|
| Database | `QLBanSach` |
| Host | `localhost` |
| Port | `1433` |
| User | `sa` |
| Password | `1` |

## Noi can cau hinh

Cap nhat 2 file:

- [src/main/resources/application.properties](src/main/resources/application.properties)
- [src/main/resources/META-INF/persistence.xml](src/main/resources/META-INF/persistence.xml)

## Cac dong quan trong

Trong `application.properties`:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=QLBanSach;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=1
```

Trong `persistence.xml`:

```xml
<property name="jakarta.persistence.jdbc.url" value="jdbc:sqlserver://localhost:1433;databaseName=QLBanSach;encrypt=true;trustServerCertificate=true"/>
<property name="jakarta.persistence.jdbc.user" value="sa"/>
<property name="jakarta.persistence.jdbc.password" value="1"/>
```

## Neu ban dung user khac

Sua dong `username/password` trong `application.properties` va `user/password` trong `persistence.xml` cho giong nhau.

## Cach test ket noi

Sau khi chay ung dung, mo:

```text
http://localhost:8080/Project/api/db-check
```

Neu dung, ket qua se bao:

- `Connection OK`
- `Current database: QLBanSach`
- `Match: true`

## Kiem tra SQL Server neu bi loi

- Dam bao service SQL Server dang chay.
- Bat `TCP/IP` trong SQL Server Configuration Manager.
- Dam bao port `1433` khong bi chan.
- Tao san database `QLBanSach`.
- Kiem tra lai mat khau cua `sa`.

## Ghi chu

Project hien tai khong con phu thuoc vao GlassFish de ket noi CSDL; ket noi duoc cau hinh truc tiep trong Spring Boot va JPA.