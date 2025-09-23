import SwiftUI

struct LostItemGridView: View {
    @ObservedObject var viewModel: LostItemViewModel
    @StateObject private var guideViewModel = LostItemGuideViewModel()

    private let columns = Array(repeating: GridItem(.flexible(), spacing: 16), count: 2)

    var body: some View {
        ZStack {
            Color.white
                .ignoresSafeArea()

            switch viewModel.state {
            case .idle:
                idleView
            case .loading:
                loadingView
            case .loaded:
                gridView
            case .empty:
                emptyView
            case .failed(let message):
                errorView(message: message)
            }

            // Modal overlay (Android 스타일)
            if let selected = viewModel.selectedItem {
                LostItemDetailModal(item: selected) {
                    withAnimation(.easeInOut(duration: 0.2)) {
                        viewModel.dismissModal()
                    }
                }
                .transition(.opacity)
            }
        }
        .refreshable {
            async let lostItemTask = viewModel.refresh()
            async let guideTask = guideViewModel.loadLostItemGuide()
            await (lostItemTask, guideTask)
        }
        .task {
            await guideViewModel.loadLostItemGuide()
        }
    }

    // MARK: - Loading View (공지사항과 동일한 형태)
    private var loadingView: some View {
        ScrollView {
            LazyVStack(spacing: 16) {
                // 분실물 가이드 카드 (항상 상단에 표시)
                LostItemGuideCard(viewModel: guideViewModel)
                    .padding(.horizontal, 20)

                ProgressView("분실물을 불러오는 중...")
                    .padding(.vertical, 40)
            }
            .padding(.top, 16)
            .padding(.bottom, 40)
        }
        }


    // MARK: - Idle View
    private var idleView: some View {
        ScrollView {
            LazyVStack(spacing: 16) {
                // 분실물 가이드 카드 (항상 상단에 표시)
                LostItemGuideCard(viewModel: guideViewModel)
                    .padding(.horizontal, 20)
            }
            .padding(.top, 16)
            .padding(.bottom, 100)
        }
    }


    // MARK: - Grid View
    private var gridView: some View {
        ScrollView {
            LazyVStack(spacing: 16) {
                // 분실물 가이드 카드 (항상 상단에 표시)
                LostItemGuideCard(viewModel: guideViewModel)
                    .padding(.horizontal, 20)

                // 분실물 그리드
                LazyVGrid(columns: columns, spacing: 16) {
                    ForEach(viewModel.items) { item in
                        LostItemCell(item: item) {
                            withAnimation(.easeInOut(duration: 0.15)) {
                                viewModel.selectItem(item)
                            }
                        }
                        .aspectRatio(1, contentMode: .fit)
                    }
                }
                .padding(.horizontal, 16)
                .padding(.top, 0)
            }
            .padding(.top, 16)
            .padding(.bottom, 100)
        }
    }

    // MARK: - Empty View (공지사항과 동일한 형태)
    private var emptyView: some View {
        ScrollView {
            LazyVStack(spacing: 16) {
                // 분실물 가이드 카드 (항상 상단에 표시)
                LostItemGuideCard(viewModel: guideViewModel)
                    .padding(.horizontal, 20)

                VStack(spacing: 12) {
                    Image(systemName: "shippingbox")
                        .font(.system(size: 24))
                        .foregroundColor(.gray)
                    Text("등록된 분실물이 없습니다")
                        .font(.system(size: 14))
                        .foregroundColor(.gray)
                }
                .padding(.vertical, 40)
            }
            .padding(.top, 16)
            .padding(.bottom, 100)
        }
    }

    // MARK: - Error View (공지사항과 동일한 형태)
    private func errorView(message: String) -> some View {
        ScrollView {
            LazyVStack(spacing: 16) {
                // 분실물 가이드 카드 (항상 상단에 표시)
                LostItemGuideCard(viewModel: guideViewModel)
                    .padding(.horizontal, 20)

                VStack(spacing: 12) {
                    Image(systemName: "exclamationmark.triangle")
                        .font(.system(size: 24))
                        .foregroundColor(.gray)
                    Text(message)
                        .font(.system(size: 14))
                        .foregroundColor(.gray)
                        .multilineTextAlignment(.center)
                }
                .padding(.vertical, 40)
            }
            .padding(.top, 16)
            .padding(.bottom, 100)
        }
    }
}

