import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';

const Offers = () => {
    const { user } = useAuth(); // Access the logged-in user
    const [offers, setOffers] = useState([]); // State to store offers
    const [error, setError] = useState(null);

    // Fetch offers of the logged-in client on component mount
    useEffect(() => {
        const fetchOffers = async () => {
            try {
                const response = await axios.get(`http://localhost:8080/offers/client/${user.id}`);
                setOffers(response.data); // Set offers data
            } catch (err) {
                setError('Failed to fetch offers. Please try again later.');
            }
        };

        fetchOffers();
    }, [user.id]);

    return (
        <div>
            <h1>Your Offers</h1>

            {error && <p style={{ color: 'red' }}>{error}</p>}

            {offers.length === 0 && !error ? (
                <p>No offers found. Create one to get started!</p>
            ) : (
                <ul>
                    {offers.map((offer, index) => (
                        <li key={index}>
                            <p><strong>Stock Type:</strong> {offer.stockType}</p>
                            <p><strong>Number of Stocks:</strong> {offer.noOfStocks}</p>
                            <p><strong>Price Per Stock:</strong> ${offer.pricePerStock.toFixed(2)}</p>
                            <p><strong>Offer Type:</strong> {offer.offerType}</p>
                            <p><strong>Status:</strong> {offer.offerStatus}</p>
                            <hr />
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default Offers;
