// Council auth utilities: interceptor setup and login API
import api from '../utils/api';

const ACCESS_TOKEN_KEY = 'access_token';

export const getAccessToken = () => localStorage.getItem(ACCESS_TOKEN_KEY);
export const setAccessToken = (token) => localStorage.setItem(ACCESS_TOKEN_KEY, token);
export const clearAccessToken = () => localStorage.removeItem(ACCESS_TOKEN_KEY);

// Attach interceptors to the shared axios instance used across the app
export const setupCouncilAuth = () => {
  // Avoid adding duplicate interceptors if setup is called multiple times
  if (setupCouncilAuth._installed) return;
  setupCouncilAuth._installed = true;

  api.interceptors.request.use((config) => {
    const token = getAccessToken();
    if (token) {
      config.headers = config.headers || {};
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  });

  api.interceptors.response.use(
    (response) => response,
    (error) => {
      const status = error?.response?.status;
      if (status === 401 || status === 403) {
        try {
          // Clear all local storage including token and festival context
          localStorage.clear();
          clearAccessToken();
          // Go to login by replacing history
          window.location.replace('/');
        } catch (_) {
          // no-op
        }
      }
      return Promise.reject(error);
    }
  );
};

// Login API for councils
export const councilLogin = async ({ username, password }) => {
  const res = await api.post('/councils/login', { username, password });
  const token = res?.data?.accessToken || res?.data?.access_token;
  if (!token) {
    throw new Error('토큰 발급에 실패했습니다.');
  }
  setAccessToken(token);
  return token;
};


