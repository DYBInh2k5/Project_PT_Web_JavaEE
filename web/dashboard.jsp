<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.project.model.dto.DashboardStats"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Dashboard</title>
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
    <%
        DashboardStats stats = (DashboardStats) request.getAttribute("stats");
        if (stats == null) {
            stats = new DashboardStats();
        }
        String errorMessage = (String) request.getAttribute("errorMessage");
    %>
    <aside class="h-screen w-72 fixed left-0 top-0 bg-[#f2f4f6] flex flex-col py-8 z-50">
        <div class="px-8 mb-10">
            <div class="flex items-center gap-3">
                <div class="w-10 h-10 bg-[#1e293b] rounded-lg flex items-center justify-center text-white">
                    <span class="material-symbols-outlined">book_4</span>
                </div>
                <div>
                    <h2 class="headline text-[#091426] font-extrabold text-lg leading-tight">Library Admin</h2>
                    <p class="text-xs text-slate-500 uppercase tracking-widest font-semibold">Management Portal</p>
                </div>
            </div>
        </div>
        <nav class="flex-1 space-y-1">
            <a class="bg-white text-[#091426] font-bold rounded-r-full shadow-sm px-8 py-3 flex items-center gap-4" href="<%= request.getContextPath() %>/dashboard">
                <span class="material-symbols-outlined">dashboard</span>
                <span class="text-sm">Dashboard</span>
            </a>
            <a class="text-slate-600 px-8 py-3 flex items-center gap-4 hover:text-[#006c49]" href="<%= request.getContextPath() %>/books"><span class="material-symbols-outlined">auto_stories</span><span class="text-sm">Inventory</span></a>
            <a class="text-slate-600 px-8 py-3 flex items-center gap-4 hover:text-[#006c49]" href="<%= request.getContextPath() %>/customers"><span class="material-symbols-outlined">group</span><span class="text-sm">Customers</span></a>
            <a class="text-slate-600 px-8 py-3 flex items-center gap-4 hover:text-[#006c49]" href="<%= request.getContextPath() %>/invoices"><span class="material-symbols-outlined">receipt_long</span><span class="text-sm">Invoices</span></a>
            <a class="text-slate-600 px-8 py-3 flex items-center gap-4 hover:text-[#006c49]" href="<%= request.getContextPath() %>/reports/revenue"><span class="material-symbols-outlined">analytics</span><span class="text-sm">Reports</span></a>
            <a class="text-slate-600 px-8 py-3 flex items-center gap-4 hover:text-[#006c49]" href="<%= request.getContextPath() %>/db-check"><span class="material-symbols-outlined">database</span><span class="text-sm">DB Check</span></a>
        </nav>
        <div class="px-8 mt-auto pt-8 border-t border-slate-200">
            <a class="text-slate-600 py-3 flex items-center gap-4 hover:text-red-600" href="<%= request.getContextPath() %>/logout">
                <span class="material-symbols-outlined">logout</span><span class="text-sm">Logout</span>
            </a>
        </div>
    </aside>

    <main class="ml-72 min-h-screen">
        <header class="w-full h-16 sticky top-0 z-40 bg-[#f7f9fb] flex justify-between items-center px-8">
            <h1 class="headline text-2xl font-extrabold text-[#091426]">Curator's Overview</h1>
            <a class="px-4 py-2 rounded-xl bg-[#1e293b] text-white text-sm font-semibold" href="<%= request.getContextPath() %>/invoices/new">New Invoice</a>
        </header>

        <div class="p-8 space-y-8">
            <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
                <div class="bg-red-100 text-red-700 rounded-xl p-4"><%= errorMessage %></div>
            <% } %>

            <section class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                <div class="bg-white p-6 rounded-xl shadow-sm">
                    <p class="text-xs font-bold text-slate-500 uppercase tracking-widest mb-1">Total Books</p>
                    <h3 class="headline text-3xl font-extrabold text-[#091426]"><%= stats.getTotalBooks() %></h3>
                </div>
                <div class="bg-white p-6 rounded-xl shadow-sm">
                    <p class="text-xs font-bold text-slate-500 uppercase tracking-widest mb-1">Total Customers</p>
                    <h3 class="headline text-3xl font-extrabold text-[#091426]"><%= stats.getTotalCustomers() %></h3>
                </div>
                <div class="bg-white p-6 rounded-xl shadow-sm">
                    <p class="text-xs font-bold text-slate-500 uppercase tracking-widest mb-1">Total Invoices</p>
                    <h3 class="headline text-3xl font-extrabold text-[#091426]"><%= stats.getTotalInvoices() %></h3>
                </div>
                <div class="bg-white p-6 rounded-xl shadow-sm">
                    <p class="text-xs font-bold text-slate-500 uppercase tracking-widest mb-1">Total Revenue</p>
                    <h3 class="headline text-3xl font-extrabold text-[#006c49]"><%= stats.getRevenue() == null ? "0" : stats.getRevenue() %></h3>
                </div>
            </section>

            <section class="grid grid-cols-1 xl:grid-cols-3 gap-6">
                <div class="xl:col-span-2 bg-white p-8 rounded-xl shadow-sm">
                    <h4 class="headline text-xl font-extrabold text-[#091426] mb-2">Revenue Trends</h4>
                    <p class="text-sm text-slate-500 mb-8">Daily performance across all categories</p>
                    <div class="h-56 flex items-end gap-2">
                        <div class="flex-1 bg-slate-200 rounded-t-lg h-[40%]"></div>
                        <div class="flex-1 bg-slate-200 rounded-t-lg h-[65%]"></div>
                        <div class="flex-1 bg-slate-200 rounded-t-lg h-[55%]"></div>
                        <div class="flex-1 bg-slate-200 rounded-t-lg h-[90%]"></div>
                        <div class="flex-1 bg-[#006c49] rounded-t-lg h-[75%]"></div>
                    </div>
                </div>

                <div class="bg-white p-8 rounded-xl shadow-sm">
                    <h4 class="headline text-xl font-extrabold text-[#091426] mb-2">Quick Access</h4>
                    <p class="text-sm text-slate-500 mb-6">Jump to core modules</p>
                    <div class="space-y-3">
                        <a class="block w-full px-4 py-3 rounded-lg bg-[#dcfce7] text-[#166534] font-bold" href="<%= request.getContextPath() %>/shop">Open Public Shop</a>
                        <a class="block w-full px-4 py-3 rounded-lg bg-[#eceef0] font-semibold" href="<%= request.getContextPath() %>/books">Manage Books</a>
                        <a class="block w-full px-4 py-3 rounded-lg bg-[#eceef0] font-semibold" href="<%= request.getContextPath() %>/customers">Manage Customers</a>
                        <a class="block w-full px-4 py-3 rounded-lg bg-[#eceef0] font-semibold" href="<%= request.getContextPath() %>/invoices">Manage Invoices</a>
                        <a class="block w-full px-4 py-3 rounded-lg bg-[#eceef0] font-semibold" href="<%= request.getContextPath() %>/promotions">Manage Promotions</a>
                        <a class="block w-full px-4 py-3 rounded-lg bg-[#eceef0] font-semibold" href="<%= request.getContextPath() %>/purchases">Manage Purchases</a>
                        <a class="block w-full px-4 py-3 rounded-lg bg-[#eceef0] font-semibold" href="<%= request.getContextPath() %>/returns">Manage Returns</a>
                        <a class="block w-full px-4 py-3 rounded-lg bg-[#eceef0] font-semibold" href="<%= request.getContextPath() %>/reports/revenue">View Reports</a>
                    </div>
                </div>
            </div>
        </div>
    </main>
</body>
</html>
