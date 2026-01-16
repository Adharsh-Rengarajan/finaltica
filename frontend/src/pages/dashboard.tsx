import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { 
  Wallet, 
  ArrowDownCircle, 
  ArrowUpCircle, 
  PiggyBank, 
  Plus, 
  ChevronRight,
  Loader,
  ArrowLeftRight
} from 'lucide-react';
import { useAuth } from '@context/authcontext';
import api from '@config/api';
import API_ENDPOINTS from '@config/endpoints';
import SummaryCard from '@components/summarycard';
import AccountCard from '@components/accountcard';
import TransactionTable from '@components/transactiontable';
import IncomeExpenseChart from '@components/incomeexpensechart';
import CategorySpendingChart from '@components/categoryspendingchart';
import { Account, Transaction, NetWorthResponse, MonthlySummary, CategorySpendingResponse, ApiResponse } from '@typings/index';
import { formatCurrency } from '@utils/formatters';
import styles from '@styles/dashboard.module.css';

const Dashboard = () => {
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [netWorth, setNetWorth] = useState<NetWorthResponse | null>(null);
  const [monthlySummary, setMonthlySummary] = useState<MonthlySummary | null>(null);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [recentTransactions, setRecentTransactions] = useState<Transaction[]>([]);
  const [incomeExpenseData, setIncomeExpenseData] = useState<any[]>([]);
  const [categoryExpenseData, setCategoryExpenseData] = useState<any[]>([]);
  const [categoryIncomeData, setCategoryIncomeData] = useState<any[]>([]);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      console.log('[DASHBOARD] Fetching dashboard data');

      const currentDate = new Date();
      const year = currentDate.getFullYear();
      const month = currentDate.getMonth() + 1;

      // Calculate date range - start of this year to now
      const startOfYear = new Date(year, 0, 1); // January 1st of current year
      const endOfToday = new Date();
      endOfToday.setHours(23, 59, 59, 999);

      console.log('[DASHBOARD] Date range for category spending:', {
        startDate: startOfYear.toISOString(),
        endDate: endOfToday.toISOString()
      });

      // Fetch all data in parallel
      const [netWorthRes, summaryRes, accountsRes, transactionsRes, categorySpendingRes] = await Promise.all([
        api.get<ApiResponse<NetWorthResponse>>(API_ENDPOINTS.ANALYTICS.NET_WORTH),
        api.get<ApiResponse<MonthlySummary>>(`${API_ENDPOINTS.ANALYTICS.MONTHLY_SUMMARY}?year=${year}&month=${month}`),
        api.get<ApiResponse<Account[]>>(API_ENDPOINTS.ACCOUNTS.BASE),
        api.get<ApiResponse<Transaction[]>>(API_ENDPOINTS.TRANSACTIONS.BASE),
        api.get<ApiResponse<CategorySpendingResponse>>(
          `${API_ENDPOINTS.ANALYTICS.CATEGORY_SPENDING}?startDate=${startOfYear.toISOString()}&endDate=${endOfToday.toISOString()}`
        ),
      ]);

      console.log('[DASHBOARD] Data fetched successfully');

      setNetWorth(netWorthRes.data.data);
      setMonthlySummary(summaryRes.data.data);
      setAccounts(accountsRes.data.data.slice(0, 3));
      setRecentTransactions(transactionsRes.data.data.slice(0, 10));

      // Fetch last 6 months for income/expense chart
      const chartData = await fetchLast6Months();
      setIncomeExpenseData(chartData);

      // Process category spending data
      console.log('[DASHBOARD] Category spending response:', categorySpendingRes.data.data);
      
      const expenseChartData = categorySpendingRes.data.data.expenses.map(cat => ({
        name: cat.categoryName,
        value: Math.abs(cat.amount),
      }));

      const incomeChartData = categorySpendingRes.data.data.income.map(cat => ({
        name: cat.categoryName,
        value: Math.abs(cat.amount),
      }));
      
      console.log('[DASHBOARD] Expense chart data:', expenseChartData);
      console.log('[DASHBOARD] Income chart data:', incomeChartData);
      
      setCategoryExpenseData(expenseChartData);
      setCategoryIncomeData(incomeChartData);

    } catch (error: any) {
      console.error('[DASHBOARD] Error fetching data:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchLast6Months = async () => {
    const currentDate = new Date();
    const data = [];
    
    for (let i = 5; i >= 0; i--) {
      const date = new Date();
      date.setMonth(currentDate.getMonth() - i);
      const year = date.getFullYear();
      const month = date.getMonth() + 1;

      try {
        const response = await api.get<ApiResponse<MonthlySummary>>(
          `${API_ENDPOINTS.ANALYTICS.MONTHLY_SUMMARY}?year=${year}&month=${month}`
        );

        const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
        data.push({
          month: monthNames[month - 1],
          income: response.data.data.totalIncome || 0,
          expense: Math.abs(response.data.data.totalExpenses) || 0,
        });
      } catch (error) {
        console.error('[DASHBOARD] Error fetching month data:', error);
      }
    }

    return data;
  };

  if (loading) {
    return (
      <div className={styles.loading}>
        <Loader size={40} className="spinner" style={{ color: '#10b981' }} />
      </div>
    );
  }

  const getGreeting = () => {
    const hour = new Date().getHours();
    if (hour < 12) return 'Good morning';
    if (hour < 18) return 'Good afternoon';
    return 'Good evening';
  };

  const getNetWorthChange = () => {
    if (!netWorth || netWorth.netWorth === 0) {
      return { message: 'No data yet', type: 'neutral' as const };
    }
    return { message: `Total: ${formatCurrency(netWorth.netWorth)}`, type: 'neutral' as const };
  };

  const getIncomeChange = () => {
    if (!monthlySummary || monthlySummary.incomeTransactionCount === 0) {
      return { message: 'No transactions yet', type: 'neutral' as const };
    }
    return { 
      message: `${monthlySummary.incomeTransactionCount} transaction${monthlySummary.incomeTransactionCount > 1 ? 's' : ''}`,
      type: 'neutral' as const 
    };
  };

  const getExpensesChange = () => {
    if (!monthlySummary || monthlySummary.expenseTransactionCount === 0) {
      return { message: 'No transactions yet', type: 'neutral' as const };
    }
    return { 
      message: `${monthlySummary.expenseTransactionCount} transaction${monthlySummary.expenseTransactionCount > 1 ? 's' : ''}`,
      type: 'neutral' as const 
    };
  };

  const getSavingsChange = () => {
    if (!monthlySummary) {
      return { message: 'No data yet', type: 'neutral' as const };
    }
    if (monthlySummary.netSavings > 0) {
      return { message: 'Positive savings this month', type: 'positive' as const };
    } else if (monthlySummary.netSavings < 0) {
      return { message: 'Negative savings this month', type: 'negative' as const };
    }
    return { message: 'Break even this month', type: 'neutral' as const };
  };

  const netWorthChange = getNetWorthChange();
  const incomeChange = getIncomeChange();
  const expensesChange = getExpensesChange();
  const savingsChange = getSavingsChange();

  return (
    <div className={styles.dashboard}>
      <div className={styles.header}>
        <h1 className={styles.title}>
          {getGreeting()}, {user?.firstName}!
        </h1>
        <p className={styles.subtitle}>
          Here's what's happening with your finances today
        </p>
      </div>

      {/* Summary Cards */}
      <div className={styles.summaryCards}>
        <SummaryCard
          title="Net Worth"
          value={formatCurrency(netWorth?.netWorth || 0)}
          change={netWorthChange.message}
          changeType={netWorthChange.type}
          icon={Wallet}
          iconBg="#dcfce7"
          iconColor="#166534"
        />
        <SummaryCard
          title="Total Income"
          value={formatCurrency(monthlySummary?.totalIncome || 0)}
          change={incomeChange.message}
          changeType={incomeChange.type}
          icon={ArrowDownCircle}
          iconBg="#dcfce7"
          iconColor="#166534"
        />
        <SummaryCard
          title="Total Expenses"
          value={formatCurrency(monthlySummary?.totalExpenses || 0)}
          change={expensesChange.message}
          changeType={expensesChange.type}
          icon={ArrowUpCircle}
          iconBg="#fee2e2"
          iconColor="#991b1b"
        />
        <SummaryCard
          title="Net Savings"
          value={formatCurrency(monthlySummary?.netSavings || 0)}
          change={savingsChange.message}
          changeType={savingsChange.type}
          icon={PiggyBank}
          iconBg="#dbeafe"
          iconColor="#1e40af"
        />
      </div>

      {/* Charts Row */}
      <div className={styles.chartsRow}>
        <div className={styles.card}>
          <div className={styles.cardHeader}>
            <h2 className={styles.cardTitle}>Income vs Expenses (Last 6 Months)</h2>
          </div>
          <IncomeExpenseChart data={incomeExpenseData} />
        </div>

        <div className={styles.card}>
          <div className={styles.cardHeader}>
            <h2 className={styles.cardTitle}>Income & Expense Categories</h2>
          </div>
          <CategorySpendingChart 
            expenseData={categoryExpenseData} 
            incomeData={categoryIncomeData}
          />
        </div>
      </div>

      {/* Accounts Section */}
      <div className={styles.card} style={{ marginBottom: '2rem' }}>
        <div className={styles.cardHeader}>
          <h2 className={styles.cardTitle}>Your Accounts</h2>
          <Link to="/accounts" className={styles.viewAllLink}>
            View All <ChevronRight size={16} />
          </Link>
        </div>
        {accounts.length > 0 ? (
          <div className={styles.accountsGrid}>
            {accounts.map((account) => (
              <AccountCard key={account.id} account={account} />
            ))}
          </div>
        ) : (
          <div className={styles.emptyState}>
            <Wallet size={48} className={styles.emptyIcon} />
            <div className={styles.emptyTitle}>No accounts yet</div>
            <p className={styles.emptyText}>Create your first account to start tracking</p>
            <Link to="/accounts">
              <button className={styles.primaryButton}>
                <Plus size={20} />
                Add Account
              </button>
            </Link>
          </div>
        )}
      </div>

      {/* Recent Transactions */}
      <div className={styles.card}>
        <div className={styles.cardHeader}>
          <h2 className={styles.cardTitle}>Recent Transactions</h2>
          <Link to="/transactions" className={styles.viewAllLink}>
            View All <ChevronRight size={16} />
          </Link>
        </div>
        {recentTransactions.length > 0 ? (
          <TransactionTable transactions={recentTransactions} />
        ) : (
          <div className={styles.emptyState}>
            <ArrowLeftRight size={48} className={styles.emptyIcon} />
            <div className={styles.emptyTitle}>No transactions yet</div>
            <p className={styles.emptyText}>Add your first transaction to get started</p>
            <Link to="/transactions">
              <button className={styles.primaryButton}>
                <Plus size={20} />
                Add Transaction
              </button>
            </Link>
          </div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;