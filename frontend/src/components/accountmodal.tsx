import { useState, FormEvent, useEffect } from 'react';
import { X, AlertCircle } from 'lucide-react';
import { Account, AccountType, CurrencyCode } from '@typings/index';
import styles from '@styles/accountmodal.module.css';

interface AccountModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: AccountFormData) => Promise<void>;
  account?: Account;
  mode: 'create' | 'edit';
}

export interface AccountFormData {
  name: string;
  type: AccountType;
  currency: CurrencyCode;
  initialBalance?: number;
}

const AccountModal = ({ isOpen, onClose, onSubmit, account, mode }: AccountModalProps) => {
  const [formData, setFormData] = useState<AccountFormData>({
    name: '',
    type: 'CHECKING' as AccountType,
    currency: 'USD' as CurrencyCode,
    initialBalance: 0,
  });

  const [errors, setErrors] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (account && mode === 'edit') {
      setFormData({
        name: account.name,
        type: account.type,
        currency: account.currency,
      });
    } else {
      setFormData({
        name: '',
        type: 'CHECKING' as AccountType,
        currency: 'USD' as CurrencyCode,
        initialBalance: 0,
      });
    }
    setErrors({});
  }, [account, mode, isOpen]);

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.name.trim()) {
      newErrors.name = 'Account name is required';
    } else if (formData.name.length < 2 || formData.name.length > 150) {
      newErrors.name = 'Account name must be between 2 and 150 characters';
    }

    if (mode === 'create') {
      if (formData.initialBalance === undefined || formData.initialBalance === null) {
        newErrors.initialBalance = 'Initial balance is required';
      } else if (formData.type === 'CREDIT' && formData.initialBalance > 0) {
        newErrors.initialBalance = 'Credit card balance must be zero or negative';
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setLoading(true);
    try {
      await onSubmit(formData);
      onClose();
    } catch (error) {
      console.error('[ACCOUNT_MODAL] Submit error:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (field: string, value: string | number) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: '' }));
    }
  };

  if (!isOpen) return null;

  return (
    <div className={styles.modalBackdrop} onClick={onClose}>
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <div className={styles.modalHeader}>
          <h2 className={styles.modalTitle}>
            {mode === 'create' ? 'Add New Account' : 'Edit Account'}
          </h2>
          <button className={styles.closeButton} onClick={onClose}>
            <X size={24} />
          </button>
        </div>

        <form className={styles.form} onSubmit={handleSubmit}>
          <div className={styles.formGroup}>
            <label className={styles.label}>Account Name</label>
            <input
              type="text"
              className={`${styles.input} ${errors.name ? styles.error : ''}`}
              placeholder="e.g., Chase Checking"
              value={formData.name}
              onChange={(e) => handleChange('name', e.target.value)}
            />
            {errors.name && (
              <span className={styles.errorMessage}>
                <AlertCircle size={16} />
                {errors.name}
              </span>
            )}
          </div>

          <div className={styles.formGroup}>
            <label className={styles.label}>Account Type</label>
            <select
              className={`${styles.select} ${errors.type ? styles.error : ''}`}
              value={formData.type}
              onChange={(e) => handleChange('type', e.target.value)}
              disabled={mode === 'edit'}
            >
              <option value="CHECKING">Checking</option>
              <option value="CREDIT">Credit Card</option>
              <option value="INVESTMENT">Investment</option>
              <option value="CASH">Cash</option>
            </select>
            {mode === 'edit' && (
              <span style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                Account type cannot be changed
              </span>
            )}
          </div>

          <div className={styles.formGroup}>
            <label className={styles.label}>Currency</label>
            <select
              className={`${styles.select} ${errors.currency ? styles.error : ''}`}
              value={formData.currency}
              onChange={(e) => handleChange('currency', e.target.value)}
            >
              <option value="USD">USD - US Dollar</option>
              <option value="INR">INR - Indian Rupee</option>
            </select>
          </div>

          {mode === 'create' && (
            <div className={styles.formGroup}>
              <label className={styles.label}>Initial Balance</label>
              <input
                type="number"
                step="0.01"
                className={`${styles.input} ${errors.initialBalance ? styles.error : ''}`}
                placeholder="0.00"
                value={formData.initialBalance}
                onChange={(e) => handleChange('initialBalance', parseFloat(e.target.value) || 0)}
              />
              {errors.initialBalance && (
                <span className={styles.errorMessage}>
                  <AlertCircle size={16} />
                  {errors.initialBalance}
                </span>
              )}
              {formData.type === 'CREDIT' && (
                <span style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                  For credit cards, use 0 or negative values
                </span>
              )}
            </div>
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
              {loading ? 'Saving...' : mode === 'create' ? 'Create Account' : 'Save Changes'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AccountModal;