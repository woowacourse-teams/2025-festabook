import SwiftUI

struct UniversitySearchView: View {
    @EnvironmentObject private var locator: ServiceLocator
    @EnvironmentObject private var appState: AppState
    @State private var searchText = ""
    @State private var festivals: [Festival] = []
    @State private var isLoading = false
    @State private var searchTask: Task<Void, Never>?
    @State private var isProcessingSelection = false
    @FocusState private var isSearchFieldFocused: Bool

    var body: some View {
        NavigationStack {
            ZStack {
                // 배경색 - 안드로이드와 동일한 연한 회색
                Color(.systemBackground)
                    .ignoresSafeArea(.all)

                VStack(spacing: 0) {
                    // 상단 로고 섹션 - 안드로이드 스타일
                    VStack(spacing: 0) {
                        // 로고 이미지
                        Image("festabook_logo")
                            .resizable()
                            .aspectRatio(contentMode: .fit)
                            .frame(width: 200, height: 60)
                            .padding(.top, 80)
                    }
                    .padding(.bottom, 15)

                    // 검색 영역 - 라벨과 검색창을 하나의 그룹으로
                    VStack(spacing: 8) {
                        // 검색창 라벨 - 안드로이드 크기에 맞게 축소
                        Text("어떤 축제로 떠나볼까요?")
                            .font(.system(size: 14))
                            .foregroundColor(.black)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(.horizontal, 40)

                        // 검색 바
                        HStack {
                            HStack(spacing: 0) {
                                TextField("대학교", text: $searchText)
                                    .font(.system(size: 14))
                                    .foregroundColor(.black)
                                    .padding(.leading, 20)
                                    .focused($isSearchFieldFocused)

                                Spacer()

                                Image(systemName: "magnifyingglass")
                                    .font(.system(size: 20))
                                    .foregroundColor(isSearchFieldFocused ? .blue : .black)
                                    .padding(.trailing, 20)
                            }
                            .frame(height: 50)
                            .background(Color.clear)
                            .overlay(
                                RoundedRectangle(cornerRadius: 25)
                                    .stroke(isSearchFieldFocused ? Color.blue : Color.black, lineWidth: isSearchFieldFocused ? 2 : 1)
                            )
                        }
                        .padding(.horizontal, 40)
                    }
                    .padding(.bottom, 20)

                    // 대학교 목록 - 안드로이드 스타일 완벽 매칭
                    if !festivals.isEmpty {
                        ScrollView {
                            LazyVStack(spacing: 0) {
                                ForEach(festivals) { festival in
                                    Button(action: {
                                        if !isProcessingSelection {
                                            selectFestival(festival)
                                        }
                                    }) {
                                        HStack {
                                            VStack(alignment: .leading, spacing: 4) {
                                                // 대학교 이름 - 메인 텍스트
                                                Text(festival.universityName)
                                                    .font(.system(size: 16, weight: .medium))
                                                    .foregroundColor(.black)

                                                // 축제 이름 - 서브 텍스트 (줄바꿈 무시하고 한 줄로 표시)
                                                Text(festival.festivalName.replacingOccurrences(of: "\n", with: " "))
                                                    .font(.system(size: 14))
                                                    .foregroundColor(.gray)
                                                    .lineLimit(1)
                                            }
                                            Spacer()
                                        }
                                        .padding(.horizontal, 40)
                                        .padding(.vertical, 12)
                                        .background(Color.clear)
                                    }
                                    .buttonStyle(PlainButtonStyle())

                                    // 구분선 (마지막 아이템 제외) - 안드로이드 스타일
                                    if festival.id != festivals.last?.id {
                                        Rectangle()
                                            .fill(Color.gray.opacity(0.2))
                                            .frame(height: 0.5)
                                            .padding(.horizontal, 40)
                                    }
                                }
                            }
                        }
                    } else if !isLoading {
                        // 축제 목록이 비어있을 때 표시
                        VStack(spacing: 16) {
                            Image(systemName: "magnifyingglass")
                                .font(.system(size: 48))
                                .foregroundColor(.gray.opacity(0.5))
                            
                            Text("검색된 축제가 없습니다")
                                .font(.system(size: 16))
                                .foregroundColor(.gray)
                            
                            Text("다른 대학교 이름을 검색해보세요")
                                .font(.system(size: 14))
                                .foregroundColor(.gray.opacity(0.7))
                        }
                        .padding(.top, 80)
                    }

                    Spacer()
                }
            }
            .overlay(alignment: .center) {
                if isLoading {
                    ProgressView("검색 중...")
                        .padding()
                        .background(Color(.systemBackground))
                        .cornerRadius(8)
                        .shadow(radius: 2)
                }
            }
            .navigationTitle("")
            .navigationBarHidden(true)
            .onChange(of: searchText) {
                performSearch()
            }
            .onTapGesture {
                // 화면 터치 시 키보드 숨기기 (검색창 영역 제외)
                hideKeyboard()
            }
            .task {
                if appState.initialFestivalList.isEmpty {
                    await loadFestivals(universityName: "")
                } else {
                    festivals = appState.initialFestivalList
                    isLoading = false
                }

                // 대학 선택 화면 진입 시 시스템 알림 권한 요청 (최초 1회)
                await requestSystemNotificationPermissionOnce()
            }
            .onChange(of: appState.initialFestivalList) { newValue in
                festivals = newValue
                isLoading = false
            }
        }
    }

