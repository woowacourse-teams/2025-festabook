import React from 'react';
import { useData } from '../hooks/useData';
import { placeCategories } from '../constants/categories';

const MapSettingsPage = () => {
    const { booths } = useData();
    return (
        <div>
            <h2 className="text-3xl font-bold mb-6">지도 설정</h2>
            <div style={{ width: '100%', height: '400px' }} className="bg-gray-200 rounded-lg shadow-sm border border-gray-300 mb-6 flex items-center justify-center">
                <p className="text-gray-500 font-semibold">지도 표시 영역</p>
            </div>
            <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4 overflow-x-auto">
                <h3 className="text-xl font-bold mb-4">플레이스 목록</h3>
                <div className="space-y-2">
                    {booths.map(booth => (
                        <div key={booth.id} className="p-3 rounded-md flex justify-between items-center bg-gray-50">
                            <div>
                                <p className="font-semibold">{booth.title}</p>
                                <p className="text-sm text-gray-500">{placeCategories[booth.category]}</p>
                            </div>
                            <button
                                className="font-bold py-2 px-4 rounded-lg text-sm bg-gray-200 hover:bg-gray-300 text-gray-800"
                            >
                                좌표 설정
                            </button>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default MapSettingsPage;
