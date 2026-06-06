# Bookora - Quản lý bán sách (Spring Boot MVC + Thymeleaf)

## Tổng quan

Ứng dụng web quản lý bán sách, xây dựng bằng **Spring Boot MVC + Thymeleaf + SQL Server**.
Cho phép khách hàng xem sách, giỏ hàng, đặt hàng; admin quản lý sản phẩm, đơn hàng, doanh thu.

## Công nghệ

| Thành phần | Công nghệ |
|-----------|-----------|
| Backend | Spring Boot 3.5, Java 21 |
| View | Thymeleaf, HTML, CSS |
| Database | SQL Server (MSSQL$SQLEXPRESS) |
| ORM | JPA (Hibernate) + Native SQL queries |
| Build | Maven (WAR) |

## Cấu trúc project

```
Project/
├── pom.xml
├── web/assets/                         # Static resources (CSS, images)
├── src/main/resources/
│   ├── application.properties          # DB config, Thymeleaf settings
│   └── templates/
│       ├── shop/                       # Customer-facing Thymeleaf views
│       │   ├── fragments.html          # Header/footer partials
│       │   ├── index.html              # Browse books (search, filter, sort, pagination)
│       │   ├── detail.html             # Book detail
│       │   ├── login.html / register.html
│       │   ├── cart.html / checkout.html
│       │   ├── my-orders.html / order-detail.html
│       │   └── order-lookup.html
│       └── admin/                      # Admin Thymeleaf views
│           ├── login.html / dashboard.html
│           ├── products.html / product-form.html
│           ├── orders.html / order-detail.html
│           └── revenue.html            # Revenue with Chart.js bar chart
└── src/java/com/project/
    ├── BookstoreApiApplication.java    # Spring Boot entry point
    ├── config/
    │   ├── DataInitializer.java        # Seeds 30+ books, 1 admin, 2 customers
    │   ├── ServletFilterConfig.java    # Registers EncodingFilter + AuthFilter
    │   └── WebRootConfig.java          # Sets web/ as document root
    ├── dao/
    │   ├── JpaSupport.java             # Shared EntityManagerFactory
    │   ├── BookDAO.java                # Book CRUD + search
    │   ├── CustomerDAO.java            # Customer CRUD + login
    │   ├── InvoiceDAO.java             # Invoice CRUD, createInvoice with stock check
    │   ├── AuthDAO.java                # Admin login
    │   ├── DashboardDAO.java           # Dashboard stats
    │   └── ReportDAO.java              # Revenue queries (day/month/year/date-range)
    ├── model/
    │   ├── Book.java (Sach), Customer.java (KhachHang)
    │   ├── Invoice.java (HoaDon), InvoiceItem.java (ChiTietHoaDon)
    │   ├── AuthUser.java (NhanVien)
    │   └── dto/
    │       ├── ShopCartItem.java
    │       ├── DashboardStats.java
    │       ├── RevenueByDate.java
    │       └── TopBookReportItem.java
    └── web/
        ├── filter/
        │   ├── EncodingFilter.java     # UTF-8 encoding
        │   └── AuthFilter.java         # Protects /admin/* routes
        └── mvc/
            ├── StoreController.java    # Customer endpoints
            └── AdminController.java    # Admin endpoints
```

## Database

- **Server**: `localhost:1433` (MSSQL$SQLEXPRESS)
- **Database**: `QLBanSach`
- **User**: `sa` / pass: `1`
- **Tables**: Sach, KhachHang, NhanVien, HoaDon, ChiTietHoaDon
- **Seed data**: 30+ sách, 1 admin, 2 khách hàng (tự động insert khi chạy app)

## Routes (Endpoints)

### Customer (`/store/*`)

