from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status, permissions
from .models import Goal, Milestone
from .serializers import GoalSerializer

class GoalListCreateView(APIView):
    permission_classes = [permissions.AllowAny]

    def get(self, request):
        goals = Goal.objects.all().order_by('-created_at')
        return Response(GoalSerializer(goals, many=True).data)

    def post(self, request):
        serializer = GoalSerializer(data=request.data)
        if serializer.is_valid():
            goal = serializer.save()
            return Response(GoalSerializer(goal).data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class MilestoneToggleView(APIView):
    permission_classes = [permissions.AllowAny]

    def patch(self, request, pk):
        try:
            milestone = Milestone.objects.get(pk=pk)
            milestone.is_completed = not milestone.is_completed
            milestone.save()
            milestone.goal.recalculate_progress()
            return Response(GoalSerializer(milestone.goal).data)
        except Milestone.DoesNotExist:
            return Response({"error": "Milestone not found"}, status=status.HTTP_404_NOT_FOUND)
