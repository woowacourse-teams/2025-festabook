import Foundation
import SwiftUI

@MainActor
final class LostItemViewModel: ObservableObject {
    @Published private(set) var items: [LostItem] = []
    @Published private(set) var state: ViewState = .idle
    @Published var selectedItem: LostItem? = nil

    private var loadTask: Task<Void, Never>?

    enum ViewState: Equatable {
        case idle
        case loading
        case loaded
        case empty
        case failed(String)

        static func == (lhs: ViewState, rhs: ViewState) -> Bool {
            switch (lhs, rhs) {
            case (.idle, .idle), (.loading, .loading), (.loaded, .loaded), (.empty, .empty):
                return true
            case (.failed(let lhsMessage), .failed(let rhsMessage)):
                return lhsMessage == rhsMessage
            default:
                return false
            }
        }
    }

    private let service: LostItemServicing

    init(service: LostItemServicing = LostItemAPI()) {
        self.service = service
        // ViewModel 생성 시점에도 동기화 보장
        let storedFestivalId = UserDefaults.standard.object(forKey: "currentFestivalId") as? Int
        if let storedFestivalId, storedFestivalId > 0 {
            APIClient.shared.updateFestivalId(storedFestivalId)
        }
    }

    func refresh() async {
        // 기존 Task 취소
        loadTask?.cancel()

        // 새로운 Task 생성
        loadTask = Task {
            await performLoad(isRefresh: true)
        }

        await loadTask?.value
    }

    private func performLoad(isRefresh: Bool) async {
        let logPrefix = isRefresh ? "(새로고침)" : "(초기 로드)"
        print("[LostItemViewModel] 📞 API 호출 시작 - 분실물 로드 \(logPrefix)")

        state = .loading

        do {
            let fetchedItems = try await service.fetchLostItems()

            // Task가 취소되었는지 확인
            if Task.isCancelled {
                print("[LostItemViewModel] Task was cancelled, ignoring result")
                return
            }

            items = fetchedItems
            state = items.isEmpty ? .empty : .loaded

            print("[LostItemViewModel] Successfully loaded \(items.count) items")

        } catch is CancellationError {
            print("[LostItemViewModel] Task cancelled - this is expected during refresh")
            // Task 취소는 정상 동작이므로 아무것도 하지 않음
            return

        } catch let lostItemError as LostItemError {
            // Task가 취소되었다면 에러 처리하지 않음
            if Task.isCancelled {
                print("[LostItemViewModel] Task cancelled, ignoring error")
                return
            }

            print("[LostItemViewModel] LostItemError: \(lostItemError)")

            switch lostItemError {
            case .empty:
                items = []
                state = .empty
            case .network(let error):
                // cancelled 에러는 새로고침 과정에서 발생하는 정상 취소이므로 무시
                if let nsError = error as NSError?, nsError.code == -999 {
                    print("[LostItemViewModel] Request cancelled (정상 취소): \(error)")
                    return // 에러 상태로 표시하지 않음
                }
                // 진짜 네트워크/전송 오류일 때만 에러 화면
                state = .failed("분실물을 불러오는데 실패했습니다.")
                print("[LostItemViewModel] Network error: \(error)")
            case .decoding(let error):
                // 디코딩 실패는 빈 리스트 처리(백엔드 포맷 이슈로 간주)
                print("[LostItemViewModel] Decoding error: \(error)")
                items = []
                state = .empty
            case .server(let code, _):
                state = .failed("서버 오류가 발생했습니다 (코드: \(code))")
            case .invalidURL:
                state = .failed("잘못된 요청입니다")
            }

        } catch {
            // Task가 취소되었다면 에러 처리하지 않음
            if Task.isCancelled {
                print("[LostItemViewModel] Task cancelled, ignoring unexpected error")
                return
            }

            print("[LostItemViewModel] Unexpected error: \(error)")
            state = .failed("알 수 없는 오류가 발생했습니다")
        }
    }

    func selectItem(_ item: LostItem) {
        print("[LostItemViewModel] Item selected: \(item.lostItemId)")
        selectedItem = item
    }

    func dismissModal() {
        print("[LostItemViewModel] Modal dismissed")
        selectedItem = nil
    }

    func loadInitialData() {
        guard state == .idle else { return }

        loadTask = Task {
            await performLoad(isRefresh: false)
        }
    }
}
