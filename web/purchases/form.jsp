<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.project.model.Book"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tao phieu nhap hang</title>
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
            var row = document.createElement('tr');
            row.className = 'border-b border-slate-200';
            row.innerHTML = document.getElementById('itemTemplate').innerHTML;
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
                <h1 class="headline text-2xl font-extrabold text-[#091426]">Tao phieu nhap hang</h1>
                <p class="text-sm text-slate-500">Nhap sach moi va tang ton kho ngay khi luu phieu.</p>
            </div>
            <a class="px-4 py-2 rounded-lg bg-slate-100 text-sm font-semibold" href="<%= request.getContextPath() %>/purchases">Quay lai</a>
        </div>

        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage != null && !errorMessage.isEmpty()) {
        %>
            <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-700"><%= errorMessage %></div>
        <% } %>

        <%
            List<Book> books = (List<Book>) request.getAttribute("books");
            if (books == null) {
                books = java.util.Collections.emptyList();
            }
        %>

        <form method="post" action="<%= request.getContextPath() %>/purchases/new" class="space-y-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                    <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="maNV">Ma nhan vien (so, tuy chon)</label>
                    <input class="w-full rounded-xl border-slate-200 bg-slate-50" type="number" id="maNV" name="maNV" min="1" placeholder="De trong neu khong muon luu" />
                </div>
                <div class="flex items-end">
                    <div class="text-xs text-slate-500">Neu de trong, he thong van luu phieu nhap va cap nhat ton kho.</div>
                </div>
            </div>

            <div class="overflow-x-auto rounded-xl border border-slate-200">
                <table class="w-full text-sm text-left">
                    <thead class="bg-[#f2f4f6] text-slate-600 uppercase tracking-widest text-xs">
                        <tr>
                            <th class="px-4 py-3">Sach</th>
                            <th class="px-4 py-3">So luong</th>
                            <th class="px-4 py-3">Don gia</th>
                            <th class="px-4 py-3"></th>
                        </tr>
                    </thead>
                    <tbody id="itemsBody">
                        <tr class="border-b border-slate-200">
                            <td class="px-4 py-3">
                                <select class="w-full rounded-xl border-slate-200 bg-slate-50" name="maSach" required>
                                    <option value="">-- Chon sach --</option>
                                    <% for (Book b : books) { %>
                                        <option value="<%= b.getMaSach() %>"><%= b.getTenSach() %> (Ton hien tai: <%= b.getSoLuong() == null ? 0 : b.getSoLuong() %>)</option>
                                    <% } %>
                                </select>
                            </td>
                            <td class="px-4 py-3"><input class="w-full rounded-xl border-slate-200 bg-slate-50" type="number" name="soLuong" min="1" value="1" required /></td>
                            <td class="px-4 py-3"><input class="w-full rounded-xl border-slate-200 bg-slate-50" type="number" name="donGia" min="0" step="0.01" value="0" required /></td>
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
                            <option value="<%= b.getMaSach() %>"><%= b.getTenSach() %> (Ton hien tai: <%= b.getSoLuong() == null ? 0 : b.getSoLuong() %>)</option>
                        <% } %>
                    </select>
                </td>
                <td class="px-4 py-3"><input class="w-full rounded-xl border-slate-200 bg-slate-50" type="number" name="soLuong" min="1" value="1" required /></td>
                <td class="px-4 py-3"><input class="w-full rounded-xl border-slate-200 bg-slate-50" type="number" name="donGia" min="0" step="0.01" value="0" required /></td>
                <td class="px-4 py-3"><button class="px-3 py-2 rounded-lg bg-red-100 text-red-700 text-xs font-semibold" type="button" onclick="removeRow(this)">Xoa dong</button></td>
            </template>

            <div class="flex flex-wrap gap-2">
                <button class="px-4 py-2 rounded-lg bg-slate-200 text-sm font-semibold" type="button" onclick="addRow()">Them dong</button>
                <button class="px-4 py-2 rounded-lg bg-[#1e293b] text-white text-sm font-semibold" type="submit">Luu phieu nhap</button>
                <a class="px-4 py-2 rounded-lg bg-slate-100 text-sm font-semibold" href="<%= request.getContextPath() %>/purchases">Huy</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>