// FestabookiOS/Config/BuildConfig.swift
import Foundation

enum BuildConfig {
    /// 기본 베이스 URL
    private static let defaultBaseURL = "https://festabook.app"
    
    static var apiBaseURL: URL {
        let urlString = string(for: "API_BASE_URL")
        guard !urlString.isEmpty else {
            // 폴백 URL 사용
            return URL(string: defaultBaseURL + "/api")!
        }
        return URL(string: urlString)!
    }

    /// 베이스 URL (이미지 경로 등에 사용)
    static var baseURL: String {
        return defaultBaseURL
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

enum ImageURLResolver {
    private static var baseURL: URL {
        return URL(string: BuildConfig.baseURL + "/")!
    }

    static func resolve(_ path: String?) -> String? {
        guard let path = path, !path.isEmpty else { return nil }

        let lowercased = path.lowercased()
        if lowercased.hasPrefix("http://") || lowercased.hasPrefix("https://") {
            return path
        }

        let trimmed = path.hasPrefix("/") ? String(path.dropFirst()) : path
        return baseURL.appendingPathComponent(trimmed).absoluteString
    }
}
