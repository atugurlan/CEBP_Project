import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Navbar from './pages/Navbar';
import Home from './pages/Home';
import Offers from './pages/Offers';
import Transactions from './pages/Transactions';
import Profile from './pages/Profile';
import { useAuth } from './context/AuthContext';

function App() {
    const { user } = useAuth();

    return (
        <Router>
            {user && <Navbar />}
            <Routes>
                {/* Public Routes */}
                <Route path="/" element={user ? <Navigate to="/home" /> : <Login />} />
                <Route path="/register" element={<Register />} />

                {/* Protected Routes */}
                {user && (
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
