#!/bin/bash

# 로그 파일 설정
mkdir -p /var/log
touch /var/log/cron.log

# 초기 데이터 로드 실행 (한 번 실행)
echo "초기 데이터 로드 시작..."
python get_cultural_events.py

# cron 서비스 시작
echo "Cron 서비스 시작..."
cron

# 로그 출력을 위한 tail (컨테이너 실행 유지)
echo "로그 출력 모니터링 시작..."
tail -f /var/log/cron.log
