import React from 'react';

// 테이블 행 스켈레톤
export const TableRowSkeleton = () => (
    <tr className="border-b border-gray-200 last:border-b-0">
        <td className="p-4">
            <div className="h-4 bg-gray-200 rounded animate-pulse w-3/4"></div>
        </td>
        <td className="p-4">
            <div className="h-4 bg-gray-200 rounded animate-pulse w-1/2 mx-auto"></div>
        </td>
        <td className="p-4">
            <div className="h-4 bg-gray-200 rounded animate-pulse w-20 ml-auto"></div>
        </td>
    </tr>
);

// 테이블 헤더 스켈레톤
export const TableHeaderSkeleton = () => (
    <thead className="table-header">
        <tr>
            <th className="p-4 text-left font-semibold min-w-[120px] w-1/4">
                <div className="h-4 bg-gray-200 rounded animate-pulse w-24"></div>
            </th>
            <th className="p-4 text-center font-semibold min-w-[100px] w-1/6">
                <div className="h-4 bg-gray-200 rounded animate-pulse w-20 mx-auto"></div>
            </th>
            <th className="p-4 text-right font-semibold min-w-[180px] w-1/4">
                <div className="h-4 bg-gray-200 rounded animate-pulse w-16 ml-auto"></div>
            </th>
        </tr>
    </thead>
);

// 테이블 스켈레톤
export const TableSkeleton = ({ rows = 5 }) => (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-x-auto">
        <table className="min-w-full w-full">
            <TableHeaderSkeleton />
            <tbody>
                {Array.from({ length: rows }).map((_, index) => (
                    <TableRowSkeleton key={index} />
                ))}
            </tbody>
        </table>
    </div>
);

// 제목 스켈레톤
export const TitleSkeleton = () => (
    <div className="h-8 bg-gray-200 rounded animate-pulse w-48 mb-2"></div>
);

// 버튼 스켈레톤
export const ButtonSkeleton = () => (
    <div className="h-10 bg-gray-200 rounded-lg animate-pulse w-32"></div>
);

// 플레이스 카드 스켈레톤
export const PlaceCardSkeleton = () => (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
        <div className="h-48 bg-gray-200 animate-pulse"></div>
        <div className="p-4">
            <div className="flex items-start justify-between mb-3">
                <div className="flex-1">
                    <div className="h-5 bg-gray-200 rounded animate-pulse w-3/4 mb-2"></div>
                    <div className="h-4 bg-gray-200 rounded animate-pulse w-1/3"></div>
                </div>
            </div>
            <div className="space-y-2">
                <div className="h-4 bg-gray-200 rounded animate-pulse w-full"></div>
                <div className="h-4 bg-gray-200 rounded animate-pulse w-2/3"></div>
                <div className="h-4 bg-gray-200 rounded animate-pulse w-1/2"></div>
            </div>
        </div>
    </div>
);

// 플레이스 대시보드 전체 스켈레톤
export const PlacePageSkeleton = () => (
    <div className="min-h-screen bg-gray-50">
        {/* 헤더 스켈레톤 */}
        <div className="bg-white shadow-sm border-b border-gray-200 mb-6">
            <div className="px-6 py-4">
                <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
                    <div>
                        <div className="h-8 bg-gray-200 rounded animate-pulse w-48 mb-2"></div>
                        <div className="h-4 bg-gray-200 rounded animate-pulse w-64"></div>
                    </div>
                    <div className="h-12 bg-gray-200 rounded-lg animate-pulse w-40"></div>
                </div>
            </div>
            <div className="px-6 pb-4">
                <div className="flex flex-col sm:flex-row gap-4">
                    <div className="flex-1 h-10 bg-gray-200 rounded-lg animate-pulse"></div>
                    <div className="h-10 bg-gray-200 rounded-lg animate-pulse w-48"></div>
                </div>
            </div>
        </div>
        
        {/* 카드 그리드 스켈레톤 */}
        <div className="px-6">
            <div className="space-y-8">
                <div>
                    <div className="flex items-center mb-4">
                        <div className="h-6 bg-gray-200 rounded animate-pulse w-32 mr-2"></div>
                        <div className="h-6 bg-gray-200 rounded-full animate-pulse w-8"></div>
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                        {Array.from({ length: 8 }).map((_, index) => (
                            <PlaceCardSkeleton key={index} />
                        ))}
                    </div>
                </div>
            </div>
        </div>
    </div>
);