    // 키보드 숨기기
    private func hideKeyboard() {
        isSearchFieldFocused = false
    }

    // 실시간 검색 수행
    private func performSearch() {
        // 이전 검색 태스크 취소
        searchTask?.cancel()
        
        // 새로운 검색 태스크 시작
        searchTask = Task {
            // 0.3초 디바운스 (연속 입력 시 마지막 입력만 처리)
            try? await Task.sleep(nanoseconds: 300_000_000) // 0.3초
            
            if !Task.isCancelled {
                await loadFestivals(universityName: searchText)
            }
        }
    }
    
    // 축제 목록 로드 (검색어 포함)
    @MainActor
    private func loadFestivals(universityName: String) async {
        isLoading = true
        
        do {
            // 검색어에 따라 API 호출
            festivals = try await locator.festivalRepo.getFestivalsByUniversity(universityName: universityName)
            if universityName.isEmpty {
                appState.initialFestivalList = festivals
            }
        } catch {
            print("축제 목록 로드 실패: \(error)")
            // 에러 시 빈 배열로 설정
            festivals = []
        }
        
        isLoading = false
    }
    
    // 축제 선택 처리
    private func selectFestival(_ festival: Festival) {
        guard !isProcessingSelection else {
            print("[UniversitySearchView] 이미 선택 처리 중 - 무시")
            return
        }

        print("[UniversitySearchView] 선택된 축제: \(festival.universityName) - ID: \(festival.festivalId)")

        isProcessingSelection = true

        // 바로 대학 선택 완료 처리
        completeUniversitySelection(festival)
    }

    // MARK: - 시스템 알림 권한 요청 (최초 1회)
    private func requestSystemNotificationPermissionOnce() async {
        // UserDefaults로 이미 요청했는지 확인
        let hasRequestedPermission = UserDefaults.standard.bool(forKey: "hasRequestedSystemNotificationPermission")

        if hasRequestedPermission {
            print("[UniversitySearchView] ✅ 시스템 알림 권한 이미 요청함 - 스킵")
            return
        }

        print("[UniversitySearchView] 🚀 시스템 권한 요청 시작")

        do {
            let granted = try await UNUserNotificationCenter.current().requestAuthorization(
                options: [.alert, .badge, .sound]
            )

            print("[UniversitySearchView] ✅ 시스템 알림 권한 요청 결과: \(granted ? "허용" : "거부")")

            if granted {
                // 권한 허용 시 APNs 등록
                await MainActor.run {
                    if let appDelegate = UIApplication.shared.delegate as? AppDelegate {
                        appDelegate.registerForAPNS()
                    }
                }
            }

            // 한 번 요청했음을 기록 (결과와 관계없이)
            UserDefaults.standard.set(true, forKey: "hasRequestedSystemNotificationPermission")

        } catch {
            print("[UniversitySearchView] ❌ 시스템 알림 권한 요청 실패: \(error)")
            UserDefaults.standard.set(true, forKey: "hasRequestedSystemNotificationPermission")
        }
    }

    // 대학 선택 완료 처리
    private func completeUniversitySelection(_ festival: Festival) {
        // 선택된 축제의 ID를 앱 전역 상태에 반영 (영속 + API 헤더 동기화 포함)
        appState.changeFestival(festival.festivalId)

        // 설정 확인
        print("[UniversitySearchView] AppState 축제 ID: \(appState.currentFestivalId)")
        print("[UniversitySearchView] APIClient 축제 ID: \(locator.api.currentFestivalId)")

        // 대학 정보를 University 객체로 변환하여 설정
        let university = University(id: festival.festivalId, name: festival.universityName, latitude: nil, longitude: nil)
        appState.selectUniversity(university)

        // 축제 정보도 설정
        appState.selectedFestival = festival

        print("[UniversitySearchView] 축제 선택 완료 - 홈 화면으로 이동")

        // 처리 완료 후 플래그 리셋
        isProcessingSelection = false
    }

}
