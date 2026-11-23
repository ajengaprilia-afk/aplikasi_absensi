package com.example.ui.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AuthActivity
import com.example.data.RegistrationDraft
import com.example.data.UiState
import com.example.data.UserProfile
import com.example.data.UserRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

data class FormFeedback(
    val isSaving: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false
)

class MainViewModel(
    private val repository: UserRepository,
    private val currentUserId: String,
    initialDraft: RegistrationDraft?,
    private val userEmail: String,
    private val userPhotoUrl: String?,
    onConsumeDraft: () -> Unit
) : ViewModel() {

    val profileState: StateFlow<UiState<UserProfile?>> =
        repository.observeUserProfile(currentUserId)
            .map<UserProfile?, UiState<UserProfile?>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing user profile", rootError)
                emit(UiState.Error(rootError.localizedMessage ?: "Gagal memuat profil pengguna."))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

    val activitiesState: StateFlow<UiState<List<AuthActivity>>> =
        repository.observeActivities(currentUserId)
            .map<List<AuthActivity>, UiState<List<AuthActivity>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing activities", rootError)
                emit(UiState.Error(rootError.localizedMessage ?: "Gagal memuat riwayat aktivitas."))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

    private val _feedback = MutableStateFlow(FormFeedback())
    val feedback: StateFlow<FormFeedback> = _feedback.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val existing = repository.observeUserProfile(currentUserId).first()
                if (existing == null && initialDraft != null) {
                    _feedback.value = FormFeedback(isSaving = true)
                    val result = repository.createUserProfile(
                        userId = currentUserId,
                        email = userEmail,
                        photoUrl = userPhotoUrl,
                        draft = initialDraft
                    )
                    onConsumeDraft()
                    result.fold(
                        onSuccess = {
                            _feedback.value = FormFeedback(
                                isSaving = false,
                                message = "Pendaftaran akun & data diri berhasil disimpan!",
                                isError = false
                            )
                        },
                        onFailure = { err ->
                            _feedback.value = FormFeedback(
                                isSaving = false,
                                message = err.localizedMessage ?: "Gagal menyimpan pendaftaran.",
                                isError = true
                            )
                        }
                    )
                } else if (existing != null) {
                    onConsumeDraft()
                    repository.recordLoginActivity(currentUserId, userEmail)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "Initial profile bootstrap error", e)
            }
        }
    }

    fun registerProfile(draft: RegistrationDraft) {
        viewModelScope.launch {
            _feedback.value = FormFeedback(isSaving = true)
            val result = repository.createUserProfile(
                userId = currentUserId,
                email = userEmail,
                photoUrl = userPhotoUrl,
                draft = draft
            )
            result.fold(
                onSuccess = {
                    _feedback.value = FormFeedback(
                        isSaving = false,
                        message = "Data diri berhasil didaftarkan!",
                        isError = false
                    )
                },
                onFailure = { err ->
                    _feedback.value = FormFeedback(
                        isSaving = false,
                        message = err.localizedMessage ?: "Gagal mendaftarkan data diri.",
                        isError = true
                    )
                }
            )
        }
    }

    fun updateProfile(draft: RegistrationDraft) {
        viewModelScope.launch {
            _feedback.value = FormFeedback(isSaving = true)
            val result = repository.updateUserProfile(
                userId = currentUserId,
                draft = draft
            )
            result.fold(
                onSuccess = {
                    _feedback.value = FormFeedback(
                        isSaving = false,
                        message = "Perubahan data diri tersimpan di Cloud Firestore.",
                        isError = false
                    )
                },
                onFailure = { err ->
                    _feedback.value = FormFeedback(
                        isSaving = false,
                        message = err.localizedMessage ?: "Gagal memperbarui data diri.",
                        isError = true
                    )
                }
            )
        }
    }

    fun clearFeedback() {
        _feedback.value = FormFeedback()
    }

    private companion object {
        const val TAG = "MainViewModel"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
