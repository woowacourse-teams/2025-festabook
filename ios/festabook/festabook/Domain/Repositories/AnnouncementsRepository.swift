import Foundation

protocol AnnouncementsRepository {
    func getAnnouncements() async throws -> AnnouncementsResponse
}

struct AnnouncementsRepositoryLive: AnnouncementsRepository {
    let api: APIClient

    func getAnnouncements() async throws -> AnnouncementsResponse {
        return try await api.get(Endpoints.News.announcements)
    }
}
