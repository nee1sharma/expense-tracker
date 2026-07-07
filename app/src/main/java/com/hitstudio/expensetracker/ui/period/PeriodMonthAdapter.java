package com.hitstudio.expensetracker.ui.period;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.hitstudio.expensetracker.R;
import com.hitstudio.expensetracker.util.MoneyFormatter;

import java.util.ArrayList;
import java.util.List;

public class PeriodMonthAdapter extends RecyclerView.Adapter<PeriodMonthAdapter.MonthViewHolder> {
    public interface Listener {
        void onMonthSelected(PeriodMonthItem item);
    }

    private final Listener listener;
    private final List<PeriodMonthItem> items = new ArrayList<>();
    private long selectedStartMillis = Long.MIN_VALUE;

    public PeriodMonthAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<PeriodMonthItem> next, long selectedStartMillis) {
        items.clear();
        if (next != null) {
            items.addAll(next);
        }
        this.selectedStartMillis = selectedStartMillis;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_period_month_tab, parent, false);
        return new MonthViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthViewHolder holder, int position) {
        PeriodMonthItem item = items.get(position);
        holder.bind(item, item.startMillis == selectedStartMillis);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MonthViewHolder extends RecyclerView.ViewHolder {
        final MaterialCardView card;
        final TextView label;
        final TextView total;
        final TextView emptyNote;

        MonthViewHolder(@NonNull View itemView) {
            super(itemView);
            card = (MaterialCardView) itemView;
            label = itemView.findViewById(R.id.period_month_label);
            total = itemView.findViewById(R.id.period_month_total);
            emptyNote = itemView.findViewById(R.id.period_month_empty);
        }

        void bind(PeriodMonthItem item, boolean selected) {
            label.setText(item.label);
            total.setText(MoneyFormatter.format(item.totalMinor, item.currencyCode));
            emptyNote.setVisibility(item.hasExpenses ? View.GONE : View.VISIBLE);

            card.setStrokeWidth(selected ? 4 : 1);
            card.setStrokeColor(ContextCompat.getColor(itemView.getContext(),
                    selected ? R.color.brand_primary : R.color.surface_variant));
            card.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(),
                    selected ? R.color.brand_primary_container : R.color.surface_soft));

            itemView.setOnClickListener(v -> listener.onMonthSelected(item));
        }
    }
}
