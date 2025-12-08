import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Ledger ledger = new Ledger();
        Budget budget = new Budget();

        Stack<Transaction> undoStack = new Stack<>();
        Queue<Transaction> scheduledBills = new LinkedList<>();

        System.out.println("Welcome to the Personal Finance Budget App!");

        boolean running = true;

        while (running) {
            System.out.println("\nPlease choose an option:");
            System.out.println("1. Add a transaction");
            System.out.println("2. View all transactions");
            System.out.println("3. View budget summary");
            System.out.println("4. Remove a transaction");
            System.out.println("5. Undo last added transaction");
            System.out.println("6. Schedule a future bill");
            System.out.println("7. Process scheduled bills");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addTransaction(scanner, ledger, budget, undoStack);
                    break;
                case "2":
                    ledger.showAllTransactions();
                    break;
                case "3":
                    budget.showSummary();
                    break;
                case "4":
                    removeTransaction(scanner, ledger, budget, undoStack);
                    break;
                case "5":
                    undoLastTransaction(ledger, budget, undoStack);
                    break;
                case "6":
                    scheduleBill(scanner, scheduledBills);
                    break;
                case "7":
                    processScheduledBills(ledger, budget, scheduledBills, undoStack);
                    break;
                case "8":
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1–8.");
            }
        }

        scanner.close();
    }

    // -------------------- Helper Methods --------------------

    // Add a transaction
    private static void addTransaction(Scanner scanner, Ledger ledger, Budget budget,
                                       Stack<Transaction> undoStack) {

        System.out.print("Enter category (e.g. Food, Bills, Income): ");
        String category = scanner.nextLine();

        System.out.print("Enter amount (negative for expense, positive for income): ");
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
        undoStack.push(t);

        System.out.println("Transaction added.");
    }

    // Remove by index
    private static void removeTransaction(Scanner scanner, Ledger ledger, Budget budget,
                                          Stack<Transaction> undoStack) {

        var list = ledger.getTransactions();

        if (list.isEmpty()) {
            System.out.println("No transactions to remove.");
            return;
        }

        System.out.println("\n--- Transaction List ---");
        for (int i = 0; i < list.size(); i++) {
            System.out.println(i + ": " + list.get(i));
        }

        System.out.print("Enter the number of the transaction to remove: ");
        String input = scanner.nextLine();

        int index;
        try {
            index = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return;
        }

        Transaction removed = ledger.removeTransaction(index);
        if (removed != null) {
            budget.removeTransaction(removed);

            // Clean undo stack if necessary
            if (!undoStack.isEmpty() && undoStack.peek() == removed) {
                undoStack.pop();
            }

            System.out.println("Transaction removed successfully.");
        }
    }

    // Undo using Stack
    private static void undoLastTransaction(Ledger ledger, Budget budget,
                                            Stack<Transaction> undoStack) {

        if (undoStack.isEmpty()) {
            System.out.println("Nothing to undo.");
            return;
        }

        Transaction last = undoStack.pop();
        boolean removed = ledger.removeTransaction(last);

        if (removed) {
            budget.removeTransaction(last);
            System.out.println("Last transaction undone.");
        } else {
            System.out.println("Undo failed — transaction not found.");
        }
    }

    // Queue: schedule a future bill
    private static void scheduleBill(Scanner scanner, Queue<Transaction> scheduledBills) {

        System.out.print("Enter category for future bill: ");
        String category = scanner.nextLine();

        System.out.print("Enter amount (negative for expense): ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Bill not scheduled.");
            return;
        }

        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        System.out.print("Enter due date: ");
        String date = scanner.nextLine();

        Transaction t = new Transaction(category, amount, description, date);
        scheduledBills.add(t);
        System.out.println("Future bill scheduled.");
    }

    // Process all bills in FIFO order
    private static void processScheduledBills(Ledger ledger, Budget budget,
                                              Queue<Transaction> scheduledBills,
                                              Stack<Transaction> undoStack) {

        if (scheduledBills.isEmpty()) {
            System.out.println("No scheduled bills to process.");
            return;
        }

        System.out.println("\nProcessing scheduled bills...");

        while (!scheduledBills.isEmpty()) {
            Transaction t = scheduledBills.poll();
            ledger.addTransaction(t);
            budget.addTransaction(t);
            undoStack.push(t);

            System.out.println("Processed: " + t);
        }

        System.out.println("All scheduled bills processed.");
    }
}
