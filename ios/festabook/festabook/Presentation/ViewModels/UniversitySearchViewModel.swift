import Foundation

@MainActor
final class UniversitySearchViewModel: ObservableObject {
    @Published var query: String = ""
    @Published var results: [University] = []
    @Published var isLoading = false

    private let repo: UniversityRepository
    init(repo: UniversityRepository) { self.repo = repo }

    func performSearch() async {
        isLoading = true
        defer { isLoading = false }
        do { results = try await repo.search(query: query) }
        catch { results = [] }
    }
}
