import SwiftUI
import Foundation

// MARK: - 링크 처리 유틸리티
struct LinkHelper {

    /// 텍스트에서 링크를 감지하고 하이퍼링크로 변환하는 함수
    static func createAttributedString(from text: String) -> AttributedString {
        var attributedString = AttributedString(text)

        // URL 패턴을 찾기 위한 정규식
        let urlPattern = #"https?://[^\s<>"]+"#

        do {
            let regex = try NSRegularExpression(pattern: urlPattern, options: .caseInsensitive)
            let nsString = text as NSString
            let matches = regex.matches(in: text, options: [], range: NSRange(location: 0, length: nsString.length))

            // 역순으로 처리하여 인덱스 변화 방지
            for match in matches.reversed() {
                let range = match.range
                let urlString = nsString.substring(with: range)

                if let url = URL(string: urlString) {
                    // AttributedString의 범위 계산
                    let startIndex = attributedString.index(attributedString.startIndex, offsetByCharacters: range.location)
                    let endIndex = attributedString.index(startIndex, offsetByCharacters: range.length)
                    let attributedRange = startIndex..<endIndex

                    // 링크 스타일 적용
                    attributedString[attributedRange].foregroundColor = .blue
                    attributedString[attributedRange].underlineStyle = .single
                    attributedString[attributedRange].link = url
                }
            }
        } catch {
            print("정규식 오류: \(error)")
        }

        return attributedString
    }
}