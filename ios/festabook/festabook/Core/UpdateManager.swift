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
        let currentComponents = current.split(separator: ".").compactMap { Int($0) }
        let latestComponents = latest.split(separator: ".").compactMap { Int($0) }

        guard currentComponents.count >= 2 && latestComponents.count >= 2 else {
            return false
        }

        // Major 버전 비교
        if currentComponents[0] < latestComponents[0] {
            return true
        }

        // Major 버전이 같으면 Minor 버전 비교
        if currentComponents[0] == latestComponents[0] && currentComponents[1] < latestComponents[1] {
            return true
        }

        return false
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