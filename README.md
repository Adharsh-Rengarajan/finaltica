# Finaltica - Smart Expense and Investment Management System

Finaltica is a professional-grade, N-tier personal finance application designed to centralize the management of liquid cash, credit liabilities, and long-term investments. By implementing a sophisticated digital ledger system, the platform bridges the gap between simple manual tracking and professional wealth management, providing users with a comprehensive view of their net worth.

## Architecture Overview

```mermaid
graph TB
    subgraph "Client Layer"
        A[React Frontend<br/>TypeScript + Vite]
        A1[Landing Page]
        A2[Authentication]
        A3[Dashboard]
        A4[Accounts]
        A5[Transactions]
        A6[Categories]
        A7[Investments]
        A8[Reports]
    end

    subgraph "CDN & Hosting"
        B[Vercel<br/>Frontend Hosting]
    end

    subgraph "API Gateway"
        C[Spring Boot Backend<br/>Java 17]
        C1[REST Controllers]
        C2[JWT Authentication Filter]
        C3[CORS Configuration]
    end

    subgraph "Business Logic Layer"
        D[Service Layer]
        D1[Auth Service]
        D2[Account Service]
        D3[Transaction Service]
        D4[Analytics Service]
        D5[Report Service]
    end

    subgraph "Data Access Layer"
        E[Spring Data JPA]
        E1[User Repository]
        E2[Account Repository]
        E3[Transaction Repository]
        E4[Category Repository]
    end

    subgraph "Database"
        F[(Supabase PostgreSQL<br/>Managed Database)]
        F1[Users Table]
        F2[Accounts Table]
        F3[Transactions Table]
        F4[Categories Table]
        F5[Investment Metadata]
    end

    subgraph "External Services"
        G[AWS S3<br/>PDF Report Storage]
        H[GitHub Actions<br/>CI/CD Pipeline]
    end

    subgraph "Container Platform"
        I[Render<br/>Docker Container Hosting]
    end

    A --> B
    B --> C
    A -.->|HTTPS/JSON| C
    
    C --> C1
    C1 --> C2
    C2 --> C3
    C3 --> D
    
    D --> D1
    D --> D2
    D --> D3
    D --> D4
    D --> D5
    
    D --> E
    E --> E1
    E --> E2
    E --> E3
    E --> E4
    
    E --> F
    F --> F1
    F --> F2
    F --> F3
    F --> F4
    F --> F5
    
    D5 --> G
    
    C -.->|Deployed via| I
    A -.->|Deployed via| B
    
    H -.->|Builds & Deploys| I
    H -.->|Builds & Deploys| B

    style A fill:#dbeafe,stroke:#1e40af,stroke-width:2px
    style C fill:#dcfce7,stroke:#166534,stroke-width:2px
    style F fill:#fef3c7,stroke:#d97706,stroke-width:2px
    style G fill:#fee2e2,stroke:#991b1b,stroke-width:2px
    style I fill:#e9d5ff,stroke:#6b21a8,stroke-width:2px
    style B fill:#e9d5ff,stroke:#6b21a8,stroke-width:2px
```

## System Architecture Details

### Frontend Architecture
```mermaid
graph LR
    subgraph "React Application"
        A[App.tsx<br/>Router]
        B[Auth Context<br/>Global State]
        C[Protected Routes]
        D[Public Routes]
        E[API Config<br/>Axios Interceptors]
    end

    subgraph "Page Components"
        P1[Landing]
        P2[Login/Signup]
        P3[Dashboard]
        P4[Accounts]
        P5[Transactions]
        P6[Categories]
        P7[Investments]
        P8[Reports]
    end

    subgraph "Shared Components"
        S1[Header]
        S2[Sidebar]
        S3[Modals]
        S4[Charts]
        S5[Tables]
    end

    A --> B
    A --> C
    A --> D
    B --> E
    C --> P3
    C --> P4
    C --> P5
    C --> P6
    C --> P7
    C --> P8
    D --> P1
    D --> P2
    
    P3 --> S1
    P3 --> S2
    P3 --> S4
    P4 --> S3
    P5 --> S5
```

