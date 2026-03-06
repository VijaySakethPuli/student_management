import React from 'react';
import { BrowserRouter, Routes, Route, Link, useLocation } from 'react-router-dom';
import { Leaf, PackagePlus, FileText, Activity } from 'lucide-react';
import OnboardProduct from './pages/OnboardProduct';
import GenerateProposal from './pages/GenerateProposal';
import AdminLogs from './pages/AdminLogs';

function Layout() {
  const location = useLocation();

  const navItems = [
    { path: '/', icon: <PackagePlus size={20} />, label: 'Onboard Product' },
    { path: '/proposal', icon: <FileText size={20} />, label: 'Smart Proposal' },
    { path: '/admin', icon: <Activity size={20} />, label: 'Admin Logs' },
  ];

  return (
    <div className="app-container">
      <aside className="sidebar">
        <div className="brand">
          <Leaf className="brand-icon" size={28} />
          <span>EcoLogic B2B</span>
        </div>
        
        <nav className="nav-links">
          {navItems.map((item) => (
            <Link 
              key={item.path} 
              to={item.path} 
              className={`nav-item ${location.pathname === item.path ? 'active' : ''}`}
            >
              {item.icon}
              {item.label}
            </Link>
          ))}
        </nav>
      </aside>

      <main className="main-content">
        <Routes>
          <Route path="/" element={<OnboardProduct />} />
          <Route path="/proposal" element={<GenerateProposal />} />
          <Route path="/admin" element={<AdminLogs />} />
        </Routes>
      </main>
    </div>
  );
}

function App() {
  return (
    <BrowserRouter>
      <Layout />
    </BrowserRouter>
  );
}

export default App;
