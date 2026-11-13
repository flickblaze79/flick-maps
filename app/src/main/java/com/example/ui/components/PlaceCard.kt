package com.example.ui.components

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlaceItem
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceElevated
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanLight
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldenYellow
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary

@Composable
fun PlaceCard(
    place: PlaceItem,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onDirectionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("place_card_${place.id}"),
        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(place.category.getComposeColor().copy(alpha = 0.18f))
                            .border(1.dp, place.category.getComposeColor(), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = place.category.displayName,
                            tint = place.category.getComposeColor(),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = place.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = SlateTextPrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = place.category.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(color = place.category.getComposeColor())
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${place.distanceKm} km away",
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("place_card_fav_${place.id}")
                ) {
                    Icon(
                        imageVector = if (place.isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save Favorite",
                        tint = if (place.isFavorite) CyanAccent else SlateTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Rating & Address Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = GoldenYellow,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${place.rating}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = SlateTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${place.userRatingsTotal})",
                        style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "$".repeat(place.priceLevel),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EmeraldGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Text(
                    text = place.tag,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyanAccent,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier
                        .background(CyanAccent.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = place.address,
                style = MaterialTheme.typography.bodyMedium.copy(color = SlateTextSecondary),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Directions Button
            Button(
                onClick = onDirectionsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .testTag("place_card_directions_${place.id}"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanAccent,
                    contentColor = CharcoalSurface
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Directions,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Get Directions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailsSheet(
    place: PlaceItem?,
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onFavoriteToggle: (PlaceItem) -> Unit,
    onDirectionsClick: (PlaceItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isOpen || place == null) return

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CharcoalSurface,
        scrimColor = Color.Black.copy(alpha = 0.7f),
        modifier = modifier.testTag("place_details_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header with Category Icon & Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(place.category.getComposeColor().copy(alpha = 0.2f))
                            .border(1.dp, place.category.getComposeColor(), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = place.category.displayName,
                            tint = place.category.getComposeColor(),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = place.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = SlateTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = place.category.displayName,
                                style = MaterialTheme.typography.labelMedium.copy(color = place.category.getComposeColor())
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${place.distanceKm} km away",
                                style = MaterialTheme.typography.labelMedium.copy(color = SlateTextSecondary)
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { onFavoriteToggle(place) },
                    modifier = Modifier.testTag("sheet_fav_button")
                ) {
                    Icon(
                        imageVector = if (place.isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save Favorite",
                        tint = if (place.isFavorite) CyanAccent else SlateTextSecondary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rating Badges Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CharcoalSurfaceVariant)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GoldenYellow,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${place.rating}",
                            style = MaterialTheme.typography.titleMedium.copy(color = SlateTextPrimary)
                        )
                    }
                    Text(
                        text = "${place.userRatingsTotal} Reviews",
                        style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(30.dp)
                        .background(CharcoalBorder)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$".repeat(place.priceLevel),
                        style = MaterialTheme.typography.titleMedium.copy(color = EmeraldGreen)
                    )
                    Text(
                        text = "Price Tier",
                        style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(30.dp)
                        .background(CharcoalBorder)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = place.tag,
                        style = MaterialTheme.typography.titleMedium.copy(color = CyanAccent)
                    )
                    Text(
                        text = "Verified Spot",
                        style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Address & Info List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = place.address,
                        style = MaterialTheme.typography.bodyMedium.copy(color = SlateTextPrimary)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = EmeraldGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = place.openingHours,
                        style = MaterialTheme.typography.bodyMedium.copy(color = SlateTextPrimary)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        tint = AmberOrange,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = place.phone,
                        style = MaterialTheme.typography.bodyMedium.copy(color = SlateTextPrimary),
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${place.phone}"))
                            context.startActivity(intent)
                        }
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = place.website,
                        style = MaterialTheme.typography.bodyMedium.copy(color = CyanAccent),
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(place.website))
                            context.startActivity(intent)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = place.description,
                style = MaterialTheme.typography.bodyMedium.copy(color = SlateTextSecondary)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons (Directions & Share Location)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val shareText = "Check out ${place.name} on Flick Map!\nAddress: ${place.address}\nCoords: ${place.latitude}, ${place.longitude}"
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share Location via"))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("share_location_button"),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SlateTextPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { onDirectionsClick(place) },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(48.dp)
                        .testTag("sheet_directions_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanAccent,
                        contentColor = CharcoalSurface
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Directions", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
