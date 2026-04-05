<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.project.model.Book"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Book Detail - Bookora</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <script src="https://cdn.tailwindcss.com?plugins=forms"></script>
    <link href="https://fonts.googleapis.com/css2?family=Noto+Serif:ital,wght@0,400;0,700;1,400;1,700&family=Manrope:wght@300;400;500;600;700&display=swap" rel="stylesheet"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/shop-bookora.css" />
</head>
<body class="shop-page shop-surface shop-bg-detail text-slate-800 min-h-screen">
<!-- Stitch Screen: Book Detail - Bookora (0f2de8de95aa4138b46ac68dc0618181) -->
<%
    Book book = (Book) request.getAttribute("book");
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<% request.setAttribute("shopHeaderMode", "detail"); %>
<%@ include file="_partials/header.jspf" %>

<main class="max-w-6xl mx-auto px-6 pb-10">
    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
        <div class="mb-4 p-3 rounded-xl bg-red-100 text-red-700"><%= errorMessage %></div>
    <% } %>

    <% if (book == null) { %>
        <div class="rounded-2xl bg-white p-10 border border-slate-200 text-slate-500">Khong tim thay sach.</div>
    <% } else { %>
        <div class="rounded-3xl bg-white border border-slate-200 p-6 md:p-8 grid grid-cols-1 md:grid-cols-2 gap-8 shadow-lg">
            <div class="rounded-2xl bg-slate-100 overflow-hidden min-h-[320px]">
                <% if (book.getAnhBia() != null && !book.getAnhBia().trim().isEmpty()) { %>
                    <img src="<%= book.getAnhBia() %>" alt="<%= book.getTenSach() %>" class="w-full h-full object-cover" />
                <% } else { %>
                    <div class="h-full w-full flex items-center justify-center text-slate-400">No cover image</div>
                <% } %>
            </div>
            <div>
                <p class="text-xs uppercase tracking-[0.2em] text-amber-600 font-bold mb-2"><%= book.getTheLoai() == null ? "General" : book.getTheLoai() %></p>
                <h1 class="brand text-3xl font-extrabold text-slate-900 mb-2"><%= book.getTenSach() %></h1>
                <p class="text-slate-500 mb-5">Tac gia: <strong><%= book.getTacGia() == null ? "Dang cap nhat" : book.getTacGia() %></strong></p>
                <div class="flex items-center gap-3 mb-6">
                    <span class="text-3xl font-extrabold text-emerald-700"><%= book.getDonGia() == null ? 0 : book.getDonGia() %></span>
                    <span class="text-xs px-2 py-1 rounded-full bg-slate-100 text-slate-600">Ton kho: <%= book.getSoLuong() == null ? 0 : book.getSoLuong() %></span>
                </div>

                <form method="post" action="<%= request.getContextPath() %>/shop/cart/add" class="flex flex-wrap items-end gap-3">
                    <input type="hidden" name="id" value="<%= book.getMaSach() %>" />
                    <input type="hidden" name="redirect" value="<%= request.getRequestURI() + (request.getQueryString() == null ? "" : ("?" + request.getQueryString())) %>" />
                    <div>
                        <label class="block text-xs font-bold uppercase text-slate-500 mb-2" for="qty">So luong</label>
                        <input id="qty" name="qty" class="w-28 rounded-xl border-slate-200 bg-slate-50" type="number" min="1" max="999" value="1" />
                    </div>
                    <button type="submit" class="px-5 py-3 rounded-xl bg-slate-900 text-white font-bold">Them vao gio hang</button>
                    <a href="<%= request.getContextPath() %>/shop" class="px-5 py-3 rounded-xl bg-slate-100 font-semibold">Quay lai shop</a>
                </form>
            </div>
        </div>

        <section class="mt-6 rounded-2xl bg-white border border-slate-200 p-5">
            <h2 class="brand text-2xl font-extrabold text-slate-900 mb-3">Goi y lien quan</h2>
            <p class="text-sm text-slate-600">Quay lai trang shop de xem them sach cung the loai va tac gia gan giong.</p>
            <a href="<%= request.getContextPath() %>/shop" class="inline-block mt-3 px-4 py-2 rounded-xl bg-slate-900 text-white text-sm font-semibold">Xem them sach</a>
        </section>
    <% } %>
</main>
<%@ include file="_partials/footer.jspf" %>
</body>
</html>