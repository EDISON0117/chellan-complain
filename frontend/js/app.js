/**
 * StreetConnect - Core Application & UI Helpers Module
 */
const App = {
  init() {
    Auth.renderNavbar();
    this.setupQuickAuthModal();
  },

  showToast(message, type = 'success') {
    let container = document.getElementById('toast-container');
    if (!container) {
      container = document.createElement('div');
      container.id = 'toast-container';
      container.className = 'toast-container-custom';
      document.body.appendChild(container);
    }

    const toastId = 'toast-' + Date.now();
    const bgClass = type === 'success' ? 'bg-success text-white'
      : type === 'danger' || type === 'error' ? 'bg-danger text-white'
      : type === 'warning' ? 'bg-warning text-dark'
      : 'bg-primary text-white';

    const icon = type === 'success' ? 'bi-check-circle-fill'
      : type === 'danger' || type === 'error' ? 'bi-exclamation-octagon-fill'
      : type === 'warning' ? 'bi-exclamation-triangle-fill'
      : 'bi-info-circle-fill';

    const html = `
      <div id="${toastId}" class="toast align-items-center ${bgClass} border-0 shadow-lg mb-2" role="alert" aria-live="assertive" aria-atomic="true">
        <div class="d-flex">
          <div class="toast-body d-flex align-items-center gap-2">
            <i class="bi ${icon} fs-5"></i>
            <div>${message}</div>
          </div>
          <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
      </div>
    `;

    container.insertAdjacentHTML('beforeend', html);
    const toastEl = document.getElementById(toastId);
    const bsToast = new bootstrap.Toast(toastEl, { delay: 4000 });
    bsToast.show();
    toastEl.addEventListener('hidden.bs.toast', () => toastEl.remove());
  },

  formatDate(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-IN', {
      day: 'numeric',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  },

  timeAgo(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const now = new Date();
    const seconds = Math.floor((now - date) / 1000);

    if (seconds < 60) return 'Just now';
    const minutes = Math.floor(seconds / 60);
    if (minutes < 60) return `${minutes} min ago`;
    const hours = Math.floor(minutes / 60);
    if (hours < 24) return `${hours} hr${hours > 1 ? 's' : ''} ago`;
    const days = Math.floor(hours / 24);
    if (days < 30) return `${days} day${days > 1 ? 's' : ''} ago`;
    const months = Math.floor(days / 30);
    if (months < 12) return `${months} month${months > 1 ? 's' : ''} ago`;
    return `${Math.floor(months / 12)} year(s) ago`;
  },

  getStatusBadge(status) {
    const s = (status || 'PENDING').toLowerCase();
    const map = {
      'pending': { label: 'Pending', icon: 'bi-hourglass-split', class: 'status-pending' },
      'under_review': { label: 'Under Review', icon: 'bi-search', class: 'status-under_review' },
      'in_progress': { label: 'In Progress', icon: 'bi-gear-wide-connected', class: 'status-in_progress' },
      'resolved': { label: 'Resolved', icon: 'bi-check-circle-fill', class: 'status-resolved' },
      'rejected': { label: 'Rejected', icon: 'bi-x-circle-fill', class: 'status-rejected' }
    };
    const item = map[s] || map['pending'];
    return `<span class="badge-status ${item.class}"><i class="bi ${item.icon}"></i> ${item.label}</span>`;
  },

  getPriorityBadge(priority) {
    const p = (priority || 'MEDIUM').toUpperCase();
    const map = {
      'CRITICAL': { label: 'Critical', class: 'priority-critical' },
      'HIGH': { label: 'High', class: 'priority-high' },
      'MEDIUM': { label: 'Medium', class: 'priority-medium' },
      'LOW': { label: 'Low', class: 'priority-low' }
    };
    const item = map[p] || map['MEDIUM'];
    return `<span class="badge-priority ${item.class}">${item.label}</span>`;
  },

  async loadNotificationSummary() {
    if (!Auth.isLoggedIn()) return;
    try {
      const countRes = await Api.get('/notifications/unread-count');
      const badge = document.getElementById('nav-notif-count');
      if (badge && countRes.data) {
        const count = countRes.data.count || 0;
        if (count > 0) {
          badge.textContent = count > 9 ? '9+' : count;
          badge.classList.remove('d-none');
        } else {
          badge.classList.add('d-none');
        }
      }

      // Also listen on click to populate dropdown
      const btn = document.getElementById('notifDropdownBtn');
      if (btn) {
        btn.addEventListener('show.bs.dropdown', () => this.fetchNotificationsList());
      }
    } catch (err) {
      console.error('Failed to load notifications count', err);
    }
  },

  async fetchNotificationsList() {
    const listEl = document.getElementById('nav-notif-list');
    if (!listEl) return;
    try {
      const res = await Api.get('/notifications');
      if (!res.data || res.data.length === 0) {
        listEl.innerHTML = `<div class="text-center py-4 text-muted small"><i class="bi bi-bell-slash fs-4 d-block mb-1"></i>No notifications yet</div>`;
        return;
      }
      listEl.innerHTML = res.data.slice(0, 7).map(n => `
        <div class="notif-item ${!n.isRead ? 'unread' : ''}" onclick="App.handleNotifClick(${n.id}, '${n.link || '#'}')">
          <div class="d-flex justify-content-between align-items-start mb-1">
            <strong class="small text-dark">${n.title}</strong>
            <span class="text-muted" style="font-size: 0.7rem;">${this.timeAgo(n.createdAt)}</span>
          </div>
          <p class="small text-muted mb-0 text-truncate">${n.message}</p>
        </div>
      `).join('');
    } catch {
      listEl.innerHTML = `<div class="text-danger small p-2">Failed to load notifications</div>`;
    }
  },

  async handleNotifClick(notifId, link) {
    try {
      await Api.put(`/notifications/${notifId}/read`);
      this.loadNotificationSummary();
    } catch {}
    if (link && link !== '#') {
      window.location.href = link;
    }
  },

  async markAllNotificationsRead() {
    try {
      await Api.put('/notifications/read-all');
      this.loadNotificationSummary();
      this.fetchNotificationsList();
      this.showToast('All notifications marked as read', 'info');
    } catch (err) {
      this.showToast('Could not mark all as read', 'error');
    }
  },

  setupQuickAuthModal() {
    // If modals aren't injected into the DOM yet, inject them
    if (document.getElementById('loginModal')) return;

    const modalHtml = `
      <!-- Login Modal -->
      <div class="modal fade" id="loginModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content border-0 shadow-lg rounded-4">
            <div class="modal-header border-0 pb-0">
              <div>
                <h4 class="modal-title fw-bold text-dark">Welcome Back</h4>
                <p class="text-muted small mb-0">Sign in to your StreetConnect community</p>
              </div>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body p-4">
              <form id="modalLoginForm" onsubmit="App.handleModalLogin(event)">
                <div class="mb-3">
                  <label class="form-label fw-semibold small">Email or Mobile Number</label>
                  <div class="input-group">
                    <span class="input-group-text bg-light"><i class="bi bi-person"></i></span>
                    <input type="text" id="modalLoginIdentifier" class="form-control" placeholder="e.g. arun.prakash@gmail.com or 9840445566" required>
                  </div>
                </div>
                <div class="mb-3">
                  <div class="d-flex justify-content-between">
                    <label class="form-label fw-semibold small">Password</label>
                    <a href="#" class="small text-decoration-none" onclick="App.handleForgotPasswordPrompt()">Forgot password?</a>
                  </div>
                  <div class="input-group">
                    <span class="input-group-text bg-light"><i class="bi bi-lock"></i></span>
                    <input type="password" id="modalLoginPassword" class="form-control" placeholder="••••••••" required>
                  </div>
                </div>
                <div class="mb-3 form-check">
                  <input type="checkbox" class="form-check-input" id="modalLoginRemember">
                  <label class="form-check-label small" for="modalLoginRemember">Keep me logged in</label>
                </div>
                <button type="submit" id="modalLoginSubmitBtn" class="btn btn-primary w-100 py-2 fw-semibold rounded-3">
                  <i class="bi bi-box-arrow-in-right me-1"></i> Sign In
                </button>
              </form>

              <!-- Quick Demo Account Switcher -->
              <div class="mt-4 pt-3 border-top">
                <div class="small fw-bold text-muted mb-2"><i class="bi bi-lightning-charge-fill text-warning me-1"></i> Quick 1-Click Demo Login:</div>
                <div class="d-grid gap-1">
                  <button type="button" class="btn btn-sm btn-outline-secondary text-start py-1 px-2" onclick="App.quickDemoLogin('arun.prakash@gmail.com', 'password123')">
                    <span class="fw-bold text-primary">Resident:</span> Arun Prakash (Gandhi St)
                  </button>
                  <button type="button" class="btn btn-sm btn-outline-secondary text-start py-1 px-2" onclick="App.quickDemoLogin('mod.tambaram@streetconnect.com', 'password123')">
                    <span class="fw-bold text-success">Moderator:</span> Venkatesh Raman (Tambaram)
                  </button>
                  <button type="button" class="btn btn-sm btn-outline-secondary text-start py-1 px-2" onclick="App.quickDemoLogin('admin@streetconnect.com', 'admin123')">
                    <span class="fw-bold text-danger">Admin:</span> Suresh Kumar
                  </button>
                </div>
              </div>
            </div>
            <div class="modal-footer border-0 pt-0 justify-content-center">
              <span class="text-muted small">New to StreetConnect?</span>
              <a href="#" class="small fw-bold text-primary text-decoration-none" data-bs-dismiss="modal" data-bs-toggle="modal" data-bs-target="#registerModal">Create an account</a>
            </div>
          </div>
        </div>
      </div>

      <!-- Register Modal -->
      <div class="modal fade" id="registerModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered modal-lg">
          <div class="modal-content border-0 shadow-lg rounded-4">
            <div class="modal-header border-0 pb-0">
              <div>
                <h4 class="modal-title fw-bold text-dark">Join Your Street Community</h4>
                <p class="text-muted small mb-0">Connect with neighbors and get civic issues resolved faster</p>
              </div>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body p-4">
              <form id="modalRegisterForm" onsubmit="App.handleModalRegister(event)">
                <div class="row g-3">
                  <div class="col-md-6">
                    <label class="form-label fw-semibold small">Full Name *</label>
                    <input type="text" id="regFullName" class="form-control" placeholder="e.g. Arun Prakash" required minlength="3">
                  </div>
                  <div class="col-md-6">
                    <label class="form-label fw-semibold small">Email Address *</label>
                    <input type="email" id="regEmail" class="form-control" placeholder="name@example.com" required>
                  </div>
                  <div class="col-md-6">
                    <label class="form-label fw-semibold small">Mobile Number (10 digits) *</label>
                    <input type="tel" id="regMobile" class="form-control" placeholder="9840112233" required pattern="[6-9][0-9]{9}">
                  </div>
                  <div class="col-md-6">
                    <label class="form-label fw-semibold small">Street / Locality *</label>
                    <select id="regStreetId" class="form-select" required>
                      <option value="">-- Choose your street --</option>
                    </select>
                  </div>
                  <div class="col-md-6">
                    <label class="form-label fw-semibold small">Create Password *</label>
                    <input type="password" id="regPassword" class="form-control" placeholder="Min 6 characters" required minlength="6">
                  </div>
                  <div class="col-md-6">
                    <label class="form-label fw-semibold small">Confirm Password *</label>
                    <input type="password" id="regConfirmPassword" class="form-control" placeholder="Re-enter password" required minlength="6">
                  </div>
                  <div class="col-12">
                    <div class="p-3 bg-light rounded-3 border">
                      <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="regNewStreetToggle" onchange="App.toggleNewStreetFields(this.checked)">
                        <label class="form-check-label small fw-semibold" for="regNewStreetToggle">
                          My street is not in the list (Add a new street)
                        </label>
                      </div>
                      <div id="newStreetFields" class="row g-2 mt-2 d-none">
                        <div class="col-md-4">
                          <input type="text" id="regNewStreetName" class="form-control form-control-sm" placeholder="Street Name">
                        </div>
                        <div class="col-md-3">
                          <input type="text" id="regNewArea" class="form-control form-control-sm" placeholder="Area / Locality">
                        </div>
                        <div class="col-md-3">
                          <input type="text" id="regNewCity" class="form-control form-control-sm" placeholder="City" value="Chennai">
                        </div>
                        <div class="col-md-2">
                          <input type="text" id="regNewPincode" class="form-control form-control-sm" placeholder="Pincode">
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="mt-4">
                  <button type="submit" id="regSubmitBtn" class="btn btn-primary w-100 py-2 fw-semibold rounded-3">
                    <i class="bi bi-person-check me-1"></i> Register &amp; Join Street
                  </button>
                </div>
              </form>
            </div>
            <div class="modal-footer border-0 pt-0 justify-content-center">
              <span class="text-muted small">Already have an account?</span>
              <a href="#" class="small fw-bold text-primary text-decoration-none" data-bs-dismiss="modal" data-bs-toggle="modal" data-bs-target="#loginModal">Sign in here</a>
            </div>
          </div>
        </div>
      </div>
    `;

    document.body.insertAdjacentHTML('beforeend', modalHtml);

    // Populate street options in register modal
    this.populateStreetDropdown('regStreetId');
  },

  async populateStreetDropdown(selectId) {
    const el = document.getElementById(selectId);
    if (!el) return;
    try {
      const res = await Api.get('/streets');
      if (res && res.data) {
        let opts = `<option value="">-- Choose your street --</option>`;
        res.data.forEach(s => {
          opts += `<option value="${s.id}">${s.name}, ${s.area} (${s.city})</option>`;
        });
        el.innerHTML = opts;
      }
    } catch (e) {
      console.error('Error fetching streets:', e);
    }
  },

  toggleNewStreetFields(show) {
    const fields = document.getElementById('newStreetFields');
    const select = document.getElementById('regStreetId');
    if (show) {
      fields.classList.remove('d-none');
      select.removeAttribute('required');
      select.value = '';
    } else {
      fields.classList.add('d-none');
      select.setAttribute('required', 'required');
    }
  },

  async handleModalLogin(e) {
    e.preventDefault();
    const btn = document.getElementById('modalLoginSubmitBtn');
    const identifier = document.getElementById('modalLoginIdentifier').value;
    const password = document.getElementById('modalLoginPassword').value;
    const rememberMe = document.getElementById('modalLoginRemember').checked;

    btn.disabled = true;
    btn.innerHTML = `<span class="spinner-border spinner-border-sm me-2"></span>Signing in...`;

    try {
      await Auth.login(identifier, password, rememberMe);
      const modalEl = document.getElementById('loginModal');
      const modal = bootstrap.Modal.getInstance(modalEl);
      if (modal) modal.hide();

      this.showToast('Login successful! Welcome back.', 'success');
      Auth.renderNavbar();

      // Trigger custom event or reload page if needed
      setTimeout(() => {
        window.location.reload();
      }, 500);
    } catch (err) {
      this.showToast(err.message || 'Login failed. Please check your credentials.', 'danger');
    } finally {
      btn.disabled = false;
      btn.innerHTML = `<i class="bi bi-box-arrow-in-right me-1"></i> Sign In`;
    }
  },

  async quickDemoLogin(identifier, password) {
    try {
      await Auth.login(identifier, password, false);
      const modalEl = document.getElementById('loginModal');
      const modal = bootstrap.Modal.getInstance(modalEl);
      if (modal) modal.hide();

      this.showToast(`Logged in as ${identifier.split('@')[0]}!`, 'success');
      setTimeout(() => window.location.reload(), 400);
    } catch (err) {
      this.showToast(err.message || 'Demo login failed', 'danger');
    }
  },

  async handleModalRegister(e) {
    e.preventDefault();
    const btn = document.getElementById('regSubmitBtn');
    const pwd = document.getElementById('regPassword').value;
    const confirmPwd = document.getElementById('regConfirmPassword').value;

    if (pwd !== confirmPwd) {
      this.showToast('Password and confirmation do not match!', 'warning');
      return;
    }

    const payload = {
      fullName: document.getElementById('regFullName').value,
      email: document.getElementById('regEmail').value,
      mobileNumber: document.getElementById('regMobile').value,
      password: pwd,
      confirmPassword: confirmPwd
    };

    const isNewStreet = document.getElementById('regNewStreetToggle').checked;
    if (isNewStreet) {
      payload.streetName = document.getElementById('regNewStreetName').value;
      payload.area = document.getElementById('regNewArea').value;
      payload.city = document.getElementById('regNewCity').value;
      payload.pincode = document.getElementById('regNewPincode').value;
    } else {
      payload.streetId = document.getElementById('regStreetId').value;
    }

    btn.disabled = true;
    btn.innerHTML = `<span class="spinner-border spinner-border-sm me-2"></span>Creating account...`;

    try {
      await Auth.register(payload);
      const modalEl = document.getElementById('registerModal');
      const modal = bootstrap.Modal.getInstance(modalEl);
      if (modal) modal.hide();

      this.showToast('Account created and joined street! Welcome!', 'success');
      setTimeout(() => window.location.reload(), 500);
    } catch (err) {
      this.showToast(err.message || 'Registration failed.', 'danger');
    } finally {
      btn.disabled = false;
      btn.innerHTML = `<i class="bi bi-person-check me-1"></i> Register &amp; Join Street`;
    }
  },

  handleForgotPasswordPrompt() {
    const id = prompt('Enter your registered email address or mobile number:');
    if (id) {
      Api.post('/auth/forgot-password', { identifier: id })
        .then(res => this.showToast(res.message, 'info'))
        .catch(err => this.showToast(err.message, 'danger'));
    }
  }
};

document.addEventListener('DOMContentLoaded', () => {
  App.init();
});
