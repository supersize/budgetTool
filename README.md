# Budget Tool (JHKArchive)

**Budget Tool** is a robust personal finance backend application designed for efficient monthly budget management and expense tracking. This project emphasizes high-performance infrastructure, cloud-native design, and automated deployment.

---

## System Architecture

The project is built on a scalable cloud-native architecture, ensuring high availability and secure data flow.

![System Architecture](https://github.com/supersize/budgetTool/blob/main/src/main/resources/static/budgetTool_cloud_arch_diagram.png)

> **Note:** This architecture separates the presentation layer (ALB) from the application logic (EC2) and data storage (RDS/Redis), following AWS best practices for security and performance.

---

## Tech Stack

### Backend & Database
- **Core:** Java 17, Spring Boot 3.x, Spring Security
- **Data:** JPA (Hibernate), Querydsl, PostgreSQL (PostGIS)
- **Caching:** Redis

### Infrastructure & DevOps
- **Cloud:** AWS (EC2, RDS, ALB, S3)
- **IAC:** Terraform (Manual Provisioning)
- **CI/CD:** GitHub Actions, Docker
- **Monitoring/Testing:** k6 (Load Testing)

---

## CI/CD Pipeline

The deployment process is automated using GitHub Actions, ensuring a seamless transition from code to production while maintaining infrastructure stability through Terraform.

![CI/CD Pipeline](https://github.com/supersize/budgetTool/blob/main/src/main/resources/static/budgetTool_cicd_pipeline.png)

- **Manual Infrastructure:** Infrastructure is managed via Terraform (`init`, `plan`, `apply`) to ensure controlled changes.
- **Automated Workflow:** Every push triggers a GitHub Action that builds the Spring Boot JAR, packages it as a Docker image, and deploys it to AWS EC2 using a Blue/Green strategy.

---

## 📊 Performance & Load Testing

To ensure the application can handle production-level traffic, I conducted stress tests using **k6**.

### Load Test Execution (500 VUs / 60s) | Automated Email Alert |
|:---:|:---:|
| ![k6 Load Test](https://github.com/supersize/budgetTool/blob/main/src/main/resources/static/load-test.gif) | ![Email Alert](<img src="https://github.com/supersize/budgetTool/blob/main/src/main/resources/static/grafana_email_alert.png" width="250"/>) |
| *Simulated traffic on production endpoint* | *Alert triggered by high CPU/Memory usage* |

- **Scenario:** Simulated 500 concurrent virtual users (VUs) accessing the production endpoint for 60 seconds.
- **Goal:** To measure the stability of the Spring Boot application and the response latency under load.
- **Result:**
  - Successfully maintained stable performance with zero request failures during the peak load.
- **Monitoring:** An automated email alert was successfully triggered when the system detected high resource utilization during the stress test, confirming the robustness of the monitoring setup.
---

## Key Features & Technical Decisions

- **Domain Modeling:** Complex relationship management between Budgets and Expenses.
- **Security First:** Implemented Spring Security to align with enterprise-level financial standards.
- **Distributed Locking:** Planned integration of **Redisson** for data consistency and concurrency control in multi-instance environments.
- **Scalability:** Designed with an Application Load Balancer (ALB) to support future horizontal scaling.

---

## ⚙ Prerequisites & Installation

### Environment
- Java 17+
- Docker & Docker Compose
- PostgreSQL 15 / Redis

### How to Run (Local)

```bash
# 1. Start Infrastructure (PostgreSQL & Redis)
docker-compose up -d

# 2. Run Application
./gradlew bootRun
```

---

## Key Features & Technical Decisions

- **Domain Modeling:** Complex relationship management between Budgets and Expenses.
- **Security First:** Implemented Spring Security to align with enterprise-level financial standards.
- **Distributed Locking:** Planned integration of **Redisson** for data consistency and concurrency control in multi-instance environments.
- **Scalability:** Designed with an Application Load Balancer (ALB) to support future horizontal scaling.

### Project Screenshots (UI)

| Dashboard & History | Account & Finance |
|:---:|:---:|
| ![Dashboard](https://github.com/supersize/budgetTool/blob/main/src/main/resources/static/bt_dashboard.png) | ![Account](https://github.com/supersize/budgetTool/blob/main/src/main/resources/static/bt_account.png) |
| *Main dashboard showing overview* | *Managing account-specific finances* |

| User Onboarding | Verification Process | New Account |
|:---:|:---:|:---:|
| <img src="https://github.com/supersize/budgetTool/blob/main/src/main/resources/static/bt_signup.png" width="250"/> | <img src="https://github.com/supersize/budgetTool/blob/main/src/main/resources/static/bt_verification.png" width="250"/> | <img src="https://github.com/supersize/budgetTool/blob/main/src/main/resources/static/bt_newaccount.png" width="250"/> |

---
