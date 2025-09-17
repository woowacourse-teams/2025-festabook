import SwiftUI

struct BottomSheetView: View {
    @ObservedObject var viewModel: MapViewModel
    @State private var dragOffset: CGFloat = 0

    var body: some View {
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
                LoadingContentView()
            } else {
                PreviewListView(viewModel: viewModel)
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
                viewModel.handleSheetChange(.large)
            case .large:
                break
            }
        }
    }
}

struct LoadingContentView: View {
    var body: some View {
        VStack(spacing: 16) {
            ForEach(0..<3, id: \.self) { _ in
                LoadingPreviewCard()
            }
        }
        .padding(.horizontal, 16)
        .padding(.top, 16)
    }
}

struct LoadingPreviewCard: View {
    var body: some View {
        HStack(alignment: .center, spacing: 12) {
            // Thumbnail placeholder
            RoundedRectangle(cornerRadius: 12)
                .fill(Color.gray.opacity(0.3))
                .frame(width: 80, height: 80)

            VStack(alignment: .leading, spacing: 4) {
                // Category badge placeholder
                RoundedRectangle(cornerRadius: 8)
                    .fill(Color.gray.opacity(0.3))
                    .frame(width: 60, height: 20)

                // Title placeholder
                RoundedRectangle(cornerRadius: 4)
                    .fill(Color.gray.opacity(0.3))
                    .frame(height: 16)

                // Description placeholder
                RoundedRectangle(cornerRadius: 4)
                    .fill(Color.gray.opacity(0.3))
                    .frame(height: 14)

                // Location placeholder
                RoundedRectangle(cornerRadius: 4)
                    .fill(Color.gray.opacity(0.3))
                    .frame(width: 100, height: 12)
            }

            Spacer()
        }
        .padding(.vertical, 12)
    }
}

struct PreviewListView: View {
    @ObservedObject var viewModel: MapViewModel

    var body: some View {
        ScrollView {
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
            .padding(.bottom, 100) // Safe area for bottom navigation
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
                if let imageUrl = preview.imageUrl, let url = URL(string: imageUrl) {
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
            if let imageUrl = preview.imageUrl, let url = URL(string: imageUrl) {
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
    BottomSheetView(viewModel: MapViewModel())
}