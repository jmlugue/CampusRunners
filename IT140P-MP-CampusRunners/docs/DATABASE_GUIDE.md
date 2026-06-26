# DATABASE_GUIDE.md

## Database Name

```sql
campusrunners_db
```

## Main Tables

1. users
2. locations
3. errands
4. errand_applications
5. errand_status_logs
6. messages
7. ratings
8. reports
9. moderation_logs
10. admin_actions

## Setup Steps

1. Open phpMyAdmin.
2. Create a new database named `campusrunners_db`.
3. Import `database/campusrunners_db.sql`.
4. Check if sample users and locations are inserted.
5. Update `backend/config/db.php` with your local database settings.

## Exporting for Submission

Before submission:

1. Open phpMyAdmin.
2. Select `campusrunners_db`.
3. Click Export.
4. Choose SQL format.
5. Save as `campusrunners_db.sql`.
6. Place it inside the `database` folder.
