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
                    appState.bootstrapFestivalIfNeeded()
                }
                .preferredColorScheme(.light)
        }
    }
}
