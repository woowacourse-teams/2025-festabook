import SwiftUI

struct SplashView: View {
    @EnvironmentObject private var serviceLocator: ServiceLocator
    @EnvironmentObject private var appState: AppState
    @EnvironmentObject private var notificationService: NotificationService

    @State private var isActive = false
    @State private var logoScale: CGFloat = 0.9
    @State private var logoOpacity: Double = 0.0

    var body: some View {
        ZStack {
            Color(.systemBackground)
                .ignoresSafeArea()

            if isActive {
                ContentView()
                    .environmentObject(serviceLocator)
                    .environmentObject(appState)
                    .environmentObject(notificationService)
                    .transition(.opacity.combined(with: .scale))
            } else {
                Image("festabook_logo")
                    .resizable()
                    .scaledToFit()
                    .frame(maxWidth: 260)
                    .scaleEffect(logoScale)
                    .opacity(logoOpacity)
                    .onAppear(perform: startSplash)
            }
        }
        .animation(.easeInOut(duration: 0.35), value: isActive)
    }

    private func startSplash() {
        withAnimation(.spring(response: 0.8, dampingFraction: 0.7, blendDuration: 0.3)) {
            logoScale = 1.0
            logoOpacity = 1.0
        }

        DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
            withAnimation {
                isActive = true
            }
        }
    }
}
