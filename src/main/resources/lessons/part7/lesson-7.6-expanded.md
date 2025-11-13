# Lesson 7.6: Cloud Deployment

**Estimated Time**: 80 minutes

---

## Introduction

Your application is built, tested, and containerized. Now it's time to deploy to the cloud and serve millions of users worldwide!

In this lesson, you'll master cloud deployment for Kotlin applications:
- ✅ Deploying Ktor apps to AWS, Google Cloud, Heroku
- ✅ Database hosting (PostgreSQL, MongoDB)
- ✅ Environment configuration and secrets management
- ✅ SSL/TLS certificates for HTTPS
- ✅ Load balancing and scaling
- ✅ Cost optimization strategies

By the end, you'll confidently deploy production-ready applications to the cloud.

---

## Cloud Platform Comparison

### AWS vs Google Cloud vs Heroku

| Feature | AWS | Google Cloud (GCP) | Heroku |
|---------|-----|-------------------|--------|
| **Ease of Use** | ⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Pricing** | Pay-as-you-go | Pay-as-you-go | Free tier + plans |
| **Best For** | Enterprise, flexibility | Kubernetes, ML | Startups, prototypes |
| **Learning Curve** | Steep | Medium | Easy |
| **Kotlin Support** | ✅ EC2, ECS, Lambda | ✅ Compute Engine, Cloud Run | ✅ Native |

**Recommendation**:
- **Learning/Prototype**: Heroku (easiest)
- **Production/Scale**: AWS or GCP (most powerful)
- **Kubernetes**: GCP (best K8s integration)

---

## Heroku Deployment (Easiest)

### Why Heroku?

- ✅ Deploy in 5 minutes
- ✅ Free tier available
- ✅ Automatic HTTPS
- ✅ Built-in database hosting
- ✅ Zero DevOps knowledge needed

### Deploy Ktor to Heroku

**1. Create Procfile**:
```
web: java -jar build/libs/my-app-all.jar
```

**2. Update build.gradle.kts**:
```kotlin
tasks {
    shadowJar {
        archiveFileName.set("my-app-all.jar")
        manifest {
            attributes["Main-Class"] = "com.example.ApplicationKt"
        }
    }

    create("stage") {
        dependsOn("shadowJar")
    }
}
```

**3. Create app.json** (optional):
```json
{
  "name": "My Ktor App",
  "description": "A Ktor backend application",
  "buildpacks": [
    {
      "url": "heroku/gradle"
    }
  ],
  "env": {
    "JWT_SECRET": {
      "description": "Secret key for JWT tokens",
      "generator": "secret"
    }
  },
  "addons": [
    {
      "plan": "heroku-postgresql:mini"
    }
  ]
}
```

**4. Deploy**:
```bash
# Login to Heroku
heroku login

# Create app
heroku create my-ktor-app

# Add PostgreSQL
heroku addons:create heroku-postgresql:mini

# Set environment variables
heroku config:set JWT_SECRET=your-secret-key

# Deploy
git push heroku main

# View logs
heroku logs --tail

# Open app
heroku open
```

**5. Configure port** (Heroku provides PORT env var):
```kotlin
// Application.kt
fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}
```

**Your app is live at**: `https://my-ktor-app.herokuapp.com`

---

## AWS Deployment

### Option 1: AWS Elastic Beanstalk (Easiest AWS)

**1. Install AWS CLI**:
```bash
pip install awscli
aws configure
```

**2. Install Elastic Beanstalk CLI**:
```bash
pip install awsebcli
```

**3. Initialize EB**:
```bash
eb init -p docker my-app --region us-east-1
```

**4. Create Dockerfile** (if not exists):
```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY build/libs/my-app-all.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

**5. Create .ebextensions/options.config**:
```yaml
option_settings:
  aws:elasticbeanstalk:application:environment:
    PORT: 8080
    DB_HOST: your-rds-endpoint.amazonaws.com
    DB_PORT: 5432
