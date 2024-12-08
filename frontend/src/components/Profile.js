import React, { useState } from 'react';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';

const Profile = () => {
    const { user, logout } = useAuth(); // Access the logged-in user and logout function
    const [stockWallet, setStockWallet] = useState([]); // Initialize as an empty array
    const [moneyWallet, setMoneyWallet] = useState(''); // To update money wallet
    const [currentMoneyWallet, setCurrentMoneyWallet] = useState(null); // To view money wallet
    const [newStock, setNewStock] = useState({ stockType: '', quantity: '' });
    const [error, setError] = useState(null);
    const [message, setMessage] = useState(null);

    const handleViewStockWallet = async () => {
        setError(null);
        setMessage(null);

        try {
            const response = await axios.get(`http://localhost:8080/stock-wallets/client/${user.id}`);
            const walletData = response.data.map((item) => ({
                stockType: item.stockType,
                quantity: item.quantity,
            }));
            setStockWallet(walletData);
        } catch (err) {
            setError('Failed to fetch stock wallet');
        }
    };

    const handleViewMoneyWallet = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/clients/${user.id}/money-wallet`);
            setCurrentMoneyWallet(response.data.moneyWallet); // Assuming the response has `moneyWallet`
        } catch (err) {
            setError('Failed to fetch money wallet');
        }
    };

    const handleAddStock = async () => {
        setError(null);
        setMessage(null);

        if (!newStock.stockType || !newStock.quantity) {
            setError('Please select a stock type and enter a quantity.');
            return;
        }

        try {
            await axios.post('http://localhost:8080/stock-wallets', {
                client: user.id,
                stockType: newStock.stockType,
                quantity: parseInt(newStock.quantity, 10),
            });
            setMessage('Stock added successfully.');
            setNewStock({ stockType: '', quantity: '' });
            handleViewStockWallet(); // Refresh stock wallet
        } catch (err) {
            setError('Failed to add stock');
        }
    };

    const handleUpdateMoneyWallet = async () => {
        setError(null);

        if (!moneyWallet) {
            setError('Please enter a new money wallet amount.');
            return;
        }

        try {
            await axios.patch(`http://localhost:8080/clients/${user.id}/money-wallet`, {
                moneyWallet: parseFloat(moneyWallet),
            });
            setMessage('Money wallet updated successfully.');
            setMoneyWallet('');
            await handleViewMoneyWallet(); // Refresh money wallet display
        } catch (err) {
            setError('Failed to update money wallet');
        }
    };

    if (!user) {
        return <p>Please log in to view your profile.</p>;
    }

    return (
        <div>
            <h1>Welcome, {user.email}</h1>
            <p>Manage your profile information here.</p>

            {/* View Stock Wallet */}
            <button onClick={handleViewStockWallet}>View Stock Wallet</button>
            {stockWallet.length > 0 && (
                <div>
                    <h3>Your Stock Wallet</h3>
                    <ul>
                        {stockWallet.map((stock, index) => (
                            <li key={index}>
                                {stock.stockType}: {stock.quantity}
                            </li>
                        ))}
                    </ul>
                </div>
            )}
            {stockWallet.length === 0 && <p>No stocks found in your wallet.</p>}

            {/* View Money Wallet */}
            <button onClick={handleViewMoneyWallet}>View Money Wallet</button>
            {currentMoneyWallet !== null && (
                <div>
                    <h3>Your Money Wallet</h3>
                    <p>${currentMoneyWallet}</p>
                </div>
            )}

            {/* Add Stock */}
            <div>
                <h3>Add Stock</h3>
                <select
                    value={newStock.stockType}
                    onChange={(e) => setNewStock({ ...newStock, stockType: e.target.value })}
                >
                    <option value="">Select Stock Type</option>
                    <option value="AMAZON">Amazon</option>
                    <option value="APPLE">Apple</option>
                    <option value="GOOGLE">Google</option>
                </select>
                <input
                    type="number"
                    placeholder="Quantity"
                    value={newStock.quantity}
                    onChange={(e) => setNewStock({ ...newStock, quantity: e.target.value })}
                />
                <button onClick={handleAddStock}>Add Stock</button>
            </div>

            {/* Update Money Wallet */}
            <div>
                <h3>Update Money Wallet</h3>
                <input
                    type="number"
                    placeholder="New Money Wallet Amount"
                    value={moneyWallet}
                    onChange={(e) => setMoneyWallet(e.target.value)}
                />
                <button onClick={handleUpdateMoneyWallet}>Update Money Wallet</button>
            </div>

            {/* Error and Success Messages */}
            {error && <p style={{ color: 'red' }}>{error}</p>}
            {message && <p style={{ color: 'green' }}>{message}</p>}

            {/* Logout */}
            <button onClick={logout} style={{ marginTop: '20px' }}>
                Logout
            </button>
        </div>
    );
};

export default Profile;
