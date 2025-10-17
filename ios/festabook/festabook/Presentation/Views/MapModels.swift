import Foundation
import UIKit
import SwiftUI

// MARK: - Core Data Models

struct LatLng: Codable {
    let latitude: Double
    let longitude: Double
}

// MARK: - Geography API Response

struct GeographyResponse: Codable {
    let zoom: Int
    let centerCoordinate: LatLng
    let polygonHoleBoundary: [LatLng]

    /// 화면 표시용 조정된 중심 좌표 (줌에 따른 선형 가중치 적용)
    var adjustedCenterCoordinate: LatLng {
        // 줌 19 기준 기준값
        let baseLat = 0.00018
        let baseLng = 0.00006

        // 줌값별 가중치: 19는 1.0 유지, 축소(작은 줌)일수록 크게, 확대(큰 줌)일수록 작게
        let scale: Double
        switch zoom {
        case Int.min..<15: scale = 14.0
        case 15:           scale = 10.0
        case 16:           scale = 7.0
        case 17:           scale = 4.0
        case 18:           scale = 2.0
        case 19:           scale = 1.0
        case 20:           scale = 0.8
        case 21:           scale = 0.6
        default:           scale = 0.2 // 22 이상
        }

        let horizontalDamping = 0.28         // 0.25~0.5 내에서 조정 권장
        let lngScale = scale * horizontalDamping

        return LatLng(
            latitude: centerCoordinate.latitude - baseLat * scale,
            longitude: centerCoordinate.longitude + baseLng * lngScale
        )
    }
}

// MARK: - Place Geography API Response

struct PlaceGeography: Codable, Identifiable {
    let placeId: Int
    let category: String
    let markerCoordinate: LatLng?
    let title: String
    let coordinate: LatLng?
    let description: String?
    let location: String?
    let host: String?
    let startTime: String?
    let endTime: String?
    let timeTags: [TimeTag]?    // 지오메트리 응답에도 포함되는 타임태그

    var id: Int { placeId }

    // 안전한 좌표 접근
    var safeCoordinate: LatLng {
        return markerCoordinate ?? coordinate ?? LatLng(latitude: 37.5665, longitude: 126.9780)
    }
}

// MARK: - Time Tag API Response

struct TimeTag: Codable, Identifiable, Hashable {
    let timeTagId: Int
    let name: String

    var id: Int { timeTagId }

    enum CodingKeys: String, CodingKey {
        case timeTagId
        case name
    }
}

// MARK: - Place Preview API Response

struct PlacePreview: Codable, Identifiable {
    let placeId: Int
    let imageUrl: String?
    let category: String
    let title: String
    let description: String?
    let location: String?
    let host: String?
    let startTime: String?
    let endTime: String?
    let coordinate: LatLng?
    let timeTags: [TimeTag]?

    var id: Int { placeId }

    // UI 표시용 안전한 텍스트
    var safeDescription: String {
        return description ?? "설명이 없습니다"
    }

    var safeLocation: String {
        return location ?? "위치 정보 없음"
    }

    var safeHost: String {
        // 카테고리별 기본값 설정
        if let host = host, !host.isEmpty {
            return host
        }
        
        switch category {
        case "BAR":
            return "학생주점 운영위원회"
        case "BOOTH":
            return "축제 운영위원회"
        case "FOOD_TRUCK":
            return "푸드트럭 업체"
        case "PHOTO_BOOTH":
            return "포토부스 운영진"
        default:
            return "주최자 정보 없음"
        }
    }

    var safeTimeInfo: String {
        // 카테고리별 기본 운영시간 설정
        if let start = startTime, let end = endTime, !start.isEmpty, !end.isEmpty {
            return "\(start) ~ \(end)"
        } else if let start = startTime, !start.isEmpty {
            return "\(start) ~"
        } else if let end = endTime, !end.isEmpty {
            return "~ \(end)"
        } else {
            // null이거나 빈 문자열인 경우 카테고리별 기본값
            switch category {
            case "BAR":
                return "16:00 ~ 23:00"
            case "BOOTH":
                return "10:00 ~ 18:00"
            case "FOOD_TRUCK":
                return "11:00 ~ 20:00"
            case "PHOTO_BOOTH":
                return "10:00 ~ 22:00"
            default:
                return "시간 미정"
            }
        }
    }

    var resolvedImageURL: String? {
        ImageURLResolver.resolve(imageUrl)
    }
}


// MARK: - Place Detail API Response

struct PlaceDetail: Codable, Identifiable {
    let placeId: Int
    let imageUrl: String?
    let placeImages: [PlaceImage]?
    let category: String
    let title: String
    let description: String?
    let location: String?
    let host: String?
    let startTime: String?
    let endTime: String?
    let coordinate: LatLng?

    var id: Int { placeId }

    var orderedImageUrls: [String] {
        if let placeImages, !placeImages.isEmpty {
            let urls = placeImages
                .sorted { $0.sequence < $1.sequence }
                .compactMap { ImageURLResolver.resolve($0.imageUrl) }
            if !urls.isEmpty { return urls }
        }

        if let fallback = ImageURLResolver.resolve(imageUrl) {
            return [fallback]
        }

        return []
    }

    // UI 표시용 안전한 텍스트
    var safeDescription: String {
        return description ?? "설명이 없습니다"
    }

    var safeLocation: String {
        return location ?? "위치 정보 없음"
    }

