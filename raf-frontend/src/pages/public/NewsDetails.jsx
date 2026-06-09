import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../../api/api';

const NewsDetails = () => {
    const { id } = useParams();
    const [news, setNews] = useState(null);
    const [authorName, setAuthorName] = useState('');
    const [text, setText] = useState('');

    const btnStyle = {
        cursor: 'pointer',
        padding: '8px 16px',
        borderRadius: '20px',
        border: '1px solid #ccc',
        fontWeight: 'bold',
        transition: 'all 0.2s',
        fontSize: '0.9rem'
    };

    const fetchNews = () => {
        api.get(`/news/${id}`)
            .then(res => setNews(res.data))
            .catch(err => console.error("Greška pri učitavanju vesti:", err));
    };

    useEffect(() => {
        fetchNews();
        api.post(`/news/${id}/visit`)
            .catch(err => console.error("Greška pri beleženju posete", err));
    }, [id]);

    const handleAddComment = (e) => {
        e.preventDefault();
        api.post(`/news/${id}/comments`, { authorName, text })
            .then(() => {
                setAuthorName('');
                setText('');
                fetchNews();
            })
            .catch(err => alert("Greška pri dodavanju komentara"));
    };

    const handleReaction = (commentId, type) => {
        api.post(`/news/reactions/comment/${commentId}`, { type })
            .then(() => fetchNews())
            .catch(err => console.error("Greška pri reakciji na komentar:", err));
    };

    const handleNewsReaction = (type) => {
        api.post(`/news/reactions/news/${id}`, { type })
            .then(() => fetchNews())
            .catch(err => console.error("Greška pri reakciji na vest:", err));
    };

    if (!news) return <div className="container">Učitavanje...</div>;

    return (
        <div className="container">
            <article className="card">
                {/* SREDJEN NASLOV: dodati word-wrap i maxWidth */}
                <h1 style={{
                    marginTop: 0,
                    wordWrap: 'break-word',
                    overflowWrap: 'break-word',
                    maxWidth: '100%',
                    lineHeight: '1.2'
                }}>
                    {news.title}
                </h1>

                {/* LIKE/DISLIKE ZA VEST */}
                <div style={{ marginBottom: '20px', display: 'flex', gap: '10px', alignItems: 'center' }}>
                    <button
                        onClick={() => handleNewsReaction('LIKE')}
                        style={{ ...btnStyle, backgroundColor: '#dcfce7', color: '#166534', borderColor: '#bbf7d0' }}>
                        👍 Lajkuj vest ({news.likes || 0})
                    </button>
                    <button
                        onClick={() => handleNewsReaction('DISLIKE')}
                        style={{ ...btnStyle, backgroundColor: '#fee2e2', color: '#991b1b', borderColor: '#fecaca' }}>
                        👎 Dislajkuj vest ({news.dislikes || 0})
                    </button>
                    <span style={{ marginLeft: '10px', color: '#64748b' }}>👁️ {news.visits || 0} pregleda</span>
                </div>

                <div style={{ marginBottom: '20px', color: '#64748b', fontSize: '0.9rem' }}>
                    <p><strong>Autor:</strong> {news.authorName}</p>
                    <p><strong>Datum:</strong> {new Date(news.publishedAt).toLocaleDateString('sr-RS')}</p>
                </div>

                <div style={{ lineHeight: '1.6', fontSize: '1.1rem', marginBottom: '30px' }}>{news.text}</div>

                {/* TAGOVI KAO LINKOVI */}
                {news.tags && news.tags.length > 0 && (
                    <div style={{ marginBottom: '30px' }}>
                        {news.tags.map(tag => (
                            <Link
                                key={tag.id}
                                to={`/tag/${tag.id}`}
                                style={{
                                    display: 'inline-block',
                                    backgroundColor: '#e2e8f0',
                                    padding: '5px 12px',
                                    borderRadius: '15px',
                                    marginRight: '10px',
                                    fontSize: '0.85rem',
                                    color: '#475569',
                                    textDecoration: 'none'
                                }}
                            >
                                #{tag.name}
                            </Link>
                        ))}
                    </div>
                )}

                {/* FORMA ZA KOMENTAR */}
                <div style={{ marginTop: '40px', padding: '20px', background: '#f8fafc', borderRadius: '8px' }}>
                    <h3>Dodaj komentar</h3>
                    <form onSubmit={handleAddComment}>
                        <input
                            placeholder="Vaše ime"
                            value={authorName}
                            onChange={e => setAuthorName(e.target.value)}
                            required
                            style={{ display: 'block', width: '100%', marginBottom: '10px', padding: '10px', borderRadius: '4px', border: '1px solid #cbd5e1' }}
                        />
                        <textarea
                            placeholder="Vaš komentar"
                            value={text}
                            onChange={e => setText(e.target.value)}
                            required
                            style={{ display: 'block', width: '100%', marginBottom: '10px', padding: '10px', borderRadius: '4px', border: '1px solid #cbd5e1', minHeight: '80px' }}
                        />
                        <button
                            type="submit"
                            style={{ padding: '10px 20px', cursor: 'pointer', backgroundColor: '#3b82f6', color: 'white', border: 'none', borderRadius: '4px' }}
                        >
                            Pošalji
                        </button>
                    </form>
                </div>

                {/* LISTA KOMENTARA */}
                <div style={{ marginTop: '30px' }}>
                    <h3>Komentari</h3>
                    {news.comments && news.comments
                        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
                        .map(c => (
                            <div key={c.id} style={{ borderBottom: '1px solid #e2e8f0', padding: '20px 0' }}>
                                <p style={{ margin: 0, fontWeight: 'bold' }}>{c.authorName}</p>
                                <p style={{ fontSize: '0.85rem', color: '#64748b', marginBottom: '10px' }}>{new Date(c.createdAt).toLocaleDateString()}</p>
                                <p style={{ margin: '0 0 10px 0' }}>{c.text}</p>

                                <div style={{ display: 'flex', gap: '10px' }}>
                                    <button
                                        onClick={() => handleReaction(c.id, 'LIKE')}
                                        style={{ ...btnStyle, padding: '4px 10px', fontSize: '0.8rem', backgroundColor: '#dcfce7', color: '#166534', borderColor: '#bbf7d0' }}>
                                        👍 {c.likes || 0}
                                    </button>
                                    <button
                                        onClick={() => handleReaction(c.id, 'DISLIKE')}
                                        style={{ ...btnStyle, padding: '4px 10px', fontSize: '0.8rem', backgroundColor: '#fee2e2', color: '#991b1b', borderColor: '#fecaca' }}>
                                        👎 {c.dislikes || 0}
                                    </button>
                                </div>
                            </div>
                        ))}
                    {(!news.comments || news.comments.length === 0) && <p>Nema komentara.</p>}
                </div>
            </article>
        </div>
    );
};

export default NewsDetails;