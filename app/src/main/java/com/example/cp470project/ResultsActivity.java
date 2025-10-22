package com.example.cp470project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    private static final String TAG = "ResultsActivity";

    private AIService aiService;
    private LearningProfile learningProfile;
    private RecyclerView daysRecyclerView;
    private ProgressBar loadingIndicator;
    private TextView errorText;
    private DayAdapter dayAdapter;
    private boolean isLoading;
    private Button finalQuizButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        daysRecyclerView = findViewById(R.id.days_recycler_view);
        loadingIndicator = findViewById(R.id.loading_indicator);
        errorText = findViewById(R.id.error_text);

        learningProfile = new LearningProfile(
                getIntent().getStringExtra("expertise_level"),
                getIntent().getIntExtra("number_of_days", 7),
                getIntent().getStringExtra("end_goal"),
                getIntent().getStringExtra("topic")
        );

        daysRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dayAdapter = new DayAdapter(new ArrayList<>(), this::onDayClicked);
        daysRecyclerView.setAdapter(dayAdapter);
        finalQuizButton = findViewById(R.id.final_quiz_button);

        finalQuizButton.setOnClickListener(v -> {
            generateFinalQuiz();
        });

        try {
            String apiKey = BuildConfig.OPENAI_API_KEY;
            Log.d(TAG, "BuildConfig.OPENAI_API_KEY = " + (apiKey != null ? apiKey : "NULL"));

            if (apiKey == null || apiKey.isEmpty()) {
                Log.e(TAG, "API Key is null or empty");
                showError("OpenAI API key not found. Please check your local.properties file.");
                return;
            }

            aiService = new AIService(apiKey);
            Log.d(TAG, "AIService created successfully");

            loadLearningPlan();

        } catch (Exception e) {
            Log.e(TAG, "Error creating AIService", e);
            showError("Error initializing AI service: " + e.getMessage());
        }
    }

    private void loadLearningPlan() {

        if (aiService == null) {
            Log.e(TAG, "aiService is null in loadLearningPlan");
            showError("AI Service not initialized");
            return;
        }

        isLoading = true;
        loadingIndicator.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);
        daysRecyclerView.setVisibility(View.GONE);

        String prompt = buildPrompt();
        Log.d(TAG, "Sending prompt to AI service");

        aiService.sendMessage(prompt, "gpt-4o-mini", new AIService.AICallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    try {
                        String content = aiService.parseResponse(response);
                        List<DailyLesson> lessons = parseLessons(content);
                        displayLessons(lessons);
                    } catch (JSONException e) {
                        Log.e(TAG, "Failed to parse response", e);
                        showError("Failed to parse learning plan: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e(TAG, "AI Service error: " + error);
                    showError("Error: " + error);
                });
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private String buildPrompt() {
        return String.format(
                "You are a personalized learning coach. Create a %d-day learning plan for learning %s.\n\n" +
                        "User Profile:\n" +
                        "- Expertise Level: %s\n" +
                        "- Learning Goal: %s\n" +
                        "- Topic: %s\n" +
                        "- Total Days: %d\n\n" +
                        "For EACH day, provide:\n" +
                        "1. A focus title and description\n" +
                        "2. 2-3 study resources (with titles, descriptions, and URLs)\n" +
                        "3. 3 multiple choice practice questions (with 4 options A-D, correct answer, and explanation)\n\n" +
                        "Provide everything in JSON format:\n" +
                        "{\n" +
                        "  \"days\": [\n" +
                        "    {\n" +
                        "      \"day\": 1,\n" +
                        "      \"focus\": \"Brief title\",\n" +
                        "      \"description\": \"What to learn today\",\n" +
                        "      \"resources\": [\n" +
                        "        {\"title\": \"Resource 1\", \"description\": \"Description\", \"url\": \"https://example.com\"}\n" +
                        "      ],\n" +
                        "      \"practiceQuestions\": [\n" +
                        "        {\n" +
                        "          \"question\": \"What is...?\",\n" +
                        "          \"optionA\": \"Option A\",\n" +
                        "          \"optionB\": \"Option B\",\n" +
                        "          \"optionC\": \"Option C\",\n" +
                        "          \"optionD\": \"Option D\",\n" +
                        "          \"correctAnswer\": \"A\",\n" +
                        "          \"explanation\": \"Why this is correct\"\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}\n" +
                        "Create exactly %d days with comprehensive content for a %s learner.",
                learningProfile.getNumberOfDays(),
                learningProfile.getTopic(),
                learningProfile.getExpertiseLevel(),
                learningProfile.getEndGoal(),
                learningProfile.getTopic(),
                learningProfile.getNumberOfDays(),
                learningProfile.getNumberOfDays(),
                learningProfile.getExpertiseLevel()
        );
    }

    private void generateFinalQuiz() {
        List<PracticeQuestion> allQuestions = new ArrayList<>();
        for (DailyLesson lesson : dayAdapter.getDays()) {
            allQuestions.addAll(lesson.getPracticeQuestions());
        }

        if (allQuestions.size() > 10) {
            allQuestions = allQuestions.subList(0, 10);
        }

        UltimateQuiz finalQuiz = new UltimateQuiz(allQuestions);

        Intent intent = new Intent(this, FinalQuizActivity.class);
        intent.putExtra("quiz", (Serializable) finalQuiz);
        startActivity(intent);
    }

    private List<DailyLesson> parseLessons(String jsonContent) throws JSONException {
        List<DailyLesson> lessons = new ArrayList<>();
        JSONObject json = new JSONObject(jsonContent);
        JSONArray daysArray = json.getJSONArray("days");

        for (int i = 0; i < daysArray.length(); i++) {
            JSONObject dayObj = daysArray.getJSONObject(i);

            List<Resource> resources = new ArrayList<>();
            JSONArray resourcesArray = dayObj.getJSONArray("resources");
            for (int j = 0; j < resourcesArray.length(); j++) {
                JSONObject resourceObj = resourcesArray.getJSONObject(j);
                Resource resource = new Resource(
                        resourceObj.getString("title"),
                        resourceObj.getString("description"),
                        resourceObj.getString("url")
                );
                resources.add(resource);
            }

            List<PracticeQuestion> practiceQuestions = new ArrayList<>();
            JSONArray questionsArray = dayObj.getJSONArray("practiceQuestions");
            for (int k = 0; k < questionsArray.length(); k++) {
                JSONObject questionObj = questionsArray.getJSONObject(k);
                PracticeQuestion question = new PracticeQuestion(
                        questionObj.getString("question"),
                        questionObj.getString("optionA"),
                        questionObj.getString("optionB"),
                        questionObj.getString("optionC"),
                        questionObj.getString("optionD"),
                        questionObj.getString("correctAnswer"),
                        questionObj.getString("explanation")
                );
                practiceQuestions.add(question);
            }

            DailyLesson lesson = new DailyLesson(
                    dayObj.getInt("day"),
                    dayObj.getString("focus"),
                    dayObj.getString("description"),
                    resources,
                    practiceQuestions
            );
            lessons.add(lesson);
        }
        return lessons;
    }

    private void displayLessons(List<DailyLesson> lessons) {
        isLoading = false;
        loadingIndicator.setVisibility(View.GONE);
        daysRecyclerView.setVisibility(View.VISIBLE);
        dayAdapter.updateDays(lessons);
    }

    private void showError(String error) {
        isLoading = false;
        loadingIndicator.setVisibility(View.GONE);
        errorText.setVisibility(View.VISIBLE);
        errorText.setText(error);
        Log.e(TAG, "Showing error: " + error);
    }

    private void onDayClicked(DailyLesson lesson) {
        Intent intent = new Intent(this, DayDetailActivity.class);
        intent.putExtra("lesson", lesson);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (aiService != null) {
            aiService.shutdown();
        }
    }
}