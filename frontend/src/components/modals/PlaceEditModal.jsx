import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';
import { placeAPI } from '../../utils/api';

const PlaceEditModal = ({ place, onClose, onSave, showToast }) => {
  const [formData, setFormData] = useState({
    placeCategory: 'FOOD_TRUCK',
    title: '',
    description: '',
    location: '',
    host: '',
    startTime: '',
    endTime: ''
  });
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  // 플레이스 데이터로 폼 초기화
  useEffect(() => {
    if (place) {
      console.log('PlaceEditModal - Received place data:', place);
      console.log('PlaceEditModal - Category fields:', {
        placeCategory: place.placeCategory,
        category: place.category
      });
      
      const category = place.placeCategory || place.category || 'FOOD_TRUCK';
      console.log('PlaceEditModal - Selected category:', category);
      
      setFormData({
        placeCategory: category,
        title: place.title || '',
        description: place.description || '',
        location: place.location || '',
        host: place.host || '',
        startTime: place.startTime || '',
        endTime: place.endTime || ''
      });
    }
  }, [place]);

  // ESC 키 이벤트 리스너
  useEffect(() => {
    const handleEscKey = (event) => {
      if (event.key === 'Escape') {
        onClose();
      }
    };

    document.addEventListener('keydown', handleEscKey);

    return () => {
      document.removeEventListener('keydown', handleEscKey);
    };
  }, [onClose]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // 에러 메시지 초기화
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

    const validateForm = () => {
    const newErrors = {};

    if (!formData.title.trim()) {
      newErrors.title = '이름을 입력해주세요.';
    } else if (formData.title.length > 255) {
      newErrors.title = '이름은 255자를 초과할 수 없습니다.';
    }

    if (!formData.description.trim()) {
      newErrors.description = '설명을 입력해주세요.';
    } else if (formData.description.length > 3000) {
      newErrors.description = '설명은 3000자를 초과할 수 없습니다.';
    }

    if (!formData.location.trim()) {
      newErrors.location = '위치를 입력해주세요.';
    } else if (formData.location.length > 100) {
      newErrors.location = '위치는 100자를 초과할 수 없습니다.';
    }

    if (!formData.host.trim()) {
      newErrors.host = '운영 주체를 입력해주세요.';
    } else if (formData.host.length > 100) {
      newErrors.host = '운영 주체는 100자를 초과할 수 없습니다.';
    }

 

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setLoading(true);
    try {
      await placeAPI.updatePlace(place.placeId, formData);
      showToast('플레이스 정보가 성공적으로 수정되었습니다.');
      // 수정된 플레이스 ID를 전달하여 부모 컴포넌트에서 최신 데이터를 다시 조회하도록 함
      onSave({ placeId: place.placeId });
      onClose();
    } catch (error) {
      console.error('Failed to update place:', error);
      showToast(error.message || '플레이스 수정에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const formatTimeOnly = (timeString) => {
    if (!timeString) return '';
    // ISO 문자열이나 시간 문자열에서 시간 부분만 추출 (HH:MM 형식)
    if (timeString.includes('T')) {
      // ISO 형식인 경우 (예: "2024-01-01T14:30:00")
      return timeString.slice(11, 16);
    } else if (timeString.includes(':')) {
      // 이미 시간 형식인 경우 (예: "14:30")
      return timeString.slice(0, 5);
    }
    return '';
  };

  return (
    <Modal isOpen={true} onClose={onClose} maxWidth="max-w-2xl">
      <div className="p-6">
        <h3 className="text-xl font-bold mb-6">플레이스 세부사항 수정</h3>
        
        <form onSubmit={handleSubmit} className="space-y-6">
          {/* 카테고리 선택 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              카테고리 *
            </label>
            <select
              name="placeCategory"
              value={formData.placeCategory}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500"
            >
              <option value="FOOD_TRUCK">푸드트럭</option>
              <option value="BOOTH">부스</option>
              <option value="BAR">주점</option>
            </select>
          </div>

          {/* 이름 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              이름 *
            </label>
                          <input
                type="text"
                name="title"
                value={formData.title}
                onChange={handleChange}
                maxLength={20}
                className={`w-full border rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 ${
                  errors.title ? 'border-red-500' : 'border-gray-300'
                }`}
                placeholder="플레이스 이름을 입력하세요 (255자 이내)"
              />
            {errors.title && (
              <p className="mt-1 text-sm text-red-600">{errors.title}</p>
            )}
          </div>

          {/* 설명 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              설명 *
            </label>
                          <textarea
                name="description"
                value={formData.description}
                onChange={handleChange}
                rows="3"
                maxLength={100}
                className={`w-full border rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 ${
                  errors.description ? 'border-red-500' : 'border-gray-300'
                }`}
                placeholder="플레이스에 대한 설명을 입력하세요 (3000자 이내)"
              />
            {errors.description && (
              <p className="mt-1 text-sm text-red-600">{errors.description}</p>
            )}
          </div>

          {/* 위치 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              위치 *
            </label>
                          <input
                type="text"
                name="location"
                value={formData.location}
                onChange={handleChange}
                maxLength={100}
                className={`w-full border rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 ${
                  errors.location ? 'border-red-500' : 'border-gray-300'
                }`}
                placeholder="플레이스 위치를 입력하세요 (100자 이내)"
              />
            {errors.location && (
              <p className="mt-1 text-sm text-red-600">{errors.location}</p>
            )}
          </div>

          {/* 주최자 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              주최자 *
            </label>
                          <input
                type="text"
                name="host"
                value={formData.host}
                onChange={handleChange}
                maxLength={100}
                className={`w-full border rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 ${
                  errors.host ? 'border-red-500' : 'border-gray-300'
                }`}
                placeholder="주최자 또는 담당자를 입력하세요 (100자 이내)"
              />
            {errors.host && (
              <p className="mt-1 text-sm text-red-600">{errors.host}</p>
            )}
          </div>

          {/* 시간 설정 */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                시작 시간
              </label>
              <input
                type="time"
                name="startTime"
                value={formatTimeOnly(formData.startTime)}
                onChange={handleChange}
                className="w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                종료 시간
              </label>
              <input
                type="time"
                name="endTime"
                value={formatTimeOnly(formData.endTime)}
                onChange={handleChange}
                className="w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500"
              />
            </div>
          </div>

          {/* 버튼 */}
          <div className="flex justify-end space-x-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              취소
            </button>
            <button
              type="submit"
              disabled={loading}
              className={`px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 ${
                loading
                  ? 'bg-gray-800 cursor-not-allowed'
                  : 'bg-gray-800'
              }`}
            >
              {loading ? '수정 중...' : '수정 완료'}
            </button>
          </div>
        </form>
      </div>
    </Modal>
  );
};

export default PlaceEditModal;
