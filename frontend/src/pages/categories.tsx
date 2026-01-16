import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, Loader, Tag, FolderOpen } from 'lucide-react';
import api from '@config/api';
import API_ENDPOINTS from '@config/endpoints';
import CategoryModal, { CategoryFormData } from '@components/categorymodal';
import DeleteConfirmModal from '@components/deleteconfirmmodal';
import { Category, CategoryType, ApiResponse } from '@typings/index';
import styles from '@styles/categories.module.css';

const Categories = () => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);

  const [modalOpen, setModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState<'create' | 'edit'>('create');
  const [modalType, setModalType] = useState<CategoryType>('EXPENSE' as CategoryType);
  const [selectedCategory, setSelectedCategory] = useState<Category | undefined>();

  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [categoryToDelete, setCategoryToDelete] = useState<Category | null>(null);
  const [deleteLoading, setDeleteLoading] = useState(false);

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      setLoading(true);
      console.log('[CATEGORIES] Fetching categories');

      const response = await api.get<ApiResponse<Category[]>>(API_ENDPOINTS.CATEGORIES.BASE);

      console.log('[CATEGORIES] Categories fetched:', response.data.data.length);
      setCategories(response.data.data);
    } catch (error: any) {
      console.error('[CATEGORIES] Error fetching categories:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateCategory = async (data: CategoryFormData) => {
    try {
      console.log('[CATEGORIES] Creating category:', data);

      await api.post<ApiResponse<Category>>(API_ENDPOINTS.CATEGORIES.BASE, data);

      console.log('[CATEGORIES] Category created successfully');
      await fetchCategories();
    } catch (error: any) {
      console.error('[CATEGORIES] Error creating category:', error);
      alert(error.response?.data?.message || 'Failed to create category');
      throw error;
    }
  };

  const handleUpdateCategory = async (data: CategoryFormData) => {
    if (!selectedCategory) return;

    try {
      console.log('[CATEGORIES] Updating category:', selectedCategory.id);

      await api.put<ApiResponse<Category>>(
        API_ENDPOINTS.CATEGORIES.BY_ID(selectedCategory.id),
        { name: data.name }
      );

      console.log('[CATEGORIES] Category updated successfully');
      await fetchCategories();
    } catch (error: any) {
      console.error('[CATEGORIES] Error updating category:', error);
      alert(error.response?.data?.message || 'Failed to update category');
      throw error;
    }
  };

  const handleDeleteCategory = async () => {
    if (!categoryToDelete) return;

    try {
      setDeleteLoading(true);
      console.log('[CATEGORIES] Deleting category:', categoryToDelete.id);

      await api.delete(API_ENDPOINTS.CATEGORIES.BY_ID(categoryToDelete.id));

      console.log('[CATEGORIES] Category deleted successfully');
      setDeleteModalOpen(false);
      setCategoryToDelete(null);
      await fetchCategories();
    } catch (error: any) {
      console.error('[CATEGORIES] Error deleting category:', error);
      alert(error.response?.data?.message || 'Failed to delete category');
    } finally {
      setDeleteLoading(false);
    }
  };

  const openCreateModal = (type: CategoryType) => {
    setModalMode('create');
    setModalType(type);
    setSelectedCategory(undefined);
    setModalOpen(true);
  };

  const openEditModal = (category: Category) => {
    setModalMode('edit');
    setModalType(category.type);
    setSelectedCategory(category);
    setModalOpen(true);
  };

  const openDeleteModal = (category: Category) => {
    setCategoryToDelete(category);
    setDeleteModalOpen(true);
  };

  const incomeCategories = categories.filter(cat => cat.type === 'INCOME');
  const expenseCategories = categories.filter(cat => cat.type === 'EXPENSE');

  if (loading) {
    return (
      <div className={styles.loading}>
        <Loader size={40} className="spinner" style={{ color: '#10b981' }} />
      </div>
    );
  }

  const renderCategoryList = (categoriesList: Category[], type: CategoryType) => {
    if (categoriesList.length === 0) {
      return (
        <div className={styles.emptyState}>
          <FolderOpen size={48} className={styles.emptyIcon} />
          <p className={styles.emptyText}>
            No {type.toLowerCase()} categories yet
          </p>
        </div>
      );
    }

    return (
      <div className={styles.categoriesList}>
        {categoriesList.map(category => (
          <div key={category.id} className={styles.categoryItem}>
            <div className={styles.categoryLeft}>
              <div
                className={styles.categoryIcon}
                style={{
                  background: type === 'INCOME' ? '#dcfce7' : '#fee2e2',
                  color: type === 'INCOME' ? '#166534' : '#991b1b',
                }}
              >
                <Tag size={20} />
              </div>
              <div className={styles.categoryInfo}>
                <div className={styles.categoryName}>{category.name}</div>
                <div className={styles.categoryMeta}>
                  {category.isGlobal ? (
                    <span className={styles.globalBadge}>Global</span>
                  ) : (
                    <span className={styles.customBadge}>Custom</span>
                  )}
                </div>
              </div>
            </div>
            <div className={styles.categoryActions}>
              {!category.isGlobal && (
                <>
                  <button
                    className={styles.iconButton}
                    onClick={() => openEditModal(category)}
                    title="Edit category"
                  >
                    <Edit size={18} />
                  </button>
                  <button
                    className={`${styles.iconButton} ${styles.delete}`}
                    onClick={() => openDeleteModal(category)}
                    title="Delete category"
                  >
                    <Trash2 size={18} />
                  </button>
                </>
              )}
            </div>
          </div>
        ))}
      </div>
    );
  };

  return (
    <div className={styles.categories}>
      <div className={styles.header}>
        <h1 className={styles.title}>Categories</h1>
        <p className={styles.subtitle}>Organize your income and expenses</p>
      </div>

      <div className={styles.columnsContainer}>
        {/* Income Categories */}
        <div className={styles.column}>
          <div className={styles.columnHeader}>
            <div className={styles.columnTitle}>
              <span>Income Categories</span>
              <span className={`${styles.badge} ${styles.badgeIncome}`}>
                {incomeCategories.length}
              </span>
            </div>
            <button
              className={`${styles.addButton} ${styles.addButtonIncome}`}
              onClick={() => openCreateModal('INCOME' as CategoryType)}
            >
              <Plus size={16} />
              Add Income
            </button>
          </div>
          {renderCategoryList(incomeCategories, 'INCOME' as CategoryType)}
        </div>

        {/* Expense Categories */}
        <div className={styles.column}>
          <div className={styles.columnHeader}>
            <div className={styles.columnTitle}>
              <span>Expense Categories</span>
              <span className={`${styles.badge} ${styles.badgeExpense}`}>
                {expenseCategories.length}
              </span>
            </div>
            <button
              className={`${styles.addButton} ${styles.addButtonExpense}`}
              onClick={() => openCreateModal('EXPENSE' as CategoryType)}
            >
              <Plus size={16} />
              Add Expense
            </button>
          </div>
          {renderCategoryList(expenseCategories, 'EXPENSE' as CategoryType)}
        </div>
      </div>

      <CategoryModal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        onSubmit={modalMode === 'create' ? handleCreateCategory : handleUpdateCategory}
        category={selectedCategory}
        mode={modalMode}
        defaultType={modalType}
      />

      <DeleteConfirmModal
        isOpen={deleteModalOpen}
        onClose={() => setDeleteModalOpen(false)}
        onConfirm={handleDeleteCategory}
        title="Delete Category"
        message={`Are you sure you want to delete "${categoryToDelete?.name}"? This will remove the category from all associated transactions.`}
        loading={deleteLoading}
      />
    </div>
  );
};

export default Categories;