package entities;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Offer {
    private final int offerID;
    private static int offerCount = 1;
    private final int clientID;
    private final StockType nameOfStock;
    private int noOfStock;
    private int noOfStockLeft;
    private int priceOfStock;
    private final OfferType offerType;
    private boolean isCompleted;
    protected Lock matcherLock = new ReentrantLock();

    public Offer(int clientID,
                 StockType nameOfStock,
                 int noOfStock,
                 int priceOfStock,
                 OfferType offerType) {
        this.offerID = offerCount;
        offerCount++;

        this.clientID = clientID;
        this.nameOfStock = nameOfStock;
        this.noOfStock = noOfStock;
        this.noOfStockLeft = noOfStock;
        this.priceOfStock = priceOfStock;
        this.offerType = offerType;
        this.isCompleted = false;
    }

    public int getOfferID() {
        return this.offerID;
    }

    public int getClientID() {
        return this.clientID;
    }

    public StockType getNameOfStock() {
        return nameOfStock;
    }

    public int getNoOfStock() {
        return noOfStock;
    }

    public int getPriceOfStock() {
        return priceOfStock;
    }

    public int getNoOfStockLeft() {
        return noOfStockLeft;
    }

    public OfferType getOfferType() {
        return offerType;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setNoOfStock(int noOfStock) {
        this.noOfStock = noOfStock;
    }

    public void setPriceOfStock(int priceOfStock) {
        this.priceOfStock = priceOfStock;
    }

    public void setIsCompleted() {
        this.isCompleted = true;
    }

    public int matchNumberOfStocks(Offer otherOffer) {
        return Math.min(this.noOfStockLeft, otherOffer.getNoOfStockLeft());
    }

    public void updateOfferAfterMatching(int noTradedStocks) {
        this.noOfStockLeft -= noTradedStocks;

        if(this.noOfStockLeft == 0) {
            this.setIsCompleted();
            Matcher.removeOffer(this);
        }
        else {
            Matcher.sendToBack(this);
        }
    }

    public String toString() {
        return "Offer ID: " + this.offerID + "\n" +
                "Client ID: " + this.clientID + "\n" +
                "Stock Name: " + this.nameOfStock + "\n" +
                "Requested Number of Stock: " + this.noOfStock + "\n" +
                "Number of Stock Left: " + this.noOfStockLeft + "\n" +
                "Price per Stock: " + this.priceOfStock + "\n" +
                "Type of offer: " + this.offerType + "\n" +
                "Offer closed: " + this.isCompleted + "\n";
    }
}
