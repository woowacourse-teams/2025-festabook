import SwiftUI

struct CurrentLocationButton: View {
    @ObservedObject var viewModel: MapViewModel

    var body: some View {
        Button(action: {
            viewModel.requestCurrentLocation()
        }) {
            Image(systemName: "scope")
                .font(.system(size: 20, weight: .medium))
                .foregroundColor(Color(UIColor.gray))
                .frame(width: 44, height: 44)
                .background(
                    RoundedRectangle(cornerRadius: 10, style: .continuous)
                        .fill(Color.white)
                )
                .overlay(
                    RoundedRectangle(cornerRadius: 10, style: .continuous)
                        .stroke(Color.black.opacity(0.08), lineWidth: 0.5)
                )
                .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)
        }
        .buttonStyle(PlainButtonStyle())
        .accessibilityLabel("현재 위치로 이동")
    }
}

#Preview {
    CurrentLocationButton(viewModel: MapViewModel())
}
