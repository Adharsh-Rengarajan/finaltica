// Enums
export enum AccountType {
  CHECKING = 'CHECKING',
  CREDIT = 'CREDIT',
  INVESTMENT = 'INVESTMENT',
  CASH = 'CASH'
}

export enum CurrencyCode {
  USD = 'USD',
  INR = 'INR'
}

export enum TransactionType {
  INCOME = 'INCOME',
  EXPENSE = 'EXPENSE',
  TRANSFER = 'TRANSFER'
}

export enum CategoryType {
  INCOME = 'INCOME',
  EXPENSE = 'EXPENSE'
}

export enum PaymentMode {
  UPI = 'UPI',
  CARD = 'CARD',
  ACH = 'ACH',
  CASH = 'CASH'
}

export enum AssetType {
  STOCK = 'STOCK',
  MUTUAL_FUND = 'MUTUAL_FUND'
}

// User Types
export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface SignupRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  confirmPassword: string;
}

export interface LoginResponse {
  token: string;
  user: User;
}

// Account Types
export interface Account {
  id: string;
  name: string;
  type: AccountType;
  currentBalance: number;
  currency: CurrencyCode;
  createdAt: string;
  updatedAt: string;
}

export interface CreateAccountRequest {
  name: string;
  type: AccountType;
  currency: CurrencyCode;
  initialBalance: number;
}

export interface UpdateAccountRequest {
  name: string;
  currency: CurrencyCode;
}

// Category Types
export interface Category {
  id: string;
  name: string;
  type: CategoryType;
  isGlobal: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateCategoryRequest {
  name: string;
  type: CategoryType;
}

export interface UpdateCategoryRequest {
  name: string;
}

// Transaction Types
export interface Transaction {
  id: string;
  accountId: string;
  accountName: string;
  categoryId: string | null;
  categoryName: string | null;
  relatedTransactionId: string | null;
  amount: number;
  type: TransactionType;
  description: string | null;
  transactionDate: string;
  paymentMode: PaymentMode;
  createdAt: string;
  updatedAt: string;
}

export interface CreateTransactionRequest {
  accountId: string;
  categoryId?: string;
  amount: number;
  type: TransactionType;
  description?: string;
  transactionDate: string;
  paymentMode: PaymentMode;
}

export interface CreateTransferRequest {
  fromAccountId: string;
  toAccountId: string;
  amount: number;
  description?: string;
  transactionDate: string;
  paymentMode: PaymentMode;
}

export interface CreateInvestmentTransactionRequest {
  accountId: string;
  assetSymbol: string;
  assetType: AssetType;
  quantity: number;
  pricePerUnit: number;
  description?: string;
  transactionDate: string;
  paymentMode: PaymentMode;
}

export interface InvestmentMetadata {
  transactionId: string;
  assetSymbol: string;
  assetType: AssetType;
  quantity: number;
  pricePerUnit: number;
  totalAmount: number;
  createdAt: string;
  updatedAt: string;
}

export interface InvestmentTransaction {
  transaction: Transaction;
  investmentMetadata: InvestmentMetadata;
}

// Analytics Types
export interface NetWorthResponse {
  totalAssets: number;
  totalLiabilities: number;
  netWorth: number;
  accounts: AccountSummary[];
}

export interface AccountSummary {
  accountName: string;
  accountType: string;
  balance: number;
}

export interface MonthlySummary {
  year: number;
  month: number;
  totalIncome: number;
  totalExpenses: number;
  netSavings: number;
  incomeTransactionCount: number;
  expenseTransactionCount: number;
}

export interface CategorySpending {
  categoryName: string;
  amount: number;
  transactionCount: number;
}

export interface CategorySpendingResponse {
  expenses: CategorySpending[];
  income: CategorySpending[];
}

// API Response Types
export interface ApiResponse<T> {
  statusCode: number;
  success: boolean;
  message: string;
  data: T;
  errors?: Record<string, string>;
}

export interface ApiError {
  statusCode: number;
  message: string;
  errors?: Record<string, string>;
}