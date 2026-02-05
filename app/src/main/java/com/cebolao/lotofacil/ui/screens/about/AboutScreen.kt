package com.cebolao.lotofacil.ui.screens.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.components.ClickableCard
import com.cebolao.lotofacil.ui.components.InfoDialog
import com.cebolao.lotofacil.ui.components.StandardScreenHeader
import com.cebolao.lotofacil.ui.theme.AppCardDefaults
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.iconLarge

@Composable
fun AboutScreen(
    onNavigateToUserStats: () -> Unit
) {
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

    val guideItems = remember {
        listOf(
            InfoItem.Rules(Icons.Default.Gavel, content = { RulesInfoContent() }),
            InfoItem.Probabilities(Icons.Default.Calculate, content = { ProbabilitiesTable() }),
            InfoItem.Bolao(Icons.Default.Group, content = { BolaoInfoContent() }),
            InfoItem.Purpose(Icons.Default.Lightbulb, content = { PurposeInfoContent() })
        )
    }

    val legalItems = remember {
        listOf(
            InfoItem.Legal(Icons.Default.Info, content = { LegalInfoContent() }),
            InfoItem.Privacy(Icons.Default.PrivacyTip, content = { PrivacyInfoContent() })
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            StandardScreenHeader(
                title = stringResource(id = R.string.studio_name),
                subtitle = stringResource(id = R.string.studio_slogan),
                icon = Icons.Outlined.Info
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                bottom = AppSpacing.xxxl
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            item {
                StudioHero()
            }
            
            item {
                SectionHeader(stringResource(R.string.about_section_guide))
            }
            
            items(guideItems, key = { it.titleResId }) { info ->
                AnimateOnEntry {
                    InfoCard(info) {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        dialogContent = info
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(AppSpacing.md))
                SectionHeader(stringResource(R.string.about_section_legal))
            }

            items(legalItems, key = { it.titleResId }) { info ->
                AnimateOnEntry {
                    InfoCard(info) {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        dialogContent = info
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(AppSpacing.md))
                UserStatsCard(onNavigateToUserStats)
            }
        }
    }
}

@Composable
private fun UserStatsCard(onClick: () -> Unit) {
    ClickableCard(
        onClick = onClick,
        backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        modifier = Modifier.padding(horizontal = AppSpacing.lg)
    ) {
        Row(
            modifier = Modifier.padding(AppSpacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(AppSpacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = R.string.user_stats_card_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = R.string.user_stats_card_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.xs)
    )
}

@Composable
private fun StudioHero() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = AppSpacing.xxl),
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
        }
    }
}

@Composable
private fun InfoCard(item: InfoItem, onClick: () -> Unit) {
    ClickableCard(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        elevation = AppCardDefaults.elevation,
        modifier = Modifier.padding(horizontal = AppSpacing.lg)
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
@Composable
fun ProbabilitiesTable() {
    val headers = listOf(
        R.string.probability_table_header_prize,
        R.string.probability_table_header_probability
    )
    val rows = listOf(
        R.string.probability_15_hits to R.string.probability_15_hits_value,
        R.string.probability_14_hits to R.string.probability_14_hits_value,
        R.string.probability_13_hits to R.string.probability_13_hits_value,
        R.string.probability_12_hits to R.string.probability_12_hits_value,
        R.string.probability_11_hits to R.string.probability_11_hits_value
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            headers.forEach { headerRes ->
                Text(
                    text = stringResource(id = headerRes),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    textAlign = if (headerRes == headers.last()) TextAlign.End else TextAlign.Start
                )
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        rows.forEach { (labelRes, valueRes) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AppSpacing.xs),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = labelRes),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(id = valueRes),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
