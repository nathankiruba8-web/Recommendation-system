# LifeSync Database Entity-Relationship Diagram (ERD)

The database schema is designed for PostgreSQL in production and supports relational integrity across decisions, weighted evaluations, goals, milestones, and daily planning schedules.

---

## Diagram

```
┌─────────────────────────────────────────────────────────┐
│                       auth_user                         │
├─────────────────────────────────────────────────────────┤
│ id: integer (PK)                                        │
│ username: varchar(150)                                  │
│ email: varchar(254)                                     │
│ password: varchar(128)                                  │
└────────────┬─────────────────────────────┬──────────────┘
             │ 1:1                         │ 1:N
             ▼                             ▼
┌──────────────────────────┐   ┌──────────────────────────┐
│    core_userprofile      │   │    decisions_decision    │
├──────────────────────────┤   ├──────────────────────────┤
│ id: uuid (PK)            │   │ id: uuid (PK)            │
│ user_id: integer (FK)    │   │ user_id: integer (FK)    │
│ risk_tolerance: varchar  │   │ title: varchar(255)      │
│ top_priorities: jsonb    │   │ dilemma_description: text│
│ created_at: timestamp    │   │ category: varchar(30)    │
└──────────────────────────┘   │ status: varchar(20)      │
                               │ deadline_date: varchar   │
                               │ created_at: timestamp    │
                               └──────┬─────────────┬─────┘
                                      │ 1:N         │ 1:N
             ┌────────────────────────┘             └───────────────────────┐
             ▼                                                              ▼
┌──────────────────────────┐                                  ┌──────────────────────────┐
│   decisions_criterion    │                                  │  decisions_decisionoption│
├──────────────────────────┤                                  ├──────────────────────────┤
│ id: uuid (PK)            │                                  │ id: uuid (PK)            │
│ decision_id: uuid (FK)   │                                  │ decision_id: uuid (FK)   │
│ name: varchar(150)       │                                  │ title: varchar(255)      │
│ weight: float            │                                  │ description: text        │
│ description: text        │                                  │ scores: jsonb            │
└──────────────────────────┘                                  │ total_weighted_score: flt│
             │                                                │ rank: integer            │
             │                                                └─────────────┬────────────┘
             │                                                              │
             └───────────────────────┬──────────────────────────────────────┘
                                     ▼ 1:1
                       ┌──────────────────────────────┐
                       │decisions_recommendation      │
                       ├──────────────────────────────┤
                       │ id: integer (PK)             │
                       │ decision_id: uuid (FK 1:1)   │
                       │ winning_option_id: varchar   │
                       │ winning_option_title: varchar│
                       │ confidence_score: float      │
                       │ executive_summary: text      │
                       │ key_strengths: jsonb         │
                       │ critical_trade_offs: jsonb   │
                       │ suggested_first_step: text   │
                       └──────────────────────────────┘

                               auth_user
                                   │ 1:N
                    ┌──────────────┴──────────────┐
                    ▼                             ▼
┌─────────────────────────────────┐   ┌───────────────────────────────┐
│           goals_goal            │   │      planner_plannertask      │
├─────────────────────────────────┤   ├───────────────────────────────┤
│ id: uuid (PK)                   │   │ id: uuid (PK)                 │
│ user_id: integer (FK)           │   │ user_id: integer (FK)         │
│ title: varchar(255)             │   │ title: varchar(255)           │
│ description: text               │   │ date: varchar(50)             │
│ category: varchar(30)           │   │ time: varchar(20)             │
│ target_date: varchar(20)        │   │ priority: varchar(10)         │
│ progress_percent: integer       │   │ is_completed: boolean         │
│ linked_decision_id: varchar     │   │ linked_goal_title: varchar    │
│ created_at: timestamp           │   │ created_at: timestamp         │
└────────────────┬────────────────┘   └───────────────────────────────┘
                 │ 1:N
                 ▼
┌─────────────────────────────────┐
│        goals_milestone          │
├─────────────────────────────────┤
│ id: uuid (PK)                   │
│ goal_id: uuid (FK)              │
│ title: varchar(255)             │
│ is_completed: boolean           │
│ due_date: varchar(20)           │
└─────────────────────────────────┘
```

---

## Schema Details

### 1. `decisions_decision`
- Primary container for personal dilemmas.
- Categorized into `CAREER`, `FINANCE`, `HEALTH`, `RELATIONSHIPS`, `EDUCATION`, and `LIFESTYLE`.
- Status lifecycle: `DRAFT` -> `EVALUATED` -> `DECIDED` -> `ARCHIVED`.

### 2. `decisions_criterion`
- Multi-criteria dimensions defined by user priorities.
- Normalized weights strictly sum to 1.0 (e.g. 0.40, 0.35, 0.25).

### 3. `decisions_decisionoption`
- Competing choices being evaluated.
- `scores` column stores a JSON dictionary mapping Criterion UUIDs to 1..10 numeric ratings.
- `total_weighted_score`: Computed via $\sum (w_i \times s_{ij})$.

### 4. `decisions_recommendation`
- One-to-one synthesis generated by the MCDA engine.
- Contains the winning option, confidence score, key strengths, trade-offs, and first action step.

### 5. `goals_goal` & `goals_milestone`
- Tracks objectives linked back to decisions.
- Automatically recalculates `progress_percent` whenever milestones are toggled.

### 6. `planner_plannertask`
- Actionable daily schedule items with priority ratings (`HIGH`, `MEDIUM`, `LOW`).
