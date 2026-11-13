package com.example.data.local

import com.example.data.model.PlaceCategory
import com.example.data.model.PlaceItem
import com.example.data.model.SearchHistoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class PlacesRepository(private val dao: FlickMapDao) {

    // Center default location: Downtown San Francisco / Metropolis (37.7749, -122.4194)
    val defaultUserLat = 37.7749
    val defaultUserLng = -122.4194

    private val initialPlaces = listOf(
        PlaceItem(
            id = "p1",
            name = "Flick Gourmet Bistro & Cafe",
            category = PlaceCategory.RESTAURANTS,
            rating = 4.8f,
            userRatingsTotal = 342,
            address = "540 Market St, Financial District",
            latitude = 37.7892,
            longitude = -122.4014,
            openingHours = "07:00 AM - 10:00 PM",
            phone = "+1 (415) 555-0182",
            website = "https://flickweb.com/bistro",
            priceLevel = 3,
            tag = "Top Rated",
            distanceKm = 0.6f,
            description = "Artisanal espresso, fresh organic breakfasts, and chef-curated evening tapas."
        ),
        PlaceItem(
            id = "p2",
            name = "Metropolitan Central Hospital",
            category = PlaceCategory.HOSPITALS,
            rating = 4.7f,
            userRatingsTotal = 890,
            address = "1001 Potrero Ave, Mission District",
            latitude = 37.7558,
            longitude = -122.4047,
            openingHours = "Open 24/7 (Emergency Service)",
            phone = "+1 (415) 555-0911",
            website = "https://flickweb.com/health",
            priceLevel = 2,
            tag = "24/7 Emergency",
            distanceKm = 2.4f,
            description = "Premier medical center with level-1 trauma services and advanced diagnostic facilities."
        ),
        PlaceItem(
            id = "p3",
            name = "Flick Academy of Tech & Design",
            category = PlaceCategory.SCHOOLS,
            rating = 4.9f,
            userRatingsTotal = 215,
            address = "750 Howard St, SoMa District",
            latitude = 37.7842,
            longitude = -122.4028,
            openingHours = "08:00 AM - 08:00 PM",
            phone = "+1 (415) 555-0344",
            website = "https://flickweb.com/academy",
            priceLevel = 2,
            tag = "STEM Innovation",
            distanceKm = 1.1f,
            description = "Leading educational center focusing on software design, robotics, and digital engineering."
        ),
        PlaceItem(
            id = "p4",
            name = "Flick Trust Bank ATM & Branch",
            category = PlaceCategory.ATMS,
            rating = 4.5f,
            userRatingsTotal = 94,
            address = "333 Bush St, Financial District",
            latitude = 37.7909,
            longitude = -122.4035,
            openingHours = "ATM 24/7 (Branch 9 AM - 5 PM)",
            phone = "+1 (415) 555-8822",
            website = "https://flickweb.com/finance",
            priceLevel = 1,
            tag = "Zero Fee",
            distanceKm = 0.9f,
            description = "24-hour contactless ATM with multi-currency withdrawals and Instant Cash deposit."
        ),
        PlaceItem(
            id = "p5",
            name = "Grand Cyan Luxury Hotel & Suites",
            category = PlaceCategory.HOTELS,
            rating = 4.9f,
            userRatingsTotal = 560,
            address = "50 Third St, Downtown",
            latitude = 37.7871,
            longitude = -122.4022,
            openingHours = "Check-in 3:00 PM / Check-out 11:00 AM",
            phone = "+1 (415) 555-7700",
            website = "https://flickweb.com/hotel",
            priceLevel = 4,
            tag = "5-Star Luxury",
            distanceKm = 0.7f,
            description = "Panoramic skyline views, rooftop lounge, high-speed fiber Wi-Fi, and luxury spa."
        ),
        PlaceItem(
            id = "p6",
            name = "Flick Clean Energy Petrol & EV Station",
            category = PlaceCategory.PETROL,
            rating = 4.6f,
            userRatingsTotal = 430,
            address = "1200 Harrison St, SoMa",
            latitude = 37.7765,
            longitude = -122.4089,
            openingHours = "Open 24/7",
            phone = "+1 (415) 555-3311",
            website = "https://flickweb.com/energy",
            priceLevel = 2,
            tag = "Fast Charging",
            distanceKm = 1.3f,
            description = "350kW Ultra-Fast EV chargers, premium unleaded fuel, car wash, and convenience market."
        ),
        PlaceItem(
            id = "p7",
            name = "Embarkadero Bay Plaza",
            category = PlaceCategory.OTHER,
            rating = 4.8f,
            userRatingsTotal = 1290,
            address = "1 Ferry Building, The Embarcadero",
            latitude = 37.7955,
            longitude = -122.3937,
            openingHours = "06:00 AM - 11:00 PM",
            phone = "+1 (415) 555-2200",
            website = "https://flickweb.com/plaza",
            priceLevel = 2,
            tag = "Scenic Waterfront",
            distanceKm = 2.1f,
            description = "Historic waterfront plaza featuring local artisan markets, ferry terminals, and ocean views."
        ),
        PlaceItem(
            id = "p8",
            name = "Cyan Sky Observation Tower",
            category = PlaceCategory.OTHER,
            rating = 4.9f,
            userRatingsTotal = 840,
            address = "1 Telegraph Hill Blvd",
            latitude = 37.8024,
            longitude = -122.4058,
            openingHours = "10:00 AM - 07:00 PM",
            phone = "+1 (415) 555-5599",
            website = "https://flickweb.com/tower",
            priceLevel = 2,
            tag = "Iconic Viewpoint",
            distanceKm = 3.2f,
            description = "Iconic tower offering 360-degree panoramic vistas of the Golden Gate and downtown bay."
        )
    )

    fun getAllPlaces(): List<PlaceItem> = initialPlaces

    fun getPlacesByCategory(category: PlaceCategory): List<PlaceItem> {
        return initialPlaces.filter { it.category == category }
    }

    fun searchPlaces(query: String): List<PlaceItem> {
        if (query.isBlank()) return initialPlaces
        val q = query.trim().lowercase()
        return initialPlaces.filter {
            it.name.lowercase().contains(q) ||
            it.address.lowercase().contains(q) ||
            it.category.displayName.lowercase().contains(q) ||
            it.tag.lowercase().contains(q)
        }
    }

    fun getFavoritePlacesFlow(): Flow<List<PlaceItem>> {
        return dao.getAllFavorites().map { entities ->
            entities.map { entity ->
                PlaceItem(
                    id = entity.id,
                    name = entity.name,
                    category = try { PlaceCategory.valueOf(entity.categoryName) } catch (_: Exception) { PlaceCategory.OTHER },
                    rating = entity.rating,
                    userRatingsTotal = entity.userRatingsTotal,
                    address = entity.address,
                    latitude = entity.latitude,
                    longitude = entity.longitude,
                    openingHours = entity.openingHours,
                    phone = entity.phone,
                    website = entity.website,
                    isFavorite = true,
                    tag = entity.customTag
                )
            }
        }
    }

    suspend fun toggleFavorite(place: PlaceItem): Boolean {
        val currentlyFav = dao.isFavorite(place.id)
        if (currentlyFav) {
            dao.deleteFavorite(place.id)
            return false
        } else {
            val entity = FavoritePlaceEntity(
                id = place.id,
                name = place.name,
                categoryName = place.category.name,
                rating = place.rating,
                userRatingsTotal = place.userRatingsTotal,
                address = place.address,
                latitude = place.latitude,
                longitude = place.longitude,
                openingHours = place.openingHours,
                phone = place.phone,
                website = place.website,
                customTag = place.tag
            )
            dao.insertFavorite(entity)
            return true
        }
    }

    suspend fun isFavorite(placeId: String): Boolean = dao.isFavorite(placeId)

    fun getRecentSearchesFlow(): Flow<List<SearchHistoryItem>> {
        return dao.getRecentSearches().map { entities ->
            entities.map { entity ->
                SearchHistoryItem(
                    id = entity.id,
                    query = entity.query,
                    placeName = entity.placeName,
                    timestamp = entity.timestamp
                )
            }
        }
    }

    suspend fun addRecentSearch(query: String, placeName: String? = null) {
        if (query.isBlank()) return
        val entity = SearchHistoryEntity(
            id = UUID.randomUUID().toString(),
            query = query.trim(),
            placeName = placeName,
            timestamp = System.currentTimeMillis()
        )
        dao.insertSearch(entity)
    }

    suspend fun deleteSearch(id: String) = dao.deleteSearch(id)

    suspend fun clearSearchHistory() = dao.clearAllSearches()
}
