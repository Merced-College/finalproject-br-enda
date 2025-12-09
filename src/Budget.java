import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

// This class handles all the money logic: totals, categories, monthly breakdowns, and recursion
public class Budget {

    private double totalIncome;
    private double totalExpenses;
    private HashMap<String, Double> categoryTotals;

    // Arrays to track income and expenses for each month (index 0 = Jan, 11 = Dec)
    private double[] monthlyIncome;
    private double[] monthlyExpenses;

    public Budget() {
        totalIncome = 0.0;
        totalExpenses = 0.0;
        categoryTotals = new HashMap<>();

        monthlyIncome = new double[12];
        monthlyExpenses = new double[12];
    }

    // I call this when I load from a file to reset everything
    public void resetAll() {
        totalIncome = 0.0;
        totalExpenses = 0.0;
        categoryTotals.clear();
        Arrays.fill(monthlyIncome, 0.0);
        Arrays.fill(monthlyExpenses, 0.0);
    }

    // Add a transaction and keep all totals updated
    public void addTransaction(Transaction t) {
        double amount = t.getAmount();
        String category = t.getCategory();
        int month = extractMonthFromDate(t.getDate());

        if (amount > 0) {
            totalIncome += amount;
            if (month >= 1 && month <= 12) {
                monthlyIncome[month - 1] += amount;
            }
        } else {
            double expense = Math.abs(amount);
            totalExpenses += expense;
            if (month >= 1 && month <= 12) {
                monthlyExpenses[month - 1] += expense;
            }
        }

        double current = categoryTotals.getOrDefault(category, 0.0);
        categoryTotals.put(category, current + Math.abs(amount));
    }

    // Undo a transaction inside the budget (used for remove and undo)
    public void removeTransaction(Transaction t) {
        if (t == null) {
            return;
        }

        double amount = t.getAmount();
        String category = t.getCategory();
        int month = extractMonthFromDate(t.getDate());

        if (amount > 0) {
            totalIncome -= amount;
            if (month >= 1 && month <= 12) {
                monthlyIncome[month - 1] -= amount;
            }
        } else {
            double expense = Math.abs(amount);
            totalExpenses -= expense;
            if (month >= 1 && month <= 12) {
                monthlyExpenses[month - 1] -= expense;
            }
        }

        Double current = categoryTotals.get(category);
        if (current != null) {
            double updated = current - Math.abs(amount);
            if (updated <= 0) {
                categoryTotals.remove(category);
            } else {
                categoryTotals.put(category, updated);
            }
        }
    }

    // Main summary for the whole budget, with a nicer layout
    public void showSummary() {
        System.out.println();
        System.out.println("=============== BUDGET SUMMARY ===============");
        System.out.printf("Total Income   : $%.2f%n", totalIncome);
        System.out.printf("Total Expenses : $%.2f%n", totalExpenses);
        System.out.printf("Net Balance    : $%.2f%n", (totalIncome - totalExpenses));
        System.out.println("----------------------------------------------");
        System.out.println("Category breakdown:");

        if (categoryTotals.isEmpty()) {
            System.out.println("No category data yet.");
        } else {
            for (String category : categoryTotals.keySet()) {
                double value = categoryTotals.get(category);
                System.out.printf("- %-12s : $%.2f%n", category, value);
            }
        }

        System.out.println("==============================================");
    }

    // Public method that I call from Main to use my recursive algorithm
    public double calculateTotalSpendingRecursive(LinkedList<Transaction> transactions) {
        return sumExpensesRecursive(transactions, 0);
    }

    // This is the actual recursive function that walks through the list
    private double sumExpensesRecursive(LinkedList<Transaction> list, int index) {
        // Base case: once index hits the end, I stop
        if (index == list.size()) {
            return 0.0;
        }

        Transaction current = list.get(index);
        double thisAmount = 0.0;

        // Only count expenses (negative amounts)
        if (current.getAmount() < 0) {
            thisAmount = Math.abs(current.getAmount());
        }

        // Recursive step: add this expense and move to the next one
        return thisAmount + sumExpensesRecursive(list, index + 1);
    }

    // This gives a more detailed report for a single month
    public void showMonthlyReport(LinkedList<Transaction> transactions, int monthNumber) {
        if (monthNumber < 1 || monthNumber > 12) {
            System.out.println("Invalid month number.");
            return;
        }

        double monthIncome = 0.0;
        double monthExpenses = 0.0;
        HashMap<String, Double> monthCategoryTotals = new HashMap<>();

        System.out.println();
        System.out.println("============== MONTH " + monthNumber + " REPORT ==============");

        for (Transaction t : transactions) {
            String date = t.getDate();
            int monthFromDate = extractMonthFromDate(date);

            if (monthFromDate == monthNumber) {
                double amount = t.getAmount();
                String category = t.getCategory();

                System.out.println(t);

                if (amount > 0) {
                    monthIncome += amount;
                } else {
                    monthExpenses += Math.abs(amount);
                }

                double current = monthCategoryTotals.getOrDefault(category, 0.0);
                monthCategoryTotals.put(category, current + Math.abs(amount));
            }
        }

        System.out.println("----------------------------------------------");
        System.out.printf("Monthly Income  : $%.2f%n", monthIncome);
        System.out.printf("Monthly Expenses: $%.2f%n", monthExpenses);
        System.out.printf("Monthly Net     : $%.2f%n", (monthIncome - monthExpenses));
        System.out.println("----------------------------------------------");
        System.out.println("Monthly totals by category:");

        if (monthCategoryTotals.isEmpty()) {
            System.out.println("No transactions found for this month.");
        } else {
            for (String category : monthCategoryTotals.keySet()) {
                double value = monthCategoryTotals.get(category);
                System.out.printf("- %-12s : $%.2f%n", category, value);
            }
        }

        System.out.println("==============================================");
    }

    // This prints out any month that actually has values in the arrays
    public void showAllMonthlyTotalsFromArrays() {
        System.out.println();
        System.out.println("=============== MONTHLY TOTALS ===============");
        boolean anyData = false;

        System.out.println("Month |   Income   |  Expenses  |   Net   ");
        System.out.println("------|------------|-----------|----------");

        for (int i = 0; i < 12; i++) {
            double income = monthlyIncome[i];
            double expenses = monthlyExpenses[i];

            if (income != 0.0 || expenses != 0.0) {
                anyData = true;
                int monthNumber = i + 1;
                double net = income - expenses;
                System.out.printf("%5d | %10.2f | %9.2f | %8.2f%n",
                                  monthNumber, income, expenses, net);
            }
        }

        if (!anyData) {
            System.out.println("No monthly data recorded yet.");
        }

        System.out.println("============================================");
    }

    // Reads a month from a date like "11/03/2025" or "1/03/2025"
    private int extractMonthFromDate(String date) {
        if (date == null || date.isEmpty()) {
            return -1;
        }

        int slashIndex = date.indexOf('/');
        if (slashIndex <= 0) {
            return -1;
        }

        String monthPart = date.substring(0, slashIndex);
        try {
            return Integer.parseInt(monthPart);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
