import SwiftUI

@main
struct festabookApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    @StateObject private var appState = AppState()

    var body: some Scene {
        WindowGroup {
            SplashView()
                .environmentObject(ServiceLocator.shared)
                .environmentObject(appState)
                .environmentObject(NotificationService.shared)
                .onAppear {
                    APIClient.shared.bootstrapFestivalIdFromStorage()
                    let stored = UserDefaults.standard.integer(forKey: "currentFestivalId")
                    if stored > 0 { appState.currentFestivalId = stored }
                    else { UserDefaults.standard.set(appState.currentFestivalId, forKey: "currentFestivalId") }
                    APIClient.shared.updateFestivalId(appState.currentFestivalId)
                }
                .preferredColorScheme(.light)
        }
    }
}
