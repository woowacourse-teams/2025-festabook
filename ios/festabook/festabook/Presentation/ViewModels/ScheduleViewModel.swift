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

    private let repository: ScheduleRepository

    init(repository: ScheduleRepository) {
        self.repository = repository
    }

    func loadEventDates() async {
        isLoadingDates = true
        errorMessage = nil

        do {
            let dates = try await repository.getEventDates()
            eventDates = dates

            if let firstDate = dates.first {
                selectedEventDate = firstDate
                await loadEvents(for: firstDate.eventDateId)
            }
        } catch {
            errorMessage = "일정 날짜를 불러오는데 실패했습니다."
            print("Error loading event dates: \(error)")
        }

        isLoadingDates = false
    }

    func loadEvents(for eventDateId: Int) async {
        isLoadingEvents = true
        errorMessage = nil

        do {
            events = try await repository.getEvents(for: eventDateId)
        } catch {
            errorMessage = "일정을 불러오는데 실패했습니다."
            print("Error loading events: \(error)")
        }

        isLoadingEvents = false
    }

    func selectDate(_ eventDate: EventDate) {
        guard selectedEventDate?.eventDateId != eventDate.eventDateId else { return }

        selectedEventDate = eventDate
        Task {
            await loadEvents(for: eventDate.eventDateId)
        }
    }
}