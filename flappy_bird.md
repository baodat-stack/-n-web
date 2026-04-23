#  Business Requirements Document (BRD)  
##  Project: Duck Flappy Game

---

## 1.  Introduction

### 1.1 Purpose
Tài liệu này mô tả các yêu cầu nghiệp vụ cho ứng dụng trò chơi **Duck Flappy** – một game giải trí đơn giản, nơi người chơi điều khiển một con vịt vượt qua các cột bằng cách chạm vào màn hình để nhảy.

### 1.2 Scope
Ứng dụng sẽ được phát triển cho nền tảng:
- Mobile (Android/iOS) *(ưu tiên)*
- Có thể mở rộng sang Web hoặc Desktop trong tương lai

---

## 2.  Business Objectives

- Tạo một trò chơi đơn giản, dễ chơi, gây nghiện  
- Thu hút người dùng trong độ tuổi 10–35  
- Đạt số lượt tải mục tiêu: 10,000+ trong 3 tháng đầu  
- Tích hợp quảng cáo để tạo doanh thu  

---

## 3.  Stakeholders

| Stakeholder     | Vai trò |
|-----------------|--------|
| Product Owner   | Định hướng sản phẩm |
| Dev Team        | Phát triển ứng dụng |
| Designer        | Thiết kế UI/UX |
| QA Tester       | Kiểm thử |
| End Users       | Người chơi |

---

## 4.  Product Overview

### 4.1 Game Concept
- Người chơi điều khiển **con vịt**  
- Chạm vào màn hình để **vịt nhảy lên**  
- Vịt sẽ **rơi xuống do trọng lực**  
- Người chơi phải né các **cột (pipes)**  

### 4.2 Core Gameplay Loop
1. Bắt đầu game  
2. Nhấn để điều khiển vịt  
3. Tránh va chạm  
4. Ghi điểm khi vượt qua cột  
5. Game over khi va chạm  

---

## 5.  Functional Requirements

### 5.1 Gameplay Mechanics
- Nhấn màn hình → vịt nhảy lên  
- Không nhấn → vịt rơi xuống  
- Tạo cột ngẫu nhiên với khoảng cách hợp lý  
- Tăng độ khó theo thời gian  

### 5.2 Scoring System
- +1 điểm mỗi khi vượt qua 1 cột  
- Hiển thị điểm hiện tại  
- Lưu điểm cao nhất (High Score)  

### 5.3 Game States
- Start Screen  
- Playing  
- Game Over  
- Pause (optional)  

### 5.4 Collision Detection
- Va chạm với:
  - Cột  
  - Mặt đất  
→ Kết thúc game  

### 5.5 Sound & Effects
- Âm thanh khi:
  - Nhảy  
  - Ghi điểm  
  - Game over  
- Hiệu ứng chuyển động mượt  

---

## 6.  Non-Functional Requirements

### 6.1 Performance
- FPS ổn định (>= 60 FPS)  
- Load game < 3 giây  

### 6.2 Usability
- Giao diện đơn giản, dễ hiểu  
- Chơi bằng 1 tay  

### 6.3 Compatibility
- Android 8.0+  
- iOS 12+  

### 6.4 Reliability
- Không crash khi chơi lâu  
- Lưu dữ liệu chính xác  

---

## 7.  UI/UX Requirements

### 7.1 Main Screen
- Nút "Start"  
- Hiển thị High Score  

### 7.2 In-game UI
- Điểm số (top screen)  
- Background chuyển động  

### 7.3 Game Over Screen
- Hiển thị điểm  
- Nút:
  - Play Again  
  - Exit  

---

## 8.  Monetization

- Quảng cáo:
  - Banner ads  
  - Interstitial ads khi game over  
- (Optional) Remove ads bằng mua trong app  

---

## 9.  Future Enhancements

- Nhiều skin cho vịt  
- Nhiều môi trường (theme)  
- Leaderboard online  
- Multiplayer (optional)  

---

## 10.  Risks

| Risk | Mitigation |
|------|-----------|
| Game quá đơn giản | Thêm feature nâng cao |
| Người chơi chán nhanh | Tăng độ khó, thêm skin |
| Hiệu năng kém | Tối ưu code |

---

## 11.  Success Metrics

- Số lượt tải  
- Thời gian chơi trung bình  
- Tỷ lệ giữ chân người dùng (Retention Rate)  
- Doanh thu từ quảng cáo  

---

##  Notes

- Game lấy cảm hứng từ Flappy Bird nhưng cần có **bản sắc riêng (con vịt, đồ họa riêng)**  
- Tập trung vào trải nghiệm mượt và gây nghiện  
