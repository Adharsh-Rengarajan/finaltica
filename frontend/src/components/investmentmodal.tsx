import { useState, FormEvent, useEffect } from 'react';
import { X, AlertCircle } from 'lucide-react';
import { Account, AssetType, PaymentMode } from '@typings/index';
import styles from '@styles/transactionmodal.module.css';

interface InvestmentModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: InvestmentFormData) => Promise<void>;
  investmentAccounts: Account[];
}

export interface InvestmentFormData {
  accountId: string;
  assetSymbol: string;
  assetType: AssetType;
  quantity: number;
  pricePerUnit: number;
  description: string;
  transactionDate: string;
  paymentMode: PaymentMode;
}

const InvestmentModal = ({ isOpen, onClose, onSubmit, investmentAccounts }: InvestmentModalProps) => {
  const [formData, setFormData] = useState<InvestmentFormData>({
    accountId: '',
    assetSymbol: '',
    assetType: 'STOCK' as AssetType,
    quantity: 0,
    pricePerUnit: 0,
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
        assetSymbol: '',
        assetType: 'STOCK' as AssetType,
        quantity: 0,
        pricePerUnit: 0,
        description: '',
        transactionDate: new Date().toISOString().slice(0, 16),
        paymentMode: 'ACH' as PaymentMode,
      });
      setErrors({});
    }
  }, [isOpen]);

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.accountId) {
      newErrors.accountId = 'Investment account is required';
    }

    if (!formData.assetSymbol.trim()) {
      newErrors.assetSymbol = 'Asset symbol is required (e.g., AAPL, GOOGL)';
    }

    if (formData.quantity <= 0) {
      newErrors.quantity = 'Quantity must be greater than 0';
    }

    if (formData.pricePerUnit <= 0) {
      newErrors.pricePerUnit = 'Price per unit must be greater than 0';
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
      await onSubmit({
        ...formData,
        transactionDate: new Date(formData.transactionDate).toISOString(),
      });
      onClose();
    } catch (error) {
      console.error('[INVESTMENT_MODAL] Submit error:', error);
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

  const totalCost = formData.quantity * formData.pricePerUnit;

  if (!isOpen) return null;

  return (
    <div className={styles.modalBackdrop} onClick={onClose}>
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <div className={styles.modalHeader}>
          <h2 className={styles.modalTitle}>Buy Investment</h2>
          <button className={styles.closeButton} onClick={onClose}>
            <X size={24} />
          </button>
        </div>

        <form className={styles.form} onSubmit={handleSubmit}>
          <div className={styles.formGroup}>
            <label className={styles.label}>Investment Account</label>
            <select
              className={`${styles.select} ${errors.accountId ? styles.error : ''}`}
              value={formData.accountId}
              onChange={(e) => handleChange('accountId', e.target.value)}
            >
              <option value="">Select investment account</option>
              {investmentAccounts.map(account => (
                <option key={account.id} value={account.id}>
                  {account.name}
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

          <div className={styles.formRow}>
            <div className={styles.formGroup}>
              <label className={styles.label}>Asset Symbol</label>
              <input
                type="text"
                className={`${styles.input} ${errors.assetSymbol ? styles.error : ''}`}
                placeholder="AAPL, GOOGL, etc."
                value={formData.assetSymbol}
                onChange={(e) => handleChange('assetSymbol', e.target.value.toUpperCase())}
                maxLength={50}
              />
              {errors.assetSymbol && (
                <span className={styles.errorMessage}>
                  <AlertCircle size={16} />
                  {errors.assetSymbol}
                </span>
              )}
            </div>

            <div className={styles.formGroup}>
              <label className={styles.label}>Asset Type</label>
              <select
                className={styles.select}
                value={formData.assetType}
                onChange={(e) => handleChange('assetType', e.target.value)}
              >
                <option value="STOCK">Stock</option>
                <option value="MUTUAL_FUND">Mutual Fund</option>
              </select>
            </div>
          </div>

          <div className={styles.formRow}>
            <div className={styles.formGroup}>
              <label className={styles.label}>Quantity</label>
              <input
                type="number"
                step="0.00000001"
                className={`${styles.input} ${errors.quantity ? styles.error : ''}`}
                placeholder="0.00"
                value={formData.quantity || ''}
                onChange={(e) => handleChange('quantity', parseFloat(e.target.value) || 0)}
              />
              {errors.quantity && (
                <span className={styles.errorMessage}>
                  <AlertCircle size={16} />
                  {errors.quantity}
                </span>
              )}
            </div>

            <div className={styles.formGroup}>
              <label className={styles.label}>Price per Unit</label>
              <input
                type="number"
                step="0.0001"
                className={`${styles.input} ${errors.pricePerUnit ? styles.error : ''}`}
                placeholder="0.00"
                value={formData.pricePerUnit || ''}
                onChange={(e) => handleChange('pricePerUnit', parseFloat(e.target.value) || 0)}
              />
              {errors.pricePerUnit && (
                <span className={styles.errorMessage}>
                  <AlertCircle size={16} />
                  {errors.pricePerUnit}
                </span>
              )}
            </div>
          </div>

          <div className={styles.formGroup}>
            <label className={styles.label}>Total Cost</label>
            <div
              style={{
                padding: '0.75rem 1rem',
                background: '#dcfce7',
                border: '2px solid #bbf7d0',
                borderRadius: '8px',
                fontWeight: 700,
                fontSize: '1.125rem',
                color: '#166534',
              }}
            >
              ${totalCost.toFixed(2)}
            </div>
          </div>

          <div className={styles.formRow}>
            <div className={styles.formGroup}>
              <label className={styles.label}>Purchase Date</label>
              <input
                type="datetime-local"
                className={styles.input}
                value={formData.transactionDate}
                onChange={(e) => handleChange('transactionDate', e.target.value)}
              />
            </div>

            <div className={styles.formGroup}>
              <label className={styles.label}>Payment Mode</label>
              <select
                className={styles.select}
                value={formData.paymentMode}
                onChange={(e) => handleChange('paymentMode', e.target.value)}
              >
                <option value="ACH">ACH</option>
                <option value="CARD">Card</option>
                <option value="UPI">UPI</option>
                <option value="CASH">Cash</option>
              </select>
            </div>
          </div>

          <div className={styles.formGroup}>
            <label className={styles.label}>Description (Optional)</label>
            <textarea
              className={styles.textarea}
              placeholder="Add notes about this purchase..."
              value={formData.description}
              onChange={(e) => handleChange('description', e.target.value)}
              maxLength={500}
            />
          </div>

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
              {loading ? 'Processing...' : 'Buy Investment'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default InvestmentModal;