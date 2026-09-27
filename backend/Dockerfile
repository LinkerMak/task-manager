FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --chown=appuser:appgroup backend/build/libs/backend-0.0.1-SNAPSHOT.jar app.jar

USER appuser

ENTRYPOINT ["java", "-jar", "app.jar"]