```

**6. Deploy**:
```bash
# Build JAR
./gradlew shadowJar

# Create environment and deploy
eb create my-app-env

# Deploy updates
eb deploy

# View status
eb status

# View logs
eb logs

# Open in browser
eb open
```

### Option 2: AWS ECS (Container Service)

**1. Create ECR repository**:
```bash
aws ecr create-repository --repository-name my-app
```

**2. Build and push Docker image**:
```bash
# Login to ECR
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin \
  YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com

# Build image
docker build -t my-app .

# Tag image
docker tag my-app:latest \
  YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/my-app:latest

# Push image
docker push YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/my-app:latest
```

**3. Create task definition** (task-definition.json):
```json
{
  "family": "my-app",
  "containerDefinitions": [
    {
      "name": "my-app",
      "image": "YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/my-app:latest",
      "memory": 512,
      "cpu": 256,
      "essential": true,
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "DB_HOST",
          "value": "your-rds-endpoint.amazonaws.com"
        }
      ],
      "secrets": [
        {
          "name": "DB_PASSWORD",
          "valueFrom": "arn:aws:secretsmanager:REGION:ACCOUNT_ID:secret:db-password"
        }
      ]
    }
  ]
}
```

**4. Create ECS service**:
```bash
# Create cluster
aws ecs create-cluster --cluster-name my-app-cluster

# Register task definition
aws ecs register-task-definition --cli-input-json file://task-definition.json

# Create service
aws ecs create-service \
  --cluster my-app-cluster \
  --service-name my-app-service \
  --task-definition my-app \
  --desired-count 2 \
  --launch-type FARGATE
```

---

## Google Cloud Deployment

### Option 1: Cloud Run (Easiest GCP)

**1. Install gcloud CLI**:
```bash
# Install from https://cloud.google.com/sdk/docs/install

# Login
gcloud auth login

# Set project
gcloud config set project YOUR_PROJECT_ID
```

**2. Build and deploy**:
```bash
# Build JAR
./gradlew shadowJar

# Deploy (builds container automatically!)
gcloud run deploy my-app \
  --source . \
  --region us-central1 \
  --platform managed \
  --allow-unauthenticated \
  --set-env-vars "JWT_SECRET=your-secret"

# Or deploy from existing Docker image
gcloud run deploy my-app \
  --image gcr.io/YOUR_PROJECT_ID/my-app:latest \
  --region us-central1 \
  --platform managed
```

**Your app is live at**: `https://my-app-HASH-uc.a.run.app`

### Option 2: Google Kubernetes Engine (GKE)

**1. Create Kubernetes cluster**:
```bash
gcloud container clusters create my-app-cluster \
  --zone us-central1-a \
  --num-nodes 2
```

**2. Build and push image**:
```bash
# Build image
docker build -t gcr.io/YOUR_PROJECT_ID/my-app:v1 .

# Push to Container Registry
docker push gcr.io/YOUR_PROJECT_ID/my-app:v1
```

**3. Create deployment.yaml**:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: my-app
  template:
    metadata:
      labels:
        app: my-app
    spec:
      containers:
      - name: my-app
        image: gcr.io/YOUR_PROJECT_ID/my-app:v1
        ports:
        - containerPort: 8080
        env:
        - name: DB_HOST
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: host
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: password
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
---
apiVersion: v1
kind: Service
metadata:
  name: my-app-service
spec:
  type: LoadBalancer
  selector:
    app: my-app
  ports:
  - port: 80
    targetPort: 8080
```

**4. Deploy to Kubernetes**:
```bash
# Apply deployment
kubectl apply -f deployment.yaml

# Get external IP
kubectl get service my-app-service

# Scale deployment
kubectl scale deployment my-app --replicas=5

