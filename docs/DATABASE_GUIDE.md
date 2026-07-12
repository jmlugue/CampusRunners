# DATABASE_GUIDE.md

## Database Name

```sql
malayanquest_db
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
2. Create a new database named `malayanquest_db`.
3. Import `database/malayanquest_db.sql`.
4. Import `database/demo_seed_records.sql` for presentation-ready demo records across student, helper, and admin screens.
5. Check if sample users, errands, applications, messages, ratings, reports, and moderation logs are inserted.
6. Update `backend/config/db.php` with your local database settings.

`demo_seed_records.sql` is idempotent, so it can be imported again to restore the demo data.

## Exporting for Submission

Before submission:

1. Open phpMyAdmin.
2. Select `malayanquest_db`.
3. Click Export.
4. Choose SQL format.
5. Save as `malayanquest_db.sql`.
6. Place it inside the `database` folder.
