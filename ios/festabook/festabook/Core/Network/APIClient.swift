import Foundation

class APIClient {
    static let shared = APIClient()

    var session: URLSession = {
        // 전역 URLCache 구성 (브라우저 유사 캐싱)
        let cache = URLCache(
            memoryCapacity: 50 * 1024 * 1024,
            diskCapacity: 200 * 1024 * 1024,
            directory: nil
        )
        URLCache.shared = cache

        let config = URLSessionConfiguration.default
        config.urlCache = cache
        // 서버 Cache-Control/ETag를 우선 활용하고, 캐시가 있으면 재사용
        config.requestCachePolicy = .returnCacheDataElseLoad
        // 압축 응답 허용
        config.httpAdditionalHeaders = [
            "Accept-Encoding": "gzip, deflate, br",
            "Accept": "application/json"
        ]
        return URLSession(configuration: config)
    }()
    private var _currentFestivalId: Int = 0

    init() {}

    var currentFestivalId: Int {
        get { _currentFestivalId }
        set { _currentFestivalId = newValue }
    }

    func updateFestivalId(_ festivalId: Int) {
        _currentFestivalId = festivalId
        print("[APIClient] Festival ID updated to: \(festivalId)")
    }

    // 앱 시작 시 UserDefaults에 저장된 값이 있으면 반영
    func bootstrapFestivalIdFromStorage() {
        let stored = UserDefaults.standard.object(forKey: "currentFestivalId") as? Int
        if let stored, stored > 0 {
            _currentFestivalId = stored
            print("[APIClient] Bootstrapped Festival ID from storage: \(stored)")
        } else {
            _currentFestivalId = 0
            print("[APIClient] No stored festival ID found during bootstrap")
        }
    }