### Backend Architecture
```mermaid
graph TB
    subgraph "Security Layer"
        A[JWT Authentication Filter]
        B[Spring Security Config]
        C[CORS Configuration]
    end

    subgraph "Controller Layer"
        D1[Auth Controller]
        D2[Account Controller]
        D3[Transaction Controller]
        D4[Category Controller]
        D5[Analytics Controller]
        D6[Report Controller]
    end

    subgraph "Service Layer"
        E1[Auth Service]
        E2[Account Service]
        E3[Transaction Service]
        E4[Category Service]
        E5[Analytics Service]
        E6[Report Service]
        E7[PDF Service]
        E8[S3 Service]
    end

    subgraph "Repository Layer"
        F1[User Repository]
        F2[Account Repository]
        F3[Transaction Repository]
        F4[Category Repository]
        F5[Investment Repository]
    end

    subgraph "Entity Layer"
        G1[User Entity]
        G2[Account Entity]
        G3[Transaction Entity]
        G4[Category Entity]
        G5[Investment Metadata]
    end

    A --> B
    B --> C
    C --> D1
    C --> D2
    C --> D3
    C --> D4
    C --> D5
    C --> D6

    D1 --> E1
    D2 --> E2
    D3 --> E3
    D4 --> E4
    D5 --> E5
    D6 --> E6

    E6 --> E7
    E6 --> E8

    E1 --> F1
    E2 --> F2
    E3 --> F3
    E4 --> F4
    E5 --> F2
    E5 --> F3

    F1 --> G1
    F2 --> G2
    F3 --> G3
    F4 --> G4
    F3 --> G5
```

### Database Schema
```mermaid
erDiagram
    USERS ||--o{ ACCOUNTS : owns
    USERS ||--o{ CATEGORIES : creates
    ACCOUNTS ||--o{ TRANSACTIONS : contains
    CATEGORIES ||--o{ TRANSACTIONS : categorizes
    TRANSACTIONS ||--o| INVESTMENT_METADATA : has
    TRANSACTIONS ||--o| TRANSACTIONS : relates_to

    USERS {
        uuid id PK
        varchar email UK
        varchar password_hash
        varchar first_name
        varchar last_name
        timestamptz created_at
        timestamptz updated_at
    }

    ACCOUNTS {
        uuid id PK
        uuid user_id FK
        varchar name
        varchar type
        numeric current_balance
        varchar currency
        timestamptz created_at
        timestamptz updated_at
    }

    CATEGORIES {
        uuid id PK
        varchar name
        varchar type
        uuid user_id FK "NULL for global"
        timestamptz created_at
        timestamptz updated_at
    }

    TRANSACTIONS {
        uuid id PK
        uuid account_id FK
        uuid category_id FK
        uuid related_transaction_id FK
        numeric amount
        varchar type
        varchar description
        timestamptz transaction_date
        varchar payment_mode
        timestamptz created_at
        timestamptz updated_at
    }

    INVESTMENT_METADATA {
        uuid transaction_id PK,FK
        varchar asset_symbol
        varchar asset_type
        numeric quantity
        numeric price_per_unit
        timestamptz created_at
        timestamptz updated_at
    }
```

## Features

### User Authentication and Security
- Secure registration and login using Spring Security.
- Stateless authentication powered by JSON Web Tokens (JWT).
- Role-based access control to ensure data isolation and privacy.

### Multipurpose Account Management
- Support for multiple virtual account types including Checking, Savings, and Credit Cards.
- Distinct logic for credit vs. debit: credit transactions increase liabilities while debit transactions decrease assets.
- Real-time balance synchronization across all linked accounts.

### Transaction Engine
- Detailed logging for three primary categories: Income, Expenses, and Investments.
- Automated updates to account balances upon transaction confirmation.
- Categorization and metadata tagging (Source, Method, Merchant, and Mode) for advanced filtering.

### Investment and Wealth Tracking
- Asset management for stocks, mutual funds, and fixed deposits.
- ROI (Return on Investment) calculation logic based on purchase price vs. current market value.
- Portfolio visualization to track long-term growth and asset allocation.

### Intelligence and Reporting
- Automated generation of monthly financial health reports in PDF format.
- Rule-based advisory engine that analyzes spending patterns to offer budget optimizations.
- Cloud-based storage for historical report retrieval.

## Tech Stack

