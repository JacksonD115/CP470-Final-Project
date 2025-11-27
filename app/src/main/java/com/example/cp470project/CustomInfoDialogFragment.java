package com.example.cp470project;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

public class CustomInfoDialogFragment extends DialogFragment {
    private String title;
    private String message;

    public CustomInfoDialogFragment(String title, String message) {
        this.title = title;
        this.message = message;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_custom_info, container, false);

        TextView titleView = view.findViewById(R.id.dialog_title);
        TextView messageView = view.findViewById(R.id.dialog_message);
        Button okButton = view.findViewById(R.id.dialog_ok_button);

        titleView.setText(title);
        messageView.setText(message);

        okButton.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        }
        return dialog;
    }
}
