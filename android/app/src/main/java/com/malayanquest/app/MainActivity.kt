package com.malayanquest.app

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.delay
import java.util.Locale

@SuppressLint("MutableCollectionMutableState")
class MainActivity : ComponentActivity() {
    private val api = ApiClient()
    private lateinit var prefs: SharedPreferences

    private var userId by mutableIntStateOf(0)
    private var fullName by mutableStateOf("")
    private var role by mutableStateOf("")
    private var screen by mutableStateOf(Screen.Splash)
    private var selectedErrand by mutableStateOf<JSONObject?>(null)
    private var selectedApplicant by mutableStateOf<JSONObject?>(null)
    private var adminEndpoint by mutableStateOf("")
    private var adminTitle by mutableStateOf("")
    private var refreshKey by mutableIntStateOf(0)

    private enum class Screen {
        Splash,
        Login,
        Register,
        Dashboard,
        PostErrand,
        BrowseErrands,
        ErrandDetails,
        Apply,
        MyPosted,
        MyHelper,
        Applicants,
        HelperProfile,
        Status,
        MessageHub,
        Chat,
        Completion,
        Rating,
        Report,
        Profile,
        EditProfile,
        History,
        UserRatings,
        CancelErrand,
        AdminDashboard,
        AdminArray,
        AdminRecordDetails,
        AdminRatings
    }

    private val categories = listOf(
        "Food Pickup",
        "Printing Pickup",
        "Document Delivery",
        "School Supplies Purchase",
        "Bluebook Purchase",
        "Bookstore Item Purchase",
        "Campus Item Delivery",
        "Classroom-to-Classroom Delivery",
        "Library or Bookstore Errand",
        "Nearby Establishment Errand",
        "Other School-Related Errand"
    )

    private val statusSteps = listOf(
        "Open",
        "Has Applicants",
        "Assigned",
        "Accepted",
        "In Progress",
        "Completed by Helper",
        "Confirmed by Requester",
        "Rated",
        "Closed"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("malayanquest_session", MODE_PRIVATE)
        userId = prefs.getInt("user_id", 0)
        fullName = prefs.getString("full_name", "") ?: ""
        role = prefs.getString("role", "") ?: ""

        setContent {
            CampusTheme {
                App()
            }
        }
    }

    @Composable
    private fun CampusTheme(content: @Composable () -> Unit) {
        MaterialTheme(
            colorScheme = lightColorScheme(
                primary = PrimaryBlue,
                secondary = SecondaryBlue,
                background = BackgroundSoft,
                surface = Color.White,
                error = DangerRed
            ),
            content = content
        )
    }

    @Composable
    private fun App() {
        LaunchedEffect(Unit) {
            delay(850)
            screen = if (userId > 0) Screen.Dashboard else Screen.Login
        }
        BackHandler(enabled = canNavigateBack()) {
            navigateBack()
        }

        val header = headerText()
        val authScreen = screen == Screen.Splash || screen == Screen.Login || screen == Screen.Register
        val studentChrome = userId > 0 && role != "admin" && !authScreen
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = if (authScreen) Color.White else BackgroundSoft
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (!authScreen) {
                    if (screen == Screen.Dashboard && role != "admin") {
                        DashboardHeader()
                    } else {
                        Header(header.first, header.second)
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(
                            horizontal = if (authScreen) 24.dp else 16.dp,
                            vertical = if (authScreen) 28.dp else 14.dp
                        ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (screen) {
                        Screen.Splash -> SplashScreen()
                        Screen.Login -> LoginScreen()
                        Screen.Register -> RegisterScreen()
                        Screen.Dashboard -> if (role == "admin") AdminDashboardScreen() else StudentDashboardScreen()
                        Screen.PostErrand -> PostErrandScreen()
                        Screen.BrowseErrands -> BrowseErrandsScreen()
                        Screen.ErrandDetails -> ErrandDetailsScreen(selectedErrand, applyMode = true)
                        Screen.Apply -> ApplyScreen(selectedErrand)
                        Screen.MyPosted -> MyPostedScreen()
                        Screen.MyHelper -> MyHelperScreen()
                        Screen.Applicants -> ApplicantsScreen(selectedErrand)
                        Screen.HelperProfile -> HelperProfileScreen(selectedApplicant)
                        Screen.Status -> StatusScreen(selectedErrand)
                        Screen.MessageHub -> MessageHubScreen()
                        Screen.Chat -> ChatScreen(selectedErrand)
                        Screen.Completion -> CompletionScreen(selectedErrand)
                        Screen.Rating -> RatingScreen(selectedErrand)
                        Screen.Report -> ReportScreen(selectedErrand)
                        Screen.Profile -> ProfileScreen()
                        Screen.EditProfile -> EditProfileScreen()
                        Screen.History -> HistoryScreen()
                        Screen.UserRatings -> UserRatingsScreen()
                        Screen.CancelErrand -> CancelErrandScreen(selectedErrand)
                        Screen.AdminDashboard -> AdminDashboardScreen()
                        Screen.AdminArray -> AdminArrayScreen(adminEndpoint, adminTitle)
                        Screen.AdminRecordDetails -> AdminRecordDetailsScreen(selectedErrand, adminTitle)
                        Screen.AdminRatings -> AdminRatingsScreen()
                    }
                }
                if (studentChrome) {
                    StudentBottomNav()
                }
            }
        }
    }

    @Composable
    private fun Header(title: String, subtitle: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (canNavigateBack()) {
                IconButton(
                    onClick = { navigateBack() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = PrimaryBlue
                    )
                }
            } else {
                Spacer(Modifier.size(40.dp))
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(title, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                if (subtitle.isNotBlank()) {
                    Text(subtitle, color = TextSecondary, fontSize = 11.sp)
                }
            }
            Spacer(Modifier.size(40.dp))
        }
        Divider(color = BorderSoft)
    }

