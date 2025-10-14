//
//  Models.swift
//  festabook
//
//  Created by 이소은 on 9/14/25.
//

import Foundation

struct EventDate: Codable, Identifiable {
    let eventDateId: Int
    let date: String

    var id: Int { eventDateId }

    var displayDate: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        if let date = formatter.date(from: date) {
            formatter.dateFormat = "M/d(E)"
            formatter.locale = Locale(identifier: "ko_KR")
            return formatter.string(from: date)
        }
        return date
    }
}

struct ScheduleEvent: Codable, Identifiable {
    let eventId: Int
    let status: EventStatus
    let startTime: String
    let endTime: String
    let title: String
    let location: String

    var id: Int { eventId }

    var timeRange: String {
        return "\(startTime) - \(endTime)"
    }
}

enum EventStatus: String, Codable {
    case completed = "COMPLETED"
    case ongoing = "ONGOING"
    case upcoming = "UPCOMING"

    var displayText: String {
        switch self {
        case .completed: return "종료"
        case .ongoing: return "진행중"
        case .upcoming: return "예정"
        }
    }

    var color: String {
        switch self {
        case .completed: return "gray"
        case .ongoing: return "blue"
        case .upcoming: return "green"
        }
    }
}

