package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.PlaceItem
import com.example.ui.components.PlaceCard
import com.example.ui.theme.CharcoalBackground
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary

@Composable
fun SavedPlacesScreen(
    favoritePlaces: List<PlaceItem>,
    onPlaceSelect: (PlaceItem) -> Unit,
    onFavoriteToggle: (PlaceItem) -> Unit,
    onDirectionsClick: (PlaceItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalBackground)
            .padding(16.dp)
            .testTag("saved_places_screen")
    ) {
        Text(
            text = "Saved Favorites",
            style = MaterialTheme.typography.displayMedium.copy(
                color = SlateTextPrimary,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = "${favoritePlaces.size} saved places in your offline database",
            style = MaterialTheme.typography.bodyMedium.copy(color = SlateTextSecondary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (favoritePlaces.isEmpty()) {
            // Friendly Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(CyanAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "No Saved Places Yet",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = SlateTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Tap the bookmark icon on any place card or detail sheet to save your favorite locations here.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = SlateTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(favoritePlaces) { place ->
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
