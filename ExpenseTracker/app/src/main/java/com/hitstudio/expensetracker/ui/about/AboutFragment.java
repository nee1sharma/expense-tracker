package com.hitstudio.expensetracker.ui.about;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hitstudio.expensetracker.BuildConfig;
import com.hitstudio.expensetracker.R;

public class AboutFragment extends Fragment {
    private static final String SUPPORT_EMAIL = "support@hitstudio.dev";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView version = view.findViewById(R.id.about_version);
        version.setText("Version " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")");
        view.findViewById(R.id.about_support_button).setOnClickListener(v ->
                composeEmail("Expense Tracker support", ""));
        view.findViewById(R.id.about_bug_button).setOnClickListener(v ->
                composeEmail("Expense Tracker bug report", bugReportBody()));
    }

    private void composeEmail(String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + SUPPORT_EMAIL));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Email app not found")
                    .setMessage("Please contact " + SUPPORT_EMAIL)
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    private String bugReportBody() {
        return "Describe what happened:\n\n\n" +
                "App version: " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")\n" +
                "Device: " + Build.MANUFACTURER + " " + Build.MODEL + "\n" +
                "Android: " + Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ")\n";
    }
}
