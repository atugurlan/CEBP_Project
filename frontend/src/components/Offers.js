import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';

const Offers = () => {
    const { user } = useAuth(); // Access the logged-in user
    const [offers, setOffers] = useState([]); // State to store all offers
    const [filteredOffers, setFilteredOffers] = useState([]); // Offers displayed based on status
    const [showHistory, setShowHistory] = useState(false); // Toggle between pending and history view
    const [error, setError] = useState(null);

    // Fetch offers of the logged-in client on component mount
    useEffect(() => {
        const fetchOffers = async () => {
            try {
                const response = await axios.get(`http://localhost:8080/offers/client/${user.id}`);
                setOffers(response.data); // Set all offers data
            } catch (err) {
                setError('Failed to fetch offers. Please try again later.');
            }
        };

        fetchOffers();
    }, [user.id]);

    // Filter offers based on the view (Pending or History)
    useEffect(() => {
        if (showHistory) {
            setFilteredOffers(
                offers.filter(
                    (offer) =>
                        offer.offerStatus === 'COMPLETED' || offer.offerStatus === 'CANCELLED'
                )
            );
        } else {
            setFilteredOffers(
                offers.filter((offer) => offer.offerStatus === 'PENDING')
            );
        }
    }, [offers, showHistory]);

    // Handle offer cancellation
    const handleCancelOffer = async (offerId) => {
        try {
            const response = await axios.put(`http://localhost:8080/offers/${offerId}/cancel`);
            if (response.status === 204) {
                // Update the offers state to reflect the cancellation
                setOffers((prevOffers) =>
                    prevOffers.map((offer) =>
                        offer.id === offerId ? { ...offer, offerStatus: 'CANCELLED' } : offer
                    )
                );
            } else {
                setError('Failed to cancel the offer. Please try again.');
            }
        } catch (err) {
            setError('Failed to cancel the offer. Please try again.');
        }
    };

    return (
        <div>
            <h1>Your Offers</h1>

            {error && <p style={{ color: 'red' }}>{error}</p>}

            {/* Toggle Button */}
            <button
                onClick={() => setShowHistory((prev) => !prev)}
                style={{ marginBottom: '20px' }}
            >
                {showHistory ? 'View Pending Offers' : 'View Offers History'}
            </button>

            {/* Display Offers */}
            {filteredOffers.length === 0 && !error ? (
                <p>
                    {showHistory
                        ? 'No completed or cancelled offers found.'
                        : 'No pending offers found. Create one to get started!'}
                </p>
            ) : (
                <ul>
                    {filteredOffers.map((offer, index) => (
                        <li key={index}>
                            <p><strong>Stock Type:</strong> {offer.stockType}</p>
                            <p><strong>Number of Stocks:</strong> {offer.noOfStocks}</p>
                            <p><strong>Price Per Stock:</strong> ${offer.pricePerStock.toFixed(2)}</p>
                            <p><strong>Offer Type:</strong> {offer.offerType}</p>
                            <p><strong>Status:</strong> {offer.offerStatus}</p>
                            {offer.offerStatus === 'PENDING' && (
                                <button
                                    onClick={() => handleCancelOffer(offer.id)}
                                    style={{ marginTop: '10px'}}
                                >
                                    Cancel Offer
                                </button>
                            )}
                            <hr />
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default Offers;
