package com.example.financeapp.models;

import java.time.YearMonth;
import java.util.*;

public class AnalyticsService {

    private final TransactionManager transactionManager;

    // Tunable thresholds
    private static final double CATEGORY_CHANGE_THRESHOLD = 0.20; // 20%
    private static final double SAVINGS_CHANGE_THRESHOLD  = 0.20; // 20%
    private static final double OVERALL_SPEND_THRESHOLD   = 0.10; // 10%
    private static final double TOP_CATEGORY_SHARE_MIN    = 0.15; // 15% of total spend
    private static final double MIN_CATEGORY_AMOUNT       = 10.0; // ignore tiny stuff

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

        // Convert to absolute (positive) amounts for "how much you spent"
        Map<String, Double> currentAbs = toPositive(currentExpensesSigned);
        Map<String, Double> prevAbs    = toPositive(prevExpensesSigned);

        // 1) Category-level comparison (current vs last month)
        addCategoryChangeInsights(recommendations, currentAbs, prevAbs);

        // 2) Overall spending trend (total expenses this month vs last)
        addOverallSpendingInsight(recommendations, currentAbs, prevAbs);

        // 3) Overall savings insight (net)
        addSavingsInsights(recommendations, userId, currentMonth, prevMonth);

        // 4) Top category share (which categories dominate)
        addTopCategoryShareInsights(recommendations, currentAbs);

        // If still empty, add a generic helpful hint
        if (recommendations.isEmpty()) {
            recommendations.add("Your spending is quite similar to last month. " +
                    "Try setting specific budgets to optimize your savings further.");
        }

