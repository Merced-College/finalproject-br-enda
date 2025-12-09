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
        Ledger ledger = new Ledger();
        Budget budget = new Budget();

        Stack<Transaction> undoStack = new Stack<>();
        Queue<Transaction> scheduledBills = new LinkedList<>();

        System.out.println("Welcome to the Personal Finance Budget App!");

        boolean running = true;

        while (running) {
            // updated menu with save/load options
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
                    budget.showSummary();
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
                    System.out.println("Invalid choice. Please enter a number from 1–12.");
            }
        }

        scanner.close();
    }

    // -------------------- SAVE DATA TO FILE --------------------
    // This writes all my transactions to a text file so they don't disappear when I close the program
    private static void saveData(Ledger ledger) {
        try {
            FileWriter writer = new FileWriter("budget_data.txt");

            // I'm basically saving each transaction as one line
            for (Transaction t : ledger.getTransactions()) {
                // I separate everything by commas so it's easy to rebuild later
                writer.write(t.getCategory() + "," 
                           + t.getAmount() + ","
                           + t.getDescription() + ","
                           + t.getDate() + "\n");
            }

            writer.close();
            System.out.println("Your data was saved to budget_data.txt!");
        } catch (IOException e) {
            System.out.println("Something went wrong while saving the file.");
        }
    }

    // -------------------- LOAD DATA FROM FILE --------------------
    // This reads the text file and rebuilds all my transactions, budget, and undo stack
    private static void loadData(Ledger ledger, Budget budget, Stack<Transaction> undoStack) {
        try {
            File file = new File("budget_data.txt");

            if (!file.exists()) {
                System.out.println("No saved data found, file does not exist.");
                return;
            }

            Scanner fileReader = new Scanner(file);

            // I make a brand new list to replace anything already in Ledger
            LinkedList<Transaction> newList = new LinkedList<>();

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

            // Replace everything in Ledger with the loaded data
            ledger.setTransactions(newList);

            // Reset budget totals before recalculating
            budget = new Budget();
            undoStack.clear();

            // Rebuild budget totals and undo stack based on loaded transactions
            for (Transaction t : newList) {
                budget.addTransaction(t);
                undoStack.push(t);
            }

            System.out.println("Your saved data was successfully loaded!");

        } catch (Exception e) {
            System.out.println("Something went wrong while loading your data.");
        }
    }

    // -------------------- The rest of Main.java stays the same --------------------
    // (addTransaction, removeTransaction, undoLastTransaction, scheduleBill, etc.)
}
