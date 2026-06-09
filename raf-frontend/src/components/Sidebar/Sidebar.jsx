import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../../api/api';

const Sidebar = () => {
    const [popularNews, setPopularNews] = useState([]);

    useEffect(() => {
        api.get('/news/popular')
            .then(res => setPopularNews(res.data))
            .catch(err => console.error("Greška pri učitavanju popularnih:", err));
    }, []);

    return (
        <aside style={{
            padding: '20px',
            borderLeft: '1px solid #e2e8f0',
            height: '100%'
        }}>
            <h3 style={{ marginTop: 0, marginBottom: '20px', color: '#1e293b' }}>
                Najpopularnije
            </h3>

            {popularNews.map(n => (
                <div key={n.id} style={{ marginBottom: '20px' }}>
                    <Link
                        to={`/vest/${n.id}`}
                        style={{
                            textDecoration: 'none',
                            color: '#1e293b',
                            fontWeight: 'bold',
                            display: 'block',
                            marginBottom: '4px'
                        }}
                    >
                        {n.title}
                    </Link>
                    <p style={{
                        fontSize: '0.8rem',
                        color: '#64748b',
                        margin: 0
                    }}>
                        {n.visits} pregleda
                    </p>
                </div>
            ))}

            {popularNews.length === 0 && (
                <p style={{ fontSize: '0.85rem', color: '#94a3b8' }}>
                    Nema trenutno popularnih vesti.
                </p>
            )}
        </aside>
    );
};

export default Sidebar;