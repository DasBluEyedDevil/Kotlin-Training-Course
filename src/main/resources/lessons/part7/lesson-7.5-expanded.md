# Lesson 7.5: CI/CD and DevOps

**Estimated Time**: 85 minutes

---

## Introduction

"It works on my machine" is no longer acceptable in professional software development.

**CI/CD (Continuous Integration/Continuous Deployment)** is the practice of:
- Automatically building and testing code on every commit
- Deploying to production multiple times per day
- Catching bugs before they reach users
- Shipping features faster with confidence

In this lesson, you'll master:
- ✅ GitHub Actions for CI/CD
- ✅ Automated testing pipelines
- ✅ Build automation with Gradle
- ✅ Code quality tools (ktlint, detekt)
- ✅ Docker for backend apps
- ✅ Publishing Android apps

---

## Why CI/CD Matters

### The Manual Deployment Nightmare

**Without CI/CD**:
```
Developer writes code
↓
Manually runs tests (sometimes)
↓
Manually builds app
↓
Manually uploads to server
↓
Prays it works
↓
It doesn't work
↓
Repeat
```

**Time**: 2-4 hours per deployment
**Frequency**: Once per week (too risky to do more)
**Errors**: Common (human mistakes)

**With CI/CD**:
```
Developer writes code
↓
Push to GitHub
↓
CI automatically:
  ✓ Builds app
  ✓ Runs all tests
  ✓ Checks code quality
  ✓ Deploys to staging
  ✓ Runs integration tests
  ✓ Deploys to production
↓
Done! (5-10 minutes)
```

**Time**: 5-10 minutes
**Frequency**: 10+ times per day
**Errors**: Rare (automated, consistent)

### Real-World Impact

**Companies Using CI/CD**:
- **Amazon**: Deploys every 11.7 seconds
- **Netflix**: Deploys 4,000+ times per day
- **Google**: 5,500 deployments per day

**Benefits**:
- 46x more frequent deployments
- 96 hours faster lead time (idea → production)
- 5x lower failure rate
- 24x faster recovery time

---

## GitHub Actions Fundamentals

### What is GitHub Actions?

GitHub Actions is a CI/CD platform that runs workflows when events occur in your repository.

**Events**: Push, pull request, release, schedule, manual trigger
**Runners**: Ubuntu, Windows, macOS virtual machines
**Actions**: Reusable workflow steps

### Basic Workflow

**.github/workflows/build.yml**:
```yaml
name: Build and Test

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew

      - name: Build with Gradle
        run: ./gradlew build

      - name: Run tests
        run: ./gradlew test

      - name: Upload test results
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: test-results
          path: build/test-results/
```

**What happens**:
1. Code is checked out
2. JDK 17 is installed
3. Gradle builds the project
4. Tests run
5. Test results are uploaded (even if tests fail)

---

## Android CI/CD Pipeline

### Complete Android Workflow

**.github/workflows/android.yml**:
```yaml
name: Android CI/CD

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]
  release:
    types: [ published ]

env:
  JAVA_VERSION: '17'

jobs:
  lint:
    name: Code Quality Check
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'

      - name: Cache Gradle packages
        uses: actions/cache@v3
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
          restore-keys: |
            ${{ runner.os }}-gradle-

      - name: Run ktlint
        run: ./gradlew ktlintCheck

      - name: Run detekt
        run: ./gradlew detekt

      - name: Upload detekt report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: detekt-report
          path: build/reports/detekt/

  test:
    name: Unit Tests
    runs-on: ubuntu-latest
    needs: lint

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'

      - name: Cache Gradle
        uses: actions/cache@v3
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*') }}

      - name: Run unit tests
        run: ./gradlew test

      - name: Generate test coverage report
        run: ./gradlew jacocoTestReport

      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          files: build/reports/jacoco/test/jacocoTestReport.xml
          fail_ci_if_error: true

      - name: Upload test results
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: test-results
          path: build/test-results/

  build:
    name: Build APK
    runs-on: ubuntu-latest
    needs: test

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'

      - name: Decode keystore
        if: github.event_name == 'release'
        run: |
          echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 --decode > keystore.jks

      - name: Build debug APK
        if: github.event_name != 'release'
        run: ./gradlew assembleDebug

      - name: Build release APK
        if: github.event_name == 'release'
        run: ./gradlew assembleRelease
        env:
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: app-apk
          path: app/build/outputs/apk/**/*.apk

  deploy:
    name: Deploy to Play Store
    runs-on: ubuntu-latest
    needs: build
    if: github.event_name == 'release'

    steps:
      - uses: actions/checkout@v4

      - name: Download APK
        uses: actions/download-artifact@v4
        with:
          name: app-apk

      - name: Deploy to Play Store
        uses: r0adkll/upload-google-play@v1
        with:
          serviceAccountJsonPlainText: ${{ secrets.PLAY_STORE_SERVICE_ACCOUNT }}
          packageName: com.example.app
          releaseFiles: app/build/outputs/apk/release/app-release.apk
          track: production
```

