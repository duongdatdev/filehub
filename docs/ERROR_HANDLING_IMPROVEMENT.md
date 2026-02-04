# Cải Tiến Xử Lý Lỗi File Upload

## Vấn đề
Trước đây, khi người dùng upload file mà thiếu các trường bắt buộc (như `department_id`, `file_type_id`), hệ thống hiển thị lỗi SQL thô khó hiểu:

```
could not execute statement [Column 'department_id' cannot be null] [insert into files ...]
```

## Giải pháp

### 1. Tạo Custom Exceptions
Đã tạo các exception riêng để xử lý lỗi:
- `FileUploadException`: Lỗi liên quan đến upload file
- `ValidationException`: Lỗi validation dữ liệu

**File:** `src/main/java/com/duongdat/filehub/exception/`

### 2. Global Exception Handler
Tạo `GlobalExceptionHandler` để xử lý tất cả các lỗi một cách tập trung và thân thiện:

**File:** `src/main/java/com/duongdat/filehub/exception/GlobalExceptionHandler.java`

#### Các loại lỗi được xử lý:

1. **ValidationException**: Lỗi validation từ business logic
2. **FileUploadException**: Lỗi upload file
3. **DataIntegrityViolationException**: Lỗi database constraints với thông báo tiếng Việt:
   - `department_id` null → "Vui lòng chọn phòng ban. Trường phòng ban là bắt buộc."
   - `file_type_id` null → "Vui lòng chọn loại file. Trường loại file là bắt buộc."
   - Duplicate entry → "File này đã tồn tại trong hệ thống."
   - Foreign key constraint → "Phòng ban được chọn không tồn tại hoặc đã bị xóa."
4. **MethodArgumentNotValidException**: Lỗi validation từ @Valid annotations
5. **MaxUploadSizeExceededException**: File quá lớn
6. **AccessDeniedException**: Không có quyền truy cập
7. **RuntimeException**: Các lỗi runtime với message thân thiện
8. **Exception**: Các lỗi chung không xác định

### 3. Validation Sớm trong FileService
Thêm method `validateUploadRequest()` để validate dữ liệu TRƯỚC KHI lưu vào database:

**File:** `src/main/java/com/duongdat/filehub/service/FileService.java`

```java
private void validateUploadRequest(FileUploadRequest request) {
    if (request.getDepartmentId() == null) {
        throw new RuntimeException("Vui lòng chọn phòng ban. Trường phòng ban là bắt buộc.");
    }
    
    if (request.getFileTypeId() == null) {
        throw new RuntimeException("Vui lòng chọn loại file. Trường loại file là bắt buộc.");
    }
    
    // Validate visibility value
    if (request.getVisibility() != null) {
        String visibility = request.getVisibility().toUpperCase();
        if (!visibility.equals("PRIVATE") && !visibility.equals("DEPARTMENT") && !visibility.equals("PUBLIC")) {
            throw new RuntimeException("Quyền truy cập không hợp lệ. Chỉ chấp nhận: PRIVATE, DEPARTMENT, hoặc PUBLIC.");
        }
    }
}
```

### 4. Cập Nhật Thông Báo Lỗi Tiếng Việt
Cập nhật các thông báo lỗi trong `UserAuthorizationService`:

**File:** `src/main/java/com/duongdat/filehub/service/UserAuthorizationService.java`

- "You don't have permission..." → "Bạn không có quyền..."
- "You must be assigned..." → "Bạn phải được phân công..."
- "User not authenticated" → "Người dùng chưa đăng nhập"

### 5. Cập Nhật ApiResponse
Thêm overload method để hỗ trợ trả về data kèm error message:

**File:** `src/main/java/com/duongdat/filehub/dto/response/ApiResponse.java`

```java
public static <T> ApiResponse<T> error(String message, T data) {
    return new ApiResponse<>(false, message, data);
}
```

## Kết quả

### Trước khi cải tiến:
```json
{
  "success": false,
  "message": "could not execute statement [Column 'department_id' cannot be null] [insert into files ...]",
  "data": null
}
```

### Sau khi cải tiến:
```json
{
  "success": false,
  "message": "Vui lòng chọn phòng ban. Trường phòng ban là bắt buộc.",
  "data": null
}
```

## Các Thông Báo Lỗi Mới

