import SwiftUI
import UIKit
import CoreLocation

#if canImport(NMapsMap)
import NMapsMap

// MARK: - NaverMapRepresentable for NMapsMap SDK 3.22.1

struct NaverMapRepresentable: UIViewRepresentable {
    @ObservedObject var viewModel: MapViewModel

    func makeUIView(context: Context) -> NMFMapView {
        let mapView = NMFMapView()

        print("[NaverMapRepresentable] NMFMapView 생성 시작")

        // Configure map settings
        mapView.positionMode = .disabled
        mapView.locationOverlay.hidden = true
        mapView.mapType = .basic
        mapView.logoAlign = .leftTop
        mapView.logoMargin = UIEdgeInsets(top: 28, left: -100, bottom: 0, right: 0)

        // Apply custom style
        print("[NaverMapRepresentable] 커스텀 스타일 적용 시작")
        mapView.setCustomStyleId("4b934c2a-71f5-4506-ab90-4e6aa14c0820",
            loadHandler: {
                print("[NaverMapRepresentable] ✅ 스타일 로드 성공")
            },
            failHandler: { error in
                print("[NaverMapRepresentable] ❌ 스타일 로드 실패 - \(error)")
            }
        )

        // 초기 카메라 위치 설정 - geography 데이터가 있으면 즉시 적용
        if let geography = viewModel.geography {
            let initialPosition = NMGLatLng(
                lat: geography.adjustedCenterCoordinate.latitude,
                lng: geography.adjustedCenterCoordinate.longitude
            )
            let initialCameraPosition = NMFCameraPosition(initialPosition, zoom: Double(geography.zoom))
            let cameraUpdate = NMFCameraUpdate(position: initialCameraPosition)
            cameraUpdate.animation = .none  // 애니메이션 없이 즉시 이동
            Task {
                _ = await mapView.moveCamera(cameraUpdate)
            }
            print("[NaverMapRepresentable] 초기 카메라 위치 설정 완료: \(geography.centerCoordinate), zoom: \(geography.zoom)")
        } else {
            // Geography 데이터가 없으면 기본 줌 레벨만 설정 (나중에 API 응답값으로 덮어씀)
            mapView.zoomLevel = 15
            print("[NaverMapRepresentable] Geography 데이터 없음 - 나중에 API 응답값으로 설정됨")
        }

        // Set delegates
        mapView.touchDelegate = context.coordinator
        mapView.addCameraDelegate(delegate: context.coordinator)

        // Create and add location button

        print("[NaverMapRepresentable] NMFMapView 생성 완료")
        return mapView
    }

    func updateUIView(_ uiView: NMFMapView, context: Context) {
        Task { @MainActor in
            await context.coordinator.updateMapView(uiView, with: viewModel)
        }
    }

    func makeCoordinator() -> Coordinator {
        Coordinator(viewModel)
    }

    // MARK: - Coordinator Class

        @MainActor
        class Coordinator: NSObject, NMFMapViewTouchDelegate, NMFMapViewCameraDelegate, CLLocationManagerDelegate {
            var viewModel: MapViewModel
            private var markers: [Int: NMFMarker] = [:]
            private var outerPolygonOverlay: NMFPolygonOverlay?  // 외곽 검정 영역
            private var innerPolygonOverlay: NMFPolygonOverlay?  // 내부 캠퍼스 영역 (투명)
            private var locationManager = CLLocationManager()
            private var locationButton: UIButton?
            private var isLocationTracking = false
            private var hasAppliedInitialGeography = false
            private var isAnimating = false
            private var lastContentInsetUpdate: Date = Date.distantPast
            private var lastCameraResetId: UUID?
            private var markerBaseSizes: [Int: CGSize] = [:]
            private var markerTitles: [Int: String] = [:]
            private var pendingMoveToCurrentLocation = false
            private var selectedMarkerId: Int?
            private let captionVisibilityZoomThreshold: Double = 16.0
            private var markerGeographies: [Int: PlaceGeography] = [:]
            private var lastCameraMoveTargetId: Int?
            private var lastLocationRequestId: UUID?
            private weak var mapViewReference: NMFMapView?

            init(_ viewModel: MapViewModel) {
                self.viewModel = viewModel
                super.init()
                setupLocationManager()
            }

            private func setupLocationManager() {
                locationManager.delegate = self
                locationManager.desiredAccuracy = kCLLocationAccuracyBest
                locationManager.distanceFilter = 10 // 10미터마다 업데이트
            }

