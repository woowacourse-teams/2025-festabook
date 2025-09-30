import SwiftUI

struct NewsRootView: View {
    private let loadTrigger: UUID?

    init(loadTrigger: UUID? = nil) {
        self.loadTrigger = loadTrigger
    }

    enum Tab: String, CaseIterable {
        case notice = "공지"
        case faq = "FAQ"
        case lost = "분실물"
    }

    @State private var selectedTab: Tab = .notice
    @State private var showAnnouncementDetail = false
    @State private var selectedFestivalId = ""
    @State private var selectedAnnouncementId = ""
    
    // 미리 생성된 ViewModels - 홈 진입시 모두 로딩
    @StateObject private var announcementsViewModel = AnnouncementsViewModel(
        repository: ServiceLocator.shared.announcementsRepository
    )
    @StateObject private var faqViewModel = FAQViewModel(
        repository: ServiceLocator.shared.faqRepository
    )
    @StateObject private var lostItemViewModel = LostItemViewModel()

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                // 상단 제목 - 일정 화면과 동일한 스타일
                HStack {
                    Text("소식")
                        .font(.system(size: 22, weight: .bold))
                        .foregroundColor(.primary)
                    Spacer()
                }
                .padding(.horizontal, 20)
                .padding(.top, 16)
                .padding(.bottom, 20)

                // 커스텀 탭바
                customTabBar

                // 탭 내용
                tabContent
            }
            .navigationDestination(isPresented: $showAnnouncementDetail) {
                AnnouncementDetailView(
                    festivalId: selectedFestivalId,
                    announcementId: selectedAnnouncementId
                )
            }
        }
        .task(id: loadTrigger) {
            guard loadTrigger != nil else { return }
            // 홈 진입시 소식 탭 데이터 모두 미리 로딩
            async let announcementsTask = announcementsViewModel.loadAnnouncements()
            async let faqTask = faqViewModel.loadFAQs()
            async let lostItemTask = lostItemViewModel.loadInitialData()
            
            await (announcementsTask, faqTask, lostItemTask)
            print("[NewsRootView] 모든 소식 데이터 로딩 완료")
        }
        .onReceive(NotificationCenter.default.publisher(for: .showAnnouncementDetail)) { notification in
            if let data = notification.object as? [String: Any],
               let festivalId = data["festivalId"] as? String,
               let announcementId = data["announcementId"] as? String {

                print("[NewsRootView] 📢 공지사항 상세 화면 표시 요청: festivalId=\(festivalId), announcementId=\(announcementId)")

                selectedFestivalId = festivalId
                selectedAnnouncementId = announcementId
                showAnnouncementDetail = true
            }
        }
    }

    private var customTabBar: some View {
        HStack(spacing: 0) {
            ForEach(Tab.allCases, id: \.self) { tab in
                Button(action: {
                    selectedTab = tab
                }) {
                    VStack(spacing: 8) {
                        Text(tab.rawValue)
                            .font(.system(size: 16, weight: .medium))
                            .foregroundColor(selectedTab == tab ? .black : .gray)

                        // 하단 밑줄
                        Rectangle()
                            .fill(selectedTab == tab ? Color.black : Color.clear)
                            .frame(height: 2)
                    }
                }
                .frame(maxWidth: .infinity)
            }
        }
        .padding(.horizontal, 20)
        .padding(.bottom, 1)
    }

    private var tabContent: some View {
        TabView(selection: $selectedTab) {
            AnnouncementsListView(viewModel: announcementsViewModel)
                .tag(Tab.notice)
            
            FAQListView(viewModel: faqViewModel)
                .tag(Tab.faq)
            
            LostFoundListView(viewModel: lostItemViewModel)
                .tag(Tab.lost)
        }
        .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
        .animation(.easeInOut(duration: 0.2), value: selectedTab)
    }
}

struct AnnouncementsListView: View {
    @ObservedObject var viewModel: AnnouncementsViewModel
    @EnvironmentObject private var appState: AppState
    @State private var expandedAnnouncementIds: Set<Int> = []
    @State private var pendingAnnouncementId: Int?

