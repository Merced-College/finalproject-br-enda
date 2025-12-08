public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to the Personal Finance Budget App!");

        // Temporary test code for Task 1
        Transaction t1 = new Transaction("Food", -25.50, "Lunch with friends", "11/03/2025");
        Transaction t2 = new Transaction("Income", 500.00, "Paycheck", "11/03/2025");

        Ledger ledger = new Ledger();
        Budget budget = new Budget();

        ledger.addTransaction(t1);
        ledger.addTransaction(t2);

        ledger.showAllTransactions();
        budget.addTransaction(t1);
        budget.addTransaction(t2);
        budget.showSummary();
    }
}
