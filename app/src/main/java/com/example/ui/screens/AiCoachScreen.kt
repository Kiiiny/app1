package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.HabitViewModel
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.theme.AppTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiCoachScreen(
    viewModel: HabitViewModel,
    modifier: Modifier = Modifier
) {
    val isArabic = LocalAppLanguage.current.isRtl
    val coachInsight by viewModel.coachInsight.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzingHabits.collectAsStateWithLifecycle()

    var userQuery by remember { mutableStateOf("") }

    val quickQuestions = remember(isArabic) {
        if (isArabic) {
            listOf(
                "كيف أثبت عادة الرياضة الصباحية؟",
                "ما هو علاج التسويف في القراءة؟",
                "كيف استغل تجميع العادات بذكاء؟",
                "كيف أتفادى انخفاض طاقتي مساءً؟"
            )
        } else {
            listOf(
                "How to maintain morning workout?",
                "Overcoming reading procrastination?",
                "Best habit stacking routines?",
                "Combating evening habit slump?"
            )
        }
    }

    LaunchedEffect(Unit) {
        if (coachInsight == null) {
            viewModel.requestAiCoachAnalysis(null)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Hero Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF6366F1).copy(alpha = 0.2f),
                                        Color(0xFFA855F7).copy(alpha = 0.12f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF6366F1).copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Psychology,
                                        contentDescription = null,
                                        tint = Color(0xFF818CF8),
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isArabic) "🧠 المدرب السلوكي الذكي" else "🧠 AI Behavioral Coach",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.colors.textPrimary
                                    )
                                    Text(
                                        text = if (isArabic) "تحليل نفسي وسلوكي لعاداتك بقوة Gemini" else "Psychological habit intelligence via Gemini",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AppTheme.colors.textMuted,
                                        fontSize = 11.sp
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.requestAiCoachAnalysis(null) },
                                    enabled = !isAnalyzing
                                ) {
                                    if (isAnalyzing) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp,
                                            color = AppTheme.colors.primary
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "Refresh",
                                            tint = AppTheme.colors.primary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = coachInsight?.summary
                                    ?: if (isArabic) "جاري استقراء أنماط عاداتك وسجلاتك السلوكية..." else "Analyzing your habit consistency...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AppTheme.colors.textSecondary,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            // Slump Warning Card
            coachInsight?.slumpWarning?.let { warning ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAB308).copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFEAB308).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFEAB308),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isArabic) "رصد نقاط التعثر والاحتكاك (Slump Detection)" else "Slump & Friction Pattern",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFACC15)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = warning,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppTheme.colors.textSecondary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            // Predictive Streak Protection Card
            coachInsight?.predictiveStreakProtection?.let { protection ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF38BDF8).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isArabic) "حماية السلسلة التنبؤية (Predictive Shield)" else "Predictive Streak Shield",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = protection,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppTheme.colors.textSecondary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            // Behavioral Rules & Tips
            val tips = coachInsight?.behavioralTips ?: emptyList()
            if (tips.isNotEmpty()) {
                item {
                    Text(
                        text = if (isArabic) "💡 نصائح علم النفس السلوكي (العادات الذرية)" else "💡 Behavioral Psychology Directives",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary
                    )
                }

                items(tips) { tip ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardElevated),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.highlight)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = Color(0xFFA855F7),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = tip,
                                style = MaterialTheme.typography.bodySmall,
                                color = AppTheme.colors.textPrimary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // Ask the Coach Section
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isArabic) "💬 استشر المدرب الذكي (توجيه فوري)" else "💬 Ask AI Coach for Guidance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.textPrimary
                )
            }

            // Quick Questions Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quickQuestions) { question ->
                        FilterChip(
                            selected = false,
                            onClick = {
                                userQuery = question
                                viewModel.requestAiCoachAnalysis(question)
                            },
                            label = { Text(question, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = AppTheme.colors.cardElevated,
                                labelColor = AppTheme.colors.textSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = false,
                                borderColor = AppTheme.colors.highlight
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Custom Input Box
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = userQuery,
                        onValueChange = { userQuery = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                text = if (isArabic) "اكتب سؤالك للمدرب الذكي..." else "Ask a custom question...",
                                color = AppTheme.colors.textMuted,
                                fontSize = 12.sp
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppTheme.colors.primary,
                            unfocusedBorderColor = AppTheme.colors.highlight,
                            focusedContainerColor = AppTheme.colors.cardElevated,
                            unfocusedContainerColor = AppTheme.colors.cardElevated,
                            focusedTextColor = AppTheme.colors.textPrimary,
                            unfocusedTextColor = AppTheme.colors.textPrimary
                        )
                    )

                    Button(
                        onClick = {
                            if (userQuery.isNotBlank() && !isAnalyzing) {
                                viewModel.requestAiCoachAnalysis(userQuery)
                            }
                        },
                        enabled = userQuery.isNotBlank() && !isAnalyzing,
                        colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.primary),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Motivational Quote
            coachInsight?.motivationalQuote?.let { quote ->
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF6366F1).copy(alpha = 0.1f))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = quote,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF818CF8),
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}
