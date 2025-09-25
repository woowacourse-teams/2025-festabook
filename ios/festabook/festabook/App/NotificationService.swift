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
    @Published private(set) var festivalNotificationIds: [Int: Int] = [:]
    @Published var fcmToken: String? = nil
    @Published var isTokenGenerating = false

    private let userDefaults = UserDefaults.standard
    private let deviceIdKey = "deviceId"
    private let notificationModalShownKey = "notificationModalShown_"
    private let fcmTokenKey = "fcmToken"
    private let syncedFcmTokenKey = "syncedFcmToken"

    private var syncedFcmToken: String? = nil
    private var currentObservedFestivalId: Int?
    private var subscribeTasks: [Int: Task<Int, Error>] = [:]
    private var unsubscribeTasks: [Int: Task<Void, Error>] = [:]
    
    private func decodeFestivalNotificationId(from data: Data?) -> Int? {
        guard let data else { return nil }
        return try? APIClient.jsonDecoder.decode(FestivalNotificationResponse.self, from: data).festivalNotificationId
    }

    private init() {
        loadStoredValues()
        Task { [weak self] in
            await self?.synchronizeSubscriptionsWithServer(focusFestivalId: nil, focusUniversityName: nil)
        }
    }
    
    // MARK: - 저장된 값 로드
    private func loadStoredValues() {
        deviceId = userDefaults.object(forKey: deviceIdKey) as? Int
        festivalNotificationIds.removeAll()
        isNotificationEnabled = false
        fcmToken = userDefaults.string(forKey: fcmTokenKey)
        syncedFcmToken = userDefaults.string(forKey: syncedFcmTokenKey)
        print("[NotificationService] Loaded deviceId: \(deviceId ?? -1), isNotificationEnabled: \(isNotificationEnabled), fcmToken: \(fcmToken?.prefix(20) ?? "nil"), syncedFcmToken: \(syncedFcmToken?.prefix(20) ?? "nil")...")
    }
    
    // MARK: - 디바이스 ID 저장
    private func saveDeviceId(_ id: Int) {
        deviceId = id
        userDefaults.set(id, forKey: deviceIdKey)
    }

    // MARK: - 서버와 동기화된 토큰 저장
    private func saveSyncedFcmToken(_ token: String) {
        syncedFcmToken = token
        userDefaults.set(token, forKey: syncedFcmTokenKey)
    }

    // MARK: - 디바이스 등록 정보 초기화
    private func clearStoredDeviceRegistration() {
        deviceId = nil
        userDefaults.removeObject(forKey: deviceIdKey)
        syncedFcmToken = nil
        userDefaults.removeObject(forKey: syncedFcmTokenKey)
        festivalNotificationIds.removeAll()
        currentObservedFestivalId = nil
        isNotificationEnabled = false
    }
    
    // MARK: - 축제 알림 구독 정보 저장
    private func saveFestivalSubscription(notificationId: Int, festivalId: Int) {
        festivalNotificationIds[festivalId] = notificationId
    }

    private func removeFestivalSubscription(festivalId: Int) {
        festivalNotificationIds.removeValue(forKey: festivalId)
    }

    // MARK: - 토글 상태 동기화
    func updateNotificationEnabled(_ enabled: Bool, for festivalId: Int?) {
        currentObservedFestivalId = festivalId
        isNotificationEnabled = enabled
        print("[NotificationService] 알림 토글 임시 업데이트: \(enabled) for festivalId: \(festivalId ?? -1)")
    }

    func refreshNotificationEnabledState(for festivalId: Int?) {
        currentObservedFestivalId = festivalId
        guard let festivalId else {
            isNotificationEnabled = false
            return
        }
        isNotificationEnabled = festivalNotificationIds[festivalId] != nil
    }

    func synchronizeSubscriptionsWithServer(focusFestivalId: Int?, focusUniversityName: String? = nil) async {
        let currentDeviceId = await MainActor.run { self.deviceId }

        guard let deviceId = currentDeviceId, deviceId > 0 else {
            print("[NotificationService] ⚠️ synchronizeSubscriptionsWithServer - deviceId 없음")
            await MainActor.run {
                self.refreshNotificationEnabledState(for: focusFestivalId)
            }
            return
        }

        let endpoint = Endpoints.Notifications.deviceSubscriptions(deviceId)

        do {
            let subscriptions: [FestivalNotificationSubscription] = try await APIClient.shared.getNotification(endpoint: endpoint)

            await MainActor.run {
                var updated: [Int: Int] = [:]

                if let focusFestivalId,
                   let focusUniversityName,
                   let matched = subscriptions.first(where: { $0.universityName == focusUniversityName }) {
                    updated[focusFestivalId] = matched.festivalNotificationId
                }

                festivalNotificationIds = updated
                refreshNotificationEnabledState(for: focusFestivalId)
                print("[NotificationService] ✅ 서버 구독 정보 동기화 완료 - 총 \(subscriptions.count)건")
            }
        } catch {
            await MainActor.run {
                refreshNotificationEnabledState(for: focusFestivalId)
            }
            print("[NotificationService] ❌ 서버 구독 정보 동기화 실패: \(error)")
        }
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
    func isFestivalSubscribed(festivalId: Int) -> Bool {
        return festivalNotificationIds[festivalId] != nil
    }

    func hasAnyFestivalSubscriptions() -> Bool {
        return !festivalNotificationIds.isEmpty
    }

    func festivalNotificationId(for festivalId: Int) -> Int? {
        return festivalNotificationIds[festivalId]
    }

    // MARK: - 현재 FCM 토큰 반환 (단순)
    func getCurrentFCMToken() -> String? {
        return fcmToken
    }

    // MARK: - 토큰 자동 갱신 시 서버 업데이트
    func updateTokenToServerIfNeeded(_ token: String) async {
        await syncDeviceRegistration(with: token, previousToken: nil)
    }
    
    // MARK: - FCM 토큰 업데이트 (AppDelegate에서 호출)
    func updateFCMToken(_ token: String) {
        DispatchQueue.main.async {
            let previousToken = self.fcmToken

            self.fcmToken = token
            self.userDefaults.set(token, forKey: self.fcmTokenKey)
            print("[NotificationService] ✅ 새 FCM 토큰 저장")

            Task {
                await self.syncDeviceRegistration(with: token, previousToken: previousToken)
            }
        }
    }
    
    // MARK: - 디바이스 등록/갱신 동기화
    private func syncDeviceRegistration(with newToken: String, previousToken: String?) async {
        let trimmedToken = newToken.trimmingCharacters(in: .whitespacesAndNewlines)

        guard !trimmedToken.isEmpty else {
            print("[NotificationService] ⚠️ 빈 FCM 토큰 - 서버 동기화 스킵")
            return
        }

        let storedDeviceId = deviceId
        let storedSyncedToken = syncedFcmToken

        if storedDeviceId == nil || storedDeviceId ?? 0 <= 0 {
            print("[NotificationService] 디바이스 미등록 상태 - 신규 등록 시도")
            do {
                let registeredDeviceId = try await registerDevice(withToken: trimmedToken)
                print("[NotificationService] ✅ 디바이스 신규 등록 완료 - deviceId: \(registeredDeviceId)")
            } catch {
                print("[NotificationService] ❌ 디바이스 신규 등록 실패: \(error)")
            }
            return
        }

        guard let existingDeviceId = storedDeviceId else {
            print("[NotificationService] ⚠️ 저장된 deviceId 조회 실패")
            return
        }

        if storedSyncedToken == trimmedToken {
            print("[NotificationService] 현재 토큰이 서버와 이미 동기화됨 - 업데이트 스킵")
            return
        }

        print("[NotificationService] 저장된 deviceId: \(existingDeviceId) 토큰 업데이트 진행")
        print("[NotificationService] 이전 토큰: \(previousToken?.prefix(20) ?? "nil")...")
        print("[NotificationService] 신규 토큰: \(trimmedToken.prefix(20))...")

        do {
            try await updateDeviceToken(deviceId: existingDeviceId, newToken: trimmedToken)
            await MainActor.run {
                self.saveSyncedFcmToken(trimmedToken)
            }
            print("[NotificationService] ✅ 디바이스 토큰 PATCH 성공")
        } catch HTTPError.server(let statusCode, _) where statusCode == 404 {
            print("[NotificationService] ⚠️ 서버에서 deviceId=\(existingDeviceId) 미존재 - 재등록 시도")
            await MainActor.run {
                self.clearStoredDeviceRegistration()
            }

            do {
                let newDeviceId = try await registerDevice(withToken: trimmedToken)
                print("[NotificationService] ✅ 재등록 성공 - deviceId: \(newDeviceId)")
            } catch {
                print("[NotificationService] ❌ 디바이스 재등록 실패: \(error)")
            }
        } catch {
            print("[NotificationService] ❌ 디바이스 토큰 PATCH 실패: \(error)")
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
            endpoint: Endpoints.Devices.register,
            body: request
        )
        
        await MainActor.run {
            saveDeviceId(response.deviceId)
            saveSyncedFcmToken(tokenToUse)
            print("[NotificationService] 디바이스 등록 성공: \(response.deviceId)")
        }

        return response.deviceId
    }

    // MARK: - 디바이스 토큰 갱신
    private func updateDeviceToken(deviceId: Int, newToken: String) async throws {
        let endpoint = Endpoints.Devices.detail(deviceId)
        let request = DeviceUpdateRequest(fcmToken: newToken)

        try await APIClient.shared.patchDevice(
            endpoint: endpoint,
            body: request
        )
    }
    
    // MARK: - 축제 알림 구독
    func subscribeToFestivalNotifications(festivalId: Int, universityName: String? = nil) async throws -> Int {
        if let existing = festivalNotificationIds[festivalId] {
            print("[NotificationService] ✅ 이미 구독된 축제 - API 호출 스킵")
            return existing
        }

        if let task = subscribeTasks[festivalId] {
            print("[NotificationService] ⏳ 구독 요청 진행 중 - 기존 요청 결과 대기")
            return try await task.value
        }

        guard let deviceId = deviceId else {
            throw NotificationError.deviceNotRegistered
        }

        let task = Task<Int, Error> {
            let request = FestivalNotificationRequest(deviceId: deviceId)
            let endpoint = Endpoints.Notifications.subscribe(festivalId: festivalId)

            do {
                // Use notification-specific POST method (no festival header)
                let response: FestivalNotificationResponse = try await APIClient.shared.postNotification(
                    endpoint: endpoint,
                    body: request
                )

                await MainActor.run {
                    saveFestivalSubscription(notificationId: response.festivalNotificationId, festivalId: festivalId)
                    if currentObservedFestivalId == festivalId {
                        isNotificationEnabled = true
                    }
                    print("[NotificationService] 축제 알림 구독 성공: \(response.festivalNotificationId)")
                }

                return response.festivalNotificationId
            } catch HTTPError.server(let statusCode, let data) where statusCode == 400 {
                if let existingId = decodeFestivalNotificationId(from: data) {
                    await MainActor.run {
                        saveFestivalSubscription(notificationId: existingId, festivalId: festivalId)
                        if currentObservedFestivalId == festivalId {
                            isNotificationEnabled = true
                        }
                        print("[NotificationService] ⚠️ 이미 구독된 축제 - 로컬 상태 동기화 (ID: \(existingId))")
                    }
                    return existingId
                }

                await synchronizeSubscriptionsWithServer(
                    focusFestivalId: festivalId,
                    focusUniversityName: universityName
                )
                if let syncedId = await MainActor.run { festivalNotificationIds[festivalId] } {
                    print("[NotificationService] ⚠️ 서버 구독 상태 복구 - ID: \(syncedId)")
                    return syncedId
                }

                throw NotificationError.notificationAlreadySubscribed
            } catch let error {
                throw error
            }
        }

        subscribeTasks[festivalId] = task

        do {
            let result = try await task.value
            subscribeTasks.removeValue(forKey: festivalId)
            return result
        } catch {
            subscribeTasks.removeValue(forKey: festivalId)
            throw error
        }
    }

    // MARK: - 축제 알림 구독 취소
    func unsubscribeFromFestivalNotifications(festivalId: Int) async throws {
        guard let festivalNotificationId = festivalNotificationIds[festivalId] else {
            throw NotificationError.notificationNotSubscribed
        }

        if let task = unsubscribeTasks[festivalId] {
            print("[NotificationService] ⏳ 구독 취소 진행 중 - 기존 요청 결과 대기")
            return try await task.value
        }

        let task = Task<Void, Error> {
            let endpoint = Endpoints.Notifications.subscription(festivalNotificationId)

            do {
                // Use notification-specific DELETE method (no festival header)
                try await APIClient.shared.deleteNotification(endpoint: endpoint)
            } catch HTTPError.server(let statusCode, _) where statusCode == 404 {
                print("[NotificationService] ⚠️ 서버에 구독 내역 없음 - 로컬 상태만 정리")
            }

            await MainActor.run {
                removeFestivalSubscription(festivalId: festivalId)
                if currentObservedFestivalId == festivalId {
                    isNotificationEnabled = false
                }
                print("[NotificationService] 축제 알림 구독 취소 성공")
            }
        }

        unsubscribeTasks[festivalId] = task

        do {
            try await task.value
            unsubscribeTasks.removeValue(forKey: festivalId)
        } catch {
            unsubscribeTasks.removeValue(forKey: festivalId)
            throw error
        }
    }
    
    // MARK: - 알림 토글 (설정 화면에서 사용)
    func toggleNotification(festivalId: Int) async {
        do {
            if isFestivalSubscribed(festivalId: festivalId) {
                await MainActor.run {
                    updateNotificationEnabled(false, for: festivalId)
                }
                try await unsubscribeFromFestivalNotifications(festivalId: festivalId)
            } else {
                await MainActor.run {
                    updateNotificationEnabled(true, for: festivalId)
                }
                _ = try await subscribeToFestivalNotifications(festivalId: festivalId)
            }
        } catch {
            await MainActor.run {
                print("[NotificationService] 알림 토글 실패: \(error)")
                let currentState = self.isFestivalSubscribed(festivalId: festivalId)
                self.updateNotificationEnabled(currentState, for: festivalId)
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
    case notificationAlreadySubscribed

    var errorDescription: String? {
        switch self {
        case .fcmTokenNotFound:
            return "FCM 토큰을 찾을 수 없습니다"
        case .deviceNotRegistered:
            return "디바이스가 등록되지 않았습니다"
        case .notificationNotSubscribed:
            return "알림이 구독되지 않았습니다"
        case .notificationAlreadySubscribed:
            return "이미 구독된 축제입니다"
        }
    }
}
