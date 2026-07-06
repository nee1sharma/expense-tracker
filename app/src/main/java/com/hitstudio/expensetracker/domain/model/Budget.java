package com.hitstudio.expensetracker.domain.model;

public class Budget {
    public long id;
    public Long categoryId;
    public long amountMinor;
    public String currencyCode;
    public BudgetPeriod period;
    public int alertThresholdPercent;
    public boolean isActive;
    public long createdAt;
    public long updatedAt;
}
