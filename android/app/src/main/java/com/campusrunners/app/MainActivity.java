package com.campusrunners.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {
    private static final String PREFS = "campusrunners_session";
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

    private LinearLayout content;
    private TextView headerTitle;
    private TextView headerSubtitle;
    private SharedPreferences prefs;
    private final ApiClient api = new ApiClient();

    private int userId;
    private String fullName;
    private String role;

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

        if (userId > 0) {
            showDashboard();
        } else {
            showLogin();
        }
    }

    @Override
    public void onBackPressed() {
        if (userId > 0) {
            showDashboard();
        } else {
            showLogin();
        }
    }

    private void showLogin() {
        clear("CampusRunners", "Login to the campus errand prototype");

        TextView title = title("Login");
        content.addView(title);

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

        TextView demo = smallText("Demo accounts use password: password\nAdmin: admin@mcl.edu.ph\nStudent: juan.dcruz@mcl.edu.ph, maria.santos@mcl.edu.ph, carlo.reyes@mcl.edu.ph");
        content.addView(demo);
    }

    private void showRegister() {
        clear("Register", "Create a verified prototype student account");

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
        clear("CampusRunners", fullName.isEmpty() ? "Student dashboard" : "Welcome, " + fullName);

        content.addView(title("Main Actions"));
        content.addView(button("Post Errand", true, v -> showPostErrand()));
        content.addView(button("Browse Errands", true, v -> showBrowseErrands()));
        content.addView(button("My Posted Errands", false, v -> showMyPostedErrands()));
        content.addView(button("My Helper Errands", false, v -> showMyHelperErrands()));

        if ("admin".equals(role)) {
            content.addView(button("Admin Dashboard", false, v -> showAdminDashboard()));
        }

        content.addView(button("Logout", false, v -> {
            prefs.edit().clear().apply();
            userId = 0;
            fullName = "";
            role = "";
            showLogin();
        }));
    }

    private void showPostErrand() {
        clear("Post Errand", "Campus and near-campus errands only");

        EditText titleInput = input("Title", InputType.TYPE_CLASS_TEXT);
        EditText description = multiInput("Description");
        Spinner category = spinner(CATEGORIES);
        EditText pickup = input("Pickup location", InputType.TYPE_CLASS_TEXT);
        EditText dropoff = input("Drop-off location", InputType.TYPE_CLASS_TEXT);
        EditText deadline = input("Deadline: 2026-07-15 13:00:00", InputType.TYPE_CLASS_TEXT);
        EditText reward = input("Reward amount, optional", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        EditText rewardNote = input("Reward note, optional", InputType.TYPE_CLASS_TEXT);

        content.addView(titleInput);
        content.addView(description);
        content.addView(label("Category"));
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

        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(userId));
        api.get("get_available_errands.php", params, response -> {
            if (!response.optBoolean("success")) {
                toast(response.optString("message"));
                return;
            }

            JSONArray errands = response.optJSONArray("data");
            if (errands == null || errands.length() == 0) {
                content.addView(smallText("No available errands yet."));
            } else {
                for (int i = 0; i < errands.length(); i++) {
                    JSONObject errand = errands.optJSONObject(i);
                    if (errand != null) {
                        content.addView(errandCard(errand, true));
                    }
                }
            }
            content.addView(button("Back", false, v -> showDashboard()));
        });
    }

    private void showApply(JSONObject errand) {
        clear("Apply as Helper", errand.optString("title"));

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
        content.addView(button("Back", false, v -> showBrowseErrands()));
    }

    private void showMyPostedErrands() {
        clear("My Posted Errands", "Requester workflow");

        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(userId));
        api.get("get_my_posted_errands.php", params, response -> {
            renderErrandList(response, false, "You have not posted errands yet.");
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
                content.addView(smallText("No applicants yet."));
            } else {
                for (int i = 0; i < applicants.length(); i++) {
                    JSONObject applicant = applicants.optJSONObject(i);
                    if (applicant != null) {
                        content.addView(applicantCard(applicant, errandId));
                    }
                }
            }
            content.addView(button("Back", false, v -> showMyPostedErrands()));
        });
    }

    private void showChat(JSONObject errand) {
        clear("Messages", errand.optString("title"));

        int requesterId = errand.optInt("requester_id");
        int helperId = errand.optInt("selected_helper_id");
        int receiverId = userId == requesterId ? helperId : requesterId;

        LinearLayout messageList = new LinearLayout(this);
        messageList.setOrientation(LinearLayout.VERTICAL);
        content.addView(messageList);

        EditText message = multiInput("Message");
        content.addView(message);
        content.addView(button("Send", true, v -> {
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
        }));

        Map<String, String> params = new HashMap<>();
        params.put("errand_id", String.valueOf(errand.optInt("errand_id")));
        params.put("user_id", String.valueOf(userId));
        api.get("get_messages.php", params, response -> {
            JSONArray messages = response.optJSONArray("data");
            if (messages == null || messages.length() == 0) {
                messageList.addView(smallText("No messages yet."));
            } else {
                for (int i = 0; i < messages.length(); i++) {
                    JSONObject item = messages.optJSONObject(i);
                    if (item != null) {
                        messageList.addView(smallText(item.optString("sender_name") + ": " + item.optString("message_text")));
                    }
                }
            }
        });

        content.addView(button("Back", false, v -> showDashboard()));
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
                content.addView(cardText(
                        "Users: " + data.optInt("total_users") + "\n" +
                                "Verified: " + data.optInt("verified_users") + "\n" +
                                "Open errands: " + data.optInt("open_errands") + "\n" +
                                "Active errands: " + data.optInt("active_errands") + "\n" +
                                "Completed errands: " + data.optInt("completed_errands") + "\n" +
                                "Cancelled errands: " + data.optInt("cancelled_errands") + "\n" +
                                "Reported errands: " + data.optInt("reported_errands") + "\n" +
                                "Flagged errands: " + data.optInt("flagged_errands")
                ));
            }

            content.addView(button("Users", false, v -> showAdminArray("admin_get_users.php", "Users")));
            content.addView(button("Errands", false, v -> showAdminArray("admin_get_errands.php", "Errands")));
            content.addView(button("Flagged Errands", false, v -> showAdminArray("admin_get_flagged_errands.php", "Flagged")));
            content.addView(button("Reports", false, v -> showAdminArray("admin_get_reports.php", "Reports")));
            content.addView(button("Back", false, v -> showDashboard()));
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
                content.addView(smallText("No records."));
            } else {
                for (int i = 0; i < data.length(); i++) {
                    JSONObject item = data.optJSONObject(i);
                    if (item != null) {
                        content.addView(cardText(item.toString()));
                    }
                }
            }
            content.addView(button("Back", false, v -> showAdminDashboard()));
        });
    }

    private void renderErrandList(JSONObject response, boolean applyMode, String emptyMessage) {
        if (!response.optBoolean("success")) {
            toast(response.optString("message"));
            return;
        }

        JSONArray errands = response.optJSONArray("data");
        if (errands == null || errands.length() == 0) {
            content.addView(smallText(emptyMessage));
            return;
        }

        for (int i = 0; i < errands.length(); i++) {
            JSONObject errand = errands.optJSONObject(i);
            if (errand != null) {
                content.addView(errandCard(errand, applyMode));
            }
        }
    }

    private void renderHelperList(JSONObject response) {
        if (!response.optBoolean("success")) {
            toast(response.optString("message"));
            return;
        }

        JSONArray errands = response.optJSONArray("data");
        if (errands == null || errands.length() == 0) {
            content.addView(smallText("You have no helper errands yet."));
            return;
        }

        for (int i = 0; i < errands.length(); i++) {
            JSONObject errand = errands.optJSONObject(i);
            if (errand == null) {
                continue;
            }

            LinearLayout card = card();
            card.addView(cardTitle(errand.optString("title")));
            card.addView(smallText(
                    errand.optString("category") + "\n" +
                            errand.optString("pickup_location") + " -> " + errand.optString("dropoff_location") + "\n" +
                            "Errand status: " + errand.optString("status") + "\n" +
                            "Application: " + errand.optString("application_status")
            ));

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

            content.addView(card);
        }
    }

    private LinearLayout errandCard(JSONObject errand, boolean applyMode) {
        LinearLayout card = card();
        card.addView(cardTitle(errand.optString("title")));
        card.addView(smallText(
                errand.optString("category") + "\n" +
                        errand.optString("pickup_location") + " -> " + errand.optString("dropoff_location") + "\n" +
                        "Deadline: " + errand.optString("deadline") + "\n" +
                        "Reward: " + errand.optString("reward_amount", "None") + "\n" +
                        "Status: " + errand.optString("status")
        ));

        if (applyMode) {
            card.addView(button("Apply", true, v -> showApply(errand)));
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
            card.addView(button("Confirm Completion", true, v -> postStatus("confirm_completion.php", errand, "requester_id", "Confirmed by Requester")));
        }
        if ("Confirmed by Requester".equals(status)) {
            card.addView(button("Rate Helper", true, v -> showRating(errand)));
        }
        return card;
    }

    private LinearLayout applicantCard(JSONObject applicant, int errandId) {
        LinearLayout card = card();
        card.addView(cardTitle(applicant.optString("helper_name")));
        card.addView(smallText(
                "Rating: " + applicant.optString("average_rating") + "\n" +
                        "Completed errands: " + applicant.optString("completed_errands") + "\n" +
                        "Offer: " + applicant.optString("offer_note") + "\n" +
                        "Estimate: " + applicant.optString("estimated_completion_time") + "\n" +
                        "Status: " + applicant.optString("status")
        ));
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

    private void clear(String title, String subtitle) {
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

    private TextView label(String text) {
        TextView view = smallText(text);
        view.setTypeface(Typeface.DEFAULT_BOLD);
        return view;
    }

    private TextView smallText(String text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(getColor(R.color.text_secondary));
        view.setTextSize(14);
        view.setLineSpacing(2, 1.0f);
        view.setLayoutParams(spacedParams());
        return view;
    }

    private TextView cardTitle(String text) {
        TextView view = new TextView(this);
        view.setText(text);
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
        view.setLayoutParams(spacedParams());
        return view;
    }

    private EditText multiInput(String hint) {
        EditText view = input(hint, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        view.setMinLines(3);
        return view;
    }

    private Spinner spinner(String[] values) {
        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setLayoutParams(spacedParams());
        return spinner;
    }

    private Button button(String text, boolean primary, View.OnClickListener listener) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(getColor(android.R.color.white));
        button.setAllCaps(false);
        button.setBackgroundResource(primary ? R.drawable.primary_button : R.drawable.secondary_button);
        button.setOnClickListener(listener);
        button.setLayoutParams(spacedParams());
        return button;
    }

    private LinearLayout card() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(12), dp(14), dp(12));
        card.setBackgroundResource(R.drawable.card_background);
        card.setLayoutParams(spacedParams());
        return card;
    }

    private LinearLayout cardText(String text) {
        LinearLayout card = card();
        card.addView(smallText(text));
        return card;
    }

    private LinearLayout.LayoutParams spacedParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dp(10));
        return params;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private void toast(String message) {
        Toast.makeText(this, message == null || message.isEmpty() ? "Done" : message, Toast.LENGTH_LONG).show();
    }
}
