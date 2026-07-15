package com.malayanquest.app

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.malayanquest.app.navigation.Screen
import com.malayanquest.app.ui.components.*
import com.malayanquest.app.ui.screens.auth.*
import com.malayanquest.app.ui.screens.common.*
import com.malayanquest.app.ui.screens.dashboard.*
import com.malayanquest.app.ui.screens.errand.*
import com.malayanquest.app.ui.screens.profile.*
import com.malayanquest.app.ui.screens.admin.*
import com.malayanquest.app.ui.screens.message.*
import com.malayanquest.app.ui.theme.*
import com.malayanquest.app.util.*
import org.json.JSONObject
import kotlinx.coroutines.delay

@SuppressLint("MutableCollectionMutableState")
class MainActivity : ComponentActivity() {
    private val api = ApiClient()
    private lateinit var prefs: SharedPreferences

    private var userId by mutableIntStateOf(0)
    private var fullName by mutableStateOf("")
    private var accountRole by mutableStateOf("")
    private var role by mutableStateOf("")
    private var screen by mutableStateOf(Screen.Splash)
    private var selectedErrand by mutableStateOf<JSONObject?>(null)
    private var selectedApplicant by mutableStateOf<JSONObject?>(null)
    private var adminEndpoint by mutableStateOf("")
    private var adminTitle by mutableStateOf("")
    private var refreshKey by mutableIntStateOf(0)
    private var previousMainScreen by mutableStateOf(Screen.BrowseErrands)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("malayanquest_session", MODE_PRIVATE)
        userId = prefs.getInt("user_id", 0)
        fullName = prefs.getString("full_name", "") ?: ""
        accountRole = prefs.getString("role", "") ?: ""
        role = accountRole

