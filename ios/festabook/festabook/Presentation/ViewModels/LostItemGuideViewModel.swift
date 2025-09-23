import Foundation
import SwiftUI

@MainActor
final class LostItemGuideViewModel: ObservableObject {
    @Published var lostItemGuide: LostItemGuide?
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var isExpanded = false

    private let repository: LostItemGuideRepositoryProtocol
    private var loadTask: Task<Void, Never>?

    init(repository: LostItemGuideRepositoryProtocol = LostItemGuideRepository()) {
        self.repository = repository
    }

    func loadLostItemGuide() async {
        // 기존 Task 취소
        loadTask?.cancel()

        // 새로운 Task 생성
        loadTask = Task {
            await performLoad()
        }

        await loadTask?.value
    }

    private func performLoad() async {
        print("[LostItemGuideViewModel] 📞 API 호출 시작 - 분실물 가이드 로드")
        isLoading = true
        errorMessage = nil

        do {
            let guide = try await repository.getLostItemGuide()

            // Task가 취소되었는지 확인
            if Task.isCancelled {
                print("[LostItemGuideViewModel] Task was cancelled, ignoring result")
                isLoading = false
                return
            }

            lostItemGuide = guide
            print("[LostItemGuideViewModel] Successfully loaded lost item guide")

        } catch is CancellationError {
            print("[LostItemGuideViewModel] Task cancelled - this is expected during refresh")
            isLoading = false
            return

        } catch let guideError as LostItemGuideError {
            // Task가 취소되었다면 에러 처리하지 않음
            if Task.isCancelled {
                print("[LostItemGuideViewModel] Task cancelled, ignoring error")
                isLoading = false
                return
            }

            print("[LostItemGuideViewModel] LostItemGuideError: \(guideError)")
            errorMessage = guideError.localizedDescription
            lostItemGuide = nil

        } catch {
            // Task가 취소되었다면 에러 처리하지 않음
            if Task.isCancelled {
                print("[LostItemGuideViewModel] Task cancelled, ignoring unexpected error")
                isLoading = false
                return
            }

            print("[LostItemGuideViewModel] Unexpected error: \(error)")
            errorMessage = "분실물 가이드를 불러오는데 실패했습니다."
            lostItemGuide = nil
        }

        isLoading = false
    }

    func toggleExpansion() {
        withAnimation(.easeInOut(duration: 0.3)) {
            isExpanded.toggle()
        }
    }
}
