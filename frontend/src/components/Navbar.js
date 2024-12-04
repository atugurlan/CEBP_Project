import React from 'react';
import { Link } from 'react-router-dom';
import '../styles/Navbar.css';

const Navbar = ({ onLogout }) => {
    return (
        <nav>
            <ul>
                <li><Link to="/home">Home</Link></li>
                <li><Link to="/offers">Offers</Link></li>
                <li><Link to="/transactions">Transactions</Link></li>
                <li><Link to="/profile">Profile</Link></li>
                <li>
                    <button onClick={onLogout} style={{ marginLeft: '1rem' }}>
                        Logout
                    </button>
                </li>
            </ul>
        </nav>
    );
};

export default Navbar;
