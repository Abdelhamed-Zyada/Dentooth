package com.example.dentooth.core.localization.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentooth.core.localization.AppLanguage
import com.example.dentooth.core.localization.LocalAppLanguage
import com.example.dentooth.core.localization.LocalizationManager

@Composable
fun LanguageToggleButton(
    modifier: Modifier = Modifier
) {
    val currentLang = LocalAppLanguage.current
    val nextLang = if (currentLang == AppLanguage.ENGLISH) AppLanguage.ARABIC else AppLanguage.ENGLISH

    Surface(
        onClick = { LocalizationManager.toggleLanguage() },
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (currentLang == AppLanguage.ENGLISH) "🇪🇬" else "🇬🇧",
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (currentLang == AppLanguage.ENGLISH) "العربية" else "English",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
