#!/bin/bash

# ==========================================================
# LIBRARY MANAGEMENT SYSTEM - STATUS CHECK SCRIPT
# ==========================================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Load environment variables if .env exists
if [ -f "$PROJECT_ROOT/.env" ]; then
    export $(grep -v '^#' "$PROJECT_ROOT/.env" | xargs)
fi

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

check_service_health() {
    local service_name=$1
    local port=$2
    local path=${3:-"/actuator/health"}
    
    if curl -s -f "http://localhost:$port$path" > /dev/null 2>&1; then
        print_success "$service_name (port $port) - HEALTHY"
        return 0
    else
        print_error "$service_name (port $port) - UNHEALTHY"
        return 1
    fi
}

check_container_status() {
    print_header "Container Status"
    
    cd "$PROJECT_ROOT"
    
    # Check if docker-compose.yml exists
    if [ ! -f "docker-compose.yml" ]; then
        print_error "docker-compose.yml not found!"
        exit 1
    fi
    
    # Get container status
    echo -e "${BLUE}📦 Container Status:${NC}"
    docker-compose ps
    
    echo -e "\n${BLUE}🔍 Detailed Status:${NC}"
    
    # List of containers to check
    containers=("library-postgres" "library-redis" "library-keycloak" "library-zookeeper" "library-kafka" "library-eureka-server" "library-api-gateway" "library-member-service" "library-catalog-service" "library-loan-service" "library-notification-service" "library-dashboard-service")
    
    for container in "${containers[@]}"; do
        if docker ps --format "table {{.Names}}" | grep -q "^$container$"; then
            status=$(docker inspect --format='{{.State.Status}}' "$container" 2>/dev/null || echo "not found")
            if [ "$status" = "running" ]; then
                print_success "$container - RUNNING"
            else
                print_warning "$container - $status"
            fi
        else
            print_error "$container - NOT FOUND"
        fi
    done
}

check_service_health_all() {
    print_header "Service Health Checks"
    
    echo -e "${BLUE}🏥 Health Check Results:${NC}"
    
    # Infrastructure services
    print_info "Infrastructure Services:"
    check_service_health "PostgreSQL" "${POSTGRES_PORT:-5432}" ""
    if command -v pg_isready &> /dev/null; then
        if pg_isready -h localhost -p "${POSTGRES_PORT:-5432}" -U "${POSTGRES_USER:-library_admin}" > /dev/null 2>&1; then
            print_success "PostgreSQL - Connection OK"
        else
            print_error "PostgreSQL - Connection FAILED"
        fi
    fi
    
    check_service_health "Redis" "${REDIS_EXTERNAL_PORT:-6379}" ""
    if command -v redis-cli &> /dev/null; then
        if redis-cli -h localhost -p "${REDIS_EXTERNAL_PORT:-6379}" ping > /dev/null 2>&1; then
            print_success "Redis - Connection OK"
        else
            print_error "Redis - Connection FAILED"
        fi
    fi
    
    check_service_health "Keycloak" "${KEYCLOAK_PORT:-9090}" "/health/ready"
    
    echo ""
    print_info "Microservices:"
    check_service_health "Eureka Server" "${EUREKA_SERVER_PORT:-8761}"
    check_service_health "API Gateway" "${API_GATEWAY_PORT:-8888}"
    check_service_health "Member Service" "${MEMBER_SERVICE_PORT:-9993}"
    check_service_health "Catalog Service" "${CATALOG_SERVICE_PORT:-9991}"
    check_service_health "Loan Service" "${LOAN_SERVICE_PORT:-9995}"
    check_service_health "Notification Service" "${NOTIFICATION_SERVICE_PORT:-9994}"
    check_service_health "Dashboard Service" "${DASHBOARD_SERVICE_PORT:-9992}"
}

check_resources() {
    print_header "Resource Usage"
    
    echo -e "${BLUE}💻 Docker Resource Usage:${NC}"
    docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.MemPerc}}\t{{.NetIO}}\t{{.BlockIO}}"
    
    echo -e "\n${BLUE}💾 Docker System Info:${NC}"
    docker system df
    
    echo -e "\n${BLUE}📊 System Resources:${NC}"
    if command -v free &> /dev/null; then
        echo "Memory:"
        free -h
    fi
    
    if command -v df &> /dev/null; then
        echo -e "\nDisk:"
        df -h
    fi
}

