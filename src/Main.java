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
            System.out.println("4. Exit");
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
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, 3, or 4.");
            }
        }

        scanner.close();
    }

    // Helper method to add a transaction
    private static void addTransaction(Scanner scanner, Ledger ledger, Budget budget) {
        System.out.print("Enter category (e.g. Food, Bills, Income): ");
        String category = scanner.nextLine();

        System.out.print("Enter amount (use negative for expenses, positive for income): ");
        double amount = 0.0;
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
}
