import { AlertCircle, X } from 'lucide-react';

interface ErrorBannerProps {
  message: string;
  onDismiss?: () => void;
}

const ErrorBanner = ({ message, onDismiss }: ErrorBannerProps) => {
  return (
    <div
      role="alert"
      style={{
        display: 'flex',
        alignItems: 'center',
        gap: '0.75rem',
        background: '#fee2e2',
        border: '1px solid #fca5a5',
        color: '#991b1b',
        padding: '0.75rem 1rem',
        borderRadius: '8px',
        marginBottom: '1rem',
      }}
    >
      <AlertCircle size={20} style={{ flexShrink: 0 }} />
      <span style={{ flex: 1 }}>{message}</span>
      {onDismiss && (
        <button
          type="button"
          onClick={onDismiss}
          aria-label="Dismiss error"
          style={{
            background: 'transparent',
            border: 'none',
            cursor: 'pointer',
            color: '#991b1b',
            padding: '0.25rem',
            display: 'flex',
          }}
        >
          <X size={18} />
        </button>
      )}
    </div>
  );
};

export default ErrorBanner;