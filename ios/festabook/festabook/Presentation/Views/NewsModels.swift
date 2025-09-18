import Foundation

struct Announcement: Codable, Identifiable, Equatable {
    let announcementId: Int
    let title: String
    let content: String
    let isPinned: Bool
    let createdAt: String

    var id: Int { announcementId }

    var displayTime: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
        if let date = formatter.date(from: createdAt) {
            let displayFormatter = DateFormatter()
            displayFormatter.dateFormat = "MM/dd HH:mm"
            return displayFormatter.string(from: date)
        }
        return ""
    }
}

struct AnnouncementsResponse: Codable {
    let pinned: [Announcement]
    let unpinned: [Announcement]
}
