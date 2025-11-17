# ================================
# Stage 1: Build Frontend (Angular)
# ================================
FROM node:20-alpine AS frontend-build

WORKDIR /app/frontend

# Copy package files
COPY fe-esimedia/package*.json ./

# Install dependencies
RUN npm ci --legacy-peer-deps

# Copy frontend source
COPY fe-esimedia/ ./

# Build Angular app for production
RUN npm run build -- --configuration=production

# ================================
# Stage 2: Build Backend (Maven)
# ================================
FROM maven:3.9-eclipse-temurin-21-alpine AS backend-build

WORKDIR /app/backend

# Copy pom.xml first for caching
COPY be-esimedia/pom.xml ./
COPY be-esimedia/mvnw ./
COPY be-esimedia/mvnw.cmd ./
COPY be-esimedia/.mvn ./.mvn

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy backend source
COPY be-esimedia/src ./src

# Build backend (skip tests for faster builds)
RUN mvn clean package -DskipTests -B

# ================================
# Stage 3: Runtime
# ================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create directory for static files
RUN mkdir -p /app/public

# Copy built JAR from backend-build stage
COPY --from=backend-build /app/backend/target/*.jar app.jar

# Copy frontend build to /app/public
COPY --from=frontend-build /app/frontend/dist/fe-esimedia/browser /app/public

# Create non-root user and set permissions
RUN addgroup -S spring && adduser -S spring -G spring && \
    chown -R spring:spring /app

USER spring:spring

# Expose port (Render uses PORT environment variable)
EXPOSE 8081

# Variables de entorno por defecto
ENV PORT=8081

# Run the application - bind to all interfaces and use PORT variable
ENTRYPOINT ["sh", "-c", "java -Xms256m -Xmx1024m -Dserver.port=${PORT:-8081} -Dserver.address=0.0.0.0 -jar app.jar"]
