package com.hitstudio.expensetracker.app;

import android.content.Context;

import com.hitstudio.expensetracker.data.file.FileStorage;
import com.hitstudio.expensetracker.data.local.StorageRegistry;
import com.hitstudio.expensetracker.data.local.room.AppDatabase;
import com.hitstudio.expensetracker.data.local.room.RoomCategoryLocalStore;
import com.hitstudio.expensetracker.data.local.room.RoomExpenseLocalStore;
import com.hitstudio.expensetracker.data.prefs.AppPreferences;
import com.hitstudio.expensetracker.data.repository.CategoryReaderImpl;
import com.hitstudio.expensetracker.data.repository.CategoryWriterImpl;
import com.hitstudio.expensetracker.data.repository.ExpenseReaderImpl;
import com.hitstudio.expensetracker.data.repository.ExpenseWriterImpl;
import com.hitstudio.expensetracker.domain.model.Category;
import com.hitstudio.expensetracker.domain.repository.CategoryReader;
import com.hitstudio.expensetracker.domain.repository.CategoryWriter;
import com.hitstudio.expensetracker.domain.repository.ExpenseReader;
import com.hitstudio.expensetracker.domain.repository.ExpenseWriter;
import com.hitstudio.expensetracker.domain.service.AggregationService;
import com.hitstudio.expensetracker.domain.service.StorageMigrationService;
import com.hitstudio.expensetracker.notify.NotificationHelper;
import com.hitstudio.expensetracker.util.AppLogger;
import com.hitstudio.expensetracker.util.concurrent.AppExecutors;
import com.hitstudio.expensetracker.work.WorkScheduler;

import java.util.ArrayList;
import java.util.List;

public class AppContainer {
    public final AppExecutors executors;
    public final AppPreferences preferences;
    public final AppDatabase database;
    public final StorageRegistry storageRegistry;
    public final ExpenseReader expenseReader;
    public final ExpenseWriter expenseWriter;
    public final CategoryReader categoryReader;
    public final CategoryWriter categoryWriter;
    public final AggregationService aggregationService;
    public final StorageMigrationService storageMigrationService;
    public final FileStorage fileStorage;
    public final NotificationHelper notificationHelper;
    public final WorkScheduler workScheduler;

    public AppContainer(Context context) {
        AppLogger.i("AppContainer", "Initializing AppContainer");
        Context appContext = context.getApplicationContext();
        executors = new AppExecutors();
        preferences = new AppPreferences(appContext);
        database = AppDatabase.create(appContext);

        RoomExpenseLocalStore expenseStore = new RoomExpenseLocalStore(database.expenseDao());
        RoomCategoryLocalStore categoryStore = new RoomCategoryLocalStore(database.categoryDao());
        storageRegistry = new StorageRegistry(preferences, expenseStore, expenseStore, categoryStore, categoryStore);

        aggregationService = new AggregationService();
        expenseReader = new ExpenseReaderImpl(storageRegistry, preferences, aggregationService);
        expenseWriter = new ExpenseWriterImpl(storageRegistry);
        categoryReader = new CategoryReaderImpl(storageRegistry);
        categoryWriter = new CategoryWriterImpl(storageRegistry);
        storageMigrationService = new StorageMigrationService();
        fileStorage = new FileStorage(appContext);
        notificationHelper = new NotificationHelper(appContext);
        workScheduler = new WorkScheduler(appContext);

        seedDefaultCategories();
    }

    private void seedDefaultCategories() {
        executors.diskIO().execute(() -> {
            if (storageRegistry.getCategoryLocalReader().count() > 0) {
                return;
            }
            storageRegistry.getCategoryLocalWriter().insertAll(defaultCategories());
        });
    }

    private List<Category> defaultCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Food", "restaurant", "#2E7D32", true, true, 0));
        categories.add(new Category("Transport", "directions", "#1565C0", true, true, 1));
        categories.add(new Category("Bills", "receipt", "#C62828", true, true, 2));
        categories.add(new Category("Utilities", "bolt", "#6A1B9A", true, true, 3));
        categories.add(new Category("Shopping", "shopping_bag", "#AD5E00", true, true, 4));
        categories.add(new Category("Health", "health", "#00838F", true, true, 5));
        categories.add(new Category("Entertainment", "movie", "#D81B60", true, true, 6));
        categories.add(new Category("Education", "school", "#455A64", true, true, 7));
        categories.add(new Category("Travel", "flight", "#5D4037", true, true, 8));
        categories.add(new Category("Other", "more", "#607D8B", true, true, 9));
        return categories;
    }
}
