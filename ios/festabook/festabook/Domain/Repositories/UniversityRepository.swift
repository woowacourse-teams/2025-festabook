import Foundation

protocol UniversityRepository {
    func search(query: String) async throws -> [University]
}

struct UniversityRepositoryLive: UniversityRepository {
    let api: APIClient
    func search(query: String) async throws -> [University] {
        // 실제 API 호출 (현재 대학교 검색 API가 없으므로 빈 배열 반환)
        // return try await api.get(Endpoints.universitiesSearch, query: [.init(name: "query", value: query)])
        throw APIError.notImplemented
    }
}

enum APIError: Error {
    case notImplemented
}



