import SwiftUI
import UIKit

struct PlaceDetailView: View {
    let place: PlaceDetail
    let onClose: () -> Void

    @State private var currentImageIndex = 0
    @State private var isImageViewerPresented = false
    @State private var viewerIndex = 0
    @State private var safeAreaTop: CGFloat = 0
    @State private var backSwipeOffset: CGFloat = 0
    @State private var isBackSwiping = false

    private var imageUrls: [String] {
        place.orderedImageUrls
    }

    private var hasDescription: Bool {
        let text = place.safeDescription.trimmingCharacters(in: .whitespacesAndNewlines)
        return !text.isEmpty && text != "설명이 없습니다"
    }

    var body: some View {
        GeometryReader { proxy in
            let topInset = proxy.safeAreaInsets.top
            let statusBarHeight = max(topInset, 50)

            ZStack(alignment: .top) {
                Color.white.ignoresSafeArea()

                ScrollView(.vertical, showsIndicators: false) {
                    VStack(alignment: .leading, spacing: 26) {
                        headerSection(topInset: topInset)
                        detailContent
                            .padding(.horizontal, 20)
                    }
                    .padding(.top, topInset + 12)
                    .padding(.bottom, 40)
                }
                .offset(x: backSwipeOffset)
                .contentShape(Rectangle())
                .gesture(backSwipeGesture)

                Color.white
                    .frame(height: statusBarHeight)
                    .frame(maxWidth: .infinity, alignment: .top)
                }
            .background(Color.white)
            .background(ModalDismissDisabler())
            .onAppear {
                currentImageIndex = 0
                safeAreaTop = topInset
                backSwipeOffset = 0
                isBackSwiping = false
            }
            .onChange(of: topInset) { newValue in
                safeAreaTop = newValue
            }
            .onChange(of: imageUrls) { _ in
                let maxIndex = max(imageUrls.count - 1, 0)
                if isImageViewerPresented {
                    currentImageIndex = min(currentImageIndex, maxIndex)
                    viewerIndex = min(viewerIndex, maxIndex)
                } else {
                    currentImageIndex = 0
                    viewerIndex = 0
                }
            }
        }
        .ignoresSafeArea(edges: .top)
        .interactiveDismissDisabled(true)
        .navigationBarBackButtonHidden(true)
        .toolbar(.hidden, for: .navigationBar)
        .fullScreenCover(isPresented: $isImageViewerPresented) {
            imageViewer
        }
    }

