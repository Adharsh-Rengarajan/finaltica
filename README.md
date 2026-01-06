# Finaltica - Smart Expense and Investment Management System

Finaltica is a professional-grade, N-tier personal finance application designed to centralize the management of liquid cash, credit liabilities, and long-term investments. By implementing a sophisticated digital ledger system, the platform bridges the gap between simple manual tracking and professional wealth management, providing users with a comprehensive view of their net worth.

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
- **React.js**: For building a dynamic, responsive user interface and financial dashboards.
- **Recharts / D3.js**: For data visualization and financial trend charting.

### Backend
- **Java 17 & Spring Boot 3**: The core framework for handling business logic and RESTful API services.
- **Spring Security**: To manage authentication and JWT-based authorization.
- **Spring Data JPA**: For streamlined database interaction and ORM mapping.

### Data and Storage
- **PostgreSQL**: A relational database ensuring ACID compliance for financial integrity.
- **AWS S3**: For scalable storage of generated PDF financial statements.
- **Supabase** - Utilized as a managed Backend-as-a-Service (BaaS) platform to provide the primary PostgreSQL instance, real-time database subscriptions for live dashboard updates, and integrated authentication metadata.

### Deployment and Infrastructure
- **Vercel**: Hosting platform for the React frontend.
- **Render**: Hosting for the Spring Boot backend service.
- **GitHub Actions**: For CI/CD automation and deployment pipelines.
