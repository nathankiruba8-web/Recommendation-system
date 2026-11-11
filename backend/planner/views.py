from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status, permissions
from .models import PlannerTask
from .serializers import PlannerTaskSerializer

class PlannerTaskListCreateView(APIView):
    permission_classes = [permissions.AllowAny]

    def get(self, request):
        tasks = PlannerTask.objects.all().order_by('-created_at')
        return Response(PlannerTaskSerializer(tasks, many=True).data)

    def post(self, request):
        serializer = PlannerTaskSerializer(data=request.data)
        if serializer.is_valid():
            task = serializer.save()
            return Response(PlannerTaskSerializer(task).data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class PlannerTaskToggleView(APIView):
    permission_classes = [permissions.AllowAny]

    def patch(self, request, pk):
        try:
            task = PlannerTask.objects.get(pk=pk)
            task.is_completed = not task.is_completed
            task.save()
            return Response(PlannerTaskSerializer(task).data)
        except PlannerTask.DoesNotExist:
            return Response({"error": "Task not found"}, status=status.HTTP_404_NOT_FOUND)
