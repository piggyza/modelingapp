package com.example.modelbookingapp.ui.screens.booking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.modelbookingapp.data.model.Resource
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    modelId: String,
    onNavigateBack: () -> Unit,
    onBookingComplete: () -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val modelState by viewModel.modelState.collectAsState()
    val bookingState by viewModel.bookingState.collectAsState()

    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var duration by remember { mutableStateOf("1") }
    var notes by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(modelId) {
        viewModel.loadModel(modelId)
    }

    LaunchedEffect(bookingState) {
        if (bookingState is Resource.Success && (bookingState as Resource.Success<String>).data.isNotEmpty()) {
            onBookingComplete()
            viewModel.resetBookingState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book a Session") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (modelState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Success -> {
                val model = (modelState as Resource.Success).data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Model Info Card
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            if (model.photos.isNotEmpty()) {
                                AsyncImage(
                                    model = model.photos.first(),
                                    contentDescription = "Model photo",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = model.name,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Text(
                                    text = "Rate: $${model.hourlyRate}/hour",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Booking Form
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select date")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(selectedDate?.let { formatDate(it) } ?: "Select Date and Time")
                    }

                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            onDateSelected = {
                                selectedDate = it
                                showDatePicker = false
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = duration,
                        onValueChange = { if (it.toIntOrNull() != null) duration = it },
                        label = { Text("Duration (hours)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            selectedDate?.let {
                                viewModel.createBooking(
                                    modelId = modelId,
                                    dateTime = it,
                                    duration = duration.toIntOrNull() ?: 1,
                                    notes = notes
                                )
                            }
                        },
                        enabled = selectedDate != null && duration.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Book Now")
                    }
                }
            }
            is Resource.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (modelState as Resource.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Show booking status
        when (bookingState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Error -> {
                Snackbar {
                    Text((bookingState as Resource.Error).message)
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (Date) -> Unit
) {
    // Implement date picker dialog using Material3 DatePicker
    // This is a placeholder - implement according to your needs
}

private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return formatter.format(date)
}