# Update image
kubectl set image deployment/my-app my-app=gcr.io/YOUR_PROJECT_ID/my-app:v2
```

---

## Database Hosting

### PostgreSQL on Heroku

```bash
# Add PostgreSQL
heroku addons:create heroku-postgresql:mini

# Get credentials
heroku pg:credentials:url DATABASE_URL
```

**Connect from Ktor**:
```kotlin
val dbUrl = System.getenv("DATABASE_URL")
  ?: "postgresql://localhost:5432/mydb"

// Parse Heroku DATABASE_URL format
// postgres://user:password@host:port/database
val uri = URI(dbUrl)
val username = uri.userInfo.split(":")[0]
val password = uri.userInfo.split(":")[1]
val dbUrl = "jdbc:postgresql://${uri.host}:${uri.port}${uri.path}"

Database.connect(
    url = dbUrl,
    driver = "org.postgresql.Driver",
    user = username,
    password = password
)
```

### Amazon RDS

**1. Create PostgreSQL instance**:
```bash
aws rds create-db-instance \
  --db-instance-identifier my-app-db \
  --db-instance-class db.t3.micro \
  --engine postgres \
  --master-username admin \
  --master-user-password YOUR_PASSWORD \
  --allocated-storage 20 \
  --vpc-security-group-ids sg-xxxxx \
  --publicly-accessible
```

**2. Connect from application**:
```kotlin
Database.connect(
    url = "jdbc:postgresql://my-app-db.xxxxx.us-east-1.rds.amazonaws.com:5432/postgres",
    driver = "org.postgresql.Driver",
    user = "admin",
    password = System.getenv("DB_PASSWORD")
)
```

### Google Cloud SQL

**1. Create instance**:
```bash
gcloud sql instances create my-app-db \
  --database-version=POSTGRES_15 \
  --tier=db-f1-micro \
  --region=us-central1
```

**2. Create database**:
```bash
gcloud sql databases create mydb --instance=my-app-db
```

**3. Connect from Cloud Run**:
```bash
gcloud run deploy my-app \
  --add-cloudsql-instances=PROJECT_ID:REGION:my-app-db \
  --set-env-vars INSTANCE_CONNECTION_NAME=PROJECT_ID:REGION:my-app-db
```

---

## SSL/TLS Certificates

### Heroku (Automatic)

Heroku provides HTTPS automatically!
```
https://my-app.herokuapp.com ✅
```

### AWS Certificate Manager

**1. Request certificate**:
```bash
aws acm request-certificate \
  --domain-name myapp.com \
  --subject-alternative-names www.myapp.com \
  --validation-method DNS
```

**2. Validate domain** (add DNS records provided by AWS)

**3. Attach to Load Balancer**:
```bash
aws elbv2 add-listener \
  --load-balancer-arn YOUR_LB_ARN \
  --protocol HTTPS \
  --port 443 \
  --certificates CertificateArn=YOUR_CERT_ARN \
  --default-actions Type=forward,TargetGroupArn=YOUR_TG_ARN
```

### Let's Encrypt with Certbot

**For self-hosted servers**:
```bash
# Install Certbot
sudo apt-get install certbot

# Get certificate
sudo certbot certonly --standalone -d myapp.com -d www.myapp.com

# Certificates are at:
# /etc/letsencrypt/live/myapp.com/fullchain.pem
# /etc/letsencrypt/live/myapp.com/privkey.pem

# Auto-renewal
sudo certbot renew --dry-run
```

**Configure Ktor for HTTPS**:
```kotlin
fun main() {
    val keyStoreFile = File("/etc/letsencrypt/live/myapp.com/keystore.jks")
    val keyStore = KeyStore.getInstance(keyStoreFile, "password".toCharArray())

    embeddedServer(Netty, environment = applicationEngineEnvironment {
        connector {
            port = 80
        }
        sslConnector(
            keyStore = keyStore,
            keyAlias = "myapp",
            keyStorePassword = { "password".toCharArray() },
            privateKeyPassword = { "password".toCharArray() }
        ) {
            port = 443
            keyStorePath = keyStoreFile
        }
        module {
            module()
        }
    }).start(wait = true)
}
```

---

## Environment Configuration

### application.conf

```hocon
ktor {
    deployment {
        port = 8080
        port = ${?PORT}  # Override with PORT env var
    }
}

