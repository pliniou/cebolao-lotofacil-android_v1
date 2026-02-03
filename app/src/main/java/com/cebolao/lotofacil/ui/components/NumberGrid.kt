package com.cebolao.lotofacil.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.ui.theme.AppSpacing

@Stable
data class NumberBallItem(
    val number: Int,
    val isSelected: Boolean,
    val isDisabled: Boolean,
    override val key: Any = number
) : StableKey

@Stable
interface StableKey {
    val key: Any
}

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NumberGrid(
    items: List<NumberBallItem>,
    modifier: Modifier = Modifier,
    columns: Int = 5
) {
    LocalDensity.current
    val configuration = LocalConfiguration.current
    
    // Optimize ball size calculation with proper density consideration
    val screenWidthDp = configuration.screenWidthDp.dp
    val spacing = AppSpacing.sm
    val horizontalPadding = AppSpacing.lg * 2
    
    val availableWidth = screenWidthDp - horizontalPadding
    val spacingTotal = spacing * (columns - 1)
    val ballSize = (availableWidth - spacingTotal) / columns
    
    // Clamp ball size to reasonable bounds
    val finalBallSize = ballSize.coerceIn(32.dp, 48.dp)

    LocalHapticFeedback.current

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(spacing),
        maxItemsInEachRow = columns
    ) {
        items.forEach { item ->
            NumberBall(
                number = item.number,
                size = finalBallSize,
                isSelected = item.isSelected,
                isHighlighted = item.isHighlighted,
                isDisabled = item.isDisabled,
                modifier = Modifier.semantics {
                    contentDescription = "Número ${item.number}"
                    role = Role.Button
                    state = when {
                        item.isSelected -> State.Selected
                        item.isDisabled -> State.Disabled
                        else -> State.Enabled
                                else -> "Not selected"
                            }
                        }
                    }
            ) {
                NumberBall(
                    number = item.number,
                    isSelected = item.isSelected,
                    isDisabled = item.isDisabled,
                    size = adaptiveBallSize
                )
            }
        }
    }
}
