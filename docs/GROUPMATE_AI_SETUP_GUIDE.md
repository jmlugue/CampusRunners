# Malayan Quest Groupmate and AI Setup Guide

Use this guide when a groupmate, classmate, or AI coding assistant needs to set up, run, debug, or continue developing the Malayan Quest project. It is written as both a human checklist and an AI handoff file.

## 1. Project Snapshot

- **Project name:** IT140P-MP-MalayanQuest
- **App display name:** Malayan Quest
- **Project type:** Android Studio mobile app with PHP REST API and MySQL database
- **Target users:** Mapúa Malayan Colleges Laguna students
- **Backend requirement:** PHP + MySQL through XAMPP, not Firebase, Supabase, or another cloud database
- **Database name:** `malayanquest_db`
- **Android API config file:** `android/app/src/main/java/com/malayanquest/app/Config.kt`
- **Default backend API folder:** `backend/api/`
- **SQL import file:** `database/malayanquest_db.sql`

## 2. Required Tools

Install these before opening the project:

1. **XAMPP** for Apache, PHP, MySQL, and phpMyAdmin.
2. **Android Studio** for opening and running the Android app.
3. **Git** for cloning, branching, committing, and pulling updates.
4. **A browser** such as Chrome or Edge for phpMyAdmin and API testing.
5. **Optional API tester** such as Postman or Thunder Client.

Recommended versions are flexible for the school prototype, but the group should use the same XAMPP and Android Studio versions when possible to reduce setup differences.

## 3. Recommended Folder Layout

For easiest XAMPP setup on Windows, keep the project inside `htdocs`:

```text
C:\xampp\htdocs\IT140P-MP-MalayanQuest
```

The expected important paths are:

```text
C:\xampp\htdocs\IT140P-MP-MalayanQuest\backend
C:\xampp\htdocs\IT140P-MP-MalayanQuest\backend\api
C:\xampp\htdocs\IT140P-MP-MalayanQuest\backend\config\db.php
C:\xampp\htdocs\IT140P-MP-MalayanQuest\database\malayanquest_db.sql
C:\xampp\htdocs\IT140P-MP-MalayanQuest\android
```

If you keep the project somewhere else, the Android API URL setting and browser URLs must match the actual Apache URL.

## 4. First-Time Setup Checklist

### Step 1: Get the Project

Clone the repository or copy the project folder from the group:

```bash
git clone <repository-url> IT140P-MP-MalayanQuest
```

If the repository is already downloaded, pull the latest changes before working:

```bash
git pull
```

### Step 2: Start XAMPP

1. Open **XAMPP Control Panel**.
2. Start **Apache**.
3. Start **MySQL**.
4. Open phpMyAdmin in a browser:

```text
http://localhost/phpmyadmin
```

If Apache uses port `8080`, open:

```text
http://localhost:8080/phpmyadmin
```

### Step 3: Import the Database

1. Open phpMyAdmin.
2. Click **Import**.
3. Choose this file from the project:

```text
database/malayanquest_db.sql
```

4. Click **Import**.
5. Confirm that the database named `malayanquest_db` appears in phpMyAdmin.

If the database already exists and you need a clean reset, drop `malayanquest_db` first, then import the SQL file again.

### Step 4: Confirm PHP Database Credentials

Open:

```text
backend/config/db.php
```

Default XAMPP credentials should be:

```php
$host = "localhost";
$db_name = "malayanquest_db";
$username = "root";
$password = "";
```

Only change these if your local MySQL setup uses a different username or password.

### Step 5: Test the Backend in a Browser

Use this URL if Apache runs on port `8080`:

```text
http://localhost:8080/IT140P-MP-MalayanQuest/backend/api/admin_dashboard.php
```

Use this URL if Apache runs on the default port `80`:

```text
http://localhost/IT140P-MP-MalayanQuest/backend/api/admin_dashboard.php
```

A successful response should be JSON and include `"success": true`.

### Step 6: Open Android Project

1. Open **Android Studio**.
2. Click **Open**.
3. Select the project folder:

