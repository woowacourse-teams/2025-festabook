import SwiftUI

struct HomeView: View {
    @EnvironmentObject private var appState: AppState
    @EnvironmentObject private var locator: ServiceLocator
    @StateObject private var notificationService = NotificationService.shared
    @State private var festivalDetail: FestivalDetail?
    @State private var lineups: [Lineup] = []
    @State private var isLoading = true
    @State private var errorMessage: String?
    @State private var showNotificationModal = false
    @State private var pendingFestivalId: Int? // FCM 토큰 대기 중인 축제 ID

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 0) {
                    // 상단 대학교 이름 + 변경 버튼 - 일정/소식 화면과 동일한 스타일
                    HStack(spacing: 8) {
                        Text(festivalDetail?.universityName ?? "페스타북대학교")
                            .font(.system(size: 24, weight: .bold))
                            .foregroundColor(.primary)

                        Button(action: {
                            // 대학교 변경 - 최초 진입점으로 돌아가기
                            appState.selectedUniversity = nil
                            appState.selectedFestival = nil
                        }) {
                            Image(systemName: "chevron.down")
                                .font(.system(size: 14, weight: .medium))
                                .foregroundColor(.black)
                        }

                        Spacer()
                    }
                    .padding(.horizontal, 20)
                    .padding(.top, 20)
                    .padding(.bottom, 20)

                    // 메인 축제 포스터 - 3:4 비율 고정
                    if let festival = festivalDetail, !festival.festivalImages.isEmpty {
                        FestivalPosterCarousel(festival: festival)
                            .padding(.bottom, 15) // 간격 줄임
                    } else if !isLoading {
                        // 포스터가 없을 때 표시 (3:4 비율)
                        GeometryReader { geometry in
                            let posterWidth = geometry.size.width - 60
                            let posterHeight = posterWidth * 4 / 3 // 3:4 비율
                            
                            RoundedRectangle(cornerRadius: 12)
                                .fill(Color.gray.opacity(0.1))
                                .frame(width: posterWidth, height: posterHeight)
                                .overlay(
                                    VStack(spacing: 12) {
                                        Image(systemName: "photo")
                                            .font(.system(size: 40))
                                            .foregroundColor(.gray.opacity(0.5))
                                        Text("포스터가 없습니다")
                                            .font(.system(size: 16, weight: .medium))
                                            .foregroundColor(.gray)
                                    }
                                )
                                .frame(maxWidth: .infinity)
                        }
                        .frame(height: (UIScreen.main.bounds.width - 40) * 4 / 3)
                        .padding(.horizontal, 20)
                        .padding(.bottom, 20)
                    }

                    // 축제 제목과 부제목 - 안드로이드 스타일
                    if let festival = festivalDetail {
                        VStack(spacing: 4) {
                            HStack {
                                Text("2025 \(festival.universityName) 봄축제")
                                    .font(.system(size: 20, weight: .bold))
                                    .foregroundColor(.black)
                                Spacer()
                            }

                            HStack {
                                Text(": 大同團結")
                                    .font(.system(size: 20, weight: .bold))
                                    .foregroundColor(.black)
                                Spacer()
                            }

                            HStack {
                                Text(festival.formattedDateRange)
                                    .font(.system(size: 14))
                                    .foregroundColor(.gray)
                                Spacer()
                            }
                            .padding(.top, 8)
                        }
                        .padding(.horizontal, 20)
                        .padding(.top, 10) // 간격 줄임
                    } else if !isLoading && errorMessage == nil {
                        // 축제 정보가 없을 때 표시
                        VStack(spacing: 4) {
                            HStack {
                                Text("축제 정보가 없습니다")
                                    .font(.system(size: 18, weight: .medium))
                                     .foregroundColor(.gray)
                                Spacer()
                            }
                        }
                        .padding(.horizontal, 20)
                        .padding(.top, 20)
                    }

                    // 구분선
                    if festivalDetail != nil {
                        Divider()
                            .padding(.horizontal, 20)
                            .padding(.top, 16)
                    }

                    // 축제 라인업 섹션 - 원형 프로필 스타일
                    CircularLineupSection(lineups: lineups, isLoading: isLoading)
                        .padding(.top, 16)
                        .padding(.horizontal, 20)

                    Spacer(minLength: 100)
                }
            }
            .navigationBarHidden(true)
            .overlay(alignment: .center) {
                if isLoading {
                    ProgressView("축제 정보를 불러오는 중...")
                        .font(.system(size: 14))
                        .padding()
                        .background(Color(.systemBackground))
                        .cornerRadius(8)
                }
            }
            .task {
                await loadFestivalDetail()
            }
            .onReceive(NotificationCenter.default.publisher(for: Notification.Name("FCMToken"))) { notification in
                print("[HomeView] ✅ FCM 토큰 수신 → 디바이스 등록 시작")

                if let festivalId = pendingFestivalId {
                    Task {
                        await handleFCMTokenReceived(festivalId: festivalId)
                    }
                }
            }
        }
        .overlay {
            // 알림 권한 모달
            if showNotificationModal {
                NotificationPermissionModal(
                    isPresented: $showNotificationModal,
                    onAllow: {
                        showNotificationModal = false
                        if let festival = festivalDetail {
                            Task {
                                await handleNotificationPermission(granted: true, festivalId: festival.festivalId)
                            }
                        }
                    },
                    onLater: {
                        showNotificationModal = false
                        if let festival = festivalDetail {
                            Task {
                                await handleNotificationPermission(granted: false, festivalId: festival.festivalId)
                            }
                        }
                    }
                )
            }
        }
    }
    
    @MainActor
    private func loadFestivalDetail() async {
        isLoading = true
        errorMessage = nil
        
        do {
            print("[HomeView] Loading festival detail for festival ID: \(locator.api.currentFestivalId)")

            // 축제 상세 정보와 라인업을 병렬로 로드
            async let festivalDetailTask = locator.festivalRepo.getFestivalDetail()
            async let lineupsTask = locator.festivalRepo.getLineups()

            festivalDetail = try await festivalDetailTask
            lineups = try await lineupsTask

            // Update university name in AppState for use in other screens
            if let universityName = festivalDetail?.universityName {
                appState.updateUniversityName(universityName)
            }

            print("[HomeView] Successfully loaded festival detail: \(festivalDetail?.universityName ?? "nil")")
            print("[HomeView] Festival images: \(festivalDetail?.festivalImages.map { $0.imageUrl } ?? [])")
            print("[HomeView] Successfully loaded lineups count: \(lineups.count)")
            print("[HomeView] Lineup image URLs: \(lineups.map { $0.imageUrl })")
        } catch {
            print("[HomeView] Error loading festival data: \(error)")
            errorMessage = "축제 정보를 불러올 수 없습니다: \(error.localizedDescription)"
            // 에러 시 데이터 없음으로 설정
            festivalDetail = nil
            lineups = []
            // 에러 시 기본값으로 설정
            appState.updateUniversityName("페스타북대학교")
        }

        isLoading = false

        // 데이터 로드 완료 후 알림 모달 표시 확인
        checkAndShowNotificationModal()
    }
    
    private func changeFestival(_ festivalId: Int) {
        // ServiceLocator의 API 클라이언트 업데이트
        locator.updateFestivalId(festivalId)

        // AppState 업데이트 (최초 진입점으로 돌아가기)
        appState.changeFestival(festivalId)

        // 새로운 축제 정보 로드
        Task {
            await loadFestivalDetail()
        }
    }

    // MARK: - 알림 모달 관련 함수들

    private func checkAndShowNotificationModal() {
        // 대학 홈 화면 진입 시 모달 표시 (축제 정보가 있을 때)
        guard let festivalId = festivalDetail?.festivalId else { return }

        // 해당 학교에 대해 모달을 아직 보여주지 않았다면 표시
        if notificationService.shouldShowNotificationModal(for: festivalId) {
            showNotificationModal = true
            print("[HomeView] 🎪 학교 \(festivalId) 진입 시 알림 모달 표시")
        }
    }

    private func markNotificationModalShown(for festivalId: Int) {
        notificationService.markNotificationModalShown(for: festivalId)
        print("[HomeView] ✅ 학교 \(festivalId) 알림 모달 표시 완료로 기록")
    }

    private func handleNotificationPermission(granted: Bool, festivalId: Int) async {
        print("[HomeView] 🚀 알림 처리 시작 - granted: \(granted)")

        if granted {
            print("[HomeView] ✅ 사용자가 모달에서 알림 허용을 선택했습니다")

            // 1. 시스템 알림 권한 요청 (권한만 요청, 구독은 FCM 토큰 수신 시)
            let permissionGranted = await notificationService.requestNotificationPermission()

            if permissionGranted {
                print("[HomeView] ✅ 시스템 알림 권한 승인됨")

                // 2. FCM 토큰이 이미 있는지 확인
                if let fcmToken = notificationService.getCurrentFCMToken(), !fcmToken.isEmpty {
                    print("[HomeView] ✅ 기존 FCM 토큰 있음 - 즉시 구독 진행")
                    await handleFCMTokenReceived(festivalId: festivalId)
                } else {
                    print("[HomeView] ⏳ FCM 토큰 대기 중 - 토큰 수신 시 자동 구독")
                    pendingFestivalId = festivalId
                }

            } else {
                print("[HomeView] ❌ 시스템 알림 권한이 거부됨 - 안내 메시지 표시")
                await showPermissionDeniedMessage()

                // 시스템 권한 거부 시 토글 상태를 OFF로 설정
                await MainActor.run {
                    notificationService.updateNotificationEnabled(false)
                }
                print("[HomeView] ❌ 시스템 권한 거부로 인해 토글 OFF 설정")
            }

        } else {
            print("[HomeView] ⚠️ 사용자가 모달에서 알림을 거부했습니다")

            // 알림 거부 시 기존 구독이 있다면 취소
            if notificationService.isFestivalSubscribed() {
                do {
                    try await notificationService.unsubscribeFromFestivalNotifications()
                    print("[HomeView] ✅ 모달 거부로 인한 구독 취소 성공")
                } catch {
                    print("[HomeView] ❌ 모달 거부 시 구독 취소 실패: \(error)")
                }
            }

            // 구독 상태와 관계없이 모달 거부 시 항상 토글을 OFF로 설정
            await MainActor.run {
                notificationService.updateNotificationEnabled(false)
            }
            print("[HomeView] ✅ 모달 거부로 인해 토글 OFF 설정")
        }

        // 모달 표시 완료로 기록
        markNotificationModalShown(for: festivalId)
    }

    private func showPermissionDeniedMessage() async {
        // TODO: 시스템 권한이 없을 때 안내 메시지 표시
        print("[HomeView] ℹ️ 시스템 설정에서 알림 권한을 허용해주세요")
    }

    // MARK: - FCM 토큰 수신 시 구독 처리
    private func handleFCMTokenReceived(festivalId: Int) async {
        // 1. 이미 구독된 학교인지 확인
        if notificationService.isFestivalSubscribed() {
            print("[HomeView] ✅ 이미 구독된 상태 - 구독 스킵")
            pendingFestivalId = nil
            return
        }

        // 2. 디바이스 등록 상태 확인 (FCM 토큰 발급시 이미 등록됨)
        if !notificationService.isDeviceRegistered() {
            print("[HomeView] ❌ 디바이스가 아직 등록되지 않음 - 구독 실패")
            pendingFestivalId = nil
            return
        }

        print("[HomeView] ✅ 디바이스 이미 등록됨 - deviceId: \(notificationService.deviceId ?? -1)")

        // 3. 축제 알림 구독
        print("[HomeView] 🎪 축제 알림 구독 시작")
        do {
            let notificationId = try await notificationService.subscribeToFestivalNotifications(festivalId: festivalId)
            print("[APIClient] ✅ 축제 알림 구독 성공")

            // 구독 성공 시 토글 상태 확실히 동기화 (NotificationService에서 이미 처리하지만 명시적으로)
            await MainActor.run {
                notificationService.updateNotificationEnabled(true)
            }
            print("[HomeView] ✅ 구독 성공으로 인해 토글 ON 동기화")
        } catch {
            print("[HomeView] ❌ 축제 알림 구독 실패: \(error)")

            // 구독 실패 시 토글 상태를 OFF로 설정
            await MainActor.run {
                notificationService.updateNotificationEnabled(false)
            }
            print("[HomeView] ❌ 구독 실패로 인해 토글 OFF 설정")
        }

        // 처리 완료 후 대기 상태 초기화
        pendingFestivalId = nil
    }

}

