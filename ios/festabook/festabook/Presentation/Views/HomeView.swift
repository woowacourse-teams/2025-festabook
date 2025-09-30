import SwiftUI
import UIKit

private enum PosterCarouselConstants {
    static let cardWidthRatio: CGFloat = 0.78
}

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
    @State private var posterImageViewerState: PosterImageViewerState?
    @State private var lastLoadedFestivalId: Int?

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 0) {
                    // 상단 대학교 이름 + 변경 버튼 - 일정/소식 화면과 동일한 스타일
                    HStack(spacing: 8) {
                        if let universityName = festivalDetail?.universityName {
                            Text(universityName)
                                .font(.system(size: 24, weight: .bold))
                                .foregroundColor(.primary)
                                .contentShape(Rectangle())
                                .onTapGesture {
                                    appState.resetFestivalSelection()
                                }
                        } else if isLoading {
                            UniversityNamePlaceholder()
                        } else {
                            Text(appState.selectedFestival?.universityName ?? 
                                 appState.selectedUniversity?.name ?? 
                                 "페스타북대학교")
                                .font(.system(size: 24, weight: .bold))
                                .foregroundColor(.primary)
                                .contentShape(Rectangle())
                                .onTapGesture {
                                    appState.resetFestivalSelection()
                                }
                        }

                        Button(action: {
                            // 대학교 변경 - 최초 진입점으로 돌아가기
                            appState.resetFestivalSelection()
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
                        FestivalPosterCarousel(festival: festival) { index, imageUrls in
                            posterImageViewerState = PosterImageViewerState(
                                imageUrls: imageUrls,
                                initialIndex: index,
                                isPagingEnabled: false
                            )
                        }
                            .padding(.bottom, 15) // 간격 줄임
                    } else if isLoading {
                        // 로딩 중 포스터 스켈레톤
                        FestivalPosterPlaceholder()
                            .padding(.bottom, 15)
                    } else {
                        // 포스터가 없을 때 표시 (3:4 비율)
                        let posterWidth = UIScreen.main.bounds.width * PosterCarouselConstants.cardWidthRatio
                        let posterHeight = posterWidth * 4 / 3 // 3:4 비율
                        
                        ZStack(alignment: .center) {
                            RoundedRectangle(cornerRadius: 12)
                                .fill(Color.gray.opacity(0.1))
                                .frame(width: posterWidth, height: posterHeight)
                                .overlay(
                                    VStack(spacing: 12) {
                                        Image(systemName: "photo")
                                            .font(.system(size: 40))
                                            .foregroundColor(.gray.opacity(0.5))
                                        Text("등록된 포스터 정보가 없습니다")
                                            .font(.system(size: 16, weight: .medium))
                                            .foregroundColor(.gray)
                                    }
                                )
                        }
                        .frame(width: UIScreen.main.bounds.width, height: posterHeight)
                        .clipped()
                        .padding(.bottom, 15) // 스켈레톤과 동일한 패딩 적용
                    }

                    // 축제 제목과 부제목 - 안드로이드 스타일
                    if let festival = festivalDetail {
                        VStack(spacing: 4) {
                            HStack {
                                Text(festival.festivalName)
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
                    } else if isLoading {
                        // 로딩 중 축제 제목/날짜 스켈레톤
                        FestivalTitlePlaceholder()
                    } else if errorMessage == nil {
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
                    if festivalDetail != nil || isLoading {
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
            .task(id: appState.currentFestivalId) {
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
        .fullScreenCover(item: $posterImageViewerState) { state in
            PosterImageViewer(state: state) {
                posterImageViewerState = nil
            }
        }
    }

    @MainActor
    private func loadFestivalDetail() async {
        isLoading = true
        errorMessage = nil
        let currentFestivalId = appState.currentFestivalId
        if lastLoadedFestivalId != currentFestivalId {
            festivalDetail = nil
            lineups = []
        }
        
        do {
            print("[HomeView] Loading festival detail for festival ID: \(locator.api.currentFestivalId)")

            // 축제 상세 정보와 라인업을 병렬로 로드
            async let festivalDetailTask = locator.festivalRepo.getFestivalDetail()
            async let lineupsTask = locator.festivalRepo.getLineups()

            festivalDetail = try await festivalDetailTask
            lineups = try await lineupsTask

            if let festival = festivalDetail {
                await notificationService.synchronizeSubscriptionsWithServer(
                    focusFestivalId: festival.festivalId,
                    focusUniversityName: festival.universityName
                )
            } else {
                await notificationService.synchronizeSubscriptionsWithServer(focusFestivalId: nil, focusUniversityName: nil)
            }

            // Update university name in AppState for use in other screens
            if let universityName = festivalDetail?.universityName {
                appState.updateUniversityName(universityName)
            }

            print("[HomeView] Successfully loaded festival detail: \(festivalDetail?.universityName ?? "nil")")
            print("[HomeView] Festival images: \(festivalDetail?.festivalImages.map { $0.imageUrl } ?? [])")
            print("[HomeView] Successfully loaded lineups count: \(lineups.count)")
            print("[HomeView] Lineup image URLs: \(lineups.map { $0.imageUrl })")
            lastLoadedFestivalId = festivalDetail?.festivalId
        } catch {
            print("[HomeView] Error loading festival data: \(error)")
            errorMessage = "축제 정보를 불러올 수 없습니다: \(error.localizedDescription)"
            // 에러 시 데이터 없음으로 설정
            festivalDetail = nil
            lineups = []
            // 에러 시 기본값으로 설정
            appState.updateUniversityName("페스타북대학교")
            await notificationService.synchronizeSubscriptionsWithServer(focusFestivalId: nil, focusUniversityName: nil)
            lastLoadedFestivalId = nil
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

            await MainActor.run {
                notificationService.updateNotificationEnabled(true, for: festivalId)
            }

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

                    Task { [festivalId] in
                        try? await Task.sleep(nanoseconds: 5_000_000_000)
                        await MainActor.run {
                            if pendingFestivalId == festivalId && !notificationService.isFestivalSubscribed(festivalId: festivalId) {
                                notificationService.updateNotificationEnabled(false, for: festivalId)
                                pendingFestivalId = nil
                                print("[HomeView] ⏱️ FCM 토큰 대기 타임아웃 - 토글 OFF 재설정")
                            }
                        }
                    }
                }

            } else {
                print("[HomeView] ❌ 시스템 알림 권한이 거부됨 - 안내 메시지 표시")
                await showPermissionDeniedMessage()
                pendingFestivalId = nil

                // 시스템 권한 거부 시 토글 상태를 OFF로 설정
                await MainActor.run {
                    notificationService.updateNotificationEnabled(false, for: festivalId)
                }
                print("[HomeView] ❌ 시스템 권한 거부로 인해 토글 OFF 설정")
            }

        } else {
            print("[HomeView] ⚠️ 사용자가 모달에서 알림을 거부했습니다")
            pendingFestivalId = nil
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
        if notificationService.isFestivalSubscribed(festivalId: festivalId) {
            print("[HomeView] ✅ 이미 해당 축제에 구독된 상태 - 구독 스킵")
            await MainActor.run {
                notificationService.updateNotificationEnabled(true, for: festivalId)
            }
            pendingFestivalId = nil
            return
        }

        // 2. 디바이스 등록 상태 확인 (FCM 토큰 발급시 이미 등록됨)
        if !notificationService.isDeviceRegistered() {
            print("[HomeView] ❌ 디바이스가 아직 등록되지 않음 - 구독 실패")
            await MainActor.run {
                notificationService.updateNotificationEnabled(false, for: festivalId)
            }
            pendingFestivalId = nil
            return
        }

        print("[HomeView] ✅ 디바이스 이미 등록됨 - deviceId: \(notificationService.deviceId ?? -1)")

        // 3. 축제 알림 구독
        print("[HomeView] 🎪 축제 알림 구독 시작")
        do {
            let universityName = festivalDetail?.universityName
            let _ = try await notificationService.subscribeToFestivalNotifications(
                festivalId: festivalId,
                universityName: universityName
            )
            print("[APIClient] ✅ 축제 알림 구독 성공")

            // 구독 성공 시 토글 상태 확실히 동기화 (NotificationService에서 이미 처리하지만 명시적으로)
            await MainActor.run {
                notificationService.updateNotificationEnabled(true, for: festivalId)
            }
            print("[HomeView] ✅ 구독 성공으로 인해 토글 ON 동기화")
        } catch {
            print("[HomeView] ❌ 축제 알림 구독 실패: \(error)")

            // 구독 실패 시 토글 상태를 OFF로 설정
            await MainActor.run {
                notificationService.updateNotificationEnabled(false, for: festivalId)
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
    var onPosterTap: ((Int, [String]) -> Void)? = nil

    private var sortedImages: [FestivalImage] {
        festival.festivalImages.sorted { $0.sequence < $1.sequence }
    }

    var body: some View {
        PosterCarousel(
            imageUrls: sortedImages.compactMap { ImageURLResolver.resolve($0.imageUrl) },
            currentIndex: $currentIndex,
            onPosterTap: { index, urls in
                onPosterTap?(index, urls)
            }
        )
        .onAppear {
            currentIndex = 0
        }
    }
}

// MARK: - 포스터 캐러셀 (풀 슬라이드)
struct PosterCarousel: View {
    let imageUrls: [String]
    @Binding var currentIndex: Int
    var onPosterTap: ((Int, [String]) -> Void)? = nil

    private var cardWidthRatio: CGFloat { PosterCarouselConstants.cardWidthRatio }
    private var posterWidth: CGFloat { UIScreen.main.bounds.width * cardWidthRatio }
    private var posterHeight: CGFloat { posterWidth * 4 / 3 }
    private var containerHeight: CGFloat {
        posterHeight
    }

    var body: some View {
        if imageUrls.isEmpty {
            EmptyView()
        } else {
            PosterCarouselContainerView(
                imageUrls: imageUrls,
                currentIndex: $currentIndex,
                cardWidthRatio: cardWidthRatio,
                cornerRadius: 12,
                shadowColor: UIColor.black.withAlphaComponent(0.18),
                shadowOpacity: 0.25,
                shadowRadius: 12,
                shadowOffset: CGSize(width: 0, height: 8),
                onPosterTap: onPosterTap
            )
            .frame(maxWidth: .infinity)
            .frame(height: containerHeight)
            .onAppear {
                let clamped = clampedIndex(currentIndex)
                if currentIndex != clamped {
                    DispatchQueue.main.async {
                        currentIndex = clamped
                    }
                }
                prefetch(urls: neighborUrls(for: clamped))
            }
            .onChange(of: currentIndex) { _, newValue in
                prefetch(urls: neighborUrls(for: newValue))
            }
            .onChange(of: imageUrls.count) { _, _ in
                let clamped = clampedIndex(currentIndex)
                if currentIndex != clamped {
                    DispatchQueue.main.async {
                        currentIndex = clamped
                    }
                }
                prefetch(urls: neighborUrls(for: clamped))
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
        let previous = index - 1
        if previous >= 0 {
            indices.insert(previous)
        }
        let next = index + 1
        if next < imageUrls.count {
            indices.insert(next)
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

private struct PosterImageViewerState: Identifiable {
    let id = UUID()
    let imageUrls: [String]
    let initialIndex: Int
    let isPagingEnabled: Bool
}

private struct PosterImageViewer: View {
    let state: PosterImageViewerState
    let onDismiss: () -> Void

    @State private var currentIndex: Int

    init(state: PosterImageViewerState, onDismiss: @escaping () -> Void) {
        self.state = state
        self.onDismiss = onDismiss
        _currentIndex = State(initialValue: state.initialIndex)
    }

    var body: some View {
        GeometryReader { geometry in
            ImageGalleryView(
                imageUrls: state.imageUrls,
                initialIndex: state.initialIndex,
                currentIndex: $currentIndex,
                allowPaging: state.isPagingEnabled,
                topInset: geometry.safeAreaInsets.top,
                onClose: onDismiss
            )
        }
        .ignoresSafeArea()
    }
}
// MARK: - UIKit Carousel Wrapper
struct PosterCarouselContainerView: UIViewControllerRepresentable {
    let imageUrls: [String]
    @Binding var currentIndex: Int
    let cardWidthRatio: CGFloat
    let cornerRadius: CGFloat
    let shadowColor: UIColor
    let shadowOpacity: Float
    let shadowRadius: CGFloat
    let shadowOffset: CGSize
    let onPosterTap: ((Int, [String]) -> Void)?

    func makeCoordinator() -> Coordinator {
        Coordinator(parent: self)
    }

    func makeUIViewController(context: Context) -> PosterCarouselViewController {
        let controller = PosterCarouselViewController()
        controller.delegate = context.coordinator
        controller.cardWidthRatio = cardWidthRatio
        controller.updateAppearance(
            cornerRadius: cornerRadius,
            shadowColor: shadowColor,
            shadowOpacity: shadowOpacity,
            shadowRadius: shadowRadius,
            shadowOffset: shadowOffset
        )
        controller.update(imageUrls: imageUrls, currentIndex: currentIndex, animated: false)
        return controller
    }

    func updateUIViewController(_ controller: PosterCarouselViewController, context: Context) {
        context.coordinator.parent = self
        controller.cardWidthRatio = cardWidthRatio
        controller.updateAppearance(
            cornerRadius: cornerRadius,
            shadowColor: shadowColor,
            shadowOpacity: shadowOpacity,
            shadowRadius: shadowRadius,
            shadowOffset: shadowOffset
        )
        controller.update(imageUrls: imageUrls, currentIndex: currentIndex, animated: false)
    }

    final class Coordinator: NSObject, PosterCarouselViewControllerDelegate {
        var parent: PosterCarouselContainerView

        init(parent: PosterCarouselContainerView) {
            self.parent = parent
        }

        func posterCarousel(_ controller: PosterCarouselViewController, didUpdateIndex index: Int) {
            guard parent.currentIndex != index else { return }
            DispatchQueue.main.async { [weak self] in
                self?.parent.currentIndex = index
            }
        }

        func posterCarousel(_ controller: PosterCarouselViewController, didSelectPosterAt index: Int) {
            guard let handler = parent.onPosterTap,
                  parent.imageUrls.indices.contains(index) else { return }
            DispatchQueue.main.async { [weak self] in
                guard let self else { return }
                handler(index, self.parent.imageUrls)
            }
        }
    }
}

protocol PosterCarouselViewControllerDelegate: AnyObject {
    func posterCarousel(_ controller: PosterCarouselViewController, didUpdateIndex index: Int)
    func posterCarousel(_ controller: PosterCarouselViewController, didSelectPosterAt index: Int)
}

final class PosterCarouselViewController: UIViewController {
    weak var delegate: PosterCarouselViewControllerDelegate?

    private var imageUrls: [String] = []
    private var repeatedImageUrls: [String] = []
    private var currentIndex: Int = 0 // actual index within imageUrls
    private var currentRepeatedIndex: Int = 0 // index within repeatedImageUrls
    private var isProgrammaticScroll = false
    private var pendingProgrammaticNotification: Int?
    private var isAdjustingContentOffset = false

    private var cellStyle = PosterCarouselCellStyle()
    var cardWidthRatio: CGFloat = PosterCarouselConstants.cardWidthRatio

    private let repetitionCount = 40 // even number to allow centering
    private var isInfiniteScrollEnabled: Bool { imageUrls.count > 1 }
    private var repeatedItemCount: Int { repeatedImageUrls.count }
    private var actualItemCount: Int { imageUrls.count }

    private lazy var collectionView: UICollectionView = {
        let layout = UICollectionViewFlowLayout()
        layout.scrollDirection = .horizontal
        layout.minimumLineSpacing = 12
        layout.sectionInset = .zero
        let cv = UICollectionView(frame: .zero, collectionViewLayout: layout)
        cv.translatesAutoresizingMaskIntoConstraints = false
        cv.backgroundColor = .clear
        cv.showsHorizontalScrollIndicator = false
        cv.decelerationRate = .fast
        cv.contentInsetAdjustmentBehavior = .never
        cv.register(PosterCarouselCell.self, forCellWithReuseIdentifier: PosterCarouselCell.reuseIdentifier)
        cv.dataSource = self
        cv.delegate = self
        return cv
    }()

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .clear
        setupLayout()
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        updateLayoutMetrics()
        centerIfNeeded()
    }

    func updateAppearance(
        cornerRadius: CGFloat,
        shadowColor: UIColor,
        shadowOpacity: Float,
        shadowRadius: CGFloat,
        shadowOffset: CGSize
    ) {
        cellStyle.cornerRadius = cornerRadius
        cellStyle.shadowColor = shadowColor
        cellStyle.shadowOpacity = shadowOpacity
        cellStyle.shadowRadius = shadowRadius
        cellStyle.shadowOffset = shadowOffset
        collectionView.visibleCells.compactMap { $0 as? PosterCarouselCell }.forEach { cell in
            cell.apply(style: cellStyle)
        }
    }

    func update(imageUrls: [String], currentIndex: Int, animated: Bool) {
        let normalizedUrls = imageUrls
        let didChangeUrls = normalizedUrls != self.imageUrls

        self.imageUrls = normalizedUrls
        self.repeatedImageUrls = buildRepeatedUrls(from: normalizedUrls)

        if didChangeUrls {
            collectionView.reloadData()
            view.layoutIfNeeded()
        }

        guard repeatedItemCount > 0 else {
            self.currentIndex = 0
            currentRepeatedIndex = 0
            let offsetX = -collectionView.contentInset.left
            collectionView.setContentOffset(CGPoint(x: offsetX, y: 0), animated: false)
            return
        }

        let clampedActualIndex = clamped(currentIndex)
        let targetRepeatedIndex = initialRepeatedIndex(for: clampedActualIndex)

        self.currentIndex = clampedActualIndex
        self.currentRepeatedIndex = targetRepeatedIndex

        let shouldAnimate = animated && !didChangeUrls
        scrollToRepeatedIndex(targetRepeatedIndex, animated: shouldAnimate, notifyDelegate: false)

        if shouldAnimate == false { // ensure immediate readiness for reverse swipe on first render
            primeRepeatedIndexIfNeeded()
        } else {
            DispatchQueue.main.async { [weak self] in
                self?.primeRepeatedIndexIfNeeded()
            }
        }
    }

    private func setupLayout() {
        view.addSubview(collectionView)

        NSLayoutConstraint.activate([
            collectionView.topAnchor.constraint(equalTo: view.topAnchor),
            collectionView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            collectionView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            collectionView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])
    }

    private func updateLayoutMetrics() {
        guard let layout = collectionView.collectionViewLayout as? UICollectionViewFlowLayout else { return }
        let availableWidth = collectionView.bounds.width
        guard availableWidth > 0 else { return }
        let ratio = min(max(cardWidthRatio, 0.5), 1.0)
        let width = availableWidth * ratio
        let height = collectionView.bounds.height
        layout.itemSize = CGSize(width: width, height: height)
        let sideInset = max((availableWidth - width) / 2, 0)
        collectionView.contentInset = UIEdgeInsets(top: 0, left: sideInset, bottom: 0, right: sideInset)
    }

    private func centerIfNeeded() {
        guard repeatedItemCount > 0,
              let expectedOffset = expectedContentOffset(forRepeatedIndex: currentRepeatedIndex) else { return }
        let delta = abs(collectionView.contentOffset.x - expectedOffset.x)
        guard delta > 1 else { return }

        scrollToRepeatedIndex(currentRepeatedIndex, animated: false, notifyDelegate: false)
    }

    private func scrollToRepeatedIndex(_ repeatedIndex: Int, animated: Bool, notifyDelegate: Bool) {
        guard repeatedImageUrls.indices.contains(repeatedIndex),
              let offset = expectedContentOffset(forRepeatedIndex: repeatedIndex) else { return }

        let actual = actualIndex(forRepeated: repeatedIndex)
        currentRepeatedIndex = repeatedIndex
        currentIndex = actual

        if animated {
            isProgrammaticScroll = true
            pendingProgrammaticNotification = notifyDelegate ? actual : nil
            collectionView.setContentOffset(offset, animated: true)
        } else {
            let previousState = isProgrammaticScroll
            isProgrammaticScroll = true
            pendingProgrammaticNotification = nil
            UIView.performWithoutAnimation {
                collectionView.setContentOffset(offset, animated: false)
                collectionView.layoutIfNeeded()
            }
            isProgrammaticScroll = previousState
            if notifyDelegate {
                delegate?.posterCarousel(self, didUpdateIndex: actual)
            }
        }
    }

    private func adjustContentOffsetIfNeeded() {
        guard isInfiniteScrollEnabled,
              !isAdjustingContentOffset,
              let layout = collectionView.collectionViewLayout as? UICollectionViewFlowLayout else { return }

        let cellWidth = layout.itemSize.width + layout.minimumLineSpacing
        guard cellWidth > 0, actualItemCount > 0 else { return }

        let inset = collectionView.contentInset.left
        let rawOffset = collectionView.contentOffset.x + inset

        let cycleWidth = cellWidth * CGFloat(actualItemCount)
        let totalWidth = cellWidth * CGFloat(repeatedItemCount)

        let minimumOffset = cycleWidth * CGFloat(repetitionCount / 4)
        let maximumOffset = totalWidth - cycleWidth * CGFloat(repetitionCount / 4)

        if rawOffset < minimumOffset {
            isAdjustingContentOffset = true
            let shift = cycleWidth * CGFloat(repetitionCount / 2)
            let newOffset = rawOffset + shift
            collectionView.setContentOffset(CGPoint(x: newOffset - inset, y: collectionView.contentOffset.y), animated: false)
            collectionView.layoutIfNeeded()
            isAdjustingContentOffset = false
        } else if rawOffset > maximumOffset {
            isAdjustingContentOffset = true
            let shift = cycleWidth * CGFloat(repetitionCount / 2)
            let newOffset = rawOffset - shift
            collectionView.setContentOffset(CGPoint(x: newOffset - inset, y: collectionView.contentOffset.y), animated: false)
            collectionView.layoutIfNeeded()
            isAdjustingContentOffset = false
        }
    }

    private func buildRepeatedUrls(from urls: [String]) -> [String] {
        guard !urls.isEmpty else { return [] }
        guard urls.count > 1 else { return urls }

        var repeated: [String] = []
        repeated.reserveCapacity(urls.count * repetitionCount)
        for _ in 0..<repetitionCount {
            repeated.append(contentsOf: urls)
        }
        return repeated
    }

    private func initialRepeatedIndex(for actualIndex: Int) -> Int {
        guard actualItemCount > 0 else { return 0 }
        guard isInfiniteScrollEnabled else { return actualIndex }
        let middleCycle = (repetitionCount / 2) * actualItemCount
        return middleCycle + actualIndex
    }

    private func expectedContentOffset(forRepeatedIndex index: Int) -> CGPoint? {
        guard let layout = collectionView.collectionViewLayout as? UICollectionViewFlowLayout else { return nil }
        let cellWidth = layout.itemSize.width + layout.minimumLineSpacing
        guard cellWidth > 0 else { return nil }
        let inset = collectionView.contentInset.left
        let offsetX = CGFloat(index) * cellWidth - inset
        return CGPoint(x: offsetX, y: 0)
    }

    private func updateIndicesForVisibleItem(notifyDelegate: Bool) {
        guard let layout = collectionView.collectionViewLayout as? UICollectionViewFlowLayout,
              layout.itemSize.width + layout.minimumLineSpacing > 0,
              actualItemCount > 0 else { return }

        let cellWidth = layout.itemSize.width + layout.minimumLineSpacing
        let inset = collectionView.contentInset.left
        let offset = collectionView.contentOffset.x + inset
        let rawIndex = Int(round(offset / cellWidth))
        let clampedRepeatedIndex = max(0, min(repeatedItemCount - 1, rawIndex))

        currentRepeatedIndex = clampedRepeatedIndex
        let actual = actualIndex(forRepeated: clampedRepeatedIndex)

        if currentIndex != actual {
            currentIndex = actual
            if notifyDelegate {
                delegate?.posterCarousel(self, didUpdateIndex: actual)
            }
        }
    }

    private func actualIndex(forRepeated index: Int) -> Int {
        guard actualItemCount > 0 else { return 0 }
        let normalized = ((index % actualItemCount) + actualItemCount) % actualItemCount
        return normalized
    }

    private func clamped(_ index: Int) -> Int {
        guard actualItemCount > 0 else { return 0 }
        return min(max(index, 0), actualItemCount - 1)
    }

    private func primeRepeatedIndexIfNeeded() {
        guard isInfiniteScrollEnabled,
              actualItemCount > 0,
              repeatedItemCount > 0 else { return }

        let desiredIndex = currentRepeatedIndex + actualItemCount
        guard desiredIndex < repeatedItemCount else { return }

        if desiredIndex != currentRepeatedIndex {
            scrollToRepeatedIndex(desiredIndex, animated: false, notifyDelegate: false)
        }
    }

}

extension PosterCarouselViewController: UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        repeatedItemCount
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        guard let cell = collectionView.dequeueReusableCell(
            withReuseIdentifier: PosterCarouselCell.reuseIdentifier,
            for: indexPath
        ) as? PosterCarouselCell else {
            return UICollectionViewCell()
        }

        guard repeatedImageUrls.indices.contains(indexPath.item) else {
            cell.apply(style: cellStyle)
            return cell
        }

        let urlString = repeatedImageUrls[indexPath.item]
        cell.configure(with: urlString, style: cellStyle)
        return cell
    }
}

extension PosterCarouselViewController: UICollectionViewDelegateFlowLayout {
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        guard !isProgrammaticScroll else { return }
        adjustContentOffsetIfNeeded()
        updateIndicesForVisibleItem(notifyDelegate: false)
    }

    func scrollViewWillEndDragging(
        _ scrollView: UIScrollView,
        withVelocity velocity: CGPoint,
        targetContentOffset: UnsafeMutablePointer<CGPoint>
    ) {
        guard let layout = collectionView.collectionViewLayout as? UICollectionViewFlowLayout,
              repeatedItemCount > 0 else { return }

        let cellWidth = layout.itemSize.width + layout.minimumLineSpacing
        guard cellWidth > 0 else { return }
        let inset = scrollView.contentInset.left
        let proposedOffsetX = targetContentOffset.pointee.x + inset
        let index = round(proposedOffsetX / cellWidth)
        let clampedIndex = Int(max(0, min(index, CGFloat(repeatedItemCount - 1))))

        let newOffsetX = CGFloat(clampedIndex) * cellWidth - inset
        targetContentOffset.pointee = CGPoint(x: newOffsetX, y: targetContentOffset.pointee.y)
    }

    func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        isProgrammaticScroll = false
        adjustContentOffsetIfNeeded()
        updateIndicesForVisibleItem(notifyDelegate: true)
    }

    func scrollViewDidEndScrollingAnimation(_ scrollView: UIScrollView) {
        isProgrammaticScroll = false
        adjustContentOffsetIfNeeded()
        updateIndicesForVisibleItem(notifyDelegate: false)

        if let pending = pendingProgrammaticNotification {
            pendingProgrammaticNotification = nil
            delegate?.posterCarousel(self, didUpdateIndex: pending)
        }
    }

    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        guard repeatedImageUrls.indices.contains(indexPath.item) else { return }
        let actual = actualIndex(forRepeated: indexPath.item)
        delegate?.posterCarousel(self, didSelectPosterAt: actual)
    }
}
// MARK: - Carousel Cell & Loader
private struct PosterCarouselCellStyle {
    var cornerRadius: CGFloat = 12
    var shadowColor: UIColor = UIColor.black.withAlphaComponent(0.18)
    var shadowOpacity: Float = 0.25
    var shadowRadius: CGFloat = 12
    var shadowOffset: CGSize = CGSize(width: 0, height: 8)
}

private final class PosterCarouselCell: UICollectionViewCell {
    static let reuseIdentifier = "PosterCarouselCell"

    private let shadowContainer: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.layer.masksToBounds = false
        return view
    }()

    private let imageView: UIImageView = {
        let imageView = UIImageView()
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.contentMode = .scaleAspectFill
        imageView.clipsToBounds = true
        imageView.backgroundColor = .secondarySystemBackground
        return imageView
    }()

    private var loadToken: UUID?

    override init(frame: CGRect) {
        super.init(frame: frame)
        contentView.backgroundColor = .clear
        contentView.clipsToBounds = false

        contentView.addSubview(shadowContainer)
        shadowContainer.addSubview(imageView)

        NSLayoutConstraint.activate([
            shadowContainer.topAnchor.constraint(equalTo: contentView.topAnchor),
            shadowContainer.leadingAnchor.constraint(equalTo: contentView.leadingAnchor),
            shadowContainer.trailingAnchor.constraint(equalTo: contentView.trailingAnchor),
            shadowContainer.bottomAnchor.constraint(equalTo: contentView.bottomAnchor),

            imageView.topAnchor.constraint(equalTo: shadowContainer.topAnchor),
            imageView.leadingAnchor.constraint(equalTo: shadowContainer.leadingAnchor),
            imageView.trailingAnchor.constraint(equalTo: shadowContainer.trailingAnchor),
            imageView.bottomAnchor.constraint(equalTo: shadowContainer.bottomAnchor)
        ])
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func prepareForReuse() {
        super.prepareForReuse()
        imageView.image = PosterCarouselCell.placeholderImage
        if let token = loadToken {
            CarouselImageLoader.shared.cancelLoad(token)
            loadToken = nil
        }
    }

    func configure(with urlString: String, style: PosterCarouselCellStyle) {
        apply(style: style)
        imageView.image = PosterCarouselCell.placeholderImage
        loadToken = CarouselImageLoader.shared.loadImage(urlString: urlString) { [weak self] image in
            guard let self else { return }
            self.imageView.image = image ?? PosterCarouselCell.placeholderImage
            self.loadToken = nil
        }
    }

    func apply(style: PosterCarouselCellStyle) {
        shadowContainer.layer.shadowColor = style.shadowColor.cgColor
        shadowContainer.layer.shadowOpacity = style.shadowOpacity
        shadowContainer.layer.shadowRadius = style.shadowRadius
        shadowContainer.layer.shadowOffset = style.shadowOffset

        imageView.layer.cornerRadius = style.cornerRadius
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        shadowContainer.layer.shadowPath = UIBezierPath(roundedRect: shadowContainer.bounds, cornerRadius: imageView.layer.cornerRadius).cgPath
    }

    private static let placeholderImage: UIImage? = {
        let size = CGSize(width: 10, height: 10)
        let renderer = UIGraphicsImageRenderer(size: size)
        return renderer.image { context in
            UIColor.systemGray5.setFill()
            context.fill(CGRect(origin: .zero, size: size))
        }
    }()
}

private final class CarouselImageLoader {
    static let shared = CarouselImageLoader()

    private let session: URLSession
    private var tasks: [UUID: URLSessionDataTask] = [:]
    private let lock = NSLock()

    private init() {
        let configuration = URLSessionConfiguration.default
        configuration.urlCache = URLCache(
            memoryCapacity: 50 * 1024 * 1024,
            diskCapacity: 200 * 1024 * 1024
        )
        configuration.requestCachePolicy = .returnCacheDataElseLoad
        session = URLSession(configuration: configuration)
    }

    @discardableResult
    func loadImage(urlString: String, completion: @escaping (UIImage?) -> Void) -> UUID? {
        guard let url = URL(string: urlString) else {
            DispatchQueue.main.async {
                completion(nil)
            }
            return nil
        }

        var request = URLRequest(url: url)
        request.cachePolicy = .returnCacheDataElseLoad

        if let cachedResponse = session.configuration.urlCache?.cachedResponse(for: request),
           let image = UIImage(data: cachedResponse.data) {
            DispatchQueue.main.async {
                completion(image)
            }
            return nil
        }

        let token = UUID()

        let task = session.dataTask(with: request) { [weak self] data, response, _ in
            var image: UIImage? = nil
            if let data, let fetchedImage = UIImage(data: data) {
                image = fetchedImage
                if let response {
                    let cachedResponse = CachedURLResponse(response: response, data: data)
                    self?.session.configuration.urlCache?.storeCachedResponse(cachedResponse, for: request)
                }
            }

            DispatchQueue.main.async {
                completion(image)
            }

            self?.lock.lock()
            self?.tasks[token] = nil
            self?.lock.unlock()
        }

        lock.lock()
        tasks[token] = task
        lock.unlock()

        task.resume()
        return token
    }

    func cancelLoad(_ token: UUID) {
        lock.lock()
        let task = tasks[token]
        tasks[token] = nil
        lock.unlock()
        task?.cancel()
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
            let imageURL = ImageURLResolver.resolve(image.imageUrl) ?? ""
            
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

    private let isoDateTimeFormatter: ISO8601DateFormatter = {
        let formatter = ISO8601DateFormatter()
        formatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        formatter.timeZone = TimeZone(identifier: "Asia/Seoul")
        return formatter
    }()

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            if isLoading {
                // 로딩 중 제목/버튼 스켈레톤
                LineupHeaderPlaceholder()
            } else {
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
                // 로딩 중 날짜 그룹과 라인업 스켈레톤
                VStack(alignment: .leading, spacing: 20) {
                    ForEach(0..<2, id: \.self) { _ in
                        VStack(alignment: .leading, spacing: 20) {
                            // 날짜 제목과 언더바 스켈레톤
                            LineupDateGroupPlaceholder()
                            
                            // 라인업 프로필 스켈레톤
                            ScrollView(.horizontal, showsIndicators: false) {
                                LazyHStack(spacing: 12) {
                                    ForEach(0..<4, id: \.self) { _ in
                                        CircularProfilePlaceholder()
                                    }
                                }
                                .padding(.horizontal, 4)
                            }
                        }
                    }
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
            if let date = parsePerformanceDate(from: lineup.performanceAt) {
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

    private func parsePerformanceDate(from value: String) -> Date? {
        for formatter in [backendFormatter, backendFormatterNoFraction, apiResponseFormatter] {
            if let date = formatter.date(from: value) {
                return date
            }
        }

        if let isoDate = isoDateTimeFormatter.date(from: value) {
            return isoDate
        }

        // ISO formatter without fractional seconds fallback
        let isoNoFraction = ISO8601DateFormatter()
        isoNoFraction.formatOptions = [.withInternetDateTime]
        isoNoFraction.timeZone = TimeZone(identifier: "Asia/Seoul")
        return isoNoFraction.date(from: value)
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
            let artistImageURL = ImageURLResolver.resolve(lineup.imageUrl) ?? ""

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

// 대학교 이름 로딩 플레이스홀더 (스켈레톤 뷰)
struct UniversityNamePlaceholder: View {
    @State private var isAnimating = false
    
    var body: some View {
        RoundedRectangle(cornerRadius: 6)
            .fill(Color.gray.opacity(0.2))
            .frame(width: 180, height: 28)
            .overlay(
                RoundedRectangle(cornerRadius: 6)
                    .fill(
                        LinearGradient(
                            colors: [Color.clear, Color.white.opacity(0.6), Color.clear],
                            startPoint: .leading,
                            endPoint: .trailing
                        )
                    )
                    .offset(x: isAnimating ? 180 : -180)
                    .mask(RoundedRectangle(cornerRadius: 6))
            )
            .clipped()
            .onAppear {
                withAnimation(.linear(duration: 1.5).repeatForever(autoreverses: false)) {
                    isAnimating = true
                }
            }
    }
}

// 축제 포스터 로딩 플레이스홀더 (스켈레톤 뷰)
struct FestivalPosterPlaceholder: View {
    @State private var isAnimating = false
    
    private var posterWidth: CGFloat {
        UIScreen.main.bounds.width * PosterCarouselConstants.cardWidthRatio
    }
    private var posterHeight: CGFloat { posterWidth * 4 / 3 }
    private let carouselSpacing: CGFloat = 12
    
var body: some View {
        ZStack(alignment: .center) {
            sideCard(multiplier: -1)
            sideCard(multiplier: 1)
            mainCard()
        }
        .frame(width: UIScreen.main.bounds.width, height: posterHeight)
        .clipped()
        .onAppear {
            withAnimation(.linear(duration: 1.5).repeatForever(autoreverses: false)) {
                isAnimating = true
            }
        }
    }

    private func mainCard() -> some View {
        placeholderCard(width: posterWidth, height: posterHeight, baseOpacity: 0.2)
            .zIndex(1)
    }

    private func sideCard(multiplier: CGFloat) -> some View {
        let sideWidth = posterWidth
        let sideHeight = posterHeight
        let horizontalOffset = (posterWidth + carouselSpacing) * multiplier

        return placeholderCard(width: sideWidth, height: sideHeight, baseOpacity: 0.14)
            .offset(x: horizontalOffset)
            .zIndex(0)
    }

    private func placeholderCard(width: CGFloat, height: CGFloat, baseOpacity: Double) -> some View {
        RoundedRectangle(cornerRadius: 12)
            .fill(Color.gray.opacity(baseOpacity))
            .frame(width: width, height: height)
            .overlay(shimmerOverlay(width: width, height: height))
    }

    private func shimmerOverlay(width: CGFloat, height: CGFloat) -> some View {
        RoundedRectangle(cornerRadius: 12)
            .fill(
                LinearGradient(
                    colors: [Color.clear, Color.white.opacity(0.45), Color.clear],
                    startPoint: .leading,
                    endPoint: .trailing
                )
            )
            .frame(width: width, height: height)
            .offset(x: isAnimating ? width : -width)
            .mask(RoundedRectangle(cornerRadius: 12))
    }
}

// 축제 제목/날짜 로딩 플레이스홀더 (스켈레톤 뷰)
struct FestivalTitlePlaceholder: View {
    @State private var isAnimating = false
    
    var body: some View {
        VStack(spacing: 4) {
            // 축제 제목 스켈레톤 (2줄)
            HStack {
                RoundedRectangle(cornerRadius: 4)
                    .fill(Color.gray.opacity(0.2))
                    .frame(width: 280, height: 22)
                    .overlay(
                        RoundedRectangle(cornerRadius: 4)
                            .fill(
                                LinearGradient(
                                    colors: [Color.clear, Color.white.opacity(0.6), Color.clear],
                                    startPoint: .leading,
                                    endPoint: .trailing
                                )
                            )
                            .offset(x: isAnimating ? 280 : -280)
                            .mask(RoundedRectangle(cornerRadius: 4))
                    )
                    .clipped()
                Spacer()
            }
            
            HStack {
                RoundedRectangle(cornerRadius: 4)
                    .fill(Color.gray.opacity(0.2))
                    .frame(width: 200, height: 22)
                    .overlay(
                        RoundedRectangle(cornerRadius: 4)
                            .fill(
                                LinearGradient(
                                    colors: [Color.clear, Color.white.opacity(0.6), Color.clear],
                                    startPoint: .leading,
                                    endPoint: .trailing
                                )
                            )
                            .offset(x: isAnimating ? 200 : -200)
                            .mask(RoundedRectangle(cornerRadius: 4))
                    )
                    .clipped()
                Spacer()
            }
            
            // 날짜 스켈레톤
            HStack {
                RoundedRectangle(cornerRadius: 4)
                    .fill(Color.gray.opacity(0.2))
                    .frame(width: 120, height: 16)
                    .overlay(
                        RoundedRectangle(cornerRadius: 4)
                            .fill(
                                LinearGradient(
                                    colors: [Color.clear, Color.white.opacity(0.6), Color.clear],
                                    startPoint: .leading,
                                    endPoint: .trailing
                                )
                            )
                            .offset(x: isAnimating ? 120 : -120)
                            .mask(RoundedRectangle(cornerRadius: 4))
                    )
                    .clipped()
                Spacer()
            }
            .padding(.top, 8)
        }
        .padding(.horizontal, 20)
        .padding(.top, 10)
        .onAppear {
            withAnimation(.linear(duration: 1.5).repeatForever(autoreverses: false)) {
                isAnimating = true
            }
        }
    }
}

// 라인업 헤더 로딩 플레이스홀더 (제목/버튼 스켈레톤)
struct LineupHeaderPlaceholder: View {
    @State private var isAnimating = false
    
    var body: some View {
        HStack {
            // "축제 라인업" 제목 스켈레톤
            RoundedRectangle(cornerRadius: 4)
                .fill(Color.gray.opacity(0.2))
                .frame(width: 120, height: 20)
                .overlay(
                    RoundedRectangle(cornerRadius: 4)
                        .fill(
                            LinearGradient(
                                colors: [Color.clear, Color.white.opacity(0.6), Color.clear],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                        )
                        .offset(x: isAnimating ? 120 : -120)
                        .mask(RoundedRectangle(cornerRadius: 4))
                )
                .clipped()
            
            Spacer()
            
            // "일정 확인하기 >" 버튼 스켈레톤
            RoundedRectangle(cornerRadius: 4)
                .fill(Color.gray.opacity(0.2))
                .frame(width: 100, height: 16)
                .overlay(
                    RoundedRectangle(cornerRadius: 4)
                        .fill(
                            LinearGradient(
                                colors: [Color.clear, Color.white.opacity(0.6), Color.clear],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                        )
                        .offset(x: isAnimating ? 100 : -100)
                        .mask(RoundedRectangle(cornerRadius: 4))
                )
                .clipped()
        }
        .onAppear {
            withAnimation(.linear(duration: 1.5).repeatForever(autoreverses: false)) {
                isAnimating = true
            }
        }
    }
}

// 라인업 날짜 그룹 로딩 플레이스홀더 (날짜 제목과 언더바 스켈레톤)
struct LineupDateGroupPlaceholder: View {
    @State private var isAnimating = false
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack(spacing: 8) {
                // 날짜 제목 스켈레톤 (예: "5월 14일")
                RoundedRectangle(cornerRadius: 4)
                    .fill(Color.gray.opacity(0.2))
                    .frame(width: 80, height: 18)
                    .overlay(
                        RoundedRectangle(cornerRadius: 4)
                            .fill(
                                LinearGradient(
                                    colors: [Color.clear, Color.white.opacity(0.6), Color.clear],
                                    startPoint: .leading,
                                    endPoint: .trailing
                                )
                            )
                            .offset(x: isAnimating ? 80 : -80)
                            .mask(RoundedRectangle(cornerRadius: 4))
                    )
                    .clipped()
                
                // "오늘" 배지 스켈레톤 (가끔 표시)
                RoundedRectangle(cornerRadius: 12)
                    .fill(Color.gray.opacity(0.15))
                    .frame(width: 40, height: 20)
                
                Spacer()
            }
            .overlay(alignment: .bottomLeading) {
                // 언더바 스켈레톤
                RoundedRectangle(cornerRadius: 2)
                    .fill(Color.gray.opacity(0.2))
                    .frame(width: min(UIScreen.main.bounds.width * 0.20, 100), height: 3)
                    .overlay(
                        RoundedRectangle(cornerRadius: 2)
                            .fill(
                                LinearGradient(
                                    colors: [Color.clear, Color.white.opacity(0.6), Color.clear],
                                    startPoint: .leading,
                                    endPoint: .trailing
                                )
                            )
                            .offset(x: isAnimating ? 100 : -100)
                            .mask(RoundedRectangle(cornerRadius: 2))
                    )
                    .clipped()
                    .offset(y: 8)
            }
        }
        .onAppear {
            withAnimation(.linear(duration: 1.5).repeatForever(autoreverses: false)) {
                isAnimating = true
            }
        }
    }
}
