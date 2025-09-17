import Foundation

final class AppState: ObservableObject {
    @Published var selectedUniversity: University?
    @Published var selectedFestival: Festival?
    @Published var currentFestivalId: Int = 1
    @Published var currentUniversityName: String = "페스타북대학교"
    
    func selectUniversity(_ university: University) {
        selectedUniversity = university
    }
    
    func changeFestival(_ festivalId: Int) {
        currentFestivalId = festivalId
        selectedFestival = nil
        selectedUniversity = nil
        // 현재 축제 ID를 영속화하고 API 헤더와 동기화
        UserDefaults.standard.set(festivalId, forKey: "currentFestivalId")
        APIClient.shared.updateFestivalId(festivalId)
    }

    func updateUniversityName(_ universityName: String) {
        currentUniversityName = universityName
    }
}
