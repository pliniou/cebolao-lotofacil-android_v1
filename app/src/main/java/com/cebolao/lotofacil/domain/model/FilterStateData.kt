package com.cebolao.lotofacil.domain.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class FilterStateData(
    val type: FilterType,
    val isEnabled: Boolean = false,
    val min: Float,
    val max: Float,
    @StringRes val descriptionRes: Int
) {
    val range: ClosedFloatingPointRange<Float>
        get() = min..max
        
    fun copyWithRange(newRange: ClosedFloatingPointRange<Float>): FilterStateData {
        return copy(min = newRange.start, max = newRange.endInclusive)
    }
}
