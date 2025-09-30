import Foundation

import Combine

final class AppState: ObservableObject {
    private enum UserDefaultsKey {
        static let currentFestivalId = "currentFestivalId"
        static let currentUniversityName = "currentUniversityName"
    }
    @Published var selectedUniversity: University?
    @Published var selectedFestival: Festival?
    @Published var currentFestivalId: Int?
    @Published var currentUniversityName: String = "페스타북대학교"
    @Published var pendingAnnouncementId: Int? = nil
    @Published var shouldNavigateToNews = false
    @Published var isInitialLoadCompleted = false
    @Published var initialFestivalList: [Festival] = []

    private var cancellables = Set<AnyCancellable>()

    init() {
        NotificationCenter.default.publisher(for: .notificationTapped)
            .compactMap { $0.object as? [String: Any] }
            .sink { [weak self] payload in
                self?.handleNotificationPayload(payload)
            }
            .store(in: &cancellables)
    }

    func selectUniversity(_ university: University) {
        selectedUniversity = university
        currentUniversityName = university.name
        UserDefaults.standard.set(university.name, forKey: UserDefaultsKey.currentUniversityName)
    }
    
    func changeFestival(_ festivalId: Int) {
        currentFestivalId = festivalId
        selectedFestival = nil
        selectedUniversity = nil
        // 현재 축제 ID를 영속화하고 API 헤더와 동기화
        UserDefaults.standard.set(festivalId, forKey: UserDefaultsKey.currentFestivalId)
        APIClient.shared.updateFestivalId(festivalId)
        ServiceLocator.shared.updateFestivalId(festivalId)

        Task {
            await NotificationService.shared.synchronizeSubscriptionsWithServer(
                focusFestivalId: festivalId,
                focusUniversityName: nil
            )
        }
    }

    func updateUniversityName(_ universityName: String) {
        currentUniversityName = universityName
        UserDefaults.standard.set(universityName, forKey: UserDefaultsKey.currentUniversityName)
    }

    func resetFestivalSelection() {
        currentFestivalId = nil
        selectedUniversity = nil
        selectedFestival = nil
        currentUniversityName = "페스타북대학교"

        UserDefaults.standard.removeObject(forKey: UserDefaultsKey.currentFestivalId)
        UserDefaults.standard.removeObject(forKey: UserDefaultsKey.currentUniversityName)

        APIClient.shared.updateFestivalId(0)
        ServiceLocator.shared.updateFestivalId(0)

        Task {
            await NotificationService.shared.synchronizeSubscriptionsWithServer(
                focusFestivalId: nil,
                focusUniversityName: nil
            )
        }
    }

    func bootstrapFestivalIfNeeded() {
        isInitialLoadCompleted = false
        APIClient.shared.bootstrapFestivalIdFromStorage()

        let storedFestivalId = UserDefaults.standard.object(forKey: UserDefaultsKey.currentFestivalId) as? Int

        guard let storedFestivalId else {
            currentFestivalId = nil
            selectedUniversity = nil
            selectedFestival = nil

            Task { [weak self] in
                await self?.loadInitialFestivalList()
            }
            return
        }

        currentFestivalId = storedFestivalId

        let storedUniversityName = UserDefaults.standard.string(forKey: UserDefaultsKey.currentUniversityName) ?? currentUniversityName
        currentUniversityName = storedUniversityName

        if selectedUniversity == nil {
            selectedUniversity = University(
                id: storedFestivalId,
                name: storedUniversityName,
                latitude: nil,
                longitude: nil
            )
        }

        APIClient.shared.updateFestivalId(storedFestivalId)
        ServiceLocator.shared.updateFestivalId(storedFestivalId)

        Task { [weak self] in
            await self?.hydrateFestivalSelection()
            await MainActor.run {
                self?.isInitialLoadCompleted = true
            }
        }
    }

