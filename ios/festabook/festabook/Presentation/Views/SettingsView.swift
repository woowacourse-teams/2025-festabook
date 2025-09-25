import SwiftUI

struct SettingsView: View {
    @StateObject private var notificationService = NotificationService.shared
    @EnvironmentObject var appState: AppState
    @State private var isToggleLocked = false
    @State private var shouldIgnoreToggleChange = false
    @State private var lastCommittedToggleValue = false

    var body: some View {
        VStack(spacing: 0) {
            // 상단 제목 - 일정/소식과 동일한 스타일
            HStack {
                Text("설정")
                    .font(.system(size: 22, weight: .bold))
                    .foregroundColor(.primary)
                Spacer()
            }
            .padding(.horizontal, 20)
            .padding(.top, 16)
            .padding(.bottom, 16)
            .background(Color.white)

            ScrollView {
                VStack(spacing: 0) {
                    // 알림 섹션
                    VStack(spacing: 0) {
                        // 섹션 헤더
                        HStack {
                            Text("알림")
                                .font(.system(size: 13, weight: .regular))
                                .foregroundColor(.secondary)
                                .textCase(.uppercase)
                            Spacer()
                        }
                        .padding(.horizontal, 20)
                        .padding(.top, 16)
                        .padding(.bottom, 8)
                        .background(Color.white)

                        // 알림 설정 항목들
                        VStack(spacing: 0) {
                            // 현재 접속 중인 대학 - {universityName}
                            HStack(alignment: .center, spacing: 12) {
                                VStack(alignment: .leading, spacing: 4) {
                                    Text("현재 접속 중인 대학")
                                        .font(.system(size: 17, weight: .regular))
                                        .foregroundColor(.primary)
                                    
                                    Text(appState.currentUniversityName)
                                        .font(.system(size: 15, weight: .regular))
                                        .foregroundColor(.secondary)
                                        .lineLimit(nil) // 여러 줄 허용
                                }

                                Spacer() // 텍스트와 토글 사이 공간 확보

                                Toggle("", isOn: $notificationService.isNotificationEnabled)
                                    .tint(.black) // 토글 버튼 색상을 검정색으로 변경
                                    .scaleEffect(0.8) // 토글 버튼 크기 축소
                                    .frame(width: 40) // 토글 버튼 영역 제한
                                    .disabled(isToggleLocked)
                                    .onChange(of: notificationService.isNotificationEnabled) { _, newValue in
                                        handleToggleChange(newValue)
                                    }
                            }
                            .padding(.horizontal, 20)
                            .padding(.vertical, 16)
                            .background(Color.white)
                        }
                    }

                    // 섹션 간 구분선
                    Divider()
                        .padding(.top, 8)

                    // 앱 정보 섹션
                    VStack(spacing: 0) {
                        // 섹션 헤더
                        HStack {
                            Text("앱 정보")
                                .font(.system(size: 13, weight: .regular))
                                .foregroundColor(.secondary)
                                .textCase(.uppercase)
                            Spacer()
                        }
                        .padding(.horizontal, 20)
                        .padding(.top, 24)
                        .padding(.bottom, 8)
                        .background(Color.white)

                        // 앱 정보 항목들
                        VStack(spacing: 0) {
                            // 앱 버전
                            HStack {
                                Text("앱 버전")
                                    .font(.system(size: 17))
                                    .foregroundColor(.primary)
                                Spacer()
                                Text(getAppVersion())
                                    .font(.system(size: 17))
                                    .foregroundColor(.secondary)
                            }
                            .padding(.horizontal, 20)
                            .padding(.vertical, 16)
                            .background(Color.white)

                            // 약관 및 정책
                            Button(action: {
                                openURL("https://spark-flea-8b5.notion.site/244a540dc0b780638e56e31c4bdb3c9f")
                            }) {
                                HStack {
                                    Text("약관 및 정책")
                                        .font(.system(size: 17))
                                        .foregroundColor(.primary)
                                    Spacer()
                                    Image(systemName: "chevron.right")
                                        .font(.system(size: 14, weight: .semibold))
                                        .foregroundColor(Color(.tertiaryLabel))
                                }
                                .padding(.horizontal, 20)
                                .padding(.vertical, 16)
                                .background(Color.white)
                            }

                            // 개발자 문의하기
                            Button(action: {
                                openURL("https://forms.gle/h3kacHPc7y6tCRQS8")
                            }) {
                                HStack {
                                    Text("개발자 문의하기")
                                        .font(.system(size: 17))
                                        .foregroundColor(.primary)
                                    Spacer()
                                    Image(systemName: "chevron.right")
                                        .font(.system(size: 14, weight: .semibold))
                                        .foregroundColor(Color(.tertiaryLabel))
                                }
                                .padding(.horizontal, 20)
                                .padding(.vertical, 16)
                                .background(Color.white)
                            }
                        }
                    }

                    // 하단 여백
                    Spacer(minLength: 50)
                }
            }
            .background(Color.white)
        }
        .background(Color.white)
        .onAppear {
            notificationService.refreshNotificationEnabledState(for: appState.currentFestivalId)
            updateToggleState()
            lastCommittedToggleValue = notificationService.isNotificationEnabled

            // 설정 화면 진입 시 NotificationService의 저장된 상태를 다시 로드하여 동기화
            notificationService.objectWillChange.send()

            Task {
                await notificationService.synchronizeSubscriptionsWithServer(
                    focusFestivalId: appState.currentFestivalId,
                    focusUniversityName: appState.currentUniversityName
                )
            }
        }
        .onChange(of: appState.currentFestivalId) { _, newValue in
            notificationService.refreshNotificationEnabledState(for: newValue)
            updateToggleState()
            lastCommittedToggleValue = notificationService.isNotificationEnabled

            Task {
                await notificationService.synchronizeSubscriptionsWithServer(
                    focusFestivalId: newValue,
                    focusUniversityName: appState.currentUniversityName
                )
            }
        }
    }

