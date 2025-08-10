import React, { useEffect, useRef, useState } from 'react';
import api from '../../utils/api';
import { getCategoryIcon } from '../../data/categories';
import { useModal } from '../../hooks/useModal';

type MapSelectorProps = {
  placeId: number;
  onSaved?: (result: any) => void;
};

const NAVER_MAP_CLIENT_ID = '09h8qpimmp';
const KOREA_BOUNDARY = [
  [39.2163345, 123.5125660],
  [39.2163345, 130.5440844],
  [32.8709533, 130.5440844],
  [32.8709533, 123.5125660],
];

declare global {
  interface ImportMetaEnv {
    VITE_NAVER_MAP_CLIENT_ID: string;
  }
  interface ImportMeta {
    env: ImportMetaEnv;
  }
  interface Window {
    naver: any;
    navermap_authFailure: () => void;
  }
}

const MapSelector: React.FC<MapSelectorProps> = ({ placeId, onSaved }) => {
  const mapRef = useRef<HTMLDivElement>(null);
  const markerRef = useRef<any>(null); // 마커를 useRef로 관리
  const [loading, setLoading] = useState(true); // 네이버 지도 API 준비 여부
  const [mapReady, setMapReady] = useState(false); // 실제 지도 준비 여부
  const [coords, setCoords] = useState<{ lat: number; lng: number } | null>(null);
  const [saving, setSaving] = useState(false);
  const [center, setCenter] = useState<{ lat: number; lng: number } | null>(null);
  const [zoom, setZoom] = useState<number | null>(null);
  const [existingMarkers, setExistingMarkers] = useState<any[]>([]); // 모든 부스 마커
  const [currentPlace, setCurrentPlace] = useState<any>(null); // 현재 편집 중인 플레이스 정보
  const existingMarkerRefs = useRef<any[]>([]);
  const [holeBoundary, setHoleBoundary] = useState<any[]>([]); // 폴리곤 홀
  const polygonRef = useRef<any>(null);
  const { openModal, showToast } = useModal();

          // GET /festivals/geography로 폴리곤 홀도 받아오기
  useEffect(() => {
    async function fetchGeography() {
      try {
        const res = await api.get('/festivals/geography');
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
      }
    }
    fetchGeography();
  }, []);

    // 네이버 지도 API
  useEffect(() => {
    if (window.naver && window.naver.maps) {
      setLoading(false);
      return;
    }
    setLoading(true);
    window.navermap_authFailure = function () {
      console.log('네이버 지도 인증 실패');
    };
    const script = document.createElement('script');
    script.src = `https://oapi.map.naver.com/openapi/v3/maps.js?ncpKeyId=${NAVER_MAP_CLIENT_ID}&submodules=gl`;
    script.async = true;
    script.onload = () => setLoading(false);
    script.onerror = () => setLoading(false);
    document.body.appendChild(script);
    return () => {
      document.body.removeChild(script);
    };
  }, []);

  // 모든 부스 마커 가져오기
  useEffect(() => {
    async function fetchMarkers() {
      try {
        const [markersRes, placeRes] = await Promise.all([
          api.get('/places/geographies'),
          api.get(`/places/${placeId}`)
        ]);
        setExistingMarkers(markersRes.data || []);
        setCurrentPlace(placeRes.data);
      } catch (e) {
        setExistingMarkers([]);
        setCurrentPlace(null);
      }
    }
    fetchMarkers();
  }, [placeId]);

  // 지도 및 클릭 이벤트 초기화
  useEffect(() => {
    if (loading || !center || !zoom) return;
    if (!window.naver || !mapRef.current) return;
    const naver = window.naver;
    const mapInstance = new naver.maps.Map(mapRef.current, {
      center: new naver.maps.LatLng(center.lat, center.lng),
      zoom: zoom + 1,
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
    setMapReady(true);
    // 폴리곤 그리기
    if (holeBoundary && holeBoundary.length > 2) {
      const outer = KOREA_BOUNDARY.map(([lat, lng]) => new naver.maps.LatLng(lat, lng));
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
    // 기존 부스 마커 표시 (좌표가 있는 경우만)
    existingMarkerRefs.current.forEach(m => m.setMap(null));
    existingMarkerRefs.current = [];
    existingMarkers.forEach(marker => {
      if (marker.markerCoordinate && marker.markerCoordinate.latitude && marker.markerCoordinate.longitude) {
        const isCurrent = marker.id === placeId;
        const m = new naver.maps.Marker({
          position: new naver.maps.LatLng(marker.markerCoordinate.latitude, marker.markerCoordinate.longitude),
          map: mapInstance,
          title: marker.category,
          icon: {
            content: isCurrent ? `
                <svg width="30" height="40" viewBox="0 0 24 29" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M4 21L12.0711 14.0002L20 21L12.7344 27.5448C12.3565 27.8852 11.7833 27.8877 11.4024 27.5507L4 21Z" fill="#C3C3C3"/>
                  <circle cx="12" cy="12" r="11.5" fill="#FF4B3E" stroke="white"/>
                  <path fill-rule="evenodd" clip-rule="evenodd" d="M18.3634 7.40738C18.5508 7.59491 18.6562 7.84921 18.6562 8.11438C18.6562 8.37954 18.5508 8.63385 18.3634 8.82138L10.868 16.3167C10.769 16.4158 10.6514 16.4944 10.522 16.548C10.3925 16.6016 10.2538 16.6292 10.1137 16.6292C9.9736 16.6292 9.83488 16.6016 9.70545 16.548C9.57602 16.4944 9.45842 16.4158 9.35937 16.3167L5.63537 12.5934C5.53986 12.5011 5.46367 12.3908 5.41126 12.2688C5.35886 12.1468 5.33127 12.0156 5.33012 11.8828C5.32896 11.75 5.35426 11.6183 5.40454 11.4954C5.45483 11.3725 5.52908 11.2609 5.62297 11.167C5.71686 11.0731 5.82852 10.9988 5.95141 10.9486C6.07431 10.8983 6.20599 10.873 6.33877 10.8741C6.47155 10.8753 6.60277 10.9029 6.72477 10.9553C6.84677 11.0077 6.95712 11.0839 7.04937 11.1794L10.1134 14.2434L16.9487 7.40738C17.0416 7.31445 17.1518 7.24073 17.2732 7.19044C17.3946 7.14014 17.5247 7.11426 17.656 7.11426C17.7874 7.11426 17.9175 7.14014 18.0389 7.19044C18.1602 7.24073 18.2705 7.31445 18.3634 7.40738Z" fill="white"/>
                </svg>
            ` : getCategoryIcon(marker.category, false),
            size: new naver.maps.Size(30, 40),
            anchor: new naver.maps.Point(15, 40)
          }
        });
        existingMarkerRefs.current.push(m);
      }
    });
    // 클릭 마커 표시
    const handleClick = (e: any) => {
      const lat = e.coord.lat();
      const lng = e.coord.lng();
      setCoords({ lat, lng });
      const toRemove = existingMarkerRefs.current.find((marker, index) => 
        existingMarkers[index]?.id === placeId
      );
      if (toRemove) {
        toRemove.setMap(null);
      }
      if (markerRef.current) {
        markerRef.current.setMap(null);
      }
            const newMarker = new naver.maps.Marker({
        position: new naver.maps.LatLng(lat, lng),
        map: mapInstance,
        icon: {
          content: `
              <svg width="30" height="40" viewBox="0 0 24 29" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M4 21L12.0711 14.0002L20 21L12.7344 27.5448C12.3565 27.8852 11.7833 27.8877 11.4024 27.5507L4 21Z" fill="#C3C3C3"/>
                <circle cx="12" cy="12" r="11.5" fill="#FF4B3E" stroke="white"/>
                <path fill-rule="evenodd" clip-rule="evenodd" d="M18.3634 7.40738C18.5508 7.59491 18.6562 7.84921 18.6562 8.11438C18.6562 8.37954 18.5508 8.63385 18.3634 8.82138L10.868 16.3167C10.769 16.4158 10.6514 16.4944 10.522 16.548C10.3925 16.6016 10.2538 16.6292 10.1137 16.6292C9.9736 16.6292 9.83488 16.6016 9.70545 16.548C9.57602 16.4944 9.45842 16.4158 9.35937 16.3167L5.63537 12.5934C5.53986 12.5011 5.46367 12.3908 5.41126 12.2688C5.35886 12.1468 5.33127 12.0156 5.33012 11.8828C5.32896 11.75 5.35426 11.6183 5.40454 11.4954C5.45483 11.3725 5.52908 11.2609 5.62297 11.167C5.71686 11.0731 5.82852 10.9988 5.95141 10.9486C6.07431 10.8983 6.20599 10.873 6.33877 10.8741C6.47155 10.8753 6.60277 10.9029 6.72477 10.9553C6.84677 11.0077 6.95712 11.0839 7.04937 11.1794L10.1134 14.2434L16.9487 7.40738C17.0416 7.31445 17.1518 7.24073 17.2732 7.19044C17.3946 7.14014 17.5247 7.11426 17.656 7.11426C17.7874 7.11426 17.9175 7.14014 18.0389 7.19044C18.1602 7.24073 18.2705 7.31445 18.3634 7.40738Z" fill="white"/>
              </svg>
        `,
          size: new naver.maps.Size(30, 40),
          anchor: new naver.maps.Point(15, 40)
        }
      });
      markerRef.current = newMarker;
    };
    naver.maps.Event.addListener(mapInstance, 'click', handleClick);
    return () => {
      naver.maps.Event.clearInstanceListeners(mapInstance);
      if (markerRef.current) {
        markerRef.current.setMap(null);
        markerRef.current = null;
      }
      existingMarkerRefs.current.forEach(m => m.setMap(null));
      existingMarkerRefs.current = [];
      if (polygonRef.current) {
        polygonRef.current.setMap(null);
        polygonRef.current = null;
      }
    };
  }, [loading, center, zoom, existingMarkers, holeBoundary]);

  const handleSave = async () => {
    if (!coords) return;
    setSaving(true);
    try {
      const res = await api.patch(`/places/${placeId}/geographies`, {
        latitude: coords.lat,
        longitude: coords.lng,
      });
      // PATCH 응답 데이터를 명확하게 전달
      const markerData = {
        placeId: res.data.placeId,
        category: currentPlace?.category,
        coordinate: res.data.coordinate,
        markerCoordinate: res.data.coordinate,
        ...res.data
      };
      onSaved && onSaved(markerData);
      showToast('좌표가 저장되었습니다.');
    } catch (e: any) {
      showToast('좌표 저장에 실패했습니다.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div>
      {loading || !center || !zoom || !mapReady ? <div>지도를 불러오는 중입니다...</div> : null}
      <div
        id="map"
        ref={mapRef}
        style={{ width: '100%', height: 400, display: loading || !center || !zoom || !mapReady ? 'none' : 'block' }}
      />
      <div className="mt-4 flex justify-end">
        <button
          onClick={handleSave}
          disabled={!coords || saving}
          className="bg-blue-600 text-white px-4 py-2 rounded disabled:bg-gray-300"
        >
          {saving ? '저장 중...' : '좌표 설정'}
        </button>
      </div>
    </div>
  );
};

export default MapSelector; 