        @MainActor
        func updateMapView(_ mapView: NMFMapView, with viewModel: MapViewModel) async {
            print("[Coordinator] updateMapView 호출")

            mapViewReference = mapView

            // Update content inset for bottom sheet
            updateContentInset(mapView, bottomSheetHeight: viewModel.visibleBottomSheetHeight)

            // Update initial geography settings (최초 1회만 적용)
            if let geography = viewModel.geography, !hasAppliedInitialGeography {
                print("[Coordinator] Geography 데이터 최초 적용: center=\(geography.centerCoordinate), zoom=\(geography.zoom)")

                // 카메라 위치가 아직 설정되지 않은 경우에만 설정 (애니메이션 없이)
                let currentPosition = mapView.cameraPosition
                let targetPosition = NMGLatLng(lat: geography.adjustedCenterCoordinate.latitude, lng: geography.adjustedCenterCoordinate.longitude)
                let targetZoom = Double(geography.zoom)

                // 현재 위치와 목표 위치가 다르면 즉시 설정 (애니메이션 없음)
                if abs(currentPosition.target.lat - targetPosition.lat) > 0.001 ||
                   abs(currentPosition.target.lng - targetPosition.lng) > 0.001 ||
                   abs(currentPosition.zoom - targetZoom) > 0.1 {
                    let cameraPosition = NMFCameraPosition(targetPosition, zoom: targetZoom)
                    let cameraUpdate = NMFCameraUpdate(position: cameraPosition)
                    cameraUpdate.animation = .none  // 애니메이션 없이 즉시 이동
                    Task {
                        _ = await mapView.moveCamera(cameraUpdate)
                    }
                    print("[Coordinator] 카메라 위치 즉시 설정 완료 (애니메이션 없음)")
                }

                updatePolygonMask(mapView, geography: geography)
                hasAppliedInitialGeography = true
            } else if let geography = viewModel.geography {
                // 폴리곤만 업데이트 (카메라 위치는 건드리지 않음)
                updatePolygonMask(mapView, geography: geography)
            } else {
                print("[Coordinator] Geography 데이터 없음 - API 호출 대기 중")
            }

            // Update markers
            print("[Coordinator] 마커 업데이트: \(viewModel.filteredMarkers.count)개")
            updateMarkers(mapView, markers: viewModel.filteredMarkers)

            if let selectedPlaceId = viewModel.selectedPlaceId,
               lastCameraMoveTargetId != selectedPlaceId,
               let targetMarker = markerGeographies[selectedPlaceId] {
                moveToMarkerWithOffset(mapView, marker: targetMarker)
            }

            // Update selected marker
            if let selectedPlaceId = viewModel.selectedPlaceId {
                print("[Coordinator] 선택된 마커: \(selectedPlaceId)")
            }
            updateSelectedMarker(mapView: mapView, placeId: viewModel.selectedPlaceId)

            // Update location button visibility based on modal state
            updateLocationButtonVisibility()

            if let requestId = viewModel.currentLocationRequestId,
               requestId != lastLocationRequestId {
                lastLocationRequestId = requestId
                handleCurrentLocationRequest(on: mapView)
            }

            // Reset camera request 처리
            if let resetId = viewModel.resetCameraRequest,
               resetId != lastCameraResetId,
               let geography = viewModel.geography {
                lastCameraResetId = resetId
                resetCamera(mapView, with: geography)
                Task { @MainActor in
                    viewModel.resetCameraRequest = nil
                }
            }

            // Caption visibility is automatically handled by Naver Maps SDK based on captionMinZoom/captionMaxZoom
        }

        private func updateContentInset(_ mapView: NMFMapView, bottomSheetHeight: CGFloat) {
            // 새로운 모달 시스템에 따른 bottom inset 계산
            let newBottomInset: CGFloat
            switch viewModel.modalType {
            case .none:
                newBottomInset = 60.0 // 바텀시트만 있을 때
            case .preview:
                newBottomInset = 120.0 // 얇은 프리뷰 모달
            case .detail:
                newBottomInset = 180.0 // 상세 모달
            }

            let currentInset = mapView.contentInset

            // ContentInset 변경이 필요한지 확인
            if abs(currentInset.bottom - newBottomInset) < 1.0 {
                return // 변경이 미미하면 스�ip
            }

            // 카메라 위치 보정을 위해 현재 중심점 저장 (unused - 나중에 필요시 사용)
            // let currentCenter = mapView.cameraPosition.target
            // let currentZoom = mapView.cameraPosition.zoom

            // ContentInset 업데이트
            mapView.contentInset = UIEdgeInsets(
                top: 60, // 필터칩 높이
                left: 16,
                bottom: newBottomInset,
                right: 16
            )

            // 카메라 위치 보정 제거 - 바깥 클릭 시 불필요한 카메라 이동 방지
            let insetDifference = newBottomInset - currentInset.bottom
            print("[Coordinator] ContentInset 변경: \(currentInset.bottom) → \(newBottomInset), 차이=\(insetDifference)")
            print("[Coordinator] 카메라 위치 보정 스킵 - 사용자 의도하지 않은 이동 방지")

            lastContentInsetUpdate = Date()
        }

