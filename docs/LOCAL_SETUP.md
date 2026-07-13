# Malayan Quest Local Setup Guide

## 1. Install Required Tools

Install these first:

1. XAMPP
2. Android Studio
3. A browser such as Chrome

## 2. Start XAMPP

1. Open XAMPP Control Panel.
2. Start `Apache`.
3. Start `MySQL`.
4. Open this in your browser:

```text
http://localhost/phpmyadmin
```

If your XAMPP Apache port is `8080`, use:

```text
http://localhost:8080/phpmyadmin
```

If phpMyAdmin opens, Apache and MySQL are running.

## 3. Put the Backend in htdocs

Copy this whole project folder:

```text
D:\MalayanQuest
```

to your XAMPP `htdocs` folder and rename it to:

```text
IT140P-MP-MalayanQuest
```

Expected backend path:

```text
C:\xampp\htdocs\IT140P-MP-MalayanQuest\backend
```

## 4. Import the Database

1. Open phpMyAdmin.
2. Click `Import`.
3. Choose:

```text
database/malayanquest_db.sql
```

4. Click `Import`.
5. Confirm that `malayanquest_db` appears in the left sidebar.

The seed accounts use this password:

```text
password
```

Sample accounts:

```text
admin@mcl.edu.ph
juan.dcruz@mcl.edu.ph
maria.santos@mcl.edu.ph
carlo.reyes@mcl.edu.ph
```

## 5. Check Database Credentials

Open:

```text
backend/config/db.php
```

Default XAMPP settings are:

```php
$host = "localhost";
$db_name = "malayanquest_db";
$username = "root";
$password = "";
```

These should work unless you changed your MySQL password.

## 6. Test the PHP API in Browser

Open:

```text
http://localhost/IT140P-MP-MalayanQuest/backend/api/admin_dashboard.php
```

If Apache is configured for port `8080`, use:

```text
http://localhost:8080/IT140P-MP-MalayanQuest/backend/api/admin_dashboard.php
```

Expected result:

```json
{
  "success": true,
  "message": "Admin dashboard counts retrieved.",
  "data": {}
}
```

The numbers inside `data` will depend on the imported records.

## 7. Open the Android App

1. Open Android Studio.
2. Click `Open`.
3. Select:

```text
D:\MalayanQuest\android
```

4. Wait for Gradle sync.
5. Run the app on an Android emulator.

For emulator testing, set `Config.DEFAULT_API_BASE_URL` in `android/app/src/main/java/com/malayanquest/app/Config.kt` to:

```text
http://10.0.2.2:8080/IT140P-MP-MalayanQuest/backend/api/
```

`10.0.2.2` means "your computer's localhost" from the Android emulator. The `:8080` part must match your Apache port. If Apache uses port `80`, remove `:8080`.

If your teammate copied only the backend folder directly into `htdocs`, use:

```text
http://10.0.2.2:8080/backend/api/
```

## 8. View the Output

You can view output in three ways:

1. Browser API output:

```text
http://localhost:8080/IT140P-MP-MalayanQuest/backend/api/admin_dashboard.php
```

2. phpMyAdmin database tables:

```text
http://localhost/phpmyadmin
```

3. Android app screens from Android Studio emulator.

## 9. Phone Testing

If you use a real phone instead of emulator:

1. Connect the phone and computer to the same Wi-Fi.
2. Find your computer IPv4 address using `ipconfig`.
3. Edit:

```text
android/app/src/main/java/com/malayanquest/app/Config.kt
```

4. Replace `10.0.2.2` with your computer IPv4 address.

Example:

```text
http://192.168.1.12:8080/IT140P-MP-MalayanQuest/backend/api/
```

If the app says the server returned `HTML` or `DOCTYPE` instead of JSON, the URL is reaching an Apache page or error page instead of the PHP API endpoint. Check the project folder name, Apache port, and `backend/api/` path.
