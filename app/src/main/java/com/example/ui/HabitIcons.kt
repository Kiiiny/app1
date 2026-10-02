package com.example.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.ui.graphics.vector.ImageVector

data class HabitIconItem(
    val id: String,
    val nameArabic: String,
    val nameEnglish: String,
    val icon: ImageVector
)

object HabitIcons {
    val allIcons = listOf(
        HabitIconItem("water", "ماء", "Water", Icons.Default.WaterDrop),
        HabitIconItem("book", "قراءة", "Book", Icons.Default.MenuBook),
        HabitIconItem("fitness", "رياضة", "Fitness", Icons.Default.FitnessCenter),
        HabitIconItem("meditation", "تأمل", "Meditation", Icons.Default.SelfImprovement),
        HabitIconItem("walk", "مشي", "Walking", Icons.Default.DirectionsWalk),
        HabitIconItem("run", "جري", "Running", Icons.Default.DirectionsRun),
        HabitIconItem("coffee", "قهوة", "Coffee", Icons.Default.LocalCafe),
        HabitIconItem("sleep", "نوم", "Sleep", Icons.Default.NightlightRound),
        HabitIconItem("star", "ذكر / تميز", "Star", Icons.Default.Star),
        HabitIconItem("goal", "إنجاز", "Goal", Icons.Default.WorkspacePremium)
    )

    fun getIconById(id: String): ImageVector {
        return allIcons.firstOrNull { it.id == id }?.icon ?: Icons.Default.Star
    }
}
