package com.cebolao.lotofacil.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.cebolao.lotofacil.ui.theme.LocalAnimationEnabled
import kotlinx.coroutines.delay

/**
 * Um wrapper que anima a entrada de seu conteúdo na tela.
 * A animação pode ser controlada globalmente através do `LocalAnimationEnabled`.
 * Otimizado para performance com animações únicas e leves, usando remember para evitar recomposições.
 */
@Composable
fun AnimateOnEntry(
    modifier: Modifier = Modifier,
    delayMillis: Long = 0,
    durationMillis: Int = 250, // Further reduced for better performance
    content: @Composable () -> Unit
) {
    val animationsEnabled = LocalAnimationEnabled.current
    
    // Optimize state management with proper remember keys
    var isVisible by remember(delayMillis) { mutableStateOf(false) }

    LaunchedEffect(delayMillis) {
        if (delayMillis > 0) delay(delayMillis)
        isVisible = true
    }

    if (animationsEnabled) {
        AnimatedVisibility(
            modifier = modifier,
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight / 16 }, // Further reduced for subtler motion
                animationSpec = tween(
                    durationMillis = durationMillis,
                    easing = androidx.compose.animation.core.FastOutSlowInEasing
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = durationMillis,
                    easing = androidx.compose.animation.core.FastOutSlowInEasing
                )
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight / 16 },
                animationSpec = tween(durationMillis = 150) // Reduced exit duration
            ) + fadeOut(
                animationSpec = tween(durationMillis = 150)
            )
        ) {
            content()
        }
    } else {
        // Se as animações estiverem desabilitadas, apenas mostra o conteúdo.
        if (isVisible) {
            Box(modifier) {
                content()
            }
        }
    }
}