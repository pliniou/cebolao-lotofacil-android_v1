package com.cebolao.lotofacil.ui.screens.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R

@Composable
fun BolaoInfoContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.about_bolao_desc_header), style = MaterialTheme.typography.titleMedium)
        Text(stringResource(R.string.about_bolao_desc_body), style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(8.dp))
        FormattedText(text = stringResource(R.string.about_bolao_desc_footer))
    }
}