    var body: some View {
        ScrollViewReader { proxy in
            ScrollView {
                LazyVStack(spacing: 12) {
                    if viewModel.isLoading && viewModel.pinnedAnnouncements.isEmpty && viewModel.unpinnedAnnouncements.isEmpty {
                        ProgressView("공지사항을 불러오는 중...")
                            .padding(.vertical, 40)
                    } else if !viewModel.isLoading && viewModel.pinnedAnnouncements.isEmpty && viewModel.unpinnedAnnouncements.isEmpty {
                        if let errorMessage = viewModel.errorMessage {
                            VStack(spacing: 12) {
                                Image(systemName: "exclamationmark.triangle")
                                    .font(.system(size: 24))
                                    .foregroundColor(.gray)
                                Text(errorMessage)
                                    .font(.system(size: 14))
                                    .foregroundColor(.gray)
                                    .multilineTextAlignment(.center)
                            }
                            .padding(.vertical, 40)
                        } else {
                            VStack(spacing: 12) {
                                Image(systemName: "doc.text")
                                    .font(.system(size: 24))
                                    .foregroundColor(.gray)
                                Text("등록된 공지사항이 없습니다")
                                    .font(.system(size: 14))
                                    .foregroundColor(.gray)
                            }
                            .padding(.vertical, 40)
                        }
                    } else {
                        ForEach(viewModel.pinnedAnnouncements) { announcement in
                            AnnouncementCard(
                                announcement: announcement,
                                isExpanded: expandedAnnouncementIds.contains(announcement.announcementId),
                                onToggle: { toggleExpansion(for: announcement.announcementId) }
                            )
                            .id(announcement.announcementId)
                        }

                        ForEach(viewModel.unpinnedAnnouncements) { announcement in
                            AnnouncementCard(
                                announcement: announcement,
                                isExpanded: expandedAnnouncementIds.contains(announcement.announcementId),
                                onToggle: { toggleExpansion(for: announcement.announcementId) }
                            )
                            .id(announcement.announcementId)
                        }
                    }
                }
                .padding(.horizontal, 20)
                .padding(.top, 16)
                .padding(.bottom, 100)
            }
            .refreshable {
                await viewModel.loadAnnouncements()
                if let id = appState.pendingAnnouncementId {
                    pendingAnnouncementId = id
                    attemptExpansionIfNeeded(using: proxy)
                }
            }
            .onAppear {
                if let id = appState.pendingAnnouncementId {
                    pendingAnnouncementId = id
                    attemptExpansionIfNeeded(using: proxy)
                }
            }
            .onChange(of: appState.pendingAnnouncementId) { newValue in
                guard let id = newValue else { return }
                pendingAnnouncementId = id
                attemptExpansionIfNeeded(using: proxy)
            }
            .onChange(of: viewModel.pinnedAnnouncements) { _ in
                pruneInvalidExpandedIds()
                attemptExpansionIfNeeded(using: proxy)
            }
            .onChange(of: viewModel.unpinnedAnnouncements) { _ in
                pruneInvalidExpandedIds()
                attemptExpansionIfNeeded(using: proxy)
            }
            .onChange(of: appState.currentFestivalId) { _ in
                Task {
                    await viewModel.loadAnnouncements()
                    if let id = appState.pendingAnnouncementId {
                        pendingAnnouncementId = id
                        attemptExpansionIfNeeded(using: proxy)
                    }
                    pruneInvalidExpandedIds()
                }
            }
        }
    }

    private func toggleExpansion(for announcementId: Int) {
        if expandedAnnouncementIds.contains(announcementId) {
            expandedAnnouncementIds.remove(announcementId)
        } else {
            expandedAnnouncementIds.insert(announcementId)
        }
    }

    private func attemptExpansionIfNeeded(using proxy: ScrollViewProxy) {
        guard let targetId = pendingAnnouncementId else { return }

        let allAnnouncements = viewModel.pinnedAnnouncements + viewModel.unpinnedAnnouncements
        guard allAnnouncements.contains(where: { $0.announcementId == targetId }) else { return }

        pendingAnnouncementId = nil
        expandedAnnouncementIds.insert(targetId)

        DispatchQueue.main.async {
            withAnimation(.easeInOut) {
                proxy.scrollTo(targetId, anchor: .top)
            }
            appState.pendingAnnouncementId = nil
        }
    }

    private func pruneInvalidExpandedIds() {
        let validIds = Set((viewModel.pinnedAnnouncements + viewModel.unpinnedAnnouncements).map { $0.announcementId })
        expandedAnnouncementIds = expandedAnnouncementIds.intersection(validIds)
    }
}

struct AnnouncementCard: View {
    let announcement: Announcement
    let isExpanded: Bool
    let onToggle: () -> Void