// 축제 포스터 캐러셀 뷰
struct FestivalPosterCarousel: View {
    let festival: FestivalDetail
    @State private var currentIndex = 0

    private var sortedImages: [FestivalImage] {
        festival.festivalImages.sorted { $0.sequence < $1.sequence }
    }

    var body: some View {
        PosterCarousel(
            imageUrls: sortedImages.map { $0.imageUrl.hasPrefix("http") ? $0.imageUrl : "https://festabook.app" + $0.imageUrl },
            festival: festival,
            currentIndex: $currentIndex
        )
        .onAppear {
            currentIndex = 0
        }
    }
}

// MARK: - 포스터 캐러셀 (풀 슬라이드 + 인디케이터)
struct PosterCarousel: View {
    let imageUrls: [String]
    let festival: FestivalDetail
    @Binding var currentIndex: Int

    @State private var internalSelection: Int = 0

    private let posterWidth: CGFloat = UIScreen.main.bounds.width * 0.75
    private var posterHeight: CGFloat { posterWidth * 4 / 3 }
    private var horizontalPadding: CGFloat {
        max((UIScreen.main.bounds.width - posterWidth) / 2, 0)
    }

    var body: some View {
        if imageUrls.isEmpty {
            EmptyView()
        } else {
            VStack(spacing: 16) {
                TabView(selection: $internalSelection) {
                    ForEach(imageUrls.indices, id: \.self) { index in
                        let imageUrl = imageUrls[index]
                        let isActive = internalSelection == index

                        PosterCard(
                            imageUrl: imageUrl,
                            festival: festival,
                            posterWidth: posterWidth,
                            posterHeight: posterHeight
                        )
                        .scaleEffect(isActive ? 1.0 : 0.96)
                        .shadow(color: .black.opacity(isActive ? 0.2 : 0.05), radius: 12, x: 0, y: 8)
                        .animation(.easeInOut(duration: 0.25), value: internalSelection)
                        .tag(index)
                    }
                }
                .frame(height: posterHeight)
                .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
                .padding(.horizontal, horizontalPadding)

                pageIndicator
            }
            .animation(.easeInOut(duration: 0.25), value: internalSelection)
            .onAppear {
                let clamped = clampedIndex(currentIndex)
                internalSelection = clamped
                currentIndex = clamped
                prefetch(urls: neighborUrls(for: clamped))
            }
            .onChange(of: currentIndex) { newValue in
                let clamped = clampedIndex(newValue)
                if internalSelection != clamped {
                    internalSelection = clamped
                }
            }
            .onChange(of: internalSelection) { newValue in
                if currentIndex != newValue {
                    currentIndex = newValue
                }
                prefetch(urls: neighborUrls(for: newValue))
            }
            .onChange(of: imageUrls.count) { _ in
                let clamped = clampedIndex(currentIndex)
                internalSelection = clamped
                currentIndex = clamped
                prefetch(urls: neighborUrls(for: clamped))
            }
        }
    }