    // MARK: - 토글 상태 업데이트 (설정 화면 진입 시 최신 상태로 동기화)
    private func updateToggleState() {
        // 설정 화면 진입 시 NotificationService의 현재 상태로 동기화
        print("[SettingsView] 설정 화면 진입 - 현재 알림 상태: \(notificationService.isNotificationEnabled)")

        guard let festivalId = appState.currentFestivalId else {
            print("[SettingsView] ⚠️ 현재 축제 ID 없음 - 토글 비활성 상태")
            notificationService.updateNotificationEnabled(false, for: nil)
            return
        }

        // NotificationService의 상태가 이미 올바르게 설정되어 있어야 하므로
        // 별도 업데이트는 필요하지 않지만, 로그로 현재 상태 확인
        if notificationService.isFestivalSubscribed(festivalId: festivalId) {
            let subscriptionId = notificationService.festivalNotificationId(for: festivalId) ?? -1
            print("[SettingsView] 축제 구독 상태: 구독됨 (ID: \(subscriptionId))")
        } else {
            print("[SettingsView] 축제 구독 상태: 미구독")
        }
    }

    // MARK: - 토글 변경 처리
    private func handleToggleChange(_ newValue: Bool) {
        guard !shouldIgnoreToggleChange else {
            shouldIgnoreToggleChange = false
            return
        }

        guard !isToggleLocked else {
            revertToggle(to: lastCommittedToggleValue)
            return
        }

        let previousValue = lastCommittedToggleValue
        isToggleLocked = true

        Task {
            guard let festivalId = appState.currentFestivalId else {
                await revertToggle(to: previousValue)
                await MainActor.run {
                    print("[SettingsView] ❌ 축제 ID가 없어 알림 구독을 변경할 수 없습니다")
                }
                await unlockToggleAfterDelay()
                return
            }

            await MainActor.run {
                notificationService.updateNotificationEnabled(newValue, for: festivalId)
            }

            do {
                if newValue {
                    // 구독 - festival 헤더 제거된 API 호출
                    _ = try await notificationService.subscribeToFestivalNotifications(
                        festivalId: festivalId,
                        universityName: appState.currentUniversityName
                    )
                } else {
                    // 구독 취소 - festival 헤더 제거된 API 호출
                    try await notificationService.unsubscribeFromFestivalNotifications(festivalId: festivalId)
                }
                print("[SettingsView] 토글 변경 성공: \(newValue)")
                await MainActor.run {
                    lastCommittedToggleValue = newValue
                }
            } catch {
                await revertToggle(to: previousValue)
                await MainActor.run {
                    print("[SettingsView] 토글 변경 실패, 롤백: \(error)")
                }
            }

            await unlockToggleAfterDelay()
        }
    }

    @MainActor
    private func revertToggle(to value: Bool) {
        shouldIgnoreToggleChange = true
        notificationService.updateNotificationEnabled(value, for: appState.currentFestivalId)
        lastCommittedToggleValue = value
        Task { @MainActor in
            shouldIgnoreToggleChange = false
        }
    }

    private func unlockToggleAfterDelay() async {
        try? await Task.sleep(nanoseconds: 1_000_000_000)
        await MainActor.run {
            isToggleLocked = false
        }
    }

    // MARK: - 앱 버전 가져오기
    private func getAppVersion() -> String {
        guard let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String else {
            return "0.1.4"
        }
        return version
    }

    // MARK: - URL 열기
    private func openURL(_ urlString: String) {
        guard let url = URL(string: urlString) else { return }
        UIApplication.shared.open(url)
    }
}
