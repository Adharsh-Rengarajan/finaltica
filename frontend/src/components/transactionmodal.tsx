import { useState, FormEvent, useEffect } from 'react';
import { X, AlertCircle, ArrowDown, ArrowUp, ArrowLeftRight } from 'lucide-react';
import { Account, Category, TransactionType, PaymentMode } from '@typings/index';
import styles from '@styles/transactionmodal.module.css';

interface TransactionModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: TransactionFormData) => Promise<void>;
  accounts: Account[];
  categories: Category[];
}

export interface TransactionFormData {
  accountId?: string;
  fromAccountId?: string;
  toAccountId?: string;
  categoryId?: string;
  amount: number;
  type: TransactionType;
  description: string;
  transactionDate: string;
  paymentMode: PaymentMode;
}

const TransactionModal = ({ isOpen, onClose, onSubmit, accounts, categories }: TransactionModalProps) => {
  const [activeTab, setActiveTab] = useState<'transaction' | 'transfer'>('transaction');
  
  const [formData, setFormData] = useState<TransactionFormData>({
    accountId: '',
    categoryId: '',
    amount: 0,
    type: 'EXPENSE' as TransactionType,
    description: '',
    transactionDate: new Date().toISOString().slice(0, 16),
    paymentMode: 'CARD' as PaymentMode,
  });

  const [transferData, setTransferData] = useState({
    fromAccountId: '',
    toAccountId: '',
    amount: 0,
    description: '',
    transactionDate: new Date().toISOString().slice(0, 16),
    paymentMode: 'ACH' as PaymentMode,
  });

  const [errors, setErrors] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!isOpen) {
      setFormData({
        accountId: '',
        categoryId: '',
        amount: 0,
        type: 'EXPENSE' as TransactionType,
        description: '',
        transactionDate: new Date().toISOString().slice(0, 16),
        paymentMode: 'CARD' as PaymentMode,
      });
      setTransferData({
        fromAccountId: '',
        toAccountId: '',
        amount: 0,
        description: '',
        transactionDate: new Date().toISOString().slice(0, 16),
        paymentMode: 'ACH' as PaymentMode,
      });
      setErrors({});
      setActiveTab('transaction');
    }
  }, [isOpen]);

  const validateTransaction = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.accountId) {
      newErrors.accountId = 'Account is required';
    }

    if (formData.amount <= 0) {
      newErrors.amount = 'Amount must be greater than 0';
    }

    if (!formData.transactionDate) {
      newErrors.transactionDate = 'Date is required';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const validateTransfer = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!transferData.fromAccountId) {
      newErrors.fromAccountId = 'From account is required';
    }

    if (!transferData.toAccountId) {
      newErrors.toAccountId = 'To account is required';
    }

    if (transferData.fromAccountId === transferData.toAccountId) {
      newErrors.toAccountId = 'Cannot transfer to the same account';
    }

    if (transferData.amount <= 0) {
      newErrors.amount = 'Amount must be greater than 0';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();

    if (activeTab === 'transaction' && !validateTransaction()) {
      return;
    }

    if (activeTab === 'transfer' && !validateTransfer()) {
      return;
    }

    setLoading(true);
    try {
      if (activeTab === 'transaction') {
        const amount = formData.type === 'INCOME' ? Math.abs(formData.amount) : -Math.abs(formData.amount);
        await onSubmit({
          ...formData,
          amount,
        });
      } else {
        await onSubmit({
          fromAccountId: transferData.fromAccountId,
          toAccountId: transferData.toAccountId,
          amount: Math.abs(transferData.amount),
          type: 'TRANSFER' as TransactionType,
          description: transferData.description,
          transactionDate: transferData.transactionDate,
          paymentMode: transferData.paymentMode,
        });
      }
      onClose();
    } catch (error) {
      console.error('[TRANSACTION_MODAL] Submit error:', error);
    } finally {
      setLoading(false);
    }
  };

  const filteredCategories = categories.filter(cat => {
    if (formData.type === 'TRANSFER') return false;
    if (formData.type === 'INCOME') return cat.type === 'INCOME';
    if (formData.type === 'EXPENSE') return cat.type === 'EXPENSE';
    return false;
  });

  if (!isOpen) return null;

  return (
    <div className={styles.modalBackdrop} onClick={onClose}>
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <div className={styles.modalHeader}>
          <h2 className={styles.modalTitle}>Add Transaction</h2>
          <button className={styles.closeButton} onClick={onClose}>
            <X size={24} />
          </button>
        </div>

        <div className={styles.tabs}>
          <button
            className={`${styles.tab} ${activeTab === 'transaction' ? styles.active : ''}`}
            onClick={() => setActiveTab('transaction')}
          >
            <ArrowDown size={18} />
            Income / Expense
          </button>
          <button
            className={`${styles.tab} ${activeTab === 'transfer' ? styles.active : ''}`}
            onClick={() => setActiveTab('transfer')}
          >
            <ArrowLeftRight size={18} />
            Transfer
          </button>
        </div>

        <form className={styles.form} onSubmit={handleSubmit}>
          {activeTab === 'transaction' ? (
            <>
              <div className={styles.formGroup}>
                <label className={styles.label}>Transaction Type</label>
                <div className={styles.typeRadios}>
                  <label className={`${styles.radioLabel} ${styles.income} ${formData.type === 'INCOME' ? styles.selected : ''}`}>
                    <input
                      type="radio"
                      className={styles.radio}
                      name="type"
                      value="INCOME"
                      checked={formData.type === 'INCOME'}
                      onChange={() => setFormData(prev => ({ ...prev, type: 'INCOME' as TransactionType, categoryId: '' }))}
                    />
                    <ArrowDown size={18} />
                    Income
                  </label>
                  <label className={`${styles.radioLabel} ${styles.expense} ${formData.type === 'EXPENSE' ? styles.selected : ''}`}>
                    <input
                      type="radio"
                      className={styles.radio}
                      name="type"
                      value="EXPENSE"
                      checked={formData.type === 'EXPENSE'}
                      onChange={() => setFormData(prev => ({ ...prev, type: 'EXPENSE' as TransactionType, categoryId: '' }))}
                    />
                    <ArrowUp size={18} />
                    Expense
                  </label>
                </div>
              </div>

              <div className={styles.formRow}>
                <div className={styles.formGroup}>
                  <label className={styles.label}>Account</label>
                  <select
                    className={`${styles.select} ${errors.accountId ? styles.error : ''}`}
                    value={formData.accountId}
                    onChange={(e) => setFormData(prev => ({ ...prev, accountId: e.target.value }))}
                  >
                    <option value="">Select account</option>
                    {accounts.map(account => (
                      <option key={account.id} value={account.id}>
                        {account.name} ({account.type})
                      </option>
                    ))}
                  </select>
                  {errors.accountId && (
                    <span className={styles.errorMessage}>
                      <AlertCircle size={16} />
                      {errors.accountId}
                    </span>
                  )}
                </div>

                <div className={styles.formGroup}>
                  <label className={styles.label}>Category</label>
                  <select
                    className={styles.select}
                    value={formData.categoryId}
                    onChange={(e) => setFormData(prev => ({ ...prev, categoryId: e.target.value }))}
                  >
                    <option value="">Select category (optional)</option>
                    {filteredCategories.map(category => (
                      <option key={category.id} value={category.id}>
                        {category.name}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div className={styles.formRow}>
                <div className={styles.formGroup}>
                  <label className={styles.label}>Amount</label>
                  <input
                    type="number"
                    step="0.01"
                    className={`${styles.input} ${errors.amount ? styles.error : ''}`}
                    placeholder="0.00"
                    value={formData.amount || ''}
                    onChange={(e) => setFormData(prev => ({ ...prev, amount: parseFloat(e.target.value) || 0 }))}
                  />
                  {errors.amount && (
                    <span className={styles.errorMessage}>
                      <AlertCircle size={16} />
                      {errors.amount}
                    </span>
                  )}
                </div>

                <div className={styles.formGroup}>
                  <label className={styles.label}>Payment Mode</label>
                  <select
                    className={styles.select}
                    value={formData.paymentMode}
                    onChange={(e) => setFormData(prev => ({ ...prev, paymentMode: e.target.value as PaymentMode }))}
                  >
                    <option value="CARD">Card</option>
                    <option value="UPI">UPI</option>
                    <option value="ACH">ACH</option>
                    <option value="CASH">Cash</option>
                  </select>
                </div>
              </div>

              <div className={styles.formGroup}>
                <label className={styles.label}>Date & Time</label>
                <input
                  type="datetime-local"
                  className={`${styles.input} ${errors.transactionDate ? styles.error : ''}`}
                  value={formData.transactionDate}
                  onChange={(e) => setFormData(prev => ({ ...prev, transactionDate: e.target.value }))}
                />
                {errors.transactionDate && (
                  <span className={styles.errorMessage}>
                    <AlertCircle size={16} />
                    {errors.transactionDate}
                  </span>
                )}
              </div>

              <div className={styles.formGroup}>
                <label className={styles.label}>Description (Optional)</label>
                <textarea
                  className={styles.textarea}
                  placeholder="Add a note about this transaction..."
                  value={formData.description}
                  onChange={(e) => setFormData(prev => ({ ...prev, description: e.target.value }))}
                  maxLength={500}
                />
              </div>
            </>
          ) : (
            <>
              <div className={styles.formRow}>
                <div className={styles.formGroup}>
                  <label className={styles.label}>From Account</label>
                  <select
                    className={`${styles.select} ${errors.fromAccountId ? styles.error : ''}`}
                    value={transferData.fromAccountId}
                    onChange={(e) => setTransferData(prev => ({ ...prev, fromAccountId: e.target.value }))}
                  >
                    <option value="">Select account</option>
                    {accounts.map(account => (
                      <option key={account.id} value={account.id}>
                        {account.name} ({account.type})
                      </option>
                    ))}
                  </select>
                  {errors.fromAccountId && (
                    <span className={styles.errorMessage}>
                      <AlertCircle size={16} />
                      {errors.fromAccountId}
                    </span>
                  )}
                </div>

                <div className={styles.formGroup}>
                  <label className={styles.label}>To Account</label>
                  <select
                    className={`${styles.select} ${errors.toAccountId ? styles.error : ''}`}
                    value={transferData.toAccountId}
                    onChange={(e) => setTransferData(prev => ({ ...prev, toAccountId: e.target.value }))}
                  >
                    <option value="">Select account</option>
                    {accounts.map(account => (
                      <option key={account.id} value={account.id}>
                        {account.name} ({account.type})
                      </option>
                    ))}
                  </select>
                  {errors.toAccountId && (
                    <span className={styles.errorMessage}>
                      <AlertCircle size={16} />
                      {errors.toAccountId}
                    </span>
                  )}
                </div>
              </div>

              <div className={styles.formRow}>
                <div className={styles.formGroup}>
                  <label className={styles.label}>Amount</label>
                  <input
                    type="number"
                    step="0.01"
                    className={`${styles.input} ${errors.amount ? styles.error : ''}`}
                    placeholder="0.00"
                    value={transferData.amount || ''}
                    onChange={(e) => setTransferData(prev => ({ ...prev, amount: parseFloat(e.target.value) || 0 }))}
                  />
                  {errors.amount && (
                    <span className={styles.errorMessage}>
                      <AlertCircle size={16} />
                      {errors.amount}
                    </span>
                  )}
                </div>

                <div className={styles.formGroup}>
                  <label className={styles.label}>Payment Mode</label>
                  <select
                    className={styles.select}
                    value={transferData.paymentMode}
                    onChange={(e) => setTransferData(prev => ({ ...prev, paymentMode: e.target.value as PaymentMode }))}
                  >
                    <option value="ACH">ACH</option>
                    <option value="CARD">Card</option>
                    <option value="UPI">UPI</option>
                    <option value="CASH">Cash</option>
                  </select>
                </div>
              </div>

              <div className={styles.formGroup}>
                <label className={styles.label}>Date & Time</label>
                <input
                  type="datetime-local"
                  className={styles.input}
                  value={transferData.transactionDate}
                  onChange={(e) => setTransferData(prev => ({ ...prev, transactionDate: e.target.value }))}
                />
              </div>

              <div className={styles.formGroup}>
                <label className={styles.label}>Description (Optional)</label>
                <textarea
                  className={styles.textarea}
                  placeholder="Add a note about this transfer..."
                  value={transferData.description}
                  onChange={(e) => setTransferData(prev => ({ ...prev, description: e.target.value }))}
                  maxLength={500}
                />
              </div>
            </>
          )}

          <div className={styles.modalActions}>
            <button
              type="button"
              className={styles.cancelButton}
              onClick={onClose}
              disabled={loading}
            >
              Cancel
            </button>
            <button
              type="submit"
              className={styles.submitButton}
              disabled={loading}
            >
              {loading ? 'Saving...' : activeTab === 'transaction' ? 'Add Transaction' : 'Transfer Funds'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default TransactionModal;