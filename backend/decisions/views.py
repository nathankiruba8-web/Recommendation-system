from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status, permissions
from .models import Decision
from .serializers import DecisionSerializer
from .engine import run_mcda_evaluation

class DecisionListCreateView(APIView):
    permission_classes = [permissions.AllowAny]

    def get(self, request):
        decisions = Decision.objects.all().order_by('-created_at')
        return Response(DecisionSerializer(decisions, many=True).data)

    def post(self, request):
        serializer = DecisionSerializer(data=request.data)
        if serializer.is_valid():
            decision = serializer.save()
            # Automatically run MCDA evaluation upon creation
            run_mcda_evaluation(decision)
            return Response(DecisionSerializer(decision).data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class DecisionDetailView(APIView):
    permission_classes = [permissions.AllowAny]

    def get(self, request, pk):
        try:
            decision = Decision.objects.get(pk=pk)
            return Response(DecisionSerializer(decision).data)
        except Decision.DoesNotExist:
            return Response({"error": "Decision not found"}, status=status.HTTP_404_NOT_FOUND)

    def delete(self, request, pk):
        try:
            decision = Decision.objects.get(pk=pk)
            decision.delete()
            return Response(status=status.HTTP_204_NO_CONTENT)
        except Decision.DoesNotExist:
            return Response({"error": "Decision not found"}, status=status.HTTP_404_NOT_FOUND)

class DecisionEvaluateView(APIView):
    permission_classes = [permissions.AllowAny]

    def post(self, request, pk):
        try:
            decision = Decision.objects.get(pk=pk)
            evaluated = run_mcda_evaluation(decision)
            return Response(DecisionSerializer(evaluated).data)
        except Decision.DoesNotExist:
            return Response({"error": "Decision not found"}, status=status.HTTP_404_NOT_FOUND)
