package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.data.model.CustomMapType
import com.example.data.model.DistanceUnit
import com.example.data.model.MapConfig
import com.example.data.model.TravelMode
import com.example.ui.theme.CharcoalBackground
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary

@Composable
fun SettingsScreen(
    mapConfig: MapConfig,
    onMapTypeChange: (CustomMapType) -> Unit,
    onTrafficToggle: () -> Unit,
    onDistanceUnitChange: (DistanceUnit) -> Unit,
    onTravelModeChange: (TravelMode) -> Unit,
    onLocationSharingToggle: (Boolean) -> Unit,
    onCloudSyncToggle: (Boolean) -> Unit,
    onOpenAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("settings_screen")
    ) {
        Text(
            text = "Settings & Configuration",
            style = MaterialTheme.typography.displayMedium.copy(
                color = SlateTextPrimary,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = "Customize map display, preferences, and Google API key options",
            style = MaterialTheme.typography.bodyMedium.copy(color = SlateTextSecondary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Map View Preferences Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Map Layer & Style",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = SlateTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                CustomMapType.values().forEach { type ->
                    val isSelected = mapConfig.mapType == type
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMapTypeChange(type) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = when (type) {
                                CustomMapType.NORMAL -> "Standard Vector Dark Map"
                                CustomMapType.SATELLITE -> "High-Res Satellite Imagery"
                                CustomMapType.TERRAIN -> "Topographic Contour Terrain"
                                CustomMapType.HYBRID -> "Hybrid Satellite with Roads"
                                CustomMapType.MAPBOX_SATELLITE_3D -> "Mapbox GL JS 3D Satellite & Terrain"

                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (isSelected) CyanAccent else SlateTextPrimary
                            )
                        )
                        RadioButton(
                            selected = isSelected,
                            onClick = { onMapTypeChange(type) },
                            colors = RadioButtonDefaults.colors(selectedColor = CyanAccent)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(CharcoalBorder)
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Traffic Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Traffic,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Live Traffic Layer",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = SlateTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Show real-time traffic congestion corridors",
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                            )
                        }
                    }

                    Switch(
                        checked = mapConfig.showTraffic,
                        onCheckedChange = { onTrafficToggle() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CharcoalBackground,
                            checkedTrackColor = CyanAccent
                        ),
                        modifier = Modifier.testTag("settings_traffic_switch")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Units & Preferences Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Straighten,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Distance Unit",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = SlateTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DistanceUnit.values().forEach { unit ->
                        val isSelected = mapConfig.distanceUnit == unit
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) CyanAccent else CharcoalSurfaceVariant)
                                .clickable { onDistanceUnitChange(unit) }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (unit == DistanceUnit.KILOMETERS) "Kilometers (km)" else "Miles (mi)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) CharcoalBackground else SlateTextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // API Key Configuration Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Google Maps API Status",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = SlateTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val apiKeyConfigured = try {
                    val key = BuildConfig.MAPS_API_KEY
                    key.isNotBlank() && key != "YOUR_GOOGLE_MAPS_API_KEY"
                } catch (_: Exception) {
                    false
                }

                Surface(
                    color = CharcoalSurfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (apiKeyConfigured) EmeraldGreen else CyanAccent)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (apiKeyConfigured) "Google Maps Key Active" else "Native Vector Engine Active",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = SlateTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Configured via AI Studio Secrets Panel (MAPS_API_KEY)",
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // About Flick Map Link Button
        Surface(
            color = CharcoalSurface,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenAbout() }
                .testTag("open_about_button")
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "About Flick Map",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = SlateTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Brand info by Flick Web Studio & API guide",
                            style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = SlateTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
