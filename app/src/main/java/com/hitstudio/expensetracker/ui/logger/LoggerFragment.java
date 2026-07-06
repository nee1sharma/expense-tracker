package com.hitstudio.expensetracker.ui.logger;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hitstudio.expensetracker.ExpenseTrackerApplication;
import com.hitstudio.expensetracker.R;
import com.hitstudio.expensetracker.app.AppContainer;
import com.hitstudio.expensetracker.domain.model.AppSettings;
import com.hitstudio.expensetracker.domain.model.Category;
import com.hitstudio.expensetracker.domain.model.Expense;
import com.hitstudio.expensetracker.domain.model.ExpenseSource;
import com.hitstudio.expensetracker.domain.model.PaymentMethod;
import com.hitstudio.expensetracker.domain.model.SyncStatus;
import com.hitstudio.expensetracker.domain.model.TransactionType;
import com.hitstudio.expensetracker.ui.common.AppViewModelFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LoggerFragment extends Fragment {
    private ExpenseViewModel viewModel;
    private AppContainer container;
    private TextInputLayout amountLayout;
    private TextInputEditText amountInput;
    private MaterialAutoCompleteTextView reasonInput;
    private TextInputEditText payeeInput;
    private TextInputEditText locationInput;
    private TextInputEditText dateInput;
    private Spinner categorySpinner;
    private Spinner paymentSpinner;
    private ArrayAdapter<String> categoryAdapter;
    private final List<Category> categories = new ArrayList<>();
    private final Calendar occurredAt = Calendar.getInstance();
    private final DateFormat dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    private Expense editingExpense;
    private long editingExpenseId;
    private long pendingCategoryId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_logger, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ExpenseTrackerApplication application = (ExpenseTrackerApplication) requireActivity().getApplication();
        container = application.getContainer();
        viewModel = new ViewModelProvider(this, new AppViewModelFactory(container)).get(ExpenseViewModel.class);

        if (getArguments() != null) {
            editingExpenseId = getArguments().getLong("expenseId", 0L);
        }

        bindViews(view);
        setupSpinners();
        updateDateText();

        dateInput.setOnClickListener(v -> pickDate());
        view.findViewById(R.id.logger_save_button).setOnClickListener(v -> save());

        reasonInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                viewModel.findSuggestion(text(reasonInput));
            } else {
                reasonInput.showDropDown();
            }
        });

        reasonInput.setOnItemClickListener((parent, v, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            viewModel.findSuggestion(selected);
        });

        viewModel.categories.observe(getViewLifecycleOwner(), this::bindCategories);
        viewModel.getRecentReasons().observe(getViewLifecycleOwner(), reasons -> {
            if (reasons != null && !reasons.isEmpty()) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_dropdown_item_1line, reasons);
                reasonInput.setAdapter(adapter);
            }
        });
        viewModel.getEditingExpense().observe(getViewLifecycleOwner(), expense -> {
            editingExpense = expense;
            if (expense != null) {
                bindExpense(expense);
            }
        });
        viewModel.getSuggestedExpense().observe(getViewLifecycleOwner(), suggestion -> {
            if (suggestion != null) {
                applySuggestion(suggestion);
            }
        });
        viewModel.getSaveResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) {
                return;
            }
            if (result.success) {
                Toast.makeText(requireContext(), "Transaction saved.", Toast.LENGTH_SHORT).show();
                NavController navController = Navigation.findNavController(requireView());
                if (!navController.popBackStack()) {
                    navController.navigate(R.id.dashboardFragment);
                }
            } else {
                Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show();
            }
        });

        if (editingExpenseId > 0L) {
            viewModel.loadExpense(editingExpenseId);
        } else {
            AppSettings settings = container.preferences.getSettings();
            paymentSpinner.setSelection(settings.defaultPaymentMethod.ordinal());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.refreshCategories();
            viewModel.loadRecentReasons();
        }
    }

    private void bindViews(View view) {
        amountLayout = view.findViewById(R.id.logger_amount_layout);
        amountInput = view.findViewById(R.id.logger_amount);
        reasonInput = view.findViewById(R.id.logger_reason);
        payeeInput = view.findViewById(R.id.logger_payee);
        locationInput = view.findViewById(R.id.logger_location);
        dateInput = view.findViewById(R.id.logger_date);
        categorySpinner = view.findViewById(R.id.logger_category_spinner);
        paymentSpinner = view.findViewById(R.id.logger_payment_spinner);
    }

    private void setupSpinners() {
        paymentSpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item,
                displayPayments()));
        categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
    }

    private void bindCategories(List<Category> next) {
        long selectedCategoryId = getSelectedCategoryId();
        categories.clear();
        if (next != null) {
            categories.addAll(next);
        }
        List<String> names = new ArrayList<>();
        for (Category category : categories) {
            names.add(category.name);
        }
        categoryAdapter.clear();
        categoryAdapter.addAll(names);
        categoryAdapter.notifyDataSetChanged();
        if (pendingCategoryId > 0) {
            selectCategory(pendingCategoryId);
            pendingCategoryId = 0;
        } else if (selectedCategoryId > 0) {
            selectCategory(selectedCategoryId);
        }
    }

    private void bindExpense(Expense expense) {
        amountInput.setText(String.format(Locale.US, "%.2f", expense.amountMinor / 100.0d));
        paymentSpinner.setSelection(expense.paymentMethod.ordinal());
        reasonInput.setText(expense.reason);
        payeeInput.setText(expense.payee);
        locationInput.setText(expense.locationText);
        occurredAt.setTimeInMillis(expense.occurredAt);
        updateDateText();
        if (!selectCategory(expense.categoryId)) {
            pendingCategoryId = expense.categoryId;
        }
    }

    private void applySuggestion(Expense suggestion) {
        if (text(payeeInput).isEmpty() && suggestion.payee != null) {
            payeeInput.setText(suggestion.payee);
        }
        if (text(locationInput).isEmpty() && suggestion.locationText != null) {
            locationInput.setText(suggestion.locationText);
        }
        // If user hasn't manually picked a category (or it's the first one), suggest the previous one
        if (suggestion.categoryId > 0) {
            selectCategory(suggestion.categoryId);
        }
    }

    private boolean selectCategory(long categoryId) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).id == categoryId) {
                categorySpinner.setSelection(i);
                return true;
            }
        }
        return false;
    }

    private long getSelectedCategoryId() {
        int position = categorySpinner.getSelectedItemPosition();
        if (position < 0 || position >= categories.size()) {
            return 0L;
        }
        return categories.get(position).id;
    }

    private void save() {
        amountLayout.setError(null);
        Long amountMinor = parseAmountMinor(text(amountInput));
        if (amountMinor == null) {
            amountLayout.setError("Enter a valid amount.");
            return;
        }
        if (categories.isEmpty() || categorySpinner.getSelectedItemPosition() < 0) {
            Toast.makeText(requireContext(), "Add or enable a category first.", Toast.LENGTH_LONG).show();
            return;
        }
        Category category = categories.get(categorySpinner.getSelectedItemPosition());
        Expense expense = editingExpense == null ? new Expense() : editingExpense;
        AppSettings settings = container.preferences.getSettings();
        expense.amountMinor = amountMinor;
        expense.currencyCode = settings.defaultCurrencyCode;
        expense.transactionType = TransactionType.EXPENSE;
        expense.paymentMethod = PaymentMethod.values()[paymentSpinner.getSelectedItemPosition()];
        expense.categoryId = category.id;
        expense.categoryName = category.name;
        expense.categoryColorHex = category.colorHex;
        expense.reason = text(reasonInput);
        expense.payee = text(payeeInput);
        expense.locationText = text(locationInput);
        expense.occurredAt = occurredAt.getTimeInMillis();
        expense.source = ExpenseSource.MANUAL;
        expense.syncStatus = SyncStatus.LOCAL_ONLY;
        expense.deleted = false;
        viewModel.save(expense);
    }

    private Long parseAmountMinor(String amountText) {
        try {
            BigDecimal value = new BigDecimal(amountText).setScale(2, RoundingMode.HALF_UP);
            if (value.compareTo(BigDecimal.ZERO) <= 0) {
                return null;
            }
            return value.movePointRight(2).longValueExact();
        } catch (ArithmeticException | NumberFormatException ex) {
            return null;
        }
    }

    private String text(View view) {
        if (view instanceof TextInputEditText) {
            TextInputEditText et = (TextInputEditText) view;
            return et.getText() == null ? "" : et.getText().toString().trim();
        } else if (view instanceof MaterialAutoCompleteTextView) {
            MaterialAutoCompleteTextView tv = (MaterialAutoCompleteTextView) view;
            return tv.getText() == null ? "" : tv.getText().toString().trim();
        }
        return "";
    }

    private String[] displayPayments() {
        PaymentMethod[] values = PaymentMethod.values();
        String[] display = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            String raw = values[i].name().toLowerCase(Locale.US).replace('_', ' ');
            display[i] = raw.substring(0, 1).toUpperCase(Locale.US) + raw.substring(1);
        }
        return display;
    }

    private void pickDate() {
        new DatePickerDialog(requireContext(),
                (picker, year, month, dayOfMonth) -> {
                    occurredAt.set(Calendar.YEAR, year);
                    occurredAt.set(Calendar.MONTH, month);
                    occurredAt.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateText();
                    pickTime();
                },
                occurredAt.get(Calendar.YEAR),
                occurredAt.get(Calendar.MONTH),
                occurredAt.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void pickTime() {
        new TimePickerDialog(requireContext(),
                (picker, hourOfDay, minute) -> {
                    occurredAt.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    occurredAt.set(Calendar.MINUTE, minute);
                    occurredAt.set(Calendar.SECOND, 0);
                    occurredAt.set(Calendar.MILLISECOND, 0);
                    updateDateText();
                },
                occurredAt.get(Calendar.HOUR_OF_DAY),
                occurredAt.get(Calendar.MINUTE),
                false)
                .show();
    }

    private void updateDateText() {
        if (dateInput != null) {
            dateInput.setText(dateTimeFormat.format(occurredAt.getTime()));
        }
    }
}
