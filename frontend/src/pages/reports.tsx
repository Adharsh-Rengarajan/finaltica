import { useState } from 'react';
import { Calendar, CalendarRange, FileText, Download, Loader, CheckCircle } from 'lucide-react';
import api from '@config/api';
import API_ENDPOINTS from '@config/endpoints';
import { ApiResponse } from '@typings/index';
import styles from '@styles/reports.module.css';

interface GeneratedReport {
  downloadUrl: string;
  reportName: string;
  generatedAt: Date;
}

const Reports = () => {
  const [monthlyReport, setMonthlyReport] = useState<GeneratedReport | null>(null);
  const [customReport, setCustomReport] = useState<GeneratedReport | null>(null);
  
  const [monthlyLoading, setMonthlyLoading] = useState(false);
  const [customLoading, setCustomLoading] = useState(false);

  const [monthlyForm, setMonthlyForm] = useState({
    year: new Date().getFullYear().toString(),
    month: (new Date().getMonth() + 1).toString(),
  });

  const [customForm, setCustomForm] = useState({
    startDate: '',
    endDate: '',
  });

  const handleGenerateMonthly = async () => {
    try {
      setMonthlyLoading(true);
      console.log('[REPORTS] Generating monthly report:', monthlyForm);

      const response = await api.get<ApiResponse<{ downloadUrl: string }>>(
        `${API_ENDPOINTS.REPORTS.MONTHLY}?year=${monthlyForm.year}&month=${monthlyForm.month}`
      );

      console.log('[REPORTS] Monthly report generated:', response.data);

      const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
      const reportName = `${monthNames[parseInt(monthlyForm.month) - 1]} ${monthlyForm.year} Statement`;

      setMonthlyReport({
        downloadUrl: response.data.data.downloadUrl,
        reportName,
        generatedAt: new Date(),
      });
    } catch (error: any) {
      console.error('[REPORTS] Error generating monthly report:', error);
      alert(error.response?.data?.message || 'Failed to generate report');
    } finally {
      setMonthlyLoading(false);
    }
  };

  const handleGenerateCustom = async () => {
    if (!customForm.startDate || !customForm.endDate) {
      alert('Please select both start and end dates');
      return;
    }

    try {
      setCustomLoading(true);
      console.log('[REPORTS] Generating custom report:', customForm);

      const startDate = new Date(customForm.startDate).toISOString();
      const endDate = new Date(customForm.endDate).toISOString();

      const response = await api.get<ApiResponse<{ downloadUrl: string }>>(
        `${API_ENDPOINTS.REPORTS.CUSTOM}?startDate=${startDate}&endDate=${endDate}`
      );

      console.log('[REPORTS] Custom report generated:', response.data);

      const reportName = `Custom Report (${customForm.startDate} to ${customForm.endDate})`;

      setCustomReport({
        downloadUrl: response.data.data.downloadUrl,
        reportName,
        generatedAt: new Date(),
      });
    } catch (error: any) {
      console.error('[REPORTS] Error generating custom report:', error);
      alert(error.response?.data?.message || 'Failed to generate report');
    } finally {
      setCustomLoading(false);
    }
  };

  const currentYear = new Date().getFullYear();
  const years = Array.from({ length: 10 }, (_, i) => currentYear - i);
  const months = [
    { value: '1', label: 'January' },
    { value: '2', label: 'February' },
    { value: '3', label: 'March' },
    { value: '4', label: 'April' },
    { value: '5', label: 'May' },
    { value: '6', label: 'June' },
    { value: '7', label: 'July' },
    { value: '8', label: 'August' },
    { value: '9', label: 'September' },
    { value: '10', label: 'October' },
    { value: '11', label: 'November' },
    { value: '12', label: 'December' },
  ];

  return (
    <div className={styles.reports}>
      <div className={styles.header}>
        <h1 className={styles.title}>Financial Reports</h1>
        <p className={styles.subtitle}>Generate and download PDF statements</p>
      </div>

      <div className={styles.reportsGrid}>
        {/* Monthly Report */}
        <div className={styles.reportCard}>
          <div className={styles.reportHeader}>
            <div
              className={styles.reportIcon}
              style={{ background: '#dbeafe', color: '#1e40af' }}
            >
              <Calendar size={28} />
            </div>
            <div className={styles.reportInfo}>
              <h2 className={styles.reportTitle}>Monthly Statement</h2>
              <p className={styles.reportDescription}>
                Generate a comprehensive report for any month with all income, expenses, and account balances
              </p>
            </div>
          </div>

          <div className={styles.form}>
            <div className={styles.formRow}>
              <div className={styles.formGroup}>
                <label className={styles.label}>Year</label>
                <select
                  className={styles.select}
                  value={monthlyForm.year}
                  onChange={(e) => setMonthlyForm(prev => ({ ...prev, year: e.target.value }))}
                >
                  {years.map(year => (
                    <option key={year} value={year}>
                      {year}
                    </option>
                  ))}
                </select>
              </div>

              <div className={styles.formGroup}>
                <label className={styles.label}>Month</label>
                <select
                  className={styles.select}
                  value={monthlyForm.month}
                  onChange={(e) => setMonthlyForm(prev => ({ ...prev, month: e.target.value }))}
                >
                  {months.map(month => (
                    <option key={month.value} value={month.value}>
                      {month.label}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <button
              className={styles.generateButton}
              onClick={handleGenerateMonthly}
              disabled={monthlyLoading}
            >
              {monthlyLoading ? (
                <>
                  <Loader size={20} className="spinner" />
                  Generating...
                </>
              ) : (
                <>
                  <FileText size={20} />
                  Generate Monthly Report
                </>
              )}
            </button>
          </div>

          {monthlyReport && (
            <div className={styles.generatedReport}>
              <div className={styles.reportDetails}>
                <div>
                  <div className={styles.reportName}>{monthlyReport.reportName}</div>
                  <div className={styles.reportDate}>
                    Generated: {monthlyReport.generatedAt.toLocaleString()}
                  </div>
                </div>
                <CheckCircle size={24} style={{ color: '#166534' }} />
              </div>
              <a
                href={monthlyReport.downloadUrl}
                target="_blank"
                rel="noopener noreferrer"
                style={{ textDecoration: 'none' }}
              >
                <button className={styles.downloadButton}>
                  <Download size={20} />
                  Download PDF
                </button>
              </a>
              <p className={styles.expiryNote}>Download link expires in 1 hour</p>
            </div>
          )}
        </div>

        {/* Custom Date Range Report */}
        <div className={styles.reportCard}>
          <div className={styles.reportHeader}>
            <div
              className={styles.reportIcon}
              style={{ background: '#dcfce7', color: '#166534' }}
            >
              <CalendarRange size={28} />
            </div>
            <div className={styles.reportInfo}>
              <h2 className={styles.reportTitle}>Custom Report</h2>
              <p className={styles.reportDescription}>
                Generate a report for any date range with detailed transaction breakdowns
              </p>
            </div>
          </div>

          <div className={styles.form}>
            <div className={styles.formGroup}>
              <label className={styles.label}>Start Date</label>
              <input
                type="date"
                className={styles.input}
                value={customForm.startDate}
                onChange={(e) => setCustomForm(prev => ({ ...prev, startDate: e.target.value }))}
              />
            </div>

            <div className={styles.formGroup}>
              <label className={styles.label}>End Date</label>
              <input
                type="date"
                className={styles.input}
                value={customForm.endDate}
                onChange={(e) => setCustomForm(prev => ({ ...prev, endDate: e.target.value }))}
              />
            </div>

            <button
              className={styles.generateButton}
              onClick={handleGenerateCustom}
              disabled={customLoading}
            >
              {customLoading ? (
                <>
                  <Loader size={20} className="spinner" />
                  Generating...
                </>
              ) : (
                <>
                  <FileText size={20} />
                  Generate Custom Report
                </>
              )}
            </button>
          </div>

          {customReport && (
            <div className={styles.generatedReport}>
              <div className={styles.reportDetails}>
                <div>
                  <div className={styles.reportName}>{customReport.reportName}</div>
                  <div className={styles.reportDate}>
                    Generated: {customReport.generatedAt.toLocaleString()}
                  </div>
                </div>
                <CheckCircle size={24} style={{ color: '#166534' }} />
              </div>
              <a
                href={customReport.downloadUrl}
                target="_blank"
                rel="noopener noreferrer"
                style={{ textDecoration: 'none' }}
              >
                <button className={styles.downloadButton}>
                  <Download size={20} />
                  Download PDF
                </button>
              </a>
              <p className={styles.expiryNote}>Download link expires in 1 hour</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Reports;