### Lỗi Validation:
- ✅ "Vui lòng chọn phòng ban. Trường phòng ban là bắt buộc."
- ✅ "Vui lòng chọn loại file. Trường loại file là bắt buộc."
- ✅ "Quyền truy cập không hợp lệ. Chỉ chấp nhận: PRIVATE, DEPARTMENT, hoặc PUBLIC."

### Lỗi Quyền:
- ✅ "Bạn không có quyền upload file vào phòng ban này"
- ✅ "Bạn không có quyền upload file vào dự án này"
- ✅ "Bạn phải được phân công vào một phòng ban để có thể upload file"
- ✅ "Bạn không có quyền thực hiện thao tác này."

### Lỗi Database:
- ✅ "File này đã tồn tại trong hệ thống."
- ✅ "Phòng ban được chọn không tồn tại hoặc đã bị xóa."
- ✅ "Dự án được chọn không tồn tại hoặc đã bị xóa."
- ✅ "Loại file được chọn không hợp lệ."

### Lỗi Upload:
- ✅ "File quá lớn. Vui lòng chọn file nhỏ hơn."
- ✅ "Đã xảy ra lỗi. Vui lòng thử lại sau."
- ✅ "Đã xảy ra lỗi không mong muốn. Vui lòng liên hệ quản trị viên."

## Testing

### Test Case 1: Upload file không có departmentId
**Request:**
```bash
POST /api/files/upload
{
  "file": "test.pdf",
  "fileTypeId": 1
  # Missing departmentId
}
```

**Response:**
```json
{
  "success": false,
  "message": "Vui lòng chọn phòng ban. Trường phòng ban là bắt buộc.",
  "data": null
}
```

### Test Case 2: Upload file không có fileTypeId
**Request:**
```bash
POST /api/files/upload
{
  "file": "test.pdf",
  "departmentId": 1
  # Missing fileTypeId
}
```

**Response:**
```json
{
  "success": false,
  "message": "Vui lòng chọn loại file. Trường loại file là bắt buộc.",
  "data": null
}
```

### Test Case 3: Upload file với departmentId không tồn tại
**Request:**
```bash
POST /api/files/upload
{
  "file": "test.pdf",
  "departmentId": 9999,  # Không tồn tại
  "fileTypeId": 1
}
```

**Response:**
```json
{
  "success": false,
  "message": "Phòng ban được chọn không tồn tại hoặc đã bị xóa.",
  "data": null
}
```

### Test Case 4: Upload duplicate file
**Request:**
```bash
POST /api/files/upload
{
  "file": "test.pdf",  # File với hash đã tồn tại
  "departmentId": 1,
  "fileTypeId": 1
}
```

**Response:**
```json
{
  "success": false,
  "message": "File này đã tồn tại trong hệ thống.",
  "data": null
}
```

## Lợi ích

1. ✅ **Thân thiện với người dùng**: Thông báo lỗi rõ ràng, dễ hiểu bằng tiếng Việt
2. ✅ **Validation sớm**: Phát hiện lỗi trước khi thao tác với database
3. ✅ **Bảo mật**: Không lộ thông tin SQL hoặc cấu trúc database
4. ✅ **Dễ maintain**: Xử lý lỗi tập trung tại một nơi
5. ✅ **Consistent**: Tất cả API đều trả về format lỗi giống nhau
6. ✅ **Logging**: Tự động log lỗi cho debugging

## Các File Đã Thay Đổi

1. ✅ `src/main/java/com/duongdat/filehub/exception/FileUploadException.java` (Mới)
2. ✅ `src/main/java/com/duongdat/filehub/exception/ValidationException.java` (Mới)
3. ✅ `src/main/java/com/duongdat/filehub/exception/GlobalExceptionHandler.java` (Mới)
4. ✅ `src/main/java/com/duongdat/filehub/service/FileService.java` (Cập nhật)
5. ✅ `src/main/java/com/duongdat/filehub/service/UserAuthorizationService.java` (Cập nhật)
6. ✅ `src/main/java/com/duongdat/filehub/dto/response/ApiResponse.java` (Cập nhật)

## Migration Notes

Không cần migration database. Các thay đổi chỉ ở tầng application logic.

## Backward Compatibility

✅ Hoàn toàn tương thích ngược. Tất cả API endpoints giữ nguyên contract, chỉ cải thiện error handling.
