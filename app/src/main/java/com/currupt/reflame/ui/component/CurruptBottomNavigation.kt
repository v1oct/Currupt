package com.currupt.reflame.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.currupt.reflame.account.model.UserEntitlement
import com.currupt.reflame.ui.design.CurruptColors

enum class CurruptNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    HOME("currupt/home", "HOME", Icons.Rounded.Home),
    GAMES("currupt/games", "GAMES", Icons.Rounded.SportsEsports),
    SETTINGS("currupt/settings", "SETTINGS", Icons.Rounded.Settings)
}

@Composable
fun CurruptBottomNavigation(
    currentRoute: String?,
    entitlement: UserEntitlement = UserEntitlement.FREE,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeAccent = CurruptColors.getAccentForEntitlement(entitlement)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .border(
                    width = 1.dp,
                    color = CurruptColors.BorderBright.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(32.dp)
                ),
            color = CurruptColors.SurfaceDark.copy(alpha = 0.9f),
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CurruptNavItem.entries.forEach { item ->
                    val isSelected = currentRoute == item.route

                    Surface(
                        onClick = {
                            if (!isSelected) {
                                onNavigate(item.route)
                            }
                        },
                        color = Color.Transparent,
                        modifier = Modifier
                            .clip(CircleShape)
                            .padding(4.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) activeAccent else CurruptColors.TextMuted,
                                modifier = Modifier.size(22.dp)
                            )
                            
                            Text(
                                text = item.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) activeAccent else CurruptColors.TextMuted
                            )

                            AnimatedVisibility(visible = isSelected) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 2.dp)
                                        .size(4.dp)
                                        .background(activeAccent, CircleShape)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
