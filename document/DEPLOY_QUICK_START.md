# 🚀 Quick Start - Deploy Hệ thống Quản lý Thư viện

## ⚡ Deploy nhanh (5 phút)

### 1. Chuẩn bị môi trường
```bash
# Clone repository
git clone <repository-url>
cd books

# Tạo file .env từ template
make env-setup

# Cập nhật các giá trị quan trọng trong .env
nano .env
```

### 2. Deploy hệ thống
```bash
# Deploy toàn bộ (build + start)
make deploy

# Hoặc sử dụng script trực tiếp
./scripts/deploy.sh
```

### 3. Kiểm tra trạng thái
```bash
# Xem trạng thái hệ thống
make status

# Xem health checks
make health

# Xem URLs truy cập
make urls
```

## 🔧 Các lệnh thường dùng

```bash
# Xem tất cả lệnh có sẵn
make help

# Khởi động/dừng
make start          # Khởi động tất cả
make stop           # Dừng tất cả
make restart        # Khởi động lại

# Monitoring
make status         # Kiểm tra trạng thái
make status-watch   # Theo dõi liên tục
make logs           # Xem logs
make logs-follow    # Theo dõi logs realtime

# Cleanup
make clean          # Dọn dẹp cơ bản
make deep-clean     # Dọn dẹp toàn bộ (XÓA DỮ LIỆU)
```

## 🌐 Access URLs

| Service | URL | Mô tả |
|---------|-----|-------|
| **API Gateway** | http://localhost:8888 | Điểm vào chính |
| **Eureka Dashboard** | http://localhost:8761 | Service registry |
| **Keycloak Admin** | http://localhost:9090/admin | Identity management |

## 🆘 Troubleshooting nhanh

### Container không khởi động được:
```bash
make logs-[service-name]  # Xem logs cụ thể
make status-containers    # Kiểm tra container status
```

### Port bị conflict:
```bash
# Thay đổi port trong .env
API_GATEWAY_PORT=8889
```

### Memory không đủ:
```bash
# Xem resource usage
make status-resources

# Tăng memory cho Docker Desktop hoặc giảm replica
```

### Reset toàn bộ:
```bash
make deep-clean  # XÓA TẤT CẢ DỮ LIỆU
make deploy      # Deploy lại từ đầu
```

## 📚 Tài liệu chi tiết

- **[DEPLOYMENT.md](DEPLOYMENT.md)** - Hướng dẫn deploy chi tiết
- **[README.md](../README.md)** - Tài liệu đặc tả hệ thống
- **[docker-compose.yml](../docker-compose.yml)** - Cấu hình container
- **[scripts/](scripts/)** - Scripts tiện ích

## 🔑 Credentials mặc định

**Keycloak Admin:**
- Username: `admin`  
- Password: `admin_password_2024`

**PostgreSQL:**
- Username: `library_admin`
- Password: (xem trong .env)

---

💡 **Tip:** Sử dụng `make help` để xem tất cả lệnh có sẵn!
