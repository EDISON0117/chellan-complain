/**
 * StreetConnect - Authentication & Session Module
 */
const Auth = {
  getUser() {
    try {
      const u = localStorage.getItem('sc_user');
      return u ? JSON.parse(u) : null;
    } catch {
      return null;
    }
  },

  setUser(user) {
    if (user) {
      localStorage.setItem('sc_user', JSON.stringify(user));
    } else {
      localStorage.removeItem('sc_user');
    }
  },

  getToken() {
    return localStorage.getItem('sc_token');
  },

  setToken(token) {
    if (token) {
      localStorage.setItem('sc_token', token);
    } else {
      localStorage.removeItem('sc_token');
    }
  },

  isLoggedIn() {
    return !!this.getToken() && !!this.getUser();
  },

  isAdmin() {
    const user = this.getUser();
    return user && user.role === 'ROLE_ADMIN';
  },

  isModerator() {
    const user = this.getUser();
    return user && (user.role === 'ROLE_MODERATOR' || user.role === 'ROLE_ADMIN');
  },

  isResident() {
    const user = this.getUser();
    return user && user.role === 'ROLE_RESIDENT';
  },

  async login(identifier, password, rememberMe = false) {
    const res = await Api.post('/auth/login', { identifier, password, rememberMe });
    if (res && res.success && res.data) {
      this.setToken(res.data.token);
      this.setUser(res.data.user);
      return res.data;
    }
    throw new Error(res.message || 'Login failed');
  },

  async register(registrationData) {
    const res = await Api.post('/auth/register', registrationData);
    if (res && res.success && res.data) {
      this.setToken(res.data.token);
      this.setUser(res.data.user);
      return res.data;
    }
    throw new Error(res.message || 'Registration failed');
  },

  async refreshCurrentUser() {
    if (!this.getToken()) return null;
    try {
      const res = await Api.get('/auth/me');
      if (res && res.success && res.data) {
        this.setUser(res.data);
        return res.data;
      }
    } catch {
      // Token might be invalid
    }
    return this.getUser();
  },

  logout() {
    this.setToken(null);
    this.setUser(null);
    window.location.href = '/index.html';
  },

  requireAuth() {
    if (!this.isLoggedIn()) {
      window.location.href = `/pages/login.html?redirect=${encodeURIComponent(window.location.pathname + window.location.search)}`;
      return false;
    }
    return true;
  },

  requireRole(role) {
    if (!this.requireAuth()) return false;
    const user = this.getUser();
    if (role === 'ROLE_ADMIN' && !this.isAdmin()) {
      alert('Access restricted to Administrators only.');
      window.location.href = '/index.html';
      return false;
    }
    if (role === 'ROLE_MODERATOR' && !this.isModerator()) {
      alert('Access restricted to Street Moderators only.');
      window.location.href = '/index.html';
      return false;
    }
    return true;
  },

  renderNavbar() {
    const navContainer = document.getElementById('navbar-auth-section');
    if (!navContainer) return;

    const user = this.getUser();

    if (!user) {
      navContainer.innerHTML = `
        <div class="d-flex align-items-center gap-2">
          <button class="btn btn-outline-primary btn-sm px-3 fw-semibold" data-bs-toggle="modal" data-bs-target="#loginModal">
            <i class="bi bi-box-arrow-in-right me-1"></i> Login
          </button>
          <button class="btn btn-primary btn-sm px-3 fw-semibold" data-bs-toggle="modal" data-bs-target="#registerModal">
            <i class="bi bi-person-plus me-1"></i> Register
          </button>
        </div>
      `;
      return;
    }

    // Role badge
    let roleBadge = '';
    if (user.role === 'ROLE_ADMIN') {
      roleBadge = '<span class="badge bg-danger ms-1" style="font-size:0.65rem;">ADMIN</span>';
    } else if (user.role === 'ROLE_MODERATOR') {
      roleBadge = '<span class="badge bg-primary ms-1" style="font-size:0.65rem;">MODERATOR</span>';
    }

    const streetName = user.street ? `${user.street.name}` : 'No Street Joined';
    const avatar = user.profileImage || 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80';

    navContainer.innerHTML = `
      <div class="d-flex align-items-center gap-3">
        <!-- Street Indicator -->
        <span class="street-badge-nav d-none d-md-inline-flex" title="Your Primary Street">
          <i class="bi bi-geo-alt-fill text-danger"></i>
          <span>${streetName}</span>
        </span>

        <!-- Notification Bell Dropdown -->
        <div class="dropdown">
          <button class="btn btn-light position-relative p-2 rounded-circle border" type="button" id="notifDropdownBtn" data-bs-toggle="dropdown" aria-expanded="false" title="Notifications">
            <i class="bi bi-bell-fill text-secondary"></i>
            <span id="nav-notif-count" class="notif-badge-pill d-none">0</span>
          </button>
          <div class="dropdown-menu dropdown-menu-end shadow-lg p-0" style="width: 320px; max-height: 420px; overflow-y: auto;" aria-labelledby="notifDropdownBtn">
            <div class="d-flex align-items-center justify-content-between p-3 border-bottom bg-light">
              <h6 class="mb-0 fw-bold"><i class="bi bi-bell me-1"></i> Notifications</h6>
              <button class="btn btn-sm btn-link text-decoration-none p-0" onclick="App.markAllNotificationsRead()">Mark all read</button>
            </div>
            <div id="nav-notif-list" class="p-2">
              <div class="text-center py-3 text-muted small">Loading notifications...</div>
            </div>
          </div>
        </div>

        <!-- User Menu Dropdown -->
        <div class="dropdown">
          <button class="btn btn-light p-1 d-flex align-items-center gap-2 rounded-pill border ps-2 pe-3" type="button" data-bs-toggle="dropdown" aria-expanded="false">
            <img src="${avatar}" alt="${user.fullName}" class="user-avatar-sm" onerror="this.src='https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80'">
            <span class="fw-semibold text-dark text-truncate d-none d-sm-inline" style="max-width: 120px;">${user.fullName}</span>
            <i class="bi bi-chevron-down text-muted small"></i>
          </button>
          <ul class="dropdown-menu dropdown-menu-end shadow-lg border-0 py-2">
            <li class="px-3 py-2 border-bottom">
              <div class="fw-bold text-dark">${user.fullName} ${roleBadge}</div>
              <div class="small text-muted text-truncate">${user.email}</div>
              <div class="small text-primary mt-1"><i class="bi bi-geo-alt"></i> ${streetName}</div>
            </li>
            <li><a class="dropdown-item py-2" href="/pages/profile.html"><i class="bi bi-person-gear me-2"></i> My Profile</a></li>
            <li><a class="dropdown-item py-2" href="/pages/my-complaints.html"><i class="bi bi-collection me-2"></i> My Complaints</a></li>
            ${this.isModerator() ? '<li><a class="dropdown-item py-2 text-primary fw-semibold" href="/pages/moderator.html"><i class="bi bi-shield-check me-2"></i> Moderator Dashboard</a></li>' : ''}
            ${this.isAdmin() ? '<li><a class="dropdown-item py-2 text-danger fw-semibold" href="/pages/admin.html"><i class="bi bi-speedometer2 me-2"></i> Admin Console</a></li>' : ''}
            <li><hr class="dropdown-divider"></li>
            <li><button class="dropdown-item py-2 text-danger" onclick="Auth.logout()"><i class="bi bi-box-arrow-right me-2"></i> Logout</button></li>
          </ul>
        </div>
      </div>
    `;

    // Load unread count
    App.loadNotificationSummary();
  }
};
