import SwiftUI

// MARK: - 알림 모달 데이터 모델
struct NotificationModalData {
    let title: String
    let body: String
    let festivalId: String?
    let announcementId: String?
}

// MARK: - 커스텀 알림 모달
struct NotificationModal: View {
    let data: NotificationModalData
    let onViewDetails: (String?, String?) -> Void
    let onDismiss: () -> Void
    
    @State private var isPresented = true
    
    var body: some View {
        ZStack {
            // 배경 오버레이
            Color.black.opacity(0.4)
                .ignoresSafeArea()
                .onTapGesture {
                    dismissModal()
                }
            
            // 모달 컨텐츠
            VStack(spacing: 0) {
                // 헤더
                HStack {
                    Image(systemName: "bell.fill")
                        .foregroundColor(.blue)
                        .font(.title2)
                    
                    Text("새로운 공지사항")
                        .font(.headline)
                        .fontWeight(.semibold)
                    
                    Spacer()
                    
                    Button(action: dismissModal) {
                        Image(systemName: "xmark.circle.fill")
                            .foregroundColor(.gray)
                            .font(.title2)
                    }
                }
                .padding(.horizontal, 20)
                .padding(.top, 20)
                
                // 제목
                Text(data.title)
                    .font(.title3)
                    .fontWeight(.bold)
                    .multilineTextAlignment(.leading)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.horizontal, 20)
                    .padding(.top, 16)
                
                // 본문
                Text(data.body)
                    .font(.body)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.leading)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.horizontal, 20)
                    .padding(.top, 8)
                
                // 버튼들
                HStack(spacing: 12) {
                    Button(action: dismissModal) {
                        Text("나중에")
                            .font(.body)
                            .fontWeight(.medium)
                            .foregroundColor(.gray)
                            .frame(maxWidth: .infinity)
                            .frame(height: 44)
                            .background(Color.gray.opacity(0.1))
                            .cornerRadius(8)
                    }
                    
                    Button(action: {
                        onViewDetails(data.festivalId, data.announcementId)
                        dismissModal()
                    }) {
                        Text("바로 보기")
                            .font(.body)
                            .fontWeight(.semibold)
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 44)
                            .background(Color.blue)
                            .cornerRadius(8)
                    }
                }
                .padding(.horizontal, 20)
                .padding(.top, 20)
                .padding(.bottom, 20)
            }
            .background(Color(.systemBackground))
            .cornerRadius(16)
            .shadow(color: .black.opacity(0.1), radius: 10, x: 0, y: 5)
            .padding(.horizontal, 20)
        }
        .opacity(isPresented ? 1 : 0)
        .animation(.easeInOut(duration: 0.3), value: isPresented)
    }
    
    private func dismissModal() {
        withAnimation {
            isPresented = false
        }
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
            onDismiss()
        }
    }
}

// MARK: - 알림 모달 매니저
class NotificationModalManager: ObservableObject {
    @Published var currentModal: NotificationModalData?
    @Published var isPresented = false
    
    func showModal(data: NotificationModalData) {
        print("[NotificationModalManager] 🔔 Foreground 알림 모달 표시: \(data.title) / \(data.body)")
        currentModal = data
        isPresented = true
    }
    
    func hideModal() {
        print("[NotificationModalManager] 알림 모달 숨김")
        isPresented = false
        currentModal = nil
    }
    
    func handleViewDetails(festivalId: String?, announcementId: String?) {
        print("[NotificationModalManager] ➡️ 공지 상세 이동: festivalId=\(festivalId ?? "nil") / announcementId=\(announcementId ?? "nil")")

        // festivalId와 announcementId가 모두 있어야 상세 화면으로 이동할 수 있음
        let deepLinkData: [String: Any]
        if let festivalId, let announcementId {
            deepLinkData = [
                "type": "announcement_detail",
                "festivalId": festivalId,
                "announcementId": announcementId
            ]
        } else {
            deepLinkData = ["type": "news"]
        }

        NotificationCenter.default.post(name: .notificationTapped, object: deepLinkData)
    }
}

// MARK: - 알림 모달 뷰 (전체 화면 오버레이)
struct NotificationModalOverlay: View {
    @StateObject private var modalManager = NotificationModalManager()
    
    var body: some View {
        ZStack {
            if modalManager.isPresented, let modalData = modalManager.currentModal {
                NotificationModal(
                    data: modalData,
                    onViewDetails: modalManager.handleViewDetails,
                    onDismiss: modalManager.hideModal
                )
            }
        }
        .onReceive(NotificationCenter.default.publisher(for: .showNotificationModal)) { notification in
            if let data = notification.object as? NotificationModalData {
                modalManager.showModal(data: data)
            }
        }
    }
}

// MARK: - Notification Names
extension Notification.Name {
    static let showNotificationModal = Notification.Name("showNotificationModal")
}
