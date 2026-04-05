<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.project.model.Book"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Shop Home - Bookora</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <script src="https://cdn.tailwindcss.com?plugins=forms"></script>
    <link href="https://fonts.googleapis.com/css2?family=Noto+Serif:ital,wght@0,400;0,700;1,400;1,700&family=Manrope:wght@300;400;500;600;700&display=swap" rel="stylesheet"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/shop-bookora.css" />
</head>
<body class="shop-page shop-surface shop-bg-home text-slate-800 min-h-screen">
<!-- Stitch Screen: Shop Home - Bookora (d6db63e0b4094dee8ffea5d5c268dbf1) -->
<% request.setAttribute("shopHeaderMode", "home"); %>
<%@ include file="_partials/header.jspf" %>

<main class="max-w-7xl mx-auto px-6 pb-12">
    <section class="rounded-3xl glass-card p-8 md:p-10 border border-white shadow-lg mb-6">
        <h1 class="brand text-4xl md:text-5xl leading-tight font-extrabold text-slate-900 mb-3">Shop ban sach truc tuyen tu QLBanSach</h1>
        <p class="text-slate-600 max-w-3xl mb-6">Tim sach theo the loai, them vao gio hang va thanh toan nhanh. Ton kho va hoa don duoc dong bo truc tiep voi he thong quan tri.</p>
        <form method="get" action="<%= request.getContextPath() %>/shop" class="flex flex-col md:flex-row gap-2 md:items-center">
            <input class="w-full md:max-w-xl rounded-2xl border-slate-200 bg-white" type="text" name="q" value="<%= request.getAttribute("q") %>" placeholder="Tim theo ten sach, tac gia, the loai" />
            <button class="px-5 py-2 rounded-2xl bg-amber-400 hover:bg-amber-300 text-slate-900 font-bold" type="submit">Tim sach</button>
        </form>
    </section>

    <section class="rounded-2xl bg-slate-900 text-slate-100 p-4 md:p-5 mb-8 border border-slate-800 shadow-sm">
        <p class="text-xs uppercase tracking-[0.2em] text-amber-300 mb-2">Promo Strip</p>
        <div class="flex flex-wrap gap-2 text-xs md:text-sm">
            <span class="px-3 py-1 rounded-full bg-white/10">WELCOME10 - giam %</span>
            <span class="px-3 py-1 rounded-full bg-white/10">SHIP15K - COUPON</span>
            <span class="px-3 py-1 rounded-full bg-white/10">MUA2TANG1 - TANG1</span>
        </div>
    </section>

    <%
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (errorMessage != null && !errorMessage.isEmpty()) {
    %>
    <div class="mb-6 p-3 rounded-xl bg-red-100 text-red-700"><%= errorMessage %></div>
    <% } %>

    <section class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
    <%
        List<Book> books = (List<Book>) request.getAttribute("books");
        if (books == null || books.isEmpty()) {
    %>
        <div class="sm:col-span-2 lg:col-span-4 rounded-2xl bg-white p-10 text-center text-slate-500 border border-slate-200">Khong tim thay sach phu hop.</div>
    <%
        } else {
            for (Book b : books) {
    %>
        <article class="rounded-2xl bg-white border border-slate-200 p-4 flex flex-col gap-3 shadow-sm hover:shadow-lg hover:-translate-y-0.5 transition-all">
            <a href="<%= request.getContextPath() %>/shop/book?id=<%= b.getMaSach() %>" class="block rounded-xl bg-slate-100 h-44 overflow-hidden">
                <% if (b.getAnhBia() != null && !b.getAnhBia().trim().isEmpty()) { %>
                    <img src="<%= b.getAnhBia() %>" alt="<%= b.getTenSach() %>" class="w-full h-full object-cover" />
                <% } else { %>
                    <div class="w-full h-full flex items-center justify-center text-slate-400 text-sm">No cover</div>
                <% } %>
            </a>
            <div class="flex-1">
                <a href="<%= request.getContextPath() %>/shop/book?id=<%= b.getMaSach() %>" class="font-bold text-slate-900 line-clamp-2"><%= b.getTenSach() == null ? "(Khong ten)" : b.getTenSach() %></a>
                <p class="text-sm text-slate-500 mt-1"><%= b.getTacGia() == null ? "Tac gia dang cap nhat" : b.getTacGia() %></p>
                <p class="text-xs text-slate-400 mt-1">Ton kho: <%= b.getSoLuong() == null ? 0 : b.getSoLuong() %></p>
            </div>
            <div class="flex items-center justify-between gap-2">
                <span class="text-lg font-extrabold text-emerald-700"><%= b.getDonGia() == null ? 0 : b.getDonGia() %></span>
                <form method="post" action="<%= request.getContextPath() %>/shop/cart/add" class="flex items-center gap-2">
                    <input type="hidden" name="id" value="<%= b.getMaSach() %>" />
                    <input type="hidden" name="qty" value="1" />
                    <input type="hidden" name="redirect" value="<%= request.getRequestURI() + (request.getQueryString() == null ? "" : ("?" + request.getQueryString())) %>" />
                    <button type="submit" class="px-3 py-2 rounded-xl bg-slate-900 text-white text-xs font-bold">Them vao gio</button>
                </form>
            </div>
        </article>
    <%
            }
        }
    %>
    </section>
</main>
<%@ include file="_partials/footer.jspf" %>
</body>
</html>