from django.urls import path
from .views import PlannerTaskListCreateView, PlannerTaskToggleView

urlpatterns = [
    path('', PlannerTaskListCreateView.as_view(), name='planner-list-create'),
    path('<uuid:pk>/toggle/', PlannerTaskToggleView.as_view(), name='planner-toggle'),
]
