package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Criterion
import com.example.model.DecisionCategory
import com.example.model.DecisionOption
import java.util.UUID
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecisionWizardDialog(
    onDismiss: () -> Unit,
    onSaveDecision: (
        title: String,
        dilemma: String,
        category: DecisionCategory,
        deadline: String,
        criteria: List<Criterion>,
        options: List<DecisionOption>
    ) -> Unit
) {
    var step by remember { mutableStateOf(1) }

    // Step 1 State
    var title by remember { mutableStateOf("") }
    var dilemma by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(DecisionCategory.CAREER) }
    var deadline by remember { mutableStateOf("2026-12-15") }

    // Step 2 State (Options)
    var option1Title by remember { mutableStateOf("Option A: Move Ahead") }
    var option1Desc by remember { mutableStateOf("Pursue high reward initiative with calculated risk") }
    var option2Title by remember { mutableStateOf("Option B: Maintain Course") }
    var option2Desc by remember { mutableStateOf("Prioritize stability, balance, and gradual refinement") }

    // Step 3 State (Criteria & weights)
    var c1Name by remember { mutableStateOf("Long-Term Value & Fulfillment") }
    var c1Weight by remember { mutableStateOf(0.40f) }
    var c2Name by remember { mutableStateOf("Financial Return & Security") }
    var c2Weight by remember { mutableStateOf(0.35f) }
    var c3Name by remember { mutableStateOf("Effort & Time Commitment") }
    var c3Weight by remember { mutableStateOf(0.25f) }

    // Step 4 State (Option ratings 1..10)
    var opt1Crit1 by remember { mutableStateOf(8.5f) }
    var opt1Crit2 by remember { mutableStateOf(7.0f) }
    var opt1Crit3 by remember { mutableStateOf(5.5f) }

    var opt2Crit1 by remember { mutableStateOf(6.0f) }
    var opt2Crit2 by remember { mutableStateOf(8.5f) }
    var opt2Crit3 by remember { mutableStateOf(9.0f) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .testTag("decision_wizard_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Decision Recommendation Wizard",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Step $step of 4: " + when (step) {
                                1 -> "Define Dilemma"
                                2 -> "Identify Options"
                                3 -> "Weigh Priorities"
                                else -> "Rate Option Matrix"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.minimumInteractiveComponentSize()) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Progress Indicator
                LinearProgressIndicator(
                    progress = { step / 4f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .clip(RoundedCornerShape(4.dp))
                )

                // Step Content
                Box(modifier = Modifier.weight(1f)) {
                    when (step) {
                        1 -> StepOneDilemma(
                            title = title, onTitleChange = { title = it },
                            dilemma = dilemma, onDilemmaChange = { dilemma = it },
                            selectedCategory = selectedCategory, onCategoryChange = { selectedCategory = it },
                            deadline = deadline, onDeadlineChange = { deadline = it }
                        )
                        2 -> StepTwoOptions(
                            opt1Title = option1Title, onOpt1TitleChange = { option1Title = it },
                            opt1Desc = option1Desc, onOpt1DescChange = { option1Desc = it },
                            opt2Title = option2Title, onOpt2TitleChange = { option2Title = it },
                            opt2Desc = option2Desc, onOpt2DescChange = { option2Desc = it }
                        )
                        3 -> StepThreeCriteria(
                            c1Name = c1Name, onC1NameChange = { c1Name = it }, c1Weight = c1Weight, onC1WeightChange = { c1Weight = it },
                            c2Name = c2Name, onC2NameChange = { c2Name = it }, c2Weight = c2Weight, onC2WeightChange = { c2Weight = it },
                            c3Name = c3Name, onC3NameChange = { c3Name = it }, c3Weight = c3Weight, onC3WeightChange = { c3Weight = it }
                        )
                        4 -> StepFourRatings(
                            opt1Title = option1Title, opt2Title = option2Title,
                            c1Name = c1Name, c2Name = c2Name, c3Name = c3Name,
                            opt1Crit1 = opt1Crit1, onOpt1Crit1Change = { opt1Crit1 = it },
                            opt1Crit2 = opt1Crit2, onOpt1Crit2Change = { opt1Crit2 = it },
                            opt1Crit3 = opt1Crit3, onOpt1Crit3Change = { opt1Crit3 = it },
                            opt2Crit1 = opt2Crit1, onOpt2Crit1Change = { opt2Crit1 = it },
                            opt2Crit2 = opt2Crit2, onOpt2Crit2Change = { opt2Crit2 = it },
                            opt2Crit3 = opt2Crit3, onOpt2Crit3Change = { opt2Crit3 = it }
                        )
                    }
                }

                // Bottom Navigation Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (step > 1) {
                        OutlinedButton(
                            onClick = { step -= 1 },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Back")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    if (step < 4) {
                        Button(
                            onClick = {
                                if (step == 1 && title.isBlank()) {
                                    title = "Personal Strategic Decision"
                                }
                                step += 1
                            },
                            modifier = Modifier.testTag("wizard_next_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Continue")
                        }
                    } else {
                        Button(
                            onClick = {
                                val crit1Id = UUID.randomUUID().toString()
                                val crit2Id = UUID.randomUUID().toString()
                                val crit3Id = UUID.randomUUID().toString()

                                val criteriaList = listOf(
                                    Criterion(id = crit1Id, name = c1Name, weight = c1Weight),
                                    Criterion(id = crit2Id, name = c2Name, weight = c2Weight),
                                    Criterion(id = crit3Id, name = c3Name, weight = c3Weight)
                                )

                                val optionsList = listOf(
                                    DecisionOption(
                                        title = option1Title,
                                        description = option1Desc,
                                        scores = mapOf(crit1Id to opt1Crit1, crit2Id to opt1Crit2, crit3Id to opt1Crit3)
                                    ),
                                    DecisionOption(
                                        title = option2Title,
                                        description = option2Desc,
                                        scores = mapOf(crit1Id to opt2Crit1, crit2Id to opt2Crit2, crit3Id to opt2Crit3)
                                    )
                                )

                                onSaveDecision(title, dilemma, selectedCategory, deadline, criteriaList, optionsList)
                            },
                            modifier = Modifier.testTag("wizard_evaluate_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Evaluate & Recommend")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepOneDilemma(
    title: String, onTitleChange: (String) -> Unit,
    dilemma: String, onDilemmaChange: (String) -> Unit,
    selectedCategory: DecisionCategory, onCategoryChange: (DecisionCategory) -> Unit,
    deadline: String, onDeadlineChange: (String) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Decision Title") },
                placeholder = { Text("e.g. Choose Between Job Offer vs Current Role") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wizard_title_input"),
                singleLine = true
            )
        }
        item {
            OutlinedTextField(
                value = dilemma,
                onValueChange = onDilemmaChange,
                label = { Text("Dilemma Description & Context") },
                placeholder = { Text("Explain what you are trying to solve and what is at stake...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            )
        }
        item {
            Text("Category", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp)) {
                DecisionCategory.values().take(3).forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { onCategoryChange(cat) },
                        label = { Text(cat.displayName) }
                    )
                }
            }
        }
        item {
            OutlinedTextField(
                value = deadline,
                onValueChange = onDeadlineChange,
                label = { Text("Target Decision Date") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

@Composable
private fun StepTwoOptions(
    opt1Title: String, onOpt1TitleChange: (String) -> Unit,
    opt1Desc: String, onOpt1DescChange: (String) -> Unit,
    opt2Title: String, onOpt2TitleChange: (String) -> Unit,
    opt2Desc: String, onOpt2DescChange: (String) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Option 1", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = opt1Title,
                onValueChange = onOpt1TitleChange,
                label = { Text("Option 1 Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = opt1Desc,
                onValueChange = onOpt1DescChange,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Text("Option 2", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = opt2Title,
                onValueChange = onOpt2TitleChange,
                label = { Text("Option 2 Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = opt2Desc,
                onValueChange = onOpt2DescChange,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StepThreeCriteria(
    c1Name: String, onC1NameChange: (String) -> Unit, c1Weight: Float, onC1WeightChange: (Float) -> Unit,
    c2Name: String, onC2NameChange: (String) -> Unit, c2Weight: Float, onC2WeightChange: (Float) -> Unit,
    c3Name: String, onC3NameChange: (String) -> Unit, c3Weight: Float, onC3WeightChange: (Float) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text(
                "Define Evaluation Criteria & Relative Importance",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        item {
            OutlinedTextField(value = c1Name, onValueChange = onC1NameChange, label = { Text("Criterion 1") }, modifier = Modifier.fillMaxWidth())
            Text("Importance Weight: ${(c1Weight * 100).roundToInt()}%", style = MaterialTheme.typography.labelSmall)
            Slider(value = c1Weight, onValueChange = onC1WeightChange, valueRange = 0.1f..0.8f)
        }
        item {
            OutlinedTextField(value = c2Name, onValueChange = onC2NameChange, label = { Text("Criterion 2") }, modifier = Modifier.fillMaxWidth())
            Text("Importance Weight: ${(c2Weight * 100).roundToInt()}%", style = MaterialTheme.typography.labelSmall)
            Slider(value = c2Weight, onValueChange = onC2WeightChange, valueRange = 0.1f..0.8f)
        }
        item {
            OutlinedTextField(value = c3Name, onValueChange = onC3NameChange, label = { Text("Criterion 3") }, modifier = Modifier.fillMaxWidth())
            Text("Importance Weight: ${(c3Weight * 100).roundToInt()}%", style = MaterialTheme.typography.labelSmall)
            Slider(value = c3Weight, onValueChange = onC3WeightChange, valueRange = 0.1f..0.8f)
        }
    }
}

@Composable
private fun StepFourRatings(
    opt1Title: String, opt2Title: String,
    c1Name: String, c2Name: String, c3Name: String,
    opt1Crit1: Float, onOpt1Crit1Change: (Float) -> Unit,
    opt1Crit2: Float, onOpt1Crit2Change: (Float) -> Unit,
    opt1Crit3: Float, onOpt1Crit3Change: (Float) -> Unit,
    opt2Crit1: Float, onOpt2Crit1Change: (Float) -> Unit,
    opt2Crit2: Float, onOpt2Crit2Change: (Float) -> Unit,
    opt2Crit3: Float, onOpt2Crit3Change: (Float) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text(
                "Rate Each Option from 1 (Poor) to 10 (Outstanding)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(opt1Title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("$c1Name: ${(opt1Crit1 * 10).roundToInt() / 10f} / 10", style = MaterialTheme.typography.labelSmall)
                    Slider(value = opt1Crit1, onValueChange = onOpt1Crit1Change, valueRange = 1f..10f)

                    Text("$c2Name: ${(opt1Crit2 * 10).roundToInt() / 10f} / 10", style = MaterialTheme.typography.labelSmall)
                    Slider(value = opt1Crit2, onValueChange = onOpt1Crit2Change, valueRange = 1f..10f)

                    Text("$c3Name: ${(opt1Crit3 * 10).roundToInt() / 10f} / 10", style = MaterialTheme.typography.labelSmall)
                    Slider(value = opt1Crit3, onValueChange = onOpt1Crit3Change, valueRange = 1f..10f)
                }
            }
        }
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(opt2Title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    Text("$c1Name: ${(opt2Crit1 * 10).roundToInt() / 10f} / 10", style = MaterialTheme.typography.labelSmall)
                    Slider(value = opt2Crit1, onValueChange = onOpt2Crit1Change, valueRange = 1f..10f)

                    Text("$c2Name: ${(opt2Crit2 * 10).roundToInt() / 10f} / 10", style = MaterialTheme.typography.labelSmall)
                    Slider(value = opt2Crit2, onValueChange = onOpt2Crit2Change, valueRange = 1f..10f)

                    Text("$c3Name: ${(opt2Crit3 * 10).roundToInt() / 10f} / 10", style = MaterialTheme.typography.labelSmall)
                    Slider(value = opt2Crit3, onValueChange = onOpt2Crit3Change, valueRange = 1f..10f)
                }
            }
        }
    }
}
