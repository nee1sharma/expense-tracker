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
    private TextInputEditText reasonInput;
    private TextInputEditText payeeInput;
    private TextInputEditText locationInput;
    private TextInputEditText notesInput;
    private TextInputEditText dateInput;
    private Spinner typeSpinner;
    private Spinner categorySpinner;
    private Spinner paymentSpinner;
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
        view.findViewById(R.id.logger_pick_date_button).setOnClickListener(v -> pickDate());
        view.findViewById(R.id.logger_pick_time_button).setOnClickListener(v -> pickTime());
        view.findViewById(R.id.logger_save_button).setOnClickListener(v -> save());

        viewModel.categories.observe(getViewLifecycleOwner(), this::bindCategories);
        viewModel.getEditingExpense().observe(getViewLifecycleOwner(), expense -> {
            editingExpense = expense;
            if (expense != null) {
                bindExpense(expense);
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

    private void bindViews(View view) {
        amountLayout = view.findViewById(R.id.logger_amount_layout);
        amountInput = view.findViewById(R.id.logger_amount);
        reasonInput = view.findViewById(R.id.logger_reason);
        payeeInput = view.findViewById(R.id.logger_payee);
        locationInput = view.findViewById(R.id.logger_location);
        notesInput = view.findViewById(R.id.logger_notes);
        dateInput = view.findViewById(R.id.logger_date);
        typeSpinner = view.findViewById(R.id.logger_type_spinner);
        categorySpinner = view.findViewById(R.id.logger_category_spinner);
        paymentSpinner = view.findViewById(R.id.logger_payment_spinner);
    }

    private void setupSpinners() {
        typeSpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Expense", "Income"}));
        paymentSpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item,
                displayPayments()));
    }

    private void bindCategories(List<Category> next) {
        categories.clear();
        if (next != null) {
            categories.addAll(next);
        }
        List<String> names = new ArrayList<>();
        for (Category category : categories) {
            names.add(category.name);
        }
        categorySpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, names));
        if (pendingCategoryId > 0) {
            selectCategory(pendingCategoryId);
            pendingCategoryId = 0;
        }
    }

    private void bindExpense(Expense expense) {
        amountInput.setText(String.format(Locale.US, "%.2f", expense.amountMinor / 100.0d));
        typeSpinner.setSelection(expense.transactionType.ordinal());
        paymentSpinner.setSelection(expense.paymentMethod.ordinal());
        reasonInput.setText(expense.reason);
        payeeInput.setText(expense.payee);
        locationInput.setText(expense.locationText);
        notesInput.setText(expense.notes);
        occurredAt.setTimeInMillis(expense.occurredAt);
        updateDateText();
        if (!selectCategory(expense.categoryId)) {
            pendingCategoryId = expense.categoryId;
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
        expense.transactionType = typeSpinner.getSelectedItemPosition() == 1 ? TransactionType.INCOME : TransactionType.EXPENSE;
        expense.paymentMethod = PaymentMethod.values()[paymentSpinner.getSelectedItemPosition()];
        expense.categoryId = category.id;
        expense.categoryName = category.name;
        expense.categoryColorHex = category.colorHex;
        expense.reason = text(reasonInput);
        expense.payee = text(payeeInput);
        expense.locationText = text(locationInput);
        expense.notes = text(notesInput);
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

    private String text(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
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
