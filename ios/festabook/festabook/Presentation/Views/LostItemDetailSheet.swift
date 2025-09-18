import SwiftUI

struct LostItemDetailSheet: View {
    let item: LostItem
    @State private var scale: CGFloat = 1.0
    @State private var offset: CGSize = .zero
    @State private var lastScale: CGFloat = 1.0
    @State private var lastOffset: CGSize = .zero

    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                // 이미지 (확대/축소 지원)
                ZoomableImageView(
                    imageUrl: item.imageUrl,
                    scale: $scale,
                    offset: $offset,
                    lastScale: $lastScale,
                    lastOffset: $lastOffset
                )
                .frame(maxHeight: 400)
                .clipShape(RoundedRectangle(cornerRadius: 12))

                // 정보
                VStack(alignment: .leading, spacing: 16) {
                    HStack {
                        Text("보관 장소 :")
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(.primary)
                        Text(item.storageLocation)
                            .font(.system(size: 16))
                            .foregroundColor(.primary)
                        Spacer()
                    }

                    HStack {
                        Text("보관 일시 :")
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(.primary)
                        Text(item.formattedCreatedAt)
                            .font(.system(size: 16))
                            .foregroundColor(.primary)
                        Spacer()
                    }
                }
                .padding(.horizontal, 20)

                Spacer(minLength: 50)
            }
            .padding(.top, 20)
        }
        .onTapGesture(count: 2) {
            // 더블탭으로 줌 토글
            withAnimation(.spring()) {
                if scale > 1.0 {
                    scale = 1.0
                    offset = .zero
                } else {
                    scale = 2.0
                }
            }
        }
    }
}

struct ZoomableImageView: View {
    let imageUrl: String
    @Binding var scale: CGFloat
    @Binding var offset: CGSize
    @Binding var lastScale: CGFloat
    @Binding var lastOffset: CGSize

    var body: some View {
        CachedAsyncImage(url: imageUrl) { image in
            image
                .resizable()
                .aspectRatio(contentMode: .fit)
                .scaleEffect(scale)
                .offset(offset)
                .gesture(
                    SimultaneousGesture(
                        // 핀치 제스처
                        MagnificationGesture()
                            .onChanged { value in
                                let newScale = lastScale * value
                                scale = max(1.0, min(newScale, 5.0))
                            }
                            .onEnded { value in
                                lastScale = scale
                            },

                        // 드래그 제스처
                        DragGesture()
                            .onChanged { value in
                                if scale > 1.0 {
                                    offset = CGSize(
                                        width: lastOffset.width + value.translation.width,
                                        height: lastOffset.height + value.translation.height
                                    )
                                }
                            }
                            .onEnded { value in
                                lastOffset = offset
                            }
                    )
                )
        } placeholder: {
            ZStack {
                Color.gray.opacity(0.3)

                ProgressView()
                    .scaleEffect(1.2)
            }
        } errorView: {
            ZStack {
                Color.gray.opacity(0.2)

                VStack(spacing: 8) {
                    Image(systemName: "exclamationmark.triangle")
                        .font(.title2)
                        .foregroundColor(.gray)
                    Text("이미지를 불러올 수 없습니다")
                        .font(.caption)
                        .foregroundColor(.gray)
                }
            }
        }
    }
}