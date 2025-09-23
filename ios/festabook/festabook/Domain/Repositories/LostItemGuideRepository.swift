import Foundation

protocol LostItemGuideRepositoryProtocol {
    func getLostItemGuide() async throws -> LostItemGuide
}

final class LostItemGuideRepository: LostItemGuideRepositoryProtocol {
    private let apiClient: APIClient

    init(apiClient: APIClient = .shared) {
        self.apiClient = apiClient
    }

    func getLostItemGuide() async throws -> LostItemGuide {
        // APIClient의 get 메서드와 유사한 방식으로 URL 구성
        var comps = URLComponents(url: BuildConfig.apiBaseURL, resolvingAgainstBaseURL: false)!
        comps.path += Endpoints.News.lostItemGuide
        guard let url = comps.url else {
            throw LostItemGuideError.invalidURL
        }

        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("application/json", forHTTPHeaderField: "Accept")

        // festival 헤더 추가 (APIClient와 동일한 방식)
        let storedFestivalId = UserDefaults.standard.object(forKey: "currentFestivalId") as? Int
        if let storedFestivalId, storedFestivalId > 0 {
            request.setValue("\(storedFestivalId)", forHTTPHeaderField: "festival")
        }

        let (data, response) = try await URLSession.shared.data(for: request)

        guard let httpResponse = response as? HTTPURLResponse else {
            throw LostItemGuideError.networkError(URLError(.unknown))
        }

        guard httpResponse.statusCode == 200 else {
            throw LostItemGuideError.serverError(httpResponse.statusCode, "Server error")
        }

        let decoder = JSONDecoder()
        do {
            let lostItemGuide = try decoder.decode(LostItemGuide.self, from: data)
            return lostItemGuide
        } catch {
            throw LostItemGuideError.decodingError(error)
        }
    }
}

enum LostItemGuideError: Error, LocalizedError {
    case invalidURL
    case networkError(Error)
    case serverError(Int, String)
    case decodingError(Error)

    var errorDescription: String? {
        switch self {
        case .invalidURL:
            return "잘못된 URL입니다."
        case .networkError(let error):
            return "네트워크 오류: \(error.localizedDescription)"
        case .serverError(let code, let message):
            return "서버 오류 (코드: \(code)): \(message)"
        case .decodingError(let error):
            return "데이터 디코딩 오류: \(error.localizedDescription)"
        }
    }
}
