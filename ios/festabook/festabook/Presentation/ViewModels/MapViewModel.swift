import Foundation
import SwiftUI

@MainActor
class MapViewModel: NSObject, ObservableObject {
    // MARK: - Published Properties
    @Published var isLoading = false
    @Published var geography: GeographyResponse?
    @Published var allMarkers: [PlaceGeography] = []
    @Published var previewsByPlaceId: [Int: PlacePreview] = [:]
    // 카테고리 다중 선택 (.all 포함)
    @Published var selectedCategories: Set<MapCategory> = [.all]
    @Published var timeTags: [TimeTag] = []
    @Published var selectedTimeTag: TimeTag?
    @Published var isTimeTagDropdownOpen = false
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

    // Place detail 다중 선택 상태
    @Published var selectedPlaceDetails: Set<PlaceDetail> = []
    @Published private(set) var tappedOrder: [Int] = []
    @Published var isLoadingPlaceDetail = false
    @Published var placeDetailError: String?
    @Published var resetCameraRequest: UUID?
    @Published var currentLocationRequestId: UUID?

    enum ModalType {
        case none           // 모달 없음 (바텀시트 표시)
        case preview        // 얇은 프리뷰 모달
        case detail         // 상세 모달
    }


    // MARK: - Computed Properties
    var filteredMarkers: [PlaceGeography] {
        var markers = allMarkers

        // Category filtering (다중 선택)
        let activeCategories = selectedCategories.contains(.all) || selectedCategories.isEmpty
            ? nil
            : Set(selectedCategories.map { $0.rawValue })
        if let active = activeCategories {
            markers = markers.filter { active.contains($0.category) }
        }

        // Time tag filtering: 지오메트리의 timeTags를 직접 사용
        if let selectedTimeTag = selectedTimeTag {
            markers = markers.filter { geography in
                guard let tags = geography.timeTags else { return false }
                return tags.contains(where: { $0.id == selectedTimeTag.id })
            }
        }

        return markers
    }

    // TimeTag 존재 여부에 따른 바텀시트 최대 높이
    var bottomSheetMaxHeight: CGFloat {
        let screenHeight = UIScreen.main.bounds.height
        return timeTags.isEmpty ? screenHeight : screenHeight * 0.9
    }

    // SheetDetent의 높이를 동적으로 계산 (TimeTag 존재 여부에 따라 .large 높이 조정)
    func heightForDetent(_ detent: SheetDetent) -> CGFloat {
        switch detent {
        case .collapsed: return 96
        case .small: return 250
        case .medium: return UIScreen.main.bounds.height * 0.5
        case .large: return bottomSheetMaxHeight
        }
    }

    var filteredPreviews: [PlacePreview] {
        var previews = Array(previewsByPlaceId.values)

        // 바텀시트에는 BOOTH, FOOD_TRUCK, BAR만 표시
        previews = previews.filter { ["BOOTH", "FOOD_TRUCK", "BAR"].contains($0.category) }

        // Category filtering (다중 선택)
        let activeCategories = selectedCategories.contains(.all) || selectedCategories.isEmpty
            ? nil
            : Set(selectedCategories.map { $0.rawValue })
        if let active = activeCategories {
            previews = previews.filter { active.contains($0.category) }
        }

        // Time tag filtering
        if let selectedTimeTag = selectedTimeTag {
            previews = previews.filter { preview in
                guard let timeTags = preview.timeTags else { return false }
                return timeTags.contains(where: { $0.id == selectedTimeTag.id })
            }
        }

        return previews
    }

    var selectedPreview: PlacePreview? {
        guard let selectedPlaceId = selectedPlaceId else { return nil }
        return previewsByPlaceId[selectedPlaceId]
    }

