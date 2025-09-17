import Foundation

struct Festival: Codable, Identifiable, Hashable {
    let festivalId: Int
    let universityName: String
    let festivalName: String
    let startDate: String
    let endDate: String
    
    var id: Int { festivalId }
    
    // 날짜 포맷팅을 위한 computed properties
    var formattedStartDate: String {
        formatDate(startDate)
    }
    
    var formattedEndDate: String {
        formatDate(endDate)
    }
    
    private func formatDate(_ dateString: String) -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        
        guard let date = formatter.date(from: dateString) else {
            return dateString
        }
        
        formatter.dateFormat = "MM.dd"
        return formatter.string(from: date)
    }
}

// 축제 이미지 모델
struct FestivalImage: Codable, Identifiable {
    let festivalImageId: Int
    let imageUrl: String
    let sequence: Int
    
    var id: Int { festivalImageId }
}

// 축제 상세 정보 모델
struct FestivalDetail: Codable, Identifiable {
    let festivalId: Int
    let universityName: String
    let festivalImages: [FestivalImage]
    let festivalName: String
    let startDate: String
    let endDate: String
    let userVisible: Bool
    
    var id: Int { festivalId }
    
    // 날짜 포맷팅을 위한 computed properties
    var formattedStartDate: String {
        formatDate(startDate)
    }
    
    var formattedEndDate: String {
        formatDate(endDate)
    }
    
    var formattedDateRange: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        
        guard let startDate = formatter.date(from: startDate),
              let endDate = formatter.date(from: endDate) else {
            return "\(startDate) ~ \(endDate)"
        }
        
        let startFormatter = DateFormatter()
        startFormatter.dateFormat = "yyyy년 M월 d일"
        
        let endFormatter = DateFormatter()
        endFormatter.dateFormat = "M월 d일"
        
        return "\(startFormatter.string(from: startDate)) ~ \(endFormatter.string(from: endDate))"
    }
    
    private func formatDate(_ dateString: String) -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        
        guard let date = formatter.date(from: dateString) else {
            return dateString
        }
        
        formatter.dateFormat = "MM.dd"
        return formatter.string(from: date)
    }
}
