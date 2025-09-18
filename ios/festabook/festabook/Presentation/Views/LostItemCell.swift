import SwiftUI

struct LostItemCell: View {
    let item: LostItem
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            ZStack {
                // Card background with border (Android style)
                RoundedRectangle(cornerRadius: 16)
                    .fill(Color.white)
                    .shadow(color: Color.black.opacity(0.06), radius: 6, x: 0, y: 2)
                    .overlay(
                        RoundedRectangle(cornerRadius: 16)
                            .stroke(Color.gray.opacity(0.25), lineWidth: 1)
                    )

                // Image
                CachedAsyncImage(url: item.imageAbsoluteURLString) { image in
                    image
                        .resizable()
                        .aspectRatio(1, contentMode: .fill) // 내부 컨텐츠를 1:1로 채우기
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                        .clipped()
                } placeholder: {
                    ZStack {
                        Color.gray.opacity(0.15)
                        Image(systemName: "photo")
                            .font(.system(size: 28))
                            .foregroundColor(.gray)
                    }
                } errorView: {
                    ZStack {
                        Color.gray.opacity(0.12)
                        Image(systemName: "exclamationmark.triangle")
                            .font(.system(size: 22))
                            .foregroundColor(.gray)
                    }
                }
                .clipShape(RoundedRectangle(cornerRadius: 16))
                .padding(4)
            }
            .aspectRatio(1, contentMode: .fit)
        }
        .buttonStyle(PlainButtonStyle())
        .accessibilityLabel("분실물 이미지, 보관 장소 \(item.storageLocation), 등록일시 \(item.formattedCreatedAt)")
        .accessibilityHint("탭하여 상세 정보 보기")
    }
}

