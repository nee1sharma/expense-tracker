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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hitstudio.expensetracker.ExpenseTrackerApplication;
import com.hitstudio.expensetracker.R;
import com.hitstudio.expensetracker.ui.common.AppViewModelFactory;
import com.hitstudio.expensetracker.ui.list.ExpenseListFragment;
import com.hitstudio.expensetracker.util.MoneyFormatter;

public class PeriodFragment extends Fragment {
    private PeriodViewModel viewModel;
    private PeriodMonthAdapter monthAdapter;
    private View contentView;
    private TextView emptyView;
    private TextView selectedLabel;
    private TextView selectedTotal;
    private RecyclerView monthRecycler;

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
        selectedLabel = view.findViewById(R.id.period_selected_label);
        selectedTotal = view.findViewById(R.id.period_selected_total);
        monthRecycler = view.findViewById(R.id.period_month_recycler);

        monthAdapter = new PeriodMonthAdapter(new PeriodMonthAdapter.Listener() {
            @Override
            public void onMonthSelected(PeriodMonthItem item) {
                viewModel.selectMonth(item.startMillis);
                openMonthHistory(view, item);
            }
        });
        monthRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        monthRecycler.setAdapter(monthAdapter);

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

        PeriodMonthItem selectedMonth = state.monthItems.get(state.selectedIndex);
        monthAdapter.submitList(state.monthItems, selectedMonth.startMillis);
        selectedLabel.setText(state.selectedMonthLabel);
        selectedTotal.setText(MoneyFormatter.format(state.selectedMonthTotalMinor, state.currencyCode));

        monthRecycler.post(() -> monthRecycler.scrollToPosition(state.selectedIndex));
    }

    private void openMonthHistory(View anchor, PeriodMonthItem item) {
        Bundle bundle = new Bundle();
        bundle.putLong(ExpenseListFragment.ARG_RANGE_START_MILLIS, item.startMillis);
        bundle.putLong(ExpenseListFragment.ARG_RANGE_END_MILLIS, item.endMillis);
        bundle.putString(ExpenseListFragment.ARG_RANGE_LABEL, item.label);
        bundle.putString(ExpenseListFragment.ARG_EMPTY_MESSAGE, getString(R.string.period_month_empty));
        Navigation.findNavController(anchor).navigate(R.id.expenseListFragment, bundle);
    }
}
