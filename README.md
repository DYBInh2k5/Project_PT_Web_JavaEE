# Project Java EE Bookstore

Day la project mon Phat trien web nen tang Java EE - shop ban sach.

## Gioi thieu

Ung dung web quan ly va ban sach duoc xay dung theo kien truc MVC (Servlet + JSP + DAO), ket noi SQL Server (QLBanSach), gom hai phan:
- He thong quan tri noi bo cho nhan vien
- Shop cong khai cho khach mua sach

## Cong nghe su dung

- Java EE (Servlet, JSP, Filter)
- JDBC va JNDI DataSource
- SQL Server (database QLBanSach)
- Apache Ant / NetBeans project structure
- GlassFish Server
- HTML, CSS, Tailwind CDN cho giao dien

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

## Cac URL quan trong

- /Project/ : trang chu
- /Project/login : dang nhap
- /Project/dashboard : dashboard
- /Project/db-check : kiem tra ket noi CSDL
- /Project/books : quan ly sach
- /Project/customers : quan ly khach hang
- /Project/invoices : quan ly hoa don
- /Project/reports/revenue : bao cao doanh thu
- /Project/promotions : quan ly khuyen mai
- /Project/shop : shop ban sach cong khai
- /Project/shop/cart : gio hang
- /Project/shop/checkout : dat hang
- /Project/shop/order-lookup : tra cuu don hang

## Cau truc thu muc

- src/java : ma nguon Java (Servlet, DAO, model)
- web : JSP, static assets, WEB-INF
- nbproject : cau hinh project NetBeans/Ant
- build.xml : script build Ant
- SETUP_DB.md : huong dan cau hinh CSDL
- MANUAL_TEST_CASES.md : test case thu cong
- IMPLEMENTATION_TRACKING.md : nhat ky trien khai

## Cach chay du an (tom tat)

1. Tao database QLBanSach tren SQL Server va import schema/data.
2. Cau hinh ket noi SQL Server theo huong dan trong SETUP_DB.md.
3. Mo project bang NetBeans hoac IDE ho tro Ant Java Web.
4. Deploy len GlassFish (context path: /Project).
5. Truy cap /Project/db-check de xac nhan ket noi CSDL.
6. Truy cap /Project/shop de vao giao dien shop.

## Ghi chu khi nop mon hoac demo

- Day la project mon hoc, muc tieu la trinh bay day du quy trinh phat trien web tren nen tang Java EE.
- Nen demo theo thu tu: Login -> Dashboard -> CRUD du lieu -> Bao cao -> Shop checkout.
- Cac tai lieu bo tro trong repo giup doi chieu yeu cau va test nhanh.

## Tac gia

- Sinh vien thuc hien: cap nhat theo thong tin nhom/ca nhan truoc khi nop.
