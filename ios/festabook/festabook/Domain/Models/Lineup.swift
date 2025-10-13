import Foundation

// 라인업 모델
struct Lineup: Codable, Identifiable {
    let lineupId: Int
    let name: String
    let imageUrl: String
    let performanceAt: String
    
    var id: Int { lineupId }
    
    // 공연 시간 포맷팅
    var formattedPerformanceTime: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
        
        guard let date = formatter.date(from: performanceAt) else {
            return performanceAt
        }
        
        let outputFormatter = DateFormatter()
        outputFormatter.dateFormat = "MM.dd HH:mm"
        return outputFormatter.string(from: date)
    }
}





