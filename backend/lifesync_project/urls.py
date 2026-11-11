from django.contrib import admin
from django.urls import path, include
from django.http import JsonResponse

def health_check(request):
    return JsonResponse({
        "status": "healthy",
        "service": "LifeSync Personal Decision Recommendation Engine API",
        "version": "1.0.0",
        "database": "connected"
    })

def api_root(request):
    return JsonResponse({
        "message": "Welcome to LifeSync REST API",
        "documentation": "/docs/",
        "endpoints": {
            "health": "/api/v1/health/",
            "auth": "/api/v1/auth/",
            "profile": "/api/v1/profile/",
            "decisions": "/api/v1/decisions/",
            "goals": "/api/v1/goals/",
            "planner": "/api/v1/planner/",
            "analytics": "/api/v1/analytics/"
        }
    })

urlpatterns = [
    path('admin/', admin.site.urls),
    path('', api_root, name='api-root'),
    path('api/v1/health/', health_check, name='health-check'),
    path('api/v1/auth/', include('core.urls')),
    path('api/v1/profile/', include('core.profile_urls')),
    path('api/v1/decisions/', include('decisions.urls')),
    path('api/v1/goals/', include('goals.urls')),
    path('api/v1/planner/', include('planner.urls')),
    path('api/v1/analytics/', include('analytics.urls')),
]
