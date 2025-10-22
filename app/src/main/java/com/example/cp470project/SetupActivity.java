package com.example.cp470project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
public class SetupActivity extends AppCompatActivity {
    private Spinner expertiseSpinner;
    private EditText daysInput;
    private EditText goalInput;
    private EditText topicInput;
    private Button generateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        expertiseSpinner = findViewById(R.id.expertiseSpinner);
        daysInput = findViewById(R.id.daysInput);
        goalInput = findViewById(R.id.goal_input);
        topicInput = findViewById(R.id.topic_input);
        generateButton = findViewById(R.id.generate_button);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.expertise_levels,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expertiseSpinner.setAdapter(adapter);

        generateButton.setOnClickListener(v -> {
            if (validateInput()) {
                LearningProfile profile = collectUserInput();
                navigateToResults(profile);
            }
        });
    }

    private boolean validateInput() {
        String daysStr = daysInput.getText().toString().trim();
        String goal = goalInput.getText().toString().trim();
        String topic = topicInput.getText().toString().trim();

        if (daysStr.isEmpty()) {
            Toast.makeText(this, "Please enter number of days", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (topic.isEmpty()) {
            Toast.makeText(this, "Please enter a topic to learn", Toast.LENGTH_SHORT).show();
            return false;
        }

        int days = Integer.parseInt(daysStr);
        if (days < 2 || days > 7) {
            Toast.makeText(this, "Please enter a number of days from 2-7", Toast.LENGTH_SHORT).show();
        }

        if (goal.isEmpty()) {
            Toast.makeText(this, "Please enter a goal", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    private LearningProfile collectUserInput() {
        String expertise = expertiseSpinner.getSelectedItem().toString().toLowerCase();
        int days = Integer.parseInt(daysInput.getText().toString().trim());
        String goal = goalInput.getText().toString().trim();
        String topic = topicInput.getText().toString().trim();

        return new LearningProfile(expertise, days, goal, topic);
    }

    private void navigateToResults(LearningProfile profile) {
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("expertise_level", profile.getExpertiseLevel());
        intent.putExtra("number_of_days", profile.getNumberOfDays());
        intent.putExtra("topic", profile.getTopic());
        intent.putExtra("end_goal", profile.getEndGoal());
        startActivity(intent);
    }

}
