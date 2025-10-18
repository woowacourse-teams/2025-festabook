import SwiftUI
import Foundation
import UIKit

// MARK: - 링크 처리 유틸리티
struct LinkHelper {

    /// 텍스트에서 링크를 감지하고 하이퍼링크로 변환하는 함수
    static func createAttributedString(from text: String, baseColor: Color? = nil) -> AttributedString {
        let mutableAttributedString = NSMutableAttributedString(string: text)
        let fullRange = NSRange(location: 0, length: (text as NSString).length)

        if let baseColor {
            mutableAttributedString.addAttribute(
                .foregroundColor,
                value: UIColor(baseColor),
                range: fullRange
            )
        }

        // URL 패턴을 찾기 위한 정규식
        let urlPattern = #"https?://[^\s<>"]+"#

        do {
            let regex = try NSRegularExpression(pattern: urlPattern, options: .caseInsensitive)
            let matches = regex.matches(in: text, options: [], range: fullRange)

            matches.forEach { match in
                guard let urlRange = Range(match.range, in: text) else { return }
                let urlString = String(text[urlRange])

                guard let url = URL(string: urlString) else { return }

                mutableAttributedString.addAttributes(
                    [
                        .foregroundColor: UIColor.systemBlue,
                        .underlineStyle: NSUnderlineStyle.single.rawValue,
                        .link: url
                    ],
                    range: match.range
                )
            }
        } catch {
            print("정규식 오류: \(error)")
        }

        guard let attributedString = try? AttributedString(mutableAttributedString) else {
            return AttributedString(text)
        }

        return attributedString
    }
}
