package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MapViewModel
import com.example.ui.components.AccountDialog
import com.example.ui.components.FlickBottomNav
import com.example.ui.components.FlickTopBar
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.DirectionsScreen
import com.example.ui.screens.ExploreMapScreen
import com.example.ui.screens.SavedPlacesScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.CharcoalBackground
import com.example.ui.theme.FlickMapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            FlickMapTheme {
                FlickMapApp()
            }
        }
    }
}

@Composable
fun FlickMapApp(
    viewModel: MapViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val mapConfig by viewModel.mapConfig.collectAsState()
    val favoritePlaces by viewModel.favoritePlaces.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()

    var selectedNavTab by remember { mutableIntStateOf(0) }
    var isAboutScreenVisible by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Display snackbar alerts for user feedback
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Custom back navigation handling
    BackHandler(enabled = isAboutScreenVisible || selectedNavTab != 0 || uiState.isDirectionsMode || uiState.isPlaceSheetOpen) {
        when {
            isAboutScreenVisible -> isAboutScreenVisible = false
            uiState.isPlaceSheetOpen -> viewModel.dismissPlaceSheet()
            uiState.isDirectionsMode -> viewModel.exitDirectionsMode()
            selectedNavTab != 0 -> selectedNavTab = 0
        }
    }

    Scaffold(
        topBar = {
            FlickTopBar(
                mapConfig = mapConfig,
                userProfile = uiState.userProfile,
                onTrafficToggle = { viewModel.toggleTraffic() },
                onMapLayerClick = {
                    val nextType = when (mapConfig.mapType) {
                        com.example.data.model.CustomMapType.NORMAL -> com.example.data.model.CustomMapType.SATELLITE
                        com.example.data.model.CustomMapType.SATELLITE -> com.example.data.model.CustomMapType.TERRAIN
                        com.example.data.model.CustomMapType.TERRAIN -> com.example.data.model.CustomMapType.HYBRID
                        com.example.data.model.CustomMapType.HYBRID -> com.example.data.model.CustomMapType.MAPBOX_SATELLITE_3D
                        com.example.data.model.CustomMapType.MAPBOX_SATELLITE_3D -> com.example.data.model.CustomMapType.NORMAL
                    }

                    viewModel.setMapType(nextType)
                },
                onAccountClick = { viewModel.toggleAccountDialog(true) }
            )
        },
        bottomBar = {
            if (!uiState.isNavigatingActive) {
                FlickBottomNav(
                    selectedIndex = selectedNavTab,
                    onDestinationSelected = { index ->
                        selectedNavTab = index
                        if (index == 0) {
                            // Returned to Explore screen
                        }
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = CharcoalBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isAboutScreenVisible) {
                AboutScreen(
                    onBackClick = { isAboutScreenVisible = false }
                )
            } else {
                Crossfade(targetState = selectedNavTab, label = "screen_transition") { tab ->
                    when (tab) {
                        0 -> ExploreMapScreen(
                            uiState = uiState,
                            mapConfig = mapConfig,
                            onSearchQueryChange = { viewModel.onSearchQueryChanged(it) },
                            onSearchPerform = { viewModel.performSearch(it) },
                            onCategorySelect = { viewModel.selectCategory(it) },
                            onPlaceSelect = { viewModel.selectPlace(it) },
                            onDismissSheet = { viewModel.dismissPlaceSheet() },
                            onFavoriteToggle = { viewModel.toggleFavorite(it) },
                            onDirectionsClick = {
                                viewModel.startDirectionsTo(it)
                                selectedNavTab = 0
                            },
                            onRecenterClick = { viewModel.recenterToUserLocation() },
                            onZoomInClick = { viewModel.zoomIn() },
                            onZoomOutClick = { viewModel.zoomOut() },
                            onModeChange = { viewModel.changeTravelMode(it) },
                            onSelectRoute = { viewModel.selectRouteIndex(it) },
                            onStartNavigation = { active -> viewModel.toggleNavigationMode(active) },
                            onNextStep = { viewModel.nextNavigationStep() },
                            onPrevStep = { viewModel.prevNavigationStep() },
                            onExitDirections = { viewModel.exitDirectionsMode() },
                            onMapClick = { viewModel.dismissPlaceSheet() }
                        )

                        1 -> SearchScreen(
                            query = uiState.searchQuery,
                            places = uiState.placesList,
                            recentSearches = recentSearches,
                            selectedCategory = uiState.selectedCategory,
                            onQueryChange = { viewModel.onSearchQueryChanged(it) },
                            onPerformSearch = {
                                viewModel.performSearch(it)
                                selectedNavTab = 0
                            },
                            onCategorySelect = { viewModel.selectCategory(it) },
                            onPlaceSelect = { place ->
                                viewModel.selectPlace(place)
                                selectedNavTab = 0
                            },
                            onFavoriteToggle = { viewModel.toggleFavorite(it) },
                            onDirectionsClick = { place ->
                                viewModel.startDirectionsTo(place)
                                selectedNavTab = 0
                            },
                            onDeleteSearch = { viewModel.deleteRecentSearch(it) },
                            onClearHistory = { viewModel.clearSearchHistory() }
                        )

                        2 -> DirectionsScreen(
                            uiState = uiState,
                            allPlaces = uiState.placesList,
                            onTravelModeSelect = { viewModel.changeTravelMode(it) },
                            onDestinationSelect = { place ->
                                viewModel.startDirectionsTo(place)
                            },
                            onStartNavigation = {
                                viewModel.toggleNavigationMode(true)
                                selectedNavTab = 0
                            }
                        )

                        3 -> SavedPlacesScreen(
                            favoritePlaces = favoritePlaces,
                            onPlaceSelect = { place ->
                                viewModel.selectPlace(place)
                                selectedNavTab = 0
                            },
                            onFavoriteToggle = { viewModel.toggleFavorite(it) },
                            onDirectionsClick = { place ->
                                viewModel.startDirectionsTo(place)
                                selectedNavTab = 0
                            }
                        )

                        4 -> SettingsScreen(
                            mapConfig = mapConfig,
                            onMapTypeChange = { viewModel.setMapType(it) },
                            onTrafficToggle = { viewModel.toggleTraffic() },
                            onDistanceUnitChange = { viewModel.setDistanceUnit(it) },
                            onTravelModeChange = { viewModel.setDefaultTravelMode(it) },
                            onLocationSharingToggle = { viewModel.toggleLocationSharing(it) },
                            onCloudSyncToggle = { viewModel.toggleCloudSync(it) },
                            onOpenAbout = { isAboutScreenVisible = true }
                        )
                    }
                }
            }

            // User Account Profile Dialog
            AccountDialog(
                userProfile = uiState.userProfile,
                isOpen = uiState.isAccountDialogOpen,
                onDismiss = { viewModel.toggleAccountDialog(false) },
                onSignIn = { viewModel.signInWithGoogle() },
                onSignOut = { viewModel.signOut() }
            )
        }
    }
}
