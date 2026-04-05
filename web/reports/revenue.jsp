<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.project.model.dto.RevenueByDate"%>
<%@page import="com.project.model.dto.TopBookReportItem"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Bao cao doanh thu</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <script src="https://cdn.tailwindcss.com?plugins=forms"></script>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet"/>
    <style>
        body { font-family: 'Inter', sans-serif; }
        .headline { font-family: 'Manrope', sans-serif; }
    </style>
</head>
<body class="bg-[#f7f9fb] text-[#191c1e]">
<div class="max-w-7xl mx-auto p-6 md:p-8 space-y-6">
    <div class="bg-white rounded-xl shadow-sm p-8">
        <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4 mb-6">
            <div>
                <h1 class="headline text-2xl font-extrabold text-[#091426]">Bao cao doanh thu va sach ban chay</h1>
                <p class="text-sm text-slate-500">Tong hop doanh thu theo ngay va danh muc sach hieu qua nhat.</p>
            </div>
            <a class="px-4 py-2 rounded-lg bg-slate-100 text-sm font-semibold" href="<%= request.getContextPath() %>/dashboard">Ve dashboard</a>
        </div>
        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            String from = String.valueOf(request.getAttribute("from"));
            String to = String.valueOf(request.getAttribute("to"));
            if (errorMessage != null && !errorMessage.isEmpty()) {
        %>
            <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-700"><%= errorMessage %></div>
        <% } %>

        <form method="get" action="<%= request.getContextPath() %>/reports/revenue" class="grid grid-cols-1 md:grid-cols-4 gap-3 mb-6">
            <div>
                <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="from">Tu ngay</label>
                <input class="w-full rounded-xl border-slate-200 bg-slate-50" type="date" id="from" name="from" value="<%= from %>" />
            </div>
            <div>
                <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="to">Den ngay</label>
                <input class="w-full rounded-xl border-slate-200 bg-slate-50" type="date" id="to" name="to" value="<%= to %>" />
            </div>
            <div class="md:col-span-2 flex items-end gap-2">
                <button class="px-4 py-2 rounded-lg bg-[#1e293b] text-white text-sm font-semibold" type="submit">Loc bao cao</button>
                <a class="px-4 py-2 rounded-lg bg-slate-100 text-sm font-semibold" href="<%= request.getContextPath() %>/reports/revenue">Dat lai</a>
            </div>
        </form>
    </div>

    <div class="bg-white rounded-xl shadow-sm p-8">
        <h2 class="headline text-xl font-bold text-[#091426] mb-3">Doanh thu theo ngay</h2>
        <div class="overflow-x-auto rounded-xl border border-slate-200">
        <table class="w-full text-sm text-left">
            <thead class="bg-[#f2f4f6] text-slate-600 uppercase tracking-widest text-xs">
                <tr>
                    <th class="px-4 py-3">Ngay</th>
                    <th class="px-4 py-3">Doanh thu</th>
                </tr>
            </thead>
            <tbody class="divide-y divide-slate-200">
            <%
                List<RevenueByDate> revenueRows = (List<RevenueByDate>) request.getAttribute("revenueRows");
                if (revenueRows == null || revenueRows.isEmpty()) {
            %>
                <tr><td class="px-4 py-6 text-slate-500" colspan="2">Khong co du lieu.</td></tr>
            <%
                } else {
                    for (RevenueByDate row : revenueRows) {
            %>
                <tr>
                    <td class="px-4 py-3"><%= row.getNgay() %></td>
                    <td class="px-4 py-3 font-semibold text-[#006c49]"><%= row.getDoanhThu() == null ? 0 : row.getDoanhThu() %></td>
                </tr>
            <%
                    }
                }
            %>
            </tbody>
        </table>
        </div>
    </div>

    <div class="bg-white rounded-xl shadow-sm p-8">
        <h2 class="headline text-xl font-bold text-[#091426] mb-3">Top 10 sach ban chay</h2>
        <div class="overflow-x-auto rounded-xl border border-slate-200">
        <table class="w-full text-sm text-left">
            <thead class="bg-[#f2f4f6] text-slate-600 uppercase tracking-widest text-xs">
                <tr>
                    <th class="px-4 py-3">Ma sach</th>
                    <th class="px-4 py-3">Ten sach</th>
                    <th class="px-4 py-3">Tong so luong</th>
                    <th class="px-4 py-3">Tong doanh thu</th>
                </tr>
            </thead>
            <tbody class="divide-y divide-slate-200">
            <%
                List<TopBookReportItem> topBooks = (List<TopBookReportItem>) request.getAttribute("topBooks");
                if (topBooks == null || topBooks.isEmpty()) {
            %>
                <tr><td class="px-4 py-6 text-slate-500" colspan="4">Khong co du lieu.</td></tr>
            <%
                } else {
                    for (TopBookReportItem item : topBooks) {
            %>
                <tr>
                    <td class="px-4 py-3"><%= item.getMaSach() %></td>
                    <td class="px-4 py-3"><%= item.getTenSach() == null ? "" : item.getTenSach() %></td>
                    <td class="px-4 py-3"><%= item.getTongSoLuong() == null ? 0 : item.getTongSoLuong() %></td>
                    <td class="px-4 py-3 font-semibold text-[#006c49]"><%= item.getTongDoanhThu() == null ? 0 : item.getTongDoanhThu() %></td>
                </tr>
            <%
                    }
                }
            %>
            </tbody>
        </table>
        </div>
    </div>
</div>
</body>
</html>
