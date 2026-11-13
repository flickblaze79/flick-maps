package com.example.data.model

enum class TravelMode(val displayName: String, val icon: String) {
    DRIVING("Driving", "directions_car"),
    WALKING("Walking", "directions_walk"),
    CYCLING("Cycling", "directions_bike"),
    TRANSIT("Transit", "directions_bus")
}

data class LatLngPoint(
    val latitude: Double,
    val longitude: Double
)

data class NavigationStep(
    val id: String,
    val instruction: String,
    val distanceText: String,
    val durationText: String,
    val maneuverType: ManeuverType,
    val point: LatLngPoint
)

enum class ManeuverType {
    START,
    TURN_LEFT,
    TURN_RIGHT,
    KEEP_LEFT,
    KEEP_RIGHT,
    CONTINUE_STRAIGHT,
    ROUNDABOUT,
    ARRIVE
}

data class RouteResult(
    val id: String,
    val mode: TravelMode,
    val originName: String,
    val destinationName: String,
    val distanceKm: Double,
    val durationMinutes: Int,
    val trafficDelayMinutes: Int = 0,
    val summary: String,
    val isRecommended: Boolean = false,
    val points: List<LatLngPoint>,
    val steps: List<NavigationStep>
)
