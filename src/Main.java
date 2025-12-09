import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Main objects that the whole app uses
        Ledger ledger = new Ledger();
        Budget budget = new Budget();

        // Stack for undo and queue for future bills
        Stack<Transaction> undoStack = new Stack<>();
        Queue<Transaction> scheduledBills = new LinkedList<>();

        System.out.println("Welcome to the Personal Finance Budget App!");

        boolean running = true;

        while (running) {
            System.out.println("\nPlease choose an option:");
            System.out.println("1. Add a transaction");
            System.out.println("2. View all transactions");
            System.out.println("3. View budget summary (includes recursion)");
            System.out.println("4. Remove a transaction");
            System.out.println("5. Undo last added transaction");
            System.out.println("6. Schedule a future bill");
            System.out.println("7. Process scheduled bills");
            System.out.println("8. View monthly report (for one month)");
            System.out.println("9. View monthly totals (using arrays)");
            System.out.println("10. Save all data to file");
            System.out.println("11. Load data from file");
            System.out.println("12. Exit");
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
                    // Normal summary
                    budget.showSummary();
                    // Extra line using the recursive algorithm
                    double recursiveTotal =
                            budget.calculateTotalSpendingRecursive(ledger.getTransactions());
                    System.out.println("Total expenses (calculated with recursion): $" + recursiveTotal);
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
                    viewMonthlyReport(scanner, ledger, budget);
                    break;

                case "9":
                    budget.showAllMonthlyTotalsFromArrays();
                    break;

                case "10":
                    saveData(ledger);
                    break;

                case "11":
                    loadData(ledger, budget, undoStack);
                    break;

                case "12":
                    System.out.println("Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 12.");
            }
        }

        scanner.close();
    }

    // ================== ADD TRANSACTION ==================
    // This is where the user adds a new income or expense
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

        // Add to ledger and budget so everything stays in sync
        ledger.addTransaction(t);
        budget.addTransaction(t);

        // Push onto the undo stack so I can undo this later if needed
        undoStack.push(t);

        System.out.println("Transaction added.");
    }

    // ================== REMOVE TRANSACTION ==================
    // This lets the user remove a transaction by its index in the list
    private static void removeTransaction(Scanner scanner, Ledger ledger, Budget budget,
                                          Stack<Transaction> undoStack) {

        var transactions = ledger.getTransactions();

        if (transactions.isEmpty()) {
            System.out.println("No transactions to remove.");
            return;
        }

        System.out.println("\n--- Transaction List ---");
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
            // Roll back this transaction in the budget too
            budget.removeTransaction(removed);

            // If that same transaction is on top of the undo stack, remove it there as well
            if (!undoStack.isEmpty() && undoStack.peek() == removed) {
                undoStack.pop();
            }

            System.out.println("Transaction removed successfully.");
        }
    }

    // ================== UNDO LAST TRANSACTION (STACK) ==================
    // Uses the stack to undo whatever was added most recently
    private static void undoLastTransaction(Ledger ledger, Budget budget,
                                            Stack<Transaction> undoStack) {

        if (undoStack.isEmpty()) {
            System.out.println("Nothing to undo.");
            return;
        }

        Transaction last = undoStack.pop();
        boolean removedFromLedger = ledger.removeTransaction(last);

        if (removedFromLedger) {
            budget.removeTransaction(last);
            System.out.println("Last transaction undone.");
        } else {
            System.out.println("Undo failed, transaction not found in ledger.");
        }
    }

    // ================== SCHEDULE BILL (QUEUE) ==================
    // This does not hit the ledger or budget yet, it just puts the bill into the queue
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

    // ================== PROCESS SCHEDULED BILLS (QUEUE) ==================
    // This pulls bills from the queue in order and actually applies them to the budget
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

    // ================== VIEW MONTHLY REPORT (ONE MONTH) ==================
    // This asks for a month number and shows just that month's activity
    private static void viewMonthlyReport(Scanner scanner, Ledger ledger, Budget budget) {

        System.out.print("Enter month number (1 to 12): ");
        String input = scanner.nextLine();

        int month;
        try {
            month = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid month number.");
            return;
        }

        budget.showMonthlyReport(ledger.getTransactions(), month);
    }

    // ================== SAVE DATA TO FILE ==================
    // Saves all transactions into a text file so I can load them back later
    private static void saveData(Ledger ledger) {
        try {
            FileWriter writer = new FileWriter("budget_data.txt");

            // Each transaction is one line in the file
            for (Transaction t : ledger.getTransactions()) {
                // I split values by commas. In a real app I would probably avoid commas in description.
                writer.write(t.getCategory() + ","
                             + t.getAmount() + ","
                             + t.getDescription() + ","
                             + t.getDate() + "\n");
            }

            writer.close();
            System.out.println("Your data was saved to budget_data.txt.");
        } catch (IOException e) {
            System.out.println("Something went wrong while saving the file.");
        }
    }

    // ================== LOAD DATA FROM FILE ==================
    // Reads the text file and rebuilds ledger, budget, and undo stack
    private static void loadData(Ledger ledger, Budget budget, Stack<Transaction> undoStack) {
        try {
            File file = new File("budget_data.txt");

            if (!file.exists()) {
                System.out.println("No saved data found. The file does not exist yet.");
                return;
            }

            Scanner fileReader = new Scanner(file);
            LinkedList<Transaction> newList = new LinkedList<>();

            // Each line should look like: category,amount,description,date
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                String[] parts = line.split(",");

                if (parts.length == 4) {
                    String category = parts[0];
                    double amount = Double.parseDouble(parts[1]);
                    String description = parts[2];
                    String date = parts[3];

                    Transaction t = new Transaction(category, amount, description, date);
                    newList.add(t);
                }
            }

            fileReader.close();

            // Replace everything in the ledger with what I just loaded
            ledger.setTransactions(newList);

            // Reset the budget and undo stack, then rebuild them from the loaded list
            budget.resetAll();
            undoStack.clear();

            for (Transaction t : newList) {
                budget.addTransaction(t);
                undoStack.push(t);
            }

            System.out.println("Your saved data was successfully loaded.");

        } catch (Exception e) {
            System.out.println("Something went wrong while loading your data.");
        }
    }
}
