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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.PlaceItem
import com.example.data.model.TravelMode
import com.example.ui.MapUiState
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.CharcoalBackground
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceElevated
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary

@Composable
fun DirectionsScreen(
    uiState: MapUiState,
    allPlaces: List<PlaceItem>,
    onTravelModeSelect: (TravelMode) -> Unit,
    onDestinationSelect: (PlaceItem) -> Unit,
    onStartNavigation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalBackground)
            .padding(16.dp)
            .testTag("directions_screen")
    ) {
        Text(
            text = "Route Planner & Navigation",
            style = MaterialTheme.typography.displayMedium.copy(
                color = SlateTextPrimary,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = "Calculate optimal routes with live traffic estimates",
            style = MaterialTheme.typography.bodyMedium.copy(color = SlateTextSecondary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Route Inputs Card (Origin & Destination)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Origin Input Box
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Starting Location",
                            style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                        )
                        Text(
                            text = uiState.originName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = SlateTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(CharcoalBorder)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Destination Input Box
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = AmberOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Destination",
                            style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                        )
                        Text(
                            text = uiState.destinationPlace?.name ?: "Select a destination below",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = if (uiState.destinationPlace != null) CyanAccent else SlateTextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Travel Mode Selector Bar
        Text(
            text = "Select Travel Mode",
            style = MaterialTheme.typography.titleMedium.copy(
                color = SlateTextPrimary,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TravelMode.values().forEach { mode ->
                val isSelected = uiState.selectedTravelMode == mode
                val icon = when (mode) {
                    TravelMode.DRIVING -> Icons.Default.DirectionsCar
                    TravelMode.WALKING -> Icons.Default.DirectionsWalk
                    TravelMode.CYCLING -> Icons.Default.DirectionsBike
                    TravelMode.TRANSIT -> Icons.Default.DirectionsBus
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) CyanAccent else CharcoalSurface)
                        .border(1.dp, if (isSelected) CyanAccent else CharcoalBorder, RoundedCornerShape(12.dp))
                        .clickable { onTravelModeSelect(mode) }
                        .testTag("directions_mode_${mode.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = mode.displayName,
                            tint = if (isSelected) CharcoalBackground else SlateTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = mode.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) CharcoalBackground else SlateTextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Destination Quick Selector List
        Text(
            text = "Choose Destination Place",
            style = MaterialTheme.typography.titleMedium.copy(
                color = SlateTextPrimary,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 20.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(allPlaces) { place ->
                val isSelected = uiState.destinationPlace?.id == place.id
                Surface(
                    color = if (isSelected) CharcoalSurfaceElevated else CharcoalSurface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) CyanAccent else CharcoalBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDestinationSelect(place) }
                        .testTag("select_dest_place_${place.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(place.category.getComposeColor().copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = place.category.getComposeColor(),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = place.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = SlateTextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "${place.address} • ${place.distanceKm} km",
                                    style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                                )
                            }
                        }

                        if (isSelected) {
                            Button(
                                onClick = onStartNavigation,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyanAccent,
                                    contentColor = CharcoalBackground
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("start_directions_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Navigation,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Navigate", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
