import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Settings.css';

const Settings = () => {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('settings');

  // User Profile State
  const [fullName, setFullName] = useState('John Doe');
  const [email, setEmail] = useState('john.doe@example.com');
  const [currency, setCurrency] = useState('USD');

  // Notification Settings State
  const [budgetAlerts, setBudgetAlerts] = useState(true);
  const [weeklyReports, setWeeklyReports] = useState(true);
  const [reminders, setReminders] = useState(false);

  const handleNavigation = (tab, path) => {
    setActiveTab(tab);
    if (path) {
      navigate(path);
    }
  };

  const handleSaveProfile = () => {
    // Save profile logic here
    alert('Profile saved successfully!');
  };

  const handleExport = (format) => {
    // Export data logic here
    alert(`Exporting data as ${format}...`);
  };

  const handleChangePassword = () => {
    // Change password logic here
    alert('Change password feature coming soon!');
  };

  const handleEnable2FA = () => {
    // Enable 2FA logic here
    alert('2FA setup coming soon!');
  };

  return (
    <div className="settings-dashboard">
      <header className="settings-header">
        <h1 className="settings-header-title">User Dashboard</h1>
        <button className="share-btn">Share</button>
      </header>

      <div className="settings-container">
        <div className="settings-page-header">
          <div>
            <h2 className="settings-title">Settings</h2>
            <p className="settings-subtitle">Account & preferences</p>
          </div>
        </div>

        <div className="settings-content">
          {/* User Profile Section */}
          <section className="settings-section">
            <div className="section-header">
              <div className="section-icon user-icon">👤</div>
              <h3 className="section-title">User Profile</h3>
            </div>

            <div className="form-group">
              <label className="form-label">Full Name</label>
              <input
                type="text"
                className="form-input"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
              />
            </div>

            <div className="form-group">
              <label className="form-label">Email</label>
              <input
                type="email"
                className="form-input"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>

            <div className="form-group">
              <label className="form-label">Currency</label>
              <select
                className="form-select"
                value={currency}
                onChange={(e) => setCurrency(e.target.value)}
              >
                <option value="USD">USD</option>
                <option value="EUR">EUR</option>
                <option value="GBP">GBP</option>
                <option value="JPY">JPY</option>
                <option value="CAD">CAD</option>
              </select>
            </div>

            <button className="save-btn" onClick={handleSaveProfile}>
              Save
            </button>
          </section>

          {/* Notifications Section */}
          <section className="settings-section">
            <div className="section-header">
              <div className="section-icon notification-icon">🔔</div>
              <h3 className="section-title">Notifications</h3>
            </div>

            <div className="toggle-item">
              <div className="toggle-info">
                <h4 className="toggle-title">Budget Alerts</h4>
                <p className="toggle-description">Notify near limits</p>
              </div>
              <label className="toggle-switch">
                <input
                  type="checkbox"
                  checked={budgetAlerts}
                  onChange={(e) => setBudgetAlerts(e.target.checked)}
                />
                <span className="toggle-slider"></span>
              </label>
            </div>

            <div className="toggle-item">
              <div className="toggle-info">
                <h4 className="toggle-title">Weekly Reports</h4>
                <p className="toggle-description">Weekly summary</p>
              </div>
              <label className="toggle-switch">
                <input
                  type="checkbox"
                  checked={weeklyReports}
                  onChange={(e) => setWeeklyReports(e.target.checked)}
                />
                <span className="toggle-slider"></span>
              </label>
            </div>

            <div className="toggle-item">
              <div className="toggle-info">
                <h4 className="toggle-title">Reminders</h4>
                <p className="toggle-description">Daily reminders</p>
              </div>
              <label className="toggle-switch">
                <input
                  type="checkbox"
                  checked={reminders}
                  onChange={(e) => setReminders(e.target.checked)}
                />
                <span className="toggle-slider"></span>
              </label>
            </div>
          </section>

          {/* Export Data Section */}
          <section className="settings-section">
            <div className="section-header">
              <div className="section-icon export-icon">📥</div>
              <h3 className="section-title">Export Data</h3>
            </div>

            <div className="export-buttons">
              <button className="export-btn" onClick={() => handleExport('CSV')}>
                CSV
              </button>
              <button className="export-btn" onClick={() => handleExport('PDF')}>
                PDF
              </button>
              <button className="export-btn" onClick={() => handleExport('JSON')}>
                JSON
              </button>
            </div>
          </section>

          {/* Security Section */}
          <section className="settings-section">
            <div className="section-header">
              <div className="section-icon security-icon">🔒</div>
              <h3 className="section-title">Security</h3>
            </div>

            <button className="security-btn" onClick={handleChangePassword}>
              Change Password
            </button>

            <button className="security-btn" onClick={handleEnable2FA}>
              2FA
            </button>
          </section>
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
        >
          <span className="nav-icon">⚙️</span>
          <span className="nav-label">Settings</span>
        </button>
      </nav>
    </div>
  );
};

export default Settings;