// 쿠키 관리 유틸리티 함수들

// 쿠키 설정
export const setCookie = (name, value, options = {}) => {
  const defaultOptions = {
    path: '/',
    secure: window.location.protocol === 'https:', // HTTPS에서만 secure 설정
    sameSite: 'lax' // 개발 환경에서 더 유연하게 설정
  };
  
  const cookieOptions = { ...defaultOptions, ...options };
  
  let cookieString = `${name}=${encodeURIComponent(value)}`;
  
  if (cookieOptions.expires) {
    if (cookieOptions.expires instanceof Date) {
      cookieString += `; expires=${cookieOptions.expires.toUTCString()}`;
    } else {
      const date = new Date();
      date.setTime(date.getTime() + (cookieOptions.expires * 24 * 60 * 60 * 1000));
      cookieString += `; expires=${date.toUTCString()}`;
    }
  }
  
  if (cookieOptions.path) cookieString += `; path=${cookieOptions.path}`;
  if (cookieOptions.domain) cookieString += `; domain=${cookieOptions.domain}`;
  if (cookieOptions.secure) cookieString += '; secure';
  if (cookieOptions.sameSite) cookieString += `; samesite=${cookieOptions.sameSite}`;
  
  document.cookie = cookieString;
};

// 쿠키 가져오기
export const getCookie = (name) => {
  const cookies = document.cookie.split(';');
  for (let cookie of cookies) {
    const [cookieName, cookieValue] = cookie.trim().split('=');
    if (cookieName === name) {
      return decodeURIComponent(cookieValue);
    }
  }
  return null;
};

// 쿠키 삭제
export const deleteCookie = (name, options = {}) => {
  const defaultOptions = {
    path: '/'
  };
  
  const cookieOptions = { ...defaultOptions, ...options };
  setCookie(name, '', { ...cookieOptions, expires: new Date(0) });
};

// ACCESS_TOKEN 쿠키 확인
export const hasAccessToken = () => {
  return getCookie('ACCESS_TOKEN') !== null;
};

// ACCESS_TOKEN 쿠키 가져오기
export const getAccessToken = () => {
  return getCookie('ACCESS_TOKEN');
};
