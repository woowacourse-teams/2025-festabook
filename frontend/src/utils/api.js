// src/utils/api.js
import axios from 'axios';

const API_HOST = 'http://festabook.woowacourse.com';

const api = axios.create({
  baseURL: API_HOST,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const festivalId = localStorage.getItem('festivalId');
  if (festivalId) {
    config.headers['festival'] = festivalId;
  }
  return config;
});

// 축제 날짜 관련 API
export const scheduleAPI = {
  // 모든 축제 날짜 조회
  getEventDates: async () => {
    try {
      const response = await api.get('/event-dates');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch event dates:', error);
      throw new Error('축제 날짜 조회에 실패했습니다.');
    }
  },

  // 새 날짜 추가
  addEventDate: async (date) => {
    try {
      await api.post('/event-dates', { date });
      // 성공 시 2XX 응답, body 없음 - 재조회 필요
      return await scheduleAPI.getEventDates();
    } catch (error) {
      console.error('Failed to add event date:', error);
      throw new Error('날짜 추가에 실패했습니다.');
    }
  },

  // 날짜 수정
  updateEventDate: async (eventDateId, date) => {
    try {
      await api.put(`/event-dates/${eventDateId}`, { date });
      // 성공 시 2XX 응답, body 없음 - 재조회 필요
      return await scheduleAPI.getEventDates();
    } catch (error) {
      console.error('Failed to update event date:', error);
      throw new Error('날짜 수정에 실패했습니다.');
    }
  },

  // 날짜 삭제
  deleteEventDate: async (eventDateId) => {
    try {
      await api.delete(`/event-dates/${eventDateId}`);
      // 성공 시 2XX 응답, body 없음 - 재조회 필요
      return await scheduleAPI.getEventDates();
    } catch (error) {
      console.error('Failed to delete event date:', error);
      throw new Error('날짜 삭제에 실패했습니다.');
    }
  },

  // 특정 날짜의 모든 일정 조회
  getEventsByDateId: async (eventDateId) => {
    try {
      const response = await api.get(`/event-dates/${eventDateId}/events`);
      return response.data;
    } catch (error) {
      console.error('Failed to fetch events:', error);
      throw new Error('일정 조회에 실패했습니다.');
    }
  },

  // 일정 추가
  createEvent: async (eventData) => {
    try {
      await api.post('/event-dates/events', eventData);
      // 성공 시 201 응답, body 없음 - 재조회 필요
    } catch (error) {
      console.error('Failed to create event:', error);
      throw new Error('일정 추가에 실패했습니다.');
    }
  },

  // 일정 수정
  updateEvent: async (eventId, eventData) => {
    try {
      await api.patch(`/event-dates/events/${eventId}`, eventData);
      // 성공 시 204 응답, body 없음 - 재조회 필요
    } catch (error) {
      console.error('Failed to update event:', error);
      throw new Error('일정 수정에 실패했습니다.');
    }
  },

  // 일정 삭제
  deleteEvent: async (eventId) => {
    try {
      await api.delete(`/event-dates/events/${eventId}`);
      // 성공 시 204 응답, body 없음 - 재조회 필요
    } catch (error) {
      console.error('Failed to delete event:', error);
      throw new Error('일정 삭제에 실패했습니다.');
    }
  }
};

// QnA 관련 API
export const qnaAPI = {
  // 모든 QnA 조회
  getQuestions: async () => {
    try {
      const response = await api.get('/questions');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch questions:', error);
      throw new Error('QnA 조회에 실패했습니다.');
    }
  },

  // 새 QnA 추가
  createQuestion: async (questionData) => {
    try {
      const response = await api.post('/questions', questionData);
      return response.data;
    } catch (error) {
      console.error('Failed to create question:', error);
      throw new Error('QnA 추가에 실패했습니다.');
    }
  },

  // QnA 수정
  updateQuestion: async (questionId, questionData) => {
    try {
      const response = await api.patch(`/questions/${questionId}`, questionData);
      return response.data;
    } catch (error) {
      console.error('Failed to update question:', error);
      throw new Error('QnA 수정에 실패했습니다.');
    }
  },

  // QnA 삭제
  deleteQuestion: async (questionId) => {
    try {
      await api.delete(`/questions/${questionId}`);
      // 성공 시 204 응답, body 없음
    } catch (error) {
      console.error('Failed to delete question:', error);
      throw new Error('QnA 삭제에 실패했습니다.');
    }
  },

  // QnA 순서 변경
  updateQuestionSequences: async (sequences) => {
    try {
      const response = await api.patch('/questions/sequences', sequences);
      return response.data;
    } catch (error) {
      console.error('Failed to update question sequences:', error);
      throw new Error('QnA 순서 변경에 실패했습니다.');
    }
  }
};

export default api;
