<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.project.model.Book"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quan ly sach</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <script src="https://cdn.tailwindcss.com?plugins=forms"></script>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet"/>
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined" rel="stylesheet"/>
    <style>
        body { font-family: 'Inter', sans-serif; }
        .headline { font-family: 'Manrope', sans-serif; }
    </style>
</head>
<body class="bg-[#f7f9fb] text-[#191c1e]">
<aside class="h-screen w-72 fixed left-0 top-0 bg-[#f2f4f6] flex flex-col py-8 z-50">
    <div class="px-8 mb-10">
        <div class="flex items-center gap-3">
            <div class="w-10 h-10 bg-[#1e293b] rounded-lg flex items-center justify-center text-white"><span class="material-symbols-outlined">book_4</span></div>
            <div>
                <h2 class="headline text-[#091426] font-extrabold text-lg">Library Admin</h2>
                <p class="text-xs text-slate-500 uppercase tracking-widest font-semibold">Management Portal</p>
            </div>
        </div>
    </div>
    <nav class="flex-1 space-y-1">
        <a class="text-slate-600 px-8 py-3 flex items-center gap-4 hover:text-[#006c49]" href="<%= request.getContextPath() %>/dashboard"><span class="material-symbols-outlined">dashboard</span><span class="text-sm">Dashboard</span></a>
        <a class="bg-white text-[#091426] font-bold rounded-r-full shadow-sm px-8 py-3 flex items-center gap-4" href="<%= request.getContextPath() %>/books"><span class="material-symbols-outlined">auto_stories</span><span class="text-sm">Inventory</span></a>
        <a class="text-slate-600 px-8 py-3 flex items-center gap-4 hover:text-[#006c49]" href="<%= request.getContextPath() %>/customers"><span class="material-symbols-outlined">group</span><span class="text-sm">Customers</span></a>
        <a class="text-slate-600 px-8 py-3 flex items-center gap-4 hover:text-[#006c49]" href="<%= request.getContextPath() %>/invoices"><span class="material-symbols-outlined">receipt_long</span><span class="text-sm">Invoices</span></a>
        <a class="text-slate-600 px-8 py-3 flex items-center gap-4 hover:text-[#006c49]" href="<%= request.getContextPath() %>/reports/revenue"><span class="material-symbols-outlined">analytics</span><span class="text-sm">Reports</span></a>
    </nav>
    <div class="px-8 mt-auto pt-8 border-t border-slate-200">
        <a class="text-slate-600 py-3 flex items-center gap-4 hover:text-red-600" href="<%= request.getContextPath() %>/logout"><span class="material-symbols-outlined">logout</span><span class="text-sm">Logout</span></a>
    </div>
</aside>

