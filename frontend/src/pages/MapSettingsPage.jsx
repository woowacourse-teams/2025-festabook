import React, { useState, useEffect, useRef } from 'react';
import { placeCategories, getCategoryIcon } from '../data/categories';
import Modal from '../components/common/Modal';
import MapSelector from '../components/map/MapSelector';
import api from '../utils/api';

const NAVER_MAP_CLIENT_ID = '09h8qpimmp';
const KOREA_BOUNDARY = [
  [39.2163345, 123.5125660],
  [39.2163345, 130.5440844],
  [32.8709533, 130.5440844],
  [32.8709533, 123.5125660],
];

const MapSettingsPage = () => {
  const [booths, setBooths] = useState([]);
  const [selectedPlace, setSelectedPlace] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [center, setCenter] = useState(null);
  const [zoom, setZoom] = useState(null);
  const [holeBoundary, setHoleBoundary] = useState([]);
  const [markers, setMarkers] = useState([]);
  const [selectedPlaceId, setSelectedPlaceId] = useState(null);
  const [mapReady, setMapReady] = useState(false);
  const [loading, setLoading] = useState(true);
  const [mapHeight, setMapHeight] = useState(() =>
    window.innerWidth < 1024 ? Math.min(Math.max(window.innerWidth * 0.8, 400), 600) : '100%'
  );

  const mapRef = useRef(null);
  const mapInstanceRef = useRef(null);
  const polygonRef = useRef(null);
  const markerRefs = useRef([]);
  const placeListRef = useRef(null);
  const placeItemRefs = useRef({});

  // 1. 스크립트 로드
  useEffect(() => {
    if (window.naver && window.naver.maps) {
      setMapReady(true);
      return;
    }

    setMapReady(false);
    window.navermap_authFailure = () => console.warn('NAVER MAP 인증 실패');

    const script = document.createElement('script');
    script.src = `https://oapi.map.naver.com/openapi/v3/maps.js?ncpKeyId=${NAVER_MAP_CLIENT_ID}&submodules=gl`;
    script.async = true;
    script.onload = () => setMapReady(true);
    script.onerror = () => setMapReady(false);
    document.body.appendChild(script);

    return () => document.body.removeChild(script);
  }, []);

  // 2. 부스, 지리정보, 마커 정보 가져오기
  useEffect(() => {
    async function fetchAll() {
      try {
        const [boothsRes, geoRes, markersRes] = await Promise.all([
          api.get('/places'),
          api.get('/festivals/geography'),
          api.get('/places/geographies')
        ]);
        setBooths(boothsRes.data || []);
        if (geoRes.data?.centerCoordinate) {
          setCenter({
            lat: geoRes.data.centerCoordinate.latitude,
            lng: geoRes.data.centerCoordinate.longitude
          });
        }
        if (geoRes.data?.zoom) setZoom(geoRes.data.zoom);
        if (geoRes.data?.polygonHoleBoundary) setHoleBoundary(geoRes.data.polygonHoleBoundary);
        setMarkers(markersRes.data || []);
      } catch {
        setBooths([]);
        setMarkers([]);
      } finally {
        setLoading(false);
      }
    }
    fetchAll();
  }, []);

  // 3. 지도 초기화 (최초 1회)
  useEffect(() => {
    if (loading || !mapReady || !center || !zoom || mapInstanceRef.current || !mapRef.current) return;

    const naver = window.naver;
    const map = new naver.maps.Map(mapRef.current, {
      center: new naver.maps.LatLng(center.lat, center.lng),
      zoom: zoom + 2,
      gl: true,
      customStyleId: '4b934c2a-71f5-4506-ab90-4e6aa14c0820',
      logoControl: false,
      mapDataControl: false,
      scaleControl: false,
      zoomControl: true,
      zoomControlOptions: {
        position: naver.maps.Position.TOP_RIGHT,
        style: naver.maps.ZoomControlStyle.SMALL
      }
    });

    mapInstanceRef.current = map;

    // 지도 클릭 시 선택 해제
    naver.maps.Event.addListener(map, 'click', () => {
      setSelectedPlaceId(null);
    });

    if (holeBoundary.length > 2) {
      const outer = KOREA_BOUNDARY.map(([lat, lng]) => new naver.maps.LatLng(lat, lng));
      const hole = holeBoundary.map((p) => new naver.maps.LatLng(p.latitude, p.longitude));
      polygonRef.current = new naver.maps.Polygon({
        map,
        paths: [outer, hole],
        fillColor: '#1b1b1b',
        fillOpacity: 0.3,
        strokeColor: '#1b1b1b',
        strokeOpacity: 0.6,
        strokeWeight: 3
      });
    }
  }, [loading, mapReady, center, zoom, holeBoundary]);

  // 4. 마커 업데이트만 따로
  useEffect(() => {
    if (!mapInstanceRef.current || !window.naver) return;
    const naver = window.naver;
    const map = mapInstanceRef.current;

    // 기존 마커들을 완전히 제거
    markerRefs.current.forEach(m => {
      if (m && m.setMap) {
        m.setMap(null);
      }
    });
    markerRefs.current = [];

    // 새로운 마커들 생성
    markers.forEach(marker => {
      if (!marker.markerCoordinate || !marker.markerCoordinate.latitude || !marker.markerCoordinate.longitude) return;
      const isSelected = selectedPlaceId === marker.id;
      const iconSize = isSelected ? { width: 40, height: 50 } : { width: 30, height: 40 };
      const anchorPoint = isSelected ? { x: 20, y: 50 } : { x: 15, y: 40 };
      
      const m = new naver.maps.Marker({
        position: new naver.maps.LatLng(marker.markerCoordinate.latitude, marker.markerCoordinate.longitude),
        map,
        title: marker.category,
        icon: {
          content: getCategoryIcon(marker.category, isSelected),
          size: new naver.maps.Size(iconSize.width, iconSize.height),
          anchor: new naver.maps.Point(anchorPoint.x, anchorPoint.y)
        }
      });
      naver.maps.Event.addListener(m, 'click', () => {
        setSelectedPlaceId(marker.id);
        // 해당 플레이스로 스크롤
        setTimeout(() => {
          const placeItem = placeItemRefs.current[marker.id];
          if (placeItem && placeListRef.current) {
            placeItem.scrollIntoView({ 
              behavior: 'smooth', 
              block: 'center' 
            });
          }
        }, 100);
      });
      markerRefs.current.push(m);
    });
  }, [markers, selectedPlaceId]);

  // 5. 반응형 높이
  useEffect(() => {
    const updateHeight = () => {
      const newHeight = window.innerWidth < 1024
        ? Math.min(Math.max(window.innerWidth * 0.8, 400), 600)
        : '100%';
      setMapHeight(newHeight);
    };
    window.addEventListener('resize', updateHeight);
    return () => window.removeEventListener('resize', updateHeight);
  }, []);

  // 사이드바 변화 감지를 위한 전역 이벤트 리스너
  useEffect(() => {
    const handleSidebarToggle = () => {
      if (mapInstanceRef.current && window.naver) {
        setTimeout(() => {
          window.naver.maps.Event.trigger(mapInstanceRef.current, 'resize');
        }, 300);
      }
    };
    
    window.addEventListener('sidebarToggle', handleSidebarToggle);
    return () => window.removeEventListener('sidebarToggle', handleSidebarToggle);
  }, []);

  // 6. ESC로 모달 닫기
  useEffect(() => {
    if (!modalOpen) return;
    const handleKeyDown = (e) => {
      if (e.key === 'Escape') setModalOpen(false);
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [modalOpen]);

  // 플레이스 클릭 시 해당 마커로 이동 및 강조 표시
  const handlePlaceClick = (place) => {
    const marker = markers.find(m => m.id === place.id);
    if (marker?.markerCoordinate?.latitude && marker?.markerCoordinate?.longitude && mapInstanceRef.current) {
      // 지도 중심을 해당 마커로 이동
      const position = new window.naver.maps.LatLng(
        marker.markerCoordinate.latitude,
        marker.markerCoordinate.longitude
      );
      mapInstanceRef.current.setCenter(position);
      mapInstanceRef.current.setZoom(18); // 확대
      
      // 선택된 플레이스 표시
      setSelectedPlaceId(place.id);
    }
  };

  return (
    <div style={{ height: '100%', boxSizing: 'border-box' }}>
      <h2 className="text-3xl font-bold mb-6">지도 설정</h2>
      <div className="flex flex-col lg:flex-row gap-8" style={{ height: 'calc(100% - 64px)' }}>
        {/* 지도 */}
        <div className="flex-grow flex justify-center items-start" style={{ height: '100%' }}>
          <div
            className="bg-gray-200 rounded-lg shadow-sm border border-gray-300 flex items-center justify-center"
            style={{ width: '100%', height: '100%', minWidth: 320, minHeight: 320, maxWidth: '100vw', maxHeight: '100%' }}
          >
            {!center || !zoom || !mapReady ? (
              <div>지도를 불러오는 중입니다...</div>
            ) : (
              <div
                ref={mapRef}
                className="w-full"
                style={{
                  height: mapHeight,
                  ...(window.innerWidth < 1024 ? { minHeight: 320, maxHeight: 600 } : {})
                }}
              />
            )}
          </div>
        </div>
        {/* 플레이스 목록 */}
        <div className="w-full lg:max-w-[420px]" style={{ height: '100%' }}>
          <div 
            ref={placeListRef}
            className="bg-white rounded-lg shadow-sm border border-gray-200 p-4 overflow-x-auto" 
            style={{ height: '100%', overflowY: 'auto' }}
          >
            <h3 className="text-xl font-bold mb-4">플레이스 목록</h3>

            <div className="space-y-2">
              {booths.map(booth => {
                const matchedMarker = markers.find(m => m.id === booth.id);
                const hasCoordinates = matchedMarker?.markerCoordinate?.latitude != null && matchedMarker?.markerCoordinate?.longitude != null;

                return (
                  <div
                    key={booth.id}
                    ref={(el) => {
                      if (el) {
                        placeItemRefs.current[booth.id] = el;
                      }
                    }}
                    className={`p-3 rounded-md flex justify-between items-center bg-gray-50 transition-all duration-150 cursor-pointer hover:bg-gray-100 ${selectedPlaceId === booth.id ? 'border-2 border-blue-500 bg-blue-50' : ''}`}
                    onClick={() => handlePlaceClick(booth)}
                  >
                    <div>
                      <p className="font-semibold">
                        {(() => {
                          // etcPlace 카테고리들은 카테고리명을 제목으로 사용
                          const etcPlaceCategories = ['TRASH_CAN', 'SMOKING', 'TOILET'];
                          if (etcPlaceCategories.includes(booth.category)) {
                            return placeCategories[booth.category];
                          }
                          // 일반 플레이스는 기존 로직 사용
                          return booth.title?.trim() ? booth.title : '플레이스 이름을 지정하여 주십시오.';
                        })()}
                      </p>
                      <div className="flex items-center gap-2">
                        {(() => {
                          // etcPlace 카테고리들은 이미 제목에 카테고리명이 표시되므로 하단에는 표시하지 않음
                          const etcPlaceCategories = ['TRASH_CAN', 'SMOKING', 'TOILET'];
                          if (!etcPlaceCategories.includes(booth.category)) {
                            return <p className="text-sm text-gray-500">{placeCategories[booth.category]}</p>;
                          }
                          return null;
                        })()}
                        {!hasCoordinates && (
                          <p className="text-xs text-red-500">좌표 미설정</p>
                        )}
                      </div>
                    </div>
                    <button
                      className={`font-bold py-2 px-4 rounded-lg text-sm ${
                        hasCoordinates 
                          ? 'bg-blue-200 hover:bg-blue-300 text-blue-800' 
                          : 'bg-red-200 hover:bg-red-300 text-red-800'
                      }`}
                      onClick={() => {
                        setSelectedPlace(booth);
                        setModalOpen(true);
                      }}
                    >
                      {hasCoordinates ? '좌표 수정' : '좌표 설정'}
                    </button>
                  </div>
                );
              })}
            </div>
          </div>
        </div>
      </div>
      {modalOpen && selectedPlace && (
        <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} maxWidth="max-w-2xl">
          <h3 className="text-xl font-bold mb-4">{selectedPlace.title} 좌표 설정</h3>
          <MapSelector
            placeId={selectedPlace.id}
            onSaved={(newMarker) => {
              setModalOpen(false);
              if (newMarker) {
                // API 응답에서 좌표 정보 추출
                const coordinate = newMarker.coordinate || newMarker.markerCoordinate;
                if (coordinate) {
                  setMarkers(prev => {
                    const existingIndex = prev.findIndex(m => m.id === newMarker.id);
                    if (existingIndex >= 0) {
                      // 기존 마커 업데이트
                      const updated = [...prev];
                      updated[existingIndex] = { ...newMarker, markerCoordinate: coordinate };
                      return updated;
                    } else {
                      // 새 마커 추가
                      return [...prev, { ...newMarker, markerCoordinate: coordinate }];
                    }
                  });
                  // 마커 업데이트 후 지도 리사이즈 트리거
                  setTimeout(() => {
                    if (mapInstanceRef.current && window.naver) {
                      window.naver.maps.Event.trigger(mapInstanceRef.current, 'resize');
                    }
                  }, 100);
                }
              }
            }}
          />
        </Modal>
      )}
    </div>
  );
};

export default MapSettingsPage;
