import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';
import { placeAPI, timeTagAPI } from '../../utils/api';

const PlaceEditModal = ({ place, onClose, onSave, showToast }) => {
  const [formData, setFormData] = useState({
    placeCategory: 'FOOD_TRUCK',
    title: '',
    description: '',
    location: '',
    host: '',
    startTime: '',
    endTime: '',
    timeTags: []
  });
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [timeTags, setTimeTags] = useState([]);
  const [selectedTimeTags, setSelectedTimeTags] = useState([]);
  
  // 시작 시간 개별 필드 관리
  const [startHours, setStartHoursState] = useState('');
  const [startMinutes, setStartMinutesState] = useState('');
  
  // 종료 시간 개별 필드 관리
  const [endHours, setEndHoursState] = useState('');
  const [endMinutes, setEndMinutesState] = useState('');

  // 시작 시간 개별 필드 업데이트 함수들
  const setStartHours = (newHours) => {
    setStartHoursState(newHours);
    setFormData(prev => {
      const currentTime = prev.startTime || '';
      const [, currentMinutes = '00'] = currentTime.split(':');
      return {
        ...prev,
        startTime: `${newHours.padStart(2, '0')}:${currentMinutes}`
      };
    });
  };

  const setStartMinutes = (newMinutes) => {
    setStartMinutesState(newMinutes);
    setFormData(prev => {
      const currentTime = prev.startTime || '';
      const [currentHours = '00'] = currentTime.split(':');
      return {
        ...prev,
        startTime: `${currentHours}:${newMinutes.padStart(2, '0')}`
      };
    });
  };

  // 종료 시간 개별 필드 업데이트 함수들
  const setEndHours = (newHours) => {
    setEndHoursState(newHours);
    setFormData(prev => {
      const currentTime = prev.endTime || '';
      const [, currentMinutes = '00'] = currentTime.split(':');
      return {
        ...prev,
        endTime: `${newHours.padStart(2, '0')}:${currentMinutes}`
      };
    });
  };

  const setEndMinutes = (newMinutes) => {
    setEndMinutesState(newMinutes);
    setFormData(prev => {
      const currentTime = prev.endTime || '';
      const [currentHours = '00'] = currentTime.split(':');
      return {
        ...prev,
        endTime: `${currentHours}:${newMinutes.padStart(2, '0')}`
      };
    });
  };

  // 시간 태그 목록 불러오기
  useEffect(() => {
    const fetchTimeTags = async () => {
      try {
        const timeTagData = await timeTagAPI.getTimeTags();
        setTimeTags(timeTagData);
      } catch (error) {
        console.error('Failed to fetch time tags:', error);
        // 시간 태그 로딩 실패는 전체 기능을 막지 않도록 조용히 처리
      }
    };
    
    fetchTimeTags();
  }, []);

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
      
      const startTime = place.startTime || '';
      const endTime = place.endTime || '';
      const [startHoursPart = '', startMinutesPart = ''] = startTime.split(':');
      const [endHoursPart = '', endMinutesPart = ''] = endTime.split(':');
      
      setFormData({
        placeCategory: category,
        title: place.title || '',
        description: place.description || '',
        location: place.location || '',
        host: place.host || '',
        startTime: startTime,
        endTime: endTime,
        timeTags: place.timeTags ? place.timeTags.map(tag => 
          typeof tag === 'string' ? tag : tag.name
        ) : []
      });
      
      // 시간 태그 선택 상태 초기화 (객체 배열에서 이름만 추출)
      if (place.timeTags) {
        const tagNames = place.timeTags.map(tag => 
          typeof tag === 'string' ? tag : tag.name
        );
        setSelectedTimeTags(tagNames);
      }
      
      // 개별 시간 상태도 업데이트
      setStartHoursState(startHoursPart);
      setStartMinutesState(startMinutesPart);
      setEndHoursState(endHoursPart);
      setEndMinutesState(endMinutesPart);
    }
  }, [place, timeTags]); // timeTags가 로드된 후에 실행되도록

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
    
    // 글자 수 제한 체크
    if (name === 'title' && value.length > 255) {
      showToast('이름은 255자 이내로 입력해주세요.');
      return;
    }
    if (name === 'description' && value.length > 3000) {
      showToast('설명은 3000자 이내로 입력해주세요.');
      return;
    }
    if (name === 'host' && value.length > 100) {
      showToast('운영 주체는 100자 이내로 입력해주세요.');
      return;
    }
    if (name === 'location' && value.length > 100) {
      showToast('위치는 100자 이내로 입력해주세요.');
      return;
    }
    
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

  // 시간 태그 체크박스 처리
  const handleTimeTagChange = (tagName, isChecked) => {
    let updatedTags;
    if (isChecked) {
      updatedTags = [...selectedTimeTags, tagName];
    } else {
      updatedTags = selectedTimeTags.filter(tag => tag !== tagName);
    }
    setSelectedTimeTags(updatedTags);
    setFormData(prev => ({
      ...prev,
      timeTags: updatedTags
    }));
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
      // timeTags 이름을 ID로 변환
      const timeTagIds = selectedTimeTags.map(tagName => {
        const tag = timeTags.find(t => t.name === tagName);
        return tag ? tag.timeTagId : null;
      }).filter(id => id !== null);

      const updateData = {
        ...formData,
        timeTags: timeTagIds // 이름 대신 ID 배열로 전송
      };

      await placeAPI.updateMainPlace(place.placeId, updateData);
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

  return (
    <Modal isOpen={true} onClose={onClose} maxWidth="max-w-xl">
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
            <div className="flex justify-between items-center mb-2">
              <label className="block text-sm font-medium text-gray-700">
                이름 *
              </label>
              <span className="text-xs text-gray-500">
                {formData.title.length}/255
              </span>
            </div>
              <input
                type="text"
                name="title"
                value={formData.title}
                onChange={handleChange}
                maxLength={255}
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
            <div className="flex justify-between items-center mb-2">
              <label className="block text-sm font-medium text-gray-700">
                설명 *
              </label>
              <span className="text-xs text-gray-500">
                {formData.description.length}/3000
              </span>
            </div>
            <textarea
                name="description"
                value={formData.description}
                onChange={handleChange}
                rows="3"
                maxLength={3000}
                className={`w-full border rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 ${
                  errors.description ? 'border-red-500' : 'border-gray-300'
                }`}
                placeholder="플레이스 설명을 입력하세요 (3000자 이내)"
              />
            {errors.description && (
              <p className="mt-1 text-sm text-red-600">{errors.description}</p>
            )}
          </div>

          {/* 위치 */}
          <div>
            <div className="flex justify-between items-center mb-2">
              <label className="block text-sm font-medium text-gray-700">
                위치 *
              </label>
              <span className="text-xs text-gray-500">
                {formData.location.length}/100
              </span>
            </div>
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

          {/* 운영 주체 */}
          <div>
            <div className="flex justify-between items-center mb-2">
              <label className="block text-sm font-medium text-gray-700">
                운영 주체 *
              </label>
              <span className="text-xs text-gray-500">
                {formData.host.length}/100
              </span>
            </div>
                          <input
                type="text"
                name="host"
                value={formData.host}
                onChange={handleChange}
                maxLength={100}
                className={`w-full border rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 ${
                  errors.host ? 'border-red-500' : 'border-gray-300'
                }`}
                placeholder="플레이스 운영 주체를 입력하세요 (100자 이내)"
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
              
              <div className="space-y-2">
                <div className="flex items-center space-x-2">
                  {/* 시간 선택 */}
                  <select
                    name="startHours"
                    value={startHours}
                    onChange={(e) => setStartHours(e.target.value)}
                    className="w-full border border-gray-300 rounded-lg shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 text-sm text-center"
                  >
                    <option value="">시간</option>
                    {Array.from({ length: 24 }, (_, i) => (
                      <option key={i} value={i.toString().padStart(2, '0')}>
                        {i.toString().padStart(2, '0')}시
                      </option>
                    ))}
                  </select>
                  {/* 분 선택 */}
                  <select
                    name="startMinutes"
                    value={startMinutes}
                    onChange={(e) => setStartMinutes(e.target.value)}
                    className="w-full border border-gray-300 rounded-lg shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 text-sm text-center"
                  >
                    <option value="">분</option>
                    {Array.from({ length: 60 }, (_, i) => (
                      <option key={i} value={i.toString().padStart(2, '0')}>
                        {i.toString().padStart(2, '0')}분
                      </option>
                    ))}
                  </select>
                </div>
              </div>
            </div>

            <div className="end-time-section">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                종료 시간
              </label>
              
              <div className="space-y-2">
                <div className="flex items-center space-x-2">
                  {/* 시간 선택 */}
                  <select
                    name="endHours"
                    value={endHours}
                    onChange={(e) => setEndHours(e.target.value)}
                    className="w-full border border-gray-300 rounded-lg shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 text-sm text-center"
                  >
                    <option value="">시간</option>
                    {Array.from({ length: 24 }, (_, i) => (
                      <option key={i} value={i.toString().padStart(2, '0')}>
                        {i.toString().padStart(2, '0')}시
                      </option>
                    ))}
                  </select>
                  {/* 분 선택 */}
                  <select
                    name="endMinutes"
                    value={endMinutes}
                    onChange={(e) => setEndMinutes(e.target.value)}
                    className="w-full border border-gray-300 rounded-lg shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 text-sm text-center"
                  >
                    <option value="">분</option>
                    {Array.from({ length: 60 }, (_, i) => (
                      <option key={i} value={i.toString().padStart(2, '0')}>
                        {i.toString().padStart(2, '0')}분
                      </option>
                    ))}
                  </select>
                </div>
              </div>
            </div>
          </div>

          {/* 시간 태그 선택 */}
          {timeTags.length > 0 && (
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-3">
                시간 태그
              </label>
              <div className="space-y-2">
                {timeTags.map(tag => (
                  <label key={tag.timeTagId} className="flex items-center space-x-3 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={selectedTimeTags.includes(tag.name)}
                      onChange={(e) => handleTimeTagChange(tag.name, e.target.checked)}
                      className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
                    />
                    <span className="text-sm text-gray-700">{tag.name}</span>
                  </label>
                ))}
              </div>
            </div>
          )}

          {/* 버튼 */}
          <div className="flex justify-end space-x-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 bg-gray-300 text-gray-700 font-bold py-2 px-4 rounded-lg hover:bg-gray-400 transition-all duration-200"
            >
              취소
            </button>
            <button
              type="submit"
              disabled={loading}
              className={`flex-1 bg-black text-white font-bold py-2 px-4 rounded-lg hover:bg-gray-800 transition-all duration-200 disabled:opacity-50 ${
                loading ? 'cursor-not-allowed' : ''
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
