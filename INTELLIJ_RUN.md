# Huong dan chay du an tren IntelliJ IDEA

Tai lieu nay danh rieng cho ban muon mo va chay project nhanh trong IntelliJ.

## Yeu cau truoc khi chay

- IntelliJ IDEA da cai dat JDK 21.
- SQL Server dang chay.
- Database `QLBanSach` da ton tai.
- Thong tin dang nhap CSDL khop voi [SQL Server setup](SQL_SERVER_SETUP.md).

## Cach mo project

1. Mo IntelliJ IDEA.
2. Chon **Open** va tro den thu muc `Project`.
3. Chon file `pom.xml` de IntelliJ nhan project Maven.
4. Cho IntelliJ tai dependency lan dau.

## Chon JDK

1. Vao **File > Project Structure**.
2. Chon **Project SDK** la JDK 21.
3. Neu can, set **Language level** tuong ung JDK 21.

## Cach chay ung dung

### Cach 1: Run class chinh

- Mo file [src/java/com/project/BookstoreApiApplication.java](src/java/com/project/BookstoreApiApplication.java).
- Bam **Run**.

### Cach 2: Run bang Maven

Mo terminal trong IntelliJ va chay:

```bash
mvn spring-boot:run
```

## URL can thu

Sau khi app chay:

- Trang chu: `http://localhost:8080/Project/`
- Kiem tra DB: `http://localhost:8080/Project/api/db-check`
- Shop cong khai: `http://localhost:8080/Project/shop`
- Dang nhap: `http://localhost:8080/Project/login`

## Neu bi loi khong chay duoc

- Kiem tra cong `8080` co bi app khac dung khong.
- Kiem tra SQL Server co dang mo khong.
- Kiem tra user/password trong `application.properties`.
- Kiem tra database `QLBanSach` co dung ten khong.
- Neu IntelliJ bao thieu dependency, bam **Reload Maven**.

## Cach debug nhanh

1. Dat breakpoint trong controller hoac DAO.
2. Chay bang **Debug** thay vi **Run**.
3. Goi `GET /Project/api/db-check` de test ket noi CSDL truoc.