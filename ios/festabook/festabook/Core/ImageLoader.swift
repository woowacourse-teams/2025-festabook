import Foundation
import SwiftUI

@MainActor
final class ImageLoader: ObservableObject {
    @Published var image: UIImage?
    @Published var isLoading = false
    @Published var hasError = false

    static let cache = URLCache(
        memoryCapacity: 50 * 1024 * 1024, // 50MB memory
        diskCapacity: 200 * 1024 * 1024   // 200MB disk
    )

    private let session: URLSession

    init() {
        let config = URLSessionConfiguration.default
        config.urlCache = Self.cache
        config.requestCachePolicy = .returnCacheDataElseLoad
        self.session = URLSession(configuration: config)
    }

    func load(from urlString: String) {
        guard let url = URL(string: urlString) else {
            print("[ImageLoader] Invalid URL: \(urlString)")
            hasError = true
            return
        }

        load(from: url)
    }

    func load(from url: URL) {
        // Reset state
        image = nil
        hasError = false
        isLoading = true

        print("[ImageLoader] Loading image from: \(url)")

        Task {
            do {
                let (data, response) = try await session.data(from: url)

                guard let httpResponse = response as? HTTPURLResponse,
                      httpResponse.statusCode == 200 else {
                    print("[ImageLoader] Invalid response for: \(url)")
                    await MainActor.run {
                        self.hasError = true
                        self.isLoading = false
                    }
                    return
                }

                guard let loadedImage = UIImage(data: data) else {
                    print("[ImageLoader] Failed to create image from data for: \(url)")
                    await MainActor.run {
                        self.hasError = true
                        self.isLoading = false
                    }
                    return
                }

                await MainActor.run {
                    self.image = loadedImage
                    self.isLoading = false
                    print("[ImageLoader] Successfully loaded image for: \(url)")
                }

            } catch {
                print("[ImageLoader] Error loading image: \(error)")
                await MainActor.run {
                    self.hasError = true
                    self.isLoading = false
                }
            }
        }
    }

    func retry(from urlString: String) {
        hasError = false
        load(from: urlString)
    }
}

struct CachedAsyncImage<Content: View, Placeholder: View, ErrorView: View>: View {
    let url: String
    let content: (Image) -> Content
    let placeholder: () -> Placeholder
    let errorView: () -> ErrorView

    @StateObject private var loader = ImageLoader()

    init(
        url: String,
        @ViewBuilder content: @escaping (Image) -> Content,
        @ViewBuilder placeholder: @escaping () -> Placeholder,
        @ViewBuilder errorView: @escaping () -> ErrorView
    ) {
        self.url = url
        self.content = content
        self.placeholder = placeholder
        self.errorView = errorView
    }

    var body: some View {
        Group {
            if let image = loader.image {
                content(Image(uiImage: image))
            } else if loader.isLoading {
                placeholder()
            } else if loader.hasError {
                errorView()
                    .onTapGesture {
                        loader.retry(from: url)
                    }
            } else {
                placeholder()
            }
        }
        .onAppear {
            loader.load(from: url)
        }
    }
}

// Convenience initializer
extension CachedAsyncImage {
    init(
        url: String,
        @ViewBuilder content: @escaping (Image) -> Content
    ) where Placeholder == Color, ErrorView == AnyView {
        self.init(
            url: url,
            content: content,
            placeholder: { Color.gray.opacity(0.3) },
            errorView: {
                AnyView(
                    VStack(spacing: 8) {
                        Image(systemName: "exclamationmark.triangle")
                            .foregroundColor(.gray)
                        Text("다시 시도")
                            .font(.caption)
                            .foregroundColor(.gray)
                    }
                )
            }
        )
    }
}

// MARK: - Image Prefetcher
actor ImagePrefetcher {
    static let shared = ImagePrefetcher()

    private var activeRequests: Set<URL> = []
    private let session: URLSession

    private init() {
        let configuration = URLSessionConfiguration.default
        configuration.urlCache = ImageLoader.cache
        configuration.requestCachePolicy = .returnCacheDataElseLoad
        configuration.timeoutIntervalForResource = 60
        session = URLSession(configuration: configuration)
    }

    func prefetch(urls: [String]) async {
        for urlString in urls {
            guard let url = URL(string: urlString) else { continue }
            await prefetch(url: url)
        }
    }

    private func prefetch(url: URL) async {
        if activeRequests.contains(url) { return }

        var request = URLRequest(url: url)
        request.cachePolicy = .returnCacheDataElseLoad

        if ImageLoader.cache.cachedResponse(for: request) != nil {
            return
        }

        activeRequests.insert(url)
        defer { activeRequests.remove(url) }

        do {
            let (data, response) = try await session.data(for: request)

            guard let httpResponse = response as? HTTPURLResponse,
                  (200...299).contains(httpResponse.statusCode) else {
                return
            }

            let cachedResponse = CachedURLResponse(response: response, data: data)
            ImageLoader.cache.storeCachedResponse(cachedResponse, for: request)
        } catch {
            // Ignore failures; cache simply remains empty for this URL
        }
    }
}
