from rest_framework import serializers
from .models import Goal, Milestone

class MilestoneSerializer(serializers.ModelSerializer):
    class Meta:
        model = Milestone
        fields = ['id', 'title', 'is_completed', 'due_date']

class GoalSerializer(serializers.ModelSerializer):
    milestones = MilestoneSerializer(many=True, required=False)

    class Meta:
        model = Goal
        fields = ['id', 'title', 'description', 'category', 'target_date', 'progress_percent', 'linked_decision_id', 'milestones', 'created_at']

    def create(self, validated_data):
        milestones_data = validated_data.pop('milestones', [])
        goal = Goal.objects.create(**validated_data)
        for m in milestones_data:
            Milestone.objects.create(goal=goal, **m)
        goal.recalculate_progress()
        return goal
