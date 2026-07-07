package com.hitstudio.expensetracker.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hitstudio.expensetracker.ExpenseTrackerApplication;
import com.hitstudio.expensetracker.R;
import com.hitstudio.expensetracker.app.AppContainer;
import com.hitstudio.expensetracker.domain.model.DateRange;
import com.hitstudio.expensetracker.domain.model.Expense;
import com.hitstudio.expensetracker.ui.common.AppViewModelFactory;

public class ExpenseListFragment extends Fragment {
    public static final String ARG_RANGE_START_MILLIS = "expenseRangeStartMillis";
    public static final String ARG_RANGE_END_MILLIS = "expenseRangeEndMillis";
    public static final String ARG_RANGE_LABEL = "expenseRangeLabel";
    public static final String ARG_EMPTY_MESSAGE = "expenseEmptyMessage";

    private ExpenseListViewModel viewModel;
    private ExpenseHistoryAdapter adapter;
    private TextView emptyView;
    private TextView titleView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expense_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ExpenseTrackerApplication application = (ExpenseTrackerApplication) requireActivity().getApplication();
        viewModel = new ViewModelProvider(this, new AppViewModelFactory(application.getContainer()))
                .get(ExpenseListViewModel.class);

        AppContainer container = application.getContainer();
        emptyView = view.findViewById(R.id.expense_list_empty);
        titleView = view.findViewById(R.id.expense_list_title);
        adapter = new ExpenseHistoryAdapter(new ExpenseHistoryAdapter.Listener() {
            @Override
            public void onActionsRequested(Expense expense) {
                showActionsDialog(view, expense);
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.expense_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.expense_list_add).setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.loggerFragment));

        bindHeading();

        LiveData<java.util.List<Expense>> source = expenseSource(container);
        source.observe(getViewLifecycleOwner(), expenses -> {
            adapter.submitList(expenses);
            emptyView.setVisibility(expenses == null || expenses.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void navigateToEdit(View view, Expense expense) {
        Bundle bundle = new Bundle();
        bundle.putLong("expenseId", expense.id);
        Navigation.findNavController(view).navigate(R.id.loggerFragment, bundle);
    }

    private void showActionsDialog(View view, Expense expense) {
        View actionsView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_expense_actions, null, false);
        Button editButton = actionsView.findViewById(R.id.expense_action_edit_button);
        Button deleteButton = actionsView.findViewById(R.id.expense_action_delete_button);

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Transaction options")
                .setView(actionsView)
                .create();

        editButton.setOnClickListener(v -> {
            dialog.dismiss();
            navigateToEdit(view, expense);
        });
        deleteButton.setOnClickListener(v -> {
            dialog.dismiss();
            confirmDelete(expense);
        });

        dialog.show();
    }

    private void confirmDelete(Expense expense) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete transaction?")
                .setMessage("This removes the item from your history and updates totals.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> viewModel.delete(expense.id))
                .show();
    }

    private LiveData<java.util.List<Expense>> expenseSource(AppContainer container) {
        Bundle args = getArguments();
        if (args == null || !args.containsKey(ARG_RANGE_START_MILLIS) || !args.containsKey(ARG_RANGE_END_MILLIS)) {
            return container.expenseReader.observeAll();
        }
        long start = args.getLong(ARG_RANGE_START_MILLIS);
        long end = args.getLong(ARG_RANGE_END_MILLIS);
        return container.expenseReader.observeInRange(new DateRange(start, end));
    }

    private void bindHeading() {
        Bundle args = getArguments();
        if (args == null) {
            titleView.setText(R.string.history_title);
            emptyView.setText(R.string.history_empty);
            return;
        }

        String label = args.getString(ARG_RANGE_LABEL, "");
        String emptyMessage = args.getString(ARG_EMPTY_MESSAGE, getString(R.string.history_empty));
        if (label == null || label.trim().isEmpty()) {
            titleView.setText(R.string.history_title);
        } else {
            titleView.setText(label);
        }
        emptyView.setText(emptyMessage);
    }
}
