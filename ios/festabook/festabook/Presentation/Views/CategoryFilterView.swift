import SwiftUI

struct CategoryFilterView: View {
    @ObservedObject var viewModel: MapViewModel

    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            LazyHStack(spacing: 8) {
                ForEach(MapCategory.allCases, id: \.self) { category in
                    CategoryChip(
                        category: category,
                        isSelected: viewModel.selectedCategories.contains(category)
                    ) {
                        withAnimation(.easeInOut(duration: 0.2)) {
                            viewModel.toggleCategory(category)
                            if category == .all {
                                viewModel.clearAllSelections()
                            }
                        }
                    }
                }
            }
            .padding(.horizontal, 16)
        }
        .background(Color.clear) // 투명 배경
    }
}

struct CategoryChip: View {
    let category: MapCategory
    let isSelected: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 6) {
                // 전체 카테고리는 아이콘 없이, 나머지는 필터 아이콘 표시
                if category.hasIcon {
                    // Category filter icon (PNG 리소스 사용)
                    Image(category.filterIconName)
                        .resizable()
                        .frame(width: 16, height: 16)
                        .foregroundColor(iconColor)
                }

                // Category label (전체 버튼은 텍스트 크기 살짝 증가)
                Text(category.displayName)
                    .font(.system(size: category.hasIcon ? 14 : 15, weight: .medium))
                    .foregroundColor(textColor)
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 8)
            .background(backgroundColor)
            .overlay(
                RoundedRectangle(cornerRadius: 20)
                    .stroke(borderColor, lineWidth: borderWidth)
            )
            .clipShape(RoundedRectangle(cornerRadius: 20))
        }
        .buttonStyle(PlainButtonStyle())
    }

    private var backgroundColor: Color {
        Color.white // 흰색 배경
    }

    private var borderColor: Color {
        isSelected ? Color.black : Color.gray.opacity(0.3)
    }

    private var borderWidth: CGFloat {
        isSelected ? 2 : 1
    }

    private var textColor: Color {
        isSelected ? Color.black : Color.gray
    }

    private var iconColor: Color {
        isSelected ? Color.primary : Color.gray
    }
}

#Preview(traits: .sizeThatFitsLayout) {
    CategoryFilterView(viewModel: MapViewModel())
}