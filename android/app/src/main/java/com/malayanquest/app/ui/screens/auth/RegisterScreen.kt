package com.malayanquest.app.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.malayanquest.app.ApiClient
import com.malayanquest.app.ui.components.AppMark
import com.malayanquest.app.ui.components.AuthTitle
import com.malayanquest.app.ui.components.CampusButton
import com.malayanquest.app.ui.components.CampusCard
import com.malayanquest.app.ui.components.CampusTextField
import com.malayanquest.app.ui.components.InfoPanel
import com.malayanquest.app.ui.theme.BorderSoft
import com.malayanquest.app.ui.theme.CardLight
import com.malayanquest.app.ui.theme.DangerRed
import com.malayanquest.app.ui.theme.PrimaryBlue
import com.malayanquest.app.ui.theme.SuccessGreen
import com.malayanquest.app.ui.theme.TextPrimary
import com.malayanquest.app.ui.theme.TextSecondary
import org.json.JSONObject
import com.malayanquest.app.ui.components.BrandAccentLine
import com.malayanquest.app.ui.theme.BrandRed

private val STUDENT_EMAIL_PATTERN = Regex(
    pattern =
        "^[a-z0-9](?:[a-z0-9._-]*[a-z0-9])?" +
                "@(?:live\\.)?mcl\\.edu\\.ph$",
    option = RegexOption.IGNORE_CASE
)

private val FULL_NAME_PATTERN = Regex(
    pattern = """^[\p{L}]+(?:[ '-][\p{L}]+)*$"""
)

@Composable
fun RegisterScreen(
    api: ApiClient,
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    onShowToast: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var studentNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var safetyAccepted by remember {
        mutableStateOf(false)
    }

    var isSubmitting by remember {
        mutableStateOf(false)
    }

    val normalizedEmail =
        email.trim().lowercase()

    val nameValid =
        FULL_NAME_PATTERN.matches(name.trim())

    val emailValid =
        STUDENT_EMAIL_PATTERN.matches(normalizedEmail)

    val studentNumberValid =
        studentNumber.length == 10

    val passwordLengthValid =
        password.length in 8..64

    val passwordHasLetter =
        password.any { it.isLetter() }

    val passwordHasNumber =
        password.any { it.isDigit() }

    val passwordStartsCorrectly =
        password.firstOrNull()?.isLetterOrDigit() == true

    val passwordSpacingValid =
        password.isNotEmpty() &&
                password == password.trim()

    val passwordValid =
        passwordLengthValid &&
                passwordHasLetter &&
                passwordHasNumber &&
                passwordStartsCorrectly &&
                passwordSpacingValid

    val passwordsMatch =
        confirmPassword.isNotEmpty() &&
                password == confirmPassword

    val canRegister =
        nameValid &&
                emailValid &&
                studentNumberValid &&
                passwordValid &&
                passwordsMatch &&
                safetyAccepted &&
                !isSubmitting

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AppMark()

        AuthTitle(
            title = "Create student account",
            subtitle = "Register using your official Mapúa MCL student details."
        )

        InfoPanel(
            title = "Private verification details",
            text = "Your school email and student number are used " +
                    "to confirm account eligibility. They are not " +
                    "displayed on your public profile.",
            icon = Icons.Filled.VerifiedUser,
            tint = PrimaryBlue,
            accentColor = BrandRed
        )

        CampusTextField(
            label = "Full name",
            value = name,
            onValueChange = {
                name = it
            },
            leadingIcon = Icons.Filled.Person
        )

        CampusTextField(
            label = "Official school email",
            value = email,
            onValueChange = {
                email = it.replace(" ", "")
            },
            keyboardType = KeyboardType.Email,
            leadingIcon = Icons.Filled.Email
        )

        ValidationText(
            text = "Use your @mcl.edu.ph or @live.mcl.edu.ph school email",
            valid = emailValid,
            showState = email.isNotBlank()
        )

        CampusTextField(
            label = "10-digit student number",
            value = studentNumber,
            onValueChange = { newValue ->
                studentNumber = newValue
                    .filter { character ->
                        character.isDigit()
                    }
                    .take(10)
            },
            keyboardType = KeyboardType.Number,
            leadingIcon = Icons.Filled.Badge
        )

        ValidationText(
            text = "Student number contains exactly 10 digits",
            valid = studentNumberValid,
            showState = studentNumber.isNotBlank()
        )

        CampusTextField(
            label = "Password",
            value = password,
            onValueChange = { newValue ->
                if (newValue.length <= 64) {
                    password = newValue
                }
            },
            keyboardType = KeyboardType.Password,
            password = true,
            leadingIcon = Icons.Filled.Lock
        )

        ValidationText(
            text = "Use letters, spaces, apostrophes, or hyphens only",
            valid = nameValid,
            showState = name.isNotBlank()
        )

        PasswordRequirements(
            lengthValid = passwordLengthValid,
            hasLetter = passwordHasLetter,
            hasNumber = passwordHasNumber,
            startsCorrectly = passwordStartsCorrectly,
            spacingValid = passwordSpacingValid,
            showState = password.isNotEmpty()
        )
        CampusTextField(
            label = "Confirm password",
            value = confirmPassword,
            onValueChange = { newValue ->
                if (newValue.length <= 64) {
                    confirmPassword = newValue
                }
            },
            keyboardType = KeyboardType.Password,
            password = true,
            leadingIcon = Icons.Filled.CheckCircle
        )

        ValidationText(
            text = "Passwords match",
            valid = passwordsMatch,
            showState = confirmPassword.isNotEmpty()
        )

        SafetyAgreementCard(
            checked = safetyAccepted,
            onCheckedChange = {
                safetyAccepted = it
            }
        )

        CampusButton(
            text = "Register student account",
            enabled = canRegister,
            loading = isSubmitting
        ) {
            isSubmitting = true

            api.post(
                endpoint = "register.php",
                body = JSONObject().apply {
                    put("full_name", name.trim())
                    put("school_email", normalizedEmail)
                    put(
                        "student_number",
                        studentNumber
                    )
                    put("password", password)
                }
            ) { response ->
                isSubmitting = false

                onShowToast(
                    response.optString(
                        "message",
                        "Unable to register."
                    )
                )

                if (response.optBoolean("success")) {
                    onRegisterSuccess()
                }
            }
        }

        Text(
            text = if (safetyAccepted) {
                "Safety agreement accepted."
            } else {
                "Accept the safety agreement to enable registration."
            },
            color = if (safetyAccepted) {
                SuccessGreen
            } else {
                TextSecondary
            },
            fontSize = 12.sp,
            modifier = Modifier.align(
                Alignment.CenterHorizontally
            )
        )

        CampusButton(
            text = "Back to sign in",
            primary = false,
            onClick = onNavigateBack
        )
    }
}

