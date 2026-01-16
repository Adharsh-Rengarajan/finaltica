import { LucideIcon } from 'lucide-react';
import styles from '@styles/summarycard.module.css';

interface SummaryCardProps {
  title: string;
  value: string;
  change?: string;
  changeType?: 'positive' | 'negative' | 'neutral';
  icon: LucideIcon;
  iconBg: string;
  iconColor: string;
}

const SummaryCard = ({ title, value, change, changeType = 'neutral', icon: Icon, iconBg, iconColor }: SummaryCardProps) => {
  return (
    <div className={styles.card}>
      <div className={styles.header}>
        <span className={styles.title}>{title}</span>
        <div className={styles.icon} style={{ background: iconBg }}>
          <Icon size={20} style={{ color: iconColor }} />
        </div>
      </div>
      <div className={styles.value}>{value}</div>
      {change && (
        <div className={`${styles.change} ${styles[changeType]}`}>
          {change}
        </div>
      )}
    </div>
  );
};

export default SummaryCard;