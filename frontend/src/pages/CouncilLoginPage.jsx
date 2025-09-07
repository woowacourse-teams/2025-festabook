import React, { useState } from 'react';
import { councilLogin } from '../auth/councilAuth';

const CouncilLoginPage = ({ onSuccess }) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    if (!username.trim() || !password.trim()) {
      setError('아이디와 비밀번호를 입력하세요.');
      return;
    }
    try {
      setLoading(true);
      await councilLogin({ username, password });
      if (onSuccess) onSuccess();
    } catch (err) {
      // 요구사항: 서버 응답 대신 고정 메시지 표시
      setError('아이디 혹은 비밀번호가 틀렸습니다. 확인해주세요');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="relative min-h-screen flex items-center justify-center bg-blue-600">
      {/* Noise background effect */}
      <div className="absolute inset-0 bg-white">
        <div className="absolute inset-0 opacity-30" style={{
          backgroundImage: `url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noiseFilter'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.65' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noiseFilter)'/%3E%3C/svg%3E")`,
          backgroundSize: '256px 256px'
        }} />
      </div>

      {/* Background logo and tagline */}
      <div className="absolute inset-0 flex flex-col items-center justify-start pt-20 pointer-events-none">
        <img src="/festabook-logo.png" alt="FestaBook" className="h-96 w-auto object-contain mb-4" />
        
      </div>

      {/* Foreground container */}
      <div className="relative w-full max-w-md bg-white p-8 rounded-xl shadow-lg">
        <div className="flex flex-col items-center mb-6">
          <h1 className="text-2xl font-semibold text-gray-800 mb-2">로그인</h1>
          <p className="text-gray-400 text-sm opacity-50">흩어진 정보를 하나로, 축제를 한 권에 담다</p>
        </div>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label htmlFor="username" className="block text-xs font-medium text-gray-500">아이디</label>
            <input id="username" type="text" value={username} onChange={(e) => setUsername(e.target.value)} className="mt-1 block w-full rounded-md border-gray-300 shadow-sm h-12 px-4 py-3 placeholder-gray-400 focus:border-black focus:ring-black" autoComplete="username" disabled={loading} />
          </div>
          <div>
            <label htmlFor="password" className="block text-xs font-medium text-gray-500">비밀번호</label>
            <input id="password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} className="mt-1 block w-full rounded-md border-gray-300 shadow-sm h-12 px-4 py-3 placeholder-gray-400 focus:border-black focus:ring-black" autoComplete="current-password" disabled={loading} />
          </div>
          {error && <p className="text-sm text-red-600">{error}</p>}
          <button type="submit" disabled={loading} className="w-full inline-flex justify-center items-center rounded-md bg-black px-4 py-3 h-12 text-white hover:bg-gray-800 focus:ring-black disabled:opacity-50">
            {loading ? '로그인 중...' : '로그인'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default CouncilLoginPage;


