# Test case nghiep vu QL Ban Sach

## 1. Dang nhap
- Buoc: Mo `/login` va nhap tai khoan hop le.
- Kq mong doi: Chuyen ve `/dashboard`, session duoc tao.

## 2. Shop catalog public
- Buoc: Truy cap `/shop` khi chua dang nhap, tim kiem theo tu khoa.
- Kq mong doi: Truy cap duoc khong can auth, danh sach sach hien thi dung.

## 3. Gio hang
- Buoc: Tu `/shop` them sach vao gio, vao `/shop/cart`, cap nhat so luong, xoa 1 dong.
- Kq mong doi: Tong tien thay doi dung theo so luong va gio hang luu theo session.

## 4. Checkout shop
- Buoc: Vao `/shop/checkout`, nhap thong tin nguoi nhan + coupon hop le, dat hang.
- Kq mong doi: Tao hoa don thanh cong trong DB, ton kho giam dung, gio hang duoc xoa sau khi dat.

## 5. Tra cuu don hang khach mua
- Buoc: Vao `/shop/order-lookup`, nhap `Ma hoa don` + `SDT` hoac `Email` da dung luc checkout.
- Kq mong doi: Hien thong tin don hang va danh sach sach da mua, sai thong tin thi bao khong tim thay.

## 6. Don cua toi (session)
- Buoc: Dat hang thanh cong tu `/shop/checkout`, sau do vao `/shop/my-orders`.
- Kq mong doi: Don vua dat hien trong danh sach, co the bam `Xem chi tiet` de sang tra cuu don.

## 7. Ma don cong khai (OrderCode)
- Buoc: Dat hang thanh cong, sao chep `Ma don cong khai`, mo `/shop/order?code={OrderCode}`.
- Kq mong doi: Hien chi tiet don hang dung voi ma vua tao; ma sai thi bao khong hop le.

## 8. Chong tao trung khach hang khi checkout
- Buoc: Dat 2 don voi cung SDT hoac Email va co nhap ten nguoi nhan.
- Kq mong doi: He thong tai su dung khach hang cu (khong tao ban ghi trung), hoa don moi van tao thanh cong.

## 9. Bao ve truy cap chi tiet don
- Buoc: Mo truc tiep `/shop/order?code={OrderCode}` trong phien moi (khong co lich su don), khong nhap SDT/Email.
- Kq mong doi: He thong yeu cau xac thuc SDT/Email; xac thuc dung thi cho xem don, sai thi bao loi.

## 10. Xuat PDF hoa don
- Buoc: Tu trang chi tiet don (`/shop/order?code=...`) bam `Tai hoa don PDF`.
- Kq mong doi: Trinh duyet tai tep PDF va noi dung hoa don khop voi don dang xem.

## 11. Kiem tra DB
- Buoc: Mo `/db-check`.
- Kq mong doi: Hien `Connection OK`, database `QLBanSach`, `Match: true`.

## 12. CRUD Sach
- Buoc: Mo `/books`, them 1 sach moi, sua lai thong tin, sau do xoa.
- Kq mong doi: Danh sach cap nhat dung, thong bao success hien thi.

## 13. CRUD Khach hang
- Buoc: Mo `/customers`, them moi, sua, xoa 1 khach hang.
- Kq mong doi: Danh sach khach hang thay doi dung va co canh bao neu xoa ban ghi dang duoc tham chieu.

## 14. Tao hoa don
- Buoc: Mo `/invoices/new`, chon khach hang, chon sach, nhap so luong, luu hoa don.
- Kq mong doi: Tao duoc hoa don moi, hien trong `/invoices/detail`, ton kho sach giam.

## 15. Ap coupon khuyen mai
- Buoc: Mo `/promotions` de xem coupon dang hieu luc, sau do vao `/invoices/new`, nhap `Ma coupon khuyen mai` hop le, luu hoa don.
- Kq mong doi: Hoa don duoc ap dung giam gia theo `KhuyenMai`, neu ma coupon het han/khong ton tai thi bao loi.

## 16. Ap dung TANG1
- Buoc: Tao khuyen mai `TANG1` voi `GiaTri = 2`, gan ma coupon (vi du `MUA2TANG1`), tao hoa don co so luong sach du dieu kien va nhap coupon nay.
- Kq mong doi: He thong tinh giam gia theo quy tac mua 2 tang 1, uu tien tru tren cac sach co don gia thap hon.

## 17. CRUD Khuyen mai
- Buoc: Dang nhap bang tai khoan admin, vao `/promotions`, tao moi 1 khuyen mai, sua gia tri, xoa ban ghi vua tao.
- Kq mong doi: Du lieu cap nhat dung tren danh sach, ban ghi duoc tim thay theo o tim kiem.

## 18. Validate trung ma coupon
- Buoc: Tao 1 khuyen mai co `MaCoupon = ABC123`, sau do tao/sua ban ghi khac cung ma nay.
- Kq mong doi: He thong chan luu va hien thong bao ma coupon da ton tai.

## 19. Bao cao doanh thu
- Buoc: Mo `/reports/revenue`, loc theo ngay.
- Kq mong doi: Hien doanh thu theo ngay va top sach ban chay.

## 20. Tra hang / doi hang
- Buoc: Mo `/returns/new`, chon hoa don, chon so luong tra cho tung sach, luu phieu tra.
- Kq mong doi: Tao duoc phieu tra hang, chi tiet luu vao `DoiTra` va `ChiTietDoiTra`, ton kho sach tang len.

## 21. Nhap hang
- Buoc: Mo `/purchases/new`, chon sach, nhap so luong va don gia, luu phieu nhap.
- Kq mong doi: Tao duoc phieu nhap, chi tiet luu vao `PhieuNhap` va `ChiTietPhieuNhap`, ton kho sach tang len.

## 22. Phan quyen
- Buoc: Dang nhap tai khoan khong phai admin va truy cap chuc nang xoa/bao cao/quan tri khuyen mai (`/promotions/new`, `/promotions/edit`, `/promotions/delete`).
- Kq mong doi: Tra ve 403.

## 23. Audit log
- Buoc: Dang nhap va thuc hien cac chuc nang chinh.
- Kq mong doi: Server log co dong `activity user=...` voi method, path, elapsedMs.