    private var pageIndicator: some View {
        HStack(spacing: 6) {
            ForEach(imageUrls.indices, id: \.self) { index in
                Circle()
                    .fill(index == internalSelection ? Color.black : Color.gray.opacity(0.3))
                    .frame(width: index == internalSelection ? 8 : 6, height: index == internalSelection ? 8 : 6)
            }
        }
    }

    private func clampedIndex(_ index: Int) -> Int {
        guard !imageUrls.isEmpty else { return 0 }
        return min(max(index, 0), imageUrls.count - 1)
    }

    private func neighborUrls(for index: Int) -> [String] {
        guard !imageUrls.isEmpty else { return [] }
        var indices: Set<Int> = [clampedIndex(index)]
        if imageUrls.count > 1 {
            indices.insert((index + 1) % imageUrls.count)
            indices.insert((index - 1 + imageUrls.count) % imageUrls.count)
        }
        return indices.sorted().map { imageUrls[$0] }
    }

    private func prefetch(urls: [String]) {
        guard !urls.isEmpty else { return }
        Task(priority: .utility) {
            await ImagePrefetcher.shared.prefetch(urls: urls)
        }
    }
}
// MARK: - 개별 포스터 카드
struct PosterCard: View {
    let imageUrl: String
    let festival: FestivalDetail
    let posterWidth: CGFloat
    let posterHeight: CGFloat
    
