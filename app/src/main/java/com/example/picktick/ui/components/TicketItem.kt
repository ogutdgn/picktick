package com.example.picktick.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.picktick.model.TicketListing
import com.example.picktick.PickTickBlue
import com.example.picktick.PickTickOrange

@Composable
fun TicketItem(listing: TicketListing, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(listing.title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = PickTickBlue)
            Text(listing.eventName, fontSize = 13.sp, color = Color.Gray)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DetailChip(listing.date)
                DetailChip(listing.time)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DetailChip(listing.location, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                DetailChip("$${listing.price.toInt()}", isPrice = true)
            }
        }
    }
}

@Composable
fun DetailChip(text: String, isPrice: Boolean = false, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isPrice) PickTickOrange.copy(alpha = 0.2f) else Color(0xFFF0F0F0),
        modifier = modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = if (isPrice) FontWeight.Bold else FontWeight.Normal,
            color = if (isPrice) PickTickOrange else Color.DarkGray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("$label: ", fontWeight = FontWeight.Bold, color = PickTickBlue)
        Text(value)
    }
}
