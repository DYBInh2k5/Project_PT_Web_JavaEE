<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.project.model.dto.ShopOrderSummary"%>
<%@page import="com.project.model.Invoice"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Orders - Bookora</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <script src="https://cdn.tailwindcss.com?plugins=forms"></script>
    <link href="https://fonts.googleapis.com/css2?family=Noto+Serif:ital,wght@0,400;0,700;1,400;1,700&family=Manrope:wght@300;400;500;600;700&display=swap" rel="stylesheet"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/shop-bookora.css" />
</head>
<body class="shop-page shop-surface shop-bg-orders text-slate-800 min-h-screen">
<!-- Stitch Screen: My Orders - Bookora (8eaf66721b064da683262e00b2300708) -->
<% request.setAttribute("shopHeaderMode", "my-orders"); %>
<%@ include file="_partials/header.jspf" %>

<main class="max-w-7xl mx-auto px-6 pb-10">
    <h1 class="brand text-4xl font-extrabold text-slate-900 mb-2">Don hang cua toi</h1>
    <p class="text-slate-600 mb-4">Danh sach don da dat trong phien hien tai, co the mo chi tiet theo ma don cong khai.</p>

    <%
        String errorMessage = (String) request.getAttribute("errorMessage");
        List<ShopOrderSummary> orders = (List<ShopOrderSummary>) request.getAttribute("orders");
    %>

    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
        <div class="mb-4 p-3 rounded-xl bg-red-100 text-red-700"><%= errorMessage %></div>
    <% } %>

    <% if (orders == null || orders.isEmpty()) { %>
        <div class="rounded-2xl bg-white border border-slate-200 p-10 text-center text-slate-500">Chua co don hang nao trong phien mua sam nay.</div>
    <% } else { %>
        <div class="rounded-2xl bg-white border border-slate-200 overflow-hidden shadow-sm">
            <table class="w-full text-sm text-left">
                <thead class="bg-slate-100 text-slate-600 uppercase tracking-widest text-xs">
                    <tr>
                        <th class="px-4 py-3">Ma HD</th>
                        <th class="px-4 py-3">Ma don</th>
                        <th class="px-4 py-3">Ngay lap</th>
                        <th class="px-4 py-3">So luong sach</th>
                        <th class="px-4 py-3">Tong tien</th>
                        <th class="px-4 py-3">Khach hang</th>
                        <th class="px-4 py-3">Thao tac</th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-slate-200">
                <% for (ShopOrderSummary order : orders) {
                    Invoice invoice = order.getInvoice();
                %>
                    <tr>
                        <td class="px-4 py-3 font-semibold"><%= invoice.getMaHD() %></td>
                        <td class="px-4 py-3"><%= order.getOrderCode() == null ? "" : order.getOrderCode() %></td>
                        <td class="px-4 py-3"><%= invoice.getNgayLap() %></td>
                        <td class="px-4 py-3"><%= order.getItemCount() %></td>
                        <td class="px-4 py-3 font-semibold text-emerald-700"><%= invoice.getTongTien() == null ? 0 : invoice.getTongTien() %></td>
                        <td class="px-4 py-3"><%= invoice.getTenKH() == null ? "Khach le" : invoice.getTenKH() %></td>
                        <td class="px-4 py-3">
                            <a class="px-3 py-2 rounded-lg bg-slate-100 text-xs font-semibold" href="<%= request.getContextPath() %>/shop/order?code=<%= order.getOrderCode() %>">Xem chi tiet</a>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    <% } %>
</main>
<%@ include file="_partials/footer.jspf" %>
</body>
</html>