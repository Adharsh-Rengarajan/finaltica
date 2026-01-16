import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, Loader, CreditCard, TrendingUp, Wallet, Filter } from 'lucide-react';
import api from '@config/api';
import API_ENDPOINTS from '@config/endpoints';
import AccountModal, { AccountFormData } from '@components/accountmodal';
import DeleteConfirmModal from '@components/deleteconfirmmodal';
import { Account, AccountType, ApiResponse } from '@typings/index';
import { formatCurrency, formatDate } from '@utils/formatters';
import styles from '@styles/accounts.module.css';

const Accounts = () => {
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [filteredAccounts, setFilteredAccounts] = useState<Account[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState<string>('ALL');
  
  const [modalOpen, setModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState<'create' | 'edit'>('create');
  const [selectedAccount, setSelectedAccount] = useState<Account | undefined>();
  
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [accountToDelete, setAccountToDelete] = useState<Account | null>(null);
  const [deleteLoading, setDeleteLoading] = useState(false);

  useEffect(() => {
    fetchAccounts();
  }, []);

  useEffect(() => {
    if (filter === 'ALL') {
      setFilteredAccounts(accounts);
    } else {
      setFilteredAccounts(accounts.filter(acc => acc.type === filter));
    }
  }, [filter, accounts]);

  const fetchAccounts = async () => {
    try {
      setLoading(true);
      console.log('[ACCOUNTS] Fetching accounts');
      
      const response = await api.get<ApiResponse<Account[]>>(API_ENDPOINTS.ACCOUNTS.BASE);
      
      console.log('[ACCOUNTS] Accounts fetched:', response.data.data.length);
      setAccounts(response.data.data);
    } catch (error: any) {
      console.error('[ACCOUNTS] Error fetching accounts:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateAccount = async (data: AccountFormData) => {
    try {
      // Match backend DTO exactly
      const payload = {
        name: data.name,
        type: data.type,
        currency: data.currency,
        initialBalance: data.initialBalance || 0
      };
      
      console.log('[ACCOUNTS] Creating account with payload:', payload);
      console.log('[ACCOUNTS] Payload types:', {
        name: typeof payload.name,
        type: typeof payload.type,
        currency: typeof payload.currency,
        initialBalance: typeof payload.initialBalance
      });
      
      const response = await api.post<ApiResponse<Account>>(
        API_ENDPOINTS.ACCOUNTS.BASE,
        payload
      );
      
      console.log('[ACCOUNTS] Account created successfully:', response.data);
      await fetchAccounts();
    } catch (error: any) {
      console.error('[ACCOUNTS] Error creating account:', error);
      console.error('[ACCOUNTS] Error response:', error.response?.data);
      console.error('[ACCOUNTS] Error status:', error.response?.status);
      console.error('[ACCOUNTS] Full error object:', JSON.stringify(error.response?.data, null, 2));
      
      // Show user-friendly error message
      const errorMessage = error.response?.data?.message || 
                          'Failed to create account. Check console for details.';
      alert(errorMessage);
      throw error;
    }
  };

  const handleUpdateAccount = async (data: AccountFormData) => {
    if (!selectedAccount) return;

    try {
      console.log('[ACCOUNTS] Updating account:', selectedAccount.id);
      
      await api.put<ApiResponse<Account>>(
        API_ENDPOINTS.ACCOUNTS.BY_ID(selectedAccount.id),
        {
          name: data.name,
          currency: data.currency,
        }
      );
      
      console.log('[ACCOUNTS] Account updated successfully');
      await fetchAccounts();
    } catch (error: any) {
      console.error('[ACCOUNTS] Error updating account:', error);
      console.error('[ACCOUNTS] Error response:', error.response?.data);
      
      const errorMessage = error.response?.data?.message || 'Failed to update account';
      alert(errorMessage);
      throw error;
    }
  };

  const handleDeleteAccount = async () => {
    if (!accountToDelete) return;

    try {
      setDeleteLoading(true);
      console.log('[ACCOUNTS] Deleting account:', accountToDelete.id);
      
      await api.delete(API_ENDPOINTS.ACCOUNTS.BY_ID(accountToDelete.id));
      
      console.log('[ACCOUNTS] Account deleted successfully');
      setDeleteModalOpen(false);
      setAccountToDelete(null);
      await fetchAccounts();
    } catch (error: any) {
      console.error('[ACCOUNTS] Error deleting account:', error);
      console.error('[ACCOUNTS] Error response:', error.response?.data);
      
      const errorMessage = error.response?.data?.message || 'Failed to delete account';
      alert(errorMessage);
    } finally {
      setDeleteLoading(false);
    }
  };

  const openCreateModal = () => {
    setModalMode('create');
    setSelectedAccount(undefined);
    setModalOpen(true);
  };

  const openEditModal = (account: Account) => {
    setModalMode('edit');
    setSelectedAccount(account);
    setModalOpen(true);
  };

  const openDeleteModal = (account: Account) => {
    setAccountToDelete(account);
    setDeleteModalOpen(true);
  };

  const getIcon = (type: AccountType) => {
    switch (type) {
      case 'INVESTMENT':
        return <TrendingUp size={24} />;
      case 'CASH':
        return <Wallet size={24} />;
      default:
        return <CreditCard size={24} />;
    }
  };

  const getIconBg = (type: AccountType) => {
    switch (type) {
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

  const getIconColor = (type: AccountType) => {
    switch (type) {
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

  const getBadgeStyle = (type: AccountType) => {
    switch (type) {
      case 'CHECKING':
        return { background: '#dbeafe', color: '#1e40af' };
      case 'CREDIT':
        return { background: '#fee2e2', color: '#991b1b' };
      case 'INVESTMENT':
        return { background: '#dcfce7', color: '#166534' };
      case 'CASH':
        return { background: '#f3f4f6', color: '#374151' };
      default:
        return { background: '#f3f4f6', color: '#374151' };
    }
  };

  if (loading) {
    return (
      <div className={styles.loading}>
        <Loader size={40} className="spinner" style={{ color: '#10b981' }} />
      </div>
    );
  }

  return (
    <div className={styles.accounts}>
      <div className={styles.header}>
        <div className={styles.headerLeft}>
          <h1 className={styles.title}>Accounts</h1>
          <p className={styles.subtitle}>
            Manage your financial accounts
          </p>
        </div>
        <div className={styles.headerRight}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Filter size={20} style={{ color: 'var(--text-secondary)' }} />
            <select
              className={styles.filterSelect}
              value={filter}
              onChange={(e) => setFilter(e.target.value)}
            >
              <option value="ALL">All Types</option>
              <option value="CHECKING">Checking</option>
              <option value="CREDIT">Credit Card</option>
              <option value="INVESTMENT">Investment</option>
              <option value="CASH">Cash</option>
            </select>
          </div>
          <button className={styles.addButton} onClick={openCreateModal}>
            <Plus size={20} />
            Add Account
          </button>
        </div>
      </div>

      {filteredAccounts.length > 0 ? (
        <div className={styles.accountsGrid}>
          {filteredAccounts.map((account) => (
            <div key={account.id} className={styles.accountCard}>
              <div className={styles.accountHeader}>
                <div
                  className={styles.iconWrapper}
                  style={{
                    background: getIconBg(account.type),
                    color: getIconColor(account.type),
                  }}
                >
                  {getIcon(account.type)}
                </div>
                <div className={styles.accountActions}>
                  <button
                    className={styles.iconButton}
                    onClick={() => openEditModal(account)}
                    title="Edit account"
                  >
                    <Edit size={18} />
                  </button>
                  <button
                    className={`${styles.iconButton} ${styles.delete}`}
                    onClick={() => openDeleteModal(account)}
                    title="Delete account"
                  >
                    <Trash2 size={18} />
                  </button>
                </div>
              </div>
              <span className={styles.typeBadge} style={getBadgeStyle(account.type)}>
                {account.type}
              </span>
              <div className={styles.accountName}>{account.name}</div>
              <div className={styles.accountBalance}>
                {formatCurrency(account.currentBalance, account.currency)}
                <span className={styles.currency}>{account.currency}</span>
              </div>
              <div className={styles.accountMeta}>
                <span>Created {formatDate(account.createdAt)}</span>
                <span>Updated {formatDate(account.updatedAt)}</span>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className={styles.emptyState}>
          <Wallet size={64} className={styles.emptyIcon} />
          <div className={styles.emptyTitle}>
            {filter === 'ALL' ? 'No accounts yet' : `No ${filter.toLowerCase()} accounts`}
          </div>
          <p className={styles.emptyText}>
            {filter === 'ALL'
              ? 'Create your first account to start tracking your finances'
              : `Create a ${filter.toLowerCase()} account to get started`}
          </p>
          <button className={styles.addButton} onClick={openCreateModal}>
            <Plus size={20} />
            Add Account
          </button>
        </div>
      )}

      <AccountModal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        onSubmit={modalMode === 'create' ? handleCreateAccount : handleUpdateAccount}
        account={selectedAccount}
        mode={modalMode}
      />

      <DeleteConfirmModal
        isOpen={deleteModalOpen}
        onClose={() => setDeleteModalOpen(false)}
        onConfirm={handleDeleteAccount}
        title="Delete Account"
        message={`Are you sure you want to delete "${accountToDelete?.name}"? This action cannot be undone. Make sure this account has no transactions.`}
        loading={deleteLoading}
      />
    </div>
  );
};

export default Accounts;