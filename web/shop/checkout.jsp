<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="com.project.model.Promotion"%>
<%@page import="com.project.model.dto.ShopCartItem"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Checkout - Bookora</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <script src="https://cdn.tailwindcss.com?plugins=forms"></script>
    <link href="https://fonts.googleapis.com/css2?family=Noto+Serif:ital,wght@0,400;0,700;1,400;1,700&family=Manrope:wght@300;400;500;600;700&display=swap" rel="stylesheet"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/shop-bookora.css" />
</head>
<body class="shop-page shop-surface shop-bg-checkout text-slate-800 min-h-screen">
<!-- Stitch Screen: Checkout - Bookora (3ef0ff2a532f46aa88b091adfe181931) -->
<% request.setAttribute("shopHeaderMode", "checkout"); %>
<%@ include file="_partials/header.jspf" %>

<main class="max-w-7xl mx-auto px-6 pb-10">
    <%
        String success = request.getParameter("success");
        String invoiceId = request.getParameter("invoiceId");
        String orderCode = request.getParameter("orderCode");
        String errorMessage = (String) request.getAttribute("errorMessage");
        List<ShopCartItem> items = (List<ShopCartItem>) request.getAttribute("items");
        BigDecimal subtotal = (BigDecimal) request.getAttribute("subtotal");
        if (subtotal == null) subtotal = BigDecimal.ZERO;
    %>

    <% if ("1".equals(success)) { %>
        <div class="mb-5 p-4 rounded-2xl bg-emerald-100 text-emerald-700">
            Dat hang thanh cong. Ma hoa don cua ban: <strong><%= invoiceId == null ? "N/A" : invoiceId %></strong>.
            <% if (orderCode != null && !orderCode.isEmpty()) { %>
                Ma don cong khai: <strong><%= orderCode %></strong>.
            <% } %>
            <a class="underline ml-2" href="<%= request.getContextPath() %>/shop">Tiep tuc mua sach</a>
            <% if (orderCode != null && !orderCode.isEmpty()) { %>
                <a class="underline ml-2" href="<%= request.getContextPath() %>/shop/order?code=<%= orderCode %>">Xem chi tiet don</a>
            <% } %>
        </div>
    <% } %>

    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
        <div class="mb-5 p-4 rounded-2xl bg-red-100 text-red-700"><%= errorMessage %></div>
    <% } %>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <section class="lg:col-span-2 rounded-2xl bg-white border border-slate-200 p-6 shadow-sm">
            <h1 class="brand text-3xl font-extrabold text-slate-900 mb-5">Thong tin thanh toan</h1>
            <form method="post" action="<%= request.getContextPath() %>/shop/checkout" class="space-y-4">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                        <label class="block text-xs font-bold uppercase text-slate-500 mb-2" for="fullName">Ho ten nguoi nhan (tu chon)</label>
                        <input id="fullName" name="fullName" type="text" class="w-full rounded-xl border-slate-200 bg-slate-50" value="<%= request.getAttribute("fullName") == null ? "" : request.getAttribute("fullName") %>" />
                    </div>
                    <div>
                        <label class="block text-xs font-bold uppercase text-slate-500 mb-2" for="phone">So dien thoai</label>
                        <input id="phone" name="phone" type="text" class="w-full rounded-xl border-slate-200 bg-slate-50" value="<%= request.getAttribute("phone") == null ? "" : request.getAttribute("phone") %>" />
                    </div>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                        <label class="block text-xs font-bold uppercase text-slate-500 mb-2" for="email">Email</label>
                        <input id="email" name="email" type="email" class="w-full rounded-xl border-slate-200 bg-slate-50" value="<%= request.getAttribute("email") == null ? "" : request.getAttribute("email") %>" />
                    </div>
                    <div>
                        <label class="block text-xs font-bold uppercase text-slate-500 mb-2" for="couponCode">Coupon</label>
                        <input id="couponCode" name="couponCode" type="text" class="w-full rounded-xl border-slate-200 bg-slate-50" placeholder="Nhap ma coupon" value="<%= request.getAttribute("couponCode") == null ? "" : request.getAttribute("couponCode") %>" />
                    </div>
                </div>

                <div>
                    <label class="block text-xs font-bold uppercase text-slate-500 mb-2" for="address">Dia chi giao hang</label>
                    <textarea id="address" name="address" rows="3" class="w-full rounded-xl border-slate-200 bg-slate-50"><%= request.getAttribute("address") == null ? "" : request.getAttribute("address") %></textarea>
                </div>

                <button type="submit" class="px-5 py-3 rounded-xl bg-slate-900 text-white font-bold shadow hover:shadow-lg transition-shadow">Xac nhan dat hang</button>
            </form>
        </section>

        <aside class="rounded-2xl bg-white border border-slate-200 p-6 shadow-sm">
            <h2 class="brand text-2xl font-extrabold text-slate-900 mb-4">Don hang</h2>
            <% if (items == null || items.isEmpty()) { %>
                <p class="text-slate-500">Gio hang trong.</p>
            <% } else { %>
                <div class="space-y-3 mb-4">
                    <% for (ShopCartItem item : items) { %>
                        <div class="flex items-start justify-between gap-2">
                            <div>
                                <p class="text-sm font-semibold"><%= item.getTenSach() %></p>
                                <p class="text-xs text-slate-500">x<%= item.getSoLuong() %></p>
                            </div>
                            <p class="text-sm font-bold text-emerald-700"><%= item.getThanhTien() %></p>
                        </div>
                    <% } %>
                </div>
                <div class="pt-4 border-t border-slate-200">
                    <p class="text-xs uppercase tracking-widest text-slate-500">Tam tinh</p>
                    <p class="text-2xl font-extrabold text-slate-900"><%= subtotal %></p>
                    <p class="text-xs text-slate-500 mt-2">Coupon hop le (% / COUPON / TANG1) se duoc ap dung khi dat hang.</p>
                </div>
            <% } %>

            <%
                List<Promotion> promotions = (List<Promotion>) request.getAttribute("promotions");
                if (promotions != null && !promotions.isEmpty()) {
            %>
                <div class="mt-5 pt-4 border-t border-slate-200">
                    <p class="text-xs uppercase tracking-widest text-slate-500 mb-2">Coupon dang hieu luc</p>
                    <div class="space-y-2 text-xs text-slate-600 max-h-44 overflow-auto pr-1">
                    <% for (Promotion p : promotions) { %>
                        <div class="flex items-center justify-between gap-2 bg-slate-50 rounded-lg px-2 py-1">
                            <span><%= p.getMaCoupon() == null ? ("KM #" + p.getMaKM()) : p.getMaCoupon() %></span>
                            <span><%= p.getHinhThuc() == null ? "" : p.getHinhThuc() %> - <%= p.getGiaTri() == null ? 0 : p.getGiaTri() %></span>
                        </div>
                    <% } %>
                    </div>
                </div>
            <% } %>
        </aside>
    </div>
</main>
<%@ include file="_partials/footer.jspf" %>
</body>
</html>