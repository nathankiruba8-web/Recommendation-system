from django.db import models
from django.contrib.auth.models import User
import uuid

class Decision(models.Model):
    CATEGORY_CHOICES = [
        ('CAREER', 'Career & Work'),
        ('FINANCE', 'Financial & Investment'),
        ('HEALTH', 'Health & Wellness'),
        ('RELATIONSHIPS', 'Relationships & Social'),
        ('EDUCATION', 'Education & Learning'),
        ('LIFESTYLE', 'Lifestyle & Relocation'),
    ]

    STATUS_CHOICES = [
        ('DRAFT', 'Drafting'),
        ('EVALUATED', 'Evaluated'),
        ('DECIDED', 'Decided'),
        ('ARCHIVED', 'Archived'),
    ]

    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='decisions', null=True, blank=True)
    title = models.CharField(max_length=255)
    dilemma_description = models.TextField(blank=True)
    category = models.CharField(max_length=30, choices=CATEGORY_CHOICES, default='CAREER')
    status = models.CharField(max_length=20, choices=STATUS_CHOICES, default='DRAFT')
    deadline_date = models.CharField(max_length=20, default='2026-12-31')
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.title

class Criterion(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    decision = models.ForeignKey(Decision, on_delete=models.CASCADE, related_name='criteria')
    name = models.CharField(max_length=150)
    weight = models.FloatField(default=0.25)
    description = models.TextField(blank=True)

    def __str__(self):
        return f"{self.name} ({self.weight})"

class DecisionOption(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    decision = models.ForeignKey(Decision, on_delete=models.CASCADE, related_name='options')
    title = models.CharField(max_length=255)
    description = models.TextField(blank=True)
    scores = models.JSONField(default=dict, blank=True) # Criterion UUID string -> score 1..10
    total_weighted_score = models.FloatField(default=0.0)
    rank = models.IntegerField(default=1)

    def __str__(self):
        return f"{self.title} (Rank #{self.rank})"

class DecisionRecommendation(models.Model):
    decision = models.OneToOneField(Decision, on_delete=models.CASCADE, related_name='recommendation')
    winning_option_id = models.CharField(max_length=100, blank=True)
    winning_option_title = models.CharField(max_length=255, blank=True)
    confidence_score = models.FloatField(default=0.0)
    executive_summary = models.TextField(blank=True)
    key_strengths = models.JSONField(default=list, blank=True)
    critical_trade_offs = models.JSONField(default=list, blank=True)
    suggested_first_step = models.TextField(blank=True)
    created_at = models.DateTimeField(auto_now_add=True)
