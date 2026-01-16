import { TrendingUp, Twitter, Linkedin, Github } from 'lucide-react';
import styles from '@styles/landingfooter.module.css';

const LandingFooter = () => {
  return (
    <footer className={styles.footer}>
      <div className={styles.container}>
        <div className={styles.topSection}>
          {/* Brand Column */}
          <div className={styles.brand}>
            <div className={styles.logo}>
              <div className={styles.logoIcon}>
                <TrendingUp size={20} />
              </div>
              <span>Finaltica</span>
            </div>
            <p className={styles.tagline}>
              Smart Financial Management for Modern Life. Track, analyze, and grow your wealth 
              with powerful insights and intuitive tools.
            </p>
            <div className={styles.socialIcons}>
              <a 
                href="https://twitter.com" 
                target="_blank" 
                rel="noopener noreferrer"
                className={styles.socialIcon}
              >
                <Twitter size={20} />
              </a>
              <a 
                href="https://linkedin.com" 
                target="_blank" 
                rel="noopener noreferrer"
                className={styles.socialIcon}
              >
                <Linkedin size={20} />
              </a>
              <a 
                href="https://github.com" 
                target="_blank" 
                rel="noopener noreferrer"
                className={styles.socialIcon}
              >
                <Github size={20} />
              </a>
            </div>
          </div>

          {/* Product Column */}
          <div className={styles.column}>
            <h4>Product</h4>
            <ul className={styles.links}>
              <li><a>Features</a></li>
              <li><a>Pricing</a></li>
              <li><a>Security</a></li>
              <li><a>Roadmap</a></li>
            </ul>
          </div>

          {/* Resources Column */}
          <div className={styles.column}>
            <h4>Resources</h4>
            <ul className={styles.links}>
              <li><a>Documentation</a></li>
              <li><a>API</a></li>
              <li><a>Blog</a></li>
              <li><a>Support</a></li>
            </ul>
          </div>

          {/* Company Column */}
          <div className={styles.column}>
            <h4>Company</h4>
            <ul className={styles.links}>
              <li><a>About</a></li>
              <li><a>Careers</a></li>
              <li><a>Contact</a></li>
              <li><a>Privacy</a></li>
              <li><a>Terms</a></li>
            </ul>
          </div>
        </div>

        <div className={styles.bottomSection}>
          <p>© 2026 Finaltica. All rights reserved. Made with ❤️ by Finaltica Team</p>
        </div>
      </div>
    </footer>
  );
};

export default LandingFooter;