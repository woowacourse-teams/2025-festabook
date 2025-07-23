import React, { useState, useEffect, useRef } from 'react';
import { placeCategories } from '../constants/categories';
import Modal from '../components/common/Modal';
import MapSelector from '../components/map/MapSelector';
import api from '../utils/api';

const DEFAULT_CENTER = { lat: 37.5665, lng: 126.9780 };
const DEFAULT_ZOOM = 15;
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
  const [center, setCenter] = useState(DEFAULT_CENTER);
  const [zoom, setZoom] = useState(DEFAULT_ZOOM);
  const [loading, setLoading] = useState(true);
  const [mapReady, setMapReady] = useState(false);
  const [holeBoundary, setHoleBoundary] = useState([]);
  const [markers, setMarkers] = useState([]);
  const mapRef = useRef(null);
  const polygonRef = useRef(null);
  const markerRefs = useRef([]);
  const [mapHeight, setMapHeight] = useState(() => {
    return window.innerWidth < 1024 ? Math.min(Math.max(window.innerWidth * 0.8, 400), 600) : '100%';
  });

  useEffect(() => {
    async function fetchBooths() {
      try {
        const res = await api.get('/places/previews');
        setBooths(res.data || []);
      } catch (e) {
        setBooths([]);
      }
    }
    fetchBooths();
  }, []);

  useEffect(() => {
    async function fetchGeography() {
      try {
        const res = await api.get('/organizations/geography');
        if (res.data && res.data.centerCoordinate) {
          setCenter({
            lat: res.data.centerCoordinate.latitude,
            lng: res.data.centerCoordinate.longitude,
          });
        }
        if (res.data && res.data.zoom) {
          setZoom(res.data.zoom);
        }
        if (res.data && res.data.polygonHoleBoundary) {
          setHoleBoundary(res.data.polygonHoleBoundary);
        }
      } catch (e) {
        // 실패 시 기본값 유지
      } finally {
        setLoading(false);
      }
    }
    fetchGeography();
  }, []);

    // 네이버 지도 API
    useEffect(() => {
        if (window.naver && window.naver.maps) {
            setMapReady(true);
            return;
        }
        setMapReady(false);
        window.navermap_authFailure = function () {
            console.log('네이버 지도 인증 실패');
        };
        const script = document.createElement('script');
        script.src = `https://oapi.map.naver.com/openapi/v3/maps.js?ncpKeyId=${NAVER_MAP_CLIENT_ID}&submodules=gl`;
        script.async = true;
        script.onload = () => setMapReady(true);
        script.onerror = () => setMapReady(false);
        document.body.appendChild(script);
        return () => {
            document.body.removeChild(script);
        };
    }, []);

    // 마커 좌표 목록 API
    useEffect(() => {
      async function fetchMarkers() {
        try {
          const res = await api.get('/places/geographies');
          setMarkers(res.data || []);
        } catch (e) {
          setMarkers([]);
        }
      }
      fetchMarkers();
    }, []);

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

    useEffect(() => {
        if (loading || !mapReady) return;
        if (!window.naver || !mapRef.current) return;
        const naver = window.naver;
        const mapInstance = new naver.maps.Map(mapRef.current, {
            center: new naver.maps.LatLng(center.lat, center.lng),
            zoom: zoom + 2,
            gl: true,
            customStyleId: '4b934c2a-71f5-4506-ab90-4e6aa14c0820',
            zoomControl: true,
            zoomControlOptions: {
                position: naver.maps.Position.TOP_RIGHT,
                style: naver.maps.ZoomControlStyle.SMALL
            }
        });
        // 폴리곤 그리기
        if (holeBoundary && holeBoundary.length > 2) {
            // 대한민국 전체 외곽선
            const outer = KOREA_BOUNDARY.map(([lat, lng]) => new naver.maps.LatLng(lat, lng));
            // 서버에서 받은 홀(구멍) 외곽선
            const hole = holeBoundary.map((p) => new naver.maps.LatLng(p.latitude, p.longitude));
            if (polygonRef.current) {
                polygonRef.current.setMap(null);
            }
            polygonRef.current = new naver.maps.Polygon({
                map: mapInstance,
                paths: [outer, hole],
                fillColor: '#1b1b1b',
                fillOpacity: 0.3,
                strokeColor: '#1b1b1b',
                strokeOpacity: 0.6,
                strokeWeight: 3
            });
        }
        // 마커 그리기
        markerRefs.current.forEach(m => m.setMap(null));
        markerRefs.current = [];
        markers.forEach(marker => {
          if (marker.markerCoordinate) {
            const m = new naver.maps.Marker({
              position: new naver.maps.LatLng(marker.markerCoordinate.latitude, marker.markerCoordinate.longitude),
              map: mapInstance,
              title: marker.category
            });
            markerRefs.current.push(m);
          }
        });
        return () => {
            naver.maps.Event.clearInstanceListeners(mapInstance);
            if (polygonRef.current) {
                polygonRef.current.setMap(null);
                polygonRef.current = null;
            }
            markerRefs.current.forEach(m => m.setMap(null));
            markerRefs.current = [];
        };
    }, [loading, mapReady, center, zoom, holeBoundary, markers]);

    useEffect(() => {
      if (!modalOpen) return;
      // ESC 키 이벤트 핸들러
      const handleKeyDown = (e) => {
        if (e.key === 'Escape') {
          setModalOpen(false);
        }
      };
      window.addEventListener('keydown', handleKeyDown);
      return () => window.removeEventListener('keydown', handleKeyDown);
    }, [modalOpen]);

    return (
        <div style={{ height: '100%', boxSizing: 'border-box' }}>
            <h2 className="text-3xl font-bold mb-6">지도 설정</h2>
            <div className="mb-2 flex gap-4 items-center">
            </div>
            <div className="flex flex-col lg:flex-row gap-8" style={{ height: 'calc(100% - 64px)' }}>
                {/* 지도 영역 */}
                <div className="flex-grow flex justify-center items-start" style={{ height: '100%' }}>
                    <div
                        className="bg-gray-200 rounded-lg shadow-sm border border-gray-300 flex items-center justify-center"
                        style={{ width: '100%', height: '100%', minWidth: 320, minHeight: 320, maxWidth: '100vw', maxHeight: '100%' }}
                        >
                        {loading || !mapReady ? (
                        <div>지도를 불러오는 중입니다...</div>
                        ) : (
                            <div
                                id="main-map"
                                ref={mapRef}
                                className="w-full"
                                style={{
                                    height: mapHeight,
                                }}
                            />
                        )}
                    </div>
                </div>
                {/* 플레이스 목록 */}
                <div className="w-full lg:max-w-[420px]" style={{ height: '100%' }}>
                    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4 overflow-x-auto" style={{ height: '100%', overflowY: 'auto' }}>
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
                                        onClick={() => { setSelectedPlace(booth); setModalOpen(true); }}
                                    >
                                        좌표 설정
                                    </button>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </div>
            {modalOpen && selectedPlace && (
                <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} maxWidth="max-w-2xl">
                    <h3 className="text-xl font-bold mb-4">{selectedPlace.title} 좌표 설정</h3>
                    <MapSelector
                        placeId={selectedPlace.id}
                        onSaved={() => { setModalOpen(false); }}
                    />
                </Modal>
            )}
        </div>
    );
};

export default MapSettingsPage;
