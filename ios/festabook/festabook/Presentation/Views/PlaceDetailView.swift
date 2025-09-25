import SwiftUI
import UIKit

struct PlaceDetailView: View {
    let place: PlaceDetail
    let onClose: () -> Void

    @State private var currentImageIndex = 0
    @State private var isImageViewerPresented = false
    @State private var viewerIndex = 0
    @State private var imageViewerStartIndex = 0  // 안전한 인덱스 보관용
    @State private var lockedViewerIndex = 0     // 뷰어 생성 시 고정된 인덱스
    @State private var safeAreaTop: CGFloat = 0
    @State private var backSwipeOffset: CGFloat = 0
    @State private var isBackSwiping = false
    @State private var isScrollDisabled = false

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
                .scrollDisabled(isScrollDisabled)
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
                print("[PlaceDetailView] onAppear - currentImageIndex: \(currentImageIndex), viewerIndex: \(viewerIndex), lockedViewerIndex: \(lockedViewerIndex), isImageViewerPresented: \(isImageViewerPresented)")
                // 처음 열 때만 인덱스 초기화 (lockedViewerIndex는 보호)
                if !isImageViewerPresented && currentImageIndex == 0 && viewerIndex == 0 && lockedViewerIndex == 0 {
                    print("[PlaceDetailView] 인덱스 초기화 실행 (lockedViewerIndex 보호)")
                    currentImageIndex = 0
                    imageViewerStartIndex = 0
                    // lockedViewerIndex는 이미 0이므로 별도 설정 불필요
                } else {
                    print("[PlaceDetailView] 인덱스 초기화 생략 - lockedViewerIndex 보호됨: \(lockedViewerIndex)")
                }
                safeAreaTop = topInset
                backSwipeOffset = 0
                isBackSwiping = false
                isScrollDisabled = false
            }
            .onChange(of: topInset) { _, newValue in
                safeAreaTop = newValue
            }
        .onChange(of: imageUrls) { _, newUrls in
            print("[PlaceDetailView] imageUrls 변경됨 - 개수: \(newUrls.count), currentImageIndex: \(currentImageIndex), viewerIndex: \(viewerIndex), lockedViewerIndex: \(lockedViewerIndex)")
            let maxIndex = max(newUrls.count - 1, 0)
            
            // 인덱스가 범위를 벗어날 때만 조정
            let newCurrentIndex = min(currentImageIndex, maxIndex)
            let newViewerIndex = min(viewerIndex, maxIndex)
            let newStartIndex = min(imageViewerStartIndex, maxIndex)
            let newLockedIndex = min(lockedViewerIndex, maxIndex)
            
            if currentImageIndex != newCurrentIndex {
                currentImageIndex = newCurrentIndex
                print("[PlaceDetailView] currentImageIndex를 \(newCurrentIndex)로 조정")
            }
            
            if viewerIndex != newViewerIndex {
                viewerIndex = newViewerIndex
                print("[PlaceDetailView] viewerIndex를 \(newViewerIndex)로 조정")
            }
            
            if imageViewerStartIndex != newStartIndex {
                imageViewerStartIndex = newStartIndex
                print("[PlaceDetailView] imageViewerStartIndex를 \(newStartIndex)로 조정")
            }
            
            // lockedViewerIndex는 뷰어가 열려있지 않을 때만 범위 조정
            if !isImageViewerPresented && lockedViewerIndex != newLockedIndex {
                lockedViewerIndex = newLockedIndex
                print("[PlaceDetailView] lockedViewerIndex를 \(newLockedIndex)로 조정")
            } else if isImageViewerPresented {
                print("[PlaceDetailView] lockedViewerIndex 보호됨 (뷰어 열린 상태): \(lockedViewerIndex)")
            }
        }
        }
        .ignoresSafeArea(edges: .top)
        .interactiveDismissDisabled(true)
        .navigationBarBackButtonHidden(true)
        .toolbar(.hidden, for: .navigationBar)
        .fullScreenCover(isPresented: $isImageViewerPresented) {
            // fullScreenCover 실행 시점 로그
            let _ = print("[PlaceDetailView] fullScreenCover 실행 - lockedViewerIndex: \(lockedViewerIndex), viewerIndex: \(viewerIndex)")
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
                        print("[PlaceDetailView] 이미지 탭됨 - 인덱스: \(index), 현재 currentImageIndex: \(currentImageIndex), viewerIndex: \(viewerIndex)")
                        
                        if isImageViewerPresented {
                            print("[PlaceDetailView] 이미지 뷰어가 이미 열려있음 - 인덱스만 갱신")
                            return
                        }
                        
                        // 고정된 뷰어 인덱스 설정 (절대 변경되지 않음)
                        lockedViewerIndex = index
                        print("[PlaceDetailView] lockedViewerIndex 설정: \(lockedViewerIndex) (고정, 절대 변경 안됨)")
                        
                        // 다른 인덱스들도 설정
                        imageViewerStartIndex = index
                        
                        if viewerIndex != index {
                            print("[PlaceDetailView] viewerIndex 설정: \(viewerIndex) -> \(index)")
                            viewerIndex = index
                        }
                        
                        if currentImageIndex != index {
                            print("[PlaceDetailView] currentImageIndex 설정: \(currentImageIndex) -> \(index)")
                            currentImageIndex = index
                        }
                        
                        print("[PlaceDetailView] 이미지 뷰어 열기 (lockedIndex: \(lockedViewerIndex), viewerIndex: \(viewerIndex))")
                        isImageViewerPresented = true
                        
                        // 즉시 상태 확인
                        print("[PlaceDetailView] 뷰어 열린 직후 상태 - lockedViewerIndex: \(lockedViewerIndex), viewerIndex: \(viewerIndex), isImageViewerPresented: \(isImageViewerPresented)")
                    }
                }
            }
        }
        .frame(height: 260)
        .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
        .frame(maxWidth: .infinity)
        .clipped()
        .onChange(of: currentImageIndex) { _, newIndex in
            print("[PlaceDetailView] TabView currentImageIndex 변경됨: \(currentImageIndex) -> \(newIndex)")
            // viewerIndex 동기화 (조건부 동기화)
            if viewerIndex != newIndex {
                print("[PlaceDetailView] viewerIndex 동기화: \(viewerIndex) -> \(newIndex)")
                viewerIndex = newIndex
            } else {
                print("[PlaceDetailView] viewerIndex 이미 동기화됨: \(viewerIndex)")
            }
            
            // imageViewerStartIndex는 탭에서만 설정되도록 변경 (자동 동기화 제거)
            // 이 로직이 imageViewer 생성 중 충돌을 일으켜서 제거
            print("[PlaceDetailView] imageViewerStartIndex 자동 동기화 생략 (충돌 방지)")
        }
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
                if !isScrollDisabled { isScrollDisabled = true }
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
                isScrollDisabled = false
            }
    }

    private var imageViewer: some View {
        // 고정된 인덱스 사용 + 안전장치
        var safeStartIndex = lockedViewerIndex
        
        // 안전장치: lockedViewerIndex가 0이고 currentImageIndex가 0이 아니면 currentImageIndex 사용
        if safeStartIndex == 0 && currentImageIndex != 0 {
            safeStartIndex = currentImageIndex
            print("[PlaceDetailView] 안전장치 작동: lockedViewerIndex가 0이므로 currentImageIndex(\(currentImageIndex)) 사용")
        }
        
        // 추가 안전장치: 여전히 0이면 viewerIndex 사용  
        if safeStartIndex == 0 && viewerIndex != 0 {
            safeStartIndex = viewerIndex
            print("[PlaceDetailView] 추가 안전장치: viewerIndex(\(viewerIndex)) 사용")
        }
        
        let clampedIndex = min(max(safeStartIndex, 0), max(imageUrls.count - 1, 0))
        print("[PlaceDetailView] imageViewer 생성 - lockedViewerIndex: \(lockedViewerIndex), safeStartIndex: \(safeStartIndex), clampedIndex: \(clampedIndex), viewerIndex: \(viewerIndex)")

        return ImageGalleryView(
            imageUrls: imageUrls,
            initialIndex: clampedIndex,
            currentIndex: Binding(
                get: { 
                    let currentValue = viewerIndex
                    print("[PlaceDetailView] imageViewer Binding get - viewerIndex: \(currentValue)")
                    return currentValue
                },
                set: { newValue in
                    let clamped = min(max(newValue, 0), max(imageUrls.count - 1, 0))
                    print("[PlaceDetailView] imageViewer Binding set - newValue: \(newValue), clamped: \(clamped), current viewerIndex: \(viewerIndex)")
                    // 실제로 값이 다를 때만 변경 (무한 루프 방지)
                    if viewerIndex != clamped {
                        viewerIndex = clamped
                        print("[PlaceDetailView] viewerIndex 업데이트: \(viewerIndex) -> \(clamped)")
                    }
                }
            ),
            topInset: safeAreaTop
        ) {
            print("[PlaceDetailView] imageViewer 닫기 - viewerIndex: \(viewerIndex), lockedIndex: \(lockedViewerIndex)")
            isImageViewerPresented = false
            // 닫을 때 lockedViewerIndex를 현재 viewerIndex로 업데이트 (다음 열기 준비)
            let finalIndex = max(viewerIndex, 0)  // 최소 0 보장
            lockedViewerIndex = finalIndex
            imageViewerStartIndex = finalIndex
            print("[PlaceDetailView] imageViewer 닫기 완료 - 최종 lockedViewerIndex: \(lockedViewerIndex)")
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
        let clampedInitialIndex = min(max(initialIndex, 0), max(imageUrls.count - 1, 0))
        self.initialIndex = clampedInitialIndex
        self._currentIndex = currentIndex
        self.topInset = topInset
        self.onClose = onClose
        _displayedIndex = State(initialValue: clampedInitialIndex)
        print("[ImageGalleryView] init - initialIndex: \(initialIndex), clampedInitialIndex: \(clampedInitialIndex), displayedIndex: \(clampedInitialIndex)")
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
            print("[ImageGalleryView] onAppear - initialIndex: \(initialIndex), displayedIndex: \(displayedIndex), currentIndex: \(currentIndex)")
            
            // displayedIndex가 initialIndex와 다르면 동기화
            if displayedIndex != initialIndex {
                print("[ImageGalleryView] displayedIndex를 \(displayedIndex) -> \(initialIndex)로 동기화")
                syncDisplayedIndex(with: initialIndex)
            }
            
            // currentIndex는 사용자가 선택한 값일 수 있으므로 강제 동기화하지 않음
            // 대신 initialIndex가 유효한 범위 내에 있는지만 확인
            let maxIndex = max(imageUrls.count - 1, 0)
            if initialIndex >= 0 && initialIndex <= maxIndex {
                print("[ImageGalleryView] currentIndex \(currentIndex) 유지 (사용자 선택 값)")
            } else {
                print("[ImageGalleryView] initialIndex \(initialIndex)가 범위를 벗어남 - currentIndex 유지")
            }
        }
        .onChange(of: imageUrls) { _, _ in
            let maxIndex = max(imageUrls.count - 1, 0)
            syncDisplayedIndex(with: min(displayedIndex, maxIndex))
            syncCurrentIndex(with: min(currentIndex, maxIndex))
        }
        .onChange(of: currentIndex) { _, newValue in
            syncDisplayedIndex(with: newValue)
        }
        .onChange(of: displayedIndex) { _, newValue in
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
        print("[ImageGalleryView] syncDisplayedIndex - target: \(target), clamped: \(clamped), current: \(displayedIndex)")
        if clamped != displayedIndex {
            print("[ImageGalleryView] displayedIndex 업데이트: \(displayedIndex) -> \(clamped)")
            displayedIndex = clamped
        } else {
            print("[ImageGalleryView] displayedIndex 이미 올바른 값: \(displayedIndex)")
        }
    }

    private func syncCurrentIndex(with target: Int) {
        let clamped = imageUrls.isEmpty ? 0 : min(max(target, 0), imageUrls.count - 1)
        print("[ImageGalleryView] syncCurrentIndex - target: \(target), clamped: \(clamped), current: \(currentIndex)")
        if clamped != currentIndex {
            print("[ImageGalleryView] currentIndex 업데이트: \(currentIndex) -> \(clamped)")
            currentIndex = clamped
        } else {
            print("[ImageGalleryView] currentIndex 이미 올바른 값: \(currentIndex)")
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
