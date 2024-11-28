package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Matcher {
    private static HashMap<StockType, HashMap<Integer, ArrayList<Offer>>> buyOffers = new HashMap<StockType, HashMap<Integer, ArrayList<Offer>>>();
    private static HashMap<StockType, HashMap<Integer, ArrayList<Offer>>> sellOffers = new HashMap<StockType, HashMap<Integer, ArrayList<Offer>>>();

    public static void addOffer(Offer offer) {
        HashMap<StockType, HashMap<Integer, ArrayList<Offer>>> offers = (offer.getOfferType() == OfferType.BUY) ? buyOffers : sellOffers;

        StockType stockType = offer.getNameOfStock();
        int price = offer.getPriceOfStock();

        HashMap<Integer, ArrayList<Offer>> priceOffers = offers.computeIfAbsent(stockType, k -> new HashMap<>());
        ArrayList<Offer> offerList = priceOffers.computeIfAbsent(price, k -> new ArrayList<>());

        offerList.add(offer);
        priceOffers = sort(priceOffers);
        offers.put(stockType, priceOffers);
    }

    private static HashMap<Integer, ArrayList<Offer>> sort(HashMap<Integer, ArrayList<Offer>> stockOffers) {
        HashMap<Integer, ArrayList<Offer>> sortedStockOffers = stockOffers.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,  // Merge function in case of duplicates
                        LinkedHashMap::new
                ));

        return sortedStockOffers;
    }

    public static void matchOffer(Offer offer) {
        if(offer.matcherLock.tryLock()) {
            try {
                HashMap<StockType, HashMap<Integer, ArrayList<Offer>>> existingOffers = (offer.getOfferType() == OfferType.BUY) ? sellOffers : buyOffers;

                StockType offerStockType = offer.getNameOfStock();
                int offerPrice = offer.getPriceOfStock();

                if(!existingOffers.containsKey(offerStockType)) {
                    System.out.println("There are no offers with that stock name.");
                    return;
                }

                HashMap<Integer, ArrayList<Offer>> existingOffersByPrice = existingOffers.get(offerStockType);

                if(!existingOffersByPrice.containsKey(offerPrice)) {
                    System.out.println("There are no offers with the same price as the offer.");
                    return;
                }

                ArrayList<Offer> existingOffersAtPrice = existingOffersByPrice.get(offerPrice);
                Offer matchedOffer = existingOffersAtPrice.getFirst();

                if(matchedOffer.matcherLock.tryLock()) {
                    try {
                        int tradedNumberOfStocks = offer.matchNumberOfStocks(matchedOffer);

                        offer.updateOfferAfterMatching(tradedNumberOfStocks);
                        matchedOffer.updateOfferAfterMatching(tradedNumberOfStocks);

                        Offer buyingOffer = (offer.getOfferType() == OfferType.BUY) ? offer : matchedOffer;
                        Offer sellingOffer = (offer.getOfferType() == OfferType.SELL) ? offer : matchedOffer;

                        if (buyingOffer.getClientID() != sellingOffer.getClientID()) {
                            createTransaction(buyingOffer, sellingOffer, tradedNumberOfStocks);
                        }

                    } finally {
                        matchedOffer.matcherLock.unlock();
                    }
                }
            } finally {
                offer.matcherLock.unlock();
            }
        }
    }

    private static void createTransaction(Offer buyingOffer, Offer sellingOffer, int noOfTradedStocks) {
        int buyerID = buyingOffer.getClientID();
        int sellerID = sellingOffer.getClientID();

        int buyingOfferID = buyingOffer.getOfferID();
        int sellingOfferID = sellingOffer.getOfferID();

        StockType offerStockType = buyingOffer.getNameOfStock();
        int offerPrice = buyingOffer.getPriceOfStock();

        Transaction transaction = new Transaction(sellerID, buyerID, sellingOfferID, buyingOfferID, offerStockType, noOfTradedStocks, offerPrice);
        int transactionPrice = noOfTradedStocks * offerPrice;

        System.out.println("--------------------------------");
        System.out.println(transaction);
        System.out.println("--------------------------------");
        System.out.println(buyingOffer);
        System.out.println(sellingOffer);
    }

    public static void removeOffer(Offer offer) {
        StockType stockType = offer.getNameOfStock();
        int price = offer.getPriceOfStock();

        HashMap<StockType, HashMap<Integer, ArrayList<Offer>>> existingOffers = (offer.getOfferType() == OfferType.BUY) ? buyOffers : sellOffers;
        HashMap<Integer, ArrayList<Offer>> existingOffersByPrice = existingOffers.get(stockType);
        ArrayList<Offer> offers = existingOffersByPrice.get(price);

        offers.remove(offer);

        if(offers.isEmpty()) {
            existingOffersByPrice.remove(price);
        }

        if(existingOffersByPrice.isEmpty()) {
            existingOffers.remove(stockType);
        }
    }

    public static void sendToBack(Offer offer) {
        StockType stockType = offer.getNameOfStock();
        int price = offer.getPriceOfStock();

        HashMap<StockType, HashMap<Integer, ArrayList<Offer>>> existingOffers = (offer.getOfferType() == OfferType.BUY) ? buyOffers : sellOffers;
        HashMap<Integer, ArrayList<Offer>> existingOffersByPrice = existingOffers.get(stockType);
        ArrayList<Offer> offers = existingOffersByPrice.get(price);

//        System.out.println(offers);
        offers.remove(offer);
//        System.out.println(offers);
        offers.add(offer);
//        System.out.println(offers);
    }
}
