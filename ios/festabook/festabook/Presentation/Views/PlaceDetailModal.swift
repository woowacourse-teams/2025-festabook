import SwiftUI

struct PlaceDetailModal: View {
    let place: PlaceDetail?
    let isLoading: Bool
    let errorMessage: String?
    let onDismiss: () -> Void

    var body: some View {
        if let errorMessage = errorMessage {
            // Error state
            VStack(spacing: 8) {
                Image(systemName: "exclamationmark.triangle")
                    .font(.system(size: 24))
                    .foregroundColor(.orange)

                Text(errorMessage)
                    .font(.system(size: 15, weight: .medium))
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 16)
            .background(
                RoundedRectangle(cornerRadius: 16, style: .continuous)
                    .fill(Color.white)
                    .shadow(color: .black.opacity(0.08), radius: 12, x: 0, y: 4)
            )
            .frame(maxWidth: .infinity)
        } else if let place = place {
            // Loaded state
            HStack(alignment: .top, spacing: 10) {
                // Left content - 더 많은 공간 확보
                VStack(alignment: .leading, spacing: 6) {
                    // Category badge
                    CategoryBadge(category: place.category)

                    // Title with optimized layout
                    Text(place.title)
                        .font(.system(size: 15, weight: .semibold))
                        .foregroundColor(.black)
                        .lineLimit(1)
                        .truncationMode(.tail)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .layoutPriority(1)

                    // Info section with custom icons (운영시간, 위치, 운영주체)
                    VStack(alignment: .leading, spacing: 4) {
                        // Operating hours
                        if !place.safeTimeInfo.isEmpty && place.safeTimeInfo != "시간 미정" {
                            HStack(spacing: 6) {
                                CustomIconView(iconName: "time_icon", fallbackSystemIcon: "clock")
                                Text(place.safeTimeInfo)
                                    .font(.system(size: 13))
                                    .foregroundColor(.gray)
                                    .lineLimit(1)
                                    .truncationMode(.tail)
                                    .frame(maxWidth: .infinity, alignment: .leading)
                                    .layoutPriority(1)
                            }
                        }

                        // Location
                        HStack(spacing: 6) {
                            CustomIconView(iconName: "location_icon", fallbackSystemIcon: "location")
                            Text(place.safeLocation)
                                .font(.system(size: 13))
                                .foregroundColor(.gray)
                                .lineLimit(1)
                                .truncationMode(.tail)
                                .frame(maxWidth: .infinity, alignment: .leading)
                                .layoutPriority(1)
                        }

                        // Host/Organizer
                        if !place.safeHost.isEmpty && place.safeHost != "주최자 정보 없음" {
                            HStack(spacing: 6) {
                                CustomIconView(iconName: "host_icon", fallbackSystemIcon: "person.2")
                                Text(place.safeHost)
                                    .font(.system(size: 13))
                                    .foregroundColor(.gray)
                                    .lineLimit(1)
                                    .truncationMode(.tail)
                                    .frame(maxWidth: .infinity, alignment: .leading)
                                    .layoutPriority(1)
                            }
                        }
                    }

                    // Description (max 2 lines with ellipsis)
                    if !place.safeDescription.isEmpty && place.safeDescription != "설명이 없습니다" {
                        Text(LinkHelper.createAttributedString(from: place.safeDescription, baseColor: .secondary))
                            .font(.system(size: 12))
                            .lineLimit(2)
                            .truncationMode(.tail)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .layoutPriority(1)
                            .fixedSize(horizontal: false, vertical: true)
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)

                // Right thumbnail - 고정 크기
                ThumbnailView(imageUrl: place.orderedImageUrls.first)
                    .frame(width: 70, height: 70)
                    .fixedSize()
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 16)
            .background(
                RoundedRectangle(cornerRadius: 16, style: .continuous)
                    .fill(Color.white)
                    .shadow(color: .black.opacity(0.08), radius: 12, x: 0, y: 4)
            )
            .frame(maxWidth: .infinity)
        } else {
            EmptyView()
        }
    }
}

// CustomIconView와 ThumbnailView는 PlaceMiniCard.swift에 이미 정의됨

#Preview {
    PlaceDetailModal(
        place: PlaceDetail(
            placeId: 1,
            imageUrl: nil,
            placeImages: nil,
            category: "BAR",
            title: "사회과학대학 주점",
            description: "사회과학대학에서 운영하는 주점입니다. 맛있는 음식과 시원한 음료를 제공합니다.",
            location: "진관홀&학술정보원 앞",
            host: "사회과학대학",
            startTime: "18:00",
            endTime: "23:59",
            coordinate: nil
        ),
        isLoading: false,
        errorMessage: nil
    ) {
        print("Detail modal dismissed")
    }
    .padding()
    .background(Color.gray.opacity(0.1))
}
