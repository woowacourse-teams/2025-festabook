import React from 'react';

const GenericPage = ({ title }) => (
    <div><h2 className="text-3xl font-bold mb-6">{title}</h2><div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6"><p className="text-gray-500">{title} 페이지 콘텐츠가 여기에 표시됩니다.</p></div></div>
);

export default GenericPage;
