package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldenYellow
import com.example.ui.theme.PurpleIndigo
import com.example.ui.theme.CyanAccent

enum class PlaceCategory(
    val displayName: String,
    val iconName: String,
    val hexColor: String
) {
    RESTAURANTS("Restaurants", "utensils", "#10B981"),
    HOSPITALS("Hospitals", "hospital", "#EF4444"),
    SCHOOLS("Schools", "school", "#8B5CF6"),
    ATMS("ATMs", "credit-card", "#F59E0B"),
    HOTELS("Hotels", "hotel", "#00E5FF"),
    PETROL("Petrol Stations", "gas-pump", "#FFC107"),
    OTHER("Points of Interest", "map-pin", "#94A3B8");

    fun getComposeColor(): Color = when (this) {
        RESTAURANTS -> EmeraldGreen
        HOSPITALS -> CrimsonRed
        SCHOOLS -> PurpleIndigo
        ATMS -> AmberOrange
        HOTELS -> CyanAccent
        PETROL -> GoldenYellow
        OTHER -> Color(0xFF94A3B8)
    }
}

data class PlaceItem(
    val id: String,
    val name: String,
    val category: PlaceCategory,
    val rating: Float = 4.5f,
    val userRatingsTotal: Int = 120,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val openingHours: String = "Open 24/7",
    val phone: String = "+1 (555) 019-2834",
    val website: String = "https://flickweb.com",
    val isFavorite: Boolean = false,
    val priceLevel: Int = 2, // 1: $, 2: $$, 3: $$$, 4: $$$$
    val tag: String = "Popular",
    val distanceKm: Float = 0.8f,
    val description: String = "Featured spot verified by Flick Maps."
)

