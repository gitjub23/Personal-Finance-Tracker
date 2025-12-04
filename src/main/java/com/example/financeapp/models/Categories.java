package com.example.financeapp.models;

import java.util.Arrays;
import java.util.List;

public final class Categories {

    private Categories() {}

    public static final String FOOD          = "Food";
    public static final String TRANSPORT     = "Transport";
    public static final String ENTERTAINMENT = "Entertainment";
    public static final String BILLS         = "Bills";
    public static final String SUBSCRIPTIONS = "Subscriptions";
    public static final String SALARY = "Salary";
    public static final String OTHERS        = "Others";

    /**
     * Default expense categories used in dropdowns, etc.
     */
    public static List<String> getDefaultExpenseCategories() {
        return Arrays.asList(
                FOOD,
                TRANSPORT,
                ENTERTAINMENT,
                BILLS,
                SUBSCRIPTIONS,
                SALARY,
                OTHERS
        );
    }
}