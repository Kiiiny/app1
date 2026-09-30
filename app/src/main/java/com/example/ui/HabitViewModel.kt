package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.HabitDatabase
import com.example.data.model.Habit
import com.example.data.model.HabitItemUiState
import com.example.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppScreen {
    DASHBOARD,
    ADD_HABIT,
    SETTINGS
}

enum class HabitFilter {
    ALL,
    PENDING,
    COMPLETED
}

data class DashboardStats(
    val totalCount: Int = 0,
    val completedCount: Int = 0,
    val progressFraction: Float = 0f,
    val progressPercentage: Int = 0,
    val longestStreak: Int = 0
)

class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HabitRepository
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val arabicDateFormat = SimpleDateFormat("EEEE، d MMMM", Locale("ar"))

    val todayDateString: String = dateFormat.format(Date())
    val todayArabicDateDisplay: String = arabicDateFormat.format(Date())

    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedFilter = MutableStateFlow(HabitFilter.ALL)
    val selectedFilter: StateFlow<HabitFilter> = _selectedFilter.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    // Settings States
    private val _remindersEnabled = MutableStateFlow(true)
    val remindersEnabled: StateFlow<Boolean> = _remindersEnabled.asStateFlow()

    private val _reminderTime = MutableStateFlow("08:00 صباحاً")
    val reminderTime: StateFlow<String> = _reminderTime.asStateFlow()

    init {
        val db = HabitDatabase.getDatabase(application)
        repository = HabitRepository(db.habitDao(), db.habitCompletionDao())

        // Seed sample habits on initial launch
        viewModelScope.launch {
            repository.seedSampleHabitsIfEmpty()
        }
    }

    val todayHabitItems: StateFlow<List<HabitItemUiState>> =
        repository.getTodayHabitItems(todayDateString)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val filteredHabitItems: StateFlow<List<HabitItemUiState>> =
        combine(todayHabitItems, _selectedFilter) { items, filter ->
            when (filter) {
                HabitFilter.ALL -> items
                HabitFilter.COMPLETED -> items.filter { it.isCompletedToday }
                HabitFilter.PENDING -> items.filter { !it.isCompletedToday }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val dashboardStats: StateFlow<DashboardStats> =
        todayHabitItems.combine(repository.getAllCompletions()) { items, _ ->
            val total = items.size
            val completed = items.count { it.isCompletedToday }
            val fraction = if (total > 0) completed.toFloat() / total.toFloat() else 0f
            val percent = (fraction * 100).toInt()
            val maxStreak = items.maxOfOrNull { it.currentStreak } ?: 0

            DashboardStats(
                totalCount = total,
                completedCount = completed,
                progressFraction = fraction,
                progressPercentage = percent,
                longestStreak = maxStreak
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardStats()
        )

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setFilter(filter: HabitFilter) {
        _selectedFilter.value = filter
    }

    fun toggleHabit(habitId: Long) {
        viewModelScope.launch {
            repository.toggleHabitCompletion(habitId, todayDateString)
        }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
            _userMessage.emit("تم حذف العادة بنجاح")
        }
    }

    fun addNewHabit(
        name: String,
        frequency: String,
        selectedDays: String,
        iconName: String,
        colorHex: String,
        timeOfDay: String,
        notes: String
    ): Boolean {
        if (name.trim().isEmpty()) {
            return false
        }
        viewModelScope.launch {
            val newHabit = Habit(
                name = name.trim(),
                frequency = frequency,
                selectedDays = selectedDays,
                iconName = iconName,
                colorHex = colorHex,
                timeOfDay = timeOfDay,
                notes = notes.trim()
            )
            repository.insertHabit(newHabit)
            _userMessage.emit("تمت إضافة عادة \"${name.trim()}\" بنجاح!")
            _currentScreen.value = AppScreen.DASHBOARD
        }
        return true
    }

    fun resetToday() {
        viewModelScope.launch {
            repository.resetTodayCompletions(todayDateString)
            _userMessage.emit("تمت إعادة تعيين إنجازات اليوم")
        }
    }

    fun loadSampleHabits() {
        viewModelScope.launch {
            repository.insertSampleHabits()
            _userMessage.emit("تم تحميل العادات النموذجية بنجاح")
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            _userMessage.emit("تم مسح كافة البيانات")
        }
    }

    fun toggleReminders() {
        _remindersEnabled.value = !_remindersEnabled.value
    }

    fun setReminderTime(time: String) {
        _reminderTime.value = time
    }
}
