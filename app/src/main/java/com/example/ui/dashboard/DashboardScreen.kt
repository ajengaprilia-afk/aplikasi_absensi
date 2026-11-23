package com.example.ui.dashboard

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.WorkOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.R
import com.example.data.ActivityCategory
import com.example.data.AuthActivity
import com.example.data.GenderOption
import com.example.data.RegistrationDraft
import com.example.data.UiState
import com.example.data.UserProfile
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassChip
import com.example.ui.components.GlassPrimaryButton
import com.example.ui.components.GlassTextField
import com.example.ui.components.GsysBackground
import com.example.ui.components.GsysBrandHeader
import com.example.ui.theme.GsysCyan
import com.example.ui.theme.GsysDeepNavy
import com.example.ui.theme.GsysError
import com.example.ui.theme.GsysGlassBorderBright
import com.example.ui.theme.GsysGlassBorderSubtle
import com.example.ui.theme.GsysIndigo
import com.example.ui.theme.GsysMidnightSurface
import com.example.ui.theme.GsysSuccess
import com.example.ui.theme.GsysTeal
import com.example.ui.theme.GsysTextMuted
import com.example.ui.theme.GsysTextPrimary
import com.example.ui.theme.GsysTextSecondary
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class DashboardTab(val label: String, val icon: ImageVector) {
    HOME("Beranda", Icons.Rounded.Badge),
    PROFILE("Data Diri", Icons.Rounded.Person),
    ACTIVITY("Aktivitas", Icons.Rounded.History)
}

