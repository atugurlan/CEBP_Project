import React, { useEffect, useState } from "react";
import axios from "axios";
import { useAuth } from "../context/AuthContext";

import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "../components/ui/card";
import { Button } from "../components/ui/button";
import { ScrollArea } from "../components/ui/scroll-area";
import { Spinner } from "../components/ui/spinner"; // Import Spinner component

const Offers = () => {
  const { user } = useAuth(); // Access the logged-in user
  const [offers, setOffers] = useState([]); // State to store all offers
  const [filteredOffers, setFilteredOffers] = useState([]); // Offers displayed based on status
  const [showHistory, setShowHistory] = useState(false); // Toggle between pending and history view
  const [loading, setLoading] = useState(true); // Loading state
  const [error, setError] = useState(null);

  // Fetch offers of the logged-in client on component mount
  useEffect(() => {
    const fetchOffers = async () => {
      setLoading(true);
      setError(null);

      try {
        const response = await axios.get(`http://localhost:8080/offers/client/${user.id}`);
        setOffers(response.data); // Set all offers data
      } catch (err) {
        setError("Failed to fetch offers. Please try again later.");
      } finally {
        setLoading(false);
      }
    };

    fetchOffers();
  }, [user.id]);

  // Filter offers based on the view (Pending or History)
  useEffect(() => {
    if (showHistory) {
      setFilteredOffers(
        offers.filter(
          (offer) => offer.offerStatus === "COMPLETED" || offer.offerStatus === "CANCELLED"
        )
      );
    } else {
      setFilteredOffers(offers.filter((offer) => offer.offerStatus === "PENDING"));
    }
  }, [offers, showHistory]);

  // Determine dynamic height for ScrollArea
  const scrollAreaHeight =
    filteredOffers.length > 2
      ? "h-[40rem]" // Full height for more than 2 rows
      : "h-auto"; // Dynamic height for 1 or 2 rows

  // Handle offer cancellation
  const handleCancelOffer = async (offerId) => {
    try {
      const response = await axios.put(`http://localhost:8080/offers/${offerId}/cancel`);
      if (response.status === 204) {
        // Update the offers state to reflect the cancellation
        setOffers((prevOffers) =>
          prevOffers.map((offer) =>
            offer.id === offerId ? { ...offer, offerStatus: "CANCELLED" } : offer
          )
        );
      } else {
        setError("Failed to cancel the offer. Please try again.");
      }
    } catch (err) {
      setError("Failed to cancel the offer. Please try again.");
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-50">
      {/* Page Header */}
      <header className="text-center mb-8">
        <h1 className="text-5xl font-bold font-sans text-gray-800 mb-4">Your Offers</h1>
        <p className="text-lg text-gray-600">
          View your pending or completed offers below.
        </p>
      </header>

      {/* Toggle Button */}
      <Button onClick={() => setShowHistory((prev) => !prev)} className="mb-6">
        {showHistory ? "View Pending Offers" : "View Offers History"}
      </Button>

      {/* Display Offers */}
      {loading ? (
        <Spinner className="w-12 h-12 text-gray-500 animate-spin" /> // Display spinner during loading
      ) : error ? (
        <p className="text-red-500">{error}</p>
      ) : filteredOffers.length === 0 ? (
        <p className="text-gray-600">
          {showHistory
            ? "No completed or cancelled offers found."
            : "No pending offers found. Create one to get started!"}
        </p>
      ) : (
        <ScrollArea className={`w-full max-w-4xl rounded-md border bg-white ${scrollAreaHeight}`}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 p-4">
            {filteredOffers.map((offer, index) => (
              <Card key={index} className="p-6 text-center">
                <CardHeader>
                  <CardTitle className="text-xl font-semibold">
                    {offer.stockType} ({offer.offerType})
                  </CardTitle>
                  <CardDescription className="text-gray-600 text-lg">
                    Status: {offer.offerStatus}
                  </CardDescription>
                </CardHeader>
                <CardContent className="text-gray-800 space-y-2 text-lg">
                  <p>
                    <strong>Number of Stocks:</strong> {offer.noOfStocks}
                  </p>
                  <p>
                    <strong>Price Per Stock:</strong> ${offer.pricePerStock.toFixed(2)}
                  </p>
                  {offer.offerStatus === "PENDING" && (
                    <Button
                      onClick={() => handleCancelOffer(offer.id)}
                      className="mt-4 bg-red-500 hover:bg-red-600"
                    >
                      Cancel Offer
                    </Button>
                  )}
                </CardContent>
              </Card>
            ))}
          </div>
        </ScrollArea>
      )}
    </div>
  );
};

export default Offers;
