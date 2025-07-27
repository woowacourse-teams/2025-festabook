import React, { useState, useEffect, useRef } from 'react';
import { placeCategories } from '../constants/categories';
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
          api.get('/places/previews'),
          api.get('/organizations/geography'),
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

    markerRefs.current.forEach(m => m.setMap(null));
    markerRefs.current = [];

    markers.forEach(marker => {
      if (!marker.markerCoordinate) return;
      const m = new naver.maps.Marker({
        position: new naver.maps.LatLng(marker.markerCoordinate.latitude, marker.markerCoordinate.longitude),
        map,
        title: marker.category,
        icon: {
          content: `
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 40 58" width="25" height="40">
              <path d="M20 0C9 0 0 9 0 20c0 11 20 38 20 38s20-27 20-38C40 9 31 0 20 0z" fill='#007aff'/>
              <circle cx="20" cy="20" r="8" fill="#ffffff"/>
            </svg>
          `,
          size: new naver.maps.Size(36, 48),
          anchor: new naver.maps.Point(18, 48)
        }
      });
      naver.maps.Event.addListener(m, 'click', () => setSelectedPlaceId(marker.id));
      markerRefs.current.push(m);
    });
  }, [markers]);

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
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4 overflow-x-auto" style={{ height: '100%', overflowY: 'auto' }}>
            <h3 className="text-xl font-bold mb-4">플레이스 목록</h3>
            <div className="space-y-2">
              {booths.map(booth => (
                <div
                  key={booth.id}
                  className={`p-3 rounded-md flex justify-between items-center bg-gray-50 transition-all duration-150 ${selectedPlaceId === booth.id ? 'border-2 border-blue-500 bg-blue-50' : ''}`}
                >
                  <div>
                    <p className="font-semibold">{booth.title}</p>
                    <p className="text-sm text-gray-500">{placeCategories[booth.category]}</p>
                  </div>
                  <button
                    className="font-bold py-2 px-4 rounded-lg text-sm bg-gray-200 hover:bg-gray-300 text-gray-800"
                    onClick={() => {
                      setSelectedPlace(booth);
                      setModalOpen(true);
                    }}
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
            onSaved={(newMarker) => {
              setModalOpen(false);
              if (newMarker && newMarker.coordinate) {
                setMarkers(prev => prev.map(m => m.id === newMarker.id ? { ...newMarker, markerCoordinate: newMarker.coordinate } : m));
              }
            }}
          />
        </Modal>
      )}
    </div>
  );
};

export default MapSettingsPage;
