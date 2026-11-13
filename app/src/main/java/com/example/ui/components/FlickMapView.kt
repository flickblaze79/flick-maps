package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomMapType
import com.example.data.model.MapConfig
import com.example.data.model.PlaceCategory
import com.example.data.model.PlaceItem
import com.example.data.model.RouteResult
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.CharcoalBackground
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceElevated
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldenYellow
import com.example.ui.theme.PurpleIndigo
import com.example.ui.theme.RouteAlternative
import com.example.ui.theme.RouteCyan
import com.example.ui.theme.RouteTrafficHeavy
import com.example.ui.theme.RouteTrafficSlow
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

// Standard Dark Google Map styling JSON
private const val DARK_MAP_STYLE_JSON = """[
  {"elementType": "geometry", "stylers": [{"color": "#161c28"}]},
  {"elementType": "labels.text.fill", "stylers": [{"color": "#8e9bb0"}]},
  {"elementType": "labels.text.stroke", "stylers": [{"color": "#161c28"}]},
  {"featureType": "administrative", "elementType": "geometry.stroke", "stylers": [{"color": "#2d384c"}]},
  {"featureType": "landscape.man_made", "elementType": "geometry.stroke", "stylers": [{"color": "#263042"}]},
  {"featureType": "poi", "elementType": "geometry", "stylers": [{"color": "#1d2535"}]},
  {"featureType": "road", "elementType": "geometry", "stylers": [{"color": "#283347"}]},
  {"featureType": "road.highway", "elementType": "geometry", "stylers": [{"color": "#2c3b54"}]},
  {"featureType": "road.highway", "elementType": "geometry.stroke", "stylers": [{"color": "#00e5ff"}]},
  {"featureType": "transit", "elementType": "geometry", "stylers": [{"color": "#222c3d"}]},
  {"featureType": "water", "elementType": "geometry", "stylers": [{"color": "#0b1019"}]}
]"""

