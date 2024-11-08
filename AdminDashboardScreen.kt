package com.example.modelbookingapp.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.modelbookingapp.data.model.Booking
import com.example.modelbookingapp.data.model.BookingStatus
import com.example.modelbookingapp.data.model.Resource
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onSignOut: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val allModelsState by viewModel.allModelsState.collectAsState()
    val allBookingsState by viewModel.allBookingsState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                actions = {
                    IconButton(onClick = { viewModel.refreshData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = {
                        authViewModel.signOut()
                        onSignOut()
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                Text(
                    text = "Recent Bookings",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
            }

            when (allBookingsState) {
                is Resource.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is Resource.Success -> {
                    val bookings = (allBookingsState as Resource.Success<List<Booking>>).data
                    if (bookings.isEmpty()) {
                        item {
                            Text(
                                text = "No bookings found",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        items(bookings) { booking ->
                            BookingCard(
                                booking = booking,
                                onStatusChange = { newStatus ->
                                    viewModel.updateBookingStatus(booking.id, newStatus)
                                }
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    item {
                        Text(
                            text = (allBookingsState as Resource.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookingCard(
    booking: Booking,
    onStatusChange: (BookingStatus) -> Unit
) {
    var showStatusDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Booking ID: ${booking.id}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Date: ${formatDate(booking.dateTime)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Duration: ${booking.duration} hours",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Amount: $${booking.totalAmount}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusChip(status = booking.status)
                TextButton(onClick = { showStatusDialog = true }) {
                    Text("Change Status")
                }
            }
        }
    }

    if (showStatusDialog) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Update Booking Status") },
            text = {
                Column {
                    BookingStatus.values().forEach { status ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = booking.status == status,
                                onClick = {
                                    onStatusChange(status)
                                    showStatusDialog = false
                                }
                            )
                            Text(
                                text = status.name,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStatusDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun StatusChip(status: BookingStatus) {
    val color = when (status) {
        BookingStatus.PENDING -> MaterialTheme.colorScheme.primary
        BookingStatus.CONFIRMED -> MaterialTheme.colorScheme.secondary
        BookingStatus.CANCELLED -> MaterialTheme.colorScheme.error
        BookingStatus.COMPLETED -> MaterialTheme.colorScheme.tertiary
    }

    Surface(
        color = color,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return formatter.format(date)
}