---

## Backend (Ktor) CI/CD Pipeline

**.github/workflows/ktor.yml**:
```yaml
name: Ktor Backend CI/CD

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest

    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: testpass
          POSTGRES_DB: testdb
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Run tests
        run: ./gradlew test
        env:
          DB_HOST: localhost
          DB_PORT: 5432
          DB_NAME: testdb
          DB_USER: postgres
          DB_PASSWORD: testpass

      - name: Generate coverage report
        run: ./gradlew jacocoTestReport

      - name: Upload coverage
        uses: codecov/codecov-action@v3

  build:
    runs-on: ubuntu-latest
    needs: test

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Build fat JAR
        run: ./gradlew shadowJar

      - name: Upload JAR
        uses: actions/upload-artifact@v4
        with:
          name: app-jar
          path: build/libs/*-all.jar

  docker:
    runs-on: ubuntu-latest
    needs: build
    if: github.ref == 'refs/heads/main'

    steps:
      - uses: actions/checkout@v4

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3

      - name: Login to Docker Hub
        uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}

      - name: Build and push
        uses: docker/build-push-action@v5
        with:
          context: .
          push: true
          tags: |
            myusername/my-app:latest
            myusername/my-app:${{ github.sha }}
          cache-from: type=registry,ref=myusername/my-app:latest
          cache-to: type=inline

  deploy:
    runs-on: ubuntu-latest
    needs: docker
    if: github.ref == 'refs/heads/main'

    steps:
      - name: Deploy to production
        uses: appleboy/ssh-action@v1.0.0
        with:
          host: ${{ secrets.DEPLOY_HOST }}
          username: ${{ secrets.DEPLOY_USER }}
          key: ${{ secrets.DEPLOY_SSH_KEY }}
          script: |
            docker pull myusername/my-app:latest
            docker stop my-app || true
            docker rm my-app || true
            docker run -d \
              --name my-app \
              -p 8080:8080 \
              -e DB_HOST=${{ secrets.DB_HOST }} \
              -e DB_PASSWORD=${{ secrets.DB_PASSWORD }} \
              myusername/my-app:latest
```

---

## Build Automation with Gradle

### Multi-Module Setup

**settings.gradle.kts**:
```kotlin
rootProject.name = "my-app"

include(":app")
include(":shared")
include(":backend")
```

**Root build.gradle.kts**:
```kotlin
plugins {
    kotlin("jvm") version "1.9.22" apply false
    kotlin("plugin.serialization") version "1.9.22" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.4"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    ktlint {
        version.set("1.0.1")
        android.set(false)
        outputToConsole.set(true)
        ignoreFailures.set(false)
    }

    detekt {
        config.setFrom(rootProject.file("detekt.yml"))
        buildUponDefaultConfig = true
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
```

### Custom Gradle Tasks

