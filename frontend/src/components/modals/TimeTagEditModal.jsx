import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';

const MAX_NAME_LEN = 40;

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
    if (name === 'name' && value.length > MAX_NAME_LEN) {
      showToast(`시간 태그 이름은 ${MAX_NAME_LEN}자 이내로 입력해주세요.`);
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
    } else if (formData.name.length > MAX_NAME_LEN) {
      newErrors.name = `시간 태그 이름은 ${MAX_NAME_LEN}자를 초과할 수 없습니다.`;
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
    <Modal isOpen={true} onClose={onClose} maxWidth="max-w-sm">
      <h3 className="text-xl font-bold mb-6">시간 태그 수정</h3>

      <form onSubmit={handleSubmit} className="space-y-4">
        {/* 시간 태그 이름 */}
        <div>
          <div className="flex justify-between items-center mb-1">
            <label className="block text-sm font-medium text-gray-700">
              시간 태그 이름
            </label>
            <span className="text-xs text-gray-500">
              {formData.name.length}/{MAX_NAME_LEN}
            </span>
          </div>
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleChange}
            placeholder="예: 1일차 오후, 2일차 저녁 등"
            className={`mt-1 block w-full border rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 ${
              errors.name ? 'border-red-500' : 'border-gray-300'
            }`}
          />
          {errors.name && (
            <p className="mt-1 text-sm text-red-600">{errors.name}</p>
          )}
        </div>
      </form>

      {/* 버튼 */}
      <div className="mt-6 flex justify-end w-full relative z-10">
        <div className="space-x-3">
          <button
            type="button"
            onClick={onClose}
            className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded-lg transition-colors duration-200"
            disabled={loading}
          >
            취소
          </button>
          <button
            type="submit"
            onClick={handleSubmit}
            disabled={loading}
            className="bg-gray-800 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded-lg transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {loading ? (
              <div className="flex items-center">
                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                수정 중...
              </div>
            ) : (
              '수정 완료'
            )}
          </button>
        </div>
      </div>
    </Modal>
  );
};

export default TimeTagEditModal;