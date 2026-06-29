package com.hitstudio.expensetracker.domain.model;

public class AppSettings {
    public String defaultCurrencyCode;
    public PaymentMethod defaultPaymentMethod;
    public StorageMode storageMode;
    public boolean includeIncomeOnDashboard;
    public int firstDayOfWeek;

    public AppSettings(String defaultCurrencyCode, PaymentMethod defaultPaymentMethod,
                       StorageMode storageMode, boolean includeIncomeOnDashboard, int firstDayOfWeek) {
        this.defaultCurrencyCode = defaultCurrencyCode;
        this.defaultPaymentMethod = defaultPaymentMethod;
        this.storageMode = storageMode;
        this.includeIncomeOnDashboard = includeIncomeOnDashboard;
        this.firstDayOfWeek = firstDayOfWeek;
    }
}
