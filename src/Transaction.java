/*
    Transaction class for Personal Finance Budget App
    Name: Brenda Romero Torres
*/

// This class is just one transaction: either an income or an expense
public class Transaction {

    private String category;
    private double amount;
    private String description;
    private String date; // keeping it simple as a String like "11/03/2025"

    public Transaction(String category, double amount, String description, String date) {
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    // This makes printing a transaction have a nice format
    @Override
    public String toString() {
        return date + " | " + category + " | $" + String.format("%.2f", amount) + " | " + description;
    }
}