        return recommendations;
    }

    // =========================================================
    // 1) Category-level comparisons
    // =========================================================

    private void addCategoryChangeInsights(List<String> recommendations,
                                           Map<String, Double> currentAbs,
                                           Map<String, Double> prevAbs) {

        Set<String> allCategories = new HashSet<>();
        allCategories.addAll(currentAbs.keySet());
        allCategories.addAll(prevAbs.keySet());

        for (String category : allCategories) {
            double curr = currentAbs.getOrDefault(category, 0.0);
            double prev = prevAbs.getOrDefault(category, 0.0);

            // Ignore very small categories to avoid noisy messages
            if (curr < MIN_CATEGORY_AMOUNT && prev < MIN_CATEGORY_AMOUNT) {
                continue;
            }

            if (prev > 0 && curr > 0) {
                double changeRatio = (curr - prev) / prev; // >0 means increased
                double changePercent = changeRatio * 100.0;

                if (changeRatio >= CATEGORY_CHANGE_THRESHOLD) {
                    recommendations.add(String.format(
                            "You spent %.0f%% more on %s than last month ($%.2f vs $%.2f). " +
                                    "Consider reducing this category by around 10%% next month.",
                            changePercent, category, curr, prev
                    ));
                } else if (changeRatio <= -CATEGORY_CHANGE_THRESHOLD) {
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
    }

    // =========================================================
    // 2) Overall spending trend (total expenses)
    // =========================================================

    private void addOverallSpendingInsight(List<String> recommendations,
                                           Map<String, Double> currentAbs,
                                           Map<String, Double> prevAbs) {

        double totalCurrent = currentAbs.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalPrev    = prevAbs.values().stream().mapToDouble(Double::doubleValue).sum();

        if (totalPrev <= 0 || totalCurrent <= 0) {
            return;
        }

        double diff = totalCurrent - totalPrev;
        double changeRatio = diff / totalPrev;
        double changePercent = changeRatio * 100.0;

        if (Math.abs(changeRatio) >= OVERALL_SPEND_THRESHOLD) { // >10%
            if (changeRatio > 0) {
                recommendations.add(String.format(
                        "Overall, you spent %.0f%% more than last month ($%.2f vs $%.2f).",
                        changePercent, totalCurrent, totalPrev
                ));
            } else {
                recommendations.add(String.format(
                        "Overall, you spent %.0f%% less than last month ($%.2f vs $%.2f). Nice progress!",
                        Math.abs(changePercent), totalCurrent, totalPrev
                ));
            }
        }
    }

    // =========================================================
    // 3) Savings / net insight (your original logic, extended)
    // =========================================================

    private void addSavingsInsights(List<String> recommendations,
                                    int userId,
                                    YearMonth currentMonth,
                                    YearMonth prevMonth) {

        double currentIncome = transactionManager.getTotalIncomeForMonth(userId, currentMonth);
        double prevIncome    = transactionManager.getTotalIncomeForMonth(userId, prevMonth);

        double currentExpensesMonth = transactionManager.getTotalExpenseForMonth(userId, currentMonth); // negative
        double prevExpensesMonth    = transactionManager.getTotalExpenseForMonth(userId, prevMonth);    // negative

        // Net savings / balance for the month (same rule as dashboard & PDF)
        double currentNet = currentIncome + currentExpensesMonth;
        double prevNet    = prevIncome + prevExpensesMonth;

        // Savings rate for current month (if income > 0)
        if (currentIncome > 0) {
            double savingsRate = currentNet / currentIncome; // can be negative
            if (savingsRate < 0) {
                recommendations.add(String.format(
                        "You spent more than you earned this month (net -$%.2f). " +
                                "Try cutting non-essential expenses or increasing income.",
                        Math.abs(currentNet)
                ));
            } else if (savingsRate < 0.10) {
                recommendations.add(String.format(
                        "You saved about %.0f%% of your income this month. " +
                                "Consider aiming for at least 10%% savings if possible.",
                        savingsRate * 100
                ));
            } else {
                recommendations.add(String.format(
                        "Great job! You saved about %.0f%% of your income this month.",
                        savingsRate * 100
                ));
            }
        } else if (currentNet > 0) {
            // No recorded income but positive net? Maybe only some transactions are recorded
            recommendations.add(String.format(
                    "You recorded a positive net balance of $%.2f this month. " +
                            "Make sure your income is also tracked for a full picture.",
                    currentNet
            ));
        }

        // Only talk about month-over-month savings if at least one month had non-zero net
        if (currentNet != 0 || prevNet != 0) {
            if (prevNet != 0) {
                double savingsChange = (currentNet - prevNet) / Math.abs(prevNet);
                double savingsChangePercent = savingsChange * 100.0;

                if (savingsChange >= SAVINGS_CHANGE_THRESHOLD) {
                    recommendations.add(String.format(
                            "Your savings improved by about %.0f%% compared to last month ($%.2f vs $%.2f). " +
                                    "Keep it up!",
                            savingsChangePercent, currentNet, prevNet
                    ));
                } else if (savingsChange <= -SAVINGS_CHANGE_THRESHOLD) {
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
            } else if (currentNet < 0 && prevNet >= 0) {
                // Was saving / breaking even, now overspending
                recommendations.add(String.format(
                        "You went from saving or breaking even to overspending about $%.2f this month. " +
                                "Try to identify 1–2 categories to reduce next month.",
                        Math.abs(currentNet)
                ));
            }
        }
    }

    // =========================================================
    // 4) Top category share insights
    // =========================================================

    private void addTopCategoryShareInsights(List<String> recommendations,
                                             Map<String, Double> currentAbs) {

        if (currentAbs.isEmpty()) return;

        double total = currentAbs.values().stream().mapToDouble(Double::doubleValue).sum();
        if (total <= 0) return;

        // Sort categories by amount descending
        List<Map.Entry<String, Double>> sorted = new ArrayList<>(currentAbs.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // Look at top 3 categories
        int limit = Math.min(3, sorted.size());
        for (int i = 0; i < limit; i++) {
            Map.Entry<String, Double> e = sorted.get(i);
            String category = e.getKey();
            double amt = e.getValue();
            double share = amt / total;

            if (amt >= MIN_CATEGORY_AMOUNT && share >= TOP_CATEGORY_SHARE_MIN) {
                recommendations.add(String.format(
                        "%s accounts for about %.0f%% of your total expenses this month ($%.2f). " +
                                "If this isn’t essential, consider setting a clear limit next month.",
                        category, share * 100, amt
                ));
            }
        }
    }

    // =========================================================
    // Helpers
    // =========================================================

    private Map<String, Double> toPositive(Map<String, Double> signedMap) {
        Map<String, Double> result = new HashMap<>();
        for (Map.Entry<String, Double> e : signedMap.entrySet()) {
            result.put(e.getKey(), Math.abs(e.getValue()));
        }
        return result;
    }
}