<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.project.model.Book"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><%= Boolean.TRUE.equals(request.getAttribute("isEdit")) ? "Sua sach" : "Them sach" %></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <script src="https://cdn.tailwindcss.com?plugins=forms"></script>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet"/>
    <style>
        body { font-family: 'Inter', sans-serif; }
        .headline { font-family: 'Manrope', sans-serif; }
    </style>
</head>
<body class="bg-[#f7f9fb] text-[#191c1e]">
<%
    Book book = (Book) request.getAttribute("book");
    if (book == null) {
        book = new Book();
    }
    boolean isEdit = Boolean.TRUE.equals(request.getAttribute("isEdit"));
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<div class="max-w-4xl mx-auto p-6 md:p-8">
<div class="bg-white rounded-xl shadow-sm p-8">
    <div class="flex items-center justify-between gap-4 mb-6">
        <div>
            <h1 class="headline text-2xl font-extrabold text-[#091426]"><%= isEdit ? "Sua sach" : "Them sach" %></h1>
            <p class="text-sm text-slate-500">Quan ly thong tin sach trong kho va gia ban.</p>
        </div>
        <a class="px-4 py-2 rounded-lg bg-slate-100 text-sm font-semibold" href="<%= request.getContextPath() %>/books">Quay lai</a>
    </div>

    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
        <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-700"><%= errorMessage %></div>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %><%= isEdit ? "/books/edit?id=" + book.getMaSach() : "/books/new" %>" class="space-y-4">
        <div>
            <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="tenSach">Ten sach *</label>
            <input class="w-full rounded-xl border-slate-200 bg-slate-50" type="text" id="tenSach" name="tenSach" required value="<%= book.getTenSach() == null ? "" : book.getTenSach() %>" />
        </div>

        <div>
            <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="tacGia">Tac gia</label>
            <input class="w-full rounded-xl border-slate-200 bg-slate-50" type="text" id="tacGia" name="tacGia" value="<%= book.getTacGia() == null ? "" : book.getTacGia() %>" />
        </div>

        <div>
            <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="theLoai">The loai</label>
            <input class="w-full rounded-xl border-slate-200 bg-slate-50" type="text" id="theLoai" name="theLoai" value="<%= book.getTheLoai() == null ? "" : book.getTheLoai() %>" />
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
                <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="donGia">Don gia</label>
                <input class="w-full rounded-xl border-slate-200 bg-slate-50" type="number" id="donGia" name="donGia" step="0.01" min="0" value="<%= book.getDonGia() == null ? "" : book.getDonGia() %>" />
            </div>

            <div>
                <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="soLuong">So luong</label>
                <input class="w-full rounded-xl border-slate-200 bg-slate-50" type="number" id="soLuong" name="soLuong" min="0" value="<%= book.getSoLuong() == null ? "" : book.getSoLuong() %>" />
            </div>
        </div>

        <div>
            <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="anhBia">Anh bia (URL/duong dan)</label>
            <input class="w-full rounded-xl border-slate-200 bg-slate-50" type="text" id="anhBia" name="anhBia" value="<%= book.getAnhBia() == null ? "" : book.getAnhBia() %>" />
        </div>

        <div class="flex flex-wrap gap-2 pt-2">
            <button type="submit" class="px-4 py-2 rounded-lg bg-[#1e293b] text-white text-sm font-semibold">Luu</button>
            <a class="px-4 py-2 rounded-lg bg-slate-100 text-sm font-semibold" href="<%= request.getContextPath() %>/books">Huy</a>
            <a class="px-4 py-2 rounded-lg bg-slate-100 text-sm font-semibold" href="<%= request.getContextPath() %>/reports/revenue">Bao cao</a>
        </div>
    </form>
</div>
</div>
</body>
</html>
