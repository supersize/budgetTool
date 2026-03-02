# 💰 Budget Tool

**Budget Tool** is a personal finance web application designed to manage monthly budgets and track expenses.

This project was developed not only to implement CRUD functionality, but also to practice building a structured and extensible backend application with security and scalability in mind.

---

## 📌 Project Goals

- Strengthen backend development skills using Spring Boot  
- Practice domain modeling (Budget ↔ Expense relationship)  
- Design a maintainable layered architecture  
- Apply security best practices suitable for financial applications  
- Prepare for future cloud-based and distributed deployment  

---

## 🛠 Tech Stack

### Backend
- Java  
- Spring Boot  
- Spring Security  
- JPA  
- Gradle  

### Database
- PostgreSQL  

### Infrastructure / Middleware
- Docker  
- Redis  

### Frontend
- HTML  
- CSS  
- JavaScript  

---

## 🔐 Security Design

This project uses **Spring Security** to ensure that the application follows established security standards.

In financial-related applications, security is critical. Instead of implementing custom authentication and authorization logic, I chose to use Spring Security because:

- It is a widely adopted and well-tested security framework  
- It helps prevent common vulnerabilities  
- It supports scalable and extensible security configurations  
- It aligns with enterprise-level application security practices  

This design decision prioritizes reliability, maintainability, and future extensibility over building ad-hoc security mechanisms.

---

## ✨ Key Features

- Monthly budget creation and management  
- Expense registration / update / deletion  
- Category-based expense classification  
- Budget vs. actual spending comparison  
- Monthly expense tracking  

---

## 🗂 Architecture Overview

- Layered structure (Controller – Service – Repository)  
- DTO-based request/response separation  
- Domain relationship modeling (Budget ↔ Expense)  
- Validation handling  
- Security configuration via Spring Security  

The application is structured to allow future integration with authentication mechanisms such as JWT and distributed session management.

---

## ⚙ Prerequisites

Before running the application, make sure the following are installed:

- Java 17+  
- Docker  
- Redis
- Postgre15

Redis is used (or planned to be used) to support distributed system design considerations such as caching and distributed locking.

---

## ▶ How to Run

### 1. Start Redis & postgres (using Docker)

```bash
docker run -d -p 6379:6379 --name budget-redis redis
docker run -d -p 5432:5432 --name budget-postgres postgres
./gradlew bootRun
```
## ☁ Cloud & Distributed System Considerations

To further enhance my cloud engineering capabilities and ensure data consistency in distributed environments, I am planning to introduce **Redisson** (Redis-based distributed toolkit).

The motivation behind this decision is:

- Strengthening cloud-native development skills  
- Ensuring data consistency in distributed environments  
- Supporting distributed locking mechanisms  
- Preparing the application for horizontal scaling  
- Improving understanding of multi-instance system behavior  

By introducing Redisson, the application will be better prepared for scalable cloud deployment scenarios, while also reinforcing my DevOps and distributed system design capabilities.
