# ---------- Build stage ----------
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

# Copy Maven Wrapper first (so we can use ./mvnw)
COPY .mvn/ .mvn/
COPY mvnw .
RUN chmod +x mvnw

# Copy pom only, warm the cache
COPY pom.xml .

# Cache Maven repository between layers (BuildKit cache mount)
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -Dmaven.repo.local=/root/.m2/repository -DskipTests dependency:go-offline

# Now copy sources and build
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -Dmaven.repo.local=/root/.m2/repository -DskipTests clean package

# ---------- Run stage ----------
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /workspace/target/pavement-api-0.0.1-SNAPSHOT.jar app.jar

ENV JAVA_OPTS=""
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
