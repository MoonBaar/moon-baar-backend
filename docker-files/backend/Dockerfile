# 빌드 스테이지
FROM amazoncorretto:17-alpine as build

WORKDIR /workspace/app

COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew bootJar
RUN mkdir -p build/libs

# 실행 스테이지
FROM amazoncorretto:17-alpine

VOLUME /tmp

COPY --from=build /workspace/app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
# ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=dev", "/app.jar"]