    var safeHost: String {
        // 카테고리별 기본값 설정
        if let host = host, !host.isEmpty {
            return host
        }

        switch category {
        case "BAR":
            return "학생주점 운영위원회"
        case "BOOTH":
            return "축제 운영위원회"
        case "FOOD_TRUCK":
            return "푸드트럭 업체"
        case "PHOTO_BOOTH":
            return "포토부스 운영진"
        default:
            return "주최자 정보 없음"
        }
    }

    var safeTimeInfo: String {
        // 카테고리별 기본 운영시간 설정
        if let start = startTime, let end = endTime, !start.isEmpty, !end.isEmpty {
            return "\(start) ~ \(end)"
        } else if let start = startTime, !start.isEmpty {
            return "\(start) ~"
        } else if let end = endTime, !end.isEmpty {
            return "~ \(end)"
        } else {
            // null이거나 빈 문자열인 경우 카테고리별 기본값
            switch category {
            case "BAR":
                return "16:00 ~ 23:00"
            case "BOOTH":
                return "10:00 ~ 18:00"
            case "FOOD_TRUCK":
                return "11:00 ~ 20:00"
            case "PHOTO_BOOTH":
                return "10:00 ~ 22:00"
            default:
                return "시간 미정"
            }
        }
    }
}

struct PlaceImage: Codable, Identifiable {
    let id: Int
    let imageUrl: String
    let sequence: Int
}

extension PlaceDetail: Hashable {
    static func == (lhs: PlaceDetail, rhs: PlaceDetail) -> Bool {
        lhs.placeId == rhs.placeId
    }

    func hash(into hasher: inout Hasher) {
        hasher.combine(placeId)
    }
}

// MARK: - Map Categories

enum MapCategory: String, CaseIterable {
    case all = "전체"
    case bar = "BAR"
    case foodTruck = "FOOD_TRUCK"
    case booth = "BOOTH"
    case stage = "STAGE"
    case photobooth = "PHOTO_BOOTH"
    case primary = "PRIMARY"
    case extra = "EXTRA"
    case parking = "PARKING"
    case toilet = "TOILET"
    case smoking = "SMOKING"
    case trashCan = "TRASH_CAN"

    var displayName: String {
        switch self {
        case .all: return "전체"
        case .bar: return "주점"
        case .foodTruck: return "푸드트럭"
        case .booth: return "부스"
        case .stage: return "무대"
        case .toilet: return "화장실"
        case .trashCan: return "쓰레기통"
        case .parking: return "주차장"
        case .primary: return "주요시설"
        case .smoking: return "흡연구역"
        case .photobooth: return "포토부스"
        case .extra: return "기타"
        }
    }

    var iconName: String {
        switch self {
        case .all: return "marker_primary"
        case .bar: return "marker_bar"
        case .foodTruck: return "marker_foodtruck"
        case .booth: return "marker_booth"
        case .stage: return "marker_stage"
        case .toilet: return "marker_toilet"
        case .trashCan: return "marker_trash"
        case .parking: return "marker_parking"
        case .primary: return "marker_primary"
        case .smoking: return "marker_smoking"
        case .photobooth: return "marker_photobooth"
        case .extra: return "marker_extra"
        }
    }

    var filterIconName: String {
        switch self {
        case .all: return "filter_all"
        case .bar: return "filter_bar"
        case .foodTruck: return "filter_foodtruck"
        case .booth: return "filter_booth"
        case .stage: return "filter_stage"
        case .toilet: return "filter_toilet"
        case .trashCan: return "filter_trash"
        case .parking: return "filter_parking"
        case .primary: return "filter_primary"
        case .smoking: return "filter_smoking"
        case .photobooth: return "filter_photobooth"
        case .extra: return "filter_extra"
        }
    }

    var selectedIconName: String {
        return "\(iconName)_selected"
    }

    // 카테고리별 상세 표시 여부 (주점/부스/푸드트럭/포토부스는 카드 형태)
    var showsDetailedCard: Bool {
        switch self {
        case .bar, .booth, .foodTruck, .photobooth:
            return true
        default:
            return false
        }
    }

    // 전체 카테고리는 아이콘이 없음
    var hasIcon: Bool {
        return self != .all
    }
}

// MARK: - Bottom Sheet States

enum SheetDetent: String, CaseIterable {
    case collapsed
    case small      // 아이템 2개 정도 보이는 높이 (새 추가)
    case medium
    case large

    var height: CGFloat {
        let window = UIApplication.shared.windows.first
        let screenHeight = window?.screen.bounds.height ?? UIScreen.main.bounds.height
        let safeAreaInsets = window?.safeAreaInsets ?? .zero
        let usableHeight = screenHeight - safeAreaInsets.top - safeAreaInsets.bottom 
        
        switch self {
        case .collapsed:
            return usableHeight * 0.12
        case .small:
            return usableHeight * 0.28
        case .medium:
            return usableHeight * 0.47
        case .large:
            return usableHeight * 0.85
        }
    }

    var topSpacing: CGFloat {
        switch self {
        case .collapsed: return 0
        case .small: return 16
        case .medium: return 32
        case .large: return 42
        }
    }

    var totalHeight: CGFloat {
        height + topSpacing
    }
}

// MARK: - SwiftUI Extensions

extension View {
    var screenWidth: CGFloat { UIScreen.main.bounds.width }
    var screenHeight: CGFloat { UIScreen.main.bounds.height }
}
