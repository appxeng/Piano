package com.howtobecomebach.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF17243A)
private val Gold = Color(0xFFD9A441)
private val RightHand = Color(0xFF3C9DAB)
private val LeftHand = Color(0xFFB47AC8)
private val Cream = Color(0xFFF7F3EA)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("bach_settings", MODE_PRIVATE)
        setContent {
            var octaveCount by remember { mutableIntStateOf(prefs.getInt("octaves", 0)) }
            var showSetup by remember { mutableStateOf(octaveCount == 0) }
            BachApp(
                octaveCount = octaveCount,
                showSetup = showSetup,
                onChooseOctaves = {
                    octaveCount = it
                    prefs.edit().putInt("octaves", it).apply()
                    showSetup = false
                },
                onChangeOctaves = { showSetup = true }
            )
        }
    }
}

@Composable
private fun BachApp(
    octaveCount: Int,
    showSetup: Boolean,
    onChooseOctaves: (Int) -> Unit,
    onChangeOctaves: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("خانه") }
    var selectedSong by remember { mutableStateOf("Love Story") }
    var practiceMode by remember { mutableStateOf("ترکیبی") }
    var selectedFile by remember { mutableStateOf<String?>(null) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        selectedFile = uri?.lastPathSegment ?: "فایل انتخاب شد"
    }

    val songs = listOf(
        "Love Story", "Mariage d’Amour (Wedding of Love)", "Für Elise",
        "Moonlight Sonata", "River Flows in You", "Canon in D",
        "The Entertainer", "Prelude in C Major", "Ode to Joy", "Clair de Lune",
        "بهار دلنشین", "مرا ببوس", "سلطان قلب‌ها", "الهه ناز",
        "مرغ سحر", "خواب‌های طلایی", "ای ایران", "شب جدایی",
        "تو ای پری کجایی", "جان مریم"
    )

    MaterialTheme(colorScheme = lightColorScheme(
        primary = Navy, secondary = Gold, background = Cream, surface = Color.White
    )) {
        if (showSetup) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("خوش آمدید به How to Become Bach", textAlign = TextAlign.End) },
                text = {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("برای شروع، تعداد اکتاوهای صفحه‌کلید را فقط یک‌بار انتخاب کنید.")
                        Spacer(Modifier.height(12.dp))
                        (2..7).forEach { count ->
                            OutlinedButton(
                                onClick = { onChooseOctaves(count) },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                            ) { Text("$count اکتاو") }
                        }
                    }
                },
                confirmButton = {}
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().background(Cream).padding(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text("How to Become Bach", color = Navy, fontSize = 25.sp, fontWeight = FontWeight.Bold)
            Text("چگونه می‌توان باخ شد؟", color = Gold, fontSize = 16.sp)
            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("خانه", "کتابخانه", "تمرین", "تنظیمات").forEach { tab ->
                    FilterChip(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        label = { Text(tab) }
                    )
                }
            }
            Spacer(Modifier.height(14.dp))

            when (selectedTab) {
                "خانه", "کتابخانه" -> {
                    Text("کتابخانه آهنگ‌ها", color = Navy, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("۲۰ قطعه پیشنهادی برای شروع؛ نت کامل قطعات دارای حق نشر باید از منبع مجاز تهیه شود.",
                        color = Color.DarkGray, fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                    Column(Modifier.weight(1f).fillMaxWidth().background(Color.White, RoundedCornerShape(14.dp)).padding(8.dp)) {
                        songs.forEach { song ->
                            Row(
                                Modifier.fillMaxWidth().clickable { selectedSong = song }
                                    .background(if (selectedSong == song) Color(0xFFFFF1D3) else Color.Transparent)
                                    .padding(9.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(if (selectedSong == song) "انتخاب‌شده" else "انتخاب",
                                    color = Gold, fontSize = 11.sp)
                                Spacer(Modifier.weight(1f))
                                Text(song, color = Navy, textAlign = TextAlign.End)
                            }
                            HorizontalDivider(color = Color(0xFFECE7DC))
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Button(onClick = { selectedTab = "تمرین" }, modifier = Modifier.fillMaxWidth()) {
                        Text("شروع تمرین: $selectedSong")
                    }
                    OutlinedButton(
                        onClick = { picker.launch(arrayOf("application/pdf", "image/*", "audio/*")) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("افزودن PDF، تصویر یا فایل صوتی") }
                    selectedFile?.let { Text("فایل انتخاب‌شده: $it", fontSize = 12.sp) }
                }
                "تمرین" -> {
                    Text("تمرین پیانو", color = Navy, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Text("قطعه: $selectedSong", color = Color.DarkGray)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("نت‌خوانی", "Synthesia", "ترکیبی").forEach {
                            FilterChip(selected = practiceMode == it, onClick = { practiceMode = it }, label = { Text(it) })
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    StaffPreview()
                    Spacer(Modifier.height(12.dp))
                    Text("صفحه‌کلید — $octaveCount اکتاو", color = Navy, fontWeight = FontWeight.Bold)
                    PianoKeyboard(octaveCount.coerceAtLeast(2))
                    Spacer(Modifier.height(8.dp))
                    Text("آبی: دست راست   |   بنفش: دست چپ", color = Color.DarkGray, fontSize = 12.sp)
                    Text("این نسخه، نمونه اولیه رابط کاربری است؛ تشخیص خودکار نت از PDF و صوت در مرحله بعد اضافه می‌شود.",
                        color = Color.Gray, fontSize = 11.sp)
                }
                else -> {
                    Text("تنظیمات", color = Navy, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Text("تعداد اکتاو ذخیره‌شده: $octaveCount")
                    OutlinedButton(onClick = onChangeOctaves) { Text("تغییر تعداد اکتاو") }
                    Text("نام برنامه: How to Become Bach")
                }
            }
        }
    }
}

@Composable
private fun StaffPreview() {
    Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp)).padding(12.dp)) {
        Text("نمونه نمایش نت روی خطوط حامل", color = Navy, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth().height(104.dp)) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
                repeat(5) { HorizontalDivider(color = Color.DarkGray, thickness = 1.dp) }
            }
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                listOf("دو", "می", "سل", "فا", "ر").forEachIndexed { i, note ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("●", color = if (i % 2 == 0) RightHand else LeftHand, fontSize = 25.sp)
                        Text(note, color = Navy, fontSize = 11.sp)
                    }
                }
            }
        }
        Text("در نسخه کامل، نت‌نویسی استاندارد با کلید سل و فا و جایگاه دقیق هر نت نمایش داده می‌شود.",
            color = Color.Gray, fontSize = 10.sp)
    }
}

@Composable
private fun PianoKeyboard(octaves: Int) {
    val notes = listOf("دو", "ر", "می", "فا", "سل", "لا", "سی")
    Column(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            repeat(octaves) { octave ->
                notes.forEachIndexed { index, note ->
                    Column(
                        Modifier.width(43.dp).height(128.dp)
                            .background(Color.White, RoundedCornerShape(bottomStart = 5.dp, bottomEnd = 5.dp))
                            .border(1.dp, Color.DarkGray, RoundedCornerShape(bottomStart = 5.dp, bottomEnd = 5.dp))
                            .clickable { },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text("${index + 1}", color = if (octave % 2 == 0) RightHand else LeftHand,
                            fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(note, color = Navy, fontSize = 10.sp)
                        Text("CDEFGAB"[index].toString(), color = Color.Gray, fontSize = 9.sp)
                        Spacer(Modifier.height(5.dp))
                    }
                }
            }
        }
        Text("نمای ساده آموزشی کلاویه‌ها؛ کلیدهای دیز/بمل و محدوده دقیق اکتاو در نسخه بعد تکمیل می‌شود.",
            color = Color.Gray, fontSize = 10.sp)
    }
}
