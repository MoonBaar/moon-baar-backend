#!/bin/bash

# ANSI 색상 코드
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

# 사용법 체크
if [ -z "$1" ]; then
   echo "${RED}Usage: ./set-github-env-secrets.sh <environment_name>${NC}"
   echo "${YELLOW}Example: ./set-github-env-secrets.sh development${NC}"
   exit 1
fi

ENVIRONMENT=$1

# Environment secrets 설정
echo "⚙️ ${GREEN}GitHub ${ENVIRONMENT} environment secrets 설정을 시작합니다...${NC}"

# .env 파일이 존재하는지 확인
if [ ! -f .env ]; then
   echo "${RED}Error: .env 파일을 찾을 수 없습니다.${NC}"
   exit 1
fi

# .env 파일을 읽어서 secrets 설정
while IFS='=' read -r key value
do
   if [ ! -z "$key" ] && [ ! -z "$value" ]; then
       gh secret set "$key" -b"$value" --env $ENVIRONMENT || exit 1
   fi
done < .env

# SSH key 설정 (선택적)
if [ -f ~/.ssh/id_ed25519 ]; then
   gh secret set SSH_PRIVATE_KEY -b"$(cat ~/.ssh/id_ed25519)" --env $ENVIRONMENT || exit 1
fi

echo "✅ ${GREEN}GitHub ${ENVIRONMENT} environment secrets 설정이 완료되었습니다!${NC}"
