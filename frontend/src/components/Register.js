import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';

const Register = () => {
    const [form, setForm] = useState({ name: '', email: '', password: '', moneyWallet: '' });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Validate required fields
        if (!form.name || !form.email || !form.password) {
            alert('Please fill all required fields');
            return;
        }

        setLoading(true);
        setError(null);

        try {
            const response = await axios.post('http://localhost:8080/clients', {
                name: form.name,
                email: form.email,
                password: form.password,
                moneyWallet: form.moneyWallet || null, // Default to null if not provided
            });

            alert('Registration successful!');
            navigate('/');
        } catch (err) {
            setError(err.response?.data?.message || err.message || 'An error occurred');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <h2>Register</h2>
            <input
                name="name"
                placeholder="Name (required)"
                value={form.name}
                onChange={handleChange}
                required
            />
            <input
                name="email"
                placeholder="Email (required)"
                type="email"
                value={form.email}
                onChange={handleChange}
                required
            />
            <input
                name="password"
                placeholder="Password (required)"
                type="password"
                value={form.password}
                onChange={handleChange}
                required
            />
            <input
                name="moneyWallet"
                placeholder="Money Wallet (optional)"
                value={form.moneyWallet}
                onChange={handleChange}
            />
            <button type="submit" disabled={loading}>
                {loading ? 'Registering...' : 'Register'}
            </button>
            {error && <p style={{ color: 'red' }}>Error: {error}</p>}
            <p>
                Already have an account? <Link to="/">Login</Link>
            </p>
        </form>
    );
};

export default Register;
