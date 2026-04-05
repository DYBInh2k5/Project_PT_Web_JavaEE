<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.project.model.Promotion"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><%= Boolean.TRUE.equals(request.getAttribute("isEdit")) ? "Sua khuyen mai" : "Them khuyen mai" %></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <script src="https://cdn.tailwindcss.com?plugins=forms"></script>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet"/>
    <style>
        body { font-family: 'Inter', sans-serif; }
        .headline { font-family: 'Manrope', sans-serif; }
    </style>
</head>
<body class="bg-[#f7f9fb] text-[#191c1e]">
<div class="max-w-4xl mx-auto p-6 md:p-8">
    <div class="bg-white rounded-xl shadow-sm p-8">
        <%
            boolean isEdit = Boolean.TRUE.equals(request.getAttribute("isEdit"));
            Promotion promotion = (Promotion) request.getAttribute("promotion");
            if (promotion == null) {
                promotion = new Promotion();
            }
        %>

        <div class="flex items-center justify-between mb-6">
            <div>
                <h1 class="headline text-2xl font-extrabold text-[#091426]"><%= isEdit ? "Sua khuyen mai" : "Them khuyen mai" %></h1>
                <p class="text-sm text-slate-500">Quan ly coupon va chuong trinh uu dai theo bang KhuyenMai.</p>
            </div>
            <a class="px-4 py-2 rounded-lg bg-slate-100 text-sm font-semibold" href="<%= request.getContextPath() %>/promotions">Quay lai danh sach</a>
        </div>

        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage != null && !errorMessage.isEmpty()) {
        %>
            <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-700"><%= errorMessage %></div>
        <% } %>

        <form method="post" class="space-y-5" action="<%= request.getContextPath() + (isEdit ? "/promotions/edit?id=" + promotion.getMaKM() : "/promotions/new") %>">
            <div>
                <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="tenKM">Ten khuyen mai</label>
                <input class="w-full rounded-xl border-slate-200 bg-slate-50" id="tenKM" name="tenKM" type="text" maxlength="100" required value="<%= promotion.getTenKM() == null ? "" : promotion.getTenKM() %>" />
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                    <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="hinhThuc">Hinh thuc</label>
                    <select class="w-full rounded-xl border-slate-200 bg-slate-50" id="hinhThuc" name="hinhThuc" required>
                        <option value="">-- Chon --</option>
                        <option value="%" <%= "%".equalsIgnoreCase(promotion.getHinhThuc()) ? "selected" : "" %>>% (phan tram)</option>
                        <option value="COUPON" <%= "COUPON".equalsIgnoreCase(promotion.getHinhThuc()) ? "selected" : "" %>>COUPON (gia tri co dinh)</option>
                        <option value="TANG1" <%= "TANG1".equalsIgnoreCase(promotion.getHinhThuc()) ? "selected" : "" %>>TANG1</option>
                    </select>
                </div>
                <div>
                    <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="giaTri">Gia tri</label>
                    <input class="w-full rounded-xl border-slate-200 bg-slate-50" id="giaTri" name="giaTri" type="number" step="0.01" min="0" required value="<%= promotion.getGiaTri() == null ? "" : promotion.getGiaTri() %>" />
                </div>
            </div>

            <div>
                <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="maCoupon">Ma coupon</label>
                <input class="w-full rounded-xl border-slate-200 bg-slate-50" id="maCoupon" name="maCoupon" type="text" maxlength="50" value="<%= promotion.getMaCoupon() == null ? "" : promotion.getMaCoupon() %>" />
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                    <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="ngayBD">Ngay bat dau</label>
                    <input class="w-full rounded-xl border-slate-200 bg-slate-50" id="ngayBD" name="ngayBD" type="date" value="<%= promotion.getNgayBD() == null ? "" : promotion.getNgayBD() %>" />
                </div>
                <div>
                    <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="ngayKT">Ngay ket thuc</label>
                    <input class="w-full rounded-xl border-slate-200 bg-slate-50" id="ngayKT" name="ngayKT" type="date" value="<%= promotion.getNgayKT() == null ? "" : promotion.getNgayKT() %>" />
                </div>
            </div>

            <div class="flex flex-wrap gap-2 pt-2">
                <button class="px-4 py-2 rounded-lg bg-[#1e293b] text-white text-sm font-semibold" type="submit"><%= isEdit ? "Luu thay doi" : "Tao khuyen mai" %></button>
                <a class="px-4 py-2 rounded-lg bg-slate-100 text-sm font-semibold" href="<%= request.getContextPath() %>/promotions">Huy</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>