import { useEffect, useState } from 'react';
import api from '../../api/api';
import { Link } from 'react-router-dom';

const MostRead = () => {
    const [news, setNews] = useState([]);
    const [page, setPage] = useState(1);

    useEffect(() => {
        api.get(`/news?page=${page}&sortBy=visits`)
           .then(res => setNews(res.data))
           .catch(err => console.error("Greška pri učitavanju:", err));
    }, [page]);

    return (
        <div className="container">
            <h1>Najčitanije vesti</h1>

            <div className="news-list">
                {news.map(n => (
                    <div key={n.id} className="card" style={{ marginBottom: '20px' }}>
                        <Link to={`/vest/${n.id}`} style={{ textDecoration: 'none', color: 'inherit' }}>
                            <h2>{n.title}</h2>
                        </Link>

                        <div style={{ fontSize: '0.85rem', color: '#64748b', marginBottom: '10px' }}>
                            <span><strong>Pregleda:</strong> {n.visits}</span> |
                            <span> <strong>Autor:</strong> {n.authorName}</span>
                        </div>
                    </div>
                ))}
            </div>

            {/* Paginacija - ista logika kao na Home stranici */}
            <div className="pagination-container">
                <button disabled={page === 1} onClick={() => setPage(page - 1)}>Prethodna</button>
                <span> Strana {page} </span>
                <button onClick={() => setPage(page + 1)}>Sledeća</button>
            </div>
        </div>
    );
};

export default MostRead;