    var body: some View {
        CachedAsyncImage(
            url: imageUrl,
            content: { image in
                image
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .frame(width: posterWidth, height: posterHeight)
                    .clipped()
                    .cornerRadius(12)
            },
            placeholder: {
                placeholderView
            },
            errorView: {
                errorView
            }
        )
        .frame(width: posterWidth, height: posterHeight)
    }

    private var placeholderView: some View {
        RoundedRectangle(cornerRadius: 12)
            .fill(Color.gray.opacity(0.2))
            .frame(width: posterWidth, height: posterHeight)
            .overlay(
                VStack(spacing: 8) {
                    ProgressView()
                        .tint(.blue)
                    Text("로딩 중...")
                        .font(.system(size: 12))
                        .foregroundColor(.gray)
                }
            )
    }

    private var errorView: some View {
        RoundedRectangle(cornerRadius: 12)
            .fill(Color.gray.opacity(0.2))
            .frame(width: posterWidth, height: posterHeight)
            .overlay(
                VStack(spacing: 8) {
                    Image(systemName: "photo")
                        .font(.system(size: 32))
                        .foregroundColor(.gray.opacity(0.5))
                    Text("이미지를 불러올 수 없습니다")
                        .font(.system(size: 12))
                        .foregroundColor(.gray)
                        .multilineTextAlignment(.center)
                }
            )
    }
}

