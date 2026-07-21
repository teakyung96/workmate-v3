# =============================================================
# workmate-was 컨테이너 이미지 (멀티스테이지 빌드)
# 빌드 컨텍스트는 저장소 루트 — 모노레포 settings.gradle 이 web/was 를 함께 include 하므로
# 루트에서 Gradle Wrapper 로 :workmate-was 만 골라 부트 JAR 를 만든다.
# =============================================================

# ---- 1) 빌드 스테이지: JDK + Gradle Wrapper 로 bootJar 생성 ----
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app
COPY . .
# 테스트는 실제 DB(pgvector) 가 필요하므로 이미지 빌드 시에는 제외한다 (기동은 compose 로 검증)
RUN chmod +x gradlew && ./gradlew :workmate-was:bootJar -x test --no-daemon

# ---- 2) 실행 스테이지: JRE 로 슬림하게 ----
FROM eclipse-temurin:17-jre
WORKDIR /app
# *-SNAPSHOT.jar 패턴은 부트 JAR 만 매칭한다 ( -plain.jar 는 제외됨 )
COPY --from=builder /app/workmate-was/build/libs/*-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
