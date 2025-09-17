import Foundation
import UserNotifications
import UIKit
#if canImport(FirebaseMessaging)
import FirebaseMessaging
#endif
#if canImport(FirebaseCore)
import FirebaseCore
#endif

// MARK: - 알림 서비스
class NotificationService: ObservableObject {
    static let shared = NotificationService()
    
    @Published var isNotificationEnabled = false
    @Published var deviceId: Int? = nil
    @Published var festivalNotificationId: Int? = nil
    @Published var fcmToken: String? = nil
    @Published var isTokenGenerating = false

    private let userDefaults = UserDefaults.standard
    private let deviceIdKey = "deviceId"
    private let festivalNotificationIdKey = "festivalNotificationId"
    private let notificationEnabledKey = "notificationEnabled"
    private let notificationModalShownKey = "notificationModalShown_"
    private let fcmTokenKey = "fcmToken"
    
    private init() {
        loadStoredValues()
    }
    
    // MARK: - 저장된 값 로드
    private func loadStoredValues() {
        deviceId = userDefaults.object(forKey: deviceIdKey) as? Int
        festivalNotificationId = userDefaults.object(forKey: festivalNotificationIdKey) as? Int
        isNotificationEnabled = userDefaults.bool(forKey: notificationEnabledKey)
        fcmToken = userDefaults.string(forKey: fcmTokenKey)
        print("[NotificationService] Loaded deviceId: \(deviceId ?? -1), festivalNotificationId: \(festivalNotificationId ?? -1), isNotificationEnabled: \(isNotificationEnabled), fcmToken: \(fcmToken?.prefix(20) ?? "nil")...")
    }
    
    // MARK: - 디바이스 ID 저장
    private func saveDeviceId(_ id: Int) {
        deviceId = id
        userDefaults.set(id, forKey: deviceIdKey)
    }
    
    // MARK: - 축제 알림 ID 저장
    private func saveFestivalNotificationId(_ id: Int) {
        festivalNotificationId = id
        userDefaults.set(id, forKey: festivalNotificationIdKey)
    }
    
    // MARK: - 알림 설정 저장
    private func saveNotificationEnabled(_ enabled: Bool) {
        isNotificationEnabled = enabled
        userDefaults.set(enabled, forKey: notificationEnabledKey)
    }

    // MARK: - 알림 설정 업데이트 (public access)
    func updateNotificationEnabled(_ enabled: Bool) {
        saveNotificationEnabled(enabled)
        print("[NotificationService] 알림 설정 업데이트: \(enabled)")
    }
    
    // MARK: - 알림 권한 상태 확인
    func getNotificationAuthorizationStatus() async -> UNAuthorizationStatus {
        let settings = await UNUserNotificationCenter.current().notificationSettings()
        return settings.authorizationStatus
    }

    // MARK: - 알림 권한 요청 (권한만 요청, 구독은 따로)
    func requestNotificationPermission() async -> Bool {
        print("[NotificationService] 📋 시스템 알림 권한 요청 시작")

        let currentStatus = await getNotificationAuthorizationStatus()

        switch currentStatus {
        case .notDetermined:
            do {
                let granted = try await UNUserNotificationCenter.current().requestAuthorization(
                    options: [.alert, .badge, .sound]
                )

                print("[NotificationService] ✅ 시스템 알림 권한 요청 결과: \(granted ? "허용" : "거부")")

                if granted {
                    // 권한 허용 시 APNs 등록
                    await MainActor.run {
                        if let appDelegate = UIApplication.shared.delegate as? AppDelegate {
                            appDelegate.registerForAPNS()
                        }
                    }
                }

                return granted
            } catch {
                print("[NotificationService] ❌ 시스템 알림 권한 요청 실패: \(error)")
                return false
            }

        case .denied:
            print("[NotificationService] ⚠️ 알림 권한이 거부된 상태입니다")
            return false

        case .authorized, .provisional, .ephemeral:
            print("[NotificationService] ✅ 알림 권한이 이미 허용된 상태입니다")
            return true

        @unknown default:
            print("[NotificationService] ❓ 알 수 없는 권한 상태: \(currentStatus)")
            return false
        }
    }