    // 현재 모달에 표시할 마지막 선택 PlaceDetail
    var currentSelectedPlace: PlaceDetail? {
        guard let selectedPlaceId = selectedPlaceId else { return nil }
        return selectedPlaceDetails.first { $0.placeId == selectedPlaceId }
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
    private var hasLoadedInitialData = false

    func loadMapData(forceReload: Bool = false) async {
        if isLoading {
            print("[MapViewModel] loadMapData 스킵 - 이미 로딩 중")
            return
        }

        if hasLoadedInitialData && !forceReload {
            print("[MapViewModel] loadMapData 스킵 - 초기 데이터가 이미 로드됨")
            return
        }

        if forceReload {
            hasLoadedInitialData = false
        }

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

            // Fetch time tags separately and handle failure gracefully
            do {
                let previousSelectedId = selectedTimeTag?.id
                let timeTags = try await repository.fetchTimeTags()
                self.timeTags = timeTags

                if let previousSelectedId,
                   let matchedTag = timeTags.first(where: { $0.id == previousSelectedId }) {
                    self.selectedTimeTag = matchedTag
                    print("  - TimeTags: \(timeTags.count)개, 이전 선택 유지: '\(matchedTag.name)'")
                } else {
                    self.selectedTimeTag = timeTags.first  // 첫 번째 Time Tag를 디폴트로 선택
                    print("  - TimeTags: \(timeTags.count)개, 첫 번째 태그 '\(timeTags.first?.name ?? "nil")' 자동 선택")
                }
            } catch {
                print("[MapViewModel] ⚠️ TimeTags API 호출 실패 (서버에서 아직 구현되지 않음): \(error)")
                self.timeTags = []  // 빈 배열로 설정하여 UI는 정상 작동
                self.selectedTimeTag = nil  // 실패 시 nil
            }

        } catch {
            errorMessage = "지도 데이터를 불러올 수 없습니다."
            showError = true
            print("[MapViewModel] API 호출 실패: \(error)")
        }

        isLoading = false
        hasLoadedInitialData = true
        print("[MapViewModel] loadMapData 완료")
    }

    func prepareForFestivalChange() {
        hasLoadedInitialData = false
        geography = nil
        allMarkers = []
        previewsByPlaceId = [:]
        selectedCategories = [.all]
        timeTags = []
        selectedTimeTag = nil
        selectedPlaceId = nil
        selectedPlaceDetails.removeAll()
        tappedOrder.removeAll()
        modalType = .none
        isLoading = false
        errorMessage = nil
        showError = false
        resetCameraRequest = UUID()
        currentLocationRequestId = nil
        print("[MapViewModel] 🔄 새 축제 진입 - 지도 데이터 초기화")
    }

    // 카테고리 토글 (다중 선택), 필터 변경 시 바텀시트 크기/모달 유지
    func toggleCategory(_ category: MapCategory) {
        var newSet = selectedCategories
        if category == .all {
            newSet = [.all]
        } else {
            if newSet.contains(.all) { newSet.remove(.all) }
            if newSet.contains(category) { newSet.remove(category) } else { newSet.insert(category) }
            if newSet.isEmpty { newSet = [.all] }
        }
        selectedCategories = newSet
    }

    func selectTimeTag(_ timeTag: TimeTag?) {
        selectedTimeTag = timeTag
        print("[MapViewModel] 🏷️ Time-Tag 선택됨: \(timeTag?.name ?? "nil")")
    }

    func toggleTimeTagDropdown() {
        isTimeTagDropdownOpen.toggle()
        print("[MapViewModel] 📋 Time-Tag 드롭다운 토글: \(isTimeTagDropdownOpen)")
    }

    func closeTimeTagDropdown() {
        isTimeTagDropdownOpen = false
        print("[MapViewModel] 📋 Time-Tag 드롭다운 닫힘")
    }

