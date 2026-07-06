package com.hitstudio.expensetracker.data.local.room;

public class ExpenseRecord {
    public long id;
    public long amountMinor;
    public String currencyCode;
    public String transactionType;
    public String paymentMethod;
    public long categoryId;
    public Long accountId;
    public Long recurringRuleId;
    public String reason;
    public String payee;
    public String locationText;
    public String source;
    public long occurredAt;
    public long createdAt;
    public long updatedAt;
    public String notes;
    public String rawInput;
    public String remoteId;
    public String syncStatus;
    public boolean deleted;
    public String categoryName;
    public String categoryColorHex;
}
