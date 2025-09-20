import SwiftUI

struct MapView: View {
    @ObservedObject var viewModel: MapViewModel
    @State private var navigationPath: [PlaceDetail] = []

    init(viewModel: MapViewModel) {
        self.viewModel = viewModel
    }

    var body: some View {
        NavigationStack(path: $navigationPath) {
            GeometryReader { geometry in
                ZStack {
                    // Map background
                    #if canImport(NMapsMap)
                    NaverMapRepresentable(viewModel: viewModel)
                        .ignoresSafeArea(edges: .bottom)
                    #else
                    VStack(spacing: 12) {
                        Text("Naver Maps SDK 미연동 상태").font(.headline)
                        Text("SDK 추가 후 NaverMapRepresentable이 자동 활성화")
                    }.padding()
                    #endif

                    // Category filter chips (top)
                    VStack {
                        CategoryFilterView(viewModel: viewModel)
                            .frame(height: 50)
                        Spacer()
                    }



                    // Conditional UI: Show bottom sheet, preview modal, or detail modal
                    VStack {
                        Spacer()
                        switch viewModel.modalType {
                        case .none:
                            BottomSheetView(viewModel: viewModel)
                                .frame(height: viewModel.sheetDetent.totalHeight)
                                .animation(.spring(response: 0.5, dampingFraction: 0.8), value: viewModel.sheetDetent)

                        case .preview:
                            PlacePreviewModal(
                                place: viewModel.selectedPlaceDetail,
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
                            .animation(.easeInOut(duration: 0.25), value: viewModel.modalType == .preview)

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
                            .padding(.bottom, viewModel.sheetDetent.height + 12)
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
        .task {
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
    func detailModal(geometry: GeometryProxy) -> some View {
        let place = viewModel.selectedPlaceDetail
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
