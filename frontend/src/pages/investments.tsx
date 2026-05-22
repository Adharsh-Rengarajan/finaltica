import { useState, useEffect } from 'react';
import { TrendingUp, TrendingDown, DollarSign, ArrowDownCircle, Plus, Loader, Trash2 } from 'lucide-react';
import api from '@config/api';
import API_ENDPOINTS from '@config/endpoints';
import SummaryCard from '@components/summarycard';
import InvestmentModal, { InvestmentFormData } from '@components/investmentmodal';
import ErrorBanner from '@components/errorbanner';
import {
  Account,
  InvestmentTransaction,
  PortfolioSummary,
  ApiResponse,
} from '@typings/index';
import { formatCurrency } from '@utils/formatters';
import styles from '@styles/investments.module.css';

const Investments = () => {
  const [investmentAccounts, setInvestmentAccounts] = useState<Account[]>([]);
  const [investmentTransactions, setInvestmentTransactions] = useState<InvestmentTransaction[]>([]);
  const [portfolio, setPortfolio] = useState<PortfolioSummary | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedAccountId, setSelectedAccountId] = useState<string>('');
  const [modalOpen, setModalOpen] = useState(false);

  useEffect(() => {
    fetchData();
  }, []);

  useEffect(() => {
    fetchInvestmentTransactions(selectedAccountId);
  }, [selectedAccountId]);

  const fetchData = async () => {
    try {
      setLoading(true);
      setError(null);

      const [accountsRes, portfolioRes] = await Promise.all([
        api.get<ApiResponse<Account[]>>(`${API_ENDPOINTS.ACCOUNTS.BASE}?type=INVESTMENT`),
        api.get<ApiResponse<PortfolioSummary>>(API_ENDPOINTS.ANALYTICS.PORTFOLIO),
      ]);

      setInvestmentAccounts(accountsRes.data.data);
      setPortfolio(portfolioRes.data.data);

      await fetchInvestmentTransactions(selectedAccountId);
    } catch (err: any) {
      console.error('[INVESTMENTS] Error fetching data:', err);
      setError(
        err.response?.data?.message ||
          'Could not load investments. Please refresh or try again later.'
      );
    } finally {
      setLoading(false);
    }
  };

  const fetchInvestmentTransactions = async (accountId?: string) => {
    try {
      const url = accountId
        ? `${API_ENDPOINTS.TRANSACTIONS.INVESTMENTS}?accountId=${accountId}`
        : API_ENDPOINTS.TRANSACTIONS.INVESTMENTS;

      const response = await api.get<ApiResponse<InvestmentTransaction[]>>(url);
      setInvestmentTransactions(response.data.data);
    } catch (err: any) {
      console.error('[INVESTMENTS] Error fetching investment transactions:', err);
      setInvestmentTransactions([]);
    }
  };

  const refreshPortfolio = async () => {
    try {
      const portfolioRes = await api.get<ApiResponse<PortfolioSummary>>(
        API_ENDPOINTS.ANALYTICS.PORTFOLIO
      );
      setPortfolio(portfolioRes.data.data);
    } catch (err) {
      console.error('[INVESTMENTS] Error refreshing portfolio:', err);
    }
  };

  const handleBuyInvestment = async (data: InvestmentFormData) => {
    try {
      await api.post<ApiResponse<InvestmentTransaction>>(
        API_ENDPOINTS.TRANSACTIONS.INVESTMENT,
        data
      );

      const accountsRes = await api.get<ApiResponse<Account[]>>(
        `${API_ENDPOINTS.ACCOUNTS.BASE}?type=INVESTMENT`
      );
      setInvestmentAccounts(accountsRes.data.data);
      await Promise.all([refreshPortfolio(), fetchInvestmentTransactions(selectedAccountId)]);
    } catch (err: any) {
      console.error('[INVESTMENTS] Error buying investment:', err);
      const errs = err.response?.data?.errors;
      const detail =
        errs?.amount ||
        errs?.account ||
        errs?.quantity ||
        errs?.pricePerUnit ||
        errs?.assetSymbol ||
        errs?.authorization;
      alert(detail || err.response?.data?.message || 'Failed to purchase investment');
      throw err;
    }
  };

  const handleDeleteInvestment = async (transactionId: string) => {
    if (!confirm('Delete this investment purchase? The account balance will be restored.')) {
      return;
    }
    try {
      await api.delete(API_ENDPOINTS.TRANSACTIONS.BY_ID(transactionId));
      const accountsRes = await api.get<ApiResponse<Account[]>>(
        `${API_ENDPOINTS.ACCOUNTS.BASE}?type=INVESTMENT`
      );
      setInvestmentAccounts(accountsRes.data.data);
      await Promise.all([refreshPortfolio(), fetchInvestmentTransactions(selectedAccountId)]);
    } catch (err: any) {
      console.error('[INVESTMENTS] Error deleting investment:', err);
      alert(err.response?.data?.message || 'Failed to delete investment');
    }
  };

  const accountCashValue = investmentAccounts.reduce(
    (sum, acc) => sum + Number(acc.currentBalance || 0),
    0
  );
  const holdingsValue = portfolio?.currentValue ?? 0;
  const totalInvested = portfolio?.totalInvested ?? 0;
  const totalReturns = portfolio?.totalReturns ?? 0;
  const returnsPct = portfolio?.returnsPercentage ?? 0;
  const totalPortfolioValue = accountCashValue + holdingsValue;
  const returnsPositive = totalReturns >= 0;

  if (loading) {
    return (
      <div className={styles.loading}>
        <Loader size={40} className="spinner" style={{ color: '#10b981' }} />
      </div>
    );
  }

  return (
    <div className={styles.investments}>
      {error && <ErrorBanner message={error} onDismiss={() => setError(null)} />}

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
            {investmentAccounts.map((account) => (
              <option key={account.id} value={account.id}>
                {account.name}
              </option>
            ))}
          </select>
          <button
            className={styles.buyButton}
            onClick={() => setModalOpen(true)}
            disabled={investmentAccounts.length === 0}
            title={
              investmentAccounts.length === 0
                ? 'Create an investment account first'
                : 'Buy a stock or mutual fund'
            }
          >
            <TrendingUp size={20} />
            Buy Investment
          </button>
        </div>
      </div>

      <div className={styles.summaryCards}>
        <SummaryCard
          title="Total Portfolio Value"
          value={formatCurrency(totalPortfolioValue)}
          icon={DollarSign}
          iconBg="#dcfce7"
          iconColor="#166534"
        />
        <SummaryCard
          title="Total Invested"
          value={formatCurrency(totalInvested)}
          icon={ArrowDownCircle}
          iconBg="#dbeafe"
          iconColor="#1e40af"
        />
        <SummaryCard
          title="Total Returns"
          value={formatCurrency(totalReturns)}
          change={`${returnsPositive ? '+' : ''}${returnsPct.toFixed(2)}%`}
          changeType={
            totalReturns > 0 ? 'positive' : totalReturns < 0 ? 'negative' : 'neutral'
          }
          icon={returnsPositive ? TrendingUp : TrendingDown}
          iconBg={returnsPositive ? '#dcfce7' : '#fee2e2'}
          iconColor={returnsPositive ? '#166534' : '#991b1b'}
        />
      </div>

      <div className={styles.tableCard}>
        <div className={styles.tableHeader}>
          <h2 className={styles.tableTitle}>Portfolio Holdings</h2>
        </div>

        {portfolio && portfolio.holdings.length > 0 ? (
          <div className={styles.tableWrapper}>
            <table className={styles.table}>
              <thead>
                <tr>
                  <th>Symbol</th>
                  <th>Asset Type</th>
                  <th>Quantity</th>
                  <th>Avg Price</th>
                  <th>Total Invested</th>
                  <th>Current Value</th>
                  <th>Return</th>
                </tr>
              </thead>
              <tbody>
                {portfolio.holdings.map((h) => {
                  const positive = h.returns >= 0;
                  return (
                    <tr key={h.assetSymbol}>
                      <td><strong>{h.assetSymbol}</strong></td>
                      <td>{h.assetType === 'STOCK' ? 'Stock' : 'Mutual Fund'}</td>
                      <td>{Number(h.totalQuantity).toFixed(4)}</td>
                      <td>{formatCurrency(h.averagePrice)}</td>
                      <td>{formatCurrency(h.totalInvested)}</td>
                      <td>{formatCurrency(h.currentValue)}</td>
                      <td style={{ color: positive ? '#166534' : '#991b1b', fontWeight: 600 }}>
                        {positive ? '+' : ''}
                        {formatCurrency(h.returns)} ({positive ? '+' : ''}
                        {Number(h.returnsPercentage).toFixed(2)}%)
                      </td>
                    </tr>
                  );
                })}
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
            {investmentAccounts.length > 0 && (
              <button className={styles.buyButton} onClick={() => setModalOpen(true)}>
                <Plus size={20} />
                Buy Investment
              </button>
            )}
          </div>
        )}
      </div>

      {investmentTransactions.length > 0 && (
        <div className={styles.tableCard} style={{ marginTop: '1.5rem' }}>
          <div className={styles.tableHeader}>
            <h2 className={styles.tableTitle}>Purchase History</h2>
          </div>
          <div className={styles.tableWrapper}>
            <table className={styles.table}>
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Symbol</th>
                  <th>Type</th>
                  <th>Quantity</th>
                  <th>Price/Unit</th>
                  <th>Total Cost</th>
                  <th>Account</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {investmentTransactions.map((it) => (
                  <tr key={it.transaction.id}>
                    <td>{new Date(it.transaction.transactionDate).toLocaleDateString()}</td>
                    <td><strong>{it.investmentMetadata.assetSymbol}</strong></td>
                    <td>{it.investmentMetadata.assetType === 'STOCK' ? 'Stock' : 'Mutual Fund'}</td>
                    <td>{Number(it.investmentMetadata.quantity).toFixed(4)}</td>
                    <td>{formatCurrency(it.investmentMetadata.pricePerUnit)}</td>
                    <td>{formatCurrency(it.investmentMetadata.totalAmount)}</td>
                    <td>{it.transaction.accountName}</td>
                    <td>
                      <button
                        onClick={() => handleDeleteInvestment(it.transaction.id)}
                        style={{
                          background: 'transparent',
                          border: 'none',
                          color: '#ef4444',
                          cursor: 'pointer',
                        }}
                        title="Delete purchase"
                      >
                        <Trash2 size={18} />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

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