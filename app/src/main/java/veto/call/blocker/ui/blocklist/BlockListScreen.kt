package veto.call.blocker.ui.blocklist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import veto.call.blocker.data.entity.BlockedContact
import veto.call.blocker.ui.VetoViewModel
import veto.call.blocker.ui.theme.VetoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockListScreen(viewModel: VetoViewModel = hiltViewModel()) {
    val blockedContacts by viewModel.allBlockedContacts.collectAsState()
    val deviceContacts by viewModel.deviceContacts.collectAsState()
    
    var showContactPicker by remember { mutableStateOf(false) }
    var selectedContactForSchedule by remember { mutableStateOf<Contact?>(null) }
    var contactToEdit by remember { mutableStateOf<BlockedContact?>(null) }
    var contactToDelete by remember { mutableStateOf<BlockedContact?>(null) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadDeviceContacts()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showContactPicker = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Contact")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (blockedContacts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No blocked contacts yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(blockedContacts) { contact ->
                        BlockedContactItem(
                            contact = contact,
                            onDeleteClick = { contactToDelete = contact },
                            onEditClick = { contactToEdit = contact }
                        )
                    }
                }
            }
        }
    }

    // Confirmation Dialog for Deletion
    if (contactToDelete != null) {
        AlertDialog(
            onDismissRequest = { contactToDelete = null },
            title = { Text("Unblock Contact") },
            text = { Text("Are you sure you want to remove ${contactToDelete?.name ?: contactToDelete?.phoneNumber} from the block list?") },
            confirmButton = {
                Button(
                    onClick = {
                        contactToDelete?.let { viewModel.removeBlockedContact(it) }
                        contactToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Yes, Unblock")
                }
            },
            dismissButton = {
                TextButton(onClick = { contactToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showContactPicker) {
        ContactPicker(
            contacts = deviceContacts,
            onContactSelected = { contact ->
                if (blockedContacts.any { it.phoneNumber == contact.phoneNumber }) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Number is already in the block list")
                    }
                } else {
                    selectedContactForSchedule = contact
                }
                showContactPicker = false
            },
            onDismiss = { showContactPicker = false }
        )
    }

    // Add New Schedule
    selectedContactForSchedule?.let { contact ->
        ScheduleBottomSheet(
            contact = contact,
            onDismiss = { selectedContactForSchedule = null },
            onSave = { startTime, endTime, activeDays ->
                viewModel.addBlockedContact(contact, startTime, endTime, activeDays)
                selectedContactForSchedule = null
            }
        )
    }

    // Edit Existing Schedule
    contactToEdit?.let { contact ->
        ScheduleBottomSheet(
            contact = Contact(contact.name ?: "", contact.phoneNumber),
            initialStartTime = contact.startTime,
            initialEndTime = contact.endTime,
            initialActiveDays = contact.activeDays,
            onDismiss = { contactToEdit = null },
            onSave = { startTime, endTime, activeDays ->
                viewModel.updateBlockedContact(
                    contact.copy(
                        startTime = startTime,
                        endTime = endTime,
                        activeDays = activeDays
                    )
                )
                contactToEdit = null
            }
        )
    }
}

@Composable
fun BlockedContactItem(
    contact: BlockedContact, 
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditClick() },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name ?: "Unknown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = contact.phoneNumber,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${contact.startTime} - ${contact.endTime}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (!contact.activeDays.isNullOrBlank()) {
                        val daysString = formatActiveDays(contact.activeDays)
                        Text(
                            text = " • $daysString",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private fun formatActiveDays(activeDays: String): String {
    val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    return activeDays.split(",")
        .mapNotNull { it.trim().toIntOrNull() }
        .map { dayNames[it - 1] }
        .joinToString(", ")
}

@Preview(showBackground = true)
@Composable
fun BlockListPreview() {
    VetoTheme {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {}) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    BlockedContactItem(
                        contact = BlockedContact(
                            name = "Spam Caller",
                            phoneNumber = "+1234567890",
                            startTime = "09:00",
                            endTime = "17:00",
                            activeDays = "2,3,4,5,6"
                        ),
                        onDeleteClick = {},
                        onEditClick = {}
                    )
                }
            }
        }
    }
}
