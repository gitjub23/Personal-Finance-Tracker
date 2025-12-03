package com.example.financeapp.controllers;

import com.example.financeapp.models.AnalyticsService;
import com.example.financeapp.models.Transaction;
import com.example.financeapp.models.TransactionManager;
import com.example.financeapp.navigation.SceneManager;
import com.example.financeapp.session.Session;
import com.example.financeapp.models.User;
import com.example.financeapp.util.CurrencyUtil;
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

        // Sort by ABS(amount) desc and take top 5
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
        double upper = (maxValue <= 0) ? 10 : maxValue * 1.2;
        yAxis.setUpperBound(upper);
        yAxis.setTickUnit(Math.max(1, upper / 5));
    }

    // ================= SMART INSIGHTS (APP-ONLY) =================
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
                    "-fx-background-color: rgba(0,0,0,0.04);" +  // light grey card
                            "-fx-padding: 8;" +
                            "-fx-background-radius: 8;" +
                            "-fx-font-size: 12px;" +
                            "-fx-text-fill: #333333;"                    // dark text
            );
            recommendationsContainer.getChildren().add(card);
        }
    }

    // ================= EXPORTS =================

    private void handleExportPdf() {
        // NOTE: requires OpenPDF (com.github.librepdf:openpdf).

        javafx.stage.FileChooser chooser = new javafx.stage.FileChooser();
        chooser.setTitle("Export Report as PDF");
        chooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        java.io.File file = chooser.showSaveDialog(null);
        if (file == null) return;

        try {
            YearMonth currentMonth = YearMonth.now();
            String monthStr = currentMonth.format(MONTH_FORMAT);

            double income = transactionManager.getTotalIncomeForMonth(currentUser.getId(), currentMonth);
            double expenses = transactionManager.getTotalExpenseForMonth(currentUser.getId(), currentMonth);
            double balance = income + expenses; // expenses are negative

            // Transactions for current month
            List<Transaction> allTx = transactionManager.getTransactionsForUser(currentUser.getId());
            List<Transaction> monthTx = allTx.stream()
                    .filter(t -> t.getDate() != null &&
                            YearMonth.from(t.getDate()).equals(currentMonth))
                    .collect(Collectors.toList());

            String symbol = CurrencyUtil.getSymbol(currentUser.getCurrencyCode());

            com.lowagie.text.Document document = new com.lowagie.text.Document();
            com.lowagie.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(file));
            document.open();

            document.add(new com.lowagie.text.Paragraph("Finance Report - " + monthStr));
            document.add(new com.lowagie.text.Paragraph("User: " + currentUser.getUsername()));
            document.add(new com.lowagie.text.Paragraph(" "));

            document.add(new com.lowagie.text.Paragraph(String.format("Income: %s%.2f", symbol, income)));
            document.add(new com.lowagie.text.Paragraph(String.format("Expenses: %s%.2f", symbol, expenses)));
            document.add(new com.lowagie.text.Paragraph(String.format("Balance: %s%.2f", symbol, balance)));
            document.add(new com.lowagie.text.Paragraph(" "));

            // Transactions table (only)
            document.add(new com.lowagie.text.Paragraph("Transactions (" + monthStr + "):"));
            document.add(new com.lowagie.text.Paragraph(" "));

            if (monthTx.isEmpty()) {
                document.add(new com.lowagie.text.Paragraph("No transactions for this month."));
            } else {
                com.lowagie.text.pdf.PdfPTable table =
                        new com.lowagie.text.pdf.PdfPTable(4); // Date, Title, Category, Amount
                table.setWidthPercentage(100);

                table.addCell("Date");
                table.addCell("Title");
                table.addCell("Category");
                table.addCell("Amount");

                for (Transaction t : monthTx) {
                    table.addCell(t.getDate() != null ? t.getDate().toString() : "");
                    String title = (t.getTitle() == null || t.getTitle().isBlank())
                            ? t.getCategory()
                            : t.getTitle();
                    table.addCell(title != null ? title : "");
                    table.addCell(t.getCategory() != null ? t.getCategory() : "");
                    String amt = String.format("%s%.2f", symbol, t.getAmount());
                    table.addCell(amt);
                }

                document.add(table);
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

            // Transactions for current month
            List<Transaction> allTx = transactionManager.getTransactionsForUser(currentUser.getId());
            List<Transaction> monthTx = allTx.stream()
                    .filter(t -> t.getDate() != null &&
                            YearMonth.from(t.getDate()).equals(currentMonth))
                    .collect(Collectors.toList());

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

            // Transactions sheet
            org.apache.poi.ss.usermodel.Sheet txSheet = workbook.createSheet("Transactions");
            org.apache.poi.ss.usermodel.Row txHeader = txSheet.createRow(0);
            txHeader.createCell(0).setCellValue("Date");
            txHeader.createCell(1).setCellValue("Title");
            txHeader.createCell(2).setCellValue("Category");
            txHeader.createCell(3).setCellValue("Amount");

            int txRowIndex = 1;
            for (Transaction t : monthTx) {
                org.apache.poi.ss.usermodel.Row row = txSheet.createRow(txRowIndex++);

                row.createCell(0).setCellValue(
                        t.getDate() != null ? t.getDate().toString() : "");

                String title = (t.getTitle() == null || t.getTitle().isBlank())
                        ? t.getCategory()
                        : t.getTitle();
                row.createCell(1).setCellValue(title != null ? title : "");
                row.createCell(2).setCellValue(t.getCategory() != null ? t.getCategory() : "");
                row.createCell(3).setCellValue(t.getAmount()); // numeric
            }

            // Autosize columns
            for (org.apache.poi.ss.usermodel.Sheet sheet : new org.apache.poi.ss.usermodel.Sheet[]{summarySheet, txSheet}) {
                int cols = sheet.getRow(0) != null ? sheet.getRow(0).getPhysicalNumberOfCells() : 0;
                for (int c = 0; c < cols; c++) {
                    sheet.autoSizeColumn(c);
                }
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