import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import axios from 'axios';
import API_ENDPOINTS from '@config/endpoints';
import { 
  saveToken, 
  saveUser, 
  getToken, 
  getUser, 
  logout as authLogout 
} from '@utils/auth';
import { User, LoginResponse, SignupRequest, ApiResponse } from '@typings/index';

interface AuthContextType {
  user: User | null;
  token: string | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<AuthResult>;
  signup: (userData: SignupRequest) => Promise<AuthResult>;
  logout: () => void;
  isAuthenticated: () => boolean;
}

interface AuthResult {
  success: boolean;
  message?: string;
  data?: any;
  errors?: Record<string, string>;
}

const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const initAuth = () => {
      const savedToken = getToken();
      const savedUser = getUser();
      
      if (savedToken && savedUser) {
        setToken(savedToken);
        setUser(savedUser);
      }
      setLoading(false);
    };

    initAuth();
  }, []);

  const login = async (email: string, password: string): Promise<AuthResult> => {
    try {
      console.log('[AUTH] Login request to:', API_ENDPOINTS.AUTH.LOGIN);
      
      const response = await axios.post<ApiResponse<LoginResponse>>(
        API_ENDPOINTS.AUTH.LOGIN,
        { email, password }
      );
      
      if (response.data.success) {
        const { token, user } = response.data.data;
        
        saveToken(token);
        saveUser(user);
        setToken(token);
        setUser(user);
        
        console.log('[AUTH] Login successful');
        return { success: true };
      }
      
      return { success: false, message: response.data.message };
    } catch (error: any) {
      console.error('[AUTH] Login failed:', error.response?.data);
      return { 
        success: false, 
        message: error.response?.data?.message || 'Login failed',
        errors: error.response?.data?.errors
      };
    }
  };

  const signup = async (userData: SignupRequest): Promise<AuthResult> => {
    try {
      console.log('[AUTH] Signup request to:', API_ENDPOINTS.AUTH.SIGNUP);
      console.log('[AUTH] Request payload:', { ...userData, password: '[REDACTED]', confirmPassword: '[REDACTED]' });
      
      const response = await axios.post<ApiResponse<User>>(
        API_ENDPOINTS.AUTH.SIGNUP,
        userData
      );
      
      console.log('[AUTH] Signup response:', response.data);
      
      if (response.data.success) {
        return { success: true, data: response.data.data };
      }
      
      return { success: false, message: response.data.message };
    } catch (error: any) {
      console.error('[AUTH] Signup failed:', error);
      console.error('[AUTH] Error response:', error.response?.data);
      return { 
        success: false, 
        message: error.response?.data?.message || 'Signup failed',
        errors: error.response?.data?.errors 
      };
    }
  };

  const logout = (): void => {
    setUser(null);
    setToken(null);
    authLogout();
  };

  const isAuthenticated = (): boolean => {
    return !!token && !!user;
  };

  const value: AuthContextType = {
    user,
    token,
    loading,
    login,
    signup,
    logout,
    isAuthenticated
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};