<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="com.project.model.Book"%>
<%@page import="com.project.model.Customer"%>
<%@page import="com.project.model.Promotion"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tao hoa don</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <script src="https://cdn.tailwindcss.com?plugins=forms"></script>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet"/>
    <style>
        body { font-family: 'Inter', sans-serif; }
        .headline { font-family: 'Manrope', sans-serif; }
    </style>
    <script>
        function addRow() {
            var table = document.getElementById('itemsBody');
            var template = document.getElementById('itemTemplate').innerHTML;
            var row = document.createElement('tr');
            row.className = 'border-b border-slate-200';
            row.innerHTML = template;
            table.appendChild(row);
        }
        function removeRow(btn) {
            var row = btn.closest('tr');
            row.parentNode.removeChild(row);
        }
    </script>
</head>
<body class="bg-[#f7f9fb] text-[#191c1e]">
<div class="max-w-7xl mx-auto p-6 md:p-8">
    <div class="bg-white rounded-xl shadow-sm p-8">
        <div class="flex items-center justify-between gap-4 mb-6">
            <div>
                <h1 class="headline text-2xl font-extrabold text-[#091426]">Tao hoa don moi</h1>
                <p class="text-sm text-slate-500">Nhap thong tin ban hang va dong chi tiet sach.</p>
            </div>
            <a class="px-4 py-2 rounded-lg bg-slate-100 text-sm font-semibold" href="<%= request.getContextPath() %>/invoices">Quay lai danh sach</a>
        </div>
        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage != null && !errorMessage.isEmpty()) {
        %>
            <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-700"><%= errorMessage %></div>
        <% } %>

        <%
            List<Book> books = (List<Book>) request.getAttribute("books");
            List<Customer> customers = (List<Customer>) request.getAttribute("customers");
            List<Promotion> promotions = (List<Promotion>) request.getAttribute("promotions");
            Map<Integer, BigDecimal> bookPriceMap = (Map<Integer, BigDecimal>) request.getAttribute("bookPriceMap");
            if (books == null) {
                books = java.util.Collections.emptyList();
            }
            if (customers == null) {
                customers = java.util.Collections.emptyList();
            }
            if (promotions == null) {
                promotions = java.util.Collections.emptyList();
            }
            if (bookPriceMap == null) {
                bookPriceMap = java.util.Collections.emptyMap();
            }
        %>

        <form method="post" action="<%= request.getContextPath() %>/invoices/new" class="space-y-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="maKH">Khach hang</label>
                <select class="w-full rounded-xl border-slate-200 bg-slate-50" id="maKH" name="maKH">
                    <option value="">Khach le</option>
                    <% for (Customer c : customers) { %>
                        <option value="<%= c.getMaKH() %>"><%= c.getTenKH() %> - <%= c.getDienThoai() == null ? "" : c.getDienThoai() %></option>
                    <% } %>
                </select>
                </div>

                <div>
                <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="maNV">Ma nhan vien (tu chon)</label>
                <input class="w-full rounded-xl border-slate-200 bg-slate-50" type="text" id="maNV" name="maNV" placeholder="VD: NV001" />
                </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                    <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="giamGia">Giam gia</label>
                    <input class="w-full rounded-xl border-slate-200 bg-slate-50" type="number" id="giamGia" name="giamGia" step="0.01" min="0" value="0" />
                </div>
                <div>
                    <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="thueVAT">Thue VAT</label>
                    <input class="w-full rounded-xl border-slate-200 bg-slate-50" type="number" id="thueVAT" name="thueVAT" step="0.01" min="0" value="0" />
                </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                    <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="couponCode">Ma coupon khuyen mai</label>
                    <input class="w-full rounded-xl border-slate-200 bg-slate-50" type="text" id="couponCode" name="couponCode" placeholder="Nhap ma coupon, neu co" />
                </div>
                <div class="rounded-xl bg-slate-50 border border-slate-200 p-4 text-sm text-slate-600">
                    <div class="font-semibold text-[#091426] mb-2">Khuyen mai dang hieu luc</div>
                    <%
                        if (promotions.isEmpty()) {
                    %>
                        <div>Khong co khuyen mai hieu luc.</div>
                    <%
                        } else {
                            for (Promotion promotion : promotions) {
                    %>
                        <div class="flex items-center justify-between gap-2 py-1">
                            <span><%= promotion.getMaCoupon() == null ? ("KM #" + promotion.getMaKM()) : promotion.getMaCoupon() %></span>
                            <span class="text-xs text-slate-500"><%= promotion.getHinhThuc() == null ? "" : promotion.getHinhThuc() %> - <%= promotion.getGiaTri() == null ? 0 : promotion.getGiaTri() %></span>
                        </div>
                    <%
                            }
                        }
                    %>
                </div>
            </div>

            <div class="text-xs text-slate-500 bg-amber-50 border border-amber-200 rounded-xl p-4">
                Coupon duoc kiem tra theo ma va ngay hieu luc. Neu khuyen mai co loai TANG1, he thong ap dung theo quy tac mua N tang 1 (N = GiaTri), tinh tren cac sach co gia thap hon trong gio hang.
            </div>

            <h2 class="headline text-xl font-bold text-[#091426]">Chi tiet sach</h2>
            <div class="overflow-x-auto rounded-xl border border-slate-200">
            <table class="w-full text-sm text-left">
                <thead class="bg-[#f2f4f6] text-slate-600 uppercase tracking-widest text-xs">
                    <tr>
                        <th class="px-4 py-3">Sach</th>
                        <th class="px-4 py-3">So luong</th>
                        <th class="px-4 py-3"></th>
                    </tr>
                </thead>
                <tbody id="itemsBody">
                    <tr class="border-b border-slate-200">
                        <td class="px-4 py-3">
                            <select class="w-full rounded-xl border-slate-200 bg-slate-50" name="maSach" required>
                                <option value="">-- Chon sach --</option>
                                <% for (Book b : books) { %>
                                    <option value="<%= b.getMaSach() %>"><%= b.getTenSach() %> (Ton: <%= b.getSoLuong() == null ? 0 : b.getSoLuong() %>)</option>
                                <% } %>
                            </select>
                        </td>
                        <td class="px-4 py-3"><input class="w-full rounded-xl border-slate-200 bg-slate-50" type="number" name="soLuong" min="1" value="1" required /></td>
                        <td class="px-4 py-3"><button class="px-3 py-2 rounded-lg bg-red-100 text-red-700 text-xs font-semibold" type="button" onclick="removeRow(this)">Xoa dong</button></td>
                    </tr>
                </tbody>
            </table>
            </div>

            <template id="itemTemplate">
                <td class="px-4 py-3">
                    <select class="w-full rounded-xl border-slate-200 bg-slate-50" name="maSach" required>
                        <option value="">-- Chon sach --</option>
                        <% for (Book b : books) { %>
                            <option value="<%= b.getMaSach() %>"><%= b.getTenSach() %> (Ton: <%= b.getSoLuong() == null ? 0 : b.getSoLuong() %>)</option>
                        <% } %>
                    </select>
                </td>
                <td class="px-4 py-3"><input class="w-full rounded-xl border-slate-200 bg-slate-50" type="number" name="soLuong" min="1" value="1" required /></td>
                <td class="px-4 py-3"><button class="px-3 py-2 rounded-lg bg-red-100 text-red-700 text-xs font-semibold" type="button" onclick="removeRow(this)">Xoa dong</button></td>
            </template>

            <div class="flex flex-wrap gap-2 pt-2">
                <button class="px-4 py-2 rounded-lg bg-slate-200 text-sm font-semibold" type="button" onclick="addRow()">Them dong sach</button>
                <button class="px-4 py-2 rounded-lg bg-[#1e293b] text-white text-sm font-semibold" type="submit">Luu hoa don</button>
                <a class="px-4 py-2 rounded-lg bg-slate-100 text-sm font-semibold" href="<%= request.getContextPath() %>/invoices">Huy</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>