**build.gradle.kts**:
```kotlin
tasks.register("deployToStaging") {
    group = "deployment"
    description = "Deploy application to staging environment"

    dependsOn("test", "shadowJar")

    doLast {
        exec {
            commandLine(
                "scp",
                "build/libs/app-all.jar",
                "user@staging-server:/opt/app/"
            )
        }

        exec {
            commandLine(
                "ssh",
                "user@staging-server",
                "systemctl restart app"
            )
        }
    }
}

tasks.register("generateReleaseNotes") {
    group = "documentation"
    description = "Generate release notes from git commits"

    doLast {
        val output = ByteArrayOutputStream()
        exec {
            commandLine("git", "log", "--pretty=format:%s", "HEAD~10..HEAD")
            standardOutput = output
        }

        val releaseNotes = output.toString()
        file("RELEASE_NOTES.md").writeText("# Release Notes\n\n$releaseNotes")
        println("Generated RELEASE_NOTES.md")
    }
}

tasks.register("checkDependencyUpdates") {
    group = "verification"
    description = "Check for dependency updates"

    doLast {
        exec {
            commandLine("./gradlew", "dependencyUpdates")
        }
    }
}
```

---

## Code Quality Tools

### ktlint Configuration

**.editorconfig**:
```properties
[*.{kt,kts}]
indent_size = 4
insert_final_newline = true
max_line_length = 120
ij_kotlin_imports_layout = *,java.**,javax.**,kotlin.**,^

[*.yml]
indent_size = 2
```

**Run ktlint**:
```bash
./gradlew ktlintCheck    # Check for issues
./gradlew ktlintFormat   # Auto-fix issues
```

### detekt Configuration

**detekt.yml**:
```yaml
complexity:
  LongMethod:
    active: true
    threshold: 50
  LongParameterList:
    active: true
    threshold: 5
  ComplexMethod:
    active: true
    threshold: 15

style:
  MagicNumber:
    active: true
    ignoreNumbers: [-1, 0, 1, 2]
  MaxLineLength:
    active: true
    maxLineLength: 120

naming:
  FunctionNaming:
    active: true
    functionPattern: '[a-z][a-zA-Z0-9]*'
  ClassNaming:
    active: true
    classPattern: '[A-Z][a-zA-Z0-9]*'

potential-bugs:
  UnsafeCast:
    active: true
  EqualsAlwaysReturnsTrueOrFalse:
    active: true
```

**Run detekt**:
```bash
./gradlew detekt
```

---

## Docker for Backend

### Dockerfile

**Dockerfile**:
```dockerfile
# Build stage
FROM gradle:8.5-jdk17 AS build

WORKDIR /app

# Copy gradle files
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

# Download dependencies (cached layer)
RUN gradle dependencies --no-daemon

# Copy source code
COPY src ./src

# Build fat JAR
RUN gradle shadowJar --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy JAR from build stage
COPY --from=build /app/build/libs/*-all.jar app.jar

# Set ownership
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### docker-compose.yml

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DB_HOST=db
      - DB_PORT=5432
      - DB_NAME=appdb
      - DB_USER=appuser
      - DB_PASSWORD=${DB_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
    depends_on:
      db:
        condition: service_healthy
    restart: unless-stopped

  db:
    image: postgres:15-alpine
    environment:
      - POSTGRES_DB=appdb
      - POSTGRES_USER=appuser
      - POSTGRES_PASSWORD=${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U appuser"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - ./ssl:/etc/nginx/ssl:ro
    depends_on:
      - app
    restart: unless-stopped

volumes:
  postgres_data:
```

**.env**:
```bash
DB_PASSWORD=strongpassword123
JWT_SECRET=your-super-secret-key-change-in-production
```

**Run with Docker Compose**:
```bash
docker-compose up -d
docker-compose logs -f app
docker-compose down
```

---

## Publishing Android Apps

### Signing Configuration

**app/build.gradle.kts**:
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_PATH") ?: "keystore.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

### Generate Keystore

```bash
keytool -genkey -v \
  -keystore my-app.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias my-app-key
```

### Prepare for Play Store

1. **Version Code & Name**:
```kotlin
android {
    defaultConfig {
        versionCode = 1
        versionName = "1.0.0"
    }
}
```

