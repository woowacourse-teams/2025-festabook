import SwiftUI

struct MapView: View {
    @ObservedObject var viewModel: MapViewModel
    @State private var navigationPath: [PlaceDetail] = []
    private let loadTrigger: UUID?

    init(viewModel: MapViewModel, loadTrigger: UUID? = nil) {
        self.viewModel = viewModel
        self.loadTrigger = loadTrigger
    }

    var body: some View {
        NavigationStack(path: $navigationPath) {
            GeometryReader { geometry in
                ZStack {
                    // Full screen map background
                    #if canImport(NMapsMap)
                    NaverMapRepresentable(viewModel: viewModel)
                        .ignoresSafeArea()
                    #else
                    Color.gray.opacity(0.3)
                        .ignoresSafeArea()
                        .overlay(
                            VStack(spacing: 12) {
                                Text("Naver Maps SDK 미연동 상태").font(.headline)
                                Text("SDK 추가 후 NaverMapRepresentable이 자동 활성화")
                            }
                        )
                    #endif

                    // UI overlays
                    VStack(spacing: 0) {
                        // Compact top bezel with time tag filter
                        topBezelWithFilter(geometry: geometry)

                        // Category filter below bezel
                        VStack {
                            CategoryFilterView(viewModel: viewModel)
                                .frame(height: 50)
                            Spacer()
                        }
                    }

                    // Time tag dropdown overlay (separate layer)
                    timeTagDropdownOverlay(geometry: geometry)

                    // Conditional UI: Show bottom sheet, preview modal, or detail modal
                    VStack {
                        Spacer()
                        switch viewModel.modalType {
                        case .none:
                            BottomSheetView(
                                viewModel: viewModel,
                                maxHeight: viewModel.bottomSheetMaxHeight,
                                safeAreaBottom: geometry.safeAreaInsets.bottom,
                                hasTopBezel: !viewModel.timeTags.isEmpty
                            )
                                .frame(height: viewModel.heightForDetent(viewModel.sheetDetent))
                                .animation(.spring(response: 0.5, dampingFraction: 0.8), value: viewModel.sheetDetent)

                        case .preview:
                            PlacePreviewModal(
                                place: viewModel.currentSelectedPlace,
                                isLoading: viewModel.isLoadingPlaceDetail,
                                errorMessage: viewModel.placeDetailError,
                                onTap: nil,
                                onDismiss: {
                                    viewModel.hideModal()
                                }
                            )
                            .frame(maxWidth: .infinity)
                            .padding(.horizontal, 16)
                            .padding(.bottom, geometry.safeAreaInsets.bottom + 60)

                        case .detail:
                            detailModal(geometry: geometry)
                        }
                    }
                    
                    // Floating current location button that tracks sheet height
                    if viewModel.modalType == .none && viewModel.sheetDetent != .large {
                        VStack {
                            Spacer()
                            HStack {
                                CurrentLocationButton(viewModel: viewModel)
                                    .padding(.leading, 16)
                                Spacer()
                            }
                            .padding(.bottom, viewModel.sheetDetent.totalHeight + 20)
                        }
                    }
                }
            }
            .navigationDestination(for: PlaceDetail.self) { place in
                PlaceDetailView(
                    place: place,
                    onClose: {
                        navigationPath.removeAll()
                    }
                )
            }
        }
        .task(id: loadTrigger) {
            guard loadTrigger != nil else { return }
            await viewModel.loadMapData()
        }
        .onReceive(NotificationCenter.default.publisher(for: .mapTabReselected)) { _ in
            viewModel.resetToInitialState()
        }
        .alert("오류", isPresented: $viewModel.showError) {
            Button("다시 시도") {
                viewModel.retryLoading()
            }
            Button("확인") {
                viewModel.dismissError()
            }
        } message: {
            Text(viewModel.errorMessage ?? "알 수 없는 오류가 발생했습니다.")
        }
    }
}

// MARK: - Private Helpers

private extension MapView {

    @ViewBuilder
    func topBezelWithFilter(geometry: GeometryProxy) -> some View {
        HStack {
            if !viewModel.timeTags.isEmpty {
                timeTagButton
            }
            Spacer()
        }
        .padding(.horizontal, 16)
        .padding(.bottom, 6)
        .background(Color.white) // 하얀색 베젤 배경
    }

    @ViewBuilder
    private var timeTagButton: some View {
        Button(action: {
            withAnimation(.easeInOut(duration: 0.2)) {
                viewModel.toggleTimeTagDropdown()
            }
        }) {
            HStack(spacing: 6) {
                if let selectedTimeTag = viewModel.selectedTimeTag {
                    Text(selectedTimeTag.name)
                        .font(.system(size: 16, weight: .semibold))
                        .foregroundColor(.black)
                        .lineLimit(1)
                }

                Image(systemName: "chevron.down")
                    .font(.system(size: 12, weight: .semibold))
                    .foregroundColor(.gray)
                    .rotationEffect(.degrees(viewModel.isTimeTagDropdownOpen ? 180 : 0))
                    .animation(.easeInOut(duration: 0.2), value: viewModel.isTimeTagDropdownOpen)
                    .opacity(1)  // 항상 표시
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 8)
            .background(Color.white)
            .overlay(
                RoundedRectangle(cornerRadius: 10)
                    .stroke(Color.white, lineWidth: 1)
            )
            .clipShape(RoundedRectangle(cornerRadius: 10))
        }
        .buttonStyle(PlainButtonStyle())
    }

