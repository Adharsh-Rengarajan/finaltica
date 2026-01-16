import { Link } from 'react-router-dom';
import { ArrowRight, Shield, TrendingUp, Activity } from 'lucide-react';
import styles from '@styles/herosection.module.css';

const HeroSection = () => {
  const scrollToSection = (id: string) => {
    const element = document.getElementById(id);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' });
    }
  };

  return (
    <section className={styles.hero}>
      <div className={styles.container}>
        <div className={styles.content}>
          <h1 className={styles.headline}>
            Take Control of Your Financial Future
          </h1>
          <p className={styles.subheadline}>
            The all-in-one platform to track expenses, manage investments, and build wealth 
            with intelligent insights. Start your journey to financial freedom today.
          </p>

          <div className={styles.ctaButtons}>
            <Link to="/signup" className={styles.primaryBtn}>
              Get Started Free
              <ArrowRight size={20} />
            </Link>
            <button 
              className={styles.secondaryBtn}
              onClick={() => scrollToSection('features')}
            >
              View Demo
            </button>
          </div>

          <div className={styles.trustBadges}>
            <div className={styles.trustBadge}>
              <Shield size={18} color="#10b981" />
              <span>Bank-level security</span>
            </div>
            <div className={styles.trustBadge}>
              <Activity size={18} color="#10b981" />
              <span>Real-time insights</span>
            </div>
            <div className={styles.trustBadge}>
              <TrendingUp size={18} color="#10b981" />
              <span>100% Free</span>
            </div>
          </div>
        </div>

        <div className={styles.visual}>
          <div className={styles.dashboardMockup}>
            <div className={styles.mockupPlaceholder}>
              Dashboard Preview
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default HeroSection;