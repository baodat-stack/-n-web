# Kế Hoạch Triển Khai: Hệ Thống Chữ Ký Số (Digital Signature)
## ShopSphere E-Commerce — Java Servlet/JSP + Tomcat 10

Tích hợp hệ thống chữ ký số RSA-2048 / SHA256withRSA vào dự án ShopSphere hiện có.  
**Nguyên tắc bất biến:** Server KHÔNG BAO GIỜ sinh hoặc thấy Private Key — Private Key chỉ tồn tại trong `DigitalSignTool.jar` trên máy người dùng.

---

## Sequence Flow Chuẩn

```
User (Offline)                          Website (Online)
──────────────────                      ──────────────────────────────────
DigitalSignTool.jar
  │
  ├─ [1] Generate Key Pair
  │     ├─ public.key   ──── Upload ──▶ UploadPublicKeyServlet
  │     │                                  │ Kiểm tra hasActiveKey()
  │     │                                  │ Nếu đã có ACTIVE KEY → TỪ CHỐI
  │     │                                  │ Lưu public_key + fingerprint
  │     └─ private.key [chỉ lưu máy]       │ Ghi key_history: GENERATED
  │                                         │ Ghi audit_logs: UPLOAD_PUBLIC_KEY
  │
  ├─ [1b] Import Existing Key (đổi máy)   (không cần upload lại nếu đã có)
  │     └─ Đọc private.key cũ từ file
  │
  │                           User đặt đơn → Download order.json
  │                           ⬅─────────── DownloadOrderJsonServlet
  │                                         │ Tạo record order_sign_requests (id=15)
  │                                         │ Lưu original_order_json + original_order_hash
  │                                         │ ★ Nhúng requestId=15 vào file JSON
  │                                         │ Ghi audit_logs: DOWNLOAD_JSON
  │
  ├─ [2] Sign order.json (có chứa requestId)
  │   (SHA256withRSA + private.key)
  │     └─ order_xxx.sig  ──── Upload ──▶ UploadSignatureServlet
  │                                         │ Đọc requestId từ sig/form
  │                                         │ Lấy ĐÚNG order_sign_requests theo requestId
  │                                         │ (không dùng getLatestByOrder)
  │                                         │ Kiểm tra đơn chỉ có 1 chữ ký
  │                                         │ Kiểm tra order.status (PENDING/CONFIRMED)
  │                                         │ Kiểm tra key.status == ACTIVE
  │                                         │ Tính hash hiện tại vs original_order_hash
  │                                         │    Khác → TAMPERED
  │                                         │ Xác thực chữ ký RSA
  │                                         │    Sai → INVALID
  │                                         │    Đúng → VERIFIED
  │                                         │ Lưu order_signatures
  │                                         │ Ghi verification_logs
  │                                         │ Ghi audit_logs: UPLOAD_SIG + VERIFY
  │
  ├─ [3] Verify Local (tuỳ chọn trước khi upload)
  │
  └─ Download order_xxx.sig ◀── Download ── DownloadSignatureServlet
                                             Ghi audit_logs: DOWNLOAD_SIGNATURE
```

> ❌ **Server KHÔNG sinh Private Key.**  
> ✅ **Private Key chỉ được tạo trong `DigitalSignTool.jar`.**  
> ❌ **Server KHÔNG tự revoke key cũ khi upload key mới.**  
> ✅ **User phải chủ động báo mất khóa trước, sau đó mới được upload key mới.**

---

## Phase 1 — Database

### [NEW] `digital_signature_schema.sql`

#### Bảng `user_keys` — Khóa công khai của người dùng
```sql
CREATE TABLE user_keys (
  id          INT PRIMARY KEY AUTO_INCREMENT,
  user_id     INT NOT NULL,
  public_key  LONGTEXT NOT NULL,
  fingerprint VARCHAR(64) NOT NULL,   -- SHA256(public_key), dùng để nhận diện khóa
  issue_date  DATETIME DEFAULT NOW(),
  revoke_date DATETIME,
  status      VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE | REVOKED
  FOREIGN KEY (user_id) REFERENCES users(id)
);
```

