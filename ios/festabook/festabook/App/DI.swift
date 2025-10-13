import Foundation

final class ServiceLocator: ObservableObject {
    static let shared = ServiceLocator()
    private init() {}

    let api = APIClient()
    lazy var universityRepo: UniversityRepository = UniversityRepositoryLive(api: api)
    lazy var festivalRepo: FestivalRepository = FestivalRepositoryLive(api: api)
    lazy var announcementsRepository: AnnouncementsRepository = AnnouncementsRepositoryLive(api: api)
    lazy var faqRepository: FAQRepository = FAQRepositoryLive(api: api)
    lazy var scheduleRepository: ScheduleRepository = ScheduleRepositoryLive(api: api)
    
    func updateFestivalId(_ festivalId: Int) {
        api.updateFestivalId(festivalId)
    }
}
