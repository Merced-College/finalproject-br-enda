import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

public class Main {

    // Color codes to make the console a little less boring
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Ledger ledger = new Ledger();
        Budget budget = new Budget();

        Stack<Transaction> undoStack = new Stack<>();
        Queue<Transaction> scheduledBills = new LinkedList<>();

        // App header
        System.out.println(CYAN + "=========================================" + RESET);
        System.out.println(CYAN + "        Personal Finance Budget App      " + RESET);
        System.out.println(CYAN + "=========================================" + RESET);

        boolean running = true;

        // Show menu once at the start
        showMenu();

        while (running) {
            System.out.print("\nEnter a command or type 'menu' to view options: ");
            String choice = scanner.nextLine().trim();

            if (choice.equalsIgnoreCase("menu")) {
                showMenu();
                continue;
            }

            switch (choice) {
                case "1":
                    addTransaction(scanner, ledger, budget, undoStack);
                    break;

                case "2":
                    ledger.showAllTransactions();
                    break;

                case "3":
                    budget.showSummary();
                    // This line uses my recursive method under the hood
                    double recursiveTotal =
                        budget.calculateTotalSpendingRecursive(ledger.getTransactions());
                    System.out.println("Total expenses (detailed calculation): $" + recursiveTotal);
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
                    viewTransactionsByCategory(scanner, ledger);
                    break;

                case "13":
                    System.out.println(CYAN + "Goodbye, thanks for using the app." + RESET);
                    running = false;
                    break;

                default:
                    System.out.println(RED + "Invalid input. Type 'menu' to see options." + RESET);
            }
        }

