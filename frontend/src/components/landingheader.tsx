import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Menu, X, TrendingUp } from 'lucide-react';
import styles from '@styles/landingheader.module.css';

const LandingHeader = () => {
  const [scrolled, setScrolled] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setScrolled(window.scrollY > 20);
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const scrollToSection = (id: string) => {
    const element = document.getElementById(id);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' });
      setMobileMenuOpen(false);
    }
  };

  return (
    <header className={`${styles.header} ${scrolled ? styles.headerScrolled : ''}`}>
      <div className={styles.container}>
        <Link to="/" className={styles.logo}>
          <div className={styles.logoIcon}>
            <TrendingUp size={20} />
          </div>
          <span>Finaltica</span>
        </Link>

        {/* Desktop Navigation */}
        <nav className={styles.nav}>
          <ul className={styles.navLinks}>
            <li>
              <a className={styles.navLink} onClick={() => scrollToSection('features')}>
                Features
              </a>
            </li>
            <li>
              <a className={styles.navLink} onClick={() => scrollToSection('how-it-works')}>
                How It Works
              </a>
            </li>
            <li>
              <a className={styles.navLink} onClick={() => scrollToSection('dashboard-preview')}>
                Preview
              </a>
            </li>
          </ul>

          <div className={styles.authButtons}>
            <Link to="/login" className={styles.loginBtn}>
              Login
            </Link>
            <Link to="/signup" className={styles.signupBtn}>
              Get Started
            </Link>
          </div>
        </nav>

        {/* Mobile Menu Button */}
        <button
          className={styles.mobileMenuBtn}
          onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
        >
          {mobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
        </button>
      </div>

      {/* Mobile Navigation */}
      <div className={`${styles.mobileNav} ${mobileMenuOpen ? styles.open : ''}`}>
        <ul className={styles.mobileNavLinks}>
          <li>
            <a className={styles.navLink} onClick={() => scrollToSection('features')}>
              Features
            </a>
          </li>
          <li>
            <a className={styles.navLink} onClick={() => scrollToSection('how-it-works')}>
              How It Works
            </a>
          </li>
          <li>
            <a className={styles.navLink} onClick={() => scrollToSection('dashboard-preview')}>
              Preview
            </a>
          </li>
        </ul>

        <div className={styles.mobileAuthButtons}>
          <Link to="/login" className={styles.loginBtn}>
            Login
          </Link>
          <Link to="/signup" className={styles.signupBtn}>
            Get Started
          </Link>
        </div>
      </div>
    </header>
  );
};

export default LandingHeader;