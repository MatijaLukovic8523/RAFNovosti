import { useContext, useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../../context/AuthContext';
import api from '../../api/api';

const Navbar = () => {
    const { user, logout } = useContext(AuthContext);
    const [categories, setCategories] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        api.get('/categories')
            .then(res => setCategories(res.data))
            .catch(err => console.error("Greška pri učitavanju:", err));
    }, []);

    const handleSignOut = () => {
        logout();
        navigate('/');
    };

    const navLinkStyle = { color: '#e2e8f0', textDecoration: 'none', fontSize: '0.95rem', fontWeight: '500' };
    const authBtnStyle = { padding: '8px 16px', borderRadius: '8px', border: '1px solid #475569', backgroundColor: 'transparent', color: 'white', cursor: 'pointer' };

    return (
        <nav style={{ backgroundColor: '#1e293b', padding: '1rem 2rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
                <Link to="/" style={{ fontSize: '1.5rem', fontWeight: 'bold', color: 'white', textDecoration: 'none' }}>
                    RAF<span style={{ color: '#3b82f6' }}>{user ? 'CMS' : 'Novosti'}</span>
                </Link>
            </div>

            {user ? (
                <div style={{ display: 'flex', gap: '20px', alignItems: 'center' }}>
                    <Link to="/vesti" style={navLinkStyle}>Vesti</Link>
                    <Link to="/kategorije" style={navLinkStyle}>Kategorije</Link>
                    <button onClick={handleSignOut} style={authBtnStyle}>Sign Out</button>
                </div>
            ) : (
                <div style={{ display: 'flex', gap: '20px', alignItems: 'center' }}>
                    <Link to="/" style={navLinkStyle}>Početna</Link>
                    <Link to="/najcitanije" style={navLinkStyle}>Najčitanije</Link>
                    {categories.map(cat => (
                        <Link key={cat.id} to={`/kategorija/${cat.id}`} style={navLinkStyle}>
                            {cat.name}
                        </Link>
                    ))}
                    <Link to="/login" style={authBtnStyle}>Sign In</Link>
                </div>
            )}
        </nav>
    );
};

export default Navbar;