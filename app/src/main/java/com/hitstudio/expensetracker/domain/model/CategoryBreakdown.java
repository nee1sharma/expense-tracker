package com.hitstudio.expensetracker.domain.model;

public class CategoryBreakdown {
    public final long categoryId;
    public final String categoryName;
    public final String colorHex;
    public final Money total;

    public CategoryBreakdown(long categoryId, String categoryName, String colorHex, Money total) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.colorHex = colorHex;
        this.total = total;
    }
}
