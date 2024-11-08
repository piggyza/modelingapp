package com.example.modelbookingapp.ui.screens.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.modelbookingapp.data.model.ModelProfile
import com.example.modelbookingapp.data.model.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ClientHomeViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _modelsState = MutableStateFlow<Resource<List<ModelProfile>>>(Resource.Loading)
    val modelsState: StateFlow<Resource<List<ModelProfile>>> = _modelsState

    init {
        loadModels()
    }

    private fun loadModels() {
        viewModelScope.launch {
            try {
                _modelsState.value = Resource.Loading

                val models = firestore.collection("models")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        doc.toObject(ModelProfile::class.java)?.copy(id = doc.id)
                    }

                _modelsState.value = Resource.Success(models)
            } catch (e: Exception) {
                _modelsState.value = Resource.Error(e.message ?: "Failed to load models")
            }
        }
    }

    fun refreshModels() {
        loadModels()
    }
}