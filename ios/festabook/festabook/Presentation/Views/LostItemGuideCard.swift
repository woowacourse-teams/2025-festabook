import SwiftUI

struct LostItemGuideCard: View {
    @ObservedObject var viewModel: LostItemGuideViewModel

    var body: some View {
        Button(action: {
            withAnimation(.easeInOut(duration: 0.3)) {
                viewModel.toggleExpansion()
            }
        }) {
            VStack(alignment: .leading, spacing: 0) {
                // 상단: 제목 + 아이콘 (한 줄)
                HStack(alignment: .center, spacing: 12) {
                    // 제목
                    Text("분실물 수령/제보 가이드")
                        .font(.system(size: 15, weight: .medium))
                        .foregroundColor(.black)
                        .multilineTextAlignment(.leading)
                        .frame(maxWidth: .infinity, alignment: .leading)

                    // 오른쪽 아이콘
                    Image(systemName: viewModel.isExpanded ? "chevron.up" : "chevron.down")
                        .font(.system(size: 14, weight: .medium))
                        .foregroundColor(.gray)
                        .transition(.opacity.combined(with: .scale))
                }
                .padding(.bottom, viewModel.isExpanded ? 12 : 0)

                // 하단: 본문 (펼쳐졌을 때만 표시)
                if viewModel.isExpanded {
                    if let guide = viewModel.lostItemGuide {
                        Text(guide.lostItemGuide)
                            .font(.system(size: 14))
                            .foregroundColor(.black)
                            .multilineTextAlignment(.leading)
                            .lineLimit(nil)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .transition(.opacity.combined(with: .scale(scale: 0.95, anchor: .top)))
                    } else if let errorMessage = viewModel.errorMessage {
                        VStack(spacing: 12) {
                            Image(systemName: "exclamationmark.triangle")
                                .font(.system(size: 24))
                                .foregroundColor(.gray)
                            Text(errorMessage)
                                .font(.system(size: 14))
                                .foregroundColor(.gray)
                                .multilineTextAlignment(.center)
                        }
                        .frame(maxWidth: .infinity)
                        .transition(.opacity.combined(with: .scale(scale: 0.95, anchor: .top)))
                    }
                }
            }
            .padding(16)
            .background(
                RoundedRectangle(cornerRadius: 12)
                    .fill(Color(red: 0.97, green: 0.97, blue: 0.97)) // FAQ와 동일한 연한 회색
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(Color.gray.opacity(0.4), lineWidth: 1)
                    )
            )
        }
        .buttonStyle(PlainButtonStyle())
        .overlay {
            if viewModel.isLoading {
                ProgressView()
                    .padding(16)
                    .background(
                        RoundedRectangle(cornerRadius: 12)
                            .fill(Color.white.opacity(0.9))
                    )
            }
        }
    }
}
