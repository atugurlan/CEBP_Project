import React, { useState, useEffect } from "react";
import axios from "axios";
import { useAuth } from "../context/AuthContext";

import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "../components/ui/card";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";

const Profile = () => {
  const { user, logout } = useAuth();
  const [stockWallet, setStockWallet] = useState([]);
  const [loadingStockWallet, setLoadingStockWallet] = useState(false); // Loading state for stock wallet
  const [showStockWallet, setShowStockWallet] = useState(false); // Toggle state for stock wallet
  const [allStockTypes, setAllStockTypes] = useState([]);
  const [moneyWallet, setMoneyWallet] = useState("");
  const [currentMoneyWallet, setCurrentMoneyWallet] = useState(null);
  const [showMoneyWallet, setShowMoneyWallet] = useState(false); // Toggle state for money wallet
  const [newStock, setNewStock] = useState({ stockType: "", quantity: "" });
  const [error, setError] = useState(null);
  const [message, setMessage] = useState(null);

  useEffect(() => {
    const fetchAllStockTypes = async () => {
      try {
        const response = await axios.get("http://localhost:8080/stock-types");
        setAllStockTypes(response.data);
      } catch (err) {
        setError("Failed to fetch stock types.");
      }
    };

    fetchAllStockTypes();
  }, []);

  const handleViewStockWallet = async () => {
    if (showStockWallet) {
      setShowStockWallet(false); // Hide stock wallet
      return;
    }

    setError(null);
    setMessage(null);
    setLoadingStockWallet(true);

    try {
      const response = await axios.get(`http://localhost:8080/stock-wallets/client/${user.id}`);
      const walletData = response.data.map((item) => ({
        stockType: item.stockType,
        quantity: item.quantity,
      }));
      setStockWallet(walletData);
      setShowStockWallet(true); // Show stock wallet
    } catch (err) {
      setError("Failed to fetch stock wallet");
    } finally {
      setLoadingStockWallet(false);
    }
  };

  const handleViewMoneyWallet = async () => {
    if (showMoneyWallet) {
      setShowMoneyWallet(false); // Hide money wallet
      return;
    }

    try {
      const response = await axios.get(`http://localhost:8080/clients/${user.id}/money-wallet`);
      setCurrentMoneyWallet(response.data.moneyWallet);
      setShowMoneyWallet(true); // Show money wallet
    } catch (err) {
      setError("Failed to fetch money wallet");
    }
  };

  const handleAddStock = async () => {
    setError(null);
    setMessage(null);

    if (!newStock.stockType || !newStock.quantity) {
      setError("Please select a stock type and enter a quantity.");
      return;
    }

    if (newStock.quantity < 0) {
      setError("Number of stocks cannot be negative.");
      setMessage("");
      return;
    }

    try {
      await axios.post("http://localhost:8080/stock-wallets", {
        client: user.id,
        stockType: newStock.stockType,
        quantity: parseInt(newStock.quantity, 10),
      });
      setMessage("Stock added successfully.");
      setNewStock({ stockType: "", quantity: "" });
      handleViewStockWallet();
    } catch (err) {
      setError("Failed to add stock");
    }
  };

  const handleUpdateMoneyWallet = async () => {
    setError(null);

    if (!moneyWallet) {
      setError("Please enter a new money wallet amount.");
      return;
    }

    if (moneyWallet < 0) {
      setError("Balance can't be negative.");
      setMessage("");
      return;
    }

    try {
      await axios.patch(`http://localhost:8080/clients/${user.id}/money-wallet`, {
        moneyWallet: parseFloat(moneyWallet),
      });
      setMessage("Money wallet updated successfully.");
      setMoneyWallet("");
      await handleViewMoneyWallet();
    } catch (err) {
      setError("Failed to update money wallet");
    }
  };

  if (!user) {
    return <p>Please log in to view your profile.</p>;
  }

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-50 space-y-6">
      {/* Header */}
      <header className="text-center">
        <h1 className="text-5xl font-bold font-sans text-gray-800 mb-4">
          Welcome, {user.email}
        </h1>
        <p className="text-lg text-gray-600">Manage your profile information here.</p>
      </header>

      {/* Stock Wallet */}
      <Card className="w-full max-w-2xl">
        <CardHeader>
          <CardTitle>Stock Wallet</CardTitle>
          <CardDescription>View and manage your stock wallet.</CardDescription>
        </CardHeader>
        <CardContent>
          <Button onClick={handleViewStockWallet} className="mb-4">
            {showStockWallet ? "Cancel" : "View Stock Wallet"}
          </Button>
          {loadingStockWallet ? (
            <p>Loading stock wallet...</p>
          ) : (
            showStockWallet && (
              <ul className="space-y-2">
                {stockWallet.map((stock, index) => (
                  <li key={index}>
                    <strong>{stock.stockType}:</strong> {stock.quantity}
                  </li>
                ))}
              </ul>
            )
          )}
        </CardContent>
      </Card>

      {/* Money Wallet */}
      <Card className="w-full max-w-2xl">
        <CardHeader>
          <CardTitle>Money Wallet</CardTitle>
          <CardDescription>View and update your money wallet balance.</CardDescription>
        </CardHeader>
        <CardContent>
          <Button onClick={handleViewMoneyWallet} className="mb-4">
            {showMoneyWallet ? "Cancel" : "View Money Wallet"}
          </Button>
          {showMoneyWallet && currentMoneyWallet !== null && (
            <p>
              <strong>Current Balance:</strong> ${currentMoneyWallet}
            </p>
          )}
        </CardContent>
      </Card>

      {/* Add Stock */}
      <Card className="w-full max-w-2xl">
        <CardHeader>
          <CardTitle>Add or Update Stock</CardTitle>
          <CardDescription>Add a new stock to your wallet or increase the quantity of an existing one.</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            <select
              value={newStock.stockType}
              onChange={(e) => setNewStock({ ...newStock, stockType: e.target.value })}
              className="border rounded-md p-2 w-full"
            >
              <option value="">Select Stock Type</option>
              {allStockTypes.map((type, index) => (
                <option key={index} value={type}>
                  {type}
                </option>
              ))}
            </select>
            <Input
              type="number"
              placeholder="Quantity"
              value={newStock.quantity}
              onChange={(e) => setNewStock({ ...newStock, quantity: e.target.value })}
            />
            <Button onClick={handleAddStock}>Add Stock</Button>
          </div>
        </CardContent>
      </Card>

      {/* Update Money Wallet */}
      <Card className="w-full max-w-2xl">
        <CardHeader>
          <CardTitle>Update Money Wallet</CardTitle>
          <CardDescription>Update your wallet balance.</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            <Input
              type="number"
              placeholder="New Money Wallet Amount"
              value={moneyWallet}
              onChange={(e) => setMoneyWallet(e.target.value)}
            />
            <Button onClick={handleUpdateMoneyWallet}>Update Money Wallet</Button>
          </div>
        </CardContent>
      </Card>

      {/* Error and Success Messages */}
      {error && <p className="text-red-500">{error}</p>}
      {message && <p className="text-green-500">{message}</p>}

      {/* Logout */}
      <Button onClick={logout} className="mt-6">
        Logout
      </Button>
    </div>
  );
};

export default Profile;