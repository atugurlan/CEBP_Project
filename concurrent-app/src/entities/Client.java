package entities;

import java.util.ArrayList;
import java.util.Map;

public class Client {
    public int getId() {
        return id;
    }

    private final int id;
    private String name;
    private static int clientCount = 1;
    private ArrayList<Transaction> transactionHistory;
    private ArrayList<Offer> offerHistory;
    private int moneyWallet;
    private Map<StockType, Integer> stockWallet;

    public Client(String name,
                  int moneyWallet,
                  Map<StockType, Integer> stockWallet) {
        this.id = clientCount;
        clientCount++;

        this.name = name;
        this.transactionHistory = new ArrayList<Transaction>();
        this.offerHistory = new ArrayList<Offer>();
        this.moneyWallet = moneyWallet;
        this.stockWallet = stockWallet;
    }

    public int postOffer(StockType stockName, int noOfStocks, int pricePerStock, OfferType offerType) {
        if(checkOffer(stockName, noOfStocks, pricePerStock, offerType)) {
            Offer newOffer = new Offer(this.id, stockName, noOfStocks, pricePerStock, offerType);
            offerHistory.add(newOffer);
            System.out.println(this.id);

            Matcher.addOffer(newOffer);
            Matcher.matchOffer(newOffer);

            return 0;
        }
        else {
            System.out.println("The offer was not created.\n");
            return 1;
        }
    }

    public int deleteOffer(int offerID) {
        if(offerHistory.isEmpty()) {
            System.out.println("There are no offers created.\n");
            return 2;
        }

        Offer foundOffer = findOffer(offerID);

        if(foundOffer == null) {
            System.out.println("The id of the offer cannot be found.\n");
            return 1;
        }

        this.offerHistory.remove(foundOffer);
        System.out.println("Offer was deleted successfully.\n");

        return 0;
    }

    public int modifyOfferByStocks(int offerID, int noOfStocks) {
        Offer foundOffer = findOffer(offerID);

        if(foundOffer == null) {
            System.out.println("The id of the offer cannot be found.\n");
            return 3;
        }

        if(foundOffer.matcherLock.tryLock()) {
            try {
                if(foundOffer.isCompleted()) {
                    System.out.println("The offer that cannot be modified due to being completed.\n");
                    return 2;
                }

                if(!checkOffer(foundOffer.getNameOfStock(), noOfStocks, foundOffer.getPriceOfStock(), foundOffer.getOfferType())) {
                    System.out.println("User does not have enough stocks to change the number of stocks to the desired value in the offer.\n");
                    return 1;
                }

                foundOffer.setNoOfStock(noOfStocks);
                System.out.println("Successfully changed the number of stocks in the offer with id " + offerID + "\n");
                return 0;
            } finally {
                foundOffer.matcherLock.unlock();
            }
        }
        return 4;
    }

    public int modifyOfferByPrice(int offerID, int priceOfStock) {
        Offer foundOffer = findOffer(offerID);

        if(foundOffer == null) {
            System.out.println("The id of the offer cannot be found.\n");
            return 3;
        }

        if(foundOffer.matcherLock.tryLock()) {
            try {
                if (foundOffer.isCompleted()) {
                    System.out.println("The offer that cannot be modified due to being completed.\n");
                    return 2;
                }

                if (!checkOffer(foundOffer.getNameOfStock(), foundOffer.getNoOfStock(), priceOfStock, foundOffer.getOfferType())) {
                    System.out.println("User does not have enough money to be able to pay the new price.\n");
                    return 1;
                }

                foundOffer.setPriceOfStock(priceOfStock);
                System.out.println("Successfully changed the price per stock in the offer with id " + offerID + "\n");
                return 0;
            } finally {
                foundOffer.matcherLock.unlock();
            }
        }
        return 4;
    }

