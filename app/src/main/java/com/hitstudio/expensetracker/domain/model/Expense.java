package com.hitstudio.expensetracker.domain.model;

public class Expense {
    public long id;
    public long amountMinor;
    public String currencyCode;
    public TransactionType transactionType;
    public PaymentMethod paymentMethod;
    public long categoryId;
    public String categoryName;
    public String categoryColorHex;
    public Long accountId;
    public Long recurringRuleId;
    public String reason;
    public String payee;
    public String locationText;
    public ExpenseSource source;
    public long occurredAt;
    public long createdAt;
    public long updatedAt;
    public String notes;
    public String rawInput;
    public String remoteId;
    public SyncStatus syncStatus;
    public boolean deleted;

    public Expense() {
        long now = System.currentTimeMillis();
        currencyCode = "USD";
        transactionType = TransactionType.EXPENSE;
        paymentMethod = PaymentMethod.CASH;
        source = ExpenseSource.MANUAL;
        syncStatus = SyncStatus.LOCAL_ONLY;
        occurredAt = now;
        createdAt = now;
        updatedAt = now;
    }
}
