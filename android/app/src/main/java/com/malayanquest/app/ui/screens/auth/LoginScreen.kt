package com.malayanquest.app.ui.screens.auth

import android.util.Patterns
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.malayanquest.app.ApiClient
import com.malayanquest.app.ui.components.AppMark
import com.malayanquest.app.ui.components.AuthTitle
import com.malayanquest.app.ui.components.Badge
import com.malayanquest.app.ui.components.BrandAccentLine
import com.malayanquest.app.ui.components.BrandHighlightLabel
import com.malayanquest.app.ui.components.CampusButton
import com.malayanquest.app.ui.components.CampusCard
import com.malayanquest.app.ui.components.CampusTextField
import com.malayanquest.app.ui.theme.BrandRed
import com.malayanquest.app.ui.theme.BorderSoft
import com.malayanquest.app.ui.theme.CardLight
import com.malayanquest.app.ui.theme.PrimaryBlue
import com.malayanquest.app.ui.theme.TextPrimary
import com.malayanquest.app.ui.theme.TextSecondary
import org.json.JSONObject
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width


@Composable
fun LoginScreen(
    api: ApiClient,
    onLoginSuccess: (JSONObject) -> Unit,
    onNavigateToRegister: () -> Unit,
    onShowToast: (String) -> Unit
) {
    var adminMode by remember {
        mutableStateOf(false)
    }

    var identifier by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var showPrototypeAccounts by remember {
        mutableStateOf(false)
    }

    var isSubmitting by remember {
        mutableStateOf(false)
    }

    val studentNumberValid =
        identifier.length == 10 &&
                identifier.all { it.isDigit() }

    val adminEmailValid =
        Patterns.EMAIL_ADDRESS
            .matcher(identifier.trim())
            .matches()

    val identifierValid = if (adminMode) {
        adminEmailValid
    } else {
        studentNumberValid
    }

    val canLogin =
        identifierValid &&
                password.isNotBlank() &&
                !isSubmitting

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AppMark()

        AuthTitle(
            title = if (adminMode) {
                "Admin sign in"
            } else {
                "Student sign in"
            },
            subtitle = if (adminMode) {
                "Access the Malayan Quest administration portal."
            } else {
                "Access campus errands using your student number."
            }
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            BrandHighlightLabel(
                text = if (adminMode) {
                    "Authorized administrators only"
                } else {
                    "For Mapúa MCL students only"
                }
            )
        }

        CampusTextField(
            label = if (adminMode) {
                "Admin email"
            } else {
                "10-digit student number"
            },
            value = identifier,
            onValueChange = { newValue ->
                identifier = if (adminMode) {
                    newValue
                        .replace(" ", "")
                        .take(150)
                } else {
                    newValue
                        .filter { it.isDigit() }
                        .take(10)
                }
            },
            keyboardType = if (adminMode) {
                KeyboardType.Email
            } else {
                KeyboardType.Number
            },
            leadingIcon = if (adminMode) {
                Icons.Filled.Email
            } else {
                Icons.Filled.Badge
            }
        )

        if (
            identifier.isNotBlank() &&
            !identifierValid
        ) {
            Text(
                text = if (adminMode) {
                    "Enter a valid administrator email."
                } else {
                    "Student number must contain exactly 10 digits."
                },
                color = BrandRed,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        CampusTextField(
            label = "Password",
            value = password,
            onValueChange = {
                password = it.take(64)
            },
            keyboardType = KeyboardType.Password,
            password = true,
            leadingIcon = Icons.Filled.Lock
        )

        CampusButton(
            text = if (adminMode) {
                "Sign in as admin"
            } else {
                "Sign in"
            },
            enabled = canLogin,
            loading = isSubmitting
        ) {
            isSubmitting = true

            api.post(
                endpoint = "login.php",
                body = JSONObject().apply {
                    put(
                        "login_type",
                        if (adminMode) {
                            "admin"
                        } else {
                            "student"
                        }
                    )

                    if (adminMode) {
                        put(
                            "school_email",
                            identifier.trim().lowercase()
                        )
                    } else {
                        put(
                            "student_number",
                            identifier
                        )
                    }

                    put("password", password)
                }
            ) { response ->
                isSubmitting = false

                if (response.optBoolean("success")) {
                    response
                        .optJSONObject("data")
                        ?.let(onLoginSuccess)
                } else {
                    onShowToast(
                        response.optString(
                            "message",
                            "Unable to sign in."
                        )
                    )
                }
            }
        }

        TextButton(
            onClick = {
                adminMode = !adminMode
                identifier = ""
                password = ""
            },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.textButtonColors(
                contentColor = PrimaryBlue
            )
        ) {
            Text(
                text = "•",
                color = BrandRed,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = Icons.Filled.AdminPanelSettings,
                contentDescription = null,
                tint = PrimaryBlue
            )

            Spacer(modifier = Modifier.width(7.dp))

            Text(
                text = if (adminMode) {
                    "Back to student sign in"
                } else {
                    "Login as admin"
                },
                color = PrimaryBlue,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (!adminMode) {
            HorizontalDivider(
                color = BorderSoft,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "New to Malayan Quest?",
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Create an account using your official " +
                            "@mcl.edu.ph or @live.mcl.edu.ph student email.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            CampusButton(
                text = "Create student account",
                modifier = Modifier.fillMaxWidth(),
                primary = false,
                onClick = onNavigateToRegister
            )
        }

        CampusCard(
            background = CardLight
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showPrototypeAccounts =
                            !showPrototypeAccounts
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = PrimaryBlue
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Prototype access",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (showPrototypeAccounts) {
                            "Tap to hide demonstration accounts"
                        } else {
                            "Tap to view demonstration accounts"
                        },
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Icon(
                    imageVector = if (showPrototypeAccounts) {
                        Icons.Filled.ExpandLess
                    } else {
                        Icons.Filled.ExpandMore
                    },
                    contentDescription = null,
                    tint = PrimaryBlue
                )
            }

            if (showPrototypeAccounts) {
                HorizontalDivider(color = BorderSoft)

                Text(
                    text = "These accounts are for classroom testing only.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Text(
                    text = "Demo password: password",
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            adminMode = false
                            identifier = "2026000001"
                            password = "password"
                        },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(
                            1.dp,
                            PrimaryBlue
                        ),
                        colors =
                            ButtonDefaults.outlinedButtonColors(
                                contentColor = PrimaryBlue
                            ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Use student")
                    }

                    OutlinedButton(
                        onClick = {
                            adminMode = true
                            identifier = "admin@mcl.edu.ph"
                            password = "password"
                        },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(
                            1.dp,
                            BrandRed
                        ),
                        colors =
                            ButtonDefaults.outlinedButtonColors(
                                contentColor = BrandRed
                            ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Use admin")
                    }
                }
            }
        }
    }
}
