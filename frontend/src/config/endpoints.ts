// API Endpoints Configuration
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'https://finaltica.onrender.com';

// Debug logging
console.log('[CONFIG] API Base URL:', API_BASE_URL);
console.log('[CONFIG] Environment variables:');
console.log('  - VITE_API_BASE_URL:', import.meta.env.VITE_API_BASE_URL);
console.log('  - VITE_API_AUTH_SIGNUP:', import.meta.env.VITE_API_AUTH_SIGNUP);
console.log('  - VITE_API_AUTH_LOGIN:', import.meta.env.VITE_API_AUTH_LOGIN);

export const API_ENDPOINTS = {
  // Auth
  AUTH: {
    SIGNUP: `${API_BASE_URL}${import.meta.env.VITE_API_AUTH_SIGNUP || '/api/auth/signup'}`,
    LOGIN: `${API_BASE_URL}${import.meta.env.VITE_API_AUTH_LOGIN || '/api/auth/login'}`,
  },
  
  // Accounts
  ACCOUNTS: {
    BASE: `${API_BASE_URL}${import.meta.env.VITE_API_ACCOUNTS || '/api/accounts'}`,
    BY_ID: (id: string) => `${API_BASE_URL}${import.meta.env.VITE_API_ACCOUNTS || '/api/accounts'}/${id}`,
  },
  
  // Categories
  CATEGORIES: {
    BASE: `${API_BASE_URL}${import.meta.env.VITE_API_CATEGORIES || '/api/categories'}`,
    BY_ID: (id: string) => `${API_BASE_URL}${import.meta.env.VITE_API_CATEGORIES || '/api/categories'}/${id}`,
  },
  
  // Transactions
  TRANSACTIONS: {
    BASE: `${API_BASE_URL}${import.meta.env.VITE_API_TRANSACTIONS || '/api/transactions'}`,
    BY_ID: (id: string) => `${API_BASE_URL}${import.meta.env.VITE_API_TRANSACTIONS || '/api/transactions'}/${id}`,
    TRANSFER: `${API_BASE_URL}${import.meta.env.VITE_API_TRANSACTIONS_TRANSFER || '/api/transactions/transfer'}`,
    INVESTMENT: `${API_BASE_URL}${import.meta.env.VITE_API_TRANSACTIONS_INVESTMENT || '/api/transactions/investment'}`,
  },
  
  // Analytics
  ANALYTICS: {
    NET_WORTH: `${API_BASE_URL}${import.meta.env.VITE_API_ANALYTICS_NETWORTH || '/api/analytics/networth'}`,
    MONTHLY_SUMMARY: `${API_BASE_URL}${import.meta.env.VITE_API_ANALYTICS_MONTHLY_SUMMARY || '/api/analytics/monthly-summary'}`,
    CATEGORY_SPENDING: `${API_BASE_URL}${import.meta.env.VITE_API_ANALYTICS_CATEGORY_SPENDING || '/api/analytics/category-spending'}`,
  },
  
  // Reports
  REPORTS: {
    MONTHLY: `${API_BASE_URL}${import.meta.env.VITE_API_REPORTS_MONTHLY || '/api/reports/monthly'}`,
    CUSTOM: `${API_BASE_URL}${import.meta.env.VITE_API_REPORTS_CUSTOM || '/api/reports/custom'}`,
  },
};

// Log final endpoints
console.log('[CONFIG] Final API Endpoints:');
console.log('  - Signup:', API_ENDPOINTS.AUTH.SIGNUP);
console.log('  - Login:', API_ENDPOINTS.AUTH.LOGIN);

export default API_ENDPOINTS;