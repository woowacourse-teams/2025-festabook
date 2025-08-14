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

// 축제 관련 API
export const festivalAPI = {
  // 축제 정보 조회
  getFestival: async () => {
    try {
      const response = await api.get('/festivals');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch festival:', error);
      throw new Error('축제 정보 조회에 실패했습니다.');
    }
  },

  // 축제 정보 수정
  updateFestivalInfo: async (festivalInfo) => {
    try {
      const response = await api.patch('/festivals/information', festivalInfo);
      return response.data;
    } catch (error) {
      console.error('Failed to update festival info:', error);
      throw new Error('축제 정보 수정에 실패했습니다.');
    }
  },

  // 축제 이미지 추가
  addFestivalImage: async (imageData) => {
    try {
      const response = await api.post('/festivals/images', imageData);
      return response.data;
    } catch (error) {
      console.error('Failed to add festival image:', error);
      throw new Error('축제 이미지 추가에 실패했습니다.');
    }
  },

  // 축제 이미지 순서 변경
  updateFestivalImageSequences: async (sequences) => {
    try {
      const response = await api.patch('/festivals/images/sequences', sequences);
      return response.data;
    } catch (error) {
      console.error('Failed to update festival image sequences:', error);
      throw new Error('축제 이미지 순서 변경에 실패했습니다.');
    }
  },

  // 축제 이미지 삭제
  deleteFestivalImage: async (festivalImageId) => {
    try {
      await api.delete(`/festivals/images/${festivalImageId}`);
    } catch (error) {
      console.error('Failed to delete festival image:', error);
      throw new Error('축제 이미지 삭제에 실패했습니다.');
    }
  }
};

// 공지사항 관련 API
export const announcementAPI = {
  // 모든 공지사항 조회
  getAnnouncements: async () => {
    try {
      const response = await api.get('/announcements');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch announcements:', error);
      throw new Error('공지사항 조회에 실패했습니다.');
    }
  },

  // 공지사항 생성
  createAnnouncement: async (announcementData) => {
    try {
      await api.post('/announcements', announcementData);
    } catch (error) {
      console.error('Failed to create announcement:', error);
      throw new Error('공지사항 생성에 실패했습니다.');
    }
  },

  // 공지사항 수정
  updateAnnouncement: async (id, announcementData) => {
    try {
      await api.patch(`/announcements/${id}`, announcementData);
    } catch (error) {
      console.error('Failed to update announcement:', error);
      throw new Error('공지사항 수정에 실패했습니다.');
    }
  },

  // 공지사항 삭제
  deleteAnnouncement: async (id) => {
    try {
      await api.delete(`/announcements/${id}`);
    } catch (error) {
      console.error('Failed to delete announcement:', error);
      throw new Error('공지사항 삭제에 실패했습니다.');
    }
  },

  // 공지사항 고정/해제
  toggleAnnouncementPin: async (id, pinned) => {
    try {
      const response = await api.patch(`/announcements/${id}/pin`, { pinned });
      return response;
    } catch (error) {
      console.error('Failed to toggle announcement pin:', error);
      throw new Error('공지사항 고정 상태 변경에 실패했습니다.');
    }
  }
};

// 플레이스 관련 API
export const placeAPI = {
  // 모든 플레이스 조회
  getPlaces: async () => {
    try {
      const response = await api.get('/places');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch places:', error);
      throw new Error('플레이스 조회에 실패했습니다.');
    }
  },

  // 플레이스 생성
  createPlace: async (placeData) => {
    try {
      const response = await api.post('/places', placeData);
      return response.data;
    } catch (error) {
      console.error('Failed to create place:', error);
      throw new Error('플레이스 생성에 실패했습니다.');
    }
  },

  // 플레이스 삭제
  deletePlace: async (id) => {
    try {
      const response = await api.delete(`/places/${id}`);
      return response;
    } catch (error) {
      console.error('Failed to delete place:', error);
      throw new Error('플레이스 삭제에 실패했습니다.');
    }
  },

  // 플레이스 수정
  updatePlace: async (placeId, placeData) => {
    try {
      const response = await api.put(`/places/${placeId}`, placeData);
      return response.data;
    } catch (error) {
      console.error('Failed to update place:', error);
      throw new Error('플레이스 수정에 실패했습니다.');
    }
  },

  // 플레이스 이미지 생성
  createPlaceImage: async (placeId, imageData) => {
    try {
      const response = await api.post(`/places/${placeId}/images`, imageData);
      return response.data;
    } catch (error) {
      console.error('Failed to create place image:', error);
      throw new Error('플레이스 이미지 생성에 실패했습니다.');
    }
  },

  // 플레이스 이미지 삭제
  deletePlaceImage: async (placeImageId) => {
    try {
      console.log('API: Deleting place image with ID:', placeImageId);
      console.log('API: Request URL:', `/places/images/${placeImageId}`);
      console.log('API: Request method: DELETE');
      const response = await api.delete(`/places/images/${placeImageId}`);
      console.log('API: Response status:', response.status);
      console.log('API: Place image deleted successfully');
    } catch (error) {
      console.error('Failed to delete place image:', error);
      console.error('Error response:', error.response);
      throw new Error('플레이스 이미지 삭제에 실패했습니다.');
    }
  },

  // 플레이스 이미지 순서 변경
  updatePlaceImageSequences: async (sequences) => {
    try {
      console.log('=== API: updatePlaceImageSequences called ===');
      console.log('API: Request URL:', '/places/images/sequences');
      console.log('API: Request method: PATCH');
      console.log('API: Request body:', JSON.stringify(sequences, null, 2));
      
      const response = await api.patch('/places/images/sequences', sequences);
      
      console.log('API: Response status:', response.status);
      console.log('API: Response data:', response.data);
      console.log('API: Place image sequences updated successfully');
      return response.data;
    } catch (error) {
      console.error('=== API: updatePlaceImageSequences failed ===');
      console.error('Error message:', error.message);
      console.error('Error response:', error.response);
      console.error('Error response data:', error.response?.data);
      console.error('Error response status:', error.response?.status);
      throw new Error('플레이스 이미지 순서 변경에 실패했습니다.');
    }
  }
};

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
