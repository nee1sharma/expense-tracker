package com.hitstudio.expensetracker.ui.settings;

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
import androidx.navigation.Navigation;

import com.hitstudio.expensetracker.ExpenseTrackerApplication;
import com.hitstudio.expensetracker.R;
import com.hitstudio.expensetracker.domain.model.AppSettings;
import com.hitstudio.expensetracker.domain.model.PaymentMethod;
import com.hitstudio.expensetracker.ui.common.AppViewModelFactory;

import java.util.Locale;

public class SettingsFragment extends Fragment {
    private static final String[] CURRENCIES = {"USD", "INR", "EUR", "GBP", "CAD", "AUD", "JPY"};

    private SettingsViewModel viewModel;
    private Spinner currencySpinner;
    private Spinner paymentSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ExpenseTrackerApplication application = (ExpenseTrackerApplication) requireActivity().getApplication();
        viewModel = new ViewModelProvider(this, new AppViewModelFactory(application.getContainer()))
                .get(SettingsViewModel.class);

        currencySpinner = view.findViewById(R.id.settings_currency_spinner);
        paymentSpinner = view.findViewById(R.id.settings_payment_spinner);

        currencySpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, CURRENCIES));
        paymentSpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, displayPayments()));

        view.findViewById(R.id.settings_save_button).setOnClickListener(v -> {
            String currency = (String) currencySpinner.getSelectedItem();
            PaymentMethod method = PaymentMethod.values()[paymentSpinner.getSelectedItemPosition()];
            viewModel.saveSettings(currency, method);
        });

        view.findViewById(R.id.settings_categories_nav).setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.categorySettingsFragment));

        view.findViewById(R.id.settings_about_button).setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.aboutFragment));

        viewModel.getSettings().observe(getViewLifecycleOwner(), this::bindSettings);
        viewModel.getMessage().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                Toast.makeText(requireContext(), result.success ? result.data : result.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindSettings(AppSettings settings) {
        for (int i = 0; i < CURRENCIES.length; i++) {
            if (CURRENCIES[i].equals(settings.defaultCurrencyCode)) {
                currencySpinner.setSelection(i);
                break;
            }
        }
        paymentSpinner.setSelection(settings.defaultPaymentMethod.ordinal());
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
}
