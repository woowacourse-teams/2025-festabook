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

const CURRENT_ICON_SVG = `
  <svg width="30" height="40" viewBox="0 0 24 29" fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M4 21L12.0711 14.0002L20 21L12.7344 27.5448C12.3565 27.8852 11.7833 27.8877 11.4024 27.5507L4 21Z" fill="#C3C3C3"/>
    <circle cx="12" cy="12" r="11.5" fill="#FF4B3E" stroke="white"/>
    <path fill-rule="evenodd" clip-rule="evenodd" d="M18.3634 7.40738c.1874.18753.2928.44183.2928.707s-.1054.57431-.2928.76184l-7.4954 7.4953c-.099.0991-.2166.1777-.346.2313-.1295.0536-.2682.0812-.4083.0812s-.2788-.0276-.4082-.0812c-.12943-.0536-.24703-.1322-.34608-.2313l-3.724-3.7233a1.079 1.079 0 0 1-.305-1.0863c.05029-.1229.12454-.2345.21843-.3284.09389-.0939.20555-.1682.32844-.2185.1229-.0503.25458-.0756.38736-.0745.13278.0012.264.0288.386.0812.122.0524.23235.1286.3246.2241l3.064 3.064 6.8353-6.83598c.0929-.09293.2031-.16665.3245-.21694.1214-.0503.2515-.07618.3828-.07618s.2605.02588.3819.07618c.1213.05029.2316.12401.3245.21694Z" fill="white"/>
  </svg>
`;

declare global {
  interface ImportMetaEnv { VITE_NAVER_MAP_CLIENT_ID: string; }
  interface ImportMeta { env: ImportMetaEnv; }
  interface Window { naver: any; navermap_authFailure: () => void; }
}

