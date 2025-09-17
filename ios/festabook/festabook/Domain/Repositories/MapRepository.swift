import Foundation

protocol MapRepositoryProtocol {
    func fetchGeography() async throws -> GeographyResponse
    func fetchPlaceGeographies() async throws -> [PlaceGeography]
    func fetchPlacePreviews() async throws -> [PlacePreview]
    func fetchPlaceDetail(_ placeId: Int) async throws -> PlaceDetail
}

class MapRepository: MapRepositoryProtocol {
    private let apiClient: APIClient

    init(apiClient: APIClient = .shared) {
        self.apiClient = apiClient
    }

    func fetchGeography() async throws -> GeographyResponse {
        return try await apiClient.get("/festivals/geography")
    }

    func fetchPlaceGeographies() async throws -> [PlaceGeography] {
        return try await apiClient.get("/places/geographies")
    }

    func fetchPlacePreviews() async throws -> [PlacePreview] {
        return try await apiClient.get("/places/previews")
    }

    func fetchPlaceDetail(_ placeId: Int) async throws -> PlaceDetail {
        return try await apiClient.get("/places/\(placeId)")
    }
}