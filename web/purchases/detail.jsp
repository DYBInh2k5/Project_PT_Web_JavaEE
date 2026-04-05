<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.project.model.PurchaseReceipt"%>
<%@page import="com.project.model.PurchaseReceiptItem"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Chi tiet phieu nhap hang</title>
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
        <h1 class="headline text-2xl font-extrabold text-[#091426] mb-4">Chi tiet phieu nhap hang</h1>
        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            String msg = request.getParameter("msg");
            PurchaseReceipt purchase = (PurchaseReceipt) request.getAttribute("purchase");
            List<PurchaseReceiptItem> items = (List<PurchaseReceiptItem>) request.getAttribute("items");
            if (errorMessage != null && !errorMessage.isEmpty()) {
        %>
            <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-700"><%= errorMessage %></div>
        <% } %>
        <% if ("created".equals(msg)) { %>
            <div class="mb-4 p-3 rounded-lg bg-green-100 text-green-700">Da tao phieu nhap thanh cong.</div>
        <% } %>

        <% if (purchase == null) { %>
            <p class="text-slate-500">Khong tim thay phieu nhap.</p>
        <% } else { %>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6 text-sm">
                <div class="p-4 bg-slate-50 rounded-xl"><span class="block text-xs uppercase text-slate-500 mb-1">Ma PN</span><strong><%= purchase.getMaPN() %></strong></div>
                <div class="p-4 bg-slate-50 rounded-xl"><span class="block text-xs uppercase text-slate-500 mb-1">Ngay nhap</span><strong><%= purchase.getNgayNhap() %></strong></div>
                <div class="p-4 bg-slate-50 rounded-xl"><span class="block text-xs uppercase text-slate-500 mb-1">Ma NV</span><strong><%= purchase.getMaNV() == null ? "" : purchase.getMaNV() %></strong></div>
                <div class="p-4 bg-[#e8f6f0] rounded-xl md:col-span-3"><span class="block text-xs uppercase text-[#006c49] mb-1">Tong tien</span><strong class="text-xl text-[#006c49]"><%= purchase.getTongTien() == null ? 0 : purchase.getTongTien() %></strong></div>
            </div>

            <h2 class="headline text-xl font-bold text-[#091426] mb-3">Chi tiet sach nhap</h2>
            <div class="overflow-x-auto rounded-xl border border-slate-200">
                <table class="w-full text-sm text-left">
                    <thead class="bg-[#f2f4f6] text-slate-600 uppercase tracking-widest text-xs">
                        <tr>
                            <th class="px-4 py-3">Ma CTPN</th>
                            <th class="px-4 py-3">Sach</th>
                            <th class="px-4 py-3">So luong</th>
                            <th class="px-4 py-3">Don gia</th>
                            <th class="px-4 py-3">Thanh tien</th>
                        </tr>
                    </thead>
                    <tbody class="divide-y divide-slate-200">
                    <% if (items == null || items.isEmpty()) { %>
                        <tr><td class="px-4 py-6 text-slate-500" colspan="5">Khong co chi tiet.</td></tr>
                    <% } else {
                        for (PurchaseReceiptItem item : items) { %>
                        <tr>
                            <td class="px-4 py-3"><%= item.getMaCTPN() %></td>
                            <td class="px-4 py-3"><%= item.getTenSach() == null ? ("Ma " + item.getMaSach()) : item.getTenSach() %></td>
                            <td class="px-4 py-3"><%= item.getSoLuong() %></td>
                            <td class="px-4 py-3"><%= item.getDonGia() %></td>
                            <td class="px-4 py-3 font-semibold text-[#006c49]"><%= item.getThanhTien() %></td>
                        </tr>
                    <%  }
                       } %>
                    </tbody>
                </table>
            </div>
        <% } %>

        <div class="mt-6 flex gap-2">
            <a class="px-4 py-2 rounded-lg bg-slate-100 text-sm font-semibold" href="<%= request.getContextPath() %>/purchases">Quay lai danh sach</a>
            <a class="px-4 py-2 rounded-lg bg-[#1e293b] text-white text-sm font-semibold" href="<%= request.getContextPath() %>/purchases/new">Tao phieu nhap khac</a>
        </div>
    </div>
</div>
</body>
</html>