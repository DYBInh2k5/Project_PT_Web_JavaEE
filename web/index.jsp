<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.project.model.AuthUser"%>
<%@page import="com.project.web.auth.AuthSession"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Trang chu he thong</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <script src="https://cdn.tailwindcss.com?plugins=forms"></script>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet"/>
    <style>
        body { font-family: 'Inter', sans-serif; }
        .headline { font-family: 'Manrope', sans-serif; }
    </style>
</head>
<body class="bg-[#f7f9fb] text-[#191c1e] min-h-screen">
<div class="max-w-6xl mx-auto p-6 md:p-10">
    <%
        AuthUser user = (AuthUser) session.getAttribute(AuthSession.AUTH_USER);
    %>
    <div class="bg-white rounded-2xl shadow-sm overflow-hidden">
        <div class="bg-gradient-to-r from-[#091426] to-[#1e293b] text-white p-8 md:p-10">
            <h1 class="headline text-3xl md:text-4xl font-extrabold mb-2">He thong quan li ban sach - Java EE</h1>
            <p class="text-slate-300">Van hanh kho sach, khach hang va hoa don tren mot dashboard tap trung.</p>
        </div>

        <div class="p-8 md:p-10">
        <% if (user == null) { %>
            <p class="text-slate-600 mb-6">Ban chua dang nhap.</p>
            <div class="flex flex-wrap gap-2">
                <a class="px-4 py-2 rounded-lg bg-[#1e293b] text-white text-sm font-semibold" href="<%= request.getContextPath() %>/login">Dang nhap</a>
                <a class="px-4 py-2 rounded-lg bg-slate-100 text-sm font-semibold" href="<%= request.getContextPath() %>/db-check">Kiem tra DB</a>
            </div>
        <% } else { %>
            <div class="mb-6">
                <p class="text-slate-600">Xin chao <strong><%= user.getHoTen() == null ? user.getTaiKhoan() : user.getHoTen() %></strong> (<%= user.getVaiTro() == null ? "N/A" : user.getVaiTro() %>)</p>
            </div>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-3">
                <a class="p-4 rounded-xl bg-[#dcfce7] hover:bg-[#bbf7d0] transition-colors" href="<%= request.getContextPath() %>/shop"><strong class="block mb-1 text-[#166534]">Shop Ban Sach</strong><span class="text-sm text-[#166534]">Trang web mua sach cong khai</span></a>
                <a class="p-4 rounded-xl bg-[#eef2f7] hover:bg-[#e4ebf4] transition-colors" href="<%= request.getContextPath() %>/dashboard"><strong class="block mb-1">Dashboard</strong><span class="text-sm text-slate-600">Tong quan hoat dong</span></a>
                <a class="p-4 rounded-xl bg-[#eef2f7] hover:bg-[#e4ebf4] transition-colors" href="<%= request.getContextPath() %>/books"><strong class="block mb-1">Quan ly Sach</strong><span class="text-sm text-slate-600">Kho va thong tin sach</span></a>
                <a class="p-4 rounded-xl bg-[#eef2f7] hover:bg-[#e4ebf4] transition-colors" href="<%= request.getContextPath() %>/customers"><strong class="block mb-1">Quan ly Khach hang</strong><span class="text-sm text-slate-600">Thong tin lien he</span></a>
                <a class="p-4 rounded-xl bg-[#eef2f7] hover:bg-[#e4ebf4] transition-colors" href="<%= request.getContextPath() %>/invoices"><strong class="block mb-1">Quan ly Hoa don</strong><span class="text-sm text-slate-600">Ban hang va chi tiet</span></a>
                <a class="p-4 rounded-xl bg-[#eef2f7] hover:bg-[#e4ebf4] transition-colors" href="<%= request.getContextPath() %>/promotions"><strong class="block mb-1">Khuyen mai</strong><span class="text-sm text-slate-600">Coupon va uu dai</span></a>
                <a class="p-4 rounded-xl bg-[#eef2f7] hover:bg-[#e4ebf4] transition-colors" href="<%= request.getContextPath() %>/purchases"><strong class="block mb-1">Nhap hang</strong><span class="text-sm text-slate-600">Cap nhat ton kho</span></a>
                <a class="p-4 rounded-xl bg-[#eef2f7] hover:bg-[#e4ebf4] transition-colors" href="<%= request.getContextPath() %>/returns"><strong class="block mb-1">Tra hang</strong><span class="text-sm text-slate-600">Doi tra va cap nhat ton kho</span></a>
                <a class="p-4 rounded-xl bg-[#eef2f7] hover:bg-[#e4ebf4] transition-colors" href="<%= request.getContextPath() %>/reports/revenue"><strong class="block mb-1">Bao cao</strong><span class="text-sm text-slate-600">Doanh thu va top sach</span></a>
                <a class="p-4 rounded-xl bg-[#fee2e2] hover:bg-[#fecaca] transition-colors" href="<%= request.getContextPath() %>/logout"><strong class="block mb-1 text-red-700">Dang xuat</strong><span class="text-sm text-red-600">Ket thuc phien lam viec</span></a>
            </div>
        <% } %>
        </div>
    </div>
</div>
</body>
</html>
