package com.example.financeapp.models;

import java.time.YearMonth;
import java.util.*;

public class AnalyticsService {

    private final TransactionManager transactionManager;

    public AnalyticsService(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * Generate recommendations by comparing the current month to the previous month.
     *
     * Assumptions:
     *   - Income totals are positive.
     *   - Expense totals (category + monthly) are NEGATIVE.
     */
    public List<String> generateMonthlyRecommendations(int userId, YearMonth currentMonth) {
        List<String> recommendations = new ArrayList<>();

        YearMonth prevMonth = currentMonth.minusMonths(1);

        // Signed category totals (likely negative for expenses)
        Map<String, Double> currentExpensesSigned =
                transactionManager.getCategoryTotalsForMonth(userId, currentMonth, false);

        Map<String, Double> prevExpensesSigned =
                transactionManager.getCategoryTotalsForMonth(userId, prevMonth, false);

        // Sums of signed values (negative if there are expenses, 0 if none)
        double totalCurrentSigned = currentExpensesSigned.values()
                .stream().mapToDouble(Double::doubleValue).sum();
        double totalPrevSigned = prevExpensesSigned.values()
                .stream().mapToDouble(Double::doubleValue).sum();

        // If there is almost no data, show a friendly message
        if (totalCurrentSigned == 0 && totalPrevSigned == 0) {
            recommendations.add("Not enough data yet to generate insights. Start adding some transactions!");
            return recommendations;
        }

        // 1) Category-level comparison (current vs last month)
        //    Use ABS values so we talk about "how much you spent"
        Set<String> allCategories = new HashSet<>();
        allCategories.addAll(currentExpensesSigned.keySet());
        allCategories.addAll(prevExpensesSigned.keySet());

        for (String category : allCategories) {
            double curr = Math.abs(currentExpensesSigned.getOrDefault(category, 0.0));
            double prev = Math.abs(prevExpensesSigned.getOrDefault(category, 0.0));

            if (prev == 0 && curr == 0) {
                continue;
            }

            if (prev > 0 && curr > 0) {
                double changeRatio = (curr - prev) / prev; // >0 means increased
                double changePercent = changeRatio * 100.0;

                if (changeRatio >= 0.2) {
                    recommendations.add(String.format(
                            "You spent %.0f%% more on %s than last month ($%.2f vs $%.2f). " +
                                    "Consider reducing this category by around 10%% next month.",
                            changePercent, category, curr, prev
                    ));
                } else if (changeRatio <= -0.2) {
                    recommendations.add(String.format(
                            "Nice! You spent %.0f%% less on %s compared to last month ($%.2f vs $%.2f).",
                            Math.abs(changePercent), category, curr, prev
                    ));
                }
            } else if (prev == 0 && curr > 0) {
                recommendations.add(String.format(
                        "You started spending on %s this month (total $%.2f). " +
                                "Make sure this aligns with your priorities.",
                        category, curr
                ));
            } else if (prev > 0 && curr == 0) {
                recommendations.add(String.format(
                        "You had no spending on %s this month (was $%.2f last month). " +
                                "Great job reducing this category!",
                        category, prev
                ));
            }
        }

        // 2) Overall savings insight
        //    Use monthly totals: income (positive), expenses (negative)
        double currentIncome = transactionManager.getTotalIncomeForMonth(userId, currentMonth);
        double prevIncome = transactionManager.getTotalIncomeForMonth(userId, prevMonth);

        double currentExpensesMonth = transactionManager.getTotalExpenseForMonth(userId, currentMonth); // negative
        double prevExpensesMonth = transactionManager.getTotalExpenseForMonth(userId, prevMonth);       // negative

        // Net savings / balance for the month (same rule as dashboard & PDF)
        double currentNet = currentIncome + currentExpensesMonth;
        double prevNet = prevIncome + prevExpensesMonth;

        // Only talk about savings if at least one month had non-zero net
        if (currentNet != 0 || prevNet != 0) {
            if (prevNet != 0) {
                double savingsChange = (currentNet - prevNet) / Math.abs(prevNet);
                double savingsChangePercent = savingsChange * 100.0;

                if (savingsChange >= 0.2) {
                    recommendations.add(String.format(
                            "Your savings improved by about %.0f%% compared to last month ($%.2f vs $%.2f). " +
                                    "Keep it up!",
                            savingsChangePercent, currentNet, prevNet
                    ));
                } else if (savingsChange <= -0.2) {
                    recommendations.add(String.format(
                            "Your savings dropped by about %.0f%% compared to last month ($%.2f vs $%.2f). " +
                                    "Review your biggest expense categories to find where to cut back.",
                            Math.abs(savingsChangePercent), currentNet, prevNet
                    ));
                }
            } else if (currentNet > 0 && prevNet <= 0) {
                // Moved from not saving (zero or overspending) to saving
                recommendations.add(String.format(
                        "You moved from not saving to saving about $%.2f this month. Great progress!",
                        currentNet
                ));
            } else if (currentNet < 0) {
                // Overspending this month
                recommendations.add(String.format(
                        "You spent about $%.2f more than you earned this month. " +
                                "Try cutting some non-essential expenses next month.",
                        Math.abs(currentNet)
                ));
            }
        }

        // If still empty, add a generic helpful hint
        if (recommendations.isEmpty()) {
            recommendations.add("Your spending is quite similar to last month. " +
                    "Try setting specific budgets to optimize your savings further.");
        }

        return recommendations;
    }
}