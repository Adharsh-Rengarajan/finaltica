/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string;
  readonly VITE_API_AUTH_SIGNUP: string;
  readonly VITE_API_AUTH_LOGIN: string;
  readonly VITE_API_ACCOUNTS: string;
  readonly VITE_API_CATEGORIES: string;
  readonly VITE_API_TRANSACTIONS: string;
  readonly VITE_API_TRANSACTIONS_TRANSFER: string;
  readonly VITE_API_TRANSACTIONS_INVESTMENT: string;
  readonly VITE_API_ANALYTICS_NETWORTH: string;
  readonly VITE_API_ANALYTICS_MONTHLY_SUMMARY: string;
  readonly VITE_API_ANALYTICS_CATEGORY_SPENDING: string;
  readonly VITE_API_REPORTS_MONTHLY: string;
  readonly VITE_API_REPORTS_CUSTOM: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}