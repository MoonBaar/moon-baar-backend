#!/bin/bash

# ANSI 색상 코드
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m'

# SSH 디렉토리 및 파일 확인
echo "SSH 설정을 확인합니다..."

# .ssh 디렉토리가 없으면 생성
if [ ! -d ~/.ssh ]; then
    echo ".ssh 디렉토리를 생성합니다..."
    mkdir -p ~/.ssh
    chmod 700 ~/.ssh
fi

# id_ed25519 파일이 이미 존재하는지 확인
if [ -f ~/.ssh/id_ed25519 ]; then
    echo "${YELLOW}SSH 키가 이미 존재합니다.${NC}"
    echo "기존 키를 사용하시겠습니까? (y/n)"
    read -r response
    if [ "$response" = "y" ]; then
        echo "${GREEN}기존 SSH 키를 사용합니다.${NC}"
    else
        echo "새로운 키로 대체하시겠습니까? 이 작업은 되돌릴 수 없습니다. (y/n)"
        read -r replace
        if [ "$replace" = "y" ]; then
            echo "${RED}Warning: 필요한 경우 기존 키를 먼저 백업하는 것을 추천합니다.${NC}"
            echo "계속하시겠습니까? (y/n)"
            read -r confirm
            if [ "$confirm" = "y" ]; then
                echo "새로운 SSH 키를 생성합니다..."
                echo "이메일 주소를 입력해주세요:"
                read -r email
                rm ~/.ssh/id_ed25519*
                ssh-keygen -t ed25519 -b 4096 -C "$email"
            else
                echo "${RED}스크립트를 종료합니다.${NC}"
                exit 1
            fi
        else
            echo "${RED}스크립트를 종료합니다.${NC}"
            exit 1
        fi
    fi
else
    # SSH 키 생성
    echo "새로운 SSH 키를 생성합니다..."
    echo "이메일 주소를 입력해주세요:"
    read -r email
    ssh-keygen -t ed25519 -b 4096 -C "$email"
fi

# authorized_keys 설정
echo "authorized_keys 파일을 설정합니다..."
cat ~/.ssh/id_ed25519.pub >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys

echo "${GREEN}SSH 키 설정이 완료되었습니다!${NC}"
