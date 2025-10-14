import UIKit
import UserNotifications
#if canImport(FirebaseCore)
import FirebaseCore
import FirebaseMessaging
#endif
#if canImport(NMapsMap)
import NMapsMap
#endif

final class AppDelegate: NSObject, UIApplicationDelegate {
    private let deepLinkService = DeepLinkService.shared
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        print("[AppDelegate] 앱 시작 - 초기화 중...")

        // Naver Maps SDK 초기화
        #if canImport(NMapsMap)
        NMFAuthManager.shared().ncpKeyId = "09h8qpimmp"
        print("[AppDelegate] Naver Maps SDK 초기화 완료 - Client ID: 09h8qpimmp")
        #endif

        // Firebase 초기화 (앱 시작 시 즉시)
        #if canImport(FirebaseCore)
        print("[AppDelegate] 🔥 Firebase 초기화 시작")
        FirebaseApp.configure()
        print("[AppDelegate] ✅ Firebase 초기화 완료")

        // Firebase 초기화 완료 직후 메시징 델리게이트 설정
        Messaging.messaging().delegate = self
        print("[AppDelegate] FCM 메시징 델리게이트 설정 완료")

        // 알림 센터 델리게이트 설정
        UNUserNotificationCenter.current().delegate = self
        print("[AppDelegate] UNUserNotificationCenter 델리게이트 설정 완료")

        // 앱 실행 시 사용자에게 알림 허용 권한을 바로 요청
        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization(
            options: authOptions,
            completionHandler: { granted, error in
                print("[AppDelegate] 🔔 앱 시작 시 알림 권한 요청 결과: \(granted ? "허용" : "거부")")
                if let error = error {
                    print("[AppDelegate] ❌ 알림 권한 요청 오류: \(error)")
                }
            }
        )

        // APNs 등록 (알림 권한과 관계없이 등록)
        application.registerForRemoteNotifications()
        print("[AppDelegate] 📱 APNs 등록 요청 완료")
        #endif

        if #available(iOS 13.0, *) {
            UIApplication.shared.connectedScenes
                .compactMap { $0 as? UIWindowScene }
                .forEach { scene in
                    scene.windows.forEach { window in
                        window.overrideUserInterfaceStyle = .light
                    }
                }
        } else {
            UIApplication.shared.windows.forEach { window in
                window.overrideUserInterfaceStyle = .light
            }
        }

        // 앱 시작 시 업데이트 확인
        scheduleUpdateCheck()

        return true
    }


    // MARK: - APNS 등록 (수동 호출용 - 이제 앱 시작 시 자동 등록됨)
    func registerForAPNS() {
        print("[AppDelegate] 📱 수동 APNS 등록 시작 (자동 등록 완료됨)")
        // 이미 앱 시작 시 자동으로 등록되므로 추가 작업 불필요
    }

    // APNs 등록 성공
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        print("[AppDelegate] ✅ APNs 등록 성공 - 토큰 길이: \(deviceToken.count) bytes")

        #if canImport(FirebaseCore)
        // FCM에 APNs 토큰 설정
        let messaging = Messaging.messaging()
        messaging.apnsToken = deviceToken
        print("[AppDelegate] FCM에 APNs 토큰 설정 완료")

        // 토큰 설정 후 강제로 FCM 토큰 요청 (필요시)
        messaging.token { token, error in
            if let error = error {
                print("[AppDelegate] FCM 토큰 요청 실패: \(error.localizedDescription)")
            } else if let token = token {
                print("[AppDelegate] FCM 토큰 즉시 요청 성공: \(token.prefix(20))...")
            }
        }
        #endif
    }
    
    // APNs 등록 실패
    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("[AppDelegate] APNs 등록 실패: \(error.localizedDescription)")
    }

    // 앱이 foreground로 돌아올 때 업데이트 확인
    func applicationWillEnterForeground(_ application: UIApplication) {
        scheduleUpdateCheck()
    }

    // MARK: - Update Check
    private func scheduleUpdateCheck() {
        // 앱 초기화 완료 후 업데이트 확인
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
            UpdateManager.shared.checkForUpdates()
        }
    }

}

#if canImport(FirebaseCore)
// MARK: - FCM MessagingDelegate
extension AppDelegate: MessagingDelegate {
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        print("[AppDelegate] ✅ FCM 토큰 콜백 수신: \(fcmToken?.prefix(20) ?? "nil")...")

        guard let token = fcmToken, !token.isEmpty else {
            print("[AppDelegate] ❌ FCM 토큰이 비어있음")
            return
        }

        // 더미 토큰 체크
        if token.hasPrefix("dummy_") {
            print("[AppDelegate] ⚠️ 더미 토큰 수신: \(token)")
            return
        }

        print("[NotificationService] ✅ 새 FCM 토큰 수신 - 무조건 서버에 반영")

