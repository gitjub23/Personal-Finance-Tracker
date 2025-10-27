import "./Dashboard.css";

export function Dashboard() {
  const data = {
    income: 6200,
    expenses: 551,
    budget: 1600,
    savings: 5649,
    expenseCategories: [
      { name: "Food", value: 27, color: "#3b82f6" },
      { name: "Transportation", value: 9, color: "#ec4899" },
      { name: "Utilities", value: 22, color: "#10b981" },
      { name: "Education", value: 18, color: "#6366f1" },
      { name: "Entertainment", value: 5, color: "#8b5cf6" },
      { name: "Dining", value: 17, color: "#f59e0b" },
      { name: "Health", value: 3, color: "#2563eb" },
    ],
    budgetSpending: [
      { category: "Food", budget: 600, spent: 500 },
      { category: "Dining", budget: 400, spent: 350 },
      { category: "Transport", budget: 300, spent: 220 },
      { category: "Entertainment", budget: 250, spent: 100 },
      { category: "Utilities", budget: 300, spent: 250 },
      { category: "Health", budget: 200, spent: 150 },
    ],
  };

  return (
    <div className="dashboard-container">
      <h1>Dashboard</h1>
      <p className="subtitle">Your financial overview</p>

      {/* Summary Cards */}
      <div className="cards-grid">
        <div className="card">
          <div className="icon income">↑</div>
          <h3>Income</h3>
          <p className="value">${data.income.toLocaleString()}</p>
        </div>

        <div className="card">
          <div className="icon expense">↓</div>
          <h3>Expenses</h3>
          <p className="value">${data.expenses.toLocaleString()}</p>
        </div>

        <div className="card">
          <div className="icon budget">💼</div>
          <h3>Budget</h3>
          <p className="value">${data.budget.toLocaleString()}</p>
        </div>

        <div className="card">
          <div className="icon savings">⭐</div>
          <h3>Savings</h3>
          <p className="value">${data.savings.toLocaleString()}</p>
        </div>
      </div>

      {/* Expense Categories */}
      <div className="chart-section">
        <h3>Expenses by Category</h3>
        <div className="pie-placeholder">[Pie Chart Placeholder]</div>
        <ul className="legend">
          {data.expenseCategories.map((cat) => (
            <li key={cat.name}>
              <span
                className="dot"
                style={{ backgroundColor: cat.color }}
              ></span>
              {cat.name}
            </li>
          ))}
        </ul>
      </div>

      {/* Budget vs Spending */}
      <div className="chart-section">
        <h3>Budget vs Spending</h3>
        <div className="bar-placeholder">[Bar Chart Placeholder]</div>
      </div>

      {/* Quick Summary */}
      <div className="summary">
        <h3>Quick Summary</h3>
        <div className="summary-row">
          <span>Net Income</span>
          <span className="green">${data.savings.toLocaleString()}</span>
        </div>
        <div className="summary-row">
          <span>Budget Used</span>
          <span>76.4%</span>
        </div>
        <div className="summary-row">
          <span>Transactions</span>
          <span>10</span>
        </div>
      </div>
    </div>
  );
}

