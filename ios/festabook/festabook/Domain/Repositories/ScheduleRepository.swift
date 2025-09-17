import Foundation

protocol ScheduleRepository {
    func getEventDates() async throws -> [EventDate]
    func getEvents(for eventDateId: Int) async throws -> [ScheduleEvent]
}

struct ScheduleRepositoryLive: ScheduleRepository {
    let api: APIClient

    func getEventDates() async throws -> [EventDate] {
        return try await api.get("/event-dates")
    }

    func getEvents(for eventDateId: Int) async throws -> [ScheduleEvent] {
        return try await api.get("/event-dates/\(eventDateId)/events")
    }
}