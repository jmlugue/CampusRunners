package com.campusrunners.app;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@SuppressLint({"SetTextI18n", "HardcodedText"})
@SuppressWarnings("SpellCheckingInspection")
public class MainActivity extends ComponentActivity {
    private static final String PREFS = "campusrunners_session";
    private static final String APP_MARK = "CR";
    private static final String[] CATEGORIES = {
            "Food Pickup",
            "Printing Pickup",
            "Document Delivery",
            "School Supplies Purchase",
            "Bluebook Purchase",
            "Bookstore Item Purchase",
            "Campus Item Delivery",
            "Classroom-to-Classroom Delivery",
            "Library or Bookstore Errand",
            "Nearby Establishment Errand",
            "Other School-Related Errand"
    };
    private static final String[] STATUS_STEPS = {
            "Open",
            "Has Applicants",
            "Assigned",
            "Accepted",
            "In Progress",
            "Completed by Helper",
            "Confirmed by Requester",
            "Rated",
            "Closed"
    };

    private LinearLayout content;
    private TextView headerTitle;
    private TextView headerSubtitle;
    private SharedPreferences prefs;
    private final ApiClient api = new ApiClient();

    private int userId;
    private String fullName;
    private String role;

    private interface JsonCardFactory {
        View create(JSONObject item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        content = findViewById(R.id.contentContainer);
        headerTitle = findViewById(R.id.headerTitle);
        headerSubtitle = findViewById(R.id.headerSubtitle);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        userId = prefs.getInt("user_id", 0);
        fullName = prefs.getString("full_name", "");
        role = prefs.getString("role", "");

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateBack();
            }
        });

        showSplash();
    }

    private void navigateBack() {
        if (userId > 0) {
            showDashboard();
        } else {
            showLogin();
        }
    }

    private void showSplash() {
        clear("CampusRunners", "School-only campus errands for MCL students");
        content.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView mark = new TextView(this);
        mark.setText(APP_MARK);
        mark.setTextColor(getColor(android.R.color.white));
        mark.setTextSize(30);
        mark.setGravity(Gravity.CENTER);
        mark.setTypeface(Typeface.DEFAULT_BOLD);
        mark.setBackground(circle(getColor(R.color.primary_blue)));
        LinearLayout.LayoutParams markParams = new LinearLayout.LayoutParams(dp(84), dp(84));
        markParams.setMargins(0, dp(32), 0, dp(16));
        content.addView(mark, markParams);

        TextView name = title("CampusRunners");
        name.setGravity(Gravity.CENTER);
        content.addView(name);

        TextView line = smallText("Post errands, apply as helpers, message, complete, rate, and report unsafe activity.");
        line.setGravity(Gravity.CENTER);
        content.addView(line);

        TextView loading = badge("Preparing prototype", R.color.secondary_blue, R.color.card_light);
        LinearLayout.LayoutParams badgeParams = wrapParams();
        badgeParams.gravity = Gravity.CENTER_HORIZONTAL;
        content.addView(loading, badgeParams);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            content.setGravity(Gravity.NO_GRAVITY);
            if (userId > 0) {
                showDashboard();
            } else {
                showLogin();
            }
        }, 900);
    }

    private void showLogin() {
        clear("CampusRunners", "Login to the campus errand prototype");

        content.addView(section("Student and Admin Login"));

        EditText email = input("School email", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        EditText password = input("Password", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        content.addView(email);
        content.addView(password);

        content.addView(button("Login", true, v -> {
            JSONObject body = new JSONObject();
            try {
                body.put("school_email", email.getText().toString().trim());
                body.put("password", password.getText().toString());
            } catch (Exception ignored) {
            }
            api.post("login.php", body, response -> {
                if (response.optBoolean("success")) {
                    JSONObject data = response.optJSONObject("data");
                    if (data != null) {
                        saveSession(data);
                        showDashboard();
                    }
                } else {
                    toast(response.optString("message"));
                }
            });
        }));

        content.addView(button("Create student account", false, v -> showRegister()));
        content.addView(infoPanel("Demo accounts use password: password\nAdmin: admin@mcl.edu.ph\nStudent: juan.dcruz@mcl.edu.ph, maria.santos@mcl.edu.ph, carlo.reyes@mcl.edu.ph"));
    }

    private void showRegister() {
        clear("Register", "Create a verified prototype student account");

        content.addView(infoPanel("Use your school email and student number. Student numbers and school emails are stored for verification and are not shown publicly."));

        EditText name = input("Full name", InputType.TYPE_CLASS_TEXT);
        EditText email = input("School email, example: student@mcl.edu.ph", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        EditText studentNumber = input("Student number", InputType.TYPE_CLASS_TEXT);
        EditText password = input("Password", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        EditText confirm = input("Confirm password", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        content.addView(name);
        content.addView(email);
        content.addView(studentNumber);
        content.addView(password);
        content.addView(confirm);

        content.addView(button("Register", true, v -> {
            if (!password.getText().toString().equals(confirm.getText().toString())) {
                toast("Passwords do not match.");
                return;
            }

            JSONObject body = new JSONObject();
            try {
                body.put("full_name", name.getText().toString().trim());
                body.put("school_email", email.getText().toString().trim());
                body.put("student_number", studentNumber.getText().toString().trim());
                body.put("password", password.getText().toString());
            } catch (Exception ignored) {
            }

            api.post("register.php", body, response -> {
                toast(response.optString("message"));
                if (response.optBoolean("success")) {
                    showLogin();
                }
            });
        }));
        content.addView(button("Back to login", false, v -> showLogin()));
    }

    private void showDashboard() {
        boolean admin = "admin".equals(role);
        clear(admin ? "Admin Dashboard" : "CampusRunners", fullName.isEmpty() ? "Student dashboard" : "Welcome, " + fullName);

        if (admin) {
            showAdminDashboard();
            return;
        }

        content.addView(greetingCard());
        content.addView(summaryGrid(new String[][]{
                {"Open Errands", "Browse"},
                {"Active Helper", "Track"},
                {"Completed", "History"},
                {"Average Rating", "Profile"}
        }, new View.OnClickListener[]{
                v -> showBrowseErrands(),
                v -> showMyHelperErrands(),
                v -> showHistory(),
                v -> showProfile()
        }));

        content.addView(section("Main Actions"));
        content.addView(actionGrid(new String[][]{
                {"Post Errand", "Create a campus request"},
                {"Browse Errands", "Find helper tasks"},
                {"My Posted", "Applicants and updates"},
                {"My Helper", "Assigned work"},
                {"Messages", "Open active chats"},
                {"History", "Past errands"},
                {"Profile", "Ratings and account"},
                {"Report", "Safety concern"}
        }, new View.OnClickListener[]{
                v -> showPostErrand(),
                v -> showBrowseErrands(),
                v -> showMyPostedErrands(),
                v -> showMyHelperErrands(),
                v -> showMessageHub(),
                v -> showHistory(),
                v -> showProfile(),
                v -> showReport(null),
        }));

        content.addView(infoPanel("Errands must be school-related, safe, and limited to campus or nearby MCL locations."));
        content.addView(button("Logout", false, v -> logout()));
    }

    private void showPostErrand() {
        clear("Post Errand", "Campus and near-campus errands only");

        content.addView(infoPanel("Banned errands include alcohol, vape products, medicines, confidential documents, answer sheets, IDs, weapons, restricted areas, and tasks that violate school rules."));

        EditText titleInput = input("Title", InputType.TYPE_CLASS_TEXT);
        EditText description = multiInput("Description");
        Spinner category = categorySpinner();
        EditText pickup = input("Pickup location", InputType.TYPE_CLASS_TEXT);
        EditText dropoff = input("Drop-off location", InputType.TYPE_CLASS_TEXT);
        EditText deadline = input("Deadline: 2026-07-15 13:00:00", InputType.TYPE_CLASS_TEXT);
        EditText reward = input("Reward amount, optional", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        EditText rewardNote = input("Reward note, optional", InputType.TYPE_CLASS_TEXT);

        content.addView(titleInput);
        content.addView(description);
        content.addView(categoryLabel());
        content.addView(category);
        content.addView(pickup);
        content.addView(dropoff);
        content.addView(deadline);
        content.addView(reward);
        content.addView(rewardNote);

        content.addView(button("Submit Errand", true, v -> {
            JSONObject body = new JSONObject();
            try {
                body.put("requester_id", userId);
                body.put("title", titleInput.getText().toString().trim());
                body.put("description", description.getText().toString().trim());
                body.put("category", category.getSelectedItem().toString());
                body.put("pickup_location", pickup.getText().toString().trim());
                body.put("dropoff_location", dropoff.getText().toString().trim());
                body.put("deadline", deadline.getText().toString().trim());
                body.put("reward_note", rewardNote.getText().toString().trim());
                String rewardText = reward.getText().toString().trim();
                if (!rewardText.isEmpty()) {
                    body.put("reward_amount", Double.parseDouble(rewardText));
                }
            } catch (Exception ignored) {
            }

            api.post("create_errand.php", body, response -> {
                toast(response.optString("message"));
                if (response.optBoolean("success")) {
                    showMyPostedErrands();
                }
            });
        }));
        content.addView(button("Back", false, v -> showDashboard()));
    }

    private void showBrowseErrands() {
        clear("Browse Errands", "Open errands from other students");
        content.addView(section("Errand Feed"));

        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(userId));
        api.get("get_available_errands.php", params, response -> {
            if (!response.optBoolean("success")) {
                toast(response.optString("message"));
                return;
            }

            JSONArray errands = response.optJSONArray("data");
            if (errands == null || errands.length() == 0) {
                content.addView(emptyState("No available errands yet."));
            } else {
                content.addView(recycler(errands, errand -> errandCard(errand, true)));
            }
            content.addView(button("Back", false, v -> showDashboard()));
        });
    }

    private void showErrandDetails(JSONObject errand, boolean applyMode) {
        clear("Errand Details", errand.optString("title"));

        LinearLayout card = card();
        addErrandCore(card, errand);
        card.addView(section("Safety Scope"));
        card.addView(smallText("Only accept if the request is safe, school-related, and limited to campus or nearby MCL locations."));
        content.addView(card);

        content.addView(section("Status Tracker"));
        content.addView(statusTracker(errand.optString("status")));

        if (applyMode) {
            content.addView(button("Apply as Helper", true, v -> showApply(errand)));
        } else {
            requesterActions(errand);
        }

        content.addView(button("Report Errand", false, v -> showReport(errand)));
        content.addView(button("Back", false, v -> {
            if (applyMode) {
                showBrowseErrands();
            } else {
                showMyPostedErrands();
            }
        }));
    }

    private void showApply(JSONObject errand) {
        clear("Apply as Helper", errand.optString("title"));

        content.addView(helperPreviewPanel(
                "Your helper profile shown to requester",
                fullName,
                "Average rating and completed errands will be loaded from your account record.",
                "Offer note and estimated completion time are public to this requester only."
        ));

        EditText offer = multiInput("Offer note");
        EditText estimate = input("Estimated completion time, example: 30 minutes", InputType.TYPE_CLASS_TEXT);
        content.addView(offer);
        content.addView(estimate);
        content.addView(button("Submit Application", true, v -> {
            JSONObject body = new JSONObject();
            try {
                body.put("errand_id", errand.optInt("errand_id"));
                body.put("helper_id", userId);
                body.put("offer_note", offer.getText().toString().trim());
                body.put("estimated_completion_time", estimate.getText().toString().trim());
            } catch (Exception ignored) {
            }

            api.post("apply_to_errand.php", body, response -> {
                toast(response.optString("message"));
                if (response.optBoolean("success")) {
                    showBrowseErrands();
                }
            });
        }));
        content.addView(button("Back", false, v -> showErrandDetails(errand, true)));
    }

    private void showMyPostedErrands() {
        clear("My Posted Errands", "Requester workflow");

        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(userId));
        api.get("get_my_posted_errands.php", params, response -> {
            renderPostedErrandList(response);
            content.addView(button("Back", false, v -> showDashboard()));
        });
    }

    private void showMyHelperErrands() {
        clear("My Helper Errands", "Applications and assigned errands");

        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(userId));
        api.get("get_my_helper_errands.php", params, response -> {
            renderHelperList(response);
            content.addView(button("Back", false, v -> showDashboard()));
        });
    }

    private void showApplicants(int errandId) {
        clear("Applicants", "Choose one helper");

        Map<String, String> params = new HashMap<>();
        params.put("errand_id", String.valueOf(errandId));
        params.put("requester_id", String.valueOf(userId));
        api.get("get_errand_applicants.php", params, response -> {
            if (!response.optBoolean("success")) {
                toast(response.optString("message"));
                return;
            }

            JSONArray applicants = response.optJSONArray("data");
            if (applicants == null || applicants.length() == 0) {
                content.addView(emptyState("No applicants yet."));
            } else {
                content.addView(recycler(applicants, applicant -> applicantCard(applicant, errandId)));
            }
            content.addView(button("Back", false, v -> showMyPostedErrands()));
        });
    }

    private void showHelperProfile(JSONObject applicant) {
        clear("Helper Profile Preview", applicant.optString("helper_name"));
        content.addView(helperPreviewPanel(
                "Public helper card",
                applicant.optString("helper_name"),
                "Rating: " + applicant.optString("average_rating", "No rating yet") + "\nCompleted errands: " + applicant.optString("completed_errands", "0"),
                "Offer: " + applicant.optString("offer_note") + "\nEstimate: " + applicant.optString("estimated_completion_time")
        ));
        content.addView(infoPanel("Privacy rule: student numbers and school email addresses are not displayed on public helper cards."));
        content.addView(button("Back", false, v -> showMyPostedErrands()));
    }

    private void showErrandStatus(JSONObject errand) {
        clear("Errand Status", errand.optString("title"));
        content.addView(statusTracker(errand.optString("status")));

        LinearLayout card = card();
        card.addView(cardTitle("Current status"));
        card.addView(badge(errand.optString("status", "Open"), colorForStatus(errand.optString("status")), R.color.card_light));
        card.addView(smallText("Use the available action buttons below to move the errand through the prototype status flow."));
        content.addView(card);

        if (errand.optInt("requester_id") == userId) {
            requesterActions(errand);
        } else {
            LinearLayout actions = card();
            helperActions(errand, actions);
            content.addView(actions);
        }
        content.addView(button("Back", false, v -> showDashboard()));
    }

    private void showMessageHub() {
        clear("Messages", "Chats open after a helper is selected");
        content.addView(infoPanel("Messaging is connected to a specific errand and is only visible to the requester and selected helper."));
        content.addView(button("My Posted Errand Chats", true, v -> showMyPostedErrands()));
        content.addView(button("My Helper Errand Chats", false, v -> showMyHelperErrands()));
        content.addView(button("Back", false, v -> showDashboard()));
    }

    private void showChat(JSONObject errand) {
        clear("Messages", errand.optString("title"));

        int requesterId = errand.optInt("requester_id");
        int helperId = errand.optInt("selected_helper_id");
        int receiverId = userId == requesterId ? helperId : requesterId;

        LinearLayout messageList = new LinearLayout(this);
        messageList.setOrientation(LinearLayout.VERTICAL);
        content.addView(messageList);

        Map<String, String> params = new HashMap<>();
        params.put("errand_id", String.valueOf(errand.optInt("errand_id")));
        params.put("user_id", String.valueOf(userId));
        api.get("get_messages.php", params, response -> {
            JSONArray messages = response.optJSONArray("data");
            if (messages == null || messages.length() == 0) {
                messageList.addView(emptyState("No messages yet."));
            } else {
                for (int i = 0; i < messages.length(); i++) {
                    JSONObject item = messages.optJSONObject(i);
                    if (item != null) {
                        int senderId = item.optInt("sender_id");
                        messageList.addView(chatBubble(
                                item.optString("sender_name"),
                                item.optString("message_text"),
                                senderId == userId
                        ));
                    }
                }
            }
        });

        LinearLayout composer = new LinearLayout(this);
        composer.setOrientation(LinearLayout.HORIZONTAL);
        composer.setGravity(Gravity.CENTER_VERTICAL);
        composer.setLayoutParams(spacedParams());
        EditText message = input("Message", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        messageParams.setMargins(0, 0, dp(8), 0);
        composer.addView(message, messageParams);
        Button send = button("Send", true, v -> {
            JSONObject body = new JSONObject();
            try {
                body.put("errand_id", errand.optInt("errand_id"));
                body.put("sender_id", userId);
                body.put("receiver_id", receiverId);
                body.put("message_text", message.getText().toString().trim());
            } catch (Exception ignored) {
            }
            api.post("send_message.php", body, response -> {
                toast(response.optString("message"));
                if (response.optBoolean("success")) {
                    showChat(errand);
                }
            });
        });
        composer.addView(send, new LinearLayout.LayoutParams(dp(92), ViewGroup.LayoutParams.WRAP_CONTENT));
        content.addView(composer);
        content.addView(button("Back", false, v -> showDashboard()));
    }

    private void showCompletionConfirmation(JSONObject errand) {
        clear("Completion Confirmation", errand.optString("title"));
        content.addView(infoPanel("Confirm only after the helper has completed the agreed errand. This moves the errand to requester-confirmed status."));
        content.addView(statusTracker(errand.optString("status")));
        content.addView(button("Confirm Completion", true, v -> postStatus("confirm_completion.php", errand, "requester_id", "Confirmed by Requester")));
        content.addView(button("Back", false, v -> showMyPostedErrands()));
    }

    private void showRating(JSONObject errand) {
        clear("Rate Helper", errand.optString("title"));

        EditText score = input("Rating 1-5", InputType.TYPE_CLASS_NUMBER);
        EditText feedback = multiInput("Feedback");
        content.addView(score);
        content.addView(feedback);
        content.addView(button("Submit Rating", true, v -> {
            JSONObject body = new JSONObject();
            try {
                body.put("errand_id", errand.optInt("errand_id"));
                body.put("rated_user_id", errand.optInt("selected_helper_id"));
                body.put("rated_by_user_id", userId);
                body.put("rating_score", Integer.parseInt(score.getText().toString().trim()));
                body.put("feedback", feedback.getText().toString().trim());
            } catch (Exception ignored) {
            }

            api.post("submit_rating.php", body, response -> {
                toast(response.optString("message"));
                if (response.optBoolean("success")) {
                    showMyPostedErrands();
                }
            });
        }));
        content.addView(button("Back", false, v -> showMyPostedErrands()));
    }

    private void showReport(JSONObject errand) {
        clear(errand == null ? "Report" : "Report Errand", "Safety and moderation");

        EditText reason = multiInput("Describe the concern");
        content.addView(infoPanel("Use reports for unsafe behavior, banned errands, restricted areas, harassment, privacy concerns, or school-rule violations."));
        content.addView(reason);

        if (errand == null) {
            content.addView(button("Back", false, v -> showDashboard()));
            return;
        }

        content.addView(button("Submit Report", true, v -> {
            JSONObject body = new JSONObject();
            try {
                body.put("errand_id", errand.optInt("errand_id"));
                body.put("reporter_id", userId);
                body.put("reason", reason.getText().toString().trim());
            } catch (Exception ignored) {
            }
            api.post("report_errand.php", body, response -> {
                toast(response.optString("message"));
                if (response.optBoolean("success")) {
                    showDashboard();
                }
            });
        }));
        content.addView(button("Back", false, v -> showErrandDetails(errand, false)));
    }

    private void showProfile() {
        clear("Profile", "Account summary and privacy-safe public data");

        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(userId));
        api.get("get_user_profile.php", params, response -> {
            JSONObject data = response.optJSONObject("data");
            LinearLayout profile = card();
            profile.addView(cardTitle(data == null ? fullName : data.optString("full_name", fullName)));
            profile.addView(smallText("Verification: " + (data == null ? "verified" : data.optString("verification_status", "verified"))));
            profile.addView(smallText("Account: " + (data == null ? "active" : data.optString("account_status", "active"))));
            profile.addView(infoPanel("Public cards show only name, average rating, completed errand count, and offer note."));
            content.addView(profile);
            content.addView(button("View History", true, v -> showHistory()));
            content.addView(button("Back", false, v -> showDashboard()));
        });
    }

    private void showHistory() {
        clear("History", "Completed errands, ratings, and feedback");

        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(userId));
        api.get("get_user_history.php", params, response -> {
            JSONArray data = response.optJSONArray("data");
            if (data == null || data.length() == 0) {
                content.addView(emptyState("No history records yet."));
            } else {
                content.addView(recycler(data, item -> adminRecordCard(item, "History Record")));
            }
            content.addView(button("Ratings", true, v -> showUserRatings()));
            content.addView(button("Back", false, v -> showDashboard()));
        });
    }

    private void showUserRatings() {
        clear("Ratings and Feedback", "Your received feedback");
        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(userId));
        api.get("get_user_ratings.php", params, response -> {
            JSONArray data = response.optJSONArray("data");
            if (data == null || data.length() == 0) {
                content.addView(emptyState("No ratings yet."));
            } else {
                content.addView(recycler(data, item -> adminRecordCard(item, "Feedback")));
            }
            content.addView(button("Back", false, v -> showHistory()));
        });
    }

    private void showAdminDashboard() {
        clear("Admin Dashboard", "Monitoring and moderation");

        Map<String, String> params = new HashMap<>();
        params.put("admin_id", String.valueOf(userId));
        api.get("admin_dashboard.php", params, response -> {
            if (!response.optBoolean("success")) {
                toast(response.optString("message"));
                return;
            }

            JSONObject data = response.optJSONObject("data");
            if (data != null) {
                content.addView(summaryGrid(new String[][]{
                        {"Total Users", String.valueOf(data.optInt("total_users"))},
                        {"Verified", String.valueOf(data.optInt("verified_users"))},
                        {"Open", String.valueOf(data.optInt("open_errands"))},
                        {"Active", String.valueOf(data.optInt("active_errands"))},
                        {"Completed", String.valueOf(data.optInt("completed_errands"))},
                        {"Cancelled", String.valueOf(data.optInt("cancelled_errands"))},
                        {"Reported", String.valueOf(data.optInt("reported_errands"))},
                        {"Flagged", String.valueOf(data.optInt("flagged_errands"))}
                }));
            }

            content.addView(section("Admin Tools"));
            content.addView(actionGrid(new String[][]{
                    {"Manage Users", "Search, restrict, deactivate"},
                    {"Manage Errands", "View and remove records"},
                    {"Reports", "Review and resolve"},
                    {"Flagged Errands", "Moderation queue"},
                    {"Ratings", "Feedback review"},
                    {"Moderation", "Rule-based logs"}
            }, new View.OnClickListener[]{
                    v -> showAdminArray("admin_get_users.php", "Manage Users"),
                    v -> showAdminArray("admin_get_errands.php", "Manage Errands"),
                    v -> showAdminArray("admin_get_reports.php", "Reports"),
                    v -> showAdminArray("admin_get_flagged_errands.php", "Flagged Errands"),
                    v -> showAdminRatingsFeedback(),
                    v -> showAdminArray("admin_get_flagged_errands.php", "Moderation Logs")
            }));
            content.addView(button("Student Dashboard", false, v -> {
                role = "student";
                showDashboard();
            }));
            content.addView(button("Logout", false, v -> logout()));
        });
    }

    private void showAdminArray(String endpoint, String screenTitle) {
        clear(screenTitle, "Admin records");

        Map<String, String> params = new HashMap<>();
        params.put("admin_id", String.valueOf(userId));
        api.get(endpoint, params, response -> {
            if (!response.optBoolean("success")) {
                toast(response.optString("message"));
                return;
            }

            JSONArray data = response.optJSONArray("data");
            if (data == null || data.length() == 0) {
                content.addView(emptyState("No records."));
            } else {
                content.addView(recycler(data, item -> adminRecordCard(item, screenTitle)));
            }
            content.addView(button("Back", false, v -> showAdminDashboard()));
        });
    }

    private void showAdminRecordDetails(JSONObject item, String title) {
        clear(title + " Details", "Admin detail view");
        content.addView(adminRecordCard(item, title));

        if (item.has("user_id")) {
            content.addView(button("Restrict User", true, v -> updateUserStatus(item.optInt("user_id"), "restricted")));
            content.addView(button("Deactivate User", false, v -> updateUserStatus(item.optInt("user_id"), "deactivated")));
        }
        if (item.has("errand_id")) {
            content.addView(button("Remove Errand", false, v -> removeErrand(item.optInt("errand_id"))));
        }
        if (item.has("report_id")) {
            content.addView(button("Resolve Report", true, v -> resolveReport(item.optInt("report_id"))));
        }
        content.addView(button("Back", false, v -> showAdminDashboard()));
    }

    private void showAdminRatingsFeedback() {
        clear("Ratings and Feedback", "Admin review screen");
        content.addView(infoPanel("This prototype keeps user ratings available through profile and history records. Add a backend endpoint for all ratings if the admin needs a full feedback export."));
        content.addView(button("View Errand Records", true, v -> showAdminArray("admin_get_errands.php", "Ratings and Feedback")));
        content.addView(button("Back", false, v -> showAdminDashboard()));
    }

    private void updateUserStatus(int targetUserId, String status) {
        JSONObject body = new JSONObject();
        try {
            body.put("admin_id", userId);
            body.put("user_id", targetUserId);
            body.put("verification_status", status);
        } catch (Exception ignored) {
        }
        api.post("admin_update_user_status.php", body, response -> {
            toast(response.optString("message"));
            showAdminDashboard();
        });
    }

    private void removeErrand(int errandId) {
        JSONObject body = new JSONObject();
        try {
            body.put("admin_id", userId);
            body.put("errand_id", errandId);
        } catch (Exception ignored) {
        }
        api.post("admin_remove_errand.php", body, response -> {
            toast(response.optString("message"));
            showAdminDashboard();
        });
    }

    private void resolveReport(int reportId) {
        JSONObject body = new JSONObject();
        try {
            body.put("admin_id", userId);
            body.put("report_id", reportId);
            body.put("resolution_note", "Resolved from Android prototype admin screen.");
        } catch (Exception ignored) {
        }
        api.post("admin_resolve_report.php", body, response -> {
            toast(response.optString("message"));
            showAdminDashboard();
        });
    }

    private void renderPostedErrandList(JSONObject response) {
        if (!response.optBoolean("success")) {
            toast(response.optString("message"));
            return;
        }

        JSONArray errands = response.optJSONArray("data");
        if (errands == null || errands.length() == 0) {
            content.addView(emptyState("You have not posted errands yet."));
            return;
        }

        content.addView(recycler(errands, errand -> errandCard(errand, false)));
    }

    private void renderHelperList(JSONObject response) {
        if (!response.optBoolean("success")) {
            toast(response.optString("message"));
            return;
        }

        JSONArray errands = response.optJSONArray("data");
        if (errands == null || errands.length() == 0) {
            content.addView(emptyState("You have no helper errands yet."));
            return;
        }

        content.addView(recycler(errands, errand -> {
            LinearLayout card = card();
            card.addView(cardTitle(errand.optString("title")));
            card.addView(badge(errand.optString("status"), colorForStatus(errand.optString("status")), R.color.card_light));
            card.addView(smallText(
                    errand.optString("category") + "\n" +
                            errand.optString("pickup_location") + " -> " + errand.optString("dropoff_location") + "\n" +
                            "Application: " + errand.optString("application_status")
            ));

            helperActions(errand, card);
            return card;
        }));
    }

    private LinearLayout errandCard(JSONObject errand, boolean applyMode) {
        LinearLayout card = card();
        addErrandCore(card, errand);
        card.addView(button("View Details", true, v -> showErrandDetails(errand, applyMode)));

        if (applyMode) {
            card.addView(button("Apply as Helper", false, v -> showApply(errand)));
            return card;
        }

        String status = errand.optString("status");
        if ("Has Applicants".equals(status) || "Open".equals(status)) {
            card.addView(button("View Applicants", true, v -> showApplicants(errand.optInt("errand_id"))));
        }
        if (errand.optInt("selected_helper_id") > 0) {
            card.addView(button("Messages", false, v -> showChat(errand)));
        }
        if ("Completed by Helper".equals(status)) {
            card.addView(button("Confirm Completion", true, v -> showCompletionConfirmation(errand)));
        }
        if ("Confirmed by Requester".equals(status)) {
            card.addView(button("Rate Helper", true, v -> showRating(errand)));
        }
        card.addView(button("Status Tracker", false, v -> showErrandStatus(errand)));
        return card;
    }

    private void addErrandCore(LinearLayout card, JSONObject errand) {
        card.addView(cardTitle(errand.optString("title")));
        card.addView(badge(errand.optString("status", "Open"), colorForStatus(errand.optString("status")), R.color.card_light));
        card.addView(smallText(
                categoryIcon(errand.optString("category")) + " " + errand.optString("category") + "\n" +
                        "Pickup: " + errand.optString("pickup_location") + "\n" +
                        "Drop-off: " + errand.optString("dropoff_location") + "\n" +
                        "Deadline: " + errand.optString("deadline") + "\n" +
                        "Reward: " + rewardText(errand) + "\n" +
                        "Requester: " + errand.optString("requester_name", "Student") + "\n" +
                        "Requester rating: " + errand.optString("requester_rating", "No rating yet")
        ));
    }

    private void requesterActions(JSONObject errand) {
        String status = errand.optString("status");
        if ("Has Applicants".equals(status) || "Open".equals(status)) {
            content.addView(button("View Applicants", true, v -> showApplicants(errand.optInt("errand_id"))));
        }
        if (errand.optInt("selected_helper_id") > 0) {
            content.addView(button("Messages", false, v -> showChat(errand)));
        }
        if ("Completed by Helper".equals(status)) {
            content.addView(button("Confirm Completion", true, v -> showCompletionConfirmation(errand)));
        }
        if ("Confirmed by Requester".equals(status)) {
            content.addView(button("Rate Helper", true, v -> showRating(errand)));
        }
    }

    private void helperActions(JSONObject errand, LinearLayout card) {
        String status = errand.optString("status");
        if ("selected".equals(errand.optString("application_status")) && "Assigned".equals(status)) {
            card.addView(button("Accept", true, v -> postStatus("accept_assigned_errand.php", errand, "helper_id", "Accepted")));
        }
        if ("Accepted".equals(status)) {
            card.addView(button("Mark In Progress", true, v -> updateStatus(errand, "In Progress")));
        }
        if ("In Progress".equals(status)) {
            card.addView(button("Mark Completed", true, v -> updateStatus(errand, "Completed by Helper")));
        }
        if (errand.optInt("selected_helper_id") == userId || "selected".equals(errand.optString("application_status"))) {
            card.addView(button("Messages", false, v -> showChat(errand)));
        }
        card.addView(button("Status Tracker", false, v -> showErrandStatus(errand)));
    }

    private LinearLayout applicantCard(JSONObject applicant, int errandId) {
        LinearLayout card = card();
        card.addView(cardTitle(applicant.optString("helper_name")));
        card.addView(smallText(
                "Rating: " + applicant.optString("average_rating", "No rating yet") + "\n" +
                        "Completed errands: " + applicant.optString("completed_errands", "0") + "\n" +
                        "Offer: " + applicant.optString("offer_note") + "\n" +
                        "Estimate: " + applicant.optString("estimated_completion_time")
        ));
        card.addView(badge(applicant.optString("status", "pending"), colorForStatus(applicant.optString("status")), R.color.card_light));
        card.addView(button("Helper Profile", false, v -> showHelperProfile(applicant)));
        if ("pending".equals(applicant.optString("status"))) {
            card.addView(button("Select Helper", true, v -> {
                JSONObject body = new JSONObject();
                try {
                    body.put("errand_id", errandId);
                    body.put("requester_id", userId);
                    body.put("application_id", applicant.optInt("application_id"));
                } catch (Exception ignored) {
                }
                api.post("select_helper.php", body, response -> {
                    toast(response.optString("message"));
                    if (response.optBoolean("success")) {
                        showMyPostedErrands();
                    }
                });
            }));
        }
        return card;
    }

    private LinearLayout adminRecordCard(JSONObject item, String title) {
        LinearLayout card = card();
        card.addView(cardTitle(title));
        card.addView(smallText(prettyJson(item)));
        card.addView(button("View", true, v -> showAdminRecordDetails(item, title)));
        return card;
    }

    private RecyclerView recycler(JSONArray data, JsonCardFactory factory) {
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView.setAdapter(new JsonCardAdapter(data, factory));
        recyclerView.setLayoutParams(spacedParams());
        return recyclerView;
    }

    private class JsonCardAdapter extends RecyclerView.Adapter<JsonCardAdapter.JsonCardHolder> {
        private final JSONArray data;
        private final JsonCardFactory factory;

        JsonCardAdapter(JSONArray data, JsonCardFactory factory) {
            this.data = data;
            this.factory = factory;
        }

        @Override
        @NonNull
        public JsonCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LinearLayout container = new LinearLayout(MainActivity.this);
            container.setOrientation(LinearLayout.VERTICAL);
            container.setLayoutParams(new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            return new JsonCardHolder(container);
        }

        @Override
        public void onBindViewHolder(@NonNull JsonCardHolder holder, int position) {
            holder.container.removeAllViews();
            JSONObject item = data.optJSONObject(position);
            if (item != null) {
                holder.container.addView(factory.create(item));
            }
        }

        @Override
        public int getItemCount() {
            return data.length();
        }

        static class JsonCardHolder extends RecyclerView.ViewHolder {
            final LinearLayout container;

            JsonCardHolder(LinearLayout itemView) {
                super(itemView);
                container = itemView;
            }
        }
    }

    private void updateStatus(JSONObject errand, String newStatus) {
        JSONObject body = new JSONObject();
        try {
            body.put("errand_id", errand.optInt("errand_id"));
            body.put("changed_by", userId);
            body.put("new_status", newStatus);
        } catch (Exception ignored) {
        }

        api.post("update_errand_status.php", body, response -> {
            toast(response.optString("message"));
            if (response.optBoolean("success")) {
                showMyHelperErrands();
            }
        });
    }

    private void postStatus(String endpoint, JSONObject errand, String userKey, String successStatus) {
        JSONObject body = new JSONObject();
        try {
            body.put("errand_id", errand.optInt("errand_id"));
            body.put(userKey, userId);
        } catch (Exception ignored) {
        }

        api.post(endpoint, body, response -> {
            toast(response.optString("message"));
            if (response.optBoolean("success")) {
                if ("Confirmed by Requester".equals(successStatus)) {
                    showMyPostedErrands();
                } else {
                    showMyHelperErrands();
                }
            }
        });
    }

    private void saveSession(JSONObject user) {
        userId = user.optInt("user_id");
        fullName = user.optString("full_name");
        role = user.optString("role");
        prefs.edit()
                .putInt("user_id", userId)
                .putString("full_name", fullName)
                .putString("role", role)
                .apply();
    }

    private void logout() {
        prefs.edit().clear().apply();
        userId = 0;
        fullName = "";
        role = "";
        showLogin();
    }

    private void clear(String title, String subtitle) {
        content.setGravity(Gravity.NO_GRAVITY);
        headerTitle.setText(title);
        headerSubtitle.setText(subtitle);
        content.removeAllViews();
    }

    private TextView title(String text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(getColor(R.color.text_primary));
        view.setTextSize(22);
        view.setTypeface(Typeface.DEFAULT_BOLD);
        view.setLayoutParams(spacedParams());
        return view;
    }

    private TextView section(String text) {
        TextView view = title(text);
        view.setTextSize(18);
        return view;
    }

    private TextView categoryLabel() {
        TextView view = smallText("Category");
        view.setTypeface(Typeface.DEFAULT_BOLD);
        return view;
    }

    private TextView smallText(String text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(getColor(R.color.text_secondary));
        view.setTextSize(14);
        view.setLineSpacing(dp(2), 1.0f);
        view.setLayoutParams(spacedParams());
        return view;
    }

    private TextView cardTitle(String text) {
        TextView view = new TextView(this);
        view.setText(text == null || text.isEmpty() ? "Untitled" : text);
        view.setTextColor(getColor(R.color.text_primary));
        view.setTextSize(17);
        view.setTypeface(Typeface.DEFAULT_BOLD);
        view.setLayoutParams(spacedParams());
        return view;
    }

    private EditText input(String hint, int inputType) {
        EditText view = new EditText(this);
        view.setHint(hint);
        view.setInputType(inputType);
        view.setTextColor(getColor(R.color.text_primary));
        view.setHintTextColor(getColor(R.color.text_secondary));
        view.setSingleLine(false);
        view.setMinHeight(dp(48));
        view.setBackgroundResource(R.drawable.input_background);
        view.setLayoutParams(spacedParams());
        return view;
    }

    private EditText multiInput(String hint) {
        EditText view = input(hint, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        view.setMinLines(3);
        view.setGravity(Gravity.TOP);
        return view;
    }

    private Spinner categorySpinner() {
        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CATEGORIES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setBackgroundResource(R.drawable.input_background);
        spinner.setPadding(dp(8), dp(6), dp(8), dp(6));
        spinner.setLayoutParams(spacedParams());
        return spinner;
    }

    private Button button(String text, boolean primary, View.OnClickListener listener) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(getColor(android.R.color.white));
        button.setAllCaps(false);
        button.setMinHeight(dp(46));
        button.setBackgroundResource(primary ? R.drawable.primary_button : R.drawable.secondary_button);
        button.setClickable(true);
        button.setFocusable(true);
        button.setEnabled(true);
        button.setOnClickListener(listener);
        button.setLayoutParams(spacedParams());
        return button;
    }

    private LinearLayout greetingCard() {
        LinearLayout card = card();
        card.setBackgroundResource(R.drawable.panel_light);
        card.addView(cardTitle("Hello, " + (fullName == null || fullName.isEmpty() ? "Student" : firstName(fullName))));
        card.addView(smallText("Manage campus errands, helper applications, messages, completion, ratings, and safety reports from one dashboard."));
        return card;
    }

    private LinearLayout summaryGrid(String[][] items) {
        return summaryGrid(items, null);
    }

    private LinearLayout summaryGrid(String[][] items, View.OnClickListener[] listeners) {
        LinearLayout grid = new LinearLayout(this);
        grid.setOrientation(LinearLayout.VERTICAL);
        grid.setLayoutParams(spacedParams());
        for (int i = 0; i < items.length; i += 2) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(spacedParams());
            View left = listeners == null ? summaryCard(items[i][0], items[i][1]) : actionTile(items[i][1], items[i][0], listeners[i]);
            row.addView(left, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            if (i + 1 < items.length) {
                LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                rightParams.setMargins(dp(10), 0, 0, 0);
                View right = listeners == null ? summaryCard(items[i + 1][0], items[i + 1][1]) : actionTile(items[i + 1][1], items[i + 1][0], listeners[i + 1]);
                row.addView(right, rightParams);
            }
            grid.addView(row);
        }
        return grid;
    }

    private LinearLayout summaryCard(String label, String value) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(12), dp(12), dp(12), dp(12));
        card.setBackgroundResource(R.drawable.card_background);
        TextView valueView = new TextView(this);
        valueView.setText(value);
        valueView.setTextColor(getColor(R.color.primary_blue));
        valueView.setTextSize(20);
        valueView.setTypeface(Typeface.DEFAULT_BOLD);
        card.addView(valueView);
        TextView labelView = smallText(label);
        labelView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        card.addView(labelView);
        return card;
    }

    private LinearLayout actionGrid(String[][] items, View.OnClickListener[] listeners) {
        LinearLayout grid = new LinearLayout(this);
        grid.setOrientation(LinearLayout.VERTICAL);
        grid.setLayoutParams(spacedParams());
        for (int i = 0; i < items.length; i += 2) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(spacedParams());
            row.addView(actionTile(items[i][0], items[i][1], listeners[i]), new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            if (i + 1 < items.length) {
                LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                rightParams.setMargins(dp(10), 0, 0, 0);
                row.addView(actionTile(items[i + 1][0], items[i + 1][1], listeners[i + 1]), rightParams);
            }
            grid.addView(row);
        }
        return grid;
    }

    private Button actionTile(String title, String subtitle, View.OnClickListener listener) {
        Button tile = new Button(this);
        tile.setText(title);
        tile.setContentDescription(title + ". " + subtitle);
        tile.setTextColor(getColor(R.color.text_primary));
        tile.setTextSize(13);
        tile.setAllCaps(false);
        tile.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        tile.setPadding(dp(12), dp(8), dp(12), dp(8));
        tile.setBackgroundResource(R.drawable.action_tile_background);
        tile.setClickable(true);
        tile.setFocusable(true);
        tile.setEnabled(true);
        tile.setMinHeight(dp(70));
        tile.setOnClickListener(listener);
        return tile;
    }

    private LinearLayout card() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(12), dp(14), dp(12));
        card.setBackgroundResource(R.drawable.card_background);
        card.setLayoutParams(spacedParams());
        return card;
    }

    private LinearLayout infoPanel(String text) {
        LinearLayout panel = card();
        panel.setBackgroundResource(R.drawable.panel_light);
        panel.addView(smallText(text));
        return panel;
    }

    private LinearLayout emptyState(String text) {
        LinearLayout panel = infoPanel(text);
        panel.setGravity(Gravity.CENTER);
        return panel;
    }

    private LinearLayout helperPreviewPanel(String eyebrow, String name, String stats, String note) {
        LinearLayout card = card();
        card.addView(badge(eyebrow, R.color.secondary_blue, R.color.card_light));
        card.addView(cardTitle(name == null || name.isEmpty() ? "Student Helper" : name));
        card.addView(smallText(stats));
        card.addView(smallText(note));
        return card;
    }

    private LinearLayout statusTracker(String currentStatus) {
        HorizontalScrollView scroll = new HorizontalScrollView(this);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.setLayoutParams(spacedParams());
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 0, dp(4), 0);

        int currentIndex = statusIndex(currentStatus);
        for (int i = 0; i < STATUS_STEPS.length; i++) {
            TextView chip = new TextView(this);
            chip.setText(STATUS_STEPS[i]);
            chip.setTextSize(12);
            chip.setGravity(Gravity.CENTER);
            chip.setTypeface(Typeface.DEFAULT_BOLD);
            boolean completed = currentIndex >= 0 && i < currentIndex;
            boolean active = i == currentIndex;
            int textColor = completed ? R.color.success_green : active ? R.color.primary_blue : R.color.text_secondary;
            int fillColor = completed ? Color.rgb(232, 245, 233) : active ? Color.rgb(220, 235, 250) : Color.rgb(244, 248, 252);
            chip.setTextColor(getColor(textColor));
            chip.setBackground(roundedPill(fillColor, getColor(textColor)));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(38));
            params.setMargins(0, 0, dp(8), 0);
            row.addView(chip, params);
        }
        scroll.addView(row);

        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.addView(scroll);
        return wrapper;
    }

    private LinearLayout chatBubble(String sender, String message, boolean mine) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(mine ? Gravity.END : Gravity.START);
        row.setLayoutParams(spacedParams());

        LinearLayout bubble = new LinearLayout(this);
        bubble.setOrientation(LinearLayout.VERTICAL);
        bubble.setPadding(dp(12), dp(8), dp(12), dp(8));
        bubble.setBackgroundResource(mine ? R.drawable.chat_bubble_mine : R.drawable.chat_bubble_other);
        LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(dp(260), ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView senderView = new TextView(this);
        senderView.setText(mine ? "You" : sender);
        senderView.setTextColor(mine ? getColor(android.R.color.white) : getColor(R.color.text_secondary));
        senderView.setTypeface(Typeface.DEFAULT_BOLD);
        senderView.setTextSize(12);
        bubble.addView(senderView);

        TextView messageView = new TextView(this);
        messageView.setText(message);
        messageView.setTextColor(mine ? getColor(android.R.color.white) : getColor(R.color.text_primary));
        messageView.setTextSize(14);
        bubble.addView(messageView);

        row.addView(bubble, bubbleParams);
        return row;
    }

    private TextView badge(String text, int textColorRes, int fillColorRes) {
        TextView badge = new TextView(this);
        badge.setText(text == null || text.isEmpty() ? "Open" : text);
        badge.setTextSize(12);
        badge.setTypeface(Typeface.DEFAULT_BOLD);
        badge.setTextColor(getColor(textColorRes));
        badge.setGravity(Gravity.CENTER);
        badge.setPadding(dp(10), dp(4), dp(10), dp(4));
        badge.setBackground(roundedPill(getColor(fillColorRes), getColor(textColorRes)));
        badge.setLayoutParams(wrapParams());
        return badge;
    }

    private GradientDrawable roundedPill(int fillColor, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fillColor);
        drawable.setCornerRadius(dp(18));
        drawable.setStroke(dp(1), strokeColor);
        return drawable;
    }

    private GradientDrawable circle(int fillColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(fillColor);
        return drawable;
    }

    private LinearLayout.LayoutParams spacedParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dp(10));
        return params;
    }

    private LinearLayout.LayoutParams wrapParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dp(10));
        return params;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private int statusIndex(String status) {
        for (int i = 0; i < STATUS_STEPS.length; i++) {
            if (STATUS_STEPS[i].equals(status)) {
                return i;
            }
        }
        return -1;
    }

    private int colorForStatus(String status) {
        if (status == null) {
            return R.color.primary_blue;
        }
        if (status.contains("Completed") || status.contains("Confirmed") || status.contains("Rated") || status.contains("Closed")) {
            return R.color.success_green;
        }
        if (status.contains("Cancel") || status.contains("Reported") || status.contains("Removed")) {
            return R.color.danger_red;
        }
        if (status.contains("Flagged") || status.contains("pending") || status.contains("Has Applicants")) {
            return R.color.warning_amber;
        }
        return R.color.primary_blue;
    }

    private String rewardText(JSONObject errand) {
        String amount = errand.optString("reward_amount", "");
        String note = errand.optString("reward_note", "");
        if (amount.isEmpty() || "null".equals(amount)) {
            return note.isEmpty() ? "None" : note;
        }
        return "PHP " + amount + (note.isEmpty() ? "" : " - " + note);
    }

    private String categoryIcon(String category) {
        if (category == null) {
            return "[ ]";
        }
        if (category.contains("Food")) {
            return "[Food]";
        }
        if (category.contains("Printing")) {
            return "[Print]";
        }
        if (category.contains("Document")) {
            return "[Doc]";
        }
        if (category.contains("Bookstore") || category.contains("Bluebook")) {
            return "[Book]";
        }
        if (category.contains("Delivery")) {
            return "[Move]";
        }
        return "[Task]";
    }

    private String firstName(String value) {
        String trimmed = value.trim();
        int space = trimmed.indexOf(" ");
        return space > 0 ? trimmed.substring(0, space) : trimmed;
    }

    private String prettyJson(JSONObject item) {
        StringBuilder builder = new StringBuilder();
        JSONArray names = item.names();
        if (names == null) {
            return item.toString();
        }
        for (int i = 0; i < names.length(); i++) {
            String key = names.optString(i);
            if ("student_number".equals(key) || "school_email".equals(key) || "password_hash".equals(key)) {
                continue;
            }
            builder.append(key.replace("_", " ")).append(": ").append(item.optString(key)).append("\n");
        }
        return builder.toString().trim();
    }

    private void toast(String message) {
        Toast.makeText(this, message == null || message.isEmpty() ? "Done" : message, Toast.LENGTH_LONG).show();
    }
}
