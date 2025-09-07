// src/utils/imageUpload.js

/**
 * 파일 유효성을 검사하는 함수
 * @param {File} file - 검사할 파일
 * @returns {Object} - { isValid: boolean, message: string }
 */
export const validateImageFile = (file) => {
  const allowedTypes = ['image/png', 'image/jpeg', 'image/jpg'];
  const maxSize = 10 * 1024 * 1024; // 10MB

  if (!allowedTypes.includes(file.type)) {
    return {
      isValid: false,
      message: 'PNG, JPG, JPEG 파일만 업로드 가능합니다.'
    };
  }

  if (file.size > maxSize) {
    return {
      isValid: false,
      message: '파일 크기는 10MB 이하여야 합니다.'
    };
  }

  return {
    isValid: true,
    message: ''
  };
};
