# LifeSync Django REST Framework Backend

Production-ready backend for the LifeSync Personal Decision Recommendation Engine, pre-configured for **Render** cloud deployment with PostgreSQL.

---

## Quickstart (Local Development on Windows)

1. Open PowerShell or Command Prompt in this `backend/` directory:
   ```powershell
   cd backend
   ```

2. Create and activate a Python virtual environment:
   ```powershell
   python -m venv venv
   .\venv\Scripts\activate
   ```

3. Install dependencies:
   ```powershell
   pip install -r requirements.txt
   ```

4. Create local database and migrate:
   ```powershell
   python manage.py migrate
   ```

5. Run the local development server:
   ```powershell
   python manage.py runserver 0.0.0.0:8000
   ```

Visit `http://127.0.0.1:8000/api/v1/health/` in your browser to verify that the service is running.

---

## Deploying to Render (Cloud Production)

1. Push this repository to your GitHub account (see root `README.md` for Git commands).
2. Go to [https://render.com](https://render.com) and sign in.
3. Click **New +** -> **Blueprint**.
4. Select your `LifeSync` repository. Render will automatically read `backend/render.yaml` and configure:
   - **PostgreSQL Database** (`lifesync-db` on the free tier)
   - **Python Web Service** (`lifesync-api`) with automatic Gunicorn server, WhiteNoise static files, and database migrations.
5. Click **Apply**.
6. When deployment finishes, copy your live Web Service URL (e.g. `https://lifesync-api.onrender.com`).
7. Open the LifeSync Android app, tap the **Cloud Sync** icon in the top right, and paste your URL:
   `https://lifesync-api.onrender.com/api/v1/`
   Then tap **Test Connection Ping**!
