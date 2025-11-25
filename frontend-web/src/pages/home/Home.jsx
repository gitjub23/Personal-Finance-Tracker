import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Home.css";

function Home() {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('home');

  const handleNavigation = (tab, path) => {
    setActiveTab(tab);
    if (path) {
      navigate(path);
    }
  };

  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <h1 className="dashboard-main-title">User Dashboard</h1>
        <button className="share-btn">Share</button>
      </header>

      <div className="dashboard-content">
        <h1 className="dashboard-title">Dashboard</h1>
        <p className="dashboard-subtitle">Your financial overview</p>

        <div className="dashboard-cards">
          <div className="dashboard-card income">
            <span>Income</span>
            <h2>$6,200</h2>
          </div>
          <div className="dashboard-card expenses">
            <span>Expenses</span>
            <h2>$551</h2>
          </div>
          <div className="dashboard-card budget">
            <span>Budget</span>
            <h2>$1,600</h2>
          </div>
          <div className="dashboard-card savings">
            <span>Savings</span>
            <h2>$5,649</h2>
          </div>
        </div>

        <div className="dashboard-summary">
          <h3>Expenses by Category</h3>
          <div className="pie-chart-placeholder">[Pie Chart Here]</div>
          <h3>Budget vs Spending</h3>
          <div className="bar-chart-placeholder">[Bar Chart Here]</div>
          <div className="quick-summary">
            <div>Net Income <span>$5649</span></div>
            <div>Budget Used <span>76.4%</span></div>
            <div>Transactions <span>10</span></div>
          </div>
        </div>
      </div>

      {/* Bottom navigation */}
      <nav className="dashboard-nav">
        <button 
          className={`nav-item ${activeTab === 'home' ? 'active' : ''}`}
          onClick={() => handleNavigation('home')}
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
          onClick={() => handleNavigation('budgets', '/budgets')}
        >
          <span className="nav-icon">💼</span>
          <span className="nav-label">Budgets</span>
        </button>
        <button 
          className={`nav-item ${activeTab === 'reports' ? 'active' : ''}`}
          onClick={() => handleNavigation('reports')}
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

export default Home;