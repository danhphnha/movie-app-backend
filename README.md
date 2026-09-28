# 🎬 Movie App Backend (RESTful API)

A high-performance **RESTful API Backend** built for the **Movie Mobile App**, powered by **Spring Boot 3**, **Google Cloud Firestore**, **Firebase Authentication**, and the **VNPay Payment Gateway** (Sandbox & Production).

---

## 🚀 Tech Stack & Architecture

* **Language & Framework**: Java 17, Spring Boot 3.5.0 (Spring Web, Spring Security, Validation, Actuator, DevTools).
* **Database**: Google Cloud Firestore (NoSQL Document Store).
* **Authentication & Authorization**:
  * Firebase Admin SDK (JWT Token Verification).
  * Role-Based Access Control (`ROLE_USER`, `ROLE_ADMIN`).
  * Stateless security preventing Broken Object Level Authorization (BOLA/IDOR).
* **Payment Integration**: VNPay Payment Gateway (HMAC-SHA512 checksums, Server-to-Server IPN Webhooks, Mobile Deep Links).
* **Build & Containerization**: Maven, Multi-stage Dockerfile (Non-root user execution, JVM memory tuning).

---

## 📂 Project Directory Structure

```text
src/main/java/org/example/mobilebackendjava/
├── config/                  # Firebase, Security, CORS configurations
│   ├── FirebaseConfig.java
│   └── SecurityConfig.java
├── controller/              # REST Controllers (Endpoints)
│   ├── FavoritesController.java
│   ├── PaymentController.java
│   ├── RevenueController.java
│   ├── ReviewController.java
│   └── HmacUtil.java
├── dto/                     # Data Transfer Objects & Validation rules
│   ├── ApiResponse.java
│   ├── CollectionCreateRequest.java
│   ├── CreateReviewRequest.java
│   ├── PaymentInitRequest.java
│   ├── ReplyRequest.java
│   └── UpdateReplyRequest.java
├── exception/               # Global Exception Handling
│   ├── AppException.java
│   ├── ForbiddenException.java
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── UnauthorizedException.java
├── model/                   # Entities & Firestore Models
│   ├── CollectionFilm.java
│   ├── Comment.java
│   ├── Movie.java
│   ├── Payment.java
│   └── PaymentResponse.java
├── security/                # Firebase Auth JWT Filter & Context Utilities
│   ├── FirebaseAuthenticationFilter.java
│   ├── SecurityUtils.java
│   └── UserPrincipal.java
└── service/                 # Business Logic Layer
    ├── CollectionFilmService.java
    ├── PaymentService.java
    ├── RevenueService.java
    └── ReviewService.java
```

---

## 🔑 Environment Variables

The application can be configured dynamically via environment variables to keep credentials and secrets secure for production:

| Variable Name | Description | Default / Fallback |
| :--- | :--- | :--- |
| `PORT` | HTTP Server Port | `8080` |
| `FIREBASE_CONFIG` | Base64-encoded string of the Firebase Service Account JSON | `(Reads local file if empty)` |
| `VNPAY_TMN_CODE` | VNPay Merchant Terminal Code | `4YUP19I4` (Sandbox) |
| `VNPAY_HASH_SECRET` | Secret key used for HMAC-SHA512 checksum calculation | `MDUIFDCRAKLNBPOFIAFNEKFRNMFBYEPX` |
| `VNPAY_PAY_URL` | VNPay Gateway Payment URL | `https://sandbox.vnpayment.vn/paymentv2/vpcpay.html` |
| `VNPAY_RETURN_URL` | Return URL redirected after customer checkout | `https://backendmobile-lqh7.onrender.com/api/vnpay-return` |

---

## 📡 REST API Documentation

All protected endpoints require a Bearer token in the request header:
```http
Authorization: Bearer <FIREBASE_ID_TOKEN>
```

