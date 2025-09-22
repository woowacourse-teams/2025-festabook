import SwiftUI

struct PlaceMiniCard: View {
    let place: PlacePreview
    let onDismiss: () -> Void

    // 카테고리별 상세 표시 여부 (BAR, BOOTH, FOOD_TRUCK만 상세 표시)
    private var shouldShowDetailedCard: Bool {
        switch place.category {
        case "BAR", "BOOTH", "FOOD_TRUCK":
            return true
        default:
            return false
        }
    }

    var body: some View {
        #if DEBUG
        // 디버깅: 카테고리 매핑 확인 (Debug 빌드에서만)
        let _ = print("[PlaceMiniCard] 표시 중: title=\(place.title), category=\(place.category), isDetailed=\(shouldShowDetailedCard)")
        #endif

        if shouldShowDetailedCard {
            // 상세 카드 (주점, 부스, 푸드트럭만)
            detailedCardView
        } else {
            // 간단한 카드 (운영본부 등)
            simpleCardView
        }
    }
    
    // 상세 카드 뷰
    private var detailedCardView: some View {
        HStack(spacing: 12) {
            // Left content
            VStack(alignment: .leading, spacing: 6) {
                // Category badge
                CategoryBadge(category: place.category)

                // Title with smaller font and ellipsis
                Text(place.title)
                    .font(.system(size: 15, weight: .semibold))
                    .foregroundColor(.black)
                    .lineLimit(1)
                    .truncationMode(.tail)

                // Info section with custom icons (운영시간, 위치, 운영주체)
                VStack(alignment: .leading, spacing: 4) {
                    // Operating hours
                    if !place.safeTimeInfo.isEmpty && place.safeTimeInfo != "시간 미정" {
                        HStack(spacing: 8) {
                            CustomIconView(iconName: "time_icon", fallbackSystemIcon: "clock")
                            Text(place.safeTimeInfo)
                                .font(.system(size: 13))
                                .foregroundColor(.gray)
                                .lineLimit(1)
                        }
                    }

                    // Location
                    HStack(spacing: 8) {
                        CustomIconView(iconName: "location_icon", fallbackSystemIcon: "location")
                        Text(place.safeLocation)
                            .font(.system(size: 13))
                            .foregroundColor(.gray)
                            .lineLimit(1)
                    }

                    // Host/Organizer
                    if !place.safeHost.isEmpty && place.safeHost != "주최자 정보 없음" {
                        HStack(spacing: 8) {
                            CustomIconView(iconName: "host_icon", fallbackSystemIcon: "person.2")
                            Text(place.safeHost)
                                .font(.system(size: 13))
                                .foregroundColor(.gray)
                                .lineLimit(1)
                        }
                    }
                }

                // Description (max 2 lines with ellipsis)
                if !place.safeDescription.isEmpty && place.safeDescription != "설명이 없습니다" {
                    Text(place.safeDescription)
                        .font(.system(size: 12))
                        .foregroundColor(.secondary)
                        .lineLimit(2)
                        .truncationMode(.tail)
                        .fixedSize(horizontal: false, vertical: true)
                }
            }

            Spacer(minLength: 12)

            // Right thumbnail
            ThumbnailView(imageUrl: place.resolvedImageURL)
        }
        .padding(.horizontal, 20)
        .padding(.vertical, 16)
        .background(
            RoundedRectangle(cornerRadius: 16, style: .continuous)
                .fill(Color.white)
                .shadow(color: .black.opacity(0.08), radius: 12, x: 0, y: 4)
        )
        .frame(maxWidth: .infinity)
    }
    
    // 간단한 카드 뷰 (운영본부, 화장실, 쓰레기통 등)
    private var simpleCardView: some View {
        HStack(spacing: 12) {
            // Category badge
            CategoryBadge(category: place.category)

            // Title
            Text(place.title)
                .font(.system(size: 15, weight: .medium))
                .foregroundColor(.primary)
                .lineLimit(1)
                .truncationMode(.tail)

            Spacer()
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 10)
        .background(
            RoundedRectangle(cornerRadius: 12, style: .continuous)
                .fill(Color.white)
                .shadow(color: .black.opacity(0.06), radius: 8, x: 0, y: 2)
        )
        .frame(maxWidth: .infinity)
    }
}


struct CustomIconView: View {
    let iconName: String
    let fallbackSystemIcon: String

    var body: some View {
        Image(systemName: fallbackSystemIcon)
            .font(.system(size: 12, weight: .medium))
            .foregroundColor(.gray)
            .frame(width: 12, height: 12)
            .fixedSize()
    }
}

struct ThumbnailView: View {
    let imageUrl: String?

    var body: some View {
        if let resolved = ImageURLResolver.resolve(imageUrl), let url = URL(string: resolved) {
            AsyncImage(url: url) { image in
                image
                    .resizable()
                    .scaledToFill()
            } placeholder: {
                RoundedRectangle(cornerRadius: 16)
                    .fill(Color.gray.opacity(0.3))
                    .overlay(
                        Image(systemName: "photo")
                            .foregroundColor(.gray)
                            .font(.system(size: 20))
                    )
            }
            .frame(width: 80, height: 80)
            .clipShape(RoundedRectangle(cornerRadius: 16))
        } else {
            RoundedRectangle(cornerRadius: 16)
                .fill(Color.gray.opacity(0.3))
                .frame(width: 80, height: 80)
                .overlay(
                    Image(systemName: "photo")
                        .foregroundColor(.gray)
                        .font(.system(size: 20))
                )
        }
    }
}

#Preview {
    PlaceMiniCard(
        place: PlacePreview(
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
        )
    ) {
        print("Card dismissed")
    }
    .padding()
    .background(Color.gray.opacity(0.1))
}
