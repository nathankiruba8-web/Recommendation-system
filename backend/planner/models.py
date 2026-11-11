from django.db import models
from django.contrib.auth.models import User
import uuid

class PlannerTask(models.Model):
    PRIORITY_CHOICES = [
        ('LOW', 'Low'),
        ('MEDIUM', 'Medium'),
        ('HIGH', 'High'),
    ]

    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='planner_tasks', null=True, blank=True)
    title = models.CharField(max_length=255)
    date = models.CharField(max_length=50, default='Today')
    time = models.CharField(max_length=20, default='09:00 AM')
    priority = models.CharField(max_length=10, choices=PRIORITY_CHOICES, default='MEDIUM')
    is_completed = models.BooleanField(default=False)
    linked_goal_title = models.CharField(max_length=255, blank=True, null=True)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.title} [{self.priority}]"
