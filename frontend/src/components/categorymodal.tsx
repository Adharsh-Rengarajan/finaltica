import { useState, FormEvent, useEffect } from 'react';
import { X, AlertCircle } from 'lucide-react';
import { Category, CategoryType } from '@typings/index';
import styles from '@styles/accountmodal.module.css';

interface CategoryModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: CategoryFormData) => Promise<void>;
  category?: Category;
  mode: 'create' | 'edit';
  defaultType?: CategoryType;
}

export interface CategoryFormData {
  name: string;
  type: CategoryType;
}

const CategoryModal = ({ isOpen, onClose, onSubmit, category, mode, defaultType }: CategoryModalProps) => {
  const [formData, setFormData] = useState<CategoryFormData>({
    name: '',
    type: defaultType || 'EXPENSE' as CategoryType,
  });

  const [errors, setErrors] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (category && mode === 'edit') {
      setFormData({
        name: category.name,
        type: category.type,
      });
    } else {
      setFormData({
        name: '',
        type: defaultType || 'EXPENSE' as CategoryType,
      });
    }
    setErrors({});
  }, [category, mode, defaultType, isOpen]);

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.name.trim()) {
      newErrors.name = 'Category name is required';
    } else if (formData.name.length < 2 || formData.name.length > 100) {
      newErrors.name = 'Category name must be between 2 and 100 characters';
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
      console.error('[CATEGORY_MODAL] Submit error:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (field: string, value: string) => {
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
            {mode === 'create' ? 'Add Category' : 'Edit Category'}
          </h2>
          <button className={styles.closeButton} onClick={onClose}>
            <X size={24} />
          </button>
        </div>

        <form className={styles.form} onSubmit={handleSubmit}>
          <div className={styles.formGroup}>
            <label className={styles.label}>Category Name</label>
            <input
              type="text"
              className={`${styles.input} ${errors.name ? styles.error : ''}`}
              placeholder="e.g., Groceries, Salary"
              value={formData.name}
              onChange={(e) => handleChange('name', e.target.value)}
              maxLength={100}
            />
            {errors.name && (
              <span className={styles.errorMessage}>
                <AlertCircle size={16} />
                {errors.name}
              </span>
            )}
          </div>

          <div className={styles.formGroup}>
            <label className={styles.label}>Type</label>
            <select
              className={styles.select}
              value={formData.type}
              onChange={(e) => handleChange('type', e.target.value)}
              disabled={mode === 'edit'}
            >
              <option value="INCOME">Income</option>
              <option value="EXPENSE">Expense</option>
            </select>
            {mode === 'edit' && (
              <span style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                Category type cannot be changed
              </span>
            )}
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
              {loading ? 'Saving...' : mode === 'create' ? 'Create Category' : 'Save Changes'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CategoryModal;