package com.hitstudio.expensetracker.ui.period;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hitstudio.expensetracker.ExpenseTrackerApplication;
import com.hitstudio.expensetracker.R;
import com.hitstudio.expensetracker.ui.common.AppViewModelFactory;
import com.hitstudio.expensetracker.ui.list.ExpenseHistoryAdapter;
import com.hitstudio.expensetracker.util.MoneyFormatter;

public class PeriodFragment extends Fragment {
    private PeriodViewModel viewModel;
    private PeriodMonthAdapter monthAdapter;
    private PeriodCategoryAdapter categoryAdapter;
    private ExpenseHistoryAdapter expenseAdapter;
    private View contentView;
    private TextView emptyView;
    private View detailView;
    private TextView promptView;
    private TextView selectedLabel;
    private TextView selectedTotal;
    private TextView emptyCategories;
    private TextView emptyHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_period, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ExpenseTrackerApplication application = (ExpenseTrackerApplication) requireActivity().getApplication();
        viewModel = new ViewModelProvider(this, new AppViewModelFactory(application.getContainer()))
                .get(PeriodViewModel.class);

        contentView = view.findViewById(R.id.period_content);
        emptyView = view.findViewById(R.id.period_empty);
        detailView = view.findViewById(R.id.period_detail_content);
        promptView = view.findViewById(R.id.period_select_prompt);
        selectedLabel = view.findViewById(R.id.period_selected_label);
        selectedTotal = view.findViewById(R.id.period_selected_total);
        emptyCategories = view.findViewById(R.id.period_empty_categories);
        emptyHistory = view.findViewById(R.id.period_empty_history);

        RecyclerView monthRecycler = view.findViewById(R.id.period_month_recycler);
        RecyclerView categoryRecycler = view.findViewById(R.id.period_category_recycler);
        RecyclerView historyRecycler = view.findViewById(R.id.period_history_recycler);

        monthAdapter = new PeriodMonthAdapter(item -> viewModel.selectMonth(item.startMillis));
        categoryAdapter = new PeriodCategoryAdapter();
        expenseAdapter = new ExpenseHistoryAdapter(expense -> { });

        monthRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        monthRecycler.setAdapter(monthAdapter);
        monthRecycler.setNestedScrollingEnabled(false);

        categoryRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        categoryRecycler.setAdapter(categoryAdapter);
        categoryRecycler.setNestedScrollingEnabled(false);

        historyRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        historyRecycler.setAdapter(expenseAdapter);
        historyRecycler.setNestedScrollingEnabled(false);

        viewModel.getState().observe(getViewLifecycleOwner(), this::bindState);
    }

    private void bindState(PeriodScreenState state) {
        if (state == null || !state.hasTrackingData) {
            contentView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            return;
        }

        contentView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);

        long selectedStartMillis = state.selectedIndex >= 0 ? state.monthItems.get(state.selectedIndex).startMillis : Long.MIN_VALUE;
        monthAdapter.submitList(state.monthItems, selectedStartMillis);

        boolean hasSelection = state.selectedIndex >= 0;
        promptView.setVisibility(hasSelection ? View.GONE : View.VISIBLE);
        detailView.setVisibility(hasSelection ? View.VISIBLE : View.GONE);
        if (!hasSelection) {
            categoryAdapter.submitList(null);
            expenseAdapter.submitList(null);
            return;
        }

        selectedLabel.setText(state.selectedMonthLabel);
        selectedTotal.setText(MoneyFormatter.format(state.selectedMonthTotalMinor, state.currencyCode));
        categoryAdapter.submitList(state.selectedMonthCategories);
        expenseAdapter.submitList(state.selectedMonthExpenses);

        emptyCategories.setVisibility(state.selectedMonthCategories.isEmpty() ? View.VISIBLE : View.GONE);
        emptyHistory.setVisibility(state.selectedMonthExpenses.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
