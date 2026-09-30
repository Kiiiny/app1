package com.example.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class HabitIconItem(
    val id: String,
    val nameArabic: String,
    val icon: ImageVector,
    val defaultColorHex: String
)

object HabitIcons {
    val allIcons = listOf(
        HabitIconItem("water", "شرب ماء", Icons.Filled.WaterDrop, "#38BDF8"),
        HabitIconItem("book", "قراءة وتعلم", Icons.Filled.AutoStories, "#C084FC"),
        HabitIconItem("fitness", "رياضة ولياقة", Icons.Filled.FitnessCenter, "#FB7185"),
        HabitIconItem("meditation", "تأمل واسترخاء", Icons.Filled.SelfImprovement, "#34D399"),
        HabitIconItem("star", "أذكار وعبادة", Icons.Filled.Star, "#FBBF24"),
        HabitIconItem("sleep", "نوم صحي", Icons.Filled.Bedtime, "#818CF8"),
        HabitIconItem("heart", "صحة وعافية", Icons.Filled.Favorite, "#F43F5E"),
        HabitIconItem("walk", "مشي ونشاط", Icons.Filled.DirectionsRun, "#10B981"),
        HabitIconItem("food", "غذاء متوازن", Icons.Filled.Restaurant, "#F97316"),
        HabitIconItem("timer", "إنتاجية وتركيز", Icons.Filled.Timer, "#A855F7"),
        HabitIconItem("code", "عمل وبرمجة", Icons.Filled.Terminal, "#2DD4BF"),
        HabitIconItem("spa", "عناية ذاتية", Icons.Filled.Spa, "#EC4899")
    )

    fun getIcon(id: String): ImageVector {
        return allIcons.firstOrNull { it.id == id }?.icon ?: Icons.Filled.Star
    }

    fun getColor(colorHex: String): Color {
        return try {
            Color(android.graphics.Color.parseColor(colorHex))
        } catch (_: Exception) {
            Color(0xFFA855F7)
        }
    }
}
