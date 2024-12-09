import entities.Client;
import entities.ClientManager;
import entities.OfferType;
import entities.StockType;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
//        Map<StockType, Integer> stockWallet1 = new HashMap<>();
//        stockWallet1.put(StockType.AMAZON, 10);
//        stockWallet1.put(StockType.GOOGLE, 10);
//
//        Client c1 = new Client("Client1", 1000, stockWallet1);
//        ClientManager.addClient(c1);
//
//        Thread threadClient1 = new Thread(() -> {
//            c1.postOffer(StockType.AMAZON, 6, 5, OfferType.BUY);
//            c1.modifyOfferByStocks(1, 4);
//        });
//
//        Map<StockType, Integer> stockWallet2 = new HashMap<>();
//        stockWallet2.put(StockType.AMAZON, 40);
//        stockWallet2.put(StockType.GOOGLE, 10);
//
//        Client c2 = new Client("Client2", 1000, stockWallet2);
//        ClientManager.addClient(c2);
//
//        Thread threadClient2 = new Thread(() -> {
//            c2.postOffer(StockType.AMAZON, 8, 5, OfferType.SELL);
//            c2.postOffer(StockType.AMAZON, 8, 5, OfferType.SELL);
//        });
//
//        Thread printClients = new Thread(() -> {
//            ClientManager.print();
//        });
//
//        threadClient1.start();
//        threadClient2.start();
//
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        printClients.start();
    }
}