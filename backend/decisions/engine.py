from .models import Decision, Criterion, DecisionOption, DecisionRecommendation

def run_mcda_evaluation(decision: Decision) -> Decision:
    criteria = list(decision.criteria.all())
    options = list(decision.options.all())

    if not criteria or not options:
        return decision

    # Normalize weights so sum is 1.0
    total_raw_weight = sum(c.weight for c in criteria)
    if total_raw_weight <= 0:
        total_raw_weight = 1.0

    normalized_weights = {str(c.id): (c.weight / total_raw_weight) for c in criteria}

    # Evaluate each option
    for option in options:
        weighted_sum = 0.0
        scores_map = option.scores or {}
        for c in criteria:
            cid = str(c.id)
            score = float(scores_map.get(cid, 5.0))
            weighted_sum += score * normalized_weights[cid]
        option.total_weighted_score = round(weighted_sum, 1)

    # Sort descending
    options.sort(key=lambda o: o.total_weighted_score, reverse=True)
    for idx, opt in enumerate(options):
        opt.rank = idx + 1
        opt.save()

    top_option = options[0]
    runner_up = options[1] if len(options) > 1 else None

    # Confidence calculation
    diff = (top_option.total_weighted_score - runner_up.total_weighted_score) if runner_up else 3.0
    confidence = min(max(50.0 + (diff * 10.0), 55.0), 96.0)

    # Strengths
    strengths = []
    top_scores = top_option.scores or {}
    for c in criteria:
        cid = str(c.id)
        t_score = float(top_scores.get(cid, 5.0))
        r_score = float((runner_up.scores or {}).get(cid, 5.0)) if runner_up else 5.0
        if t_score >= 7.5 or t_score > r_score:
            strengths.append(f"Dominates in \"{c.name}\" ({t_score}/10, Weight: {int(normalized_weights[cid]*100)}%)")
    if not strengths:
        strengths.append("Provides highest aggregate utility across all evaluated dimensions.")

    # Trade-offs
    trade_offs = []
    if runner_up:
        r_scores = runner_up.scores or {}
        for c in criteria:
            cid = str(c.id)
            t_score = float(top_scores.get(cid, 5.0))
            r_score = float(r_scores.get(cid, 5.0))
            if r_score > t_score:
                trade_offs.append(f"\"{runner_up.title}\" offers superior \"{c.name}\" ({r_score} vs {t_score}/10).")
    if not trade_offs:
        trade_offs.append("Requires commitment consistency and continuous execution monitoring.")

    summary = (
        f"Based on Multi-Criteria Decision Analysis across {len(criteria)} weighted dimensions, "
        f"\"{top_option.title}\" is the recommended choice with a composite score of "
        f"{top_option.total_weighted_score}/10 (Confidence: {int(confidence)}%)."
    )

    first_step = f"Set an immediate milestone and schedule the first implementation task for \"{top_option.title}\" in your LifeSync Planner."

    DecisionRecommendation.objects.update_or_create(
        decision=decision,
        defaults={
            "winning_option_id": str(top_option.id),
            "winning_option_title": top_option.title,
            "confidence_score": round(confidence, 1),
            "executive_summary": summary,
            "key_strengths": strengths[:3],
            "critical_trade_offs": trade_offs[:3],
            "suggested_first_step": first_step
        }
    )

    decision.status = 'EVALUATED'
    decision.save()
    return decision
