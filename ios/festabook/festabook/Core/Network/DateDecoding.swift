import Foundation

// MARK: - Flexible ISO8601 helpers (shared)
extension ISO8601DateFormatter {
    static let standardShared: ISO8601DateFormatter = {
        let f = ISO8601DateFormatter()
        f.formatOptions = [.withInternetDateTime]
        return f
    }()

    static let fractionalShared: ISO8601DateFormatter = {
        let f = ISO8601DateFormatter()
        f.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        return f
    }()
}

enum FlexibleDateParserShared {
    static let formatter: DateFormatter = {
        let f = DateFormatter()
        f.locale = Locale(identifier: "en_US_POSIX")
        f.timeZone = TimeZone(secondsFromGMT: 0)
        f.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
        return f
    }()

    static func adjustToSixFractionDigits(_ raw: String) -> String? {
        guard let dotRange = raw.range(of: ".") else { return raw }
        let head = String(raw[..<dotRange.lowerBound])
        var fraction = String(raw[dotRange.upperBound...])
        if let tzStart = fraction.firstIndex(where: { !$0.isNumber }) {
            fraction = String(fraction[..<tzStart])
        }
        if fraction.count == 6 { return head + "." + fraction }
        if fraction.count < 6 { return head + "." + fraction.padding(toLength: 6, withPad: "0", startingAt: 0) }
        return head + "." + String(fraction.prefix(6))
    }
}


