import SwiftUI

struct PlacePreviewModal: View {
    let place: PlaceDetail?
    let isLoading: Bool
    let errorMessage: String?
    let onTap: () -> Void
    let onDismiss: () -> Void

    var body: some View {
        Button(action: onTap) {
            if isLoading {
                // Loading state
                HStack(spacing: 10) {
                    ProgressView()
                        .scaleEffect(0.8)
                        .fixedSize()

                    Text("정보를 불러오는 중...")
                        .font(.system(size: 15, weight: .medium))
                        .foregroundColor(.secondary)
                        .frame(maxWidth: .infinity, alignment: .leading)
                }
                .padding(.horizontal, 20)
                .padding(.vertical, 16)
                .background(
                    RoundedRectangle(cornerRadius: 12, style: .continuous)
                        .fill(Color.white)
                        .shadow(color: .black.opacity(0.06), radius: 8, x: 0, y: 2)
                )
                .frame(maxWidth: .infinity)
            } else if let errorMessage = errorMessage {
                // Error state
                HStack(spacing: 10) {
                    Image(systemName: "exclamationmark.triangle")
                        .foregroundColor(.orange)
                        .fixedSize()

                    Text(errorMessage)
                        .font(.system(size: 15, weight: .medium))
                        .foregroundColor(.secondary)
                        .frame(maxWidth: .infinity, alignment: .leading)
                }
                .padding(.horizontal, 20)
                .padding(.vertical, 16)
                .background(
                    RoundedRectangle(cornerRadius: 12, style: .continuous)
                        .fill(Color.white)
                        .shadow(color: .black.opacity(0.06), radius: 8, x: 0, y: 2)
                )
                .frame(maxWidth: .infinity)
            } else if let place = place {
                // Loaded state - 뱃지가 위에, 제목이 밑에
                VStack(spacing: 6) {
                    HStack {
                        CategoryBadge(category: place.category)
                        Spacer()
                    }

                    HStack {
                        Text(place.title)
                            .font(.system(size: 15, weight: .medium))
                            .foregroundColor(.primary)
                            .lineLimit(1)
                            .truncationMode(.tail)
                            .frame(maxWidth: .infinity, alignment: .leading)
                        Spacer()
                    }
                }
                .padding(.horizontal, 20)
                .padding(.vertical, 16)
                .background(
                    RoundedRectangle(cornerRadius: 12, style: .continuous)
                        .fill(Color.white)
                        .shadow(color: .black.opacity(0.06), radius: 8, x: 0, y: 2)
                )
                .frame(maxWidth: .infinity)
            } else {
                // Fallback state
                Text("장소 정보 없음")
                    .font(.system(size: 15, weight: .medium))
                    .foregroundColor(.secondary)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.horizontal, 20)
                    .padding(.vertical, 16)
                    .background(
                        RoundedRectangle(cornerRadius: 12, style: .continuous)
                            .fill(Color.white)
                            .shadow(color: .black.opacity(0.06), radius: 8, x: 0, y: 2)
                    )
                    .frame(maxWidth: .infinity)
            }
        }
        .buttonStyle(PlainButtonStyle())
    }
}

#Preview {
    PlacePreviewModal(
        place: PlaceDetail(
            placeId: 1,
            imageUrl: nil,
            category: "BOOTH",
            title: "하리랜드:최강단과대 결정전",
            description: "플레이스 설명이 아직 없습니다.",
            location: "진관홀&학술정보원 앞",
            host: "축제 운영위원회",
            startTime: "10:00",
            endTime: "18:00",
            coordinate: nil
        ),
        isLoading: false,
        errorMessage: nil,
        onTap: {
            print("Preview modal tapped")
        },
        onDismiss: {
            print("Preview modal dismissed")
        }
    )
    .padding()
    .background(Color.gray.opacity(0.1))
}