database {
    host = "localhost"
    host = ${?DB_HOST}
    port = 5432
    port = ${?DB_PORT}
    name = "mydb"
    name = ${?DB_NAME}
    user = "user"
    user = ${?DB_USER}
    password = "password"
    password = ${?DB_PASSWORD}
}

jwt {
    secret = "change-me-in-production"
    secret = ${?JWT_SECRET}
    issuer = "my-app"
    audience = "my-app-users"
    realm = "my-app"
}
```

### Secrets Management

**AWS Secrets Manager**:
```kotlin
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest

fun getSecret(secretName: String): String {
    val client = SecretsManagerClient.create()
    val request = GetSecretValueRequest.builder()
        .secretId(secretName)
        .build()
    val response = client.getSecretValue(request)
    return response.secretString()
}

// Usage
val dbPassword = getSecret("prod/db/password")
```

**Google Secret Manager**:
```kotlin
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient

fun getSecret(projectId: String, secretId: String): String {
    SecretManagerServiceClient.create().use { client ->
        val name = "projects/$projectId/secrets/$secretId/versions/latest"
        val response = client.accessSecretVersion(name)
        return response.payload.data.toStringUtf8()
    }
}
```

---

## Load Balancing and Scaling

### Horizontal Scaling

**AWS Auto Scaling**:
```bash
# Create Auto Scaling group
aws autoscaling create-auto-scaling-group \
  --auto-scaling-group-name my-app-asg \
  --min-size 2 \
  --max-size 10 \
  --desired-capacity 2 \
  --target-group-arns YOUR_TG_ARN \
  --vpc-zone-identifier subnet-xxxxx

# Create scaling policy (scale when CPU > 70%)
aws autoscaling put-scaling-policy \
  --auto-scaling-group-name my-app-asg \
  --policy-name scale-up \
  --scaling-adjustment 1 \
  --adjustment-type ChangeInCapacity
```

**Google Cloud Run** (automatic):
```bash
gcloud run deploy my-app \
  --min-instances 1 \
  --max-instances 10 \
  --concurrency 80
```

### Vertical Scaling

**Change instance size**:
```bash
# AWS
aws ec2 modify-instance-attribute \
  --instance-id i-xxxxx \
  --instance-type t3.large

# GCP
gcloud compute instances set-machine-type INSTANCE_NAME \
  --machine-type n1-standard-2
```

---

## Exercise 1: Deploy to Heroku

Deploy a Ktor backend to Heroku with PostgreSQL.

---

## Solution 1

**1. Project setup**:

**Procfile**:
```
web: java -jar build/libs/my-app-all.jar
```

**build.gradle.kts**:
```kotlin
plugins {
    kotlin("jvm") version "1.9.22"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

tasks {
    shadowJar {
        archiveFileName.set("my-app-all.jar")
        manifest {
            attributes["Main-Class"] = "com.example.ApplicationKt"
        }
    }

    create("stage") {
        dependsOn("shadowJar")
    }
}
```

**src/main/kotlin/com/example/Application.kt**:
```kotlin
package com.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import java.net.URI

fun main() {
    // Heroku provides PORT
    val port = System.getenv("PORT")?.toInt() ?: 8080

    // Connect to Heroku PostgreSQL
    val dbUrl = System.getenv("DATABASE_URL")?.let { url ->
        // Parse postgres://user:password@host:port/database
        val uri = URI(url)
        val userInfo = uri.userInfo.split(":")
        Database.connect(
            url = "jdbc:postgresql://${uri.host}:${uri.port}${uri.path}",
            driver = "org.postgresql.Driver",
            user = userInfo[0],
            password = userInfo[1]
        )
    }

    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        routing {
            get("/") {
                call.respondText("Hello from Heroku!")
            }

            get("/health") {
                call.respondText("OK")
            }
        }
    }.start(wait = true)
}
```

**2. Deploy**:
```bash
# Create Heroku app
heroku create my-ktor-app

