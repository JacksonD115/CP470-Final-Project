package com.example.cp470project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
public class DayDetailActivity extends AppCompatActivity {
    private DailyLesson lesson;
    private LinearLayout resourcesContainer;
    private LinearLayout questionsContainer;
    private TextView dayTitle;
    private TextView dayDescription;
    private ProgressBar progressBar;
    private Button markCompleteButton;
    private Button shareDayButton;
    private Button nextDayButton;
    private Button previousDayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_detail);

        lesson = (DailyLesson) getIntent().getSerializableExtra("lesson");

        dayTitle = findViewById(R.id.day_title);
        dayDescription = findViewById(R.id.day_description);
        resourcesContainer = findViewById(R.id.resources_container);
        questionsContainer = findViewById(R.id.questions_container);

        progressBar = findViewById(R.id.progress_bar);
        markCompleteButton = findViewById(R.id.btn_mark_complete);
        shareDayButton = findViewById(R.id.btn_share_day);
        nextDayButton = findViewById(R.id.btn_next_day);
        previousDayButton = findViewById(R.id.btn_previous_day);

        markCompleteButton.setOnClickListener(v -> {
            showSnackbar("Day marked as complete!");
            Toast.makeText(this, "Great job!", Toast.LENGTH_SHORT).show();
        });

        shareDayButton.setOnClickListener(v -> {
            showSnackbar("Sharing day content...");
        });

        nextDayButton.setOnClickListener(v -> {
            Toast.makeText(this, "Next day", Toast.LENGTH_SHORT).show();
        });

        previousDayButton.setOnClickListener(v -> {
            Toast.makeText(this, "Previous day", Toast.LENGTH_SHORT).show();
        });

        setupDayContent();
    }

    private void setupDayContent() {
        dayTitle.setText("Day " + lesson.getDay() + ": " + lesson.getFocus());
        dayDescription.setText(lesson.getDescription());

        addResources(lesson.getResources());

        addPracticeQuestions(lesson.getPracticeQuestions());
    }

    private void addResources(List<Resource> resources) {
        for (Resource resource : resources) {
            CardView resourceCard = (CardView) getLayoutInflater().inflate(R.layout.item_resource, resourcesContainer, false);

            TextView title = resourceCard.findViewById(R.id.resource_title);
            TextView description = resourceCard.findViewById(R.id.resource_description);
            TextView url = resourceCard.findViewById(R.id.resource_url);
            Button openButton = resourceCard.findViewById(R.id.open_button);

            title.setText(resource.getTitle());
            description.setText(resource.getDescription());
            url.setText(resource.getUrl());

            openButton.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(resource.getUrl()));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Could not open link", Toast.LENGTH_SHORT).show();
                }
            });

            resourcesContainer.addView(resourceCard);
        }
    }

    private void addPracticeQuestions(List<PracticeQuestion> practiceQuestions) {
        for (int i = 0; i < practiceQuestions.size(); i++) {
            PracticeQuestion question = practiceQuestions.get(i);
            CardView questionCard = (CardView) getLayoutInflater().inflate(R.layout.item_practice_question, questionsContainer, false);

            TextView questionText = questionCard.findViewById(R.id.question_text);
            TextView optionA = questionCard.findViewById(R.id.option_a);
            TextView optionB = questionCard.findViewById(R.id.option_b);
            TextView optionC = questionCard.findViewById(R.id.option_c);
            TextView optionD = questionCard.findViewById(R.id.option_d);
            TextView explanation = questionCard.findViewById(R.id.explanation);
            Button showAnswer = questionCard.findViewById(R.id.show_answer);

            questionText.setText("Q" + (i + 1) + ": " + question.getQuestion());
            optionA.setText("A) " + question.getOptionA());
            optionB.setText("B) " + question.getOptionB());
            optionC.setText("C) " + question.getOptionC());
            optionD.setText("D) " + question.getOptionD());

            explanation.setText("Answer: " + question.getCorrectAnswer() + "\nExplanation: " + question.getExplanation());
            explanation.setVisibility(View.GONE);

            showAnswer.setOnClickListener(v -> {
                if (explanation.getVisibility() == View.GONE) {
                    explanation.setVisibility(View.VISIBLE);
                    showAnswer.setText("Hide Answer");
                } else {
                    explanation.setVisibility(View.GONE);
                    showAnswer.setText("Show Answer");
                }
            });

            questionsContainer.addView(questionCard);
        }
    }

    private void showSnackbar(String message) {
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
        }
    }
}
