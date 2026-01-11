package com.cebolao.lotofacil.data

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
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
enum class FilterType(
    val title: String,
    val description: String,
    val fullRange: ClosedFloatingPointRange<Float>,
    val defaultRange: ClosedFloatingPointRange<Float>,
    @Serializable(with = ImageVectorSerializer::class)
    val icon: ImageVector,
    val historicalSuccessRate: Float
) {
    SOMA_DEZENAS(
        title = "Soma das Dezenas",
        description = "A soma dos 15 números. A grande maioria dos sorteios (cerca de 72%) possui uma soma entre 170 e 220. Este é um dos filtros mais eficazes para eliminar combinações improváveis.",
        fullRange = 120f..270f, // Soma mínima (1..15) e máxima (11..25)
        defaultRange = 170f..220f,
        icon = Icons.Default.Calculate,
        historicalSuccessRate = 0.72f
    ),
    PARES(
        title = "Números Pares",
        description = "A quantidade de números pares. O ideal é um equilíbrio. Cerca de 78% dos sorteios têm entre 6 e 9 números pares.",
        fullRange = 0f..12f, // Existem 12 números pares de 1 a 25
        defaultRange = 6f..9f,
        icon = Icons.Default.Numbers,
        historicalSuccessRate = 0.78f
    ),
    PRIMOS(
        title = "Números Primos",
        description = "A quantidade de números primos. Existem 9 primos (2,3,5,7,11,13,17,19,23). Cerca de 74% dos sorteios têm entre 4 e 7 primos.",
        fullRange = 0f..9f,
        defaultRange = 4f..7f,
        icon = Icons.Default.Percent,
        historicalSuccessRate = 0.74f
    ),
    MOLDURA(
        title = "Dezenas na Moldura",
        description = "A quantidade de números nas bordas do volante (16 números no total). Cerca de 76% dos sorteios têm entre 8 e 11 números da moldura.",
        fullRange = 0f..15f, // Máximo de 15 números no jogo
        defaultRange = 8f..11f,
        icon = Icons.Default.Grid4x4,
        historicalSuccessRate = 0.76f
    ),
    RETRATO(
        title = "Dezenas no Retrato",
        description = "A quantidade de números no centro do volante (9 números no total). Cerca de 71% dos sorteios têm entre 4 e 7 números do retrato.",
        fullRange = 0f..9f,
        defaultRange = 4f..7f,
        icon = Icons.Outlined.ShapeLine,
        historicalSuccessRate = 0.71f
    ),
    FIBONACCI(
        title = "Sequência de Fibonacci",
        description = "A quantidade de números da sequência de Fibonacci (1,2,3,5,8,13,21). Cerca de 68% dos sorteios têm entre 3 e 5 números de Fibonacci.",
        fullRange = 0f..7f,
        defaultRange = 3f..5f,
        icon = Icons.Default.Timeline,
        historicalSuccessRate = 0.68f
    ),
    MULTIPLOS_DE_3(
        title = "Múltiplos de 3",
        description = "A quantidade de números que são múltiplos de 3. Cerca de 69% dos sorteios têm entre 3 e 6 múltiplos de 3.",
        fullRange = 0f..8f, // Existem 8 múltiplos de 3
        defaultRange = 3f..6f,
        icon = Icons.Default.Functions,
        historicalSuccessRate = 0.69f
    ),
    REPETIDAS_CONCURSO_ANTERIOR(
        title = "Repetidas do Anterior",
        description = "A quantidade de números que se repetiram do concurso anterior. Este é um padrão muito forte: 84% dos sorteios repetem entre 8 e 10 números.",
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