# Add PostgreSQL
heroku addons:create heroku-postgresql:mini

# Deploy
git add .
git commit -m "Initial deployment"
git push heroku main

# View logs
heroku logs --tail

# Open app
heroku open
```

**3. Verify deployment**:
```bash
curl https://my-ktor-app.herokuapp.com/
# Output: Hello from Heroku!

curl https://my-ktor-app.herokuapp.com/health
# Output: OK
```

---

## Exercise 2: Deploy to AWS with Docker

Deploy a containerized Ktor app to AWS ECS.

---

## Solution 2

**1. Create Dockerfile**:
```dockerfile
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*-all.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s CMD wget -q --spider http://localhost:8080/health || exit 1
CMD ["java", "-jar", "app.jar"]
```

**2. Push to ECR**:
```bash
# Create repository
aws ecr create-repository --repository-name my-ktor-app

# Get login command
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin \
  YOUR_ACCOUNT.dkr.ecr.us-east-1.amazonaws.com

# Build and push
docker build -t my-ktor-app .
docker tag my-ktor-app:latest \
  YOUR_ACCOUNT.dkr.ecr.us-east-1.amazonaws.com/my-ktor-app:latest
docker push YOUR_ACCOUNT.dkr.ecr.us-east-1.amazonaws.com/my-ktor-app:latest
```

**3. Create ECS task definition**:
```json
{
  "family": "my-ktor-app",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "256",
  "memory": "512",
  "containerDefinitions": [
    {
      "name": "my-ktor-app",
      "image": "YOUR_ACCOUNT.dkr.ecr.us-east-1.amazonaws.com/my-ktor-app:latest",
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "JWT_SECRET",
          "value": "your-secret-key"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/my-ktor-app",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "ecs"
        }
      }
    }
  ]
}
```

**4. Deploy to ECS**:
```bash
# Create cluster
aws ecs create-cluster --cluster-name my-app-cluster

# Register task definition
aws ecs register-task-definition --cli-input-json file://task-definition.json

# Create service with load balancer
aws ecs create-service \
  --cluster my-app-cluster \
  --service-name my-ktor-service \
  --task-definition my-ktor-app \
  --desired-count 2 \
  --launch-type FARGATE \
  --network-configuration "awsvpcConfiguration={subnets=[subnet-xxxxx],securityGroups=[sg-xxxxx],assignPublicIp=ENABLED}" \
  --load-balancers "targetGroupArn=arn:aws:elasticloadbalancing:...,containerName=my-ktor-app,containerPort=8080"
```

---

## Exercise 3: Set Up Auto-Scaling

Configure auto-scaling for your cloud deployment.

---

## Solution 3

**Heroku**:
```bash
# Upgrade to Standard or Performance dyno
heroku dyno:resize web=standard-1x

# Enable auto-scaling (2-10 dynos)
heroku ps:autoscale:enable \
  --min 2 \
  --max 10 \
  --p95-response-time 500ms
```

**AWS ECS**:
```bash
# Register scalable target
aws application-autoscaling register-scalable-target \
  --service-namespace ecs \
  --resource-id service/my-app-cluster/my-ktor-service \
  --scalable-dimension ecs:service:DesiredCount \
  --min-capacity 2 \
  --max-capacity 10

# Create scaling policy (target tracking)
aws application-autoscaling put-scaling-policy \
  --service-namespace ecs \
  --resource-id service/my-app-cluster/my-ktor-service \
  --scalable-dimension ecs:service:DesiredCount \
  --policy-name cpu-scaling-policy \
  --policy-type TargetTrackingScaling \
  --target-tracking-scaling-policy-configuration file://scaling-policy.json
