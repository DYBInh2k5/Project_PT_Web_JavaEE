# Project Spring Boot Bookstore

Toi hoc mon JavaEE, day la du an cuoi ki cua toi.

## Gioi thieu

Du an da duoc chuan hoa de chay tren Spring Boot + JPA + Bootstrap, ket noi SQL Server (QLBanSach), bao gom:
- Backend Spring Boot
- JPA cho luong sach chinh
- Giao dien Bootstrap 5 cho trang vao va demo
- API/shop cong khai cho khach mua sach

## Cong nghe su dung

- Spring Boot
- Spring Data JPA / Hibernate
- Spring MVC REST Controller
- Bootstrap 5
- JPA native queries cho cac module legacy
- SQL Server (database QLBanSach)
- Maven
- HTML, CSS

## Chuc nang chinh

- Dang nhap, dang xuat, phan quyen vai tro
- CRUD Sach, KhachHang, KhuyenMai
- Tao hoa don, chi tiet hoa don, tru ton kho theo giao dich
- Bao cao doanh thu va top sach ban chay
- Module nhap hang va doi tra
- Shop public:
  - Catalog sach, tim kiem, trang chi tiet
  - Gio hang session, checkout tao hoa don that
  - Ap dung coupon (% giam, coupon tien, TANG1)
  - Tra cuu don hang theo ma cong khai
  - Xuat hoa don PDF

## Cac API quan trong

- /api/auth/login : dang nhap
- /api/auth/logout : dang xuat
- /api/dashboard : dashboard
- /api/db-check : kiem tra ket noi CSDL
- /api/books : quan ly sach
- /api/customers : quan ly khach hang
- /api/invoices : quan ly hoa don
- /api/reports/revenue : bao cao doanh thu
- /api/promotions : quan ly khuyen mai
- /api/shop/books : shop ban sach cong khai
- /api/shop/cart : gio hang
- /api/shop/checkout : dat hang
- /api/shop/order-lookup : tra cuu don hang

## Tai lieu huong dan

- [Huong dan chay tren IntelliJ](INTELLIJ_RUN.md)
- [Huong dan ket noi SQL Server](SQL_SERVER_SETUP.md)
- [Test case nghiep vu](MANUAL_TEST_CASES.md)
- [Checklist hoan thien du an](JAVAEE_PROJECT_CHECKLIST.md)

## Cau truc thu muc

- src/java : ma nguon Java (Spring Boot API, DAO, model)
- src/main/resources : cau hinh Spring Boot + JPA + SQL Server
- pom.xml : cau hinh Maven + Spring Boot
- src/java/com/project/config/ServletFilterConfig.java : dang ky filter theo Spring Boot (khong dung web.xml)
- web : giao dien JSP va Bootstrap cho trang vao
- SETUP_DB.md : tai lieu cau hinh CSDL cu (da cap nhat theo Spring Boot)
- MANUAL_TEST_CASES.md : test case thu cong
- IMPLEMENTATION_TRACKING.md : nhat ky trien khai

## Cach chay du an (tom tat)

1. Tao database `QLBanSach` tren SQL Server va import schema/data.
2. Mo [SQL Server setup](SQL_SERVER_SETUP.md) de doi thong tin dang nhap neu can.
3. Mo [IntelliJ guide](INTELLIJ_RUN.md) va chay ung dung.
4. Goi `GET /Project/api/db-check` de xac nhan ket noi CSDL.

## Ghi chu khi nop mon hoac demo

- Day la project mon hoc, muc tieu la trinh bay day du quy trinh phat trien web voi Spring Boot, JPA va Bootstrap.
- Nen demo theo thu tu: Login -> Dashboard -> CRUD du lieu -> Bao cao -> Shop checkout.
- Cac tai lieu bo tro trong repo giup doi chieu yeu cau va test nhanh.
- Phan servlet/JSP cu van duoc giu lai de tuong thich giao dien, nhung logic CSDL da duoc chuyen sang JPA.

## Tac gia

- Sinh vien thuc hien: Võ Duy Bình
- Nganh: Ky thuat phan mem
- Truong: Dai hoc Hoa Sen
- MSSV: 22301500
