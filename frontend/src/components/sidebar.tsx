import { Link, useLocation } from 'react-router-dom';
import { 
  Home, 
  CreditCard, 
  ArrowLeftRight, 
  FolderOpen, 
  TrendingUp, 
  FileText 
} from 'lucide-react';
import styles from '@styles/sidebar.module.css';

interface SidebarProps {
  isOpen: boolean;
  onClose: () => void;
}

const Sidebar = ({ isOpen }: SidebarProps) => {
  const location = useLocation();

  const navItems = [
    {
      section: 'Main',
      items: [
        { path: '/dashboard', label: 'Dashboard', icon: Home },
        { path: '/accounts', label: 'Accounts', icon: CreditCard },
        { path: '/transactions', label: 'Transactions', icon: ArrowLeftRight },
      ]
    },
    {
      section: 'Management',
      items: [
        { path: '/categories', label: 'Categories', icon: FolderOpen },
        { path: '/investments', label: 'Investments', icon: TrendingUp },
        { path: '/reports', label: 'Reports', icon: FileText },
      ]
    }
  ];

  const isActive = (path: string) => location.pathname === path;

  return (
    <aside className={`${styles.sidebar} ${isOpen ? styles.open : styles.closed}`}>
      <nav className={styles.nav}>
        {navItems.map((section, idx) => (
          <div key={idx} className={styles.navSection}>
            <div className={styles.sectionTitle}>{section.section}</div>
            <ul className={styles.navList}>
              {section.items.map((item) => {
                const Icon = item.icon;
                return (
                  <li key={item.path}>
                    <Link
                      to={item.path}
                      className={`${styles.navItem} ${isActive(item.path) ? styles.active : ''}`}
                    >
                      <Icon size={20} />
                      {item.label}
                    </Link>
                  </li>
                );
              })}
            </ul>
          </div>
        ))}
      </nav>
    </aside>
  );
};

export default Sidebar;