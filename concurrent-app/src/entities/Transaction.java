package entities;

public class Transaction {
    private final int id;
    private static int transactionCounter = 1;
    private final int sellerID;
    private final int buyerID;
    private final int sellingOfferID;
    private final int buyingOfferID;
    private final StockType nameOfStock;
    private final int noOfTradedStocks;
    private final int priceOfTradedStocks;
    private final int totalPrice;

    public Transaction(int sellerID,
                       int buyerID,
                       int sellingOfferID,
                       int buyingOfferID,
                       StockType nameOfStock,
                       int noOfTradedStocks,
                       int priceOfTradedStocks) {
        this.id = transactionCounter;
        transactionCounter++;

        this.sellerID = sellerID;
        this.buyerID = buyerID;
        this.sellingOfferID = sellingOfferID;
        this.buyingOfferID = buyingOfferID;
        this.nameOfStock = nameOfStock;
        this.noOfTradedStocks = noOfTradedStocks;
        this.priceOfTradedStocks = priceOfTradedStocks;
        this.totalPrice = this.noOfTradedStocks * this.priceOfTradedStocks;
    }

    public String toString() {
        return  "Transaction ID: " + this.id + "\n" +
                "Buyer ID: " + this.buyerID + "\n" +
                "Buying Offer ID: " + this.buyingOfferID + "\n" +
                "Seller ID: " + this.sellerID + "\n" +
                "Selling Offer ID: " + this.sellingOfferID + "\n" +
                "Name of the Traded Stock: " + this.nameOfStock + "\n" +
                "Number of the Traded Stock: " + this.noOfTradedStocks + "\n" +
                "Price of the Traded Stock: " + this.priceOfTradedStocks + "\n" +
                "Total Price of the Transaction: " + this.totalPrice + "\n";
    }
}
