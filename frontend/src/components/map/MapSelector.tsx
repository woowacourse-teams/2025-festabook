import React, { useEffect, useRef, useState } from 'react';
import api from '../../utils/api';

type MapSelectorProps = {
  placeId: number;
  onSaved?: (result: any) => void;
};

const DEFAULT_CENTER = { lat: 37.5665, lng: 126.9780 };
const DEFAULT_ZOOM = 15;
const NAVER_MAP_CLIENT_ID = '09h8qpimmp';

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
  const [center, setCenter] = useState(DEFAULT_CENTER);
  const [zoom, setZoom] = useState(DEFAULT_ZOOM);

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

  // 지도 및 클릭 이벤트 초기화
  useEffect(() => {
    if (loading) return;
    if (!window.naver || !mapRef.current) return;
    const naver = window.naver;
    const mapInstance = new naver.maps.Map(mapRef.current, {
      center: new naver.maps.LatLng(center.lat, center.lng),
      zoom: zoom + 1,
      gl: true,
      customStyleId: '4b934c2a-71f5-4506-ab90-4e6aa14c0820',
      zoomControl: true,
      zoomControlOptions: {
          position: naver.maps.Position.TOP_RIGHT,
          style: naver.maps.ZoomControlStyle.SMALL
      }
    });
    setMapReady(true);
    const handleClick = (e: any) => {
      const lat = e.coord.lat();
      const lng = e.coord.lng();
      setCoords({ lat, lng });
      if (markerRef.current) {
        markerRef.current.setMap(null);
      }
      const newMarker = new naver.maps.Marker({
        position: new naver.maps.LatLng(lat, lng),
        map: mapInstance,
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
    };
  }, [loading, center, zoom]);

  const handleSave = async () => {
    if (!coords) return;
    setSaving(true);
    try {
      const res = await api.post('/places/geographies', {
        placeId,
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
      {loading || !mapReady ? <div>지도를 불러오는 중입니다...</div> : null}
      <div
        id="map"
        ref={mapRef}
        style={{ width: '100%', height: 400, display: loading || !mapReady ? 'none' : 'block' }}
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