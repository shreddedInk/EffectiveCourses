FROM eclipse-temurin:17-jdk as build
WORKDIR /workspace/app

# Copy gradle files
COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./

# Download dependencies
RUN ./gradlew dependencies

# Copy source code
COPY src src

# Build the application
RUN ./gradlew build -x test
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

FROM eclipse-temurin:17-jre
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Run as non-root user for security
RUN addgroup --system --gid 1001 appgroup && \\
    adduser --system --uid 1001 --gid 1001 appuser
USER appuser

ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.coffeeshop.CoffeeShopApplication"]
