# 🌿 EcoLogic B2B — AI-Powered Sustainability Dashboard

A full-stack enterprise application that leverages **Gemini 2.5 Flash AI** to streamline sustainable product onboarding and generate budget-optimized B2B procurement proposals.

Built with **Spring Boot** (Backend), **React + Vite** (Frontend), and **MySQL** (Database).

---

## 🏗️ Architecture Overview

```
┌─────────────────────┐       ┌──────────────────────────────┐       ┌───────────────────┐
│   React Frontend    │──────▶│   Spring Boot REST API       │──────▶│   MySQL Database   │
│   (Vite, Port 5173) │◀──────│   (Port 8080)                │◀──────│   (proposal_db)    │
└─────────────────────┘       │                              │       └───────────────────┘
                              │   ┌────────────────────────┐ │
                              │   │  Gemini 2.5 Flash API  │ │
                              │   └────────────────────────┘ │
                              └──────────────────────────────┘
```

### Backend (Spring Boot 3.2)
- **Controller Layer**: RESTful endpoints for product analysis, proposal generation, and admin log viewing.
- **Service Layer**: Core business logic including AI prompt engineering and validation.
- **Data Access Layer**: Spring Data JPA repositories for MySQL interaction.
- **CORS Configuration**: Allows cross-origin requests from the Vite dev server.

### Frontend (React 19 + Vite)
- **Design Philosophy**: "The Great Unclutter" — minimalist, data-forward UI with progressive disclosure.
- **Aesthetic**: Dark mode, glassmorphism effects, smooth micro-animations.
- **Pages**: Product Onboarding, Smart Proposal Generator, Admin Logs Viewer.

---

## ✨ Core Features

### Module 1: AI Product Onboarding
- Input a messy product description (e.g., *"Handmade bag from recycled ocean plastic"*).
- Gemini AI extracts: **Category**, **SEO Tags**, **Sustainability Badges**, and **Inferred B2B Price**.
- Review and save the enriched product data directly to the inventory database.

### Module 2: Smart B2B Proposal Generator
- Enter a target procurement budget.
- AI selects an optimal mix of sustainable products from the database.
- **Math Guard Validation**: Java backend independently recalculates the total cost using real database prices to ensure the AI's allocation never exceeds the budget.
- Generates an **Impact Summary** explaining sustainability benefits.

### Admin Log Viewer
- Full audit trail of every AI prompt and response.
- Timestamps and module identification for compliance and debugging.

---

## 🧠 AI Prompt Engineering

### Product Onboarding Prompt
Uses **Strict System Instructions** to force structured JSON output:
- Role: *"AI Sustainability Product Categorizer"*
- Output: Exact JSON schema with `category`, `seo_tags`, `sustainability_badges`, `inferred_price`
- Guard: Explicit instruction to avoid markdown wrapping

### Proposal Generator Prompt
Uses **Few-Shot Prompting with Context Grounding**:
- Dynamic injection of real product data (ID, name, price, stock, sustainability score) from the database
- Budget constraint embedded in the system instruction
- Strict JSON output schema with `recommended_mix`, `total_allocation`, `remaining_budget`, `impact_summary`

---

## 🛠️ Tech Stack

| Layer      | Technology                          |
|------------|-------------------------------------|
| Frontend   | React 19, Vite, Axios, Lucide React |
| Backend    | Spring Boot 3.2, Spring Data JPA    |
| Database   | MySQL 8.x                           |
| AI Engine  | Google Gemini 2.5 Flash API         |
| Build Tool | Maven                               |

---

## 🚀 Getting Started

