package com.cebolao.lotofacil.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.ui.theme.AppSpacing

@Stable
data class NumberBallItem(
    val number: Int,
    val isSelected: Boolean,
    val isDisabled: Boolean,
    val isHighlighted: Boolean = false,
    override val key: Any = number
) : StableKey

@Stable
interface StableKey {
    val key: Any
}

@Stable
data class GridLayoutConfig(
    val spacing: dp,
    val ballSize: dp
)

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun NumberGrid(
    items: List<NumberBallItem>,
    modifier: Modifier = Modifier,
    columns: Int = 5,
    ballSize: androidx.compose.ui.unit.Dp? = null,
    onNumberClicked: ((Int) -> Unit)? = null
) {
    val configuration = LocalConfiguration.current
    
    val gridConfig = remember(configuration.screenWidthDp, columns) {
        val screenWidthDp = configuration.screenWidthDp.dp
        val spacing = AppSpacing.sm
        val horizontalPadding = AppSpacing.lg * 2
        
        val availableWidth = screenWidthDp - horizontalPadding
        val spacingTotal = spacing * (columns - 1)
        val calculatedBallSize = (availableWidth - spacingTotal) / columns
        
        val finalBallSize = ballSize ?: calculatedBallSize.coerceIn(32.dp, 48.dp)
        GridLayoutConfig(spacing, finalBallSize)
    }

    LazyVerticalGrid(
        modifier = modifier.fillMaxWidth(),
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(gridConfig.spacing),
        horizontalArrangement = Arrangement.spacedBy(gridConfig.spacing),
        verticalArrangement = Arrangement.spacedBy(gridConfig.spacing)
    ) {
        items(
            items = items,
            key = { it.key }
        ) { item ->
            NumberBall(
                number = item.number,
                size = gridConfig.ballSize,
                isSelected = item.isSelected,
                isHighlighted = item.isHighlighted,
                isDisabled = item.isDisabled,
                onClick = { onNumberClicked?.invoke(item.number) }
            )
        }
    }
}