    private func headerSection(topInset: CGFloat) -> some View {
        ZStack(alignment: .topLeading) {
            carousel

            if imageUrls.count > 1 {
                PageIndicator(total: imageUrls.count, currentIndex: currentImageIndex)
                    .padding(.bottom, 12)
                    .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .bottom)
            }

            CloseButton(action: onClose)
                .padding(.top, topInset + 48)
                .padding(.leading, 16)
        }
    }

    private var carousel: some View {
        TabView(selection: $currentImageIndex) {
            if imageUrls.isEmpty {
                placeholderImage()
                    .tag(0)
            } else {
                ForEach(Array(imageUrls.enumerated()), id: \.offset) { index, url in
                    AsyncImage(url: URL(string: url)) { image in
                        image
                            .resizable()
                            .scaledToFill()
                            .frame(maxWidth: .infinity, maxHeight: .infinity)
                    } placeholder: {
                        placeholderImage()
                    }
                    .tag(index)
                    .contentShape(Rectangle())
                    .onTapGesture {
                        currentImageIndex = index
                        viewerIndex = index
                        if isImageViewerPresented {
                            // 이미 열려 있으면 인덱스만 갱신
                            return
                        }
                        DispatchQueue.main.async {
                            isImageViewerPresented = true
                        }
                    }
                }
            }
        }
        .frame(height: 260)
        .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
        .frame(maxWidth: .infinity)
        .clipped()
    }

    private var detailContent: some View {
        VStack(alignment: .leading, spacing: 18) {
            VStack(alignment: .leading, spacing: 6) {
                CategoryBadge(category: place.category)

                Text(place.title)
                    .font(.system(size: 20, weight: .semibold))
                    .foregroundColor(.primary)
                    .multilineTextAlignment(.leading)
                    .lineLimit(2)
            }

            VStack(alignment: .leading, spacing: 12) {
                DetailInfoRow(icon: "clock.fill", text: place.safeTimeInfo)
                DetailInfoRow(icon: "location.fill", text: place.safeLocation)
                DetailInfoRow(icon: "person.2.fill", text: place.safeHost)
            }

            if hasDescription {
                Text(place.safeDescription)
                    .font(.system(size: 13))
                    .foregroundColor(.secondary)
                    .lineSpacing(4)
            }
        }
    }

    @ViewBuilder
    private func placeholderImage() -> some View {
        Color(.systemGray5)
            .overlay(
                Image(systemName: "photo")
                    .font(.system(size: 32))
                    .foregroundColor(.gray)
            )
            .frame(maxWidth: .infinity)
            .frame(height: 260)
    }

    private var backSwipeGesture: some Gesture {
        DragGesture(minimumDistance: 15, coordinateSpace: .local)
            .onChanged { value in
                guard value.startLocation.x < 30 else { return }
                guard abs(value.translation.width) > abs(value.translation.height) else { return }
                guard value.translation.width > 0 else { return }

                isBackSwiping = true
                backSwipeOffset = max(0, value.translation.width)
            }
            .onEnded { value in
                guard isBackSwiping else { return }
                let translation = value.translation.width
                let shouldDismiss = translation > 120

                withAnimation(.spring(response: 0.35, dampingFraction: 0.85)) {
                    backSwipeOffset = shouldDismiss ? UIScreen.main.bounds.width : 0
                }

                if shouldDismiss {
                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.18) {
                        onClose()
                    }
                } else {
                    backSwipeOffset = 0
                }

                isBackSwiping = false
            }
    }

    private var imageViewer: some View {
        let clampedIndex = min(max(viewerIndex, 0), max(imageUrls.count - 1, 0))

        return ImageGalleryView(
            imageUrls: imageUrls,
            initialIndex: clampedIndex,
            currentIndex: Binding(
                get: { viewerIndex },
                set: { viewerIndex = min(max($0, 0), max(imageUrls.count - 1, 0)) }
            ),
            topInset: safeAreaTop
        ) {
            isImageViewerPresented = false
        }
    }
}

private struct PagingImageView: UIViewControllerRepresentable {
    let imageUrls: [String]
    @Binding var currentIndex: Int
    let onZoomChange: (Bool) -> Void

    func makeCoordinator() -> Coordinator {
        Coordinator(parent: self)
    }

    func makeUIViewController(context: Context) -> UIPageViewController {
        let controller = UIPageViewController(transitionStyle: .scroll, navigationOrientation: .horizontal)
        controller.dataSource = context.coordinator
        controller.delegate = context.coordinator
        controller.view.backgroundColor = .black
        context.coordinator.configure(with: self, pageViewController: controller)
        return controller
    }

    func updateUIViewController(_ pageViewController: UIPageViewController, context: Context) {
        context.coordinator.configure(with: self, pageViewController: pageViewController)
    }

    final class Coordinator: NSObject, UIPageViewControllerDataSource, UIPageViewControllerDelegate {
        var parent: PagingImageView
        var controllers: [ZoomableImageHostingController] = []

        init(parent: PagingImageView) {
            self.parent = parent
        }

        func configure(with parent: PagingImageView, pageViewController: UIPageViewController) {
            self.parent = parent

            rebuildControllers()

            guard !controllers.isEmpty else {
                pageViewController.setViewControllers([], direction: .forward, animated: false)
                return
            }

            let desiredIndex = min(max(parent.currentIndex, 0), controllers.count - 1)
            let desiredController = controllers[desiredIndex]
            let currentController = pageViewController.viewControllers?.first as? ZoomableImageHostingController

            if currentController !== desiredController {
                let direction: UIPageViewController.NavigationDirection = (currentController?.index ?? 0) <= desiredIndex ? .forward : .reverse
                pageViewController.setViewControllers([desiredController], direction: direction, animated: currentController != nil)
            }

            parent.currentIndex = desiredIndex
            parent.onZoomChange(false)

            pageViewController.dataSource = controllers.count > 1 ? self : nil
            pageViewController.delegate = self
        }

