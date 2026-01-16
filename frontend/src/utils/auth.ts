import { User } from '@typings/index';

const saveToken = (token: string): void => {
  localStorage.setItem('authToken', token);
};

const getToken = (): string | null => {
  return localStorage.getItem('authToken');
};

const removeToken = (): void => {
  localStorage.removeItem('authToken');
};

const isAuthenticated = (): boolean => {
  return !!getToken();
};

const saveUser = (user: User): void => {
  localStorage.setItem('user', JSON.stringify(user));
};

const getUser = (): User | null => {
  const user = localStorage.getItem('user');
  return user ? JSON.parse(user) : null;
};

const logout = (): void => {
  removeToken();
  localStorage.removeItem('user');
  window.location.href = '/login';
};

export {
  saveToken,
  getToken,
  removeToken,
  isAuthenticated,
  saveUser,
  getUser,
  logout
};