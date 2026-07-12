package com.malayanquest.app

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
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
                .padding(start = 16.dp, end = 16.dp, top = 34.dp, bottom = 16.dp),
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
                Text(title, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                if (subtitle.isNotBlank()) {
                    Text(subtitle, color = TextSecondary, fontSize = 12.sp, textAlign = TextAlign.Center)
                }
            }
            Spacer(Modifier.size(40.dp))
        }
        Divider(color = BorderSoft)
    }

    @Composable
    private fun DashboardHeader() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(PrimaryBlue, Color(0xFF0A63D8))
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
            )
            Column(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 34.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painterResource(R.drawable.malayan_quest_logo),
                        contentDescription = "Malayan Quest",
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Malayan Quest", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    Badge("Verified Student", Color.White, fill = Color.White.copy(alpha = 0.16f), border = Color.White)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    UserAvatar(fullName, size = 54.dp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Hello, ${firstName(fullName).ifBlank { "Student" }}!",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("What campus errand can we help with today?", color = Color(0xFFDCEBFA), fontSize = 13.sp)
                    }
                }
            }
        }
    }

    @Composable
    private fun SplashScreen() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(660.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Brush.verticalGradient(listOf(PrimaryBlue, SecondaryBlue))),
        ) {
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .align(Alignment.TopEnd)
                    .padding(top = 20.dp, end = 12.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(painterResource(R.drawable.malayan_quest_logo), contentDescription = "Malayan Quest", modifier = Modifier.size(82.dp))
                }
                Text("Malayan Quest", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text("Campus errands, made easier.", color = Color(0xFFEAF3FF), fontSize = 16.sp, textAlign = TextAlign.Center)
                Badge("MCL student service app", Color.White, fill = Color.White.copy(alpha = 0.14f), border = Color.White)
            }
            androidx.compose.material3.LinearProgressIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 42.dp, vertical = 42.dp)
                    .clip(RoundedCornerShape(12.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.25f)
            )
        }
    }

    @Composable
    private fun LoginScreen() {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        AppMark()
        AuthTitle("Welcome back!", "Sign in to continue your campus errands.")
        Badge("For MCL students only", PrimaryBlue)
        CampusTextField("School email", email, { email = it }, KeyboardType.Email, leadingIcon = Icons.Filled.Email)
        CampusTextField("Password", password, { password = it }, KeyboardType.Password, password = true, leadingIcon = Icons.Filled.Lock)
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
        InfoPanel("Demo accounts use password: password\nAdmin: admin@mcl.edu.ph\nStudent: juan.dcruz@mcl.edu.ph, maria.santos@mcl.edu.ph, carlo.reyes@mcl.edu.ph", Icons.Filled.Info, "Prototype accounts")
    }

    @Composable
    private fun RegisterScreen() {
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var studentNumber by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirm by remember { mutableStateOf("") }

        AppMark()
        AuthTitle("Create Student Account", "Use your MCL student details to join Malayan Quest.")
        InfoPanel("Use your school email and student number. Student numbers and school emails are stored for verification and are not shown publicly.", Icons.Filled.Info, "Private verification")
        CampusTextField("Full name", name, { name = it }, leadingIcon = Icons.Filled.Person)
        CampusTextField("School email, example: student@mcl.edu.ph", email, { email = it }, KeyboardType.Email, leadingIcon = Icons.Filled.Email)
        CampusTextField("Student number", studentNumber, { studentNumber = it }, leadingIcon = Icons.Filled.Badge)
        CampusTextField("Password", password, { password = it }, KeyboardType.Password, password = true, leadingIcon = Icons.Filled.Lock)
        CampusTextField("Confirm password", confirm, { confirm = it }, KeyboardType.Password, password = true, leadingIcon = Icons.Filled.CheckCircle)
        InfoPanel("I agree to use Malayan Quest only for safe, school-related errands.", Icons.Filled.Security, "Safety agreement")
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
        SummaryGrid(
            listOf(
                "Open errands" to "5",
                "Active tasks" to "0",
                "Completed" to "0",
                "Rating" to "0.00"
            )
        )
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
        InfoPanel("Errands must be school-related, safe, and limited to campus or nearby MCL locations.", Icons.Filled.Security, "Campus Safety Reminder")
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

        InfoPanel("Describe a small, safe, school-related errand.", Icons.Filled.Security, "Campus-only request")
        SectionCard("Errand Details", Icons.Filled.Assignment) {
            CampusTextField("Title", title, { title = it }, leadingIcon = Icons.Filled.Assignment)
            CampusTextField("Description", description, { description = it }, multiline = true, leadingIcon = Icons.Filled.Info)
            CategoryDropdown(category) { category = it }
        }
        SectionCard("Locations", Icons.Filled.LocationOn) {
            CampusTextField("Pickup location", pickup, { pickup = it }, leadingIcon = Icons.Filled.LocationOn)
            CampusTextField("Drop-off location", dropoff, { dropoff = it }, leadingIcon = Icons.Filled.LocalShipping)
        }
        SectionCard("Time and Reward", Icons.Filled.Schedule) {
            CampusTextField("Deadline: 2026-07-15 13:00:00", deadline, { deadline = it }, leadingIcon = Icons.Filled.CalendarMonth)
            CampusTextField("Reward amount, optional", reward, { reward = it }, KeyboardType.Decimal, leadingIcon = Icons.Filled.Payments)
            CampusTextField("Reward note, optional", rewardNote, { rewardNote = it }, leadingIcon = Icons.Filled.Info)
        }
        InfoPanel("Do not post errands involving confidential documents, prohibited items, exams, IDs, medicine, or unsafe tasks.", Icons.Filled.Report, "Safety Reminder", DangerRed)
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
        CampusTextField("Search errands or locations", keyword, { keyword = it }, leadingIcon = Icons.Filled.Search)
        CategoryDropdown(category, includeAll = true) { category = it }
        FilterChipRow(listOf("All", "Food", "Printing", "Bluebook", "Delivery", "Supplies"), "All")
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
        ErrandCoreCard(errand)
        InfoPanel("Only accept if the request is safe, school-related, and limited to campus or nearby MCL locations.", Icons.Filled.Security, "Safety Note")
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

        ErrandCoreCard(errand)
        HelperPreviewPanel(
            "Your helper profile shown to requester",
            fullName,
            "Rating and completed errands are shown as trust indicators.",
            "Offer note and estimated completion time are public to this requester only."
        )
        SectionCard("Helper Offer", Icons.Filled.PersonSearch) {
            CampusTextField("Offer note, example: I can do this before 12:30 PM.", offer, { offer = it }, multiline = true, leadingIcon = Icons.Filled.Info)
            CampusTextField("Estimated time, example: 20 minutes", estimate, { estimate = it }, leadingIcon = Icons.Filled.Schedule)
        }
        InfoPanel("Apply only if you can complete the errand safely and on time.", Icons.Filled.Security, "Trust reminder")
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
                ErrandCore(errand)
                DetailRow("Application", errand.optString("application_status"))
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
                "Rating: ${applicant.optString("average_rating", "No rating yet")} • Completed errands: ${applicant.optString("completed_errands", "0")}",
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
        ErrandCoreCard(errand)
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
        InfoPanel("Confirm only after the helper has completed the agreed errand.", Icons.Filled.CheckCircle, "Completion confirmation", SuccessGreen)
        ErrandCoreCard(errand)
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

        ErrandCoreCard(errand)
        SectionCard("Rate Helper", Icons.Filled.Star) {
            RatingRow(score.ifBlank { "0" })
            CampusTextField("Rating 1-5", score, { score = it }, KeyboardType.Number, leadingIcon = Icons.Filled.Star)
            CampusTextField("Feedback", feedback, { feedback = it }, multiline = true, leadingIcon = Icons.Filled.Info)
        }
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

        InfoPanel("Reports help keep Malayan Quest safe.", Icons.Filled.Security, if (errand == null) "Report User" else "Report Errand", DangerRed)
        if (errand == null) {
            SectionCard("Report Details", Icons.Filled.Report) {
                CampusTextField("Reported user ID", reportedUserId, { reportedUserId = it }, KeyboardType.Number, leadingIcon = Icons.Filled.Person)
                GenericDropdown("Reason for reporting", reason, reportReasons) { reason = it }
                CampusTextField("Details - describe what happened", details, { details = it }, multiline = true, leadingIcon = Icons.Filled.Info)
            }
            ConfirmDangerButton(
                text = "Submit User Report",
                dialogTitle = "Submit this report?",
                dialogMessage = "This will send the report to admin records for review."
            ) {
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
            ErrandCoreCard(errand)
            SectionCard("Report Details", Icons.Filled.Report) {
                GenericDropdown("Reason for reporting", reason, reportReasons) { reason = it }
                CampusTextField("Additional details - describe what happened", details, { details = it }, multiline = true, leadingIcon = Icons.Filled.Info)
            }
            ConfirmDangerButton(
                text = "Submit Report",
                dialogTitle = "Submit this report?",
                dialogMessage = "This will send the errand report to admin records for review."
            ) {
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
            SectionCard("Private Verification", Icons.Filled.Security) {
                DetailRow("School email", data.optString("school_email"))
                DetailRow("Student number", data.optString("student_number"))
            }
        }
        MenuRow("Edit Profile", "Update your display name") { screen = Screen.EditProfile }
        MenuRow("Privacy & Safety", "Your email and student number stay hidden publicly") { }
        MenuRow("Ratings", "View received feedback") { screen = Screen.UserRatings }
        MenuRow("History", "Review completed and cancelled errands") { screen = Screen.History }
        DangerButton("Logout") { logout() }
        BackButton()
    }

    @Composable
    private fun EditProfileScreen() {
        var name by remember { mutableStateOf(fullName) }
        HelperPreviewPanel("Editable profile", fullName, "Verified Student", "Only safe profile fields are editable in this prototype.")
        SectionCard("Display Information", Icons.Filled.Person) {
            CampusTextField("Full name", name, { name = it }, leadingIcon = Icons.Filled.Person)
            Text("School email and student number stay private verification records.", color = TextSecondary, fontSize = 12.sp)
        }
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
            HistoryCard(item)
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
            FeedbackCard(item)
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
        InfoPanel("Cancellation may affect trust and reliability.", Icons.Filled.Cancel, "Cancel carefully", DangerRed)
        ErrandCoreCard(errand)
        SectionCard("Reason", Icons.Filled.Info) {
            CampusTextField("Explain why this errand is being cancelled", reason, { reason = it }, multiline = true, leadingIcon = Icons.Filled.Info)
        }
        ConfirmDangerButton(
            text = "Cancel Errand",
            dialogTitle = "Cancel this errand?",
            dialogMessage = "This action will be recorded in the errand history.",
            confirmText = "Cancel Errand"
        ) {
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
        InfoPanel("Monitor Malayan Quest activity and moderate unsafe records.", Icons.Filled.Security, "Admin Dashboard")
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
            CampusTextField("Search by name", keyword, { keyword = it }, leadingIcon = Icons.Filled.Search)
            FilterChipRow(listOf("All", "Verified", "Restricted", "Deactivated"), "All")
            InfoPanel("The current backend returns all users. This search field is a UI placeholder until server-side filtering is added.")
        } else {
            FilterChipRow(
                when {
                    title.contains("Errands") -> listOf("All", "Open", "Active", "Completed", "Cancelled", "Reported", "Flagged")
                    title.contains("Reports") -> listOf("Pending", "Under Review", "Resolved", "Dismissed")
                    else -> listOf("All", "Allowed", "Flagged", "Rejected")
                },
                "All"
            )
        }
        if (title == "Moderation Logs") {
            InfoPanel("The backend does not currently have a moderation log endpoint, so this screen reuses flagged errands for the prototype.", Icons.Filled.Report, "Moderation logs", WarningAmber)
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
            InfoPanel("Admin actions change account access and should only be used for verified moderation reasons.", Icons.Filled.Security, "Admin Actions", WarningAmber)
            ConfirmDangerButton("Restrict User", "Restrict this user?", "The account will be marked restricted for prototype moderation.") { updateUserStatus(item.optInt("user_id"), "restricted") }
            ConfirmDangerButton("Deactivate User", "Deactivate this user?", "The account will no longer be active until restored.") { updateUserStatus(item.optInt("user_id"), "deactivated") }
        }
        if (item.has("errand_id")) {
            ConfirmDangerButton("Remove Errand", "Remove this errand?", "This action should only be used for inappropriate or unsafe errands.") { removeErrand(item.optInt("errand_id")) }
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
            Divider(color = BorderSoft)
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
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colorForStatus(errand.optString("status")).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(categoryVector(errand.optString("category")), contentDescription = errand.optString("category"), tint = colorForStatus(errand.optString("status")), modifier = Modifier.size(23.dp))
            }
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
                UserAvatar(applicant.optString("helper_name"), size = 48.dp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(applicant.optString("helper_name"), color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    RatingRow(applicant.optString("average_rating", "0.00"))
                }
                Badge(applicant.optString("status", "pending"), colorForStatus(applicant.optString("status")))
            }
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
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(iconColor(title).copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(actionIcon(title), contentDescription = title, tint = iconColor(title), modifier = Modifier.size(22.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(recordHeadline(item, title), color = TextSecondary, fontSize = 13.sp)
                }
                recordStatus(item)?.let { Badge(it, colorForStatus(it)) }
            }
            StructuredRecord(item)
            if (clickable) {
                CampusButton("View") {
                    openAdminRecord(item, title)
                }
            }
        }
    }

    @Composable
    private fun StructuredRecord(item: JSONObject) {
        val hidden = setOf("password_hash")
        val priority = listOf(
            "full_name", "role", "verification_status", "account_status",
            "title", "category", "status", "moderation_status",
            "reason", "report_status", "rating_score", "feedback",
            "requester_name", "helper_name", "created_at", "deadline"
        )
        val names = item.names() ?: return
        val keys = mutableListOf<String>()
        priority.forEach { if (item.has(it) && it !in hidden) keys.add(it) }
        for (i in 0 until names.length()) {
            val key = names.optString(i)
            if (key !in hidden && key !in keys && keys.size < 8) keys.add(key)
        }
        keys.take(8).forEach { key ->
            DetailRow(key.replace("_", " "), item.optString(key))
        }
    }

    @Composable
    private fun RatingRow(value: String) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            repeat(5) {
                Icon(Icons.Filled.Star, contentDescription = "Rating", tint = WarningAmber, modifier = Modifier.size(14.dp))
            }
            Text(value.ifBlank { "0.00" }, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }

    @Composable
    private fun HistoryCard(item: JSONObject) {
        CampusCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SuccessGreen.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.History, contentDescription = "History", tint = SuccessGreen, modifier = Modifier.size(22.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.optString("title", "History Record"), color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text(item.optString("created_at", item.optString("updated_at", "Recent activity")), color = TextSecondary, fontSize = 12.sp)
                }
                Badge(item.optString("status", "Recorded"), colorForStatus(item.optString("status")))
            }
            StructuredRecord(item)
        }
    }

    @Composable
    private fun FeedbackCard(item: JSONObject) {
        CampusCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(WarningAmber.copy(alpha = 0.13f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Star, contentDescription = "Feedback", tint = WarningAmber, modifier = Modifier.size(22.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Rating ${item.optString("rating_score", item.optString("score", "0"))}", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    RatingRow(item.optString("rating_score", "0"))
                }
            }
            DetailRow("Feedback", item.optString("feedback", "No feedback text."))
            DetailRow("Errand", item.optString("title", item.optString("errand_title", "Related errand")))
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
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                UserAvatar(name, size = 56.dp)
                Column(modifier = Modifier.weight(1f)) {
                    Badge(eyebrow, SecondaryBlue)
                    Text(name.ifBlank { "Student Helper" }, color = TextPrimary, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    Text(stats, color = TextSecondary, fontSize = 13.sp)
                }
            }
            InfoPanel(note, Icons.Filled.Info, "Helper details")
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
                Text(initials(data.optString("full_name", fullName)), color = PrimaryBlue, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Text(data.optString("full_name", fullName), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Badge("Verified Student", Color.White, fill = Color(0xFF0A63D8), border = Color.White)
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
                Icon(actionIcon(title), contentDescription = title, tint = iconColor(title), modifier = Modifier.size(22.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(subtitle, color = TextSecondary, fontSize = 12.sp)
                }
                Icon(Icons.Filled.ChevronRight, contentDescription = "Open", tint = TextSecondary, modifier = Modifier.size(20.dp))
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
            InfoPanel("You will be notified at every update.", Icons.Filled.Info, "Status updates")
        }
    }

    @Composable
    private fun SummaryGrid(items: List<Pair<String, String>>) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.chunked(2).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    rowItems.forEach { item ->
                        CampusCard(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(iconColor(item.first).copy(alpha = 0.10f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(actionIcon(item.first), contentDescription = item.first, tint = iconColor(item.first), modifier = Modifier.size(19.dp))
                                }
                                Column {
                                    Text(item.second, color = PrimaryBlue, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                                    Text(item.first, color = TextSecondary, fontSize = 12.sp)
                                }
                            }
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
                        .clip(RoundedCornerShape(18.dp))
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
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, BorderSoft, RoundedCornerShape(16.dp))
                .clickable { action() }
                .height(116.dp)
                .padding(14.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconColor(title).copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = actionIcon(title),
                    contentDescription = title,
                    tint = iconColor(title),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(subtitle, color = TextSecondary, fontSize = 10.sp)
            Spacer(Modifier.height(2.dp))
            Icon(Icons.Filled.ChevronRight, contentDescription = "Open", tint = BorderSoft, modifier = Modifier.size(16.dp))
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
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = background),
            border = BorderStroke(1.dp, BorderSoft),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                content = content
            )
        }
    }

    @Composable
    private fun InfoPanel(
        text: String,
        icon: ImageVector = Icons.Filled.Info,
        title: String = "",
        tint: Color = SecondaryBlue
    ) {
        CampusCard(background = CardLight) {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(tint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = title.ifBlank { "Info" }, tint = tint, modifier = Modifier.size(19.dp))
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    if (title.isNotBlank()) {
                        Text(title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(text, color = TextSecondary, fontSize = 14.sp)
                }
            }
        }
    }

    @Composable
    private fun EmptyState(text: String) {
        CampusCard(background = CardLight) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Search, contentDescription = "Empty", tint = SecondaryBlue, modifier = Modifier.size(42.dp))
                Text(text, color = TextPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text("Check again later or adjust your filters.", color = TextSecondary, fontSize = 13.sp, textAlign = TextAlign.Center)
            }
        }
    }

    @Composable
    private fun SectionTitle(text: String) {
        Text(text, color = PrimaryBlue, fontSize = 19.sp, fontWeight = FontWeight.Bold)
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
        IconTextRow(iconForLabel(label), label, value)
    }

    @Composable
    private fun IconTextRow(icon: ImageVector, label: String, value: String?) {
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEAF3FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = label, tint = SecondaryBlue, modifier = Modifier.size(18.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(label.uppercase(Locale.US), color = SecondaryBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(
                    if (value.isNullOrBlank() || value == "null") "Not specified" else value,
                    color = TextPrimary,
                    fontSize = 15.sp
                )
            }
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
            modifier = modifier.height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (primary) PrimaryBlue else Color.White,
                contentColor = if (primary) Color.White else PrimaryBlue
            ),
            border = if (primary) null else BorderStroke(1.dp, Color(0xFF8DB2D9))
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
            modifier = modifier.height(52.dp),
            shape = RoundedCornerShape(14.dp),
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
        multiline: Boolean = false,
        leadingIcon: ImageVector? = null
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            leadingIcon = leadingIcon?.let { icon ->
                { Icon(icon, contentDescription = label, tint = SecondaryBlue) }
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = if (multiline) 3 else 1,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color(0xFFB8C9DA),
                focusedLabelColor = PrimaryBlue
            )
        )
    }

    @Composable
    private fun AppMark() {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(painterResource(R.drawable.malayan_quest_logo), contentDescription = "Malayan Quest", modifier = Modifier.size(58.dp))
            }
            Text("Malayan Quest", color = PrimaryBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }

    @Composable
    private fun UserAvatar(name: String, size: androidx.compose.ui.unit.Dp = 48.dp) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(Color(0xFFEAF3FF)),
            contentAlignment = Alignment.Center
        ) {
            Text(initials(name), color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }

    @Composable
    private fun SectionCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
        CampusCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEAF3FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = title, tint = PrimaryBlue, modifier = Modifier.size(19.dp))
                }
                Text(title, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
            content()
        }
    }

    @Composable
    private fun FilterChipRow(chips: List<String>, selected: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            chips.forEach { chip ->
                val active = chip == selected
                Text(
                    chip,
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (active) PrimaryBlue else Color.White)
                        .border(1.dp, if (active) PrimaryBlue else BorderSoft, RoundedCornerShape(18.dp))
                        .padding(horizontal = 13.dp, vertical = 8.dp),
                    color = if (active) Color.White else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }

    @Composable
    private fun ConfirmDangerButton(
        text: String,
        dialogTitle: String,
        dialogMessage: String,
        confirmText: String = text,
        onConfirm: () -> Unit
    ) {
        var showDialog by remember { mutableStateOf(false) }
        DangerButton(text) { showDialog = true }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(dialogTitle, fontWeight = FontWeight.Bold) },
                text = { Text(dialogMessage, color = TextSecondary) },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        onConfirm()
                    }) {
                        Text(confirmText, color = DangerRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Go Back", color = PrimaryBlue)
                    }
                }
            )
        }
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

    private fun categoryVector(category: String?): ImageVector {
        return when {
            category == null -> Icons.Filled.Assignment
            category.contains("Food", ignoreCase = true) -> Icons.Filled.Storefront
            category.contains("Printing", ignoreCase = true) -> Icons.Filled.Assignment
            category.contains("Bookstore", ignoreCase = true) || category.contains("Bluebook", ignoreCase = true) -> Icons.Filled.Storefront
            category.contains("Delivery", ignoreCase = true) -> Icons.Filled.LocalShipping
            else -> Icons.Filled.Assignment
        }
    }

    private fun iconForLabel(label: String): ImageVector {
        return when {
            label.contains("pickup", ignoreCase = true) -> Icons.Filled.LocationOn
            label.contains("drop", ignoreCase = true) -> Icons.Filled.LocalShipping
            label.contains("deadline", ignoreCase = true) || label.contains("date", ignoreCase = true) -> Icons.Filled.Schedule
            label.contains("reward", ignoreCase = true) || label.contains("amount", ignoreCase = true) -> Icons.Filled.Payments
            label.contains("requester", ignoreCase = true) || label.contains("helper", ignoreCase = true) || label.contains("user", ignoreCase = true) -> Icons.Filled.Person
            label.contains("rating", ignoreCase = true) || label.contains("score", ignoreCase = true) -> Icons.Filled.Star
            label.contains("status", ignoreCase = true) || label.contains("verification", ignoreCase = true) || label.contains("account", ignoreCase = true) -> Icons.Filled.Verified
            label.contains("category", ignoreCase = true) -> Icons.Filled.Storefront
            label.contains("offer", ignoreCase = true) || label.contains("note", ignoreCase = true) -> Icons.Filled.Info
            else -> Icons.Filled.Assignment
        }
    }

    private fun recordHeadline(item: JSONObject, title: String): String {
        return when {
            item.has("full_name") -> item.optString("full_name")
            item.has("title") -> item.optString("title")
            item.has("reason") -> item.optString("reason")
            item.has("feedback") -> item.optString("feedback")
            else -> title
        }.ifBlank { title }
    }

    private fun recordStatus(item: JSONObject): String? {
        return when {
            item.has("verification_status") -> item.optString("verification_status")
            item.has("account_status") -> item.optString("account_status")
            item.has("status") -> item.optString("status")
            item.has("moderation_status") -> item.optString("moderation_status")
            item.has("report_status") -> item.optString("report_status")
            else -> null
        }?.takeIf { it.isNotBlank() && it != "null" }
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

    private fun initials(value: String): String {
        val parts = value.trim().split(" ").filter { it.isNotBlank() }
        if (parts.isEmpty()) return "MQ"
        return parts.take(2).joinToString("") { it.first().uppercaseChar().toString() }
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
