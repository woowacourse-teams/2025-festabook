import Foundation

protocol MapRepositoryProtocol {
    func fetchGeography() async throws -> GeographyResponse
    func fetchPlaceGeographies() async throws -> [PlaceGeography]
    func fetchPlacePreviews() async throws -> [PlacePreview]
    func fetchPlaceDetail(_ placeId: Int) async throws -> PlaceDetail
    func fetchTimeTags() async throws -> [TimeTag]
}

class MapRepository: MapRepositoryProtocol {
    private let apiClient: APIClient

    init(apiClient: APIClient = .shared) {
        self.apiClient = apiClient
    }

    func fetchGeography() async throws -> GeographyResponse {
        return try await apiClient.get(Endpoints.Festivals.geography)
    }

    func fetchPlaceGeographies() async throws -> [PlaceGeography] {
        return try await apiClient.get(Endpoints.Places.geographies)
    }

    func fetchPlacePreviews() async throws -> [PlacePreview] {
        return try await apiClient.get(Endpoints.Places.previews)
    }

    func fetchPlaceDetail(_ placeId: Int) async throws -> PlaceDetail {
        return try await apiClient.get(Endpoints.Places.detail(placeId))
    }

    func fetchTimeTags() async throws -> [TimeTag] {
        return try await apiClient.get(Endpoints.TimeTags.list)
    }
}
