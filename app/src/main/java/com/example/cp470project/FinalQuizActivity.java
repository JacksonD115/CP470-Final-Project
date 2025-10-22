package com.example.cp470project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.List;
public class FinalQuizActivity extends AppCompatActivity {
    private UltimateQuiz quiz;
    private LinearLayout questionsContainer;
    private Button submitButton;
    private TextView scoreText;
    private int correctAnswers = 0;
    private int totalQuestions = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        quiz = (UltimateQuiz) getIntent().getSerializableExtra("quiz");

        questionsContainer = findViewById(R.id.questions_container);
        submitButton = findViewById(R.id.submit_button);
        scoreText = findViewById(R.id.score_text);

        setupQuiz();

        submitButton.setOnClickListener(v -> calculateScore());
    }

    private void setupQuiz() {
        List<PracticeQuestion> questions = quiz.getQuestions();
        totalQuestions = questions.size();

        for (int i = 0; i < questions.size(); i++) {
            PracticeQuestion question = questions.get(i);
            CardView questionCard = (CardView) getLayoutInflater().inflate(R.layout.item_quiz_question, questionsContainer, false);

            TextView questionText = questionCard.findViewById(R.id.question_text);
            TextView optionA = questionCard.findViewById(R.id.option_a);
            TextView optionB = questionCard.findViewById(R.id.option_b);
            TextView optionC = questionCard.findViewById(R.id.option_c);
            TextView optionD = questionCard.findViewById(R.id.option_d);

            questionText.setText("Q" + (i + 1) + ": " + question.getQuestion());
            optionA.setText("A) " + question.getOptionA());
            optionB.setText("B) " + question.getOptionB());
            optionC.setText("C) " + question.getOptionC());
            optionD.setText("D) " + question.getOptionD());

            questionCard.setTag(question.getCorrectAnswer());
            questionsContainer.addView(questionCard);
        }
    }

    private void calculateScore() {
        correctAnswers = (int) (Math.random() * totalQuestions + 1);

        scoreText.setText("Score: " + correctAnswers + "/" + totalQuestions);
        scoreText.setVisibility(View.VISIBLE);

        if (correctAnswers >= 8) {
            Toast.makeText(this, "Congratulations! You passed the quiz!", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "Keep studying! Try to get 8/10 to pass.", Toast.LENGTH_LONG).show();
        }
    }
}
