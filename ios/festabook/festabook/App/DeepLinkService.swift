import Foundation
import SwiftUI

// MARK: - 딥링크 타입
enum DeepLinkType: String, CaseIterable {
    case announcement = "announcement"
    case announcementDetail = "announcement_detail"  // 공지사항 상세 화면
    case festival = "festival"
    case news = "news"
    case schedule = "schedule"
}

// MARK: - 딥링크 데이터
struct DeepLinkData {
    let type: DeepLinkType
    let festivalId: String?
    let announcementId: String?
    let newsId: String?
    let scheduleId: String?
    
    init(type: DeepLinkType, festivalId: String? = nil, announcementId: String? = nil, newsId: String? = nil, scheduleId: String? = nil) {
        self.type = type
        self.festivalId = festivalId
        self.announcementId = announcementId
        self.newsId = newsId
        self.scheduleId = scheduleId
    }
    
    init(from dictionary: [String: Any]) {
        let typeString = dictionary["type"] as? String ?? ""
        self.type = DeepLinkType(rawValue: typeString) ?? .announcement
        self.festivalId = dictionary["festivalId"] as? String
        self.announcementId = dictionary["announcementId"] as? String
        self.newsId = dictionary["newsId"] as? String
        self.scheduleId = dictionary["scheduleId"] as? String
    }
}

// MARK: - 딥링크 서비스
class DeepLinkService: ObservableObject {
    static let shared = DeepLinkService()
    
    @Published var pendingDeepLink: DeepLinkData?
    @Published var isProcessing = false
    
    private init() {
        // 알림 탭 이벤트 구독
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(handleNotificationTapped),
            name: .notificationTapped,
            object: nil
        )
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    // MARK: - 알림 탭 처리
    @objc private func handleNotificationTapped(_ notification: Notification) {
        guard let userInfo = notification.object as? [String: Any] else {
            print("[DeepLinkService] ❌ 알림 데이터 형식 오류")
            return
        }
        
        let deepLinkData = DeepLinkData(from: userInfo)
        print("[DeepLinkService] 🔗 딥링크 처리: type=\(deepLinkData.type), festivalId=\(deepLinkData.festivalId ?? "nil"), announcementId=\(deepLinkData.announcementId ?? "nil")")
        
        processDeepLink(deepLinkData)
    }
    
    // MARK: - 딥링크 처리
    func processDeepLink(_ data: DeepLinkData) {
        guard !isProcessing else {
            print("[DeepLinkService] ⚠️ 딥링크 처리 중 - 대기열에 추가")
            pendingDeepLink = data
            return
        }
        
        isProcessing = true
        print("[DeepLinkService] 🚀 딥링크 처리 시작: \(data.type)")
        
        // 딥링크 타입에 따른 처리
        switch data.type {
        case .announcement:
            handleAnnouncementDeepLink(data)
        case .announcementDetail:
            handleAnnouncementDetailDeepLink(data)  // 공지사항 상세 화면
        case .festival:
            handleFestivalDeepLink(data)
        case .news:
            handleNewsDeepLink(data)
        case .schedule:
            handleScheduleDeepLink(data)
        }
        
        // 처리 완료 후 대기 중인 딥링크가 있으면 처리
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
            self.isProcessing = false
            if let pending = self.pendingDeepLink {
                self.pendingDeepLink = nil
                self.processDeepLink(pending)
            }
        }
    }
    
    // MARK: - 공지사항 딥링크 처리 (기존)
    private func handleAnnouncementDeepLink(_ data: DeepLinkData) {
        print("[DeepLinkService] 📢 공지사항 일반 딥링크 처리")

        guard let festivalId = data.festivalId,
              let announcementId = data.announcementId else {
            print("[DeepLinkService] ❌ 공지사항 딥링크에 필수 데이터 없음")
            return
        }

        // 축제 ID 업데이트
        if let festivalIdInt = Int(festivalId) {
            ServiceLocator.shared.updateFestivalId(festivalIdInt)
            print("[DeepLinkService] ✅ 축제 ID 업데이트: \(festivalIdInt)")
        }

        // 임시로 News 탭으로 이동
        NotificationCenter.default.post(name: .navigateToTab, object: "news")
    }

    // MARK: - 공지사항 상세 화면 딥링크 처리 (FCM 알림용)
    private func handleAnnouncementDetailDeepLink(_ data: DeepLinkData) {
        print("[DeepLinkService] 🎯 공지사항 상세 화면 딥링크 처리")

        guard let festivalId = data.festivalId,
              let announcementId = data.announcementId else {
            print("[DeepLinkService] ❌ 공지사항 상세 딥링크에 필수 데이터 없음")
            // 데이터가 없으면 소식 탭으로 이동
            NotificationCenter.default.post(name: .navigateToTab, object: "news")
            return
        }

        // 축제 ID 업데이트
        if let festivalIdInt = Int(festivalId) {
            ServiceLocator.shared.updateFestivalId(festivalIdInt)
            print("[DeepLinkService] ✅ 축제 ID 업데이트: \(festivalIdInt)")
        }

        print("[DeepLinkService] ➡️ 공지사항 딥링크로 뉴스 탭 이동: festivalId=\(festivalId), announcementId=\(announcementId)")

        // 소식 탭으로 이동하여 목록을 펼칠 수 있도록 함
        NotificationCenter.default.post(name: .navigateToTab, object: "news")
    }
    
    // MARK: - 축제 딥링크 처리
    private func handleFestivalDeepLink(_ data: DeepLinkData) {
        print("[DeepLinkService] 🎪 축제 딥링크 처리")
        
        guard let festivalId = data.festivalId else {
            print("[DeepLinkService] ❌ 축제 딥링크에 festivalId 없음")
            return
        }
        
        // 축제 ID 업데이트
        if let festivalIdInt = Int(festivalId) {
            ServiceLocator.shared.updateFestivalId(festivalIdInt)
            print("[DeepLinkService] ✅ 축제 ID 업데이트: \(festivalIdInt)")
        }
        
        // 홈 화면으로 이동
        NotificationCenter.default.post(name: .navigateToTab, object: "home")
    }
    
    // MARK: - 뉴스 딥링크 처리
    private func handleNewsDeepLink(_ data: DeepLinkData) {
        print("[DeepLinkService] 📰 뉴스 딥링크 처리")
        
        // 뉴스 탭으로 이동
        NotificationCenter.default.post(name: .navigateToTab, object: "news")
    }
    
    // MARK: - 일정 딥링크 처리
    private func handleScheduleDeepLink(_ data: DeepLinkData) {
        print("[DeepLinkService] 📅 일정 딥링크 처리")
        
        // 일정 탭으로 이동
        NotificationCenter.default.post(name: .navigateToTab, object: "schedule")
    }
}