    var body: some View {
        Button(action: {
            withAnimation(.easeInOut(duration: 0.3)) {
                onToggle()
            }
        }) {
            VStack(alignment: .leading, spacing: 0) {
                // 상단: 아이콘 + 제목 + 시간 (한 줄)
                HStack(alignment: .center, spacing: 8) {
                    // 왼쪽 아이콘 (더 작게)
                    Image(systemName: announcement.isPinned ? "pin.fill" : "speaker.wave.2.fill")
                        .font(.system(size: 14))
                        .foregroundColor(announcement.isPinned ? .gray : .black)
                        .frame(width: 16)

                    // 제목
                    Text(announcement.title)
                        .font(.system(size: 16, weight: .medium))
                        .foregroundColor(.black)
                        .multilineTextAlignment(.leading)
                        .frame(maxWidth: .infinity, alignment: .leading)

                    // 시간
                    Text(announcement.displayTime)
                        .font(.system(size: 12))
                        .foregroundColor(.gray)
                }
                .padding(.bottom, isExpanded ? 8 : 0)

                // 하단: 본문 (펼쳐졌을 때만 표시, 아이콘 아래 정렬)
                if isExpanded {
                    HStack(alignment: .top, spacing: 8) {
                        // 아이콘 영역 (투명, 정렬을 위해)
                        Color.clear
                            .frame(width: 16)

                        // 본문
                        Text(LinkHelper.createAttributedString(from: announcement.content))
                            .font(.system(size: 14))
                            .foregroundColor(.black)
                            .multilineTextAlignment(.leading)
                            .lineLimit(nil)
                            .frame(maxWidth: .infinity, alignment: .leading)
                    }
                    .transition(.opacity.combined(with: .scale(scale: 0.95, anchor: .top)))
                }
            }
            .padding(16)
            .background(
                RoundedRectangle(cornerRadius: 12)
                    .fill(backgroundColor)
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(Color.gray.opacity(0.4), lineWidth: 1)
                    )
            )
        }
        .buttonStyle(PlainButtonStyle())
    }

    private var backgroundColor: Color {
        announcement.isPinned ? Color(red: 0.97, green: 0.97, blue: 0.97) : Color.white
    }
}

@MainActor
class AnnouncementsViewModel: ObservableObject {
    @Published var pinnedAnnouncements: [Announcement] = []
    @Published var unpinnedAnnouncements: [Announcement] = []
    @Published var isLoading = false
    @Published var errorMessage: String?

    private let repository: AnnouncementsRepository
    private var loadTask: Task<Void, Never>?

    init(repository: AnnouncementsRepository) {
        self.repository = repository
    }

    func loadAnnouncements() async {
        // 기존 Task 취소
        loadTask?.cancel()

        // 새로운 Task 생성
        loadTask = Task {
            await performLoad()
        }

        await loadTask?.value
    }

    private func performLoad() async {
        print("[AnnouncementsViewModel] 📞 API 호출 시작 - 공지사항 로드")
        isLoading = true
        errorMessage = nil

        // 새로고침 표시를 위해 데이터 초기화
        pinnedAnnouncements = []
        unpinnedAnnouncements = []

        do {
            let response = try await repository.getAnnouncements()

            // Task가 취소되었는지 확인
            if Task.isCancelled {
                print("[AnnouncementsViewModel] Task was cancelled, ignoring result")
                isLoading = false
                return
            }

            pinnedAnnouncements = response.pinned
            unpinnedAnnouncements = response.unpinned
            print("Successfully loaded announcements: \(response.pinned.count) pinned, \(response.unpinned.count) unpinned")

        } catch is CancellationError {
            print("[AnnouncementsViewModel] Task cancelled - this is expected during refresh")
            isLoading = false
            return

        } catch {
            // Task가 취소되었다면 에러 처리하지 않음
            if Task.isCancelled {
                print("[AnnouncementsViewModel] Task cancelled, ignoring error")
                isLoading = false
                return
            }

            // cancelled 에러는 새로고침 과정에서 발생하는 정상 취소이므로 무시
            if let nsError = error as NSError?, nsError.code == -999 {
                print("[AnnouncementsViewModel] Request cancelled (정상 취소): \(error)")
                isLoading = false
                return // 에러 상태로 표시하지 않음
            }

            errorMessage = "공지사항을 불러오는데 실패했습니다."
            print("Error loading announcements: \(error)")
            // 진짜 에러 시에만 빈 배열 표시
            pinnedAnnouncements = []
            unpinnedAnnouncements = []
        }

        isLoading = false
    }
}

struct FAQListView: View {
    @ObservedObject var viewModel: FAQViewModel