@Composable
fun MainFeatureScreen(
    viewModel: MainViewModel,
    userEmail: String,
    userDisplayName: String,
    userPhotoUrl: String?,
    onSignOutClick: () -> Unit
) {
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val activitiesState by viewModel.activitiesState.collectAsStateWithLifecycle()
    val feedback by viewModel.feedback.collectAsStateWithLifecycle()

    GsysBackground {
        when (val state = profileState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing),
                    contentAlignment = Alignment.Center
                ) {
                    GlassCard(contentPadding = PaddingValues(28.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = GsysCyan)
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Menghubungkan Profil GSYS...",
                                style = MaterialTheme.typography.titleMedium,
                                color = GsysTextPrimary
                            )
                        }
                    }
                }
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GlassCard(
                        modifier = Modifier.widthIn(max = 460.dp),
                        contentPadding = PaddingValues(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = null,
                            tint = GsysError,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Gagal Memuat Sesi",
                            style = MaterialTheme.typography.headlineSmall,
                            color = GsysTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = GsysTextSecondary
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        GlassPrimaryButton(
                            text = "Keluar & Muat Ulang",
                            icon = Icons.AutoMirrored.Rounded.Logout,
                            onClick = onSignOutClick
                        )
                    }
                }
            }

            is UiState.Success -> {
                val profile = state.data
                if (profile == null) {
                    CompleteProfileOnboardingScreen(
                        userEmail = userEmail,
                        defaultName = userDisplayName,
                        feedback = feedback,
                        onSubmit = viewModel::registerProfile,
                        onSignOutClick = onSignOutClick
                    )
                } else {
                    AuthenticatedDashboard(
                        profile = profile,
                        activitiesState = activitiesState,
                        feedback = feedback,
                        userPhotoUrl = userPhotoUrl,
                        onUpdateProfile = viewModel::updateProfile,
                        onClearFeedback = viewModel::clearFeedback,
                        onSignOutClick = onSignOutClick
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompleteProfileOnboardingScreen(
    userEmail: String,
    defaultName: String,
    feedback: FormFeedback,
    onSubmit: (RegistrationDraft) -> Unit,
    onSignOutClick: () -> Unit
) {
    var fullName by rememberSaveable { mutableStateOf(defaultName) }
    var username by rememberSaveable {
        mutableStateOf(
            userEmail.substringBefore("@")
                .replace(Regex("[^a-zA-Z0-9_]"), "_")
                .take(24)
        )
    }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var genderName by rememberSaveable { mutableStateOf(GenderOption.LAKI_LAKI.label) }
    var birthDate by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var bio by rememberSaveable { mutableStateOf("") }

    var fullNameError by remember { mutableStateOf<String?>(null) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

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
                ) { Text(stringResource(R.string.action_select)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        ) { DatePicker(state = datePickerState) }
    }

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GsysBrandHeader(
                    subtitle = "Lengkapi Registrasi Data Diri",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onSignOutClick,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("onboarding_sign_out_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Logout,
                        contentDescription = "Keluar",
                        tint = GsysTextSecondary
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = GsysTeal.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, GsysTeal.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Email,
                        contentDescription = null,
                        tint = GsysTeal
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Email Terverifikasi",
                            style = MaterialTheme.typography.labelSmall,
                            color = GsysTeal
                        )
                        Text(
                            text = userEmail.ifBlank { "Akun Google" },
                            style = MaterialTheme.typography.labelLarge,
                            color = GsysTextPrimary
                        )
                    }
                }
            }

            if (feedback.message != null) {
                FeedbackBanner(feedback = feedback)
            }

            GlassCard(contentPadding = PaddingValues(22.dp)) {
                Text(
                    text = "Data Diri Baru",
                    style = MaterialTheme.typography.headlineSmall,
                    color = GsysTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Lengkapi data diri Anda satu kali untuk mengaktifkan Kartu Identitas GSYS.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GsysTextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    GlassTextField(
                        value = fullName,
                        onValueChange = { fullName = it; fullNameError = null },
                        label = "Nama Lengkap *",
                        leadingIcon = Icons.Rounded.Person,
                        errorText = fullNameError,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.testTag("onboard_fullname_input")
                    )
                    GlassTextField(
                        value = username,
                        onValueChange = { username = it.replace(" ", ""); usernameError = null },
                        label = "Username *",
                        leadingIcon = Icons.Rounded.AlternateEmail,
                        errorText = usernameError,
                        modifier = Modifier.testTag("onboard_username_input")
                    )
                    GlassTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it; phoneError = null },
                        label = "Nomor Telepon *",
                        leadingIcon = Icons.Rounded.Phone,
                        errorText = phoneError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.testTag("onboard_phone_input")
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GenderOption.entries.forEach { option ->
                            GlassChip(
                                label = option.label,
                                selected = genderName == option.label,
                                onClick = { genderName = option.label },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                    ) {
                        GlassTextField(
                            value = birthDate,
                            onValueChange = {},
                            readOnly = true,
                            label = "Tanggal Lahir (Opsional)",
                            leadingIcon = Icons.Rounded.CalendarMonth,
                            trailingContent = {
                                TextButton(onClick = { showDatePicker = true }) {
                                    Text("Pilih", color = GsysCyan)
                                }
                            }
                        )
                    }
                    GlassTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = "Alamat Domisili (Opsional)",
                        leadingIcon = Icons.Rounded.Home,
                        singleLine = false,
                        maxLines = 2
                    )
                    GlassTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = "Pekerjaan / Bio Singkat (Opsional)",
                        leadingIcon = Icons.Rounded.WorkOutline,
                        singleLine = false,
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                GlassPrimaryButton(
                    text = "Simpan & Masuk ke GSYS",
                    icon = Icons.Rounded.CheckCircle,
                    isLoading = feedback.isSaving,
                    onClick = {
                        val trimmedName = fullName.trim()
                        val trimmedUser = username.trim()
                        val trimmedPhone = phoneNumber.trim()
                        var hasErr = false
                        if (trimmedName.length < 2) {
                            fullNameError = "Nama lengkap minimal 2 karakter"
                            hasErr = true
                        }
                        if (trimmedUser.length < 3) {
                            usernameError = "Username minimal 3 karakter"
                            hasErr = true
                        }
                        if (trimmedPhone.length < 6) {
                            phoneError = "Nomor telepon minimal 6 digit"
                            hasErr = true
                        }
                        if (hasErr) return@GlassPrimaryButton

                        onSubmit(
                            RegistrationDraft(
                                fullName = trimmedName,
                                username = trimmedUser,
                                phoneNumber = trimmedPhone,
                                gender = GenderOption.fromRaw(genderName),
                                birthDate = birthDate.trim(),
                                address = address.trim(),
                                bio = bio.trim()
                            )
                        )
                    },
                    modifier = Modifier.testTag("onboard_submit_button")
                )
            }
        }
    }
}

