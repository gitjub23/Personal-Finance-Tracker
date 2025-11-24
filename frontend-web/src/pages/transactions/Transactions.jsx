import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Transactions.css';

const TransactionsDashboard = () => {
  const [transactions, setTransactions] = useState([
    { id: 1, name: 'Salary', category: 'Income', date: 'Oct 10', amount: 5000.00, type: 'income' },
    { id: 2, name: 'Grocery Shopping', category: 'Food', date: 'Oct 9', amount: -150.00, type: 'expense' },
    { id: 3, name: 'Netflix Subscription', category: 'Entertainment', date: 'Oct 8', amount: -15.00, type: 'expense' },
    { id: 4, name: 'Uber Ride', category: 'Transportation', date: 'Oct 7', amount: -25.00, type: 'expense' },
    { id: 5, name: 'Restaurant Dinner', category: 'Dining', date: 'Oct 6', amount: -80.00, type: 'expense' },
    { id: 6, name: 'Freelance Project', category: 'Income', date: 'Oct 5', amount: 1200.00, type: 'income' },
    { id: 7, name: 'Electricity Bill', category: 'Utilities', date: 'Oct 4', amount: -120.00, type: 'expense' },
    { id: 8, name: 'Gym Membership', category: 'Health', date: 'Oct 3', amount: -50.00, type: 'expense' },
    { id: 9, name: 'Coffee Shop', category: 'Dining', date: 'Oct 2', amount: -12.00, type: 'expense' },
    { id: 10, name: 'Online Course', category: 'Education', date: 'Oct 1', amount: -99.00, type: 'expense' },
  ]);

  const [activeTab, setActiveTab] = useState('transactions');
  const [searchTerm, setSearchTerm] = useState('');
  const [filterCategory, setFilterCategory] = useState('All');

  const handleDelete = (id) => {
    setTransactions(transactions.filter(t => t.id !== id));
  };

  const filteredTransactions = transactions.filter(transaction => {
    const matchesSearch = transaction.name.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesCategory = filterCategory === 'All' || transaction.category === filterCategory;
    return matchesSearch && matchesCategory;
  });

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
          </div>
          <button className="add-btn">+ Add</button>
        </div>

        <div className="search-filter-bar">
          <div className="search-box">
            <span className="search-icon">🔍</span>
            <input 
              type="text" 
              placeholder="Search..." 
              className="search-input"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
          <select 
            className="filter-dropdown"
            value={filterCategory}
            onChange={(e) => setFilterCategory(e.target.value)}
          >
            <option>All</option>
            <option>Income</option>
            <option>Food</option>
            <option>Entertainment</option>
            <option>Transportation</option>
            <option>Dining</option>
            <option>Utilities</option>
            <option>Health</option>
            <option>Education</option>
          </select>
        </div>

        <div className="transactions-list">
          {filteredTransactions.map(transaction => (
            <div key={transaction.id} className="transaction-item">
              <div className="transaction-info">
                <h3 className="transaction-name">{transaction.name}</h3>
                <div className="transaction-meta">
                  <span className={`category-badge ${transaction.type}`}>
                    {transaction.category}
                  </span>
                  <span className="transaction-date">{transaction.date}</span>
                </div>
              </div>
              <div className="transaction-actions">
                <span className={`transaction-amount ${transaction.type}`}>
                  {transaction.amount > 0 ? '+' : ''}${Math.abs(transaction.amount).toFixed(2)}
                </span>
                <button className="icon-btn">✏️</button>
                <button className="icon-btn" onClick={() => handleDelete(transaction.id)}>🗑️</button>
              </div>
            </div>
          ))}
        </div>
      </div>

      <nav className="bottom-nav">
        <button className={`nav-item ${activeTab === 'home' ? 'active' : ''}`} onClick={() => setActiveTab('home')}>
          <span className="nav-icon">⊞</span>
          <span className="nav-label">Home</span>
        </button>
        <button className={`nav-item ${activeTab === 'transactions' ? 'active' : ''}`} onClick={() => setActiveTab('transactions')}>
          <span className="nav-icon">📋</span>
          <span className="nav-label">Transactions</span>
        </button>
        <button className={`nav-item ${activeTab === 'budgets' ? 'active' : ''}`} onClick={() => setActiveTab('budgets')}>
          <span className="nav-icon">💼</span>
          <span className="nav-label">Budgets</span>
        </button>
        <button className={`nav-item ${activeTab === 'reports' ? 'active' : ''}`} onClick={() => setActiveTab('reports')}>
          <span className="nav-icon">📈</span>
          <span className="nav-label">Reports</span>
        </button>
        <button className={`nav-item ${activeTab === 'settings' ? 'active' : ''}`} onClick={() => setActiveTab('settings')}>
          <span className="nav-icon">⚙️</span>
          <span className="nav-label">Settings</span>
        </button>
      </nav>
    </div>
  );
};

export default TransactionsDashboard;