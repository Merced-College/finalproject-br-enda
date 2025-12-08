public class Transaction {
    private String category;
    private double amount;
    private String description;
    private String date; // you can later switch to a date type if you want

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

    // For printing a transaction nicely
    @Override
    public String toString() {
        return date + " | " + category + " | $" + amount + " | " + description;
    }
}