```text
IT140P-MP-MalayanQuest/android
```

4. Wait for Gradle sync to finish.
5. If Android Studio asks to trust the project, trust it only if it came from the group repository.

### Step 7: Configure Android API URL

Open:

```text
android/app/src/main/java/com/malayanquest/app/Config.kt
```

For the Android emulator, use your computer localhost through `10.0.2.2`:

```text
http://10.0.2.2:8080/IT140P-MP-MalayanQuest/backend/api/
```

If Apache uses port `80`, remove `:8080`:

```text
http://10.0.2.2/IT140P-MP-MalayanQuest/backend/api/
```

For a real Android phone, replace `10.0.2.2` with your computer IPv4 address. Example:

```text
http://192.168.1.12:8080/IT140P-MP-MalayanQuest/backend/api/
```

The phone and computer must be connected to the same Wi-Fi network.

If the backend folder is copied directly into `htdocs` instead of the whole project folder, use `/backend/api/` instead of `/IT140P-MP-MalayanQuest/backend/api/`.

## 5. Running the App

### Emulator Run

1. Start XAMPP Apache and MySQL first.
2. Start an Android emulator in Android Studio.
3. Confirm `Config.DEFAULT_API_BASE_URL` uses `10.0.2.2`.
4. Click **Run** in Android Studio.
5. Log in or register using sample accounts.

### Real Phone Run

1. Start XAMPP Apache and MySQL first.
2. Connect phone and computer to the same Wi-Fi.
3. Find your computer IPv4 address:

```cmd
ipconfig
```

4. Update `Config.DEFAULT_API_BASE_URL` to use that IPv4 address.
5. Run the Android app on the connected phone.
6. If requests fail, check Windows Firewall and allow Apache through private networks.

## 6. Sample Accounts

Seed accounts from the SQL file use this prototype password:

```text
password
```

Common sample accounts:

```text
admin@mcl.edu.ph
juan.dcruz@mcl.edu.ph
maria.santos@mcl.edu.ph
carlo.reyes@mcl.edu.ph
```

Use the admin account for dashboard and moderation testing. Use student accounts to test requester and helper flows.

## 7. Required Demo Flow

Use this sequence for a presentation-ready test:

1. Log in as a requester.
2. Post a safe school-related errand.
3. Log out.
4. Log in as a helper.
5. Browse available errands.
6. Apply to the requester errand with an offer note.
7. Log out.
8. Log in as the requester.
9. Open the errand applicants.
10. Select one helper.
11. Log out.
12. Log in as the selected helper.
13. Accept the assigned errand.
14. Mark the errand as in progress.
15. Send a message.
16. Mark the errand as completed by helper.
17. Log out.
18. Log in as the requester.
19. Confirm completion.
20. Rate the helper.
21. Log in as admin.
22. Check dashboard counts, users, errands, reports, flagged records, and moderation logs.

## 8. Troubleshooting Guide

### Problem: API returns database connection error

Check these items:

- XAMPP MySQL is running.
- `malayanquest_db` exists in phpMyAdmin.
- `backend/config/db.php` credentials match your MySQL setup.
- The SQL file imported without errors.

### Problem: Browser URL shows 404 Not Found

Check these items:

- Project folder is inside `C:\xampp\htdocs`.
- Folder name in the URL exactly matches the actual folder name.
- Apache is running.
- The endpoint file exists inside `backend/api/`.

### Problem: Android app cannot connect to backend

Check these items:

- Apache and MySQL are running before opening the app.
- Emulator uses `10.0.2.2`, not `localhost`.
- Real phone uses the computer IPv4 address, not `10.0.2.2`.
- The port in `Config.DEFAULT_API_BASE_URL` matches Apache.
- Windows Firewall is not blocking Apache.
- Phone and computer are on the same Wi-Fi.
- If the message mentions `DOCTYPE` or `HTML`, the app reached an Apache error page instead of a JSON PHP API endpoint. Fix the API URL path, port, or folder name.

### Problem: Login fails for sample accounts

Check these items:

