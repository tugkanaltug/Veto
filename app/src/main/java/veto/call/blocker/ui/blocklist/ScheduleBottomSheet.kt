package veto.call.blocker.ui.blocklist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import veto.call.blocker.ui.theme.VetoTheme
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleBottomSheet(
    contact: Contact,
    initialStartTime: String? = null,
    initialEndTime: String? = null,
    initialActiveDays: String? = null,
    onDismiss: () -> Unit,
    onSave: (startTime: String, endTime: String, activeDays: String) -> Unit
) {
    var startTime by remember { mutableStateOf(initialStartTime ?: "00:00") }
    var endTime by remember { mutableStateOf(initialEndTime ?: "23:59") }
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val selectedDays = remember { 
        val list = mutableStateListOf<Int>()
        if (initialActiveDays != null) {
            list.addAll(initialActiveDays.split(",").filter { it.isNotEmpty() }.map { it.toInt() })
        }
        list
    }

    val showStartTimePicker = remember { mutableStateOf(false) }
    val showEndTimePicker = remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Block Schedule",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Set when to block calls from ${contact.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Time Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TimeCard(
                    label = "Start Time",
                    time = startTime,
                    onClick = { showStartTimePicker.value = true },
                    modifier = Modifier.weight(1f)
                )
                TimeCard(
                    label = "End Time",
                    time = endTime,
                    onClick = { showEndTimePicker.value = true },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Day Selection
            Text(
                text = "Active Days",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                items(daysOfWeek.indices.toList()) { index ->
                    val dayValue = index + 1
                    val isSelected = selectedDays.contains(dayValue)
                    DayChip(
                        day = daysOfWeek[index],
                        isSelected = isSelected,
                        onClick = {
                            if (isSelected) selectedDays.remove(dayValue)
                            else selectedDays.add(dayValue)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    onSave(startTime, endTime, selectedDays.sorted().joinToString(","))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save Schedule", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showStartTimePicker.value) {
        VetoTimePickerDialog(
            initialHour = startTime.split(":")[0].toInt(),
            initialMinute = startTime.split(":")[1].toInt(),
            onDismiss = { showStartTimePicker.value = false },
            onTimeSelected = { h, m ->
                startTime = String.format(Locale.getDefault(), "%02d:%02d", h, m)
                showStartTimePicker.value = false
            }
        )
    }

    if (showEndTimePicker.value) {
        VetoTimePickerDialog(
            initialHour = endTime.split(":")[0].toInt(),
            initialMinute = endTime.split(":")[1].toInt(),
            onDismiss = { showEndTimePicker.value = false },
            onTimeSelected = { h, m ->
                endTime = String.format(Locale.getDefault(), "%02d:%02d", h, m)
                showEndTimePicker.value = false
            }
        )
    }
}

@Composable
fun TimeCard(label: String, time: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, style = MaterialTheme.typography.labelMedium)
            Text(
                text = time,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun DayChip(day: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.take(1),
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetoTimePickerDialog(
    initialHour: Int = 0,
    initialMinute: Int = 0,
    onDismiss: () -> Unit,
    onTimeSelected: (Int, Int) -> Unit
) {
    val state = rememberTimePickerState(initialHour = initialHour, initialMinute = initialMinute)
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onTimeSelected(state.hour, state.minute) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            TimePicker(state = state)
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ScheduleBottomSheetPreview() {
    VetoTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            ScheduleBottomSheet(
                contact = Contact("John Doe", "123456789"),
                onDismiss = {},
                onSave = { _, _, _ -> }
            )
        }
    }
}