        private func updateCameraPosition(_ mapView: NMFMapView, geography: GeographyResponse) {
            let targetPosition = NMGLatLng(lat: geography.adjustedCenterCoordinate.latitude, lng: geography.adjustedCenterCoordinate.longitude)
            let targetZoom = Double(geography.zoom)

            print("[Coordinator] 📍 Geography API 초기 카메라 설정:")
            print("  - API center: lat=\(geography.centerCoordinate.latitude), lng=\(geography.centerCoordinate.longitude)")
            print("  - Adjusted center: lat=\(geography.adjustedCenterCoordinate.latitude), lng=\(geography.adjustedCenterCoordinate.longitude)")
            print("  - API zoom: \(geography.zoom)")
            print("  - 현재 카메라: lat=\(mapView.cameraPosition.target.lat), lng=\(mapView.cameraPosition.target.lng), zoom=\(mapView.cameraPosition.zoom)")

            let cameraPosition = NMFCameraPosition(targetPosition, zoom: targetZoom)
            let cameraUpdate = NMFCameraUpdate(position: cameraPosition)
            cameraUpdate.animation = .easeIn
            cameraUpdate.animationDuration = 1.0 // 부드러운 초기 이동

            mapView.moveCamera(cameraUpdate)

            // 설정 후 검증
            DispatchQueue.main.asyncAfter(deadline: .now() + 1.2) {
                let finalPosition = mapView.cameraPosition
                print("[Coordinator] ✅ 초기 카메라 설정 완료:")
                print("  - 최종 center: lat=\(finalPosition.target.lat), lng=\(finalPosition.target.lng)")
                print("  - 최종 zoom: \(finalPosition.zoom)")
                print("  - 목표 대비 차이: lat=\(abs(finalPosition.target.lat - targetPosition.lat)), lng=\(abs(finalPosition.target.lng - targetPosition.lng)), zoom=\(abs(finalPosition.zoom - targetZoom))")
            }
        }


        private func moveToMarkerWithOffset(_ mapView: NMFMapView, marker: PlaceGeography) {
            if selectedMarkerId == marker.placeId {
                lastCameraMoveTargetId = marker.placeId
                return
            }
            // 애니메이션 중이거나 최근에 contentInset 변경이 있었으면 대기
            if isAnimating {
                print("[Coordinator] 카메라 애니메이션 진행 중 - 마커 이동 스킵")
                return
            }

            let timeSinceLastInsetUpdate = Date().timeIntervalSince(lastContentInsetUpdate)
            if timeSinceLastInsetUpdate < 0.2 { // 200ms 내에 inset 변경이 있었으면 대기
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) { [weak self] in
                    self?.moveToMarkerWithOffset(mapView, marker: marker)
                }
                print("[Coordinator] ContentInset 변경 직후 - 마커 이동 지연 실행")
                return
            }

            let coord = marker.safeCoordinate
            let markerLatLng = NMGLatLng(lat: coord.latitude, lng: coord.longitude)

            // 현재 카메라 상태 가져오기 (줌 레벨 유지)
            let currentCamera = mapView.cameraPosition
            print("[Coordinator] 현재 카메라 상태 - 줌: \(currentCamera.zoom), 위치: \(currentCamera.target)")

            // 현재 카메라 중심과 마커 좌표의 거리 계산 (약 30m 이내면 이동 스킵)
            let currentLatLng = currentCamera.target
            let distance = calculateDistance(
                lat1: currentLatLng.lat,
                lng1: currentLatLng.lng,
                lat2: markerLatLng.lat,
                lng2: markerLatLng.lng
            )

            print("[Coordinator] 현재 위치와 마커 거리: \(String(format: "%.2f", distance))m")

            // 50m 이내면 카메라 이동 스킵 (적절한 기준으로 설정하여 불필요한 애니메이션 방지)
            if distance < 45.0 {
                print("[Coordinator] 마커가 이미 화면 중앙 근처에 있음 (거리: \(String(format: "%.2f", distance))m) - 카메라 이동 스킵")
                lastCameraMoveTargetId = marker.placeId
                return
            }

            // 애니메이션 시작 플래그 설정
            isAnimating = true

            // 줌 레벨을 명시적으로 유지하면서 좌표만 이동
            let cameraPosition = NMFCameraPosition(
                markerLatLng,
                zoom: currentCamera.zoom  // 현재 줌 레벨 명시적 유지
            )
            let cameraUpdate = NMFCameraUpdate(position: cameraPosition)

            // Pivot 설정: 카드 영역을 제외한 나머지 화면의 중앙에 마커 위치
            cameraUpdate.pivot = CGPoint(x: 0.5, y: 0.45)

            // 거리에 따른 동적 애니메이션 시간 계산
            let dynamicDuration = calculateAnimationDuration(for: distance)

            // 자연스러운 애니메이션 설정
            cameraUpdate.animation = .easeIn
            cameraUpdate.animationDuration = dynamicDuration

            lastCameraMoveTargetId = marker.placeId
            mapView.moveCamera(cameraUpdate)

            // 애니메이션 완료 후 플래그 해제 (동적 시간 + 여유 시간)
            DispatchQueue.main.asyncAfter(deadline: .now() + dynamicDuration + 0.05) { [weak self] in
                self?.isAnimating = false
            }

            print("[Coordinator] 마커로 카메라 이동 (줌 명시적 유지: \(currentCamera.zoom), 거리: \(String(format: "%.2f", distance))m, 애니메이션: easeIn \(String(format: "%.2f", dynamicDuration))s): \(coord)")
        }
        
        // 거리에 따른 애니메이션 시간 계산
        private func calculateAnimationDuration(for distance: Double) -> TimeInterval {
            // 기본 공식: 500m 이동에 약 1초, 최소 0.2초, 최대 1.2초
            let baseDuration = distance / 500.0
            let clampedDuration = min(max(baseDuration, 0.2), 1.2)

            print("[Coordinator] 🎬 애니메이션 시간 계산: 거리 \(String(format: "%.2f", distance))m → 기본 \(String(format: "%.2f", baseDuration))s → 조정됨 \(String(format: "%.2f", clampedDuration))s")

            return clampedDuration
        }

