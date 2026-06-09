import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api/api';

const Register = () => {
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: ''
    });
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleRegister = (e) => {
        e.preventDefault();

        api.post('/auth/register', formData)
            .then(res => {
                console.log("Uspešna registracija!");
                navigate('/login');
            })
            .catch(err => {
                console.error("Greška pri registraciji:", err);
                alert("Došlo je do greške. Proverite podatke.");
            });
    };

    return (
        <div style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            minHeight: '80vh',
            padding: '20px'
        }}>
            <div style={{ maxWidth: '400px', width: '100%' }}>
                <article className="card" style={{ padding: '30px' }}>
                    <h2 style={{ textAlign: 'center', marginBottom: '25px', color: '#1e293b' }}>
                        Registracija
                    </h2>

                    <form onSubmit={handleRegister}>
                        <div style={{ marginBottom: '15px' }}>
                            <label style={{ display: 'block', marginBottom: '5px', fontWeight: '500' }}>Ime</label>
                            <input name="firstName" onChange={handleChange} required
                                style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #cbd5e1', boxSizing: 'border-box' }} />
                        </div>
                        <div style={{ marginBottom: '15px' }}>
                            <label style={{ display: 'block', marginBottom: '5px', fontWeight: '500' }}>Prezime</label>
                            <input name="lastName" onChange={handleChange} required
                                style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #cbd5e1', boxSizing: 'border-box' }} />
                        </div>
                        <div style={{ marginBottom: '15px' }}>
                            <label style={{ display: 'block', marginBottom: '5px', fontWeight: '500' }}>Email</label>
                            <input name="email" type="email" onChange={handleChange} required
                                style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #cbd5e1', boxSizing: 'border-box' }} />
                        </div>
                        <div style={{ marginBottom: '25px' }}>
                            <label style={{ display: 'block', marginBottom: '5px', fontWeight: '500' }}>Lozinka</label>
                            <input name="password" type="password" onChange={handleChange} required
                                style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #cbd5e1', boxSizing: 'border-box' }} />
                        </div>

                        <button type="submit"
                            style={{
                                width: '100%', padding: '12px', backgroundColor: '#3b82f6', color: 'white',
                                border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold', fontSize: '1rem'
                            }}>
                            Registruj se
                        </button>
                    </form>
                </article>
            </div>
        </div>
    );
};

export default Register;