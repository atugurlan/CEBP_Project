import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';

const Home = () => {
    const { user } = useAuth(); // Access the logged-in user
    const [stockWallet, setStockWallet] = useState([]); // To fetch available stocks
    const [allStockTypes, setAllStockTypes] = useState([]); // To fetch all stock types
    const [moneyWallet, setMoneyWallet] = useState(0); // To fetch the money wallet
    const [offerDetails, setOfferDetails] = useState({
        stockType: '',
        noOfStocks: '',
        pricePerStock: '',
        offerType: 'SELL',
    }); // Form state
    const [showForm, setShowForm] = useState(false); // Control form visibility
    const [message, setMessage] = useState(null);
    const [error, setError] = useState(null);

    // Fetch stock wallet and money wallet on component mount
    useEffect(() => {
        const fetchDetails = async () => {
            try {
                const stockResponse = await axios.get(`http://localhost:8080/stock-wallets/client/${user.id}`);
                setStockWallet(stockResponse.data);

                const moneyResponse = await axios.get(`http://localhost:8080/clients/${user.id}/money-wallet`);
                setMoneyWallet(moneyResponse.data.moneyWallet);

                // Fetch all stock types from backend
                const stockTypesResponse = await axios.get('http://localhost:8080/stock-types');
                setAllStockTypes(stockTypesResponse.data);
            } catch (err) {
                setError('Failed to fetch wallet or stock type details');
            }
        };

        fetchDetails();
    }, [user.id]);

    // Handle form submission
    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage(null);
        setError(null);

        // Validate noOfStocks and pricePerStock
        if (offerDetails.offerType === 'SELL') {
            const selectedStock = stockWallet.find((stock) => stock.stockType === offerDetails.stockType);
            if (!selectedStock) {
                setError('Invalid stock type selected.');
                return;
            }

            if (parseInt(offerDetails.noOfStocks, 10) > selectedStock.quantity) {
                setError('The number of stocks exceeds the available quantity.');
                return;
            }
        }

        const maxPrice = moneyWallet / parseInt(offerDetails.noOfStocks, 10);
        if (parseFloat(offerDetails.pricePerStock) > maxPrice) {
            setError(`Price per stock cannot exceed ${maxPrice.toFixed(2)}.`);
            return;
        }

        // Prepare and send POST request
        try {
            const offerPayload = {
                client: user.id,
                stockType: offerDetails.stockType,
                noOfStocks: parseInt(offerDetails.noOfStocks, 10),
                pricePerStock: parseFloat(offerDetails.pricePerStock),
                offerType: offerDetails.offerType,
            };

            await axios.post('http://localhost:8080/offers', offerPayload);

            setMessage('Offer successfully added!');
            setOfferDetails({
                stockType: '',
                noOfStocks: '',
                pricePerStock: '',
                offerType: 'SELL',
            }); // Reset form
            setShowForm(false); // Hide form after successful submission
        } catch (err) {
            setError('Failed to add offer. Please try again.');
        }
    };

    return (
        <div>
            <h1>Welcome to the Home Page</h1>
            <p>This is the main dashboard.</p>

            <button
                onClick={() => {
                    setShowForm(true);
                    setMessage(null);
                    setError(null);
                }}
            >
                Add an Offer
            </button>

            {showForm && (
                <form onSubmit={handleSubmit}>
                    <h3>Add a New Offer</h3>

                    <label htmlFor="offerType">Offer Type:</label>
                    <select
                        id="offerType"
                        value={offerDetails.offerType}
                        onChange={(e) =>
                            setOfferDetails({
                                ...offerDetails,
                                offerType: e.target.value,
                                stockType: '', // Reset stock type when changing offer type
                            })
                        }
                        required
                    >
                        <option value="SELL">SELL</option>
                        <option value="BUY">BUY</option>
                    </select>

                    <label htmlFor="stockType">Stock Type:</label>
                    <select
                        id="stockType"
                        value={offerDetails.stockType}
                        onChange={(e) => setOfferDetails({ ...offerDetails, stockType: e.target.value })}
                        required
                    >
                        <option value="">Select a stock type</option>
                        {offerDetails.offerType === 'SELL'
                            ? stockWallet.map((stock, index) => (
                                  <option key={index} value={stock.stockType}>
                                      {stock.stockType} (Available: {stock.quantity})
                                  </option>
                              ))
                            : allStockTypes.map((type, index) => (
                                  <option key={index} value={type}>
                                      {type}
                                  </option>
                              ))}
                    </select>

                    <label htmlFor="noOfStocks">Number of Stocks:</label>
                    <input
                        id="noOfStocks"
                        type="number"
                        value={offerDetails.noOfStocks}
                        onChange={(e) => setOfferDetails({ ...offerDetails, noOfStocks: e.target.value })}
                        required
                    />

                    <label htmlFor="pricePerStock">Price Per Stock:</label>
                    <input
                        id="pricePerStock"
                        type="number"
                        step="0.01"
                        value={offerDetails.pricePerStock}
                        onChange={(e) => setOfferDetails({ ...offerDetails, pricePerStock: e.target.value })}
                        required
                    />

                    <button type="submit">Submit Offer</button>
                </form>
            )}

            {error && <p style={{ color: 'red' }}>{error}</p>}
            {message && <p style={{ color: 'green' }}>{message}</p>}
        </div>
    );
};

export default Home;
