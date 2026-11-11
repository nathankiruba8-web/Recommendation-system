from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import permissions
from decisions.models import Decision, DecisionRecommendation
from goals.models import Goal

class AnalyticsSummaryView(APIView):
    permission_classes = [permissions.AllowAny]

    def get(self, request):
        total_decisions = Decision.objects.count()
        decided_count = Decision.objects.filter(status__in=['EVALUATED', 'DECIDED']).count()
        active_goals = Goal.objects.count()

        goals = Goal.objects.all()
        goal_completion_rate = 0
        if goals.exists():
            goal_completion_rate = int(sum(g.progress_percent for g in goals) / goals.count())

        recs = DecisionRecommendation.objects.all()
        avg_confidence = 82.5
        if recs.exists():
            avg_confidence = round(sum(r.confidence_score for r in recs) / recs.count(), 1)

        category_breakdown = {}
        for d in Decision.objects.all():
            cat = d.get_category_display()
            category_breakdown[cat] = category_breakdown.get(cat, 0) + 1

        return Response({
            "total_decisions_count": total_decisions,
            "decided_count": decided_count,
            "active_goals_count": active_goals,
            "goal_completion_rate": goal_completion_rate,
            "category_breakdown": category_breakdown,
            "average_confidence_score": avg_confidence
        })
