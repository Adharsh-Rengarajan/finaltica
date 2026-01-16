import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '@context/authcontext';
import { Loader } from 'lucide-react';
import Layout from './layout';

const ProtectedRoute = () => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return (
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh',
        flexDirection: 'column',
        gap: '1rem'
      }}>
        <Loader size={40} className="spinner" style={{ color: '#10b981' }} />
        <p style={{ color: '#6b7280' }}>Loading...</p>
      </div>
    );
  }

  if (!isAuthenticated()) {
    return <Navigate to="/login" replace />;
  }

  return (
    <Layout>
      <Outlet />
    </Layout>
  );
};

export default ProtectedRoute;