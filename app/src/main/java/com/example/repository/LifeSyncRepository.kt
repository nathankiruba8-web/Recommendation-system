package com.example.repository

import com.example.engine.DecisionRecommendationEngine
import com.example.model.*
import com.example.network.ApiConfigurationManager
import com.example.network.AuthRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class LifeSyncRepository {

    private val _userProfile = MutableStateFlow(
        UserProfile(
            username = "Alex Mercer",
            email = "alex.mercer@lifesync.app",
            riskTolerance = "MODERATE",
            topPriorities = listOf("Work-Life Balance", "Career Acceleration", "Financial Freedom")
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _decisions = MutableStateFlow<List<Decision>>(createInitialSampleDecisions())
    val decisions: StateFlow<List<Decision>> = _decisions.asStateFlow()

    private val _goals = MutableStateFlow<List<Goal>>(createInitialSampleGoals())
    val goals: StateFlow<List<Goal>> = _goals.asStateFlow()

    private val _plannerTasks = MutableStateFlow<List<PlannerTask>>(createInitialSampleTasks())
    val plannerTasks: StateFlow<List<PlannerTask>> = _plannerTasks.asStateFlow()

    private val _connectionStatus = MutableStateFlow("Demo Mode (Offline Ready)")
    val connectionStatus: StateFlow<String> = _connectionStatus.asStateFlow()

    fun isDemoMode(): Boolean = ApiConfigurationManager.isDemoMode

    fun setDemoMode(enabled: Boolean) {
        ApiConfigurationManager.isDemoMode = enabled
        _connectionStatus.value = if (enabled) "Demo Mode (Offline Ready)" else "Ready to Connect"
    }

    fun updateServerUrl(url: String) {
        ApiConfigurationManager.updateBaseUrl(url)
    }

    suspend fun testBackendConnection(): Result<String> {
        return try {
            val response = ApiConfigurationManager.apiService.checkHealth()
            _connectionStatus.value = "Connected to Live Render Backend (${response.service})"
            Result.success("Success: Connected to ${response.service} (${response.status})")
        } catch (e: Exception) {
            val errorMsg = "Connection check failed: ${e.localizedMessage ?: "Unknown network error"}"
            _connectionStatus.value = "Offline / Connection Error"
            Result.failure(Exception(errorMsg))
        }
    }

    suspend fun login(username: String, pass: String): Result<UserProfile> {
        if (ApiConfigurationManager.isDemoMode) {
            val profile = _userProfile.value.copy(username = username)
            _userProfile.value = profile
            return Result.success(profile)
        }
        return try {
            val res = ApiConfigurationManager.apiService.login(AuthRequest(username, pass))
            ApiConfigurationManager.authToken = res.token
            _userProfile.value = res.user
            Result.success(res.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun saveDecision(decision: Decision): Decision {
        val evaluated = DecisionRecommendationEngine.evaluateDecision(decision)
        val currentList = _decisions.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.id == evaluated.id }
        if (existingIndex >= 0) {
            currentList[existingIndex] = evaluated
        } else {
            currentList.add(0, evaluated)
        }
        _decisions.value = currentList
        return evaluated
    }

    fun deleteDecision(decisionId: String) {
        _decisions.value = _decisions.value.filter { it.id != decisionId }
    }

    fun convertDecisionToGoal(decision: Decision): Goal {
        val recommendation = decision.recommendation
        val winningTitle = recommendation?.winningOptionTitle ?: decision.title
        val newGoal = Goal(
            title = "Execute: $winningTitle",
            description = recommendation?.suggestedFirstStep ?: decision.dilemmaDescription,
            category = decision.category,
            targetDate = decision.deadlineDate,
            progressPercent = 15,
            linkedDecisionId = decision.id,
            milestones = listOf(
                Milestone(title = "Finalize commitments and transition steps", isCompleted = false),
                Milestone(title = recommendation?.suggestedFirstStep ?: "Review initial results", isCompleted = false),
                Milestone(title = "Evaluate 90-day progress against expected criteria", isCompleted = false)
            )
        )
        addGoal(newGoal)

        // Also add an immediate Planner task
        addPlannerTask(
            PlannerTask(
                title = recommendation?.suggestedFirstStep ?: "Initiate plan for $winningTitle",
                date = "Today",
                priority = TaskPriority.HIGH,
                linkedGoalTitle = newGoal.title
            )
        )

        return newGoal
    }

    fun addGoal(goal: Goal) {
        _goals.value = listOf(goal) + _goals.value
    }

    fun toggleMilestone(goalId: String, milestoneId: String) {
        _goals.value = _goals.value.map { goal ->
            if (goal.id == goalId) {
                val updatedMilestones = goal.milestones.map { m ->
                    if (m.id == milestoneId) m.copy(isCompleted = !m.isCompleted) else m
                }
                val completedCount = updatedMilestones.count { it.isCompleted }
                val newProgress = if (updatedMilestones.isNotEmpty()) {
                    (completedCount * 100) / updatedMilestones.size
                } else 0
                goal.copy(milestones = updatedMilestones, progressPercent = newProgress)
            } else {
                goal
            }
        }
    }

    fun addPlannerTask(task: PlannerTask) {
        _plannerTasks.value = listOf(task) + _plannerTasks.value
    }

    fun togglePlannerTask(taskId: String) {
        _plannerTasks.value = _plannerTasks.value.map { task ->
            if (task.id == taskId) task.copy(isCompleted = !task.isCompleted) else task
        }
    }

    fun getAnalytics(): AnalyticsSummary {
        val totalDecisions = _decisions.value.size
        val decided = _decisions.value.count { it.status == DecisionStatus.EVALUATED || it.status == DecisionStatus.DECIDED }
        val activeGoals = _goals.value.size
        val avgProgress = if (_goals.value.isNotEmpty()) _goals.value.map { it.progressPercent }.average().toInt() else 0
        val avgConfidence = if (_decisions.value.isNotEmpty()) {
            _decisions.value.mapNotNull { it.recommendation?.confidenceScore }.average().toFloat()
        } else 82.5f

        val breakdown = _decisions.value.groupBy { it.category.displayName }.mapValues { it.value.size }

        return AnalyticsSummary(
            totalDecisionsCount = totalDecisions,
            decidedCount = decided,
            activeGoalsCount = activeGoals,
            goalCompletionRate = avgProgress,
            categoryBreakdown = breakdown,
            averageConfidenceScore = if (avgConfidence.isNaN()) 80f else avgConfidence
        )
    }

    private fun createInitialSampleDecisions(): List<Decision> {
        val c1 = Criterion(id = "c1", name = "Work-Life Balance", weight = 0.35f)
        val c2 = Criterion(id = "c2", name = "Compensation & Equity", weight = 0.30f)
        val c3 = Criterion(id = "c3", name = "Career Growth & Learning", weight = 0.20f)
        val c4 = Criterion(id = "c4", name = "Team Culture & Stability", weight = 0.15f)

        val opt1 = DecisionOption(
            id = "o1",
            title = "Accept Series-B AI Startup Offer",
            description = "High growth trajectory, generous equity grant, fast-paced environment",
            scores = mapOf("c1" to 6.0f, "c2" to 9.2f, "c3" to 9.5f, "c4" to 7.0f)
        )
        val opt2 = DecisionOption(
            id = "o2",
            title = "Stay at Mature Enterprise Company",
            description = "Established engineering role, predictable 40-hr week, lower risk",
            scores = mapOf("c1" to 8.8f, "c2" to 7.5f, "c3" to 6.2f, "c4" to 8.5f)
        )

        val initialDecision = Decision(
            id = "d1",
            title = "Career Crossroad: Startup Offer vs Current Role",
            dilemmaDescription = "Weighing long-term leadership acceleration against guaranteed stability and flexible hours.",
            category = DecisionCategory.CAREER,
            status = DecisionStatus.DRAFT,
            deadlineDate = "2026-12-15",
            criteria = listOf(c1, c2, c3, c4),
            options = listOf(opt1, opt2)
        )

        return listOf(DecisionRecommendationEngine.evaluateDecision(initialDecision))
    }

    private fun createInitialSampleGoals(): List<Goal> {
        return listOf(
            Goal(
                id = "g1",
                title = "Transition to Senior Tech Lead",
                description = "Master cloud-native architecture and lead end-to-end product delivery.",
                category = DecisionCategory.CAREER,
                targetDate = "2026-11-30",
                progressPercent = 66,
                milestones = listOf(
                    Milestone(title = "Complete advanced architecture certification", isCompleted = true),
                    Milestone(title = "Deliver microservices latency optimization project", isCompleted = true),
                    Milestone(title = "Mentor two junior software engineers", isCompleted = false)
                )
            ),
            Goal(
                id = "g2",
                title = "Emergency Fund & Wealth Reserve",
                description = "Build a dedicated 6-month liquid cushion in high-yield account.",
                category = DecisionCategory.FINANCE,
                targetDate = "2026-12-31",
                progressPercent = 50,
                milestones = listOf(
                    Milestone(title = "Audit monthly recurring subscriptions", isCompleted = true),
                    Milestone(title = "Automate $500 monthly transfer on payday", isCompleted = true),
                    Milestone(title = "Reach $15,000 threshold goal", isCompleted = false)
                )
            )
        )
    }

    private fun createInitialSampleTasks(): List<PlannerTask> {
        return listOf(
            PlannerTask(title = "Review startup term sheet and vesting schedule", date = "Today", time = "10:00 AM", priority = TaskPriority.HIGH),
            PlannerTask(title = "Draft 90-day learning roadmap for cloud certification", date = "Today", time = "02:30 PM", priority = TaskPriority.MEDIUM),
            PlannerTask(title = "Verify monthly investment allocation transfer", date = "Tomorrow", time = "09:00 AM", priority = TaskPriority.LOW)
        )
    }
}
