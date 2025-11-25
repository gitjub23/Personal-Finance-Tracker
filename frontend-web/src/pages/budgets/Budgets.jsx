import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Budgets.css';

const Budgets = () => {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('budgets');

  const [budgets, setBudgets] = useState([
    {
      id: 1,
      category: 'Food',
      spent: 350.00,
      limit: 500.00,
      color: '#1a1a1a'
    },
    {
      id: 2,
      category: 'Dining',
      spent: 292.00,
      limit: 300.00,
      color: '#f59e0b'
    },
    {
      id: 3,
      category: 'Transportation',
      spent: 125.00,
      limit: 200.00,
      color: '#3b82f6'
    },
    {
      id: 4,
      category: 'Entertainment',
      spent: 45.00,
      limit: 150.00,
      color: '#8b5cf6'
    },
    {
      id: 5,
      category: 'Utilities',
      spent: 120.00,
      limit: 200.00,
      color: '#ef4444'
    },
    {
      id: 6,
      category: 'Health',
      spent: 50.00,
      limit: 150.00,
      color: '#10b981'
    }
  ]);

  const handleNavigation = (tab, path) => {
    setActiveTab(tab);
    if (path) {
      navigate(path);
    }
  };

  const handleDelete = (id) => {
    if (window.confirm('Are you sure you want to delete this budget?')) {
      setBudgets(budgets.filter(b => b.id !== id));
    }
  };

  const calculatePercentage = (spent, limit) => {
    return ((spent / limit) * 100).toFixed(1);
  };

  const getProgressColor = (percentage) => {
    if (percentage >= 90) return '#ef4444'; // red
    if (percentage >= 70) return '#f59e0b'; // orange/yellow
    return '#1a1a1a'; // black
  };

  const isApproachingLimit = (percentage) => {
    return percentage >= 90;
  };

  return (
    <div className="budgets-dashboard">
      <header className="budgets-header">
        <h1 className="budgets-header-title">User Dashboard</h1>
        <button className="share-btn">Share</button>
      </header>

      <div className="budgets-container">
        <div className="budgets-page-header">
          <div>
            <h2 className="budgets-title">Budgets</h2>
            <p className="budgets-subtitle">Track spending limits</p>
          </div>
          <button className="add-btn">+ Add</button>
        </div>

        <div className="budgets-list">
          {budgets.map(budget => {
            const percentage = calculatePercentage(budget.spent, budget.limit);
            const remaining = budget.limit - budget.spent;
            const progressColor = getProgressColor(percentage);
            const showWarning = isApproachingLimit(percentage);

            return (
              <div key={budget.id} className="budget-item">
                <div className="budget-header">
                  <div className="budget-info">
                    <h3 className="budget-category">{budget.category}</h3>
                    <p className="budget-amounts">
                      ${budget.spent.toFixed(2)} of ${budget.limit.toFixed(2)}
                    </p>
                  </div>
                  <div className="budget-actions">
                    <button className="icon-btn" title="Edit">✏️</button>
                    <button 
                      className="icon-btn" 
                      title="Delete"
                      onClick={() => handleDelete(budget.id)}
                    >
                      🗑️
                    </button>
                  </div>
                </div>

                <div className="progress-bar-container">
                  <div 
                    className="progress-bar-fill"
                    style={{
                      width: `${Math.min(percentage, 100)}%`,
                      backgroundColor: progressColor
                    }}
                  />
                </div>

                <div className="budget-footer">
                  <span className="budget-percentage">{percentage}% used</span>
                  <span className="budget-remaining">${remaining.toFixed(2)} left</span>
                </div>

                {showWarning && (
                  <div className="budget-warning">
                    <span className="warning-icon">⚠</span>
                    <span className="warning-text">Approaching limit</span>
                  </div>
                )}
              </div>
            );
          })}
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
          onClick={() => handleNavigation('transactions', '/transactions')}
        >
          <span className="nav-icon">📋</span>
          <span className="nav-label">Transactions</span>
        </button>
        <button 
          className={`nav-item ${activeTab === 'budgets' ? 'active' : ''}`}
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
        >
          <span className="nav-icon">⚙️</span>
          <span className="nav-label">Settings</span>
        </button>
      </nav>
    </div>
  );
};

export default Budgets;