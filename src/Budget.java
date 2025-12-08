import java.util.HashMap;

public class Budget {
    private double totalIncome;
    private double totalExpenses;
    private HashMap<String, Double> categoryTotals;

    public Budget() {
        totalIncome = 0.0;
        totalExpenses = 0.0;
        categoryTotals = new HashMap<>();
    }

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
}