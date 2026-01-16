import { PieChart, Pie, Cell, ResponsiveContainer, Legend, Tooltip } from 'recharts';

interface ChartData {
  name: string;
  value: number;
  type: 'income' | 'expense';
}

interface CategorySpendingChartProps {
  expenseData: Array<{ name: string; value: number }>;
  incomeData: Array<{ name: string; value: number }>;
}

const EXPENSE_COLORS = ['#ef4444', '#dc2626', '#f87171', '#fca5a5', '#fee2e2'];
const INCOME_COLORS = ['#10b981', '#059669', '#34d399', '#6ee7b7', '#a7f3d0'];

const CategorySpendingChart = ({ expenseData, incomeData }: CategorySpendingChartProps) => {
  // Combine both datasets with type indicator
  const combinedData: ChartData[] = [
    ...expenseData.map(item => ({ ...item, type: 'expense' as const })),
    ...incomeData.map(item => ({ ...item, type: 'income' as const })),
  ];

  if (combinedData.length === 0) {
    return (
      <div style={{ 
        height: '300px', 
        display: 'flex', 
        alignItems: 'center', 
        justifyContent: 'center', 
        color: 'var(--text-secondary)',
        flexDirection: 'column',
        gap: '1rem'
      }}>
        <p>No transaction data available</p>
        <p style={{ fontSize: '0.875rem' }}>Add income and expense transactions to see category breakdown</p>
      </div>
    );
  }

  const getColor = (entry: ChartData, index: number) => {
    if (entry.type === 'expense') {
      return EXPENSE_COLORS[index % EXPENSE_COLORS.length];
    }
    return INCOME_COLORS[index % INCOME_COLORS.length];
  };

  const renderLabel = (entry: any) => {
    const percent = (entry.percent * 100).toFixed(0);
    return `${percent}%`;
  };

  const CustomTooltip = ({ active, payload }: any) => {
    if (active && payload && payload.length) {
      const data = payload[0].payload;
      return (
        <div style={{
          backgroundColor: 'white',
          border: '1px solid #e5e7eb',
          borderRadius: '8px',
          padding: '0.75rem',
          boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
        }}>
          <p style={{ 
            fontWeight: 600, 
            marginBottom: '0.25rem',
            color: data.type === 'income' ? '#10b981' : '#ef4444'
          }}>
            {data.name}
          </p>
          <p style={{ fontSize: '0.875rem', color: '#6b7280' }}>
            {data.type === 'income' ? 'Income' : 'Expense'}: ${data.value.toFixed(2)}
          </p>
        </div>
      );
    }
    return null;
  };

  return (
    <ResponsiveContainer width="100%" height={300}>
      <PieChart>
        <Pie
          data={combinedData}
          cx="50%"
          cy="45%"
          labelLine={true}
          label={renderLabel}
          outerRadius={90}
          innerRadius={50}
          fill="#8884d8"
          dataKey="value"
          paddingAngle={2}
        >
          {combinedData.map((entry, index) => (
            <Cell 
              key={`cell-${index}`} 
              fill={getColor(entry, index)}
            />
          ))}
        </Pie>
        <Tooltip content={<CustomTooltip />} />
        <Legend 
          verticalAlign="bottom"
          height={60}
          iconType="circle"
          wrapperStyle={{ fontSize: '0.875rem' }}
          formatter={(value, entry: any) => {
            const type = entry.payload.type === 'income' ? '(Income)' : '(Expense)';
            return `${value} ${type}`;
          }}
        />
      </PieChart>
    </ResponsiveContainer>
  );
};

export default CategorySpendingChart;