        // 두 지점 간의 거리 계산 (미터 단위)
        private func calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double) -> Double {
            let earthRadius = 6371000.0 // 지구 반지름 (미터)
            
            let dLat = (lat2 - lat1) * .pi / 180.0
            let dLng = (lng2 - lng1) * .pi / 180.0
            
            let a = sin(dLat / 2) * sin(dLat / 2) +
                    cos(lat1 * .pi / 180.0) * cos(lat2 * .pi / 180.0) *
                    sin(dLng / 2) * sin(dLng / 2)
            
            let c = 2 * atan2(sqrt(a), sqrt(1 - a))
            
            return earthRadius * c
        }

        private func updatePolygonMask(_ mapView: NMFMapView, geography: GeographyResponse) {
            // Remove existing polygon overlays
            if let existingOuter = outerPolygonOverlay {
                existingOuter.mapView = nil
                outerPolygonOverlay = nil
            }
            if let existingInner = innerPolygonOverlay {
                existingInner.mapView = nil
                innerPolygonOverlay = nil
            }

            // 폴리곤 홀 바운더리 검증
            guard !geography.polygonHoleBoundary.isEmpty else {
                print("[Coordinator] 폴리곤 경계 데이터가 없습니다")
                return
            }

            print("[Coordinator] 🎯 진정한 Polygon Hole: NMGPolygon의 interiorRings 사용")
            print("[Coordinator] API에서 받은 캠퍼스 경계 좌표: \(geography.polygonHoleBoundary.count)개")

            // 1. 대한민국 전체 영역 (exterior ring - 시계방향)
            let koreaOuterCoords = [
                NMGLatLng(lat: 32.0, lng: 124.0),  // 남서쪽
                NMGLatLng(lat: 39.0, lng: 124.0),  // 북서쪽
                NMGLatLng(lat: 39.0, lng: 132.0),  // 북동쪽
                NMGLatLng(lat: 32.0, lng: 132.0),  // 남동쪽
                NMGLatLng(lat: 32.0, lng: 124.0)   // 시계방향으로 닫기
            ]

            // 2. 캠퍼스 영역 (interior ring - 반시계방향)
            var campusCoords = geography.polygonHoleBoundary.map {
                NMGLatLng(lat: $0.latitude, lng: $0.longitude)
            }

            // 폴리곤 닫기 - 첫 좌표를 맨 뒤에 추가
            if let firstPoint = campusCoords.first {
                let lastPoint = campusCoords.last!
                let latDiff = abs(firstPoint.lat - lastPoint.lat)
                let lngDiff = abs(firstPoint.lng - lastPoint.lng)
                if latDiff > 0.000001 || lngDiff > 0.000001 {
                    campusCoords.append(firstPoint)
                    print("[Coordinator] 캠퍼스 폴리곤 닫기: 첫 좌표 (\(firstPoint.lat), \(firstPoint.lng)) 추가")
                }
            }

            // interior ring은 반시계방향이어야 함 (hole 규칙)
            campusCoords.reverse()

            print("[Coordinator] 📍 Exterior ring: \(koreaOuterCoords.count)개 좌표 (시계방향)")
            print("[Coordinator] 📍 Interior ring: \(campusCoords.count)개 좌표 (반시계방향)")

            // 3. NMGPolygon에 interiorRings 사용해서 진정한 hole 구현
            let exteriorRing = NMGLineString(points: koreaOuterCoords)
            let interiorRing = NMGLineString(points: campusCoords)

            // iOS SDK에서 interiorRings 시도 및 fallback
            let polygonWithHole: NMGPolygon<AnyObject>

            // 시도 1: iOS에서 interiorRings 지원 여부 확인
            let polygonWithInterior = NMGPolygon(ring: exteriorRing, interiorRings: [interiorRing])
            // NMGPolygon은 non-optional이므로 항상 생성됨
            polygonWithHole = unsafeBitCast(polygonWithInterior, to: NMGPolygon<AnyObject>.self)
            print("[Coordinator] ✅ iOS에서 interiorRings 지원됨 - 진정한 polygon hole 사용")

            // 4. 폴리곤 오버레이 생성 및 스타일링
            let polygonOverlay = NMFPolygonOverlay()
            polygonOverlay.polygon = polygonWithHole

            // 색상을 더 연하게 조정 (0.5 -> 0.3)
            polygonOverlay.fillColor = UIColor.black.withAlphaComponent(0.3)  // 더 연한 반투명
            polygonOverlay.outlineColor = UIColor.black.withAlphaComponent(0.6)  // 외곽선도 연하게
            polygonOverlay.outlineWidth = 2
            polygonOverlay.zIndex = 0
            polygonOverlay.mapView = mapView
            self.outerPolygonOverlay = polygonOverlay

            print("[Coordinator] ✅ 진정한 Polygon Hole 구현 완료")
            print("[Coordinator]   ✓ Exterior: 대한민국 전체 (시계방향)")
            print("[Coordinator]   ✓ Interior: 캠퍼스 구멍 (반시계방향)")
            print("[Coordinator]   ✓ 색상: 더 연한 반투명 (alpha=0.3)")
            print("[Coordinator]   ✓ 결과: 캠퍼스 영역이 완전히 투명하게 뚫림")
        }

        // interiorRings 미지원 시 fallback 방식
        private func createFallbackCampusOverlay(mapView: NMFMapView, campusCoords: [NMGLatLng]) {
            let campusLineString = NMGLineString(points: campusCoords)
            let campusPolygon = NMGPolygon(ring: campusLineString)

            let campusOverlay = NMFPolygonOverlay()
            campusOverlay.polygon = unsafeBitCast(campusPolygon, to: NMGPolygon<AnyObject>.self)

            // 지도의 실제 배경색과 최대한 비슷하게 맞춤
            // 네이버 지도의 기본 배경색: 연한 베이지/크림색
            campusOverlay.fillColor = UIColor(red: 0.97, green: 0.96, blue: 0.93, alpha: 1.0)  // 지도 배경과 유사한 색
            campusOverlay.outlineColor = UIColor.black.withAlphaComponent(0.6)  // 연한 검정 테두리
            campusOverlay.outlineWidth = 2
            campusOverlay.zIndex = 1  // 외곽 폴리곤보다 높은 z-index
            campusOverlay.mapView = mapView
            self.innerPolygonOverlay = campusOverlay

            print("[Coordinator] 📌 Fallback 캠퍼스 오버레이 생성: 지도 배경색과 유사하게 설정")
        }

        private var captionAnimationTimer: Timer?
    
    private func updateMarkers(_ mapView: NMFMapView, markers: [PlaceGeography]) {
            // Remove existing markers and timer
            captionAnimationTimer?.invalidate()
            self.markers.values.forEach { $0.mapView = nil }
            self.markers.removeAll()
            markerBaseSizes.removeAll()

            markerTitles.removeAll()
            markerGeographies.removeAll()
            lastCameraMoveTargetId = nil

            print("[Coordinator] 🗺 마커 업데이트 시작: \(markers.count)개 마커")

            for marker in markers {
                let nmfMarker = NMFMarker()
                let coord = marker.safeCoordinate
                nmfMarker.position = NMGLatLng(lat: coord.latitude, lng: coord.longitude)

                setMarkerIcon(nmfMarker, category: marker.category)
                markerBaseSizes[marker.placeId] = CGSize(width: nmfMarker.width, height: nmfMarker.height)

                let title = viewModel.previewsByPlaceId[marker.placeId]?.title ?? marker.title
                nmfMarker.captionAligns = [.bottom]
                nmfMarker.captionMinZoom = captionVisibilityZoomThreshold
                nmfMarker.captionText = title
                nmfMarker.captionOffset = 6
                nmfMarker.isHideCollidedCaptions = true // 마커와 겹치는 다른 마커의 캡션만 숨김
                markerTitles[marker.placeId] = title

                nmfMarker.touchHandler = { [weak self] _ in
                    Task { @MainActor in
                        guard let self else { return }
                        print("[Coordinator] 🎯 마커 클릭됨: placeId=\(marker.placeId)")
                        self.hideLocationButton()
                        self.viewModel.selectPlace(marker.placeId)
                        self.moveToMarkerWithOffset(mapView, marker: marker)
                        self.updateSelectedMarker(mapView: mapView, placeId: marker.placeId)
                    }
                    return true
                }

                nmfMarker.mapView = mapView
                self.markers[marker.placeId] = nmfMarker
                markerGeographies[marker.placeId] = marker
            }
        }

        private func setMarkerIcon(_ marker: NMFMarker, category: String) {
            // Try to find matching category
            if let mapCategory = MapCategory.allCases.first(where: { $0.rawValue == category }) {
                // Try to load custom icon from bundle resources
                if let iconUIImage = UIImage(named: mapCategory.iconName) {
                    // Use UIImage for proper aspect ratio
                    let iconImage = NMFOverlayImage(image: iconUIImage)
                    marker.iconImage = iconImage

                    // Maintain original aspect ratio with reasonable size
                    let aspectRatio = iconUIImage.size.width / iconUIImage.size.height
                    let baseHeight: CGFloat = 32
                    marker.width = baseHeight * aspectRatio
                    marker.height = baseHeight
                } else {
                    // Fallback: Use primary marker as default
                    setDefaultMarkerIcon(marker)
                    print("[Coordinator] 커스텀 아이콘 없음, 기본 마커 사용: \(mapCategory.iconName)")
                }
            } else {
                // Fallback: Unknown category gets default marker
                setDefaultMarkerIcon(marker)
                print("[Coordinator] 알 수 없는 카테고리, 기본 마커 사용: \(category)")
            }
        }

        private func setDefaultMarkerIcon(_ marker: NMFMarker) {
            // Use marker_primary as the default fallback
            if let defaultImage = UIImage(named: "marker_primary") {
                marker.iconImage = NMFOverlayImage(image: defaultImage)
                let aspectRatio = defaultImage.size.width / defaultImage.size.height
                let baseHeight: CGFloat = 32
                marker.width = baseHeight * aspectRatio
                marker.height = baseHeight
                print("[Coordinator] marker_primary 사용")
            } else {
                // Final fallback: try to use any available marker image
                let fallbackMarkers = ["marker_bar", "marker_booth", "marker_stage", "marker_toilet"]
                for markerName in fallbackMarkers {
                    if let fallbackImage = UIImage(named: markerName) {
                        marker.iconImage = NMFOverlayImage(image: fallbackImage)
                        marker.width = 24
                        marker.height = 32
                        print("[Coordinator] \(markerName) 대체 마커 사용")
                        return
                    }
                }

                // Ultimate fallback: create a simple colored marker programmatically
                let fallbackImage = createSimpleMarkerImage()
                marker.iconImage = NMFOverlayImage(image: fallbackImage)
                marker.width = 24
                marker.height = 32
                print("[Coordinator] 프로그래매틱 기본 마커 생성")
            }
        }

        private func createSimpleMarkerImage() -> UIImage {
            let size = CGSize(width: 24, height: 32)
            UIGraphicsBeginImageContextWithOptions(size, false, 0)
            defer { UIGraphicsEndImageContext() }

            let context = UIGraphicsGetCurrentContext()!

            // Draw a simple red circle with white border
            context.setFillColor(UIColor.red.cgColor)
            context.setStrokeColor(UIColor.white.cgColor)
            context.setLineWidth(2)

            let circleRect = CGRect(x: 2, y: 2, width: 20, height: 20)
            context.fillEllipse(in: circleRect)
            context.strokeEllipse(in: circleRect)

            return UIGraphicsGetImageFromCurrentImageContext() ?? UIImage()
        }

        @MainActor
        private func updateSelectedMarker(mapView: NMFMapView, placeId: Int?) {
            markers.forEach { id, marker in
                if let base = markerBaseSizes[id] {
                    CATransaction.begin()
                    CATransaction.setAnimationDuration(0.2)
                    marker.width = base.width
                    marker.height = base.height
                    CATransaction.commit()
                }
                marker.zIndex = 0
                marker.captionText = markerTitles[id] ?? ""
                marker.captionOffset = 6
                marker.captionMinZoom = captionVisibilityZoomThreshold
            }

            selectedMarkerId = placeId

            if placeId == nil {
                lastCameraMoveTargetId = nil
            }

            guard let placeId = placeId,
                  let selectedMarker = markers[placeId],
                  let base = markerBaseSizes[placeId] else { return }

            CATransaction.begin()
            CATransaction.setAnimationDuration(0.2)
            selectedMarker.width = base.width * 1.1
            selectedMarker.height = base.height * 1.1
            selectedMarker.zIndex = 100
            selectedMarker.captionTextSize = 13
            selectedMarker.captionOffset = 10
            selectedMarker.captionMinZoom = 0
            CATransaction.commit()
        }

        // MARK: - Location Button Management

        func createLocationButton(on mapView: NMFMapView) {
            let button = UIButton(type: .custom)
            button.backgroundColor = .white
            button.layer.cornerRadius = 10
            button.layer.borderWidth = 0.5
            button.layer.borderColor = UIColor.black.withAlphaComponent(0.08).cgColor
            button.layer.shadowColor = UIColor.black.cgColor
            button.layer.shadowOffset = CGSize(width: 0, height: 2)
            button.layer.shadowOpacity = 0.15
            button.layer.shadowRadius = 4

            // Use target/scope icon similar to Naver Maps
            let configuration = UIImage.SymbolConfiguration(pointSize: 18, weight: .medium)
            let targetImage = UIImage(systemName: "scope", withConfiguration: configuration)
            button.setImage(targetImage, for: .normal)
            button.tintColor = UIColor.gray

            button.addTarget(self, action: #selector(locationButtonTapped), for: .touchUpInside)

            mapView.addSubview(button)
            button.translatesAutoresizingMaskIntoConstraints = false

            // Position above bottom sheet on the LEFT side (한눈에 보기 버튼 바로 위)
            NSLayoutConstraint.activate([
                button.leadingAnchor.constraint(equalTo: mapView.safeAreaLayoutGuide.leadingAnchor, constant: 16),
                button.bottomAnchor.constraint(equalTo: mapView.safeAreaLayoutGuide.bottomAnchor, constant: -100), // Will be updated dynamically
                button.widthAnchor.constraint(equalToConstant: 44),
                button.heightAnchor.constraint(equalToConstant: 44)
            ])

            self.locationButton = button
            print("[Coordinator] 📍 현위치 버튼 생성 완료 (왼쪽 배치)")
        }

        private func updateLocationButtonVisibility() {
            guard locationButton != nil else { return }

            // Show button only when in "한눈에 보기" mode (no modal)
            let shouldShow = viewModel.modalType == .none && viewModel.sheetDetent != .large
            
            if shouldShow {
                showLocationButton()
                updateLocationButtonPosition()
            } else {
                hideLocationButton()
            }
        }

        private func updateLocationButtonPosition() {
            guard let button = locationButton,
                  let mapView = button.superview as? NMFMapView else { return }

            // Update bottom constraint to position above bottom sheet
            let bottomInset: CGFloat
            switch viewModel.modalType {
            case .none:
                bottomInset = viewModel.sheetDetent.totalHeight + 12 // Consistent gap above sheet top
            case .preview:
                bottomInset = 140 // Above preview modal
            case .detail:
                bottomInset = 420 // Above detail modal
            }

            // Remove and recreate constraints for dynamic positioning
            button.removeFromSuperview()
            mapView.addSubview(button)
            button.translatesAutoresizingMaskIntoConstraints = false

            NSLayoutConstraint.activate([
                button.leadingAnchor.constraint(equalTo: mapView.safeAreaLayoutGuide.leadingAnchor, constant: 16),
                button.bottomAnchor.constraint(equalTo: mapView.safeAreaLayoutGuide.bottomAnchor, constant: -bottomInset),
                button.widthAnchor.constraint(equalToConstant: 44),
                button.heightAnchor.constraint(equalToConstant: 44)
            ])
        }

        // MARK: - Location Button Show/Hide Methods

        func showLocationButton() {
            guard let button = locationButton else { return }
            UIView.animate(withDuration: 0.3) {
                button.alpha = 1.0
                button.isHidden = false
            }
            print("[Coordinator] 현위치 버튼 표시")
        }

        func hideLocationButton() {
            guard let button = locationButton else { return }
            UIView.animate(withDuration: 0.3) {
                button.alpha = 0.0
                button.isHidden = true
            }
            print("[Coordinator] 현위치 버튼 숨김")
        }

        @objc private func locationButtonTapped() {
            guard let mapView = mapViewReference ?? locationButton?.superview as? NMFMapView else { return }

            handleCurrentLocationRequest(on: mapView)
        }

        @MainActor
        private func handleCurrentLocationRequest(on mapView: NMFMapView) {
            
            switch locationManager.authorizationStatus {
            case .notDetermined:
                pendingMoveToCurrentLocation = true
                locationManager.requestWhenInUseAuthorization()
            case .denied, .restricted:
                showLocationPermissionAlert()
            case .authorizedWhenInUse, .authorizedAlways:
                moveToCurrentLocation(mapView)
            @unknown default:
                break
            }
        }

        private func moveToCurrentLocation(_ mapView: NMFMapView) {
            guard let location = locationManager.location else {
                pendingMoveToCurrentLocation = true
                locationManager.startUpdatingLocation()
                print("[Coordinator] 위치 정보 요청 중...")
                return
            }

            moveToCurrentLocation(mapView, location: location)
        }

        private func moveToCurrentLocation(_ mapView: NMFMapView, location: CLLocation) {
            let coord = NMGLatLng(lat: location.coordinate.latitude, lng: location.coordinate.longitude)

            // Configure location overlay - show blue dot for current location
            let locationOverlay = mapView.locationOverlay
            locationOverlay.hidden = false
            locationOverlay.location = coord
            locationOverlay.circleColor = UIColor.systemBlue.withAlphaComponent(0.2)
            locationOverlay.circleOutlineColor = UIColor.systemBlue
            locationOverlay.circleOutlineWidth = 2
            locationOverlay.circleRadius = 15

            mapView.positionMode = .normal
            // Move camera with animation (공식 문서에 따른 구현)
            let cameraUpdate = NMFCameraUpdate(scrollTo: coord)
            cameraUpdate.animation = .easeIn
            cameraUpdate.animationDuration = 0.5

            Task {
                _ = await mapView.moveCamera(cameraUpdate)
            }

            print("[Coordinator] 📍 현재 위치로 이동: lat=\(location.coordinate.latitude), lng=\(location.coordinate.longitude)")
            print("[Coordinator] 📍 positionMode: \(mapView.positionMode)")
            print("[Coordinator] 📍 위치 정확도: \(location.horizontalAccuracy)m")
        }

        // MARK: - CLLocationManagerDelegate

        nonisolated func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
            guard let location = locations.last else { return }
            Task { @MainActor in
                if let mapView = mapViewReference ?? locationButton?.superview as? NMFMapView {
                    moveToCurrentLocation(mapView, location: location)
                    isLocationTracking = true
                    pendingMoveToCurrentLocation = false
                }
                locationManager.stopUpdatingLocation()
                print("[Coordinator] ✅ 위치 업데이트 성공: lat=\(location.coordinate.latitude), lng=\(location.coordinate.longitude)")
                print("[Coordinator] 📍 위치 정확도: \(location.horizontalAccuracy)m")
                print("[Coordinator] 📍 위치 시간: \(location.timestamp)")
            }
        }

        nonisolated func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
            print("[Coordinator] ❌ 위치 업데이트 실패: \(error.localizedDescription)")
            if let clError = error as? CLError {
                switch clError.code {
                case .locationUnknown:
                    print("[Coordinator] 위치를 찾을 수 없습니다")
                case .denied:
                    print("[Coordinator] 위치 서비스가 거부되었습니다")
                    Task { @MainActor in
                        pendingMoveToCurrentLocation = false
                    }
                case .network:
                    print("[Coordinator] 네트워크 오류로 위치를 가져올 수 없습니다")
                default:
                    print("[Coordinator] 기타 위치 오류: \(clError.code.rawValue)")
                }
            }
        }

        nonisolated func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
            Task { @MainActor in
                switch status {
                case .authorizedWhenInUse, .authorizedAlways:
                    print("[Coordinator] ✅ 위치 권한 허용됨")
                    if pendingMoveToCurrentLocation,
                       let mapView = mapViewReference ?? locationButton?.superview as? NMFMapView {
                        moveToCurrentLocation(mapView)
                    }
                    pendingMoveToCurrentLocation = false
                case .denied, .restricted:
                    print("[Coordinator] ❌ 위치 권한 거부됨 - 설정에서 권한을 허용해주세요")
                    showLocationPermissionAlert()
                    pendingMoveToCurrentLocation = false
                case .notDetermined:
                    print("[Coordinator] 📍 위치 권한 미결정")
                @unknown default:
                    print("[Coordinator] 📍 위치 권한 상태 불명")
                    break
                }
            }
        }

        @MainActor
        private func showLocationPermissionAlert() {
            pendingMoveToCurrentLocation = false
            guard let window = UIApplication.shared.connectedScenes
                .compactMap({ $0 as? UIWindowScene })
                .first?.windows
                .first(where: { $0.isKeyWindow }) else { return }

            let alert = UIAlertController(
                title: "위치 권한 필요",
                message: "현재 위치를 사용하려면 설정에서 위치 접근 권한을 허용해주세요.",
                preferredStyle: .alert
            )

            alert.addAction(UIAlertAction(title: "설정으로 이동", style: .default) { _ in
                if let settingsUrl = URL(string: UIApplication.openSettingsURLString) {
                    UIApplication.shared.open(settingsUrl)
                }
            })

            alert.addAction(UIAlertAction(title: "취소", style: .cancel))

            window.rootViewController?.present(alert, animated: true)
        }

        private func resetCamera(_ mapView: NMFMapView, with geography: GeographyResponse) {
            print("[Coordinator] 🧭 지도 초기화 실행")
            mapView.positionMode = .disabled
            mapView.locationOverlay.hidden = true
            isLocationTracking = false

            let target = NMGLatLng(lat: geography.adjustedCenterCoordinate.latitude, lng: geography.adjustedCenterCoordinate.longitude)
            let cameraPosition = NMFCameraPosition(target, zoom: Double(geography.zoom))
            let cameraUpdate = NMFCameraUpdate(position: cameraPosition)
            cameraUpdate.animation = .easeIn
            cameraUpdate.animationDuration = 0.8

            Task {
                _ = await mapView.moveCamera(cameraUpdate)
            }

            updateSelectedMarker(mapView: mapView, placeId: nil)
            showLocationButton()
            print("[Coordinator] 🧭 지도 초기 위치로 복귀 완료")
        }

        // MARK: - NMFMapViewCameraDelegate

        nonisolated func mapViewCameraIdle(_ mapView: NMFMapView) {}

        nonisolated func mapView(_ mapView: NMFMapView, cameraIsChangingByReason reason: Int) {
            // 카메라가 변경되는 동안에는 아무 작업도 하지 않음
        }

        // MARK: - NMFMapViewTouchDelegate

        nonisolated func mapView(_ mapView: NMFMapView, didTapMap latlng: NMGLatLng, point: CGPoint) {
            // Return to bottom sheet when tapping empty map space
            Task { @MainActor in
                if viewModel.modalType != .none {
                    // Close any modal and return to bottom sheet
                    viewModel.hideModal()
                    print("[Coordinator] 지도 빈 영역 클릭 - bottom sheet로 복귀")
                }
                updateSelectedMarker(mapView: mapView, placeId: nil)
                // 지도 빈 영역 클릭 시 현위치 버튼 다시 표시
                showLocationButton()
            }
        }
    }
}

