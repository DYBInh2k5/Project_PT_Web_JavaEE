# Theo doi trien khai du an QL Ban Sach

Ngay bat dau: 2026-04-05

## Muc tieu hoc phan
- Xay dung ung dung web Java EE quan li ban sach.
- Ket noi SQL Server database: QLBanSach.
- Trien khai theo module de de demo va bao cao.

## Tien do hien tai
- [x] Ket noi SQL Server (JNDI + JDBC fallback)
- [x] Endpoint kiem tra DB `/db-check`
- [x] Chuan hoa Java EE theo mon hoc
  - MVC: Servlet + JSP + DAO
  - Cau hinh `web.xml` (filter, session-timeout, error-page, welcome-file)
  - Encoding filter UTF-8 cho toan he thong
- [x] Module Sach v1 (Servlet + JSP + JDBC DAO)
  - Danh sach + tim kiem
  - Them sach
  - Sua sach
  - Xoa sach
- [x] Module KhachHang (CRUD day du)
- [x] Module HoaDon + ChiTietHoaDon (danh sach, tao hoa don, chi tiet)
- [x] Transaction tao hoa don kem tru ton kho Sach
- [x] Dashboard thong ke tong quan
- [x] Dang nhap NhanVien + session + logout + auth filter
- [x] Phan quyen theo VaiTro (Admin/NhanVien)
  - URL Admin: xoa Sach, xoa KhachHang, bao cao
- [x] Bao cao thong ke doanh thu
  - Doanh thu theo ngay trong khoang loc
  - Top 10 sach ban chay
- [x] Tich hop giao dien tu Stitch vao JSP runtime
  - Da import layout moi cho: Login, Dashboard, Kho sach, Hoa don, Bao cao, Error pages (403/404/500)
  - Giu nguyen binding du lieu Servlet/JSP va URL routing hien co
  - Dong bo style hien dai bang Tailwind CDN + fonts theo template Stitch
- [x] Ghi log hoat dong nguoi dung
  - Filter ghi activity log cho request da dang nhap
- [x] Bo test case nghiep vu quan trong
  - Da tao `MANUAL_TEST_CASES.md` cho cac flow chinh
- [x] Module Tra hang / Doi hang
  - Dung schema `DoiTra` va `ChiTietDoiTra`
  - Luu phieu tra, chi tiet va cap nhat ton kho sach
- [x] Module Nhap hang
  - Dung schema `PhieuNhap` va `ChiTietPhieuNhap`
  - Luu phieu nhap, chi tiet va cap nhat ton kho sach
- [x] Module Khuyen mai / coupon
  - Dung schema `KhuyenMai`
  - CRUD khuyen mai (them, sua, xoa, tim kiem)
  - Ap coupon vao hoa don neu ma hop le va con hieu luc
  - Ho tro khuyen mai `TANG1` theo quy tac mua N tang 1 (N = GiaTri)
  - Validate khong cho trung `MaCoupon` khi them/sua
- [x] Module Shop ban sach (public)
  - Catalog cong khai `/shop` + tim kiem sach
  - Trang chi tiet sach `/shop/book?id={MaSach}`
  - Gio hang session (`add/update/remove`)
  - Checkout public tao hoa don that vao `HoaDon` + `ChiTietHoaDon`
  - Tai su dung khach hang theo SDT/Email de tranh tao trung
  - Ho tro coupon `%`, `COUPON`, `TANG1` trong checkout
  - Ma don cong khai (order code) cho khach mua
  - Trang chi tiet don theo ma cong khai `/shop/order?code=...`
  - Bao ve trang chi tiet don: yeu cau xac thuc SDT/Email neu khong nam trong session cua khach
  - Xuat hoa don PDF tu trang chi tiet don
  - Tra cuu don hang cho khach mua qua `/shop/order-lookup`
  - Lich su `Don cua toi` trong session qua `/shop/my-orders`

