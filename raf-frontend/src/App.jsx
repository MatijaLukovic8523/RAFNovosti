import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import './App.css';
import ProtectedRoute from './components/ProtectedRoute';
import MainLayout from './layouts/MainLayout';
import Home from './pages/public/Home';
import MostRead from './pages/public/MostRead';
import CategoryNews from './pages/public/CategoryNews';
import NewsDetails from './pages/public/NewsDetails';
import Login from './pages/public/Login';
import Register from './pages/public/Register';
import Categories from './pages/protected/Categories';
import Vesti from './pages/protected/Vesti';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Sve rute unutar ovog Route-a koriste MainLayout */}
          <Route path="/" element={<MainLayout />}>
            {/* Javne rute */}
            <Route index element={<Home />} />
            <Route path="najcitanije" element={<MostRead />} />
            <Route path="kategorija/:id" element={<CategoryNews />} />
            <Route path="vest/:id" element={<NewsDetails />} />
            <Route path="login" element={<Login />} />
            <Route path="register" element={<Register />} />

            {/* Zaštićene CMS rute */}
            <Route path="kategorije" element={
              <ProtectedRoute>
                <Categories />
              </ProtectedRoute>
            } />
            <Route path="vesti" element={
              <ProtectedRoute>
                <Vesti />
              </ProtectedRoute>
            } />
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;