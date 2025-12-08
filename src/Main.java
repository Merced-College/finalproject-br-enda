import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Ledger ledger = new Ledger();
        Budget budget = new Budget();

        System.out.println("Welcome to the Personal Finance Budget App!");

        boolean running = true;

        while (running) {
            System.out.println("\nPlease choose an option:");
            System.out.println("1. Add a transaction");
            System.out.println("2. View all transactions");
            System.out.println("3. View budget summary");
            System.out.println("4. Remove a transaction");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addTransaction(scanner, ledger, budget);
                    break;
                case "2":
                    ledger.showAllTransactions();
                    break;
                case "3":
                    budget.showSummary();
                    break;
                case "4":
                    removeTransaction(scanner, ledger, budget);
                    break;
                case "5":
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, 3, 4, or 5.");
            }
        }

        scanner.close();
    }

    // ---------- helper methods below must be INSIDE the Main class ----------

    // Add a transaction
    private static void addTransaction(Scanner scanner, Ledger ledger, Budget budget) {
        System.out.print("Enter category (e.g. Food, Bills, Income): ");
        String category = scanner.nextLine();

        System.out.print("Enter amount (use negative for expenses, positive for income): ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Transaction cancelled.");
            return;
        }

        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        System.out.print("Enter date (e.g. 11/03/2025): ");
        String date = scanner.nextLine();

        Transaction t = new Transaction(category, amount, description, date);
        ledger.addTransaction(t);
        budget.addTransaction(t);

        System.out.println("Transaction added to ledger and budget.");
    }

    // Remove a transaction
    private static void removeTransaction(Scanner scanner, Ledger ledger, Budget budget) {
        System.out.println("\n--- Remove a Transaction ---");

        var transactions = ledger.getTransactions();

        if (transactions.isEmpty()) {
            System.out.println("There are no transactions to remove.");
            return;
        }

        // Show transactions with index numbers
        for (int i = 0; i < transactions.size(); i++) {
            System.out.println(i + ": " + transactions.get(i));
        }

        System.out.print("Enter the number of the transaction to remove: ");
        String input = scanner.nextLine();

        int index;
        try {
            index = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. No transaction removed.");
            return;
        }

        Transaction removed = ledger.removeTransaction(index);
        if (removed != null) {
            budget.removeTransaction(removed);
            System.out.println("Budget updated after removal.");
        }
    }
}
