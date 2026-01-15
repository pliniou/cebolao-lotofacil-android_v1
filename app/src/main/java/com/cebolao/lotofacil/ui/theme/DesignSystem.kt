package com.cebolao.lotofacil.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp

data class Spacing(
    val xs: androidx.compose.ui.unit.Dp = 4.dp,
    val sm: androidx.compose.ui.unit.Dp = 8.dp,
    val md: androidx.compose.ui.unit.Dp = 12.dp,
    val lg: androidx.compose.ui.unit.Dp = 16.dp,
    val xl: androidx.compose.ui.unit.Dp = 20.dp,
    val xxl: androidx.compose.ui.unit.Dp = 24.dp,
    val xxxl: androidx.compose.ui.unit.Dp = 32.dp
)

val AppSpacing = Spacing()

data class Elevation(
    val none: androidx.compose.ui.unit.Dp = 0.dp,
    val xs: androidx.compose.ui.unit.Dp = 1.dp,
    val sm: androidx.compose.ui.unit.Dp = 2.dp,
    val md: androidx.compose.ui.unit.Dp = 4.dp,
    val lg: androidx.compose.ui.unit.Dp = 8.dp,
    val xl: androidx.compose.ui.unit.Dp = 12.dp
)

val AppElevation = Elevation()

data class CardDefaults(
    val defaultPadding: androidx.compose.ui.unit.Dp = AppSpacing.lg,
    val contentSpacing: androidx.compose.ui.unit.Dp = AppSpacing.md,
    val buttonSpacing: androidx.compose.ui.unit.Dp = AppSpacing.sm,
    val elevation: androidx.compose.ui.unit.Dp = AppElevation.none,
    val pinnedElevation: androidx.compose.ui.unit.Dp = AppElevation.xs
)

val AppCardDefaults = CardDefaults()

// Composable helpers for dimension resources
@Composable
fun spacingXs() = dimensionResource(id = com.cebolao.lotofacil.R.dimen.spacing_xs)

@Composable
fun spacingSm() = dimensionResource(id = com.cebolao.lotofacil.R.dimen.spacing_sm)

@Composable
fun spacingMd() = dimensionResource(id = com.cebolao.lotofacil.R.dimen.spacing_md)

@Composable
fun spacingLg() = dimensionResource(id = com.cebolao.lotofacil.R.dimen.spacing_lg)

@Composable
fun spacingXl() = dimensionResource(id = com.cebolao.lotofacil.R.dimen.spacing_xl)

@Composable
fun spacingXxl() = dimensionResource(id = com.cebolao.lotofacil.R.dimen.spacing_xxl)

@Composable
fun spacingXxxl() = dimensionResource(id = com.cebolao.lotofacil.R.dimen.spacing_xxxl)

@Composable
fun elevationNone() = dimensionResource(id = com.cebolao.lotofacil.R.dimen.elevation_none)

@Composable
fun elevationXs() = dimensionResource(id = com.cebolao.lotofacil.R.dimen.elevation_xs)

@Composable
fun elevationSm() = dimensionResource(id = com.cebolao.lotofacil.R.dimen.elevation_sm)

@Composable
fun elevationMd() = dimensionResource(id = com.cebolao.lotofacil.R.dimen.elevation_md)

@Composable
fun elevationLg() = dimensionResource(id = com.cebolao.lotofacil.R.dimen.elevation_lg)

@Composable
fun elevationXl() = dimensionResource(id = com.cebolao.lotofacil.R.dimen.elevation_xl)

@Composable
fun iconSmall() = dimensionResource(id = com.cebolao.lotofacil.R.dimen.icon_small)

@Composable
fun iconMedium() = dimensionResource(id = com.cebolao.lotofacil.R.dimen.icon_medium)

@Composable
fun iconLarge() = dimensionResource(id = com.cebolao.lotofacil.R.dimen.icon_large)

@Composable
fun iconExtraLarge() = dimensionResource(id = com.cebolao.lotofacil.R.dimen.icon_extra_large)

@Composable
fun iconButtonSize() = dimensionResource(id = com.cebolao.lotofacil.R.dimen.icon_button_size)

@Composable
fun iconSizeInsideButton() = dimensionResource(id = com.cebolao.lotofacil.R.dimen.icon_size_inside_button)
