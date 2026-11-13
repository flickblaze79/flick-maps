package com.example.data.model

data class SearchHistoryItem(
    val id: String,
    val query: String,
    val placeName: String? = null,
    val category: PlaceCategory? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class UserProfile(
    val id: String = "user_guest",
    val name: String = "Flick Navigator",
    val email: String = "user@flickweb.com",
    val avatarUrl: String? = null,
    val isSignedIn: Boolean = false,
    val savedPlacesCount: Int = 0
)

enum class CustomMapType {
    NORMAL,
    SATELLITE,
    TERRAIN,
    HYBRID,
    MAPBOX_SATELLITE_3D
}


enum class DistanceUnit {
    KILOMETERS,
    MILES
}

enum class MapThemeStyle {
    DARK_CYAN,
    OLED_BLACK,
    STANDARD_DARK
}

data class MapConfig(
    val mapType: CustomMapType = CustomMapType.NORMAL,
    val showTraffic: Boolean = true,
    val showBuilding3D: Boolean = true,
    val distanceUnit: DistanceUnit = DistanceUnit.KILOMETERS,
    val themeStyle: MapThemeStyle = MapThemeStyle.DARK_CYAN,
    val defaultTravelMode: TravelMode = TravelMode.DRIVING,
    val isLocationSharingEnabled: Boolean = true,
    val isCloudSyncEnabled: Boolean = false
)