- The database seed was imported successfully.
- You are using the password `password` for seeded accounts.
- The Android app is pointed to the same backend you tested in the browser.
- The PHP `login.php` endpoint returns JSON when tested directly or through an API tester.

### Problem: Gradle sync fails

Try these fixes:

1. Open the `android` folder, not the repository root, in Android Studio.
2. Let Android Studio download missing Gradle files.
3. Check internet connection.
4. Use **File > Sync Project with Gradle Files**.
5. If needed, invalidate caches and restart Android Studio.

## 9. Development Rules for Groupmates

Follow these rules when changing the project:

1. Keep the main backend as PHP REST API and MySQL.
2. Do not replace the backend with Firebase, Supabase, or another cloud database.
3. Do not put database credentials inside Android files.
4. Use PHP prepared statements for database queries.
5. Use `password_hash` when storing new passwords.
6. Use `password_verify` when checking passwords.
7. Do not publicly display student numbers or school email addresses in helper cards.
8. Keep the blue campus theme consistent.
9. Update documentation when setup steps, endpoints, database tables, or demo flow change.
10. Test both the backend endpoint and the Android flow before submitting changes.

## 10. Git Workflow for Groupmates

Create a branch before working:

```bash
git checkout -b feature/short-description
```

After editing files, check changes:

```bash
git status
```

Commit changes with a clear message:

```bash
git add <changed-files>
git commit -m "Add short description of change"
```

Push the branch and create a pull request:

```bash
git push origin feature/short-description
```

Do not directly push to `main` unless the group agreed to do so.

## 11. AI Assistant Handoff Prompt

Copy and paste this prompt into any AI coding assistant before asking it to modify the project:

```text
You are helping with IT140P-MP-MalayanQuest, an Android Studio mobile app with a PHP REST API and MySQL database. The app is Malayan Quest, a school-only micro-errand request app for Mapúa Malayan Colleges Laguna students.

Important constraints:
- Keep the backend as PHP REST API + MySQL.
- Do not replace the backend with Firebase, Supabase, or another cloud database.
- Use database name malayanquest_db.
- SQL export file is database/malayanquest_db.sql.
- PHP API files are in backend/api/.
- PHP database config is backend/config/db.php.
- Android project is in android/.
- Android API URL is configured in android/app/src/main/java/com/malayanquest/app/Config.kt.
- Use prepared statements in PHP.
- Use password_hash and password_verify for passwords.
- Do not expose database credentials in Android.
- Do not publicly show student numbers or school email addresses.
- Preserve the clean blue MCL-inspired theme.

Before coding:
1. Read AGENTS.md, README.md, docs/LOCAL_SETUP.md, and docs/GROUPMATE_AI_SETUP_GUIDE.md.
2. Inspect the existing files instead of inventing unrelated structure.
3. Make the smallest safe change that satisfies the task.
4. Update docs if setup, endpoints, schema, or demo flow changes.
5. Run relevant checks and summarize what was tested.
```

## 12. Files to Read Before Making Major Changes

Use these files as the main source of truth:

```text
AGENTS.md
docs/LOCAL_SETUP.md
docs/API_DOCUMENTATION.md
docs/DATABASE_GUIDE.md
docs/USER_GUIDE.md
docs/GROUPMATE_AI_SETUP_GUIDE.md
README.md
backend/config/db.php
android/app/src/main/java/com/malayanquest/app/Config.kt
database/malayanquest_db.sql
```

## 13. Final Pre-Demo Checklist

Before the group demo, confirm these items:

- XAMPP Apache starts without port conflicts.
- XAMPP MySQL starts successfully.
- `malayanquest_db` exists and has data.
- Browser can open at least one backend API endpoint.
- Android app launches from Android Studio.
- Android app can log in with seeded accounts.
- Requester can post an errand.
- Helper can apply to the errand.
- Requester can select the helper.
- Helper can accept, message, and mark complete.
- Requester can confirm and rate.
- Admin can view dashboard and records.
- README, API documentation, database guide, user guide, evaluation summary, and presentation flow are ready.
