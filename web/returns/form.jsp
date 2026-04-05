<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.project.model.Invoice"%>
<%@page import="com.project.model.InvoiceItem"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tao phieu tra hang</title>
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
        <div class="flex items-center justify-between gap-4 mb-6">
            <div>
                <h1 class="headline text-2xl font-extrabold text-[#091426]">Tao phieu tra hang</h1>
                <p class="text-sm text-slate-500">Chon hoa don va so luong can tra theo tung sach.</p>
            </div>
            <a class="px-4 py-2 rounded-lg bg-slate-100 text-sm font-semibold" href="<%= request.getContextPath() %>/returns">Quay lai</a>
        </div>

        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage != null && !errorMessage.isEmpty()) {
        %>
            <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-700"><%= errorMessage %></div>
        <% } %>

        <%
            List<Invoice> invoices = (List<Invoice>) request.getAttribute("invoices");
            List<InvoiceItem> invoiceItems = (List<InvoiceItem>) request.getAttribute("invoiceItems");
            Integer selectedMaHD = (Integer) request.getAttribute("selectedMaHD");
            if (invoices == null) {
                invoices = java.util.Collections.emptyList();
            }
            if (invoiceItems == null) {
                invoiceItems = java.util.Collections.emptyList();
            }
        %>

        <form method="get" action="<%= request.getContextPath() %>/returns/new" class="grid grid-cols-1 md:grid-cols-3 gap-3 mb-6">
            <div class="md:col-span-2">
                <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="maHD">Hoa don</label>
                <select class="w-full rounded-xl border-slate-200 bg-slate-50" id="maHD" name="maHD">
                    <% for (Invoice inv : invoices) { %>
                        <option value="<%= inv.getMaHD() %>" <%= selectedMaHD != null && selectedMaHD.intValue() == inv.getMaHD() ? "selected" : "" %>>HD <%= inv.getMaHD() %> - <%= inv.getTenKH() == null ? "Khach le" : inv.getTenKH() %></option>
                    <% } %>
                </select>
            </div>
            <div class="flex items-end">
                <button class="w-full px-4 py-2 rounded-lg bg-slate-200 text-sm font-semibold" type="submit">Tai hoa don</button>
            </div>
        </form>

        <form method="post" action="<%= request.getContextPath() %>/returns/new" class="space-y-6">
            <input type="hidden" name="maHD" value="<%= selectedMaHD == null ? "" : selectedMaHD %>" />

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                    <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="lyDo">Ly do tra hang</label>
                    <input class="w-full rounded-xl border-slate-200 bg-slate-50" type="text" id="lyDo" name="lyDo" placeholder="Vi du: sach bi loi, giao nham..." />
                </div>
                <div>
                    <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="kieuXuLy">Kieu xu ly</label>
                    <select class="w-full rounded-xl border-slate-200 bg-slate-50" id="kieuXuLy" name="kieuXuLy">
                        <option value="1">Hoan tien</option>
                        <option value="2">Doi sach</option>
                        <option value="3">Bao hanh</option>
                    </select>
                </div>
            </div>

            <div>
                <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="ghiChu">Ghi chu</label>
                <textarea class="w-full rounded-xl border-slate-200 bg-slate-50" id="ghiChu" name="ghiChu" rows="3"></textarea>
            </div>

            <div class="overflow-x-auto rounded-xl border border-slate-200">
                <table class="w-full text-sm text-left">
                    <thead class="bg-[#f2f4f6] text-slate-600 uppercase tracking-widest text-xs">
                        <tr>
                            <th class="px-4 py-3">Sach</th>
                            <th class="px-4 py-3">So luong da ban</th>
                            <th class="px-4 py-3">So luong tra</th>
                        </tr>
                    </thead>
                    <tbody class="divide-y divide-slate-200">
                    <% if (invoiceItems.isEmpty()) { %>
                        <tr><td class="px-4 py-6 text-slate-500" colspan="3">Khong co chi tiet hoa don de tra.</td></tr>
                    <% } else {
                        for (InvoiceItem item : invoiceItems) { %>
                        <tr>
                            <td class="px-4 py-3">
                                <input type="hidden" name="maSach" value="<%= item.getMaSach() %>" />
                                <div class="font-semibold text-[#091426]"><%= item.getTenSach() == null ? ("Ma " + item.getMaSach()) : item.getTenSach() %></div>
                            </td>
                            <td class="px-4 py-3"><%= item.getSoLuong() %></td>
                            <td class="px-4 py-3">
                                <input class="w-full max-w-40 rounded-xl border-slate-200 bg-slate-50" type="number" name="soLuong" min="1" max="<%= item.getSoLuong() == null ? 1 : item.getSoLuong() %>" value="1" />
                            </td>
                        </tr>
                    <%  }
                       } %>
                    </tbody>
                </table>
            </div>

            <div class="flex flex-wrap gap-2">
                <button class="px-4 py-2 rounded-lg bg-[#1e293b] text-white text-sm font-semibold" type="submit">Luu phieu tra</button>
                <a class="px-4 py-2 rounded-lg bg-slate-100 text-sm font-semibold" href="<%= request.getContextPath() %>/returns">Huy</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>
