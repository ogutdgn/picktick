package com.example.picktick.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

@Composable
fun PriceRangeBar(minPrice: String, maxPrice: String, onMinChange: (String) -> Unit, onMaxChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
        Text(text = "Price Range", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = minPrice,
                onValueChange = { if (it.all { c -> c.isDigit() }) onMinChange(it) },
                modifier = Modifier.weight(1f),
                label = { Text("Min $") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            Text("-", fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = maxPrice,
                onValueChange = { if (it.all { c -> c.isDigit() }) onMaxChange(it) },
                modifier = Modifier.weight(1f),
                label = { Text("Max $") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

@Composable
fun DateRangeFilter(startDate: String, endDate: String, onStartChange: (String) -> Unit, onEndChange: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    fun showDatePicker(onDateSelected: (String) -> Unit) {
        DatePickerDialog(
            context,
            { _, year, month, day -> onDateSelected("$day/${month + 1}/$year") },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
        Text(text = "Date Range", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { showDatePicker { onStartChange(it) } },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(text = startDate.ifEmpty { "Start Date" }, color = Color.DarkGray, fontSize = 12.sp)
            }
            OutlinedButton(
                onClick = { showDatePicker { onEndChange(it) } },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(text = endDate.ifEmpty { "End Date" }, color = Color.DarkGray, fontSize = 12.sp)
            }
        }
    }
}