### 1. Collections & Favorites
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/getCollectionsByUser` | `User` | Get all movie collections of the current authenticated user |
| `GET` | `/api/getFilmsByCollectionId?collectionId=...` | `User` | Get all movies within a collection (ownership verified) |
| `POST` | `/api/addCollection` | `User` | Create a new movie collection |
| `POST` | `/api/addFilmToCollection?collectionId=...` | `User` | Add a movie to a collection |
| `GET` | `/api/checkFilmInCollection?collectionId=...&slug=...` | `User` | Check if a movie is in a specific collection |
| `GET` | `/api/checkFilmInUserCollections?slug=...` | `User` | Check if a movie is in any collection of the user |
| `DELETE` | `/api/deleteFilmFromCollection?collectionId=...&slug=...` | `User` | Remove a movie from a collection |
| `DELETE` | `/api/deleteCollection?collectionId=...` | `User` | Delete an entire collection and its movie references |
| `GET` | `/api/getAllCollections` | `Admin` | View all collections system-wide |

---

### 2. Reviews & Threaded Comments
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/reviews/{slug}` | `Public` | Get all public reviews for a movie |
| `GET` | `/api/reviews/{slug}/average` | `Public` | Get average rating score and total review count |
| `POST` | `/api/reviews` | `User` | Submit a new review or update own existing review |
| `GET` | `/api/reviews/my-review/{slug}` | `User` | Get own review for a specific movie |
| `DELETE` | `/api/reviews/{reviewId}` | `User/Admin` | Delete a review (owner or Admin) |
| `POST` | `/api/reviews/reply` | `User` | Post a threaded reply to a comment (`parentId`) |
| `GET` | `/api/reviews/{commentId}/replies` | `Public` | Fetch replies for a specific comment |
| `PUT` | `/api/reviews/{reviewId}/reply` | `User` | Edit own reply |
| `PUT` | `/api/reviews/{reviewId}/hide` | `Admin` | Hide an inappropriate comment from public view |
| `PUT` | `/api/reviews/{reviewId}/show` | `Admin` | Restore a hidden comment |
| `GET` | `/api/reviews/all` | `Admin` | Get all comments for moderation |

---

### 3. VNPay Payments
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/pay` | `User` | Create a transaction and generate VNPay payment URL |
| `GET` | `/api/vnpay-ipn` | `Public` | Server-to-Server IPN Webhook from VNPay |
| `GET` | `/api/vnpay-return` | `Public` | Browser redirect URL returning Deep Link to Mobile App |

---

### 4. Revenue Reports
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/revenue` | `Admin` | Get complete transaction history |
| `GET` | `/api/revenue/total` | `Admin` | Compute total revenue from successful orders |

---

### 5. Health Check & Monitoring
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/actuator/health` | `Public` | Application health check status |
| `GET` | `/actuator/info` | `Public` | Application metadata and build information |

---

## 🛠️ Local Development & Setup

### 1. Prerequisites
* **Java 17 (JDK)**
* **Maven 3.8+** (or use included `./mvnw`)
* **Docker** (optional)

### 2. Configure Firebase Credentials
Place your `movieapp-f0c63-0f983a1aa75c.json` in `src/main/resources/` or set the `FIREBASE_CONFIG` environment variable:
```bash
# Linux / macOS
export FIREBASE_CONFIG=$(base64 -w 0 < path/to/serviceAccountKey.json)

# Windows (PowerShell)
$env:FIREBASE_CONFIG = [Convert]::ToBase64String([IO.File]::ReadAllBytes("path\to\serviceAccountKey.json"))
```

### 3. Build & Run Application
```bash
# Run using Maven Wrapper
./mvnw spring-boot:run

# Or package into an executable JAR and run
./mvnw clean package -DskipTests
java -jar target/MobileBackEndJava-0.0.1-SNAPSHOT.jar
```

---

## 🐳 Docker Deployment

### 1. Build the Docker Image
```bash
docker build -t movie-app-backend:latest .
```

### 2. Run the Container
```bash
docker run -d \
  -p 8080:8080 \
  -e PORT=8080 \
  -e FIREBASE_CONFIG="<YOUR_BASE64_FIREBASE_KEY>" \
  -e VNPAY_TMN_CODE="<YOUR_TMN_CODE>" \
  -e VNPAY_HASH_SECRET="<YOUR_HASH_SECRET>" \
  --name movie-backend \
  movie-app-backend:latest
```

---

## 📦 Standardized JSON Response Format

All responses are wrapped in a uniform `ApiResponse<T>` payload:

**Success Response:**
```json
{
  "success": true,
  "message": "Success",
  "data": { ... },
  "timestamp": "2026-09-28T04:00:00Z"
}
```

**Error Response:**
```json
{
  "success": false,
  "message": "You do not have permission to access this resource",
  "data": null,
  "timestamp": "2026-09-28T04:00:00Z"
}
```
