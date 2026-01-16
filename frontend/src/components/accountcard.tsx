import { CreditCard, TrendingUp, Wallet } from 'lucide-react';
import { Account } from '@typings/index';
import { formatCurrency } from '@utils/formatters';
import styles from '@styles/accountcard.module.css';

interface AccountCardProps {
  account: Account;
  onClick?: () => void;
}

const AccountCard = ({ account, onClick }: AccountCardProps) => {
  const getIcon = () => {
    switch (account.type) {
      case 'INVESTMENT':
        return <TrendingUp size={20} />;
      case 'CASH':
        return <Wallet size={20} />;
      default:
        return <CreditCard size={20} />;
    }
  };

  const getIconBg = () => {
    switch (account.type) {
      case 'CHECKING':
        return '#dbeafe';
      case 'CREDIT':
        return '#fee2e2';
      case 'INVESTMENT':
        return '#dcfce7';
      case 'CASH':
        return '#f3f4f6';
      default:
        return '#f3f4f6';
    }
  };

  const getIconColor = () => {
    switch (account.type) {
      case 'CHECKING':
        return '#1e40af';
      case 'CREDIT':
        return '#991b1b';
      case 'INVESTMENT':
        return '#166534';
      case 'CASH':
        return '#374151';
      default:
        return '#374151';
    }
  };

  const getBadgeClass = () => {
    switch (account.type) {
      case 'CHECKING':
        return styles.badgeChecking;
      case 'CREDIT':
        return styles.badgeCredit;
      case 'INVESTMENT':
        return styles.badgeInvestment;
      case 'CASH':
        return styles.badgeCash;
      default:
        return styles.badgeCash;
    }
  };

  return (
    <div className={styles.card} onClick={onClick}>
      <div className={styles.header}>
        <div className={styles.iconWrapper} style={{ background: getIconBg(), color: getIconColor() }}>
          {getIcon()}
        </div>
        <span className={`${styles.badge} ${getBadgeClass()}`}>
          {account.type}
        </span>
      </div>
      <div className={styles.name}>{account.name}</div>
      <div className={styles.balance}>
        {formatCurrency(account.currentBalance, account.currency)}
        <span className={styles.currency}>{account.currency}</span>
      </div>
    </div>
  );
};

export default AccountCard;