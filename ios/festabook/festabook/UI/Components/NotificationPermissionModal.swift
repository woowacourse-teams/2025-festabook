import SwiftUI

// MARK: - 알림 권한 모달
struct NotificationPermissionModal: View {
    @Binding var isPresented: Bool
    let onAllow: () -> Void
    let onLater: () -> Void
    
    var body: some View {
        ZStack {
            // 배경 오버레이 (반투명 검정색)
            Color.black.opacity(0.5)
                .ignoresSafeArea()
                // 배경 탭 시 모달 닫기 방지 (버튼으로만 닫기)
            
            // 모달 컨텐츠
            VStack(spacing: 16) {
                // 벨 아이콘 (검정색)
                Image(systemName: "bell.fill")
                    .font(.system(size: 32))
                    .foregroundColor(.black)
                
                // 제목 (굵은 텍스트) - 강제 줄바꿈
                VStack(spacing: 2) {
                    Text("우리 학교 축제의")
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(.black)
                    Text("최신 정보를 놓치지 마세요!")
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(.black)
                }
                .multilineTextAlignment(.center)
                .padding(.horizontal, 8)
                
                // 설명 텍스트 (일반 텍스트)
                Text("연예인 공연 정보, 우천 취소, 일정 변경 등 축제의 중요한 소식을 가장 빠르게 알려드릴게요.")
                    .font(.system(size: 11))
                    .foregroundColor(.black)
                    .multilineTextAlignment(.center)
                    .lineLimit(nil)
                    .padding(.horizontal, 4)
                
                // 버튼들 (크기 축소)
                HStack(spacing: 8) {
                    // 다음에 버튼 (흰색 배경 + 회색 텍스트)
                    Button(action: onLater) {
                        Text("다음에")
                            .font(.system(size: 11, weight: .medium))
                            .foregroundColor(.gray)
                            .frame(maxWidth: .infinity)
                            .frame(height: 32)
                            .background(Color.white)
                            .overlay(
                                RoundedRectangle(cornerRadius: 8)
                                    .stroke(Color.gray.opacity(0.3), lineWidth: 1)
                            )
                            .cornerRadius(8)
                    }

                    // 알림 받기 버튼 (검정색 배경 + 흰색 텍스트)
                    Button(action: onAllow) {
                        Text("알림 받기")
                            .font(.system(size: 11, weight: .medium))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 32)
                            .background(Color.black)
                            .cornerRadius(8)
                    }
                }
                .padding(.top, 4)
            }
            .padding(18)
            .background(Color.white)
            .cornerRadius(16)
            .shadow(color: .black.opacity(0.15), radius: 12, x: 0, y: 6)
            .padding(.horizontal, 40)
        }
    }
}

// MARK: - 프리뷰
struct NotificationPermissionModal_Previews: PreviewProvider {
    @State static var isPresented = true
    
    static var previews: some View {
        NotificationPermissionModal(
            isPresented: $isPresented,
            onAllow: {
                print("알림 받기 선택")
            },
            onLater: {
                print("다음에 선택")
            }
        )
    }
}
