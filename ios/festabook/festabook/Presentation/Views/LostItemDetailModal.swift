import SwiftUI

struct LostItemDetailModal: View {
    let item: LostItem
    let onDismiss: () -> Void

    var body: some View {
        ZStack {
            // Dimmed background
            Color.black.opacity(0.55)
                .ignoresSafeArea()
                .onTapGesture { onDismiss() }

            VStack(spacing: 0) {
                // Image with padding (여백) - 전체 요소 크기 축소
                CachedAsyncImage(url: item.imageAbsoluteURLString) { image in
                    image
                        .resizable()
                        .scaledToFit()
                        .padding(.horizontal, 12) // 이미지 좌우 여백 살짝 축소
                        .padding(.top, 16)
                } placeholder: {
                    ZStack {
                        Color.gray.opacity(0.2)
                        Image(systemName: "photo")
                            .font(.system(size: 36))
                            .foregroundColor(.gray)
                    }
                } errorView: {
                    ZStack {
                        Color.gray.opacity(0.2)
                        Image(systemName: "exclamationmark.triangle")
                            .font(.system(size: 28))
                            .foregroundColor(.gray)
                    }
                }
                .frame(maxWidth: .infinity)
                .frame(height: 260)
                .clipped()

                // Info - 이미지와의 거리 축소 및 한 줄 구성
                VStack(alignment: .leading, spacing: 10) {
                    HStack(alignment: .firstTextBaseline, spacing: 6) {
                        Text("보관 장소:")
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(.primary)
                        Text(item.storageLocation)
                            .font(.system(size: 16))
                            .foregroundColor(.primary)
                            .lineLimit(1)
                    }

                    HStack(alignment: .firstTextBaseline, spacing: 6) {
                        Text("보관 일시:")
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(.primary)
                        Text(item.formattedCreatedAt)
                            .font(.system(size: 16))
                            .foregroundColor(.primary)
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.horizontal, 16)
                .padding(.vertical, 16)
            }
            .background(Color.white)
            .cornerRadius(18)
            .shadow(color: Color.black.opacity(0.2), radius: 20, x: 0, y: 10)
            .padding(.horizontal, 40) // 카드 폭을 조금 더 줄여 가로폭 좁게
            .padding(.bottom, 12)
        }
        .accessibilityAddTraits(.isModal)
    }
}


