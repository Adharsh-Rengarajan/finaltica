import { useState, useEffect } from 'react';
import { TrendingUp, DollarSign, ArrowDownCircle, Plus, Loader, Eye, Trash2 } from 'lucide-react';
import api from '@config/api';
import API_ENDPOINTS from '@config/endpoints';
import SummaryCard from '@components/summarycard';
import InvestmentModal, { InvestmentFormData } from '@components/investmentmodal';
import { Account, Transaction, InvestmentTransaction, ApiResponse } from '@typings/index';
import { formatCurrency } from '@utils/formatters';
import styles from '@styles/investments.module.css';

const Investments = () => {
  const [investmentAccounts, setInvestmentAccounts] = useState<Account[]>([]);
  const [investmentTransactions, setInvestmentTransactions] = useState<InvestmentTransaction[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedAccountId, setSelectedAccountId] = useState<string>('');
  const [modalOpen, setModalOpen] = useState(false);

  useEffect(() => {
    fetchData();
  }, []);

  useEffect(() => {
    if (selectedAccountId) {
      filterByAccount();
    } else {
      fetchInvestmentTransactions();
    }
  }, [selectedAccountId]);

  const fetchData = async () => {
    try {
      setLoading(true);
      console.log('[INVESTMENTS] Fetching data');

      const accountsRes = await api.get<ApiResponse<Account[]>>(
        `${API_ENDPOINTS.ACCOUNTS.BASE}?type=INVESTMENT`
      );

      console.log('[INVESTMENTS] Investment accounts fetched:', accountsRes.data.data.length);
      setInvestmentAccounts(accountsRes.data.data);

      await fetchInvestmentTransactions();
    } catch (error: any) {
      console.error('[INVESTMENTS] Error fetching data:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchInvestmentTransactions = async () => {
    try {
      const response = await api.get<ApiResponse<Transaction[]>>(API_ENDPOINTS.TRANSACTIONS.BASE);
      
      // Filter for investment transactions only (those with investment metadata)
      const allTransactions = response.data.data;
      
      // For now, we'll just show all transactions from investment accounts
      // In a real app, you'd fetch only investment transactions with metadata
      const investmentTxns = allTransactions.filter(t => {
        const account = investmentAccounts.find(a => a.id === t.accountId);
        return account?.type === 'INVESTMENT';
      });

      console.log('[INVESTMENTS] Investment transactions:', investmentTxns.length);
      
      // Convert to InvestmentTransaction type (simplified for now)
      setInvestmentTransactions([]);
    } catch (error: any) {
      console.error('[INVESTMENTS] Error fetching transactions:', error);
    }
  };

  const filterByAccount = async () => {
    if (!selectedAccountId) {
      fetchInvestmentTransactions();
      return;
    }

    try {
      const response = await api.get<ApiResponse<Transaction[]>>(
        `${API_ENDPOINTS.TRANSACTIONS.BASE}?accountId=${selectedAccountId}`
      );
      
      console.log('[INVESTMENTS] Filtered transactions:', response.data.data.length);
      setInvestmentTransactions([]);
    } catch (error: any) {
      console.error('[INVESTMENTS] Error filtering transactions:', error);
    }
  };

  const handleBuyInvestment = async (data: InvestmentFormData) => {
    try {
      console.log('[INVESTMENTS] Buying investment:', data);

      await api.post<ApiResponse<InvestmentTransaction>>(
        API_ENDPOINTS.TRANSACTIONS.INVESTMENT,
        data
      );

      console.log('[INVESTMENTS] Investment purchased successfully');
      await fetchData();
    } catch (error: any) {
      console.error('[INVESTMENTS] Error buying investment:', error);
      alert(error.response?.data?.message || 'Failed to purchase investment');
      throw error;
    }
  };

  const calculatePortfolioSummary = () => {
    const totalValue = investmentAccounts.reduce((sum, acc) => sum + acc.currentBalance, 0);
    return {
      totalValue,
      totalInvested: 0,
      totalReturns: 0,
    };
  };

  const summary = calculatePortfolioSummary();

  if (loading) {
    return (
      <div className={styles.loading}>
        <Loader size={40} className="spinner" style={{ color: '#10b981' }} />
      </div>
    );
  }

  return (
    <div className={styles.investments}>
      <div className={styles.header}>
        <div className={styles.headerLeft}>
          <h1 className={styles.title}>Investment Portfolio</h1>
          <p className={styles.subtitle}>Track your stocks and mutual funds</p>
        </div>
        <div className={styles.headerRight}>
          <select
            className={styles.filterSelect}
            value={selectedAccountId}
            onChange={(e) => setSelectedAccountId(e.target.value)}
          >
            <option value="">All Investment Accounts</option>
            {investmentAccounts.map(account => (
              <option key={account.id} value={account.id}>
                {account.name}
              </option>
            ))}
          </select>
          <button className={styles.buyButton} onClick={() => setModalOpen(true)}>
            <TrendingUp size={20} />
            Buy Investment
          </button>
        </div>
      </div>

      {/* Portfolio Summary */}
      <div className={styles.summaryCards}>
        <SummaryCard
          title="Total Portfolio Value"
          value={formatCurrency(summary.totalValue)}
          icon={DollarSign}
          iconBg="#dcfce7"
          iconColor="#166534"
        />
        <SummaryCard
          title="Total Invested"
          value={formatCurrency(summary.totalInvested)}
          icon={ArrowDownCircle}
          iconBg="#dbeafe"
          iconColor="#1e40af"
        />
        <SummaryCard
          title="Total Returns"
          value={formatCurrency(summary.totalReturns)}
          change={summary.totalReturns > 0 ? '+0.0%' : '0.0%'}
          changeType={summary.totalReturns > 0 ? 'positive' : 'neutral'}
          icon={TrendingUp}
          iconBg="#dcfce7"
          iconColor="#166534"
        />
      </div>

      {/* Holdings Table */}
      <div className={styles.tableCard}>
        <div className={styles.tableHeader}>
          <h2 className={styles.tableTitle}>Portfolio Holdings</h2>
        </div>

        {investmentTransactions.length > 0 ? (
          <div className={styles.tableWrapper}>
            <table className={styles.table}>
              <thead>
                <tr>
                  <th>Symbol</th>
                  <th>Asset Type</th>
                  <th>Quantity</th>
                  <th>Avg Price</th>
                  <th>Total Value</th>
                  <th>Return</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {/* Holdings will be displayed here */}
              </tbody>
            </table>
          </div>
        ) : (
          <div className={styles.emptyState}>
            <TrendingUp size={64} className={styles.emptyIcon} />
            <div className={styles.emptyTitle}>No investments yet</div>
            <p className={styles.emptyText}>
              {investmentAccounts.length === 0
                ? 'Create an investment account first, then start buying stocks or mutual funds'
                : 'Purchase your first investment to start building your portfolio'}
            </p>
            <button className={styles.buyButton} onClick={() => setModalOpen(true)}>
              <Plus size={20} />
              Buy Investment
            </button>
          </div>
        )}
      </div>

      <InvestmentModal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        onSubmit={handleBuyInvestment}
        investmentAccounts={investmentAccounts}
      />
    </div>
  );
};

export default Investments;