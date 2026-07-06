package com.hitstudio.expensetracker.ui.settings;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.hitstudio.expensetracker.R;
import com.hitstudio.expensetracker.domain.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategorySettingsAdapter extends RecyclerView.Adapter<CategorySettingsAdapter.CategoryViewHolder> {
    public interface Listener {
        void onActiveChanged(Category category, boolean active);
        void onMove(Category category, int direction);
        void onEdit(Category category);
    }

    private final Listener listener;
    private final List<Category> categories = new ArrayList<>();

    public CategorySettingsAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<Category> next) {
        categories.clear();
        if (next != null) {
            categories.addAll(next);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_settings, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.color.setBackground(colorStrip(category.colorHex));
        holder.name.setText(category.name);
        holder.status.setText(category.isActive ? "Visible" : "Hidden");
        holder.activeSwitch.setOnCheckedChangeListener(null);
        holder.activeSwitch.setChecked(category.isActive);
        holder.activeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> listener.onActiveChanged(category, isChecked));
        holder.upButton.setEnabled(position > 0);
        holder.downButton.setEnabled(position < categories.size() - 1);
        holder.upButton.setOnClickListener(v -> listener.onMove(category, -1));
        holder.downButton.setOnClickListener(v -> listener.onMove(category, 1));
        holder.itemView.setOnClickListener(v -> listener.onEdit(category));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    private GradientDrawable colorStrip(String colorHex) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(100f);
        try {
            drawable.setColor(Color.parseColor(colorHex));
        } catch (IllegalArgumentException ignored) {
            drawable.setColor(Color.parseColor("#607D8B"));
        }
        return drawable;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        final View color;
        final TextView name;
        final TextView status;
        final SwitchMaterial activeSwitch;
        final MaterialButton upButton;
        final MaterialButton downButton;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            color = itemView.findViewById(R.id.category_color);
            name = itemView.findViewById(R.id.category_name);
            status = itemView.findViewById(R.id.category_status);
            activeSwitch = itemView.findViewById(R.id.category_active_switch);
            upButton = itemView.findViewById(R.id.category_move_up);
            downButton = itemView.findViewById(R.id.category_move_down);
        }
    }
}
