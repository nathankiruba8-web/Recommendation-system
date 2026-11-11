package com.example.engine

import com.example.model.Criterion
import com.example.model.Decision
import com.example.model.DecisionOption
import com.example.model.DecisionRecommendation
import com.example.model.DecisionStatus
import kotlin.math.roundToInt

object DecisionRecommendationEngine {

    /**
     * Evaluates a decision using the Multi-Criteria Decision Analysis (MCDA) Weighted Sum Model.
     * Computes individual and overall scores, assigns ranks, and synthesizes recommendation rationales.
     */
    fun evaluateDecision(decision: Decision): Decision {
        if (decision.options.isEmpty() || decision.criteria.isEmpty()) {
            return decision
        }

        // Normalize weights so they sum strictly to 1.0
        val totalRawWeight = decision.criteria.sumOf { it.weight.toDouble() }.toFloat().coerceAtLeast(0.001f)
        val normalizedCriteria = decision.criteria.map {
            it.copy(weight = it.weight / totalRawWeight)
        }

        // Calculate weighted score for each option
        val evaluatedOptions = decision.options.map { option ->
            var weightedScoreSum = 0f
            normalizedCriteria.forEach { criterion ->
                val rawScore = option.scores[criterion.id] ?: 5.0f // default mid-score if unrated
                weightedScoreSum += rawScore * criterion.weight
            }
            // Scale out of 10.0
            val finalScore = (weightedScoreSum * 10f).roundToInt() / 10f
            option.copy(totalWeightedScore = finalScore)
        }.sortedByDescending { it.totalWeightedScore }

        // Assign ranks
        val rankedOptions = evaluatedOptions.mapIndexed { index, option ->
            option.copy(rank = index + 1)
        }

        val topOption = rankedOptions.firstOrNull() ?: return decision
        val secondOption = rankedOptions.getOrNull(1)

        // Confidence calculation based on score differential
        val scoreDiff = if (secondOption != null) {
            topOption.totalWeightedScore - secondOption.totalWeightedScore
        } else {
            3.0f
        }
        val confidencePercent = ((50f + (scoreDiff * 10f)).coerceIn(55f, 96f) * 10f).roundToInt() / 10f

        // Identify key strengths of top option
        val topStrengths = mutableListOf<String>()
        normalizedCriteria.forEach { criterion ->
            val topScore = topOption.scores[criterion.id] ?: 5.0f
            val runnerUpScore = secondOption?.scores?.get(criterion.id) ?: 5.0f
            if (topScore >= 7.5f || topScore > runnerUpScore) {
                topStrengths.add("Dominates in \"${criterion.name}\" (Score: ${topScore}/10, Weight: ${(criterion.weight * 100).roundToInt()}%)")
            }
        }
        if (topStrengths.isEmpty()) {
            topStrengths.add("Provides highest aggregate utility across all evaluated life priorities.")
        }

        // Identify trade-offs / potential vulnerabilities
        val tradeOffs = mutableListOf<String>()
        if (secondOption != null) {
            normalizedCriteria.forEach { criterion ->
                val topScore = topOption.scores[criterion.id] ?: 5.0f
                val runnerUpScore = secondOption.scores[criterion.id] ?: 5.0f
                if (runnerUpScore > topScore) {
                    tradeOffs.add("\"${secondOption.title}\" offers superior \"${criterion.name}\" (${runnerUpScore} vs ${topScore}/10).")
                }
            }
        }
        if (tradeOffs.isEmpty()) {
            tradeOffs.add("Requires monitoring implementation timeline and commitment consistency.")
        }

        val summary = "Based on Multi-Criteria Decision Analysis across ${normalizedCriteria.size} weighted dimensions, \"${topOption.title}\" is the recommended choice with a composite score of ${topOption.totalWeightedScore}/10 (Confidence: ${confidencePercent}%)."

        val firstStep = "Set an immediate target deadline and convert \"${topOption.title}\" into actionable milestones in your LifeSync Planner."

        val recommendation = DecisionRecommendation(
            winningOptionId = topOption.id,
            winningOptionTitle = topOption.title,
            confidenceScore = confidencePercent,
            executiveSummary = summary,
            keyStrengths = topStrengths.take(3),
            criticalTradeOffs = tradeOffs.take(3),
            suggestedFirstStep = firstStep
        )

        return decision.copy(
            criteria = normalizedCriteria,
            options = rankedOptions,
            recommendation = recommendation,
            status = DecisionStatus.EVALUATED
        )
    }
}
