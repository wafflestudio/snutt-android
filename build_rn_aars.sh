#!/bin/bash

set -e

# 색상 및 로그 함수
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 기본 설정
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR"

BUILDER_PROJECT_NAME="AARBuilder"
RN_VERSION="0.72.3"
TARGET_LIBS_DIR="$PROJECT_ROOT/libs"
WORK_DIR="$PROJECT_ROOT/aar_builder_temp"

# React Native 0.72.3 호환 라이브러리 정의
LIBRARY_NAMES=(
    "@react-native-async-storage/async-storage"
    "react-native-gesture-handler"
    "react-native-reanimated"
    "react-native-safe-area-context"
    "react-native-screens"
    "react-native-svg"
    "@react-native-picker/picker"
)

LIBRARY_VERSIONS=(
    "1.19.8"    # @react-native-async-storage/async-storage
    "2.12.0"    # react-native-gesture-handler
    "3.4.2"     # react-native-reanimated
    "4.7.1"     # react-native-safe-area-context
    "3.23.0"    # react-native-screens
    "13.11.0"   # react-native-svg
    "2.4.10"    # @react-native-picker/picker
)

# 빌드할 라이브러리 목록
LIBRARIES_TO_BUILD=(
    "@react-native-async-storage/async-storage"
    "react-native-gesture-handler"
    "react-native-reanimated"
    "react-native-safe-area-context"
    "react-native-screens"
    "react-native-svg"
    "@react-native-picker/picker"
)

# AAR 파일명 매핑
AAR_MAPPINGS=(
    "react-native-async-storage_async-storage"
    "react-native-gesture-handler"
    "react-native-reanimated"
    "react-native-safe-area-context"
    "react-native-screens"
    "react-native-svg"
    "react-native-picker_picker"
)

# 작업 공간 준비
prepare_workspace() {
    log_info "📁 작업 공간 준비 중..."
    
    # 기존 작업 디렉토리 삭제
    if [ -d "$WORK_DIR" ]; then
        log_warning "기존 작업 디렉토리를 삭제합니다: $WORK_DIR"
        rm -rf "$WORK_DIR"
    fi
    
    # 새 작업 디렉토리 및 libs 디렉토리 생성
    mkdir -p "$WORK_DIR"
    mkdir -p "$TARGET_LIBS_DIR"
    
    log_success "작업 공간 준비 완료"
}

# React Native 프로젝트 생성
create_rn_project() {
    log_info "🚀 React Native 프로젝트 생성 중..."
    
    cd "$WORK_DIR"
    
    # React Native 프로젝트 생성
    if npx react-native@"$RN_VERSION" init "$BUILDER_PROJECT_NAME" --version "$RN_VERSION" --skip-install; then
        log_success "React Native 프로젝트 생성 완료"
    else
        log_error "React Native 프로젝트 생성 실패"
        exit 1
    fi
    
    cd "$BUILDER_PROJECT_NAME"
    
    # 기본 의존성 설치
    log_info "📦 기본 의존성 설치 중..."
    if npm install; then
        log_success "기본 의존성 설치 완료"
    else
        log_error "기본 의존성 설치 실패"
        exit 1
    fi

    # Hermes 설정 추가
    log_info "🔧 Hermes 설정 추가 중..."
    local app_build_gradle="android/app/build.gradle"
    if [ -f "$app_build_gradle" ]; then
        echo "" >> "$app_build_gradle"
        echo "android.packagingOptions.jniLibs.useLegacyPackaging = true" >> "$app_build_gradle"
        log_success "Hermes 설정 추가 완료"
    else
        log_error "app/build.gradle 파일을 찾을 수 없습니다."
    fi
}

