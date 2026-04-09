package com.example.thesis.view.popUpContent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.thesis.view.popUpContent.parts.ImageDetailsCard
import com.example.thesis.view.popUpContent.parts.PictureResultCard

@Composable
@Preview
fun PopUpContent() {
    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {

        PictureResultCard()
        Spacer(modifier = Modifier.height(50.dp))
        ImageDetailsCard()
    }
}