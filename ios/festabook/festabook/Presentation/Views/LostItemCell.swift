import SwiftUI

struct LostItemCell: View {
    let item: LostItem
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            GeometryReader { proxy in
                let side = proxy.size.width
                ZStack {
                    RoundedRectangle(cornerRadius: 16)
                        .fill(Color.white)
                        .shadow(color: Color.black.opacity(0.06), radius: 6, x: 0, y: 2)
                        .overlay(
                            RoundedRectangle(cornerRadius: 16)
                                .stroke(Color.gray.opacity(0.25), lineWidth: 1)
                        )

                    CachedAsyncImage(url: item.imageAbsoluteURLString) { image in
                        image
                            .resizable()
                            .scaledToFill()
                            .frame(width: side - 8, height: side - 8)
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                    } placeholder: {
                        ZStack {
                            Color.gray.opacity(0.15)
                            Image(systemName: "photo")
                                .font(.system(size: 28))
                                .foregroundColor(.gray)
                        }
                        .frame(width: side - 8, height: side - 8)
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                    } errorView: {
                        ZStack {
                            Color.gray.opacity(0.12)
                            Image(systemName: "exclamationmark.triangle")
                                .font(.system(size: 22))
                                .foregroundColor(.gray)
                        }
                        .frame(width: side - 8, height: side - 8)
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                    }
                }
                .frame(width: proxy.size.width, height: proxy.size.height)
            }
            .aspectRatio(1, contentMode: .fit)
        }
        .buttonStyle(PlainButtonStyle())
        .accessibilityLabel("분실물 이미지, 보관 장소 \(item.storageLocation), 등록일시 \(item.formattedCreatedAt)")
        .accessibilityHint("탭하여 상세 정보 보기")
    }
}
