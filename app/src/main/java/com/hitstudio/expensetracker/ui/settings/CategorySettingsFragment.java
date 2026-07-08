package com.hitstudio.expensetracker.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hitstudio.expensetracker.ExpenseTrackerApplication;
import com.hitstudio.expensetracker.R;
import com.hitstudio.expensetracker.domain.model.Category;
import com.hitstudio.expensetracker.ui.common.AppViewModelFactory;

public class CategorySettingsFragment extends Fragment {

    private SettingsViewModel viewModel;
    private CategorySettingsAdapter categoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ExpenseTrackerApplication application = (ExpenseTrackerApplication) requireActivity().getApplication();
        viewModel = new ViewModelProvider(this, new AppViewModelFactory(application.getContainer()))
                .get(SettingsViewModel.class);

        categoryAdapter = new CategorySettingsAdapter(new CategorySettingsAdapter.Listener() {
            @Override
            public void onActiveChanged(Category category, boolean active) {
                viewModel.setCategoryActive(category, active);
            }

            @Override
            public void onMove(Category category, int direction) {
                viewModel.moveCategory(category, direction);
            }

            @Override
            public void onEdit(Category category) {
                showUpdateCategoryDialog(category);
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.settings_category_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(categoryAdapter);

        view.findViewById(R.id.settings_add_category).setOnClickListener(v -> showAddCategoryDialog());

        viewModel.categories.observe(getViewLifecycleOwner(), categoryAdapter::submitList);
        viewModel.getMessage().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                Toast.makeText(requireContext(), result.success ? result.data : result.message, Toast.LENGTH_SHORT).show();
                viewModel.clearMessage();
            }
        });
    }

    private void showAddCategoryDialog() {
        EditText input = new EditText(requireContext());
        input.setHint("Category name");
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add category")
                .setView(input)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Add", (dialog, which) -> viewModel.addCategory(input.getText().toString()))
                .show();
    }

    private void showUpdateCategoryDialog(Category category) {
        EditText input = new EditText(requireContext());
        input.setHint("Category name");
        input.setText(category.name);
        input.setSelection(category.name.length());
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Update category")
                .setView(input)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Update", (dialog, which) -> viewModel.updateCategory(category, input.getText().toString()))
                .show();
    }
}
