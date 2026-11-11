package com.example.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.UUID

@JsonClass(generateAdapter = true)
data class UserProfile(
    val id: String = UUID.randomUUID().toString(),
    val username: String = "LifeSync User",
    val email: String = "user@lifesync.app",
    val riskTolerance: String = "MODERATE", // CONSERVATIVE, MODERATE, AGGRESSIVE
    val topPriorities: List<String> = listOf("Work-Life Balance", "Career Growth", "Financial Security")
)

enum class DecisionCategory(val displayName: String) {
    CAREER("Career & Work"),
    FINANCE("Financial & Investment"),
    HEALTH("Health & Wellness"),
    RELATIONSHIPS("Relationships & Social"),
    EDUCATION("Education & Learning"),
    LIFESTYLE("Lifestyle & Relocation")
}

enum class DecisionStatus(val displayName: String) {
    DRAFT("Drafting"),
    EVALUATED("Evaluated"),
    DECIDED("Decided"),
    ARCHIVED("Archived")
}

@JsonClass(generateAdapter = true)
data class Criterion(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val weight: Float = 0.25f, // 0.0 to 1.0 (Sum of weights = 1.0)
    val description: String = ""
)

@JsonClass(generateAdapter = true)
data class DecisionOption(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val pros: List<String> = emptyList(),
    val cons: List<String> = emptyList(),
    // Criterion ID -> Score (1.0 to 10.0)
    val scores: Map<String, Float> = emptyMap(),
    val totalWeightedScore: Float = 0f,
    val rank: Int = 1
)

@JsonClass(generateAdapter = true)
data class DecisionRecommendation(
    val winningOptionId: String = "",
    val winningOptionTitle: String = "",
    val confidenceScore: Float = 0f, // 0 to 100%
    val executiveSummary: String = "",
    val keyStrengths: List<String> = emptyList(),
    val criticalTradeOffs: List<String> = emptyList(),
    val suggestedFirstStep: String = ""
)

@JsonClass(generateAdapter = true)
data class Decision(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val dilemmaDescription: String,
    val category: DecisionCategory = DecisionCategory.CAREER,
    val status: DecisionStatus = DecisionStatus.DRAFT,
    val deadlineDate: String = "2026-12-31",
    val criteria: List<Criterion> = emptyList(),
    val options: List<DecisionOption> = emptyList(),
    val recommendation: DecisionRecommendation? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class Milestone(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val isCompleted: Boolean = false,
    val dueDate: String = ""
)

@JsonClass(generateAdapter = true)
data class Goal(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val category: DecisionCategory = DecisionCategory.CAREER,
    val targetDate: String = "2026-12-31",
    val progressPercent: Int = 0, // 0 to 100
    val linkedDecisionId: String? = null,
    val milestones: List<Milestone> = emptyList()
)

enum class TaskPriority {
    LOW, MEDIUM, HIGH
}

@JsonClass(generateAdapter = true)
data class PlannerTask(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val date: String, // YYYY-MM-DD
    val time: String = "09:00 AM",
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val isCompleted: Boolean = false,
    val linkedGoalTitle: String? = null
)

@JsonClass(generateAdapter = true)
data class AnalyticsSummary(
    val totalDecisionsCount: Int = 0,
    val decidedCount: Int = 0,
    val activeGoalsCount: Int = 0,
    val goalCompletionRate: Int = 0,
    val categoryBreakdown: Map<String, Int> = emptyMap(),
    val averageConfidenceScore: Float = 0f
)
