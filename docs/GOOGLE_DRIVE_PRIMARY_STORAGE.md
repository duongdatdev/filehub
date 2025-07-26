# FileHub - Google Drive Primary Storage Implementation

## Tóm tắt thay đổi

Hệ thống FileHub đã được cập nhật để sử dụng **Google Drive làm phương thức lưu trữ chính** thay vì local storage như trước đây.

## Kiến trúc mới

### 🎯 **Lưu trữ ưu tiên (Primary Storage)**
- **Google Drive Simulation**: Phương thức lưu trữ chính
- Sử dụng thư mục `uploads/google-drive-simulation/` để mô phỏng Google Drive
- Mỗi file được gán một UUID duy nhất làm Drive File ID

### 🔄 **Lưu trữ dự phòng (Fallback Storage)**  
- **Local Storage**: Phương thức dự phòng
- Chỉ sử dụng khi Google Drive không khả dụng
- Đảm bảo file không bị mất khi có sự cố

## Cấu hình mới

### `application.properties`
```properties
# Cấu hình lưu trữ
file.storage.primary=google-drive        # Phương thức chính
file.storage.fallback=local             # Phương thức dự phòng

# Google Drive simulation
google.drive.enabled=true               # Bật Google Drive simulation
google.drive.application.name=FileHub
google.drive.folder.name=FileHub-Storage
```

## Quy trình hoạt động mới

### 📤 **Upload File**
```
1. Validate file (size, type, hash)
2. Tạo unique filename & Drive File ID
3. PRIMARY: Upload lên Google Drive simulation
   ├── Thành công → Lưu Drive File ID vào database
   └── Thất bại → Chuyển sang Fallback
4. FALLBACK: Lưu file vào local storage (nếu cần)
5. Cập nhật metadata trong database
```

### 📥 **Download File**
```
1. Tìm file metadata trong database
2. PRIMARY: Tải từ Google Drive (nếu có Drive File ID)
   ├── Thành công → Trả file về user
   └── Thất bại → Chuyển sang Fallback
3. FALLBACK: Tải từ local storage
4. Cập nhật download count
```

### 🗑️ **Delete File**
```
1. Đánh dấu soft delete trong database
2. PRIMARY: Xóa từ Google Drive simulation
3. FALLBACK: Xóa từ local storage (nếu có)
4. Log kết quả từng bước
```

## Tính năng mới

### ✅ **Reliability (Độ tin cậy)**
- **Dual storage**: File được lưu ở cả 2 nơi nếu cần
- **Graceful fallback**: Tự động chuyển sang phương thức dự phòng
- **Error recovery**: Hệ thống vẫn hoạt động khi 1 storage bị lỗi

### 📊 **Monitoring & Logging**
- **Detailed logs**: Chi tiết quá trình upload/download/delete
- **Storage source tracking**: Biết file đang được lấy từ đâu
- **Error tracking**: Theo dõi lỗi từng storage

### 🔧 **Configuration Flexibility**
- **Configurable priority**: Có thể đổi thứ tự ưu tiên storage
- **Enable/disable options**: Bật/tắt từng loại storage
- **Easy switching**: Dễ dàng chuyển đổi giữa các mode

## Google Drive Simulation

### Cách hoạt động
- **Thư mục simulation**: `uploads/google-drive-simulation/`
- **File naming**: `{UUID}_{original_filename}`
- **Unique IDs**: Mỗi file có Drive File ID duy nhất
- **API simulation**: Mô phỏng các API của Google Drive

### Lợi ích
- **Development friendly**: Không cần Google Cloud credentials thật
- **Testing**: Dễ dàng test tính năng mới
- **Migration**: Chuẩn bị sẵn cho Google Drive thật

## Migration từ phiên bản cũ

### File hiện có
- **Backward compatible**: File cũ vẫn hoạt động bình thường
- **Gradual migration**: File mới sẽ sử dụng kiến trúc mới
- **No data loss**: Không mất dữ liệu trong quá trình chuyển đổi

### Database
- **New fields**: `drive_file_id`, `drive_folder_id` đã có sẵn
- **Existing data**: Dữ liệu cũ không bị ảnh hưởng
- **Schema compatible**: Tương thích với schema hiện tại

## Troubleshooting

### Khi Google Drive không khả dụng
```
2024-XX-XX WARN: Failed to upload to Google Drive (primary), falling back to local storage
2024-XX-XX INFO: File stored in fallback storage (local): /uploads/filename.ext
```

### Khi cả 2 storage đều lỗi
```
2024-XX-XX ERROR: Failed to store file in both primary and fallback storage
Exception: IOException - Both storages unavailable
```

### Logs thường thấy
```
INFO: Google Drive simulation initialized. Storage directory: uploads/google-drive-simulation
INFO: File uploaded to Google Drive (primary): uuid-12345
INFO: File downloaded from Google Drive (primary): uuid-12345
INFO: File deleted from Google Drive (primary): uuid-12345
```

## Testing

### Verify Google Drive Priority
1. Upload file → Check logs cho "Google Drive (primary)"
2. Download file → Check logs cho download source  
3. Delete file → Verify xóa từ cả 2 storage

### Test Fallback Mechanism
1. Disable Google Drive: `google.drive.enabled=false`
2. Upload file → Should fallback to local
3. Enable Google Drive → New files use Drive priority

## Lưu ý quan trọng

### ⚠️ **Simulation vs Real Google Drive**
- Hiện tại đang dùng **simulation** cho development
- Để sử dụng Google Drive thật, cần:
  - Google Cloud Service Account
  - Google Drive API credentials
  - Cập nhật `GoogleDriveServiceImpl` với real API calls

### 🔐 **Security**
- Service account file được gitignore
- Credentials không được commit vào code
- Production cần real Google Cloud setup

### 📈 **Performance**
- Google Drive có thể chậm hơn local storage
- Network dependency cho primary storage  
- Cân nhắc caching strategy cho tương lai

---

## Next Steps

1. **Production Setup**: Thay simulation bằng real Google Drive API
2. **Monitoring**: Thêm metrics cho storage performance  
3. **Caching**: Implement cache layer cho frequently accessed files
4. **Compression**: Tự động nén file trước khi upload
5. **Sync**: Đồng bộ file giữa storages nếu cần
