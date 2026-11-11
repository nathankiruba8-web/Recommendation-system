from rest_framework import serializers
from .models import PlannerTask

class PlannerTaskSerializer(serializers.ModelSerializer):
    class Meta:
        model = PlannerTask
        fields = ['id', 'title', 'date', 'time', 'priority', 'is_completed', 'linked_goal_title', 'created_at']