const MapSelector: React.FC<MapSelectorProps> = ({ placeId, onSaved }) => {
  const mapRef = useRef<HTMLDivElement>(null);
  const [loading, setLoading] = useState(true);
  const [mapReady, setMapReady] = useState(false);
  const [coords, setCoords] = useState<{ lat: number; lng: number } | null>(null);
  const [saving, setSaving] = useState(false);
  const [center, setCenter] = useState<{ lat: number; lng: number } | null>(null);
  const [zoom, setZoom] = useState<number | null>(null);
  const [existingMarkers, setExistingMarkers] = useState<any[]>([]);
  const [currentPlace, setCurrentPlace] = useState<any>(null);
  const existingMarkerRefs = useRef<any[]>([]);
  const [holeBoundary, setHoleBoundary] = useState<any[]>([]);
  const polygonRef = useRef<any>(null);
  const currentMarkerRef = useRef<any>(null);
  const { showToast } = useModal();

  const placeIdNum = Number(placeId);
  const getMarkerId = (m: any) => Number(m?.placeId ?? m?.id ?? m?.place_id);

  useEffect(() => {
    async function fetchGeography() {
      try {
        const res = await api.get('/festivals/geography');
        if (res.data?.centerCoordinate) {
          setCenter({ lat: res.data.centerCoordinate.latitude, lng: res.data.centerCoordinate.longitude });
        }
        if (res.data?.zoom) setZoom(res.data.zoom);
        if (res.data?.polygonHoleBoundary) setHoleBoundary(res.data.polygonHoleBoundary);
      } catch { /* noop */ }
    }
    fetchGeography();
  }, []);

  useEffect(() => {
    if (window.naver && window.naver.maps) { setLoading(false); return; }
    setLoading(true);
    window.navermap_authFailure = function () { console.log('네이버 지도 인증 실패'); };
    const script = document.createElement('script');
    script.src = `https://oapi.map.naver.com/openapi/v3/maps.js?ncpKeyId=${NAVER_MAP_CLIENT_ID}&submodules=gl`;
    script.async = true;
    script.onload = () => setLoading(false);
    script.onerror = () => setLoading(false);
    document.body.appendChild(script);
    return () => { document.body.removeChild(script); };
  }, []);

  useEffect(() => {
    async function fetchMarkers() {
      try {
        const [markersRes, placeRes] = await Promise.all([
          api.get('/places/geographies'),
          api.get(`/places/${placeId}`)
        ]);
        setExistingMarkers(markersRes.data || []);
        setCurrentPlace(placeRes.data);
      } catch {
        setExistingMarkers([]);
        setCurrentPlace(null);
      }
    }
    fetchMarkers();
  }, [placeId]);

  useEffect(() => {
    if (loading || !center || !zoom) return;
    if (!window.naver || !mapRef.current) return;
    const naver = window.naver;

    const mapInstance = new naver.maps.Map(mapRef.current, {
      center: new naver.maps.LatLng(center.lat, center.lng),
      zoom: zoom + 1,
      gl: true,
      customStyleId: '4b934c2a-71f5-4506-ab90-4e6aa14c0820',
      logoControl: false, mapDataControl: false, scaleControl: false,
      zoomControl: true, zoomControlOptions: { position: naver.maps.Position.TOP_RIGHT, style: naver.maps.ZoomControlStyle.SMALL }
    });
    setMapReady(true);

    // 폴리곤
    if (holeBoundary && holeBoundary.length > 2) {
      const outer = KOREA_BOUNDARY.map(([lat, lng]) => new naver.maps.LatLng(lat, lng));
      const hole = holeBoundary.map((p: any) => new naver.maps.LatLng(p.latitude, p.longitude));
      if (polygonRef.current) polygonRef.current.setMap(null);
      polygonRef.current = new naver.maps.Polygon({
        map: mapInstance, paths: [outer, hole],
        fillColor: '#1b1b1b', fillOpacity: 0.3, strokeColor: '#1b1b1b', strokeOpacity: 0.6, strokeWeight: 3
      });
    }

    // 모든 이전 마커 제거 후 재생성
    existingMarkerRefs.current.forEach(m => m.setMap(null));
    existingMarkerRefs.current = [];

    // 편집 대상(placeId) 마커는 생성 스킵
    existingMarkers.forEach(marker => {
      const mid = getMarkerId(marker);
      if (!marker?.markerCoordinate?.latitude || !marker?.markerCoordinate?.longitude) return;
      if (mid === placeIdNum) return;

      const m = new naver.maps.Marker({
        position: new naver.maps.LatLng(marker.markerCoordinate.latitude, marker.markerCoordinate.longitude),
        map: mapInstance,
        title: marker.category,
        icon: {
          content: getCategoryIcon(marker.category, false),
          size: new naver.maps.Size(30, 40),
          anchor: new naver.maps.Point(15, 40)
        }
      });
      (m as any).__placeId = mid;
      existingMarkerRefs.current.push(m);
    });

    // 혹시라도 남아있는 동일 placeId 마커 강제 제거(세이프가드)
    existingMarkerRefs.current = existingMarkerRefs.current.filter(m => {
      const same = Number((m as any).__placeId) === placeIdNum;
      if (same) m.setMap(null);
      return !same;
    });

    // 현재 편집 마커 upsert
    const upsertCurrentMarker = (pos: { lat: number; lng: number }) => {
      const position = new naver.maps.LatLng(pos.lat, pos.lng);
      if (currentMarkerRef.current) {
        currentMarkerRef.current.setPosition(position);
        return;
      }
      currentMarkerRef.current = new naver.maps.Marker({
        position, map: mapInstance, title: currentPlace?.category ?? 'current',
        icon: { content: CURRENT_ICON_SVG, size: new naver.maps.Size(30, 40), anchor: new naver.maps.Point(15, 40) }
      });
      (currentMarkerRef.current as any).__placeId = placeIdNum;
    };

    // 초기 위치 배치
    const current = existingMarkers.find(m => getMarkerId(m) === placeIdNum);
    const initialLat = current?.markerCoordinate?.latitude;
    const initialLng = current?.markerCoordinate?.longitude;
    if (initialLat && initialLng) {
      upsertCurrentMarker({ lat: initialLat, lng: initialLng });
      setCoords({ lat: initialLat, lng: initialLng });
    }

    // 클릭 시 이동 + 동일 placeId 기존 마커 강제 제거
    const handleClick = (e: any) => {
      const lat = e.coord.lat(); const lng = e.coord.lng();
      setCoords({ lat, lng });
      // 혹시 남은 동일 placeId 마커 제거
      existingMarkerRefs.current = existingMarkerRefs.current.filter(m => {
        const same = Number((m as any).__placeId) === placeIdNum;
        if (same) m.setMap(null);
        return !same;
      });
      upsertCurrentMarker({ lat, lng });
    };

    naver.maps.Event.addListener(mapInstance, 'click', handleClick);

    return () => {
      naver.maps.Event.clearInstanceListeners(mapInstance);
      if (currentMarkerRef.current) { currentMarkerRef.current.setMap(null); currentMarkerRef.current = null; }
      existingMarkerRefs.current.forEach(m => m.setMap(null));
      existingMarkerRefs.current = [];
      if (polygonRef.current) { polygonRef.current.setMap(null); polygonRef.current = null; }
    };
  }, [loading, center, zoom, existingMarkers, holeBoundary, currentPlace, placeIdNum]);

  const handleSave = async () => {
    if (!coords) return;
    setSaving(true);
    try {
      const res = await api.patch(`/places/${placeId}/geographies`, {
        latitude: coords.lat, longitude: coords.lng,
      });
      const markerData = {
        placeId: res.data.placeId,
        category: currentPlace?.category,
        coordinate: res.data.coordinate,
        markerCoordinate: res.data.coordinate,
        ...res.data
      };
      onSaved && onSaved(markerData);
      showToast('좌표가 저장되었습니다.');
    } catch {
      showToast('좌표 저장에 실패했습니다.');
    } finally { setSaving(false); }
  };

  return (
    <div>
      {loading || !center || !zoom || !mapReady ? <div>지도를 불러오는 중입니다...</div> : null}
      <div id="map" ref={mapRef}
           style={{ width: '100%', height: 400, display: loading || !center || !zoom || !mapReady ? 'none' : 'block' }} />
      <div className="mt-4 flex justify-end">
        <button onClick={handleSave} disabled={!coords || saving}
                className="bg-blue-600 text-white px-4 py-2 rounded disabled:bg-gray-300">
          {saving ? '저장 중...' : '좌표 설정'}
        </button>
      </div>
    </div>
  );
};

export default MapSelector;
