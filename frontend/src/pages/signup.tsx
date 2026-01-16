import { useState, FormEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { 
  User, 
  Mail, 
  Lock, 
  TrendingUp, 
  ArrowRight, 
  AlertCircle, 
  CheckCircle, 
  Eye, 
  EyeOff 
} from 'lucide-react';
import { useAuth } from '@context/authcontext';
import styles from '@styles/signup.module.css';

interface FormErrors {
  firstName?: string;
  lastName?: string;
  email?: string;
  password?: string;
  confirmPassword?: string;
  terms?: string;
}

const Signup = () => {
  const navigate = useNavigate();
  const { signup } = useAuth();

  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmPassword: '',
    termsAccepted: false
  });

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [errors, setErrors] = useState<FormErrors>({});
  const [loading, setLoading] = useState(false);
  const [alertMessage, setAlertMessage] = useState<{ type: 'error' | 'success'; message: string } | null>(null);

  const getPasswordStrength = (password: string): { strength: 'weak' | 'medium' | 'strong'; label: string } => {
    if (password.length === 0) return { strength: 'weak', label: '' };
    
    let score = 0;
    if (password.length >= 8) score++;
    if (/[a-z]/.test(password) && /[A-Z]/.test(password)) score++;
    if (/\d/.test(password)) score++;
    if (/[@$!%*?&#]/.test(password)) score++;

    if (score <= 2) return { strength: 'weak', label: 'Weak' };
    if (score === 3) return { strength: 'medium', label: 'Medium' };
    return { strength: 'strong', label: 'Strong' };
  };

  const validateForm = (): boolean => {
    const newErrors: FormErrors = {};

    // First name
    if (!formData.firstName) {
      newErrors.firstName = 'First name is required';
    } else if (formData.firstName.length < 2) {
      newErrors.firstName = 'First name must be at least 2 characters';
    }

    // Last name
    if (!formData.lastName) {
      newErrors.lastName = 'Last name is required';
    } else if (formData.lastName.length < 2) {
      newErrors.lastName = 'Last name must be at least 2 characters';
    }

    // Email
    if (!formData.email) {
      newErrors.email = 'Email is required';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'Please enter a valid email';
    }

    // Password
    if (!formData.password) {
      newErrors.password = 'Password is required';
    } else if (!/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#])[A-Za-z\d@$!%*?&#]{8,}$/.test(formData.password)) {
      newErrors.password = 'Password must be at least 8 characters with uppercase, lowercase, number, and special character';
    }

    // Confirm password
    if (!formData.confirmPassword) {
      newErrors.confirmPassword = 'Please confirm your password';
    } else if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = 'Passwords do not match';
    }

    // Terms
    if (!formData.termsAccepted) {
      newErrors.terms = 'You must accept the terms and privacy policy';
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
      const result = await signup({
        firstName: formData.firstName,
        lastName: formData.lastName,
        email: formData.email,
        password: formData.password,
        confirmPassword: formData.confirmPassword
      });

      if (result.success) {
        setAlertMessage({ 
          type: 'success', 
          message: 'Account created successfully! Redirecting to login...' 
        });
        setTimeout(() => {
          navigate('/login');
        }, 2000);
      } else {
        setAlertMessage({ 
          type: 'error', 
          message: result.message || 'Signup failed. Please try again.' 
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
    if (errors[field as keyof FormErrors]) {
      setErrors(prev => ({ ...prev, [field]: undefined }));
    }
  };

  const passwordStrength = getPasswordStrength(formData.password);

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

        <h1 className={styles.heading}>Create Your Account</h1>
        <p className={styles.subheading}>Start your financial journey today</p>

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
          <div className={styles.formRow}>
            <div className={styles.formGroup}>
              <label className={styles.label}>First Name</label>
              <div className={styles.inputWrapper}>
                <User className={styles.inputIcon} size={20} />
                <input
                  type="text"
                  className={`${styles.input} ${errors.firstName ? styles.error : ''}`}
                  placeholder="John"
                  value={formData.firstName}
                  onChange={(e) => handleChange('firstName', e.target.value)}
                />
              </div>
              {errors.firstName && (
                <span className={styles.errorMessage}>
                  <AlertCircle size={16} />
                  {errors.firstName}
                </span>
              )}
            </div>

            <div className={styles.formGroup}>
              <label className={styles.label}>Last Name</label>
              <div className={styles.inputWrapper}>
                <User className={styles.inputIcon} size={20} />
                <input
                  type="text"
                  className={`${styles.input} ${errors.lastName ? styles.error : ''}`}
                  placeholder="Doe"
                  value={formData.lastName}
                  onChange={(e) => handleChange('lastName', e.target.value)}
                />
              </div>
              {errors.lastName && (
                <span className={styles.errorMessage}>
                  <AlertCircle size={16} />
                  {errors.lastName}
                </span>
              )}
            </div>
          </div>

          <div className={styles.formGroup}>
            <label className={styles.label}>Email</label>
            <div className={styles.inputWrapper}>
              <Mail className={styles.inputIcon} size={20} />
              <input
                type="email"
                className={`${styles.input} ${errors.email ? styles.error : ''}`}
                placeholder="john.doe@example.com"
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
                type={showPassword ? 'text' : 'password'}
                className={`${styles.input} ${styles.inputWithToggle} ${errors.password ? styles.error : ''}`}
                placeholder="Enter your password"
                value={formData.password}
                onChange={(e) => handleChange('password', e.target.value)}
              />
              <button
                type="button"
                className={styles.togglePassword}
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
              </button>
            </div>
            {errors.password && (
              <span className={styles.errorMessage}>
                <AlertCircle size={16} />
                {errors.password}
              </span>
            )}
            {formData.password && (
              <div className={styles.passwordStrength}>
                <div className={styles.strengthLabel}>
                  <span>Password strength</span>
                  <span>{passwordStrength.label}</span>
                </div>
                <div className={styles.strengthBar}>
                  <div className={`${styles.strengthFill} ${styles[`strength${passwordStrength.strength.charAt(0).toUpperCase() + passwordStrength.strength.slice(1)}`]}`} />
                </div>
              </div>
            )}
          </div>

          <div className={styles.formGroup}>
            <label className={styles.label}>Confirm Password</label>
            <div className={styles.inputWrapper}>
              <Lock className={styles.inputIcon} size={20} />
              <input
                type={showConfirmPassword ? 'text' : 'password'}
                className={`${styles.input} ${styles.inputWithToggle} ${errors.confirmPassword ? styles.error : formData.confirmPassword && formData.password === formData.confirmPassword ? styles.success : ''}`}
                placeholder="Confirm your password"
                value={formData.confirmPassword}
                onChange={(e) => handleChange('confirmPassword', e.target.value)}
              />
              <button
                type="button"
                className={styles.togglePassword}
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
              >
                {showConfirmPassword ? <EyeOff size={20} /> : <Eye size={20} />}
              </button>
            </div>
            {errors.confirmPassword && (
              <span className={styles.errorMessage}>
                <AlertCircle size={16} />
                {errors.confirmPassword}
              </span>
            )}
            {formData.confirmPassword && formData.password === formData.confirmPassword && (
              <span className={styles.successMessage}>
                <CheckCircle size={16} />
                Passwords match
              </span>
            )}
          </div>

          <div className={styles.checkboxGroup}>
            <input
              type="checkbox"
              className={styles.checkbox}
              checked={formData.termsAccepted}
              onChange={(e) => handleChange('termsAccepted', e.target.checked)}
            />
            <label className={styles.checkboxLabel}>
              I agree to the <a>Terms of Service</a> and <a>Privacy Policy</a>
            </label>
          </div>
          {errors.terms && (
            <span className={styles.errorMessage}>
              <AlertCircle size={16} />
              {errors.terms}
            </span>
          )}

          <button 
            type="submit" 
            className={styles.submitButton}
            disabled={loading}
          >
            {loading ? 'Creating Account...' : 'Create Account'}
            {!loading && <ArrowRight size={20} />}
          </button>
        </form>

        <div className={styles.divider}>or</div>

        <div className={styles.loginLink}>
          Already have an account? <Link to="/login">Sign in</Link>
        </div>
      </div>
    </div>
  );
};

export default Signup;