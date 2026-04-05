<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.project.model.Customer"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Khach hang</title>
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
<%
    Customer c = (Customer) request.getAttribute("customer");
    if (c == null) {
        c = new Customer();
    }
    boolean isEdit = Boolean.TRUE.equals(request.getAttribute("isEdit"));
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
    <div class="bg-white rounded-xl shadow-sm p-8">
        <div class="flex items-center justify-between gap-4 mb-6">
            <div>
                <h1 class="headline text-2xl font-extrabold text-[#091426]"><%= isEdit ? "Sua khach hang" : "Them khach hang" %></h1>
                <p class="text-sm text-slate-500">Cap nhat thong tin lien lac de quan ly don hang chinh xac.</p>
            </div>
            <a class="px-4 py-2 rounded-lg bg-slate-100 text-sm font-semibold" href="<%= request.getContextPath() %>/customers">Quay lai</a>
        </div>
        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-700"><%= errorMessage %></div>
        <% } %>

        <form method="post" action="<%= request.getContextPath() %><%= isEdit ? "/customers/edit?id=" + c.getMaKH() : "/customers/new" %>" class="space-y-4">
            <div>
                <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="tenKH">Ten khach hang *</label>
                <input class="w-full rounded-xl border-slate-200 bg-slate-50" type="text" id="tenKH" name="tenKH" required value="<%= c.getTenKH() == null ? "" : c.getTenKH() %>" />
            </div>

            <div>
                <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="dienThoai">Dien thoai</label>
                <input class="w-full rounded-xl border-slate-200 bg-slate-50" type="text" id="dienThoai" name="dienThoai" value="<%= c.getDienThoai() == null ? "" : c.getDienThoai() %>" />
            </div>

            <div>
                <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="email">Email</label>
                <input class="w-full rounded-xl border-slate-200 bg-slate-50" type="text" id="email" name="email" value="<%= c.getEmail() == null ? "" : c.getEmail() %>" />
            </div>

            <div>
                <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="diaChi">Dia chi</label>
                <textarea class="w-full rounded-xl border-slate-200 bg-slate-50" id="diaChi" name="diaChi" rows="3"><%= c.getDiaChi() == null ? "" : c.getDiaChi() %></textarea>
            </div>

            <div class="flex gap-2 pt-2">
                <button type="submit" class="px-4 py-2 rounded-lg bg-[#1e293b] text-white text-sm font-semibold">Luu</button>
                <a class="px-4 py-2 rounded-lg bg-slate-100 text-sm font-semibold" href="<%= request.getContextPath() %>/customers">Huy</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>