    public int modifyOfferByStocksAndPrice(int offerID, int noOfStocks, int priceOfStock) {
        Offer foundOffer = findOffer(offerID);

        if(foundOffer == null) {
            System.out.println("The id of the offer cannot be found.\n");
            return 3;
        }

        if(foundOffer.matcherLock.tryLock()) {
            try {
                if(foundOffer.isCompleted()) {
                    System.out.println("The offer that cannot be modified due to being completed.\n");
                    return 2;
                }

                if(!checkOffer(foundOffer.getNameOfStock(), noOfStocks, priceOfStock, foundOffer.getOfferType())) {
                    System.out.println("User does not have enough money to be able to pay the new price.\n");
                    return 1;
                }

                foundOffer.setNoOfStock(noOfStocks);
                foundOffer.setPriceOfStock(priceOfStock);
                System.out.println("Successfully changed the number of stocks and price per stock in the offer with id " + offerID + "\n");
                return 0;
            } finally {
                foundOffer.matcherLock.unlock();
            }
        }
        return 4;
    }

    public void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }

    public void updateBuyerWallet(StockType nameOfStock, int noOfTradedOfStocks, int transactionPrice) {
        this.stockWallet.put(nameOfStock, this.stockWallet.getOrDefault(nameOfStock, 0) + noOfTradedOfStocks);
        this.moneyWallet -= transactionPrice;
    }

    public void updateSellerWallet(StockType nameOfStock, int noOfTradedOfStocks, int transactionPrice) {
        this.stockWallet.put(nameOfStock, this.stockWallet.get(nameOfStock) - noOfTradedOfStocks);
        this.moneyWallet += transactionPrice;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Client Id: ").append(this.id).append("\n").append("Name: ").append(this.name).append("\n").append("Money Wallet: ").append(this.moneyWallet).append("\n");

        if(this.stockWallet.isEmpty()) {
            str.append("There are no stocks available in the client's stock wallet").append("\n");
        }
        else {
            str.append("Stocks available: ").append("\n");
            for (Map.Entry<StockType, Integer> stock : this.stockWallet.entrySet()) {
                StockType key = stock.getKey();

                str.append("  - ").append(key).append(": ").append(stock.getValue()).append("\n");
            }
        }

        if(this.offerHistory.isEmpty()) {
            str.append("There were no offers made.").append("\n");
        }
        else {
            str.append("Created offers by the user:").append("\n");
            str.append("------------------------------------------------------------").append("\n");
            for(Offer offer : this.offerHistory) {
                str.append(offer.toString());
                str.append("------------------------------------------------------------").append("\n");
            }
        }

        if(this.transactionHistory.isEmpty()) {
            str.append("There were no transactions closed.").append("\n");
        }
        else {
            str.append("Transactions for this user:").append("\n");
            str.append("------------------------------------------------------------").append("\n");
            for(Transaction transaction : this.transactionHistory) {
                str.append(transaction.toString());
                str.append("------------------------------------------------------------").append("\n");
            }
        }

        return str.toString();
    }

    private Offer findOffer(int offerID) {
        for(Offer offer : this.offerHistory) {
            if(offer.getOfferID() == offerID) {
                return offer;
            }
        }

        return null;
    }

    private boolean checkOffer(StockType stockName, int noOfStocks, int pricePerStock, OfferType offerType) {
        if(offerType == OfferType.BUY) {
            //check if the user has money
            int price = noOfStocks * pricePerStock;
            if(price > this.moneyWallet) {
                System.out.println("User does not have enough money to create this offer.\n");
                return false;
            }
        }
        else {
            // check if the user has that specific stock
            boolean foundStock = false;
            for (Map.Entry<StockType, Integer> stock : this.stockWallet.entrySet()) {
                StockType key = stock.getKey();
                if(key.equals(stockName)) {
                    foundStock = true;

                    Integer noOfStocksInWallet = stock.getValue();
                    if(noOfStocksInWallet < noOfStocks) {
                        System.out.println("The stock is available, but the client does not have enough stocks to create the offer.\n");
                        return false;
                    }
                }
            }

            if(!foundStock) {
                System.out.println("The user does not have that specific stock in his wallet.\n");
                return false;
            }
        }

        return true;
    }
}