        private func rebuildControllers() {
            let desiredCount = parent.imageUrls.count

            if controllers.count != desiredCount {
                controllers = parent.imageUrls.enumerated().map { index, urlString in
                    ZoomableImageHostingController(
                        index: index,
                        urlString: urlString,
                        onZoomChange: { [weak self] zooming in
                            self?.parent.onZoomChange(zooming)
                        }
                    )
                }
            } else {
                for (index, controller) in controllers.enumerated() {
                    controller.update(
                        index: index,
                        urlString: parent.imageUrls[index],
                        onZoomChange: { [weak self] zooming in
                            self?.parent.onZoomChange(zooming)
                        }
                    )
                }
            }

            controllers.sort { $0.index < $1.index }
        }

        func pageViewController(_ pageViewController: UIPageViewController, viewControllerBefore viewController: UIViewController) -> UIViewController? {
            guard let controller = viewController as? ZoomableImageHostingController,
                  let currentPosition = controllers.firstIndex(where: { $0 === controller }) else { return nil }
            let previousIndex = currentPosition - 1
            guard previousIndex >= 0 else { return nil }
            return controllers[safe: previousIndex]
        }

        func pageViewController(_ pageViewController: UIPageViewController, viewControllerAfter viewController: UIViewController) -> UIViewController? {
            guard let controller = viewController as? ZoomableImageHostingController,
                  let currentPosition = controllers.firstIndex(where: { $0 === controller }) else { return nil }
            let nextIndex = currentPosition + 1
            guard nextIndex < controllers.count else { return nil }
            return controllers[safe: nextIndex]
        }

        func pageViewController(_ pageViewController: UIPageViewController, didFinishAnimating finished: Bool, previousViewControllers: [UIViewController], transitionCompleted completed: Bool) {
            guard completed,
                  let visible = pageViewController.viewControllers?.first as? ZoomableImageHostingController else { return }
            parent.currentIndex = visible.index
            parent.onZoomChange(false)
        }
    }
}

private extension Array {
    subscript(safe index: Int) -> Element? {
        guard indices.contains(index) else { return nil }
        return self[index]
    }
}

private final class ZoomableImageHostingController: UIHostingController<ZoomableAsyncImage> {
    private(set) var index: Int
    private var urlString: String
    private var onZoomChange: (Bool) -> Void

    init(index: Int, urlString: String, onZoomChange: @escaping (Bool) -> Void) {
        self.index = index
        self.urlString = urlString
        self.onZoomChange = onZoomChange
        let view = ZoomableAsyncImage(url: URL(string: urlString), onZoomChange: onZoomChange)
        super.init(rootView: view)
        self.view.backgroundColor = .black
    }

    @MainActor
    required dynamic init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func update(index: Int, urlString: String, onZoomChange: @escaping (Bool) -> Void) {
        self.index = index
        self.urlString = urlString
        self.onZoomChange = onZoomChange
        rootView = ZoomableAsyncImage(url: URL(string: urlString), onZoomChange: onZoomChange)
        self.view.backgroundColor = .black
    }
}

private struct ModalDismissDisabler: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        let controller = UIViewController()
        controller.view.backgroundColor = .clear
        controller.isModalInPresentation = true
        return controller
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

private struct DetailInfoRow: View {
    let icon: String
    let text: String

    var body: some View {
        HStack(alignment: .center, spacing: 8) {
            Image(systemName: icon)
                .font(.system(size: 15, weight: .semibold))
                .foregroundColor(.gray)
                .frame(width: 22)

            Text(text)
                .font(.system(size: 13.5))
                .foregroundColor(.primary)
                .multilineTextAlignment(.leading)

            Spacer()
        }
    }
}

private struct CloseButton: View {
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Image(systemName: "chevron.left")
                .font(.system(size: 17, weight: .semibold))
                .foregroundColor(.black)
                .frame(width: 42, height: 42)
                .background(
                    Circle()
                        .fill(Color.white)
                        .shadow(color: Color.black.opacity(0.12), radius: 6, x: 0, y: 3)
                )
        }
        .accessibilityLabel("뒤로")
    }
}

