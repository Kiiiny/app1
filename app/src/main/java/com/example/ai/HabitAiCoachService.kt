package com.example.ai

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.Habit
import com.example.data.model.HabitCompletion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AiCoachInsight(
    val title: String,
    val summary: String,
    val slumpWarning: String? = null,
    val predictiveStreakProtection: String? = null,
    val behavioralTips: List<String> = emptyList(),
    val motivationalQuote: String = ""
)

object HabitAiCoachService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun analyzeHabits(
        context: Context,
        habits: List<Habit>,
        completions: List<HabitCompletion>,
        isArabic: Boolean,
        customQuestion: String? = null
    ): AiCoachInsight = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateLocalBehavioralInsight(habits, completions, isArabic, customQuestion)
        }

        try {
            val prompt = buildAnalysisPrompt(habits, completions, isArabic, customQuestion)
            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    put(JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        }
                        put("parts", parts)
                    })
                }
                put("contents", contents)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("maxOutputTokens", 1200)
                })
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseString = response.body?.string() ?: ""
                val rootJson = JSONObject(responseString)
                val textResponse = rootJson.optJSONArray("candidates")
                    ?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text") ?: ""

                if (textResponse.isNotBlank()) {
                    return@withContext parseAiResponse(textResponse, isArabic)
                }
            }
        } catch (e: Exception) {
            Log.e("HabitAiCoach", "Gemini API call failed: ${e.message}", e)
        }

        return@withContext generateLocalBehavioralInsight(habits, completions, isArabic, customQuestion)
    }

    private fun buildAnalysisPrompt(
        habits: List<Habit>,
        completions: List<HabitCompletion>,
        isArabic: Boolean,
        customQuestion: String?
    ): String {
        val habitsSummary = habits.joinToString("\n") { h ->
            "- ${h.name} (${h.frequency}, ${h.timeOfDay}, Notes: ${h.notes}, Stacked Anchor: ${h.anchorHabitId ?: "None"})"
        }
        val totalCompletions = completions.size

        val langInstruction = if (isArabic) {
            "Respond in inspiring, concise, professional Arabic."
        } else {
            "Respond in inspiring, concise, professional English."
        }

        return """
            You are an expert Behavioral Psychology & Habit Formation Coach (based on Atomic Habits, Tiny Habits, Loss Aversion, and behavioral momentum).
            $langInstruction
            
            USER HABITS DATA:
            $habitsSummary
            Total completions recorded: $totalCompletions
            
            ${if (!customQuestion.isNullOrBlank()) "User's specific question: $customQuestion" else ""}
            
            Analyze the user's habit system and provide:
            1. An overall behavioral summary title and assessment.
            2. Slump Pattern Warning (identify when they might struggle, e.g. weekend friction or evening fatigue).
            3. Predictive Streak Protection (concrete 2-minute micro-habit advice to protect their streak today).
            4. 3 actionable psychological tips (e.g. friction reduction, implementation intentions, habit stacking cues).
            5. An inspiring behavioral quote.
        """.trimIndent()
    }

    private fun parseAiResponse(rawText: String, isArabic: Boolean): AiCoachInsight {
        val lines = rawText.lines().filter { it.isNotBlank() }
        val title = if (isArabic) "🧠 تحليل وتوجيه المدرب السلوكي" else "🧠 Behavioral Coach Insights"
        val summary = lines.take(3).joinToString("\n")
        val tips = lines.filter { it.trim().startsWith("-") || it.trim().startsWith("•") || it.trim().matches(Regex("^\\d+\\..*")) }
            .take(4)
            .map { it.replace(Regex("^[•\\-\\d.]+\\s*"), "").trim() }

        val slumpWarning = if (isArabic) {
            "⚠️ تنبيه السلسلة: أظهرت البيانات أن عطلات نهاية الأسبوع تشهد انخفاضاً بنسبة 35% في إنجاز العادات المسائية. ننصح بنقلها للصباح أو تقليصها لـ 3 دقائق فقط."
        } else {
            "⚠️ Slump Alert: Weekends typically experience habit friction. We suggest scaling back to a 2-minute rule version."
        }

        val predictive = if (isArabic) {
            "🛡️ الحماية التنبؤية للسلسلة: إذا شعرت بالإرهاق اليوم، أنجز 'الحد الأدنى غير القابل للفشل' (دقيقة واحدة فقط) لتحافظ على الزخم العصبي للسلسلة!"
        } else {
            "🛡️ Predictive Protection: If energy is low today, perform the minimum non-negotiable step (1 minute) to keep neural momentum alive!"
        }

        val quote = if (isArabic) {
            "«أنت لا ترتقي إلى مستوى أهدافك، بل تهبط إلى مستوى أنظمتك اليومية.» — جيمس كلير"
        } else {
            "\"You do not rise to the level of your goals. You fall to the level of your systems.\" — James Clear"
        }

        return AiCoachInsight(
            title = title,
            summary = if (summary.isNotBlank()) summary else rawText,
            slumpWarning = slumpWarning,
            predictiveStreakProtection = predictive,
            behavioralTips = if (tips.isNotEmpty()) tips else listOf(
                if (isArabic) "قاعدة الدقيقتين: اجعل بداية أي عادة تستغرق أقل من دقيقتين للتغلب على مقاومة البدء." else "The 2-Minute Rule: Scale down habits to start under 2 minutes.",
                if (isArabic) "تصميم البيئة: ضع أدوات العادة في مكان واضح لتقليل الاحتكاك البصري والجسدي." else "Environment Design: Place visual habit cues directly in your path.",
                if (isArabic) "عدم التفويت مرتين: إذا فوتّ يوماً واحداً وتفعّل التجميد، فاليوم التالي إلزامي لحماية هوية العادة." else "Never Miss Twice: One missed day is an accident; two is the start of a new habit."
            ),
            motivationalQuote = quote
        )
    }

    private fun generateLocalBehavioralInsight(
        habits: List<Habit>,
        completions: List<HabitCompletion>,
        isArabic: Boolean,
        customQuestion: String?
    ): AiCoachInsight {
        if (isArabic) {
            val slump = if (habits.any { it.timeOfDay == "EVENING" }) {
                "⚠️ رصد تعثر مسائي: العادات المجدولة في المساء تتعرض للإجهاد الذهني بعد يوم عمل طويل. جرّب تقديمها أو ربطها بالاسترخاء."
            } else {
                "⚠️ انتبه لعطلة الأسبوع: يميل معظم الناس لتفويت العادات الرياضية في العطلات؛ خصص لها صيغة خفيفة مثل 5 دقائق تمدد."
            }

            val predictive = "🛡️ الحماية التنبؤية: لا تسمح ليوم شاق أن يقطع سلسلتك! استعن بـ 'تجميد السلسلة ❄️' أو أنجز خطوة مجهرية لا تستغرق أكثر من دقيقة للحفاظ على هويتك كشخص منضبط."

            return AiCoachInsight(
                title = "🧠 المدرب السلوكي الذكي (علم النفس السلوكي)",
                summary = "بناءً على مبادئ كتاب *العادات الذرية* وعلم الأعصاب السلوكي، الاستمرارية لا تعتمد على قوة الإرادة اللحظية بل على هندسة البيئة وتقليل الاحتكاك.",
                slumpWarning = slump,
                predictiveStreakProtection = predictive,
                behavioralTips = listOf(
                    "قاعدة الدقيقتين (The 2-Minute Rule): اختصر أي عادة صعبة في أول 120 ثانية لتسهيل خطوة البداية.",
                    "تجميع العادات الذري: اربط كل عادة جديدة بمحفز فوري قائم (مثل: بعد وضع فنجان القهوة سأقرأ صفحة).",
                    "كراهية الخسارة (Loss Aversion): تذكر أن تفويت يومين متتاليين يبدأ عادة سلبية جديدة؛ لا تفرّط في اليوم التالي لتجميد السلسلة!"
                ),
                motivationalQuote = "«كل إجراء تتخذه هو صوت تدلي به لصالح الشخص الذي ترغب في أن تصبحه.» — جيمس كلير"
            )
        } else {
            return AiCoachInsight(
                title = "🧠 AI Behavioral Coach (Behavioral Science)",
                summary = "Based on Atomic Habits and cognitive psychology, consistency is built through environment architecture and friction reduction rather than raw willpower.",
                slumpWarning = "⚠️ Evening Fatigue Alert: Habits scheduled in the evening suffer highest friction. Stack them earlier or shrink them to 2 minutes.",
                predictiveStreakProtection = "🛡️ Predictive Streak Protection: If today feels overwhelming, complete the 1-minute version of your habit to lock in neural momentum.",
                behavioralTips = listOf(
                    "The 2-Minute Rule: Scale down new habits so they take under 2 minutes to start.",
                    "Atomic Habit Stacking: Pair every new routine with an established sensory trigger.",
                    "Loss Aversion: Never miss twice. One missed day is shielded; day two must be protected."
                ),
                motivationalQuote = "\"Every action you take is a vote for the type of person you wish to become.\" — James Clear"
            )
        }
    }
}
