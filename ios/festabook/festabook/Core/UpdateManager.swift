import UIKit
import SwiftUI

class UpdateManager: ObservableObject {

    // MARK: - Singleton
    static let shared = UpdateManager()

    // MARK: - Properties
    private let appStoreCheck = AppStoreCheck()
    @Published var shouldShowUpdateAlert = false
    @Published var latestVersion: String?

    // MARK: - Initialization
    private init() {}

    // MARK: - Public Methods
    func checkForUpdates() {
        appStoreCheck.latestVersion { [weak self] latestVersion in
            DispatchQueue.main.async {
                self?.handleVersionCheck(latestVersion: latestVersion)
            }
        }
    }

    // MARK: - Private Methods
    private func handleVersionCheck(latestVersion: String?) {
        guard let latestVersion = latestVersion else {
            print("[UpdateManager] 앱스토어 버전 확인 실패")
            return
        }

        let currentVersion = AppStoreCheck.appVersion ?? ""

        if shouldUpdate(current: currentVersion, latest: latestVersion) {
            self.latestVersion = latestVersion
            self.shouldShowUpdateAlert = true
            showUpdateAlert(version: latestVersion)
        }
    }

    private func shouldUpdate(current: String, latest: String) -> Bool {
        guard let currentVersion = parseVersion(current),
              let latestVersion = parseVersion(latest) else {
            return false
        }

        if currentVersion.major != latestVersion.major {
            return latestVersion.major > currentVersion.major
        }

        if currentVersion.minor != latestVersion.minor {
            return latestVersion.minor > currentVersion.minor
        }

        return false
    }

    private func parseVersion(_ version: String) -> (major: Int, minor: Int, patch: Int)? {
        let components = version.split(separator: ".").map(String.init)

        guard components.count >= 2,
              let major = Int(components[0]),
              let minor = Int(components[1]) else {
            return nil
        }

        let patchValue: Int
        if components.count >= 3 {
            patchValue = Int(components[2]) ?? 0
        } else {
            patchValue = 0
        }

        return (major, minor, patchValue)
    }

    private func showUpdateAlert(version: String) {
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let window = windowScene.windows.first,
              let rootViewController = window.rootViewController else {
            print("[UpdateManager] 루트 뷰 컨트롤러를 찾을 수 없음")
            return
        }

        let alert = UIAlertController(
            title: "신규 버전 출시 안내",
            message: "새로운 버전이 출시 되었어요!\n지금 바로 업데이트해보세요.",
            preferredStyle: .alert
        )

        let updateAction = UIAlertAction(title: "업데이트", style: .default) { _ in
            self.appStoreCheck.openAppStore()

            // 앱스토어로 이동 후 앱 강제 종료
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                exit(0)
            }
        }

        alert.addAction(updateAction)

        DispatchQueue.main.async {
            rootViewController.present(alert, animated: true, completion: nil)
        }
    }
}
