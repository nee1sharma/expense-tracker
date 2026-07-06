package com.hitstudio.expensetracker.ui.list;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.hitstudio.expensetracker.R;
import com.hitstudio.expensetracker.domain.model.Expense;
import com.hitstudio.expensetracker.domain.model.Money;
import com.hitstudio.expensetracker.domain.model.TransactionType;
import com.hitstudio.expensetracker.util.MoneyFormatter;

import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseHistoryAdapter extends RecyclerView.Adapter<ExpenseHistoryAdapter.GroupViewHolder> {
    public interface Listener {
        void onActionsRequested(Expense expense);
    }

    private final Listener listener;
    private final List<DayGroup> groups = new ArrayList<>();
    private final ZoneId zoneId = ZoneId.systemDefault();
    private final DateTimeFormatter headerFormatter =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault());
    private final DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());

    public ExpenseHistoryAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<Expense> next) {
        groups.clear();
        if (next == null || next.isEmpty()) {
            notifyDataSetChanged();
            return;
        }

        List<Expense> expenses = new ArrayList<>(next);
        expenses.sort(Comparator.comparingLong((Expense expense) -> expense.occurredAt).reversed());

        LocalDate currentDate = null;
        DayGroup currentGroup = null;
        for (Expense expense : expenses) {
            LocalDate expenseDate = LocalDate.ofInstant(Instant.ofEpochMilli(expense.occurredAt), zoneId);
            if (!expenseDate.equals(currentDate)) {
                currentDate = expenseDate;
                currentGroup = new DayGroup(expenseDate);
                groups.add(currentGroup);
            }
            currentGroup.expenses.add(expense);
            if (isExpense(expense)) {
                currentGroup.totalMinor += expense.amountMinor;
            }
            if (currentGroup.currencyCode == null) {
                currentGroup.currencyCode = normalizeCurrency(expense.currencyCode);
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense_day_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        holder.bind(groups.get(position), position);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    private String dayLabel(LocalDate date) {
        LocalDate today = LocalDate.now(zoneId);
        if (date.equals(today)) {
            return "Today";
        }
        if (date.equals(today.minusDays(1))) {
            return "Yesterday";
        }
        return headerFormatter.format(date);
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }
        return "";
    }

    private String displayEnum(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Cash";
        }
        String lower = value.toLowerCase(Locale.US).replace('_', ' ');
        return lower.substring(0, 1).toUpperCase(Locale.US) + lower.substring(1);
    }

    private GradientDrawable dot(String colorHex) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        try {
            int color = Color.parseColor(colorHex);
            drawable.setColor(color);
            drawable.setAlpha(40); // 15% opacity
        } catch (IllegalArgumentException ignored) {
            drawable.setColor(Color.parseColor("#607D8B"));
            drawable.setAlpha(40);
        }
        return drawable;
    }

    private boolean isExpense(Expense expense) {
        TransactionType type = expense.transactionType == null ? TransactionType.EXPENSE : expense.transactionType;
        return type != TransactionType.INCOME;
    }

    private String normalizeCurrency(String currencyCode) {
        return currencyCode == null || currencyCode.trim().isEmpty() ? "USD" : currencyCode.trim();
    }

    private class DayGroup {
        final LocalDate date;
        final List<Expense> expenses = new ArrayList<>();
        long totalMinor;
        String currencyCode;

        DayGroup(LocalDate date) {
            this.date = date;
        }
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {
        final MaterialCardView card;
        final TextView header;
        final TextView total;
        final RecyclerView recyclerView;
        final ExpenseItemsAdapter adapter;

        GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            card = (MaterialCardView) itemView;
            header = itemView.findViewById(R.id.expense_day_label);
            total = itemView.findViewById(R.id.expense_day_total);
            recyclerView = itemView.findViewById(R.id.expense_group_recycler);
            adapter = new ExpenseItemsAdapter();
            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(adapter);
        }

        void bind(DayGroup group, int position) {
            header.setText(dayLabel(group.date));
            total.setText(MoneyFormatter.format(group.totalMinor, group.currencyCode));
            adapter.submitList(group.expenses);

            int background = ContextCompat.getColor(itemView.getContext(),
                    position % 2 == 0 ? R.color.surface_soft : R.color.surface_background);
            card.setCardBackgroundColor(background);
        }
    }

    private class ExpenseItemsAdapter extends RecyclerView.Adapter<ExpenseRowViewHolder> {
        private final List<Expense> expenses = new ArrayList<>();

        void submitList(List<Expense> next) {
            expenses.clear();
            if (next != null) {
                expenses.addAll(next);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ExpenseRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
            return new ExpenseRowViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ExpenseRowViewHolder holder, int position) {
            holder.bind(expenses.get(position));
        }

        @Override
        public int getItemCount() {
            return expenses.size();
        }
    }

    private class ExpenseRowViewHolder extends RecyclerView.ViewHolder {
        final View dot;
        final TextView title;
        final TextView meta;
        final TextView amount;

        ExpenseRowViewHolder(@NonNull View itemView) {
            super(itemView);
            dot = itemView.findViewById(R.id.expense_category_dot);
            title = itemView.findViewById(R.id.expense_title);
            meta = itemView.findViewById(R.id.expense_meta);
            amount = itemView.findViewById(R.id.expense_amount);
        }

        void bind(Expense expense) {
            String titleText = firstNonBlank(expense.reason, expense.payee, expense.categoryName, "Expense");
            String categoryText = firstNonBlank(expense.categoryName, "Uncategorized");
            String paymentMethod = expense.paymentMethod == null ? "CASH" : expense.paymentMethod.name();
            String metaText = categoryText + " • " +
                    timeFormatter.format(new Date(expense.occurredAt)) + " • " +
                    displayEnum(paymentMethod);
            String amountText = MoneyFormatter.format(expense.amountMinor, expense.currencyCode);
            TransactionType type = expense.transactionType == null ? TransactionType.EXPENSE : expense.transactionType;

            if (type == TransactionType.INCOME) {
                amountText = "+" + amountText;
                amount.setTextColor(Color.parseColor("#2E7D32"));
            } else {
                amountText = "-" + amountText;
                amount.setTextColor(Color.parseColor("#B3261E"));
            }

            title.setText(titleText);
            meta.setText(metaText);
            amount.setText(amountText);
            dot.setBackground(dot(expense.categoryColorHex));

            itemView.setOnClickListener(v -> listener.onActionsRequested(expense));
            itemView.setOnLongClickListener(v -> {
                listener.onActionsRequested(expense);
                return true;
            });
        }
    }
}
