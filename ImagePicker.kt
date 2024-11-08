package com.example.modelbookingapp.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ImagePicker(
    images: List<String>,
    onImagesSelected: (List<Uri>) -> Unit,
    onImageDelete: (String) -> Unit,
    maxImages: Int = 10
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            onImagesSelected(uris)
        }
    }

    Column {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(images) { imageUrl ->
                Card(
                    modifier = Modifier.size(120.dp)
                ) {
                    Box {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Selected image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { onImageDelete(imageUrl) },
                            modifier = Modifier.align(androidx.compose.ui.Alignment.TopEnd)
                        ) {
                            Icon(Icons.Default.Delete, "Delete image")
                        }
                    }
                }
            }

            if (images.size < maxImages) {
                item {
                    Card(
                        modifier = Modifier.size(120.dp),
                        onClick = { imagePickerLauncher.launch("image/*") }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, "Add image")
                        }
                    }
                }
            }
        }
    }
}