import { useState, FormEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Mail, Lock, TrendingUp, ArrowRight, AlertCircle, CheckCircle } from 'lucide-react';
import { useAuth } from '@context/authcontext';
import styles from '@styles/login.module.css';

const Login = () => {
  const navigate = useNavigate();
  const { login } = useAuth();

  const [formData, setFormData] = useState({
    email: '',
    password: '',
    rememberMe: false
  });

  const [errors, setErrors] = useState<{ email?: string; password?: string }>({});
  const [loading, setLoading] = useState(false);
  const [alertMessage, setAlertMessage] = useState<{ type: 'error' | 'success'; message: string } | null>(null);

  const validateForm = (): boolean => {
    const newErrors: { email?: string; password?: string } = {};

    // Email validation
    if (!formData.email) {
      newErrors.email = 'Email is required';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'Please enter a valid email';
    }

    // Password validation
    if (!formData.password) {
      newErrors.password = 'Password is required';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setAlertMessage(null);

    if (!validateForm()) {
      return;
    }

    setLoading(true);

    try {
      const result = await login(formData.email, formData.password);

      if (result.success) {
        setAlertMessage({ type: 'success', message: 'Login successful! Redirecting...' });
        setTimeout(() => {
          navigate('/dashboard');
        }, 1000);
      } else {
        setAlertMessage({ 
          type: 'error', 
          message: result.message || 'Invalid email or password' 
        });
      }
    } catch (error) {
      setAlertMessage({ 
        type: 'error', 
        message: 'An error occurred. Please try again.' 
      });
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (field: string, value: string | boolean) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    // Clear error for this field
    if (errors[field as keyof typeof errors]) {
      setErrors(prev => ({ ...prev, [field]: undefined }));
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <div className={styles.logoContainer}>
          <Link to="/" className={styles.logo}>
            <div className={styles.logoIcon}>
              <TrendingUp size={24} />
            </div>
            <span>Finaltica</span>
          </Link>
        </div>

        <h1 className={styles.heading}>Welcome Back</h1>
        <p className={styles.subheading}>Sign in to your account to continue</p>

        {alertMessage && (
          <div className={`${styles.alert} ${alertMessage.type === 'error' ? styles.alertError : styles.alertSuccess}`}>
            <div className={styles.alertIcon}>
              {alertMessage.type === 'error' ? (
                <AlertCircle size={20} />
              ) : (
                <CheckCircle size={20} />
              )}
            </div>
            <div className={styles.alertContent}>
              <div className={styles.alertTitle}>
                {alertMessage.type === 'error' ? 'Error' : 'Success'}
              </div>
              <div>{alertMessage.message}</div>
            </div>
          </div>
        )}

        <form className={styles.form} onSubmit={handleSubmit}>
          <div className={styles.formGroup}>
            <label className={styles.label}>Email</label>
            <div className={styles.inputWrapper}>
              <Mail className={styles.inputIcon} size={20} />
              <input
                type="email"
                className={`${styles.input} ${errors.email ? styles.error : ''}`}
                placeholder="Enter your email"
                value={formData.email}
                onChange={(e) => handleChange('email', e.target.value)}
              />
            </div>
            {errors.email && (
              <span className={styles.errorMessage}>
                <AlertCircle size={16} />
                {errors.email}
              </span>
            )}
          </div>

          <div className={styles.formGroup}>
            <label className={styles.label}>Password</label>
            <div className={styles.inputWrapper}>
              <Lock className={styles.inputIcon} size={20} />
              <input
                type="password"
                className={`${styles.input} ${errors.password ? styles.error : ''}`}
                placeholder="Enter your password"
                value={formData.password}
                onChange={(e) => handleChange('password', e.target.value)}
              />
            </div>
            {errors.password && (
              <span className={styles.errorMessage}>
                <AlertCircle size={16} />
                {errors.password}
              </span>
            )}
          </div>

          <div className={styles.checkboxGroup}>
            <label className={styles.checkboxLabel}>
              <input
                type="checkbox"
                className={styles.checkbox}
                checked={formData.rememberMe}
                onChange={(e) => handleChange('rememberMe', e.target.checked)}
              />
              Remember me
            </label>
            <a className={styles.forgotLink}>Forgot password?</a>
          </div>

          <button 
            type="submit" 
            className={styles.submitButton}
            disabled={loading}
          >
            {loading ? 'Signing in...' : 'Sign In'}
            {!loading && <ArrowRight size={20} />}
          </button>
        </form>

        <div className={styles.divider}>or</div>

        <div className={styles.signupLink}>
          Don't have an account? <Link to="/signup">Sign up</Link>
        </div>
      </div>
    </div>
  );
};

export default Login;