### Frontend
- **React 18 with TypeScript**: Type-safe, component-based UI architecture
- **Vite**: Fast build tool and development server
- **React Router v6**: Client-side routing and navigation
- **Recharts**: Interactive financial data visualization
- **Axios**: HTTP client with request/response interceptors
- **Lucide React**: Modern icon library
- **CSS Modules**: Scoped styling with design system

### Backend
- **Java 17 & Spring Boot 4.0.1**: The core framework for handling business logic and RESTful API services.
- **Spring Security**: To manage authentication and JWT-based authorization.
- **Spring Data JPA**: For streamlined database interaction and ORM mapping.
- **Hibernate 7.2**: Advanced ORM with PostgreSQL dialect support
- **JWT (jjwt 0.12.5)**: Secure token-based authentication
- **iText7**: PDF generation for financial reports
- **AWS SDK**: S3 integration for report storage
- **Lombok**: Reduced boilerplate code

### Data and Storage
- **PostgreSQL 13**: A relational database ensuring ACID compliance for financial integrity.
- **AWS S3**: For scalable storage of generated PDF financial statements.
- **Supabase**: Utilized as a managed Backend-as-a-Service (BaaS) platform to provide the primary PostgreSQL instance, real-time database subscriptions for live dashboard updates, and integrated authentication metadata.

### Deployment and Infrastructure
- **Vercel**: Hosting platform for the React frontend with automatic deployments.
- **Render**: Docker-based hosting for the Spring Boot backend service.
- **GitHub Actions**: For CI/CD automation and deployment pipelines.
- **Docker**: Containerized backend for consistent deployment environments.

## Data Flow

```mermaid
sequenceDiagram
    participant U as User
    participant F as Frontend (React)
    participant V as Vercel CDN
    participant R as Render (Backend)
    participant S as Spring Boot
    participant DB as Supabase PostgreSQL
    participant S3 as AWS S3

    U->>F: Access Application
    F->>V: Request Static Assets
    V-->>F: Serve React App
    
    U->>F: Login
    F->>R: POST /api/auth/login
    R->>S: Authenticate User
    S->>DB: Query Users Table
    DB-->>S: User Data
    S-->>R: Generate JWT Token
    R-->>F: Return Token + User Info
    F->>F: Store Token in LocalStorage
    
    U->>F: Create Account
    F->>R: POST /api/accounts (with JWT)
    R->>S: Validate JWT
    S->>DB: Insert Account
    DB-->>S: Account Created
    S-->>R: Success Response
    R-->>F: Account Data
    F->>F: Update UI
    
    U->>F: Add Transaction
    F->>R: POST /api/transactions (with JWT)
    R->>S: Process Transaction
    S->>DB: Begin Transaction
    DB->>DB: Insert Transaction
    DB->>DB: Update Account Balance
    DB-->>S: Transaction Complete
    S-->>R: Success Response
    R-->>F: Transaction Data
    F->>F: Refresh Dashboard
    
    U->>F: Generate Report
    F->>R: GET /api/reports/monthly
    R->>S: Fetch Data & Generate PDF
    S->>DB: Query Transactions
    DB-->>S: Transaction Data
    S->>S: Generate PDF with iText7
    S->>S3: Upload PDF
    S3-->>S: Pre-signed URL
    S-->>R: Download Link
    R-->>F: Report URL
    F-->>U: Download PDF
```

## Security Architecture

```mermaid
graph TB
    subgraph "Request Flow"
        A[Client Request] --> B{JWT Token Present?}
        B -->|No| C[Return 401 Unauthorized]
        B -->|Yes| D[JWT Authentication Filter]
        D --> E{Token Valid?}
        E -->|No| C
        E -->|Yes| F[Extract User ID from Token]
        F --> G[Load User from Database]
        G --> H{User Found?}
        H -->|No| C
        H -->|Yes| I[Set Authentication Context]
        I --> J[Spring Security Filter Chain]
        J --> K{Endpoint Authorized?}
        K -->|No| L[Return 403 Forbidden]
        K -->|Yes| M[Process Request]
        M --> N[Service Layer]
        N --> O[Repository Layer]
        O --> P[(Database)]
        P --> O
        O --> N
        N --> M
        M --> Q[Return Response]
    end

    style B fill:#fef3c7,stroke:#d97706
    style E fill:#fef3c7,stroke:#d97706
    style H fill:#fef3c7,stroke:#d97706
    style K fill:#fef3c7,stroke:#d97706
    style C fill:#fee2e2,stroke:#991b1b
    style L fill:#fee2e2,stroke:#991b1b
    style Q fill:#dcfce7,stroke:#166534
```

