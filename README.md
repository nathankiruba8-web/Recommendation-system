# LifeSync – Personal Decision Recommendation Engine

[![Kotlin](https://img.shields.io/badge/Kotlin-Jetpack%20Compose%20M3-blue?logo=kotlin)](https://developer.android.com/jetpack/compose)
[![Django](https://img.shields.io/badge/Django-5.0%20REST%20Framework-green?logo=django)](https://www.django-rest-framework.org/)
[![Database](https://img.shields.io/badge/PostgreSQL-Render%20Cloud-blue?logo=postgresql)](https://render.com)
[![License](https://img.shields.io/badge/License-MIT-purple.svg)](LICENSE)

LifeSync is a personal decision support and goal alignment ecosystem consisting of a modern **Jetpack Compose Android application** and a production-grade **Django REST Framework backend** deployed on Render with PostgreSQL. It eliminates decision paralysis by transforming complex life choices—career moves, major financial investments, relocation, and habits—into structured, weighted multi-criteria evaluations enriched with intelligent recommendation synthesis.

---

## System Architecture

```
┌────────────────────────────────────────────────────────────────────────┐
│                        LifeSync Android Client                         │
│                    (Kotlin + Jetpack Compose M3)                       │
├────────────────────────────────────────────────────────────────────────┤
│ Presentation Layer:                                                    │
│  ├── DashboardScreen (Quick Metrics, Active Dilemmas, Daily Focus)     │
│  ├── DecisionsScreen (MCDA Wizard, Score Matrices, Pros/Cons Breakdown)│
│  ├── GoalsScreen (Progress Rings, Category Tagging, Milestones)        │
│  ├── PlannerScreen (Daily Action Items, Priority Chips, Checklists)    │
│  ├── AnalyticsScreen (Life Pillars Distribution, Resolution Velocity)  │
│  └── SettingsDialog (Live Cloud URL Configuration & Ping Test)         │
├────────────────────────────────────────────────────────────────────────┤
│ Logic & Network Layer:                                                 │
│  ├── DecisionRecommendationEngine (Multi-Criteria Weighted Sum Model)  │
│  ├── Retrofit & OkHttp (Dynamic URL Switcher, Auth Headers, Error Safe)│
│  └── LifeSyncRepository (Seamless Online Sync + Offline Demo Fallback) │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ HTTPS REST API
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                  Render Production Cloud Web Service                   │
│                     (Django 5 + REST Framework)                        │
├────────────────────────────────────────────────────────────────────────┤
│  ├── Security: CORS Headers, WhiteNoise Static Files, Gunicorn WSGI    │
│  ├── /api/v1/health/    (Service Health & Live Connectivity Ping)      │
│  ├── /api/v1/auth/      (User Registration, Token Login)               │
│  ├── /api/v1/profile/   (Personal Priorities & Risk Profile)           │
│  ├── /api/v1/decisions/ (CRUD, Matrix Weights, Evaluation Engine)      │
│  ├── /api/v1/goals/     (Life Goals & Dynamic Progress Recalculation)  │
│  ├── /api/v1/planner/   (Daily Schedules & Priority Tasks)             │
│  └── /api/v1/analytics/ (Aggregated Life Alignment Metrics)            │
├────────────────────────────────────────────────────────────────────────┤
│ Persistence:                                                           │
│  └── Managed Render PostgreSQL Database (Production)                   │
└────────────────────────────────────────────────────────────────────────┘
```

---

## Key Features

1. **Multi-Criteria Decision Analysis (MCDA) Wizard**:
   - Step 1: Define dilemma context, category, and target decision date.
   - Step 2: Formulate competing alternative options.
   - Step 3: Weigh customizable criteria (Work-Life Balance, Compensation, Fulfillment, Time Commitment).
   - Step 4: Rate options on 1–10 slider scales.
   - Step 5: Instant automated matrix calculation producing an optimal winner, confidence percentage, key strengths, and critical trade-offs.

2. **From Deciding to Doing (One-Tap Goal Conversion)**:
   - Convert any winning recommendation into a trackable Goal and schedule the immediate next action step in your Daily Planner.

3. **Daily Action Planner**:
   - Filter by Today, Pending, or Completed.
   - High, Medium, and Low priority tags with strike-through completion status.

4. **Life Alignment Analytics**:
   - Distribution of decisions across life pillars (Career, Finance, Health, Relationships).
   - Average decision confidence score and goal velocity tracking.

5. **Cloud Deployment & Resilient Connectivity**:
   - Pre-configured `render.yaml` for 1-click cloud provisioning with PostgreSQL.
   - Built-in live connection tester in the mobile app with automatic offline demo mode fallback.

---

## Project Structure

```
LifeSync/
├── app/                                 # Android Client (Kotlin + Jetpack Compose)
│   ├── src/main/java/com/example/
│   │   ├── MainActivity.kt              # App entry point
│   │   ├── model/LifeSyncModels.kt      # Domain data classes & enums
│   │   ├── engine/                      # Multi-Criteria Decision Engine
│   │   ├── network/                     # Retrofit API service & Dynamic URL Manager
│   │   ├── repository/                  # Repository layer with Demo & Cloud data
│   │   ├── ui/
│   │   │   ├── LifeSyncApp.kt           # Root App Scaffold & M3 Bottom Nav
│   │   │   ├── screens/                 # Dashboard, Decisions, Goals, Planner, Settings
│   │   │   ├── components/              # Badges, Metric Cards, TopBar
│   │   │   └── theme/                   # LifeSync Neo-Modern Theme & Color Tokens
│   │   └── res/                         # Vector icons, custom adaptive launcher icon
│   └── build.gradle.kts                 # Android Gradle dependencies & plugins
│
├── backend/                             # Production-Ready Django REST Backend
│   ├── manage.py                        # Django CLI utility
│   ├── requirements.txt                 # Production dependencies (Django, DRF, Psycopg2)
│   ├── render.yaml                      # Render 1-click Infrastructure-as-Code Blueprint
│   ├── build.sh                         # Render build script (migrations, collectstatic)
│   ├── Procfile                         # Gunicorn web server process definition
│   ├── .env.example                     # Environment variables template
│   ├── lifesync_project/                # Settings, WSGI, ASGI, Root URLs
│   ├── core/                            # User authentication and profile models
│   ├── decisions/                       # Decisions, criteria, options, MCDA engine
│   ├── goals/                           # Goals and milestones tracking
│   ├── planner/                         # Daily tasks and schedules
│   └── analytics/                       # Aggregation & metrics endpoints
│
├── docs/                                # Technical Documentation
│   ├── API_DOCUMENTATION.md             # Complete OpenAPI / REST schema & endpoints
│   ├── DATABASE_ERD.md                  # Entity Relationship Diagram & table specs
│   └── TESTING_REPORT.md                # Verification test report & audit results
│
├── .gitignore                           # Git ignore rules for Android Studio & Python
├── metadata.json                        # Platform metadata
└── README.md                            # Comprehensive setup & deployment guide
```

---

## Step-by-Step Beginner Guide on Windows

Follow these simple steps on your Windows computer to upload your project to GitHub and deploy the backend to Render.

### Prerequisites on Windows
1. **Git for Windows**: Download from [git-scm.com](https://git-scm.com/download/win) (choose 64-bit Windows Setup) and install with default settings.
2. **GitHub Account**: Sign up at [github.com](https://github.com) if you do not have one.
3. **Android Studio**: Installed on your Windows PC to run and preview the Android app.
4. **Python 3.10+** (Optional for local testing): Download from [python.org](https://www.python.org/downloads/windows/).

---

### Step 1: Create a New GitHub Repository

1. Open your browser and go to [github.com/new](https://github.com/new).
2. For **Repository name**, enter: `LifeSync`.
3. Set visibility to **Public** (or **Private**).
4. **Important**: Leave all initialization checkboxes (**Add a README file**, **Add .gitignore**, **Choose a license**) **UNCHECKED** because your project already has these configured.
5. Click **Create repository**.
6. Copy your repository's URL (e.g. `https://github.com/YOUR_USERNAME/LifeSync.git`).

---

### Step 2: Open Windows PowerShell in the Project Directory

1. Open **File Explorer** on Windows and navigate to where this project folder is saved.
2. Click on the address bar at the top of File Explorer, type `powershell`, and press **Enter**.
3. A blue PowerShell window will open directly inside your project folder.

---

### Step 3: Connect Local Project to GitHub and Push

Copy and paste the following commands one by one into your PowerShell window (replace `YOUR_USERNAME` with your actual GitHub username):

```powershell
# 1. Initialize Git repository
git init

# 2. Add all source code files
git add .

# 3. Commit the project
git commit -m "Initial release of LifeSync Personal Decision Recommendation Engine"

# 4. Set the default branch to main
git branch -M main

# 5. Link your local project to your GitHub repository
git remote add origin https://github.com/YOUR_USERNAME/LifeSync.git

# 6. Push your complete code to GitHub
git push -u origin main
```

> **Note**: If Git asks you to sign in, a browser popup will appear prompting you to authorize GitHub. Simply click **Authorize** or enter your credentials.

---

### Step 4: Deploy the Django Backend to Render (Free Cloud Hosting)

Render will build and host your Django REST API and automatically provision a PostgreSQL database using the included `render.yaml` configuration.

1. Go to [render.com](https://render.com) and click **Sign Up** (choose **Continue with GitHub**).
2. Once logged in, click the blue **New +** button in the top right corner and select **Blueprint**.
3. Connect your GitHub account and select your **LifeSync** repository.
4. Render will read the `backend/render.yaml` file automatically:
   - It will display two items to create:
     - **lifesync-db** (Managed PostgreSQL database)
     - **lifesync-api** (Web Service running Django & Gunicorn)
5. Click **Apply**.
6. Render will now provision your database, install dependencies from `requirements.txt`, collect static files, run database migrations, and launch your API!
7. When the build completes (usually 2–3 minutes), Render displays your live service URL at the top of the dashboard:
   ```
   https://lifesync-api-xxxx.onrender.com
   ```
8. Verify your live API by clicking on the URL or opening:
   ```
   https://lifesync-api-xxxx.onrender.com/api/v1/health/
   ```
   You should see:
   ```json
   {"status": "healthy", "service": "LifeSync Personal Decision Recommendation Engine API", "version": "1.0.0", "database": "connected"}
   ```

---

### Step 5: Connect the Android App to Your Live Render Backend

1. Open **Android Studio** on your Windows PC.
2. Click **File** -> **Open** and select your `LifeSync` root folder.
3. Allow Gradle to sync.
4. Run the application on your Android emulator or physical phone by clicking the green **Run (Play)** button.
5. In the LifeSync app:
   - Tap the **Cloud Sync icon** in the top right corner of the app bar.
   - In the **Base API URL** field, enter your Render URL ending with `/api/v1/`:
     ```
     https://lifesync-api-xxxx.onrender.com/api/v1/
     ```
   - Tap **Test Connection Ping**.
   - You will see a green confirmation badge: **"Success: Connected to LifeSync Personal Decision Recommendation Engine API"**!
   - Toggle off **Offline Demo Mode** to sync all new decisions, goals, and daily tasks directly with your cloud database.

---

## Running the Backend Locally on Windows (Optional)

If you wish to run the backend on your Windows PC before deploying to Render:

```powershell
cd backend
python -m venv venv
.\venv\Scripts\activate
pip install -r requirements.txt
python manage.py migrate
python manage.py runserver 0.0.0.0:8000
```
When running the Android emulator on the same PC, set the Android app's URL to `http://10.0.2.2:8000/api/v1/`.

---

## Documentation Quick Links

- [Complete REST API Documentation](docs/API_DOCUMENTATION.md)
- [Database Entity Relationship Diagram (ERD)](docs/DATABASE_ERD.md)
- [Verification & Testing Report](docs/TESTING_REPORT.md)
- [Backend Specific Quickstart](backend/README.md)

---

## Technologies Used

- **Android Client**: Kotlin, Jetpack Compose, Material Design 3, Coroutines, StateFlow, Retrofit 2, OkHttp 3, Moshi.
- **Backend**: Python 3.11, Django 5.0, Django REST Framework, Gunicorn, WhiteNoise, Psycopg2.
- **Database & Cloud**: PostgreSQL, Render Cloud Platform, Git & GitHub.

---

## License

This project is licensed under the MIT License.