# 라이브러리 빌드
build_library() {
    local package_name="$1"
    local version="$2"
    local aar_name="$3"
    
    log_info "🔨 빌드 중: $package_name@$version"
    
    cd "$WORK_DIR/$BUILDER_PROJECT_NAME"
    
    # 라이브러리 설치 부분을 제거
    
    # Android 빌드
    log_info "  🏗️ Android AAR 빌드 중..."
    cd android
    
    # Gradle 빌드 명령어 결정
    local gradle_task=":${aar_name}:assembleRelease"
    
    if ./gradlew "$gradle_task"; then
        log_success "  ✅ Android 빌드 완료"
    else
        log_error "  ❌ Android 빌드 실패"

        # 필요한 네이티브 라이브러리(.so 파일 등)가 누락된 경우 전체 프로젝트 빌드로 해결을 시도
        log_warning "  🤔 기본 빌드 실패. 프로젝트 전체 빌드를 시도합니다..."
        if ./gradlew assembleRelease; then
            log_success "  ✅ 프로젝트 전체 빌드 성공"
        else
            log_error "  ❌ 프로젝트 전체 빌드도 실패했습니다."
            return 1
        fi
    fi
    
    # AAR 파일 복사
    local aar_source="../node_modules/$package_name/android/build/outputs/aar/${aar_name}-release.aar"
    
    # @로 시작하는 패키지명 처리
    if [[ "$package_name" == @* ]]; then
        local package_path=$(echo "$package_name" | sed 's/@//g' | tr '/' '/')
        aar_source="../node_modules/@$package_path/android/build/outputs/aar/${aar_name}-release.aar"
    fi
    
    if [ -f "$aar_source" ]; then
        cp "$aar_source" "$TARGET_LIBS_DIR/"
        log_success "  ✅ AAR 파일 복사 완료: ${aar_name}-release.aar"
    else
        log_error "  ❌ AAR 파일을 찾을 수 없습니다: $aar_source"
        return 1
    fi
    
    cd ..
}

# 정리 작업
cleanup() {
    log_info "🧹 정리 작업 중..."
    
    if [ -d "$WORK_DIR" ]; then
        rm -rf "$WORK_DIR"
        log_success "임시 작업 디렉토리 삭제 완료"
    fi
}

# 메인 실행
main() {
    trap cleanup EXIT

    log_info "🎯 React Native AAR 빌드 시작"
    log_info "React Native 버전: $RN_VERSION"
    log_info "빌드할 라이브러리 수: ${#LIBRARY_NAMES[@]}"
    
    # 작업 공간 준비
    prepare_workspace
    
    # React Native 프로젝트 생성
    create_rn_project
    
    # 모든 라이브러리 한번에 설치
    cd "$WORK_DIR/$BUILDER_PROJECT_NAME"
    local install_command="npm install"
    for i in "${!LIBRARY_NAMES[@]}"; do
        install_command+=" ${LIBRARY_NAMES[$i]}@${LIBRARY_VERSIONS[$i]}"
    done

    log_info "📦 모든 라이브러리 설치 중..."
    if eval "$install_command"; then
        log_success "✅ 모든 라이브러리 설치 완료"
    else
        log_error "❌ 라이브러리 설치 실패"
        exit 1
    fi
    
    # 라이브러리 빌드
    local success_count=0
    local failed_count=0
    local build_index=0
    local total_count=${#LIBRARIES_TO_BUILD[@]}

    for lib_to_build in "${LIBRARIES_TO_BUILD[@]}"; do
        ((build_index++))
        local found=false
        for i in "${!LIBRARY_NAMES[@]}"; do
            if [[ "${LIBRARY_NAMES[$i]}" == "$lib_to_build" ]]; then
                local package_name="${LIBRARY_NAMES[$i]}"
                local version="${LIBRARY_VERSIONS[$i]}"
                local aar_name="${AAR_MAPPINGS[$i]}"

                log_info "📋 라이브러리 빌드: $build_index/$total_count"
                
                if build_library "$package_name" "$version" "$aar_name"; then
                    ((success_count++))
                    log_success "✅ $package_name 빌드 성공"
                else
                    ((failed_count++))
                    log_error "❌ $package_name 빌드 실패"
                fi
                
                echo "----------------------------------------"
                found=true
                break
            fi
        done

        if [[ "$found" == false ]]; then
            log_warning "라이브러리를 찾을 수 없습니다: $lib_to_build"
            ((failed_count++))
        fi
    done
    
    # 결과 보고
    log_info "📊 빌드 결과:"
    log_info "  성공: $success_count"
    log_info "  실패: $failed_count"
    
    if [ "$failed_count" -eq 0 ]; then
        log_success "🎉 모든 실패한 라이브러리 빌드 완료!"
    else
        log_warning "⚠️ 일부 라이브러리 빌드 실패"
    fi
    
    # 생성된 AAR 파일 목록
    log_info "📦 생성된 AAR 파일들:"
    ls -la "$TARGET_LIBS_DIR"/*.aar 2>/dev/null || log_warning "생성된 AAR 파일이 없습니다."
    
    log_success "🏁 AAR 빌드 프로세스 완료"
}

# 스크립트 실행
main "$@" 