// 개별 축제 카드 (실제 S3 이미지 사용)
struct AndroidStyleFestivalCard: View {
    let festival: FestivalDetail
    let image: FestivalImage
    let isCurrent: Bool
    let posterWidth: CGFloat
    let posterHeight: CGFloat

    var body: some View {
        ZStack {
            // 실제 S3 이미지 URL 구성 및 로딩 (API와 같은 도메인 사용)
            let imageURL = image.imageUrl.hasPrefix("http") ? image.imageUrl : "https://festabook.app" + image.imageUrl
            
            AsyncImage(url: URL(string: imageURL)) { phase in
                switch phase {
                case .success(let loadedImage):
                    loadedImage
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                        .frame(width: posterWidth, height: posterHeight)
                        .clipped()
                        .onAppear {
                            print("[AndroidStyleFestivalCard] 이미지 로딩 성공: \(imageURL)")
                        }
                case .failure(let error):
                    // 이미지 로딩 실패 시 빈 상태 표시
                    Rectangle()
                        .fill(Color.gray.opacity(0.1))
                        .frame(width: posterWidth, height: posterHeight)
                        .overlay(
                            VStack(spacing: 8) {
                                Image(systemName: "photo")
                                    .font(.system(size: 32))
                                    .foregroundColor(.gray.opacity(0.5))
                                Text("이미지를 불러올 수 없습니다")
                                    .font(.system(size: 12))
                                    .foregroundColor(.gray)
                                    .multilineTextAlignment(.center)
                            }
                        )
                        .onAppear {
                            print("[AndroidStyleFestivalCard] 이미지 로딩 실패: \(imageURL), 에러: \(error)")
                        }
                case .empty:
                    // 로딩 중
                    Rectangle()
                        .fill(Color.gray.opacity(0.1))
                        .frame(width: posterWidth, height: posterHeight)
                        .overlay(
                            VStack(spacing: 8) {
                                ProgressView()
                                    .tint(.blue)
                                Text("로딩 중...")
                                    .font(.system(size: 12))
                                    .foregroundColor(.gray)
                            }
                        )
                        .onAppear {
                            print("[AndroidStyleFestivalCard] 이미지 로딩 시작: \(imageURL)")
                        }
                @unknown default:
                    Rectangle()
                        .fill(Color.gray.opacity(0.1))
                        .frame(width: posterWidth, height: posterHeight)
                        .overlay(
                            VStack(spacing: 8) {
                                Image(systemName: "exclamationmark.triangle")
                                    .font(.system(size: 32))
                                    .foregroundColor(.gray.opacity(0.5))
                                Text("알 수 없는 오류")
                                    .font(.system(size: 12))
                                    .foregroundColor(.gray)
                            }
                        )
                }
            }

        }
        .cornerRadius(12)
        .shadow(color: .black.opacity(0.1), radius: 4, x: 0, y: 2)
    }
}

