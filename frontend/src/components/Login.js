import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';

const Login = () => {
    const [form, setForm] = useState({ email: '', password: '' });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const { login } = useAuth(); // Access login function from AuthContext
    const navigate = useNavigate();

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!form.email || !form.password) {
            alert('Please fill all required fields');
            return;
        }

        setLoading(true);
        setError(null);

        try {
            const response = await axios.post('http://localhost:8080/clients/login', {
                email: form.email,
                password: form.password,
            });

            const userData = response.data; // Assuming response contains id, email, and password
            login(userData); // Save user data in AuthContext

            navigate('/home'); // Redirect to Home page
        } catch (err) {
            setError(err.response?.data?.message || 'Invalid email or password');
        } finally {
            setLoading(false);
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <h2>Login</h2>
            <input
                name="email"
                placeholder="Email"
                type="email"
                value={form.email}
                onChange={handleChange}
                required
            />
            <input
                name="password"
                placeholder="Password"
                type="password"
                value={form.password}
                onChange={handleChange}
                required
            />
            <button type="submit" disabled={loading}>
                {loading ? 'Logging in...' : 'Login'}
            </button>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            <p>
                Don't have an account? <Link to="/register">Register</Link>
            </p>
        </form>
    );
};

export default Login;
