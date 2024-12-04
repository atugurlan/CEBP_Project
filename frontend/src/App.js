import React, { useState } from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import Login from './components/Login';
import Register from './components/Register';
import Navbar from './components/Navbar';
import Home from './components/Home';
import Offers from './components/Offers';
import Transactions from './components/Transactions';
import Profile from './components/Profile';

function App() {
    const [isLoggedIn, setIsLoggedIn] = useState(false); // Simulate login state

    const handleLogin = () => {
        setIsLoggedIn(true);
    };

    const handleLogout = () => {
        setIsLoggedIn(false);
    };

    return (
        <Router>
            {isLoggedIn && <Navbar onLogout={handleLogout} />}
            <Routes>
                {/* Public Routes */}
                <Route path="/" element={isLoggedIn ? <Navigate to="/home" /> : <Login onLogin={handleLogin} />} />
                <Route path="/register" element={<Register />} />

                {/* Protected Routes */}
                {isLoggedIn && (
                    <>
                        <Route path="/home" element={<Home />} />
                        <Route path="/offers" element={<Offers />} />
                        <Route path="/transactions" element={<Transactions />} />
                        <Route path="/profile" element={<Profile />} />
                    </>
                )}

                {/* Redirect unknown routes */}
                <Route path="*" element={<Navigate to="/" />} />
            </Routes>
        </Router>
    );
}

export default App;
