import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import Login from './components/Login';
import Register from './components/Register';
import Navbar from './components/Navbar';
import Home from './components/Home';
import Offers from './components/Offers';
import Transactions from './components/Transactions';
import Profile from './components/Profile';
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