        scanner.close();
    }

    // Shows the main menu
    private static void showMenu() {
        System.out.println();
        System.out.println(YELLOW + "--------------- Main Menu ---------------" + RESET);
        System.out.println("1. Add a transaction");
        System.out.println("2. View all transactions");
        System.out.println("3. View budget summary");
        System.out.println("4. Remove a transaction");
        System.out.println("5. Undo last added transaction");
        System.out.println("6. Schedule a future bill");
        System.out.println("7. Process scheduled bills");
        System.out.println("8. View monthly report");
        System.out.println("9. View monthly totals");
        System.out.println("10. Save data to file");
        System.out.println("11. Load data from file");
        System.out.println("12. View transactions by category");
        System.out.println("13. Exit");
        System.out.println(YELLOW + "-----------------------------------------" + RESET);
    }

    // ================== ADD TRANSACTION ==================
    // This is where the user adds a new income or expense
    private static void addTransaction(Scanner scanner, Ledger ledger, Budget budget,
                                       Stack<Transaction> undoStack) {

        System.out.print("Enter category (for example: Food, Bills, Income): ");
        String category = scanner.nextLine();

        // Ask if this is income or an expense, and keep asking until it's valid
        boolean isExpense = false;
        while (true) {
            System.out.print("Is this Income or Expense? (I/E): ");
            String type = scanner.nextLine().trim().toUpperCase();

            if (type.equals("I")) {
                isExpense = false;
                break;
            } else if (type.equals("E")) {
                isExpense = true;
                break;
            } else {
                System.out.println(RED + "Invalid type. Please use I for income or E for expense." + RESET);
            }
        }

        // Ask for amount and keep retrying on bad input
        double amount = 0.0;
        while (true) {
            System.out.print("Enter amount (just the number, no negative sign): ");
            String input = scanner.nextLine().trim();

            try {
                amount = Double.parseDouble(input);
                if (amount < 0) {
                    System.out.println(RED + "Please enter a positive number. The app will handle expenses as negative." + RESET);
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid amount. Please enter a number." + RESET);
            }
        }

        // If it is an expense, flip the sign here instead of making the user do it
        if (isExpense && amount > 0) {
            amount = -amount;
        }

        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        System.out.print("Enter date (for example: 11/03/2025): ");
        String date = scanner.nextLine();

        Transaction t = new Transaction(category, amount, description, date);

        // Add to ledger and budget so everything stays in sync
        ledger.addTransaction(t);
        budget.addTransaction(t);

        // Push onto the undo stack so I can undo this later if needed
        undoStack.push(t);

        System.out.println(GREEN + "Transaction added." + RESET);
    }

    // ================== REMOVE TRANSACTION ==================
    // Lets the user remove a transaction by its index in the list
    private static void removeTransaction(Scanner scanner, Ledger ledger, Budget budget,
                                          Stack<Transaction> undoStack) {

        var transactions = ledger.getTransactions();

        if (transactions.isEmpty()) {
            System.out.println(YELLOW + "No transactions to remove." + RESET);
            return;
        }

        while (true) {
            System.out.println("\n--- Transaction List ---");
            for (int i = 0; i < transactions.size(); i++) {
                System.out.println(i + ": " + transactions.get(i));
            }

            System.out.print("Enter the number of the transaction to remove (or 'c' to cancel): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("c")) {
                System.out.println("Remove cancelled.");
                return;
            }

            int index;
            try {
                index = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid number. Please try again." + RESET);
                continue;
            }

            if (index < 0 || index >= transactions.size()) {
                System.out.println(RED + "Index out of range. Please try again." + RESET);
                continue;
            }

            Transaction removed = ledger.removeTransaction(index);
            if (removed != null) {
                // Roll back this transaction in the budget too
                budget.removeTransaction(removed);

                // If that same transaction is on top of the undo stack, remove it there as well
                if (!undoStack.isEmpty() && undoStack.peek() == removed) {
                    undoStack.pop();
                }

                System.out.println(GREEN + "Transaction removed." + RESET);
            }
            break;
        }
    }

    // ================== UNDO LAST TRANSACTION (STACK) ==================
    // Uses the stack to undo whatever was added most recently
    private static void undoLastTransaction(Ledger ledger, Budget budget,
                                            Stack<Transaction> undoStack) {

        if (undoStack.isEmpty()) {
            System.out.println(YELLOW + "Nothing to undo." + RESET);
            return;
        }

        Transaction last = undoStack.pop();
        boolean removedFromLedger = ledger.removeTransaction(last);

        if (removedFromLedger) {
            budget.removeTransaction(last);
            System.out.println(GREEN + "Last transaction undone." + RESET);
        } else {
            System.out.println(RED + "Undo failed, transaction not found in ledger." + RESET);
        }
    }

    // ================== SCHEDULE BILL (QUEUE) ==================
    // This does not hit the ledger or budget yet; it just puts the bill into the queue
    private static void scheduleBill(Scanner scanner, Queue<Transaction> scheduledBills) {

        System.out.print("Enter category for the future bill: ");
        String category = scanner.nextLine();

        double amount = 0.0;
        while (true) {
            System.out.print("Enter bill amount (just the number, no negative sign): ");
            String input = scanner.nextLine().trim();
            try {
                amount = Double.parseDouble(input);
                if (amount < 0) {
                    System.out.println(RED + "Please enter a positive number. The app will store it as an expense." + RESET);
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid amount. Please enter a number." + RESET);
            }
        }

        // Force future bills to always be expenses
        amount = -Math.abs(amount);

        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        System.out.print("Enter due date: ");
        String date = scanner.nextLine().trim();

        while (!isValidDate(date)) {
            System.out.print("Invalid date format. Enter due date as M/D/YYYY (or 'c' to cancel): ");
            date = scanner.nextLine().trim();
            if (date.equalsIgnoreCase("c")) {
                System.out.println("Scheduling cancelled.");
                return;
            }
        }

        Transaction t = new Transaction(category, amount, description, date);
        scheduledBills.add(t);

        System.out.println(GREEN + "Future bill scheduled." + RESET);
    }

    // ================== PROCESS SCHEDULED BILLS (QUEUE) ==================
    // This pulls bills from the queue in order and actually applies them to the budget
    private static void processScheduledBills(Ledger ledger, Budget budget,
                                              Queue<Transaction> scheduledBills,
                                              Stack<Transaction> undoStack) {

        if (scheduledBills.isEmpty()) {
            System.out.println(YELLOW + "No scheduled bills to process." + RESET);
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

        System.out.println(GREEN + "All scheduled bills processed." + RESET);
    }

    // ================== VIEW MONTHLY REPORT ==================
    // Asks for a month number and shows just that month's activity
    private static void viewMonthlyReport(Scanner scanner, Ledger ledger, Budget budget) {

        int month = -1;

        while (true) {
            System.out.print("Enter month number (1 to 12) or 'c' to cancel: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("c")) {
                System.out.println("Monthly report cancelled.");
                return;
            }

            try {
                month = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid month number. Please try again." + RESET);
                continue;
            }

            if (month < 1 || month > 12) {
                System.out.println(RED + "Month must be between 1 and 12." + RESET);
                continue;
            }

            break;
        }

        budget.showMonthlyReport(ledger.getTransactions(), month);
    }

    // ================== VIEW BY CATEGORY ==================
    // Lets me filter the transaction list by a single category, like "Food"
    private static void viewTransactionsByCategory(Scanner scanner, Ledger ledger) {
        System.out.print("Enter category to filter by: ");
        String category = scanner.nextLine().trim();

        var transactions = ledger.getTransactions();

        System.out.println("\n--- Transactions in category: " + category + " ---");
        boolean foundAny = false;

        for (Transaction t : transactions) {
            if (t.getCategory().equalsIgnoreCase(category)) {
                System.out.println(t);
                foundAny = true;
            }
        }

        if (!foundAny) {
            System.out.println(YELLOW + "No transactions found for that category." + RESET);
        }
    }

    // ================== SAVE DATA TO FILE ==================
    // Saves all transactions into a text file so I can load them back later
    private static void saveData(Ledger ledger) {
        // Use try-with-resources and a safe delimiter ("|") with simple escaping
        try (FileWriter writer = new FileWriter("budget_data.txt")) {
            for (Transaction t : ledger.getTransactions()) {
                String line = escapeField(t.getCategory()) + "|" + t.getAmount() + "|" + escapeField(t.getDescription()) + "|" + escapeField(t.getDate()) + "\n";
                writer.write(line);
            }
            System.out.println(GREEN + "Data saved to budget_data.txt." + RESET);
        } catch (IOException e) {
            System.out.println(RED + "Something went wrong while saving the file: " + e.getMessage() + RESET);
        }
    }

    // ================== LOAD DATA FROM FILE ==================
    // Reads the text file and rebuilds ledger, budget, and undo stack
    private static void loadData(Ledger ledger, Budget budget, Stack<Transaction> undoStack) {
        File file = new File("budget_data.txt");
        if (!file.exists()) {
            System.out.println(YELLOW + "No saved data found." + RESET);
            return;
        }

        // Read using try-with-resources and a pipe delimiter, undoing simple escaping
        LinkedList<Transaction> newList = new LinkedList<>();
        try (Scanner fileReader = new Scanner(file)) {
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                String[] parts = line.split("\\|", -1);
                if (parts.length == 4) {
                    String category = unescapeField(parts[0]);
                    double amount = Double.parseDouble(parts[1]);
                    String description = unescapeField(parts[2]);
                    String date = unescapeField(parts[3]);
                    Transaction t = new Transaction(category, amount, description, date);
                    newList.add(t);
                }
            }
        } catch (Exception e) {
            System.out.println(RED + "Something went wrong while loading your data: " + e.getMessage() + RESET);
            return;
        }

        // Replace everything in the ledger with what I just loaded
        ledger.setTransactions(newList);

        // Reset the budget and undo stack, then rebuild them from the loaded list
        budget.resetAll();
        undoStack.clear();

        for (Transaction t : newList) {
            budget.addTransaction(t);
            undoStack.push(t);
        }

        System.out.println(GREEN + "Saved data loaded." + RESET);
    }

    // Escape '|' characters when saving to a pipe-delimited file
    private static String escapeField(String s) {
        if (s == null) return "";
        return s.replace("%", "%25").replace("|", "%7C");
    }

    private static String unescapeField(String s) {
        if (s == null) return "";
        return s.replace("%7C", "|").replace("%25", "%");
    }

    // Basic date validation using java.time (accepts M/D/YYYY or MM/DD/YYYY)
    private static boolean isValidDate(String date) {
        if (date == null) return false;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("M/d/yyyy");
        try {
            LocalDate.parse(date, fmt);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
