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

// 플레이스 대시보드 전체 스켈레톤
export const PlacePageSkeleton = () => (
    <div>
        <div className="flex justify-between items-center mb-6">
            <TitleSkeleton />
            <ButtonSkeleton />
        </div>
        
        <div className="space-y-10">
            <div>
                <div className="h-6 bg-gray-200 rounded animate-pulse w-32 ml-1 mt-10 mb-2"></div>
                <TableSkeleton rows={3} />
            </div>
            
            <div>
                <div className="h-6 bg-gray-200 rounded animate-pulse w-32 ml-1 mt-10 mb-2"></div>
                <TableSkeleton rows={2} />
            </div>
        </div>
    </div>
);
