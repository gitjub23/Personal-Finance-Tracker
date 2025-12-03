package com.example.financeapp.controllers;

import com.example.financeapp.models.AnalyticsService;
import com.example.financeapp.models.TransactionManager;
import com.example.financeapp.navigation.SceneManager;
import com.example.financeapp.session.Session;
import com.example.financeapp.models.User;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportsController {

    @FXML private LineChart<String, Number> incomeExpenseChart;
    @FXML private BarChart<String, Number> topCategoriesChart;

    @FXML private Label periodLabel;
    @FXML private Label monthLabel;
    @FXML private Label infoLabel;

    @FXML private VBox recommendationsContainer;

    @FXML private Button exportPdfButton;
    @FXML private Button exportExcelButton;

    private final TransactionManager transactionManager = new TransactionManager();
    private final AnalyticsService analyticsService = new AnalyticsService(transactionManager);

    private User currentUser;

    private static final DateTimeFormatter MONTH_FORMAT =
            DateTimeFormatter.ofPattern("MMM yyyy");

    @FXML
    private void initialize() {

        // Session check
        if (!Session.isLoggedIn() || Session.getCurrentUser() == null) {
            SceneManager.switchTo("LoginView");
            return;
        }
        currentUser = Session.getCurrentUser();

        periodLabel.setText("Last 6 months overview");

        // Wire exports
        exportPdfButton.setOnAction(e -> handleExportPdf());
        exportExcelButton.setOnAction(e -> handleExportExcel());

        loadIncomeExpenseTrend(6);
        loadTopCategoriesForCurrentMonth();
        loadRecommendations();
    }

    // ================= INCOME vs EXPENSE TREND =================
    private void loadIncomeExpenseTrend(int monthsBack) {
        incomeExpenseChart.getData().clear();

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expense");

        YearMonth now = YearMonth.now();

        // oldest → newest
        for (int i = monthsBack - 1; i >= 0; i--) {
            YearMonth month = now.minusMonths(i);
            String label = month.format(MONTH_FORMAT);

            double income = transactionManager
                    .getTotalIncomeForMonth(currentUser.getId(), month);
            double expenses = transactionManager
                    .getTotalExpenseForMonth(currentUser.getId(), month);

            incomeSeries.getData().add(new XYChart.Data<>(label, income));
            expenseSeries.getData().add(new XYChart.Data<>(label, expenses));
        }

        incomeExpenseChart.getData().addAll(incomeSeries, expenseSeries);
    }

    // ================= TOP EXPENSE CATEGORIES (THIS MONTH) =================
    private void loadTopCategoriesForCurrentMonth() {
        topCategoriesChart.getData().clear();

        YearMonth currentMonth = YearMonth.now();
        monthLabel.setText("Month: " + currentMonth.format(MONTH_FORMAT));

        // false => expenses
        Map<String, Double> raw = transactionManager
                .getCategoryTotalsForMonth(currentUser.getId(), currentMonth, false);

        if (raw.isEmpty()) {
            infoLabel.setText("No expenses recorded for this month yet.");
            infoLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");
            return;
        } else {
            infoLabel.setText("");
        }

        // Sort by ABS(amount) desc and take top 5 (largest spenders)
        List<Map.Entry<String, Double>> top =
                raw.entrySet().stream()
                        .sorted((a, b) -> Double.compare(
                                Math.abs(b.getValue()),
                                Math.abs(a.getValue())))
                        .limit(5)
                        .collect(Collectors.toList());

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Expenses");

        double maxValue = 0;

        // Use positive values so bars grow UP from 0
        for (Map.Entry<String, Double> entry : top) {
            String category = entry.getKey();
            double rawTotal = entry.getValue();      // negative in DB
            double value = Math.abs(rawTotal);       // positive height

            series.getData().add(new XYChart.Data<>(category, value));

            if (value > maxValue) {
                maxValue = value;
            }
        }

        topCategoriesChart.getData().add(series);

        // Configure Y axis to go from 0 upwards
        NumberAxis yAxis = (NumberAxis) topCategoriesChart.getYAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        // Add a little headroom above the tallest bar
        double upper = (maxValue <= 0) ? 10 : maxValue * 1.2;
        yAxis.setUpperBound(upper);
        yAxis.setTickUnit(Math.max(1, upper / 5));
    }

    // ================= SMART INSIGHTS / RECOMMENDATIONS =================
    private void loadRecommendations() {
        recommendationsContainer.getChildren().clear();

        YearMonth currentMonth = YearMonth.now();
        List<String> recs =
                analyticsService.generateMonthlyRecommendations(currentUser.getId(), currentMonth);

        if (recs.isEmpty()) {
            Label l = new Label("No insights available yet.");
            l.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");
            recommendationsContainer.getChildren().add(l);
            return;
        }

        for (String rec : recs) {
            Label card = new Label(rec);
            card.setWrapText(true);
            card.setStyle(
                    "-fx-background-color: rgba(255,255,255,0.1);" +
                            "-fx-padding: 8;" +
                            "-fx-background-radius: 8;" +
                            "-fx-font-size: 12px;" +
                            "-fx-text-fill: #ffffff;"
            );
            recommendationsContainer.getChildren().add(card);
        }
    }

    // ================= EXPORTS =================

    private void handleExportPdf() {
        // NOTE: requires a PDF library like OpenPDF or iText.
        // Example uses OpenPDF (com.github.librepdf:openpdf).

        javafx.stage.FileChooser chooser = new javafx.stage.FileChooser();
        chooser.setTitle("Export Report as PDF");
        chooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        java.io.File file = chooser.showSaveDialog(null);
        if (file == null) return;

        try {
            // Collect some basic data
            YearMonth currentMonth = YearMonth.now();
            String monthStr = currentMonth.format(MONTH_FORMAT);

            double income = transactionManager.getTotalIncomeForMonth(currentUser.getId(), currentMonth);
            double expenses = transactionManager.getTotalExpenseForMonth(currentUser.getId(), currentMonth);
            double balance = income + expenses; // expenses are negative

            java.util.List<String> recs =
                    analyticsService.generateMonthlyRecommendations(currentUser.getId(), currentMonth);

            // ---- OpenPDF example ----
            com.lowagie.text.Document document = new com.lowagie.text.Document();
            com.lowagie.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(file));
            document.open();

            document.add(new com.lowagie.text.Paragraph("Finance Report - " + monthStr));
            document.add(new com.lowagie.text.Paragraph("User: " + currentUser.getUsername()));
            document.add(new com.lowagie.text.Paragraph(" "));
            document.add(new com.lowagie.text.Paragraph(String.format("Income: %.2f", income)));
            document.add(new com.lowagie.text.Paragraph(String.format("Expenses: %.2f", expenses)));
            document.add(new com.lowagie.text.Paragraph(String.format("Balance: %.2f", balance)));
            document.add(new com.lowagie.text.Paragraph(" "));

            document.add(new com.lowagie.text.Paragraph("Smart Insights:"));
            for (String rec : recs) {
                document.add(new com.lowagie.text.Paragraph("• " + rec));
            }

            document.close();

            infoLabel.setText("Exported PDF to: " + file.getAbsolutePath());
            infoLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-size: 12px;");

        } catch (Exception ex) {
            ex.printStackTrace();
            infoLabel.setText("Failed to export PDF: " + ex.getMessage());
            infoLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        }
    }

    private void handleExportExcel() {
        // NOTE: requires Apache POI: org.apache.poi:poi-ooxml

        javafx.stage.FileChooser chooser = new javafx.stage.FileChooser();
        chooser.setTitle("Export Report as Excel");
        chooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        java.io.File file = chooser.showSaveDialog(null);
        if (file == null) return;

        try {
            YearMonth currentMonth = YearMonth.now();
            String monthStr = currentMonth.format(MONTH_FORMAT);

            double income = transactionManager.getTotalIncomeForMonth(currentUser.getId(), currentMonth);
            double expenses = transactionManager.getTotalExpenseForMonth(currentUser.getId(), currentMonth);
            double balance = income + expenses; // expenses are negative

            java.util.List<String> recs =
                    analyticsService.generateMonthlyRecommendations(currentUser.getId(), currentMonth);

            org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();

            // Summary sheet
            org.apache.poi.ss.usermodel.Sheet summarySheet = workbook.createSheet("Summary");
            org.apache.poi.ss.usermodel.Row r0 = summarySheet.createRow(0);
            r0.createCell(0).setCellValue("Finance Report - " + monthStr);
            org.apache.poi.ss.usermodel.Row r1 = summarySheet.createRow(2);
            r1.createCell(0).setCellValue("Income");
            r1.createCell(1).setCellValue(income);
            org.apache.poi.ss.usermodel.Row r2 = summarySheet.createRow(3);
            r2.createCell(0).setCellValue("Expenses");
            r2.createCell(1).setCellValue(expenses);
            org.apache.poi.ss.usermodel.Row r3 = summarySheet.createRow(4);
            r3.createCell(0).setCellValue("Balance");
            r3.createCell(1).setCellValue(balance);

            // Insights sheet
            org.apache.poi.ss.usermodel.Sheet insightsSheet = workbook.createSheet("Insights");
            org.apache.poi.ss.usermodel.Row header = insightsSheet.createRow(0);
            header.createCell(0).setCellValue("Smart Insights");
            int rowIndex = 1;
            for (String rec : recs) {
                org.apache.poi.ss.usermodel.Row row = insightsSheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(rec);
            }

            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
                workbook.write(fos);
            }
            workbook.close();

            infoLabel.setText("Exported Excel to: " + file.getAbsolutePath());
            infoLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-size: 12px;");

        } catch (Exception ex) {
            ex.printStackTrace();
            infoLabel.setText("Failed to export Excel: " + ex.getMessage());
            infoLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        }
    }
}