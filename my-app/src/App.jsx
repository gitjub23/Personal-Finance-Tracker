import { useState } from "react";
import "./App.css";

export default function App() {
  const [activeTab, setActiveTab] = useState("login");

  return (
    <div className="app-container">
      <div className="header">
        <div className="logo-box">
          <span className="logo-symbol">$</span>
        </div>
        <h1>FinanceTracker</h1>
        <p>Manage your money with ease</p>
      </div>

      <div className="card">
        <div className="tabs">
          <button
            onClick={() => setActiveTab("login")}
            className={activeTab === "login" ? "tab active" : "tab"}
          >
            Login
          </button>
          <button
            onClick={() => setActiveTab("signup")}
            className={activeTab === "signup" ? "tab active" : "tab"}
          >
            Sign Up
          </button>
        </div>

        {activeTab === "login" ? (
          <form className="form">
            <div>
              <label>Email</label>
              <div className="input-group">
                <span className="icon">@</span>
                <input type="email" placeholder="you@example.com" />
              </div>
            </div>

            <div>
              <label>Password</label>
              <div className="input-group">
                <span className="icon">🔒</span>
                <input type="password" placeholder="••••••••" />
              </div>
            </div>

            <div className="actions">
              <label>
                <input type="checkbox" /> Remember me
              </label>
              <a href="#">Forgot password?</a>
            </div>

            <button type="submit" className="primary-btn">
              Sign In
            </button>

            <div className="divider">
              <div></div>
              <span>Or continue with</span>
              <div></div>
            </div>

            <div className="oauth">
              <button type="button" className="oauth-btn">
                <img
                  src="https://www.svgrepo.com/show/475656/google-color.svg"
                  alt="Google"
                />
                Google
              </button>
              <button type="button" className="oauth-btn">
                <img
                  src="https://www.svgrepo.com/show/303128/apple-logo.svg"
                  alt="Apple"
                />
                Apple
              </button>
            </div>
          </form>
        ) : (
          <div className="signup-placeholder">
            Sign Up form placeholder
          </div>
        )}
      </div>

      <div className="demo-banner">
        <b>Demo Mode:</b> Use any email/password to try the app
      </div>
    </div>
  );
}