## Cac URL hien co
- `/` : Trang chu
- `/login` : Dang nhap he thong
- `/logout` : Dang xuat he thong
- `/dashboard` : Dashboard tong quan
- `/reports/revenue` : Bao cao doanh thu + top sach ban chay
- `/db-check` : Kiem tra ket noi CSDL
- `/books` : Danh sach sach
- `/books/new` : Them sach
- `/books/edit?id={MaSach}` : Sua sach
- `/customers` : Danh sach khach hang
- `/customers/new` : Them khach hang
- `/customers/edit?id={MaKH}` : Sua khach hang
- `/invoices` : Danh sach hoa don
- `/invoices/new` : Tao hoa don moi
- `/invoices/detail?id={MaHD}` : Xem chi tiet hoa don
- `/promotions` : Danh sach khuyen mai
- `/promotions/new` : Them khuyen mai
- `/promotions/edit?id={MaKM}` : Sua khuyen mai
- `/shop` : Trang shop ban sach cong khai
- `/shop/book?id={MaSach}` : Chi tiet sach cho khach mua
- `/shop/cart` : Gio hang
- `/shop/checkout` : Thanh toan va dat hang
- `/shop/order?code={OrderCode}` : Chi tiet don theo ma cong khai
- `/shop/order/pdf?code={OrderCode}` : Tai hoa don PDF
- `/shop/order-lookup` : Tra cuu don hang theo MaHD + SDT/Email
- `/shop/my-orders` : Lich su don trong phien mua hang hien tai

## Cau truc da tao
- `src/java/com/project/model/Book.java`
- `src/java/com/project/dao/BookDAO.java`
- `src/java/com/project/web/book/BookListServlet.java`
- `src/java/com/project/web/book/BookFormServlet.java`
- `src/java/com/project/web/book/BookDeleteServlet.java`
- `web/books/list.jsp`
- `web/books/form.jsp`

## Ke hoach tiep theo de tiep tuc ngay
1. Tach layout chung JSP (`header.jspf`, `footer.jspf`) de dung style MVC ro hon.
2. Can nhac bo sung migration CSDL neu muon luu lich su coupon/khuyen mai da ap cho tung hoa don.
3. Bo sung bao cao thang/quy va xuat Excel/PDF.
4. Mo rong man hinh lenh xoa/quan ly phieu nhap va doi tra neu can.

## Nhat ky cap nhat giao dien Stitch (2026-04-05)
- Da lay day du HTML/PNG tu `stitch_exports`.
- Da tao prompt Stitch cho shop: `STITCH_PROMPT_SHOP_V2.md`.
- Da tao prompt Stitch bo sung cho admin: `STITCH_PROMPT_ADMIN_V2.md`.
- Da cay UI vao cac trang chinh:
  - `web/login.jsp`
  - `web/dashboard.jsp`
  - `web/books/list.jsp`
  - `web/books/form.jsp`
  - `web/customers/list.jsp`
  - `web/customers/form.jsp`
  - `web/invoices/list.jsp`
  - `web/invoices/new.jsp`
  - `web/invoices/detail.jsp`
  - `web/reports/revenue.jsp`
  - `web/index.jsp`
  - `web/error/403.jsp`, `web/error/404.jsp`, `web/error/500.jsp`
- Da kiem tra loi cu phap qua VS Code Problems: khong phat hien loi moi tren cac file vua cap nhat.
- Trang thai hien tai: da dong bo giao dien cho toan bo JSP trong ung dung.
- Trang thai hien tai: da co audit log + bo test case, module nhap hang da co schema nhung chua cấy vao app.
- Trang thai hien tai: da mo rong them module tra hang / doi hang theo schema san co trong QLBanSach.
- Trang thai hien tai: da co ca module nhap hang, tra hang / doi hang va audit log.
- Trang thai hien tai: da co module khuyen mai va ap coupon vao hoa don.
- Trang thai hien tai: da ho tro `TANG1` va validate trung `MaCoupon` trong module khuyen mai.
- Trang thai hien tai: da co web shop cong khai tu catalog den checkout, dong bo truc tiep voi he thong hoa don.
- Trang thai hien tai: da co lich su don hang theo session cho khach mua (`Don cua toi`).
- Trang thai hien tai: da co `OrderCode` de tra cuu/chi tiet don va da tranh tao trung khach hang khi checkout.
- Trang thai hien tai: da co xac thuc bo sung truoc khi xem chi tiet don va da tai duoc hoa don PDF.
- Trang thai hien tai: da tai bo Stitch Bookora V2 (7 man hinh) vao `stitch_exports/bookora_v2` va cay tiep vao cac JSP shop.
- Trang thai hien tai: da tach style chung `web/assets/shop-bookora.css` va dong bo lai 7 trang shop theo mot design system thong nhat.
- Trang thai hien tai: da tach `header.jspf` va `footer.jspf` dung chung cho 7 trang shop de de bao tri va tranh lap code.
