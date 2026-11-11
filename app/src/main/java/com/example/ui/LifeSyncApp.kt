package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.components.LifeSyncTopBar
import com.example.ui.screens.*
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.LifeSyncViewModel

@Composable
fun LifeSyncApp(
    viewModel: LifeSyncViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val decisions by viewModel.decisions.collectAsState()
    val goals by viewModel.goals.collectAsState()
    val plannerTasks by viewModel.plannerTasks.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()

    val showWizard by viewModel.showDecisionWizard.collectAsState()
    val selectedDecision by viewModel.selectedDecisionForDetail.collectAsState()
    val showAddGoal by viewModel.showAddGoalDialog.collectAsState()
    val showAddTask by viewModel.showAddTaskDialog.collectAsState()
    val showSettings by viewModel.showSettingsDialog.collectAsState()
    val testResult by viewModel.serverTestResult.collectAsState()
    val isTesting by viewModel.isTestingServer.collectAsState()

    Scaffold(
        topBar = {
            LifeSyncTopBar(
                connectionStatus = connectionStatus,
                onOpenSettings = { viewModel.openSettingsDialog() }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .testTag("main_navigation_bar")
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationBarItem(
                    selected = currentTab == AppNavTab.DASHBOARD,
                    onClick = { viewModel.selectTab(AppNavTab.DASHBOARD) },
                    icon = { Icon(if (currentTab == AppNavTab.DASHBOARD) Icons.Default.Dashboard else Icons.Outlined.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") },
                    modifier = Modifier.testTag("nav_dashboard")
                )
                NavigationBarItem(
                    selected = currentTab == AppNavTab.DECISIONS,
                    onClick = { viewModel.selectTab(AppNavTab.DECISIONS) },
                    icon = { Icon(if (currentTab == AppNavTab.DECISIONS) Icons.Default.Psychology else Icons.Outlined.Psychology, contentDescription = "Decisions") },
                    label = { Text("Decisions") },
                    modifier = Modifier.testTag("nav_decisions")
                )
                NavigationBarItem(
                    selected = currentTab == AppNavTab.GOALS,
                    onClick = { viewModel.selectTab(AppNavTab.GOALS) },
                    icon = { Icon(if (currentTab == AppNavTab.GOALS) Icons.Default.Flag else Icons.Outlined.Flag, contentDescription = "Goals") },
                    label = { Text("Goals") },
                    modifier = Modifier.testTag("nav_goals")
                )
                NavigationBarItem(
                    selected = currentTab == AppNavTab.PLANNER,
                    onClick = { viewModel.selectTab(AppNavTab.PLANNER) },
                    icon = { Icon(if (currentTab == AppNavTab.PLANNER) Icons.Default.CalendarToday else Icons.Outlined.CalendarToday, contentDescription = "Planner") },
                    label = { Text("Planner") },
                    modifier = Modifier.testTag("nav_planner")
                )
                NavigationBarItem(
                    selected = currentTab == AppNavTab.ANALYTICS,
                    onClick = { viewModel.selectTab(AppNavTab.ANALYTICS) },
                    icon = { Icon(if (currentTab == AppNavTab.ANALYTICS) Icons.Default.Analytics else Icons.Outlined.Analytics, contentDescription = "Analytics") },
                    label = { Text("Analytics") },
                    modifier = Modifier.testTag("nav_analytics")
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = modifier.testTag("lifesync_root_scaffold")
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentTab) {
                AppNavTab.DASHBOARD -> DashboardScreen(
                    userProfile = userProfile,
                    decisions = decisions,
                    goals = goals,
                    plannerTasks = plannerTasks,
                    onOpenDecision = { viewModel.openDecisionDetail(it) },
                    onNewDecisionClick = { viewModel.openDecisionWizard() },
                    onNewGoalClick = { viewModel.openAddGoalDialog() }
                )
                AppNavTab.DECISIONS -> DecisionsScreen(
                    decisions = decisions,
                    onOpenDecision = { viewModel.openDecisionDetail(it) },
                    onDeleteDecision = { viewModel.deleteDecision(it) },
                    onNewDecisionClick = { viewModel.openDecisionWizard() }
                )
                AppNavTab.GOALS -> GoalsScreen(
                    goals = goals,
                    onToggleMilestone = { gId, mId -> viewModel.toggleMilestone(gId, mId) },
                    onAddGoalClick = { viewModel.openAddGoalDialog() }
                )
                AppNavTab.PLANNER -> PlannerScreen(
                    tasks = plannerTasks,
                    onToggleTask = { viewModel.togglePlannerTask(it) },
                    onAddTaskClick = { viewModel.openAddTaskDialog() }
                )
                AppNavTab.ANALYTICS -> AnalyticsScreen(
                    analytics = viewModel.getAnalytics()
                )
            }
        }

        // Dialogs
        if (showWizard) {
            DecisionWizardDialog(
                onDismiss = { viewModel.closeDecisionWizard() },
                onSaveDecision = { title, dilemma, cat, deadline, criteria, options ->
                    viewModel.saveDecision(title, dilemma, cat, deadline, criteria, options)
                }
            )
        }

        selectedDecision?.let { decision ->
            DecisionDetailDialog(
                decision = decision,
                onDismiss = { viewModel.closeDecisionDetail() },
                onConvertToGoal = { viewModel.convertCurrentDecisionToGoal() }
            )
        }

        if (showAddGoal) {
            AddGoalDialog(
                onDismiss = { viewModel.closeAddGoalDialog() },
                onSaveGoal = { title, desc, cat, date, milestones ->
                    viewModel.addGoal(title, desc, cat, date, milestones)
                }
            )
        }

        if (showAddTask) {
            AddTaskDialog(
                onDismiss = { viewModel.closeAddTaskDialog() },
                onSaveTask = { title, date, priority, goal ->
                    viewModel.addPlannerTask(title, date, priority, goal)
                }
            )
        }

        if (showSettings) {
            SettingsDialog(
                onDismiss = { viewModel.closeSettingsDialog() },
                onTestConnection = { url -> viewModel.testServerConnection(url) },
                onToggleDemoMode = { enabled -> viewModel.toggleDemoMode(enabled) },
                testResult = testResult,
                isTesting = isTesting
            )
        }
    }
}
