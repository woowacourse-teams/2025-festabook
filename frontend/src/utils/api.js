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

// 응답 인터셉터 - 에러 처리
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 400) {
      // 백엔드에서 제공하는 에러 메시지 사용
      const message = error.response.data?.message || '잘못된 요청입니다.';
      throw new Error(message);
    }
    if (error.response?.status === 404) {
      throw new Error('요청한 리소스를 찾을 수 없습니다.');
    }
    if (error.response?.status === 500) {
      throw new Error('서버 내부 오류가 발생했습니다.');
    }
    // 네트워크 에러 등
    if (!error.response) {
      throw new Error('네트워크 연결을 확인해주세요.');
    }
    throw error;
  }
);

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

// 분실물 관련 API
export const lostItemAPI = {
  // 모든 분실물 조회
  getLostItems: async () => {
    try {
      const response = await api.get('/lost-items');
      // 백엔드 응답을 프론트엔드 형식으로 변환
      return response.data.map(item => ({
        id: item.lostItemId,
        imageUrl: item.imageUrl,
        storageLocation: item.storageLocation,
        pickupStatus: item.status,
        createdAt: item.createdAt
      }));
    } catch (error) {
      console.error('Failed to fetch lost items:', error);
      throw new Error('분실물 조회에 실패했습니다.');
    }
  },

  // 분실물 등록
  createLostItem: async (lostItemData) => {
    try {
      const response = await api.post('/lost-items', lostItemData);
      // LostItemResponse를 프론트엔드 형식으로 변환
      return {
        id: response.data.lostItemId,
        imageUrl: response.data.imageUrl,
        storageLocation: response.data.storageLocation,
        pickupStatus: response.data.status,
        createdAt: response.data.createdAt
      };
    } catch (error) {
      console.error('Failed to create lost item:', error);
      throw new Error('분실물 등록에 실패했습니다.');
    }
  },

  // 분실물 수정
  updateLostItem: async (lostItemId, updateData) => {
    try {
      const response = await api.patch(`/lost-items/${lostItemId}`, updateData);
      // LostItemUpdateResponse를 프론트엔드 형식으로 변환
      return {
        id: response.data.lostItemId,
        imageUrl: response.data.imageUrl,
        storageLocation: response.data.storageLocation
      };
    } catch (error) {
      console.error('Failed to update lost item:', error);
      throw new Error('분실물 수정에 실패했습니다.');
    }
  },

  // 분실물 삭제  
  deleteLostItem: async (lostItemId) => {
    try {
      await api.delete(`/lost-items/${lostItemId}`);
      // 204 No Content - 반환값 없음
    } catch (error) {
      console.error('Failed to delete lost item:', error);
      throw new Error('분실물 삭제에 실패했습니다.');
    }
  },

  // 분실물 상태 변경
  updateLostItemStatus: async (lostItemId, status) => {
    try {
      const response = await api.patch(`/lost-items/${lostItemId}/status`, { status });
      // LostItemStatusUpdateResponse를 프론트엔드 형식으로 변환
      return {
        id: response.data.lostItemId,
        pickupStatus: response.data.status
      };
    } catch (error) {
      console.error('Failed to update lost item status:', error);
      throw new Error('분실물 상태 변경에 실패했습니다.');
    }
  }
};

export default api;
