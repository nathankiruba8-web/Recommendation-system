from django.db import models
from django.contrib.auth.models import User
import uuid

class Goal(models.Model):
    CATEGORY_CHOICES = [
        ('CAREER', 'Career & Work'),
        ('FINANCE', 'Financial & Investment'),
        ('HEALTH', 'Health & Wellness'),
        ('RELATIONSHIPS', 'Relationships & Social'),
        ('EDUCATION', 'Education & Learning'),
        ('LIFESTYLE', 'Lifestyle & Relocation'),
    ]

    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='goals', null=True, blank=True)
    title = models.CharField(max_length=255)
    description = models.TextField(blank=True)
    category = models.CharField(max_length=30, choices=CATEGORY_CHOICES, default='CAREER')
    target_date = models.CharField(max_length=20, default='2026-12-31')
    progress_percent = models.IntegerField(default=0)
    linked_decision_id = models.CharField(max_length=100, blank=True, null=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return f"{self.title} ({self.progress_percent}%)"

    def recalculate_progress(self):
        milestones = self.milestones.all()
        if not milestones:
            self.progress_percent = 0
        else:
            completed = milestones.filter(is_completed=True).count()
            self.progress_percent = int((completed / milestones.count()) * 100)
        self.save()

class Milestone(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    goal = models.ForeignKey(Goal, on_delete=models.CASCADE, related_name='milestones')
    title = models.CharField(max_length=255)
    is_completed = models.BooleanField(default=False)
    due_date = models.CharField(max_length=20, blank=True)

    def __str__(self):
        return f"{self.title} [{'x' if self.is_completed else ' '}]"
