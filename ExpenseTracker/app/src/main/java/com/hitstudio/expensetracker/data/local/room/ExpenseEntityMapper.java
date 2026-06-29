package com.hitstudio.expensetracker.data.local.room;

import com.hitstudio.expensetracker.domain.model.Expense;
import com.hitstudio.expensetracker.domain.model.ExpenseSource;
import com.hitstudio.expensetracker.domain.model.PaymentMethod;
import com.hitstudio.expensetracker.domain.model.SyncStatus;
import com.hitstudio.expensetracker.domain.model.TransactionType;

import java.util.ArrayList;
import java.util.List;

public class ExpenseEntityMapper {
    private ExpenseEntityMapper() {
    }

    public static ExpenseEntity toEntity(Expense expense) {
        ExpenseEntity entity = new ExpenseEntity();
        entity.id = expense.id;
        entity.amountMinor = expense.amountMinor;
        entity.currencyCode = valueOr(expense.currencyCode, "USD");
        entity.transactionType = enumName(expense.transactionType, TransactionType.EXPENSE);
        entity.paymentMethod = enumName(expense.paymentMethod, PaymentMethod.CASH);
        entity.categoryId = expense.categoryId;
        entity.accountId = expense.accountId;
        entity.recurringRuleId = expense.recurringRuleId;
        entity.reason = clean(expense.reason);
        entity.payee = clean(expense.payee);
        entity.locationText = clean(expense.locationText);
        entity.source = enumName(expense.source, ExpenseSource.MANUAL);
        entity.occurredAt = expense.occurredAt;
        entity.createdAt = expense.createdAt;
        entity.updatedAt = expense.updatedAt;
        entity.notes = clean(expense.notes);
        entity.rawInput = clean(expense.rawInput);
        entity.remoteId = clean(expense.remoteId);
        entity.syncStatus = enumName(expense.syncStatus, SyncStatus.LOCAL_ONLY);
        entity.deleted = expense.deleted;
        return entity;
    }

    public static Expense fromRecord(ExpenseRecord record) {
        if (record == null) {
            return null;
        }
        Expense expense = new Expense();
        expense.id = record.id;
        expense.amountMinor = record.amountMinor;
        expense.currencyCode = valueOr(record.currencyCode, "USD");
        expense.transactionType = enumValue(TransactionType.class, record.transactionType, TransactionType.EXPENSE);
        expense.paymentMethod = enumValue(PaymentMethod.class, record.paymentMethod, PaymentMethod.CASH);
        expense.categoryId = record.categoryId;
        expense.categoryName = valueOr(record.categoryName, "Uncategorized");
        expense.categoryColorHex = valueOr(record.categoryColorHex, "#607D8B");
        expense.accountId = record.accountId;
        expense.recurringRuleId = record.recurringRuleId;
        expense.reason = valueOr(record.reason, "");
        expense.payee = valueOr(record.payee, "");
        expense.locationText = valueOr(record.locationText, "");
        expense.source = enumValue(ExpenseSource.class, record.source, ExpenseSource.MANUAL);
        expense.occurredAt = record.occurredAt;
        expense.createdAt = record.createdAt;
        expense.updatedAt = record.updatedAt;
        expense.notes = valueOr(record.notes, "");
        expense.rawInput = valueOr(record.rawInput, "");
        expense.remoteId = valueOr(record.remoteId, "");
        expense.syncStatus = enumValue(SyncStatus.class, record.syncStatus, SyncStatus.LOCAL_ONLY);
        expense.deleted = record.deleted;
        return expense;
    }

    public static List<Expense> fromRecords(List<ExpenseRecord> records) {
        List<Expense> expenses = new ArrayList<>();
        if (records == null) {
            return expenses;
        }
        for (ExpenseRecord record : records) {
            expenses.add(fromRecord(record));
        }
        return expenses;
    }

    public static List<ExpenseEntity> toEntities(List<Expense> expenses) {
        List<ExpenseEntity> entities = new ArrayList<>();
        if (expenses == null) {
            return entities;
        }
        for (Expense expense : expenses) {
            entities.add(toEntity(expense));
        }
        return entities;
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private static String valueOr(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private static <T extends Enum<T>> T enumValue(Class<T> type, String value, T fallback) {
        try {
            return Enum.valueOf(type, value);
        } catch (IllegalArgumentException | NullPointerException ignored) {
            return fallback;
        }
    }

    private static <T extends Enum<T>> String enumName(T value, T fallback) {
        return value == null ? fallback.name() : value.name();
    }
}
