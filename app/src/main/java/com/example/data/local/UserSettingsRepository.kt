package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.CustomMapType
import com.example.data.model.DistanceUnit
import com.example.data.model.MapConfig
import com.example.data.model.MapThemeStyle
import com.example.data.model.TravelMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserSettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("flick_map_prefs", Context.MODE_PRIVATE)

    private val _configFlow = MutableStateFlow(loadConfig())
    val configFlow: StateFlow<MapConfig> = _configFlow.asStateFlow()

    private fun loadConfig(): MapConfig {
        return MapConfig(
            mapType = try { CustomMapType.valueOf(prefs.getString("map_type", CustomMapType.NORMAL.name)!!) } catch (_: Exception) { CustomMapType.NORMAL },
            showTraffic = prefs.getBoolean("show_traffic", true),
            showBuilding3D = prefs.getBoolean("show_building_3d", true),
            distanceUnit = try { DistanceUnit.valueOf(prefs.getString("distance_unit", DistanceUnit.KILOMETERS.name)!!) } catch (_: Exception) { DistanceUnit.KILOMETERS },
            themeStyle = try { MapThemeStyle.valueOf(prefs.getString("theme_style", MapThemeStyle.DARK_CYAN.name)!!) } catch (_: Exception) { MapThemeStyle.DARK_CYAN },
            defaultTravelMode = try { TravelMode.valueOf(prefs.getString("default_travel_mode", TravelMode.DRIVING.name)!!) } catch (_: Exception) { TravelMode.DRIVING },
            isLocationSharingEnabled = prefs.getBoolean("location_sharing", true),
            isCloudSyncEnabled = prefs.getBoolean("cloud_sync", false)
        )
    }

    fun updateMapType(type: CustomMapType) {
        prefs.edit().putString("map_type", type.name).apply()
        _configFlow.value = _configFlow.value.copy(mapType = type)
    }

    fun toggleTraffic(enabled: Boolean) {
        prefs.edit().putBoolean("show_traffic", enabled).apply()
        _configFlow.value = _configFlow.value.copy(showTraffic = enabled)
    }

    fun updateDistanceUnit(unit: DistanceUnit) {
        prefs.edit().putString("distance_unit", unit.name).apply()
        _configFlow.value = _configFlow.value.copy(distanceUnit = unit)
    }

    fun updateThemeStyle(style: MapThemeStyle) {
        prefs.edit().putString("theme_style", style.name).apply()
        _configFlow.value = _configFlow.value.copy(themeStyle = style)
    }

    fun updateDefaultTravelMode(mode: TravelMode) {
        prefs.edit().putString("default_travel_mode", mode.name).apply()
        _configFlow.value = _configFlow.value.copy(defaultTravelMode = mode)
    }

    fun toggleLocationSharing(enabled: Boolean) {
        prefs.edit().putBoolean("location_sharing", enabled).apply()
        _configFlow.value = _configFlow.value.copy(isLocationSharingEnabled = enabled)
    }

    fun toggleCloudSync(enabled: Boolean) {
        prefs.edit().putBoolean("cloud_sync", enabled).apply()
        _configFlow.value = _configFlow.value.copy(isCloudSyncEnabled = enabled)
    }
}
