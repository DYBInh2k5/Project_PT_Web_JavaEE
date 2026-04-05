<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>403 - Forbidden</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <script src="https://cdn.tailwindcss.com?plugins=forms"></script>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet"/>
    <style>
        body { font-family: 'Inter', sans-serif; }
        .headline { font-family: 'Manrope', sans-serif; }
    </style>
</head>
<body class="bg-[#f6f7f9] min-h-screen flex items-center justify-center p-6">
    <div class="max-w-lg w-full bg-white rounded-2xl shadow-xl p-10 text-center border-t-8 border-[#d97706]">
        <h1 class="headline text-5xl font-extrabold text-[#1f2937] mb-2">403</h1>
        <h2 class="headline text-2xl font-bold text-[#111827] mb-3">Khong du quyen truy cap</h2>
        <p class="text-slate-500 mb-8">Tai khoan hien tai khong co quyen truy cap chuc nang nay.</p>
        <div class="flex flex-wrap gap-2 justify-center">
            <a class="px-4 py-2 rounded-lg bg-[#1e293b] text-white text-sm font-semibold" href="<%= request.getContextPath() %>/dashboard">Ve dashboard</a>
            <a class="px-4 py-2 rounded-lg bg-slate-100 text-sm font-semibold" href="<%= request.getContextPath() %>/logout">Dang xuat</a>
        </div>
    </div>
</body>
</html>