    // MARK: - 디바이스 등록 상태 확인
    func isDeviceRegistered() -> Bool {
        return deviceId != nil && deviceId! > 0
    }

    // MARK: - 축제 구독 상태 확인
    func isFestivalSubscribed() -> Bool {
        return festivalNotificationId != nil && festivalNotificationId! > 0
    }

    // MARK: - 현재 FCM 토큰 반환 (단순)
    func getCurrentFCMToken() -> String? {
        return fcmToken
    }

    // MARK: - 토큰 자동 갱신 시 서버 업데이트
    func updateTokenToServerIfNeeded(_ token: String) async {
        // 기존에 디바이스가 등록되어 있고, 토큰이 변경된 경우에만 서버 업데이트
        guard let deviceId = deviceId, deviceId > 0 else {
            print("[NotificationService] 디바이스 미등록 상태 - 서버 업데이트 스킵")
            return
        }

        guard fcmToken != token else {
            print("[NotificationService] 토큰 변경 없음 - 서버 업데이트 스킵")
            return
        }

        print("[NotificationService] 토큰 변경 감지 - 서버에 업데이트 중...")

        // TODO: 디바이스 토큰 업데이트 API 호출 구현 필요
        // do {
        //     let _ = try await updateDeviceToken(deviceId: deviceId, newToken: token)
        //     print("[NotificationService] ✅ 서버 토큰 업데이트 완료")
        // } catch {
        //     print("[NotificationService] ❌ 서버 토큰 업데이트 실패: \(error)")
        // }

        print("[NotificationService] ✅ 서버 토큰 업데이트 완료 (현재 구현 대기중)")
    }
    
    // MARK: - FCM 토큰 업데이트 (AppDelegate에서 호출)
    func updateFCMToken(_ token: String) {
        DispatchQueue.main.async {
            // 이전 토큰을 먼저 저장 (비교용)
            let previousToken = self.fcmToken

            // 새 토큰 저장
            self.fcmToken = token
            self.userDefaults.set(token, forKey: self.fcmTokenKey)
            print("[NotificationService] ✅ 새 FCM 토큰 저장")

            // FCM 토큰 받으면 즉시 디바이스 등록 (deviceId 저장용)
            Task {
                await self.registerDeviceIfNeeded(fcmToken: token, previousToken: previousToken)
            }
        }
    }

    // MARK: - 디바이스 등록 (토큰 변경 시 항상 새로 등록)
    private func registerDeviceIfNeeded(fcmToken: String, previousToken: String?) async {
        // 토큰이 변경되었거나 디바이스가 미등록 상태면 새로 등록
        if !isDeviceRegistered() || previousToken != fcmToken {
            print("[NotificationService] FCM 토큰 변경 감지 또는 미등록 - 디바이스 등록 시작")
            print("[NotificationService] 이전 토큰: \(previousToken?.prefix(20) ?? "nil")...")
            print("[NotificationService] 새 토큰: \(fcmToken.prefix(20))...")

            do {
                let registeredDeviceId = try await registerDevice(withToken: fcmToken)
                print("[NotificationService] ✅ 디바이스 등록 성공 - deviceId: \(registeredDeviceId) 저장됨")
            } catch {
                print("[NotificationService] ❌ 디바이스 등록 실패: \(error)")
            }
        } else {
            print("[NotificationService] 토큰 변경 없음 - 디바이스 등록 스킵")
        }
    }


