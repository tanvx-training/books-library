#!/bin/bash

# ==========================================================
# LIBRARY MANAGEMENT SYSTEM - STOP SCRIPT
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

stop_services() {
    print_header "Dừng Services"
    
    cd "$PROJECT_ROOT"
    
    print_info "Dừng tất cả containers..."
    docker-compose down
    
    print_success "Tất cả containers đã được dừng"
}

cleanup_containers() {
    print_header "Dọn dẹp Containers"
    
    # Remove stopped containers
    print_info "Xóa containers đã dừng..."
    docker container prune -f
    
    print_success "Đã xóa containers đã dừng"
}

cleanup_images() {
    print_header "Dọn dẹp Images"
    
    # List library images
    library_images=$(docker images --filter "reference=library/*" -q)
    
    if [ -n "$library_images" ]; then
        print_info "Tìm thấy $(echo "$library_images" | wc -l) library images"
        echo -e "${YELLOW}Library images:${NC}"
        docker images --filter "reference=library/*"
        
        echo -e "\n${YELLOW}Bạn có muốn xóa các library images này không? (y/N)${NC}"
        read -r response
        if [[ "$response" =~ ^[Yy]$ ]]; then
            docker rmi $library_images
            print_success "Đã xóa library images"
        else
            print_info "Giữ lại library images"
        fi
    else
        print_info "Không tìm thấy library images để xóa"
    fi
    
    # Remove dangling images
    print_info "Xóa dangling images..."
    docker image prune -f
    
    print_success "Đã dọn dẹp images"
}

cleanup_volumes() {
    print_header "Dọn dẹp Volumes"
    
    cd "$PROJECT_ROOT"
    
    # List project volumes
    project_volumes=$(docker volume ls --filter "name=books_" -q)
    
    if [ -n "$project_volumes" ]; then
        print_warning "⚠️  CẢNH BÁO: Thao tác này sẽ XÓA TẤT CẢ DỮ LIỆU!"
        echo -e "${YELLOW}Volumes được tìm thấy:${NC}"
        docker volume ls --filter "name=books_"
        
        echo -e "\n${RED}Bạn có CHẮC CHẮN muốn xóa tất cả volumes (dữ liệu sẽ bị mất vĩnh viễn)? (y/N)${NC}"
        read -r response
        if [[ "$response" =~ ^[Yy]$ ]]; then
            echo -e "${RED}Xác nhận lần cuối - Nhập 'DELETE' để tiếp tục:${NC}"
            read -r confirm
            if [[ "$confirm" == "DELETE" ]]; then
                docker volume rm $project_volumes
                print_success "Đã xóa project volumes"
            else
                print_info "Hủy xóa volumes"
            fi
        else
            print_info "Giữ lại volumes"
        fi
    else
        print_info "Không tìm thấy project volumes để xóa"
    fi
    
    # Remove unused volumes
    print_info "Xóa unused volumes..."
    docker volume prune -f
    
    print_success "Đã dọn dẹp volumes"
}

cleanup_networks() {
    print_header "Dọn dẹp Networks"
    
    # Remove unused networks
    print_info "Xóa unused networks..."
    docker network prune -f
    
    print_success "Đã dọn dẹp networks"
}

show_cleanup_status() {
    print_header "Trạng thái sau khi dọn dẹp"
    
    echo -e "${BLUE}📊 Docker System Usage:${NC}"
    docker system df
    
    echo -e "\n${BLUE}🔍 Remaining Library Components:${NC}"
    
    # Check for remaining containers
    remaining_containers=$(docker ps -a --filter "name=library-" --format "table {{.Names}}\t{{.Status}}" | tail -n +2)
    if [ -n "$remaining_containers" ]; then
        echo -e "${YELLOW}Containers:${NC}"
        echo "$remaining_containers"
    else
        echo -e "${GREEN}No library containers remaining${NC}"
    fi
    
    # Check for remaining images
    remaining_images=$(docker images --filter "reference=library/*" --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}" | tail -n +2)
    if [ -n "$remaining_images" ]; then
        echo -e "${YELLOW}Images:${NC}"
        echo "$remaining_images"
    else
        echo -e "${GREEN}No library images remaining${NC}"
    fi
    
    # Check for remaining volumes
    remaining_volumes=$(docker volume ls --filter "name=books_" --format "table {{.Name}}\t{{.Driver}}" | tail -n +2)
    if [ -n "$remaining_volumes" ]; then
        echo -e "${YELLOW}Volumes:${NC}"
        echo "$remaining_volumes"
    else
        echo -e "${GREEN}No library volumes remaining${NC}"
    fi
}

show_help() {
    echo "Usage: $0 [OPTIONS]"
    echo
    echo "Dừng và dọn dẹp Library Management System"
    echo
    echo "Options:"
    echo "  --stop-only        Chỉ dừng services, không dọn dẹp"
    echo "  --full-cleanup     Dọn dẹp toàn bộ (containers, images, volumes, networks)"
    echo "  --cleanup-volumes  Dọn dẹp bao gồm cả volumes (XÓA DỮ LIỆU)"
    echo "  --help, -h         Hiển thị help"
    echo
    echo "Examples:"
    echo "  $0                 # Dừng services và dọn dẹp cơ bản"
    echo "  $0 --stop-only     # Chỉ dừng services"
    echo "  $0 --full-cleanup  # Dọn dẹp toàn bộ bao gồm dữ liệu"
}

# Main execution
main() {
    # Parse command line arguments
    STOP_ONLY=false
    FULL_CLEANUP=false
    CLEANUP_VOLUMES=false
    
    while [[ $# -gt 0 ]]; do
        case $1 in
            --stop-only)
                STOP_ONLY=true
                shift
                ;;
            --full-cleanup)
                FULL_CLEANUP=true
                CLEANUP_VOLUMES=true
                shift
                ;;
            --cleanup-volumes)
                CLEANUP_VOLUMES=true
                shift
                ;;
            --help|-h)
                show_help
                exit 0
                ;;
            *)
                print_error "Unknown option: $1"
                show_help
                exit 1
                ;;
        esac
    done
    
    print_header "🛑 LIBRARY MANAGEMENT SYSTEM CLEANUP"
    
    # Always stop services first
    stop_services
    
    if [ "$STOP_ONLY" = true ]; then
        print_success "🎉 Services đã được dừng thành công!"
        print_info "Để khởi động lại, chạy: ./scripts/deploy.sh"
        exit 0
    fi
    
    # Basic cleanup
    cleanup_containers
    cleanup_networks
    
    # Clean up images if requested
    if [ "$FULL_CLEANUP" = true ]; then
        cleanup_images
    fi
    
    # Clean up volumes if requested
    if [ "$CLEANUP_VOLUMES" = true ]; then
        cleanup_volumes
    fi
    
    show_cleanup_status
    
    print_success "🎉 Dọn dẹp hoàn thành!"
    
    if [ "$FULL_CLEANUP" = true ]; then
        print_info "Hệ thống đã được dọn dẹp hoàn toàn."
    else
        print_info "Để dọn dẹp toàn bộ (bao gồm dữ liệu), chạy: $0 --full-cleanup"
    fi
    
    print_info "Để khởi động lại hệ thống, chạy: ./scripts/deploy.sh"
}

# Run main function
main "$@"