    func get<T: Decodable>(_ path: String, query: [URLQueryItem] = [], requiresFestivalId: Bool = true) async throws -> T {
        var comps = URLComponents(url: BuildConfig.apiBaseURL, resolvingAgainstBaseURL: false)!
        comps.path += path
        comps.queryItems = query.isEmpty ? nil : query
        guard let url = comps.url else { throw HTTPError.invalidURL }

        var req = URLRequest(url: url)
        req.httpMethod = "GET"
        req.setValue("application/json", forHTTPHeaderField: "Accept")
        // 서버 정책 존중: 기본은 useProtocolCachePolicy (세션 기본 설정으로도 동작)
        req.cachePolicy = .useProtocolCachePolicy

        if requiresFestivalId {
            // 최신 festivalId를 저장소에서 동기화 (단일 소스: UserDefaults)
            let storedFestivalId = UserDefaults.standard.object(forKey: "currentFestivalId") as? Int
            if let storedFestivalId, storedFestivalId > 0, storedFestivalId != currentFestivalId {
                _currentFestivalId = storedFestivalId
                print("[APIClient] Synced Festival ID from storage: \(storedFestivalId)")
            }

            // 축제 ID 검증: 0 또는 음수면 요청 금지
            guard currentFestivalId > 0 else {
                throw HTTPError.transport(NSError(domain: "APIClient", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid festivalId: \(currentFestivalId)"]))
            }
            req.setValue("\(currentFestivalId)", forHTTPHeaderField: "festival")
        }

        print("[APIClient] Request URL: \(url)")
        if requiresFestivalId {
            print("[APIClient] Festival Header: \(currentFestivalId)")
        }
        print("[APIClient] All Headers: \(req.allHTTPHeaderFields ?? [:])")
        // 공통 헤더: x-request-id 등 필요 시 추가

        do {
            let (data, resp) = try await session.data(for: req)
            guard let http = resp as? HTTPURLResponse else { throw HTTPError.transport(URLError(.badServerResponse)) }
            
            print("[APIClient] Response Status: \(http.statusCode)")
            if let responseData = String(data: data, encoding: .utf8) {
                print("[APIClient] Response Data: \(responseData)")
            }
            
            guard (200..<300).contains(http.statusCode) else { 
                print("[APIClient] Error Status: \(http.statusCode), Data: \(String(data: data, encoding: .utf8) ?? "nil")")
                throw HTTPError.server(http.statusCode, data) 
            }
            let result: T = try APIClient.jsonDecoder.decode(T.self, from: data)
            print("[APIClient] Successfully decoded response")
            return result
        } catch let e as HTTPError { throw e }
          catch { throw HTTPError.transport(error) }
    }
    
    // MARK: - POST 요청
    func post<T: Decodable, U: Encodable>(endpoint: String, body: U) async throws -> T {
        var comps = URLComponents(url: BuildConfig.apiBaseURL, resolvingAgainstBaseURL: false)!
        comps.path += endpoint
        guard let url = comps.url else { throw HTTPError.invalidURL }
        
        // 최신 festivalId 동기화
        let storedFestivalId = UserDefaults.standard.object(forKey: "currentFestivalId") as? Int
        if let storedFestivalId, storedFestivalId > 0, storedFestivalId != currentFestivalId {
            _currentFestivalId = storedFestivalId
            print("[APIClient] Synced Festival ID from storage: \(storedFestivalId)")
        }

        var req = URLRequest(url: url)
        req.httpMethod = "POST"
        req.setValue("application/json", forHTTPHeaderField: "Content-Type")
        req.setValue("application/json", forHTTPHeaderField: "Accept")
        req.setValue("\(currentFestivalId)", forHTTPHeaderField: "festival")
        
        do {
            req.httpBody = try JSONEncoder().encode(body)
        } catch {
            throw HTTPError.transport(error)
        }
        
        print("[APIClient] POST Request URL: \(url)")
        print("[APIClient] POST Festival Header: \(currentFestivalId)")
        
        do {
            let (data, resp) = try await session.data(for: req)
            guard let http = resp as? HTTPURLResponse else { throw HTTPError.transport(URLError(.badServerResponse)) }
            
            print("[APIClient] POST Response Status: \(http.statusCode)")
            
            guard (200..<300).contains(http.statusCode) else {
                throw HTTPError.server(http.statusCode, data)
            }
            
            let result: T = try APIClient.jsonDecoder.decode(T.self, from: data)
            return result
        } catch let e as HTTPError { throw e }
          catch { throw HTTPError.transport(error) }
    }
    
    // MARK: - DELETE 요청
    func delete(endpoint: String) async throws {
        var comps = URLComponents(url: BuildConfig.apiBaseURL, resolvingAgainstBaseURL: false)!
        comps.path += endpoint
        guard let url = comps.url else { throw HTTPError.invalidURL }
        
        // 최신 festivalId 동기화
        let storedFestivalId = UserDefaults.standard.object(forKey: "currentFestivalId") as? Int
        if let storedFestivalId, storedFestivalId > 0, storedFestivalId != currentFestivalId {
            _currentFestivalId = storedFestivalId
            print("[APIClient] Synced Festival ID from storage: \(storedFestivalId)")
        }

        var req = URLRequest(url: url)
        req.httpMethod = "DELETE"
        req.setValue("application/json", forHTTPHeaderField: "Accept")
        req.setValue("\(currentFestivalId)", forHTTPHeaderField: "festival")
        
        print("[APIClient] DELETE Request URL: \(url)")
        print("[APIClient] DELETE Festival Header: \(currentFestivalId)")
        
        do {
            let (_, resp) = try await session.data(for: req)
            guard let http = resp as? HTTPURLResponse else { throw HTTPError.transport(URLError(.badServerResponse)) }
            
            print("[APIClient] DELETE Response Status: \(http.statusCode)")
            
            guard (200..<300).contains(http.statusCode) else {
                throw HTTPError.server(http.statusCode, Data())
            }
        } catch let e as HTTPError { throw e }
          catch { throw HTTPError.transport(error) }
    }

    // MARK: - Notification API methods (without festival header)
    func postNotification<T: Decodable, U: Encodable>(endpoint: String, body: U) async throws -> T {
        var comps = URLComponents(url: BuildConfig.apiBaseURL, resolvingAgainstBaseURL: false)!
        comps.path += endpoint
        guard let url = comps.url else { throw HTTPError.invalidURL }

        var req = URLRequest(url: url)
        req.httpMethod = "POST"
        req.setValue("application/json", forHTTPHeaderField: "Content-Type")
        req.setValue("application/json", forHTTPHeaderField: "Accept")
        // NO festival header for notification APIs

        do {
            req.httpBody = try JSONEncoder().encode(body)
        } catch {
            throw HTTPError.transport(error)
        }

        print("[APIClient] Notification POST URL: \(url)")
        print("[APIClient] Headers: \(req.allHTTPHeaderFields ?? [:])")

        do {
            let (data, resp) = try await session.data(for: req)
            guard let http = resp as? HTTPURLResponse else { throw HTTPError.transport(URLError(.badServerResponse)) }

            print("[APIClient] Notification POST Response Status: \(http.statusCode)")

            // Check for 201 Created specifically for notification subscription
            guard http.statusCode == 201 else {
                print("[APIClient] Expected 201 Created, got: \(http.statusCode)")
                throw HTTPError.server(http.statusCode, data)
            }

            let result: T = try APIClient.jsonDecoder.decode(T.self, from: data)
            return result
        } catch let e as HTTPError { throw e }
          catch { throw HTTPError.transport(error) }
    }

    func getNotification<T: Decodable>(endpoint: String) async throws -> T {
        var comps = URLComponents(url: BuildConfig.apiBaseURL, resolvingAgainstBaseURL: false)!
        comps.path += endpoint
        guard let url = comps.url else { throw HTTPError.invalidURL }

        var req = URLRequest(url: url)
        req.httpMethod = "GET"
        req.setValue("application/json", forHTTPHeaderField: "Accept")

        do {
            let (data, resp) = try await session.data(for: req)
            guard let http = resp as? HTTPURLResponse else { throw HTTPError.transport(URLError(.badServerResponse)) }

            print("[APIClient] Notification GET Response Status: \(http.statusCode)")

            guard (200..<300).contains(http.statusCode) else {
                throw HTTPError.server(http.statusCode, data)
            }

            let result: T = try APIClient.jsonDecoder.decode(T.self, from: data)
            return result
        } catch let e as HTTPError { throw e }
          catch { throw HTTPError.transport(error) }
    }

    // MARK: - Device API methods (without festival header)
    func postDevice<T: Decodable, U: Encodable>(endpoint: String, body: U) async throws -> T {
        var comps = URLComponents(url: BuildConfig.apiBaseURL, resolvingAgainstBaseURL: false)!
        comps.path += endpoint
        guard let url = comps.url else { throw HTTPError.invalidURL }

        var req = URLRequest(url: url)
        req.httpMethod = "POST"
        req.setValue("application/json", forHTTPHeaderField: "Content-Type")
        req.setValue("application/json", forHTTPHeaderField: "Accept")
        // NO festival header for device APIs

        do {
            req.httpBody = try JSONEncoder().encode(body)
        } catch {
            throw HTTPError.transport(error)
        }

        print("[APIClient] Device POST URL: \(url)")
        print("[APIClient] Headers: \(req.allHTTPHeaderFields ?? [:])")

        do {
            let (data, resp) = try await session.data(for: req)
            guard let http = resp as? HTTPURLResponse else { throw HTTPError.transport(URLError(.badServerResponse)) }

            print("[APIClient] Device POST Response Status: \(http.statusCode)")

            guard (200..<300).contains(http.statusCode) else {
                print("[APIClient] Device API Error - Status: \(http.statusCode)")
                throw HTTPError.server(http.statusCode, data)
            }

            let result: T = try APIClient.jsonDecoder.decode(T.self, from: data)
            return result
        } catch let e as HTTPError { throw e }
          catch { throw HTTPError.transport(error) }
    }

    func patchDevice<U: Encodable>(endpoint: String, body: U) async throws {
        var comps = URLComponents(url: BuildConfig.apiBaseURL, resolvingAgainstBaseURL: false)!
        comps.path += endpoint
        guard let url = comps.url else { throw HTTPError.invalidURL }

        var req = URLRequest(url: url)
        req.httpMethod = "PATCH"
        req.setValue("application/json", forHTTPHeaderField: "Content-Type")
        req.setValue("application/json", forHTTPHeaderField: "Accept")

        do {
            req.httpBody = try JSONEncoder().encode(body)
        } catch {
            throw HTTPError.transport(error)
        }

        print("[APIClient] Device PATCH URL: \(url)")
        print("[APIClient] Headers: \(req.allHTTPHeaderFields ?? [:])")

        do {
            let (data, resp) = try await session.data(for: req)
            guard let http = resp as? HTTPURLResponse else { throw HTTPError.transport(URLError(.badServerResponse)) }

            print("[APIClient] Device PATCH Response Status: \(http.statusCode)")

            guard (200..<300).contains(http.statusCode) else {
                print("[APIClient] Device PATCH Error - Status: \(http.statusCode)")
                throw HTTPError.server(http.statusCode, data)
            }
        } catch let e as HTTPError { throw e }
          catch { throw HTTPError.transport(error) }
    }

    func deleteNotification(endpoint: String) async throws {
        var comps = URLComponents(url: BuildConfig.apiBaseURL, resolvingAgainstBaseURL: false)!
        comps.path += endpoint
        guard let url = comps.url else { throw HTTPError.invalidURL }

        var req = URLRequest(url: url)
        req.httpMethod = "DELETE"
        req.setValue("application/json", forHTTPHeaderField: "Accept")
        // NO festival header for notification APIs

        print("[APIClient] Notification DELETE URL: \(url)")
        print("[APIClient] Headers: \(req.allHTTPHeaderFields ?? [:])")

        do {
            let (_, resp) = try await session.data(for: req)
            guard let http = resp as? HTTPURLResponse else { throw HTTPError.transport(URLError(.badServerResponse)) }

            print("[APIClient] Notification DELETE Response Status: \(http.statusCode)")

            // Check for 204 No Content specifically for notification unsubscription
            guard http.statusCode == 204 else {
                print("[APIClient] Expected 204 No Content, got: \(http.statusCode)")
                throw HTTPError.server(http.statusCode, Data())
            }
        } catch let e as HTTPError { throw e }
          catch { throw HTTPError.transport(error) }
    }
}

// MARK: - JSONDecoder with flexible ISO8601 fractional seconds
extension APIClient {
    /// 공용 JSONDecoder: ISO8601(소수점 0~6자리)와 'Z' 유무를 유연하게 처리
    static let jsonDecoder: JSONDecoder = {
        let decoder = JSONDecoder()
        decoder.dateDecodingStrategy = .custom { decoder in
            let container = try decoder.singleValueContainer()
            let raw = try container.decode(String.self)

            // 1) ISO8601 + fractional seconds 시도 (withFractionalSeconds)
            if let date = ISO8601DateFormatter.fractionalShared.date(from: raw) {
                return date
            }
            if let date = ISO8601DateFormatter.standardShared.date(from: raw) {
                return date
            }

            // 2) 'yyyy-MM-dd'T'HH:mm:ss.SSSSSS' (최대 6자리) 수용: 자리수 보정
            if let adjusted = FlexibleDateParserShared.adjustToSixFractionDigits(raw),
               let date = FlexibleDateParserShared.formatter.date(from: adjusted) {
                return date
            }

            throw DecodingError.dataCorrupted(
                .init(codingPath: decoder.codingPath,
                      debugDescription: "Invalid date format: \(raw)")
            )
        }
        return decoder
    }()
}

private enum FlexibleDateParser {
    /// KST/UTC 명시 없는 문자열을 UTC 기준으로 파싱 후, 표시 시점에 현지화합니다.
    static let formatter: DateFormatter = {
        let f = DateFormatter()
        f.locale = Locale(identifier: "en_US_POSIX")
        f.timeZone = TimeZone(secondsFromGMT: 0)
        f.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
        return f
    }()

    static func adjustToSixFractionDigits(_ raw: String) -> String? {
        // raw: 2025-09-16T15:57:44.81871  (소수점 5자리, 타임존 표기 없음)
        guard let dotRange = raw.range(of: "." ) else { return raw }
        let head = String(raw[..<dotRange.lowerBound])
        var fraction = String(raw[dotRange.upperBound...])
        // 타임존(Z 또는 +09:00 등) 제거
        if let tzStart = fraction.firstIndex(where: { !$0.isNumber }) {
            fraction = String(fraction[..<tzStart])
        }
        if fraction.count == 6 { return head + "." + fraction }
        if fraction.count < 6 { return head + "." + fraction.padding(toLength: 6, withPad: "0", startingAt: 0) }
        // 6자리 초과 시 절단
        let truncated = String(fraction.prefix(6))
        return head + "." + truncated
    }
}

private extension ISO8601DateFormatter {
    static let standard: ISO8601DateFormatter = {
        let f = ISO8601DateFormatter()
        f.formatOptions = [.withInternetDateTime]
        return f
    }()

    static let fractional: ISO8601DateFormatter = {
        let f = ISO8601DateFormatter()
        f.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        return f
    }()
}
