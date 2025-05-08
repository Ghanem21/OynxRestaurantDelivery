package com.androidghanem.oynxrestaurantdelivery.ui.screens.login.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.androidghanem.domain.model.Language
import com.androidghanem.oynxrestaurantdelivery.R
import com.androidghanem.oynxrestaurantdelivery.ui.theme.MontserratFontFamily
import com.androidghanem.oynxrestaurantdelivery.ui.theme.PrimaryTeal

@Composable
fun LanguageDialog(
    languages: List<Language>,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    if (languages.isEmpty()) {
        return
    }

    val selectedLanguageCode = remember {
        mutableStateOf(languages.find { it.isSelected }?.code ?: "en")
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.choose_language),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryTeal,
                    fontFamily = MontserratFontFamily,
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .align(Alignment.Start)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Arabic option
                    val arabicLanguage = languages.find { it.code == "ar" }
                    if (arabicLanguage != null) {
                        LanguageOption(
                            language = arabicLanguage,
                            isSelected = selectedLanguageCode.value == "ar",
                            onSelect = {
                                selectedLanguageCode.value = "ar"
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // English option
                    val englishLanguage = languages.find { it.code == "en" }
                    if (englishLanguage != null) {
                        LanguageOption(
                            language = englishLanguage,
                            isSelected = selectedLanguageCode.value == "en",
                            onSelect = {
                                selectedLanguageCode.value = "en"
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Button(
                    onClick = {
                        onLanguageSelected(selectedLanguageCode.value)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = stringResource(R.string.apply),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        fontFamily = MontserratFontFamily
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageOption(
    language: Language,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isSelected) Color(0xFFE6F7F9) else Color.White
    val borderColor = if (isSelected) PrimaryTeal else Color.LightGray
    val borderWidth = if (isSelected) 2.dp else 1.dp

    val layoutDirection = LocalLayoutDirection.current
    val isRtl = layoutDirection == LayoutDirection.Rtl

    val flagRes = when (language.code) {
        "ar" -> R.drawable.arabic_flag
        "en" -> R.drawable.english_flag
        else -> R.drawable.english_flag // Default
    }

    Box(
        modifier = modifier
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onSelect() }
            .padding(horizontal = 8.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = flagRes),
                contentDescription = stringResource(R.string.flag_for, language.name),
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer {
                        scaleX = if (isRtl) -1f else 1f
                    }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = language.localizedName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    fontFamily = MontserratFontFamily
                )

                // Display English name for Arabic language and vice versa
                if (language.code == "ar") {
                    Text(
                        text = stringResource(R.string.arabic),
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontFamily = MontserratFontFamily
                    )
                } else {
                    Text(
                        text = stringResource(R.string.english),
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontFamily = MontserratFontFamily
                    )
                }
            }
        }
    }
}