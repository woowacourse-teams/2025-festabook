import SwiftUI

struct SettingsView: View {
    @StateObject private var notificationService = NotificationService.shared
    @EnvironmentObject var appState: AppState

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
                                Text("현재 접속 중인 대학 - \(appState.currentUniversityName)")
                                    .font(.system(size: 17, weight: .regular))
                                    .foregroundColor(.primary)
                                    .lineLimit(1) // 한 줄로 제한
                                    .truncationMode(.tail) // 말줄임표로 처리

                                Spacer() // 텍스트와 토글 사이 공간 확보

                                Toggle("", isOn: $notificationService.isNotificationEnabled)
                                    .tint(.black) // 토글 버튼 색상을 검정색으로 변경
                                    .scaleEffect(0.8) // 토글 버튼 크기 축소
                                    .frame(width: 40) // 토글 버튼 영역 제한
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
                        .padding(.top, 24)

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
            updateToggleState()

            // 설정 화면 진입 시 NotificationService의 저장된 상태를 다시 로드하여 동기화
            notificationService.objectWillChange.send()
        }
    }

    // MARK: - 토글 상태 업데이트 (설정 화면 진입 시 최신 상태로 동기화)
    private func updateToggleState() {
        // 설정 화면 진입 시 NotificationService의 현재 상태로 동기화
        print("[SettingsView] 설정 화면 진입 - 현재 알림 상태: \(notificationService.isNotificationEnabled)")

        // NotificationService의 상태가 이미 올바르게 설정되어 있어야 하므로
        // 별도 업데이트는 필요하지 않지만, 로그로 현재 상태 확인
        if notificationService.isFestivalSubscribed() {
            print("[SettingsView] 축제 구독 상태: 구독됨 (ID: \(notificationService.festivalNotificationId ?? -1))")
        } else {
            print("[SettingsView] 축제 구독 상태: 미구독")
        }
    }

    // MARK: - 토글 변경 처리
    private func handleToggleChange(_ newValue: Bool) {
        // 이전 값 백업 (API 실패 시 롤백용)
        let previousValue = notificationService.isNotificationEnabled

        Task {
            do {
                if newValue {
                    // 구독 - festival 헤더 제거된 API 호출
                    _ = try await notificationService.subscribeToFestivalNotifications(festivalId: appState.currentFestivalId)
                } else {
                    // 구독 취소 - festival 헤더 제거된 API 호출
                    try await notificationService.unsubscribeFromFestivalNotifications()
                }
                print("[SettingsView] 토글 변경 성공: \(newValue)")
            } catch {
                // API 실패 시 토글 상태 롤백
                await MainActor.run {
                    notificationService.isNotificationEnabled = previousValue
                    print("[SettingsView] 토글 변경 실패, 롤백: \(error)")
                }
            }
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
