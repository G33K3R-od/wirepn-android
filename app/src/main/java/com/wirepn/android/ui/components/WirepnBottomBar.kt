package com.wirepn.android.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

data class WirepnNavTab(
    val route: String,
    val labelRes: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
)

@Composable
fun WirepnBottomBar(
    navController: NavHostController,
    tabs: List<WirepnNavTab>,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    val onVar = MaterialTheme.colorScheme.onSurfaceVariant
    val surface = MaterialTheme.colorScheme.surfaceContainerHighest
    val outline = MaterialTheme.colorScheme.outline

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            color = surface.copy(alpha = 0.96f),
            tonalElevation = 0.dp,
            shadowElevation = 10.dp,
            border = BorderStroke(1.dp, outline.copy(alpha = 0.14f)),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                tabs.forEach { tab ->
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == tab.route } == true
                    WirepnDockItem(
                        modifier = Modifier.weight(1f),
                        tab = tab,
                        selected = selected,
                        activeColor = primary,
                        inactiveColor = onVar,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.WirepnDockItem(
    modifier: Modifier = Modifier,
    tab: WirepnNavTab,
    selected: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.94f,
        animationSpec = tween(220),
        label = "dockScale",
    )
    val alpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0.75f,
        animationSpec = tween(220),
        label = "dockAlpha",
    )

    val interaction = remember { MutableInteractionSource() }
    val pillShape = RoundedCornerShape(18.dp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 2.dp)
            .clip(pillShape)
            .then(
                if (selected) {
                    Modifier.background(
                        Brush.verticalGradient(
                            0f to activeColor.copy(alpha = 0.2f),
                            1f to activeColor.copy(alpha = 0.06f),
                        ),
                        pillShape,
                    )
                } else {
                    Modifier
                },
            )
            .clickable(
                interactionSource = interaction,
                indication = ripple(bounded = true, color = activeColor.copy(alpha = 0.3f)),
                onClick = onClick,
            )
            .padding(vertical = 8.dp, horizontal = 2.dp),
    ) {
        Icon(
            imageVector = tab.icon,
            contentDescription = stringResource(tab.labelRes),
            modifier = Modifier
                .size(26.dp)
                .scale(scale)
                .alpha(alpha),
            tint = if (selected) activeColor else inactiveColor,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(tab.labelRes),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            softWrap = false,
            fontSize = 11.sp,
            lineHeight = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (selected) activeColor else inactiveColor.copy(alpha = 0.88f),
        )
    }
}
