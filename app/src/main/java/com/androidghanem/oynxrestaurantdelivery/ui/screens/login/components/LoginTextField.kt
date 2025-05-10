package com.androidghanem.oynxrestaurantdelivery.ui.screens.login.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidghanem.oynxrestaurantdelivery.ui.theme.PrimaryTeal
import com.androidghanem.oynxrestaurantdelivery.ui.theme.TextFieldBackground

/**
 * Custom text field specifically styled for login screen with improved focus handling
 */
@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val hasFocus = remember { mutableStateOf(false) }
    
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                fontSize = 14.sp,
                text = label,
                modifier = if (value.isEmpty() && !hasFocus.value) Modifier.fillMaxWidth() else Modifier,
                textAlign = if (value.isEmpty() && !hasFocus.value) TextAlign.Center else TextAlign.Start,
                color = PrimaryTeal
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .onFocusChanged { hasFocus.value = it.isFocused },
        shape = RoundedCornerShape(32.dp),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = TextFieldBackground,
            focusedContainerColor = TextFieldBackground,
            focusedBorderColor = PrimaryTeal,
            unfocusedBorderColor = PrimaryTeal.copy(alpha = 0.7f),
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedLabelColor = PrimaryTeal,
            unfocusedLabelColor = PrimaryTeal.copy(alpha = 0.7f),
        ),
        singleLine = true
    )
}