    @ViewBuilder
    func timeTagDropdownOverlay(geometry: GeometryProxy) -> some View {
        if viewModel.isTimeTagDropdownOpen && !viewModel.timeTags.isEmpty {
            ZStack {
                // 완전 투명한 배경 (blur 없음)
                Color.clear
                    .contentShape(Rectangle())
                    .onTapGesture {
                        withAnimation(.easeInOut(duration: 0.15)) {
                            viewModel.closeTimeTagDropdown()
                        }
                    }
                    .ignoresSafeArea()

                // 드롭다운 리스트 - 베젤 바로 밑에 딱 붙게 배치
                VStack(spacing: 0) {
                    VStack(spacing: 0) {
                        // TimeTag 항목들 - timeTags가 비어있지 않을 때만 표시
                        ForEach(Array(viewModel.timeTags.enumerated()), id: \.element.id) { index, timeTag in
                            customDropdownItem(
                                title: timeTag.name,
                                isSelected: viewModel.selectedTimeTag?.id == timeTag.id,
                                isFirst: index == 0,
                                isDisabled: false
                            ) { viewModel.selectTimeTag(timeTag) }
                        }
                    }
                    .background(Color.white)
                    .cornerRadius(8)
                    .shadow(color: .black.opacity(0.25), radius: 10, x: 0, y: 5) // 더 강한 그림자로 덮어쓰기 효과
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(Color.gray.opacity(0.12), lineWidth: 0.5)
                    )
                    .frame(width: 120) // 더 컴팩트하게 (140 -> 120)

                    Spacer()
                }
                .frame(maxWidth: .infinity, alignment: .topLeading)
                .padding(.leading, 8) // 왼쪽 여백
                .padding(.top, geometry.safeAreaInsets.top) // 베젤 바로 밑, 마커들 위에 덮어쓰기
                .transition(.asymmetric(
                    insertion: .opacity.combined(with: .scale(scale: 0.96, anchor: .topLeading)),
                    removal: .opacity.combined(with: .scale(scale: 0.96, anchor: .topLeading))
                ))
            }
            .zIndex(2000) // 마커들보다 훨씬 높은 z-index로 완전히 덮어쓰기
        }
    }

    @ViewBuilder
    private func customDropdownItem(
        title: String,
        isSelected: Bool,
        isFirst: Bool,
        isDisabled: Bool = false,
        action: @escaping () -> Void
    ) -> some View {
        Button(action: {
            action()
            withAnimation(.easeInOut(duration: 0.15)) {
                viewModel.closeTimeTagDropdown()
            }
        }) {
            VStack(spacing: 0) {
                HStack {
                    Text(title)
                        .font(.system(size: 14, weight: .regular))
                        .foregroundColor(isSelected ? .black : Color(.systemGray))
                        .frame(maxWidth: .infinity, alignment: .leading)
                }
                .padding(.horizontal, 12) // 더 컴팩트하게
                .padding(.vertical, 10) // 더 컴팩트하게
                .background(isSelected ? Color.blue.opacity(0.08) : Color.clear) // 더 연한 하늘색
                .contentShape(Rectangle()) // 전체 영역 터치 가능

                // 구분선 - 첫 번째 항목이 아닐 때만 표시
                if !isFirst {
                    Rectangle()
                        .fill(Color(.systemGray4).opacity(0.4))
                        .frame(height: 0.5)
                        .padding(.leading, 12)
                }
            }
        }
        .buttonStyle(PlainButtonStyle())
    }
    @ViewBuilder
    func detailModal(geometry: GeometryProxy) -> some View {
        let place = viewModel.currentSelectedPlace
        let isNavigableCategory = ["BOOTH", "BAR", "FOOD_TRUCK"].contains(place?.category ?? "")

        let modal = PlaceDetailModal(
            place: place,
            isLoading: viewModel.isLoadingPlaceDetail,
            errorMessage: viewModel.placeDetailError
        ) {
            viewModel.hideModal()
        }
        .frame(maxWidth: .infinity)
        .padding(.horizontal, 16)
        .padding(.bottom, geometry.safeAreaInsets.bottom + 60)
        .animation(.easeInOut(duration: 0.25), value: viewModel.modalType == .detail)

        if isNavigableCategory, let place {
            modal
                .contentShape(Rectangle())
                .onTapGesture {
                    navigationPath.append(place)
                }
                .accessibilityAddTraits(.isButton)
        } else {
            modal
        }
    }
}