```

**scaling-policy.json**:
```json
{
  "TargetValue": 70.0,
  "PredefinedMetricSpecification": {
    "PredefinedMetricType": "ECSServiceAverageCPUUtilization"
  },
  "ScaleOutCooldown": 60,
  "ScaleInCooldown": 60
}
```

**Google Cloud Run** (automatic!):
```bash
gcloud run deploy my-app \
  --min-instances 2 \
  --max-instances 10 \
  --cpu-throttling \
  --concurrency 100
```

---

## Why This Matters

### Production Reality

**Without Cloud Deployment**:
- Apps run on your laptop
- Users can't access
- No scalability
- No reliability

**With Cloud Deployment**:
- Accessible worldwide 24/7
- Auto-scales to handle traffic
- 99.9% uptime guarantee
- Professional infrastructure

### Cost Comparison

**Small App (10K users)**:
- Heroku: $7-25/month
- AWS: $10-30/month
- GCP: $10-30/month

**Medium App (100K users)**:
- Heroku: $50-200/month
- AWS: $50-150/month (more optimized)
- GCP: $50-150/month

---

## Checkpoint Quiz

### Question 1
Which cloud platform is easiest for beginners?

A) AWS
B) Google Cloud
C) Heroku
D) Azure

### Question 2
What protocol should production APIs use?

A) HTTP
B) HTTPS
C) FTP
D) Either HTTP or HTTPS

### Question 3
What is horizontal scaling?

A) Adding more CPU to one server
B) Adding more servers
C) Upgrading RAM
D) Buying faster disks

### Question 4
Why use environment variables for secrets?

A) Faster performance
B) Keep secrets out of code
C) Reduce file size
D) Better formatting

### Question 5
What does a load balancer do?

A) Compiles code faster
B) Distributes traffic across multiple servers
C) Stores database backups
D) Monitors server health

---

## Quiz Answers

**Question 1: C) Heroku**

Heroku is designed for simplicity:
- Deploy with `git push heroku main`
- Automatic HTTPS
- Built-in database
- No server management

AWS/GCP are more powerful but complex.

---

**Question 2: B) HTTPS**

HTTPS is mandatory for production:
- Encrypts data in transit
- Prevents man-in-the-middle attacks
- Required by browsers
- Improves SEO

HTTP is only for local development.

---

**Question 3: B) Adding more servers**

Scaling types:
- **Horizontal**: Add more servers (better)
- **Vertical**: Bigger server (limited)

Horizontal scaling = unlimited capacity

---

**Question 4: B) Keep secrets out of code**

Environment variables:
- Don't commit secrets to Git
- Different values per environment
- Easy to rotate
- More secure

Never hardcode secrets!

---

**Question 5: B) Distributes traffic across multiple servers**

Load balancers:
- Distribute requests evenly
- Health check servers
- Remove failed servers
- Enable horizontal scaling

Essential for high availability.

---

## What You've Learned

✅ Deploying Ktor apps to Heroku (easiest)
✅ Deploying to AWS (Elastic Beanstalk, ECS)
✅ Deploying to Google Cloud (Cloud Run, GKE)
✅ Database hosting (PostgreSQL on cloud platforms)
✅ SSL/TLS certificates for HTTPS
✅ Environment configuration and secrets management
✅ Load balancing and auto-scaling
✅ Cost optimization strategies

---

## Next Steps

In **Lesson 7.7: Monitoring and Analytics**, you'll learn:
- Application logging strategies
- Error tracking (Sentry, Firebase Crashlytics)
- Analytics (Firebase Analytics, Mixpanel)
- Performance monitoring
- APM tools
- User feedback integration

Your app is deployed - now let's monitor it!

---
