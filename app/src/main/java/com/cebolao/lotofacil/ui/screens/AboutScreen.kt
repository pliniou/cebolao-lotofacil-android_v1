package com.cebolao.lotofacil.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.components.ClickableCard
import com.cebolao.lotofacil.ui.components.InfoDialog
import com.cebolao.lotofacil.ui.screens.about.BolaoInfoContent
import com.cebolao.lotofacil.ui.screens.about.InfoItem
import com.cebolao.lotofacil.ui.screens.about.LegalInfoContent
import com.cebolao.lotofacil.ui.screens.about.PrivacyInfoContent
import com.cebolao.lotofacil.ui.screens.about.ProbabilitiesTable
import com.cebolao.lotofacil.ui.screens.about.PurposeInfoContent
import com.cebolao.lotofacil.ui.screens.about.RulesInfoContent
import com.cebolao.lotofacil.ui.theme.AppCardDefaults
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.iconLarge

@Composable
fun AboutScreen() {
    var dialogContent by remember { mutableStateOf<InfoItem?>(null) }
    val hapticFeedback = LocalHapticFeedback.current

    dialogContent?.let { item ->
        InfoDialog(
            onDismissRequest = { 
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                dialogContent = null 
            },
            dialogTitle = stringResource(id = item.titleResId),
            icon = item.icon
        ) { item.content() }
    }

    val items = remember {
        listOf(
            InfoItem.Rules(Icons.Default.Gavel, content = { RulesInfoContent() }),
            InfoItem.Probabilities(Icons.Default.Calculate, content = { ProbabilitiesTable() }),
            InfoItem.Bolao(Icons.Default.Group, content = { BolaoInfoContent() }),
            InfoItem.Purpose(Icons.Default.Lightbulb, content = { PurposeInfoContent() }),
            InfoItem.Legal(Icons.Default.Info, content = { LegalInfoContent() }),
            InfoItem.Privacy(Icons.Default.PrivacyTip, content = { PrivacyInfoContent() })
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        contentPadding = PaddingValues(
            top = AppSpacing.lg, 
            bottom = 120.dp
        ),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
    ) {
        item {
            StudioHero()
        }
        items(items, key = { it.titleResId }) { info ->
            AnimateOnEntry(Modifier.padding(horizontal = AppSpacing.xl)) {
                InfoCard(info) { 
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    dialogContent = info 
                }
            }
        }
    }
}

@Composable
private fun StudioHero() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = AppSpacing.xxl,
                start = AppSpacing.lg,
                end = AppSpacing.lg
            ),
        contentAlignment = Alignment.Center
    ) {
        // Studio Hero as background/decorative element
        Image(
            painter = painterResource(R.drawable.ic_studiohero),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .alpha(0.15f),
            contentScale = ContentScale.Crop
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_cebolalogo),
                contentDescription = stringResource(id = R.string.studio_logo_description),
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(AppSpacing.lg))
            Text(
                stringResource(R.string.studio_name),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            Text(
                stringResource(R.string.studio_slogan),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun InfoCard(item: InfoItem, onClick: () -> Unit) {
    ClickableCard(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        elevation = AppCardDefaults.elevation
    ) {
        Row(
            modifier = Modifier.padding(AppCardDefaults.defaultPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                item.icon, 
                null, 
                tint = MaterialTheme.colorScheme.primary, 
                modifier = Modifier.size(iconLarge())
            )
            Spacer(Modifier.width(AppSpacing.md))
            Column(Modifier.weight(1f)) {
                Text(stringResource(id = item.titleResId), style = MaterialTheme.typography.titleMedium)
                Text(
                    stringResource(id = item.subtitleResId),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
