import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from '@context/authcontext';
import ProtectedRoute from '@components/protectedroute';

// Pages
import Landing from '@pages/landing';
import Login from '@pages/login';
import Signup from '@pages/signup';
import Dashboard from '@pages/dashboard';
import Accounts from '@pages/accounts';
import Transactions from '@pages/transactions';
import Categories from '@pages/categories';
import Investments from '@pages/investments';
import Reports from '@pages/reports';

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<Landing />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          
          {/* Protected Routes */}
          <Route element={<ProtectedRoute />}>
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/accounts" element={<Accounts />} />
            <Route path="/transactions" element={<Transactions />} />
            <Route path="/categories" element={<Categories />} />
            <Route path="/investments" element={<Investments />} />
            <Route path="/reports" element={<Reports />} />
          </Route>

          {/* Catch all - redirect to landing */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;