> **Fingerprint** = `SHA256(public_key_base64)`, lưu dạng hex 64 ký tự.  
> Dùng để hiển thị "dấu vân tay khóa" cho user nhận diện, thay vì lộ cả public key dài.

#### Bảng `key_history` — Lịch sử toàn bộ hành động với khóa
```sql
CREATE TABLE key_history (
  id          INT PRIMARY KEY AUTO_INCREMENT,
  user_id     INT NOT NULL,
  key_id      INT NOT NULL,
  action      VARCHAR(20) NOT NULL, -- GENERATED | REVOKED
  action_date DATETIME DEFAULT NOW(),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (key_id)  REFERENCES user_keys(id)
);
```

#### Bảng `order_sign_requests` — Lưu JSON gốc mỗi lần Download *(tách riêng)*
```sql
CREATE TABLE order_sign_requests (
  id                  INT PRIMARY KEY AUTO_INCREMENT,
  order_id            INT NOT NULL,
  user_id             INT NOT NULL,
  original_order_json LONGTEXT NOT NULL,    -- JSON gốc lúc user Download
  original_order_hash VARCHAR(255) NOT NULL, -- SHA256(original_order_json)
  download_time       DATETIME DEFAULT NOW(),
  FOREIGN KEY (order_id) REFERENCES orders(id),
  FOREIGN KEY (user_id)  REFERENCES users(id)
);
```

> **Tại sao tách riêng bảng này?**
> - User có thể Download nhiều lần trước khi ký (lần 1, lần 2, lần 3...)
> - Mỗi lần Download tạo 1 record mới (id tăng dần)
> - Lúc Download, `order_signatures` **chưa tồn tại** → không thể lưu vào đó

> **Giải quyết vấn đề "ký sai bản":**
> - Mỗi lần Download, server nhúng `requestId` vào JSON
> - Khi Upload sig, user gửi kèm `requestId`
> - Server lấy **đúng record** theo `requestId`, KHÔNG dùng `getLatestByOrder()`
> - Đảm bảo verify đúng bản JSON mà user thực sự đã ký

#### Bảng `order_signatures` — Chữ ký của đơn hàng
```sql
CREATE TABLE order_signatures (
  id              INT PRIMARY KEY AUTO_INCREMENT,
  order_id        INT NOT NULL UNIQUE,  -- UNIQUE: 1 đơn chỉ có 1 chữ ký
  key_id          INT NOT NULL,
  request_id      INT NOT NULL,         -- Liên kết đến đúng order_sign_requests đã ký
  signature_data  LONGTEXT NOT NULL,
  sign_time       DATETIME DEFAULT NOW(),
  verify_status   VARCHAR(20) DEFAULT 'SIGNED',
  FOREIGN KEY (order_id)   REFERENCES orders(id),
  FOREIGN KEY (key_id)     REFERENCES user_keys(id),
  FOREIGN KEY (request_id) REFERENCES order_sign_requests(id)
);
```

**Chính sách 1 chữ ký / đơn:**
- Cột `order_id` có ràng buộc `UNIQUE` → DB tự chặn insert lần 2
- Servlet kiểm tra trước: nếu đã có chữ ký → báo lỗi

**Phân biệt trạng thái `verify_status`:**

| Trạng thái | Ý nghĩa |
|---|---|
| `NOT_SIGNED` | Đơn chưa được ký |
| `SIGNED` | Đã upload chữ ký, chưa verify |
| `VERIFIED` | Chữ ký hợp lệ, dữ liệu toàn vẹn |
| `INVALID` | Chữ ký sai (sai private key hoặc sai public key) |
| `TAMPERED` | Hash hiện tại ≠ hash lúc download → DB bị sửa sau khi ký |
| `KEY_REVOKED` | Khóa đã bị thu hồi tại thời điểm upload → đơn không hợp lệ |

