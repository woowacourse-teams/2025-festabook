// src/utils/api.js
import axios from 'axios';

const API_HOST = import.meta.env.VITE_API_HOST;
const api = axios.create({
  baseURL: API_HOST,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 파일 업로드용 별도 인스턴스
const fileApi = axios.create({
  baseURL: API_HOST,
  headers: {
    'Content-Type': 'multipart/form-data',
  },
});

api.interceptors.request.use((config) => {
  const festivalId = localStorage.getItem('festivalId');
  if (festivalId) {
    config.headers['festival'] = festivalId;
  }
  const token = localStorage.getItem('access_token');
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }
  return config;
});

// 파일 업로드용 인터셉터
fileApi.interceptors.request.use((config) => {
  const festivalId = localStorage.getItem('festivalId');
  if (festivalId) {
    config.headers['festival'] = festivalId;
  }
  const token = localStorage.getItem('access_token');
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }
  return config;
});

// Unauthorized handling centralized here as well
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error?.response?.status;
    if (status === 401 || status === 403) {
      try {
        localStorage.clear();
        window.location.replace('/');
      } catch (error) {
        // 에러 처리 시 로그만 출력
        console.error('Error during logout redirect:', error);
      }
    }
    return Promise.reject(error);
  }
);

fileApi.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error?.response?.status;
    if (status === 401 || status === 403) {
      try {
        localStorage.clear();
        window.location.replace('/');
      } catch (error) {
        console.error('Error during logout redirect:', error);
      }
    }
    return Promise.reject(error);
  }
);

