package com.hitstudio.expensetracker.ui.period;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hitstudio.expensetracker.R;
import com.hitstudio.expensetracker.util.MoneyFormatter;

import java.util.ArrayList;
import java.util.List;

public class PeriodCategoryAdapter extends RecyclerView.Adapter<PeriodCategoryAdapter.CategoryViewHolder> {
    private final List<PeriodCategoryItem> items = new ArrayList<>();

    public void submitList(List<PeriodCategoryItem> next) {
        items.clear();
        if (next != null) {
            items.addAll(next);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_breakdown, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        final View color;
        final TextView name;
        final TextView amount;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            color = itemView.findViewById(R.id.breakdown_color);
            name = itemView.findViewById(R.id.breakdown_name);
            amount = itemView.findViewById(R.id.breakdown_amount);
        }

        void bind(PeriodCategoryItem item) {
            name.setText(item.categoryName);
            amount.setText(MoneyFormatter.format(item.amountMinor, item.currencyCode));
            GradientDrawable dot = new GradientDrawable();
            dot.setShape(GradientDrawable.OVAL);
            try {
                dot.setColor(Color.parseColor(item.colorHex));
            } catch (IllegalArgumentException ignored) {
                dot.setColor(Color.parseColor("#607D8B"));
            }
            color.setBackground(dot);
        }
    }
}
