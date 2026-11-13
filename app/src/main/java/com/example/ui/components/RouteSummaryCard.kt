package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ManeuverType
import com.example.data.model.NavigationStep
import com.example.data.model.RouteResult
import com.example.data.model.TravelMode
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceElevated
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldenYellow
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary

@Composable
fun RouteSummaryCard(
    routes: List<RouteResult>,
    selectedRouteIndex: Int,
    selectedMode: TravelMode,
    isNavigatingActive: Boolean,
    activeStepIndex: Int,
    onModeChange: (TravelMode) -> Unit,
    onSelectRoute: (Int) -> Unit,
    onStartNavigation: (Boolean) -> Unit,
    onNextStep: () -> Unit,
    onPrevStep: () -> Unit,
    onExitDirections: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isStepsExpanded by remember { mutableStateOf(false) }
    val currentRoute = routes.getOrNull(selectedRouteIndex)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("route_summary_card"),
        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Destination & Exit Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onExitDirections,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("exit_directions_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Exit Directions",
                            tint = SlateTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = currentRoute?.destinationName ?: "Destination",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = SlateTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "From ${currentRoute?.originName ?: "Current Location"}",
                            style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                        )
                    }
                }

                if (isNavigatingActive) {
                    Surface(
                        color = EmeraldGreen.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "NAVIGATING",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = EmeraldGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Travel Mode Chips (Driving, Walking, Cycling, Transit)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TravelMode.values().forEach { mode ->
                    val isSelected = selectedMode == mode
                    val icon = when (mode) {
                        TravelMode.DRIVING -> Icons.Default.DirectionsCar
                        TravelMode.WALKING -> Icons.Default.DirectionsWalk
                        TravelMode.CYCLING -> Icons.Default.DirectionsBike
                        TravelMode.TRANSIT -> Icons.Default.DirectionsBus
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) CyanAccent else CharcoalSurfaceVariant)
                            .clickable { onModeChange(mode) }
                            .testTag("mode_chip_${mode.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = icon,
                                contentDescription = mode.displayName,
                                tint = if (isSelected) CharcoalSurface else SlateTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = mode.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) CharcoalSurface else SlateTextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Active Route Time / Distance Summary Banner
            if (currentRoute != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CharcoalSurfaceElevated)
                        .border(1.dp, CyanAccent.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${currentRoute.durationMinutes}",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    color = CyanAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 32.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "min",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = CyanAccent,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${currentRoute.distanceKm} km",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = SlateTextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = currentRoute.summary,
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                            )
                        }
                    }

                    if (currentRoute.trafficDelayMinutes > 0) {
                        Surface(
                            color = AmberOrange.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Traffic,
                                    contentDescription = null,
                                    tint = AmberOrange,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "+${currentRoute.trafficDelayMinutes}m traffic",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = AmberOrange,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Alternative Route Choices Selector
            if (routes.size > 1 && !isNavigatingActive) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    routes.forEachIndexed { idx, route ->
                        val isSelected = idx == selectedRouteIndex
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CyanAccent.copy(alpha = 0.2f) else CharcoalSurfaceVariant)
                                .border(1.dp, if (isSelected) CyanAccent else CharcoalBorder, RoundedCornerShape(8.dp))
                                .clickable { onSelectRoute(idx) }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Route ${idx + 1} (${route.durationMinutes}m • ${route.distanceKm}km)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) CyanAccent else SlateTextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Turn-by-Turn Steps Toggle & Navigation Action Controls
            if (!isNavigatingActive) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { isStepsExpanded = !isStepsExpanded },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("toggle_steps_button"),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SlateTextPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (isStepsExpanded) "Hide Steps" else "Turn Steps (${currentRoute?.steps?.size ?: 0})")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (isStepsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Button(
                        onClick = { onStartNavigation(true) },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .testTag("start_navigation_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanAccent,
                            contentColor = CharcoalSurface
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Start Navigation", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Live Navigation Step Controls (Prev, Step Text, Next, Stop)
                val activeStep = currentRoute?.steps?.getOrNull(activeStepIndex)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CharcoalSurfaceVariant)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CyanAccent.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Navigation,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = "Step ${activeStepIndex + 1} of ${currentRoute?.steps?.size ?: 0}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = CyanAccent)
                                )
                                Text(
                                    text = activeStep?.instruction ?: "Proceed on route",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = SlateTextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onPrevStep,
                            enabled = activeStepIndex > 0,
                            modifier = Modifier.testTag("nav_prev_step_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.NavigateBefore,
                                contentDescription = "Previous Step",
                                tint = if (activeStepIndex > 0) CyanAccent else SlateTextSecondary
                            )
                        }

                        Button(
                            onClick = { onStartNavigation(false) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("stop_navigation_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Stop Nav", fontSize = 12.sp)
                        }

                        IconButton(
                            onClick = onNextStep,
                            modifier = Modifier.testTag("nav_next_step_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.NavigateNext,
                                contentDescription = "Next Step",
                                tint = CyanAccent
                            )
                        }
                    }
                }
            }

            // Expandable Turn-by-Turn Steps List
            AnimatedVisibility(
                visible = isStepsExpanded && !isNavigatingActive,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "Turn-by-Turn Directions",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = SlateTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        itemsIndexed(currentRoute?.steps ?: emptyList()) { idx, step ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(CharcoalSurfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${idx + 1}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CyanAccent,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = step.instruction,
                                        style = MaterialTheme.typography.bodyMedium.copy(color = SlateTextPrimary)
                                    )
                                    Text(
                                        text = "${step.distanceText} • ${step.durationText}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
