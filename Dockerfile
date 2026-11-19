FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Datadog APM 에이전트. 버전을 올릴 때는 Maven Central의 jar로 sha256을 다시 계산해 함께 바꾼다
ARG DD_JAVA_AGENT_VERSION=1.67.1
ARG DD_JAVA_AGENT_SHA256=beb620dc2e4783fd328a1ae23f8fd4fcb757ac23e5bb05efb04c6aa3e51a5e43
RUN wget -qO /dd-java-agent.jar \
      "https://repo1.maven.org/maven2/com/datadoghq/dd-java-agent/${DD_JAVA_AGENT_VERSION}/dd-java-agent-${DD_JAVA_AGENT_VERSION}.jar" \
    && echo "${DD_JAVA_AGENT_SHA256}  /dd-java-agent.jar" | sha256sum -c -

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src
RUN chmod +x gradlew && ./gradlew build -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /dd-java-agent.jar dd-java-agent.jar
COPY --from=builder /app/build/libs/*.jar app.jar

# Datadog 통합 서비스 태깅용 버전. CI에서 커밋 SHA를 넣는다
ARG DD_VERSION=dev
ENV DD_VERSION=${DD_VERSION}

EXPOSE 8080
# 에이전트는 JAVA_TOOL_OPTIONS로 붙인다(docker-compose.yml). 로컬에서 그냥 띄우면 에이전트 없이 실행된다
ENTRYPOINT ["java", "-jar", "app.jar"]
