import SwiftUI

struct MapView: View {
    @ObservedObject var viewModel: MapViewModel

    init(viewModel: MapViewModel) {
        self.viewModel = viewModel
    }

    var body: some View {
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
                        // Bottom sheet - "한 눈에 보기" (when no modal is shown)
                        BottomSheetView(viewModel: viewModel)
                            .frame(height: viewModel.sheetDetent.height)
                            .animation(.spring(response: 0.5, dampingFraction: 0.8), value: viewModel.sheetDetent)

                    case .preview:
                        // Preview modal - 얇은 모달 (badge + title only) - 나머지 카테고리용
                        PlacePreviewModal(
                            place: viewModel.selectedPlaceDetail,
                            isLoading: viewModel.isLoadingPlaceDetail,
                            errorMessage: viewModel.placeDetailError,
                            onTap: {
                                // 나머지 카테고리는 탭해도 상세 모달로 전환하지 않음
                                // 아무 동작 없음 (또는 필요시 다른 액션)
                                print("간단한 모달 탭됨 - 추가 동작 없음")
                            },
                            onDismiss: {
                                // Close modal and return to bottom sheet
                                viewModel.hideModal()
                            }
                        )
                            .frame(maxWidth: .infinity)
                            .padding(.horizontal, 16)
                            .padding(.bottom, geometry.safeAreaInsets.bottom + 60)
                            .animation(.easeInOut(duration: 0.25), value: viewModel.modalType == .preview)

                    case .detail:
                        // Detail modal - 상세 모달 (full info)
                        PlaceDetailModal(
                            place: viewModel.selectedPlaceDetail,
                            isLoading: viewModel.isLoadingPlaceDetail,
                            errorMessage: viewModel.placeDetailError
                        ) {
                            // Close modal and return to bottom sheet
                            viewModel.hideModal()
                        }
                            .frame(maxWidth: .infinity)
                            .padding(.horizontal, 16)
                            .padding(.bottom, geometry.safeAreaInsets.bottom + 60)
                            .animation(.easeInOut(duration: 0.25), value: viewModel.modalType == .detail)
                    }
                }
            }
        }
        .task {
            await viewModel.loadMapData()
        }
        .onReceive(NotificationCenter.default.publisher(for: .mapTabReselected)) { _ in
            viewModel.resetCameraToInitial()
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
