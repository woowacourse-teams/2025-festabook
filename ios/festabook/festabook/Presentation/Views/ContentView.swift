import SwiftUI

struct ContentView: View {
    @EnvironmentObject private var appState: AppState
    
    var body: some View {
        ZStack {
            NavigationStack {
                if appState.selectedUniversity == nil {
                    UniversitySearchView()
                } else {
                    MainTabView()
                }
            }
            
            // 알림 모달 오버레이
            NotificationModalOverlay()
        }
    }
}

// MARK: - 탭 네비게이션 매니저
class TabNavigationManager: ObservableObject {
    @Published var selectedTab: MainTabView.Tab = .home
    @Published var pendingSelection: MainTabView.Tab?
    @Published var shouldAnimatePendingSelection = true
    
    init() {
        // 딥링크 이벤트 구독
        NotificationCenter.default.addObserver(
            forName: .navigateToTab,
            object: nil,
            queue: .main
        ) { [weak self] notification in
            guard let self else { return }

            if let animated = notification.userInfo?["animated"] as? Bool {
                self.shouldAnimatePendingSelection = animated
            } else {
                self.shouldAnimatePendingSelection = true
            }

            if let tabName = notification.object as? String,
               let tab = MainTabView.Tab.fromIdentifier(tabName) {
                print("[TabNavigationManager] 딥링크로 탭 이동: \(tabName)")
                self.pendingSelection = tab
            }
        }
    }
}

struct MainTabView: View {
    @StateObject private var tabManager = TabNavigationManager()
    @EnvironmentObject private var appState: AppState

    // Persistent ViewModels for each tab to prevent re-initialization
    @StateObject private var scheduleViewModel = ScheduleViewModel(repository: ServiceLocator.shared.scheduleRepository)
    @StateObject private var mapViewModel = MapViewModel()
    @State private var loadedTabs: Set<Tab> = [.home]
    @State private var scheduleLoadTrigger: UUID?
    @State private var mapLoadTrigger: UUID?
    @State private var newsLoadTrigger: UUID?
    @State private var newsViewID = UUID()

    enum Tab: String, CaseIterable {
        case home = "홈"
        case schedule = "일정"
        case map = "지도"
        case news = "소식"
        case settings = "설정"

        var icon: String {
            switch self {
            case .home: return "house.fill"
            case .schedule: return "calendar"
            case .map: return "map.fill"
            case .news: return "newspaper.fill"
            case .settings: return "gearshape.fill"
            }
        }

        static func fromIdentifier(_ identifier: String) -> Tab? {
            if let tab = Tab(rawValue: identifier) {
                return tab
            }

            switch identifier.lowercased() {
            case "home": return .home
            case "schedule": return .schedule
            case "map": return .map
            case "news": return .news
            case "settings": return .settings
            default: return nil
            }
        }
    }

    var body: some View {
        ZStack(alignment: .bottom) {
            // Main content - All views exist simultaneously, only visibility changes
            ZStack {
                // Home view
                HomeView()
                    .opacity(tabManager.selectedTab == .home ? 1 : 0)
                    .allowsHitTesting(tabManager.selectedTab == .home)

                // Schedule view with persistent ViewModel
                ScheduleView(viewModel: scheduleViewModel, loadTrigger: scheduleLoadTrigger)
                    .opacity(tabManager.selectedTab == .schedule ? 1 : 0)
                    .allowsHitTesting(tabManager.selectedTab == .schedule)

                // Map view with persistent ViewModel
                MapView(viewModel: mapViewModel, loadTrigger: mapLoadTrigger)
                    .opacity(tabManager.selectedTab == .map ? 1 : 0)
                    .allowsHitTesting(tabManager.selectedTab == .map)

                // News view
                NewsRootView(loadTrigger: newsLoadTrigger)
                    .id(newsViewID)
                    .opacity(tabManager.selectedTab == .news ? 1 : 0)
                    .allowsHitTesting(tabManager.selectedTab == .news)

                // Settings view
                SettingsView()
                    .opacity(tabManager.selectedTab == .settings ? 1 : 0)
                    .allowsHitTesting(tabManager.selectedTab == .settings)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)

            // Custom Tab Bar
            CustomTabBar(
                selectedTab: $tabManager.selectedTab,
                onMapButtonTap: { wasAlreadySelected in
                    if wasAlreadySelected {
                        mapViewModel.resetToInitialState()
                    }
                }
            )
        }
        .ignoresSafeArea(.keyboard, edges: .bottom)
        .onChange(of: tabManager.pendingSelection) { _, newValue in
            guard let tab = newValue else { return }
            tabManager.pendingSelection = nil
            let shouldAnimate = tabManager.shouldAnimatePendingSelection
            tabManager.shouldAnimatePendingSelection = true

            if shouldAnimate {
                withAnimation(.easeInOut) {
                    tabManager.selectedTab = tab
                }
            } else {
                tabManager.selectedTab = tab
            }
        }
        .onChange(of: appState.shouldNavigateToNews) { _, shouldNavigate in
            guard shouldNavigate else { return }
            appState.shouldNavigateToNews = false
            withAnimation(.easeInOut) {
                tabManager.selectedTab = .news
            }
        }
        .onChange(of: tabManager.selectedTab) { _, newTab in
            triggerLoad(for: newTab)
        }
        .onAppear {
            if appState.shouldNavigateToNews {
                appState.shouldNavigateToNews = false
                tabManager.selectedTab = .news
            }
            triggerLoad(for: tabManager.selectedTab, force: true)
        }
        .onChange(of: appState.currentFestivalId) { _, _ in
            handleFestivalChange()
        }
    }
}

