import Foundation

struct University: Codable, Identifiable, Hashable {
    let id: Int
    let name: String
    let latitude: Double?
    let longitude: Double?
}
