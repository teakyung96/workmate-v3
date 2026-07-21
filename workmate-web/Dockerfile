# =============================================================
# workmate-web 컨테이너 이미지 (멀티스테이지 빌드)
# workmate-vue 의 UMD 산출물이 workmate-web/src/main/resources/static/common-ui 에
# 커밋돼 있으므로 이미지 빌드에 Node.js 가 필요 없다 (Gradle 빌드만으로 충분).
# =============================================================

# ---- 1) 빌드 스테이지 ----
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew :workmate-web:bootJar -x test --no-daemon

# ---- 2) 실행 스테이지 ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/workmate-web/build/libs/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
