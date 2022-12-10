FROM eclipse-temurin:11-jdk-alpine as builder

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN ./mvnw dependency:go-offline -B -Dmaven.artifact.threads=30

COPY src src
RUN chmod u+x ./mvnw && ./mvnw package -DskipTests && mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:11-jre-alpine

ARG DEPENDENCY=/app/target/dependency

COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app

ENV TZ="Asia/Ho_Chi_Minh"
ENTRYPOINT ["java","-cp","app:app/lib/*", "com.langthang.LangThangApplication"]