package api;
import entities.*;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import java.io.*;
import java.util.*;

public class ClientApiServer {
    private static Map<StockType, Integer> stockWallet1 = new HashMap<>() {{
        put(StockType.APPLE, 10);
        put(StockType.GOOGLE, 20);
    }};

    private static Client client = new Client("Client99", 10000, stockWallet1); // Sample client object

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.createContext("/createClient", new CreateClientHandler());
        server.createContext("/postOffer", new PostOfferHandler());
        server.createContext("/deleteOffer", new DeleteOfferHandler());
        server.createContext("/modifyOfferByStocks", new ModifyOfferByStocksHandler());
        server.createContext("/modifyOfferByPrice", new ModifyOfferByPriceHandler());
        server.createContext("/modifyOfferByStocksAndPrice", new ModifyOfferByStocksAndPriceHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8081");
        ClientManager.addClient(client);
    }

    static class CreateClientHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());

                    // Parse the parameters
                    String name = params.get("name");
                    int moneyWallet = Integer.parseInt(params.get("moneyWallet"));

                    // Parse initial stocks for the client if provided (format: STOCK_TYPE=quantity)
                    Map<StockType, Integer> stockWallet = new HashMap<>();
                    String stocks = params.get("stocks");
                    if (stocks != null) {
                        String[] stockEntries = stocks.split(",");
                        for (String stockEntry : stockEntries) {
                            String[] stockData = stockEntry.split("=");
                            StockType stockType = StockType.valueOf(stockData[0]);
                            int quantity = Integer.parseInt(stockData[1]);
                            stockWallet.put(stockType, quantity);
                        }
                    }

                    // Create the new client
                    Client newClient = new Client(name, moneyWallet, stockWallet);
                    ClientManager.addClient(newClient);

                    // Send response
                    String response = "Client created successfully with ID: " + newClient.getId();
                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } catch (Exception e) {
                    String response = "An error occurred: " + e.getMessage();
                    exchange.sendResponseHeaders(500, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                }
            } else {
                String response = "Method not allowed";
                exchange.sendResponseHeaders(405, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }

//    static class PostOfferHandler implements HttpHandler {
//        @Override
//        public void handle(HttpExchange exchange) throws IOException {
//            if ("POST".equals(exchange.getRequestMethod())) {
//                try {
//                    Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
//                    StockType stockName = StockType.valueOf(params.get("stockName"));
//                    int noOfStocks = Integer.parseInt(params.get("noOfStocks"));
//                    int pricePerStock = Integer.parseInt(params.get("pricePerStock"));
//                    OfferType offerType = OfferType.valueOf(params.get("offerType"));
//
//                    System.out.println("StockType: " + stockName);
//                    System.out.println("Number of Stocks: " + noOfStocks);
//                    System.out.println("Price per Stock: " + pricePerStock);
//                    System.out.println("OfferType: " + offerType);
//
//                    int statusCode = client.postOffer(stockName, noOfStocks, pricePerStock, offerType);
//
//                    String response;
//                    int httpStatus;
//
//                    if (statusCode == 0) {
//                        response = "Offer posted successfully.";
//                        httpStatus = 200;
//                    } else {
//                        response = "Failed to create offer. Check parameters and try again.";
//                        httpStatus = 400;
//                    }
//
//                    exchange.sendResponseHeaders(httpStatus, response.length());
//                    try (OutputStream os = exchange.getResponseBody()) {
//                        os.write(response.getBytes());
//                    }
//                } catch (Exception e) {
//                    String response = "An error occurred: " + e.getMessage();
//                    exchange.sendResponseHeaders(500, response.length());
//                    try (OutputStream os = exchange.getResponseBody()) {
//                        os.write(response.getBytes());
//                    }
//                }
//            } else {
//                String response = "Method not allowed";
//                exchange.sendResponseHeaders(405, response.length());
//                try (OutputStream os = exchange.getResponseBody()) {
//                    os.write(response.getBytes());
//                }
//            }
//        }
//    }

    static class PostOfferHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    // Read the request body
                    InputStream inputStream = exchange.getRequestBody();
                    StringBuilder requestBody = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            requestBody.append(line);
                        }
                    }

                    // Convert request body to a String
                    String jsonString = requestBody.toString();

                    // Manually parse JSON string (requires strict formatting)
                    Map<String, String> bodyParams = parseJson(jsonString);

                    // Extract and parse parameters
                    StockType stockName = StockType.valueOf(bodyParams.get("stockName"));
                    int noOfStocks = Integer.parseInt(bodyParams.get("noOfStocks"));
                    int pricePerStock = Integer.parseInt(bodyParams.get("pricePerStock"));
                    OfferType offerType = OfferType.valueOf(bodyParams.get("offerType"));

                    System.out.println("StockType: " + stockName);
                    System.out.println("Number of Stocks: " + noOfStocks);
                    System.out.println("Price per Stock: " + pricePerStock);
                    System.out.println("OfferType: " + offerType);


                    // create the client with the data from request body
                    // Call client.postOffer with parsed data
                    int statusCode = client.postOffer(stockName, noOfStocks, pricePerStock, offerType);

                    // Prepare response based on client.postOffer result
                    String response;
                    int httpStatus;

                    if (statusCode == 0) {
                        response = "Offer posted successfully.";
                        httpStatus = 200;
                    } else {
                        response = "Failed to create offer. Check parameters and try again.";
                        httpStatus = 400;
                    }

                    // Send the response
                    exchange.sendResponseHeaders(httpStatus, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } catch (Exception e) {
                    // Handle exceptions
                    String response = "An error occurred: " + e.getMessage();
                    exchange.sendResponseHeaders(500, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                }
            } else {
                // Handle unsupported methods
                String response = "Method not allowed";
                exchange.sendResponseHeaders(405, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }

        // Simple JSON parser (handles flat JSON structures)
        private Map<String, String> parseJson(String jsonString) {
            Map<String, String> result = new HashMap<>();
            jsonString = jsonString.trim().replaceAll("[{}\"]", ""); // Remove braces and quotes
            String[] pairs = jsonString.split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                result.put(keyValue[0].trim(), keyValue[1].trim());
            }
            return result;
        }
    }

    static class DeleteOfferHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("DELETE".equals(exchange.getRequestMethod())) {
                try {
                    Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
                    int offerID = Integer.parseInt(params.get("offerID"));

                    int statusCode = client.deleteOffer(offerID);
                    String response;
                    int httpStatus;

                    if (statusCode == 2) {
                        response = "There are no offers created.";
                        httpStatus = 404;
                    } else if (statusCode == 1) {
                        response = "Offer with the specified ID not found.";
                        httpStatus = 404;
                    } else {
                        response = "Offer deleted successfully.";
                        httpStatus = 200;
                    }

                    exchange.sendResponseHeaders(httpStatus, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } catch (Exception e) {
                    String response = "An error occurred: " + e.getMessage();
                    exchange.sendResponseHeaders(500, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                }
            } else {
                String response = "Method not allowed";
                exchange.sendResponseHeaders(405, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }

    static class ModifyOfferByStocksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("PUT".equals(exchange.getRequestMethod())) {
                try {
                    Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
                    int offerID = Integer.parseInt(params.get("offerID"));
                    int noOfStocks = Integer.parseInt(params.get("noOfStocks"));

                    int statusCode = client.modifyOfferByStocks(offerID, noOfStocks);
                    String response;
                    int httpStatus;

                    if (statusCode == 4) {
                        response = "Failed to update offer. Offer is being processed right now.";
                        httpStatus = 403;
                    } else if (statusCode == 3) {
                        response = "Offer with the specified ID not found.";
                        httpStatus = 404;
                    } else if (statusCode == 2) {
                        response = "The offer cannot be modified as it is completed.";
                        httpStatus = 400;
                    } else if (statusCode == 1) {
                        response = "User does not have enough stocks to change the number of stocks to the desired value in the offer.";
                        httpStatus = 403;
                    } else {
                        response = "Offer modified by stocks successfully.";
                        httpStatus = 200;
                    }

                    exchange.sendResponseHeaders(httpStatus, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } catch (Exception e) {
                    String response = "An error occurred: " + e.getMessage();
                    exchange.sendResponseHeaders(500, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                }
            } else {
                String response = "Method not allowed";
                exchange.sendResponseHeaders(405, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }

    static class ModifyOfferByPriceHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("PUT".equals(exchange.getRequestMethod())) {
                try {
                    Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
                    int offerID = Integer.parseInt(params.get("offerID"));
                    int priceOfStock = Integer.parseInt(params.get("priceOfStock"));

                    int statusCode = client.modifyOfferByPrice(offerID, priceOfStock);
                    String response;
                    int httpStatus;

                    if (statusCode == 4) {
                        response = "Failed to update offer. Offer is being processed right now.";
                        httpStatus = 403;
                    } else if (statusCode == 3) {
                        response = "Offer with the specified ID not found.";
                        httpStatus = 404;
                    } else if (statusCode == 2) {
                        response = "The offer cannot be modified as it is completed.";
                        httpStatus = 400;
                    } else if (statusCode == 1) {
                        response = "User does not have enough money to be able to pay the new price.";
                        httpStatus = 403;
                    } else {
                        response = "Offer modified by price successfully.";
                        httpStatus = 200;
                    }

                    exchange.sendResponseHeaders(httpStatus, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } catch (Exception e) {
                    String response = "An error occurred: " + e.getMessage();
                    exchange.sendResponseHeaders(500, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                }
            } else {
                String response = "Method not allowed";
                exchange.sendResponseHeaders(405, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }

    static class ModifyOfferByStocksAndPriceHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("PUT".equals(exchange.getRequestMethod())) {
                try {
                    Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
                    int offerID = Integer.parseInt(params.get("offerID"));
                    int noOfStocks = Integer.parseInt(params.get("noOfStocks"));
                    int priceOfStock = Integer.parseInt(params.get("priceOfStock"));

                    int statusCode = client.modifyOfferByStocksAndPrice(offerID, noOfStocks, priceOfStock);
                    String response;
                    int httpStatus;

                    if (statusCode == 4) {
                        response = "Failed to update offer. Offer is being processed right now.";
                        httpStatus = 403;
                    } else if (statusCode == 3) {
                        response = "Offer with the specified ID not found.";
                        httpStatus = 404;
                    } else if (statusCode == 2) {
                        response = "The offer cannot be modified as it is completed.";
                        httpStatus = 400;
                    } else if (statusCode == 1) {
                        response = "User does not have enough money or stocks to be able to update the offer.";
                        httpStatus = 403;
                    } else {
                        response = "Offer modified by stocks and price successfully.";
                        httpStatus = 200;
                    }

                    exchange.sendResponseHeaders(httpStatus, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } catch (Exception e) {
                    String response = "An error occurred: " + e.getMessage();
                    exchange.sendResponseHeaders(500, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                }
            } else {
                String response = "Method not allowed";
                exchange.sendResponseHeaders(405, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }

    private static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            result.put(entry[0], entry[1]);
        }
        return result;
    }
}
