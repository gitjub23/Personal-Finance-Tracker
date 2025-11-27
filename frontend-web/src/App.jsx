import React, { useState } from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import './App.css';
import Login from "./pages/login/Login";
import Home from "./pages/home/Home";
import TransactionsDashboard from "./pages/transactions/Transactions";
import Budgets from "./pages/budgets/Budgets";
import Settings from "./pages/settings/Settings";

function App() {
  // Demo-mode authentication state
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // Callback for login or signup success.
  const handleLoginSuccess = () => {
    setIsAuthenticated(true);
  };

  return (
    <BrowserRouter>
      <Routes>
        {/* Landing route: Login page unless authenticated */}
        <Route
          path="/"
          element={
            isAuthenticated
              ? <Navigate to="/dashboard" />
              : <Login onLogin={handleLoginSuccess} />
          }
        />
        {/* Dashboard route: requires authentication */}
        <Route
          path="/dashboard"
          element={
            isAuthenticated
              ? <Home />
              : <Navigate to="/" />
          }
        />
        {/* Transactions route: requires authentication */}
        <Route
          path="/transactions"
          element={
            isAuthenticated
              ? <TransactionsDashboard />
              : <Navigate to="/" />
          }
        />
        {/* Budgets route: requires authentication */}
        <Route
          path="/budgets"
          element={
            isAuthenticated
              ? <Budgets />
              : <Navigate to="/" />
          }
        />
        {/* Settings route: requires authentication */}
        <Route
          path="/settings"
          element={
            isAuthenticated
              ? <Settings />
              : <Navigate to="/" />
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;