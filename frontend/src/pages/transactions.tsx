import { useState, useEffect } from 'react';
import { Plus, ArrowLeftRight, Loader, X, Filter } from 'lucide-react';
import api from '@config/api';
import API_ENDPOINTS from '@config/endpoints';
import TransactionModal, { TransactionFormData } from '@components/transactionmodal';
import TransactionTable from '@components/transactiontable';
import DeleteConfirmModal from '@components/deleteconfirmmodal';
import { Transaction, Account, Category, TransactionType, ApiResponse } from '@typings/index';
import styles from '@styles/transactions.module.css';

const Transactions = () => {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);

  const [filters, setFilters] = useState({
    accountId: '',
    categoryId: '',
    type: '',
    startDate: '',
    endDate: '',
  });

  const [modalOpen, setModalOpen] = useState(false);
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [transactionToDelete, setTransactionToDelete] = useState<Transaction | null>(null);
  const [deleteLoading, setDeleteLoading] = useState(false);

  useEffect(() => {
    fetchData();
  }, []);

  useEffect(() => {
    applyFilters();
  }, [filters]);

  const fetchData = async () => {
    try {
      setLoading(true);
      console.log('[TRANSACTIONS] Fetching data');

      const [transactionsRes, accountsRes, categoriesRes] = await Promise.all([
        api.get<ApiResponse<Transaction[]>>(API_ENDPOINTS.TRANSACTIONS.BASE),
        api.get<ApiResponse<Account[]>>(API_ENDPOINTS.ACCOUNTS.BASE),
        api.get<ApiResponse<Category[]>>(API_ENDPOINTS.CATEGORIES.BASE),
      ]);

      console.log('[TRANSACTIONS] Data fetched successfully');
      setTransactions(transactionsRes.data.data);
      setAccounts(accountsRes.data.data);
      setCategories(categoriesRes.data.data);
    } catch (error: any) {
      console.error('[TRANSACTIONS] Error fetching data:', error);
    } finally {
      setLoading(false);
    }
  };

  const applyFilters = async () => {
    try {
      console.log('[TRANSACTIONS] Applying filters:', filters);

      const params = new URLSearchParams();
      if (filters.accountId) params.append('accountId', filters.accountId);
      if (filters.categoryId) params.append('categoryId', filters.categoryId);
      if (filters.type) params.append('type', filters.type);
      if (filters.startDate) params.append('startDate', new Date(filters.startDate).toISOString());
      if (filters.endDate) params.append('endDate', new Date(filters.endDate).toISOString());

      const queryString = params.toString();
      const url = queryString ? `${API_ENDPOINTS.TRANSACTIONS.BASE}?${queryString}` : API_ENDPOINTS.TRANSACTIONS.BASE;

      const response = await api.get<ApiResponse<Transaction[]>>(url);
      setTransactions(response.data.data);
    } catch (error: any) {
      console.error('[TRANSACTIONS] Error applying filters:', error);
    }
  };

  const handleCreateTransaction = async (data: TransactionFormData) => {
    try {
      console.log('[TRANSACTIONS] Creating transaction:', data);

      if (data.fromAccountId && data.toAccountId) {
        // Transfer
        await api.post<ApiResponse<any>>(API_ENDPOINTS.TRANSACTIONS.TRANSFER, {
          fromAccountId: data.fromAccountId,
          toAccountId: data.toAccountId,
          amount: data.amount,
          description: data.description,
          transactionDate: new Date(data.transactionDate).toISOString(),
          paymentMode: data.paymentMode,
        });
      } else {
        // Regular transaction
        await api.post<ApiResponse<Transaction>>(API_ENDPOINTS.TRANSACTIONS.BASE, {
          accountId: data.accountId,
          categoryId: data.categoryId || undefined,
          amount: data.amount,
          type: data.type,
          description: data.description,
          transactionDate: new Date(data.transactionDate).toISOString(),
          paymentMode: data.paymentMode,
        });
      }

      console.log('[TRANSACTIONS] Transaction created successfully');
      await fetchData();
    } catch (error: any) {
      console.error('[TRANSACTIONS] Error creating transaction:', error);
      alert(error.response?.data?.message || 'Failed to create transaction');
      throw error;
    }
  };

  const handleDeleteTransaction = async () => {
    if (!transactionToDelete) return;

    try {
      setDeleteLoading(true);
      console.log('[TRANSACTIONS] Deleting transaction:', transactionToDelete.id);

      await api.delete(API_ENDPOINTS.TRANSACTIONS.BY_ID(transactionToDelete.id));

      console.log('[TRANSACTIONS] Transaction deleted successfully');
      setDeleteModalOpen(false);
      setTransactionToDelete(null);
      await fetchData();
    } catch (error: any) {
      console.error('[TRANSACTIONS] Error deleting transaction:', error);
      alert(error.response?.data?.message || 'Failed to delete transaction');
    } finally {
      setDeleteLoading(false);
    }
  };

  const clearFilters = () => {
    setFilters({
      accountId: '',
      categoryId: '',
      type: '',
      startDate: '',
      endDate: '',
    });
  };

  const hasActiveFilters = Object.values(filters).some(v => v !== '');

  if (loading) {
    return (
      <div className={styles.loading}>
        <Loader size={40} className="spinner" style={{ color: '#10b981' }} />
      </div>
    );
  }

  return (
    <div className={styles.transactions}>
      <div className={styles.header}>
        <div className={styles.headerLeft}>
          <h1 className={styles.title}>Transactions</h1>
          <p className={styles.subtitle}>Track all your financial activity</p>
        </div>
        <div className={styles.headerRight}>
          <button className={styles.addButton} onClick={() => setModalOpen(true)}>
            <Plus size={20} />
            Add Transaction
          </button>
        </div>
      </div>

      {/* Filter Bar */}
      <div className={styles.filterBar}>
        <div className={styles.filterGrid}>
          <div className={styles.filterGroup}>
            <label className={styles.filterLabel}>
              <Filter size={16} /> Account
            </label>
            <select
              className={styles.filterSelect}
              value={filters.accountId}
              onChange={(e) => setFilters(prev => ({ ...prev, accountId: e.target.value }))}
            >
              <option value="">All Accounts</option>
              {accounts.map(account => (
                <option key={account.id} value={account.id}>
                  {account.name}
                </option>
              ))}
            </select>
          </div>

          <div className={styles.filterGroup}>
            <label className={styles.filterLabel}>Category</label>
            <select
              className={styles.filterSelect}
              value={filters.categoryId}
              onChange={(e) => setFilters(prev => ({ ...prev, categoryId: e.target.value }))}
            >
              <option value="">All Categories</option>
              {categories.map(category => (
                <option key={category.id} value={category.id}>
                  {category.name} ({category.type})
                </option>
              ))}
            </select>
          </div>

          <div className={styles.filterGroup}>
            <label className={styles.filterLabel}>Type</label>
            <select
              className={styles.filterSelect}
              value={filters.type}
              onChange={(e) => setFilters(prev => ({ ...prev, type: e.target.value }))}
            >
              <option value="">All Types</option>
              <option value="INCOME">Income</option>
              <option value="EXPENSE">Expense</option>
              <option value="TRANSFER">Transfer</option>
            </select>
          </div>

          <div className={styles.filterGroup}>
            <label className={styles.filterLabel}>Date Range</label>
            <div style={{ display: 'flex', gap: '0.5rem' }}>
              <input
                type="date"
                className={styles.filterInput}
                value={filters.startDate}
                onChange={(e) => setFilters(prev => ({ ...prev, startDate: e.target.value }))}
                placeholder="Start"
              />
              <input
                type="date"
                className={styles.filterInput}
                value={filters.endDate}
                onChange={(e) => setFilters(prev => ({ ...prev, endDate: e.target.value }))}
                placeholder="End"
              />
            </div>
          </div>
        </div>

        {hasActiveFilters && (
          <div className={styles.filterActions}>
            <button className={styles.clearButton} onClick={clearFilters}>
              <X size={16} />
              Clear Filters
            </button>
          </div>
        )}
      </div>

      {/* Transactions Table */}
      <div className={styles.tableCard}>
        {transactions.length > 0 ? (
          <div className={styles.tableWrapper}>
            <TransactionTable transactions={transactions} />
          </div>
        ) : (
          <div className={styles.emptyState}>
            <ArrowLeftRight size={64} className={styles.emptyIcon} />
            <div className={styles.emptyTitle}>No transactions found</div>
            <p className={styles.emptyText}>
              {hasActiveFilters
                ? 'Try adjusting your filters or add a new transaction'
                : 'Add your first transaction to get started'}
            </p>
            <button className={styles.addButton} onClick={() => setModalOpen(true)}>
              <Plus size={20} />
              Add Transaction
            </button>
          </div>
        )}
      </div>

      <TransactionModal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        onSubmit={handleCreateTransaction}
        accounts={accounts}
        categories={categories}
      />

      <DeleteConfirmModal
        isOpen={deleteModalOpen}
        onClose={() => setDeleteModalOpen(false)}
        onConfirm={handleDeleteTransaction}
        title="Delete Transaction"
        message={`Are you sure you want to delete this transaction? This will update the account balance accordingly.`}
        loading={deleteLoading}
      />
    </div>
  );
};

export default Transactions;