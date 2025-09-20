import SwiftUI
import Foundation

// MARK: - 공지사항 상세 화면
struct AnnouncementDetailView: View {
    let festivalId: String
    let announcementId: String

    @State private var announcementData: AnnouncementDetail?
    @State private var isLoading = true
    @State private var errorMessage: String?

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    if isLoading {
                        ProgressView("공지사항 로딩 중...")
                            .frame(maxWidth: .infinity, maxHeight: .infinity)
                    } else if let error = errorMessage {
                        Text("오류: \(error)")
                            .foregroundColor(.red)
                            .padding()
                    } else if let announcement = announcementData {
                        VStack(alignment: .leading, spacing: 16) {
                            // 제목
                            Text(announcement.title)
                                .font(.title2)
                                .fontWeight(.bold)
                                .padding(.horizontal)

                            // 작성일
                            Text("작성일: \(announcement.createdAt)")
                                .font(.caption)
                                .foregroundColor(.secondary)
                                .padding(.horizontal)

                            // 내용
                            Text(LinkHelper.createAttributedString(from: announcement.content))
                                .font(.body)
                                .padding(.horizontal)
                                .padding(.bottom)
                        }
                    } else {
                        Text("공지사항을 찾을 수 없습니다")
                            .padding()
                    }
                }
            }
            .navigationTitle("공지사항")
            .navigationBarTitleDisplayMode(.inline)
        }
        .onAppear {
            loadAnnouncementDetail()
        }
    }

    private func loadAnnouncementDetail() {
        print("[AnnouncementDetailView] 공지사항 상세 정보 로드: festivalId=\(festivalId), announcementId=\(announcementId)")

        Task {
            do {
                let endpoint = Endpoints.News.announcementDetail(Int(announcementId) ?? 0)
                let announcement: AnnouncementDetail = try await APIClient.shared.get(endpoint)

                await MainActor.run {
                    self.announcementData = announcement
                    self.isLoading = false
                }
            } catch {
                await MainActor.run {
                    self.errorMessage = error.localizedDescription
                    self.isLoading = false
                }
            }
        }
    }

}

// MARK: - 공지사항 상세 데이터 모델
struct AnnouncementDetail: Decodable {
    let id: String
    let title: String
    let content: String
    let createdAt: String
    let festivalId: String
}

// MARK: - 미리보기
#Preview {
    AnnouncementDetailView(festivalId: "10", announcementId: "42")
}
