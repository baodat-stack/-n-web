# 🏛️ TÀI LIỆU PHÂN TÍCH KIẾN TRÚC & BẢO MẬT (TECH LEAD PERSPECTIVE)

**Người soạn thảo:** Tech Lead  
**Dự án:** ShopSphere E-Commerce - Phân hệ Chữ Ký Số (Digital Signature)  
**Mục tiêu:** Giải thích cặn kẽ nguyên lý, quyết định thiết kế (Design Decisions) và cơ chế phòng thủ đằng sau từng dòng code đã triển khai. Tài liệu này dùng để training team dev và bảo vệ trước hội đồng kiến trúc/bảo mật.

---

## 1. Sinh và quản lý khóa hoàn toàn Offline (Zero-Knowledge Private Key)
**🔹 Vấn đề:**
Nếu chúng ta cho phép Server sinh cặp khóa (Key Pair) rồi cho User tải về, Server sẽ có quyền truy cập vào `Private Key` trong một khoảnh khắc nào đó. Điều này vi phạm nghiêm trọng nguyên tắc **Chống chối bỏ (Non-repudiation)**. User có thể cãi rằng: *"Tôi không ký đơn này, do Server bị hack hoặc Admin đã lén lấy Private Key của tôi để ký!"*.

**🔹 Giải pháp & Quyết định kỹ thuật:**
- **Giải pháp:** Xây dựng phần mềm `DigitalSignTool.jar` chạy độc lập (Standalone Java SE) hoàn toàn offline trên máy tính người dùng.
- **Design Decision:** Chúng ta áp dụng kiến trúc **Zero-Knowledge** đối với Private Key. Máy chủ ShopSphere chỉ nhận và lưu `public.key`. Server tuyệt đối không biết, không thấy và không lưu Private Key. 
- **Kết quả:** Trách nhiệm bảo vệ Private Key được chuyển giao 100% cho Client. Chữ ký số mang giá trị pháp lý tuyệt đối vì Private Key chỉ tồn tại duy nhất trên thiết bị của người dùng.

## 2. Ràng buộc "Stateful" giữa Bản tải về và Chữ ký (Nhúng `requestId`)
**🔹 Vấn đề:**
Một bài toán khó là lỗi "Race Condition" trong nghiệp vụ: 
1. User tải `order.json` (Bản số 1).
2. Ai đó lén vào DB sửa giá trị đơn hàng.
3. User lại tải `order.json` (Bản số 2).
4. User dùng tool ký **Bản số 1**, rồi upload chữ ký lên.
Nếu server chỉ gọi hàm `getLatestByOrder()`, nó sẽ lấy thông tin của Bản số 2 để xác thực chữ ký của Bản số 1 $\rightarrow$ Lỗi logic nghiêm trọng.

**🔹 Giải pháp & Quyết định kỹ thuật:**
- **Giải pháp:** Tách riêng bảng `order_sign_requests`. Mỗi lần bấm nút Download, Server tạo một Record lưu cấu trúc JSON tại thời điểm đó, sinh ra `requestId` và **nhúng thẳng vào trong ruột file JSON tải về**. Khi User upload `.sig`, họ phải gửi kèm `requestId` đó.
- **Design Decision:** Tạo ra sự liên kết 1-1 (One-to-One Binding) giữa Chữ ký và Snapshot dữ liệu tại thời điểm tải. Điều này giúp hệ thống luôn biết chính xác User đang ký cho "phiên bản" nào của đơn hàng.

## 3. Chính sách 1 Khóa Hoạt Động Cứng (Strict 1-Active-Key Policy)
**🔹 Vấn đề:**
Nhiều hệ thống lập trình ẩu bằng cách: Cứ User tải public key mới lên là tự động xóa/ghi đè key cũ. Việc tự động hóa này rất nguy hiểm. Hacker lén vào tài khoản và upload key của hắn, hệ thống tự vô hiệu hóa key của nạn nhân mà nạn nhân không hề hay biết.

**🔹 Giải pháp & Quyết định kỹ thuật:**
- **Giải pháp:** Sử dụng hàm `hasActiveKey()`. Nếu User đang có khóa `ACTIVE`, nút Upload Key bị ẩn đi và chặn cứng ở tầng Servlet. Bắt buộc User phải thực hiện quy trình "Báo mất khóa / Thu hồi khóa" (Revoke) trước.
- **Design Decision:** Tech Lead thà hy sinh một chút sự tiện lợi (UX) để đổi lấy sự an toàn tuyệt đối (Security). Việc bắt User phải tự tay ấn "Revoke" giúp họ nhận thức rõ ràng sự quan trọng của vòng đời khóa (Key Lifecycle).

