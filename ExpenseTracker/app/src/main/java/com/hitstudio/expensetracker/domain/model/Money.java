package com.hitstudio.expensetracker.domain.model;

public class Money {
    public final long amountMinor;
    public final String currencyCode;

    public Money(long amountMinor, String currencyCode) {
        this.amountMinor = amountMinor;
        this.currencyCode = currencyCode == null || currencyCode.trim().isEmpty() ? "USD" : currencyCode;
    }

    public Money plus(Money other) {
        if (other == null) {
            return this;
        }
        return new Money(amountMinor + other.amountMinor, currencyCode);
    }
}
