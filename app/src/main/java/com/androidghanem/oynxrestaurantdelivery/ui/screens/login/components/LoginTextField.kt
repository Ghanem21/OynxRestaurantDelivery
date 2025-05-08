package com.androidghanem.oynxrestaurantdelivery.ui.screens.login.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidghanem.oynxrestaurantdelivery.ui.theme.TextFieldBackground

@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
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
                textAlign = TextAlign.Center
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .onFocusChanged { hasFocus.value = it.isFocused },
        shape = RoundedCornerShape(8.dp),
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = TextFieldBackground,
            focusedContainerColor = TextFieldBackground
        ),
        singleLine = true
    )
}