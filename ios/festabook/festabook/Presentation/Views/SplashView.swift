import SwiftUI

struct SplashView: View {
    @EnvironmentObject private var serviceLocator: ServiceLocator
    @EnvironmentObject private var appState: AppState
    @EnvironmentObject private var notificationService: NotificationService

    @State private var isActive = false
    @State private var logoBrightness: Double = -0.35
    @State private var hasMinimumDisplayTimeElapsed = false

    var body: some View {
        ZStack {
            Color(.systemBackground)
                .ignoresSafeArea()

            if isActive {
                ContentView()
                    .environmentObject(serviceLocator)
                    .environmentObject(appState)
                    .environmentObject(notificationService)
                    .transition(.opacity)
            } else {
                Image("festabook_logo")
                    .resizable()
                    .scaledToFit()
                    .frame(maxWidth: 260)
                    .brightness(logoBrightness)
                    .onAppear(perform: startSplash)
            }
        }
        .animation(.easeInOut(duration: 0.4), value: isActive)
        .onChange(of: appState.isInitialLoadCompleted) { _ in
            proceedIfReady()
        }
    }

    private func startSplash() {
        withAnimation(.easeInOut(duration: 0.6)) {
            logoBrightness = 0.0
        }

        DispatchQueue.main.asyncAfter(deadline: .now() + 0.8) {
            hasMinimumDisplayTimeElapsed = true
            proceedIfReady()
        }
    }

    private func proceedIfReady() {
        guard !isActive, hasMinimumDisplayTimeElapsed, appState.isInitialLoadCompleted else { return }
        withAnimation {
            isActive = true
        }
    }
}
