import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/RAFNovosti/api',
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json'
    }
});

api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    const visitorId = localStorage.getItem('visitorId');

    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    if (visitorId) {
        config.headers['X-Visitor-ID'] = visitorId;
    }

    return config;
});

export default api;