| Method | Path | Chức năng |
|--------|------|-----------|
| GET | `/store/` | Browse sách (search, category filter, sort, pagination) |
| GET | `/store/detail?id=` | Xem chi tiết sách |
| GET | `/store/login` | Form đăng nhập |
| POST | `/store/login` | Xử lý đăng nhập khách hàng |
| GET | `/store/register` | Form đăng ký |
| POST | `/store/register` | Xử lý đăng ký |
| GET | `/store/logout` | Đăng xuất |
| GET | `/store/cart` | Xem giỏ hàng |
| POST | `/store/cart/add` | Thêm vào giỏ |
| POST | `/store/cart/update` | Sửa số lượng |
| GET | `/store/cart/remove?id=` | Xóa khỏi giỏ |
| GET | `/store/checkout` | Form thanh toán (yêu cầu login) |
| POST | `/store/checkout` | Xử lý thanh toán |
| GET | `/store/my-orders` | Lịch sử đơn hàng (yêu cầu login) |
| GET | `/store/order?code=` | Chi tiết đơn hàng |
| GET | `/store/order-lookup` | Form tra cứu đơn hàng |

### Admin (`/admin/*`)

| Method | Path | Chức năng |
|--------|------|-----------|
| GET | `/admin/login` | Form đăng nhập admin |
| POST | `/admin/login` | Xử lý đăng nhập |
| GET | `/admin/logout` | Đăng xuất |
| GET | `/admin/`, `/admin/dashboard` | Dashboard thống kê |
| GET | `/admin/products` | Danh sách sản phẩm |
| GET | `/admin/products/add` | Form thêm sản phẩm |
| POST | `/admin/products/add` | Thêm sản phẩm |
| GET | `/admin/products/edit/{id}` | Form sửa sản phẩm |
| POST | `/admin/products/edit/{id}` | Cập nhật sản phẩm |
| POST | `/admin/products/delete/{id}` | Xóa sản phẩm |
| GET | `/admin/orders` | Danh sách đơn hàng (filter theo trạng thái) |
| GET | `/admin/orders/{id}` | Chi tiết đơn hàng |
| POST | `/admin/orders/{id}/status` | Cập nhật trạng thái (New/Shipped/Paid) |
| GET | `/admin/revenue?date=` | Doanh thu: chọn ngày → xem tổng ngày/tháng/năm + biểu đồ 7 ngày |

## Tính năng chi tiết

### Khách hàng
1. **Đăng ký / Đăng nhập / Đăng xuất** — session-based (`STORE_CUSTOMER`)
2. **Xem sách** — phân trang (12 sản phẩm/trang), tìm theo tên, lọc theo thể loại, sort theo giá tăng/giảm
3. **Giỏ hàng** — session-based (`STORE_CART`), thêm/sửa/xóa, thanh toán yêu cầu login
4. **Đặt hàng** — kiểm tra tồn kho, trừ stock, tạo hóa đơn
5. **Lịch sử đơn hàng** — xem danh sách + chi tiết đơn đã đặt

### Admin
1. **Đăng nhập / Đăng xuất** — session-based (`STORE_ADMIN`), AuthFilter bảo vệ route `/admin/*`
2. **Quản lý sản phẩm** — CRUD (thêm/sửa/xóa sách)
3. **Quản lý đơn hàng** — xem danh sách, cập nhật trạng thái (New → Shipped → Paid)
4. **Doanh thu** — chọn ngày → hiển thị:
   - Tổng doanh thu ngày
   - Tổng doanh thu tháng
   - Tổng doanh thu năm
   - Biểu đồ cột 7 ngày gần nhất (Chart.js)

## Tài khoản test

### Admin
- Username: `admin` / Password: `admin123`

### Khách hàng
- Username: `customer1` / Password: `123`
- Username: `customer2` / Password: `123`

## Cách chạy

1. Mở SQL Server Management Studio, tạo database `QLBanSach` (hoặc chạy lần đầu app sẽ tự tạo)
2. Mở project trong IntelliJ IDEA
3. Kiểm tra file `src/main/resources/application.properties` — cập nhật password SQL Server nếu khác
4. Chạy `BookstoreApiApplication.java` (Spring Boot main class)
5. Truy cập:
   - **Customer**: http://localhost:8080/store/
   - **Admin**: http://localhost:8080/admin/login

## Lưu ý

- Cart được lưu trong session (`STORE_CART`), sẽ mất khi restart server
- Image URL của sách có thể là link ngoài hoặc đường dẫn local trong `/assets/seed/`
- Khi checkout, server tự động kiểm tra tồn kho và trừ số lượng
