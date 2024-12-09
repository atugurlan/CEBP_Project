import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext'; // Import AuthContext
import '../styles/Navbar.css';

const Navbar = () => {
    const { logout } = useAuth(); // Access logout function from AuthContext

    return (
        <nav>
            <ul>
                <li><Link to="/home">Home</Link></li>
                <li><Link to="/offers">Offers</Link></li>
                <li><Link to="/transactions">Transactions</Link></li>
                <li><Link to="/profile">Profile</Link></li>
                <li>
                    <button onClick={logout} style={{ marginLeft: '1rem' }}>
                        Logout
                    </button>
                </li>
            </ul>
        </nav>
    );
};

export default Navbar;
