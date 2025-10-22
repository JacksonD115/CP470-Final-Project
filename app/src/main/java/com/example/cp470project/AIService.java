package com.example.cp470project;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class AIService {
    private static final String TAG = "AIService";
    public static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private final String apiKey;
    private final ExecutorService executorService;

    public AIService(String apiKey) {
        this.apiKey = apiKey;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public interface AICallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public void sendMessage(String userMessage, AICallback callback) {
        sendMessage(userMessage, "GPT-4o mini", callback);
    }

    public void sendMessage(String userMessage, String model, AICallback callback) {
        executorService.execute(() -> {
            try {
                String response = makeAPICall(userMessage, model);
                callback.onSuccess(response);
            } catch (Exception e) {
                Log.e(TAG, "Error calling OpenAI API", e);
                callback.onError(e.getMessage());
            }
        });
    }

    private String makeAPICall(String userMessage, String model) throws IOException, JSONException {
        URL url = new URL(OPENAI_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setDoInput(true);
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(120000);

            JSONObject requestBody = createRequestBody(userMessage, model, true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                return readResponse(conn);
            } else {
                String errorResponse = readErrorResponse(conn);
                throw new IOException("API Error (Code: " + responseCode + "): " + errorResponse);
            }

        } finally {
            conn.disconnect();
        }
    }

    public void generateLearningPlan(LearningProfile profile, int currentDay, AICallback callback) {
        String prompt = buildLearningPrompt(profile, currentDay);

        executorService.execute(() -> {
            try {
                String response = makeAPICallWithJsonMode(prompt, "gpt-4o-mini");
                callback.onSuccess(response);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    private String makeAPICallWithJsonMode(String prompt, String model) throws IOException, JSONException {
        URL url = new URL(OPENAI_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer" + apiKey);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);

            JSONObject requestBody = createRequestBodyWithJsonMode(prompt, model, true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                return readResponse(conn);
            } else {
                String errorResponse = readErrorResponse(conn);
                throw  new IOException("API Error (Code: " + responseCode + "): " + errorResponse);
            }
        } finally {
            conn.disconnect();
        }
    }

    private JSONObject createRequestBodyWithJsonMode(String userMessage, String model, boolean jsonMode) throws JSONException {

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", model);

        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", userMessage);
        messages.put(message);

        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        requestBody.put("maxc_tokens", 2000);

        if (jsonMode) {
            JSONObject responseFormat = new JSONObject();
            responseFormat.put("type", "json_object");
            requestBody.put("response_format", responseFormat);
        }

        return requestBody;
    }

    @SuppressLint("DefaultLocale")
    private String buildLearningPrompt(LearningProfile profile, int currentDate) {
        return String.format(
                "You are a personalized learning coach. Create a detailed learning plan for day %d of %d.\n\n" +
                        "User Profile:\n" +
                        "- Expertise Level: %s\n" +
                        "- Learning Goal: %s\n" +
                        "- Total Days: %d\n\n" +
                        "- Topic: %s\n\n" +
                        "Provide a structured daily lesson in JSON format with:\n" +
                        "{\n" +
                        "  \"dayNumber\": %d,\n" +
                        "  \"summary\": \"brief overview of the day\",\n" +
                        "  \"readings\": [\n" +
                        "    {\"title\": \"...\", \"description\": \"...\", \"estimatedMinutes\": 20}\n" +
                        "  ],\n" +
                        "  \"exercises\": [\n" +
                        "    {\"title\": \"...\", \"description\": \"...\", \"difficulty\": \"easy|medium|hard\", \"estimatedMinutes\": 30}\n" +
                        "  ]\n" +
                        "}\n" +
                        "Focus on practical, actionable content appropriate for a %s learner.",
                currentDate,
                profile.getNumberOfDays(),
                profile.getExpertiseLevel(),
                profile.getEndGoal(),
                profile.getNumberOfDays(),
                profile.getTopic(),
                currentDate,
                profile.getExpertiseLevel()
        );
    }

    private JSONObject createRequestBody(String userMessage, String model, boolean jsonMode) throws  JSONException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", model);

        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", userMessage);
        messages.put(message);

        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.8);
        requestBody.put("max_tokens", 3000);

        if (jsonMode) {
            JSONObject responseFormat = new JSONObject();
            responseFormat.put("type", "json_object");
            requestBody.put("response_format", responseFormat);
        }

        return requestBody;
    }

    private String readResponse(HttpURLConnection conn) throws IOException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
        }

        return response.toString();
    }

    private String readErrorResponse(HttpURLConnection conn) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) {
                response.append(line.trim());
            }
            return response.toString();
        } catch (Exception e) {
            return "Unable to read error response";
        }
    }

    public String parseResponse(String jsonResponse) throws JSONException {
        JSONObject json = new JSONObject(jsonResponse);
        JSONArray choices = json.getJSONArray("choices");
        JSONObject firstChoice = choices.getJSONObject(0);
        JSONObject message = firstChoice.getJSONObject("message");
        return message.getString("content");
    }

    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
