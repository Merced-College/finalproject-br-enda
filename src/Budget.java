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

    // Show basic summary using the running totals
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

    // Public method you can call from Main
    public double calculateTotalSpendingRecursive(LinkedList<Transaction> transactions) {
        return sumExpensesRecursive(transactions, 0);
    }

    // Private helper that actually uses recursion
    private double sumExpensesRecursive(LinkedList<Transaction> list, int index) {
        // Base case: no more items in the list
        if (index == list.size()) {
            return 0.0;
        }

        Transaction current = list.get(index);
        double thisAmount = 0.0;

        // Only count expenses (negative amounts)
        if (current.getAmount() < 0) {
            thisAmount = Math.abs(current.getAmount());
        }

        // Recursive call: move to the next index
        return thisAmount + sumExpensesRecursive(list, index + 1);
    }
}
