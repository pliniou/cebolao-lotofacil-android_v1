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

// Composable helpers for common sizes that might be used across the app
@Composable
fun iconSmall() = 18.dp

@Composable
fun iconMedium() = 24.dp

@Composable
fun iconLarge() = 32.dp

@Composable
fun iconExtraLarge() = 48.dp

@Composable
fun iconButtonSize() = 40.dp
