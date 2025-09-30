package com.johan.racketmatchapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CombinedScoreboard(
    p1Display: String,
    p2Display: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Stor, tydlig resultatrad: 15–15, 40–30, 6–6 (tiebreak)
        Text(
            text = "$p1Display – $p2Display",
            textAlign = TextAlign.Center,
            fontSize = 64.sp,                 // gör den STOR
            fontWeight = FontWeight.Black,
            lineHeight = 64.sp,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Score $p1Display to $p2Display" }
        )
    }
}
