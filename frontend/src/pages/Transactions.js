import React, { useState, useEffect } from "react";
import axios from "axios";
import { useAuth } from "../context/AuthContext";

import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "../components/ui/card";
import { Button } from "../components/ui/button";
import { ScrollArea } from "../components/ui/scroll-area";
import { Spinner } from "../components/ui/spinner"; // Custom spinner component

const Transactions = () => {
  const { user } = useAuth(); // Access the logged-in user
  const [transactions, setTransactions] = useState([]); // All transactions
  const [filteredTransactions, setFilteredTransactions] = useState([]); // Filtered transactions for display
  const [showSelling, setShowSelling] = useState(true); // Toggle between selling and buying
  const [loading, setLoading] = useState(true); // Loading state
  const [error, setError] = useState(null);

  // Fetch transactions on component mount
  useEffect(() => {
    const fetchTransactions = async () => {
      setLoading(true);
      setError(null);

      try {
        // Fetch selling transactions
        const sellingResponse = await axios.get(
          `http://localhost:8080/transactions/selling-client/${user.id}`
        );

        // Fetch buying transactions
        const buyingResponse = await axios.get(
          `http://localhost:8080/transactions/buying-client/${user.id}`
        );

        setTransactions([
          ...sellingResponse.data.map((t) => ({ ...t, type: "SELLING" })),
          ...buyingResponse.data.map((t) => ({ ...t, type: "BUYING" })),
        ]);
      } catch (err) {
        setError("Failed to fetch transactions. Please try again later.");
      } finally {
        setLoading(false); // Stop loading spinner
      }
    };

    fetchTransactions();
  }, [user.id]);

  // Filter transactions based on the current view (selling or buying)
  useEffect(() => {
    if (showSelling) {
      setFilteredTransactions(transactions.filter((t) => t.type === "SELLING"));
    } else {
      setFilteredTransactions(transactions.filter((t) => t.type === "BUYING"));
    }
  }, [transactions, showSelling]);

  // Determine dynamic height for ScrollArea
  const scrollAreaHeight =
    filteredTransactions.length > 2
      ? "h-[36rem]" // Full height for more than 2 rows
      : "h-auto"; // Dynamic height for 1 or 2 rows

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-50">
      {/* Page Header */}
      <header className="text-center mb-8">
        <h1 className="text-5xl font-bold font-sans text-gray-800 mb-4">
          {showSelling ? "Your Selling Transactions" : "Your Buying Transactions"}
        </h1>
        <p className="text-lg text-gray-600">
          View details of your transactions below.
        </p>
      </header>

      {/* Toggle Button */}
      <Button onClick={() => setShowSelling((prev) => !prev)} className="mb-6">
        {showSelling ? "View Buying Transactions" : "View Selling Transactions"}
      </Button>

      {/* Display Transactions */}
      {loading ? (
        <Spinner className="w-12 h-12 text-gray-500 animate-spin" /> // Display spinner during loading
      ) : error ? (
        <p className="text-red-500">{error}</p>
      ) : filteredTransactions.length === 0 ? (
        <p className="text-gray-600">
          {showSelling
            ? "No selling transactions found."
            : "No buying transactions found."}
        </p>
      ) : (
        <ScrollArea className={`w-full max-w-4xl rounded-md border bg-white ${scrollAreaHeight}`}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 p-4">
            {filteredTransactions.map((transaction, index) => (
              <Card key={index} className="p-6 text-center">
                <CardHeader>
                  <CardTitle className="text-xl font-semibold">
                    Transaction #{index + 1}
                  </CardTitle>
                  <CardDescription className="text-gray-600 text-lg">
                    {transaction.type === "SELLING" ? "Selling" : "Buying"} Transaction
                  </CardDescription>
                </CardHeader>
                <CardContent className="text-gray-800 space-y-2 text-lg">
                  <p>
                    <strong>Stock Type:</strong> {transaction.tradedStockType}
                  </p>
                  <p>
                    <strong>Quantity:</strong> {transaction.noOfTradedStocks}
                  </p>
                  <p>
                    <strong>Price Per Stock:</strong> $
                    {transaction.pricePerStock.toFixed(2)}
                  </p>
                  <p>
                    <strong>Total Price:</strong> ${transaction.totalPrice.toFixed(2)}
                  </p>
                  {transaction.type === "SELLING" ? (
                    <p>
                      <strong>Buyer ID:</strong> {transaction.buyingClientId}
                    </p>
                  ) : (
                    <p>
                      <strong>Seller ID:</strong> {transaction.sellingClientId}
                    </p>
                  )}
                  <p>
                    <strong>Selling Offer ID:</strong> {transaction.sellingOfferId}
                  </p>
                  <p>
                    <strong>Buying Offer ID:</strong> {transaction.buyingOfferId}
                  </p>
                </CardContent>
              </Card>
            ))}
          </div>
        </ScrollArea>
      )}
    </div>
  );
};

export default Transactions;
