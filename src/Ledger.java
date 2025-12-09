/*
    Ledger class for Personal Finance Budget App
    Name: Brenda Romero Torres
*/

import java.util.LinkedList;

// This class keeps track of all transactions, kind of like a mini bank statement
public class Ledger {

    private LinkedList<Transaction> transactions;

    public Ledger() {
        // LinkedList lets me add and remove easily while keeping order
        transactions = new LinkedList<>();
    }

    // Add a new transaction to the history
    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    // Show every transaction currently in the ledger
    public void showAllTransactions() {
        System.out.println("\n--- Transaction History ---");
        if (transactions.isEmpty()) {
            System.out.println("No transactions yet.");
        } else {
            for (Transaction t : transactions) {
                System.out.println(t);
            }
        }
    }

    // Gives me access to the whole list when I need it elsewhere
    public LinkedList<Transaction> getTransactions() {
        return transactions;
    }

    // Remove a transaction by index (used when the user picks a number in the menu)
    public Transaction removeTransaction(int index) {
        if (index < 0 || index >= transactions.size()) {
            System.out.println("Invalid index. No transaction removed.");
            return null;
        }
        Transaction removed = transactions.remove(index);
        System.out.println("Removed transaction: " + removed);
        return removed;
    }

    // Remove a specific transaction object (used for undo)
    public boolean removeTransaction(Transaction t) {
        boolean removed = transactions.remove(t);
        if (removed) {
            System.out.println("Removed transaction: " + t);
        }
        return removed;
    }

    // This lets me replace the whole list at once, which I use when loading from a file
    public void setTransactions(LinkedList<Transaction> newList) {
        transactions = newList;
    }
}
