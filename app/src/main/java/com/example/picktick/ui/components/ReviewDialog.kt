package com.example.picktick.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.picktick.PickTickBlue
import com.example.picktick.PickTickOrange

@Composable
fun ReviewDialog(targetName: String, onDismiss: () -> Unit, onSubmit: (Float) -> Unit) {
    var selectedRating by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rate $targetName") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("How was your experience?", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= selectedRating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = PickTickOrange,
                            modifier = Modifier.size(36.dp).clickable { selectedRating = i }
                        )
                    }
                }
                if (selectedRating > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when (selectedRating) {
                            1 -> "Poor"
                            2 -> "Fair"
                            3 -> "Good"
                            4 -> "Very Good"
                            else -> "Excellent!"
                        },
                        color = PickTickOrange,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (selectedRating > 0) onSubmit(selectedRating.toFloat()) },
                enabled = selectedRating > 0
            ) {
                Text("Submit", color = if (selectedRating > 0) PickTickBlue else Color.Gray, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
        }
    )
}
