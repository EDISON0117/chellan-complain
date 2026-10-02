/**
 * StreetConnect - API Client Module
 */
const API_BASE_URL = window.location.origin.includes(':8080')
  ? `${window.location.origin}/api`
  : 'http://localhost:8080/api';

const Api = {
  getToken() {
    return localStorage.getItem('sc_token');
  },

  getHeaders(isMultipart = false) {
    const headers = {};
    const token = this.getToken();
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    if (!isMultipart) {
      headers['Content-Type'] = 'application/json';
    }
    return headers;
  },

  async request(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const isMultipart = options.body instanceof FormData;
    const headers = { ...this.getHeaders(isMultipart), ...(options.headers || {}) };

    try {
      const response = await fetch(url, {
        ...options,
        headers
      });

      const data = await response.json().catch(() => null);

      if (!response.ok) {
        if (response.status === 401) {
          // Token expired or invalid
          console.warn('Unauthorized request. Token might have expired.');
          if (endpoint !== '/auth/login' && endpoint !== '/auth/register') {
            // Optional auto redirect or state clear if not an auth attempt
          }
        }
        const errorMsg = data && data.message ? data.message : `Request failed (${response.status})`;
        throw new Error(errorMsg);
      }

      return data;
    } catch (err) {
      console.error(`API Error [${endpoint}]:`, err);
      throw err;
    }
  },

  get(endpoint, params = {}) {
    const query = new URLSearchParams();
    Object.entries(params).forEach(([key, val]) => {
      if (val !== undefined && val !== null && val !== '') {
        query.append(key, val);
      }
    });
    const queryString = query.toString();
    const fullEndpoint = queryString ? `${endpoint}?${queryString}` : endpoint;
    return this.request(fullEndpoint, { method: 'GET' });
  },

  post(endpoint, data) {
    const isMultipart = data instanceof FormData;
    return this.request(endpoint, {
      method: 'POST',
      body: isMultipart ? data : JSON.stringify(data)
    });
  },

  put(endpoint, data = {}) {
    return this.request(endpoint, {
      method: 'PUT',
      body: JSON.stringify(data)
    });
  },

  delete(endpoint) {
    return this.request(endpoint, { method: 'DELETE' });
  },

  async uploadFile(file) {
    const formData = new FormData();
    formData.append('file', file);
    return this.post('/uploads', formData);
  }
};
