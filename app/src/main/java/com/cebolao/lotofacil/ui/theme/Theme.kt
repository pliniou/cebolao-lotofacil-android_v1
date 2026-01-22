package com.cebolao.lotofacil.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
)

private val DarkColors = darkColorScheme(
    primary = BrandPrimary,
    onPrimary = BaseBackground,
    primaryContainer = BrandSubtle,
    onPrimaryContainer = BrandPrimary,
    secondary = BrandSecondary,
    onSecondary = BaseBackground,
    secondaryContainer = Surface2,
    onSecondaryContainer = TextSecondary,
    tertiary = Success,
    onTertiary = BaseBackground,
    tertiaryContainer = Surface2,
    onTertiaryContainer = Success,
    error = Error,
    onError = BaseBackground,
    errorContainer = Color(0xFF4A1015),
    onErrorContainer = Error,
    background = BaseBackground,
    onBackground = TextPrimary,
    surface = Surface1,
    onSurface = TextPrimary,
    surfaceVariant = Surface2,
    onSurfaceVariant = TextSecondary,
    outline = OutlineStroke,
    outlineVariant = TextTertiary,
)

val LocalAnimationEnabled = staticCompositionLocalOf { true }

@Composable
fun CebolaoLotofacilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // window.statusBarColor = Color.Transparent.toArgb()
            // window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    val appColors = AppColors.fromColorScheme(colorScheme)
    
    // Create adaptive typography based on current configuration
    val adaptiveTypography = createAdaptiveTypography()

    CompositionLocalProvider(
        LocalAppColors provides appColors,
        LocalAnimationEnabled provides true
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = adaptiveTypography,
            shapes = Shapes,
            content = content
        )
    }
}

// Theme Preview Components
@Preview(showBackground = true, name = "Dark Theme")
@Composable
private fun DarkThemePreview() {
    CebolaoLotofacilTheme(darkTheme = true) {
        ThemePreviewContent()
    }
}

@Preview(showBackground = true, name = "Light Theme")
@Composable
private fun LightThemePreview() {
    CebolaoLotofacilTheme(darkTheme = false) {
        ThemePreviewContent()
    }
}

@Composable
private fun ThemePreviewContent() {
    val colors = LocalAppColors.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
    ) {
        // Header with logo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(colors.surface2, CircleShape)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🧅",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Column {
                Text(
                    text = "Cebolão Lotofácil",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = "Análise e Geração de Jogos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
        }

        // Color palette showcase
        Text(
            text = "Palette Showcase",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )
        
        // Surface colors
        Row(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier.size(60.dp),
                colors = CardDefaults.cardColors(containerColor = colors.background)
            ) {}
            Card(
                modifier = Modifier.size(60.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface1)
            ) {}
            Card(
                modifier = Modifier.size(60.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface2)
            ) {}
            Card(
                modifier = Modifier.size(60.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface3)
            ) {}
        }

        // Brand colors
        Row(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier.size(60.dp),
                colors = CardDefaults.cardColors(containerColor = colors.brandPrimary)
            ) {}
            Card(
                modifier = Modifier.size(60.dp),
                colors = CardDefaults.cardColors(containerColor = colors.brandSecondary)
            ) {}
            Card(
                modifier = Modifier.size(60.dp),
                colors = CardDefaults.cardColors(containerColor = colors.brandSubtle)
            ) {}
        }

        // Status colors
        Row(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier.size(60.dp),
                colors = CardDefaults.cardColors(containerColor = colors.success)
            ) {}
            Card(
                modifier = Modifier.size(60.dp),
                colors = CardDefaults.cardColors(containerColor = colors.warning)
            ) {}
            Card(
                modifier = Modifier.size(60.dp),
                colors = CardDefaults.cardColors(containerColor = colors.error)
            ) {}
        }

        // Interactive elements
        Card(
            colors = CardDefaults.cardColors(containerColor = colors.surface1)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Interactive Elements",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                
                Button(onClick = {}) {
                    Text("Gerar Jogo")
                }
                
                Row(
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(colors.brandPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "01",
                            color = colors.background,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(colors.surface2, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "02",
                            color = colors.textPrimary,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(colors.disabledContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "03",
                            color = colors.disabledContent,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
