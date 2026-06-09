import { useState, useEffect } from 'react';
import api from '../../api/api';

const Vesti = () => {
    const [vesti, setVesti] = useState([]);
    const [kategorije, setKategorije] = useState([]);
    const [formData, setFormData] = useState({ title: '', content: '', categoryId: '' });
    const [editId, setEditId] = useState(null);

    useEffect(() => {
        fetchMyVesti();
        fetchKategorije();
    }, []);

    const fetchMyVesti = () => {
        api.get('/news/my-news')
            .then(res => setVesti(res.data))
            .catch(err => console.error("Greška pri učitavanju vesti:", err));
    };

    const fetchKategorije = () => {
        api.get('/categories')
            .then(res => setKategorije(res.data))
            .catch(err => console.error("Greška pri učitavanju kategorija:", err));
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        const payload = {
            title: formData.title,
            text: formData.content,
            category: {
                id: parseInt(formData.categoryId)
            }
        };

        const request = editId
            ? api.put(`/news/${editId}`, payload)
            : api.post('/news', payload);

        request.then(() => {
            setFormData({ title: '', content: '', categoryId: '' });
            setEditId(null);
            fetchMyVesti();
        }).catch(err => {
            console.error("Greška pri slanju:", err);
            alert("Greška pri čuvanju vesti.");
        });
    };

    const handleDelete = (id) => {
        if (window.confirm("Obriši vest?")) {
            api.delete(`/news/${id}`).then(fetchMyVesti);
        }
    };

    const startEdit = (v) => {
        setEditId(v.id);
        setFormData({
            title: v.title,
            content: v.text,
            categoryId: v.category?.id || ''
        });
    };

    return (
        <div style={{ padding: '2rem' }}>
            <h2>Moje vesti</h2>
            <form onSubmit={handleSubmit} style={{ marginBottom: '2rem', display: 'flex', gap: '10px' }}>
                <input placeholder="Naslov" value={formData.title} onChange={e => setFormData({...formData, title: e.target.value})} required />
                <input placeholder="Sadržaj" value={formData.content} onChange={e => setFormData({...formData, content: e.target.value})} required />
                <select value={formData.categoryId} onChange={e => setFormData({...formData, categoryId: e.target.value})} required>
                    <option value="">Izaberi kategoriju</option>
                    {kategorije.map(cat => <option key={cat.id} value={cat.id}>{cat.name}</option>)}
                </select>
                <button type="submit">{editId ? 'Sačuvaj' : 'Dodaj'}</button>
            </form>

            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                    <tr>
                        <th style={{padding: '10px', textAlign: 'left'}}>Naslov</th>
                        <th style={{padding: '10px', textAlign: 'left'}}>Kategorija</th>
                        <th style={{padding: '10px', textAlign: 'left'}}>Akcije</th>
                    </tr>
                </thead>
                <tbody>
                    {vesti.map(v => (
                        <tr key={v.id} style={{ borderBottom: '1px solid #ddd' }}>
                            <td style={{padding: '10px'}}>{v.title}</td>
                            <td style={{padding: '10px'}}>{v.category?.name || 'Bez kategorije'}</td>
                            <td style={{padding: '10px'}}>
                                <button onClick={() => startEdit(v)}>Izmeni</button>
                                <button onClick={() => handleDelete(v.id)} style={{ marginLeft: '10px' }}>Obriši</button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default Vesti;