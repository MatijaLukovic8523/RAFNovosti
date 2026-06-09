import { Outlet, useLocation } from 'react-router-dom';
import Navbar from '../components/Navbar/Navbar';
import Sidebar from '../components/Sidebar/Sidebar';

const MainLayout = () => {
    const location = useLocation();

    const noSidebarRoutes = ['/login', '/register'];

    const showSidebar = !noSidebarRoutes.includes(location.pathname);

    return (
        <div className="app-container">
            <Navbar />

            {/* Kontejner koji drži main i sidebar */}
            <div style={{
                display: 'flex',
                maxWidth: '1200px',
                margin: '20px auto',
                gap: '20px',
                padding: '0 20px'
            }}>
                <main style={{ flex: showSidebar ? 3 : 4 }}>
                    <Outlet />
                </main>

                {/* Sidebar se renderuje SAMO ako showSidebar nije false */}
                {showSidebar && (
                    <aside style={{ flex: 1 }}>
                        <Sidebar />
                    </aside>
                )}
            </div>
        </div>
    );
};

export default MainLayout;