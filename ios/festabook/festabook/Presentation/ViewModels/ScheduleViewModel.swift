import Foundation
import SwiftUI

@MainActor
class ScheduleViewModel: ObservableObject {
    @Published var eventDates: [EventDate] = []
    @Published var events: [ScheduleEvent] = []
    @Published var selectedEventDate: EventDate?
    @Published var isLoadingDates = false
    @Published var isLoadingEvents = false
    @Published var errorMessage: String?
    @Published var scrollTargetEventId: Int?

    private let repository: ScheduleRepository
    private var eventsByDate: [Int: [ScheduleEvent]] = [:]

    init(repository: ScheduleRepository) {
        self.repository = repository
    }

    func loadEventDates(preserveSelection: Bool = false, scrollToOngoing: Bool = true) async {
        isLoadingDates = true
        errorMessage = nil

        let previousSelectedId = preserveSelection ? selectedEventDate?.eventDateId : nil

        do {
            let timeline = try await repository.getTimeline()
            eventDates = timeline.map { $0.asEventDate }
            eventsByDate = timeline.reduce(into: [:]) { partialResult, item in
                partialResult[item.eventDateId] = item.events
            }

            guard !eventDates.isEmpty else {
                selectedEventDate = nil
                events = []
                scrollTargetEventId = nil
                isLoadingDates = false
                isLoadingEvents = false
                return
            }

            if let previousSelectedId,
               let matchedDate = eventDates.first(where: { $0.eventDateId == previousSelectedId }) {
                selectedEventDate = matchedDate
                applyEvents(for: matchedDate.eventDateId, scrollToOngoing: scrollToOngoing)
            } else if let todayIndex = indexForToday(in: eventDates) {
                let todayDate = eventDates[todayIndex]
                selectedEventDate = todayDate
                applyEvents(for: todayDate.eventDateId, scrollToOngoing: scrollToOngoing)
            } else if let firstDate = eventDates.first {
                selectedEventDate = firstDate
                applyEvents(for: firstDate.eventDateId, scrollToOngoing: scrollToOngoing)
            }
        } catch {
            errorMessage = "일정 날짜를 불러오는데 실패했습니다."
            print("Error loading event dates: \(error)")
            events = []
            selectedEventDate = nil
            scrollTargetEventId = nil
        }

        isLoadingDates = false
        isLoadingEvents = false
    }

    func selectDate(_ eventDate: EventDate) {
        guard selectedEventDate?.eventDateId != eventDate.eventDateId else { return }

        selectedEventDate = eventDate
        applyEvents(for: eventDate.eventDateId, scrollToOngoing: false)
    }

    func resetForNewFestival() {
        eventDates = []
        events = []
        selectedEventDate = nil
        isLoadingDates = false
        isLoadingEvents = false
        errorMessage = nil
        scrollTargetEventId = nil
        eventsByDate = [:]
    }

    func events(for eventDateId: Int) -> [ScheduleEvent] {
        eventsByDate[eventDateId] ?? []
    }

    private func indexForToday(in eventDates: [EventDate]) -> Int? {
        let calendar = Calendar.current
        let today = calendar.startOfDay(for: Date())

        for (index, eventDate) in eventDates.enumerated() {
            if let date = ScheduleViewModel.dateFormatter.date(from: eventDate.date),
               calendar.isDate(date, inSameDayAs: today) {
                return index
            }
        }

        return nil
    }

    private static let dateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.calendar = Calendar(identifier: .gregorian)
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.timeZone = TimeZone.current
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter
    }()

    private func applyEvents(for eventDateId: Int, scrollToOngoing: Bool) {
        isLoadingEvents = false

        let fetchedEvents = eventsByDate[eventDateId] ?? []
        events = fetchedEvents

        if scrollToOngoing {
            scrollTargetEventId = fetchedEvents.first(where: { $0.status == .ongoing })?.id
        } else {
            scrollTargetEventId = nil
        }
    }
}
