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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlaceCategory
import com.example.data.model.PlaceItem
import com.example.data.model.SearchHistoryItem
import com.example.ui.components.PlaceCard
import com.example.ui.theme.CharcoalBackground
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary

@Composable
fun SearchScreen(
    query: String,
    places: List<PlaceItem>,
    recentSearches: List<SearchHistoryItem>,
    selectedCategory: PlaceCategory?,
    onQueryChange: (String) -> Unit,
    onPerformSearch: (String) -> Unit,
    onCategorySelect: (PlaceCategory?) -> Unit,
    onPlaceSelect: (PlaceItem) -> Unit,
    onFavoriteToggle: (PlaceItem) -> Unit,
    onDirectionsClick: (PlaceItem) -> Unit,
    onDeleteSearch: (String) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalBackground)
            .padding(16.dp)
            .testTag("search_screen")
    ) {
        Text(
            text = "Search & Discovery",
            style = MaterialTheme.typography.displayMedium.copy(
                color = SlateTextPrimary,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = "Explore places, categories, and recent searches",
            style = MaterialTheme.typography.bodyMedium.copy(color = SlateTextSecondary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search Input Box
        Surface(
            color = CharcoalSurface,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = {
                        Text(
                            text = "Search any place, hospital, fuel station...",
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
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = SlateTextSecondary
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_screen_input")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category Cards Grid (When query is empty)
        if (query.isEmpty() && selectedCategory == null) {
            Text(
                text = "Browse Nearby Categories",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = SlateTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.height(200.dp)
            ) {
                items(PlaceCategory.values()) { cat ->
                    val color = cat.getComposeColor()
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCategorySelect(cat) }
                            .testTag("search_category_grid_${cat.name.lowercase()}"),
                        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(color.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = color,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = cat.displayName,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = SlateTextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recent Searches History Section
            if (recentSearches.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Recent Searches",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = SlateTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    TextButton(
                        onClick = onClearHistory,
                        modifier = Modifier.testTag("clear_recent_searches_button")
                    ) {
                        Text("Clear All", color = CyanAccent, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(recentSearches) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(CharcoalSurface)
                                .border(1.dp, CharcoalBorder, RoundedCornerShape(10.dp))
                                .clickable { onPerformSearch(item.query) }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = SlateTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = item.query,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = SlateTextPrimary)
                                )
                            }
                            IconButton(
                                onClick = { onDeleteSearch(item.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove Search",
                                    tint = SlateTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Search Results List
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedCategory != null) "Category: ${selectedCategory.displayName}" else "Search Results (${places.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = SlateTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
                if (selectedCategory != null) {
                    TextButton(onClick = { onCategorySelect(null) }) {
                        Text("Show All", color = CyanAccent)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(places) { place ->
                    PlaceCard(
                        place = place,
                        onClick = { onPlaceSelect(place) },
                        onFavoriteToggle = { onFavoriteToggle(place) },
                        onDirectionsClick = { onDirectionsClick(place) }
                    )
                }
            }
        }
    }
}
