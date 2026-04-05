<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.project.model.Customer"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Khach hang</title>
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
        <a class="text-slate-600 px-8 py-3 flex items-center gap-4 hover:text-[#006c49]" href="<%= request.getContextPath() %>/books"><span class="material-symbols-outlined">auto_stories</span><span class="text-sm">Inventory</span></a>
        <a class="bg-white text-[#091426] font-bold rounded-r-full shadow-sm px-8 py-3 flex items-center gap-4" href="<%= request.getContextPath() %>/customers"><span class="material-symbols-outlined">group</span><span class="text-sm">Customers</span></a>
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
                <h1 class="headline text-2xl font-extrabold text-[#091426]">Quan ly khach hang</h1>
                <p class="text-sm text-slate-500">Thong tin lien he va lich su mua hang cua khach.</p>
            </div>
            <a class="inline-flex items-center gap-2 px-4 py-3 rounded-xl bg-[#1e293b] text-white text-sm font-semibold" href="<%= request.getContextPath() %>/customers/new">
                <span class="material-symbols-outlined text-base">person_add</span>Them khach hang
            </a>
        </div>
        <%
            String msg = request.getParameter("msg");
            String errorMessage = (String) request.getAttribute("errorMessage");
            if ("created".equals(msg)) {
        %>
            <div class="mb-4 p-3 rounded-lg bg-green-100 text-green-700">Da them khach hang.</div>
        <% } else if ("updated".equals(msg)) { %>
            <div class="mb-4 p-3 rounded-lg bg-green-100 text-green-700">Da cap nhat khach hang.</div>
        <% } else if ("deleted".equals(msg)) { %>
            <div class="mb-4 p-3 rounded-lg bg-green-100 text-green-700">Da xoa khach hang.</div>
        <% } else if ("delete_failed".equals(msg)) { %>
            <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-700">Khong the xoa khach hang (co the dang tham chieu boi hoa don).</div>
        <% }
           if (errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-700"><%= errorMessage %></div>
        <% } %>

        <div class="flex flex-col md:flex-row gap-3 items-start md:items-center justify-between mb-6">
            <form method="get" action="<%= request.getContextPath() %>/customers" class="w-full md:max-w-xl flex gap-2">
                <input class="w-full rounded-xl border-slate-200 bg-slate-50" type="text" name="q" value="<%= request.getAttribute("q") %>" placeholder="Tim ten, dien thoai, email" />
                <button class="px-4 rounded-xl bg-slate-200 font-semibold" type="submit">Tim</button>
            </form>
            <a class="px-4 py-2 rounded-lg bg-slate-100 text-sm" href="<%= request.getContextPath() %>/dashboard">Dashboard</a>
        </div>

        <div class="overflow-x-auto rounded-xl border border-slate-200">
            <table class="w-full text-sm text-left">
                <thead class="bg-[#f2f4f6] text-slate-600 uppercase tracking-widest text-xs">
                    <tr>
                        <th class="px-4 py-3">Ma KH</th>
                        <th class="px-4 py-3">Ten KH</th>
                        <th class="px-4 py-3">Dien thoai</th>
                        <th class="px-4 py-3">Email</th>
                        <th class="px-4 py-3">Dia chi</th>
                        <th class="px-4 py-3">Thao tac</th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-slate-200">
            <%
                List<Customer> customers = (List<Customer>) request.getAttribute("customers");
                if (customers == null || customers.isEmpty()) {
            %>
                <tr><td class="px-4 py-6 text-slate-500" colspan="6">Khong co du lieu khach hang.</td></tr>
            <%
                } else {
                    for (Customer c : customers) {
            %>
                <tr>
                    <td class="px-4 py-3"><%= c.getMaKH() %></td>
                    <td class="px-4 py-3 font-semibold text-[#091426]"><%= c.getTenKH() == null ? "" : c.getTenKH() %></td>
                    <td class="px-4 py-3"><%= c.getDienThoai() == null ? "" : c.getDienThoai() %></td>
                    <td class="px-4 py-3"><%= c.getEmail() == null ? "" : c.getEmail() %></td>
                    <td class="px-4 py-3"><%= c.getDiaChi() == null ? "" : c.getDiaChi() %></td>
                    <td class="px-4 py-3">
                        <div class="flex gap-2">
                        <a class="px-3 py-1 rounded-lg bg-slate-200 text-xs font-semibold" href="<%= request.getContextPath() %>/customers/edit?id=<%= c.getMaKH() %>">Sua</a>
                        <form method="post" action="<%= request.getContextPath() %>/customers/delete" onsubmit="return confirm('Xoa khach hang nay?');">
                            <input type="hidden" name="id" value="<%= c.getMaKH() %>" />
                            <button class="px-3 py-1 rounded-lg bg-red-100 text-red-700 text-xs font-semibold" type="submit">Xoa</button>
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