@Composable
private fun AuthenticatedDashboard(
    profile: UserProfile,
    activitiesState: UiState<List<AuthActivity>>,
    feedback: FormFeedback,
    userPhotoUrl: String?,
    onUpdateProfile: (RegistrationDraft) -> Unit,
    onClearFeedback: () -> Unit,
    onSignOutClick: () -> Unit
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val currentTab = DashboardTab.entries[selectedTabIndex]

    BackHandler(enabled = selectedTabIndex != 0) {
        selectedTabIndex = 0
        onClearFeedback()
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 700.dp

        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (!useRail) {
                    NavigationBar(
                        containerColor = GsysMidnightSurface.copy(alpha = 0.88f),
                        contentColor = GsysTextPrimary,
                        modifier = Modifier
                            .border(1.dp, GsysGlassBorderSubtle)
                            .navigationBarsPadding()
                    ) {
                        DashboardTab.entries.forEachIndexed { index, tab ->
                            NavigationBarItem(
                                selected = selectedTabIndex == index,
                                onClick = {
                                    selectedTabIndex = index
                                    onClearFeedback()
                                },
                                icon = {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = GsysCyan,
                                    selectedTextColor = GsysCyan,
                                    indicatorColor = GsysCyan.copy(alpha = 0.18f),
                                    unselectedIconColor = GsysTextMuted,
                                    unselectedTextColor = GsysTextMuted
                                ),
                                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useRail) {
                    NavigationRail(
                        containerColor = GsysMidnightSurface.copy(alpha = 0.86f),
                        contentColor = GsysTextPrimary,
                        modifier = Modifier
                            .fillMaxHeight()
                            .border(1.dp, GsysGlassBorderSubtle)
                            .statusBarsPadding()
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        DashboardTab.entries.forEachIndexed { index, tab ->
                            NavigationRailItem(
                                selected = selectedTabIndex == index,
                                onClick = {
                                    selectedTabIndex = index
                                    onClearFeedback()
                                },
                                icon = {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = GsysCyan,
                                    selectedTextColor = GsysCyan,
                                    indicatorColor = GsysCyan.copy(alpha = 0.18f),
                                    unselectedIconColor = GsysTextMuted,
                                    unselectedTextColor = GsysTextMuted
                                ),
                                modifier = Modifier.testTag("rail_tab_${tab.name.lowercase()}")
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .statusBarsPadding()
                        .imePadding(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    AnimatedContent(
                        targetState = currentTab,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "dashboardTab"
                    ) { tab ->
                        when (tab) {
                            DashboardTab.HOME -> HomeIdentityPane(
                                profile = profile,
                                userPhotoUrl = userPhotoUrl,
                                onEditProfile = { selectedTabIndex = 1 },
                                onViewActivities = { selectedTabIndex = 2 },
                                onSignOutClick = onSignOutClick
                            )

                            DashboardTab.PROFILE -> EditProfilePane(
                                profile = profile,
                                feedback = feedback,
                                onUpdateProfile = onUpdateProfile
                            )

                            DashboardTab.ACTIVITY -> ActivityAndSecurityPane(
                                profile = profile,
                                activitiesState = activitiesState,
                                onSignOutClick = onSignOutClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeIdentityPane(
    profile: UserProfile,
    userPhotoUrl: String?,
    onEditProfile: () -> Unit,
    onViewActivities: () -> Unit,
    onSignOutClick: () -> Unit
) {
    val completeness = profile.completenessPercent()
    Column(
        modifier = Modifier
            .widthIn(max = 620.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GsysBrandHeader(subtitle = "Identitas Digital Terverifikasi")

        // Hero GSYS Digital Identity Card
        GlassCard(contentPadding = PaddingValues(0.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(148.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_gsys_id_banner),
                    contentDescription = "Banner Kartu Identitas GSYS",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    GsysDeepNavy.copy(alpha = 0.25f),
                                    GsysDeepNavy.copy(alpha = 0.85f)
                                )
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ProfileAvatar(
                            photoUrl = userPhotoUrl ?: profile.photoUrl,
                            fullName = profile.fullName
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = profile.fullName,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = GsysTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Rounded.Verified,
                                    contentDescription = "Terverifikasi",
                                    tint = GsysCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = "@${profile.username}",
                                style = MaterialTheme.typography.labelMedium,
                                color = GsysCyan
                            )
                            Text(
                                text = profile.email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = GsysTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kelengkapan Data Diri",
                        style = MaterialTheme.typography.labelLarge,
                        color = GsysTextSecondary
                    )
                    Text(
                        text = "$completeness%",
                        style = MaterialTheme.typography.labelMedium,
                        color = GsysTeal
                    )
                }
                LinearProgressIndicator(
                    progress = { completeness / 100f },
                    color = GsysTeal,
                    trackColor = Color.White.copy(alpha = 0.12f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassChip(
                        label = "Edit Data Diri",
                        selected = true,
                        leadingIcon = Icons.Rounded.Edit,
                        onClick = onEditProfile,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_edit_profile_chip")
                    )
                    GlassChip(
                        label = "Riwayat",
                        selected = false,
                        leadingIcon = Icons.Rounded.History,
                        onClick = onViewActivities,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_activities_chip")
                    )
                }
            }
        }

        // Personal Details Summary Card
        GlassCard(contentPadding = PaddingValues(20.dp)) {
            Text(
                text = "Ringkasan Data Diri",
                style = MaterialTheme.typography.titleLarge,
                color = GsysTextPrimary
            )
            Spacer(modifier = Modifier.height(14.dp))

            ProfileInfoRow(
                icon = Icons.Rounded.Phone,
                label = "Nomor Telepon",
                value = profile.phoneNumber
            )
            ProfileInfoRow(
                icon = Icons.Rounded.Person,
                label = "Jenis Kelamin",
                value = profile.gender
            )
            ProfileInfoRow(
                icon = Icons.Rounded.CalendarMonth,
                label = "Tanggal Lahir",
                value = profile.birthDate.ifBlank { "Belum diisi" }
            )
            ProfileInfoRow(
                icon = Icons.Rounded.Home,
                label = "Alamat Domisili",
                value = profile.address.ifBlank { "Belum diisi" }
            )
            ProfileInfoRow(
                icon = Icons.Rounded.WorkOutline,
                label = "Pekerjaan / Bio",
                value = profile.bio.ifBlank { "Belum diisi" }
            )
        }

        OutlinedButton(
            onClick = onSignOutClick,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, GsysError.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .minimumInteractiveComponentSize()
                .testTag("home_sign_out_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Logout,
                contentDescription = null,
                tint = GsysError,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.action_sign_out),
                color = GsysError,
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfilePane(
    profile: UserProfile,
    feedback: FormFeedback,
    onUpdateProfile: (RegistrationDraft) -> Unit
) {
    var fullName by remember(profile.id) { mutableStateOf(profile.fullName) }
    var username by remember(profile.id) { mutableStateOf(profile.username) }
    var phoneNumber by remember(profile.id) { mutableStateOf(profile.phoneNumber) }
    var genderName by remember(profile.id) { mutableStateOf(profile.gender) }
    var birthDate by remember(profile.id) { mutableStateOf(profile.birthDate) }
    var address by remember(profile.id) { mutableStateOf(profile.address) }
    var bio by remember(profile.id) { mutableStateOf(profile.bio) }

    LaunchedEffect(profile) {
        fullName = profile.fullName
        username = profile.username
        phoneNumber = profile.phoneNumber
        genderName = profile.gender
        birthDate = profile.birthDate
        address = profile.address
        bio = profile.bio
    }

    var fullNameError by remember { mutableStateOf<String?>(null) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

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
                ) { Text(stringResource(R.string.action_select)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Column(
        modifier = Modifier
            .widthIn(max = 620.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Kelola Data Diri",
            style = MaterialTheme.typography.headlineMedium,
            color = GsysTextPrimary
        )

        if (feedback.message != null) {
            FeedbackBanner(feedback = feedback)
        }

        GlassCard(contentPadding = PaddingValues(20.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                GlassTextField(
                    value = fullName,
                    onValueChange = { fullName = it; fullNameError = null },
                    label = "Nama Lengkap *",
                    leadingIcon = Icons.Rounded.Person,
                    errorText = fullNameError,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    modifier = Modifier.testTag("edit_fullname_input")
                )
                GlassTextField(
                    value = username,
                    onValueChange = { username = it.replace(" ", ""); usernameError = null },
                    label = "Username *",
                    leadingIcon = Icons.Rounded.AlternateEmail,
                    errorText = usernameError,
                    modifier = Modifier.testTag("edit_username_input")
                )
                GlassTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it; phoneError = null },
                    label = "Nomor Telepon *",
                    leadingIcon = Icons.Rounded.Phone,
                    errorText = phoneError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.testTag("edit_phone_input")
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
                                selected = genderName == option.label,
                                onClick = { genderName = option.label },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                ) {
                    GlassTextField(
                        value = birthDate,
                        onValueChange = {},
                        readOnly = true,
                        label = "Tanggal Lahir",
                        leadingIcon = Icons.Rounded.CalendarMonth,
                        trailingContent = {
                            TextButton(onClick = { showDatePicker = true }) {
                                Text("Ubah", color = GsysCyan)
                            }
                        }
                    )
                }
                GlassTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = "Alamat Domisili",
                    leadingIcon = Icons.Rounded.Home,
                    singleLine = false,
                    maxLines = 2
                )
                GlassTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = "Pekerjaan / Bio",
                    leadingIcon = Icons.Rounded.WorkOutline,
                    singleLine = false,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            GlassPrimaryButton(
                text = "Simpan Perubahan",
                icon = Icons.Rounded.Save,
                isLoading = feedback.isSaving,
                onClick = {
                    val trimmedName = fullName.trim()
                    val trimmedUser = username.trim()
                    val trimmedPhone = phoneNumber.trim()
                    var hasErr = false
                    if (trimmedName.length < 2) {
                        fullNameError = "Nama lengkap minimal 2 karakter"
                        hasErr = true
                    }
                    if (trimmedUser.length < 3) {
                        usernameError = "Username minimal 3 karakter"
                        hasErr = true
                    }
                    if (trimmedPhone.length < 6) {
                        phoneError = "Nomor telepon minimal 6 digit"
                        hasErr = true
                    }
                    if (hasErr) return@GlassPrimaryButton

                    onUpdateProfile(
                        RegistrationDraft(
                            fullName = trimmedName,
                            username = trimmedUser,
                            phoneNumber = trimmedPhone,
                            gender = GenderOption.fromRaw(genderName),
                            birthDate = birthDate.trim(),
                            address = address.trim(),
                            bio = bio.trim()
                        )
                    )
                },
                modifier = Modifier.testTag("save_profile_button")
            )
        }
    }
}

@Composable
private fun ActivityAndSecurityPane(
    profile: UserProfile,
    activitiesState: UiState<List<AuthActivity>>,
    onSignOutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .widthIn(max = 620.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Aktivitas & Keamanan",
            style = MaterialTheme.typography.headlineMedium,
            color = GsysTextPrimary
        )

        GlassCard(contentPadding = PaddingValues(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Security,
                    contentDescription = null,
                    tint = GsysTeal
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Status Autentikasi Firebase",
                    style = MaterialTheme.typography.titleLarge,
                    color = GsysTextPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            ProfileInfoRow(
                icon = Icons.Rounded.Email,
                label = "Email Terdaftar",
                value = profile.email
            )
            ProfileInfoRow(
                icon = Icons.Rounded.Badge,
                label = "Firebase UID",
                value = profile.userId
            )
            Spacer(modifier = Modifier.height(12.dp))
            GlassPrimaryButton(
                text = stringResource(R.string.action_sign_out),
                icon = Icons.AutoMirrored.Rounded.Logout,
                onClick = onSignOutClick,
                modifier = Modifier.testTag("sign_out_button")
            )
        }

        GlassCard(contentPadding = PaddingValues(18.dp)) {
            Text(
                text = "Riwayat Sesi & Perubahan",
                style = MaterialTheme.typography.titleLarge,
                color = GsysTextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            when (activitiesState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = GsysCyan, modifier = Modifier.size(26.dp))
                    }
                }

                is UiState.Error -> {
                    Text(
                        text = activitiesState.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GsysError
                    )
                }

                is UiState.Success -> {
                    if (activitiesState.data.isEmpty()) {
                        Text(
                            text = "Belum ada aktivitas tercatat.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GsysTextMuted
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            activitiesState.data.forEach { item ->
                                ActivityLogRow(item = item)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityLogRow(item: AuthActivity) {
    val category = ActivityCategory.fromRaw(item.category)
    val accent = when (category) {
        ActivityCategory.AUTH -> GsysTeal
        ActivityCategory.PROFILE -> GsysCyan
        ActivityCategory.SECURITY -> GsysIndigo
    }
    val dateLabel = formatTimestamp(item.createdAt)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, GsysGlassBorderSubtle, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(accent)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.action,
                    style = MaterialTheme.typography.labelLarge,
                    color = GsysTextPrimary
                )
                Text(
                    text = dateLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = GsysTextMuted
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.detail,
                style = MaterialTheme.typography.bodyMedium,
                color = GsysTextSecondary
            )
        }
    }
}

@Composable
private fun ProfileAvatar(
    photoUrl: String?,
    fullName: String
) {
    val initials = fullName.trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }
        .ifBlank { "G" }

    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(listOf(GsysCyan, GsysIndigo))
            )
            .border(2.dp, GsysGlassBorderBright, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (!photoUrl.isNullOrBlank()) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "Foto profil $fullName",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = initials,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = GsysDeepNavy
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.07f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = GsysCyan,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = GsysTextMuted
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = GsysTextPrimary
            )
        }
    }
}

@Composable
private fun FeedbackBanner(feedback: FormFeedback) {
    val accent = if (feedback.isError) GsysError else GsysSuccess
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = accent.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (feedback.isError) Icons.Rounded.Info else Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = feedback.message.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                color = GsysTextPrimary
            )
        }
    }
}

private fun formatTimestamp(timestamp: Timestamp?): String {
    val date = (timestamp ?: Timestamp.now()).toDate()
    val formatter = SimpleDateFormat("dd MMM, HH:mm", Locale.forLanguageTag("id-ID"))
    return formatter.format(date)
}
