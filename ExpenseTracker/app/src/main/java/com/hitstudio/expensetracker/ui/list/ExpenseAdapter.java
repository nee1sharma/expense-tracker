package com.hitstudio.expensetracker.ui.list;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.hitstudio.expensetracker.R;
import com.hitstudio.expensetracker.domain.model.Expense;
import com.hitstudio.expensetracker.domain.model.TransactionType;
import com.hitstudio.expensetracker.util.MoneyFormatter;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    public interface Listener {
        void onEdit(Expense expense);
        void onDelete(Expense expense);
    }

    private final Listener listener;
    private final List<Expense> expenses = new ArrayList<>();
    private final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

    public ExpenseAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<Expense> next) {
        expenses.clear();
        if (next != null) {
            expenses.addAll(next);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        String title = firstNonBlank(expense.reason, expense.payee, expense.categoryName, "Expense");
        String meta = expense.categoryName + " | " +
                dateFormat.format(new Date(expense.occurredAt)) + " | " +
                displayEnum(expense.paymentMethod.name());
        String amount = MoneyFormatter.format(expense.amountMinor, expense.currencyCode);
        if (expense.transactionType == TransactionType.INCOME) {
            amount = "+" + amount;
            holder.amount.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            amount = "-" + amount;
            holder.amount.setTextColor(Color.parseColor("#B3261E"));
        }

        holder.title.setText(title);
        holder.meta.setText(meta);
        holder.amount.setText(amount);
        holder.dot.setBackground(dot(expense.categoryColorHex));
        holder.editButton.setOnClickListener(v -> listener.onEdit(expense));
        holder.deleteButton.setOnClickListener(v -> listener.onDelete(expense));
    }

    @Override
    public int getItemCount() {
        return expenses.size();
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
        String lower = value.toLowerCase(Locale.US).replace('_', ' ');
        return lower.substring(0, 1).toUpperCase(Locale.US) + lower.substring(1);
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

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        final View dot;
        final TextView title;
        final TextView meta;
        final TextView amount;
        final MaterialButton editButton;
        final MaterialButton deleteButton;

        ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            dot = itemView.findViewById(R.id.expense_category_dot);
            title = itemView.findViewById(R.id.expense_title);
            meta = itemView.findViewById(R.id.expense_meta);
            amount = itemView.findViewById(R.id.expense_amount);
            editButton = itemView.findViewById(R.id.expense_edit_button);
            deleteButton = itemView.findViewById(R.id.expense_delete_button);
        }
    }
}
