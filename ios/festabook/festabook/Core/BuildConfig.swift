// FestabookiOS/Config/BuildConfig.swift
import Foundation

enum BuildConfig {
    static var apiBaseURL: URL {
        let urlString = string(for: "API_BASE_URL")
        guard !urlString.isEmpty else {
            // 폴백 URL 사용
            return URL(string: "https://festabook.app/api")!
        }
        return URL(string: urlString)!
    }
    static var naverMapClientId: String { string(for: "NAVER_MAP_CLIENT_ID") }

    private static func string(for key: String) -> String {
        // 여러 방법으로 시도
        if let value = Bundle.main.object(forInfoDictionaryKey: key) as? String, !value.isEmpty {
            return value
        }
        
        // 프로세스 환경 변수에서도 시도
        if let value = ProcessInfo.processInfo.environment[key], !value.isEmpty {
            return value
        }
        
        return ""
    }
}
