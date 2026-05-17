<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.project.model.AuthUser"%>
<%@page import="com.project.web.auth.AuthSession"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Bookstore Spring Boot</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU90FeRpok6YctnYmDr5bK5fQvvoS5B0jz0h3b4d6tbt0P" crossorigin="anonymous">
    <style>
        body {
            background: linear-gradient(135deg, #0f172a 0%, #1e293b 45%, #e2e8f0 45%, #f8fafc 100%);
            min-height: 100vh;
        }
        .hero-card {
            backdrop-filter: blur(16px);
            background: rgba(15, 23, 42, 0.72);
            border: 1px solid rgba(148, 163, 184, 0.22);
            box-shadow: 0 24px 80px rgba(15, 23, 42, 0.25);
        }
    </style>
</head>
<body>
<%
    AuthUser user = (AuthUser) session.getAttribute(AuthSession.AUTH_USER);
    String contextPath = request.getContextPath();
%>
<div class="container py-5 py-lg-6">
    <div class="row justify-content-center">
        <div class="col-12 col-xl-10">
            <div class="hero-card text-white rounded-4 overflow-hidden">
                <div class="p-4 p-md-5 p-lg-6">
                    <div class="d-flex flex-wrap justify-content-between align-items-start gap-3 mb-4">
                        <div>
                            <span class="badge text-bg-warning text-dark rounded-pill mb-3">Spring Boot + JPA + Bootstrap + SQL Server</span>
                            <h1 class="display-5 fw-bold mb-3">Project mon Phat trien web nen tang Java EE</h1>
                            <p class="lead text-white-50 mb-0">Ung dung shop ban sach da chuan hoa de chay tren IntelliJ IDE voi Spring Boot, JPA va SQL Server.</p>
                        </div>
                        <div class="text-end">
                            <div class="small text-white-50">Trang thai dang nhap</div>
                            <div class="fw-semibold"><%= user == null ? "Chua dang nhap" : (user.getHoTen() == null ? user.getTaiKhoan() : user.getHoTen()) %></div>
                        </div>
                    </div>

                    <div class="row g-3 mb-4">
                        <div class="col-md-4">
                            <div class="bg-white text-dark rounded-4 p-4 h-100">
                                <div class="text-uppercase text-primary small fw-semibold">Backend</div>
                                <h2 class="h5 fw-bold mb-2">Spring Boot + JPA</h2>
                                <p class="mb-0 text-secondary">Cau hinh san san de chay trong IntelliJ voi Maven va Hibernate.</p>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="bg-white text-dark rounded-4 p-4 h-100">
                                <div class="text-uppercase text-success small fw-semibold">Database</div>
                                <h2 class="h5 fw-bold mb-2">SQL Server QLBanSach</h2>
                                <p class="mb-0 text-secondary">Ket noi truc tiep den database hoc phan qua JDBC + JPA.</p>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="bg-white text-dark rounded-4 p-4 h-100">
                                <div class="text-uppercase text-warning small fw-semibold">UI</div>
                                <h2 class="h5 fw-bold mb-2">Bootstrap 5</h2>
                                <p class="mb-0 text-secondary">Giao dien vao trang da san sang de demo, bao cao va nop bai.</p>
                            </div>
                        </div>
                    </div>

                    <div class="d-flex flex-wrap gap-2 mb-4">
                        <a class="btn btn-warning btn-lg fw-semibold" href="<%= contextPath %>/shop">Vao shop</a>
                        <a class="btn btn-outline-light btn-lg fw-semibold" href="<%= contextPath %>/api/db-check">Kiem tra DB</a>
                        <a class="btn btn-outline-light btn-lg fw-semibold" href="<%= contextPath %>/login">Dang nhap</a>
                    </div>

                    <div class="row g-3">
                        <div class="col-lg-8">
                            <div class="bg-white text-dark rounded-4 p-4 h-100">
                                <h2 class="h4 fw-bold mb-3">Huong dan chay nhanh trong IntelliJ</h2>
                                <ol class="mb-0 text-secondary">
                                    <li>Mo project bang IntelliJ nhu mot Maven project.</li>
                                    <li>Chon JDK 21.</li>
                                    <li>Chay class <span class="fw-semibold">com.project.BookstoreApiApplication</span>.</li>
                                    <li>Mo <span class="fw-semibold"><%= contextPath %>/api/db-check</span> de kiem tra ket noi SQL Server.</li>
                                </ol>
                            </div>
                        </div>
                        <div class="col-lg-4">
                            <div class="bg-white text-dark rounded-4 p-4 h-100">
                                <h2 class="h5 fw-bold mb-3">Liên ket nhanh</h2>
                                <div class="d-grid gap-2">
                                    <a class="btn btn-light border" href="<%= contextPath %>/dashboard">Dashboard</a>
                                    <a class="btn btn-light border" href="<%= contextPath %>/books">Quan ly sach</a>
                                    <a class="btn btn-light border" href="<%= contextPath %>/shop/order-lookup">Tra cuu don hang</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>
