package com.example.thesis.view.middleContent.parts

import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Preview

@Composable
fun DetectionDetailsCard() {
    var defect by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var roadSection by remember { mutableStateOf("") }
    var station by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UploadResultTitleText()


            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InputBox("Defect", defect, { defect = it }, Modifier.weight(1f))
                InputBox("Quantity", quantity, { quantity = it }, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InputBox("Road Section", roadSection, { roadSection = it }, Modifier.weight(1f))
                InputBox("Station", station, { station = it }, Modifier.weight(1f))
            }

            // --- Map Placeholder ---//TODO GIS MAP VIEW FOR SPECIFIC UPLOAD

            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.LightGray, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("GIS Map Component Goes Here", color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(24.dp))
            CoordinateTable()

            // --- Confirm Button Section ---
            Spacer(modifier = Modifier.height(32.dp)) // Extra space before the button

            androidx.compose.material3.Button(
                onClick = {
                    /* TODO: Handle confirmation logic here */
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF000000), // A professional blue, or use MaterialTheme.colorScheme.primary
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Confirm Details",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun InputBox(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        androidx.compose.material3.OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF2F2F2), // Light gray like your image
                focusedContainerColor = Color(0xFFF2F2F2),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Gray
            )
        )
    }
}


@Composable
fun CoordinateTable() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Subtle Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            Spacer(
                modifier = Modifier
                    .weight(0.3f)
            )
            Text(
                text = "DECIMAL",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
            )
            Text(
                text = "DMS",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
            )
        }

        CoordinateRow("Latitude")
        CoordinateRow("Longitude")
    }
}

@Composable
fun CoordinateRow(label: String) {
    var decimalValue by remember { mutableStateOf("") }
    var dmsValue by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Side Label
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray,
            modifier = Modifier
                .weight(0.4F)
        )

        // The "Input Capsule"
        Row(
            modifier = Modifier
                .weight(2f)
                .background(
                    color = Color(0xFFF2F2F2),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CoordinateInputField(
                value = decimalValue,
                onValueChange = { decimalValue = it },
                modifier = Modifier
                    .weight(1f)
            )

            // Vertical Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(Color.LightGray)
            )

            CoordinateInputField(
                value = dmsValue,
                onValueChange = { dmsValue = it },
                modifier = Modifier
                    .weight(1f)
            )
        }
    }
}

@Composable
fun CoordinateInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.padding(horizontal = 8.dp),
        textStyle = androidx.compose.ui.text.TextStyle(
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = Color.Black
        ),
        decorationBox = { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    "",
                    style = androidx.compose.ui.text.TextStyle(
                        textAlign = TextAlign.Center,
                        color = Color.LightGray,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            innerTextField()
        }
    )
}

@Composable
fun UploadResultTitleText(){
    Column (modifier = Modifier.padding(bottom = 12.dp)){
        Text(
            text = "Road Damage Details",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Left,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
