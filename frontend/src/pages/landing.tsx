import { Link } from 'react-router-dom';
import { 
  CreditCard, 
  BarChart3, 
  ArrowLeftRight, 
  TrendingUp, 
  FileText, 
  Shield,
  UserPlus,
  Link as LinkIcon,
  LineChart,
  ArrowRight,
  Sparkles
} from 'lucide-react';
import LandingHeader from '@components/landingheader';
import HeroSection from '@components/herosection';
import FeatureCard from '@components/featurecard';
import LandingFooter from '@components/landingfooter';
import styles from '@styles/landing.module.css';

const Landing = () => {
  const features = [
    {
      icon: CreditCard,
      title: 'Multi-Account Tracking',
      description: 'Manage checking, savings, credit cards, and investment accounts all in one place. Real-time balance updates with every transaction.',
      badge: '4 Account Types'
    },
    {
      icon: BarChart3,
      title: 'Intelligent Analytics',
      description: 'Get instant insights into your net worth, monthly cash flow, and spending patterns with beautiful visualizations.',
      badge: 'Live Dashboard'
    },
    {
      icon: ArrowLeftRight,
      title: 'Comprehensive Transactions',
      description: 'Track income, expenses, and transfers with smart categorization. Filter by date, category, or account type.',
      badge: 'Unlimited History'
    },
    {
      icon: TrendingUp,
      title: 'Investment Tracking',
      description: 'Monitor your stock and mutual fund portfolio. Track quantity, purchase price, and current value for ROI calculations.',
      badge: 'ROI Calculator'
    },
    {
      icon: FileText,
      title: 'PDF Reports',
      description: 'Generate professional monthly statements and custom reports. Securely stored in the cloud and accessible anytime.',
      badge: 'Cloud Storage'
    },
    {
      icon: Shield,
      title: 'Enterprise Security',
      description: 'Your financial data is protected with JWT authentication, encrypted passwords, and secure AWS infrastructure.',
      badge: 'AES-256 Encrypted'
    }
  ];

  return (
    <div className={styles.landing}>
      <LandingHeader />
      <HeroSection />

      {/* Features Section */}
      <section id="features" className={`${styles.section} ${styles.sectionLight}`}>
        <div className={styles.container}>
          <div className={styles.sectionHeader}>
            <h2 className={styles.sectionTitle}>Everything You Need to Master Your Money</h2>
            <p className={styles.sectionSubtitle}>
              Powerful features designed to give you complete control over your financial life
            </p>
          </div>
          <div className={styles.featuresGrid}>
            {features.map((feature, index) => (
              <FeatureCard key={index} {...feature} />
            ))}
          </div>
        </div>
      </section>

      {/* How It Works Section */}
      <section id="how-it-works" className={`${styles.section} ${styles.sectionGreen}`}>
        <div className={styles.container}>
          <div className={styles.sectionHeader}>
            <h2 className={styles.sectionTitle}>Get Started in 3 Simple Steps</h2>
            <p className={styles.sectionSubtitle}>
              Start managing your finances like a pro in under 5 minutes
            </p>
          </div>
          <div className={styles.timeline}>
            <div className={styles.timelineStep}>
              <div className={styles.stepNumber}>1</div>
              <div className={styles.stepIcon}>
                <UserPlus size={32} />
              </div>
              <h3 className={styles.stepTitle}>Create Your Account</h3>
              <p className={styles.stepDescription}>
                Sign up with your email in under 60 seconds. No credit card required, no hidden fees.
              </p>
              <div className={styles.stepVisual}>
                <div className={styles.stepVisualPlaceholder}>
                  Signup Form
                </div>
              </div>
            </div>

            <div className={styles.timelineStep}>
              <div className={styles.stepNumber}>2</div>
              <div className={styles.stepIcon}>
                <LinkIcon size={32} />
              </div>
              <h3 className={styles.stepTitle}>Connect Your Accounts</h3>
              <p className={styles.stepDescription}>
                Add your checking, savings, credit cards, and investment accounts. Set initial balances and start tracking.
              </p>
              <div className={styles.stepVisual}>
                <div className={styles.stepVisualPlaceholder}>
                  Account Cards
                </div>
              </div>
            </div>

            <div className={styles.timelineStep}>
              <div className={styles.stepNumber}>3</div>
              <div className={styles.stepIcon}>
                <LineChart size={32} />
              </div>
              <h3 className={styles.stepTitle}>Track & Grow Your Wealth</h3>
              <p className={styles.stepDescription}>
                Log transactions, view insights, generate reports. Watch your net worth grow with intelligent financial guidance.
              </p>
              <div className={styles.stepVisual}>
                <div className={styles.stepVisualPlaceholder}>
                  Analytics Dashboard
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Dashboard Preview Section */}
      <section id="dashboard-preview" className={`${styles.section} ${styles.sectionLight}`}>
        <div className={styles.container}>
          <div className={styles.sectionHeader}>
            <h2 className={styles.sectionTitle}>Professional Financial Management Made Simple</h2>
            <p className={styles.sectionSubtitle}>
              All your financial data in one beautiful, intuitive dashboard
            </p>
          </div>
          <div className={styles.dashboardPreview}>
            <div className={styles.previewImage}>
              <div className={styles.previewPlaceholder}>
                Full Dashboard Screenshot
              </div>
            </div>
          </div>
          <div className={styles.statsRow}>
            <div className={styles.statItem}>
              <div className={styles.statNumber}>35,000+</div>
              <div className={styles.statLabel}>Transactions Tracked</div>
            </div>
            <div className={styles.statItem}>
              <div className={styles.statNumber}>$10M+</div>
              <div className={styles.statLabel}>Assets Managed</div>
            </div>
            <div className={styles.statItem}>
              <div className={styles.statNumber}>500+</div>
              <div className={styles.statLabel}>Happy Users</div>
            </div>
          </div>
        </div>
      </section>

      {/* Final CTA Section */}
      <section className={`${styles.section} ${styles.sectionDark}`}>
        <div className={styles.container}>
          <div className={styles.ctaContent}>
            <h2 className={styles.ctaHeadline}>Ready to Transform Your Finances?</h2>
            <p className={styles.ctaSubheadline}>
              Join thousands managing their money smarter
            </p>
            <Link to="/signup" className={styles.ctaButton}>
              <Sparkles size={20} />
              Start Free Today
              <ArrowRight size={20} />
            </Link>
            <p className={styles.ctaFinePrint}>
              No credit card required • 100% Free Forever • Cancel Anytime
            </p>
          </div>
        </div>
      </section>

      <LandingFooter />
    </div>
  );
};

export default Landing;