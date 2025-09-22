import SwiftUI

struct MarkerDetailModal: View {
    let place: PlacePreview

    private var category: MapCategory? {
        MapCategory.allCases.first { $0.rawValue == place.category }
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            // Handle bar (pill drag indicator)
            RoundedRectangle(cornerRadius: 3)
                .fill(Color.gray.opacity(0.4))
                .frame(width: 36, height: 5)
                .frame(maxWidth: .infinity)
                .padding(.top, 12)
                .padding(.bottom, 20)

            // Main content
            HStack(alignment: .top, spacing: 16) {
                // Left content
                VStack(alignment: .leading, spacing: 12) {
                    // Category with icon
                    HStack(spacing: 8) {
                        if let category = category, category.hasIcon {
                            ZStack {
                                Circle()
                                    .fill(categoryBackgroundColor)
                                    .frame(width: 32, height: 32)

                                Image(category.filterIconName)
                                    .resizable()
                                    .frame(width: 16, height: 16)
                                    .foregroundColor(.white)
                            }
                        }

                        Text(category?.displayName ?? place.category)
                            .font(.system(size: 14, weight: .semibold))
                            .foregroundColor(.secondary)
                    }

                    // Title (최대 2줄)
                    Text(place.title)
                        .font(.system(size: 20, weight: .bold))
                        .foregroundColor(.primary)
                        .lineLimit(2)
                        .multilineTextAlignment(.leading)

                    // Info rows
                    VStack(alignment: .leading, spacing: 8) {
                        // Time (있을 경우만)
                        if place.startTime != nil || place.endTime != nil {
                            InfoRowCompact(icon: "clock.fill", text: place.safeTimeInfo)
                        }

                        // Location
                        InfoRowCompact(icon: "location.fill", text: place.safeLocation)

                        // Host (있을 경우만)
                        if place.host != nil {
                            InfoRowCompact(icon: "person.fill", text: place.safeHost)
                        }
                    }

                    // Description (최대 2줄, 초과 시 줄임표)
                    if let description = place.description {
                        Text(description)
                            .font(.system(size: 14))
                            .foregroundColor(.secondary)
                            .lineLimit(2)
                            .truncationMode(.tail)
                            .multilineTextAlignment(.leading)
                    }
                }

                Spacer()

                // Right image (96×96)
                if let imageUrl = place.imageUrl, let url = URL(string: imageUrl) {
                    AsyncImage(url: url) { image in
                        image
                            .resizable()
                            .scaledToFill()
                    } placeholder: {
                        RoundedRectangle(cornerRadius: 12)
                            .fill(Color.gray.opacity(0.3))
                            .overlay(
                                Image(systemName: "photo")
                                    .foregroundColor(.gray)
                                    .font(.system(size: 24))
                            )
                    }
                    .frame(width: 96, height: 96)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                } else {
                    // Placeholder illustration
                    RoundedRectangle(cornerRadius: 12)
                        .fill(Color.gray.opacity(0.2))
                        .frame(width: 96, height: 96)
                        .overlay(
                            Image(systemName: "photo")
                                .foregroundColor(.gray)
                                .font(.system(size: 28))
                        )
                }
            }
            .padding(.horizontal, 20)

            Spacer()
        }
        .background(
            RoundedRectangle(cornerRadius: 28, style: .continuous)
                .fill(Color.white)
        )
    }

    private var categoryBackgroundColor: Color {
        guard let category = category else { return .gray }

        switch category {
        case .bar: return .orange
        case .booth: return .blue
        case .foodTruck: return .green
        case .photobooth: return .purple
        case .stage: return .red
        default: return .gray
        }
    }
}

struct InfoRowCompact: View {
    let icon: String
    let text: String

    var body: some View {
        HStack(spacing: 6) {
            Image(systemName: icon)
                .font(.system(size: 12))
                .foregroundColor(.secondary)
                .frame(width: 14)

            Text(text)
                .font(.system(size: 13))
                .foregroundColor(.secondary)
        }
    }
}

struct DetailedMarkerCard: View {
    let place: PlacePreview
    let category: MapCategory

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            // Image
            if let imageUrl = place.imageUrl, let url = URL(string: imageUrl) {
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
                    MarkerCategoryBadge(category: place.category)
                    Spacer()
                }

                Text(place.title)
                    .font(.system(size: 24, weight: .bold))
                    .foregroundColor(.primary)

                // Description
                if let description = place.description {
                    Text(description)
                        .font(.system(size: 16))
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.leading)
                }

                // Location info
                MarkerInfoRow(icon: "location.fill", text: place.safeLocation)
            }
            .padding(.horizontal, 16)
        }
    }
}

struct SimpleMarkerView: View {
    let place: PlacePreview

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                MarkerCategoryBadge(category: place.category)
                Spacer()
            }

            Text(place.title)
                .font(.system(size: 20, weight: .bold))
                .foregroundColor(.primary)

            MarkerInfoRow(icon: "location.fill", text: place.safeLocation)
        }
        .padding(.horizontal, 16)
    }
}

struct MarkerCategoryBadge: View {
    let category: String

    var body: some View {
        HStack(spacing: 4) {
            // Category icon
            if let mapCategory = MapCategory.allCases.first(where: { $0.rawValue == category }), mapCategory.hasIcon {
                Image(mapCategory.iconName)
                    .resizable()
                    .frame(width: 16, height: 16)

                Text(mapCategory.displayName)
                    .font(.system(size: 14, weight: .medium))
                    .foregroundColor(.primary)
            } else if let mapCategory = MapCategory.allCases.first(where: { $0.rawValue == category }) {
                // 아이콘이 없는 경우 (전체 등)
                Text(mapCategory.displayName)
                    .font(.system(size: 14, weight: .medium))
                    .foregroundColor(.primary)
            } else {
                Text(category)
                    .font(.system(size: 14, weight: .medium))
                    .foregroundColor(.primary)
            }
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 6)
        .background(Color.gray.opacity(0.1))
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }
}

struct MarkerInfoRow: View {
    let icon: String
    let text: String

    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: icon)
                .font(.system(size: 16))
                .foregroundColor(.secondary)
                .frame(width: 20)

            Text(text)
                .font(.system(size: 16))
                .foregroundColor(.secondary)

            Spacer()
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .background(Color.gray.opacity(0.1))
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }
}

#Preview {
    MarkerDetailModal(place: PlacePreview(
        placeId: 1,
        imageUrl: nil,
        category: "BAR",
        title: "사회과학대학 주점",
        description: "플레이스 설명이 아직 없습니다.",
        location: "진관홀&학술정보원 앞",
        host: "사회과학대학",
        startTime: "18:00",
        endTime: "23:59",
        coordinate: nil,
        timeTags: nil
    ))
}