## Deployment Architecture

```mermaid
graph TB
    subgraph "Development"
        A[Developer] -->|git push| B[GitHub Repository]
    end

    subgraph "CI/CD Pipeline"
        B --> C[GitHub Actions]
        C --> D{Build & Test}
        D -->|Frontend| E[Build React App]
        D -->|Backend| F[Build Docker Image]
    end

    subgraph "Production Deployment"
        E --> G[Vercel]
        F --> H[Render]
        
        G --> I[Frontend Instance<br/>React SPA]
        H --> J[Backend Container<br/>Spring Boot + Docker]
    end

    subgraph "Infrastructure"
        J --> K[(Supabase PostgreSQL)]
        J --> L[AWS S3<br/>Report Storage]
        I -.->|API Calls| J
    end

    subgraph "Monitoring"
        I --> M[Vercel Analytics]
        J --> N[Render Logs]
        K --> O[Supabase Monitoring]
    end

    style G fill:#e9d5ff,stroke:#6b21a8
    style H fill:#e9d5ff,stroke:#6b21a8
    style K fill:#fef3c7,stroke:#d97706
    style L fill:#fee2e2,stroke:#991b1b
```

## Features

### User Authentication and Security
- Secure registration and login using Spring Security.
- Stateless authentication powered by JSON Web Tokens (JWT).
- Role-based access control to ensure data isolation and privacy.

### Multipurpose Account Management
- Support for multiple virtual account types including Checking, Savings, and Credit Cards.
- Distinct logic for credit vs. debit: credit transactions increase liabilities while debit transactions decrease assets.
- Real-time balance synchronization across all linked accounts.

### Transaction Engine
- Detailed logging for three primary categories: Income, Expenses, and Investments.
- Automated updates to account balances upon transaction confirmation.
- Categorization and metadata tagging (Source, Method, Merchant, and Mode) for advanced filtering.

### Investment and Wealth Tracking
- Asset management for stocks, mutual funds, and fixed deposits.
- ROI (Return on Investment) calculation logic based on purchase price vs. current market value.
- Portfolio visualization to track long-term growth and asset allocation.

### Intelligence and Reporting
- Automated generation of monthly financial health reports in PDF format.
- Rule-based advisory engine that analyzes spending patterns to offer budget optimizations.
- Cloud-based storage for historical report retrieval.

## Tech Stack

### Frontend
- **React 18 with TypeScript**: Type-safe, component-based UI architecture
- **Vite**: Fast build tool and development server
- **React Router v6**: Client-side routing and navigation
- **Recharts**: Interactive financial data visualization
- **Axios**: HTTP client with request/response interceptors
- **Lucide React**: Modern icon library
- **CSS Modules**: Scoped styling with design system

### Backend
- **Java 17 & Spring Boot 4.0.1**: The core framework for handling business logic and RESTful API services.
- **Spring Security**: To manage authentication and JWT-based authorization.
- **Spring Data JPA**: For streamlined database interaction and ORM mapping.
- **Hibernate 7.2**: Advanced ORM with PostgreSQL dialect support
- **JWT (jjwt 0.12.5)**: Secure token-based authentication
- **iText7**: PDF generation for financial reports
- **AWS SDK**: S3 integration for report storage
- **Lombok**: Reduced boilerplate code

### Data and Storage
- **PostgreSQL 13**: A relational database ensuring ACID compliance for financial integrity.
- **AWS S3**: For scalable storage of generated PDF financial statements.
- **Supabase**: Utilized as a managed Backend-as-a-Service (BaaS) platform to provide the primary PostgreSQL instance, real-time database subscriptions for live dashboard updates, and integrated authentication metadata.

### Deployment and Infrastructure
- **Vercel**: Hosting platform for the React frontend with automatic deployments.
- **Render**: Docker-based hosting for the Spring Boot backend service.
- **GitHub Actions**: For CI/CD automation and deployment pipelines.
- **Docker**: Containerized backend for consistent deployment environments.

