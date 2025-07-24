import React, { useEffect, useRef, useState } from 'react';
import api from '../../utils/api';

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
  const existingMarkerRefs = useRef<any[]>([]);
  const [holeBoundary, setHoleBoundary] = useState<any[]>([]); // 폴리곤 홀
  const polygonRef = useRef<any>(null);

  // GET /organizations/geography로 폴리곤 홀도 받아오기
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
        const res = await api.get('/places/geographies');
        setExistingMarkers(res.data || []);
      } catch (e) {
        setExistingMarkers([]);
      }
    }
    fetchMarkers();
  }, []);

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
    // 기존 부스 마커 표시
    existingMarkerRefs.current.forEach(m => m.setMap(null));
    existingMarkerRefs.current = [];
    existingMarkers.forEach(marker => {
      if (marker.markerCoordinate) {
        const isCurrent = marker.id === placeId;
        const m = new naver.maps.Marker({
          position: new naver.maps.LatLng(marker.markerCoordinate.latitude, marker.markerCoordinate.longitude),
          map: mapInstance,
          title: marker.category,
          icon: {
            content: `
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 40 58" width="25" height="40">
                  <path d="M20 0C9 0 0 9 0 20c0 11 20 38 20 38s20-27 20-38C40 9 31 0 20 0z" fill="${isCurrent ? '#ff3b30' : '#007aff'}"/>
                  <circle cx="20" cy="20" r="8" fill="#ffffff"/>
                </svg>
          `,
          size: new naver.maps.Size(36, 48),
          anchor: new naver.maps.Point(18, 48)
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
      const toRemove = existingMarkerRefs.current.find(marker =>
        marker.getTitle() === existingMarkers.find(m => m.id === placeId)?.category
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
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 40 58" width="25" height="40">
                <path d="M20 0C9 0 0 9 0 20c0 11 20 38 20 38s20-27 20-38C40 9 31 0 20 0z" fill='#ff3b30'/>
                <circle cx="20" cy="20" r="8" fill="#ffffff"/>
              </svg>
        `,
        size: new naver.maps.Size(36, 48),
        anchor: new naver.maps.Point(18, 48)
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
      onSaved && onSaved(res.data);
      alert('좌표가 저장되었습니다!');
    } catch (e: any) {
      alert(e.message);
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