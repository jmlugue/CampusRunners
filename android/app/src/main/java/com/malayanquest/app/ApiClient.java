package com.malayanquest.app;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiClient {
    public interface Callback {
        void onComplete(JSONObject response);
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public void get(String endpoint, Map<String, String> params, Callback callback) {
        executor.execute(() -> {
            JSONObject result = request("GET", endpoint, params, null);
            mainHandler.post(() -> callback.onComplete(result));
        });
    }

    public void post(String endpoint, JSONObject body, Callback callback) {
        executor.execute(() -> {
            JSONObject result = request("POST", endpoint, null, body);
            mainHandler.post(() -> callback.onComplete(result));
        });
    }

    private JSONObject request(String method, String endpoint, Map<String, String> params, JSONObject body) {
        HttpURLConnection connection = null;
        try {
            String urlText = Config.API_BASE_URL + endpoint;
            if ("GET".equals(method) && params != null && !params.isEmpty()) {
                urlText += "?" + encodeParams(params);
            }

            URL url = new URL(urlText);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Accept", "application/json");

            if ("POST".equals(method)) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                writer.write(body == null ? "{}" : body.toString());
                writer.flush();
                writer.close();
                outputStream.close();
            }

            InputStream stream = connection.getResponseCode() >= 400
                    ? connection.getErrorStream()
                    : connection.getInputStream();
            String text = readStream(stream);
            return new JSONObject(text);
        } catch (Exception e) {
            JSONObject error = new JSONObject();
            try {
                error.put("success", false);
                error.put("message", "Connection failed: " + e.getMessage());
            } catch (Exception ignored) {
            }
            return error;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String encodeParams(Map<String, String> params) throws Exception {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return builder.toString();
    }

    private String readStream(InputStream stream) throws Exception {
        if (stream == null) {
            return "{\"success\":false,\"message\":\"Empty server response.\"}";
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        return builder.toString();
    }
}
