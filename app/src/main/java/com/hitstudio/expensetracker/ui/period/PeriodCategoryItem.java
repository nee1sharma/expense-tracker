package com.hitstudio.expensetracker.ui.period;

public class PeriodCategoryItem {
    public final long categoryId;
    public final String categoryName;
    public final String colorHex;
    public final long amountMinor;
    public final String currencyCode;

    public PeriodCategoryItem(long categoryId, String categoryName, String colorHex, long amountMinor, String currencyCode) {
        this.categoryId = categoryId;
        this.categoryName = categoryName == null || categoryName.trim().isEmpty() ? "Uncategorized" : categoryName;
        this.colorHex = colorHex == null || colorHex.trim().isEmpty() ? "#607D8B" : colorHex;
        this.amountMinor = amountMinor;
        this.currencyCode = currencyCode == null || currencyCode.trim().isEmpty() ? "USD" : currencyCode;
    }
}
