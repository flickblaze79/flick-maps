package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MapConfig
import com.example.data.model.PlaceCategory
import com.example.data.model.PlaceItem
import com.example.data.model.RouteResult
import com.example.ui.MapUiState
import com.example.ui.components.FlickMapView
import com.example.ui.components.PlaceDetailsSheet
import com.example.ui.components.RouteSummaryCard
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary

@Composable
fun ExploreMapScreen(
    uiState: MapUiState,
    mapConfig: MapConfig,
    onSearchQueryChange: (String) -> Unit,
    onSearchPerform: (String) -> Unit,
    onCategorySelect: (PlaceCategory?) -> Unit,
    onPlaceSelect: (PlaceItem) -> Unit,
    onDismissSheet: () -> Unit,
    onFavoriteToggle: (PlaceItem) -> Unit,
    onDirectionsClick: (PlaceItem) -> Unit,
    onRecenterClick: () -> Unit,
    onZoomInClick: () -> Unit,
    onZoomOutClick: () -> Unit,
    onModeChange: (com.example.data.model.TravelMode) -> Unit,
    onSelectRoute: (Int) -> Unit,
    onStartNavigation: (Boolean) -> Unit,
    onNextStep: () -> Unit,
    onPrevStep: () -> Unit,
    onExitDirections: () -> Unit,
    onMapClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("explore_map_screen")
    ) {
        // Fullscreen Interactive Map or Mapbox 3D Satellite WebView
        if (mapConfig.mapType == com.example.data.model.CustomMapType.MAPBOX_SATELLITE_3D) {
            com.example.ui.components.MapboxWebViewScreen(
                latitude = uiState.userLat,
                longitude = uiState.userLng,
                zoom = uiState.cameraZoom.toDouble(),
                modifier = Modifier.fillMaxSize()
            )
        } else {
            FlickMapView(
                userLat = uiState.userLat,
                userLng = uiState.userLng,
                cameraLat = uiState.cameraLat,
                cameraLng = uiState.cameraLng,
                cameraZoom = uiState.cameraZoom,
                places = uiState.placesList,
                selectedPlace = uiState.selectedPlace,
                routes = uiState.routes,
                selectedRouteIndex = uiState.selectedRouteIndex,
                isNavigatingActive = uiState.isNavigatingActive,
                activeStepIndex = uiState.activeStepIndex,
                mapConfig = mapConfig,
                onPlaceSelected = onPlaceSelect,
                onRecenterClick = onRecenterClick,
                onZoomInClick = onZoomInClick,
                onZoomOutClick = onZoomOutClick,
                onMapClick = onMapClick,
                modifier = Modifier.fillMaxSize()
            )
        }


        // Floating Top Search Bar & Category Chips Overlay (hidden during active turn-by-turn navigation)
        if (!uiState.isDirectionsMode) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Floating Search Input Bar
                Surface(
                    color = CharcoalSurface,
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = CyanAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = {
                                Text(
                                    text = "Search places, addresses, categories...",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = SlateTextSecondary)
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                                unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                                focusedTextColor = SlateTextPrimary,
                                unfocusedTextColor = SlateTextPrimary
                            ),
                            trailingIcon = {
                                if (uiState.searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { onSearchQueryChange("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear",
                                            tint = SlateTextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("map_search_input")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category Chips Carousel
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(PlaceCategory.values()) { category ->
                        val isSelected = uiState.selectedCategory == category
                        val catColor = category.getComposeColor()

                        Surface(
                            color = if (isSelected) catColor else CharcoalSurface,
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) catColor else CharcoalBorder
                            ),
                            modifier = Modifier
                                .clickable { onCategorySelect(category) }
                                .testTag("category_chip_${category.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) CharcoalSurface else catColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = category.displayName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) CharcoalSurface else SlateTextPrimary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Directions Mode Route Summary Overlay Panel at Bottom
        if (uiState.isDirectionsMode && uiState.routes.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                RouteSummaryCard(
                    routes = uiState.routes,
                    selectedRouteIndex = uiState.selectedRouteIndex,
                    selectedMode = uiState.selectedTravelMode,
                    isNavigatingActive = uiState.isNavigatingActive,
                    activeStepIndex = uiState.activeStepIndex,
                    onModeChange = onModeChange,
                    onSelectRoute = onSelectRoute,
                    onStartNavigation = onStartNavigation,
                    onNextStep = onNextStep,
                    onPrevStep = onPrevStep,
                    onExitDirections = onExitDirections
                )
            }
        }

        // Place Details Sheet Modal
        PlaceDetailsSheet(
            place = uiState.selectedPlace,
            isOpen = uiState.isPlaceSheetOpen,
            onDismiss = onDismissSheet,
            onFavoriteToggle = onFavoriteToggle,
            onDirectionsClick = onDirectionsClick
        )
    }
}
