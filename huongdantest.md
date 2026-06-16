# 🧪 Hướng Dẫn Quy Trình Test Chữ Ký Số (ShopSphere)

Dưới đây là kịch bản test (Test Cases) chi tiết từ đầu đến cuối để bạn trình bày khi bảo vệ đồ án, chứng minh toàn bộ các tính năng bảo mật đã hoạt động chính xác.

---

## PHẦN 1: TEST TÍNH NĂNG CỦA NGƯỜI DÙNG (USER)

### Kịch bản 1: Sinh khóa và Upload Public Key
1. **Đăng nhập** vào website bằng tài khoản User.
2. Trên Navbar, bấm vào **Key Management** (Quản lý Khóa).
3. Giao diện báo: *"Bạn chưa có khóa hoạt động"*.
4. Mở Command Prompt / Terminal ở thư mục dự án, chạy file `tool/build_tool.bat` sau đó chạy `java DigitalSignTool`.
5. Trong Tool, chọn `1. Generate Key Pair`.
   - Tool báo thành công và in ra **Key Fingerprint**.
   - Có 2 file được tạo ra: `public.key` và `private.key`.
6. Quay lại website, bấm **Chọn File**, trỏ tới `public.key` vừa tạo và ấn **Upload Key**.
7. Website load lại, hiện khung xanh lá ✅ **Active Key Found**.
   - Kiểm tra xem **Fingerprint** hiển thị trên web có khớp với chuỗi fingerprint tool in ra ở bước 5 không.

### Kịch bản 2: Đặt hàng và Ký đơn hàng (Luồng chuẩn - VERIFIED)
1. Thêm sản phẩm vào giỏ và thanh toán (Checkout) thành công.
2. Vào **Orders**, chọn **View order details** của đơn vừa đặt.
3. Kéo xuống phần **🔐 Digital Signature**:
   - Bấm nút vàng **⬇ Download order.json** (File này tải về chứa dữ liệu đơn và `requestId`).
4. Mở file `order.json` vừa tải bằng Notepad. Ghi nhớ con số ở dòng `"requestId": ...` (Ví dụ: `15`).
5. Quay lại **DigitalSignTool**, chọn `3. Sign Order`.
   - Nhập đường dẫn tới file `order.json` (VD: `C:\Downloads\order_1.json`).
   - Tool sẽ dùng `private.key` để tạo ra file `order_1.sig`.
6. Trên website, ở phần Upload Signature:
   - Nhập số **Request ID** (VD: `15`) vào ô trống.
   - Chọn file `order_1.sig` và bấm **Upload Signature**.
7. Giao diện load lại, báo xanh lá: **Result: VERIFIED**. Chữ ký hợp lệ!

---

## PHẦN 2: TEST CÁC KỊCH BẢN TẤN CÔNG / LỖI BẢO MẬT (USER)

### Kịch bản 3: Sửa đổi dữ liệu (TAMPERED)
*Mục đích: Chứng minh nếu Hacker (hoặc Admin xấu) lén vào DB sửa giá tiền đơn hàng, chữ ký sẽ phát hiện ra.*
1. Đặt 1 đơn hàng mới -> Download `order.json` (VD: `requestId` là `16`).
2. Mở **MySQL Workbench**, vào bảng `orders`, lén sửa `total_amount` của đơn hàng đó (hoặc sửa `quantity` trong `order_items`).
3. Dùng tool offline ký file `order.json` cũ để ra file `.sig`.
4. Lên web upload file `.sig` kèm `requestId` là 16.
5. **Kết quả:** Hệ thống báo vàng ⚠️ **TAMPERED**. (Vì Hash hiện tại trong DB khác với Hash lúc user bấm Download).

### Kịch bản 4: Ký bằng khóa sai (INVALID)
1. Dùng tài khoản **User A**, Download `order.json`.
2. Tạo một folder khác, copy `DigitalSignTool` ra đó, sinh 1 cặp khóa **mới tinh** (Khóa giả mạo).
3. Dùng Khóa giả mạo để Sign file `order.json` của User A.
4. Lên tài khoản User A để upload file `.sig` giả mạo.
5. **Kết quả:** Hệ thống báo đỏ ❌ **INVALID**. (Vì `.sig` được tạo từ private key không khớp với public key đang lưu trên server).

### Kịch bản 5: User báo mất khóa (KEY_REVOKED)
1. Vào **Key Management**. Bấm nút đỏ **Report Lost Key / Revoke**. 
   - Trạng thái khóa chuyển thành REVOKED (hiện trong Key History).
   - Form Upload Public Key mới sẽ hiện ra lại.
2. Thử vào một đơn hàng cũ chưa ký. Cố tình upload file `.sig` được tạo bằng khóa cũ.
3. **Kết quả:** Hệ thống báo đen ⛔ **KEY_REVOKED**. (Chặn upload chữ ký vì tại thời điểm upload, user không có khóa Active).

### Kịch bản 6: Chống Replay Attack (Upload 1 chữ ký cho 2 đơn)
1. Đặt Đơn hàng 1, download và ký ra `order_1.sig`. Upload thành công -> VERIFIED.
2. Đặt Đơn hàng 2, nhưng cố tình lấy file `order_1.sig` để upload cho Đơn hàng 2.
3. **Kết quả:** Hệ thống báo đỏ ❌ **INVALID**. (Vì trong JSON có chứa cứng ID đơn hàng, chữ ký đơn 1 không thể áp dụng cho đơn 2).

---

## PHẦN 3: TEST TÍNH NĂNG CỦA ADMIN (KIỂM TOÁN VÀ QUẢN TRỊ)

### Kịch bản 7: Xem trạng thái chữ ký tổng quan
1. Đăng xuất, đăng nhập bằng tài khoản **Admin**.
2. Trên Navbar, vào **Admin**.
3. Trang **All Customer Orders** giờ đây có thêm cột **Signature**.
4. Bạn sẽ thấy ngay lập tức đơn nào đang có màu xanh `VERIFIED`, đơn nào màu đỏ `INVALID`, `TAMPERED`, hay `Unsigned`. Giúp Admin yên tâm trước khi ấn nút giao hàng (Mark Shipped).

### Kịch bản 8: Xem Audit Logs (Nhật ký kiểm toán)
1. Trên Navbar, bấm vào **Audit Logs**.
2. Thử dùng dropdown **Filter by Action**:
   - Lọc `UPLOAD_PUBLIC_KEY`: Thấy lịch sử User upload khóa mới.
   - Lọc `DOWNLOAD_JSON`: Thấy lịch sử User lấy file về máy.
   - Lọc `VERIFY`: Thấy lịch sử hệ thống xác thực.
   - Lọc `REVOKE_KEY`: Thấy lịch sử báo mất khóa.
*Tính năng này đảm bảo mọi hành vi liên quan tới an toàn thông tin đều không thể chối cãi.*

### Kịch bản 9: Xem Verification Logs (Nhật ký xác thực)
1. Trên Navbar, bấm vào **Verify Logs**.
2. Đây là nơi Admin theo dõi các cuộc tấn công. 
3. Xem các bản ghi bị báo `INVALID` hoặc `TAMPERED`. 
   - Admin sẽ biết ngay User ID nào, Đơn hàng số mấy đang có người cố tình can thiệp hoặc sử dụng sai khóa. Cột Message sẽ ghi rõ lý do từ chối.