private struct PageIndicator: View {
    let total: Int
    let currentIndex: Int

    var body: some View {
        HStack(spacing: 6) {
            ForEach(0..<total, id: \.self) { index in
                Circle()
                    .fill(index == currentIndex ? Color.black : Color.black.opacity(0.2))
                    .frame(width: index == currentIndex ? 7 : 5, height: index == currentIndex ? 7 : 5)
            }
        }
        .padding(.vertical, 8)
        .padding(.horizontal, 12)
        .background(
            Capsule()
                .fill(Color.white.opacity(0.85))
        )
    }
}

private struct ShadowModifier: ViewModifier {
    func body(content: Content) -> some View {
        content
            .shadow(color: Color.black.opacity(0.08), radius: 18, x: 0, y: 10)
    }
}

private struct ImageGalleryView: View {
    let imageUrls: [String]
    private let initialIndex: Int
    @Binding var currentIndex: Int
    let topInset: CGFloat
    let onClose: () -> Void

    @Environment(\.dismiss) private var dismiss
    @State private var dismissalOffset: CGFloat = 0
    @State private var isZoomed = false
    @State private var displayedIndex: Int

    init(
        imageUrls: [String],
        initialIndex: Int,
        currentIndex: Binding<Int>,
        topInset: CGFloat,
        onClose: @escaping () -> Void
    ) {
        self.imageUrls = imageUrls
        self.initialIndex = min(max(initialIndex, 0), max(imageUrls.count - 1, 0))
        self._currentIndex = currentIndex
        self.topInset = topInset
        self.onClose = onClose
        _displayedIndex = State(initialValue: self.initialIndex)
    }

    var body: some View {
        ZStack(alignment: .topTrailing) {
            if imageUrls.isEmpty {
                Color.black
                    .ignoresSafeArea()
                    .overlay(
                        VStack(spacing: 16) {
                            Image(systemName: "photo")
                                .font(.system(size: 48))
                                .foregroundColor(.white.opacity(0.6))
                            Text("이미지를 불러올 수 없습니다")
                                .foregroundColor(.white.opacity(0.8))
                                .font(.system(size: 16, weight: .medium))
                        }
                    )
            } else {
                PagingImageView(
                    imageUrls: imageUrls,
                    currentIndex: $displayedIndex,
                    onZoomChange: { isZoomed = $0 }
                )
                .background(Color.black.ignoresSafeArea())
                .offset(y: dismissalOffset)
                .animation(.spring(), value: dismissalOffset)
                .simultaneousGesture(dismissGesture)
            }

            if imageUrls.count > 1 {
                PageIndicator(total: imageUrls.count, currentIndex: displayedIndex)
                    .padding(.bottom, 24)
                    .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .bottom)
            }

            Button(action: close) {
                Image(systemName: "xmark")
                    .font(.system(size: 18, weight: .semibold))
                    .foregroundColor(.white)
                    .frame(width: 36, height: 36)
                    .background(Color.black.opacity(0.5))
                    .clipShape(Circle())
                    .padding(.top, max(topInset + 12, 44))
                    .padding(.trailing, 20)
            }
        }
        .onAppear {
            syncDisplayedIndex(with: initialIndex)
            syncCurrentIndex(with: displayedIndex)
        }
        .onChange(of: imageUrls) { _ in
            let maxIndex = max(imageUrls.count - 1, 0)
            syncDisplayedIndex(with: min(displayedIndex, maxIndex))
            syncCurrentIndex(with: min(currentIndex, maxIndex))
        }
        .onChange(of: currentIndex) { newValue in
            syncDisplayedIndex(with: newValue)
        }
        .onChange(of: displayedIndex) { newValue in
            syncCurrentIndex(with: newValue)
        }
        .edgesIgnoringSafeArea(.all)
    }

    private func close() {
        dismiss()
        dismissalOffset = 0
        isZoomed = false
        onClose()
    }

    private func syncDisplayedIndex(with target: Int) {
        let clamped = imageUrls.isEmpty ? 0 : min(max(target, 0), imageUrls.count - 1)
        if clamped != displayedIndex {
            displayedIndex = clamped
        }
    }

    private func syncCurrentIndex(with target: Int) {
        let clamped = imageUrls.isEmpty ? 0 : min(max(target, 0), imageUrls.count - 1)
        if clamped != currentIndex {
            currentIndex = clamped
        }
    }

    private var dismissGesture: some Gesture {
        DragGesture(minimumDistance: 20)
            .onChanged { value in
                guard !isZoomed else { return }
                guard abs(value.translation.height) > abs(value.translation.width) else { return }
                if value.translation.height > 0 {
                    dismissalOffset = value.translation.height
                } else {
                    dismissalOffset = 0
                }
            }
            .onEnded { value in
                guard !isZoomed else {
                    withAnimation(.spring()) { dismissalOffset = 0 }
                    return
                }

                guard abs(value.translation.height) > abs(value.translation.width) else {
                    withAnimation(.spring()) { dismissalOffset = 0 }
                    return
                }

                if value.translation.height > 120 {
                    close()
                } else {
                    withAnimation(.spring()) { dismissalOffset = 0 }
                }
            }
    }
}

