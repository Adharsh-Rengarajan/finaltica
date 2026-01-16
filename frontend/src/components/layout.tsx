import { useState, ReactNode } from 'react';
import Header from './header';
import Sidebar from './sidebar';

interface LayoutProps {
  children: ReactNode;
}

const Layout = ({ children }: LayoutProps) => {
  const [sidebarOpen, setSidebarOpen] = useState(true);

  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      <Header onMenuClick={toggleSidebar} />
      
      <div style={{ display: 'flex', flex: 1, position: 'relative' }}>
        <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />
        
        <main
          style={{
            flex: 1,
            marginLeft: sidebarOpen ? '260px' : '0',
            padding: '2rem',
            background: 'var(--bg-primary)',
            minHeight: 'calc(100vh - 70px)',
            transition: 'margin-left 0.3s ease',
          }}
        >
          {children}
        </main>
      </div>
    </div>
  );
};

export default Layout;