import { useState } from "react";
import "./Auth.css";

export function Auth({ onLogin }) {
  const [activeTab, setActiveTab] = useState("login");

  const handleSubmit = (e) => {
    e.preventDefault();
    onLogin();
  };

  return (
    <div className="auth-container">
      <div className="auth-header">
        <div className="logo-box">
          <span className="logo-symbol">$</span>
        </div>
        <h1>FinanceTracker</h1>
        <p>Manage your money with ease</p>
      </div>

      <div className="auth-card">
        <div className="tabs">
          <button
            onClick={() => setActiveTab("login")}
            className={`tab ${activeTab === "login" ? "active" : ""}`}
          >
            Login
          </button>
          <button
            onClick={() => setActiveTab("signup")}
            className={`tab ${activeTab === "signup" ? "active" : ""}`}
          >
            Sign Up
          </button>
        </div>

        {activeTab === "login" ? (
          <form onSubmit={handleSubmit} className="auth-form">
            <label>Email</label>
            <div className="input-group">
              <span className="icon">📧</span>
              <input type="email" placeholder="you@example.com" required />
            </div>

            <label>Password</label>
            <div className="input-group">
              <span className="icon">🔒</span>
              <input type="password" placeholder="••••••••" required />
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
          <form onSubmit={handleSubmit} className="auth-form">
            <label>Full Name</label>
            <div className="input-group">
              <span className="icon">👤</span>
              <input type="text" placeholder="John Doe" required />
            </div>

            <label>Email</label>
            <div className="input-group">
              <span className="icon">📧</span>
              <input type="email" placeholder="you@example.com" required />
            </div>

            <label>Password</label>
            <div className="input-group">
              <span className="icon">🔒</span>
              <input type="password" placeholder="••••••••" required />
            </div>

            <label>Confirm Password</label>
            <div className="input-group">
              <span className="icon">🔒</span>
              <input type="password" placeholder="••••••••" required />
            </div>

            <div className="checkbox-row">
              <input type="checkbox" required />
              <span>
                I agree to the{" "}
                <a href="#" className="link">
                  Terms of Service
                </a>{" "}
                and{" "}
                <a href="#" className="link">
                  Privacy Policy
                </a>
              </span>
            </div>

            <button type="submit" className="primary-btn">
              Create Account
            </button>

            <div className="divider">
              <div></div>
              <span>Or sign up with</span>
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
        )}
      </div>

      <div className="demo-banner">
        <b>Demo Mode:</b> Use any email/password to try the app
      </div>
    </div>
  );
}
