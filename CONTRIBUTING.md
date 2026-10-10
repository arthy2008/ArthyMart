# Contributing to ArthyMart

Thank you for contributing to **ArthyMart**! This guide details the setup and contribution workflow for Anna University Capstone evaluations and future project extensions.

---

## 1. Local Setup from Git Clone

### Prerequisites
- **JDK 17** (OpenJDK or Oracle JDK)
- **Apache Maven 3.8+**
- **Apache Tomcat 9.0.x** (Servlet 4.0 / `javax.servlet.*`)
- **Git**

### Setup Steps
1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/ArthyMart.git
   cd ArthyMart
   ```

2. **Configure Environment:**
   Copy `.env.example` to `.env`:
   ```bash
   cp .env.example .env
   ```
   *Note: `.env` is gitignored to avoid committing sensitive information.*

3. **Build and Run Verification Suite:**
   ```bash
   mvn -B clean verify
   ```
   This compiles all code, runs all unit and integration tests (H2 in-memory), and performs Checkstyle and SpotBugs audits.

4. **Deploy to Apache Tomcat 9:**
   - Copy `target/ArthyMart.war` to your Tomcat `webapps/` folder:
     ```bash
     cp target/ArthyMart.war $TOMCAT_HOME/webapps/
     ```
   - Start Tomcat:
     ```bash
     $TOMCAT_HOME/bin/startup.sh   # Linux / macOS
     $TOMCAT_HOME\bin\startup.bat  # Windows
     ```
   - Access the application at: `http://localhost:8080/ArthyMart/`

---

## 2. Seed Accounts for Testing

| Role | Email | Password |
|:---|:---|:---|
| **Admin** | `admin@arthymart.com` | `password123` |
| **Seller** | `seller@arthymart.com` | `password123` |
| **Buyer** | `buyer@arthymart.com` | `password123` |

---

## 3. Branching & Commit Conventions

- Work in feature branches: `feature/<feature-name>`
- Conventional commit message prefixes:
  - `feat:` for new capabilities
  - `fix:` for bug fixes
  - `test:` for unit/integration tests
  - `docs:` for documentation updates
- Every feature must satisfy the **Definition of Done**:
  - Compiles clean (`mvn clean verify`).
  - Zero Checkstyle and SpotBugs errors.
  - Tests written and passing.
