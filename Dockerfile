FROM --platform=$BUILDPLATFORM eclipse-temurin:17-jdk as builder

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN ./mvnw dependency:go-offline -B -Dmaven.artifact.threads=30

COPY src src
RUN chmod u+x ./mvnw && ./mvnw package -DskipTests -Dspring.profiles.active=production && mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM --platform=$BUILDPLATFORM eclipse-temurin:17-jre

ARG DEPENDENCY=/app/target/dependency

COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app

ENV TZ="Asia/Ho_Chi_Minh"
ENV SPRING_PROFILES_ACTIVE=production

ENTRYPOINT ["java","-cp","app:app/lib/*", "com.langthang.LangThangApplication"]