    // MARK: - 디바이스 등록
    func registerDevice(withToken fcmToken: String? = nil) async throws -> Int {
        let tokenToUse: String

        if let providedToken = fcmToken {
            tokenToUse = providedToken
            print("[NotificationService] 디바이스 등록 - 제공된 토큰 사용: \(tokenToUse.prefix(20))...")
        } else {
            guard let currentToken = getCurrentFCMToken(), !currentToken.isEmpty else {
                throw NotificationError.fcmTokenNotFound
            }
            tokenToUse = currentToken
            print("[NotificationService] 디바이스 등록 - 저장된 토큰 사용: \(tokenToUse.prefix(20))...")
        }

        let deviceIdentifier = await UIDevice.current.identifierForVendor?.uuidString ?? UUID().uuidString

        let request = DeviceRegistrationRequest(
            deviceIdentifier: deviceIdentifier,
            fcmToken: tokenToUse
        )
        
        let response: DeviceRegistrationResponse = try await APIClient.shared.postDevice(
            endpoint: Endpoints.devices,
            body: request
        )
        
        await MainActor.run {
            saveDeviceId(response.deviceId)
            print("[NotificationService] 디바이스 등록 성공: \(response.deviceId)")
        }
        
        return response.deviceId
    }
    
    // MARK: - 축제 알림 구독
    func subscribeToFestivalNotifications(festivalId: Int) async throws -> Int {
        guard let deviceId = deviceId else {
            throw NotificationError.deviceNotRegistered
        }

        let request = FestivalNotificationRequest(deviceId: deviceId)
        let endpoint = "\(Endpoints.festivalNotifications)/\(festivalId)/notifications"

        // Use notification-specific POST method (no festival header)
        let response: FestivalNotificationResponse = try await APIClient.shared.postNotification(
            endpoint: endpoint,
            body: request
        )

        await MainActor.run {
            saveFestivalNotificationId(response.festivalNotificationId)
            saveNotificationEnabled(true)
            print("[NotificationService] 축제 알림 구독 성공: \(response.festivalNotificationId)")
        }

        return response.festivalNotificationId
    }
    
    // MARK: - 축제 알림 구독 취소
    func unsubscribeFromFestivalNotifications() async throws {
        guard let festivalNotificationId = festivalNotificationId else {
            throw NotificationError.notificationNotSubscribed
        }

        let endpoint = "\(Endpoints.festivalNotificationCancel)/\(festivalNotificationId)"

        // Use notification-specific DELETE method (no festival header)
        try await APIClient.shared.deleteNotification(endpoint: endpoint)

        await MainActor.run {
            saveFestivalNotificationId(0) // 0으로 리셋
            saveNotificationEnabled(false)
            print("[NotificationService] 축제 알림 구독 취소 성공")
        }
    }
    
    // MARK: - 알림 토글 (설정 화면에서 사용)
    func toggleNotification(festivalId: Int) async {
        do {
            if isNotificationEnabled {
                try await unsubscribeFromFestivalNotifications()
            } else {
                _ = try await subscribeToFestivalNotifications(festivalId: festivalId)
            }
        } catch {
            await MainActor.run {
                print("[NotificationService] 알림 토글 실패: \(error)")
            }
        }
    }
    
    // MARK: - 학교별 모달 표시 여부 관리
    func shouldShowNotificationModal(for festivalId: Int) -> Bool {
        let key = notificationModalShownKey + "\(festivalId)"
        return !userDefaults.bool(forKey: key)
    }
    
    func markNotificationModalShown(for festivalId: Int) {
        let key = notificationModalShownKey + "\(festivalId)"
        userDefaults.set(true, forKey: key)
        print("[NotificationService] 학교 \(festivalId) 모달 표시 완료로 기록")
    }
}

// MARK: - 알림 에러 타입
enum NotificationError: Error, LocalizedError {
    case fcmTokenNotFound
    case deviceNotRegistered
    case notificationNotSubscribed
    
    var errorDescription: String? {
        switch self {
        case .fcmTokenNotFound:
            return "FCM 토큰을 찾을 수 없습니다"
        case .deviceNotRegistered:
            return "디바이스가 등록되지 않았습니다"
        case .notificationNotSubscribed:
            return "알림이 구독되지 않았습니다"
        }
    }
}