2. **App Bundle**:
```bash
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

3. **Upload to Play Console**:
   - Create app listing
   - Upload app bundle
   - Fill store listing (title, description, screenshots)
   - Set pricing & distribution
   - Submit for review

---

## Exercise 1: Set Up Complete Android CI/CD

Create a GitHub Actions workflow that:
1. Runs ktlint and detekt
2. Runs unit tests with coverage
3. Builds debug APK for PRs
4. Builds signed release APK for releases
5. Uploads to GitHub releases

---

## Solution 1

**.github/workflows/android-ci-cd.yml**:
```yaml
name: Android CI/CD

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]
  release:
    types: [ published ]

env:
  JAVA_VERSION: '17'

jobs:
  code-quality:
    name: Code Quality
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'

      - name: Cache Gradle
        uses: actions/cache@v3
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*') }}
          restore-keys: ${{ runner.os }}-gradle-

      - name: Run ktlint
        run: ./gradlew ktlintCheck

      - name: Run detekt
        run: ./gradlew detekt

      - name: Upload detekt report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: detekt-report
          path: build/reports/detekt/

      - name: Comment PR with detekt results
        if: github.event_name == 'pull_request'
        uses: actions/github-script@v7
        with:
          script: |
            const fs = require('fs');
            const report = fs.readFileSync('build/reports/detekt/detekt.txt', 'utf8');
            github.rest.issues.createComment({
              issue_number: context.issue.number,
              owner: context.repo.owner,
              repo: context.repo.repo,
              body: `## Detekt Report\n\`\`\`\n${report}\n\`\`\``
            });

  test:
    name: Unit Tests
    runs-on: ubuntu-latest
    needs: code-quality

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'

      - name: Cache Gradle
        uses: actions/cache@v3
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*') }}

      - name: Run tests
        run: ./gradlew test

      - name: Generate coverage report
        run: ./gradlew jacocoTestReport

      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          files: build/reports/jacoco/test/jacocoTestReport.xml

      - name: Comment PR with coverage
        if: github.event_name == 'pull_request'
        uses: actions/github-script@v7
        with:
          script: |
            const fs = require('fs');
            const xml = fs.readFileSync('build/reports/jacoco/test/jacocoTestReport.xml', 'utf8');
            // Parse coverage percentage from XML
            github.rest.issues.createComment({
              issue_number: context.issue.number,
              owner: context.repo.owner,
              repo: context.repo.repo,
              body: `## Test Coverage\nSee full report in artifacts.`
            });

  build-debug:
    name: Build Debug APK
    runs-on: ubuntu-latest
    needs: test
    if: github.event_name == 'pull_request'

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'

      - name: Build debug APK
        run: ./gradlew assembleDebug

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: debug-apk
          path: app/build/outputs/apk/debug/app-debug.apk

  build-release:
    name: Build Release APK
    runs-on: ubuntu-latest
    needs: test
    if: github.event_name == 'release'

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'

      - name: Decode keystore
        run: echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 --decode > keystore.jks

      - name: Build release APK
        run: ./gradlew assembleRelease
        env:
          KEYSTORE_PATH: keystore.jks
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}

      - name: Upload to GitHub Release
        uses: actions/upload-release-asset@v1
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        with:
          upload_url: ${{ github.event.release.upload_url }}
          asset_path: app/build/outputs/apk/release/app-release.apk
          asset_name: app-release.apk
          asset_content_type: application/vnd.android.package-archive
```

---

## Exercise 2: Create Docker Setup for Ktor

Create a complete Docker setup for a Ktor backend with PostgreSQL.

---

## Solution 2

**Dockerfile**:
```dockerfile
FROM gradle:8.5-jdk17 AS build

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

RUN gradle dependencies --no-daemon

COPY src ./src

RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=build /app/build/libs/*-all.jar app.jar

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:8080/health || exit 1

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]
```

**docker-compose.yml**:
```yaml
version: '3.8'

services:
  app:
    build: .
    container_name: ktor-app
    ports:
      - "8080:8080"
    environment:
      - DB_HOST=db
      - DB_PORT=5432
      - DB_NAME=ktordb
      - DB_USER=ktoruser
      - DB_PASSWORD=${DB_PASSWORD:-changeme}
      - JWT_SECRET=${JWT_SECRET:-change-in-production}
      - LOG_LEVEL=INFO
    depends_on:
      db:
        condition: service_healthy
    restart: unless-stopped
    networks:
      - app-network

  db:
    image: postgres:15-alpine
    container_name: postgres-db
    environment:
      - POSTGRES_DB=ktordb
      - POSTGRES_USER=ktoruser
      - POSTGRES_PASSWORD=${DB_PASSWORD:-changeme}
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ktoruser -d ktordb"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped
    networks:
      - app-network

  nginx:
    image: nginx:alpine
    container_name: nginx-proxy
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - ./ssl:/etc/nginx/ssl:ro
    depends_on:
      - app
    restart: unless-stopped
    networks:
      - app-network

networks:
  app-network:
    driver: bridge

volumes:
  postgres_data:
```

**nginx.conf**:
```nginx
events {
    worker_connections 1024;
}

http {
    upstream ktor_backend {
        server app:8080;
    }

    server {
        listen 80;
        server_name example.com;

        location / {
            proxy_pass http://ktor_backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        location /health {
            proxy_pass http://ktor_backend/health;
            access_log off;
        }
    }
}
```

**Makefile**:
```makefile
.PHONY: build up down logs clean

build:
	docker-compose build

up:
	docker-compose up -d

down:
	docker-compose down

logs:
	docker-compose logs -f app

clean:
	docker-compose down -v
	docker system prune -f

restart:
	docker-compose restart app

shell:
	docker-compose exec app sh
```

**Usage**:
```bash
make build
make up
make logs
make down
```

---

## Exercise 3: Automated Release Process

Create a workflow that automatically:
1. Bumps version number
2. Generates changelog
3. Creates GitHub release
4. Deploys to production

---

## Solution 3

**.github/workflows/release.yml**:
```yaml
name: Automated Release

on:
  workflow_dispatch:
    inputs:
      version_bump:
        description: 'Version bump type'
        required: true
        type: choice
        options:
          - patch
          - minor
          - major

jobs:
  release:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0
          token: ${{ secrets.GITHUB_TOKEN }}

      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Get current version
        id: current_version
        run: |
          VERSION=$(grep "versionName" app/build.gradle.kts | sed 's/.*"\(.*\)".*/\1/')
          echo "version=$VERSION" >> $GITHUB_OUTPUT

      - name: Bump version
        id: bump_version
        run: |
          CURRENT="${{ steps.current_version.outputs.version }}"
          IFS='.' read -ra PARTS <<< "$CURRENT"
          MAJOR=${PARTS[0]}
          MINOR=${PARTS[1]}
          PATCH=${PARTS[2]}

          case "${{ github.event.inputs.version_bump }}" in
            major)
              MAJOR=$((MAJOR + 1))
              MINOR=0
              PATCH=0
              ;;
            minor)
              MINOR=$((MINOR + 1))
              PATCH=0
              ;;
            patch)
              PATCH=$((PATCH + 1))
              ;;
          esac

          NEW_VERSION="$MAJOR.$MINOR.$PATCH"
          echo "new_version=$NEW_VERSION" >> $GITHUB_OUTPUT

      - name: Update version in build.gradle.kts
        run: |
          sed -i 's/versionName = ".*"/versionName = "${{ steps.bump_version.outputs.new_version }}"/' app/build.gradle.kts

      - name: Generate changelog
        id: changelog
        run: |
          PREVIOUS_TAG=$(git describe --tags --abbrev=0)
          CHANGELOG=$(git log ${PREVIOUS_TAG}..HEAD --pretty=format:"- %s")
          echo "changelog<<EOF" >> $GITHUB_OUTPUT
          echo "$CHANGELOG" >> $GITHUB_OUTPUT
          echo "EOF" >> $GITHUB_OUTPUT

      - name: Commit version bump
        run: |
          git config user.name "GitHub Actions"
          git config user.email "actions@github.com"
          git add app/build.gradle.kts
          git commit -m "chore: bump version to ${{ steps.bump_version.outputs.new_version }}"
          git push

      - name: Create GitHub Release
        uses: actions/create-release@v1
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        with:
          tag_name: v${{ steps.bump_version.outputs.new_version }}
          release_name: Release ${{ steps.bump_version.outputs.new_version }}
          body: |
            ## What's Changed
            ${{ steps.changelog.outputs.changelog }}

            **Full Changelog**: https://github.com/${{ github.repository }}/compare/v${{ steps.current_version.outputs.version }}...v${{ steps.bump_version.outputs.new_version }}
          draft: false
          prerelease: false

      - name: Build release APK
        run: ./gradlew assembleRelease
        env:
          KEYSTORE_PATH: keystore.jks
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}

      - name: Deploy to production
        run: |
          # Your deployment logic here
          echo "Deploying version ${{ steps.bump_version.outputs.new_version }}"
