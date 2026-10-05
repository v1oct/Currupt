package com.currupt.reflame.feature.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.currupt.reflame.core.model.Game
import com.currupt.reflame.core.model.GameProfile
import com.currupt.reflame.ui.design.CurruptColors
import com.currupt.reflame.ui.design.CurruptTypography

@Composable
fun CurruptGamesScreen(
    gameManager: GameManager,
    modifier: Modifier = Modifier
) {
    val gameState by gameManager.state.collectAsState()
    val profileRepository = remember { LocalGameProfileRepository() }

    LaunchedEffect(Unit) {
        gameManager.loadGames()
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "GAME CATALOG",
            style = CurruptTypography.BrandHeader
        )
        Text(
            text = "V1 Supported Games & Coming Soon Placeholders",
            style = CurruptTypography.Subtitle
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (gameState.availableGames.isEmpty()) {
            Text(
                text = "Loading registered games...",
                style = CurruptTypography.Body
            )
        } else {
            gameState.availableGames.forEach { game ->
                val gameProfile by produceState<GameProfile?>(initialValue = null, game.id) {
                    value = profileRepository.getGameProfile(game.id)
                }

                GameCatalogItemCard(
                    game = game,
                    profile = gameProfile
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun GameCatalogItemCard(
    game: Game,
    profile: GameProfile?
) {
    val isSupported = game.isV1Supported

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = if (isSupported) CurruptColors.BorderBright else CurruptColors.BorderSubtle,
                shape = RoundedCornerShape(16.dp)
            ),
        color = CurruptColors.CardSurface
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.05f)),
                        color = Color.Transparent
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = game.displayName.take(1),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = game.displayName,
                            style = CurruptTypography.CardTitle
                        )
                        Text(
                            text = game.packageNames.firstOrNull() ?: "Unknown package",
                            style = CurruptTypography.Caption
                        )
                    }
                }

                // Status Badge
                Surface(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                    color = if (isSupported) CurruptColors.StatusGreen.copy(alpha = 0.15f) else CurruptColors.TextMuted.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (isSupported) "SUPPORTED V1" else "COMING SOON",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSupported) CurruptColors.StatusGreen else CurruptColors.TextMuted,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (profile != null && profile.enabledTools.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Assigned Tools:",
                    style = CurruptTypography.Caption
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    profile.enabledTools.forEach { toolId ->
                        Surface(
                            modifier = Modifier.clip(RoundedCornerShape(6.dp)),
                            color = CurruptColors.SurfaceDark
                        ) {
                            Text(
                                text = toolId,
                                fontSize = 11.sp,
                                color = CurruptColors.TextSecondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
