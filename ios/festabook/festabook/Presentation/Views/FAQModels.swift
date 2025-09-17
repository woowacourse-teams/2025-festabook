import Foundation

struct FAQ: Codable, Identifiable {
    let questionId: Int
    let question: String
    let answer: String
    let sequence: Int

    var id: Int { questionId }
}