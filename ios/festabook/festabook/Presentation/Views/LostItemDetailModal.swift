import SwiftUI

struct LostItemDetailModal: View {
    let item: LostItem
    let onDismiss: () -> Void

    private let modalWidth: CGFloat = 280
    private let horizontalPadding: CGFloat = 16
    private var imageSide: CGFloat { modalWidth - horizontalPadding * 2 }

    var body: some View {
        ZStack {
            // Dimmed background
            Color.black.opacity(0.55)
                .ignoresSafeArea()
                .onTapGesture { onDismiss() }

            VStack(spacing: 18) {
                // Image
                CachedAsyncImage(url: item.imageAbsoluteURLString) { image in
                    image
                        .resizable()
                        .scaledToFill()
                        .frame(width: imageSide, height: imageSide)
                        .clipped()
                        .cornerRadius(14)
                } placeholder: {
                    ZStack {
                        Color.gray.opacity(0.2)
                        Image(systemName: "photo")
                            .font(.system(size: 36))
                            .foregroundColor(.gray)
                    }
                    .frame(width: imageSide, height: imageSide)
                    .cornerRadius(14)
                } errorView: {
                    ZStack {
                        Color.gray.opacity(0.2)
                        Image(systemName: "exclamationmark.triangle")
                            .font(.system(size: 28))
                            .foregroundColor(.gray)
                    }
                    .frame(width: imageSide, height: imageSide)
                    .cornerRadius(14)
                }
                .padding(.top, 20)
                .padding(.horizontal, horizontalPadding)

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
                .padding(.horizontal, horizontalPadding)
                .padding(.bottom, 20)
            }
            .background(Color.white)
            .cornerRadius(18)
            .shadow(color: Color.black.opacity(0.2), radius: 20, x: 0, y: 10)
            .frame(width: modalWidth)
            .padding(.bottom, 12)
        }
        .accessibilityAddTraits(.isModal)
    }
}
