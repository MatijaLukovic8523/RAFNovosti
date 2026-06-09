import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../../api/api';

const CategoryNews = () => {
    const { id } = useParams();
    const [news, setNews] = useState([]);
    const [page, setPage] = useState(1);

    const formatDate = (dateString) => {
        if (!dateString) return 'Nepoznat datum';
        return new Date(dateString).toLocaleDateString('sr-RS', {
            year: 'numeric', month: 'long', day: 'numeric'
        });
    };

    useEffect(() => {
        api.get(`/news/category/${id}?page=${page}`)
           .then(res => setNews(res.data))
           .catch(err => console.error("Greška pri učitavanju:", err));

        setPage(1);
    }, [id]);

    useEffect(() => {
        if (page > 1) {
            api.get(`/news/category/${id}?page=${page}`)
               .then(res => setNews(res.data));
        }
    }, [page, id]);

    return (
        <div className="container">
            <h1>Vesti iz kategorije</h1>

            <div className="news-list">
                {news.map(n => (
                    <div key={n.id} className="card" style={{ marginBottom: '20px' }}>
                        <Link to={`/vest/${n.id}`} style={{ textDecoration: 'none', color: 'inherit' }}>
                            <h2 style={{ marginTop: 0 }}>{n.title}</h2>
                        </Link>

                        {/* Meta podaci kao na Home stranici */}
                        <div style={{ fontSize: '0.85rem', color: '#64748b', marginBottom: '10px' }}>
                            <span><strong>Autor:</strong> {n.authorName || 'Nepoznat'}</span> |
                            <span> <strong>Kategorija:</strong> {n.category?.name || 'Bez kategorije'}</span> |
                            <span> <strong>Datum:</strong> {formatDate(n.publishedAt)}</span>
                        </div>

                        <p>{n.text?.substring(0, 150)}...</p>
                    </div>
                ))}
            </div>

            {/* Paginacija */}
            <div className="pagination-container">
                <button disabled={page === 1} onClick={() => setPage(page - 1)}>Prethodna</button>
                <span> Strana {page} </span>
                <button onClick={() => setPage(page + 1)}>Sledeća</button>
            </div>
        </div>
    );
};

export default CategoryNews;