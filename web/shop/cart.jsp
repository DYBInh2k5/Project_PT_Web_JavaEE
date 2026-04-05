<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="com.project.model.dto.ShopCartItem"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Shopping Cart - Bookora</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <script src="https://cdn.tailwindcss.com?plugins=forms"></script>
    <link href="https://fonts.googleapis.com/css2?family=Noto+Serif:ital,wght@0,400;0,700;1,400;1,700&family=Manrope:wght@300;400;500;600;700&display=swap" rel="stylesheet"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/shop-bookora.css" />
</head>
<body class="shop-page shop-surface shop-bg-cart text-slate-800 min-h-screen">
<!-- Stitch Screen: Shopping Cart - Bookora (f1587fcb89d54c9d879e243b300af4a4) -->
<% request.setAttribute("shopHeaderMode", "cart"); %>
<%@ include file="_partials/header.jspf" %>

<main class="max-w-7xl mx-auto px-6 pb-10">
    <h1 class="brand text-4xl font-extrabold text-slate-900 mb-4">Gio hang cua ban</h1>
    <%
        String errorMessage = (String) request.getAttribute("errorMessage");
        List<ShopCartItem> items = (List<ShopCartItem>) request.getAttribute("items");
        BigDecimal total = (BigDecimal) request.getAttribute("total");
        if (total == null) {
            total = BigDecimal.ZERO;
        }
    %>
    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
        <div class="mb-4 p-3 rounded-xl bg-red-100 text-red-700"><%= errorMessage %></div>
    <% } %>

    <% if (items == null || items.isEmpty()) { %>
        <div class="rounded-2xl bg-white border border-slate-200 p-10 text-center text-slate-500">
            <p class="text-5xl mb-3">📚</p>
            <p>Gio hang dang trong. Hay quay lai shop de them sach.</p>
        </div>
    <% } else { %>
        <div class="rounded-2xl bg-white border border-slate-200 overflow-hidden shadow-sm">
            <table class="w-full text-sm text-left">
                <thead class="bg-slate-100 text-slate-600 uppercase tracking-widest text-xs">
                    <tr>
                        <th class="px-4 py-3">Sach</th>
                        <th class="px-4 py-3">Don gia</th>
                        <th class="px-4 py-3">So luong</th>
                        <th class="px-4 py-3">Thanh tien</th>
                        <th class="px-4 py-3"></th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-slate-200">
                <% for (ShopCartItem item : items) { %>
                    <tr>
                        <td class="px-4 py-3">
                            <div class="font-semibold text-slate-900"><%= item.getTenSach() %></div>
                            <div class="text-xs text-slate-500"><%= item.getTacGia() == null ? "" : item.getTacGia() %> | Ton: <%= item.getTonKho() == null ? 0 : item.getTonKho() %></div>
                        </td>
                        <td class="px-4 py-3"><%= item.getDonGia() == null ? 0 : item.getDonGia() %></td>
                        <td class="px-4 py-3">
                            <form method="post" action="<%= request.getContextPath() %>/shop/cart/update" class="flex items-center gap-2">
                                <input type="hidden" name="id" value="<%= item.getMaSach() %>" />
                                <input class="w-24 rounded-xl border-slate-200 bg-slate-50" type="number" min="0" max="999" name="qty" value="<%= item.getSoLuong() %>" />
                                <button class="px-3 py-2 rounded-lg bg-slate-100 text-xs font-semibold" type="submit">Cap nhat</button>
                            </form>
                        </td>
                        <td class="px-4 py-3 font-semibold text-emerald-700"><%= item.getThanhTien() %></td>
                        <td class="px-4 py-3">
                            <form method="post" action="<%= request.getContextPath() %>/shop/cart/remove">
                                <input type="hidden" name="id" value="<%= item.getMaSach() %>" />
                                <button class="px-3 py-2 rounded-lg bg-red-100 text-red-700 text-xs font-semibold" type="submit">Xoa</button>
                            </form>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
        <div class="mt-4 rounded-2xl bg-white border border-slate-200 p-5 flex flex-wrap items-center justify-between gap-3">
            <div>
                <p class="text-xs uppercase text-slate-500 tracking-widest">Tong tam tinh</p>
                <p class="text-2xl font-extrabold text-slate-900"><%= total %></p>
            </div>
            <a href="<%= request.getContextPath() %>/shop/checkout" class="px-5 py-3 rounded-xl bg-slate-900 text-white font-bold">Di toi thanh toan</a>
        </div>
    <% } %>
</main>
<%@ include file="_partials/footer.jspf" %>
</body>
</html>