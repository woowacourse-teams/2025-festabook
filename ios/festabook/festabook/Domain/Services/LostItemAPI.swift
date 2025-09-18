import Foundation

enum LostItemError: LocalizedError {
    case network(Error)
    case decoding(Error)
    case server(Int, Data?)
    case empty
    case invalidURL

    var errorDescription: String? {
        switch self {
        case .network(let error):
            return "네트워크 오류: \(error.localizedDescription)"
        case .decoding(let error):
            return "데이터 파싱 오류: \(error.localizedDescription)"
        case .server(let code, _):
            return "서버 오류 (코드: \(code))"
        case .empty:
            return "데이터가 없습니다"
        case .invalidURL:
            return "잘못된 URL입니다"
        }
    }
}

protocol LostItemServicing {
    func fetchLostItems() async throws -> [LostItem]
}

final class LostItemAPI: LostItemServicing {
    private let apiClient: APIClient

    init(apiClient: APIClient = .shared) {
        self.apiClient = apiClient
    }

    func fetchLostItems() async throws -> [LostItem] {
        do {
            print("[LostItemAPI] Fetching lost items...")
            // 서버 스펙: GET /lost-items, 헤더 festival: {id}
            // Base URL이 이미 "/api"를 포함하므로 여기서는 "/lost-items"만 전달
            let allItems: [LostItem] = try await apiClient.get(Endpoints.News.lostItems)
            print("[LostItemAPI] Successfully fetched \(allItems.count) total lost items")

            // PENDING 상태만 필터링하고 최신순 정렬
            let pendingItems = allItems
                .filter { $0.pickupStatus == "PENDING" }
                .sorted { $0.createdAt > $1.createdAt }

            print("[LostItemAPI] Filtered to \(pendingItems.count) pending items")

            // 빈 배열이어도 에러가 아닌 정상 상태로 처리
            return pendingItems

        } catch let httpError as HTTPError {
            print("[LostItemAPI] HTTP Error: \(httpError)")
            switch httpError {
            case .invalidURL:
                throw LostItemError.invalidURL
            case .server(let code, let data):
                throw LostItemError.server(code, data)
            case .transport(let error):
                throw LostItemError.network(error)
            case .decoding(let error):
                throw LostItemError.decoding(error)
            }
        } catch let decodingError as DecodingError {
            print("[LostItemAPI] Decoding error: \(decodingError)")
            throw LostItemError.decoding(decodingError)
        } catch {
            print("[LostItemAPI] Unknown error: \(error)")
            throw LostItemError.network(error)
        }
    }
}
