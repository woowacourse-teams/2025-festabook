// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "festabook",
    platforms: [
        .iOS(.v15)
    ],
    dependencies: [
        .package(url: "https://github.com/navermaps/ios-map-sdk", from: "3.16.0")
    ],
    targets: [
        .target(
            name: "festabook",
            dependencies: [
                .product(name: "NMapsMap", package: "ios-map-sdk")
            ]
        )
    ]
)