@Composable
private fun PasswordRequirements(
    lengthValid: Boolean,
    hasLetter: Boolean,
    hasNumber: Boolean,
    startsCorrectly: Boolean,
    spacingValid: Boolean,
    showState: Boolean
) {
    CampusCard(
        background = CardLight
    ) {
        Text(
            text = "Password requirements",
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )

        RequirementRow(
            text = "8 to 64 characters",
            valid = lengthValid,
            showState = showState
        )

        RequirementRow(
            text = "Contains at least one letter",
            valid = hasLetter,
            showState = showState
        )

        RequirementRow(
            text = "Contains at least one number",
            valid = hasNumber,
            showState = showState
        )

        RequirementRow(
            text = "Begins with a letter or number",
            valid = startsCorrectly,
            showState = showState
        )

        RequirementRow(
            text = "No spaces at the beginning or end",
            valid = spacingValid,
            showState = showState
        )

        Text(
            text = "Periods, underscores, hyphens, and other " +
                    "symbols may be used after the first character.",
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun RequirementRow(
    text: String,
    valid: Boolean,
    showState: Boolean
) {
    val indicatorColor = when {
        !showState -> TextSecondary
        valid -> SuccessGreen
        else -> DangerRed
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (showState && valid) {
                Icons.Filled.CheckCircle
            } else {
                Icons.Filled.RadioButtonUnchecked
            },
            contentDescription = null,
            tint = indicatorColor,
            modifier = Modifier.size(17.dp)
        )

        Text(
            text = text,
            color = indicatorColor,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun ValidationText(
    text: String,
    valid: Boolean,
    showState: Boolean
) {
    val textColor = when {
        !showState -> TextSecondary
        valid -> SuccessGreen
        else -> DangerRed
    }

    Text(
        text = text,
        color = textColor,
        fontSize = 12.sp,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun SafetyAgreementCard(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onCheckedChange(!checked)
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (checked) {
                PrimaryBlue.copy(alpha = 0.07f)
            } else {
                Color.White
            }
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (checked) {
                PrimaryBlue
            } else {
                BorderSoft
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = PrimaryBlue,
                    checkmarkColor = Color.White
                )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        top = 9.dp,
                        end = 8.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = "Safety agreement",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "I will use Malayan Quest only for safe, " +
                            "lawful, and school-related errands and will " +
                            "follow campus policies.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }
    }
}
