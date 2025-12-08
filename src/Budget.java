import java.util.HashMap;
import java.util.LinkedList;

public class Budget {

    private double totalIncome;
    private double totalExpenses;
    private HashMap<String, Double> categoryTotals;

    public Budget() {
        totalIncome = 0.0;
        totalExpenses = 0.0;
        categoryTotals = new HashMap<>();
    }

    // Add a transaction and update totals
    public void addTransaction(Transaction t) {
        double amount = t.getAmount();
        String category = t.getCategory();

        if (amount > 0) {
            totalIncome += amount;
        } else {
            totalExpenses += Math.abs(amount);
        }

        double current = categoryTotals.getOrDefault(category, 0.0);
        categoryTotals.put(category, current + Math.abs(amount));
    }

    // Remove a transaction and roll back totals
    public void removeTransaction(Transaction t) {
        if (t == null) {
            return;
        }

        double amount = t.getAmount();
        String category = t.getCategory();

        if (amount > 0) {
            totalIncome -= amount;
        } else {
            totalExpenses -= Math.abs(amount);
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

    // Overall summary
    public void showSummary() {
        System.out.println("\n--- Budget Summary ---");
        System.out.println("Total Income: $" + totalIncome);
        System.out.println("Total Expenses: $" + totalExpenses);
        System.out.println("Net Balance: $" + (totalIncome - totalExpenses));

        System.out.println("\nBy Category:");
        for (String category : categoryTotals.keySet()) {
            System.out.println("- " + category + ": $" + categoryTotals.get(category));
        }
    }

    // ================= Recursive total spending method =================

    // Public method called from Main
    public double calculateTotalSpendingRecursive(LinkedList<Transaction> transactions) {
        return sumExpensesRecursive(transactions, 0);
    }

    // Private helper that actually uses recursion
    private double sumExpensesRecursive(LinkedList<Transaction> list, int index) {
        // Base case
        if (index == list.size()) {
            return 0.0;
        }

        Transaction current = list.get(index);
        double thisAmount = 0.0;

        // Only count expenses (negative amounts)
        if (current.getAmount() < 0) {
            thisAmount = Math.abs(current.getAmount());
        }

        // Recursive step
        return thisAmount + sumExpensesRecursive(list, index + 1);
    }

    // ================= Monthly report method =================

    // monthNumber should be 1 to 12
    public void showMonthlyReport(LinkedList<Transaction> transactions, int monthNumber) {
        if (monthNumber < 1 || monthNumber > 12) {
            System.out.println("Invalid month number.");
            return;
        }

        double monthIncome = 0.0;
        double monthExpenses = 0.0;
        HashMap<String, Double> monthCategoryTotals = new HashMap<>();

        System.out.println("\n--- Monthly Report for Month " + monthNumber + " ---");

        for (Transaction t : transactions) {
            String date = t.getDate();
            int monthFromDate = extractMonthFromDate(date);
            if (monthFromDate == monthNumber) {
                double amount = t.getAmount();
                String category = t.getCategory();

                // Show the transaction
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

        System.out.println("\nMonthly Income: $" + monthIncome);
        System.out.println("Monthly Expenses: $" + monthExpenses);
        System.out.println("Monthly Net: $" + (monthIncome - monthExpenses));

        System.out.println("\nMonthly totals by category:");
        if (monthCategoryTotals.isEmpty()) {
            System.out.println("No transactions found for this month.");
        } else {
            for (String category : monthCategoryTotals.keySet()) {
                System.out.println("- " + category + ": $" + monthCategoryTotals.get(category));
            }
        }
    }

    // Helper to parse month from date string like "11/03/2025" or "1/03/2025"
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
