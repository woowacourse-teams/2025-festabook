import Foundation

protocol FAQRepository {
    func getFAQs() async throws -> [FAQ]
}

struct FAQRepositoryLive: FAQRepository {
    let api: APIClient

    func getFAQs() async throws -> [FAQ] {
        return try await api.get(Endpoints.News.faqs)
    }
}