// 공통 이미지 업로드 API
export const imageAPI = {
  // 이미지 파일 업로드
  uploadImage: async (file) => {
    try {
      const formData = new FormData();
      formData.append('image', file);
      
      const response = await fileApi.post('/images', formData);
      return response.data;
    } catch (error) {
      console.error('Failed to upload image:', error);
      throw new Error('이미지 업로드에 실패했습니다.');
    }
  }
};

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

  // 분실물 가이드 조회
  getLostItemGuide: async () => {
    try {
      const response = await api.get('/festivals/lost-item-guide');
      // 응답 구조 문서 상과 실제는 다를 수 있으나, 여기서는 lostItemGuide만 추출하도록 가정
      return response.data?.lostItemGuide ?? '';
    } catch (error) {
      console.error('Failed to fetch lost item guide:', error);
      throw new Error('분실물 가이드 조회에 실패했습니다.');
    }
  },

  // 분실물 가이드 수정
  updateLostItemGuide: async (lostItemGuide) => {
    try {
      const response = await api.patch('/festivals/lost-item-guide', { lostItemGuide });
      return response.data?.lostItemGuide ?? lostItemGuide;
    } catch (error) {
      console.error('Failed to update lost item guide:', error);
      throw new Error('분실물 가이드 수정에 실패했습니다.');
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
  },

  // 공지사항 알림 전송
  sendNotification: async (announcementId) => {
    try {
      const response = await api.post(`/announcements/${announcementId}/notifications`);
      return response.data;
    } catch (error) {
      console.error('Failed to send notification:', error);
      if (error.response?.status === 429) {
        throw new Error('알림 전송 요청이 너무 많습니다. 잠시 후 다시 시도해주세요.');
      } else if (error.response?.status === 404) {
        throw new Error('공지사항을 찾을 수 없습니다.');
      } else {
        throw new Error('알림 전송에 실패했습니다.');
      }
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

  // 메인 플레이스 수정
  updateMainPlace: async (placeId, placeData) => {
    try {
      const response = await api.patch(`/places/main/${placeId}`, placeData);
      return response.data;
    } catch (error) {
      console.error('Failed to update main place:', error);
      throw new Error('메인 플레이스 수정에 실패했습니다.');
    }
  },

  // 기타 플레이스 수정
  updateEtcPlace: async (placeId, placeData) => {
    try {
      const response = await api.patch(`/places/etc/${placeId}`, placeData);
      return response.data;
    } catch (error) {
      console.error('Failed to update etc place:', error);
      throw new Error('기타 플레이스 수정에 실패했습니다.');
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
  },

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

// FAQ 관련 API
export const qnaAPI = {
  // 모든 FAQ 조회
  getQuestions: async () => {
    try {
      const response = await api.get('/questions');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch questions:', error);
      throw new Error('FAQ 조회에 실패했습니다.');
    }
  },

  // 새 FAQ 추가
  createQuestion: async (questionData) => {
    try {
      const response = await api.post('/questions', questionData);
      return response.data;
    } catch (error) {
      console.error('Failed to create question:', error);
      throw new Error('FAQ 추가에 실패했습니다.');
    }
  },

  // FAQ 수정
  updateQuestion: async (questionId, questionData) => {
    try {
      const response = await api.patch(`/questions/${questionId}`, questionData);
      return response.data;
    } catch (error) {
      console.error('Failed to update question:', error);
      throw new Error('FAQ 수정에 실패했습니다.');
    }
  },

  // FAQ 삭제
  deleteQuestion: async (questionId) => {
    try {
      await api.delete(`/questions/${questionId}`);
      // 성공 시 204 응답, body 없음
    } catch (error) {
      console.error('Failed to delete question:', error);
      throw new Error('FAQ 삭제에 실패했습니다.');
    }
  },

  // FAQ 순서 변경
  updateQuestionSequences: async (sequences) => {
    try {
      const response = await api.patch('/questions/sequences', sequences);
      return response.data;
    } catch (error) {
      console.error('Failed to update question sequences:', error);
      throw new Error('FAQ 순서 변경에 실패했습니다.');
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
                pickupStatus: item.pickupStatus,
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
                pickupStatus: response.data.pickupStatus,
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
            const response = await api.patch(`/lost-items/${lostItemId}/status`, { pickupStatus : status});
            // LostItemStatusUpdateResponse를 프론트엔드 형식으로 변환
            return {
                id: response.data.lostItemId,
                pickupStatus: response.data.pickupStatus
            };
        } catch (error) {
            console.error('Failed to update lost item status:', error);
            throw new Error('분실물 상태 변경에 실패했습니다.');
        }
    }
};

// 라인업 관련 API
export const lineupAPI = {
  // 특정 축제의 모든 라인업 조회
  getLineups: async () => {
    try {
      const response = await api.get('/lineups');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch lineups:', error);
      throw new Error('라인업 조회에 실패했습니다.');
    }
  },

  // 라인업 추가
  addLineup: async (lineupData) => {
    try {
      const response = await api.post('/lineups', lineupData);
      return response.data;
    } catch (error) {
      console.error('Failed to add lineup:', error);
      throw new Error('라인업 추가에 실패했습니다.');
    }
  },

  // 라인업 수정
  updateLineup: async (lineupId, lineupData) => {
    try {
      const response = await api.patch(`/lineups/${lineupId}`, lineupData);
      return response.data;
    } catch (error) {
      console.error('Failed to update lineup:', error);
      throw new Error('라인업 수정에 실패했습니다.');
    }
  },

  // 라인업 삭제
  deleteLineup: async (lineupId) => {
    try {
      await api.delete(`/lineups/${lineupId}`);
    } catch (error) {
      console.error('Failed to delete lineup:', error);
      throw new Error('라인업 삭제에 실패했습니다.');
    }
  }
};

// 시간 태그 관련 API
export const timeTagAPI = {
  // 시간 태그 목록 조회
  getTimeTags: async () => {
    try {
      const response = await api.get('/time-tags');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch time tags:', error);
      throw new Error('시간 태그 조회에 실패했습니다.');
    }
  },

  // 시간 태그 추가
  createTimeTag: async (timeTagData) => {
    try {
      const response = await api.post('/time-tags', timeTagData);
      return response.data;
    } catch (error) {
      console.error('Failed to create time tag:', error);
      throw new Error('시간 태그 추가에 실패했습니다.');
    }
  }
};

// 학생회 관련 API
export const councilAPI = {
  // 학생회 비밀번호 변경
  changePassword: async (passwordData) => {
    try {
      const response = await api.patch('/councils/password', passwordData);
      return response.data;
    } catch (error) {
      console.error('Failed to change password:', error);
      throw new Error('비밀번호 변경에 실패했습니다.');
    }
  }
};

export default api;
