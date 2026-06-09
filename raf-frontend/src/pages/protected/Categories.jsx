import { useEffect, useState } from 'react';
import api from '../../api/api';

const Categories = () => {
    const [categories, setCategories] = useState([]);
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [editId, setEditId] = useState(null);

    useEffect(() => {
        fetchCategories();
    }, []);

    const fetchCategories = () => {
        api.get('/categories').then(res => setCategories(res.data));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const data = { name, description };
        if (editId) {
            api.put(`/categories/${editId}`, data).then(() => {
                setEditId(null);
                resetForm();
                fetchCategories();
            });
        } else {
            api.post('/categories', data).then(() => {
                resetForm();
                fetchCategories();
            });
        }
    };

    const resetForm = () => { setName(''); setDescription(''); };

    const startEdit = (cat) => {
        setEditId(cat.id);
        setName(cat.name);
        setDescription(cat.description);
    };

    const handleDelete = (id) => {
        if (window.confirm("Sigurno brisanje?")) {
            api.delete(`/categories/${id}`).then(() => fetchCategories()).catch(err => alert(err.response.data));
        }
    };

    return (
        <div style={{ padding: '20px' }}>
            <h2>{editId ? 'Izmeni kategoriju' : 'Dodaj kategoriju'}</h2>
            <form onSubmit={handleSubmit} style={{ marginBottom: '20px' }}>
                <input value={name} onChange={e => setName(e.target.value)} placeholder="Ime" required />
                <input value={description} onChange={e => setDescription(e.target.value)} placeholder="Opis" style={{ marginLeft: '10px' }} />
                <button type="submit" style={{ marginLeft: '10px' }}>{editId ? 'Sačuvaj' : 'Dodaj'}</button>
            </form>

            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead><tr><th>Ime</th><th>Opis</th><th>Akcije</th></tr></thead>
                <tbody>
                    {categories.map(cat => (
                        <tr key={cat.id} style={{ borderBottom: '1px solid #ddd' }}>
                            <td>{cat.name}</td>
                            <td>{cat.description}</td>
                            <td>
                                <button onClick={() => startEdit(cat)}>Izmeni</button>
                                <button onClick={() => handleDelete(cat.id)} style={{ marginLeft: '10px' }}>Obriši</button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default Categories;