private struct ZoomableAsyncImage: View {
    let url: URL?
    let onZoomChange: (Bool) -> Void

    @State private var scale: CGFloat = 1
    @State private var lastScale: CGFloat = 1
    @State private var offset: CGSize = .zero
    @State private var accumulatedOffset: CGSize = .zero

    private let maxScale: CGFloat = 4

    var body: some View {
        GeometryReader { proxy in
            ZStack {
                if let url {
                    AsyncImage(url: url) { phase in
                        switch phase {
                        case .empty:
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        case .success(let image):
                            let renderedImage = image
                                .resizable()
                                .scaledToFit()
                                .frame(width: proxy.size.width, height: proxy.size.height)
                                .scaleEffect(scale)
                                .offset(offset)

                            renderedImage
                                .simultaneousGesture(zoomGesture)
                                .simultaneousGesture(panGesture)
                        case .failure:
                            placeholder
                        @unknown default:
                            placeholder
                        }
                    }
                    .onAppear {
                        resetPosition()
                        onZoomChange(false)
                    }
                } else {
                    placeholder
                }
            }
            .frame(width: proxy.size.width, height: proxy.size.height)
        }
    }

    private var zoomGesture: some Gesture {
        MagnificationGesture()
            .onChanged { value in
                var newScale = lastScale * value
                newScale = min(max(newScale, 1), maxScale)
                scale = newScale
                onZoomChange(scale > 1.02)
            }
            .onEnded { _ in
                lastScale = scale
                if scale <= 1.01 {
                    resetPosition()
                    onZoomChange(false)
                }
            }
    }

    private var panGesture: some Gesture {
        DragGesture()
            .onChanged { value in
                guard scale > 1.01 else { return }
                let translated = CGSize(
                    width: accumulatedOffset.width + value.translation.width,
                    height: accumulatedOffset.height + value.translation.height
                )
                offset = translated
            }
            .onEnded { _ in
                guard scale > 1.01 else { return }
                accumulatedOffset = offset
            }
    }

    private var placeholder: some View {
        Image(systemName: "photo")
            .font(.system(size: 44))
            .foregroundColor(.white.opacity(0.7))
    }

    private func resetPosition() {
        withAnimation(.spring()) {
            scale = 1
            lastScale = 1
            offset = .zero
            accumulatedOffset = .zero
        }
    }
}

#Preview {
    let sample = PlaceDetail(
        placeId: 1,
        imageUrl: nil,
        placeImages: [
            PlaceImage(id: 0, imageUrl: "https://picsum.photos/id/1018/600/400", sequence: 0),
            PlaceImage(id: 1, imageUrl: "https://picsum.photos/id/1020/600/400", sequence: 1)
        ],
        category: "BAR",
        title: "공용 주점",
        description: "1일차 - 교육학과/중국통상학과/영어영문학과\n2일차 - 국어국문학과/역사학과/일어일문학과\n3일차 - 인문과학대학",
        location: "광개토관 앞",
        host: "공용",
        startTime: "18:00",
        endTime: "23:59",
        coordinate: nil
    )

    PlaceDetailView(place: sample) {}
}
