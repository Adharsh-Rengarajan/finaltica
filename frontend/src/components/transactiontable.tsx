import { Transaction } from '@typings/index';
import { formatCurrency, formatDate } from '@utils/formatters';
import styles from '@styles/transactiontable.module.css';

interface TransactionTableProps {
  transactions: Transaction[];
}

const TransactionTable = ({ transactions }: TransactionTableProps) => {
  const getTypeBadgeClass = (type: string) => {
    switch (type) {
      case 'INCOME':
        return styles.typeIncome;
      case 'EXPENSE':
        return styles.typeExpense;
      case 'TRANSFER':
        return styles.typeTransfer;
      default:
        return '';
    }
  };

  const getAmountClass = (type: string) => {
    switch (type) {
      case 'INCOME':
        return styles.amountPositive;
      case 'EXPENSE':
        return styles.amountNegative;
      case 'TRANSFER':
        return styles.amountNeutral;
      default:
        return '';
    }
  };

  const formatAmount = (amount: number, type: string) => {
    const formatted = formatCurrency(Math.abs(amount));
    if (type === 'INCOME') return `+${formatted}`;
    if (type === 'EXPENSE') return `-${formatted}`;
    return formatted;
  };

  if (transactions.length === 0) {
    return (
      <div style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-secondary)' }}>
        No transactions yet
      </div>
    );
  }

  return (
    <table className={styles.table}>
      <thead>
        <tr>
          <th>Date</th>
          <th>Description</th>
          <th>Category</th>
          <th>Account</th>
          <th>Type</th>
          <th style={{ textAlign: 'right' }}>Amount</th>
        </tr>
      </thead>
      <tbody>
        {transactions.map((transaction) => (
          <tr key={transaction.id}>
            <td className={styles.date}>{formatDate(transaction.transactionDate)}</td>
            <td className={styles.description}>
              {transaction.description || 'No description'}
            </td>
            <td className={styles.category}>
              {transaction.categoryName || '-'}
            </td>
            <td className={styles.account}>{transaction.accountName}</td>
            <td>
              <span className={`${styles.typeBadge} ${getTypeBadgeClass(transaction.type)}`}>
                {transaction.type}
              </span>
            </td>
            <td className={`${styles.amount} ${getAmountClass(transaction.type)}`}>
              {formatAmount(transaction.amount, transaction.type)}
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default TransactionTable;