import java.util.LinkedList;

public class Ledger {

    private LinkedList<Transaction> transactions;

    public Ledger() {
        transactions = new LinkedList<>();
    }

    // Add a transaction to the ledger
    public void addTransaction(Transaction t) {
        transactions.add(t);
        System.out.println("Transaction added.");
    }

    // Show all transactions in the ledger
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

    // Get the list of transactions
    public LinkedList<Transaction> getTransactions() {
        return transactions;
    }

    // Remove a transaction by index (used in menu option 4)
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
            System.out.println("Removed transaction (undo): " + t);
        }
        return removed;
    }
    
    // This lets me replace the whole list when I load from a file
    public void setTransactions(LinkedList<Transaction> newList) {
        transactions = newList;
    }
}
