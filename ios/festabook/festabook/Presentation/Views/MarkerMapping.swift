// MarkerMapping.swift - Assets Catalog based mapping
import Foundation

enum MarkerCategory: String {
    case booth
    case bar
    case foodtruck
    case stage
    case photobooth
    case smoking
    case trash
    case toilet
    case parking
    case primary
    case extra
}

let MarkerIconByCategory: [MarkerCategory: String] = [
    .booth: "marker_booth",
    .bar: "marker_bar",
    .foodtruck: "marker_foodtruck",
    .stage: "marker_stage",
    .photobooth: "marker_photobooth",
    .smoking: "marker_smoking",
    .trash: "marker_trash",
    .toilet: "marker_toilet",
    .parking: "marker_parking",
    .primary: "marker_primary",
    .extra: "marker_extra"
]
