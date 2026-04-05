<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Dang nhap he thong</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <script src="https://cdn.tailwindcss.com?plugins=forms"></script>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet"/>
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined" rel="stylesheet"/>
    <style>
        body { font-family: 'Inter', sans-serif; }
        .headline { font-family: 'Manrope', sans-serif; }
    </style>
</head>
<body class="bg-[#f7f9fb] min-h-screen flex items-center justify-center p-6">
<main class="w-full max-w-[1050px] grid grid-cols-1 md:grid-cols-12 overflow-hidden rounded-xl bg-white shadow-xl">
    <div class="hidden md:flex md:col-span-5 flex-col justify-between p-12 text-white bg-gradient-to-br from-[#091426] to-[#1e293b]">
        <div>
            <div class="flex items-center gap-2 mb-16">
                <span class="material-symbols-outlined text-3xl">auto_stories</span>
                <h1 class="headline font-extrabold text-2xl tracking-tight">The Digital Curator</h1>
            </div>
            <h2 class="headline font-bold text-4xl leading-tight mb-6">Mastering the Art of Literary Management.</h2>
            <p class="text-slate-300 text-lg leading-relaxed max-w-xs">A sophisticated archive for scholars and keepers of the written word.</p>
        </div>
        <div class="p-4 bg-white/10 rounded-xl border border-white/10">
            <p class="text-sm italic">"A library is not a luxury but one of the necessities of life."</p>
            <p class="text-xs mt-2 uppercase tracking-widest opacity-70">Henry Ward Beecher</p>
        </div>
    </div>

    <div class="col-span-1 md:col-span-7 p-8 md:p-16 flex flex-col justify-center">
        <div class="max-w-md mx-auto w-full">
            <header class="mb-8">
                <h3 class="headline font-bold text-3xl text-[#091426] mb-2">Management Access</h3>
                <p class="text-slate-500 text-sm">Please provide your library credentials to proceed.</p>
            </header>

        <%
            String msg = request.getParameter("msg");
            String errorMessage = (String) request.getAttribute("errorMessage");
            if ("logged_out".equals(msg)) {
        %>
            <div class="mb-6 p-4 rounded-lg bg-green-100 text-green-700">Ban da dang xuat.</div>
        <% }
            if (errorMessage != null && !errorMessage.isEmpty()) {
        %>
            <div class="mb-6 p-4 rounded-lg bg-red-100 text-red-700"><%= errorMessage %></div>
        <% } %>

        <form method="post" action="<%= request.getContextPath() %>/login" class="space-y-6">
            <div>
                <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="username">Username</label>
                <div class="relative">
                    <span class="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-slate-400">account_circle</span>
                    <input class="w-full pl-12 pr-4 py-4 bg-slate-100 border-none rounded-xl focus:ring-2 focus:ring-[#1e293b]/40" type="text" id="username" name="username" placeholder="curator_admin" required />
                </div>
            </div>

            <div>
                <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2" for="password">Password</label>
                <div class="relative">
                    <span class="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-slate-400">lock</span>
                    <input class="w-full pl-12 pr-4 py-4 bg-slate-100 border-none rounded-xl focus:ring-2 focus:ring-[#1e293b]/40" type="password" id="password" name="password" placeholder="••••••••" required />
                </div>
            </div>

            <div class="flex items-center">
                <input class="w-4 h-4 rounded border-slate-300" id="remember" name="remember" type="checkbox" />
                <label class="ml-3 text-sm text-slate-600" for="remember">Stay authorized on this station</label>
            </div>

            <button class="w-full bg-gradient-to-br from-[#091426] to-[#1e293b] text-white headline font-bold py-4 rounded-xl shadow-lg hover:opacity-90 transition-all" type="submit">Login</button>
        </form>

        <p class="mt-8 text-sm text-slate-500">Tai khoan mau de test: <strong>admin / 123</strong> hoac <strong>Duy / 123</strong>.</p>
        </div>
    </div>
</main>
</body>
</html>