## API Endpoints

### Authentication
- `POST /api/auth/signup` - User registration
- `POST /api/auth/login` - User authentication

### Accounts
- `GET /api/accounts` - Get all user accounts
- `GET /api/accounts?type={type}` - Filter accounts by type
- `GET /api/accounts/{id}` - Get specific account
- `POST /api/accounts` - Create new account
- `PUT /api/accounts/{id}` - Update account
- `DELETE /api/accounts/{id}` - Delete account

### Transactions
- `GET /api/transactions` - Get all transactions
- `GET /api/transactions?accountId={id}` - Filter by account
- `GET /api/transactions?categoryId={id}` - Filter by category
- `GET /api/transactions?type={type}` - Filter by type
- `GET /api/transactions?startDate={date}&endDate={date}` - Filter by date range
- `POST /api/transactions` - Create transaction
- `POST /api/transactions/transfer` - Create transfer
- `POST /api/transactions/investment` - Create investment
- `DELETE /api/transactions/{id}` - Delete transaction

### Categories
- `GET /api/categories` - Get all categories (global + user)
- `GET /api/categories?type={type}` - Filter by type
- `POST /api/categories` - Create custom category
- `PUT /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category

### Analytics
- `GET /api/analytics/networth` - Calculate net worth
- `GET /api/analytics/monthly-summary?year={year}&month={month}` - Monthly summary
- `GET /api/analytics/category-spending?startDate={date}&endDate={date}` - Category breakdown

### Reports
- `GET /api/reports/monthly?year={year}&month={month}` - Generate monthly PDF
- `GET /api/reports/custom?startDate={date}&endDate={date}` - Generate custom PDF

## Getting Started

### Prerequisites
- Node.js 18+
- Java 17+
- Maven 3.9+
- Docker (optional)
- PostgreSQL 13+ (or Supabase account)
- AWS Account (for S3)

### Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Create .env file
cat > .env << EOF
VITE_API_BASE_URL=https://your-backend-url.com
EOF

# Run development server
npm run dev

# Build for production
npm run build
```

### Backend Setup

```bash
cd backend

# Create .env file with database credentials
cat > .env << EOF
DB_URL=jdbc:postgresql://your-supabase-url/postgres
DB_USERNAME=postgres
DB_PASSWORD=your-password
JWT_SECRET=your-secret-key-at-least-256-bits
AWS_ACCESS_KEY_ID=your-key
AWS_SECRET_ACCESS_KEY=your-secret
AWS_S3_BUCKET_NAME=your-bucket
AWS_REGION=us-east-1
SERVER_PORT=8080
EOF

# Run with Maven
./mvnw spring-boot:run

# Or build Docker image
docker build -t finaltica-backend .
docker run -p 8080:8080 finaltica-backend
```

## Project Structure

```
finaltica/
├── frontend/
│   ├── src/
│   │   ├── components/      # Reusable UI components
│   │   ├── pages/           # Page components
│   │   ├── context/         # React context (auth)
│   │   ├── config/          # API configuration
│   │   ├── utils/           # Utility functions
│   │   ├── typings/         # TypeScript types
│   │   ├── styles/          # CSS modules
│   │   ├── App.tsx
│   │   └── main.tsx
│   ├── public/
│   ├── package.json
│   ├── vite.config.ts
│   └── tsconfig.json
│
└── backend/
    ├── src/
    │   ├── main/
    │   │   ├── java/com/finaltica/application/
    │   │   │   ├── config/          # Security, CORS, AWS
    │   │   │   ├── controller/      # REST endpoints
    │   │   │   ├── service/         # Business logic
    │   │   │   ├── repository/      # Data access
    │   │   │   ├── entity/          # JPA entities
    │   │   │   ├── dto/             # Data transfer objects
    │   │   │   ├── enums/           # Enumerations
    │   │   │   ├── filter/          # JWT filter
    │   │   │   ├── util/            # Utilities
    │   │   │   └── Application.java
    │   │   └── resources/
    │   │       └── application.properties
    │   └── test/
    ├── Dockerfile
    └── pom.xml
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License.

## Authors

- **Adharsh Rengarajan** - Initial work

## Acknowledgments

- Spring Boot documentation
- React and Recharts communities
- Supabase and Render teams for excellent developer platforms
