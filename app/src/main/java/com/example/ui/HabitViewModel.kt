package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiCoachInsight
import com.example.ai.HabitAiCoachService
import com.example.data.local.HabitDatabase
import com.example.data.model.DailyCompletionRate
import com.example.data.model.Habit
import com.example.data.model.HabitCompletion
import com.example.data.model.HabitItemUiState
import com.example.data.model.PeriodStats
import com.example.data.model.StatsPeriod
import com.example.data.repository.HabitRepository
import com.example.health.HealthStepTracker
import com.example.reminder.HabitNotificationHelper
import com.example.reminder.HabitReminderScheduler
import com.example.ui.localization.AppLanguage
import com.example.ui.statistics.HabitStatsCalculator
import com.example.widget.HabitAppWidgetProvider
import kotlinx.coroutines.flow.firstOrNull
import com.example.ui.theme.AppThemePalette
import com.example.ui.theme.AppThemeSettings
import com.example.ui.theme.CardDensity
import com.example.ui.theme.FontScalePreference
import com.example.ui.theme.ThemeMode
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
    STATISTICS,
    AI_COACH,
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
    private val prefs = application.getSharedPreferences("app_theme_preferences", Context.MODE_PRIVATE)

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val todayDateString: String = dateFormat.format(Date())

    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedFilter = MutableStateFlow(HabitFilter.ALL)
    val selectedFilter: StateFlow<HabitFilter> = _selectedFilter.asStateFlow()

    private val _selectedPeriod = MutableStateFlow(StatsPeriod.WEEKLY)
    val selectedPeriod: StateFlow<StatsPeriod> = _selectedPeriod.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    private val _appLanguage = MutableStateFlow(loadInitialLanguage())
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private val _themeSettings = MutableStateFlow(loadInitialThemeSettings())
    val themeSettings: StateFlow<AppThemeSettings> = _themeSettings.asStateFlow()

    private val _remindersEnabled = MutableStateFlow(prefs.getBoolean("reminders_enabled", true))
    val remindersEnabled: StateFlow<Boolean> = _remindersEnabled.asStateFlow()

    private val _reminderTime = MutableStateFlow(prefs.getString("reminder_time", "08:00 AM") ?: "08:00 AM")
    val reminderTime: StateFlow<String> = _reminderTime.asStateFlow()

    // Step Counter State
    private val _todaySteps = MutableStateFlow(0)
    val todaySteps: StateFlow<Int> = _todaySteps.asStateFlow()

    // AI Coach State
    private val _coachInsight = MutableStateFlow<AiCoachInsight?>(null)
    val coachInsight: StateFlow<AiCoachInsight?> = _coachInsight.asStateFlow()

    private val _isAnalyzingHabits = MutableStateFlow(false)
    val isAnalyzingHabits: StateFlow<Boolean> = _isAnalyzingHabits.asStateFlow()

    // Vacation Mode State
    private val _isVacationModeEnabled = MutableStateFlow(prefs.getBoolean("vacation_mode_enabled", false))
    val isVacationModeEnabled: StateFlow<Boolean> = _isVacationModeEnabled.asStateFlow()

    init {
        val db = HabitDatabase.getDatabase(application)
        repository = HabitRepository(db.habitDao(), db.habitCompletionDao())

        viewModelScope.launch {
            repository.seedSampleHabitsIfEmpty()
        }

        if (_remindersEnabled.value) {
            val (h, m) = HabitReminderScheduler.parseTimeString(_reminderTime.value)
            HabitReminderScheduler.scheduleDailyReminder(application, h, m)
        }

        try {
            HealthStepTracker.initStepTracking(application)
            refreshSteps()
            HabitAppWidgetProvider.updateAllWidgets(application)
        } catch (_: Exception) {}
    }

    private fun loadInitialLanguage(): AppLanguage {
        val langName = prefs.getString("app_language", AppLanguage.ARABIC.name)
        return try {
            AppLanguage.valueOf(langName ?: AppLanguage.ARABIC.name)
        } catch (_: Exception) {
            AppLanguage.ARABIC
        }
    }

    fun switchLanguage(language: AppLanguage) {
        _appLanguage.value = language
        prefs.edit().putString("app_language", language.name).apply()
        viewModelScope.launch {
            val msg = if (language == AppLanguage.ARABIC) {
                "تم تغيير لغة التطبيق إلى العربية (RTL)"
            } else {
                "App language switched to English (LTR)"
            }
            _userMessage.emit(msg)
        }
    }

    private fun loadInitialThemeSettings(): AppThemeSettings {
        val paletteName = prefs.getString("theme_palette", AppThemePalette.ROYAL_PURPLE.name)
        val modeName = prefs.getString("theme_mode", ThemeMode.DARK.name)
        val densityName = prefs.getString("card_density", CardDensity.COMFORTABLE.name)
        val fontScaleName = prefs.getString("font_scale", FontScalePreference.MEDIUM.name)

        val palette = try {
            AppThemePalette.valueOf(paletteName ?: AppThemePalette.ROYAL_PURPLE.name)
        } catch (_: Exception) {
            AppThemePalette.ROYAL_PURPLE
        }

        val mode = try {
            ThemeMode.valueOf(modeName ?: ThemeMode.DARK.name)
        } catch (_: Exception) {
            ThemeMode.DARK
        }

        val density = try {
            CardDensity.valueOf(densityName ?: CardDensity.COMFORTABLE.name)
        } catch (_: Exception) {
            CardDensity.COMFORTABLE
        }

        val fontScale = try {
            FontScalePreference.valueOf(fontScaleName ?: FontScalePreference.MEDIUM.name)
        } catch (_: Exception) {
            FontScalePreference.MEDIUM
        }

        return AppThemeSettings(
            palette = palette,
            mode = mode,
            cardDensity = density,
            fontScale = fontScale
        )
    }

    fun setThemePalette(palette: AppThemePalette) {
        _themeSettings.value = _themeSettings.value.copy(palette = palette)
        prefs.edit().putString("theme_palette", palette.name).apply()
        viewModelScope.launch {
            val msg = if (_appLanguage.value == AppLanguage.ARABIC) {
                "تم تغيير سمة الألوان: ${palette.displayNameArabic}"
            } else {
                "Theme palette changed to ${palette.displayNameEnglish}"
            }
            _userMessage.emit(msg)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeSettings.value = _themeSettings.value.copy(mode = mode)
        prefs.edit().putString("theme_mode", mode.name).apply()
        viewModelScope.launch {
            val msg = if (_appLanguage.value == AppLanguage.ARABIC) {
                "تم تفعيل ${mode.displayNameArabic}"
            } else {
                "Switched to ${mode.displayNameEnglish}"
            }
            _userMessage.emit(msg)
        }
    }

    fun setCardDensity(density: CardDensity) {
        _themeSettings.value = _themeSettings.value.copy(cardDensity = density)
        prefs.edit().putString("card_density", density.name).apply()
        viewModelScope.launch {
            val msg = if (_appLanguage.value == AppLanguage.ARABIC) {
                "تم تغيير كثافة العرض: ${density.displayNameArabic}"
            } else {
                "Card density updated"
            }
            _userMessage.emit(msg)
        }
    }

    fun setFontScale(scale: FontScalePreference) {
        _themeSettings.value = _themeSettings.value.copy(fontScale = scale)
        prefs.edit().putString("font_scale", scale.name).apply()
        viewModelScope.launch {
            val msg = if (_appLanguage.value == AppLanguage.ARABIC) {
                "تم تغيير حجم الخط: ${scale.displayNameArabic}"
            } else {
                "Font scale updated"
            }
            _userMessage.emit(msg)
        }
    }

    fun resetThemeSettings() {
        val defaultSettings = AppThemeSettings()
        _themeSettings.value = defaultSettings
        prefs.edit()
            .putString("theme_palette", defaultSettings.palette.name)
            .putString("theme_mode", defaultSettings.mode.name)
            .putString("card_density", defaultSettings.cardDensity.name)
            .putString("font_scale", defaultSettings.fontScale.name)
            .apply()
        viewModelScope.launch {
            val msg = if (_appLanguage.value == AppLanguage.ARABIC) {
                "تمت استعادة المظهر الافتراضي (الأرجواني الملكي الداكن)"
            } else {
                "Reset to default appearance (Royal Dark Purple)"
            }
            _userMessage.emit(msg)
        }
    }

    val allHabits: StateFlow<List<Habit>> = repository.allHabits.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allCompletions: StateFlow<List<HabitCompletion>> = repository.getAllCompletions().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

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
        todayHabitItems.combine(allHabits) { items, habits ->
            val total = items.size
            val completed = items.count { it.isCompletedToday }
            val fraction = if (total > 0) completed.toFloat() / total.toFloat() else 0f
            val percentage = (fraction * 100).toInt()
            val longestStreak = items.maxOfOrNull { it.currentStreak } ?: 0

            DashboardStats(
                totalCount = total,
                completedCount = completed,
                progressFraction = fraction,
                progressPercentage = percentage,
                longestStreak = longestStreak
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardStats()
        )

    val periodStats: StateFlow<PeriodStats> =
        combine(allHabits, allCompletions, _selectedPeriod, _appLanguage) { habits, completions, period, lang ->
            HabitStatsCalculator.calculatePeriodStats(
                period = period,
                habits = habits,
                allCompletions = completions,
                todayDateString = todayDateString,
                language = lang
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PeriodStats(
                period = StatsPeriod.WEEKLY,
                rates = emptyList(),
                averageRate = 0,
                bestDayName = "",
                totalHabitsCount = 0,
                totalCompletionsInPeriod = 0
            )
        )

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setFilter(filter: HabitFilter) {
        _selectedFilter.value = filter
    }

    fun setStatsPeriod(period: StatsPeriod) {
        _selectedPeriod.value = period
    }

    fun refreshSteps() {
        val steps = HealthStepTracker.getTodaySteps(getApplication())
        _todaySteps.value = steps
        checkHealthHabitsAutoCompletion(steps)
    }

    fun addTestSteps(delta: Int) {
        val newSteps = HealthStepTracker.addSimulationSteps(getApplication(), delta)
        _todaySteps.value = newSteps
        checkHealthHabitsAutoCompletion(newSteps)
    }

    private fun checkHealthHabitsAutoCompletion(currentSteps: Int) {
        viewModelScope.launch {
            val habits = repository.allHabits.firstOrNull() ?: return@launch
            val db = HabitDatabase.getDatabase(getApplication())
            val todayCompletions = db.habitCompletionDao().getCompletionsListForDate(todayDateString).map { it.habitId }.toSet()
            val autoCompleted = HealthStepTracker.checkAutoCompletions(getApplication(), currentSteps, habits, todayCompletions)
            for (h in autoCompleted) {
                repository.toggleHabitCompletion(h.id, todayDateString)
                val msg = if (_appLanguage.value == AppLanguage.ARABIC) {
                    "🎉 رائع! تم إنجاز عادة \"${h.name}\" تلقائياً بعد بلوغ $currentSteps خطوة!"
                } else {
                    "🎉 Goal reached! Completed \"${h.name}\" automatically ($currentSteps steps)!"
                }
                _userMessage.emit(msg)
            }
            HabitAppWidgetProvider.updateAllWidgets(getApplication())
        }
    }

    fun toggleHabit(habitId: Long) {
        viewModelScope.launch {
            val isNowCompleted = repository.toggleHabitCompletion(habitId, todayDateString)
            HabitAppWidgetProvider.updateAllWidgets(getApplication())

            if (isNowCompleted) {
                val allHabitsList = repository.allHabits.firstOrNull() ?: emptyList()
                val thisHabit = allHabitsList.find { it.id == habitId }
                val nextInStack = allHabitsList.find { it.anchorHabitId == habitId }

                if (nextInStack != null && thisHabit != null) {
                    val msg = if (_appLanguage.value == AppLanguage.ARABIC) {
                        "🔗 أحسنت! أنجزت \"${thisHabit.name}\"، وحان الآن دور العادة المترابطة: \"${nextInStack.name}\" ✨"
                    } else {
                        "🔗 Great! You finished \"${thisHabit.name}\", now up next: \"${nextInStack.name}\" ✨"
                    }
                    _userMessage.emit(msg)
                }
            }
        }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
            HabitAppWidgetProvider.updateAllWidgets(getApplication())
            val msg = if (_appLanguage.value == AppLanguage.ARABIC) "تم حذف العادة بنجاح" else "Habit deleted successfully"
            _userMessage.emit(msg)
        }
    }

    fun addNewHabit(
        name: String,
        frequency: String,
        selectedDays: String,
        iconName: String,
        colorHex: String,
        timeOfDay: String,
        notes: String,
        anchorHabitId: Long? = null,
        stackCue: String = "",
        isHealthSynced: Boolean = false,
        targetSteps: Int = 10000
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
                notes = notes.trim(),
                anchorHabitId = anchorHabitId,
                stackCue = stackCue.trim(),
                isHealthSynced = isHealthSynced,
                targetSteps = targetSteps
            )
            repository.insertHabit(newHabit)
            HabitAppWidgetProvider.updateAllWidgets(getApplication())
            val msg = if (_appLanguage.value == AppLanguage.ARABIC) {
                "تمت إضافة عادة \"${name.trim()}\" بنجاح!"
            } else {
                "Added habit \"${name.trim()}\" successfully!"
            }
            _userMessage.emit(msg)
            _currentScreen.value = AppScreen.DASHBOARD
        }
        return true
    }

    fun resetTodayProgress() {
        viewModelScope.launch {
            repository.resetTodayProgress(todayDateString)
            HabitAppWidgetProvider.updateAllWidgets(getApplication())
            val msg = if (_appLanguage.value == AppLanguage.ARABIC) {
                "تم تصفير إنجاز اليوم بنجاح"
            } else {
                "Reset today's progress"
            }
            _userMessage.emit(msg)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            HabitAppWidgetProvider.updateAllWidgets(getApplication())
            val msg = if (_appLanguage.value == AppLanguage.ARABIC) {
                "تم مسح كافة العادات والسجلات بالكامل"
            } else {
                "All habits and records cleared"
            }
            _userMessage.emit(msg)
        }
    }

    fun loadSampleHabits() {
        viewModelScope.launch {
            repository.insertSampleHabits()
            HabitAppWidgetProvider.updateAllWidgets(getApplication())
            val msg = if (_appLanguage.value == AppLanguage.ARABIC) {
                "تم تحميل العادات النموذجية وسجل الإنجاز للأسبوعين الماضيين بنجاح!"
            } else {
                "Loaded rich sample habits and two weeks history successfully!"
            }
            _userMessage.emit(msg)
        }
    }

    fun toggleReminders(enabled: Boolean) {
        _remindersEnabled.value = enabled
        prefs.edit().putBoolean("reminders_enabled", enabled).apply()
        if (enabled) {
            val (h, m) = HabitReminderScheduler.parseTimeString(_reminderTime.value)
            HabitReminderScheduler.scheduleDailyReminder(getApplication(), h, m)
        } else {
            HabitReminderScheduler.cancelDailyReminder(getApplication())
        }
        viewModelScope.launch {
            val msg = if (enabled) {
                if (_appLanguage.value == AppLanguage.ARABIC) "تم تفعيل التذكير اليومي" else "Daily reminders enabled"
            } else {
                if (_appLanguage.value == AppLanguage.ARABIC) "تم إيقاف التذكير اليومي" else "Daily reminders disabled"
            }
            _userMessage.emit(msg)
        }
    }

    fun setReminderTime(timeString: String) {
        _reminderTime.value = timeString
        prefs.edit().putString("reminder_time", timeString).apply()
        if (_remindersEnabled.value) {
            val (h, m) = HabitReminderScheduler.parseTimeString(timeString)
            HabitReminderScheduler.scheduleDailyReminder(getApplication(), h, m)
        }
        viewModelScope.launch {
            val msg = if (_appLanguage.value == AppLanguage.ARABIC) {
                "تم ضبط وقت التذكير على: $timeString"
            } else {
                "Reminder time set to $timeString"
            }
            _userMessage.emit(msg)
        }
    }

    fun sendTestNotificationNow() {
        viewModelScope.launch {
            val items = todayHabitItems.value
            val pendingHabits = items.filter { !it.isCompletedToday }.map { it.habit.name }
            val title = if (_appLanguage.value == AppLanguage.ARABIC) {
                "✨ حان وقت إنجاز عاداتك اليومية!"
            } else {
                "✨ Time for your daily habits!"
            }
            val message = if (pendingHabits.isNotEmpty()) {
                if (_appLanguage.value == AppLanguage.ARABIC) {
                    "لديك ${pendingHabits.size} عادات متبقية لليوم لم تكتمل بعد. استمر في الحفاظ على سلسلتك!"
                } else {
                    "You have ${pendingHabits.size} pending habits today. Keep your streak alive!"
                }
            } else {
                if (_appLanguage.value == AppLanguage.ARABIC) {
                    "أحسنت! جميع عادات اليوم مكتملة بالكامل ✨"
                } else {
                    "Great job! All habits completed for today ✨"
                }
            }

            HabitNotificationHelper.showHabitReminderNotification(
                context = getApplication(),
                title = title,
                message = message,
                pendingHabitsList = pendingHabits
            )

            val confirmMsg = if (_appLanguage.value == AppLanguage.ARABIC) {
                "تم إرسال الإشعار التجريبي بنجاح! تفقد لوحة إشعارات جهازك"
            } else {
                "Test notification sent! Check your notification shade"
            }
            _userMessage.emit(confirmMsg)
        }
    }

    fun requestAiCoachAnalysis(customQuestion: String? = null) {
        viewModelScope.launch {
            _isAnalyzingHabits.value = true
            val habitsList = repository.allHabits.firstOrNull() ?: emptyList()
            val completionsList = repository.getAllCompletions().firstOrNull() ?: emptyList()
            val insight = HabitAiCoachService.analyzeHabits(
                context = getApplication(),
                habits = habitsList,
                completions = completionsList,
                isArabic = _appLanguage.value.isRtl,
                customQuestion = customQuestion
            )
            _coachInsight.value = insight
            _isAnalyzingHabits.value = false
        }
    }

    fun toggleVacationMode(enabled: Boolean) {
        _isVacationModeEnabled.value = enabled
        prefs.edit().putBoolean("vacation_mode_enabled", enabled).apply()
        viewModelScope.launch {
            val msg = if (enabled) {
                if (_appLanguage.value == AppLanguage.ARABIC) {
                    "🏖️ تم تفعيل وضع الإجازة: سلاسلك وزخمك محمية بالكامل!"
                } else {
                    "🏖️ Vacation Mode activated: Your streaks are 100% shielded!"
                }
            } else {
                if (_appLanguage.value == AppLanguage.ARABIC) {
                    "💪 مرحباً بعودتك! تم استئناف تتبع العادات اليومية بنشاط."
                } else {
                    "💪 Welcome back! Habit tracking resumed."
                }
            }
            _userMessage.emit(msg)
        }
    }

    fun getTodayFormattedDate(language: AppLanguage): String {
        val locale = if (language.isRtl) Locale("ar") else Locale.US
        val format = SimpleDateFormat("EEEE، d MMMM yyyy", locale)
        return format.format(Date())
    }
}
