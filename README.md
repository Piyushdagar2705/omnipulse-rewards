# Multi-Channel Customer Rewards & Feedback App

An omnichannel customer engagement platform powered by a unified **Java Spring Boot REST API** that serves **Web (React)**, **Mobile (React Native for iOS & Android)**, and **Social Messaging (WhatsApp Business Cloud API)** channels, complete with **OAuth2 Social Login**.

Customers can sign in, submit feedback from any channel, and earn reward points tracked in real time against a single centralized MySQL database.

---

## Architecture

```text
+-------------------+     +-----------------------+     +------------------------+
|    Web Client     |     |     Mobile Client     |     |   WhatsApp Channel     |
|   (React.js)      |     | (React Native / Expo) |     | (Meta Cloud Webhook)   |
|  localhost:3000   |     |    iOS & Android      |     |    via ngrok tunnel    |
+---------+---------+     +-----------+-----------+     +-----------+------------+
          |                           |                             |
          +---------------------------+-----------------------------+
                                      |
                               HTTP / REST API
                                      v
                 +-----------------------------------------+
                 |       Java Spring Boot Backend          |
                 |             (Port 8080)                 |
                 |  - REST APIs (/api/feedback, /rewards)  |
                 |  - OAuth2 Social Login (Google/GitHub)  |
                 |  - WhatsApp Webhook (/webhook)          |
                 +--------------------+--------------------+
                                      |
                                 Spring Data JPA
                                      v
                 +-----------------------------------------+
                 |        MySQL 8.0 (Docker)               |
                 |      Port 3307 -> 3306 (spring_social)  |
                 +-----------------------------------------+
```

---

## Key Features

* **Unified Java Backend:** A single Spring Boot REST API manages authentication, customer feedback ingestion, and reward points calculation across all channels.
* **Web Portal (`web-client`):** React-based customer interface supporting OAuth2 social login and feedback submission.
* **Cross-Platform Mobile App (`mobile-client`):** React Native (Expo) client for iOS and Android consuming the same backend APIs.
* **Social Integration:**
  * **OAuth2 Social Login:** Configured with Spring Security OAuth2 for Google, Facebook, and GitHub authentication.
  * **WhatsApp Business Webhook:** Accepts Meta verification handshakes (`GET /webhook`) and logs incoming customer messages (`POST /webhook`) directly as reward-eligible feedback.
* **Automated Reward Points:** Every feedback submission across `WEB`, `MOBILE`, or `WHATSAPP` automatically credits **50 reward points** to the customer's account.

---

## Tech Stack

| Layer | Technologies |
| :--- | :--- |
| **Backend & APIs** | Java, Spring Boot, Spring Security (OAuth2 + JWT), Spring Data JPA, Hibernate, Maven |
| **Web Frontend** | React.js, JavaScript, HTML5, CSS3 |
| **Mobile Frontend** | React Native, Expo |
| **Social & Tunneling** | OAuth2 Social Login, Meta WhatsApp Business Cloud API Webhook, ngrok |
| **Database & DevOps** | MySQL 8.0, Docker |

---

## Project Structure

```text
Multi-Channel-Customer-Rewards-Feedback-App/
├── backend/                     # Java Spring Boot REST API & WhatsApp Webhook
│   ├── src/main/java/com/example/springsocial/
│   │   ├── config/              # SecurityConfig (OAuth2, CORS, route permissions)
│   │   ├── controller/          # EngagementController, WhatsAppWebhookController, AuthController
│   │   ├── model/               # User, Feedback JPA entities
│   │   └── repository/          # UserRepository, FeedbackRepository
│   └── src/main/resources/
│       └── application.yml      # MySQL & OAuth2 configuration
├── web-client/                  # React web application (Port 3000)
├── mobile-client/               # React Native (Expo) mobile application
└── README.md
```

---

## API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/feedback` | Submit customer feedback (`customerEmail`, `channel`, `message`) and award 50 points |
| `GET` | `/api/rewards/{email}` | Retrieve total reward points and submission history for a customer |
| `GET` | `/webhook` | Meta WhatsApp webhook verification handshake (`hub.mode`, `hub.verify_token`, `hub.challenge`) |
| `POST` | `/webhook` | Receive incoming WhatsApp messages and persist them under the `WHATSAPP` channel |
| `POST` | `/auth/login` | Standard email/password login returning a JWT |
| `GET` | `/oauth2/authorize/{provider}` | Initiate OAuth2 social login (`google`, `github`, `facebook`) |

---

## Prerequisites

* **Java JDK** (17+) & **Apache Maven**
* **Node.js** (v18+) & **npm**
* **Docker Desktop**
* **ngrok** (authenticated with your ngrok authtoken for WhatsApp webhook testing)
* **Expo Go** app on your iOS/Android device (or an Android Emulator)

---

## Getting Started (Local Setup)

### 1. Start the MySQL Database (Docker)
Run MySQL 8.0 on host port `3307` (mapped to container port `3306` to avoid conflicts with any local MySQL installation):

```powershell
docker run --name omnichannel-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=spring_social -p 3307:3306 -d mysql:8.0
```

Verify `backend/src/main/resources/application.yml` points to port `3307`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3307/spring_social?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useLegacyDatetimeCode=false
    username: root
    password: root
```

### 2. Run the Java Spring Boot Backend (Port 8080)
```powershell
cd backend
mvn spring-boot:run
```

### 3. Run the React Web Client (Port 3000)
Open a second terminal:
```powershell
cd web-client
npm install
$env:NODE_OPTIONS="--openssl-legacy-provider"
npm start
```
Open `http://localhost:3000` in your browser.

### 4. Run the React Native Mobile Client
Open a third terminal:
```powershell
cd mobile-client
npm install
npx expo start
```
Scan the QR code using **Expo Go** on your phone (or press `a` for Android Emulator).
> **Note:** Update the mobile API base URL to your machine's local IPv4 address (found via `ipconfig`), e.g., `http://<YOUR_IPV4>:8080`, since mobile devices cannot reach `localhost`.

### 5. Expose the WhatsApp Webhook via ngrok
Open a fourth terminal:
```powershell
ngrok http 8080
```
Use the generated `https://<your-id>.ngrok-free.app/webhook` URL and verify token `omnichannel_secret_token` in your Meta Developer Dashboard.

---

## Quick Verification Tests

**Test WhatsApp Webhook Verification:**
```powershell
Invoke-RestMethod "http://localhost:8080/webhook?hub.mode=subscribe&hub.verify_token=omnichannel_secret_token&hub.challenge=TEST_OK"
# Expected output: TEST_OK
```

**Submit Feedback via REST API:**
```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/feedback" -ContentType "application/json" -Body '{"customerEmail":"user@example.com","channel":"WEB","message":"Great rewards experience!"}'
```

**Check Customer Reward Points:**
```powershell
Invoke-RestMethod "http://localhost:8080/api/rewards/user@example.com"
```

---

## Author

**Piyush Dagar**
* GitHub: [github.com/Piyushdagar2705](https://github.com/Piyushdagar2705)
* LinkedIn: [linkedin.com/in/piyushdagar27](https://linkedin.com/in/piyushdagar27)