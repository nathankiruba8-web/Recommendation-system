package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.*
import com.example.repository.LifeSyncRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppNavTab(val label: String) {
    DASHBOARD("Dashboard"),
    DECISIONS("Decisions"),
    GOALS("Goals"),
    PLANNER("Planner"),
    ANALYTICS("Analytics")
}

class LifeSyncViewModel(
    private val repository: LifeSyncRepository = LifeSyncRepository()
) : ViewModel() {

    private val _currentTab = MutableStateFlow(AppNavTab.DASHBOARD)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    val decisions: StateFlow<List<Decision>> = repository.decisions
    val goals: StateFlow<List<Goal>> = repository.goals
    val plannerTasks: StateFlow<List<PlannerTask>> = repository.plannerTasks
    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val connectionStatus: StateFlow<String> = repository.connectionStatus

    // Dialog & Flow States
    private val _showDecisionWizard = MutableStateFlow(false)
    val showDecisionWizard: StateFlow<Boolean> = _showDecisionWizard.asStateFlow()

    private val _selectedDecisionForDetail = MutableStateFlow<Decision?>(null)
    val selectedDecisionForDetail: StateFlow<Decision?> = _selectedDecisionForDetail.asStateFlow()

    private val _showAddGoalDialog = MutableStateFlow(false)
    val showAddGoalDialog: StateFlow<Boolean> = _showAddGoalDialog.asStateFlow()

    private val _showAddTaskDialog = MutableStateFlow(false)
    val showAddTaskDialog: StateFlow<Boolean> = _showAddTaskDialog.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _serverTestResult = MutableStateFlow<String?>(null)
    val serverTestResult: StateFlow<String?> = _serverTestResult.asStateFlow()

    private val _isTestingServer = MutableStateFlow(false)
    val isTestingServer: StateFlow<Boolean> = _isTestingServer.asStateFlow()

    fun selectTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun openDecisionWizard() {
        _showDecisionWizard.value = true
    }

    fun closeDecisionWizard() {
        _showDecisionWizard.value = false
    }

    fun openDecisionDetail(decision: Decision) {
        _selectedDecisionForDetail.value = decision
    }

    fun closeDecisionDetail() {
        _selectedDecisionForDetail.value = null
    }

    fun openAddGoalDialog() {
        _showAddGoalDialog.value = true
    }

    fun closeAddGoalDialog() {
        _showAddGoalDialog.value = false
    }

    fun openAddTaskDialog() {
        _showAddTaskDialog.value = true
    }

    fun closeAddTaskDialog() {
        _showAddTaskDialog.value = false
    }

    fun openSettingsDialog() {
        _serverTestResult.value = null
        _showSettingsDialog.value = true
    }

    fun closeSettingsDialog() {
        _showSettingsDialog.value = false
    }

    fun saveDecision(
        title: String,
        dilemma: String,
        category: DecisionCategory,
        deadline: String,
        criteria: List<Criterion>,
        options: List<DecisionOption>
    ) {
        val newDecision = Decision(
            title = title.ifBlank { "Untitled Decision" },
            dilemmaDescription = dilemma,
            category = category,
            deadlineDate = deadline,
            criteria = criteria,
            options = options
        )
        val evaluated = repository.saveDecision(newDecision)
        _showDecisionWizard.value = false
        _selectedDecisionForDetail.value = evaluated
    }

    fun deleteDecision(id: String) {
        repository.deleteDecision(id)
        if (_selectedDecisionForDetail.value?.id == id) {
            _selectedDecisionForDetail.value = null
        }
    }

    fun convertCurrentDecisionToGoal() {
        val current = _selectedDecisionForDetail.value ?: return
        repository.convertDecisionToGoal(current)
        _selectedDecisionForDetail.value = null
        _currentTab.value = AppNavTab.GOALS
    }

    fun toggleMilestone(goalId: String, milestoneId: String) {
        repository.toggleMilestone(goalId, milestoneId)
    }

    fun addGoal(title: String, desc: String, category: DecisionCategory, targetDate: String, milestonesList: List<String>) {
        val milestones = milestonesList.filter { it.isNotBlank() }.map {
            Milestone(title = it.trim(), isCompleted = false)
        }
        val goal = Goal(
            title = title,
            description = desc,
            category = category,
            targetDate = targetDate,
            milestones = milestones
        )
        repository.addGoal(goal)
        _showAddGoalDialog.value = false
    }

    fun togglePlannerTask(taskId: String) {
        repository.togglePlannerTask(taskId)
    }

    fun addPlannerTask(title: String, date: String, priority: TaskPriority, linkedGoal: String?) {
        val task = PlannerTask(
            title = title,
            date = date.ifBlank { "Today" },
            priority = priority,
            linkedGoalTitle = linkedGoal?.ifBlank { null }
        )
        repository.addPlannerTask(task)
        _showAddTaskDialog.value = false
    }

    fun testServerConnection(url: String) {
        viewModelScope.launch {
            _isTestingServer.value = true
            _serverTestResult.value = null
            repository.updateServerUrl(url)
            val result = repository.testBackendConnection()
            result.onSuccess {
                _serverTestResult.value = it
            }.onFailure {
                _serverTestResult.value = "Error: ${it.message}"
            }
            _isTestingServer.value = false
        }
    }

    fun toggleDemoMode(enabled: Boolean) {
        repository.setDemoMode(enabled)
    }

    fun getAnalytics(): AnalyticsSummary = repository.getAnalytics()
}
