<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.project.model.Promotion"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Khuyen mai</title>
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
        <%
            String msg = request.getParameter("msg");
        %>
        <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4 mb-6">
            <div>
                <h1 class="headline text-2xl font-extrabold text-[#091426]">Danh sach khuyen mai</h1>
                <p class="text-sm text-slate-500">Quan ly coupon va uu dai tu bang KhuyenMai.</p>
            </div>
            <div class="flex gap-2">
                <a class="px-4 py-2 rounded-lg bg-slate-100 text-sm font-semibold" href="<%= request.getContextPath() %>/invoices/new">Tao hoa don</a>
                <a class="px-4 py-2 rounded-lg bg-[#1e293b] text-white text-sm font-semibold" href="<%= request.getContextPath() %>/promotions/new">Them khuyen mai</a>
            </div>
        </div>

        <form method="get" action="<%= request.getContextPath() %>/promotions" class="mb-4 flex gap-2">
            <input class="w-full md:w-96 rounded-xl border-slate-200 bg-slate-50" type="text" name="q" value="<%= request.getAttribute("keyword") == null ? "" : request.getAttribute("keyword") %>" placeholder="Tim theo ten, loai, ma coupon" />
            <button class="px-4 py-2 rounded-lg bg-slate-200 text-sm font-semibold" type="submit">Tim</button>
        </form>

        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage != null && !errorMessage.isEmpty()) {
        %>
            <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-700"><%= errorMessage %></div>
        <% } %>
        <% if ("created".equals(msg)) { %>
            <div class="mb-4 p-3 rounded-lg bg-green-100 text-green-700">Da tao khuyen mai thanh cong.</div>
        <% } else if ("updated".equals(msg)) { %>
            <div class="mb-4 p-3 rounded-lg bg-green-100 text-green-700">Da cap nhat khuyen mai thanh cong.</div>
        <% } else if ("deleted".equals(msg)) { %>
            <div class="mb-4 p-3 rounded-lg bg-green-100 text-green-700">Da xoa khuyen mai thanh cong.</div>
        <% } else if ("delete_failed".equals(msg)) { %>
            <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-700">Xoa khuyen mai that bai.</div>
        <% } else if ("invalid".equals(msg)) { %>
            <div class="mb-4 p-3 rounded-lg bg-amber-100 text-amber-700">Du lieu khong hop le.</div>
        <% } %>

        <div class="overflow-x-auto rounded-xl border border-slate-200">
            <table class="w-full text-sm text-left">
                <thead class="bg-[#f2f4f6] text-slate-600 uppercase tracking-widest text-xs">
                    <tr>
                        <th class="px-4 py-3">Ma KM</th>
                        <th class="px-4 py-3">Ten KM</th>
                        <th class="px-4 py-3">Loai</th>
                        <th class="px-4 py-3">Gia tri</th>
                        <th class="px-4 py-3">Ma coupon</th>
                        <th class="px-4 py-3">Ngay bat dau</th>
                        <th class="px-4 py-3">Ngay ket thuc</th>
                        <th class="px-4 py-3">Thao tac</th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-slate-200">
                <%
                    List<Promotion> promotions = (List<Promotion>) request.getAttribute("promotions");
                    if (promotions == null || promotions.isEmpty()) {
                %>
                    <tr><td class="px-4 py-6 text-slate-500" colspan="8">Khong co du lieu khuyen mai.</td></tr>
                <%
                    } else {
                        for (Promotion promotion : promotions) {
                %>
                    <tr>
                        <td class="px-4 py-3 font-semibold text-[#091426]"><%= promotion.getMaKM() %></td>
                        <td class="px-4 py-3"><%= promotion.getTenKM() == null ? "" : promotion.getTenKM() %></td>
                        <td class="px-4 py-3"><%= promotion.getHinhThuc() == null ? "" : promotion.getHinhThuc() %></td>
                        <td class="px-4 py-3"><%= promotion.getGiaTri() == null ? 0 : promotion.getGiaTri() %></td>
                        <td class="px-4 py-3"><%= promotion.getMaCoupon() == null ? "" : promotion.getMaCoupon() %></td>
                        <td class="px-4 py-3"><%= promotion.getNgayBD() == null ? "" : promotion.getNgayBD() %></td>
                        <td class="px-4 py-3"><%= promotion.getNgayKT() == null ? "" : promotion.getNgayKT() %></td>
                        <td class="px-4 py-3">
                            <div class="flex gap-2">
                                <a class="px-3 py-1 rounded-lg bg-slate-100 text-xs font-semibold" href="<%= request.getContextPath() %>/promotions/edit?id=<%= promotion.getMaKM() %>">Sua</a>
                                <form method="post" action="<%= request.getContextPath() %>/promotions/delete" onsubmit="return confirm('Ban chac chan muon xoa khuyen mai nay?');">
                                    <input type="hidden" name="id" value="<%= promotion.getMaKM() %>" />
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
</div>
</body>
</html>