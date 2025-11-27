package com.example.cp470project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cp470project.data.models.PlanWithLessons;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    private final OnPlanClickListener clickListener;
    private final DateFormat dateFormat = DateFormat.getDateTimeInstance();
    private List<PlanWithLessons> plans = new ArrayList<>();

    public interface OnPlanClickListener {
        void onPlanClick(PlanWithLessons plan);
    }

    public PlanAdapter(OnPlanClickListener listener) {
        this.clickListener = listener;
    }

    public void submitList(List<PlanWithLessons> newPlans) {
        plans = newPlans != null ? newPlans : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        PlanWithLessons plan = plans.get(position);
        holder.bind(plan, clickListener, dateFormat);
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    static class PlanViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView topicText;
        private final TextView daysText;
        private final TextView createdText;

        PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.plan_title);
            topicText = itemView.findViewById(R.id.plan_topic);
            daysText = itemView.findViewById(R.id.plan_days);
            createdText = itemView.findViewById(R.id.plan_created);
        }

        void bind(PlanWithLessons plan,
                  OnPlanClickListener listener,
                  DateFormat dateFormat) {

            titleText.setText(plan.plan.endGoal != null && !plan.plan.endGoal.isEmpty()
                    ? plan.plan.endGoal
                    : "Learning Plan");

            topicText.setText("Topic: " + plan.plan.topic);
            daysText.setText(plan.plan.numberOfDays + " day plan");
            createdText.setText(dateFormat.format(new Date(plan.plan.createdAt)));

            itemView.setOnClickListener(v -> listener.onPlanClick(plan));
        }
    }
}