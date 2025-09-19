import Foundation

protocol FestivalRepository {
    func getFestivalsByUniversity(universityName: String) async throws -> [Festival]
    func getFestivalDetail() async throws -> FestivalDetail
    func getLineups() async throws -> [Lineup]
}

struct FestivalRepositoryLive: FestivalRepository {
    let api: APIClient
    
    func getFestivalsByUniversity(universityName: String) async throws -> [Festival] {
        let queryItems = [URLQueryItem(name: "universityName", value: universityName)]
        return try await api.get(Endpoints.Festivals.universities, query: queryItems, requiresFestivalId: false)
    }
    
    func getFestivalDetail() async throws -> FestivalDetail {
        return try await api.get(Endpoints.Festivals.detail)
    }
    
    func getLineups() async throws -> [Lineup] {
        return try await api.get(Endpoints.Festivals.lineups)
    }
}
