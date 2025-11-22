import React, { useState } from "react";
import "./Login.css";
import { FcGoogle } from "react-icons/fc";
import { FaApple } from "react-icons/fa";

function Login({ onLogin }) {
  const [tab, setTab] = useState("login");
  const [loginData, setLoginData] = useState({ email: "", password: "", remember: false });
  const [signupData, setSignupData] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
    agree: false,
  });

  const handleLoginChange = (e) => {
    const { name, value, type, checked } = e.target;
    setLoginData((prev) => ({
      ...prev, [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleSignupChange = (e) => {
    const { name, value, type, checked } = e.target;
    setSignupData((prev) => ({
      ...prev, [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleLogin = (e) => {
    e.preventDefault();
    alert("Demo Mode: Login successful!");
    if (onLogin) onLogin();
  };

  const handleSignup = (e) => {
    e.preventDefault();
    alert("Demo Mode: Account created!");
    if (onLogin) onLogin();
  };

  return (
    <div className="auth-container">
      <div className="auth-logo">
        <svg width="42" height="42" viewBox="0 0 24 24" fill="none">
          <rect width="24" height="24" rx="12" fill="url(#dollarGradient)" />
          <path d="M12 3v18M7 8.5C7 6.75 8.67 5.5 12 5.5C15.33 5.5 17 6.75 17 8.5C17 10.25 15.33 11.5 12 11.5C8.67 11.5 7 12.75 7 14.5C7 16.25 8.67 17.5 12 17.5C15.33 17.5 17 16.25 17 14.5"
            stroke="#fff" strokeWidth="1.8" strokeLinecap="round" />
          <defs>
            <linearGradient id="dollarGradient" x1="0" y1="0" x2="24" y2="24" gradientUnits="userSpaceOnUse">
              <stop stopColor="#3b82f6" />
              <stop offset="1" stopColor="#2563eb" />
            </linearGradient>
          </defs>
        </svg>
      </div>
      <h2 className="auth-title">FinanceTracker</h2>
      <p className="auth-subtitle">Manage your money with ease</p>
      <div className="auth-card">
        <div className="auth-tabs">
          <button className={tab === "login" ? "active" : ""} onClick={() => setTab("login")}>Login</button>
          <button className={tab === "signup" ? "active" : ""} onClick={() => setTab("signup")}>Sign Up</button>
        </div>
        {tab === "login" && (
          <form onSubmit={handleLogin}>
            <div>
              <label>Email</label>
              <input
                name="email"
                type="email"
                value={loginData.email}
                onChange={handleLoginChange}
                placeholder="you@example.com"
                required
              />
            </div>
            <div>
              <label>Password</label>
              <input
                name="password"
                type="password"
                value={loginData.password}
                onChange={handleLoginChange}
                placeholder="••••••••"
                required
              />
            </div>
            <div className="auth-row">
              <label>
                <input
                  name="remember"
                  type="checkbox"
                  checked={loginData.remember}
                  onChange={handleLoginChange}
                />
                Remember me
              </label>
              <a href="#">Forgot password?</a>
            </div>
            <button type="submit" className="auth-btn-primary">Sign In</button>
          </form>
        )}
        {tab === "signup" && (
          <form onSubmit={handleSignup}>
            <div>
              <label>Full Name</label>
              <input
                name="name"
                type="text"
                value={signupData.name}
                onChange={handleSignupChange}
                placeholder="John Doe"
                required
              />
            </div>
            <div>
              <label>Email</label>
              <input
                name="email"
                type="email"
                value={signupData.email}
                onChange={handleSignupChange}
                placeholder="you@example.com"
                required
              />
            </div>
            <div>
              <label>Password</label>
              <input
                name="password"
                type="password"
                value={signupData.password}
                onChange={handleSignupChange}
                required
              />
            </div>
            <div>
              <label>Confirm Password</label>
              <input
                name="confirmPassword"
                type="password"
                value={signupData.confirmPassword}
                onChange={handleSignupChange}
                required
              />
            </div>
            <div className="auth-row">
              <label>
                <input
                  name="agree"
                  type="checkbox"
                  checked={signupData.agree}
                  onChange={handleSignupChange}
                  required
                />
                I agree tto the <a href="#">Terms of Service</a> and <a href="#">Privacy Policy</a>
              </label>
            </div>
            <button type="submit" className="auth-btn-primary">Create Account</button>
          </form>
        )}
        <div className="auth-divider">Or sign up with</div>
        <div className="auth-social-buttons">
          <button type="button" className="auth-social-button">
            <FcGoogle size={20} style={{ marginRight: 8 }} /> Google
          </button>
          <button type="button" className="auth-social-button">
            <FaApple size={20} style={{ marginRight: 8 }} /> Apple
          </button>
        </div>
      </div>
      <div className="auth-demo-note">
        <b>Demo Mode:</b> Use any email/password to try the app
      </div>
    </div>
  );
}

export default Login;
