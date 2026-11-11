from django.urls import path
from .views import DecisionListCreateView, DecisionDetailView, DecisionEvaluateView

urlpatterns = [
    path('', DecisionListCreateView.as_view(), name='decision-list-create'),
    path('<uuid:pk>/', DecisionDetailView.as_view(), name='decision-detail'),
    path('<uuid:pk>/evaluate/', DecisionEvaluateView.as_view(), name='decision-evaluate'),
]
