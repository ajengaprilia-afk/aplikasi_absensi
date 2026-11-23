package com.example.data

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class GenderOption(val label: String) {
    LAKI_LAKI("Laki-laki"),
    PEREMPUAN("Perempuan"),
    LAINNYA("Lainnya");

    companion object {
        fun fromRaw(raw: String?): GenderOption =
            entries.firstOrNull { it.label == raw } ?: LAINNYA
    }
}

enum class ActivityCategory(val value: String) {
    AUTH("auth"),
    PROFILE("profile"),
    SECURITY("security");

    companion object {
        fun fromRaw(raw: String?): ActivityCategory =
            entries.firstOrNull { it.value == raw } ?: AUTH
    }
}

@Keep
@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val email: String = "",
    val fullName: String = "",
    val username: String = "",
    val phoneNumber: String = "",
    val gender: String = GenderOption.LAKI_LAKI.label,
    val birthDate: String = "",
    val address: String = "",
    val bio: String = "",
    val photoUrl: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun completenessPercent(): Int {
        val checks = listOf(
            fullName.isNotBlank(),
            username.isNotBlank(),
            email.isNotBlank(),
            phoneNumber.isNotBlank(),
            gender.isNotBlank(),
            birthDate.isNotBlank(),
            address.isNotBlank(),
            bio.isNotBlank()
        )
        return (checks.count { it } * 100) / checks.size
    }
}

@Keep
@IgnoreExtraProperties
data class AuthActivity(
    @DocumentId val id: String = "",
    val action: String = "",
    val detail: String = "",
    val category: String = ActivityCategory.AUTH.value,
    @ServerTimestamp val createdAt: Timestamp? = null
)

data class RegistrationDraft(
    val fullName: String = "",
    val username: String = "",
    val phoneNumber: String = "",
    val gender: GenderOption = GenderOption.LAKI_LAKI,
    val birthDate: String = "",
    val address: String = "",
    val bio: String = ""
)

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
