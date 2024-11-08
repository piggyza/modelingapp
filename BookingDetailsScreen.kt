package com.example.modelbookingapp.ui.screens.booking

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
fun BookingDetailsScreen(
    bookingId: String,
    onNavigateBack: () -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val bookingState by viewModel.bookingState.collectAsState()

    LaunchedEffect(bookingId) {
        viewModel.loadBooking(bookingId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = bookingState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Success -> {
                BookingContent(
                    booking = state.data,
                    modifier = Modifier.padding(padding)
                )
            }
            is Resource.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingContent(
    booking: Booking,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Booking Date",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formatDate(booking.dateTime),
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Status",
                    style = MaterialTheme.typography.titleMedium
                )
                StatusChip(booking.status)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Duration",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${booking.duration} hours",
                    style = MaterialTheme.typography.bodyLarge
                )

                if (booking.notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Notes",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = booking.notes,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Total Amount",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$${booking.totalAmount}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun StatusChip(status: BookingStatus) {
    Surface(
        color = when (status) {
            BookingStatus.PENDING -> MaterialTheme.colorScheme.primary
            BookingStatus.CONFIRMED -> MaterialTheme.colorScheme.secondary
            BookingStatus.CANCELLED -> MaterialTheme.colorScheme.error
            BookingStatus.COMPLETED -> MaterialTheme.colorScheme.tertiary
        },
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