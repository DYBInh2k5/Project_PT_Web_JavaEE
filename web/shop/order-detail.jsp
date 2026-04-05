<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.project.model.Invoice"%>
<%@page import="com.project.model.InvoiceItem"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Order Detail - Bookora</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <script src="https://cdn.tailwindcss.com?plugins=forms"></script>
    <link href="https://fonts.googleapis.com/css2?family=Noto+Serif:ital,wght@0,400;0,700;1,400;1,700&family=Manrope:wght@300;400;500;600;700&display=swap" rel="stylesheet"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/shop-bookora.css" />
</head>
<body class="shop-page shop-surface shop-bg-order text-slate-800 min-h-screen">
<!-- Stitch Screen: Order Detail - Bookora (a96af57d17e94ba282bac1c708f00fb6) -->
<% request.setAttribute("shopHeaderMode", "order-detail"); %>
<%@ include file="_partials/header.jspf" %>

<main class="max-w-7xl mx-auto px-6 pb-10">
    <%
        String errorMessage = (String) request.getAttribute("errorMessage");
        Invoice invoice = (Invoice) request.getAttribute("invoice");
        List<InvoiceItem> items = (List<InvoiceItem>) request.getAttribute("items");
        String orderCode = (String) request.getAttribute("orderCode");
        boolean verificationRequired = Boolean.TRUE.equals(request.getAttribute("verificationRequired"));
    %>

    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
        <div class="mb-4 p-3 rounded-xl bg-red-100 text-red-700"><%= errorMessage %></div>
    <% } %>

    <% if (verificationRequired) { %>
        <section class="rounded-2xl bg-white border border-slate-200 p-6 mb-5">
            <h2 class="text-xl font-extrabold text-slate-900 mb-2">Xac thuc de xem don hang</h2>
            <p class="text-sm text-slate-600 mb-4">Nhap so dien thoai hoac email da dung khi dat don.</p>
            <form method="get" action="<%= request.getContextPath() %>/shop/order" class="grid grid-cols-1 md:grid-cols-4 gap-3">
                <input type="hidden" name="code" value="<%= orderCode == null ? "" : orderCode %>" />
                <input class="rounded-xl border-slate-200 bg-slate-50" type="text" name="phone" placeholder="So dien thoai" value="<%= request.getAttribute("phone") == null ? "" : request.getAttribute("phone") %>" />
                <input class="rounded-xl border-slate-200 bg-slate-50" type="email" name="email" placeholder="Email" value="<%= request.getAttribute("email") == null ? "" : request.getAttribute("email") %>" />
                <button type="submit" class="px-4 py-2 rounded-xl bg-slate-900 text-white font-bold">Xac thuc</button>
            </form>
        </section>
    <% } %>

    <% if (invoice != null) { %>
        <section class="rounded-2xl bg-white border border-slate-200 p-6 mb-5">
            <h1 class="brand text-3xl font-extrabold text-slate-900 mb-2">Chi tiet don hang</h1>
            <p class="text-sm text-slate-600 mb-4">Ma don cong khai duoc bao ve bang session hoac xac thuc lien he.</p>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-3 text-sm">
                <div class="p-3 rounded-xl bg-slate-50"><span class="text-xs uppercase text-slate-500 block mb-1">Ma don</span><strong><%= orderCode == null ? "" : orderCode %></strong></div>
                <div class="p-3 rounded-xl bg-slate-50"><span class="text-xs uppercase text-slate-500 block mb-1">Ma hoa don</span><strong><%= invoice.getMaHD() %></strong></div>
                <div class="p-3 rounded-xl bg-slate-50"><span class="text-xs uppercase text-slate-500 block mb-1">Ngay lap</span><strong><%= invoice.getNgayLap() %></strong></div>
                <div class="p-3 rounded-xl bg-slate-50"><span class="text-xs uppercase text-slate-500 block mb-1">Khach hang</span><strong><%= invoice.getTenKH() == null ? "Khach le" : invoice.getTenKH() %></strong></div>
                <div class="p-3 rounded-xl bg-slate-50"><span class="text-xs uppercase text-slate-500 block mb-1">So dien thoai</span><strong><%= invoice.getDienThoaiKH() == null ? "" : invoice.getDienThoaiKH() %></strong></div>
                <div class="p-3 rounded-xl bg-slate-50"><span class="text-xs uppercase text-slate-500 block mb-1">Tong tien</span><strong class="text-emerald-700"><%= invoice.getTongTien() == null ? 0 : invoice.getTongTien() %></strong></div>
            </div>
            <div class="mt-4">
                <a class="px-3 py-2 rounded-lg bg-slate-900 text-white text-xs font-semibold" href="<%= request.getContextPath() %>/shop/order/pdf?code=<%= orderCode %>">Tai hoa don PDF</a>
            </div>
        </section>

        <section class="rounded-2xl bg-white border border-slate-200 overflow-hidden">
            <table class="w-full text-sm text-left">
                <thead class="bg-slate-100 text-slate-600 uppercase tracking-widest text-xs">
                    <tr>
                        <th class="px-4 py-3">Sach</th>
                        <th class="px-4 py-3">So luong</th>
                        <th class="px-4 py-3">Don gia</th>
                        <th class="px-4 py-3">Thanh tien</th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-slate-200">
                <% if (items == null || items.isEmpty()) { %>
                    <tr><td class="px-4 py-6 text-slate-500" colspan="4">Khong co chi tiet don hang.</td></tr>
                <% } else { for (InvoiceItem item : items) { %>
                    <tr>
                        <td class="px-4 py-3"><%= item.getTenSach() == null ? ("Ma " + item.getMaSach()) : item.getTenSach() %></td>
                        <td class="px-4 py-3"><%= item.getSoLuong() %></td>
                        <td class="px-4 py-3"><%= item.getDonGia() %></td>
                        <td class="px-4 py-3 font-semibold text-emerald-700"><%= item.getThanhTien() %></td>
                    </tr>
                <% } } %>
                </tbody>
            </table>
        </section>
    <% } %>
</main>
<%@ include file="_partials/footer.jspf" %>
</body>
</html>