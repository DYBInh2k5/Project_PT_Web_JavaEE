<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.project.model.ReturnTransaction"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tra hang</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <script src="https://cdn.tailwindcss.com?plugins=forms"></script>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet"/>
    <style>
        body { font-family: 'Inter', sans-serif; }
        .headline { font-family: 'Manrope', sans-serif; }
    </style>
</head>
<body class="bg-[#f7f9fb] text-[#191c1e]">
<div class="max-w-7xl mx-auto p-6 md:p-8">
    <div class="bg-white rounded-xl shadow-sm p-8">
        <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4 mb-6">
            <div>
                <h1 class="headline text-2xl font-extrabold text-[#091426]">Danh sach phieu tra hang</h1>
                <p class="text-sm text-slate-500">Theo doi cac phieu doi tra va cap nhat ton kho.</p>
            </div>
            <a class="px-4 py-2 rounded-lg bg-[#1e293b] text-white text-sm font-semibold" href="<%= request.getContextPath() %>/returns/new">Tao phieu tra hang</a>
        </div>
        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage != null && !errorMessage.isEmpty()) {
        %>
            <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-700"><%= errorMessage %></div>
        <% } %>

        <div class="overflow-x-auto rounded-xl border border-slate-200">
            <table class="w-full text-sm text-left">
                <thead class="bg-[#f2f4f6] text-slate-600 uppercase tracking-widest text-xs">
                    <tr>
                        <th class="px-4 py-3">Ma DT</th>
                        <th class="px-4 py-3">Ma HD</th>
                        <th class="px-4 py-3">Ngay doi</th>
                        <th class="px-4 py-3">Khach hang</th>
                        <th class="px-4 py-3">Ly do</th>
                        <th class="px-4 py-3">Thao tac</th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-slate-200">
                <%
                    List<ReturnTransaction> returns = (List<ReturnTransaction>) request.getAttribute("returns");
                    if (returns == null || returns.isEmpty()) {
                %>
                    <tr><td class="px-4 py-6 text-slate-500" colspan="6">Khong co phieu doi tra.</td></tr>
                <%
                    } else {
                        for (ReturnTransaction tx : returns) {
                %>
                    <tr>
                        <td class="px-4 py-3 font-semibold text-[#091426]"><%= tx.getMaDT() %></td>
                        <td class="px-4 py-3"><%= tx.getMaHD() %></td>
                        <td class="px-4 py-3"><%= tx.getNgayDoi() == null ? "" : tx.getNgayDoi() %></td>
                        <td class="px-4 py-3"><%= tx.getTenKH() == null ? "" : tx.getTenKH() %></td>
                        <td class="px-4 py-3"><%= tx.getLyDo() == null ? "" : tx.getLyDo() %></td>
                        <td class="px-4 py-3"><a class="px-3 py-1 rounded-lg bg-slate-200 text-xs font-semibold" href="<%= request.getContextPath() %>/returns/detail?id=<%= tx.getMaDT() %>">Chi tiet</a></td>
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