    func selectPlace(_ placeId: Int) {
        print("[MapViewModel] 📍 selectPlace 호출됨: placeId=\(placeId)")

        let alreadySelected = selectedPlaceDetails.contains { $0.placeId == placeId }

        if alreadySelected {
            if let remove = selectedPlaceDetails.first(where: { $0.placeId == placeId }) {
                selectedPlaceDetails.remove(remove)
            }
            tappedOrder.removeAll { $0 == placeId }

            let removedWasCurrent = (selectedPlaceId == placeId)
            if selectedPlaceDetails.isEmpty {
                selectedPlaceId = nil
                hideModal()
            } else if removedWasCurrent {
                selectedPlaceId = nil
                selectedPlaceDetails.removeAll()
                tappedOrder.removeAll()
                hideModal()
            } else {
                if let currentId = selectedPlaceId,
                   let current = selectedPlaceDetails.first(where: { $0.placeId == currentId }) {
                    showModalForPlace(current)
                }
            }
        } else {
            selectedPlaceId = placeId
            placeDetailError = nil
            tappedOrder.removeAll { $0 == placeId }
            tappedOrder.append(placeId)
            Task { await fetchAndAddPlaceDetail(placeId) }
        }
    }

    private func fetchAndAddPlaceDetail(_ placeId: Int) async {
        print("[MapViewModel] 🔄 fetchAndAddPlaceDetail 시작: placeId=\(placeId)")

        isLoadingPlaceDetail = true
        placeDetailError = nil

        do {
            let placeDetail = try await repository.fetchPlaceDetail(placeId)

            selectedPlaceDetails.insert(placeDetail)
            print("[MapViewModel] ✅ 마커 선택 추가: \(placeDetail.title)")
            tappedOrder.removeAll { $0 == placeDetail.placeId }
            tappedOrder.append(placeDetail.placeId)
            showModalForPlace(placeDetail)

        } catch {
            print("[MapViewModel] ❌ PlaceDetail 가져오기 실패: \(error)")
            placeDetailError = "정보를 불러올 수 없습니다"
            modalType = .preview  // 에러가 발생해도 간단한 모달은 표시
        }

        isLoadingPlaceDetail = false
    }

    private func showModalForPlace(_ place: PlaceDetail) {
        switch place.category {
        case "BAR", "BOOTH", "FOOD_TRUCK":
            modalType = .detail
        default:
            modalType = .preview
        }
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

        // PlaceDetail 관련 상태 초기화 (다중 선택은 유지)
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
        selectedPlaceDetails.removeAll()
        tappedOrder.removeAll()
        isLoadingPlaceDetail = false
        placeDetailError = nil
        isMiniCardVisible = false
        selectedMiniCardPlace = nil
        sheetDetent = .small

        resetCameraRequest = UUID()
        print("[MapViewModel] 🧭 지도 초기 상태로 리셋 요청")
    }

    func requestCurrentLocation() {
        currentLocationRequestId = UUID()
        print("[MapViewModel] 📍 현위치 버튼 탭 - 위치 요청 트리거 갱신")
    }

    func resetToInitialState() {
        resetCameraToInitial()
        sheetDetent = .small
        print("[MapViewModel] 한 눈에 보기 초기 상태로 복귀")
    }



    func handleSheetChange(_ detent: SheetDetent) {
        sheetDetent = detent
        // 바텀시트 높이 업데이트
        visibleBottomSheetHeight = heightForDetent(detent)
    }

    func dismissError() {
        showError = false
        errorMessage = nil
    }

    func retryLoading() {
        Task {
            await loadMapData(forceReload: true)
        }
    }

    func dismissMarkerModal() {
        isMarkerModalPresented = false
        selectedMarkerPlace = nil
        selectedPlaceId = nil
    }

    // 전체 버튼에서 호출: 모든 마커 선택/모달 해제, 시트 크기 유지
    func clearAllSelections() {
        selectedPlaceDetails.removeAll()
        tappedOrder.removeAll()
        selectedPlaceId = nil
        modalType = .none
        isLoadingPlaceDetail = false
        placeDetailError = nil
        print("[MapViewModel] 🔄 clearAllSelections 호출 - 선택/모달 초기화")
    }
}