    var body: some View {
        ScrollView {
            LazyVStack(spacing: 12) {
                if viewModel.isLoading && viewModel.faqs.isEmpty {
                    // 초기 로딩 상태
                    ProgressView("FAQ를 불러오는 중...")
                        .padding(.vertical, 40)
                } else if !viewModel.isLoading && viewModel.faqs.isEmpty {
                    // 데이터가 없는 상태
                    if let errorMessage = viewModel.errorMessage {
                        VStack(spacing: 12) {
                            Image(systemName: "exclamationmark.triangle")
                                .font(.system(size: 24))
                                .foregroundColor(.gray)
                            Text(errorMessage)
                                .font(.system(size: 14))
                                .foregroundColor(.gray)
                                .multilineTextAlignment(.center)
                        }
                        .padding(.vertical, 40)
                    } else {
                        VStack(spacing: 12) {
                            Image(systemName: "questionmark.circle")
                                .font(.system(size: 24))
                                .foregroundColor(.gray)
                            Text("등록된 FAQ가 없습니다")
                                .font(.system(size: 14))
                                .foregroundColor(.gray)
                        }
                        .padding(.vertical, 40)
                    }
                } else {
                    ForEach(viewModel.faqs) { faq in
                        FAQCard(faq: faq)
                    }
                }
            }
            .padding(.horizontal, 20)
            .padding(.top, 16)
            .padding(.bottom, 100)
        }
        .refreshable {
            await viewModel.loadFAQs()
        }
    }
}

struct FAQCard: View {
    let faq: FAQ
    @State private var isExpanded = false

    var body: some View {
        Button(action: {
            withAnimation(.easeInOut(duration: 0.3)) {
                isExpanded.toggle()
            }
        }) {
            VStack(alignment: .leading, spacing: 0) {
                // 상단: Q. 제목 + 아이콘 (한 줄)
                HStack(alignment: .center, spacing: 12) {
                    // Q. 제목
                    Text("Q. \(faq.question)")
                        .font(.system(size: 15, weight: .medium))
                        .foregroundColor(.black)
                        .multilineTextAlignment(.leading)
                        .frame(maxWidth: .infinity, alignment: .leading)

                    // 오른쪽 아이콘
                    Image(systemName: isExpanded ? "chevron.up" : "chevron.down")
                        .font(.system(size: 14, weight: .medium))
                        .foregroundColor(.gray)
                        .transition(.opacity.combined(with: .scale))
                }
                .padding(.bottom, isExpanded ? 12 : 0)

                // 하단: 본문 (펼쳐졌을 때만 표시)
                if isExpanded {
                    Text(faq.answer)
                        .font(.system(size: 14))
                        .foregroundColor(.black)
                        .multilineTextAlignment(.leading)
                        .lineLimit(nil)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .transition(.opacity.combined(with: .scale(scale: 0.95, anchor: .top)))
                }
            }
            .padding(16)
            .background(
                RoundedRectangle(cornerRadius: 12)
                    .fill(Color(red: 0.97, green: 0.97, blue: 0.97)) // pinned 공지와 동일한 연한 회색
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(Color.gray.opacity(0.4), lineWidth: 1)
                    )
            )
        }
        .buttonStyle(PlainButtonStyle())
    }
}

@MainActor
class FAQViewModel: ObservableObject {
    @Published var faqs: [FAQ] = []
    @Published var isLoading = false
    @Published var errorMessage: String?

    private let repository: FAQRepository
    private var loadTask: Task<Void, Never>?

    init(repository: FAQRepository) {
        self.repository = repository
    }

    func loadFAQs() async {
        // 기존 Task 취소
        loadTask?.cancel()

        // 새로운 Task 생성
        loadTask = Task {
            await performLoad()
        }

        await loadTask?.value
    }

    private func performLoad() async {
        print("[FAQViewModel] 📞 API 호출 시작 - FAQ 로드")
        isLoading = true
        errorMessage = nil

        // 새로고침 표시를 위해 데이터 초기화
        faqs = []

        do {
            let loadedFAQs = try await repository.getFAQs()

            // Task가 취소되었는지 확인
            if Task.isCancelled {
                print("[FAQViewModel] Task was cancelled, ignoring result")
                isLoading = false
                return
            }

            // sequence 순서대로 정렬
            faqs = loadedFAQs.sorted { $0.sequence < $1.sequence }
            print("Successfully loaded FAQs: \(faqs.count) items")

        } catch is CancellationError {
            print("[FAQViewModel] Task cancelled - this is expected during refresh")
            isLoading = false
            return

        } catch {
            // Task가 취소되었다면 에러 처리하지 않음
            if Task.isCancelled {
                print("[FAQViewModel] Task cancelled, ignoring error")
                isLoading = false
                return
            }

            // cancelled 에러는 새로고침 과정에서 발생하는 정상 취소이므로 무시
            if let nsError = error as NSError?, nsError.code == -999 {
                print("[FAQViewModel] Request cancelled (정상 취소): \(error)")
                isLoading = false
                return // 에러 상태로 표시하지 않음
            }

            errorMessage = "FAQ를 불러오는데 실패했습니다."
            print("Error loading FAQs: \(error)")
            // 진짜 에러 시에만 빈 배열 표시
            faqs = []
        }

        isLoading = false
    }
}
