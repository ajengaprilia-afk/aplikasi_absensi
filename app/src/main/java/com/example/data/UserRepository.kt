package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class UserRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("Pengguna harus masuk dengan Google sebelum mengakses Firestore.")
    }

    fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
        if (userId.isBlank()) {
            emit(null)
            return@flow
        }
        val docRef = db.collection("users").document(userId)
        emitAll(
            docRef.snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(
                            UserProfile::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    } else {
                        null
                    }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(exceptionToLog, OperationType.GET, docRef.path)
                    }
                    throw error
                }
        )
    }

    fun observeActivities(userId: String): Flow<List<AuthActivity>> = flow {
        if (userId.isBlank()) {
            emit(emptyList())
            return@flow
        }
        val path = "users/$userId/activities"
        emitAll(
            db.collection("users")
                .document(userId)
                .collection("activities")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(25)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        AuthActivity::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun createUserProfile(
        userId: String,
        email: String,
        photoUrl: String?,
        draft: RegistrationDraft
    ): Result<Unit> {
        val docPath = "users/$userId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Sesi pengguna berubah; operasi dibatalkan." }
            val cleanFullName = draft.fullName.trim().take(80)
            val cleanUsername = draft.username.trim().take(30)
            val cleanPhone = draft.phoneNumber.trim().take(25)
            val cleanEmail = email.trim().take(120)
            require(cleanFullName.length >= 2) { "Nama lengkap minimal 2 karakter." }
            require(cleanUsername.length >= 3) { "Username minimal 3 karakter." }
            require(cleanPhone.length >= 6) { "Nomor telepon minimal 6 karakter." }
            require(cleanEmail.length >= 3) { "Email tidak valid." }

            val payload = mutableMapOf<String, Any>(
                "userId" to uid,
                "email" to cleanEmail,
                "fullName" to cleanFullName,
                "username" to cleanUsername,
                "phoneNumber" to cleanPhone,
                "gender" to draft.gender.label,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            draft.birthDate.trim().take(40).takeIf { it.isNotEmpty() }?.let {
                payload["birthDate"] = it
            }
            draft.address.trim().take(200).takeIf { it.isNotEmpty() }?.let {
                payload["address"] = it
            }
            draft.bio.trim().take(200).takeIf { it.isNotEmpty() }?.let {
                payload["bio"] = it
            }
            photoUrl?.trim()?.take(500)?.takeIf { it.isNotEmpty() }?.let {
                payload["photoUrl"] = it
            }

            db.collection("users").document(uid).set(payload).await()
            logActivityInternal(
                userId = uid,
                action = "Registrasi Akun",
                detail = "Data diri @$cleanUsername ($cleanEmail) berhasil didaftarkan.",
                category = ActivityCategory.AUTH
            )
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(
        userId: String,
        draft: RegistrationDraft
    ): Result<Unit> {
        val docPath = "users/$userId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Sesi pengguna berubah; operasi dibatalkan." }
            val cleanFullName = draft.fullName.trim().take(80)
            val cleanUsername = draft.username.trim().take(30)
            val cleanPhone = draft.phoneNumber.trim().take(25)
            require(cleanFullName.length >= 2) { "Nama lengkap minimal 2 karakter." }
            require(cleanUsername.length >= 3) { "Username minimal 3 karakter." }
            require(cleanPhone.length >= 6) { "Nomor telepon minimal 6 karakter." }

            val updates = mapOf<String, Any>(
                "fullName" to cleanFullName,
                "username" to cleanUsername,
                "phoneNumber" to cleanPhone,
                "gender" to draft.gender.label,
                "birthDate" to draft.birthDate.trim().take(40),
                "address" to draft.address.trim().take(200),
                "bio" to draft.bio.trim().take(200),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            db.collection("users").document(uid).update(updates).await()
            logActivityInternal(
                userId = uid,
                action = "Pembaruan Profil",
                detail = "Data diri @$cleanUsername telah diperbarui.",
                category = ActivityCategory.PROFILE
            )
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun recordLoginActivity(userId: String, email: String): Result<Unit> {
        val path = "users/$userId/activities"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Sesi pengguna berubah; operasi dibatalkan." }
            logActivityInternal(
                userId = uid,
                action = "Login Berhasil",
                detail = "Sesi terautentikasi melalui email $email.",
                category = ActivityCategory.SECURITY
            )
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    private suspend fun logActivityInternal(
        userId: String,
        action: String,
        detail: String,
        category: ActivityCategory
    ) {
        val activityDoc = db.collection("users")
            .document(userId)
            .collection("activities")
            .document()
        val payload = mapOf(
            "action" to action.trim().take(60),
            "detail" to detail.trim().take(200),
            "category" to category.value,
            "createdAt" to FieldValue.serverTimestamp()
        )
        activityDoc.set(payload).await()
    }
}
