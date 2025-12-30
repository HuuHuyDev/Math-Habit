# FCM Token - Lỗi Đã Fix ✅

## Các Lỗi Đã Sửa:

### 1. ✅ Missing Method `resendSavedToken()`
- **Lỗi**: `LoginActivity` gọi `fcmTokenManager.resendSavedToken()` nhưng method không tồn tại
- **Fix**: Thêm method `resendSavedToken()` vào `FcmTokenManager.java`

### 2. ✅ Inconsistent Token Handling
- **Lỗi**: `MyFirebaseMessagingService.onNewToken()` có method `sendTokenToServer()` rỗng
- **Fix**: Sử dụng `FcmTokenManager.sendTokenToServer()` thay vì method cũ

### 3. ✅ Code Cleanup
- **Fix**: Xóa method `sendTokenToServer()` cũ trong `MyFirebaseMessagingService`
- **Fix**: Đảm bảo tất cả FCM operations đều thông qua `FcmTokenManager`

## Files Đã Sửa:

1. **`FcmTokenManager.java`**:
   - ✅ Thêm method `resendSavedToken()`
   - ✅ Method này kiểm tra token đã lưu và gửi lên server

2. **`MyFirebaseMessagingService.java`**:
   - ✅ Cập nhật `onNewToken()` để sử dụng `FcmTokenManager`
   - ✅ Xóa method `sendTokenToServer()` cũ

## Cách Hoạt Động:

### Login Flow:
1. User login → `AuthRepository.registerFcmToken()`
2. `FcmTokenManager.registerToken()` → `refreshAndSendToken()`
3. Lấy FCM token từ Firebase → Gửi lên server

### Token Refresh:
1. Firebase tạo token mới → `MyFirebaseMessagingService.onNewToken()`
2. Lưu token vào SharedPref
3. `FcmTokenManager.sendTokenToServer()` → Gửi lên server

### Logout Flow:
1. User logout → `AuthRepository` gọi `FcmTokenManager.unregisterToken()`
2. Gửi DELETE request lên server
3. Clear local data

### Resend Token:
1. App khởi động → `LoginActivity.resendFcmToken()`
2. `FcmTokenManager.resendSavedToken()` → Kiểm tra token đã lưu
3. Nếu có token → Gửi lên server, nếu không → Lấy token mới

## Test FCM:

### 1. Test Token Registration:
```java
// Trong LoginActivity hoặc MainActivity
FcmTokenManager fcmTokenManager = new FcmTokenManager(this);
fcmTokenManager.refreshAndSendToken();
```

### 2. Test Token Removal:
```java
// Khi logout
FcmTokenManager fcmTokenManager = new FcmTokenManager(this);
fcmTokenManager.unregisterToken();
```

### 3. Check Logs:
- Tag: `FcmTokenManager`
- Success: "FCM token sent to server successfully"
- Error: "Failed to send FCM token: [error_code]"

## Backend Status:
- ✅ Server đang chạy: `http://192.168.56.2:8080/api/`
- ✅ Endpoints: `/device/fcm-token` (POST/DELETE)
- ⚠️ Cần FCM Server Key thật trong `.env`

## Next Steps:
1. Test trên thiết bị thật
2. Thêm FCM Server Key vào backend
3. Test push notification end-to-end