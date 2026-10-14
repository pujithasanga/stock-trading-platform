import java.io.*;
import java.util.*;

/*
 * Stock Trading Platform
 * CodeAlpha Internship - Task 2
 */

class Stock {
    private String symbol;
    private String name;
    private double price;

    public Stock(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void displayStock() {
        System.out.printf(
            "%-10s %-20s ₹%.2f%n",
            symbol, name, price
        );
    }
}


class Holding {
    private String symbol;
    private int quantity;
    private double averageBuyPrice;

    public Holding(String symbol, int quantity, double averageBuyPrice) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.averageBuyPrice = averageBuyPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getAverageBuyPrice() {
        return averageBuyPrice;
    }

    public void buyMore(int newQuantity, double price) {
        double totalCost =
                (quantity * averageBuyPrice) +
                (newQuantity * price);

        quantity += newQuantity;
        averageBuyPrice = totalCost / quantity;
    }

    public void sell(int quantityToSell) {
        quantity -= quantityToSell;
    }
}


class Portfolio {
    private ArrayList<Holding> holdings;
    private double cash;

    public Portfolio(double initialCash) {
        this.cash = initialCash;
        this.holdings = new ArrayList<>();
    }

    public double getCash() {
        return cash;
    }

    public ArrayList<Holding> getHoldings() {
        return holdings;
    }

    public boolean buyStock(Stock stock, int quantity) {

        if (quantity <= 0) {
            System.out.println("Invalid quantity.");
            return false;
        }

        double totalCost = stock.getPrice() * quantity;

        if (totalCost > cash) {
            System.out.println("Insufficient balance.");
            return false;
        }

        Holding existingHolding = findHolding(stock.getSymbol());

        if (existingHolding != null) {
            existingHolding.buyMore(quantity, stock.getPrice());
        } else {
            holdings.add(
                new Holding(
                    stock.getSymbol(),
                    quantity,
                    stock.getPrice()
                )
            );
        }

        cash -= totalCost;

        System.out.println("\nStock purchased successfully!");
        System.out.printf(
            "Bought %d shares of %s for ₹%.2f%n",
            quantity,
            stock.getSymbol(),
            totalCost
        );

        return true;
    }

    public boolean sellStock(Stock stock, int quantity) {

        if (quantity <= 0) {
            System.out.println("Invalid quantity.");
            return false;
        }

        Holding holding = findHolding(stock.getSymbol());

        if (holding == null) {
            System.out.println("You do not own this stock.");
            return false;
        }

        if (holding.getQuantity() < quantity) {
            System.out.println("You don't have enough shares.");
            return false;
        }

        double saleValue = stock.getPrice() * quantity;

        holding.sell(quantity);
        cash += saleValue;

        if (holding.getQuantity() == 0) {
            holdings.remove(holding);
        }

        System.out.println("\nStock sold successfully!");
        System.out.printf(
            "Sold %d shares of %s for ₹%.2f%n",
            quantity,
            stock.getSymbol(),
            saleValue
        );

        return true;
    }

    private Holding findHolding(String symbol) {

        for (Holding holding : holdings) {
            if (holding.getSymbol().equalsIgnoreCase(symbol)) {
                return holding;
            }
        }

        return null;
    }

    public void displayPortfolio(ArrayList<Stock> stocks) {

        System.out.println("\n========== YOUR PORTFOLIO ==========");

        System.out.printf("Available Cash: ₹%.2f%n", cash);

        if (holdings.isEmpty()) {
            System.out.println("No stocks in portfolio.");
            return;
        }

        double totalInvestment = 0;
        double currentValue = 0;

        System.out.println(
            "\nSymbol     Quantity     Avg Buy     Current     Profit/Loss"
        );

        System.out.println(
            "-----------------------------------------------------------"
        );

        for (Holding holding : holdings) {

            Stock stock = findStock(stocks, holding.getSymbol());

            if (stock == null) {
                continue;
            }

            double investment =
                    holding.getQuantity()
                    * holding.getAverageBuyPrice();

            double value =
                    holding.getQuantity()
                    * stock.getPrice();

            double profitLoss = value - investment;

            totalInvestment += investment;
            currentValue += value;

            System.out.printf(
                "%-10s %-12d ₹%-10.2f ₹%-10.2f ₹%.2f%n",
                holding.getSymbol(),
                holding.getQuantity(),
                holding.getAverageBuyPrice(),
                stock.getPrice(),
                profitLoss
            );
        }

        double totalProfitLoss =
                currentValue - totalInvestment;

        System.out.println(
            "\n-----------------------------------------------"
        );

        System.out.printf(
            "Total Investment : ₹%.2f%n",
            totalInvestment
        );

        System.out.printf(
            "Current Value    : ₹%.2f%n",
            currentValue
        );

        System.out.printf(
            "Total Profit/Loss: ₹%.2f%n",
            totalProfitLoss
        );

        System.out.printf(
            "Total Portfolio  : ₹%.2f%n",
            cash + currentValue
        );
    }

    private Stock findStock(
            ArrayList<Stock> stocks,
            String symbol) {

        for (Stock stock : stocks) {
            if (stock.getSymbol().equalsIgnoreCase(symbol)) {
                return stock;
            }
        }

        return null;
    }
}


public class StockTradingPlatform {

