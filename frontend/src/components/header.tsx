import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Menu, TrendingUp, User, Settings, LogOut, ChevronDown } from 'lucide-react';
import { useAuth } from '@context/authcontext';
import styles from '@styles/header.module.css';

interface HeaderProps {
  onMenuClick: () => void;
}

const Header = ({ onMenuClick }: HeaderProps) => {
  const { user, logout } = useAuth();
  const [dropdownOpen, setDropdownOpen] = useState(false);

  const handleLogout = () => {
    logout();
  };

  const getInitials = () => {
    if (!user) return 'U';
    return `${user.firstName.charAt(0)}${user.lastName.charAt(0)}`.toUpperCase();
  };

  return (
    <header className={styles.header}>
      <div className={styles.left}>
        <button className={styles.menuButton} onClick={onMenuClick}>
          <Menu size={24} />
        </button>

        <Link to="/dashboard" className={styles.logo}>
          <div className={styles.logoIcon}>
            <TrendingUp size={20} />
          </div>
          <span>Finaltica</span>
        </Link>
      </div>

      <div className={styles.right}>
        <div className={styles.userMenu}>
          <button
            className={styles.userButton}
            onClick={() => setDropdownOpen(!dropdownOpen)}
          >
            <div className={styles.userAvatar}>{getInitials()}</div>
            <span className={styles.userName}>
              {user?.firstName} {user?.lastName}
            </span>
            <ChevronDown size={16} />
          </button>

          <div className={`${styles.dropdown} ${dropdownOpen ? styles.open : ''}`}>
            <button className={styles.dropdownItem}>
              <User size={16} />
              Profile
            </button>
            <button className={styles.dropdownItem}>
              <Settings size={16} />
              Settings
            </button>
            <div className={styles.dropdownDivider} />
            <button className={styles.dropdownItem} onClick={handleLogout}>
              <LogOut size={16} />
              Logout
            </button>
          </div>
        </div>
      </div>

      {dropdownOpen && (
        <div
          style={{
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            zIndex: 99,
          }}
          onClick={() => setDropdownOpen(false)}
        />
      )}
    </header>
  );
};

export default Header;