package com.cebolao.lotofacil.ui.screens.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R

@Composable
fun PurposeInfoContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.about_purpose_desc_header), style = MaterialTheme.typography.titleMedium)
        Text(stringResource(R.string.about_purpose_desc_body), style = MaterialTheme.typography.bodyLarge)
        Text(stringResource(R.string.about_purpose_desc_footer), style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic)
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        InfoListItem(icon = Icons.Default.CheckCircle, text = stringResource(R.string.about_purpose_item1_title), description = stringResource(R.string.about_purpose_item1_desc))
        InfoListItem(icon = Icons.Default.CheckCircle, text = stringResource(R.string.about_purpose_item2_title), description = stringResource(R.string.about_purpose_item2_desc))
        InfoListItem(icon = Icons.Default.CheckCircle, text = stringResource(R.string.about_purpose_item3_title), description = stringResource(R.string.about_purpose_item3_desc))
    }
}