        // FCM 토큰이 올 때마다 무조건 NotificationService에 전달 (최신 토큰 보장)
        NotificationService.shared.updateFCMToken(token)

        // NotificationCenter로 FCM 토큰 발급 이벤트 전송 ("FCMToken" 이벤트명 사용)
        NotificationCenter.default.post(name: Notification.Name("FCMToken"), object: nil)

        // 서버 동기화는 NotificationService 내부에서 자동 처리됨
    }

    // data-only 메시지 수신 처리 (토픽 기반 메시지 포함)
    func messaging(_ messaging: Messaging, didReceive remoteMessage: [AnyHashable : Any]) {
        print("[AppDelegate] 📩 토픽 기반 FCM 메시지 수신: \(remoteMessage)")

        // data payload 파싱 확인
        if let title = remoteMessage["title"] as? String,
           let body = remoteMessage["body"] as? String {
            let festivalId = remoteMessage["festivalId"] as? String
            let announcementId = remoteMessage["announcementId"] as? String
            print("[AppDelegate] 📋 토픽 메시지 파싱: title=\(title), body=\(body), festivalId=\(festivalId ?? "nil"), announcementId=\(announcementId ?? "nil")")
        }

        handleDataOnlyMessage(remoteMessage)
    }
}

// MARK: - Remote Notifications
extension AppDelegate {
    // Background/Terminated 상태에서 data-only 메시지 수신
    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable : Any], fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        print("[AppDelegate] 📩 Background/Terminated에서 토픽 기반 푸시 메시지 수신: \(userInfo)")

        // data payload 파싱 확인
        if let title = userInfo["title"] as? String,
           let body = userInfo["body"] as? String {
            let festivalId = userInfo["festivalId"] as? String
            let announcementId = userInfo["announcementId"] as? String
            print("[AppDelegate] 📋 Background 토픽 메시지 파싱: title=\(title), body=\(body), festivalId=\(festivalId ?? "nil"), announcementId=\(announcementId ?? "nil")")
        }

        // data-only 메시지 처리
        handleDataOnlyMessage(userInfo)

        completionHandler(.newData)
    }

    // FCM 메시지 처리 (notification + data payload 모두 지원)
    private func handleDataOnlyMessage(_ userInfo: [AnyHashable: Any]) {
        print("[AppDelegate] 🔍 FCM 메시지 처리 시작: \(userInfo)")

        // data payload에서 정보 추출
        let festivalId = userInfo["festivalId"] as? String
        let announcementId = userInfo["announcementId"] as? String

        // FCM v1 API notification + data 구조에서는 data-only 메시지에서만 로컬 알림 생성
        // (notification이 있는 경우 시스템이 자동으로 알림 표시)

        // data payload에서 title, body 추출 시도
        var title = userInfo["title"] as? String
        var body = userInfo["body"] as? String

        // data payload에 title, body가 없으면 data-only 메시지가 아닌 것으로 간주
        guard let dataTitle = title, let dataBody = body else {
            print("[AppDelegate] ℹ️ notification payload 전용 메시지 (data-only 아님)")
            // notification payload는 시스템에서 자동 처리되므로 별도 처리 불필요
            return
        }

        print("[AppDelegate] 📋 data-only FCM 메시지 파싱: title=\(dataTitle), body=\(dataBody), festivalId=\(festivalId ?? "nil"), announcementId=\(announcementId ?? "nil")")

        // 앱 상태 확인
        let appState = UIApplication.shared.applicationState

        switch appState {
        case .active:
            // Foreground 상태: 로컬 알림 생성 (시스템에서 자동 표시)
            print("[AppDelegate] 🔔 Foreground data-only 알림 생성: \(dataTitle) / \(dataBody)")
            createLocalNotification(title: dataTitle, body: dataBody, festivalId: festivalId, announcementId: announcementId)

        case .background, .inactive:
            // Background/Inactive 상태: 로컬 알림 생성
            print("[AppDelegate] 📲 Background data-only 알림 생성: \(dataTitle) / \(dataBody)")
            createLocalNotification(title: dataTitle, body: dataBody, festivalId: festivalId, announcementId: announcementId)

        @unknown default:
            print("[AppDelegate] ❓ 알 수 없는 앱 상태: \(appState)")
            // 기본적으로 로컬 알림 생성
            createLocalNotification(title: dataTitle, body: dataBody, festivalId: festivalId, announcementId: announcementId)
        }
    }
    
    // 로컬 알림 생성 및 표시 (FCM data payload용)
    private func createLocalNotification(title: String, body: String, festivalId: String?, announcementId: String?) {
        print("[AppDelegate] 🔔 로컬 알림 생성 시작: title=\(title), festivalId=\(festivalId ?? "nil"), announcementId=\(announcementId ?? "nil")")

        let content = UNMutableNotificationContent()
        content.title = title
        content.body = body
        content.sound = .default
        content.badge = 1

        // 딥링크용 사용자 정보 추가
        var userInfo: [String: Any] = [
            "title": title,
            "body": body
        ]

        if let festivalId = festivalId {
            userInfo["festivalId"] = festivalId
        }
        if let announcementId = announcementId {
            userInfo["announcementId"] = announcementId
        }

        content.userInfo = userInfo
        print("[AppDelegate] 📋 로컬 알림 userInfo 설정: \(userInfo)")

        // 즉시 트리거 (1초 후)
        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 1, repeats: false)

        // 알림 요청 생성
        let identifier = "festival_announcement_\(announcementId ?? UUID().uuidString)"
        let request = UNNotificationRequest(identifier: identifier, content: content, trigger: trigger)

        // 알림 등록
        UNUserNotificationCenter.current().add(request) { error in
            if let error = error {
                print("[AppDelegate] ❌ 로컬 알림 등록 실패: \(error.localizedDescription)")
            } else {
                print("[AppDelegate] ✅ 로컬 알림 등록 성공: \(identifier)")
            }
        }
    }
}