// 원형 라인업 섹션 (이미지와 동일한 스타일)
struct CircularLineupSection: View {
    let lineups: [Lineup]
    let isLoading: Bool

    private let displayFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "ko_KR")
        formatter.timeZone = TimeZone(identifier: "Asia/Seoul")
        formatter.dateFormat = "M.d"
        return formatter
    }()

    private let backendFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.timeZone = TimeZone(identifier: "Asia/Seoul")
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
        return formatter
    }()

    private let backendFormatterNoFraction: DateFormatter = {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.timeZone = TimeZone(identifier: "Asia/Seoul")
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
        return formatter
    }()

    // API 응답 형식에 맞는 새로운 formatter (timezone 정보 없음)
    private let apiResponseFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.timeZone = TimeZone(identifier: "Asia/Seoul")
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
        return formatter
    }()

    private let isoFormatter: ISO8601DateFormatter = {
        let formatter = ISO8601DateFormatter()
        formatter.formatOptions = [.withFullDate]
        formatter.timeZone = TimeZone(identifier: "Asia/Seoul")
        return formatter
    }()

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack {
                Text("축제 라인업")
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(.black)

                Spacer()

                Button("일정 확인하기 >") {
                    NotificationCenter.default.post(
                        name: .navigateToTab,
                        object: MainTabView.Tab.schedule.rawValue,
                        userInfo: ["animated": false]
                    )
                }
                .font(.system(size: 14))
                .foregroundColor(.gray)
            }

            if !lineups.isEmpty {
                VStack(alignment: .leading, spacing: 20) {
                    ForEach(groupedLineups) { group in
                        VStack(alignment: .leading, spacing: 20) {
                            VStack(alignment: .leading, spacing: 16) {
                                HStack(spacing: 8) {
                                    Text(group.displayLabel)
                                        .font(.system(size: 16, weight: .bold))
                                        .foregroundColor(.black)

                                    if group.isToday {
                                        Text("오늘")
                                            .font(.system(size: 12, weight: .semibold))
                                            .foregroundColor(.white)
                                            .padding(.horizontal, 8)
                                            .padding(.vertical, 4)
                                            .background(Color.black)
                                            .cornerRadius(12)
                                    }

                                    Spacer()
                                }
                                .overlay(alignment: .bottomLeading) {
                                    RoundedRectangle(cornerRadius: 2)
                                        .fill(Color.gray.opacity(0.7))
                                        .frame(width: min(UIScreen.main.bounds.width * 0.20, 100), height: 3)
                                        .offset(y: 8)
                                }
                            }

                            ScrollView(.horizontal, showsIndicators: false) {
                                LazyHStack(spacing: 12) {
                                    ForEach(group.lineups) { lineup in
                                        CircularArtistProfile(lineup: lineup)
                                    }
                                }
                                .padding(.horizontal, 4)
                            }
                        }
                    }
                }
            } else if isLoading {
                ScrollView(.horizontal, showsIndicators: false) {
                    LazyHStack(spacing: 12) {
                        ForEach(0..<6, id: \.self) { _ in
                            CircularProfilePlaceholder()
                        }
                    }
                    .padding(.horizontal, 4)
                }
            } else {
                VStack(spacing: 8) {
                    Image(systemName: "person.3")
                        .font(.system(size: 28))
                        .foregroundColor(.gray.opacity(0.6))
                    Text("등록된 라인업 정보가 없습니다")
                        .font(.system(size: 14, weight: .medium))
                        .foregroundColor(.gray)
                }
                .frame(maxWidth: .infinity)
                .padding(.vertical, 24)
            }
        }
    }

    private var groupedLineups: [LineupGroup] {
        let calendar = Calendar.current
        var dateGroups: [Date: [Lineup]] = [:]
        var unknownLineups: [Lineup] = []

        for lineup in lineups {
            var parsed: Date?
            
            // 여러 DateFormatter를 순서대로 시도
            let formatters = [apiResponseFormatter, backendFormatter, backendFormatterNoFraction]
            
            for formatter in formatters {
                if let date = formatter.date(from: lineup.performanceAt) {
                    parsed = date
                    break
                }
            }
            
            if let date = parsed {
                let key = calendar.startOfDay(for: date)
                dateGroups[key, default: []].append(lineup)
            } else {
                print("[CircularLineupSection] 날짜 파싱 실패: \(lineup.performanceAt)")
                unknownLineups.append(lineup)
            }
        }

        var results: [LineupGroup] = []
        let sortedDates = dateGroups.keys.sorted()

        for date in sortedDates {
            guard let items = dateGroups[date] else { continue }
            let label = displayFormatter.string(from: date)
            results.append(
                LineupGroup(
                    id: isoIdentifier(for: date),
                    displayLabel: label,
                    isToday: calendar.isDateInToday(date),
                    lineups: items
                )
            )
        }

        if !unknownLineups.isEmpty {
            results.append(
                LineupGroup(
                    id: "unknown",
                    displayLabel: "기타",
                    isToday: false,
                    lineups: unknownLineups
                )
            )
        }

        return results
    }

    private func isoIdentifier(for date: Date) -> String {
        isoFormatter.string(from: date)
    }

    private struct LineupGroup: Identifiable {
        let id: String
        let displayLabel: String
        let isToday: Bool
        let lineups: [Lineup]
    }
}

