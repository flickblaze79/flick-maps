package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.MapConfig
import com.example.data.model.UserProfile
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanAccentVariant
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary

@Composable
fun FlickTopBar(
    mapConfig: MapConfig,
    userProfile: UserProfile,
    onTrafficToggle: () -> Unit,
    onMapLayerClick: () -> Unit,
    onAccountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("flick_top_bar"),
        color = CharcoalSurface,
        tonalElevation = 6.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Studio Tag
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onAccountClick() }
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyanAccent.copy(alpha = 0.15f))
                        .border(1.dp, CyanAccent, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_flick_map_logo_1794036071068),
                        contentDescription = "Flick Map Logo",
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Flick Maps",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = SlateTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "3D",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier
                                .background(CyanAccent.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "by Flick Web",

                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SlateTextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Quick Actions (Traffic, Layers, Account profile)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Live Traffic Toggle
                IconButton(
                    onClick = onTrafficToggle,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (mapConfig.showTraffic) CyanAccent.copy(alpha = 0.2f) else CharcoalSurfaceVariant)
                        .testTag("traffic_toggle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Traffic,
                        contentDescription = "Toggle Traffic",
                        tint = if (mapConfig.showTraffic) CyanAccent else SlateTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Map Layer Picker Button
                IconButton(
                    onClick = onMapLayerClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CharcoalSurfaceVariant)
                        .testTag("map_layer_picker_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = "Map Layers",
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // User Account / Cloud Sync Avatar Button
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(CharcoalSurfaceVariant)
                        .clickable { onAccountClick() }
                        .padding(4.dp)
                        .testTag("user_account_button"),
                    contentAlignment = Alignment.Center
                ) {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = if (userProfile.isSignedIn) EmeraldGreen else SlateTextSecondary,
                                modifier = Modifier.size(8.dp)
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account Profile",
                            tint = if (userProfile.isSignedIn) CyanAccent else SlateTextSecondary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}
