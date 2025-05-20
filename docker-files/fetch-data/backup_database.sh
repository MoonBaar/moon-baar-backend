#!/bin/bash

# 백업 설정
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_DIR="/backups"
BACKUP_FILE="$BACKUP_DIR/moonbaar_db_$TIMESTAMP.sql"

# 백업 디렉토리 생성
mkdir -p $BACKUP_DIR

# 데이터베이스 백업
echo "시작: MySQL 데이터베이스 백업 - $TIMESTAMP"
mysqldump -h $DB_HOST -u $DB_USER -p$DB_PASSWORD $DB_NAME > $BACKUP_FILE

# 압축
gzip $BACKUP_FILE
echo "완료: 백업 파일 생성됨 - ${BACKUP_FILE}.gz"

# 오래된 백업 파일 삭제 (30일 이상 된 파일)
find $BACKUP_DIR -name "moonbaar_db_*.sql.gz" -type f -mtime +30 -delete
echo "오래된 백업 파일 정리 완료"