#### Bảng `verification_logs` — Ghi mỗi lần verify
```sql
CREATE TABLE verification_logs (
  id          INT PRIMARY KEY AUTO_INCREMENT,
  order_id    INT NOT NULL,
  user_id     INT NOT NULL,
  verify_time DATETIME DEFAULT NOW(),
  result      VARCHAR(20) NOT NULL,  -- VERIFIED | INVALID | TAMPERED | KEY_REVOKED
  message     TEXT,
  FOREIGN KEY (order_id) REFERENCES orders(id)
);
```

#### Bảng `audit_logs` — Nhật ký hành động bảo mật
```sql
CREATE TABLE audit_logs (
  id          INT PRIMARY KEY AUTO_INCREMENT,
  user_id     INT,
  order_id    INT,
  action_type VARCHAR(50) NOT NULL,  -- Dùng enum AuditAction trong Java để tránh typo
  description TEXT,
  created_at  DATETIME DEFAULT NOW()
);
```

---

## Phase 2 — Tool Offline (`DigitalSignTool.jar`)

**Chạy hoàn toàn offline — không cần Internet.**

### [NEW] `DigitalSignTool.java` (standalone Java SE)

**Menu:**

| # | Chức năng | Mô tả |
|---|---|---|
| **1** | **Generate Key Pair** | Sinh RSA-2048 → xuất `public.key` + `private.key` (Base64) |
| **2** | **Import Existing Key** | Đọc `private.key` từ file → dùng cho máy mới / cài lại Windows |
| **3** | **Sign Order** | Đọc `order.json` + `private.key` → xuất `order_{id}.sig` |
| **4** | **Verify Local** | Kiểm tra `order.json` + `order.sig` + `public.key` trước upload |

> **Import Existing Key** xử lý: đổi máy, cài lại Windows, backup key sang thiết bị khác.  
> Chỉ cần copy `private.key` + `public.key` sang máy mới → Tool dùng lại được.

**JSON chuẩn khi Download (chứa `requestId` do server nhúng):**
```json
{
  "requestId": 15,
  "orderId": 1,
  "userId": 5,
  "items": [
    {"productId": 10, "quantity": 2, "unitPrice": 100000}
  ],
  "totalPrice": 200000,
  "orderDate": "2026-06-15T10:00:00"
}
```

