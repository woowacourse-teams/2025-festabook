// src/utils/api.js
import axios from 'axios';

const API_HOST = 'http://festabook.woowacourse.com';
// const API_HOST = 'http://localhost:8080';

const api = axios.create({
  baseURL: API_HOST,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const organization = localStorage.getItem('organization');
  if (organization) {
    config.headers['organization'] = organization;
  }
  return config;
});

export default api;
