import React, { useState, useEffect, useRef } from 'react';
import { placeCategories, getCategoryIcon } from '../data/categories';
import Modal from '../components/common/Modal';
import MapSelector from '../components/map/MapSelector';
import { usePage } from '../hooks/usePage';
import { useModal } from '../hooks/useModal';
import { timeTagAPI } from '../utils/api';
import api from '../utils/api';

const NAVER_MAP_CLIENT_ID = '09h8qpimmp';
const KOREA_BOUNDARY = [
  [39.2163345, 123.5125660],
  [39.2163345, 130.5440844],
  [32.8709533, 130.5440844],
  [32.8709533, 123.5125660],
];

const PlaceGeographyPage = () => {
  const { setPage } = usePage();
  const { openModal } = useModal();
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

  // 시간 태그 관련 상태
  const [timeTags, setTimeTags] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedTimeTags, setSelectedTimeTags] = useState([]);
  const [filteredPlaces, setFilteredPlaces] = useState([]);

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

  // 시간 태그 데이터 로드
  useEffect(() => {
    const fetchTimeTags = async () => {
      try {
        const timeTagData = await timeTagAPI.getTimeTags();
        setTimeTags(timeTagData);
      } catch (error) {
        console.error('시간 태그 로드 실패:', error);
      }
    };
    fetchTimeTags();
  }, []);



  // 초기 데이터 로드 시 filteredPlaces 설정
  useEffect(() => {
    if (booths && booths.length > 0) {
      setFilteredPlaces(booths);
    }
  }, [booths]);

  // 필터링 로직 (PlacePage와 동일한 방식)
  useEffect(() => {
    if (!booths || booths.length === 0) {
      setFilteredPlaces([]);
      return;
    }

    let filtered = [...booths];

    // 검색어 필터링
    if (searchTerm.trim()) {
      const searchLower = searchTerm.toLowerCase();
      filtered = filtered.filter(place => {
        const title = place.title || place.name || '';
        const categoryName = getCategoryName(place.category) || '';
        return title.toLowerCase().includes(searchLower) ||
               categoryName.toLowerCase().includes(searchLower);
      });
    }

    // 시간 태그 필터링 (PlacePage와 동일한 로직)
    if (selectedTimeTags.length > 0) {
      const selectedTagNames = selectedTimeTags.map(tagId => {
        const tag = timeTags.find(t => t.timeTagId === tagId);
        return tag ? tag.name : null;
      }).filter(name => name !== null);
      
      filtered = filtered.filter(place => {
        if (!place.timeTags) return false;
        
        // timeTags가 문자열 배열인지 객체 배열인지 확인
        const placeTagNames = place.timeTags.map(tag => 
          typeof tag === 'string' ? tag : tag.name
        );
        
        return selectedTagNames.some(tagName => 
          placeTagNames.includes(tagName)
        );
      });
    }

    setFilteredPlaces(filtered);
  }, [booths, searchTerm, selectedTimeTags, timeTags]);

  // 헬퍼 함수
  const getCategoryName = (categoryKey) => {
    return placeCategories[categoryKey] || '';
  };

  // 시간 태그 필터 핸들러
  const handleTimeTagFilterChange = (tagId, isChecked) => {
    if (isChecked) {
      setSelectedTimeTags(prev => [...prev, tagId]);
    } else {
      setSelectedTimeTags(prev => prev.filter(id => id !== tagId));
    }
  };

  // 검색어 핸들러
  const handleSearchChange = (e) => {
    setSearchTerm(e.target.value);
  };

  // 시간 태그 추가 핸들러
  const handleTimeTagAdd = async (data) => {
    if (!data.name || !data.name.trim()) {
      console.error('시간 태그 이름이 없습니다.');
      return;
    }
    try {
      await timeTagAPI.createTimeTag({ name: data.name.trim() });
      // 시간 태그 목록 새로고침
      const timeTagData = await timeTagAPI.getTimeTags();
      setTimeTags(timeTagData);
    } catch (error) {
      console.error('시간 태그 추가 실패:', error);
    }
  };

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

  // 4. 마커 업데이트만 따로 (필터링된 결과만 표시)
  useEffect(() => {
    if (!mapInstanceRef.current || !window.naver || !filteredPlaces || !markers) return;
    const naver = window.naver;
    const map = mapInstanceRef.current;

    // 기존 마커들을 완전히 제거
    markerRefs.current.forEach(m => {
      if (m && m.setMap) {
        m.setMap(null);
      }
    });
    markerRefs.current = [];

    // 필터링된 플레이스에 해당하는 마커만 표시
    const filteredPlaceIds = new Set(filteredPlaces.map(place => place.id || place.placeId));
    const filteredMarkers = markers.filter(marker => 
      filteredPlaceIds.has(marker.placeId || marker.id)
    );

    // 새로운 마커들 생성
    filteredMarkers.forEach(marker => {
      if (!marker.markerCoordinate || !marker.markerCoordinate.latitude || !marker.markerCoordinate.longitude) return;
      const markerId = marker.placeId || marker.id;
      const isSelected = selectedPlaceId === markerId;
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
        // marker.id 대신 marker.placeId를 우선 사용하고, 없으면 marker.id 사용
        const markerId = marker.placeId || marker.id;
        setSelectedPlaceId(markerId);
        // 해당 플레이스로 스크롤
        setTimeout(() => {
          const placeItem = placeItemRefs.current[markerId];
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
  }, [markers, selectedPlaceId, filteredPlaces]);

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
    // place.id 대신 place.placeId를 우선 사용하고, 없으면 place.id 사용
    const placeId = place.placeId || place.id;
    const marker = markers.find(m => (m.placeId || m.id) === placeId);
    if (marker?.markerCoordinate?.latitude && marker?.markerCoordinate?.longitude && mapInstanceRef.current) {
      // 지도 중심을 해당 마커로 이동
      const position = new window.naver.maps.LatLng(
        marker.markerCoordinate.latitude,
        marker.markerCoordinate.longitude
      );
      mapInstanceRef.current.setCenter(position);
      mapInstanceRef.current.setZoom(18); // 확대
      
      // 선택된 플레이스 표시
      setSelectedPlaceId(placeId);
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

            {/* 검색창 */}
            <div className="mb-4">
              <input
                type="text"
                placeholder="플레이스 이름 또는 카테고리 검색..."
                value={searchTerm}
                onChange={handleSearchChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
              />
            </div>

            {/* 시간 태그 필터 */}
            <div className="mb-4">
              <div className="flex justify-between items-center mb-2">
                <span className="text-sm font-medium text-gray-700">시간 태그 필터</span>
                <button
                  onClick={() => openModal('timeTagAdd', { onSave: handleTimeTagAdd })}
                  className="text-blue-600 hover:text-blue-800 text-xs font-medium"
                >
                  + 추가
                </button>
              </div>
              {timeTags && timeTags.length > 0 ? (
                <div className="flex flex-wrap gap-1">
                  {timeTags.map(tag => (
                    <label
                      key={tag.timeTagId}
                      className="inline-flex items-center cursor-pointer"
                    >
                      <input
                        type="checkbox"
                        checked={selectedTimeTags.includes(tag.timeTagId)}
                        onChange={(e) => handleTimeTagFilterChange(tag.timeTagId, e.target.checked)}
                        className="sr-only"
                      />
                      <span
                        className={`px-2 py-1 text-xs rounded-full border transition-colors ${
                          selectedTimeTags.includes(tag.timeTagId)
                            ? 'bg-blue-500 text-white border-blue-500'
                            : 'bg-gray-100 text-gray-700 border-gray-300 hover:bg-gray-200'
                        }`}
                      >
                        {tag.name}
                      </span>
                    </label>
                  ))}
                </div>
              ) : (
                <p className="text-xs text-gray-500">시간 태그가 없습니다.</p>
              )}
            </div>

            {filteredPlaces.length > 0 ? (
              <div className="space-y-2">
                {filteredPlaces.map(booth => {
                  const boothId = booth.placeId || booth.id;
                  const matchedMarker = markers.find(m => (m.placeId || m.id) === boothId);
                  const hasCoordinates = matchedMarker?.markerCoordinate?.latitude != null && matchedMarker?.markerCoordinate?.longitude != null;

                  return (
                    <div
                      key={boothId}
                      ref={(el) => {
                        if (el) {
                          placeItemRefs.current[boothId] = el;
                        }
                      }}
                      className={`p-3 rounded-md flex justify-between items-center bg-gray-50 transition-all duration-150 cursor-pointer hover:bg-gray-100 ${selectedPlaceId === boothId ? 'border-2 border-blue-500 bg-blue-50' : ''}`}
                      onClick={() => handlePlaceClick(booth)}
                    >
                      <div>
                        <p className="font-semibold">
                          {(() => {
                            // 일반 플레이스는 기존 로직 사용
                            return booth.title?.trim() ? booth.title : '플레이스 이름을 지정하여 주십시오.';
                          })()}
                        </p>
                        <div className="flex items-center gap-2">
                          <p className="text-sm text-gray-500">{getCategoryName(booth.category)}</p>
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
            ) : (
              /* 플레이스가 없을 때 또는 필터링 결과가 없을 때 */
              <div className="text-center py-12">
                <i className="fas fa-map-marker-alt text-4xl text-gray-400 mb-4"></i>
                {booths.length === 0 ? (
                  <>
                    <p className="text-gray-500 mb-4">등록된 플레이스가 없습니다</p>
                    <button
                      onClick={() => setPage('place')}
                      className="bg-gray-800 hover:bg-gray-900 text-white px-4 py-2 rounded-lg transition-colors"
                    >
                      플레이스 관리로 이동
                    </button>
                  </>
                ) : (
                  <p className="text-gray-500">필터 조건에 맞는 플레이스가 없습니다</p>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
      {modalOpen && selectedPlace && (
        <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} maxWidth="max-w-2xl">
          <h3 className="text-xl font-bold mb-4">{selectedPlace.title} 좌표 설정</h3>
          <MapSelector
            placeId={selectedPlace.placeId || selectedPlace.id}
            onSaved={(newMarker) => {
              setModalOpen(false);
              if (newMarker) {
                // API 응답에서 좌표 정보 추출
                const coordinate = newMarker.coordinate || newMarker.markerCoordinate;
                if (coordinate) {
                  setMarkers(prev => {
                    // newMarker.id 대신 newMarker.placeId를 우선 사용하고, 없으면 newMarker.id 사용
                    const markerId = newMarker.placeId || newMarker.id;
                    const existingIndex = prev.findIndex(m => (m.placeId || m.id) === markerId);
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

export default PlaceGeographyPage;
