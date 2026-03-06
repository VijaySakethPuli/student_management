import React, { useState } from 'react';
import axios from 'axios';
import { Loader2, CheckCircle2, Save } from 'lucide-react';

export default function OnboardProduct() {
  const [description, setDescription] = useState('');
  const [isProcessing, setIsProcessing] = useState(false);
  const [productData, setProductData] = useState(null);
  const [isSaved, setIsSaved] = useState(false);

  // Form State for Editing
  const [name, setName] = useState('');
  const [stock, setStock] = useState(100);

  const handleAnalyze = async () => {
    if (!description.trim()) return;
    
    setIsProcessing(true);
    setIsSaved(false);
    try {
      const response = await axios.post('http://localhost:8080/api/products/analyze', description, {
        headers: { 'Content-Type': 'text/plain' }
      });
      setProductData(response.data);
      setName(`New ${response.data.category} Item`);
    } catch (error) {
      console.error("Error analyzing product", error);
      alert("Failed to analyze description");
    } finally {
      setIsProcessing(false);
    }
  };

  const handleSave = async () => {
    try {
      const payload = {
        name: name,
        pricePerUnit: productData.inferred_price,
        stockQuantity: stock,
        sustainabilityScore: productData.sustainability_badges.length * 2 // Arbitrary score calc
      };
      
      await axios.post('http://localhost:8080/api/products/save', payload);
      setIsSaved(true);
    } catch (error) {
      console.error("Error saving", error);
      alert("Failed to save product");
    }
  };

  return (
    <div className="page-container">
      <div className="header-section">
        <h1>Product Onboarding</h1>
        <p className="subtitle">AI-assisted categorization and tagging for new sustainable inventory.</p>
      </div>

      <div className="glass-card" style={{ marginBottom: '2rem' }}>
        <div className="form-group">
          <label className="form-label">Raw Product Description</label>
          <textarea 
            className="form-control" 
            placeholder='e.g., "Handmade bag from recycled ocean plastic, very durable..."'
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
        </div>
        
        <button 
          className="btn btn-primary" 
          onClick={handleAnalyze} 
          disabled={isProcessing || !description.trim()}
        >
          {isProcessing ? <Loader2 className="animate-pulse" size={18} style={{ animation: 'spin 1s linear infinite' }} /> : <CheckCircle2 size={18} />}
          {isProcessing ? 'Processing with AI...' : 'Analyze Description'}
        </button>
      </div>

      {productData && (
        <div className="glass-card" style={{ borderLeft: '4px solid var(--accent-primary)' }}>
          <h2>Extracted Details</h2>
          
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem', marginBottom: '2rem' }}>
            <div>
              <div className="form-group">
                <label className="form-label">Category</label>
                <input className="form-control" readOnly value={productData.category} />
              </div>
              <div className="form-group">
                <label className="form-label">Inferred Price ($)</label>
                <input 
                  className="form-control" 
                  type="number" 
                  value={productData.inferred_price} 
                  onChange={(e) => setProductData({...productData, inferred_price: parseFloat(e.target.value)})}
                />
              </div>
            </div>

            <div>
              <div className="form-group">
                <label className="form-label">Sustainability Badges</label>
                <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap', marginTop: '0.5rem' }}>
                  {productData.sustainability_badges.map((badge, idx) => (
                    <span key={idx} className="badge">{badge}</span>
                  ))}
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">SEO Tags</label>
                <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap', marginTop: '0.5rem' }}>
                  {productData.seo_tags.map((tag, idx) => (
                    <span key={idx} className="badge" style={{ backgroundColor: 'rgba(59, 130, 246, 0.15)', color: 'var(--accent-secondary)', borderColor: 'rgba(59, 130, 246, 0.3)' }}>#{tag}</span>
                  ))}
                </div>
              </div>
            </div>
          </div>

          <hr style={{ borderColor: 'var(--border-color)', margin: '2rem 0' }} />
          
          <h3>Finalize Inventory</h3>
          <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '1rem', marginTop: '1rem' }}>
            <div className="form-group">
              <label className="form-label">Product Name</label>
              <input className="form-control" value={name} onChange={e => setName(e.target.value)} />
            </div>
            <div className="form-group">
              <label className="form-label">Initial Stock</label>
              <input type="number" className="form-control" value={stock} onChange={e => setStock(parseInt(e.target.value))} />
            </div>
          </div>

          <div style={{ marginTop: '1rem' }}>
            <button className="btn btn-primary" onClick={handleSave} disabled={isSaved}>
              <Save size={18} />
              {isSaved ? 'Saved to Database!' : 'Confirm & Save Product'}
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