    private func loadInitialFestivalList() async {
        defer {
            Task { @MainActor [weak self] in
                self?.isInitialLoadCompleted = true
            }
        }

        do {
            let festivals = try await ServiceLocator.shared.festivalRepo.getFestivalsByUniversity(universityName: "")
            await MainActor.run { [weak self] in
                self?.initialFestivalList = festivals
            }
        } catch {
            print("[AppState] ⚠️ Failed to load initial festival list: \(error)")
            await MainActor.run { [weak self] in
                self?.initialFestivalList = []
            }
        }
    }

    private func hydrateFestivalSelection() async {
        do {
            let detail = try await ServiceLocator.shared.festivalRepo.getFestivalDetail()

            await MainActor.run { [weak self] in
                guard let self else { return }
                guard self.selectedUniversity != nil else { return }

                let restoredFestival = Festival(
                    festivalId: detail.festivalId,
                    universityName: detail.universityName,
                    festivalName: detail.festivalName,
                    startDate: detail.startDate,
                    endDate: detail.endDate
                )

                self.selectedFestival = restoredFestival
                self.selectedUniversity = University(
                    id: detail.festivalId,
                    name: detail.universityName,
                    latitude: nil,
                    longitude: nil
                )
                self.currentUniversityName = detail.universityName
                UserDefaults.standard.set(detail.universityName, forKey: UserDefaultsKey.currentUniversityName)
            }
        } catch {
            print("[AppState] ⚠️ Failed to hydrate festival selection: \(error)")
            await MainActor.run { [weak self] in
                self?.isInitialLoadCompleted = true
            }
        }
    }

    private func handleNotificationPayload(_ payload: [String: Any]) {
        guard let type = payload["type"] as? String else { return }

        switch type {
        case "announcement_detail", "announcement":
            handleAnnouncementDeepLink(payload)
        default:
            break
        }
    }

    private func handleAnnouncementDeepLink(_ payload: [String: Any]) {
        guard
            let festivalIdString = payload["festivalId"] as? String,
            let festivalId = Int(festivalIdString),
            let announcementIdString = payload["announcementId"] as? String,
            let announcementId = Int(announcementIdString)
        else {
            print("[AppState] ⚠️ 알림 payload에 festivalId 또는 announcementId 없음: \(payload)")
            return
        }

        Task(priority: .userInitiated) {
            await MainActor.run {
                pendingAnnouncementId = announcementId
                shouldNavigateToNews = true
                NotificationCenter.default.post(name: .navigateToTab, object: "news")
            }

            // 축제가 이미 동일하고, 대학 선택이 유지되어 있으면 재선택 생략
            let needsFestivalUpdate = await MainActor.run { currentFestivalId != festivalId || selectedUniversity == nil }

            guard needsFestivalUpdate else { return }

            let previousUniversity = await MainActor.run { selectedUniversity }

            await MainActor.run {
                changeFestival(festivalId)
                if let preserved = previousUniversity {
                    // 잠시 기존 축제 화면을 유지하여 검색 화면으로 튀는 현상 방지
                    selectedUniversity = preserved
                } else {
                    selectedUniversity = University(
                        id: festivalId,
                        name: currentUniversityName,
                        latitude: nil,
                        longitude: nil
                    )
                }
            }

            do {
                let detail = try await ServiceLocator.shared.festivalRepo.getFestivalDetail()
                await MainActor.run {
                    let festival = Festival(
                        festivalId: detail.festivalId,
                        universityName: detail.universityName,
                        festivalName: detail.festivalName,
                        startDate: detail.startDate,
                        endDate: detail.endDate
                    )

                    selectedFestival = festival
                    selectedUniversity = University(
                        id: detail.festivalId,
                        name: detail.universityName,
                        latitude: nil,
                        longitude: nil
                    )
                    currentUniversityName = detail.universityName
                }
            } catch {
                print("[AppState] ❌ 축제 상세 로드 실패: \(error)")
                await MainActor.run {
                    if selectedUniversity == nil {
                        selectedUniversity = University(
                            id: festivalId,
                            name: currentUniversityName,
                            latitude: nil,
                            longitude: nil
                        )
                    }
                }
            }
        }
    }
}
