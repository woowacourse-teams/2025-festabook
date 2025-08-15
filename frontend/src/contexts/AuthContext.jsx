import React, { createContext, useContext, useState, useEffect } from 'react';
import { authAPI } from '../utils/api';
import { hasAccessToken, deleteCookie } from '../utils/cookies';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // 인증 상태 확인
  const checkAuthStatus = async () => {
    try {
      setLoading(true);
      if (hasAccessToken()) {
        const userData = await authAPI.checkAuth();
        setUser(userData);
        setIsAuthenticated(true);
      } else {
        setUser(null);
        setIsAuthenticated(false);
      }
    } catch (error) {
      console.error('Auth status check failed:', error);
      setUser(null);
      setIsAuthenticated(false);
      // 토큰이 유효하지 않으면 쿠키 삭제
      deleteCookie('ACCESS_TOKEN');
    } finally {
      setLoading(false);
    }
  };

  // 로그인
  const login = async (credentials) => {
    try {
      const response = await authAPI.login(credentials);
      setUser(response.user || response);
      setIsAuthenticated(true);
      return response;
    } catch (error) {
      throw error;
    }
  };

  // 로그아웃
  const logout = async () => {
    try {
      await authAPI.logout();
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      setUser(null);
      setIsAuthenticated(false);
      // 로컬에서도 쿠키 삭제
      deleteCookie('ACCESS_TOKEN');
    }
  };

  // 컴포넌트 마운트 시 인증 상태 확인
  useEffect(() => {
    checkAuthStatus();
  }, []);

  const value = {
    user,
    loading,
    isAuthenticated,
    login,
    logout,
    checkAuthStatus
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};
