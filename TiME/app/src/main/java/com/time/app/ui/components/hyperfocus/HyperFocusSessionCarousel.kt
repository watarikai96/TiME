package com.time.app.ui.components.hyperfocus

import com.time.app.viewmodel.HyperFocusViewModel
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.time.app.model.ExecutableSession
import com.time.app.ui.theme.QuietCraftTheme


//HELPERS

enum class CarouselType {
    ACTIVE
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HyperFocusSessionCarousel(
    listState: LazyListState,
    hyperFocusViewModel: HyperFocusViewModel,
    sessions: List<ExecutableSession>,
    currentIndex: Int,
    isPaused: Boolean,
    originalStart: Long = 0L, // <-- Add this
    type: CarouselType = CarouselType.ACTIVE,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onEnd: () -> Unit,
    onDelete: (Int) -> Unit,
    onDurationChange: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Already passed listState now
    val snapping = rememberSnapFlingBehavior(listState) //

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth - 64.dp
    val visibleIndex = listState.firstVisibleItemIndex

    val sessionNumberMap = sessions.mapIndexedNotNull { i, s ->
        if (!s.isBreak) i to s
        else null
    }.mapIndexed { realIndex, (originalIndex, _) ->
        originalIndex to realIndex + 1
    }.toMap()


    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        Column(
            verticalArrangement = Arrangement.spacedBy(QuietCraftTheme.Spacing.medium),
            modifier = modifier
        ) {
            // Dot Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                sessions.forEachIndexed { i, session ->
                    val isSelected = i == visibleIndex
                    val categoryColor = session.categoryColor?.toInt() ?: 0xFF888888.toInt()
                    val baseColor = if (session.isBreak)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    else
                        Color(categoryColor)

                    val animatedSize by animateDpAsState(
                        targetValue = if (isSelected) 18.dp else 12.dp,
                        animationSpec = tween(250, easing = FastOutSlowInEasing),
                        label = "DotSize"
                    )

                    val animatedColor by animateColorAsState(
                        targetValue = if (isSelected) baseColor else baseColor.copy(alpha = 0.4f),
                        animationSpec = tween(250, easing = FastOutSlowInEasing),
                        label = "DotColor"
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(animatedSize)
                            .clip(CircleShape)
                            .background(animatedColor)
                    ) {
                        if (isSelected && !session.isBreak) {
                            sessionNumberMap[i]?.let { number ->
                                Text(
                                    text = number.toString(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Carousel
            CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
            LazyRow(
                state = listState,
                flingBehavior = snapping,
                contentPadding = PaddingValues(horizontal = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(QuietCraftTheme.Spacing.large),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                itemsIndexed(sessions) { index, _ ->
                    val session = hyperFocusViewModel.sessionQueue.getOrNull(index)
                    if (session != null) {
                        HyperFocusSessionCard(
                            viewModel = hyperFocusViewModel,
                            session = session,
                            sessionNumber = sessionNumberMap[index],
                            sessionIndex = index,
                            currentSessionIndex = currentIndex,
                            isPaused = isPaused,
                            originalStart = originalStart,
                            type = type,
                            onPause = onPause,
                            onResume = onResume,
                            onEnd = onEnd,
                            onDelete = { onDelete(index) },
                            onDurationChange = { newDuration -> onDurationChange(index, newDuration) },
                            modifier = Modifier.width(cardWidth)
                        )
                    }
                }


            }
            }
        }
    }
}