// 원형 아티스트 프로필 (이미지와 동일한 스타일)
struct CircularArtistProfile: View {
    let lineup: Lineup
    private let profileSize: CGFloat = 64

    var body: some View {
        VStack(spacing: 8) {
            // 원형 프로필 이미지
            let artistImageURL = lineup.imageUrl.isEmpty ? "" : (lineup.imageUrl.hasPrefix("http") ? lineup.imageUrl : "https://festabook.app" + lineup.imageUrl)

            AsyncImage(url: artistImageURL.isEmpty ? nil : URL(string: artistImageURL)) { phase in
                switch phase {
                case .success(let image):
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                        .frame(width: profileSize, height: profileSize)
                        .clipShape(Circle())
                case .failure(_), .empty:
                    Circle()
                        .fill(Color.gray.opacity(0.2))
                        .frame(width: profileSize, height: profileSize)
                        .overlay(
                            Image(systemName: "music.note")
                                .font(.system(size: 20))
                                .foregroundColor(.gray.opacity(0.6))
                        )
                @unknown default:
                    Circle()
                        .fill(Color.gray.opacity(0.2))
                        .frame(width: profileSize, height: profileSize)
                }
            }

            // 아티스트 이름 (한 줄만, 말줄임 처리)
            Text(lineup.name)
                .font(.system(size: 13, weight: .medium))
                .foregroundColor(.black)
                .multilineTextAlignment(.center)
                .lineLimit(1)
                .truncationMode(.tail)
                .frame(width: profileSize + 8)
        }
        .frame(width: profileSize + 8)
    }
}

// 로딩 플레이스홀더 (스켈레톤 뷰)
struct CircularProfilePlaceholder: View {
    private let profileSize: CGFloat = 64
    @State private var isAnimating = false

    var body: some View {
        VStack(spacing: 8) {
            // 원형 플레이스홀더
            Circle()
                .fill(Color.gray.opacity(0.2))
                .frame(width: profileSize, height: profileSize)
                .overlay(
                    Circle()
                        .fill(
                            LinearGradient(
                                colors: [Color.clear, Color.white.opacity(0.6), Color.clear],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                        )
                        .offset(x: isAnimating ? profileSize : -profileSize)
                        .mask(Circle())
                )
                .clipped()

            // 텍스트 플레이스홀더
            RoundedRectangle(cornerRadius: 4)
                .fill(Color.gray.opacity(0.2))
                .frame(width: 40, height: 12)
        }
        .onAppear {
            withAnimation(.linear(duration: 1.5).repeatForever(autoreverses: false)) {
                isAnimating = true
            }
        }
    }
}
