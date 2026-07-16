package com.malayanquest.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.malayanquest.app.R
import com.malayanquest.app.navigation.Screen
import com.malayanquest.app.ui.theme.*
import java.util.Locale

@Composable
fun Header(
    title: String,
    subtitle: String,
    canNavigateBack: Boolean,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(start = 16.dp, end = 16.dp, top = 34.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (canNavigateBack) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
    HorizontalDivider(color = BorderSoft)
}

@Composable
fun DashboardHeader(fullName: String) {
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
fun CampusCard(
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
fun BrandHighlightLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(CardLight)
            .border(
                width = 1.dp,
                color = BorderSoft,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(
                horizontal = 14.dp,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(BrandRed)
        )

        Text(
            text = text,
            color = PrimaryBlue,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
@Composable
fun InfoPanel(
    text: String,
    icon: ImageVector = Icons.Filled.Info,
    title: String = "",
    tint: Color = SecondaryBlue,
    accentColor: Color? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardLight
        ),
        border = BorderStroke(1.dp, BorderSoft),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            if (accentColor != null) {
                Box(
                    modifier = Modifier
                        .width(5.dp)
                        .fillMaxHeight()
                        .background(accentColor)
                )
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(tint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title.ifBlank {
                            "Information"
                        },
                        tint = tint,
                        modifier = Modifier.size(19.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    if (title.isNotBlank()) {
                        Text(
                            text = title,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = text,
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CampusButton(
    text: String,
    modifier: Modifier = Modifier,
    primary: Boolean = true,
    enabled: Boolean = true,
    loading: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (primary) PrimaryBlue else Color.White,
            contentColor = if (primary) Color.White else PrimaryBlue,

            disabledContainerColor = if (primary) {
                PrimaryBlue.copy(alpha = 0.38f)
            } else {
                Color(0xFFF1F3F6)
            },

            disabledContentColor = if (primary) {
                Color.White.copy(alpha = 0.75f)
            } else {
                TextSecondary
            }
        ),
        border = if (primary) {
            null
        } else {
            BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.45f))
        }
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = if (primary) Color.White else PrimaryBlue
            )
        } else {
            Text(
                text = text,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DangerButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DangerRed,
            contentColor = Color.White,
            disabledContainerColor = DangerRed.copy(alpha = 0.32f),
            disabledContentColor = Color.White.copy(alpha = 0.75f)
        )
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CampusTextField(
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
fun Badge(
    text: String,
    color: Color,
    fill: Color = color,
    border: Color = color,
    contentColor: Color = Color.White
) {
    Text(
        text = text.ifBlank { "Open" },
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(fill)
            .border(1.dp, border, RoundedCornerShape(18.dp))
            .padding(horizontal = 11.dp, vertical = 5.dp),
        color = contentColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun UserAvatar(name: String, size: Dp = 48.dp) {
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
fun DetailRow(label: String, value: String?) {
    IconTextRow(iconForLabel(label), label, value)
}

@Composable
fun IconTextRow(icon: ImageVector, label: String, value: String?) {
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
fun AppMark() {
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
fun SectionCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
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
fun TabStrip(tabs: List<String>, selected: String, onSelected: (String) -> Unit) {
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
fun FilterChipRow(chips: List<String>, selected: String, onSelected: (String) -> Unit) {
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
                    .clickable { onSelected(chip) } // <--- Added this line so they actually work
                    .padding(horizontal = 13.dp, vertical = 8.dp),
                color = if (active) Color.White else TextSecondary,
                fontSize = 12.sp,
                fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun ConfirmDangerButton(
    text: String,
    dialogTitle: String,
    dialogMessage: String,
    confirmText: String = text,
    enabled: Boolean = true,
    onConfirm: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    DangerButton(text, enabled = enabled) { showDialog = true }
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
fun RatingRow(value: String) {
    val score = value.toFloatOrNull()?.coerceIn(0f, 5f) ?: 0f
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(5) { index ->
            Icon(
                Icons.Filled.Star,
                contentDescription = null,
                tint = if (index < score.toInt()) WarningAmber else BorderSoft,
                modifier = Modifier.size(14.dp)
            )
        }
        Text(value.ifBlank { "0.00" }, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(value: String, categories: List<String>, includeAll: Boolean = false, onChange: (String) -> Unit) {
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
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
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
fun GenericDropdown(label: String, value: String, options: List<String>, onChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
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

@Composable
fun StudentBottomNav(
    currentScreen: Screen,
    showAdminDashboard: Boolean = false,
    onNavigate: (Screen) -> Unit,
    onAdminDashboard: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        HorizontalDivider(color = BorderSoft)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem("Feed", Icons.Filled.Home, currentScreen == Screen.BrowseErrands) { onNavigate(Screen.BrowseErrands) }
            BottomNavItem("My Tasks", Icons.Filled.Work, currentScreen == Screen.MyTasks) { onNavigate(Screen.MyTasks) }
            BottomNavItem("Profile", Icons.Filled.AccountCircle, currentScreen == Screen.Profile || currentScreen == Screen.EditProfile) { onNavigate(Screen.Profile) }
            if (showAdminDashboard) {
                BottomNavItem("Admin", Icons.Filled.AdminPanelSettings, false, onAdminDashboard)
            }
        }
    }
}

@Composable
fun BottomNavItem(label: String, icon: ImageVector, selected: Boolean, action: () -> Unit) {
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

@Composable
fun EmptyState(text: String) {
    CampusCard(background = CardLight) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.Search, contentDescription = "Empty", tint = SecondaryBlue, modifier = Modifier.size(42.dp))
            Text(text, color = TextPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text("Check again later or adjust your filters.", color = TextSecondary, fontSize = 13.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(text, color = PrimaryBlue, fontSize = 19.sp, fontWeight = FontWeight.Bold)
}

@Composable
fun AuthTitle(title: String, subtitle: String) {
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
fun BrandAccentLine(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(48.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(BrandRed)
    )
}

@Composable
fun ProfileStat(label: String, value: String, modifier: Modifier = Modifier) {
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
fun SummaryGrid(items: List<Pair<String, String>>) {
    CampusCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PrimaryBlue.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Assessment, contentDescription = "Activity overview", tint = PrimaryBlue, modifier = Modifier.size(21.dp))
            }
            Column {
                Text("Activity Overview", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Current platform records", color = TextSecondary, fontSize = 12.sp)
            }
        }
        HorizontalDivider(color = BorderSoft)
        items.chunked(2).forEachIndexed { rowIndex, rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowItems.forEachIndexed { itemIndex, item ->
                    SummaryMetric(item.first, item.second, Modifier.weight(1f))
                    if (itemIndex == 0 && rowItems.size > 1) {
                        Box(Modifier.width(1.dp).height(54.dp).background(BorderSoft))
                    }
                }
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
            if (rowIndex < items.chunked(2).lastIndex) {
                HorizontalDivider(color = BorderSoft)
            }
        }
    }
}

@Composable
private fun SummaryMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(horizontal = 6.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconColor(label).copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(actionIcon(label), contentDescription = label, tint = iconColor(label), modifier = Modifier.size(18.dp))
        }
        Column {
            Text(value, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(label, color = TextSecondary, fontSize = 11.sp, maxLines = 1)
        }
    }
}

@Composable
fun ActionGrid(items: List<Pair<String, String>>, actions: List<() -> Unit>) {
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
fun ActionTile(title: String, subtitle: String, action: () -> Unit, modifier: Modifier = Modifier) {
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
fun MessageRouteCard(title: String, subtitle: String, badge: String, action: () -> Unit) {
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
fun GreetingCard(fullName: String) {
    CampusCard(background = CardLight) {
        Badge("Verified school-only prototype", SecondaryBlue)
        Text("Hello, ${firstName(fullName).ifBlank { "Student" }}", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Manage campus errands, helper applications, messages, completion, ratings, and safety reports from one dashboard.", color = TextSecondary)
    }
}

// Helper functions that were in MainActivity but are pure logic
fun firstName(value: String): String {
    val trimmed = value.trim()
    val space = trimmed.indexOf(" ")
    return if (space > 0) trimmed.substring(0, space) else trimmed
}

fun initials(value: String): String {
    val parts = value.trim().split(" ").filter { it.isNotBlank() }
    if (parts.isEmpty()) return "MQ"
    return parts.take(2).joinToString("") { it.first().uppercaseChar().toString() }
}

fun iconForLabel(label: String): ImageVector {
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
        else -> Icons.AutoMirrored.Filled.Assignment
    }
}

fun actionIcon(title: String): ImageVector {
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
        else -> Icons.AutoMirrored.Filled.Assignment
    }
}

fun iconColor(title: String): Color {
    return when {
        title.contains("Report") -> DangerRed
        title.contains("Message") -> SuccessGreen
        title.contains("History") -> Color(0xFF4F46E5)
        title.contains("Helper") || title.contains("Profile") -> SecondaryBlue
        title.contains("Posted") -> WarningAmber
        else -> PrimaryBlue
    }
}
