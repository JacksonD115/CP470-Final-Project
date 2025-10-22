package com.example.cp470project;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {
    private List<DailyLesson> days;
    private OnDayClickListener clickListener;

    public interface OnDayClickListener {
        void onDayClick(DailyLesson lesson);
    }

    public DayAdapter(List<DailyLesson> days, OnDayClickListener listener) {
        this.days = days;
        this.clickListener = listener;
    }

    public void updateDays(List<DailyLesson> newDays) {
        this.days = newDays;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        DailyLesson lesson = days.get(position);
        holder.bind(lesson, clickListener);
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public List<DailyLesson> getDays() {
        return days;
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        private TextView dayNumber;
        private TextView dayFocus;
        private TextView dayDescription;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            dayNumber = itemView.findViewById(R.id.day_number);
            dayFocus = itemView.findViewById(R.id.day_focus);
            dayDescription = itemView.findViewById(R.id.day_description);
        }

        public void bind(DailyLesson lesson, OnDayClickListener listener){
            dayNumber.setText("Day " + lesson.getDay());
            dayFocus.setText("Focus Area: " + lesson.getFocus());
            dayDescription.setText(lesson.getDescription());

            itemView.setOnClickListener(v -> listener.onDayClick(lesson));
        }
     }
}