### Prerequisites
- **Java 17+** (JDK)
- **Node.js 18+** and **npm**
- **MySQL 8.x** running on `localhost:3306`
- **Gemini API Key** from [Google AI Studio](https://aistudio.google.com/apikey)

### 1. Database Setup
```sql
-- MySQL will auto-create the database, but ensure the server is running:
-- Username: root | Password: root (configurable in application.properties)
```

### 2. Backend Setup
```bash
# Set your Gemini API key as an environment variable
# Windows (PowerShell):
$env:GEMINI_API_KEY = "your_gemini_api_key_here"

# Linux / macOS:
export GEMINI_API_KEY=your_gemini_api_key_here

# Run the Spring Boot application
mvn spring-boot:run
```
The `DataSeeder` will automatically populate 5 sample sustainable products on first launch.

### 3. Frontend Setup
```bash
cd frontend
npm install
npm run dev
```
Navigate to `http://localhost:5173` in your browser.

---

## 📁 Project Structure

```
Rayeva_2/
├── src/main/java/com/ai/proposalgenerator/
│   ├── config/
│   │   ├── CorsConfig.java          # CORS configuration
│   │   └── DataSeeder.java          # Auto-populates sample products
│   ├── controller/
│   │   ├── ProductController.java   # Product analysis & save endpoints
│   │   ├── ProposalController.java  # Proposal generation endpoint
│   │   └── AdminLogController.java  # AI log retrieval endpoint
│   ├── dto/
│   │   ├── ProductCategorizationResponse.java
│   │   └── AiProposalResponse.java
│   ├── model/
│   │   ├── Product.java             # JPA entity for products table
│   │   ├── Proposal.java            # JPA entity for proposals table
│   │   └── AiLog.java               # JPA entity for ai_logs table
│   ├── repository/
│   │   ├── ProductRepository.java
│   │   ├── ProposalRepository.java
│   │   └── AiLogRepository.java
│   ├── service/
│   │   ├── ProductOnboardingService.java  # Module 1 AI logic
│   │   └── ProposalService.java           # Module 2 AI + Math Guard
│   └── ProposalGeneratorApplication.java
├── src/main/resources/
│   └── application.properties       # DB & API configuration
├── frontend/
│   ├── src/
│   │   ├── pages/
│   │   │   ├── OnboardProduct.jsx   # Module 1 UI
│   │   │   ├── GenerateProposal.jsx # Module 2 UI
│   │   │   └── AdminLogs.jsx        # Admin Logs UI
│   │   ├── App.jsx                  # Router & Layout
│   │   └── index.css                # Global dark-theme styling
│   └── package.json
├── pom.xml
└── README.md
```

---

## 🔒 Security Notes

- The Gemini API key is **not** stored in the codebase. It must be set as the `GEMINI_API_KEY` environment variable before running the application.
- Database credentials in `application.properties` are defaults for local development. Update them for production deployments.

---

## 📊 Database Schema

### `products`
| Column               | Type    | Description                        |
|----------------------|---------|------------------------------------|
| id                   | BIGINT  | Auto-generated primary key         |
| name                 | VARCHAR | Product name                       |
| price_per_unit       | DOUBLE  | B2B price per unit                 |
| stock_quantity       | INT     | Available inventory count          |
| sustainability_score | INT     | Sustainability rating (1-10)       |

### `proposals`
| Column           | Type    | Description                            |
|------------------|---------|----------------------------------------|
| id               | BIGINT  | Auto-generated primary key             |
| total_budget     | DOUBLE  | User-specified budget ceiling          |
| final_cost       | DOUBLE  | Math Guard-validated actual cost       |
| proposal_summary | TEXT    | Full AI-generated proposal JSON        |

### `ai_logs`
| Column      | Type      | Description                          |
|-------------|-----------|--------------------------------------|
| id          | BIGINT    | Auto-generated primary key           |
| module_name | VARCHAR   | Source module (Module1 or Module2)    |
| prompt      | TEXT      | Full prompt sent to Gemini           |
| response    | TEXT      | Raw AI response                      |
| timestamp   | TIMESTAMP | Auto-generated creation time         |

---

## 📝 API Endpoints

| Method | Endpoint                            | Description                          |
|--------|-------------------------------------|--------------------------------------|
| POST   | `/api/products/analyze`             | Analyze a product description via AI |
| POST   | `/api/products/save`                | Save a product to the database       |
| POST   | `/api/proposals/generate?budget=N`  | Generate a budget-optimized proposal |
| GET    | `/api/admin/logs`                   | Retrieve all AI interaction logs     |