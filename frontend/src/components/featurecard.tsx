import { LucideIcon } from 'lucide-react';
import styles from '@styles/featurecard.module.css';

interface FeatureCardProps {
  icon: LucideIcon;
  title: string;
  description: string;
  badge: string;
}

const FeatureCard = ({ icon: Icon, title, description, badge }: FeatureCardProps) => {
  return (
    <div className={styles.card}>
      <div className={styles.iconContainer}>
        <Icon size={32} />
      </div>
      <div className={styles.badge}>{badge}</div>
      <h3 className={styles.title}>{title}</h3>
      <p className={styles.description}>{description}</p>
    </div>
  );
};

export default FeatureCard;