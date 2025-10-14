import Foundation

protocol ScheduleRepository {
    func getEventDates() async throws -> [EventDate]
    func getEvents(for eventDateId: Int) async throws -> [ScheduleEvent]
    func getTimeline() async throws -> [EventDateTimelineResponse]
}

struct ScheduleRepositoryLive: ScheduleRepository {
    let api: APIClient

    func getEventDates() async throws -> [EventDate] {
        return try await api.get(Endpoints.Schedule.dates)
    }

    func getEvents(for eventDateId: Int) async throws -> [ScheduleEvent] {
        return try await api.get(Endpoints.Schedule.events(for: eventDateId))
    }

    func getTimeline() async throws -> [EventDateTimelineResponse] {
        let dates = try await getEventDates()

        guard !dates.isEmpty else { return [] }

        var timeline: [EventDateTimelineResponse] = []
        timeline.reserveCapacity(dates.count)

        try await withThrowingTaskGroup(of: EventDateTimelineResponse.self) { group in
            for date in dates {
                group.addTask {
                    let events = try await getEvents(for: date.eventDateId)
                    return EventDateTimelineResponse(
                        eventDateId: date.eventDateId,
                        date: date.date,
                        events: events
                    )
                }
            }

            for try await response in group {
                timeline.append(response)
            }
        }

        return timeline.sorted { $0.eventDateId < $1.eventDateId }
    }
}

struct EventDateTimelineResponse: Codable {
    let eventDateId: Int
    let date: String
    let events: [ScheduleEvent]

    var asEventDate: EventDate {
        EventDate(eventDateId: eventDateId, date: date)
    }
}