    @Composable
    private fun DashboardHeader() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryBlue)
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 22.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.weight(1f))
                Text("Malayan Quest", color = Color(0xFFDCEBFA), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                "Hello, ${firstName(fullName).ifBlank { "Student" }}!",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text("What do you want to do today?", color = Color(0xFFDCEBFA), fontSize = 13.sp)
            Badge("Verified Prototype", Color.White, fill = Color(0xFF0A63D8), border = Color.White)
        }
    }

    @Composable
    private fun SplashScreen() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 74.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEAF3FF)),
                contentAlignment = Alignment.Center
            ) {
                Text("MQ", color = PrimaryBlue, fontSize = 38.sp, fontWeight = FontWeight.Bold)
            }
            Text("Malayan Quest", color = PrimaryBlue, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Student Errand Platform", color = SecondaryBlue, fontSize = 15.sp)
            Spacer(Modifier.height(42.dp))
            Badge("Prototype Version", PrimaryBlue)
        }
    }

    @Composable
    private fun LoginScreen() {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        AuthTitle("Welcome Back!", "Sign in to your account")
        CampusTextField("School email", email, { email = it }, KeyboardType.Email)
        CampusTextField("Password", password, { password = it }, KeyboardType.Password, password = true)
        CampusButton("Login") {
            api.post("login.php", JSONObject().apply {
                put("school_email", email.trim())
                put("password", password)
            }) { response ->
                if (response.optBoolean("success")) {
                    response.optJSONObject("data")?.let {
                        saveSession(it)
                        screen = Screen.Dashboard
                    }
                } else {
                    toast(response.optString("message"))
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text("or", color = TextSecondary, fontSize = 12.sp)
        }
        CampusButton("Create student account", primary = false) { screen = Screen.Register }
        InfoPanel("Demo accounts use password: password\nAdmin: admin@mcl.edu.ph\nStudent: juan.dcruz@mcl.edu.ph, maria.santos@mcl.edu.ph, carlo.reyes@mcl.edu.ph")
    }

    @Composable
    private fun RegisterScreen() {
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var studentNumber by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirm by remember { mutableStateOf("") }

        AuthTitle("Create Student Account", "All accounts are verified for prototype use.")
        InfoPanel("Use your school email and student number. Student numbers and school emails are stored for verification and are not shown publicly.")
        CampusTextField("Full name", name, { name = it })
        CampusTextField("School email, example: student@mcl.edu.ph", email, { email = it }, KeyboardType.Email)
        CampusTextField("Student number", studentNumber, { studentNumber = it })
        CampusTextField("Password", password, { password = it }, KeyboardType.Password, password = true)
        CampusTextField("Confirm password", confirm, { confirm = it }, KeyboardType.Password, password = true)
        CampusButton("Register") {
            if (password != confirm) {
                toast("Passwords do not match.")
                return@CampusButton
            }
            api.post("register.php", JSONObject().apply {
                put("full_name", name.trim())
                put("school_email", email.trim())
                put("student_number", studentNumber.trim())
                put("password", password)
            }) { response ->
                toast(response.optString("message"))
                if (response.optBoolean("success")) screen = Screen.Login
            }
        }
        CampusButton("Back to login", primary = false) { screen = Screen.Login }
    }

    @Composable
    private fun StudentDashboardScreen() {
        ActionGrid(
            listOf(
                "Post Errand" to "Create a campus request",
                "Browse Errands" to "Find helper tasks",
                "My Posted" to "Applicants and updates",
                "My Helper" to "Assigned work",
                "Messages" to "Open active chats",
                "History" to "Past errands",
                "Profile" to "Ratings and account",
                "Report" to "Safety concern"
            ),
            listOf(
                { screen = Screen.PostErrand },
                { screen = Screen.BrowseErrands },
                { screen = Screen.MyPosted },
                { screen = Screen.MyHelper },
                { screen = Screen.MessageHub },
                { screen = Screen.History },
                { screen = Screen.Profile },
                {
                    selectedErrand = null
                    screen = Screen.Report
                }
            )
        )
        InfoPanel("Errands must be school-related, safe, and limited to campus or nearby MCL locations.")
        CampusButton("Logout", primary = false) { logout() }
    }

    @Composable
    private fun PostErrandScreen() {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var category by remember { mutableStateOf(categories.first()) }
        var pickup by remember { mutableStateOf("") }
        var dropoff by remember { mutableStateOf("") }
        var deadline by remember { mutableStateOf("2026-07-15 13:00:00") }
        var reward by remember { mutableStateOf("") }
        var rewardNote by remember { mutableStateOf("") }

        InfoPanel("Banned errands include alcohol, vape products, medicines, confidential documents, answer sheets, IDs, weapons, restricted areas, and tasks that violate school rules.")
        CampusTextField("Title", title, { title = it })
        CampusTextField("Description", description, { description = it }, multiline = true)
        CategoryDropdown(category) { category = it }
        CampusTextField("Pickup location", pickup, { pickup = it })
        CampusTextField("Drop-off location", dropoff, { dropoff = it })
        CampusTextField("Deadline: 2026-07-15 13:00:00", deadline, { deadline = it })
        CampusTextField("Reward amount, optional", reward, { reward = it }, KeyboardType.Decimal)
        CampusTextField("Reward note, optional", rewardNote, { rewardNote = it })
        CampusButton("Submit Errand") {
            val body = JSONObject().apply {
                put("requester_id", userId)
                put("title", title.trim())
                put("description", description.trim())
                put("category", category)
                put("pickup_location", pickup.trim())
                put("dropoff_location", dropoff.trim())
                put("deadline", deadline.trim())
                put("reward_note", rewardNote.trim())
                if (reward.trim().isNotEmpty()) put("reward_amount", reward.trim().toDoubleOrNull() ?: 0.0)
            }
            api.post("create_errand.php", body) { response ->
                toast(response.optString("message"))
                if (response.optBoolean("success")) {
                    refreshKey++
                    screen = Screen.MyPosted
                }
            }
        }
        BackButton()
    }

    @Composable
    private fun BrowseErrandsScreen() {
        var keyword by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("All Categories") }
        var appliedKeyword by remember { mutableStateOf("") }
        var appliedCategory by remember { mutableStateOf("") }

        SectionTitle("Errand Feed")
        CampusTextField("Search keyword or location", keyword, { keyword = it })
        CategoryDropdown(category, includeAll = true) { category = it }
        CampusButton("Apply Filters") {
            appliedKeyword = keyword.trim()
            appliedCategory = if (category == "All Categories") "" else category
            refreshKey++
        }
        RemoteList(
            endpoint = "get_available_errands.php",
            params = mapOf("user_id" to userId.toString(), "keyword" to appliedKeyword, "category" to appliedCategory),
            emptyText = "No available errands yet."
        ) { item ->
            ErrandCard(item, applyMode = true)
        }
        BackButton()
    }

    @Composable
    private fun ErrandDetailsScreen(errand: JSONObject?, applyMode: Boolean) {
        if (errand == null) {
            EmptyState("No errand selected.")
            BackButton()
            return
        }
        CampusCard {
            ErrandCore(errand)
            SectionTitle("Safety Scope")
            Text("Only accept if the request is safe, school-related, and limited to campus or nearby MCL locations.", color = TextSecondary)
        }
        SectionTitle("Status Tracker")
        StatusTracker(errand.optString("status"))
        if (applyMode) {
            CampusButton("Apply as Helper") {
                selectedErrand = errand
                screen = Screen.Apply
            }
        } else {
            RequesterActions(errand)
        }
        CampusButton("Report Errand", primary = false) {
            selectedErrand = errand
            screen = Screen.Report
        }
        BackButton()
    }

    @Composable
    private fun ApplyScreen(errand: JSONObject?) {
        if (errand == null) {
            EmptyState("No errand selected.")
            BackButton()
            return
        }
        var offer by remember { mutableStateOf("") }
        var estimate by remember { mutableStateOf("") }

        HelperPreviewPanel(
            "Your helper profile shown to requester",
            fullName,
            "Average rating and completed errands will be loaded from your account record.",
            "Offer note and estimated completion time are public to this requester only."
        )
        CampusTextField("Offer note", offer, { offer = it }, multiline = true)
        CampusTextField("Estimated completion time, example: 30 minutes", estimate, { estimate = it })
        CampusButton("Submit Application") {
            api.post("apply_to_errand.php", JSONObject().apply {
                put("errand_id", errand.optInt("errand_id"))
                put("helper_id", userId)
                put("offer_note", offer.trim())
                put("estimated_completion_time", estimate.trim())
            }) { response ->
                toast(response.optString("message"))
                if (response.optBoolean("success")) {
                    refreshKey++
                    screen = Screen.BrowseErrands
                }
            }
        }
        CampusButton("Back", primary = false) { screen = Screen.ErrandDetails }
    }

    @Composable
    private fun MyPostedScreen() {
        var selectedTab by remember { mutableStateOf("Active") }
        TabStrip(
            tabs = listOf("Active", "Completed", "Cancelled"),
            selected = selectedTab,
            onSelected = { selectedTab = it }
        )
        RemoteList(
            endpoint = "get_my_posted_errands.php",
            params = mapOf("user_id" to userId.toString()),
            emptyText = "You have not posted errands yet."
        ) { item ->
            if (matchesErrandTab(item.optString("status"), selectedTab)) {
                ErrandCard(item, applyMode = false)
            }
        }
        BackButton()
    }

    @Composable
    private fun MyHelperScreen() {
        RemoteList(
            endpoint = "get_my_helper_errands.php",
            params = mapOf("user_id" to userId.toString()),
            emptyText = "You have no helper errands yet."
        ) { errand ->
            CampusCard {
                Text(errand.optString("title"), color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Badge(errand.optString("status"), colorForStatus(errand.optString("status")))
                Text(
                    errand.optString("category") + "\n" +
                        errand.optString("pickup_location") + " -> " + errand.optString("dropoff_location") + "\n" +
                        "Application: " + errand.optString("application_status"),
                    color = TextSecondary
                )
                HelperActions(errand)
            }
        }
        BackButton()
    }

    @Composable
    private fun ApplicantsScreen(errand: JSONObject?) {
        if (errand == null) {
            EmptyState("No errand selected.")
            BackButton()
            return
        }
        RemoteList(
            endpoint = "get_errand_applicants.php",
            params = mapOf("errand_id" to errand.optInt("errand_id").toString(), "requester_id" to userId.toString()),
            emptyText = "No applicants yet."
        ) { applicant ->
            ApplicantCard(applicant, errand.optInt("errand_id"))
        }
        CampusButton("Back", primary = false) { screen = Screen.MyPosted }
    }

    @Composable
    private fun HelperProfileScreen(applicant: JSONObject?) {
        if (applicant == null) {
            EmptyState("No helper selected.")
        } else {
            HelperPreviewPanel(
                "Public helper card",
                applicant.optString("helper_name"),
                "Rating: ${applicant.optString("average_rating", "No rating yet")}\nCompleted errands: ${applicant.optString("completed_errands", "0")}",
                "Offer: ${applicant.optString("offer_note")}\nEstimate: ${applicant.optString("estimated_completion_time")}"
            )
            InfoPanel("Privacy rule: student numbers and school email addresses are not displayed on public helper cards.")
        }
        CampusButton("Back", primary = false) { screen = Screen.Applicants }
    }

    @Composable
    private fun StatusScreen(errand: JSONObject?) {
        if (errand == null) {
            EmptyState("No errand selected.")
            BackButton()
            return
        }
        StatusTracker(errand.optString("status"))
        CampusCard {
            Text("Current status", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Badge(errand.optString("status", "Open"), colorForStatus(errand.optString("status")))
            Text("Use the available action buttons below to move the errand through the prototype status flow.", color = TextSecondary)
        }
        if (errand.optInt("requester_id") == userId) RequesterActions(errand) else CampusCard { HelperActions(errand) }
        BackButton()
    }

    @Composable
    private fun MessageHubScreen() {
        TabStrip(
            tabs = listOf("All", "Errands", "System"),
            selected = "All",
            onSelected = {}
        )
        InfoPanel("Messaging is connected to a specific errand and is only visible to the requester and selected helper.")
        MessageRouteCard("My Posted Errand Chats", "Open chats for errands where you selected a helper.", "Requester") {
            screen = Screen.MyPosted
        }
        MessageRouteCard("My Helper Errand Chats", "Open chats for errands assigned to you as helper.", "Helper") {
            screen = Screen.MyHelper
        }
        BackButton()
    }

    @Composable
    private fun ChatScreen(errand: JSONObject?) {
        if (errand == null) {
            EmptyState("No errand selected.")
            BackButton()
            return
        }
        val requesterId = errand.optInt("requester_id")
        val helperId = errand.optInt("selected_helper_id")
        val receiverId = if (userId == requesterId) helperId else requesterId
        var message by remember { mutableStateOf("") }

        RemoteList(
            endpoint = "get_messages.php",
            params = mapOf("errand_id" to errand.optInt("errand_id").toString(), "user_id" to userId.toString()),
            emptyText = "No messages yet."
        ) { item ->
            ChatBubble(
                sender = item.optString("sender_name"),
                message = item.optString("message_text"),
                mine = item.optInt("sender_id") == userId
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                CampusTextField("Message", message, { message = it }, multiline = true)
            }
            CampusButton("Send", modifier = Modifier.width(92.dp)) {
                api.post("send_message.php", JSONObject().apply {
                    put("errand_id", errand.optInt("errand_id"))
                    put("sender_id", userId)
                    put("receiver_id", receiverId)
                    put("message_text", message.trim())
                }) { response ->
                    toast(response.optString("message"))
                    if (response.optBoolean("success")) {
                        message = ""
                        refreshKey++
                    }
                }
            }
        }
        BackButton()
    }

    @Composable
    private fun CompletionScreen(errand: JSONObject?) {
        if (errand == null) {
            EmptyState("No errand selected.")
            BackButton()
            return
        }
        InfoPanel("Confirm only after the helper has completed the agreed errand. This moves the errand to requester-confirmed status.")
        StatusTracker(errand.optString("status"))
        CampusButton("Confirm Completion") {
            postStatus("confirm_completion.php", errand, "requester_id", "Confirmed by Requester")
        }
        CampusButton("Back", primary = false) { screen = Screen.MyPosted }
    }

    @Composable
    private fun RatingScreen(errand: JSONObject?) {
        if (errand == null) {
            EmptyState("No errand selected.")
            BackButton()
            return
        }
        var score by remember { mutableStateOf("") }
        var feedback by remember { mutableStateOf("") }

        CampusTextField("Rating 1-5", score, { score = it }, KeyboardType.Number)
        CampusTextField("Feedback", feedback, { feedback = it }, multiline = true)
        CampusButton("Submit Rating") {
            api.post("submit_rating.php", JSONObject().apply {
                put("errand_id", errand.optInt("errand_id"))
                put("rated_user_id", errand.optInt("selected_helper_id"))
                put("rated_by_user_id", userId)
                put("rating_score", score.toIntOrNull() ?: 0)
                put("feedback", feedback.trim())
            }) { response ->
                toast(response.optString("message"))
                if (response.optBoolean("success")) {
                    refreshKey++
                    screen = Screen.MyPosted
                }
            }
        }
        CampusButton("Back", primary = false) { screen = Screen.MyPosted }
    }

    @Composable
    private fun ReportScreen(errand: JSONObject?) {
        var reportedUserId by remember { mutableStateOf("") }
        var reason by remember { mutableStateOf("Unsafe or prohibited request") }
        var details by remember { mutableStateOf("") }
        val reportReasons = listOf(
            "Unsafe or prohibited request",
            "Restricted area",
            "Harassment or threat",
            "Privacy concern",
            "School-rule violation",
            "Other safety concern"
        )

        InfoPanel("Use reports for unsafe behavior, banned errands, restricted areas, harassment, privacy concerns, or school-rule violations.")
        if (errand == null) {
            CampusTextField("Reported user ID", reportedUserId, { reportedUserId = it }, KeyboardType.Number)
            GenericDropdown("Reason for reporting", reason, reportReasons) { reason = it }
            CampusTextField("Details", details, { details = it }, multiline = true)
            DangerButton("Submit User Report") {
                api.post("report_user.php", JSONObject().apply {
                    put("reported_user_id", reportedUserId.toIntOrNull() ?: 0)
                    put("reported_by_user_id", userId)
                    put("reason", reason.trim())
                    put("details", details.trim())
                }) { response ->
                    toast(response.optString("message"))
                    if (response.optBoolean("success")) screen = Screen.Dashboard
                }
            }
        } else {
            GenericDropdown("Reason for reporting", reason, reportReasons) { reason = it }
            CampusTextField("Additional details, optional", details, { details = it }, multiline = true)
            DangerButton("Submit Report") {
                api.post("report_errand.php", JSONObject().apply {
                    put("errand_id", errand.optInt("errand_id"))
                    put("reporter_id", userId)
                    put("reason", (reason + if (details.isBlank()) "" else ": ${details.trim()}").trim())
                }) { response ->
                    toast(response.optString("message"))
                    if (response.optBoolean("success")) screen = Screen.Dashboard
                }
            }
        }
        BackButton()
    }

    @Composable
    private fun ProfileScreen() {
        RemoteObject(
            endpoint = "get_user_profile.php",
            params = mapOf("user_id" to userId.toString()),
            emptyText = "Profile unavailable."
        ) { data ->
            ProfileHeader(data)
        }
        MenuRow("Edit Profile", "Update your display name") { screen = Screen.EditProfile }
        MenuRow("Privacy & Safety", "Your email and student number stay hidden publicly") { }
        MenuRow("History", "Review completed and cancelled errands") { screen = Screen.History }
        BackButton()
    }

    @Composable
    private fun EditProfileScreen() {
        var name by remember { mutableStateOf(fullName) }
        InfoPanel("Only safe profile fields are editable in this prototype. School email and student number stay private verification records.")
        CampusTextField("Full name", name, { name = it })
        CampusButton("Save Profile") {
            api.post("update_user_profile.php", JSONObject().apply {
                put("user_id", userId)
                put("full_name", name.trim())
            }) { response ->
                toast(response.optString("message"))
                if (response.optBoolean("success")) {
                    fullName = name.trim()
                    prefs.edit().putString("full_name", fullName).apply()
                    screen = Screen.Profile
                }
            }
        }
        CampusButton("Back", primary = false) { screen = Screen.Profile }
    }

    @Composable
    private fun HistoryScreen() {
        var selectedTab by remember { mutableStateOf("All") }
        TabStrip(
            tabs = listOf("All", "Posted", "As Helper"),
            selected = selectedTab,
            onSelected = { selectedTab = it }
        )
        RemoteList(
            endpoint = "get_user_history.php",
            params = mapOf("user_id" to userId.toString()),
            emptyText = "No history records yet."
        ) { item ->
            AdminRecordCard(item, "History Record")
        }
        CampusButton("Ratings") { screen = Screen.UserRatings }
        BackButton()
    }

    @Composable
    private fun UserRatingsScreen() {
        RemoteList(
            endpoint = "get_user_ratings.php",
            params = mapOf("user_id" to userId.toString()),
            emptyText = "No ratings yet."
        ) { item ->
            AdminRecordCard(item, "Feedback")
        }
        CampusButton("Back", primary = false) { screen = Screen.History }
    }

    @Composable
    private fun CancelErrandScreen(errand: JSONObject?) {
        if (errand == null) {
            EmptyState("No errand selected.")
            BackButton()
            return
        }
        var reason by remember { mutableStateOf("") }
        InfoPanel("Cancellation is allowed before in-progress work. If the errand is already In Progress, a reason is required and saved in the status logs.")
        ErrandCoreCard(errand)
        CampusTextField("Cancellation reason", reason, { reason = it }, multiline = true)
        CampusButton("Cancel Errand") {
            api.post("cancel_errand.php", JSONObject().apply {
                put("errand_id", errand.optInt("errand_id"))
                put("user_id", userId)
                put("reason", reason.trim())
            }) { response ->
                toast(response.optString("message"))
                if (response.optBoolean("success")) {
                    refreshKey++
                    screen = Screen.Dashboard
                }
            }
        }
        BackButton()
    }

    @Composable
    private fun AdminDashboardScreen() {
        RemoteObject(
            endpoint = "admin_dashboard.php",
            params = mapOf("admin_id" to userId.toString()),
            emptyText = "Dashboard unavailable."
        ) { data ->
            SummaryGrid(
                listOf(
                    "Total Users" to data.optInt("total_users").toString(),
                    "Verified" to data.optInt("verified_users").toString(),
                    "Open" to data.optInt("open_errands").toString(),
                    "Active" to data.optInt("active_errands").toString(),
                    "Completed" to data.optInt("completed_errands").toString(),
                    "Cancelled" to data.optInt("cancelled_errands").toString(),
                    "Reported" to data.optInt("reported_errands").toString(),
                    "Flagged" to data.optInt("flagged_errands").toString()
                )
            )
        }
        SectionTitle("Admin Tools")
        ActionGrid(
            listOf(
                "Manage Users" to "Search, restrict, deactivate",
                "Manage Errands" to "View and remove records",
                "Reports" to "Review and resolve",
                "Flagged Errands" to "Moderation queue",
                "Ratings" to "Feedback review",
                "Moderation" to "Rule-based logs"
            ),
            listOf(
                { openAdminArray("admin_get_users.php", "Manage Users") },
                { openAdminArray("admin_get_errands.php", "Manage Errands") },
                { openAdminArray("admin_get_reports.php", "Reports") },
                { openAdminArray("admin_get_flagged_errands.php", "Flagged Errands") },
                { screen = Screen.AdminRatings },
                { openAdminArray("admin_get_flagged_errands.php", "Moderation Logs") }
            )
        )
        CampusButton("Student Dashboard", primary = false) {
            role = "student"
            screen = Screen.Dashboard
        }
        CampusButton("Logout", primary = false) { logout() }
    }

    @Composable
    private fun AdminArrayScreen(endpoint: String, title: String) {
        if (title == "Manage Users") {
            var keyword by remember { mutableStateOf("") }
            CampusTextField("Search displayed user records", keyword, { keyword = it })
            InfoPanel("The current backend returns all users. This search field is a UI placeholder until server-side filtering is added.")
        }
        if (title == "Moderation Logs") {
            InfoPanel("The backend does not currently have a moderation log endpoint, so this screen reuses flagged errands for the prototype.")
        }
        RemoteList(
            endpoint = endpoint,
            params = mapOf("admin_id" to userId.toString()),
            emptyText = "No records."
        ) { item ->
            AdminRecordCard(item, title)
        }
        CampusButton("Back", primary = false) { screen = Screen.AdminDashboard }
    }

    @Composable
    private fun AdminRecordDetailsScreen(item: JSONObject?, title: String) {
        if (item == null) {
            EmptyState("No record selected.")
            CampusButton("Back", primary = false) { screen = Screen.AdminDashboard }
            return
        }
        AdminRecordCard(item, title, clickable = false)
        if (item.has("user_id")) {
            CampusButton("Restrict User") { updateUserStatus(item.optInt("user_id"), "restricted") }
            CampusButton("Deactivate User", primary = false) { updateUserStatus(item.optInt("user_id"), "deactivated") }
        }
        if (item.has("errand_id")) {
            CampusButton("Remove Errand", primary = false) { removeErrand(item.optInt("errand_id")) }
        }
        if (item.has("report_id")) {
            CampusButton("Resolve Report") { resolveReport(item.optInt("report_id")) }
        }
        CampusButton("Back", primary = false) { screen = Screen.AdminDashboard }
    }

    @Composable
    private fun AdminRatingsScreen() {
        InfoPanel("This prototype keeps user ratings available through profile and history records. Add a backend endpoint for all ratings if the admin needs a full feedback export.")
        CampusButton("View Errand Records") { openAdminArray("admin_get_errands.php", "Ratings and Feedback") }
        CampusButton("Back", primary = false) { screen = Screen.AdminDashboard }
    }

    @Composable
    private fun RemoteList(
        endpoint: String,
        params: Map<String, String>,
        emptyText: String,
        itemContent: @Composable (JSONObject) -> Unit
    ) {
        var loading by remember(endpoint, params.toString(), refreshKey) { mutableStateOf(true) }
        var error by remember(endpoint, params.toString(), refreshKey) { mutableStateOf("") }
        var data by remember(endpoint, params.toString(), refreshKey) { mutableStateOf(JSONArray()) }

        LaunchedEffect(endpoint, params.toString(), refreshKey) {
            loading = true
            api.get(endpoint, HashMap(params)) { response ->
                loading = false
                if (!response.optBoolean("success")) {
                    error = response.optString("message")
                    data = JSONArray()
                } else {
                    error = ""
                    data = response.optJSONArray("data") ?: JSONArray()
                }
            }
        }

        when {
            loading -> InfoPanel("Loading records...")
            error.isNotBlank() -> InfoPanel(error)
            data.length() == 0 -> EmptyState(emptyText)
            else -> {
                for (i in 0 until data.length()) {
                    data.optJSONObject(i)?.let { itemContent(it) }
                }
            }
        }
    }

    @Composable
    private fun RemoteObject(
        endpoint: String,
        params: Map<String, String>,
        emptyText: String,
        content: @Composable (JSONObject) -> Unit
    ) {
        var loading by remember(endpoint, params.toString(), refreshKey) { mutableStateOf(true) }
        var error by remember(endpoint, params.toString(), refreshKey) { mutableStateOf("") }
        var data by remember(endpoint, params.toString(), refreshKey) { mutableStateOf<JSONObject?>(null) }

        LaunchedEffect(endpoint, params.toString(), refreshKey) {
            loading = true
            api.get(endpoint, HashMap(params)) { response ->
                loading = false
                if (!response.optBoolean("success")) {
                    error = response.optString("message")
                    data = null
                } else {
                    error = ""
                    data = response.optJSONObject("data")
                }
            }
        }

        when {
            loading -> InfoPanel("Loading records...")
            error.isNotBlank() -> InfoPanel(error)
            data == null -> EmptyState(emptyText)
            else -> content(data!!)
        }
    }

    @Composable
    private fun ErrandCard(errand: JSONObject, applyMode: Boolean) {
        CampusCard {
            ErrandCore(errand)
            CampusButton("View Details") {
                openErrandScreen(errand, Screen.ErrandDetails)
            }
            if (applyMode) {
                CampusButton("Apply as Helper", primary = false) {
                    openErrandScreen(errand, Screen.Apply)
                }
            } else {
                val status = errand.optString("status")
                if (status == "Has Applicants" || status == "Open") {
                    CampusButton("View Applicants") {
                        openErrandScreen(errand, Screen.Applicants)
                    }
                }
                if (errand.optInt("selected_helper_id") > 0) {
                    CampusButton("Messages", primary = false) {
                        openErrandScreen(errand, Screen.Chat)
                    }
                }
                if (status == "Completed by Helper") {
                    CampusButton("Confirm Completion") {
                        openErrandScreen(errand, Screen.Completion)
                    }
                }
                if (status == "Confirmed by Requester") {
                    CampusButton("Rate Helper") {
                        openErrandScreen(errand, Screen.Rating)
                    }
                }
                if (status in listOf("Open", "Has Applicants", "Assigned", "Accepted", "In Progress")) {
                    CampusButton("Cancel Errand", primary = false) {
                        openErrandScreen(errand, Screen.CancelErrand)
                    }
                }
                CampusButton("Status Tracker", primary = false) {
                    openErrandScreen(errand, Screen.Status)
                }
            }
        }
    }

    @Composable
    private fun ErrandCoreCard(errand: JSONObject) {
        CampusCard { ErrandCore(errand) }
    }

    @Composable
    private fun ErrandCore(errand: JSONObject) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                errand.optString("title", "Untitled"),
                modifier = Modifier.weight(1f),
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Badge(errand.optString("status", "Open"), colorForStatus(errand.optString("status")))
        }
        Badge("${categoryIcon(errand.optString("category"))} ${errand.optString("category")}", SecondaryBlue)
        DetailRow("Pickup", errand.optString("pickup_location"))
        DetailRow("Drop-off", errand.optString("dropoff_location"))
        DetailRow("Deadline", errand.optString("deadline"))
        DetailRow("Reward", rewardText(errand))
        DetailRow("Requester", errand.optString("requester_name", "Student"))
        DetailRow("Rating", errand.optString("requester_rating", "No rating yet"))
    }

    @Composable
    private fun RequesterActions(errand: JSONObject) {
        val status = errand.optString("status")
        if (status == "Has Applicants" || status == "Open") {
            CampusButton("View Applicants") {
                selectedErrand = errand
                screen = Screen.Applicants
            }
        }
        if (errand.optInt("selected_helper_id") > 0) {
            CampusButton("Messages", primary = false) {
                selectedErrand = errand
                screen = Screen.Chat
            }
        }
        if (status == "Completed by Helper") {
            CampusButton("Confirm Completion") {
                selectedErrand = errand
                screen = Screen.Completion
            }
        }
        if (status == "Confirmed by Requester") {
            CampusButton("Rate Helper") {
                selectedErrand = errand
                screen = Screen.Rating
            }
        }
        if (status in listOf("Open", "Has Applicants", "Assigned", "Accepted", "In Progress")) {
            CampusButton("Cancel Errand", primary = false) {
                selectedErrand = errand
                screen = Screen.CancelErrand
            }
        }
    }

    @Composable
    private fun HelperActions(errand: JSONObject) {
        val status = errand.optString("status")
        if (errand.optString("application_status") == "selected" && status == "Assigned") {
            CampusButton("Accept") { postStatus("accept_assigned_errand.php", errand, "helper_id", "Accepted") }
        }
        if (status == "Accepted") {
            CampusButton("Mark In Progress") { updateStatus(errand, "In Progress") }
        }
        if (status == "In Progress") {
            CampusButton("Mark Completed") { updateStatus(errand, "Completed by Helper") }
        }
        if (errand.optInt("selected_helper_id") == userId || errand.optString("application_status") == "selected") {
            CampusButton("Messages", primary = false) {
                selectedErrand = errand
                screen = Screen.Chat
            }
            CampusButton("Cancel/Withdraw", primary = false) {
                selectedErrand = errand
                screen = Screen.CancelErrand
            }
        }
        CampusButton("Status Tracker", primary = false) {
            selectedErrand = errand
            screen = Screen.Status
        }
    }

    @Composable
    private fun ApplicantCard(applicant: JSONObject, errandId: Int) {
        CampusCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    applicant.optString("helper_name"),
                    modifier = Modifier.weight(1f),
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Badge(applicant.optString("status", "pending"), colorForStatus(applicant.optString("status")))
            }
            DetailRow("Rating", applicant.optString("average_rating", "No rating yet"))
            DetailRow("Completed", applicant.optString("completed_errands", "0") + " errands")
            DetailRow("Offer", applicant.optString("offer_note"))
            DetailRow("Estimate", applicant.optString("estimated_completion_time"))
            CampusButton("Helper Profile", primary = false) {
                openApplicantScreen(applicant)
            }
            if (applicant.optString("status") == "pending") {
                CampusButton("Select Helper") {
                    selectHelper(applicant, errandId)
                }
            }
        }
    }

    @Composable
    private fun AdminRecordCard(item: JSONObject, title: String, clickable: Boolean = true) {
        CampusCard {
            Text(title, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(prettyJson(item), color = TextSecondary)
            if (clickable) {
                CampusButton("View") {
                    openAdminRecord(item, title)
                }
            }
        }
    }

    @Composable
    private fun GreetingCard() {
        CampusCard(background = CardLight) {
            Badge("Verified school-only prototype", SecondaryBlue)
            Text("Hello, ${firstName(fullName).ifBlank { "Student" }}", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Manage campus errands, helper applications, messages, completion, ratings, and safety reports from one dashboard.", color = TextSecondary)
        }
    }

    @Composable
    private fun HelperPreviewPanel(eyebrow: String, name: String, stats: String, note: String) {
        CampusCard {
            Badge(eyebrow, SecondaryBlue)
            Text(name.ifBlank { "Student Helper" }, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(stats, color = TextSecondary)
            Text(note, color = TextSecondary)
        }
    }

    @Composable
    private fun ProfileHeader(data: JSONObject) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(PrimaryBlue)
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile",
                    tint = TextSecondary,
                    modifier = Modifier.size(70.dp)
                )
            }
            Text(data.optString("full_name", fullName), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Badge("Verified Prototype", Color.White, fill = Color(0xFF0A63D8), border = Color.White)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ProfileStat("Completed", data.optString("completed_errands", "0"), Modifier.weight(1f))
                ProfileStat("Rating", data.optString("average_rating", "0.00"), Modifier.weight(1f))
                ProfileStat("Account", data.optString("account_status", "active"), Modifier.weight(1f))
            }
        }
    }

    @Composable
    private fun ProfileStat(label: String, value: String, modifier: Modifier = Modifier) {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = PrimaryBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(label, color = TextSecondary, fontSize = 10.sp)
        }
    }

    @Composable
    private fun MenuRow(title: String, subtitle: String, action: () -> Unit) {
        CampusCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { action() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Filled.Assignment, contentDescription = title, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(subtitle, color = TextSecondary, fontSize = 12.sp)
                }
                Text(">", color = TextSecondary, fontWeight = FontWeight.Bold)
            }
        }
    }

    @Composable
    private fun ChatBubble(sender: String, message: String, mine: Boolean) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start) {
            Column(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (mine) PrimaryBlue else Color.White)
                    .border(1.dp, if (mine) PrimaryBlue else BorderSoft, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(if (mine) "You" else sender, color = if (mine) Color.White else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(message, color = if (mine) Color.White else TextPrimary, fontSize = 14.sp)
            }
        }
    }

    @Composable
    private fun StatusTracker(currentStatus: String) {
        val currentIndex = statusSteps.indexOf(currentStatus)
        CampusCard {
            statusSteps.forEachIndexed { index, step ->
                val color = when {
                    currentIndex >= 0 && index < currentIndex -> SuccessGreen
                    index == currentIndex -> PrimaryBlue
                    else -> TextSecondary
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(if (index <= currentIndex && currentIndex >= 0) color else Color.White)
                                .border(2.dp, color, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (index < currentIndex) {
                                Text("✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        if (index < statusSteps.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(22.dp)
                                    .background(if (index < currentIndex) SuccessGreen else BorderSoft)
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(step, color = color, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(if (index <= currentIndex && currentIndex >= 0) "Updated in current workflow" else "Pending", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
            InfoPanel("You will be notified at every update.")
        }
    }

    @Composable
    private fun SummaryGrid(items: List<Pair<String, String>>) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.chunked(2).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    rowItems.forEach { item ->
                        CampusCard(modifier = Modifier.weight(1f)) {
                            Text(item.second, color = PrimaryBlue, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text(item.first, color = TextSecondary, fontSize = 13.sp)
                        }
                    }
                    if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }

    @Composable
    private fun ActionGrid(items: List<Pair<String, String>>, actions: List<() -> Unit>) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.chunked(2).forEachIndexed { rowIndex, rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    rowItems.forEachIndexed { itemIndex, item ->
                        val actionIndex = rowIndex * 2 + itemIndex
                        ActionTile(item.first, item.second, actions[actionIndex], Modifier.weight(1f))
                    }
                    if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }

    @Composable
    private fun TabStrip(tabs: List<String>, selected: String, onSelected: (String) -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .border(1.dp, BorderSoft, RoundedCornerShape(8.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tabs.forEach { tab ->
                val active = tab == selected
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (active) Color(0xFFEAF3FF) else Color.Transparent)
                        .clickable { onSelected(tab) }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(tab, color = if (active) PrimaryBlue else TextSecondary, fontSize = 12.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }
    }

    @Composable
    private fun MessageRouteCard(title: String, subtitle: String, badge: String, action: () -> Unit) {
        CampusCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEAF3FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ChatBubble, contentDescription = title, tint = PrimaryBlue, modifier = Modifier.size(22.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(subtitle, color = TextSecondary, fontSize = 12.sp)
                }
                Badge(badge, SecondaryBlue)
            }
            CampusButton("Open", primary = false, onClick = action)
        }
    }

    @Composable
    private fun ActionTile(title: String, subtitle: String, action: () -> Unit, modifier: Modifier = Modifier) {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .border(1.dp, BorderSoft, RoundedCornerShape(8.dp))
                .clickable { action() }
                .height(104.dp)
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = actionIcon(title),
                contentDescription = title,
                tint = iconColor(title),
                modifier = Modifier.size(26.dp)
            )
            Spacer(Modifier.height(6.dp))
            Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(subtitle, color = TextSecondary, fontSize = 10.sp)
        }
    }

    @Composable
    private fun CampusCard(
        modifier: Modifier = Modifier,
        background: Color = Color.White,
        content: @Composable ColumnScope.() -> Unit
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = background),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = content
            )
        }
    }

    @Composable
    private fun InfoPanel(text: String) {
        CampusCard(background = CardLight) {
            Text(text, color = TextSecondary, fontSize = 14.sp)
        }
    }

    @Composable
    private fun EmptyState(text: String) {
        CampusCard(background = CardLight) {
            Text(text, color = TextSecondary)
        }
    }

    @Composable
    private fun SectionTitle(text: String) {
        Text(text, color = PrimaryBlue, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }

    @Composable
    private fun AuthTitle(title: String, subtitle: String) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp, bottom = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(title, color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = TextSecondary, fontSize = 13.sp)
        }
    }

    @Composable
    private fun DetailRow(label: String, value: String?) {
        Column {
            Text(label.uppercase(Locale.US), color = SecondaryBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(
                if (value.isNullOrBlank() || value == "null") "Not specified" else value,
                color = TextPrimary,
                fontSize = 14.sp
            )
        }
    }

    @Composable
    private fun Badge(
        text: String,
        color: Color,
        fill: Color = CardLight,
        border: Color = color
    ) {
        Text(
            text = text.ifBlank { "Open" },
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(fill)
                .border(1.dp, border, RoundedCornerShape(18.dp))
                .padding(horizontal = 11.dp, vertical = 5.dp),
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }

    @Composable
    private fun CampusButton(
        text: String,
        primary: Boolean = true,
        modifier: Modifier = Modifier.fillMaxWidth(),
        onClick: () -> Unit
    ) {
        Button(
            onClick = onClick,
            modifier = modifier.height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (primary) PrimaryBlue else Color.White,
                contentColor = if (primary) Color.White else PrimaryBlue
            ),
            border = if (primary) null else ButtonDefaults.outlinedButtonBorder
        ) {
            Text(text, fontWeight = FontWeight.Bold)
        }
    }

    @Composable
    private fun DangerButton(
        text: String,
        modifier: Modifier = Modifier.fillMaxWidth(),
        onClick: () -> Unit
    ) {
        Button(
            onClick = onClick,
            modifier = modifier.height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DangerRed,
                contentColor = Color.White
            )
        ) {
            Text(text, fontWeight = FontWeight.Bold)
        }
    }

    @Composable
    private fun BackButton() {
        CampusButton("Back", primary = false) { navigateBack() }
    }

    @Composable
    private fun CampusTextField(
        label: String,
        value: String,
        onValueChange: (String) -> Unit,
        keyboardType: KeyboardType = KeyboardType.Text,
        password: Boolean = false,
        multiline: Boolean = false
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            minLines = if (multiline) 3 else 1,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
            shape = RoundedCornerShape(8.dp)
        )
    }

    @Composable
    private fun StudentBottomNav() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Divider(color = BorderSoft)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem("Home", Icons.Filled.Home, screen == Screen.Dashboard) { screen = Screen.Dashboard }
                BottomNavItem("Messages", Icons.Filled.ChatBubble, screen == Screen.MessageHub || screen == Screen.Chat) { screen = Screen.MessageHub }
                BottomNavItem("History", Icons.Filled.History, screen == Screen.History || screen == Screen.UserRatings) { screen = Screen.History }
                BottomNavItem("Profile", Icons.Filled.AccountCircle, screen == Screen.Profile || screen == Screen.EditProfile) { screen = Screen.Profile }
            }
        }
    }

    @Composable
    private fun BottomNavItem(label: String, icon: ImageVector, selected: Boolean, action: () -> Unit) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { action() }
                .padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) PrimaryBlue else TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Text(label, color = if (selected) PrimaryBlue else TextSecondary, fontSize = 10.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun CategoryDropdown(value: String, includeAll: Boolean = false, onChange: (String) -> Unit) {
        val options = if (includeAll) listOf("All Categories") + categories else categories
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun GenericDropdown(label: String, value: String, options: List<String>, onChange: (String) -> Unit) {
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                label = { Text(label) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }

    private fun headerText(): Pair<String, String> {
        return when (screen) {
            Screen.Splash -> "Malayan Quest" to "School-only campus errands for MCL students"
            Screen.Login -> "Malayan Quest" to "Login to the campus errand prototype"
            Screen.Register -> "Register" to "Create a verified prototype student account"
            Screen.Dashboard -> if (role == "admin") "Admin Dashboard" to "Monitoring and moderation" else "Malayan Quest" to (if (fullName.isBlank()) "Student dashboard" else "Welcome, $fullName")
            Screen.PostErrand -> "Post Errand" to "Campus and near-campus errands only"
            Screen.BrowseErrands -> "Browse Errands" to "Open errands from other students"
            Screen.ErrandDetails -> "Errand Details" to selectedErrand.optTitle()
            Screen.Apply -> "Apply as Helper" to selectedErrand.optTitle()
            Screen.MyPosted -> "My Posted Errands" to "Requester workflow"
            Screen.MyHelper -> "My Helper Errands" to "Applications and assigned errands"
            Screen.Applicants -> "Applicants" to "Choose one helper"
            Screen.HelperProfile -> "Helper Profile Preview" to (selectedApplicant?.optString("helper_name") ?: "")
            Screen.Status -> "Errand Status" to selectedErrand.optTitle()
            Screen.MessageHub -> "Messages" to "Chats open after a helper is selected"
            Screen.Chat -> "Messages" to selectedErrand.optTitle()
            Screen.Completion -> "Completion Confirmation" to selectedErrand.optTitle()
            Screen.Rating -> "Rate Helper" to selectedErrand.optTitle()
            Screen.Report -> if (selectedErrand == null) "Report User" to "Safety and moderation" else "Report Errand" to "Safety and moderation"
            Screen.Profile -> "Profile" to "Account summary and privacy-safe public data"
            Screen.EditProfile -> "Edit Profile" to "Update account display details"
            Screen.History -> "History" to "Completed errands, ratings, and feedback"
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
            screen != Screen.Dashboard &&
            screen != Screen.AdminDashboard
    }

    private fun navigateBack() {
        screen = if (userId > 0) Screen.Dashboard else Screen.Login
    }

    private fun openAdminArray(endpoint: String, title: String) {
        adminEndpoint = endpoint
        adminTitle = title
        refreshKey++
        screen = Screen.AdminArray
    }

    private fun openErrandScreen(errand: JSONObject, target: Screen) {
        selectedErrand = errand
        screen = target
    }

    private fun openApplicantScreen(applicant: JSONObject) {
        selectedApplicant = applicant
        screen = Screen.HelperProfile
    }

    private fun openAdminRecord(item: JSONObject, title: String) {
        selectedErrand = item
        adminTitle = title
        screen = Screen.AdminRecordDetails
    }

    private fun selectHelper(applicant: JSONObject, errandId: Int) {
        api.post("select_helper.php", JSONObject().apply {
            put("errand_id", errandId)
            put("requester_id", userId)
            put("application_id", applicant.optInt("application_id"))
        }) { response ->
            toast(response.optString("message"))
            if (response.optBoolean("success")) {
                refreshKey++
                screen = Screen.MyPosted
            }
        }
    }

    private fun saveSession(data: JSONObject) {
        userId = data.optInt("user_id")
        fullName = data.optString("full_name")
        role = data.optString("role")
        prefs.edit()
            .putInt("user_id", userId)
            .putString("full_name", fullName)
            .putString("role", role)
            .apply()
    }

    private fun logout() {
        prefs.edit().clear().apply()
        userId = 0
        fullName = ""
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
                screen = Screen.MyHelper
            }
        }
    }

    private fun postStatus(endpoint: String, errand: JSONObject, userKey: String, successStatus: String) {
        api.post(endpoint, JSONObject().apply {
            put("errand_id", errand.optInt("errand_id"))
            put(userKey, userId)
        }) { response ->
            toast(response.optString("message"))
            if (response.optBoolean("success")) {
                errand.put("status", successStatus)
                refreshKey++
                screen = Screen.Dashboard
            }
        }
    }

    private fun updateUserStatus(targetUserId: Int, status: String) {
        api.post("admin_update_user_status.php", JSONObject().apply {
            put("admin_id", userId)
            put("user_id", targetUserId)
            put("verification_status", status)
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

    private fun rewardText(errand: JSONObject): String {
        val amount = errand.optString("reward_amount", "")
        val note = errand.optString("reward_note", "")
        return if (amount.isBlank() || amount == "null") {
            note.ifBlank { "None" }
        } else {
            "PHP $amount" + if (note.isBlank() || note == "null") "" else " - $note"
        }
    }

    private fun categoryIcon(category: String?): String {
        return when {
            category == null -> "[ ]"
            category.contains("Food", ignoreCase = true) -> "[Food]"
            category.contains("Printing", ignoreCase = true) -> "[Print]"
            category.contains("Document", ignoreCase = true) -> "[Doc]"
            category.contains("Bookstore", ignoreCase = true) || category.contains("Bluebook", ignoreCase = true) -> "[Book]"
            category.contains("Delivery", ignoreCase = true) -> "[Move]"
            else -> "[Task]"
        }
    }

    private fun colorForStatus(status: String?): Color {
        if (status == null) return PrimaryBlue
        return when {
            status.contains("Completed") || status.contains("Confirmed") || status.contains("Rated") || status.contains("Closed") -> SuccessGreen
            status.contains("Cancel") || status.contains("Reported") || status.contains("Removed") -> DangerRed
            status.contains("Flagged") || status.contains("pending") || status.contains("Has Applicants") -> WarningAmber
            else -> PrimaryBlue
        }
    }

    private fun matchesErrandTab(status: String?, tab: String): Boolean {
        val value = status.orEmpty()
        return when (tab) {
            "Completed" -> value.contains("Completed") || value.contains("Confirmed") || value.contains("Rated") || value.contains("Closed")
            "Cancelled" -> value.contains("Cancel")
            else -> !value.contains("Completed") && !value.contains("Confirmed") && !value.contains("Rated") && !value.contains("Closed") && !value.contains("Cancel")
        }
    }

    private fun firstName(value: String): String {
        val trimmed = value.trim()
        val space = trimmed.indexOf(" ")
        return if (space > 0) trimmed.substring(0, space) else trimmed
    }

    private fun actionLabel(title: String): String {
        return when {
            title.contains("Post") -> "CREATE"
            title.contains("Browse") -> "FIND"
            title.contains("Message") -> "CHAT"
            title.contains("Report") -> "SAFETY"
            title.contains("Profile") || title.contains("Rating") -> "ACCOUNT"
            title.contains("History") -> "RECORD"
            title.contains("Helper") -> "HELPER"
            title.contains("Posted") -> "REQUESTER"
            else -> "OPEN"
        }
    }

    private fun actionIcon(title: String): ImageVector {
        return when {
            title.contains("Post") -> Icons.Filled.AddCircle
            title.contains("Browse") -> Icons.Filled.Search
            title.contains("Message") -> Icons.Filled.ChatBubble
            title.contains("Report") -> Icons.Filled.Flag
            title.contains("Profile") || title.contains("Rating") -> Icons.Filled.AccountCircle
            title.contains("History") -> Icons.Filled.History
            title.contains("Helper") -> Icons.Filled.Person
            title.contains("Posted") -> Icons.Filled.Work
            title.contains("Errand") -> Icons.Filled.LocalShipping
            else -> Icons.Filled.Assignment
        }
    }

    private fun iconColor(title: String): Color {
        return when {
            title.contains("Report") -> DangerRed
            title.contains("Message") -> SuccessGreen
            title.contains("History") -> Color(0xFF4F46E5)
            title.contains("Helper") || title.contains("Profile") -> SecondaryBlue
            title.contains("Posted") -> WarningAmber
            else -> PrimaryBlue
        }
    }

    private fun prettyJson(item: JSONObject): String {
        val names = item.names() ?: return item.toString()
        val builder = StringBuilder()
        for (i in 0 until names.length()) {
            val key = names.optString(i)
            if (key == "student_number" || key == "school_email" || key == "password_hash") continue
            builder.append(key.replace("_", " ")).append(": ").append(item.optString(key)).append("\n")
        }
        return builder.toString().trim()
    }

    private companion object {
        val PrimaryBlue = Color(0xFF0B4EA2)
        val SecondaryBlue = Color(0xFF1976D2)
        val BackgroundSoft = Color(0xFFEEF5FB)
        val CardLight = Color(0xFFF4F8FC)
        val BorderSoft = Color(0xFFD7E6F5)
        val TextPrimary = Color(0xFF102A43)
        val TextSecondary = Color(0xFF6B7280)
        val SuccessGreen = Color(0xFF2E7D32)
        val WarningAmber = Color(0xFFF9A825)
        val DangerRed = Color(0xFFD32F2F)
    }
}
