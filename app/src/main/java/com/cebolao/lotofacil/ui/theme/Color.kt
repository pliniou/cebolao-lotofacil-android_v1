package com.cebolao.lotofacil.ui.theme

import androidx.compose.ui.graphics.Color

// Vintage, geometric palette (earthy + muted accents).
// Keep existing token names to avoid churn across the codebase.

// Pantone 2593U / 2627U Palette (Flat Retro Material)

// Base Colors (Dark - Keeping dark theme base but tinted purple/neutral)
val BaseBackground = Color(0xFF1A1521) // Very Dark Purple Grey
val Surface1 = Color(0xFF241C2D) // Dark Purple Surface
val Surface2 = Color(0xFF2E243A) // Lighter Purple Surface
val Surface3 = Color(0xFF3A2D48) // Highlight Surface
val OutlineStroke = Color(0xFF5A4B6B) // Purple-ish Outline

// Text Colors
val TextPrimary = Color(0xFFF2F0F4) // Off-white/Ghost White
val TextSecondary = Color(0xFFC7BCCF) // Muted lavender
val TextTertiary = Color(0xFF988C9F) 

// Brand Colors (Pantone 2593U / 2627U)
val BrandPrimary = Color(0xFF803594) // Pantone 2593U (R128 G53 B148)
val BrandSecondary = Color(0xFF702A82) // Pantone 2627U (R112 G42 B130)
val BrandSubtle = Color(0xFF4A2558) // Deep muted purple for backgrounds

// Support Palette (Caixa-inspired)
val CaixaTurquoise = Color(0xFF54BBAA) // ~Pantone 326C
val CaixaBlue = Color(0xFF005CA9) // ~Pantone 287C
val CaixaOrange = Color(0xFFF39200) // ~Pantone 151C

// Status Colors
val Success = CaixaTurquoise
val Warning = CaixaOrange
val Error = Color(0xFFE05B5B) 

// Disabled States
val DisabledContainer = Color(0xFF2A2430)
val DisabledContent = Color(0xFF6A5D75)

// Material 3 Light Theme (Light version of the palette)
val LightPrimary = BrandPrimary
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFFAD7FF)
val LightOnPrimaryContainer = BrandSecondary
val LightSecondary = BrandSecondary
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFF0DBFF)
val LightOnSecondaryContainer = Color(0xFF2B1238) // Dark purple text
val LightTertiary = CaixaTurquoise
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFC6F8EF)
val LightOnTertiaryContainer = Color(0xFF003730)
val LightError = Color(0xFFB3261E)
val LightErrorContainer = Color(0xFFFFDAD4)
val LightOnError = Color(0xFFFFFFFF)
val LightOnErrorContainer = Color(0xFF410001)
val LightBackground = Color(0xFFFDFBFD) // Very light purple tint
val LightOnBackground = Color(0xFF1C1B1F)
val LightSurface = Color(0xFFFFFBFF)
val LightOnSurface = Color(0xFF1C1B1F)
val LightSurfaceVariant = Color(0xFFE7E0EB)
val LightOnSurfaceVariant = Color(0xFF49454F)
val LightOutline = Color(0xFF79747E)
val LightOutlineVariant = Color(0xFFCAC4D0)
