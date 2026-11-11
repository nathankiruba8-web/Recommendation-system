from django.urls import path
from .views import GoalListCreateView, MilestoneToggleView

urlpatterns = [
    path('', GoalListCreateView.as_view(), name='goal-list-create'),
    path('milestones/<uuid:pk>/toggle/', MilestoneToggleView.as_view(), name='milestone-toggle'),
]
