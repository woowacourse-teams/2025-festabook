import Foundation
import SwiftUI

@MainActor
class MapViewModel: NSObject, ObservableObject {
    // MARK: - Published Properties
    @Published var isLoading = false
    @Published var geography: GeographyResponse?
    @Published var allMarkers: [PlaceGeography] = []
    @Published var previewsByPlaceId: [Int: PlacePreview] = [:]
    @Published var selectedCategory: MapCategory = .all
    @Published var selectedPlaceId: Int?
    @Published var sheetDetent: SheetDetent = .small   // 초기 상태를 small로 설정 (아이템 2개 정도)
    @Published var errorMessage: String?
    @Published var showError = false
    @Published var isMarkerModalPresented = false
    @Published var selectedMarkerPlace: PlacePreview?
    @Published var visibleBottomSheetHeight: CGFloat = 300 // 바텀시트 가시 높이
    @Published var isMiniCardVisible: Bool = false
    @Published var selectedMiniCardPlace: PlacePreview?

    // 새로운 모달 상태 관리
    @Published var modalType: ModalType = .none
    @Published var selectedPlace: PlacePreview?

    // Place detail 관련 상태
    @Published var selectedPlaceDetail: PlaceDetail?
    @Published var isLoadingPlaceDetail = false
    @Published var placeDetailError: String?
    @Published var resetCameraRequest: UUID?

    enum ModalType {
        case none           // 모달 없음 (바텀시트 표시)
        case preview        // 얇은 프리뷰 모달
        case detail         // 상세 모달
    }


    // MARK: - Computed Properties
    var filteredMarkers: [PlaceGeography] {
        guard selectedCategory != .all else { return allMarkers }
        return allMarkers.filter { $0.category == selectedCategory.rawValue }
    }

    var filteredPreviews: [PlacePreview] {
        guard selectedCategory != .all else { return Array(previewsByPlaceId.values) }
        return previewsByPlaceId.values.filter { $0.category == selectedCategory.rawValue }
    }

    var selectedPreview: PlacePreview? {
        guard let selectedPlaceId = selectedPlaceId else { return nil }
        return previewsByPlaceId[selectedPlaceId]
    }

    // Helper method to get preview by place ID
    func preview(for placeId: Int) -> PlacePreview? {
        return previewsByPlaceId[placeId]
    }

    // MARK: - Dependencies
    private let repository: MapRepositoryProtocol

    override init() {
        self.repository = MapRepository()
        super.init()
    }

    init(repository: MapRepositoryProtocol = MapRepository()) {
        self.repository = repository
        super.init()
    }

    // MARK: - Public Methods
    func loadMapData() async {
        print("[MapViewModel] loadMapData 시작")
        isLoading = true
        errorMessage = nil

        do {
            print("[MapViewModel] API 호출 시작")
            async let geographyTask = repository.fetchGeography()
            async let markersTask = repository.fetchPlaceGeographies()
            async let previewsTask = repository.fetchPlacePreviews()

            let (geography, markers, previews) = try await (geographyTask, markersTask, previewsTask)

            print("[MapViewModel] API 응답 성공:")
            print("  - Geography: center=(\(geography.centerCoordinate.latitude), \(geography.centerCoordinate.longitude)), zoom=\(geography.zoom)")
            print("  - Markers: \(markers.count)개")
            print("  - Previews: \(previews.count)개")

            self.geography = geography
            self.allMarkers = markers
            self.previewsByPlaceId = Dictionary(uniqueKeysWithValues: previews.map { ($0.placeId, $0) })

        } catch {
            errorMessage = "지도 데이터를 불러올 수 없습니다."
            showError = true
            print("[MapViewModel] API 호출 실패: \(error)")
        }

        isLoading = false
        print("[MapViewModel] loadMapData 완료")
    }

    func selectCategory(_ category: MapCategory) {
        selectedCategory = category
        hideModal()
    }

