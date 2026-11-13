package com.example

import com.example.data.local.RouteRepository
import com.example.data.model.TravelMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

  @Test
  fun testAppNameConstant() {
    val appName = "Flick Map"
    assertEquals("Flick Map", appName)
  }

  @Test
  fun testRouteCalculation() {
    val routeRepository = RouteRepository()
    val routes = routeRepository.calculateRoutes(
      originName = "Current Location",
      originLat = 37.7749,
      originLng = -122.4194,
      destinationName = "San Francisco Ferry Building",
      destLat = 37.7955,
      destLng = -122.3937,
      mode = TravelMode.DRIVING
    )
    assertTrue(routes.isNotEmpty())
    assertEquals(TravelMode.DRIVING, routes.first().mode)
  }
}