## 4. Chống thay đổi DB và Insider Threat (Original Hash vs Current Hash)
**🔹 Vấn đề:**
Chữ ký số dùng để chống User thay đổi thông tin sau khi đặt, nhưng ai sẽ chống lại Quản trị viên cơ sở dữ liệu (DBA) có quyền lén sửa bảng `orders`?

**🔹 Giải pháp & Quyết định kỹ thuật:**
- **Giải pháp:** Lưu lại toàn bộ chuỗi JSON gốc `original_order_json` và tính `original_order_hash` lúc User tải đơn về. Tại thời điểm Verify, Server dùng `OrderJsonBuilder` quét lại DB một lần nữa để tạo JSON mới hiện tại. So sánh Hash hiện tại với Hash lúc tải. Nếu lệch, trả về trạng thái `TAMPERED`.
- **Design Decision:** Đây là cơ chế phòng thủ chiều sâu (Defense in Depth). Nó bảo vệ hệ thống khỏi mối đe dọa từ bên trong nội bộ tổ chức (Insider Threat).

## 5. Phân rã trạng thái chữ ký phục vụ Giám định số (Digital Forensics)
**🔹 Vấn đề:**
Hàm `signature.verify()` của Java chỉ trả về `boolean` (`true` hoặc `false`). Nhưng nếu một chữ ký báo `false`, Business Analyst / Admin sẽ hỏi: "Sai do đâu? Do dữ liệu bị sửa, hay do khóa đã bị thu hồi, hay do gắn lộn chữ ký của đơn khác?".

**🔹 Giải pháp & Quyết định kỹ thuật:**
- **Giải pháp:** Thay vì một cột boolean, chúng ta dùng Enum/Varchar với 4 trạng thái giám định riêng biệt:
  - `VERIFIED`: Hoàn hảo.
  - `TAMPERED`: Khớp chữ ký nhưng dữ liệu trong DB đã bị ai đó sửa.
  - `KEY_REVOKED`: Khóa hợp lệ, nhưng tại lúc upload, khóa này đã bị báo mất.
  - `INVALID`: Hoàn toàn sai lệch thuật toán mã hóa (Ký bằng Private key của người khác).
- **Design Decision:** Thiết kế hệ thống không chỉ để chạy đúng, mà còn phải dễ điều tra khi có sự cố. Trạng thái càng chi tiết, chi phí điều tra (Forensic Cost) càng thấp.

## 6. Định danh khóa bằng Fingerprint (SHA-256) thay vì chuỗi Base64
**🔹 Vấn đề:**
Chuỗi Public Key RSA 2048-bit dài khoảng 400 ký tự. Nếu vứt thẳng lên giao diện JSP, nó sẽ làm vỡ Layout, và con người không thể nhìn 400 ký tự để phân biệt Khóa A với Khóa B.

**🔹 Giải pháp & Quyết định kỹ thuật:**
- **Giải pháp:** Băm (Hash) chuỗi Public Key bằng SHA-256, chuyển về hệ Hexadecimal, tạo ra `Fingerprint` (Dấu vân tay khóa - 64 ký tự).
- **Design Decision:** Áp dụng chuẩn UX của thế giới bảo mật (như PGP, SSH, GitHub GPG Keys). User chỉ cần đối chiếu vài ký tự cuối của Fingerprint trên Tool offline với trên Web là biết mình đang dùng đúng khóa.

## 7. Thiết kế bảng Audit Logs dựa trên Enumeration
**🔹 Vấn đề:**
Ghi log các hành động bảo mật vào cột `VARCHAR` bằng cách gõ String `"UPLOAD_PUBLIC_KEY"` rất dễ gây ra lỗi chính tả (Typo) giữa các developer khác nhau, dẫn tới vỡ logic khi query filter.

**🔹 Giải pháp & Quyết định kỹ thuật:**
- **Giải pháp:** Bắt buộc dùng `enum AuditAction` ở tầng Java. Các phương thức DAO chỉ chấp nhận truyền tham số type `AuditAction`.
- **Design Decision:** Cưỡng chế tính toàn vẹn dữ liệu (Data Integrity) ngay từ tầng code biên dịch (Compile-time type safety). Không ai có thể ghi một log tào lao vào hệ thống.
