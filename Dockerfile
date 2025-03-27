FROM eclipse-temurin:17-jdk as build
WORKDIR /workspace/app

COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./

RUN ./gradlew dependencies

COPY src src

RUN ./gradlew build -x test
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/coffee-shop-backend-0.0.1-SNAPSHOT.jar)
RUN jar -tf build/libs/coffee-shop-backend-0.0.1-SNAPSHOT.jar

FROM eclipse-temurin:17-jre
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

RUN addgroup --system --gid 1001 appgroup && \
    adduser --system --uid 1001 --gid 1001 appuser
USER appuser

ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.coffeeshop.CoffeeShopApplication"]