import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';

const Transactions = () => {
    const { user } = useAuth(); // Access the logged-in user
    const [transactions, setTransactions] = useState([]); // All transactions
    const [filteredTransactions, setFilteredTransactions] = useState([]); // Filtered transactions for display
    const [showSelling, setShowSelling] = useState(true); // Toggle between selling and buying
    const [error, setError] = useState(null);

    // Fetch transactions on component mount
    useEffect(() => {
        const fetchTransactions = async () => {
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
                    ...sellingResponse.data.map((t) => ({ ...t, type: 'SELLING' })),
                    ...buyingResponse.data.map((t) => ({ ...t, type: 'BUYING' })),
                ]);
            } catch (err) {
                setError('Failed to fetch transactions. Please try again later.');
            }
        };

        fetchTransactions();
    }, [user.id]);

    // Filter transactions based on the current view (selling or buying)
    useEffect(() => {
        if (showSelling) {
            setFilteredTransactions(transactions.filter((t) => t.type === 'SELLING'));
        } else {
            setFilteredTransactions(transactions.filter((t) => t.type === 'BUYING'));
        }
    }, [transactions, showSelling]);

    return (
        <div>
            <h1>{showSelling ? 'Your Selling Transactions' : 'Your Buying Transactions'}</h1>

            {error && <p style={{ color: 'red' }}>{error}</p>}

            {/* Toggle Button */}
            <button
                onClick={() => setShowSelling((prev) => !prev)}
                style={{ marginBottom: '20px' }}
            >
                {showSelling ? 'View Buying Transactions' : 'View Selling Transactions'}
            </button>

            {/* Display Transactions */}
            {filteredTransactions.length === 0 && !error ? (
                <p>
                    {showSelling
                        ? 'No selling transactions found.'
                        : 'No buying transactions found.'}
                </p>
            ) : (
                <ul>
                    {filteredTransactions.map((transaction, index) => (
                        <li key={index}>
                            <p><strong>Stock Type:</strong> {transaction.tradedStockType}</p>
                            <p><strong>Quantity:</strong> {transaction.noOfTradedStocks}</p>
                            <p><strong>Price Per Stock:</strong> ${transaction.pricePerStock.toFixed(2)}</p>
                            <p><strong>Total Price:</strong> ${transaction.totalPrice.toFixed(2)}</p>
                            {transaction.type === 'SELLING' ? (
                                <p><strong>Buyer ID:</strong> {transaction.buyingClientId}</p>
                            ) : (
                                <p><strong>Seller ID:</strong> {transaction.sellingClientId}</p>
                            )}
                            <p><strong>Selling Offer ID:</strong> {transaction.sellingOfferId}</p>
                            <p><strong>Buying Offer ID:</strong> {transaction.buyingOfferId}</p>
                            <hr />
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default Transactions;
