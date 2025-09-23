import SwiftUI
import UIKit

struct BottomSheetView: View {
    @ObservedObject var viewModel: MapViewModel
    @State private var dragOffset: CGFloat = 0
    let maxHeight: CGFloat
    let safeAreaBottom: CGFloat
    let hasTopBezel: Bool

    var body: some View {
        VStack(spacing: 0) {
            // 상단 여백: large일 때 마커와 겹치지 않도록 추가 간격 부여
            if viewModel.timeTags.isEmpty {
                // 타임태그 없을 때: 마커 바로 밑에서 시작하므로 최소 여백만
                Color.clear
                    .frame(height: viewModel.sheetDetent.topSpacing + (viewModel.sheetDetent == .large ? 8 : 0))
                    .allowsHitTesting(false)
            } else {
                // 타임태그 있을 때: 기존처럼 안전한 여백 유지
                Color.clear
                    .frame(height: viewModel.sheetDetent.topSpacing + (viewModel.sheetDetent == .large ? 40 : 0))
                    .allowsHitTesting(false)
            }

            sheetContent
        }
        .modifier(IgnoresSafeAreaModifier(shouldIgnore: viewModel.timeTags.isEmpty))
    }

    private var sheetContent: some View {
        VStack(spacing: 0) {
            // Handle bar
            RoundedRectangle(cornerRadius: 3)
                .fill(Color.gray.opacity(0.4))
                .frame(width: 36, height: 5)
                .padding(.top, 8)

            // Title (간격 축소)
            HStack {
                Text("한 눈에 보기")
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(.black)
                Spacer()
            }
            .padding(.horizontal, 16)
            .padding(.top, 12)
            .padding(.bottom, 8)

            // Content
            if viewModel.isLoading {
                LoadingContentView(safeAreaBottom: safeAreaBottom, hasTopBezel: hasTopBezel, detent: viewModel.sheetDetent)
            } else {
                PreviewListView(viewModel: viewModel, safeAreaBottom: safeAreaBottom, hasTopBezel: hasTopBezel, detent: viewModel.sheetDetent)
            }
        }
        .background(
            RoundedRectangle(cornerRadius: 20, style: .continuous)
                .fill(Color.white)
                .shadow(color: .black.opacity(0.1), radius: 10, x: 0, y: -5)
        )
        .offset(y: dragOffset)
            .gesture(
            DragGesture()
                .onChanged { value in
                    dragOffset = max(value.translation.height, -100)
                }
                .onEnded { value in
                    withAnimation(.spring()) {
                        handleDragEnd(translation: value.translation.height)
                        dragOffset = 0
                    }
                }
            )
            // 필터 변경에 따른 바텀시트 크기/포지션 자동 변경 방지: 추가 애니메이션 바인딩 제거
    }

    private func handleDragEnd(translation: CGFloat) {
        let velocity = translation

        if velocity > 50 {
            // Drag down
            switch viewModel.sheetDetent {
            case .large:
                viewModel.handleSheetChange(.medium)
            case .medium:
                viewModel.handleSheetChange(.small)
            case .small:
                viewModel.handleSheetChange(.collapsed)
            case .collapsed:
                break
            }
        } else if velocity < -50 {
            // Drag up
            switch viewModel.sheetDetent {
            case .collapsed:
                viewModel.handleSheetChange(.small)
            case .small:
                viewModel.handleSheetChange(.medium)
            case .medium:
                // .large로 갈 때 maxHeight를 초과하지 않도록 제한
                if viewModel.sheetDetent.totalHeight < maxHeight {
                    viewModel.handleSheetChange(.large)
                }
            case .large:
                break
            }
        }
    }
}

struct LoadingContentView: View {
    let safeAreaBottom: CGFloat
    let hasTopBezel: Bool
    let detent: SheetDetent
    private func bottomPadding() -> CGFloat {
        let base = max(safeAreaBottom, 8) + 16
        switch detent {
        case .collapsed:
            return base
        case .small:
            return base + 20
        case .medium:
            return base + 24
        case .large:
            return base + (hasTopBezel ? 46 : 126)
        }
    }
    var body: some View {
        ScrollView {
            LazyVStack(spacing: 0) {
                ForEach(0..<3, id: \.self) { index in
                    LoadingPreviewCard()
                    if index < 2 {
                        Divider()
                            .background(Color.gray.opacity(0.3))
                            .padding(.horizontal, 16)
                    }
                }
            }
            .padding(.top, 4)
            .padding(.bottom, bottomPadding())
        }
    }
}