<main class="ml-72 min-h-screen p-8">
    <div class="bg-white rounded-xl shadow-sm p-8">
        <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4 mb-6">
            <div>
                <h1 class="headline text-2xl font-extrabold text-[#091426]">Book Inventory</h1>
                <p class="text-sm text-slate-500">Track, search and curate catalog titles.</p>
            </div>
            <a class="inline-flex items-center gap-2 px-4 py-3 rounded-xl bg-[#1e293b] text-white text-sm font-semibold" href="<%= request.getContextPath() %>/books/new">
                <span class="material-symbols-outlined text-base">add</span>Them sach
            </a>
        </div>

    <%
        String msg = request.getParameter("msg");
        String errorMessage = (String) request.getAttribute("errorMessage");
        if ("created".equals(msg)) {
    %>
        <div class="mb-4 p-3 rounded-lg bg-green-100 text-green-700">Da them sach moi.</div>
    <% } else if ("updated".equals(msg)) { %>
        <div class="mb-4 p-3 rounded-lg bg-green-100 text-green-700">Da cap nhat sach.</div>
    <% } else if ("deleted".equals(msg)) { %>
        <div class="mb-4 p-3 rounded-lg bg-green-100 text-green-700">Da xoa sach.</div>
    <% } else if ("delete_failed".equals(msg)) { %>
        <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-700">Khong the xoa sach (co the dang duoc tham chieu boi du lieu khac).</div>
    <% }
       if (errorMessage != null && !errorMessage.isEmpty()) { %>
        <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-700"><%= errorMessage %></div>
    <% } %>

    <div class="flex flex-col md:flex-row gap-3 items-start md:items-center justify-between mb-6">
        <form method="get" action="<%= request.getContextPath() %>/books" class="w-full md:max-w-xl flex gap-2">
            <input class="w-full rounded-xl border-slate-200 bg-slate-50" type="text" name="q" value="<%= request.getAttribute("q") %>" placeholder="Tim theo ten sach, tac gia, the loai" />
            <button type="submit" class="px-4 rounded-xl bg-slate-200 font-semibold">Tim</button>
        </form>
        <div class="flex gap-2">
            <a class="px-4 py-2 rounded-lg bg-slate-100 text-sm" href="<%= request.getContextPath() %>/dashboard">Dashboard</a>
            <a class="px-4 py-2 rounded-lg bg-slate-100 text-sm" href="<%= request.getContextPath() %>/reports/revenue">Bao cao</a>
        </div>
    </div>

    <div class="overflow-x-auto rounded-xl border border-slate-200">
        <table class="w-full text-sm text-left">
            <thead class="bg-[#f2f4f6] text-slate-600 uppercase tracking-widest text-xs">
                <tr>
                    <th class="px-4 py-3">Ma</th>
                    <th class="px-4 py-3">Ten sach</th>
                    <th class="px-4 py-3">Tac gia</th>
                    <th class="px-4 py-3">The loai</th>
                    <th class="px-4 py-3">Don gia</th>
                    <th class="px-4 py-3">So luong</th>
                    <th class="px-4 py-3">Anh bia</th>
                    <th class="px-4 py-3">Thao tac</th>
                </tr>
            </thead>
            <tbody class="divide-y divide-slate-200">
        <%
            List<Book> books = (List<Book>) request.getAttribute("books");
            if (books == null || books.isEmpty()) {
        %>
            <tr>
                <td class="px-4 py-6 text-slate-500" colspan="8">Khong co du lieu sach.</td>
            </tr>
        <%
            } else {
                for (Book b : books) {
        %>
            <tr>
                <td class="px-4 py-3"><%= b.getMaSach() %></td>
                <td class="px-4 py-3 font-semibold text-[#091426]"><%= b.getTenSach() == null ? "" : b.getTenSach() %></td>
                <td class="px-4 py-3"><%= b.getTacGia() == null ? "" : b.getTacGia() %></td>
                <td class="px-4 py-3"><%= b.getTheLoai() == null ? "" : b.getTheLoai() %></td>
                <td class="px-4 py-3"><%= b.getDonGia() == null ? "" : b.getDonGia() %></td>
                <td class="px-4 py-3"><%= b.getSoLuong() == null ? "" : b.getSoLuong() %></td>
                <td class="px-4 py-3 text-slate-500"><%= b.getAnhBia() == null ? "" : b.getAnhBia() %></td>
                <td class="px-4 py-3">
                    <div class="flex gap-2">
                    <a class="px-3 py-1 rounded-lg bg-slate-200 text-xs font-semibold" href="<%= request.getContextPath() %>/books/edit?id=<%= b.getMaSach() %>">Sua</a>
                    <form method="post" action="<%= request.getContextPath() %>/books/delete" onsubmit="return confirm('Ban chac chan muon xoa sach nay?');">
                        <input type="hidden" name="id" value="<%= b.getMaSach() %>" />
                        <button type="submit" class="px-3 py-1 rounded-lg bg-red-100 text-red-700 text-xs font-semibold">Xoa</button>
                    </form>
                    </div>
                </td>
            </tr>
        <%
                }
            }
        %>
            </tbody>
        </table>
    </div>
    </div>
</main>
</body>
</html>
