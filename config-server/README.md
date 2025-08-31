# Config Server

Config Server là thành phần trung tâm quản lý cấu hình cho tất cả các microservice trong hệ thống Library Management.

## Tính năng

- **Quản lý cấu hình tập trung**: Tất cả cấu hình của các microservice được lưu trữ tại một nơi
- **Hỗ trợ nhiều profile**: Development, production, docker, etc.
- **Tích hợp Eureka**: Đăng ký với service discovery
- **Health monitoring**: Endpoint actuator để kiểm tra trạng thái

## Cấu trúc thư mục

```
config-server/
├── src/main/resources/config/          # Cấu hình cho các service
│   ├── api-gateway.properties          # Cấu hình API Gateway
│   ├── catalog-service.properties      # Cấu hình Catalog Service
│   ├── member-service.properties       # Cấu hình Member Service
│   ├── loan-service.properties         # Cấu hình Loan Service
│   ├── notification-service.properties # Cấu hình Notification Service
│   └── dashboard-service.properties    # Cấu hình Dashboard Service
├── Dockerfile                          # Docker configuration
└── pom.xml                            # Maven dependencies
```

## Cấu hình

### Application Properties

- **Port**: 8889
- **Eureka**: Đăng ký với eureka-server:8761
- **Config location**: `classpath:/config` và `file:./config-repo/`

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `EUREKA_HOST` | Eureka server host | localhost |
| `SPRING_PROFILES_ACTIVE` | Active profile | native |

## Triển khai

### 1. Build và chạy local

```bash
# Build project
./mvnw clean package

# Chạy application
java -jar target/config-server-0.0.1-SNAPSHOT.jar
```

### 2. Triển khai với Docker

```bash
# Sử dụng script tự động
./scripts/deploy-config-server.sh

# Hoặc thủ công
docker-compose up -d --build config-server
```

### 3. Kiểm tra triển khai

```bash
# Kiểm tra health
curl http://localhost:8889/actuator/health

# Kiểm tra cấu hình service
curl http://localhost:8889/api-gateway/default

# Sử dụng script kiểm tra
./scripts/test-config-server.sh
```

## API Endpoints

### Health Check
```
GET /actuator/health
```

### Lấy cấu hình service
```
GET /{service-name}/{profile}
GET /{service-name}/{profile}/{label}
```

Ví dụ:
- `GET /api-gateway/default` - Cấu hình API Gateway với profile default
- `GET /catalog-service/docker` - Cấu hình Catalog Service với profile docker

## Monitoring

Config Server expose các endpoint actuator:

- `/actuator/health` - Trạng thái sức khỏe
- `/actuator/info` - Thông tin ứng dụng

## Troubleshooting

### 1. Service không lấy được cấu hình

Kiểm tra:
- Config Server đã khởi động chưa
- Network connectivity giữa service và config server
- Cấu hình `spring.config.import` trong service

### 2. Eureka registration failed

Kiểm tra:
- Eureka Server đã chạy chưa
- Biến môi trường `EUREKA_HOST` đúng chưa
- Network connectivity

### 3. Config file không tìm thấy

Kiểm tra:
- File cấu hình có tồn tại trong `src/main/resources/config/`
- Tên file khớp với service name
- Profile được chỉ định đúng

## Dependencies

- Spring Boot 3.x
- Spring Cloud Config Server
- Spring Cloud Netflix Eureka Client
- Spring Boot Actuator

## Logs

Xem logs của Config Server:

```bash
# Docker logs
docker-compose logs -f config-server

# Local logs
tail -f logs/config-server.log
```