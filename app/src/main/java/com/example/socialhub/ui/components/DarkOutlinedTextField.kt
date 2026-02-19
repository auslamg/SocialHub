package com.example.socialhub.ui.components

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun DarkOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    outlineColor: Color = AppColors.LightGreyText
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        colors = TextFieldDefaults.colors(
            focusedTextColor = AppColors.WhiteText,
            unfocusedTextColor = AppColors.WhiteText,
            focusedContainerColor = AppColors.PostCardBG,
            unfocusedContainerColor = AppColors.PostCardBG,
            cursorColor = AppColors.BlackText,
            focusedSupportingTextColor = AppColors.LightGreyText,
            unfocusedSupportingTextColor = AppColors.LightGreyText,
            focusedIndicatorColor = outlineColor,
            unfocusedIndicatorColor = outlineColor,
            focusedLabelColor = AppColors.LightGreyText,
            unfocusedLabelColor = AppColors.LightGreyText
        )
    )
}
