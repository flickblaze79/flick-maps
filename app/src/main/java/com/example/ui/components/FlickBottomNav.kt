package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Directions
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceElevated
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary

data class NavDestination(
    val routeIndex: Int,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

val flickNavDestinations = listOf(
    NavDestination(0, "Explore", Icons.Filled.Explore, Icons.Outlined.Explore, "nav_explore_tab"),
    NavDestination(1, "Search", Icons.Filled.Search, Icons.Outlined.Search, "nav_search_tab"),
    NavDestination(2, "Directions", Icons.Filled.Directions, Icons.Outlined.Directions, "nav_directions_tab"),
    NavDestination(3, "Saved", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder, "nav_saved_tab"),
    NavDestination(4, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings, "nav_settings_tab")
)

@Composable
fun FlickBottomNav(
    selectedIndex: Int,
    onDestinationSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("flick_bottom_nav"),
        containerColor = CharcoalSurface,
        tonalElevation = 8.dp
    ) {
        flickNavDestinations.forEach { dest ->
            val isSelected = selectedIndex == dest.routeIndex
            NavigationBarItem(
                selected = isSelected,
                onClick = { onDestinationSelected(dest.routeIndex) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) dest.selectedIcon else dest.unselectedIcon,
                        contentDescription = dest.title,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = dest.title,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CyanAccent,
                    selectedTextColor = CyanAccent,
                    indicatorColor = CharcoalSurfaceElevated,
                    unselectedIconColor = SlateTextSecondary,
                    unselectedTextColor = SlateTextSecondary
                ),
                modifier = Modifier.testTag(dest.testTag)
            )
        }
    }
}