    func selectPlace(_ placeId: Int) {
        print("[MapViewModel] 📍 selectPlace 호출됨: placeId=\(placeId)")

        guard selectedPlaceId != placeId || selectedPlaceDetail == nil else {
            print("[MapViewModel] 동일 장소 재선택 - 상태 유지")
            return
        }

        selectedPlaceId = placeId

        // 기존 상태 초기화
        selectedPlaceDetail = nil
        placeDetailError = nil

        // 상세 정보 가져오기
        Task {
            await fetchPlaceDetail(placeId)
        }
    }

    func fetchPlaceDetail(_ placeId: Int) async {
        print("[MapViewModel] 🔄 fetchPlaceDetail 시작: placeId=\(placeId)")

        isLoadingPlaceDetail = true
        placeDetailError = nil

        do {
            let placeDetail = try await repository.fetchPlaceDetail(placeId)

            self.selectedPlaceDetail = placeDetail

            print("[MapViewModel] ✅ PlaceDetail 가져오기 성공: title=\(placeDetail.title), category=\(placeDetail.category)")

            // 카테고리에 따라 적절한 모달 표시
            switch placeDetail.category {
            case "BAR", "BOOTH", "FOOD_TRUCK":
                modalType = .detail
                print("[MapViewModel] 🎯 상세 모달 표시: \(placeDetail.title) (category: \(placeDetail.category))")
            default:
                modalType = .preview
                print("[MapViewModel] 🎯 간단한 모달 표시: \(placeDetail.title) (category: \(placeDetail.category))")
            }

        } catch {
            print("[MapViewModel] ❌ PlaceDetail 가져오기 실패: \(error)")
            placeDetailError = "정보를 불러올 수 없습니다"
            modalType = .preview  // 에러가 발생해도 간단한 모달은 표시
        }

        isLoadingPlaceDetail = false
    }

    // Mini card management
    func showMiniCard(for placeId: Int) {
        if let preview = previewsByPlaceId[placeId] {
            selectedMiniCardPlace = preview
            isMiniCardVisible = true
        }
    }

    func hideMiniCard() {
        isMiniCardVisible = false
        selectedMiniCardPlace = nil
        selectedPlaceId = nil
        print("[MapViewModel] 카드 숨김, bottom sheet 복귀")
    }

    // 더 이상 사용되지 않는 메서드들 제거됨
    // selectPlace()에서 카테고리에 따라 직접 적절한 모달을 표시

    func hideModal() {
        modalType = .none
        selectedPlace = nil
        selectedPlaceId = nil

        // PlaceDetail 관련 상태 초기화
        selectedPlaceDetail = nil
        isLoadingPlaceDetail = false
        placeDetailError = nil

        // 바텀시트를 적절한 높이(small)로 복구 - 아이템 2개 정도 보이는 크기
        sheetDetent = .small

        print("[MapViewModel] 모달 숨김, bottom sheet 복귀 (small 크기)")
    }

    func resetCameraToInitial() {
        guard geography != nil else { return }

        modalType = .none
        selectedPlaceId = nil
        selectedPlaceDetail = nil
        isLoadingPlaceDetail = false
        placeDetailError = nil
        isMiniCardVisible = false
        selectedMiniCardPlace = nil
        sheetDetent = .small

        resetCameraRequest = UUID()
        print("[MapViewModel] 🧭 지도 초기 상태로 리셋 요청")
    }

    func resetToInitialState() {
        resetCameraToInitial()
        sheetDetent = .small
        print("[MapViewModel] 한 눈에 보기 초기 상태로 복귀")
    }



    func handleSheetChange(_ detent: SheetDetent) {
        sheetDetent = detent
        // 바텀시트 높이 업데이트
        visibleBottomSheetHeight = detent.height
    }

    func dismissError() {
        showError = false
        errorMessage = nil
    }

    func retryLoading() {
        Task {
            await loadMapData()
        }
    }

    func dismissMarkerModal() {
        isMarkerModalPresented = false
        selectedMarkerPlace = nil
        selectedPlaceId = nil
    }
}