@Composable
fun FlickMapView(
    userLat: Double,
    userLng: Double,
    cameraLat: Double,
    cameraLng: Double,
    cameraZoom: Float,
    places: List<PlaceItem>,
    selectedPlace: PlaceItem?,
    routes: List<RouteResult>,
    selectedRouteIndex: Int,
    isNavigatingActive: Boolean,
    activeStepIndex: Int,
    mapConfig: MapConfig,
    onPlaceSelected: (PlaceItem) -> Unit,
    onRecenterClick: () -> Unit,
    onZoomInClick: () -> Unit,
    onZoomOutClick: () -> Unit,
    onMapClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMapsSdkLoaded by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(cameraLat, cameraLng), cameraZoom)
    }

    LaunchedEffect(cameraLat, cameraLng, cameraZoom) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(cameraLat, cameraLng), cameraZoom)
    }

    val context = LocalContext.current
    val mapProperties = remember(mapConfig) {
        MapProperties(
            mapType = when (mapConfig.mapType) {
                CustomMapType.SATELLITE -> MapType.SATELLITE
                CustomMapType.TERRAIN -> MapType.TERRAIN
                CustomMapType.HYBRID -> MapType.HYBRID
                else -> MapType.NORMAL
            },
            isTrafficEnabled = mapConfig.showTraffic,
            mapStyleOptions = try {
                MapStyleOptions(DARK_MAP_STYLE_JSON)
            } catch (_: Exception) {
                null
            }
        )
    }

    val mapUiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
            compassEnabled = true,
            myLocationButtonEnabled = false
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("flick_map_view_container")
    ) {
        // Attempt Native Google Maps SDK rendering, fall back smoothly to high-performance Canvas vector map
        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = mapUiSettings,
                onMapLoaded = { isMapsSdkLoaded = true },
                onMapClick = { onMapClick() }
            ) {
                // User location marker
                Marker(
                    state = MarkerState(position = LatLng(userLat, userLng)),
                    title = "Your Location",
                    snippet = "Current GPS Position"
                )

                // Place pins
                places.forEach { place ->
                    Marker(
                        state = MarkerState(position = LatLng(place.latitude, place.longitude)),
                        title = place.name,
                        snippet = "${place.category.displayName} • ${place.rating}★",
                        onClick = {
                            onPlaceSelected(place)
                            true
                        }
                    )
                }

                // Render Route Polylines
                routes.forEachIndexed { index, route ->
                    val isSelected = index == selectedRouteIndex
                    Polyline(
                        points = route.points.map { LatLng(it.latitude, it.longitude) },
                        color = if (isSelected) RouteCyan else RouteAlternative,
                        width = if (isSelected) 14f else 8f
                    )
                }
            }

            // High-Performance Interactive Canvas Overlay & Fallback Renderer
            if (!isMapsSdkLoaded) {
                FlickCanvasMap(
                    userLat = userLat,
                    userLng = userLng,
                    cameraLat = cameraLat,
                    cameraLng = cameraLng,
                    zoom = cameraZoom,
                    places = places,
                    selectedPlace = selectedPlace,
                    routes = routes,
                    selectedRouteIndex = selectedRouteIndex,
                    isNavigatingActive = isNavigatingActive,
                    mapConfig = mapConfig,
                    onPlaceClick = onPlaceSelected,
                    onMapClick = onMapClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Floating Map Controls Panel (Recenter, Zoom, Traffic status)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.End
        ) {
            // Live Traffic Indicator Badge
            if (mapConfig.showTraffic) {
                Surface(
                    color = CharcoalSurfaceElevated,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f)),
                    modifier = Modifier.shadow(4.dp, RoundedCornerShape(20.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
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
                            text = "Live Traffic Active",
                            style = MaterialTheme.typography.labelSmall.copy(color = SlateTextPrimary)
                        )
                    }
                }
            }

            // Recenter Location FAB
            FloatingActionButton(
                onClick = onRecenterClick,
                containerColor = CharcoalSurface,
                contentColor = CyanAccent,
                shape = CircleShape,
                modifier = Modifier
                    .size(48.dp)
                    .border(1.dp, CharcoalBorder, CircleShape)
                    .testTag("recenter_location_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "My Location",
                    modifier = Modifier.size(22.dp)
                )
            }

            // Zoom Controls Vertical Bar
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(CharcoalSurface)
                    .border(1.dp, CharcoalBorder, RoundedCornerShape(16.dp))
            ) {
                IconButton(
                    onClick = onZoomInClick,
                    modifier = Modifier.size(44.dp).testTag("zoom_in_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Zoom In",
                        tint = SlateTextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .width(44.dp)
                        .height(1.dp)
                        .background(CharcoalBorder)
                )

                IconButton(
                    onClick = onZoomOutClick,
                    modifier = Modifier.size(44.dp).testTag("zoom_out_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Zoom Out",
                        tint = SlateTextPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun FlickCanvasMap(
    userLat: Double,
    userLng: Double,
    cameraLat: Double,
    cameraLng: Double,
    zoom: Float,
    places: List<PlaceItem>,
    selectedPlace: PlaceItem?,
    routes: List<RouteResult>,
    selectedRouteIndex: Int,
    isNavigatingActive: Boolean,
    mapConfig: MapConfig,
    onPlaceClick: (PlaceItem) -> Unit,
    onMapClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 45f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar"
    )

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, _, _ ->
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
            .clickable { onMapClick() }
            .testTag("flick_canvas_map")
    ) {
        val width = size.width
        val height = size.height

        val isSatellite = mapConfig.mapType == CustomMapType.SATELLITE || mapConfig.mapType == CustomMapType.HYBRID

        // Base Map Background Color
        drawRect(color = if (isSatellite) Color(0xFF0F1A12) else CharcoalBackground)

        // Center projection helper
        val center = Offset(width / 2f + offsetX, height / 2f + offsetY)
        val scale = zoom * 80f

        fun latLngToScreen(lat: Double, lng: Double): Offset {
            val dx = (lng - cameraLng) * scale
            val dy = (cameraLat - lat) * scale
            return Offset(center.x + dx.toFloat(), center.y + dy.toFloat())
        }

        // 1. Draw Water / Coastal Body Shape
        val waterPath = Path().apply {
            moveTo(center.x - width * 0.8f, center.y - height * 0.9f)
            cubicTo(
                center.x - width * 0.3f, center.y - height * 0.6f,
                center.x - width * 0.5f, center.y + height * 0.4f,
                center.x - width * 0.9f, center.y + height * 0.9f
            )
            lineTo(center.x - width, center.y + height)
            lineTo(center.x - width, center.y - height)
            close()
        }
        drawPath(
            path = waterPath,
            color = if (isSatellite) Color(0xFF0B212D) else Color(0xFF0F172A)
        )

        // 2. Draw Urban Block Grids
        val gridStep = 80f * (zoom / 14f)
        var x = (center.x % gridStep)
        while (x < width) {
            drawLine(
                color = Color(0xFF1E293B).copy(alpha = 0.4f),
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 1f
            )
            x += gridStep
        }

        var y = (center.y % gridStep)
        while (y < height) {
            drawLine(
                color = Color(0xFF1E293B).copy(alpha = 0.4f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
            y += gridStep
        }

        // 3. Draw Major Road Network lines
        val mainRoads = listOf(
            listOf(Offset(center.x - 600f, center.y - 120f), Offset(center.x + 600f, center.y - 120f)),
            listOf(Offset(center.x - 600f, center.y + 180f), Offset(center.x + 600f, center.y + 180f)),
            listOf(Offset(center.x - 140f, center.y - 600f), Offset(center.x - 140f, center.y + 600f)),
            listOf(Offset(center.x + 220f, center.y - 600f), Offset(center.x + 220f, center.y + 600f))
        )

        mainRoads.forEach { road ->
            drawLine(
                color = Color(0xFF26334D),
                start = road[0],
                end = road[1],
                strokeWidth = 12f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = CyanAccent.copy(alpha = 0.15f),
                start = road[0],
                end = road[1],
                strokeWidth = 2f
            )
        }

        // Live Traffic Overlay (Amber / Red lines along major corridors)
        if (mapConfig.showTraffic) {
            drawLine(
                color = RouteTrafficSlow,
                start = Offset(center.x - 200f, center.y - 120f),
                end = Offset(center.x + 50f, center.y - 120f),
                strokeWidth = 6f
            )
            drawLine(
                color = RouteTrafficHeavy,
                start = Offset(center.x + 50f, center.y - 120f),
                end = Offset(center.x + 250f, center.y - 120f),
                strokeWidth = 6f
            )
        }

        // 4. Draw Active Routes (Polyline Path)
        routes.forEachIndexed { index, route ->
            val isSelected = index == selectedRouteIndex
            if (route.points.size >= 2) {
                val routePath = Path()
                val firstPt = latLngToScreen(route.points.first().latitude, route.points.first().longitude)
                routePath.moveTo(firstPt.x, firstPt.y)

                for (i in 1 until route.points.size) {
                    val pt = latLngToScreen(route.points[i].latitude, route.points[i].longitude)
                    routePath.lineTo(pt.x, pt.y)
                }

                // Outer glow shadow for route
                if (isSelected) {
                    drawPath(
                        path = routePath,
                        color = CyanGlow,
                        style = Stroke(width = 24f, cap = StrokeCap.Round)
                    )
                }

                drawPath(
                    path = routePath,
                    color = if (isSelected) RouteCyan else RouteAlternative,
                    style = Stroke(
                        width = if (isSelected) 10f else 6f,
                        cap = StrokeCap.Round,
                        pathEffect = if (!isSelected) PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f) else null
                    )
                )
            }
        }

        // 5. Draw Place Pins
        places.forEach { place ->
            val pos = latLngToScreen(place.latitude, place.longitude)
            val isSelected = selectedPlace?.id == place.id
            val pinColor = place.category.getComposeColor()

            // Pin Outer Glow
            if (isSelected) {
                drawCircle(
                    color = pinColor.copy(alpha = 0.3f),
                    radius = 28f,
                    center = pos
                )
            }

            // Pin Base Circle
            drawCircle(
                color = pinColor,
                radius = if (isSelected) 16f else 12f,
                center = pos
            )
            drawCircle(
                color = CharcoalBackground,
                radius = if (isSelected) 8f else 5f,
                center = pos
            )

            // Pin Stem Line
            drawLine(
                color = pinColor,
                start = pos,
                end = Offset(pos.x, pos.y + 12f),
                strokeWidth = 4f
            )
        }

        // 6. Draw User GPS Location Beacon
        val userPos = latLngToScreen(userLat, userLng)

        // Pulsing radar ring
        drawCircle(
            color = CyanAccent.copy(alpha = (1f - pulseRadius / 45f).coerceIn(0f, 1f)),
            radius = pulseRadius,
            center = userPos
        )

        // GPS Core Dot
        drawCircle(color = Color.White, radius = 10f, center = userPos)
        drawCircle(color = CyanAccent, radius = 7f, center = userPos)
    }
}
