package com.example.ui.auth

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material.icons.rounded.WorkOutline
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.R
import com.example.auth.onGoogleSignInClicked
import com.example.data.GenderOption
import com.example.data.RegistrationDraft
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassChip
import com.example.ui.components.GlassPrimaryButton
import com.example.ui.components.GlassSegmentedSwitch
import com.example.ui.components.GlassTextField
import com.example.ui.components.GsysBackground
import com.example.ui.components.GsysBrandHeader
import com.example.ui.theme.GsysCyan
import com.example.ui.theme.GsysError
import com.example.ui.theme.GsysGlassBorderSubtle
import com.example.ui.theme.GsysTeal
import com.example.ui.theme.GsysTextMuted
import com.example.ui.theme.GsysTextPrimary
import com.example.ui.theme.GsysTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onPrepareRegistrationDraft: (RegistrationDraft?) -> Unit,
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Registration personal data fields
    var fullName by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var selectedGenderName by rememberSaveable { mutableStateOf(GenderOption.LAKI_LAKI.label) }
    var birthDate by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var bio by rememberSaveable { mutableStateOf("") }

    var fullNameError by remember { mutableStateOf<String?>(null) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    BackHandler(enabled = selectedTab != 0) {
        selectedTab = 0
        errorMessage = null
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("id-ID"))
                            birthDate = formatter.format(Date(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(R.string.action_select))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    GsysBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .imePadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                GsysBrandHeader(subtitle = stringResource(R.string.auth_brand_subtitle))

                GlassSegmentedSwitch(
                    options = listOf(
                        stringResource(R.string.tab_login),
                        stringResource(R.string.tab_register)
                    ),
                    selectedIndex = selectedTab,
                    onSelect = { index ->
                        selectedTab = index
                        errorMessage = null
                    },
                    tagPrefix = "auth_tab"
                )

                if (!errorMessage.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = GsysError.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, GsysError.copy(alpha = 0.45f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = null,
                                tint = GsysError,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = errorMessage.orEmpty(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = GsysTextPrimary
                            )
                        }
                    }
                }

                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "authTabContent"
                ) { tab ->
                    if (tab == 0) {
                        LoginGlassPane(
                            isLoading = isLoading,
                            onLoginClick = {
                                errorMessage = null
                                onPrepareRegistrationDraft(null)
                                isLoading = true
                                onGoogleSignInClicked(
                                    context = context,
                                    credentialManager = credentialManager,
                                    onAuthSuccess = {
                                        isLoading = false
                                        onAuthSuccess()
                                    },
                                    onAuthError = { msg ->
                                        isLoading = false
                                        errorMessage = msg
                                    },
                                    scope = scope,
                                    onAuthCancelled = {
                                        isLoading = false
                                    }
                                )
                            },
                            onSwitchToRegister = {
                                selectedTab = 1
                                errorMessage = null
                            }
                        )
                    } else {
                        RegisterGlassPane(
                            fullName = fullName,
                            onFullNameChange = {
                                fullName = it
                                fullNameError = null
                            },
                            fullNameError = fullNameError,
                            username = username,
                            onUsernameChange = {
                                username = it.replace(" ", "")
                                usernameError = null
                            },
                            usernameError = usernameError,
                            phoneNumber = phoneNumber,
                            onPhoneNumberChange = {
                                phoneNumber = it
                                phoneError = null
                            },
                            phoneError = phoneError,
                            selectedGender = GenderOption.fromRaw(selectedGenderName),
                            onGenderSelect = { selectedGenderName = it.label },
                            birthDate = birthDate,
                            onOpenDatePicker = { showDatePicker = true },
                            address = address,
                            onAddressChange = { address = it },
                            bio = bio,
                            onBioChange = { bio = it },
                            isLoading = isLoading,
                            onRegisterClick = {
                                val trimmedName = fullName.trim()
                                val trimmedUser = username.trim()
                                val trimmedPhone = phoneNumber.trim()
                                var hasError = false

                                if (trimmedName.length < 2) {
                                    fullNameError = "Nama lengkap minimal 2 karakter"
                                    hasError = true
                                }
                                if (trimmedUser.length < 3) {
                                    usernameError = "Username minimal 3 karakter"
                                    hasError = true
                                }
                                if (trimmedPhone.length < 6) {
                                    phoneError = "Nomor telepon minimal 6 digit"
                                    hasError = true
                                }
                                if (hasError) return@RegisterGlassPane

                                val draft = RegistrationDraft(
                                    fullName = trimmedName,
                                    username = trimmedUser,
                                    phoneNumber = trimmedPhone,
                                    gender = GenderOption.fromRaw(selectedGenderName),
                                    birthDate = birthDate.trim(),
                                    address = address.trim(),
                                    bio = bio.trim()
                                )
                                errorMessage = null
                                onPrepareRegistrationDraft(draft)
                                isLoading = true
                                onGoogleSignInClicked(
                                    context = context,
                                    credentialManager = credentialManager,
                                    onAuthSuccess = {
                                        isLoading = false
                                        onAuthSuccess()
                                    },
                                    onAuthError = { msg ->
                                        isLoading = false
                                        errorMessage = msg
                                    },
                                    scope = scope,
                                    onAuthCancelled = {
                                        isLoading = false
                                    }
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun LoginGlassPane(
    isLoading: Boolean,
    onLoginClick: () -> Unit,
    onSwitchToRegister: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        GlassCard(contentPadding = PaddingValues(22.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.VerifiedUser,
                    contentDescription = null,
                    tint = GsysCyan,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = stringResource(R.string.login_card_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = GsysTextPrimary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.login_card_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = GsysTextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Visual highlights inside frosted inset rows
            FeatureHighlightRow(
                icon = Icons.Rounded.Security,
                title = "Autentikasi Email Google",
                subtitle = "Terverifikasi langsung melalui Firebase Authentication & Credential Manager"
            )
            Spacer(modifier = Modifier.height(10.dp))
            FeatureHighlightRow(
                icon = Icons.Rounded.Badge,
                title = "Kartu Identitas & Data Diri",
                subtitle = "Profil pribadi tersimpan aman dan terenkripsi di Cloud Firestore"
            )

            Spacer(modifier = Modifier.height(22.dp))

            GlassPrimaryButton(
                text = stringResource(R.string.action_login_google),
                icon = Icons.AutoMirrored.Rounded.Login,
                isLoading = isLoading,
                onClick = onLoginClick,
                modifier = Modifier.testTag("login_google_button")
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onSwitchToRegister)
                    .testTag("switch_to_register_button")
                    .padding(vertical = 10.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pengguna baru? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GsysTextSecondary
                )
                Text(
                    text = "Daftar & Isi Data Diri",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = GsysCyan
                )
            }
        }

        GlassCard(
            highlightBorder = false,
            contentPadding = PaddingValues(16.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = null,
                    tint = GsysTeal,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.auth_provider_note),
                    style = MaterialTheme.typography.bodyMedium,
                    color = GsysTextMuted
                )
            }
        }
    }
}

@Composable
private fun RegisterGlassPane(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    fullNameError: String?,
    username: String,
    onUsernameChange: (String) -> Unit,
    usernameError: String?,
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    phoneError: String?,
    selectedGender: GenderOption,
    onGenderSelect: (GenderOption) -> Unit,
    birthDate: String,
    onOpenDatePicker: () -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    bio: String,
    onBioChange: (String) -> Unit,
    isLoading: Boolean,
    onRegisterClick: () -> Unit
) {
    GlassCard(contentPadding = PaddingValues(22.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.PersonAdd,
                contentDescription = null,
                tint = GsysTeal,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.register_card_title),
                style = MaterialTheme.typography.headlineSmall,
                color = GsysTextPrimary
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.register_card_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = GsysTextSecondary
        )

        Spacer(modifier = Modifier.height(18.dp))

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            GlassTextField(
                value = fullName,
                onValueChange = onFullNameChange,
                label = "Nama Lengkap *",
                placeholder = "Contoh: Ajeng Aprilia",
                leadingIcon = Icons.Rounded.Person,
                errorText = fullNameError,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.testTag("reg_fullname_input")
            )

            GlassTextField(
                value = username,
                onValueChange = onUsernameChange,
                label = "Username *",
                placeholder = "Contoh: ajeng_gsys",
                leadingIcon = Icons.Rounded.AlternateEmail,
                errorText = usernameError,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.testTag("reg_username_input")
            )

            GlassTextField(
                value = phoneNumber,
                onValueChange = onPhoneNumberChange,
                label = "Nomor Telepon / WhatsApp *",
                placeholder = "Contoh: 081234567890",
                leadingIcon = Icons.Rounded.Phone,
                errorText = phoneError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.testTag("reg_phone_input")
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Jenis Kelamin",
                    style = MaterialTheme.typography.labelLarge,
                    color = GsysTextSecondary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GenderOption.entries.forEach { option ->
                        GlassChip(
                            label = option.label,
                            selected = selectedGender == option,
                            onClick = { onGenderSelect(option) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("reg_gender_${option.name.lowercase()}")
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenDatePicker)
                    .testTag("reg_birthdate_input")
            ) {
                GlassTextField(
                    value = birthDate,
                    onValueChange = {},
                    readOnly = true,
                    label = "Tanggal Lahir (Opsional)",
                    placeholder = "Pilih tanggal lahir",
                    leadingIcon = Icons.Rounded.CalendarMonth,
                    trailingContent = {
                        TextButton(onClick = onOpenDatePicker) {
                            Text("Pilih", color = GsysCyan)
                        }
                    }
                )
            }

            GlassTextField(
                value = address,
                onValueChange = onAddressChange,
                label = "Alamat Domisili (Opsional)",
                placeholder = "Kota / Alamat lengkap",
                leadingIcon = Icons.Rounded.Home,
                singleLine = false,
                maxLines = 2,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.testTag("reg_address_input")
            )

            GlassTextField(
                value = bio,
                onValueChange = onBioChange,
                label = "Pekerjaan / Bio Singkat (Opsional)",
                placeholder = "Contoh: UI/UX Designer & Tech Enthusiast",
                leadingIcon = Icons.Rounded.WorkOutline,
                singleLine = false,
                maxLines = 2,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.testTag("reg_bio_input")
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        GlassPrimaryButton(
            text = stringResource(R.string.action_register_google),
            icon = Icons.Rounded.CheckCircle,
            isLoading = isLoading,
            onClick = onRegisterClick,
            modifier = Modifier.testTag("register_google_button")
        )
    }
}

@Composable
private fun FeatureHighlightRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(GsysCyan.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GsysCyan,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = GsysTextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = GsysTextMuted
            )
        }
    }
}