> ⚠️ `requestId` — server nhúng vào để khi upload sig, biết đúng bản nào đã ký  
> ⚠️ `orderId` + `userId` — chống **Replay Attack** (không thể gắn sig đơn #1 vào đơn #2)

**Thuật toán:** `SHA256withRSA`, khóa `RSA 2048 bit`

---

## Phase 3 — Backend Website

### Enum Layer

#### [NEW] `AuditAction.java` (enum)
```java
public enum AuditAction {
    UPLOAD_PUBLIC_KEY,
    UPLOAD_SIG,
    VERIFY,
    REVOKE_KEY,
    DOWNLOAD_JSON,
    DOWNLOAD_SIGNATURE;
}
```
> Dùng enum thay vì raw String → tránh typo, IDE hỗ trợ autocomplete.  
> Khi ghi DB: `AuditAction.VERIFY.name()` → `"VERIFY"`  
> Khi filter JSP: so sánh bằng `AuditAction.valueOf(str)`

---

### Model Layer

#### [NEW] `UserKey.java`
Fields: `id`, `userId`, `publicKey`, `fingerprint`, `issueDate`, `revokeDate`, `status`

#### [NEW] `KeyHistory.java`
Fields: `id`, `userId`, `keyId`, `action`, `actionDate`

#### [NEW] `OrderSignRequest.java`
Fields: `id`, `orderId`, `userId`, `originalOrderJson`, `originalOrderHash`, `downloadTime`

#### [NEW] `OrderSignature.java`
Fields: `id`, `orderId`, `keyId`, `requestId`, `signatureData`, `signTime`, `verifyStatus`

#### [NEW] `VerificationLog.java`
Fields: `id`, `orderId`, `userId`, `verifyTime`, `result`, `message`

#### [NEW] `AuditLog.java`
Fields: `id`, `userId`, `orderId`, `actionType` (**AuditAction**), `description`, `createdAt`

---

### DAO Layer

#### [NEW] `UserKeyDAO.java`
- `saveKey(int userId, String publicKeyBase64, String fingerprint)` — lưu khóa mới, ghi `key_history: GENERATED`
- `getActiveKey(int userId)` — lấy khóa ACTIVE
- `getKeyById(int keyId)` — lấy khóa theo id
- `revokeKey(int keyId)` — đặt `status=REVOKED`, ghi `revoke_date`, ghi `key_history: REVOKED`
- `hasActiveKey(int userId)` — trả về true/false, dùng để chặn upload key mới
- `getKeyHistory(int userId)` — lịch sử khóa

#### [NEW] `OrderSignRequestDAO.java`
- `save(int orderId, int userId, String originalJson, String originalHash)` → trả về `requestId` (auto-increment)
- `getById(int requestId)` — **lấy đúng record theo requestId** (thay vì getLatestByOrder)

#### [NEW] `SignatureDAO.java`
- `hasSignature(int orderId)` — kiểm tra đơn đã có chữ ký chưa
- `saveSignature(int orderId, int keyId, int requestId, String sigData, String verifyStatus)` — lưu chữ ký
- `getSignatureByOrder(int orderId)` — lấy chữ ký
- `updateVerifyStatus(int sigId, String status)` — cập nhật kết quả

#### [NEW] `VerificationLogDAO.java`
- `log(int orderId, int userId, String result, String message)` — ghi mỗi lần verify

#### [NEW] `AuditLogDAO.java`
- `log(int userId, int orderId, AuditAction action, String description)` — dùng **enum** thay String

---

### Service Layer

#### [NEW] `KeyManagementService.java`
```
uploadPublicKey(int userId, String publicKeyBase64):
  1. Validate Base64 RSA-2048 format
  2. if hasActiveKey(userId):
       throw Exception("Bạn đang có khóa hoạt động. Vui lòng báo mất khóa trước.")
       ← KHÔNG tự revoke, bắt buộc user chủ động
  3. Tính fingerprint = SHA256(publicKeyBase64)
  4. Lưu vào user_keys (status=ACTIVE)
  5. Ghi key_history: GENERATED
  6. Ghi audit_logs: AuditAction.UPLOAD_PUBLIC_KEY

revokeUserKey(int userId):
  1. Lấy ACTIVE key
  2. Đặt status=REVOKED, revoke_date=NOW()
  3. Ghi key_history: REVOKED
  4. Ghi audit_logs: AuditAction.REVOKE_KEY
```

#### [NEW] `SignatureService.java`

```
verifyAndSaveSignature(orderId, requestId, uploadedSigBytes, userId):
                         ↑ requestId do client gửi lên khi upload

  Bước 0 — Chính sách 1 chữ ký / đơn:
    if SignatureDAO.hasSignature(orderId):
        return ERROR("Đơn này đã có chữ ký. Không cho phép upload lại.")

  Bước 1 — Kiểm tra trạng thái đơn hàng:
    if order.status IN (PACKAGING, SHIPPING, COMPLETED, CANCELLED):
        return REJECTED("Đơn hàng không được phép ký")
    Chỉ cho phép: PENDING | CONFIRMED

  Bước 2 — Kiểm tra khóa có ACTIVE không:
    activeKey = UserKeyDAO.getActiveKey(userId)
    if activeKey == null:
        → ghi verification_logs: KEY_REVOKED
        → return KEY_REVOKED
    ← ĐƠN GIẢN: không cố chứng minh sign_time vs revoke_time
       vì server không biết user ký offline lúc mấy giờ

  Bước 3 — Lấy đúng bản Download theo requestId:
    signRequest = OrderSignRequestDAO.getById(requestId)
      ★ KHÔNG dùng getLatestByOrder() → tránh lỗi khi download nhiều lần
    if signRequest == null || signRequest.orderId != orderId:
        return ERROR("Request không hợp lệ")
    Tính SHA256(order_json_hiện_tại_từ_DB)
    if hash_hiện_tại ≠ signRequest.original_order_hash:
        → ghi verification_logs: TAMPERED
        → return TAMPERED ("Dữ liệu đơn hàng đã bị thay đổi sau khi download")

  Bước 4 — Xác thực chữ ký RSA:
    Dùng signRequest.original_order_json làm dữ liệu gốc
    SHA256withRSA.verify(activeKey.publicKey, original_order_json, uploadedSig):
        Đúng  → ghi verification_logs: VERIFIED → return VERIFIED
        Sai   → ghi verification_logs: INVALID  → return INVALID
```

> **Tại sao dùng `requestId` thay `getLatestByOrder()`?**
> ```
> Download lần 1 → request #1 (user ký bản này)
> Download lần 2 → request #2
> Download lần 3 → request #3
> Upload sig      → server cần verify bằng request #1, không phải #3
> ```
> JSON đã chứa `requestId: 1` → server lấy đúng bản → verify chính xác.

**Ví dụ thực tế:**

| Tình huống | Kết quả |
|---|---|
| User có ACTIVE KEY, upload sig đúng | **VERIFIED** ✅ |
| User đã revoke key, upload sig | **KEY_REVOKED** ❌ |
| Upload key mới khi đang có ACTIVE KEY | **Từ chối upload** 🚫 |
| Sửa `quantity` trong DB sau khi download | **TAMPERED** ⚠️ |
| Upload sai file `.sig` | **INVALID** ❌ |
| Upload sig lần 2 cho cùng 1 đơn | **Từ chối** 🚫 |
| Chưa download order.json mà upload sig | **Lỗi** 🚫 |
| Gắn sig đơn #1 vào đơn #2 (Replay Attack) | **INVALID** ❌ |
| Download 3 lần, ký bản 1, upload → dùng requestId | **VERIFIED** ✅ (verify đúng bản) |

#### [NEW] `OrderJsonBuilder.java` (util)
- `buildJson(int orderId, int requestId)` → JSON chuẩn từ DB, **nhúng `requestId`** vào JSON
- `computeHash(String json)` → SHA256 hex string

---

### Servlet Layer

#### [NEW] `UploadPublicKeyServlet.java` — `POST /user/uploadPublicKey`
- Nhận file `public.key` từ multipart upload
- Gọi `KeyManagementService.uploadPublicKey()` → **Nếu đã có ACTIVE KEY → trả lỗi 400**
- Ghi `audit_logs`: `AuditAction.UPLOAD_PUBLIC_KEY`

#### [NEW] `UploadSignatureServlet.java` — `POST /user/uploadSignature`
- Nhận: `orderId` + `requestId` + file `.sig`
- **Dùng `requestId` để lấy đúng bản Download đã ký**
- Kiểm tra đã có chữ ký chưa → từ chối nếu đã có
- Kiểm tra `order.status` (PENDING/CONFIRMED)
- Gọi `SignatureService.verifyAndSaveSignature(orderId, requestId, ...)`
- Ghi `verification_logs` + `audit_logs`

#### [NEW] `RevokeKeyServlet.java` — `POST /user/revokeKey`
- Gọi `KeyManagementService.revokeUserKey()`
- Ghi `audit_logs`: `AuditAction.REVOKE_KEY`

#### [NEW] `DownloadOrderJsonServlet.java` — `GET /user/downloadOrderJson?orderId=X`
- Tạo record `order_sign_requests` → nhận `requestId`
- Tái tạo JSON chuẩn qua `OrderJsonBuilder.buildJson(orderId, requestId)` → **nhúng `requestId` vào JSON**
- Lưu `original_order_json` + `original_order_hash` vào `order_sign_requests`
- Xuất file `order_{id}.json`
- Ghi `audit_logs`: `AuditAction.DOWNLOAD_JSON`

#### [NEW] `DownloadSignatureServlet.java` — `GET /user/downloadSignature?orderId=X`
- Lấy `signature_data` từ `order_signatures`
- Xuất file `order_{id}.sig`
- Ghi `audit_logs`: `AuditAction.DOWNLOAD_SIGNATURE`

#### [NEW] `AuditLogServlet.java` — `GET /admin/auditLogs`
- Admin xem `audit_logs` với filter theo `AuditAction` enum

#### [NEW] `VerificationLogServlet.java` — `GET /admin/verificationLogs`
- Admin xem `verification_logs` — toàn bộ lịch sử verify

---

## Phase 4 — Giao Diện (JSP)

### User Pages

#### [NEW] `user/keyManagement.jsp`
- Trạng thái khóa: ACTIVE / REVOKED / Chưa có
- **Fingerprint** của khóa hiện tại (64 ký tự hex)
- Form **Upload `public.key`**:
  - Nếu đang có ACTIVE KEY → cảnh báo đỏ + **ẩn form**
  - Nếu không có KEY → hiện form upload
- Nút **Report Lost Key** → thu hồi khóa (chỉ hiện khi có ACTIVE KEY)
- Bảng `key_history`: `action`, `action_date`, `fingerprint`

#### [MODIFY] `user/orderDetails.jsp`
Thêm section **Chữ Ký Số**:
- Nút **Download `order.json`** → ký offline
- Form **Upload `.sig`** (kèm hidden field `requestId`):
  - Chỉ hiện khi: status PENDING/CONFIRMED **VÀ** đơn chưa có chữ ký **VÀ** user có ACTIVE KEY
  - Nếu đã có chữ ký → ẩn form, hiện "Đơn này đã được ký"
  - Nếu chưa có key → hiện "Bạn cần upload khóa công khai trước"
- Nút **Download `.sig`** (nếu đã có chữ ký)
- Badge trạng thái: 🟢 VERIFIED / 🔴 INVALID / 🟠 TAMPERED / 🔵 SIGNED / ⛔ KEY_REVOKED / ⚪ NOT_SIGNED

#### [MODIFY] `user/orders.jsp`
- Thêm cột **Chữ Ký** với badge màu

### Admin Pages

#### [NEW] `admin/auditLogs.jsp`
- Bảng: `user`, `order_id`, `action_type`, `description`, `created_at`
- Filter dropdown dùng giá trị từ `AuditAction` enum

#### [NEW] `admin/verificationLogs.jsp`
- Bảng: `order_id`, `user`, `verify_time`, `result` (màu sắc), `message`
- Hiện tất cả lần verify — kể cả INVALID/TAMPERED

#### [MODIFY] `admin/viewOrders.jsp`
- Thêm cột **Verify Status** với màu sắc

---

## Phase 5 — Cấu Hình

#### [MODIFY] `web.xml`
Đăng ký 7 Servlet mới:
- `UploadPublicKeyServlet` → `/user/uploadPublicKey`
- `UploadSignatureServlet` → `/user/uploadSignature`
- `RevokeKeyServlet` → `/user/revokeKey`
- `DownloadOrderJsonServlet` → `/user/downloadOrderJson`
- `DownloadSignatureServlet` → `/user/downloadSignature`
- `AuditLogServlet` → `/admin/auditLogs`
- `VerificationLogServlet` → `/admin/verificationLogs`

#### [MODIFY] `navbar.jsp`
- Thêm menu **"Quản lý Khóa"** cho User
- Thêm menu **"Audit Logs"** + **"Verification Logs"** cho Admin

---

## Tổng Hợp File

| Loại | File | Hành động |
|---|---|---|
| SQL | `digital_signature_schema.sql` | [NEW] |
| Java Tool | `DigitalSignTool.java` | [NEW] |
| Enum | `AuditAction.java` | [NEW] |
| Model | `UserKey.java` | [NEW] |
| Model | `KeyHistory.java` | [NEW] |
| Model | `OrderSignRequest.java` | [NEW] |
| Model | `OrderSignature.java` | [NEW] |
| Model | `VerificationLog.java` | [NEW] |
| Model | `AuditLog.java` | [NEW] |
| DAO | `UserKeyDAO.java` | [NEW] |
| DAO | `OrderSignRequestDAO.java` | [NEW] |
| DAO | `SignatureDAO.java` | [NEW] |
| DAO | `VerificationLogDAO.java` | [NEW] |
| DAO | `AuditLogDAO.java` | [NEW] |
| Service | `KeyManagementService.java` | [NEW] |
| Service | `SignatureService.java` | [NEW] |
| Util | `OrderJsonBuilder.java` | [NEW] |
| Servlet | `UploadPublicKeyServlet.java` | [NEW] |
| Servlet | `UploadSignatureServlet.java` | [NEW] |
| Servlet | `RevokeKeyServlet.java` | [NEW] |
| Servlet | `DownloadOrderJsonServlet.java` | [NEW] |
| Servlet | `DownloadSignatureServlet.java` | [NEW] |
| Servlet | `AuditLogServlet.java` | [NEW] |
| Servlet | `VerificationLogServlet.java` | [NEW] |
| JSP | `user/keyManagement.jsp` | [NEW] |
| JSP | `admin/auditLogs.jsp` | [NEW] |
| JSP | `admin/verificationLogs.jsp` | [NEW] |
| JSP | `user/orderDetails.jsp` | [MODIFY] |
| JSP | `user/orders.jsp` | [MODIFY] |
| JSP | `admin/viewOrders.jsp` | [MODIFY] |
| Config | `web.xml` | [MODIFY] |
| JSP | `navbar.jsp` | [MODIFY] |

**Tổng: 20 file mới + 12 file sửa = 32 file**

---

## Tiêu Chí Nghiệm Thu

| # | Tiêu chí | Cách kiểm tra |
|---|---|---|
| 1 | Sinh key offline | `DigitalSignTool.jar` → Generate → `public.key` + `private.key` |
| 2 | Import key đổi máy | Copy key sang máy mới → Tool Import → Sign được |
| 3 | Upload `public.key` khi chưa có key | Upload thành công, fingerprint hiển thị |
| 4 | **Upload key khi đang có ACTIVE KEY** | **Từ chối**, hiện thông báo cảnh báo |
| 5 | Báo mất khóa → upload key mới | Revoke → upload key mới thành công |
| 6 | Fingerprint hiển thị đúng | Trang keyManagement hiện 64 ký tự hex |
| 7 | Download `order.json` chứa `requestId` | File JSON có trường `requestId`, DB có record |
| 8 | Download nhiều lần → mỗi lần `requestId` khác | Kiểm tra bảng `order_sign_requests` |
| 9 | **Ký bản cũ, upload → verify đúng bản** | Download 3 lần, ký bản 1 → dùng requestId=1 → VERIFIED |
| 10 | Ký offline + upload `.sig` → VERIFIED | `verify_status = VERIFIED` |
| 11 | **Upload sig lần 2 → bị từ chối** | Thông báo "Đơn đã có chữ ký" |
| 12 | Sửa DB sau khi download → TAMPERED | Sửa `quantity` → hash không khớp → TAMPERED |
| 13 | Sai chữ ký → INVALID | Upload sai `.sig` |
| 14 | **Key đã REVOKED → upload sig** | **KEY_REVOKED** (chỉ kiểm tra status lúc upload) |
| 15 | Chống Replay Attack | Sig đơn #1 gắn vào đơn #2 → INVALID |
| 16 | Đơn PACKAGING bị từ chối ký | Status reject |
| 17 | Download `.sig` | Tải lại file chữ ký |
| 18 | `verification_logs` ghi đủ | Mọi lần verify đều có record |
| 19 | `audit_logs` dùng enum | Kiểm tra 6 giá trị `AuditAction`, không typo |
| 20 | Tool chạy offline | Tắt mạng → Generate + Sign vẫn hoạt động |
