package com.example.data.local

import com.example.data.model.LatLngPoint
import com.example.data.model.ManeuverType
import com.example.data.model.NavigationStep
import com.example.data.model.RouteResult
import com.example.data.model.TravelMode
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

class RouteRepository {

    fun calculateRoutes(
        originName: String = "Current Location",
        originLat: Double = 37.7749,
        originLng: Double = -122.4194,
        destinationName: String,
        destLat: Double,
        destLng: Double,
        mode: TravelMode = TravelMode.DRIVING
    ): List<RouteResult> {
        val directDistance = calculateHaversineDistance(originLat, originLng, destLat, destLng)

        // Generate primary route
        val primaryRoute = createRoute(
            id = "route_primary",
            originName = originName,
            originLat = originLat,
            originLng = originLng,
            destinationName = destinationName,
            destLat = destLat,
            destLng = destLng,
            mode = mode,
            distanceFactor = 1.25,
            summary = when (mode) {
                TravelMode.DRIVING -> "Fastest via Main Freeway & Cyan Blvd"
                TravelMode.WALKING -> "Direct pedestrian path via City Park"
                TravelMode.CYCLING -> "Protected bike lane route"
                TravelMode.TRANSIT -> "Line 14 Rapid + 5 min walk"
            },
            isRecommended = true,
            trafficDelayMins = if (mode == TravelMode.DRIVING) 3 else 0
        )

        // Generate alternative route 1
        val altRoute1 = createRoute(
            id = "route_alt_1",
            originName = originName,
            originLat = originLat,
            originLng = originLng,
            destinationName = destinationName,
            destLat = destLat,
            destLng = destLng,
            mode = mode,
            distanceFactor = 1.42,
            summary = when (mode) {
                TravelMode.DRIVING -> "Via Waterfront Expressway (Avoids Tolls)"
                TravelMode.WALKING -> "Scenic Embarcadero promenade"
                TravelMode.CYCLING -> "Quiet residential avenue path"
                TravelMode.TRANSIT -> "Metro Rail M-Line Express"
            },
            isRecommended = false,
            trafficDelayMins = if (mode == TravelMode.DRIVING) 1 else 0
        )

        return listOf(primaryRoute, altRoute1)
    }

    private fun createRoute(
        id: String,
        originName: String,
        originLat: Double,
        originLng: Double,
        destinationName: String,
        destLat: Double,
        destLng: Double,
        mode: TravelMode,
        distanceFactor: Double,
        summary: String,
        isRecommended: Boolean,
        trafficDelayMins: Int
    ): RouteResult {
        val straightDistKm = calculateHaversineDistance(originLat, originLng, destLat, destLng)
        val roadDistKm = (straightDistKm * distanceFactor).coerceAtLeast(0.4)

        val speedKmH = when (mode) {
            TravelMode.DRIVING -> 42.0
            TravelMode.WALKING -> 4.8
            TravelMode.CYCLING -> 18.0
            TravelMode.TRANSIT -> 28.0
        }

        val baseDurationMins = ((roadDistKm / speedKmH) * 60).roundToInt().coerceAtLeast(2)
        val totalDurationMins = baseDurationMins + trafficDelayMins

        // Generate detailed path polyline coordinates
        val waypoints = generatePolylinePoints(
            originLat, originLng,
            destLat, destLng,
            numPoints = 8
        )

        // Generate step by step turn-by-turn navigation instructions
        val steps = generateSteps(originName, destinationName, waypoints, mode, roadDistKm)

        return RouteResult(
            id = id,
            mode = mode,
            originName = originName,
            destinationName = destinationName,
            distanceKm = (roadDistKm * 10).roundToInt() / 10.0,
            durationMinutes = totalDurationMins,
            trafficDelayMinutes = trafficDelayMins,
            summary = summary,
            isRecommended = isRecommended,
            points = waypoints,
            steps = steps
        )
    }

    private fun generatePolylinePoints(
        startLat: Double, startLng: Double,
        endLat: Double, endLng: Double,
        numPoints: Int
    ): List<LatLngPoint> {
        val points = mutableListOf<LatLngPoint>()
        points.add(LatLngPoint(startLat, startLng))

        val midLat = (startLat + endLat) / 2
        val midLng = (startLng + endLng) / 2

        // Add realistic roadway bends
        for (i in 1 until numPoints - 1) {
            val fraction = i.toDouble() / (numPoints - 1)
            val jitterLat = (Math.random() - 0.5) * 0.003
            val jitterLng = (Math.random() - 0.5) * 0.003

            val lat = startLat + (endLat - startLat) * fraction + jitterLat
            val lng = startLng + (endLng - startLng) * fraction + jitterLng
            points.add(LatLngPoint(lat, lng))
        }

        points.add(LatLngPoint(endLat, endLng))
        return points
    }

    private fun generateSteps(
        originName: String,
        destinationName: String,
        points: List<LatLngPoint>,
        mode: TravelMode,
        totalKm: Double
    ): List<NavigationStep> {
        val steps = mutableListOf<NavigationStep>()
        val stepDistKm = totalKm / (points.size - 1)

        steps.add(
            NavigationStep(
                id = "step_start",
                instruction = "Head out from $originName",
                distanceText = "100 m",
                durationText = "1 min",
                maneuverType = ManeuverType.START,
                point = points.first()
            )
        )

        val maneuvers = listOf(
            ManeuverType.TURN_RIGHT to "Turn right onto Market Street",
            ManeuverType.KEEP_LEFT to "Keep left toward Cyan Expressway",
            ManeuverType.CONTINUE_STRAIGHT to "Continue straight for 800m",
            ManeuverType.ROUNDABOUT to "At the roundabout, take the 2nd exit",
            ManeuverType.TURN_LEFT to "Turn left onto Flick Plaza Avenue"
        )

        for (i in 1 until points.size - 1) {
            val (mType, mText) = maneuvers[(i - 1) % maneuvers.size]
            val stepDistMeters = (stepDistKm * 1000).roundToInt()
            val stepDistText = if (stepDistMeters >= 1000) "${(stepDistMeters / 100.0).roundToInt() / 10.0} km" else "$stepDistMeters m"

            steps.add(
                NavigationStep(
                    id = "step_$i",
                    instruction = mText,
                    distanceText = stepDistText,
                    durationText = "${(stepDistMeters / 500).coerceAtLeast(1)} min",
                    maneuverType = mType,
                    point = points[i]
                )
            )
        }

        steps.add(
            NavigationStep(
                id = "step_arrive",
                instruction = "Arrive at $destinationName",
                distanceText = "0 m",
                durationText = "0 min",
                maneuverType = ManeuverType.ARRIVE,
                point = points.last()
            )
        )

        return steps
    }

    fun calculateHaversineDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