private extension MainTabView {
    func triggerLoad(for tab: Tab, force: Bool = false) {
        if !force && loadedTabs.contains(tab) { return }

        switch tab {
        case .home:
            loadedTabs.insert(.home)
        case .schedule:
            scheduleLoadTrigger = UUID()
            loadedTabs.insert(.schedule)
        case .map:
            mapLoadTrigger = UUID()
            loadedTabs.insert(.map)
        case .news:
            newsLoadTrigger = UUID()
            loadedTabs.insert(.news)
        case .settings:
            loadedTabs.insert(.settings)
        }
    }

    func handleFestivalChange() {
        loadedTabs = [.home]
        scheduleLoadTrigger = nil
        mapLoadTrigger = nil
        newsLoadTrigger = nil
        newsViewID = UUID()

        scheduleViewModel.resetForNewFestival()
        mapViewModel.prepareForFestivalChange()

        let currentTab = tabManager.selectedTab
        if currentTab != .home {
            triggerLoad(for: currentTab, force: true)
        }
    }
}

struct CustomTabBar: View {
    @Binding var selectedTab: MainTabView.Tab
    var onMapButtonTap: (_ wasAlreadySelected: Bool) -> Void = { _ in }

    var body: some View {
        VStack(spacing: 0) {
            HStack(spacing: 0) {
                // Home tab
                TabBarItem(
                    tab: .home,
                    selectedTab: $selectedTab,
                    isCenter: false
                )

                // Schedule tab
                TabBarItem(
                    tab: .schedule,
                    selectedTab: $selectedTab,
                    isCenter: false
                )

                // Center Map button (circular and elevated)
                Button(action: {
                    let wasSelected = selectedTab == .map
                    selectedTab = .map
                    onMapButtonTap(wasSelected)
                }) {
                    Image(systemName: "map.fill")
                        .font(.system(size: 26, weight: .medium))
                        .foregroundColor(.white)
                        .frame(width: 64, height: 64)
                        .background(Color.black)
                        .clipShape(Circle())
                }
                .offset(y: -12) // Elevate the center button more

                // News tab
                TabBarItem(
                    tab: .news,
                    selectedTab: $selectedTab,
                    isCenter: false
                )

                // Settings tab
                TabBarItem(
                    tab: .settings,
                    selectedTab: $selectedTab,
                    isCenter: false
                )
            }
            .frame(height: 70)
            .background(
                Color.white
                    .shadow(color: .black.opacity(0.1), radius: 1, x: 0, y: -1)
            )
            .padding(.bottom, 0)
        }
        .background(Color.white)
        .edgesIgnoringSafeArea(.bottom)
    }
}

struct TabBarItem: View {
    let tab: MainTabView.Tab
    @Binding var selectedTab: MainTabView.Tab
    let isCenter: Bool

    var body: some View {
        Button(action: {
            if selectedTab == tab {
                if tab == .map {
                    NotificationCenter.default.post(name: .mapTabReselected, object: nil)
                }
            }
            selectedTab = tab
        }) {
            VStack(spacing: 4) {
                Image(systemName: tab.icon)
                    .font(.system(size: 20, weight: .medium))
                    .foregroundColor(selectedTab == tab ? .black : Color(hex: "C0C0C0"))

                Text(tab.rawValue)
                    .font(.system(size: 12, weight: .medium))
                    .foregroundColor(selectedTab == tab ? .black : Color(hex: "C0C0C0"))
            }
            .frame(maxWidth: .infinity)
            .frame(height: 60)
        }
        .buttonStyle(PlainButtonStyle())
    }
}

// Color extension to support hex colors
extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3: // RGB (12-bit)
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6: // RGB (24-bit)
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: // ARGB (32-bit)
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (1, 1, 1, 0)
        }

        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue:  Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
}

#Preview {
    ContentView()
        .environmentObject(ServiceLocator.shared)
        .environmentObject(AppState())
}
