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
import com.hitstudio.expensetracker.util.AppLogger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

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
        version.setText("Version " + BuildConfig.VERSION_NAME + " (Build " + BuildConfig.VERSION_CODE + ")");
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
            AppLogger.e("AboutFragment", "No email app found to handle mailto intent", ex);
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Email app not found")
                    .setMessage("Please contact " + SUPPORT_EMAIL)
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    private String bugReportBody() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- BUG REPORT ---\n\n");
        sb.append("Describe what happened:\n[Enter details here]\n\n");
        sb.append("--- SYSTEM INFO ---\n");
        sb.append("App Version: ").append(BuildConfig.VERSION_NAME).append(" (").append(BuildConfig.VERSION_CODE).append(")\n");
        sb.append("Device: ").append(Build.MANUFACTURER).append(" ").append(Build.MODEL).append("\n");
        sb.append("Android: ").append(Build.VERSION.RELEASE).append(" (SDK ").append(Build.VERSION.SDK_INT).append(")\n");
        sb.append("ABIs: ").append(Arrays.toString(Build.SUPPORTED_ABIS)).append("\n");
        sb.append("\n--- RECENT ERROR LOGS ---\n");
        sb.append(getRecentErrorLogs());
        return sb.toString();
    }

    private String getRecentErrorLogs() {
        StringBuilder sb = new StringBuilder();
        
        // 1. Get logs captured by our AppLogger (includes crashes)
        sb.append("--- CAPTURED APP LOGS ---\n");
        sb.append(AppLogger.getCapturedLogs());
        sb.append("\n");

        // 2. Fallback/Supplement with Logcat
        sb.append("--- SYSTEM LOGCAT (Last 50 Error lines) ---\n");
        try {
            Process process = Runtime.getRuntime().exec("logcat -d -t 50 *:E");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            int count = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
                count++;
            }
            if (count == 0) {
                sb.append("No recent system error logs found.\n");
            }
        } catch (Exception e) {
            sb.append("Failed to retrieve system logs: ").append(e.getMessage()).append("\n");
        }
        
        return sb.toString();
    }
}
