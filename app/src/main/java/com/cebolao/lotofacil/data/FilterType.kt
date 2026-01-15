package com.cebolao.lotofacil.data

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Grid4x4
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.outlined.ShapeLine
import androidx.compose.ui.graphics.vector.ImageVector
import com.cebolao.lotofacil.R
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
enum class FilterType(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    val fullRange: ClosedFloatingPointRange<Float>,
    val defaultRange: ClosedFloatingPointRange<Float>,
    @Serializable(with = ImageVectorSerializer::class)
    val icon: ImageVector,
    val historicalSuccessRate: Float
) {
    SOMA_DEZENAS(
        titleRes = R.string.filter_soma_dezenas_title,
        descriptionRes = R.string.filter_soma_dezenas_description,
        fullRange = 120f..270f, // Soma mínima (1..15) e máxima (11..25)
        defaultRange = 170f..220f,
        icon = Icons.Default.Calculate,
        historicalSuccessRate = 0.72f
    ),
    PARES(
        titleRes = R.string.filter_pares_title,
        descriptionRes = R.string.filter_pares_description,
        fullRange = 0f..12f, // Existem 12 números pares de 1 a 25
        defaultRange = 6f..9f,
        icon = Icons.Default.Numbers,
        historicalSuccessRate = 0.78f
    ),
    PRIMOS(
        titleRes = R.string.filter_primos_title,
        descriptionRes = R.string.filter_primos_description,
        fullRange = 0f..9f,
        defaultRange = 4f..7f,
        icon = Icons.Default.Percent,
        historicalSuccessRate = 0.74f
    ),
    MOLDURA(
        titleRes = R.string.filter_moldura_title,
        descriptionRes = R.string.filter_moldura_description,
        fullRange = 0f..15f, // Máximo de 15 números no jogo
        defaultRange = 8f..11f,
        icon = Icons.Default.Grid4x4,
        historicalSuccessRate = 0.76f
    ),
    RETRATO(
        titleRes = R.string.filter_retrato_title,
        descriptionRes = R.string.filter_retrato_description,
        fullRange = 0f..9f,
        defaultRange = 4f..7f,
        icon = Icons.Outlined.ShapeLine,
        historicalSuccessRate = 0.71f
    ),
    FIBONACCI(
        titleRes = R.string.filter_fibonacci_title,
        descriptionRes = R.string.filter_fibonacci_description,
        fullRange = 0f..7f,
        defaultRange = 3f..5f,
        icon = Icons.Default.Timeline,
        historicalSuccessRate = 0.68f
    ),
    MULTIPLOS_DE_3(
        titleRes = R.string.filter_multiplos_3_title,
        descriptionRes = R.string.filter_multiplos_3_description,
        fullRange = 0f..8f, // Existem 8 múltiplos de 3
        defaultRange = 3f..6f,
        icon = Icons.Default.Functions,
        historicalSuccessRate = 0.69f
    ),
    REPETIDAS_CONCURSO_ANTERIOR(
        titleRes = R.string.filter_repetidas_anterior_title,
        descriptionRes = R.string.filter_repetidas_anterior_description,
        fullRange = 0f..15f,
        defaultRange = 8f..10f,
        icon = Icons.Default.Repeat,
        historicalSuccessRate = 0.84f
    );
}

@Serializable
enum class FilterCategory {
    MATHEMATICAL,
    DISTRIBUTION,
    POSITIONAL,
    TEMPORAL
}

object ImageVectorSerializer : KSerializer<ImageVector> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ImageVector", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ImageVector) {
        val identifier = when (value) {
            Icons.Default.Calculate -> "calculate"
            Icons.Default.Numbers -> "numbers"
            Icons.Default.Percent -> "percent"
            Icons.Default.Grid4x4 -> "grid"
            Icons.Outlined.ShapeLine -> "shape"
            Icons.Default.Timeline -> "timeline"
            Icons.Default.Functions -> "functions"
            Icons.Default.Repeat -> "repeat"
            else -> "unknown"
        }
        encoder.encodeString(identifier)
    }

    override fun deserialize(decoder: Decoder): ImageVector {
        return when (decoder.decodeString()) {
            "calculate" -> Icons.Default.Calculate
            "numbers" -> Icons.Default.Numbers
            "percent" -> Icons.Default.Percent
            "grid" -> Icons.Default.Grid4x4
            "shape" -> Icons.Outlined.ShapeLine
            "timeline" -> Icons.Default.Timeline
            "functions" -> Icons.Default.Functions
            "repeat" -> Icons.Default.Repeat
            else -> Icons.Default.Calculate
        }
    }
}