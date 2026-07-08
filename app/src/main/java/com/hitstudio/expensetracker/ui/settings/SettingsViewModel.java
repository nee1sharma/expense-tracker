package com.hitstudio.expensetracker.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hitstudio.expensetracker.app.AppContainer;
import com.hitstudio.expensetracker.domain.model.AppSettings;
import com.hitstudio.expensetracker.domain.model.Category;
import com.hitstudio.expensetracker.domain.model.PaymentMethod;
import com.hitstudio.expensetracker.domain.model.StorageMode;
import com.hitstudio.expensetracker.util.concurrent.Result;

import java.util.List;

public class SettingsViewModel extends ViewModel {
    public final LiveData<List<Category>> categories;
    private final AppContainer container;
    private final MutableLiveData<AppSettings> settings = new MutableLiveData<>();
    private final MutableLiveData<Result<String>> message = new MutableLiveData<>();

    public SettingsViewModel(AppContainer container) {
        this.container = container;
        categories = container.categoryReader.observeAll();
        settings.setValue(container.preferences.getSettings());
    }

    public LiveData<AppSettings> getSettings() {
        return settings;
    }

    public LiveData<Result<String>> getMessage() {
        return message;
    }

    public void clearMessage() {
        message.setValue(null);
    }

    public void saveSettings(String currencyCode, PaymentMethod paymentMethod) {
        AppSettings next = new AppSettings(currencyCode, paymentMethod, StorageMode.ROOM_LOCAL, false, java.util.Calendar.MONDAY);
        container.preferences.saveSettings(next);
        settings.setValue(next);
        message.setValue(Result.success("Settings saved."));
    }

    public void addCategory(String rawName) {
        String name = rawName == null ? "" : rawName.trim();
        if (name.isEmpty()) {
            message.setValue(Result.error("Category name is required."));
            return;
        }
        container.executors.diskIO().execute(() -> {
            if (container.categoryReader.getByName(name) != null) {
                container.executors.mainThread().execute(() -> message.setValue(Result.error("Category already exists.")));
                return;
            }
            int sortOrder = container.categoryReader.getAll().size();
            long id = container.categoryWriter.add(new Category(name, "label", "#00838F", false, true, sortOrder));
            container.executors.mainThread().execute(() -> {
                if (id > 0) {
                    message.setValue(Result.success("Category added."));
                } else {
                    message.setValue(Result.error("Could not add category."));
                }
            });
        });
    }

    public void updateCategory(Category category, String rawName) {
        String name = rawName == null ? "" : rawName.trim();
        if (name.isEmpty()) {
            message.setValue(Result.error("Category name is required."));
            return;
        }
        container.executors.diskIO().execute(() -> {
            Category existing = container.categoryReader.getByName(name);
            if (existing != null && existing.id != category.id) {
                container.executors.mainThread().execute(() -> message.setValue(Result.error("Category name already exists.")));
                return;
            }
            category.name = name;
            category.updatedAt = System.currentTimeMillis();
            container.categoryWriter.update(category);
            container.executors.mainThread().execute(() -> message.setValue(Result.success("Category updated.")));
        });
    }

    public void setCategoryActive(Category category, boolean active) {
        if (category == null) {
            return;
        }
        container.executors.diskIO().execute(() -> {
            category.isActive = active;
            category.updatedAt = System.currentTimeMillis();
            container.categoryWriter.update(category);
        });
    }

    public void moveCategory(Category category, int direction) {
        if (category == null || direction == 0) {
            return;
        }
        container.executors.diskIO().execute(() -> {
            List<Category> all = container.categoryReader.getAll();
            int index = -1;
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).id == category.id) {
                    index = i;
                    break;
                }
            }
            int target = index + direction;
            if (index < 0 || target < 0 || target >= all.size()) {
                return;
            }
            Category current = all.get(index);
            Category other = all.get(target);
            int oldOrder = current.sortOrder;
            current.sortOrder = other.sortOrder;
            other.sortOrder = oldOrder;
            long now = System.currentTimeMillis();
            current.updatedAt = now;
            other.updatedAt = now;
            container.categoryWriter.update(current);
            container.categoryWriter.update(other);
        });
    }
}
