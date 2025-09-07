import React, { useState, useEffect } from 'react';
import CouncilLoginPage from '../pages/CouncilLoginPage';
import { getAccessToken } from './councilAuth';

const AuthGate = ({ children }) => {
  const [hasToken, setHasToken] = useState(Boolean(getAccessToken()));

  useEffect(() => {
    const onStorage = () => setHasToken(Boolean(getAccessToken()));
    window.addEventListener('storage', onStorage);
    return () => window.removeEventListener('storage', onStorage);
  }, []);

  if (!hasToken) {
    return <CouncilLoginPage onSuccess={() => setHasToken(true)} />;
  }
  return children;
};

export default AuthGate;


