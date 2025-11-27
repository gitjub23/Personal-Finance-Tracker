import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Transactions.css';

const TransactionsDashboard = () => {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('transactions');
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(false); // Start as false to not block navigation
  const [error, setError] = useState(null);
  const userId = 1;

  // Fetch transactions from backend - but don't let errors break the page
  useEffect(() => {
    fetchTransactions();
  }, []);

  const fetchTransactions = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await fetch(`http://localhost:8080/api/transactions/user/${userId}`);
      
      if (!response.ok) {
        // If backend returns error, use mock data instead
        console.warn('Backend not available, using mock data');
        setTransactions(getMockTransactions());
        return;
      }
      
      const data = await response.json();
      setTransactions(data);
    } catch (err) {
      // If network error, use mock data
      console.warn('Network error, using mock data:', err.message);
      setTransactions(getMockTransactions());
      setError('Backend connection failed - showing demo data');
    } finally {
      setLoading(false);
    }
  };

  // Mock data for when backend is unavailable
  const getMockTransactions = () => {
    return [
      { id: 1, name: 'Salary', category: 'Income', date: '2024-01-15', amount: 5000.00, type: 'INCOME' },
      { id: 2, name: 'Groceries', category: 'Food', date: '2024-01-14', amount: -150.00, type: 'EXPENSE' },
      { id: 3, name: 'Netflix', category: 'Entertainment', date: '2024-01-13', amount: -15.00, type: 'EXPENSE' },
      { id: 4, name: 'Freelance Work', category: 'Income', date: '2024-01-12', amount: 1200.00, type: 'INCOME' },
    ];
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this transaction?')) {
      try {
        // Try to delete from backend if available
        await fetch(`http://localhost:8080/api/transactions/${id}`, {
          method: 'DELETE',
        });
      } catch (err) {
        console.warn('Backend delete failed, removing from UI only');
      }
      // Always remove from UI
      setTransactions(transactions.filter(t => t.id !== id));
    }
  };

  const handleNavigation = (tab, path) => {
    setActiveTab(tab);
    if (path) {
      navigate(path);
    }
  };

  const getNormalizedType = (type) => {
    return type?.toLowerCase();
  };

  if (loading) {
    return (
      <div className="dashboard">
        <div className="loading-state">Loading transactions...</div>
      </div>
    );
  }

  return (
    <div className="dashboard">
      <header className="dashboard-header">
        <h1 className="dashboard-title">User Dashboard</h1>
        <button className="share-btn">Share</button>
      </header>

      <div className="transactions-container">
        <div className="transactions-header">
          <div>
            <h2 className="transactions-title">Transactions</h2>
            <p className="transactions-subtitle">Manage your finances</p>
            {error && (
              <div style={{ color: 'orange', fontSize: '14px', marginTop: '5px' }}>
                ⚠️ {error}
              </div>
            )}
          </div>
          <button className="add-btn">+ Add</button>
        </div>

        <div className="transactions-list">
          {transactions.length === 0 ? (
            <div style={{ padding: '20px', textAlign: 'center', color: '#666' }}>
              No transactions found.
            </div>
          ) : (
            transactions.map(transaction => {
              const normalizedType = getNormalizedType(transaction.type);
              return (
                <div key={transaction.id} className="transaction-item">
                  <div className="transaction-info">
                    <h3 className="transaction-name">{transaction.name}</h3>
                    <div className="transaction-meta">
                      <span className={`category-badge ${transaction.type} ${normalizedType}`}>
                        {transaction.category}
                      </span>
                      <span className="transaction-date">
                        {transaction.date ? new Date(transaction.date).toLocaleDateString() : 'No date'}
                      </span>
                    </div>
                  </div>
                  <div className="transaction-actions">
                    <span className={`transaction-amount ${transaction.type} ${normalizedType}`}>
                      {normalizedType === 'income' ? '+' : '-'}
                      ${Math.abs(transaction.amount).toFixed(2)}
                    </span>
                    <button className="icon-btn">✏️</button>
                    <button 
                      className="icon-btn" 
                      onClick={() => handleDelete(transaction.id)}
                    >
                      🗑️
                    </button>
                  </div>
                </div>
              );
            })
          )}
        </div>
      </div>

      <nav className="bottom-nav">
        <button 
          className={`nav-item ${activeTab === 'home' ? 'active' : ''}`}
          onClick={() => handleNavigation('home', '/dashboard')}
        >
          <span className="nav-icon">⊞</span>
          <span className="nav-label">Home</span>
        </button>
        <button 
          className={`nav-item ${activeTab === 'transactions' ? 'active' : ''}`}
        >
          <span className="nav-icon">📋</span>
          <span className="nav-label">Transactions</span>
        </button>
        <button 
          className={`nav-item ${activeTab === 'budgets' ? 'active' : ''}`}
          onClick={() => handleNavigation('budgets', '/budgets')}
        >
          <span className="nav-icon">💼</span>
          <span className="nav-label">Budgets</span>
        </button>
        <button 
          className={`nav-item ${activeTab === 'reports' ? 'active' : ''}`}
        >
          <span className="nav-icon">📈</span>
          <span className="nav-label">Reports</span>
        </button>
        <button 
          className={`nav-item ${activeTab === 'settings' ? 'active' : ''}`}
          onClick={() => handleNavigation('settings', '/settings')}
        >
          <span className="nav-icon">⚙️</span>
          <span className="nav-label">Settings</span>
        </button>
      </nav>
    </div>
  );
}
export default TransactionsDashboard;

