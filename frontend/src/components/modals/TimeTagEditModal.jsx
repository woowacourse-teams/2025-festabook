import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';

const TimeTagEditModal = ({ timeTag, onSave, onClose, showToast }) => {
  const [formData, setFormData] = useState({
    name: ''
  });
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  // timeTag 데이터로 폼 초기화
  useEffect(() => {
    if (timeTag) {
      setFormData({
        name: timeTag.name || ''
      });
    }
  }, [timeTag]);

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
    if (name === 'name' && value.length > 50) {
      showToast('시간 태그 이름은 50자 이내로 입력해주세요.');
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

  const validateForm = () => {
    const newErrors = {};

    if (!formData.name.trim()) {
      newErrors.name = '시간 태그 이름을 입력해주세요.';
    } else if (formData.name.length > 50) {
      newErrors.name = '시간 태그 이름은 50자를 초과할 수 없습니다.';
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
      await onSave(timeTag.timeTagId, formData);
      onClose();
    } catch (error) {
      console.error('Failed to update time tag:', error);
      // onSave에서 이미 에러 처리를 하므로 여기서는 별도 처리 없음
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal isOpen={true} onClose={onClose} maxWidth="max-w-md">
      <div className="p-6">
        <h3 className="text-xl font-bold mb-6">시간 태그 수정</h3>
        
        <form onSubmit={handleSubmit} className="space-y-4">
          {/* 시간 태그 이름 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              시간 태그 이름 *
            </label>
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              placeholder="예: 1일차 오후, 2일차 저녁 등"
              className={`w-full border rounded-lg shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 ${
                errors.name ? 'border-red-500' : 'border-gray-300'
              }`}
              maxLength={50}
            />
            {errors.name && (
              <p className="mt-1 text-sm text-red-600">{errors.name}</p>
            )}
            <p className="mt-1 text-xs text-gray-500">
              {formData.name.length}/50자
            </p>
          </div>

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
              className={`flex-1 bg-blue-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-blue-700 transition-all duration-200 disabled:opacity-50 ${
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

export default TimeTagEditModal;