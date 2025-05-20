FROM python:3.11-slim
WORKDIR /app

# 필요한 시스템 패키지 설치
RUN apt-get update && apt-get install -y bash cron mariadb-client

# Python 의존성 설치
COPY requirements.txt .
RUN pip install -r requirements.txt

# 데이터 수집 스크립트 복사
COPY get_cultural_events.py .

# 백업 스크립트 생성
COPY backup_database.sh .
RUN chmod +x backup_database.sh

# 크론 설정 파일 생성 및 등록
COPY crontab /etc/cron.d/app-cron
RUN chmod 0644 /etc/cron.d/app-cron
RUN crontab /etc/cron.d/app-cron

# 시작 스크립트 복사 및 실행 권한 부여
COPY entrypoint.sh .
RUN chmod +x entrypoint.sh

ENTRYPOINT ["./entrypoint.sh"]
