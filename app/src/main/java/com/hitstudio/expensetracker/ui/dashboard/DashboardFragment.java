package com.hitstudio.expensetracker.ui.dashboard;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hitstudio.expensetracker.ExpenseTrackerApplication;
import com.hitstudio.expensetracker.R;
import com.hitstudio.expensetracker.domain.model.CategoryBreakdown;
import com.hitstudio.expensetracker.domain.model.DashboardSummary;
import com.hitstudio.expensetracker.domain.model.Expense;
import com.hitstudio.expensetracker.ui.common.AppViewModelFactory;
import com.hitstudio.expensetracker.ui.list.ExpenseAdapter;
import com.hitstudio.expensetracker.util.MoneyFormatter;

public class DashboardFragment extends Fragment {
    private DashboardViewModel viewModel;
    private ExpenseAdapter adapter;
    private TextView todayTotal;
    private TextView weekTotal;
    private TextView monthTotal;
    private LinearLayout topCategories;
    private TextView emptyTopCategories;
    private TextView emptyRecent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ExpenseTrackerApplication application = (ExpenseTrackerApplication) requireActivity().getApplication();
        viewModel = new ViewModelProvider(this, new AppViewModelFactory(application.getContainer()))
                .get(DashboardViewModel.class);

        todayTotal = view.findViewById(R.id.dashboard_today_total);
        weekTotal = view.findViewById(R.id.dashboard_week_total);
        monthTotal = view.findViewById(R.id.dashboard_month_total);
        topCategories = view.findViewById(R.id.dashboard_top_categories);
        emptyTopCategories = view.findViewById(R.id.dashboard_empty_categories);
        emptyRecent = view.findViewById(R.id.dashboard_empty_recent);

        adapter = new ExpenseAdapter(new ExpenseAdapter.Listener() {
            @Override
            public void onEdit(Expense expense) {
                Bundle bundle = new Bundle();
                bundle.putLong("expenseId", expense.id);
                Navigation.findNavController(view).navigate(R.id.loggerFragment, bundle);
            }

            @Override
            public void onDelete(Expense expense) {
                confirmDelete(expense);
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.dashboard_recent_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.dashboard_add_expense).setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.loggerFragment));
        view.findViewById(R.id.dashboard_view_all).setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.periodFragment));

        viewModel.summary.observe(getViewLifecycleOwner(), this::bindSummary);
        viewModel.recentExpenses.observe(getViewLifecycleOwner(), expenses -> {
            adapter.submitList(expenses);
            emptyRecent.setVisibility(expenses == null || expenses.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void bindSummary(DashboardSummary summary) {
        todayTotal.setText(MoneyFormatter.format(summary.todaySpend.amountMinor, summary.todaySpend.currencyCode));
        weekTotal.setText(MoneyFormatter.format(summary.weekSpend.amountMinor, summary.weekSpend.currencyCode));
        monthTotal.setText(MoneyFormatter.format(summary.monthSpend.amountMinor, summary.monthSpend.currencyCode));
        topCategories.removeAllViews();
        emptyTopCategories.setVisibility(summary.topCategories.isEmpty() ? View.VISIBLE : View.GONE);
        for (CategoryBreakdown breakdown : summary.topCategories) {
            View row = LayoutInflater.from(requireContext()).inflate(R.layout.item_category_breakdown, topCategories, false);
            View dot = row.findViewById(R.id.breakdown_color);
            TextView name = row.findViewById(R.id.breakdown_name);
            TextView amount = row.findViewById(R.id.breakdown_amount);
            dot.setBackground(dot(breakdown.colorHex));
            name.setText(breakdown.categoryName);
            amount.setText(MoneyFormatter.format(breakdown.total.amountMinor, breakdown.total.currencyCode));
            topCategories.addView(row);
        }
    }

    private GradientDrawable dot(String colorHex) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        try {
            drawable.setColor(Color.parseColor(colorHex));
        } catch (IllegalArgumentException ignored) {
            drawable.setColor(Color.parseColor("#607D8B"));
        }
        return drawable;
    }

    private void confirmDelete(Expense expense) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete transaction?")
                .setMessage("This removes the item from your history and updates totals.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> viewModel.delete(expense.id))
                .show();
    }
}
