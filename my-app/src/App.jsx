import { useState } from "react";
import { Auth } from "./Auth";
import { Dashboard } from "./Dashboard"; // import the dashboard screen
import "./App.css";

export default function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  const handleLogin = () => {
    setIsAuthenticated(true);
  };

  // show login/signup until user authenticates
  if (!isAuthenticated) {
    return <Auth onLogin={handleLogin} />;
  }

  // once authenticated, show dashboard
  return (
    <div className="app-container">
      <Dashboard />
    </div>
  );
}