#else
// MARK: - Fallback for when NMapsMap is not available

struct NaverMapRepresentable: UIViewRepresentable {
    @ObservedObject var viewModel: MapViewModel

    func makeUIView(context: Context) -> UIView {
        let view = UIView()
        view.backgroundColor = .systemGray6

        let label = UILabel()
        label.text = "Naver Maps SDK가 필요합니다"
        label.textAlignment = .center
        label.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(label)

        NSLayoutConstraint.activate([
            label.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            label.centerYAnchor.constraint(equalTo: view.centerYAnchor)
        ])

        return view
    }

    func updateUIView(_ uiView: UIView, context: Context) {}
}
#endif

// MARK: - Supporting Types
// 실제 MapModels.swift, MapViewModel.swift 파일의 타입들을 사용합니다.

/*
 MARK: - 네이버 지도 SDK 3.22.1 인증 설정 가이드

 방법 1: Info.plist 자동 설정 (권장)
 Info.plist에 다음 키를 추가:
 <key>NMFClientId</key>
 <string>발급받은_클라이언트_ID</string>

 방법 2: AppDelegate 수동 설정
 AppDelegate.swift의 application(_:didFinishLaunchingWithOptions:)에서:
 import NMapsMap
 NMFAuthManager.shared().clientId = "발급받은_클라이언트_ID"

 Bundle ID와 네이버 클라우드 플랫폼에 등록된 iOS 앱 정보가 일치해야 합니다.
 */