show_access_urls() {
    print_header "Access URLs"
    
    echo -e "${BLUE}🌐 Service URLs:${NC}"
    echo -e "  🔍 Eureka Server:    http://localhost:${EUREKA_SERVER_PORT:-8761}"
    echo -e "  🚪 API Gateway:      http://localhost:${API_GATEWAY_PORT:-8888}"
    echo -e "  🔐 Keycloak Admin:   http://localhost:${KEYCLOAK_PORT:-9090}/admin"
    echo -e "  👥 Member Service:   http://localhost:${MEMBER_SERVICE_PORT:-9993}/actuator/health"
    echo -e "  📚 Catalog Service:  http://localhost:${CATALOG_SERVICE_PORT:-9991}/actuator/health"
    echo -e "  💳 Loan Service:     http://localhost:${LOAN_SERVICE_PORT:-9995}/actuator/health"
    echo -e "  📧 Notification Service: http://localhost:${NOTIFICATION_SERVICE_PORT:-9994}/actuator/health"
    echo -e "  📊 Dashboard Service: http://localhost:${DASHBOARD_SERVICE_PORT:-9992}/actuator/health"
    
    echo -e "\n${BLUE}🔗 Database & Infrastructure:${NC}"
    echo -e "  🗄️  PostgreSQL:       localhost:${POSTGRES_PORT:-5432}"
    echo -e "  ⚡ Redis:            localhost:${REDIS_EXTERNAL_PORT:-6379}"
    echo -e "  📨 Kafka:            localhost:${KAFKA_EXTERNAL_PORT:-9092}"
    echo -e "  🏠 Zookeeper:        localhost:${ZOOKEEPER_PORT:-2181}"
    
    echo -e "\n${BLUE}🔑 Default Credentials:${NC}"
    echo -e "  Keycloak Admin: ${KEYCLOAK_ADMIN:-admin} / ${KEYCLOAK_ADMIN_PASSWORD:-admin_password_2024}"
    echo -e "  PostgreSQL: ${POSTGRES_USER:-library_admin} / [password from .env]"
}

check_logs() {
    print_header "Recent Logs"
    
    echo -e "${BLUE}📝 Recent Error Logs (last 10 lines per service):${NC}"
    
    cd "$PROJECT_ROOT"
    
    services=("eureka-server" "api-gateway" "member-service" "catalog-service" "loan-service" "notification-service" "dashboard-service")
    
    for service in "${services[@]}"; do
        echo -e "\n${YELLOW}--- $service ---${NC}"
        docker-compose logs --tail=5 "$service" 2>/dev/null | grep -i error || echo "No recent errors"
    done
}

check_eureka_registry() {
    print_header "Service Registry"
    
    echo -e "${BLUE}🏢 Eureka Service Registry:${NC}"
    
    # Try to get services from Eureka
    if curl -s "http://localhost:${EUREKA_SERVER_PORT:-8761}/eureka/apps" > /dev/null 2>&1; then
        echo -e "${GREEN}Eureka Server is accessible${NC}"
        
        # Get registered services
        registered_services=$(curl -s -H "Accept: application/json" "http://localhost:${EUREKA_SERVER_PORT:-8761}/eureka/apps" | jq -r '.applications.application[]?.name // empty' 2>/dev/null || echo "")
        
        if [ -n "$registered_services" ]; then
            echo -e "${BLUE}Registered Services:${NC}"
            echo "$registered_services" | while read -r service; do
                if [ -n "$service" ]; then
                    print_success "$service"
                fi
            done
        else
            print_warning "No services registered with Eureka yet"
        fi
    else
        print_error "Cannot connect to Eureka Server"
    fi
}

show_help() {
    echo "Usage: $0 [OPTIONS]"
    echo
    echo "Check status of Library Management System"
    echo
    echo "Options:"
    echo "  --containers       Show only container status"
    echo "  --health          Show only health checks"
    echo "  --resources       Show only resource usage"
    echo "  --urls            Show only access URLs"
    echo "  --logs            Show recent error logs"
    echo "  --eureka          Show Eureka registry status"
    echo "  --watch           Continuous monitoring (refresh every 30s)"
    echo "  --help, -h        Show this help"
    echo
    echo "Examples:"
    echo "  $0                # Full status check"
    echo "  $0 --health       # Only health checks"
    echo "  $0 --watch        # Continuous monitoring"
}

watch_mode() {
    while true; do
        clear
        print_header "🔄 CONTINUOUS MONITORING - $(date)"
        check_container_status
        check_service_health_all
        check_resources
        
        echo -e "\n${BLUE}Refreshing in 30 seconds... (Ctrl+C to exit)${NC}"
        sleep 30
    done
}

# Main execution
main() {
    print_header "📊 LIBRARY MANAGEMENT SYSTEM STATUS"
    
    # Parse command line arguments
    case "${1:-}" in
        --containers)
            check_container_status
            ;;
        --health)
            check_service_health_all
            ;;
        --resources)
            check_resources
            ;;
        --urls)
            show_access_urls
            ;;
        --logs)
            check_logs
            ;;
        --eureka)
            check_eureka_registry
            ;;
        --watch)
            watch_mode
            ;;
        --help|-h)
            show_help
            exit 0
            ;;
        "")
            # Full status check
            check_container_status
            echo ""
            check_service_health_all
            echo ""
            check_eureka_registry
            echo ""
            show_access_urls
            echo ""
            check_resources
            ;;
        *)
            print_error "Unknown option: $1"
            show_help
            exit 1
            ;;
    esac
    
    echo -e "\n${GREEN}Status check completed! 🎉${NC}"
}

# Run main function
main "$@"