```

---

## Why This Matters

### Business Impact

**Deployment Frequency**:
- Without CI/CD: 1x per week
- With CI/CD: 10x per day
- Result: 50x faster time-to-market

**Quality**:
- Automated tests catch 80% of bugs before production
- Code quality tools prevent technical debt
- Consistent builds eliminate "works on my machine"

**Developer Productivity**:
- No manual deployment steps
- Immediate feedback on code quality
- More time for features, less for debugging

---

## Checkpoint Quiz

### Question 1
What does CI/CD stand for?

A) Code Integration / Code Deployment
B) Continuous Integration / Continuous Deployment
C) Constant Improvement / Constant Development
D) Central Integration / Central Deployment

### Question 2
What's the main benefit of automated testing in CI/CD?

A) Faster builds
B) Smaller APKs
C) Catching bugs before they reach production
D) Better code formatting

### Question 3
What is Docker used for?

A) Compiling Kotlin code
B) Packaging applications in containers
C) Writing tests
D) Designing UI

### Question 4
What does ktlint do?

A) Compiles Kotlin code
B) Runs tests
C) Checks and formats code style
D) Deploys applications

### Question 5
Why use Gradle caching in CI/CD?

A) To save disk space
B) To speed up builds by reusing dependencies
C) To improve code quality
D) To reduce APK size

---

## Quiz Answers

**Question 1: B) Continuous Integration / Continuous Deployment**

- **CI**: Automatically build and test on every commit
- **CD**: Automatically deploy to production

Benefits: Faster releases, fewer bugs, happier developers

---

**Question 2: C) Catching bugs before they reach production**

Automated tests in CI:
- Run on every commit
- Catch regressions immediately
- Prevent broken code from merging
- Save time and money

---

**Question 3: B) Packaging applications in containers**

Docker containers:
- Include app + all dependencies
- Run consistently everywhere
- Easy to deploy and scale
- Isolated from host system

---

**Question 4: C) Checks and formats code style**

ktlint enforces:
- Consistent code formatting
- Kotlin style guide
- Team coding standards
- Prevents "style wars"

---

**Question 5: B) To speed up builds by reusing dependencies**

Gradle caching:
- Downloads dependencies once
- Reuses on subsequent builds
- 5-10x faster builds
- Less network usage

---

## What You've Learned

✅ Why CI/CD is essential for modern development
✅ GitHub Actions for automated builds and tests
✅ Complete Android CI/CD pipeline
✅ Complete Ktor backend CI/CD pipeline
✅ Build automation with Gradle
✅ Code quality tools (ktlint, detekt)
✅ Docker for containerized deployment
✅ Publishing Android apps to Play Store
✅ Automated release processes

---

## Next Steps

In **Lesson 7.6: Cloud Deployment**, you'll learn:
- Deploying Ktor apps to AWS, GCP, Heroku
- Database hosting and management
- Environment configuration
- SSL/TLS setup
- Scaling strategies
- Cost optimization

Your CI/CD pipeline is ready - now let's deploy to the cloud!

---
