package com.example.cp470project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.cp470project.data.LearningRepository;
import com.example.cp470project.data.models.PlanWithLessons;

public class DeletePlanDialogFragment extends DialogFragment {

    private PlanWithLessons plan;
    private int position;
    private Runnable onDeleteCallback;
    private LearningRepository repository;

    public DeletePlanDialogFragment(PlanWithLessons plan, int position, Runnable onDeleteCallback) {
        this.plan = plan;
        this.position = position;
        this.onDeleteCallback = onDeleteCallback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        repository = new LearningRepository(requireContext());

        String title = "Delete Learning Plan?";
        String message = "Are you sure you want to delete this learning plan? This action cannot be undone.";

        if (plan != null && plan.plan != null) {
            message = "Delete plan: \"" + plan.plan.topic + "\"?\n\n" +
                    "This will permanently delete all lessons, resources, and practice questions. " +
                    "This action cannot be undone.";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Delete", (dialog, which) -> {
                    // User confirmed deletion
                    deletePlan();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // User cancelled, just dismiss
                    dismiss();
                });

        return builder.create();
    }

    private void deletePlan() {
        if (plan == null || plan.plan == null) {
            Toast.makeText(requireContext(), "Error: Plan not found", Toast.LENGTH_SHORT).show();
            return;
        }

        long planId = plan.plan.id;

        repository.deletePlan(planId, () -> {
            // Run on UI thread
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Plan deleted successfully", Toast.LENGTH_SHORT).show();

                    // Call the callback to notify parent activity
                    if (onDeleteCallback != null) {
                        onDeleteCallback.run();
                    }

                    dismiss();
                });
            }
        });
    }
}