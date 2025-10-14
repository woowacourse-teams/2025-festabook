import Foundation

struct LostItem: Decodable, Identifiable {
    let lostItemId: Int
    let imageUrl: String
    let storageLocation: String
    let pickupStatus: String
    let createdAt: Date

    var id: Int { lostItemId }

    enum CodingKeys: String, CodingKey {
        case lostItemId, imageUrl, storageLocation, pickupStatus, createdAt
    }

    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)

        lostItemId = try container.decode(Int.self, forKey: .lostItemId)
        imageUrl = try container.decode(String.self, forKey: .imageUrl)
        storageLocation = try container.decode(String.self, forKey: .storageLocation)
        pickupStatus = try container.decode(String.self, forKey: .pickupStatus)

        // 날짜는 APIClient.jsonDecoder의 전략으로 파싱됨. 
        // 여기서는 문자열을 직접 읽어 공용 디코더 전략을 다시 활용한다.
        let dateString = try container.decode(String.self, forKey: .createdAt)
        if let date = ISO8601DateFormatter.fractionalShared.date(from: dateString)
            ?? ISO8601DateFormatter.standardShared.date(from: dateString)
            ?? FlexibleDateParserShared.formatter.date(from: FlexibleDateParserShared.adjustToSixFractionDigits(dateString) ?? dateString) {
            createdAt = date
        } else {
            throw DecodingError.dataCorruptedError(
                forKey: .createdAt,
                in: container,
                debugDescription: "Invalid date format: \(dateString)"
            )
        }
    }

    var formattedCreatedAt: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy.MM.dd HH:mm"
        formatter.locale = Locale(identifier: "ko_KR")
        formatter.timeZone = TimeZone(identifier: "Asia/Seoul")
        return formatter.string(from: createdAt)
    }

    // API가 "/images/.." 상대 경로를 줄 수 있어 절대 URL 문자열로 변환
    var imageAbsoluteURLString: String {
        return ImageURLResolver.resolve(imageUrl) ?? ""
    }
}

extension LostItem {
    enum PickupStatus: String, CaseIterable {
        case pending = "PENDING"
        case returned = "RETURNED"

        var displayName: String {
            switch self {
            case .pending: return "보관중"
            case .returned: return "반환완료"
            }
        }
    }

    var pickupStatusEnum: PickupStatus {
        return PickupStatus(rawValue: pickupStatus) ?? .pending
    }

}