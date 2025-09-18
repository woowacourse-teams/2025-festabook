import SwiftUI
import CoreLocation

struct CurrentLocationButton: View {
    @ObservedObject var viewModel: MapViewModel

    var body: some View {
        Button(action: {
            // No longer used - location button is now handled by NaverMapRepresentable
        }) {
            Image(systemName: "location.fill")
                .font(.system(size: 18, weight: .medium))
                .foregroundColor(.blue)
                .frame(width: 48, height: 48)
                .background(Color.white)
                .clipShape(Circle())
                .shadow(color: .black.opacity(0.2), radius: 6, x: 0, y: 3)
        }
    }
}

class LocationManager: NSObject, ObservableObject, CLLocationManagerDelegate {
    @Published var userLocation: CLLocation?
    @Published var authorizationStatus: CLAuthorizationStatus = .notDetermined

    private let manager = CLLocationManager()

    override init() {
        super.init()
        manager.delegate = self
        manager.desiredAccuracy = kCLLocationAccuracyBest
        authorizationStatus = manager.authorizationStatus
    }

    func requestLocation() {
        switch authorizationStatus {
        case .notDetermined:
            manager.requestWhenInUseAuthorization()
        case .authorizedWhenInUse, .authorizedAlways:
            manager.requestLocation()
        case .denied, .restricted:
            // Show settings alert
            print("[LocationManager] Location access denied")
        @unknown default:
            break
        }
    }

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else { return }
        userLocation = location
    }

    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("[LocationManager] Failed to get location: \(error)")
    }

    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        authorizationStatus = status
        if status == .authorizedWhenInUse || status == .authorizedAlways {
            manager.requestLocation()
        }
    }
}

#Preview {
    CurrentLocationButton(viewModel: MapViewModel())
}