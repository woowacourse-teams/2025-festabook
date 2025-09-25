import Foundation

// MARK: - 디바이스 등록 요청
struct DeviceRegistrationRequest: Codable {
    let deviceIdentifier: String
    let fcmToken: String
}

// MARK: - 디바이스 등록 응답
struct DeviceRegistrationResponse: Codable {
    let deviceId: Int
}

// MARK: - 디바이스 토큰 갱신 요청
struct DeviceUpdateRequest: Codable {
    let fcmToken: String
}

// MARK: - 축제 알림 구독 요청
struct FestivalNotificationRequest: Codable {
    let deviceId: Int
}

// MARK: - 축제 알림 구독 응답
struct FestivalNotificationResponse: Codable {
    let festivalNotificationId: Int
}

struct FestivalNotificationSubscription: Codable {
    let festivalNotificationId: Int
    let universityName: String
    let festivalName: String
}
