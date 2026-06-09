import { useEffect, useState } from 'react';
import api from '../../api/api';
import { Link } from 'react-router-dom';

const Home = () => {
    const [news, setNews] = useState([]);
    const [page, setPage] = useState(1);

    useEffect(() => {
        api.get(`/news?page=${page}&sortBy=publishedAt`)
           .then(res => setNews(res.data))
           .catch(err => console.error("Greška pri učitavanju vesti:", err));
    }, [page]);

    const formatDate = (dateString) => {
        if (!dateString) return 'Nepoznat datum';
        return new Date(dateString).toLocaleDateString('sr-RS', {
            year: 'numeric', month: 'long', day: 'numeric'
        });
    };

    return (
        <div className="container">
            <h1>Najnovije vesti</h1>

            {news.map(n => (
                <div key={n.id} className="card" style={{ marginBottom: '20px' }}>
                    <Link to={`/vest/${n.id}`} style={{ textDecoration: 'none', color: 'inherit' }}>
                        <h2 style={{ marginTop: 0 }}>{n.title}</h2>
                    </Link>

                    {/* Ovde su ispravljena polja prema tvom JSON-u */}
                    <div style={{ fontSize: '0.85rem', color: '#64748b', marginBottom: '10px' }}>
                        <span><strong>Autor:</strong> {n.authorName || 'Nepoznat'}</span> |
                        <span> <strong>Kategorija:</strong> {n.category?.name || 'Bez kategorije'}</span> |
                        <span> <strong>Datum:</strong> {formatDate(n.publishedAt)}</span>
                    </div>

                    <p>{n.text?.substring(0, 150)}...</p>
                </div>
            ))}

            <div className="pagination-container">
                <button disabled={page === 1} onClick={() => setPage(page - 1)}>Prethodna</button>
                <span> Strana {page} </span>
                <button onClick={() => setPage(page + 1)}>Sledeća</button>
            </div>
        </div>
    );
};

export default Home;