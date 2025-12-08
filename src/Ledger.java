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
}
