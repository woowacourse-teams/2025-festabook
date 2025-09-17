import Foundation

enum HTTPError: Error, LocalizedError {
    case invalidURL
    case transport(Error)
    case server(Int, Data?)
    case decoding(Error)

    var errorDescription: String? {
        switch self {
        case .invalidURL: return "잘못된 URL"
        case .transport(let e): return "네트워크 오류: \(e.localizedDescription)"
        case .server(let code, _): return "서버 오류(\(code))"
        case .decoding: return "응답 파싱 실패"
        }
    }
}