struct PreviewListView: View {
    @ObservedObject var viewModel: MapViewModel
    let safeAreaBottom: CGFloat
    let hasTopBezel: Bool
    let detent: SheetDetent
    private func bottomPadding() -> CGFloat {
        let base = max(safeAreaBottom, 8) + 16
        switch detent {
        case .collapsed:
            return base
        case .small:
            return base + 20
        case .medium:
            return base + 24
        case .large:
            return base + (hasTopBezel ? 46 : 126)
        }
    }

    var body: some View {
        ScrollView {
            let selectedCategoryNames: Set<String> = Set(viewModel.selectedCategories.map { $0.rawValue })
            let otherCategories: Set<String> = ["STAGE", "PHOTO_BOOTH", "PRIMARY", "EXTRA", "PARKING", "TOILET", "SMOKING", "TRASH_CAN"]
            
            // 선택된 카테고리가 모두 기타 카테고리인지 확인
            let onlyOtherCategories = !selectedCategoryNames.isEmpty && selectedCategoryNames.isSubset(of: otherCategories)
            
            if viewModel.filteredPreviews.isEmpty {
                // 빈 상태 메시지
                VStack(spacing: 12) {
                    Image(systemName: onlyOtherCategories ? "info.circle" : "storefront")
                        .font(.system(size: 24))
                        .foregroundColor(.gray)
                    Text(onlyOtherCategories ? 
                        "기타 부스는 '한눈에 보기'에서 확인할 수 없습니다." :
                        "등록된 부스 정보가 없습니다")
                        .font(.system(size: 14))
                        .foregroundColor(.gray)
                        .multilineTextAlignment(.center)
                }
                .frame(maxWidth: .infinity)
                .padding(.horizontal, 20)
                .padding(.top, 20)
                .padding(.bottom, 40)
            } else if !viewModel.filteredPreviews.isEmpty {
                LazyVStack(spacing: 0) {
                    ForEach(viewModel.filteredPreviews, id: \.id) { preview in
                        PreviewCard(preview: preview) {
                            viewModel.selectPlace(preview.placeId)
                        }

                        if preview.id != viewModel.filteredPreviews.last?.id {
                            Divider()
                                .background(Color.gray.opacity(0.3))
                                .padding(.horizontal, 16)
                        }
                    }
                }
                .padding(.top, 4) // 간격 축소
            .padding(.bottom, bottomPadding())
            }
        }
    }
}

struct PreviewCard: View {
    let preview: PlacePreview
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack(alignment: .center, spacing: 12) {
                // Thumbnail (80×80pt로 조정, 세로 중앙 정렬)
                if let imageUrl = preview.resolvedImageURL, let url = URL(string: imageUrl) {
                    AsyncImage(url: url) { image in
                        image
                            .resizable()
                            .scaledToFill()
                    } placeholder: {
                        RoundedRectangle(cornerRadius: 12)
                            .fill(Color.gray.opacity(0.3))
                    }
                    .frame(width: 80, height: 80)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                } else {
                    // Default placeholder when imageUrl is null
                    RoundedRectangle(cornerRadius: 12)
                        .fill(Color.gray.opacity(0.3))
                        .frame(width: 80, height: 80)
                        .overlay(
                            Image(systemName: "photo")
                                .foregroundColor(.gray)
                                .font(.system(size: 20))
                        )
                }

                VStack(alignment: .leading, spacing: 4) {
                    // Category badge
                    CategoryBadge(category: preview.category)

                    // Title (크기 살짝 줄임)
                    Text(preview.title)
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(.black)
                        .multilineTextAlignment(.leading)
                        .lineLimit(1)

                    // Description (한 줄로 제한)
                    Text(preview.safeDescription)
                        .font(.system(size: 14))
                        .foregroundColor(.gray)
                        .lineLimit(1)
                        .truncationMode(.tail)
                        .multilineTextAlignment(.leading)

                    // Location
                    HStack(spacing: 4) {
                        Image(systemName: "location.fill")
                            .font(.system(size: 10))
                            .foregroundColor(.gray)
                        Text(preview.safeLocation)
                            .font(.system(size: 12))
                            .foregroundColor(.gray)
                    }

                    // 운영시간과 주최자 정보 제거 (포토카드 UI처럼 간단하게)
                }

                Spacer()
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
        }
        .buttonStyle(PlainButtonStyle())
    }
}

struct CategoryBadge: View {
    let category: String

