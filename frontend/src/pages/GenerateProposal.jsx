import React, { useState } from 'react';
import axios from 'axios';
import { Loader2, Calculator, CheckCircle } from 'lucide-react';

export default function GenerateProposal() {
  const [budget, setBudget] = useState(1000);
  const [isGenerating, setIsGenerating] = useState(false);
  const [proposal, setProposal] = useState(null);
  const [error, setError] = useState('');

  const handleGenerate = async () => {
    setIsGenerating(true);
    setError('');
    setProposal(null);
    
    try {
      const response = await axios.post(`http://localhost:8080/api/proposals/generate?budget=${budget}`);
      // The response.data.proposalSummary holds the JSON string we need to parse
      const summaryObj = JSON.parse(response.data.proposalSummary);
      setProposal({
        ...response.data,
        details: summaryObj
      });
    } catch (err) {
      console.error(err);
      setError(err.response?.data || "Math Guard Validation Failed or API Error.");
    } finally {
      setIsGenerating(false);
    }
  };

  return (
    <div className="page-container">
      <div className="header-section">
        <h1>Smart Proposal Builder</h1>
        <p className="subtitle">AI-driven B2B mixes optimized for sustainability and strict budget limits.</p>
      </div>

      <div className="glass-card" style={{ marginBottom: '2rem', display: 'flex', gap: '1rem', alignItems: 'flex-end' }}>
        <div className="form-group" style={{ marginBottom: 0, flex: 1 }}>
          <label className="form-label">Target Budget ($)</label>
          <input 
            type="number" 
            className="form-control" 
            value={budget} 
            onChange={(e) => setBudget(Number(e.target.value))}
            min="100"
          />
        </div>
        
        <button 
          className="btn btn-primary" 
          onClick={handleGenerate} 
          disabled={isGenerating || budget <= 0}
        >
          {isGenerating ? <Loader2 className="animate-pulse" size={18} style={{ animation: 'spin 1s linear infinite' }} /> : <Calculator size={18} />}
          {isGenerating ? 'Calculating Mix...' : 'Generate Proposal'}
        </button>
      </div>

      {error && (
        <div className="glass-card" style={{ borderLeft: '4px solid var(--danger)', color: 'var(--danger)', marginBottom: '2rem' }}>
          <strong>Error: </strong> {error}
        </div>
      )}

      {proposal && (
        <div className="glass-card" style={{ borderTop: '4px solid var(--accent-secondary)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
            <h2>Proposal Summary</h2>
            <div className="badge" style={{ fontSize: '1rem', padding: '0.5rem 1rem' }}>
              <CheckCircle size={16} /> Total: ${proposal.finalCost.toFixed(2)} / ${proposal.totalBudget}
            </div>
          </div>

          <div style={{ backgroundColor: 'rgba(0,0,0,0.2)', padding: '1.5rem', borderRadius: '0.5rem', marginBottom: '2rem' }}>
            <h4 style={{ color: 'var(--text-secondary)', marginBottom: '0.5rem', textTransform: 'uppercase', fontSize: '0.875rem', letterSpacing: '0.05em' }}>
              Impact Statement
            </h4>
            <p style={{ fontSize: '1.1rem', color: 'var(--accent-primary)', fontStyle: 'italic' }}>
              "{proposal.details.impact_summary}"
            </p>
          </div>

          <h3>Itemized Cost Breakdown</h3>
          <table className="data-table">
            <thead>
              <tr>
                <th>Product ID</th>
                <th>Quantity</th>
                <th style={{ textAlign: 'right' }}>Calculated Cost</th>
              </tr>
            </thead>
            <tbody>
              {proposal.details.recommended_mix.map((item, idx) => (
                <tr key={idx}>
                  <td>#{item.product_id}</td>
                  <td>{item.quantity} units</td>
                  <td style={{ textAlign: 'right', fontWeight: '500' }}>${item.cost.toFixed(2)}</td>
                </tr>
              ))}
            </tbody>
            <tfoot>
              <tr>
                <td colSpan="2" style={{ textAlign: 'right', color: 'var(--text-secondary)' }}>Remaining Budget:</td>
                <td style={{ textAlign: 'right', color: 'var(--accent-primary)', fontWeight: 'bold' }}>${proposal.details.remaining_budget.toFixed(2)}</td>
              </tr>
            </tfoot>
          </table>
        </div>
      )}
    </div>
  );
}
