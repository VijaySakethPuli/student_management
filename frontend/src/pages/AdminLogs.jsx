import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Database } from 'lucide-react';

export default function AdminLogs() {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchLogs = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/admin/logs');
        setLogs(response.data);
      } catch (error) {
        console.error("Failed to fetch logs", error);
      } finally {
        setLoading(false);
      }
    };
    fetchLogs();
  }, []);

  return (
    <div className="page-container">
      <div className="header-section">
        <h1>Admin Log Viewer</h1>
        <p className="subtitle">Audit trail of AI prompts and responses for compliance and debugging.</p>
      </div>

      <div className="glass-card">
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1.5rem', color: 'var(--text-secondary)' }}>
          <Database size={18} />
          <span>{logs.length} total records found</span>
        </div>

        {loading ? (
          <p>Loading logs...</p>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
            {logs.map((log) => (
              <div key={log.id} style={{ backgroundColor: 'rgba(0,0,0,0.3)', border: '1px solid var(--border-color)', borderRadius: '0.5rem', overflow: 'hidden' }}>
                <div style={{ padding: '1rem', borderBottom: '1px solid var(--border-color)', display: 'flex', justifyContent: 'space-between', backgroundColor: 'rgba(255,255,255,0.02)' }}>
                  <span className="badge" style={{ backgroundColor: 'rgba(245, 158, 11, 0.15)', color: 'var(--warning)', borderColor: 'rgba(245, 158, 11, 0.3)' }}>
                    {log.moduleName}
                  </span>
                  <span style={{ fontSize: '0.875rem', color: 'var(--text-muted)' }}>
                    {new Date(log.timestamp).toLocaleString()}
                  </span>
                </div>
                
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1px', backgroundColor: 'var(--border-color)' }}>
                  <div style={{ padding: '1.5rem', backgroundColor: 'var(--bg-base)' }}>
                    <h4 style={{ color: 'var(--text-secondary)', marginBottom: '1rem', fontSize: '0.875rem', textTransform: 'uppercase' }}>Sent Prompt</h4>
                    <pre style={{ whiteSpace: 'pre-wrap', fontSize: '0.875rem', color: 'var(--text-primary)', fontFamily: 'monospace' }}>
                      {log.prompt}
                    </pre>
                  </div>
                  <div style={{ padding: '1.5rem', backgroundColor: 'var(--bg-base)' }}>
                    <h4 style={{ color: 'var(--text-secondary)', marginBottom: '1rem', fontSize: '0.875rem', textTransform: 'uppercase' }}>Received Response (JSON)</h4>
                    <pre style={{ whiteSpace: 'pre-wrap', fontSize: '0.875rem', color: 'var(--accent-primary)', fontFamily: 'monospace' }}>
                      {log.response}
                    </pre>
                  </div>
                </div>
              </div>
            ))}
            
            {logs.length === 0 && (
              <div style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-muted)' }}>
                No AI interactions logged yet.
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
