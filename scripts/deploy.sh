#!/bin/bash

# ==========================================================
# LIBRARY MANAGEMENT SYSTEM - DEPLOY SCRIPT
# ==========================================================

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Functions
print_header() {
    echo -e "${BLUE}============================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}============================================${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

check_prerequisites() {
    print_header "Kiểm tra Điều kiện Cần thiết"
    
    # Check Docker
    if ! command -v docker &> /dev/null; then
        print_error "Docker chưa được cài đặt. Vui lòng cài đặt Docker trước."
        exit 1
    fi
    print_success "Docker đã được cài đặt: $(docker --version | cut -d' ' -f3)"
    
    # Check Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose chưa được cài đặt. Vui lòng cài đặt Docker Compose trước."
        exit 1
    fi
    print_success "Docker Compose đã được cài đặt: $(docker-compose --version | cut -d' ' -f4)"
    
    # Check if Docker daemon is running
    if ! docker info &> /dev/null; then
        print_error "Docker daemon không chạy. Vui lòng khởi động Docker."
        exit 1
    fi
    print_success "Docker daemon đang chạy"
    
    # Check Java and Maven for building
    if ! command -v java &> /dev/null; then
        print_warning "Java chưa được cài đặt. Sẽ sử dụng Docker để build."
    else
        print_success "Java đã được cài đặt: $(java -version 2>&1 | head -n 1)"
    fi
    
    if ! command -v mvn &> /dev/null; then
        print_warning "Maven chưa được cài đặt. Sẽ sử dụng Maven wrapper."
    else
        print_success "Maven đã được cài đặt: $(mvn -version | head -n 1)"
    fi
}

setup_environment() {
    print_header "Thiết lập Môi trường"
    
    cd "$PROJECT_ROOT"
    
    # Check if .env exists
    if [ ! -f ".env" ]; then
        if [ -f "document/env-template.txt" ]; then
            print_info "Copying .env template..."
            cp document/env-template.txt .env
            print_warning "⚠️  Vui lòng cập nhật file .env với các giá trị phù hợp!"
            print_info "Đặc biệt chú ý:"
            print_info "  - POSTGRES_PASSWORD"
            print_info "  - JWT_SECRET_KEY"
            print_info "  - MAIL_USERNAME và MAIL_PASSWORD (nếu cần gửi email)"
            read -p "Nhấn Enter để tiếp tục sau khi đã cập nhật .env..."
        else
            print_error "Không tìm thấy file .env template. Vui lòng tạo file .env theo hướng dẫn."
            exit 1
        fi
    else
        print_success "File .env đã tồn tại"
    fi
    
    # Load environment variables
    if [ -f ".env" ]; then
        export $(grep -v '^#' .env | xargs)
        print_success "Đã load biến môi trường từ .env"
    fi
}

build_services() {
    print_header "Build Microservices"
    
    cd "$PROJECT_ROOT"
    
    # List of services to build
    services=("eureka-server" "api-gateway" "member-service" "catalog-service" "loan-service" "notification-service" "dashboard-service")
    
    print_info "Building services với Maven..."
    
    # Build parent project first
    print_info "Building parent project..."
    if command -v mvn &> /dev/null; then
        mvn clean compile -DskipTests
    else
        ./mvnw clean compile -DskipTests
    fi
    
    # Build each service
    for service in "${services[@]}"; do
        print_info "Building $service..."
        cd "$service"
        if [ -f "mvnw" ]; then
            ./mvnw clean package -DskipTests
        elif command -v mvn &> /dev/null; then
            mvn clean package -DskipTests
        else
            print_error "Không thể build $service - không tìm thấy Maven"
            exit 1
        fi
        cd "$PROJECT_ROOT"
        print_success "✅ Built $service"
    done
    
    print_success "Hoàn thành build tất cả services"
}

start_infrastructure() {
    print_header "Khởi động Infrastructure Services"
    
    cd "$PROJECT_ROOT"
    
    print_info "Khởi động PostgreSQL, Redis, Keycloak, Zookeeper và Kafka..."
    
    # Start infrastructure services first
    docker-compose up -d postgres redis keycloak zookeeper kafka
    
    print_info "Đợi infrastructure services sẵn sàng..."
    
    # Wait for PostgreSQL
    print_info "Đợi PostgreSQL..."
    timeout=60
    while ! docker-compose exec -T postgres pg_isready -U "${POSTGRES_USER:-library_admin}" -d "${POSTGRES_DB:-library_db}" >/dev/null 2>&1; do
        sleep 2
        timeout=$((timeout-2))
        if [ $timeout -le 0 ]; then
            print_error "Timeout waiting for PostgreSQL"
            exit 1
        fi
    done
    print_success "PostgreSQL đã sẵn sàng"
    
    # Wait for Keycloak
    print_info "Đợi Keycloak (có thể mất vài phút)..."
    timeout=180
    while ! curl -f http://localhost:${KEYCLOAK_PORT:-9090}/health/ready >/dev/null 2>&1; do
        sleep 5
        timeout=$((timeout-5))
        if [ $timeout -le 0 ]; then
            print_error "Timeout waiting for Keycloak"
            exit 1
        fi
    done
    print_success "Keycloak đã sẵn sàng"
    
    # Wait for Kafka
    print_info "Đợi Kafka..."
    timeout=60
    while ! docker-compose exec -T kafka kafka-broker-api-versions --bootstrap-server localhost:19092 >/dev/null 2>&1; do
        sleep 3
        timeout=$((timeout-3))
        if [ $timeout -le 0 ]; then
            print_error "Timeout waiting for Kafka"
            exit 1
        fi
    done
    print_success "Kafka đã sẵn sàng"
    
    print_success "Tất cả infrastructure services đã sẵn sàng"
}

start_microservices() {
    print_header "Khởi động Microservices"
    
    cd "$PROJECT_ROOT"
    
    # Start Eureka Server first
    print_info "Khởi động Eureka Server..."
    docker-compose up -d eureka-server
    
    # Wait for Eureka
    print_info "Đợi Eureka Server..."
    timeout=90
    while ! curl -f http://localhost:${EUREKA_SERVER_PORT:-8761}/actuator/health >/dev/null 2>&1; do
        sleep 3
        timeout=$((timeout-3))
        if [ $timeout -le 0 ]; then
            print_error "Timeout waiting for Eureka Server"
            exit 1
        fi
    done
    print_success "Eureka Server đã sẵn sàng"
    
    # Start API Gateway
    print_info "Khởi động API Gateway..."
    docker-compose up -d api-gateway
    
    # Wait for API Gateway
    print_info "Đợi API Gateway..."
    timeout=120
    while ! curl -f http://localhost:${API_GATEWAY_PORT:-8888}/actuator/health >/dev/null 2>&1; do
        sleep 3
        timeout=$((timeout-3))
        if [ $timeout -le 0 ]; then
            print_error "Timeout waiting for API Gateway"
            exit 1
        fi
    done
    print_success "API Gateway đã sẵn sàng"
    
    # Start all other microservices
    print_info "Khởi động các microservices còn lại..."
    docker-compose up -d member-service catalog-service loan-service notification-service dashboard-service
    
    # Wait for all services
    services=("member-service:${MEMBER_SERVICE_PORT:-9993}" "catalog-service:${CATALOG_SERVICE_PORT:-9991}" "loan-service:${LOAN_SERVICE_PORT:-9995}" "notification-service:${NOTIFICATION_SERVICE_PORT:-9994}" "dashboard-service:${DASHBOARD_SERVICE_PORT:-9992}")
    
    for service in "${services[@]}"; do
        service_name=$(echo $service | cut -d: -f1)
        service_port=$(echo $service | cut -d: -f2)
        
        print_info "Đợi $service_name..."
        timeout=150
        while ! curl -f http://localhost:$service_port/actuator/health >/dev/null 2>&1; do
            sleep 5
            timeout=$((timeout-5))
            if [ $timeout -le 0 ]; then
                print_error "Timeout waiting for $service_name"
                exit 1
            fi
        done
        print_success "$service_name đã sẵn sàng"
    done
    
    print_success "Tất cả microservices đã khởi động thành công"
}

show_status() {
    print_header "Trạng thái Hệ thống"
    
    echo -e "${BLUE}📊 Service Status:${NC}"
    docker-compose ps
    
    echo -e "\n${BLUE}🌐 Access URLs:${NC}"
    echo -e "  🔍 Eureka Server:    http://localhost:${EUREKA_SERVER_PORT:-8761}"
    echo -e "  🚪 API Gateway:      http://localhost:${API_GATEWAY_PORT:-8888}"
    echo -e "  🔐 Keycloak Admin:   http://localhost:${KEYCLOAK_PORT:-9090}/admin"
    echo -e "  👥 Member Service:   http://localhost:${MEMBER_SERVICE_PORT:-9993}/actuator/health"
    echo -e "  📚 Catalog Service:  http://localhost:${CATALOG_SERVICE_PORT:-9991}/actuator/health"
    echo -e "  💳 Loan Service:     http://localhost:${LOAN_SERVICE_PORT:-9995}/actuator/health"
    echo -e "  📧 Notification Service: http://localhost:${NOTIFICATION_SERVICE_PORT:-9994}/actuator/health"
    echo -e "  📊 Dashboard Service: http://localhost:${DASHBOARD_SERVICE_PORT:-9992}/actuator/health"
    echo -e "  🗄️  PostgreSQL:       localhost:${POSTGRES_PORT:-5432}"
    echo -e "  ⚡ Redis:            localhost:${REDIS_EXTERNAL_PORT:-6379}"
    echo -e "  📨 Kafka:            localhost:${KAFKA_EXTERNAL_PORT:-9092}"
    
    echo -e "\n${BLUE}🔑 Default Credentials:${NC}"
    echo -e "  Keycloak Admin: ${KEYCLOAK_ADMIN:-admin} / ${KEYCLOAK_ADMIN_PASSWORD:-admin_password_2024}"
    echo -e "  PostgreSQL: ${POSTGRES_USER:-library_admin} / ${POSTGRES_PASSWORD:-library_secure_password_2024}"
}

cleanup_on_error() {
    print_error "Deploy thất bại. Dọn dẹp..."
    docker-compose down
    exit 1
}

# Main execution
main() {
    trap cleanup_on_error ERR
    
    print_header "🚀 LIBRARY MANAGEMENT SYSTEM DEPLOYMENT"
    
    # Parse command line arguments
    SKIP_BUILD=false
    SKIP_INFRA=false
    
    while [[ $# -gt 0 ]]; do
        case $1 in
            --skip-build)
                SKIP_BUILD=true
                shift
                ;;
            --skip-infra)
                SKIP_INFRA=true
                shift
                ;;
            --help|-h)
                echo "Usage: $0 [OPTIONS]"
                echo "Options:"
                echo "  --skip-build    Skip building services (use existing JAR files)"
                echo "  --skip-infra    Skip infrastructure setup (assume already running)"
                echo "  --help, -h      Show this help message"
                exit 0
                ;;
            *)
                print_error "Unknown option: $1"
                exit 1
                ;;
        esac
    done
    
    check_prerequisites
    setup_environment
    
    if [ "$SKIP_BUILD" = false ]; then
        build_services
    else
        print_info "Bỏ qua bước build (sử dụng JAR files hiện có)"
    fi
    
    if [ "$SKIP_INFRA" = false ]; then
        start_infrastructure
    else
        print_info "Bỏ qua khởi động infrastructure (giả sử đã chạy)"
    fi
    
    start_microservices
    show_status
    
    print_success "🎉 Deploy hoàn thành thành công!"
    print_info "Hệ thống đã sẵn sàng sử dụng."
    print_info "Để dừng hệ thống, chạy: docker-compose down"
    print_info "Để xem logs, chạy: docker-compose logs -f [service-name]"
}

# Run main function
main "$@"