    var body: some View {
        let mapCategory = MapCategory.allCases.first(where: { $0.rawValue == category })

        #if DEBUG
        // 디버깅: 매핑 과정 확인 (Debug 빌드에서만)
        let _ = print("[CategoryBadge] category=\(category) → mapCategory=\(mapCategory?.rawValue ?? "nil"), displayName=\(mapCategory?.displayName ?? "unknown"), filterIcon=\(mapCategory?.filterIconName ?? "none")")
        #endif

        HStack(spacing: 4) {
            // Category icon (FilterIcons 사용)
            if let mapCategory = mapCategory, mapCategory.hasIcon {
                Image(mapCategory.filterIconName)
                    .resizable()
                    .frame(width: 12, height: 12)

                Text(mapCategory.displayName)
                    .font(.system(size: 12, weight: .medium))
                    .foregroundColor(.primary)
            } else if let mapCategory = mapCategory {
                // 아이콘이 없는 경우 (전체 등)
                Text(mapCategory.displayName)
                    .font(.system(size: 12, weight: .medium))
                    .foregroundColor(.primary)
            } else {
                // 매핑되지 않는 경우
                Text(category)
                    .font(.system(size: 12, weight: .medium))
                    .foregroundColor(.primary)
            }
        }
        .padding(.horizontal, 8)
        .padding(.vertical, 4)
        .background(Color.gray.opacity(0.1))
        .clipShape(RoundedRectangle(cornerRadius: 8))
    }
}

struct SelectedPlaceDetailView: View {
    let preview: PlacePreview
    let onBack: () -> Void

    private var category: MapCategory? {
        MapCategory.allCases.first { $0.rawValue == preview.category }
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                // Header with back button
                HStack {
                    Button(action: onBack) {
                        Image(systemName: "chevron.left")
                            .font(.system(size: 18, weight: .medium))
                            .foregroundColor(.primary)
                    }

                    Spacer()

                    Text("장소 정보")
                        .font(.system(size: 16, weight: .medium))
                        .foregroundColor(.secondary)

                    Spacer()

                    // Placeholder for symmetry
                    Image(systemName: "chevron.left")
                        .font(.system(size: 18, weight: .medium))
                        .foregroundColor(.clear)
                }
                .padding(.horizontal, 16)
                .padding(.top, 8)

                // Content based on category
                if let category = category, category.showsDetailedCard {
                    // Detailed card for 주점/부스/푸드트럭
                    DetailedPlaceCard(preview: preview, category: category)
                } else {
                    // Simple view for other categories
                    SimplePlaceView(preview: preview)
                }
            }
        }
    }
}

struct DetailedPlaceCard: View {
    let preview: PlacePreview
    let category: MapCategory

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            // Image
            if let imageUrl = preview.resolvedImageURL, let url = URL(string: imageUrl) {
                AsyncImage(url: url) { image in
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                } placeholder: {
                    RoundedRectangle(cornerRadius: 16)
                        .fill(Color.gray.opacity(0.3))
                        .frame(height: 200)
                        .overlay(
                            Image(systemName: "photo")
                                .foregroundColor(.gray)
                                .font(.system(size: 40))
                        )
                }
                .frame(height: 200)
                .clipShape(RoundedRectangle(cornerRadius: 16))
            }

            VStack(alignment: .leading, spacing: 12) {
                // Category and title
                HStack {
                    CategoryBadge(category: preview.category)
                    Spacer()
                }

                Text(preview.title)
                    .font(.system(size: 24, weight: .bold))
                    .foregroundColor(.primary)

                // Description
                if let description = preview.description {
                    Text(description)
                        .font(.system(size: 16))
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.leading)
                }

                // Info cards
                VStack(spacing: 8) {
                    InfoRow(icon: "location.fill", text: preview.safeLocation)
                    InfoRow(icon: "clock.fill", text: preview.safeTimeInfo)
                    if preview.host != nil {
                        InfoRow(icon: "person.fill", text: preview.safeHost)
                    }
                }
            }
            .padding(.horizontal, 16)
        }
    }
}

struct SimplePlaceView: View {
    let preview: PlacePreview

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                CategoryBadge(category: preview.category)
                Spacer()
            }

            Text(preview.title)
                .font(.system(size: 20, weight: .bold))
                .foregroundColor(.primary)

            InfoRow(icon: "location.fill", text: preview.safeLocation)
        }
        .padding(.horizontal, 16)
    }
}

struct InfoRow: View {
    let icon: String
    let text: String

    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: icon)
                .font(.system(size: 14))
                .foregroundColor(.secondary)
                .frame(width: 16)

            Text(text)
                .font(.system(size: 14))
                .foregroundColor(.secondary)

            Spacer()
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 8)
        .background(Color.gray.opacity(0.1))
        .clipShape(RoundedRectangle(cornerRadius: 8))
    }
}

#Preview {
    BottomSheetView(viewModel: MapViewModel(), maxHeight: UIScreen.main.bounds.height, safeAreaBottom: 34, hasTopBezel: false)
}

// MARK: - Custom Modifier
struct IgnoresSafeAreaModifier: ViewModifier {
    let shouldIgnore: Bool

    func body(content: Content) -> some View {
        if shouldIgnore {
            content.ignoresSafeArea(edges: .top)
        } else {
            content
        }
    }
}