        setContent {
            CampusTheme {
                App()
            }
        }
    }

    @Composable
    private fun App() {
        LaunchedEffect(Unit) {
            delay(850)
            screen = if (userId > 0) {
                if (role == "admin") Screen.AdminDashboard else Screen.BrowseErrands
            } else Screen.Login
        }
        BackHandler(enabled = canNavigateBack()) {
            navigateBack()
        }

        val header = headerText()
        val authScreen = screen == Screen.Splash || screen == Screen.Login || screen == Screen.Register
        val studentChrome = userId > 0 && role != "admin" && !authScreen
        val scrollState = rememberScrollState()

        LaunchedEffect(screen) {
            scrollState.scrollTo(0)
        }

        Scaffold(
            topBar = {
                if (!authScreen) {
                    Header(
                        title = header.first,
                        subtitle = header.second,
                        canNavigateBack = canNavigateBack(),
                        onBack = { navigateBack() }
                    )
                }
            },
            bottomBar = {
                if (studentChrome) {
                    StudentBottomNav(
                        currentScreen = screen,
                        showAdminDashboard = accountRole == "admin",
                        onNavigate = { target ->
                            if (target == Screen.Profile) {
                                selectedErrand = null
                                selectedApplicant = null
                            }
                            if (target == Screen.BrowseErrands || target == Screen.MyTasks) {
                                refreshKey++
                            }
                            screen = target
                        },
                        onAdminDashboard = {
                            role = "admin"
                            screen = Screen.AdminDashboard
                        }
                    )
                }
            },
            floatingActionButton = {
                if (studentChrome && (screen == Screen.BrowseErrands || screen == Screen.MyTasks)) {
                    FloatingActionButton(
                        onClick = {
                            previousMainScreen = screen
                            screen = Screen.PostErrand
                        },
                        containerColor = PrimaryBlue,
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(Icons.Filled.Add, "Post Errand")
                    }
                }
            },
            containerColor = if (authScreen) Color.White else BackgroundSoft
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(
                        horizontal = if (authScreen) 24.dp else 16.dp,
                        vertical = if (authScreen) 28.dp else 14.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (screen) {
                    Screen.Splash -> SplashScreen()
                    Screen.Login -> LoginScreen(
                        api = api,
                        onLoginSuccess = { data ->
                            saveSession(data)
                            screen = if (role == "admin") Screen.AdminDashboard else Screen.BrowseErrands
                        },
                        onNavigateToRegister = { screen = Screen.Register },
                        onShowToast = { toast(it) }
                    )
                    Screen.Register -> RegisterScreen(
                        api = api,
                        onRegisterSuccess = { screen = Screen.Login },
                        onNavigateBack = { screen = Screen.Login },
                        onShowToast = { toast(it) }
                    )
                    Screen.BrowseErrands -> BrowseErrandsScreen(
                        api = api,
                        userId = userId,
                        refreshKey = refreshKey,
                        onNavigate = { target, errand ->
                            selectedErrand = errand
                            previousMainScreen = Screen.BrowseErrands // Save context
                            screen = target
                        }
                    )
                    Screen.MyTasks -> MyTasksScreen(
                        api = api,
                        userId = userId,
                        refreshKey = refreshKey,
                        onNavigate = { target, errand ->
                            selectedErrand = errand
                            previousMainScreen = Screen.MyTasks // Save context
                            screen = target
                        },
                        onUpdateStatus = { errand, newStatus -> updateStatus(errand, newStatus) },
                        onPostStatus = { endpoint, errand, userKey, successStatus -> postStatus(endpoint, errand, userKey, successStatus) }
                    )
                    Screen.PostErrand -> PostErrandScreen(
                        api = api,
                        userId = userId,
                        onSuccess = {
                            refreshKey++
                            screen = Screen.MyTasks
                        },
                        onNavigateBack = { navigateBack() },
                        onShowToast = { toast(it) }
                    )
                    Screen.ErrandDetails -> ErrandDetailsScreen(
                        errand = selectedErrand,
                        applyMode = (selectedErrand?.optInt("requester_id") != userId) &&
                                (selectedErrand?.optString("status") == "Open" || selectedErrand?.optString("status") == "Has Applicants"),
                        userId = userId,
                        onApply = {
                            selectedErrand = it
                            screen = Screen.Apply
                        },
                        onReport = {
                            selectedErrand = it
                            screen = Screen.Report
                        },
                        onNavigateBack = { navigateBack() },
                        onViewApplicants = {
                            selectedErrand = it
                            screen = Screen.Applicants
                        },
                        onMessages = {
                            selectedErrand = it
                            screen = Screen.Chat
                        },
                        onConfirmCompletion = { confirmedErrand ->
                            postStatus(
                                endpoint = "confirm_completion.php",
                                errand = confirmedErrand,
                                userKey = "requester_id",
                                successStatus = "Confirmed by Requester"
                            ) {
                                selectedErrand = confirmedErrand
                                screen = Screen.Rating
                            }
                        },
                        onRateHelper = {
                            selectedErrand = it
                            screen = Screen.Rating
                        },
                        onCancelErrand = {
                            selectedErrand = it
                            screen = Screen.CancelErrand
                        }
                    )
                    Screen.Apply -> ApplyScreen(
                        api = api,
                        userId = userId,
                        fullName = fullName,
                        errand = selectedErrand,
                        onSuccess = {
                            refreshKey++
                            screen = Screen.MyTasks
                        },
                        onNavigateBack = { screen = Screen.ErrandDetails },
                        onShowToast = { toast(it) }
                    )
                    Screen.Applicants -> ApplicantsScreen(
                        api = api,
                        userId = userId,
                        errand = selectedErrand,
                        onNavigate = { target, applicant ->
                            selectedApplicant = applicant
                            screen = target
                        },
                        onSelectHelper = { applicant, errandId -> selectHelper(applicant, errandId) },
                        onNavigateBack = { navigateBack() } // UPDATED
                    )
                    Screen.HelperProfile -> HelperProfileScreen(
                        applicant = selectedApplicant,
                        onNavigateBack = { screen = Screen.Applicants }
                    )
                    Screen.Status -> StatusScreen(
                        errand = selectedErrand,
                        userId = userId,
                        onRequesterActions = { e ->
                            RequesterActions(
                                errand = e,
                                onViewApplicants = { obj -> selectedErrand = obj; screen = Screen.Applicants },
                                onMessages = { obj -> selectedErrand = obj; screen = Screen.Chat },
                                onConfirmCompletion = { obj -> selectedErrand = obj; screen = Screen.Completion },
                                onRateHelper = { obj -> selectedErrand = obj; screen = Screen.Rating },
                                onCancelErrand = { obj -> selectedErrand = obj; screen = Screen.CancelErrand }
                            )
                        },
                        onHelperActions = { e ->
                            HelperActions(
                                errand = e,
                                userId = userId,
                                onUpdateStatus = { _, s -> updateStatus(e, s) },
                                onPostStatus = { ep, _, k, s -> postStatus(ep, e, k, s) },
                                onMessages = { obj -> selectedErrand = obj; screen = Screen.Chat },
                                onCancel = { obj -> selectedErrand = obj; screen = Screen.CancelErrand },
                                onStatusTracker = { obj -> selectedErrand = obj; screen = Screen.Status }
                            )
                        },
                        onNavigateBack = { navigateBack() }
                    )
                    Screen.Chat -> ChatScreen(
                        api = api,
                        userId = userId,
                        errand = selectedErrand,
                        refreshKey = refreshKey,

                        onSendMessage = { receiverId, msg, onResult ->

                            api.post("send_message.php", JSONObject().apply {
                                put(
                                    "errand_id",
                                    selectedErrand?.optInt("errand_id")
                                )
                                put("sender_id", userId)
                                put("receiver_id", receiverId)
                                put("message_text", msg.trim())
                            }) { response ->

                                val success =
                                    response.optBoolean("success")

                                toast(response.optString("message"))

                                if (success) {
                                    refreshKey++
                                }
                                onResult(success)
                            }
                        },

                        onNavigateBack = {
                            navigateBack()
                        }
                    )
                    Screen.Completion -> CompletionScreen(
                        errand = selectedErrand,
                        onConfirmCompletion = { confirmedErrand ->
                            postStatus(
                                endpoint = "confirm_completion.php",
                                errand = confirmedErrand,
                                userKey = "requester_id",
                                successStatus = "Confirmed by Requester"
                            ) {
                                screen = Screen.Rating
                            }
                        },
                        onNavigateBack = { navigateBack() } // UPDATED
                    )
                    Screen.Rating -> RatingScreen(
                        api = api,
                        userId = userId,
                        errand = selectedErrand,
                        onSuccess = {
                            refreshKey++
                            screen = Screen.MyTasks
                        },
                        onNavigateBack = { navigateBack() }, // UPDATED
                        onShowToast = { toast(it) }
                    )
                    Screen.Report -> ReportScreen(
                        api = api,
                        userId = userId,
                        errand = selectedErrand,
                        onSuccess = { screen = Screen.MyTasks },
                        onNavigateBack = { navigateBack() },
                        onShowToast = { toast(it) }
                    )
                    Screen.Profile -> ProfileScreen(
                        api = api,
                        userId = userId,
                        fullName = fullName,
                        onNavigate = { target ->
                            if (target == Screen.Report) {
                                selectedErrand = null
                            }
                            screen = target
                        },
                        onLogout = { logout() }
                    )
                    Screen.EditProfile -> EditProfileScreen(
                        api = api,
                        userId = userId,
                        currentFullName = fullName,
                        onSuccess = { newName ->
                            fullName = newName
                            prefs.edit().putString("full_name", fullName).apply()
                            screen = Screen.Profile
                        },
                        onNavigateBack = { screen = Screen.Profile },
                        onShowToast = { toast(it) }
                    )
                    Screen.History -> HistoryScreen(
                        api = api,
                        userId = userId,
                        onNavigate = { targetScreen, errand ->
                            if (errand != null) {
                                selectedErrand = errand
                            }
                            previousMainScreen = Screen.History // Save context
                            screen = targetScreen
                        },
                        onNavigateBack = { navigateBack() }
                    )
                    Screen.UserRatings -> UserRatingsScreen(
                        api = api,
                        userId = userId,
                        onNavigateBack = { screen = Screen.History }
                    )
                    Screen.CancelErrand -> CancelErrandScreen(
                        api = api,
                        userId = userId,
                        errand = selectedErrand,
                        onSuccess = {
                            refreshKey++
                            screen = Screen.MyTasks
                        },
                        onNavigateBack = { navigateBack() },
                        onShowToast = { toast(it) }
                    )
                    Screen.AdminDashboard -> AdminDashboardScreen(
                        api = api,
                        userId = userId,
                        onOpenAdminArray = { endpoint, title ->
                            adminEndpoint = endpoint
                            adminTitle = title
                            refreshKey++
                            screen = Screen.AdminArray
                        },
                        onNavigateToRatings = { screen = Screen.AdminRatings },
                        onSwitchToStudent = {
                            role = "student"
                            screen = Screen.BrowseErrands
                        },
                        onLogout = { logout() }
                    )
                    Screen.AdminArray -> AdminArrayScreen(
                        api = api,
                        adminId = userId,
                        endpoint = adminEndpoint,
                        title = adminTitle,
                        onViewRecord = { item, title ->
                            selectedErrand = item
                            adminTitle = title
                            screen = Screen.AdminRecordDetails
                        },
                        onNavigateBack = { screen = Screen.AdminDashboard }
                    )
                    Screen.AdminRecordDetails -> AdminRecordDetailsScreen(
                        item = selectedErrand,
                        title = adminTitle,
                        onUpdateUserStatus = { targetId, status -> updateUserStatus(targetId, status) },
                        onRemoveErrand = { errandId -> removeErrand(errandId) },
                        onResolveReport = { reportId -> resolveReport(reportId) },
                        onNavigateBack = { screen = Screen.AdminDashboard }
                    )
                    Screen.AdminRatings -> AdminRatingsScreen(
                        api = api,
                        adminId = userId,
                        onNavigateBack = { screen = Screen.AdminDashboard }
                    )
                    else -> {}
                }
            }
        }
    }

    private fun headerText(): Pair<String, String> {
        return when (screen) {
            Screen.Splash -> "Malayan Quest" to "School-only campus errands for MMCL students"
            Screen.Login -> "Malayan Quest" to "Login to the campus errand prototype"
            Screen.Register -> "Register" to "Create a verified prototype student account"
            Screen.Dashboard -> if (role == "admin") "Admin Dashboard" to "Monitoring and moderation" else "Malayan Quest" to (if (fullName.isBlank()) "Student dashboard" else "Welcome, $fullName")
            Screen.PostErrand -> "Post Errand" to "Campus and near-campus errands only"
            Screen.BrowseErrands -> "Errand Feed" to "Available tasks around campus"
            Screen.ErrandDetails -> "Errand Details" to selectedErrand.optTitle()
            Screen.Apply -> "Apply as Helper" to selectedErrand.optTitle()
            Screen.MyPosted -> "Requested Errands" to "Workflow"
            Screen.MyHelper -> "Helper Tasks" to "Assignments"
            Screen.MyTasks -> "My Tasks" to "Requested and Doing"
            Screen.Applicants -> "Applicants" to "Choose one helper"
            Screen.HelperProfile -> "Helper Profile Preview" to (selectedApplicant?.optString("helper_name") ?: "")
            Screen.Status -> "Errand Status" to selectedErrand.optTitle()
            Screen.MessageHub -> "Messages" to "Chats open after a helper is selected"
            Screen.Chat -> "Messages" to selectedErrand.optTitle()
            Screen.Completion -> "Completion Confirmation" to selectedErrand.optTitle()
            Screen.Rating -> "Rate Helper" to selectedErrand.optTitle()
            Screen.Report -> if (selectedErrand == null) "Report Problem" to "Safety and moderation" else "Report Errand" to "Safety and moderation"
            Screen.Profile -> "Profile" to "Account summary and tools"
            Screen.EditProfile -> "Edit Profile" to "Update account display details"
            Screen.History -> "History" to "Completed errands"
            Screen.UserRatings -> "Ratings and Feedback" to "Your received feedback"
            Screen.CancelErrand -> "Cancel Errand" to selectedErrand.optTitle()
            Screen.AdminDashboard -> "Admin Dashboard" to "Monitoring and moderation"
            Screen.AdminArray -> adminTitle to "Admin records"
            Screen.AdminRecordDetails -> "$adminTitle Details" to "Admin detail view"
            Screen.AdminRatings -> "Ratings and Feedback" to "Admin review screen"
        }
    }

    private fun canNavigateBack(): Boolean {
        return screen != Screen.Splash &&
                screen != Screen.Login &&
                screen != Screen.BrowseErrands &&
                screen != Screen.MyTasks &&
                screen != Screen.Profile &&
                screen != Screen.AdminDashboard
    }

    private fun navigateBack() {
        screen = when (screen) {
            Screen.ErrandDetails -> previousMainScreen
            Screen.Apply -> Screen.ErrandDetails
            Screen.Applicants -> previousMainScreen
            Screen.Status -> previousMainScreen
            Screen.Chat -> previousMainScreen
            Screen.Completion -> previousMainScreen
            Screen.Rating -> previousMainScreen
            Screen.CancelErrand -> previousMainScreen
            Screen.Report -> if (selectedErrand != null) Screen.ErrandDetails else previousMainScreen
            Screen.HelperProfile -> Screen.Applicants
            Screen.EditProfile -> Screen.Profile
            Screen.History -> Screen.Profile
            Screen.UserRatings -> Screen.History
            Screen.AdminArray -> Screen.AdminDashboard
            Screen.AdminRecordDetails -> Screen.AdminArray
            Screen.AdminRatings -> Screen.AdminDashboard
            Screen.PostErrand -> previousMainScreen
            Screen.Register -> Screen.Login
            else -> if (userId > 0) {
                if (role == "admin") Screen.AdminDashboard else Screen.BrowseErrands
            } else Screen.Login
        }
    }

    private fun selectHelper(applicant: JSONObject, errandId: Int) {
        api.post("select_helper.php", JSONObject().apply {
            put("errand_id", errandId)
            put("requester_id", userId)
            put("application_id", applicant.optInt("application_id"))
        }) { response ->

            toast(response.optString("message"))

            // Stop when helper selection fails.
            if (!response.optBoolean("success")) {
                return@post
            }

            api.get(
                "get_errand_details.php",
                mapOf(
                    "errand_id" to errandId.toString()
                )
            ) { detailsResponse ->

                if (!detailsResponse.optBoolean("success")) {
                    toast(
                        detailsResponse.optString(
                            "message",
                            "Helper selected, but the errand could not be refreshed."
                        )
                    )

                    refreshKey++
                    screen = Screen.MyTasks
                    return@get
                }

                val updatedErrand =
                    detailsResponse.optJSONObject("data")

                val selectedHelperId =
                    updatedErrand?.optInt("selected_helper_id") ?: 0

                if (updatedErrand == null || selectedHelperId <= 0) {
                    toast(
                        "Helper selected, but no valid helper ID was returned."
                    )

                    refreshKey++
                    screen = Screen.MyTasks
                    return@get
                }

                selectedErrand = updatedErrand
                refreshKey++
                screen = Screen.MyTasks
            }
        }
    }

    private fun saveSession(data: JSONObject) {
        userId = data.optInt("user_id")
        fullName = data.optString("full_name")
        role = data.optString("role")
        accountRole = role
        prefs.edit()
            .putInt("user_id", userId)
            .putString("full_name", fullName)
            .putString("role", role)
            .apply()
    }

    private fun logout() {
        prefs.edit()
            .remove("user_id")
            .remove("full_name")
            .remove("role")
            .apply()
        userId = 0
        fullName = ""
        accountRole = ""
        role = ""
        selectedErrand = null
        selectedApplicant = null
        screen = Screen.Login
    }

    private fun updateStatus(errand: JSONObject, newStatus: String) {
        api.post("update_errand_status.php", JSONObject().apply {
            put("errand_id", errand.optInt("errand_id"))
            put("changed_by", userId)
            put("new_status", newStatus)
            put("reason", "")
        }) { response ->
            toast(response.optString("message"))
            if (response.optBoolean("success")) {
                refreshKey++
                screen = Screen.MyTasks
            }
        }
    }

    private fun postStatus(
        endpoint: String,
        errand: JSONObject,
        userKey: String,
        successStatus: String,
        onSuccess: () -> Unit = { screen = Screen.MyTasks }
    ) {
        api.post(endpoint, JSONObject().apply {
            put("errand_id", errand.optInt("errand_id"))
            put(userKey, userId)
        }) { response ->
            toast(response.optString("message"))
            if (response.optBoolean("success")) {
                errand.put("status", successStatus)
                refreshKey++
                onSuccess()
            }
        }
    }

    private fun updateUserStatus(targetUserId: Int, status: String) {
        api.post("admin_update_user_status.php", JSONObject().apply {
            put("admin_id", userId)
            put("target_user_id", targetUserId)
            put("account_status", status)
        }) { response ->
            toast(response.optString("message"))
            refreshKey++
            screen = Screen.AdminDashboard
        }
    }

    private fun removeErrand(errandId: Int) {
        api.post("admin_remove_errand.php", JSONObject().apply {
            put("admin_id", userId)
            put("errand_id", errandId)
        }) { response ->
            toast(response.optString("message"))
            refreshKey++
            screen = Screen.AdminDashboard
        }
    }

    private fun resolveReport(reportId: Int) {
        api.post("admin_resolve_report.php", JSONObject().apply {
            put("admin_id", userId)
            put("report_id", reportId)
            put("resolution_note", "Resolved from Android prototype admin screen.")
        }) { response ->
            toast(response.optString("message"))
            refreshKey++
            screen = Screen.AdminDashboard
        }
    }

    private fun toast(message: String?) {
        Toast.makeText(this, if (message.isNullOrBlank()) "Done" else message, Toast.LENGTH_LONG).show()
    }

    private fun JSONObject?.optTitle(): String {
        return this?.optString("title") ?: ""
    }
}