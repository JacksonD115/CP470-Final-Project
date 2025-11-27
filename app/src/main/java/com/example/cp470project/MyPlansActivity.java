package com.example.cp470project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cp470project.data.LearningRepository;
import com.example.cp470project.data.models.PlanWithLessons;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyPlansActivity extends AppCompatActivity implements PlanAdapter.OnPlanClickListener {

    private LearningRepository repository;
    private PlanAdapter adapter;
    private ProgressBar loading;
    private TextView emptyState;
    private RecyclerView plansRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_plans);

        loading = findViewById(R.id.loading_indicator);
        emptyState = findViewById(R.id.emptyText);
        plansRecycler = findViewById(R.id.plans_recycler_view);

        adapter = new PlanAdapter(this);
        plansRecycler.setLayoutManager(new LinearLayoutManager(this));
        plansRecycler.setAdapter(adapter);

        repository = new LearningRepository(this);
        loadPlans();
    }

    private void loadPlans() {
        loading.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        plansRecycler.setVisibility(View.GONE);

        try {
            repository.getAllPlans(plans -> runOnUiThread(() -> {
                loading.setVisibility(View.GONE);
                if (plans.isEmpty() || plans == null) {
                    emptyState.setVisibility(View.VISIBLE);
                    emptyState.setText("No saved plans yet. Create your first plan!");
                } else {
                    plansRecycler.setVisibility(View.VISIBLE);
                    adapter.submitList(plans);
                }
            }));
        } catch (Exception e) {
            loading.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            emptyState.setText("Error loading plans: " + e.getMessage());
            android.util.Log.e("MyPlansActivity", "Error loading plans", e);
        }
    }
    @Override
    public void onPlanClick(PlanWithLessons plan) {
        Intent intent = new Intent(this, PlanDetailActivity.class);
        intent.putExtra("plan_id", plan.plan.id);
        startActivity(intent);
    }
}
