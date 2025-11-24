import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Home.css"; // For dashboard styles

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
        {/* Example placeholder for chart */}
        <div className="pie-chart-placeholder">[Pie Chart Here]</div>
        <h3>Budget vs Spending</h3>
        <div className="bar-chart-placeholder">[Bar Chart Here]</div>
        <div className="quick-summary">
          <div>Net Income <span>$5649</span></div>
          <div>Budget Used <span>76.4%</span></div>
          <div>Transactions <span>10</span></div>
        </div>
      </div>

      {/* Bottom navigation */}
      <nav className="dashboard-nav">
        <button 
          className={activeTab === 'home' ? 'active' : ''}
          onClick={() => handleNavigation('home')}
        >
          Home
        </button>
        <button 
          className={activeTab === 'transactions' ? 'active' : ''}
          onClick={() => handleNavigation('transactions', '/transactions')}
        >
          Transactions
        </button>
        <button 
          className={activeTab === 'budgets' ? 'active' : ''}
          onClick={() => handleNavigation('budgets')}
        >
          Budgets
        </button>
        <button 
          className={activeTab === 'reports' ? 'active' : ''}
          onClick={() => handleNavigation('reports')}
        >
          Reports
        </button>
        <button 
          className={activeTab === 'settings' ? 'active' : ''}
          onClick={() => handleNavigation('settings')}
        >
          Settings
        </button>
      </nav>
    </div>
  );
}

export default Home;