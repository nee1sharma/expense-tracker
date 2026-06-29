package com.hitstudio.expensetracker.ui.list;

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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hitstudio.expensetracker.ExpenseTrackerApplication;
import com.hitstudio.expensetracker.R;
import com.hitstudio.expensetracker.domain.model.Expense;
import com.hitstudio.expensetracker.ui.common.AppViewModelFactory;

public class ExpenseListFragment extends Fragment {
    private ExpenseListViewModel viewModel;
    private ExpenseAdapter adapter;
    private TextView emptyView;

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

        emptyView = view.findViewById(R.id.expense_list_empty);
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

        RecyclerView recyclerView = view.findViewById(R.id.expense_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.expense_list_add).setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.loggerFragment));

        viewModel.expenses.observe(getViewLifecycleOwner(), expenses -> {
            adapter.submitList(expenses);
            emptyView.setVisibility(expenses == null || expenses.isEmpty() ? View.VISIBLE : View.GONE);
        });
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
