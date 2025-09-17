import SwiftUI

struct ScheduleView: View {
    @ObservedObject var viewModel: ScheduleViewModel

    init(viewModel: ScheduleViewModel) {
        self.viewModel = viewModel
    }

    var body: some View {
        VStack(spacing: 0) {
            // 작은 섹션 타이틀
            HStack {
                Text("일정 타임라인")
                    .font(.system(size: 24, weight: .bold))
                    .foregroundColor(.primary)
                Spacer()
            }
            .padding(.horizontal, 20)
            .padding(.top, 16)
            .padding(.bottom, 12)

            // 상단 날짜 선택 탭
            dateSelectionTabs

            Divider()

            // 타임라인 컨텐츠
            timelineContent
        }
        .task {
            await viewModel.loadEventDates()
        }
    }

    // MARK: - 날짜 선택 탭
    private var dateSelectionTabs: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 12) {
                ForEach(viewModel.eventDates) { eventDate in
                    dateTab(for: eventDate)
                }
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 12)
        }
        .background(Color(.systemBackground))
    }

    private func dateTab(for eventDate: EventDate) -> some View {
        Button(action: {
            viewModel.selectDate(eventDate)
        }) {
            Text(eventDate.displayDate)
                .font(.system(size: 15, weight: .medium))
                .foregroundColor(viewModel.selectedEventDate?.eventDateId == eventDate.eventDateId ? .white : .gray)
                .padding(.horizontal, 16)
                .padding(.vertical, 10)
                .background(
                    RoundedRectangle(cornerRadius: 20)
                        .fill(viewModel.selectedEventDate?.eventDateId == eventDate.eventDateId ? Color.black : Color.clear)
                )
        }
    }

    // MARK: - 타임라인 컨텐츠
    private var timelineContent: some View {
        Group {
            if viewModel.isLoadingEvents || viewModel.isLoadingDates {
                ProgressView("로딩 중...")
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else if let errorMessage = viewModel.errorMessage {
                VStack(spacing: 16) {
                    Image(systemName: "exclamationmark.triangle")
                        .font(.system(size: 50))
                        .foregroundColor(.orange)
                    Text(errorMessage)
                        .font(.body)
                        .multilineTextAlignment(.center)
                    Button("다시 시도") {
                        Task {
                            await viewModel.loadEventDates()
                        }
                    }
                    .buttonStyle(.bordered)
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else {
                timelineList
            }
        }
    }

    private var timelineList: some View {
        ScrollView {
            ZStack(alignment: .topLeading) {
                // Background timeline line
                if !viewModel.events.isEmpty {
                    Rectangle()
                        .fill(Color.gray.opacity(0.3))
                        .frame(width: 2)
                        .padding(.leading, 29) // 20 padding + 9 to center in 20pt dot area
                }

                // Event cards and dots
                LazyVStack(spacing: 24) {
                    ForEach(Array(viewModel.events.enumerated()), id: \.element.id) { index, event in
                        TimelineEventRow(
                            event: event,
                            isFirst: index == 0,
                            isLast: index == viewModel.events.count - 1
                        )
                    }
                }
                .padding(.horizontal, 20)
                .padding(.top, 20)
                .padding(.bottom, 40)
            }
        }
        .refreshable {
            await viewModel.loadEventDates()
        }
    }
}

struct TimelineEventRow: View {
    let event: ScheduleEvent
    let isFirst: Bool
    let isLast: Bool

    var body: some View {
        HStack(alignment: .center, spacing: 20) {
            // 왼쪽 dot만 (선은 배경에서 그려짐)
            ZStack {
                // 파동 애니메이션 (진행중일 때만)
                if event.status == .ongoing {
                    WaveAnimationView()
                }

                // 중앙 점
                Circle()
                    .fill(dotColor)
                    .frame(width: 12, height: 12)
            }
            .frame(width: 20, height: 12)

            // 오른쪽 이벤트 카드
            eventCard
                .frame(maxWidth: .infinity)
        }
    }

    private var dotColor: Color {
        switch event.status {
        case .completed: return Color.gray
        case .ongoing: return .blue
        case .upcoming: return .green
        }
    }

    private var eventCard: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack(alignment: .top) {
                VStack(alignment: .leading, spacing: 8) {
                    Text(event.title)
                        .font(.system(size: 18, weight: .medium))
                        .foregroundColor(textColor)

                    HStack(spacing: 6) {
                        Image(systemName: "clock")
                            .font(.system(size: 14))
                            .foregroundColor(iconColor)
                        Text(event.timeRange)
                            .font(.system(size: 14))
                            .foregroundColor(textColor)
                    }

                    HStack(spacing: 6) {
                        Image(systemName: "location")
                            .font(.system(size: 14))
                            .foregroundColor(iconColor)
                        Text(event.location)
                            .font(.system(size: 14))
                            .foregroundColor(textColor)
                    }
                }

                Spacer()

                // 상태 뱃지
                statusBadge
            }
        }
        .padding(20)
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(Color.white)
                .overlay(
                    RoundedRectangle(cornerRadius: 12)
                        .stroke(borderColor, lineWidth: 1.5)
                )
        )
    }

    private var textColor: Color {
        switch event.status {
        case .completed: return Color.gray
        case .ongoing, .upcoming: return Color.black
        }
    }

    private var iconColor: Color {
        switch event.status {
        case .completed: return Color.gray
        case .ongoing, .upcoming: return Color.gray
        }
    }

    private var borderColor: Color {
        switch event.status {
        case .completed: return Color.gray
        case .ongoing: return Color.blue
        case .upcoming: return Color.green
        }
    }

    private var statusBadge: some View {
        Group {
            switch event.status {
            case .ongoing:
                Text("진행중")
                    .font(.system(size: 12, weight: .regular))
                    .foregroundColor(.white)
                    .padding(.horizontal, 12)
                    .padding(.vertical, 6)
                    .background(
                        RoundedRectangle(cornerRadius: 6)
                            .fill(Color.black)
                    )
            case .upcoming:
                Text("예정")
                    .font(.system(size: 12, weight: .regular))
                    .foregroundColor(.black)
                    .padding(.horizontal, 12)
                    .padding(.vertical, 6)
                    .background(
                        RoundedRectangle(cornerRadius: 6)
                            .fill(Color.white)
                            .overlay(
                                RoundedRectangle(cornerRadius: 6)
                                    .stroke(Color.black, lineWidth: 1)
                            )
                    )
            case .completed:
                Text("종료")
                    .font(.system(size: 12, weight: .regular))
                    .foregroundColor(Color.gray)
            }
        }
    }
}

struct WaveAnimationView: View {
    @State private var animationScale: CGFloat = 1.0
    @State private var animationOpacity: Double = 1.0

    var body: some View {
        Circle()
            .stroke(Color.blue, lineWidth: 2)
            .scaleEffect(animationScale)
            .opacity(animationOpacity)
            .onAppear {
                withAnimation(
                    .easeOut(duration: 1.5)
                    .repeatForever(autoreverses: false)
                ) {
                    animationScale = 3.0
                    animationOpacity = 0.0
                }
            }
    }
}
