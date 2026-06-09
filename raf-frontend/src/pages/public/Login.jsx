import { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../../context/AuthContext';
import api from '../../api/api';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const { setUser } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleLogin = (e) => {
        e.preventDefault();

        api.post('/auth/login', { email, password })
            .then(res => {
                localStorage.setItem('token', res.data.token);

                setUser({ loggedIn: true });

                navigate('/kategorije');
            })
            .catch(err => {
                console.error("Login error:", err);
                alert("Pogrešni podaci ili greška pri prijavi.");
            });
    };

    return (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '70vh' }}>
            <div style={{ maxWidth: '400px', width: '100%' }}>
                <article className="card" style={{ padding: '30px' }}>
                    <h2 style={{ textAlign: 'center', marginBottom: '25px' }}>Prijava</h2>
                    <form onSubmit={handleLogin}>
                        <input
                            type="email"
                            placeholder="Email"
                            onChange={e => setEmail(e.target.value)}
                            required
                            style={{ width: '100%', marginBottom: '15px', padding: '10px', boxSizing: 'border-box' }}
                        />
                        <input
                            type="password"
                            placeholder="Lozinka"
                            onChange={e => setPassword(e.target.value)}
                            required
                            style={{ width: '100%', marginBottom: '25px', padding: '10px', boxSizing: 'border-box' }}
                        />
                        <button
                            type="submit"
                            style={{
                                width: '100%',
                                padding: '12px',
                                backgroundColor: '#3b82f6',
                                color: 'white',
                                border: 'none',
                                borderRadius: '6px',
                                cursor: 'pointer',
                                fontWeight: 'bold'
                            }}
                        >
                            Uloguj se
                        </button>
                    </form>
                </article>
            </div>
        </div>
    );
};

export default Login;