const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'https://finaltica.onrender.com';

export const API_ENDPOINTS = {
  AUTH: {
    SIGNUP: `${API_BASE_URL}${import.meta.env.VITE_API_AUTH_SIGNUP || '/api/auth/signup'}`,
    LOGIN: `${API_BASE_URL}${import.meta.env.VITE_API_AUTH_LOGIN || '/api/auth/login'}`,
  },

  ACCOUNTS: {
    BASE: `${API_BASE_URL}${import.meta.env.VITE_API_ACCOUNTS || '/api/accounts'}`,
    BY_ID: (id: string) => `${API_BASE_URL}${import.meta.env.VITE_API_ACCOUNTS || '/api/accounts'}/${id}`,
  },

  CATEGORIES: {
    BASE: `${API_BASE_URL}${import.meta.env.VITE_API_CATEGORIES || '/api/categories'}`,
    BY_ID: (id: string) => `${API_BASE_URL}${import.meta.env.VITE_API_CATEGORIES || '/api/categories'}/${id}`,
  },

  TRANSACTIONS: {
    BASE: `${API_BASE_URL}${import.meta.env.VITE_API_TRANSACTIONS || '/api/transactions'}`,
    BY_ID: (id: string) => `${API_BASE_URL}${import.meta.env.VITE_API_TRANSACTIONS || '/api/transactions'}/${id}`,
    TRANSFER: `${API_BASE_URL}${import.meta.env.VITE_API_TRANSACTIONS_TRANSFER || '/api/transactions/transfer'}`,
    INVESTMENT: `${API_BASE_URL}${import.meta.env.VITE_API_TRANSACTIONS_INVESTMENT || '/api/transactions/investment'}`,
    INVESTMENTS: `${API_BASE_URL}${import.meta.env.VITE_API_TRANSACTIONS_INVESTMENTS || '/api/transactions/investments'}`,
  },

  ANALYTICS: {
    NET_WORTH: `${API_BASE_URL}${import.meta.env.VITE_API_ANALYTICS_NETWORTH || '/api/analytics/networth'}`,
    MONTHLY_SUMMARY: `${API_BASE_URL}${import.meta.env.VITE_API_ANALYTICS_MONTHLY_SUMMARY || '/api/analytics/monthly-summary'}`,
    CATEGORY_SPENDING: `${API_BASE_URL}${import.meta.env.VITE_API_ANALYTICS_CATEGORY_SPENDING || '/api/analytics/category-spending'}`,
    PORTFOLIO: `${API_BASE_URL}${import.meta.env.VITE_API_ANALYTICS_PORTFOLIO || '/api/analytics/portfolio'}`,
  },

  REPORTS: {
    MONTHLY: `${API_BASE_URL}${import.meta.env.VITE_API_REPORTS_MONTHLY || '/api/reports/monthly'}`,
    CUSTOM: `${API_BASE_URL}${import.meta.env.VITE_API_REPORTS_CUSTOM || '/api/reports/custom'}`,
  },
};

export default API_ENDPOINTS;