# LifeSync REST API Documentation

The LifeSync backend is built using Django REST Framework (DRF) and PostgreSQL. It exposes a modular, RESTful API providing decision analysis, goal tracking, task scheduling, and analytics.

---

## Base URLs

- **Production (Render)**: `https://<your-render-service>.onrender.com/api/v1/`
- **Local Development**: `http://127.0.0.1:8000/api/v1/`
- **Android Emulator Localhost**: `http://10.0.2.2:8000/api/v1/`

---

## Authentication

All authenticated requests require an `Authorization` header containing the user token:

```http
Authorization: Token 9944b09199c62bcf9418ad846dd0e4bbdfc6ee4b
```

---

## 1. System Health

### Check Service Status
- **Method**: `GET`
- **Endpoint**: `/health/`
- **Response**: `200 OK`
```json
{
  "status": "healthy",
  "service": "LifeSync Personal Decision Recommendation Engine API",
  "version": "1.0.0",
  "database": "connected"
}
```

---

## 2. Authentication & Profiles

### Register New User
- **Method**: `POST`
- **Endpoint**: `/auth/register/`
- **Payload**:
```json
{
  "username": "alex_mercer",
  "email": "alex@lifesync.app",
  "password": "StrongPassword123!"
}
```
- **Response**: `201 Created`
```json
{
  "token": "4a71b26f5...",
  "user": {
    "id": "e2c3491b-...",
    "username": "alex_mercer",
    "email": "alex@lifesync.app",
    "risk_tolerance": "MODERATE",
    "top_priorities": ["Work-Life Balance", "Career Acceleration", "Financial Freedom"]
  }
}
```

### User Login
- **Method**: `POST`
- **Endpoint**: `/auth/login/`
- **Payload**:
```json
{
  "username": "alex_mercer",
  "password": "StrongPassword123!"
}
```
- **Response**: `200 OK` (returns token and user profile)

### Get / Update Profile
- **Method**: `GET` / `PUT`
- **Endpoint**: `/profile/`
- **Payload (PUT)**:
```json
{
  "risk_tolerance": "AGGRESSIVE",
  "top_priorities": ["Startup Growth", "Deep Learning", "Health"]
}
```

---

## 3. Decision Recommendation Engine

### List Decisions
- **Method**: `GET`
- **Endpoint**: `/decisions/`
- **Response**: `200 OK` (Array of Decision objects)

### Create Decision
- **Method**: `POST`
- **Endpoint**: `/decisions/`
- **Payload**:
```json
{
  "title": "Choose Between Job Offer vs Current Role",
  "dilemma_description": "Evaluating high-growth startup offer vs corporate stability.",
  "category": "CAREER",
  "deadline_date": "2026-12-15",
  "criteria": [
    {"name": "Work-Life Balance", "weight": 0.35},
    {"name": "Compensation & Equity", "weight": 0.35},
    {"name": "Career Growth", "weight": 0.30}
  ],
  "options": [
    {
      "title": "Accept AI Startup Offer",
      "description": "Generous equity, fast-paced environment",
      "scores": {
        "<criteria_1_uuid>": 6.5,
        "<criteria_2_uuid>": 9.2,
        "<criteria_3_uuid>": 9.5
      }
    },
    {
      "title": "Stay at Current Enterprise",
      "description": "Predictable 40-hr week, lower risk",
      "scores": {
        "<criteria_1_uuid>": 8.8,
        "<criteria_2_uuid>": 7.5,
        "<criteria_3_uuid>": 6.0
      }
    }
  ]
}
```
- **Response**: `201 Created` (Evaluated decision object with ranked options and recommendation)

### Trigger Evaluation
- **Method**: `POST`
- **Endpoint**: `/decisions/{id}/evaluate/`
- **Response**: `200 OK`
```json
{
  "id": "d1-uuid",
  "status": "EVALUATED",
  "recommendation": {
    "winning_option_id": "o1-uuid",
    "winning_option_title": "Accept AI Startup Offer",
    "confidence_score": 85.5,
    "executive_summary": "Based on Multi-Criteria Decision Analysis across 3 weighted dimensions, 'Accept AI Startup Offer' is the recommended choice with a composite score of 8.3/10 (Confidence: 86%).",
    "key_strengths": [
      "Dominates in 'Compensation & Equity' (9.2/10, Weight: 35%)",
      "Dominates in 'Career Growth' (9.5/10, Weight: 30%)"
    ],
    "critical_trade_offs": [
      "'Stay at Current Enterprise' offers superior 'Work-Life Balance' (8.8 vs 6.5/10)."
    ],
    "suggested_first_step": "Set an immediate milestone and schedule the first implementation task for 'Accept AI Startup Offer' in your LifeSync Planner."
  }
}
```

---

## 4. Goals & Milestones

### List Goals
- **Method**: `GET`
- **Endpoint**: `/goals/`

### Create Goal
- **Method**: `POST`
- **Endpoint**: `/goals/`
- **Payload**:
```json
{
  "title": "Transition to Senior Tech Lead",
  "description": "Master distributed systems and lead platform modernization.",
  "category": "CAREER",
  "target_date": "2026-11-30",
  "milestones": [
    {"title": "Complete Cloud Architecture Certification", "is_completed": true},
    {"title": "Lead system latency optimization initiative", "is_completed": false}
  ]
}
```

### Toggle Milestone Completion
- **Method**: `PATCH`
- **Endpoint**: `/goals/milestones/{id}/toggle/`
- **Response**: `200 OK` (Returns updated goal with auto-recalculated progress percentage)

---

## 5. Daily Planner

### List Tasks
- **Method**: `GET`
- **Endpoint**: `/planner/`

### Create Task
- **Method**: `POST`
- **Endpoint**: `/planner/`
- **Payload**:
```json
{
  "title": "Review startup equity vesting term sheet",
  "date": "Today",
  "time": "10:00 AM",
  "priority": "HIGH",
  "linked_goal_title": "Transition to Senior Tech Lead"
}
```

### Toggle Task Completion
- **Method**: `PATCH`
- **Endpoint**: `/planner/{id}/toggle/`

---

## 6. Analytics

### Get Aggregated Metrics
- **Method**: `GET`
- **Endpoint**: `/analytics/`
- **Response**: `200 OK`
```json
{
  "total_decisions_count": 5,
  "decided_count": 4,
  "active_goals_count": 3,
  "goal_completion_rate": 65,
  "category_breakdown": {
    "Career & Work": 3,
    "Financial & Investment": 1,
    "Health & Wellness": 1
  },
  "average_confidence_score": 86.4
}
```