    private static Scanner scanner = new Scanner(System.in);

    private static ArrayList<Stock> stocks =
            new ArrayList<>();

    private static Portfolio portfolio =
            new Portfolio(100000);

    public static void main(String[] args) {

        initializeStocks();

        System.out.println(
            "=========================================="
        );

        System.out.println(
            "       STOCK TRADING PLATFORM"
        );

        System.out.println(
            "=========================================="
        );

        System.out.println(
            "Initial Balance: ₹100000"
        );

        int choice;

        do {

            displayMenu();

            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();

            switch (choice) {

                case 1:
                    displayMarket();
                    break;

                case 2:
                    buyStock();
                    break;

                case 3:
                    sellStock();
                    break;

                case 4:
                    portfolio.displayPortfolio(stocks);
                    break;

                case 5:
                    savePortfolio();
                    break;

                case 6:
                    loadPortfolio();
                    break;

                case 7:
                    System.out.println(
                        "\nThank you for using the Stock Trading Platform!"
                    );
                    break;

                default:
                    System.out.println(
                        "Invalid choice. Try again."
                    );
            }

        } while (choice != 7);

        scanner.close();
    }


    private static void initializeStocks() {

        stocks.add(
            new Stock("TCS", "Tata Consultancy", 3500)
        );

        stocks.add(
            new Stock("INFY", "Infosys", 1500)
        );

        stocks.add(
            new Stock("RELIANCE", "Reliance Industries", 2800)
        );

        stocks.add(
            new Stock("HDFC", "HDFC Bank", 1700)
        );

        stocks.add(
            new Stock("ITC", "ITC Limited", 450)
        );
    }


    private static void displayMenu() {

        System.out.println(
            "\n========== MAIN MENU =========="
        );

        System.out.println("1. View Stock Market");
        System.out.println("2. Buy Stock");
        System.out.println("3. Sell Stock");
        System.out.println("4. View Portfolio");
        System.out.println("5. Save Portfolio");
        System.out.println("6. Load Portfolio");
        System.out.println("7. Exit");

        System.out.println(
            "==============================="
        );
    }


    private static void displayMarket() {

        System.out.println(
            "\n========== STOCK MARKET =========="
        );

        System.out.printf(
            "%-10s %-20s %s%n",
            "Symbol",
            "Company",
            "Price"
        );

        System.out.println(
            "------------------------------------------"
        );

        for (Stock stock : stocks) {
            stock.displayStock();
        }
    }


    private static void buyStock() {

        displayMarket();

        System.out.print(
            "\nEnter stock symbol: "
        );

        String symbol = scanner.next();

        Stock selectedStock = findStock(symbol);

        if (selectedStock == null) {
            System.out.println(
                "Stock not found."
            );
            return;
        }

        System.out.print(
            "Enter quantity: "
        );

        int quantity = scanner.nextInt();

        portfolio.buyStock(
            selectedStock,
            quantity
        );
    }


    private static void sellStock() {

        portfolio.displayPortfolio(stocks);

        System.out.print(
            "\nEnter stock symbol: "
        );

        String symbol = scanner.next();

        Stock selectedStock = findStock(symbol);

        if (selectedStock == null) {
            System.out.println(
                "Stock not found."
            );
            return;
        }

        System.out.print(
            "Enter quantity to sell: "
        );

        int quantity = scanner.nextInt();

        portfolio.sellStock(
            selectedStock,
            quantity
        );
    }


    private static Stock findStock(String symbol) {

        for (Stock stock : stocks) {

            if (stock.getSymbol()
                    .equalsIgnoreCase(symbol)) {

                return stock;
            }
        }

        return null;
    }


    private static void savePortfolio() {

        try {

            FileWriter writer =
                    new FileWriter("portfolio.txt");

            writer.write(
                "========== PORTFOLIO ==========\n"
            );

            writer.write(
                String.format(
                    "Available Cash: ₹%.2f\n\n",
                    portfolio.getCash()
                )
            );

            writer.write(
                "Holdings:\n"
            );

            for (Holding holding :
                    portfolio.getHoldings()) {

                writer.write(
                    String.format(
                        "%s | Quantity: %d | Avg Buy Price: %.2f\n",
                        holding.getSymbol(),
                        holding.getQuantity(),
                        holding.getAverageBuyPrice()
                    )
                );
            }

            writer.close();

            System.out.println(
                "Portfolio saved successfully to portfolio.txt"
            );

        } catch (IOException e) {

            System.out.println(
                "Error while saving portfolio."
            );
        }
    }


    private static void loadPortfolio() {

        try {

            File file =
                    new File("portfolio.txt");

            if (!file.exists()) {

                System.out.println(
                    "No saved portfolio found."
                );

                return;
            }

            Scanner fileScanner =
                    new Scanner(file);

            System.out.println(
                "\n========== SAVED PORTFOLIO =========="
            );

            while (fileScanner.hasNextLine()) {

                System.out.println(
                    fileScanner.nextLine()
                );
            }

            fileScanner.close();

        } catch (IOException e) {

            System.out.println(
                "Error while loading portfolio."
            );
        }
    }
}