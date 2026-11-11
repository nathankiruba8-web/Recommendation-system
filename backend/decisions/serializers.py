from rest_framework import serializers
from .models import Decision, Criterion, DecisionOption, DecisionRecommendation

class CriterionSerializer(serializers.ModelSerializer):
    class Meta:
        model = Criterion
        fields = ['id', 'name', 'weight', 'description']

class DecisionOptionSerializer(serializers.ModelSerializer):
    class Meta:
        model = DecisionOption
        fields = ['id', 'title', 'description', 'scores', 'total_weighted_score', 'rank']

class DecisionRecommendationSerializer(serializers.ModelSerializer):
    class Meta:
        model = DecisionRecommendation
        fields = ['winning_option_id', 'winning_option_title', 'confidence_score', 'executive_summary', 'key_strengths', 'critical_trade_offs', 'suggested_first_step']

class DecisionSerializer(serializers.ModelSerializer):
    criteria = CriterionSerializer(many=True, required=False)
    options = DecisionOptionSerializer(many=True, required=False)
    recommendation = DecisionRecommendationSerializer(read_only=True)

    class Meta:
        model = Decision
        fields = ['id', 'title', 'dilemma_description', 'category', 'status', 'deadline_date', 'criteria', 'options', 'recommendation', 'created_at']

    def create(self, validated_data):
        criteria_data = validated_data.pop('criteria', [])
        options_data = validated_data.pop('options', [])
        decision = Decision.objects.create(**validated_data)

        for c in criteria_data:
            Criterion.objects.create(decision=decision, **c)

        for o in options_data:
            DecisionOption.objects.create(decision=decision, **o)

        return decision
