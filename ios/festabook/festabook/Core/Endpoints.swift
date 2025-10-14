import Foundation

enum Endpoints {
    enum Festivals {
        static let universities = "/festivals/universities"
        static let detail = "/festivals"
        static let lineups = "/lineups"
        static let geography = "/festivals/geography"
    }

    enum Schedule {
        static let dates = "/event-dates"
        static func events(for eventDateId: Int) -> String {
            "\(dates)/\(eventDateId)/events"
        }
    }

    enum Places {
        static let geographies = "/places/geographies"
        static let previews = "/places/previews"
        static func detail(_ placeId: Int) -> String {
            "/places/\(placeId)"
        }
    }

    enum Devices {
        static let register = "/devices"
        static func detail(_ deviceId: Int) -> String {
            "\(register)/\(deviceId)"
        }
    }

    enum Notifications {
        static func subscribe(festivalId: Int) -> String {
            "\(Festivals.detail)/\(festivalId)/notifications/ios"
        }

        static func subscription(_ notificationId: Int) -> String {
            "/festivals/notifications/\(notificationId)"
        }
    }

    enum News {
        static let announcements = "/announcements"
        static func announcementDetail(_ id: Int) -> String {
            "\(announcements)/\(id)"
        }
        static let faqs = "/questions"
        static let lostItems = "/lost-items"
    }
}
