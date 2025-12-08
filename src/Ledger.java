import java.util.LinkedList;

public class Ledger {
    private LinkedList<Transaction> transactions;

    public Ledger() {
        transactions = new LinkedList<>();
    }

    public void addTransaction(Transaction t) {
        transactions.add(t);
        System.out.println("Transaction added.");
    }

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

    public LinkedList<Transaction> getTransactions() {
        return transactions;
    }

    public Transaction removeTransaction(int index) {
        if (index < 0 || index >= transactions.size()) {
            System.out.println("Invalid index. No transaction removed.");
            return null;
        }
        Transaction removed = transactions.remove(index);
        System.out.println("Removed transaction: " + removed);
        return removed;
    }

}
