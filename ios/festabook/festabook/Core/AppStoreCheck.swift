import UIKit
import Foundation

class AppStoreCheck {

    // MARK: - Constants
    private static let appleID = "6752591661"

    // MARK: - App Info
    static let appVersion = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String
    static let buildNumber = Bundle.main.infoDictionary?["CFBundleVersion"] as? String
    static let appStoreOpenUrlString = "itms-apps://itunes.apple.com/app/apple-store/id\(appleID)"

    // MARK: - Public Methods
    func latestVersion(completion: @escaping (String?) -> Void) {
        let currentVersion = Self.appVersion ?? "알 수 없음"
        print("[AppStoreCheck] 현재 앱 버전: \(currentVersion)")

        // Cache-busting을 위한 타임스탬프 파라미터 추가
        let timestamp = Int(Date().timeIntervalSince1970)
        let urlString = "https://itunes.apple.com/lookup?id=\(Self.appleID)&country=kr&t=\(timestamp)"

        guard let url = URL(string: urlString) else {
            print("[AppStoreCheck] URL 생성 실패")
            completion(nil)
            return
        }

        // URLRequest 생성 및 캐시 무효화 설정
        var request = URLRequest(url: url)
        request.cachePolicy = .reloadIgnoringLocalAndRemoteCacheData
        request.setValue("no-cache", forHTTPHeaderField: "Cache-Control")
        request.setValue("no-cache", forHTTPHeaderField: "Pragma")
        request.timeoutInterval = 30.0

        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("[AppStoreCheck] 네트워크 오류: \(error.localizedDescription)")
                completion(nil)
                return
            }

            guard let data = data else {
                print("[AppStoreCheck] 응답 데이터 없음")
                completion(nil)
                return
            }

            do {
                guard let json = try JSONSerialization.jsonObject(with: data) as? [String: Any],
                      let results = json["results"] as? [[String: Any]],
                      !results.isEmpty,
                      let appStoreVersion = results[0]["version"] as? String else {
                    print("[AppStoreCheck] 앱스토어 버전 정보 파싱 실패")
                    completion(nil)
                    return
                }

                print("[AppStoreCheck] 앱스토어 최신 버전: \(appStoreVersion)")
                completion(appStoreVersion)

            } catch {
                print("[AppStoreCheck] JSON 파싱 오류: \(error.localizedDescription)")
                completion(nil)
            }
        }.resume()
    }

    func openAppStore() {
        guard let url = URL(string: Self.appStoreOpenUrlString),
              UIApplication.shared.canOpenURL(url) else {
            print("[AppStoreCheck] 앱스토어 URL을 열 수 없습니다")
            return
        }

        UIApplication.shared.open(url, options: [:], completionHandler: nil)
    }
}