// MARK: - UNUserNotificationCenterDelegate
extension AppDelegate: UNUserNotificationCenterDelegate {
    // Foreground(앱 켜진 상태)에서도 시스템 알림 표시
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        let userInfo = notification.request.content.userInfo
        print("[AppDelegate] 📱 Foreground에서 시스템 알림 수신: \(userInfo)")

        // notification payload에서 정보 추출 (FCM v1 API)
        let notificationTitle = notification.request.content.title
        let notificationBody = notification.request.content.body

        // data payload에서 정보 추출
        let festivalId = userInfo["festivalId"] as? String
        let announcementId = userInfo["announcementId"] as? String

        print("[AppDelegate] 📋 Foreground 알림 파싱: title=\(notificationTitle), body=\(notificationBody), festivalId=\(festivalId ?? "nil"), announcementId=\(announcementId ?? "nil")")

        // Foreground에서도 시스템 알림 배너, 사운드, 배지 표시
        completionHandler([.list, .banner, .sound, .badge])
    }
    
    // 사용자가 알림을 탭했을 때 처리 (토픽 기반 메시지 포함)
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        let userInfo = response.notification.request.content.userInfo
        print("[AppDelegate] 📱 토픽 기반 알림 탭 처리: \(userInfo)")

        // 토픽 기반 알림 데이터 처리 로직
        handleNotificationTap(userInfo: userInfo)

        completionHandler()
    }
    
    // 알림 탭 처리 로직 (공지사항 상세 화면으로 이동)
    private func handleNotificationTap(userInfo: [AnyHashable: Any]) {
        print("[AppDelegate] 🔗 알림 탭 처리 시작: \(userInfo)")

        // FCM data payload에서 정보 추출
        let festivalId = userInfo["festivalId"] as? String
        let announcementId = userInfo["announcementId"] as? String

        if let festivalId = festivalId, let announcementId = announcementId {
            print("[AppDelegate] 📢 공지사항 딥링크 처리: festivalId=\(festivalId), announcementId=\(announcementId)")

            // 축제 ID 업데이트 (ServiceLocator를 통해)
            if let festivalIdInt = Int(festivalId) {
                DispatchQueue.main.async {
                    ServiceLocator.shared.updateFestivalId(festivalIdInt)
                    print("[AppDelegate] ✅ 축제 ID 업데이트: \(festivalIdInt)")
                }
            }

            // 공지사항 상세 화면으로 이동하는 딥링크 데이터
            let deepLinkData: [String: Any] = [
                "type": "announcement_detail",
                "festivalId": festivalId,
                "announcementId": announcementId
            ]

            // NotificationCenter로 딥링크 이벤트 전송
            NotificationCenter.default.post(name: .notificationTapped, object: deepLinkData)
            print("[AppDelegate] 📤 공지사항 상세 화면 딥링크 이벤트 전송 완료")
        } else {
            print("[AppDelegate] ⚠️ 딥링크 정보 부족 (festivalId=\(festivalId ?? "nil"), announcementId=\(announcementId ?? "nil"))")
            // 일반적인 알림 처리 - 소식 탭으로 이동
            let deepLinkData: [String: Any] = [
                "type": "news"
            ]
            NotificationCenter.default.post(name: .notificationTapped, object: deepLinkData)
        }
    }
}
#endif

// MARK: - Notification Names
extension Notification.Name {
    static let notificationTapped = Notification.Name("notificationTapped")
    static let fcmTokenReceived = Notification.Name("fcmTokenReceived")
    static let navigateToTab = Notification.Name("navigateToTab")
    static let showAnnouncementDetail = Notification.Name("showAnnouncementDetail")
    static let mapTabReselected = Notification.Name("mapTabReselected")
}
