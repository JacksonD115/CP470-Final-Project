package com.example.cp470project;

import static android.app.ProgressDialog.show;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cp470project.data.LearningRepository;
import com.example.cp470project.data.models.LessonWithDetails;
import com.example.cp470project.data.models.PlanWithLessons;
import com.example.cp470project.data.LearningPlanEntity;
import com.example.cp470project.LearningProfile;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlanDetailActivity extends AppCompatActivity {
    public static final String EXTRA_PLAN_ID = "plan_id";
    private static final String TAG = "PlanDetailActivity";

    private LearningRepository repository;
    private PlanWithLessons currentPlan;
    private long currentPlanId;

    private TextView titleText;
    private TextView topicText;
    private TextView goalText;
    private TextView daysText;
    private TextView dateText;
    private ProgressBar loadingIndicator;
    private LinearLayout lessonsContainer;
    private TextView emptyState;
    private Button shareButton;
    private Button exportButton;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_detail);

        currentPlanId = getIntent().getLongExtra(EXTRA_PLAN_ID, -1);
        if (currentPlanId == -1) {
            Toast.makeText(this, "Invalid plan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        repository = new LearningRepository(this);
        loadPlan(currentPlanId);

        shareButton.setOnClickListener(v -> {

        });

        exportButton.setOnClickListener(v -> {

        });

        deleteButton.setOnClickListener(v -> {

            if (currentPlan != null) {
                new DeletePlanDialogFragment(currentPlan, 0, () -> {
                    finish();
                }).show(getSupportFragmentManager(), "DeleteDialog");
            } else {
                PlanWithLessons tempPlan = new PlanWithLessons();
                tempPlan.plan = new com.example.cp470project.data.LearningPlanEntity("", 0, "", "", 0);
                tempPlan.plan.id = currentPlanId;
                new DeletePlanDialogFragment(tempPlan, 0, () -> {
                    finish();
                }).show(getSupportFragmentManager(), "DeleteDialog");
            }
        });
    }

    private void initViews() {
        titleText = findViewById(R.id.plan_title);
        topicText = findViewById(R.id.plan_topic);
        goalText = findViewById(R.id.plan_goal);
        daysText = findViewById(R.id.plan_days);
        dateText = findViewById(R.id.plan_date);
        loadingIndicator = findViewById(R.id.loading_indicator);
        lessonsContainer = findViewById(R.id.lessons_container);
        emptyState = findViewById(R.id.empty_text);
        shareButton = findViewById(R.id.btn_share);
        exportButton = findViewById(R.id.btn_export);
        deleteButton = findViewById(R.id.btn_delete);
    }

    private void loadPlan(long planId) {
        showLoading(true);
        repository.getPlanById(planId, plan -> runOnUiThread(() -> {
            showLoading(false);
            if (plan == null) {
                emptyState.setVisibility(View.VISIBLE);
                emptyState.setText("Plan not found");
                return;
            }
            bindPlan(plan);
        }));
    }

    private class LoadPlanTask extends AsyncTask<Long, Void, PlanWithLessons> {
        private long planId;

        public LoadPlanTask(long planId) {
            this.planId = planId;
        }
        @Override
        protected void onPreExecute() {
            showLoading(true);
        }
        @Override
        protected PlanWithLessons doInBackground(Long... longs) {
            final PlanWithLessons[] result = new PlanWithLessons[1];
            repository.getPlanById(planId, plan -> result[0] = currentPlan);

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return  result[0];
        }

        @Override
        protected void onPostExecute(PlanWithLessons plan) {
            showLoading(false);
            if (plan == null) {
                emptyState.setVisibility(View.VISIBLE);
                emptyState.setText("Plan not found");
                showSnackbar("Plan not found");
            } else {
                bindPlan(plan);
            }
        }
    }

    private void showLoading(boolean loading) {
        loadingIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);
        lessonsContainer.setVisibility(loading ? View.GONE : View.VISIBLE);
        emptyState.setVisibility(View.GONE);
    }

    private void bindPlan(PlanWithLessons plan) {
        currentPlan = plan;

        titleText.setText(plan.plan.endGoal != null && !plan.plan.endGoal.isEmpty()
                ? plan.plan.endGoal
                : "Learning Plan");
        topicText.setText("Topic: " + plan.plan.topic);
        goalText.setText("Goal: " + plan.plan.endGoal);
        daysText.setText(plan.plan.numberOfDays + " day plan");
        dateText.setText(android.text.format.DateFormat.format(
                "MMM dd, yyyy h:mm a", plan.plan.createdAt));

        lessonsContainer.removeAllViews();
        for (LessonWithDetails lesson : plan.lessons) {
            View lessonView = getLayoutInflater().inflate(R.layout.item_saved_lesson, lessonsContainer, false);
            bindLesson(lessonView, lesson);
            lessonsContainer.addView(lessonView);
        }
    }

    private void bindLesson(View view, LessonWithDetails lessonDetails) {
        TextView dayTitle = view.findViewById(R.id.lesson_title);
        TextView lessonDescription = view.findViewById(R.id.lesson_description);
        LinearLayout resourcesContainer = view.findViewById(R.id.resources_container);
        LinearLayout questionsContainer = view.findViewById(R.id.questions_container);

        dayTitle.setText("Day " + lessonDetails.lesson.day + ": " + lessonDetails.lesson.focus);
        lessonDescription.setText(lessonDetails.lesson.description);

        view.setClickable(true);
        view.setFocusable(true);

        resourcesContainer.setClickable(false);
        resourcesContainer.setFocusable(false);
        questionsContainer.setClickable(false);
        questionsContainer.setFocusable(false);

        view.setOnClickListener(v -> {
            DailyLesson dailyLesson = convertToDailyLesson(lessonDetails);
            Intent intent = new Intent(this, com.example.cp470project.DayDetailActivity.class);
            intent.putExtra("lesson", (Serializable) dailyLesson);
            startActivity(intent);
        });

        resourcesContainer.removeAllViews();
        for (int i = 0; i < lessonDetails.resources.size(); i++) {
            View resourceView = getLayoutInflater().inflate(R.layout.item_saved_resource, resourcesContainer, false);
            TextView title = resourceView.findViewById(R.id.resource_title);
            TextView url = resourceView.findViewById(R.id.resource_url);

            title.setText(lessonDetails.resources.get(i).title);
            url.setText(lessonDetails.resources.get(i).url);

            resourcesContainer.addView(resourceView);
        }

        questionsContainer.removeAllViews();
        for (int i = 0; i < lessonDetails.questions.size(); i++) {
            View questionView = getLayoutInflater().inflate(R.layout.item_saved_question, questionsContainer, false);
            TextView questionText = questionView.findViewById(R.id.question_text);
            TextView answerText = questionView.findViewById(R.id.answer_text);

            questionText.setText(lessonDetails.questions.get(i).question);
            answerText.setText("Answer: " + lessonDetails.questions.get(i).correctAnswer);

            questionsContainer.addView(questionView);
        }
    }

    private DailyLesson convertToDailyLesson(LessonWithDetails lessonDetails) {
        if (lessonDetails == null || lessonDetails.lesson == null) {
            Log.e(TAG, "lessonDetails or lesson is null");
            return null;
        }

        List<Resource> resources = new ArrayList<>();
        if (lessonDetails.resources != null) {
            for (com.example.cp470project.data.ResourceEntity entity : lessonDetails.resources) {
                if (entity != null) {
                    resources.add(new Resource(
                            entity.title,
                            entity.description,
                            entity.url
                    ));
                }
            }
        }

        List<PracticeQuestion> questions = new ArrayList<>();
        if (lessonDetails.questions != null) {
            for (com.example.cp470project.data.PracticeQuestionEntity entity : lessonDetails.questions) {
                if (entity != null) {
                    questions.add(new PracticeQuestion(
                            entity.question,
                            entity.optionA,
                            entity.optionB,
                            entity.optionC,
                            entity.optionD,
                            entity.correctAnswer,
                            entity.explanation
                    ));
                }
            }
        }

        return new DailyLesson(
                lessonDetails.lesson.day,
                lessonDetails.lesson.focus,
                lessonDetails.lesson.description,
                resources,
                questions
        );
    }


    private void showSnackbar(String message) {
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
        }
    }
}