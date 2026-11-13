package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.FlickMapDatabase
import com.example.data.local.PlacesRepository
import com.example.data.local.RouteRepository
import com.example.data.local.UserSettingsRepository
import com.example.data.model.CustomMapType
import com.example.data.model.DistanceUnit
import com.example.data.model.MapConfig
import com.example.data.model.MapThemeStyle
import com.example.data.model.PlaceCategory
import com.example.data.model.PlaceItem
import com.example.data.model.RouteResult
import com.example.data.model.SearchHistoryItem
import com.example.data.model.TravelMode
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MapUiState(
    val userLat: Double = 37.7749,
    val userLng: Double = -122.4194,
    val cameraLat: Double = 37.7749,
    val cameraLng: Double = -122.4194,
    val cameraZoom: Float = 14f,
    val searchQuery: String = "",
    val selectedCategory: PlaceCategory? = null,
    val placesList: List<PlaceItem> = emptyList(),
    val selectedPlace: PlaceItem? = null,
    val isPlaceSheetOpen: Boolean = false,
    val isDirectionsMode: Boolean = false,
    val originName: String = "Current Location",
    val destinationPlace: PlaceItem? = null,
    val selectedTravelMode: TravelMode = TravelMode.DRIVING,
    val routes: List<RouteResult> = emptyList(),
    val selectedRouteIndex: Int = 0,
    val isNavigatingActive: Boolean = false,
    val activeStepIndex: Int = 0,
    val userProfile: UserProfile = UserProfile(),
    val isAccountDialogOpen: Boolean = false,
    val snackbarMessage: String? = null
)

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val database = FlickMapDatabase.getInstance(application)
    private val placesRepository = PlacesRepository(database.dao())
    private val routeRepository = RouteRepository()
    private val settingsRepository = UserSettingsRepository(application)

    private val _uiState = MutableStateFlow(MapUiState(placesList = placesRepository.getAllPlaces()))
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    val mapConfig: StateFlow<MapConfig> = settingsRepository.configFlow

    val favoritePlaces: StateFlow<List<PlaceItem>> = placesRepository.getFavoritePlacesFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val recentSearches: StateFlow<List<SearchHistoryItem>> = placesRepository.getRecentSearchesFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        // Refresh places with initial favorite states
        viewModelScope.launch {
            favoritePlaces.collect { favs ->
                val favIds = favs.map { it.id }.toSet()
                val updatedList = _uiState.value.placesList.map { place ->
                    place.copy(isFavorite = favIds.contains(place.id))
                }
                _uiState.value = _uiState.value.copy(placesList = updatedList)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        val filtered = placesRepository.searchPlaces(query)
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            placesList = filtered
        )
    }

    fun performSearch(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            placesRepository.addRecentSearch(query)
            val filtered = placesRepository.searchPlaces(query)
            _uiState.value = _uiState.value.copy(
                searchQuery = query,
                placesList = filtered
            )
            if (filtered.isNotEmpty()) {
                selectPlace(filtered.first())
            } else {
                showSnackbar("No matching places found for '$query'")
            }
        }
    }

    fun selectCategory(category: PlaceCategory?) {
        val newCategory = if (_uiState.value.selectedCategory == category) null else category
        val list = if (newCategory == null) {
            placesRepository.getAllPlaces()
        } else {
            placesRepository.getPlacesByCategory(newCategory)
        }
        _uiState.value = _uiState.value.copy(
            selectedCategory = newCategory,
            placesList = list
        )
    }

    fun selectPlace(place: PlaceItem) {
        _uiState.value = _uiState.value.copy(
            selectedPlace = place,
            isPlaceSheetOpen = true,
            cameraLat = place.latitude,
            cameraLng = place.longitude,
            cameraZoom = 15.5f
        )
    }

    fun dismissPlaceSheet() {
        _uiState.value = _uiState.value.copy(isPlaceSheetOpen = false)
    }

    fun toggleFavorite(place: PlaceItem) {
        viewModelScope.launch {
            val isNowFav = placesRepository.toggleFavorite(place)
            val updatedPlace = place.copy(isFavorite = isNowFav)
            val updatedList = _uiState.value.placesList.map {
                if (it.id == place.id) updatedPlace else it
            }
            _uiState.value = _uiState.value.copy(
                placesList = updatedList,
                selectedPlace = if (_uiState.value.selectedPlace?.id == place.id) updatedPlace else _uiState.value.selectedPlace
            )
            val msg = if (isNowFav) "Saved '${place.name}' to Favorites" else "Removed '${place.name}' from Favorites"
            showSnackbar(msg)
        }
    }

    fun startDirectionsTo(destination: PlaceItem) {
        val mode = mapConfig.value.defaultTravelMode
        val routes = routeRepository.calculateRoutes(
            originName = "Current Location",
            originLat = _uiState.value.userLat,
            originLng = _uiState.value.userLng,
            destinationName = destination.name,
            destLat = destination.latitude,
            destLng = destination.longitude,
            mode = mode
        )
        _uiState.value = _uiState.value.copy(
            isDirectionsMode = true,
            destinationPlace = destination,
            selectedTravelMode = mode,
            routes = routes,
            selectedRouteIndex = 0,
            isPlaceSheetOpen = false,
            isNavigatingActive = false,
            cameraLat = (destination.latitude + _uiState.value.userLat) / 2,
            cameraLng = (destination.longitude + _uiState.value.userLng) / 2,
            cameraZoom = 13f
        )
    }

    fun changeTravelMode(mode: TravelMode) {
        val dest = _uiState.value.destinationPlace ?: return
        val routes = routeRepository.calculateRoutes(
            originName = _uiState.value.originName,
            originLat = _uiState.value.userLat,
            originLng = _uiState.value.userLng,
            destinationName = dest.name,
            destLat = dest.latitude,
            destLng = dest.longitude,
            mode = mode
        )
        _uiState.value = _uiState.value.copy(
            selectedTravelMode = mode,
            routes = routes,
            selectedRouteIndex = 0,
            activeStepIndex = 0
        )
    }

    fun selectRouteIndex(index: Int) {
        if (index in _uiState.value.routes.indices) {
            _uiState.value = _uiState.value.copy(
                selectedRouteIndex = index,
                activeStepIndex = 0
            )
        }
    }

    fun toggleNavigationMode(active: Boolean) {
        _uiState.value = _uiState.value.copy(
            isNavigatingActive = active,
            activeStepIndex = 0
        )
        if (active) {
            val route = _uiState.value.routes.getOrNull(_uiState.value.selectedRouteIndex)
            val firstStep = route?.steps?.firstOrNull()
            if (firstStep != null) {
                _uiState.value = _uiState.value.copy(
                    cameraLat = firstStep.point.latitude,
                    cameraLng = firstStep.point.longitude,
                    cameraZoom = 17f
                )
            }
            showSnackbar("Turn-by-turn navigation started")
        }
    }

    fun nextNavigationStep() {
        val route = _uiState.value.routes.getOrNull(_uiState.value.selectedRouteIndex) ?: return
        val currentIdx = _uiState.value.activeStepIndex
        if (currentIdx < route.steps.lastIndex) {
            val nextIdx = currentIdx + 1
            val nextStep = route.steps[nextIdx]
            _uiState.value = _uiState.value.copy(
                activeStepIndex = nextIdx,
                cameraLat = nextStep.point.latitude,
                cameraLng = nextStep.point.longitude,
                cameraZoom = 17f
            )
        } else {
            showSnackbar("Destination reached! You have arrived.")
            toggleNavigationMode(false)
        }
    }

    fun prevNavigationStep() {
        val route = _uiState.value.routes.getOrNull(_uiState.value.selectedRouteIndex) ?: return
        val currentIdx = _uiState.value.activeStepIndex
        if (currentIdx > 0) {
            val prevIdx = currentIdx - 1
            val prevStep = route.steps[prevIdx]
            _uiState.value = _uiState.value.copy(
                activeStepIndex = prevIdx,
                cameraLat = prevStep.point.latitude,
                cameraLng = prevStep.point.longitude,
                cameraZoom = 17f
            )
        }
    }

    fun exitDirectionsMode() {
        _uiState.value = _uiState.value.copy(
            isDirectionsMode = false,
            destinationPlace = null,
            routes = emptyList(),
            isNavigatingActive = false
        )
    }

    fun updateLocation(lat: Double, lng: Double) {
        _uiState.value = _uiState.value.copy(
            userLat = lat,
            userLng = lng,
            cameraLat = lat,
            cameraLng = lng,
            cameraZoom = 15f
        )
        showSnackbar("Location updated to GPS coordinates")
    }

    @android.annotation.SuppressLint("MissingPermission")
    fun recenterToUserLocation() {
        val context = getApplication<Application>()
        val fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    _uiState.value = _uiState.value.copy(
                        userLat = location.latitude,
                        userLng = location.longitude,
                        cameraLat = location.latitude,
                        cameraLng = location.longitude,
                        cameraZoom = 16f
                    )
                    showSnackbar("Real GPS location (Accuracy: ${location.accuracy}m)")
                } else {
                    _uiState.value = _uiState.value.copy(
                        cameraLat = _uiState.value.userLat,
                        cameraLng = _uiState.value.userLng,
                        cameraZoom = 15f
                    )
                    showSnackbar("Location unavailable. Using current coordinates.")
                }
            }
            .addOnFailureListener {
                _uiState.value = _uiState.value.copy(
                    cameraLat = _uiState.value.userLat,
                    cameraLng = _uiState.value.userLng,
                    cameraZoom = 15f
                )
                showSnackbar("Location unavailable. Check GPS settings.")
            }
    }


    fun zoomIn() {
        _uiState.value = _uiState.value.copy(cameraZoom = (_uiState.value.cameraZoom + 1f).coerceAtMost(20f))
    }

    fun zoomOut() {
        _uiState.value = _uiState.value.copy(cameraZoom = (_uiState.value.cameraZoom - 1f).coerceAtLeast(3f))
    }

    fun setMapType(type: CustomMapType) {
        settingsRepository.updateMapType(type)
        showSnackbar("Map layer updated to ${type.name}")
    }

    fun toggleTraffic() {
        val current = mapConfig.value.showTraffic
        settingsRepository.toggleTraffic(!current)
        val state = if (!current) "enabled" else "disabled"
        showSnackbar("Live traffic layer $state")
    }

    fun setDistanceUnit(unit: DistanceUnit) {
        settingsRepository.updateDistanceUnit(unit)
    }

    fun setThemeStyle(style: MapThemeStyle) {
        settingsRepository.updateThemeStyle(style)
    }

    fun setDefaultTravelMode(mode: TravelMode) {
        settingsRepository.updateDefaultTravelMode(mode)
    }

    fun toggleLocationSharing(enabled: Boolean) {
        settingsRepository.toggleLocationSharing(enabled)
    }

    fun toggleCloudSync(enabled: Boolean) {
        settingsRepository.toggleCloudSync(enabled)
        val msg = if (enabled) "Cloud sync activated with Firebase" else "Local mode active"
        showSnackbar(msg)
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            placesRepository.clearSearchHistory()
            showSnackbar("Search history cleared")
        }
    }

    fun deleteRecentSearch(id: String) {
        viewModelScope.launch {
            placesRepository.deleteSearch(id)
        }
    }

    fun toggleAccountDialog(open: Boolean) {
        _uiState.value = _uiState.value.copy(isAccountDialogOpen = open)
    }

    fun signInWithGoogle() {
        val updatedProfile = UserProfile(
            id = "user_google_1",
            name = "Flick Member",
            email = "user@flickweb.com",
            isSignedIn = true,
            savedPlacesCount = favoritePlaces.value.size
        )
        _uiState.value = _uiState.value.copy(
            userProfile = updatedProfile,
            isAccountDialogOpen = false
        )
        settingsRepository.toggleCloudSync(true)
        showSnackbar("Signed in successfully via Google Sign-In")
    }

    fun signOut() {
        _uiState.value = _uiState.value.copy(
            userProfile = UserProfile(),
            isAccountDialogOpen = false
        )
        settingsRepository.toggleCloudSync(false)
        showSnackbar("Signed out from account")
    }

    fun showSnackbar(message: String) {
        _uiState.value = _uiState.value.copy(snackbarMessage = message)
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }
}
