const api = "/api";
let currentUser = null;
let isAdmin = false;
let users = [];
const tokenKey = "ai_recommend_token";

function token() { return localStorage.getItem(tokenKey); }
function authHeaders(extra = {}) { return {...extra, ...(token() ? {"X-Auth-Token": token()} : {})}; }
function showToast(message, type = "success") {
  const el = document.getElementById("toast"); el.textContent = message; el.className = `toast ${type} show`;
  setTimeout(() => el.classList.remove("show"), 2500);
}
function switchAuth(mode) {
  const login = mode === "login";
  document.getElementById("loginForm").classList.toggle("hidden", !login);
  document.getElementById("registerForm").classList.toggle("hidden", login);
  document.getElementById("loginTab").classList.toggle("active", login);
  document.getElementById("registerTab").classList.toggle("active", !login);
  document.getElementById("authMessage").textContent = "";
}
function showAdminHint() {
  document.getElementById("loginEmail").value = "admin@airecommend.com";
  document.getElementById("loginPassword").value = "Admin@123";
  document.getElementById("authMessage").textContent = "Demo admin credentials filled. Click Login.";
}
async function register(event) {
  event.preventDefault();
  const body = {
    name: document.getElementById("regName").value.trim(),
    email: document.getElementById("regEmail").value.trim(),
    password: document.getElementById("regPassword").value,
    confirmPassword: document.getElementById("regConfirm").value,
    preferences: document.getElementById("regPreferences").value.trim()
  };
  try {
    const res = await fetch(api + "/auth/register", {method:"POST", headers:{"Content-Type":"application/json"}, body:JSON.stringify(body)});
    const data = await res.json();
    if (!res.ok || !data.success) throw new Error(data.message || "Registration failed.");
    localStorage.setItem(tokenKey, data.token);
    currentUser = data.user; isAdmin = false; enterApp();
    showToast("Account created successfully!");
  } catch (e) { document.getElementById("authMessage").textContent = e.message; }
}
async function login(event) {
  event.preventDefault();
  const body = {email:document.getElementById("loginEmail").value.trim(), password:document.getElementById("loginPassword").value};
  try {
    const res = await fetch(api + "/auth/login", {method:"POST", headers:{"Content-Type":"application/json"}, body:JSON.stringify(body)});
    const data = await res.json();
    if (!res.ok || !data.success) throw new Error(data.message || "Login failed.");
    localStorage.setItem(tokenKey, data.token); isAdmin = data.admin; currentUser = data.user; enterApp();
    showToast(isAdmin ? "Admin login successful!" : "Login successful!");
  } catch (e) { document.getElementById("authMessage").textContent = e.message; }
}
async function checkSession() {
  if (!token()) return showAuth();
  try {
    const res = await fetch(api + "/auth/me", {headers:authHeaders()});
    if (!res.ok) throw new Error();
    const data = await res.json(); isAdmin = data.admin; currentUser = data.user; enterApp();
  } catch { localStorage.removeItem(tokenKey); showAuth(); }
}
function showAuth() {
  document.getElementById("authPage").classList.remove("hidden");
  document.getElementById("appHeader").classList.add("hidden"); document.getElementById("appMain").classList.add("hidden"); document.getElementById("appFooter").classList.add("hidden");
}
function enterApp() {
  document.getElementById("authPage").classList.add("hidden"); document.getElementById("appHeader").classList.remove("hidden"); document.getElementById("appMain").classList.remove("hidden"); document.getElementById("appFooter").classList.remove("hidden");
  document.getElementById("adminPage").classList.toggle("hidden", !isAdmin);
  document.getElementById("adminNav").classList.toggle("hidden", !isAdmin);
  if (isAdmin) showPage("adminPage"); else showPage("userPage");
  if (!isAdmin) renderUserDashboard();
}
async function logout() {
  try { await fetch(api + "/auth/logout", {method:"POST", headers:authHeaders()}); } catch {}
  localStorage.removeItem(tokenKey); currentUser = null; isAdmin = false; showAuth(); switchAuth("login"); showToast("Logged out.");
}
function showPage(id) {
  if (id === "adminPage" && !isAdmin) { showToast("Admin access required.", "error"); return; }
  document.querySelectorAll(".page").forEach(p => p.classList.add("hidden"));
  document.getElementById(id).classList.remove("hidden");
  if (id === "adminPage") { loadStats(); loadSettings(); loadItems(); }
  if (id === "userPage" && currentUser) renderUserDashboard();
}
function renderUserDashboard() {
  document.getElementById("welcomeTitle").textContent = `Welcome, ${currentUser.name}!`;
  document.getElementById("profileName").textContent = currentUser.name;
  document.getElementById("profileEmail").textContent = currentUser.email;
  document.getElementById("preferenceCount").textContent = currentUser.preferences.length;
  document.getElementById("preferences").value = currentUser.preferences.join(", ");
  loadRecommendations();
}
async function loadRecommendations() {
  if (!currentUser) return;
  const data = await fetch(api + "/recommendations/" + currentUser.id, {headers:authHeaders()}).then(r => r.json());
  document.getElementById("recommendations").innerHTML = data.map(r => `
    <div class="card"><div class="card-top"><span class="category">${r.item.category}</span><span class="match">${r.score}%</span></div>
      <h3>${r.item.name}</h3><p class="small">${r.reason}</p><p>Rating: ${r.item.rating}/5 • ₹${r.item.price}</p>
      <div>${r.item.tags.map(t => `<span class="tag">${t}</span>`).join("")}</div>
    </div>`).join("");
}
async function savePreferences() {
  const preferences = document.getElementById("preferences").value;
  const res = await fetch(api + "/users/" + currentUser.id + "/preferences", {method:"PUT", headers:authHeaders({"Content-Type":"application/json"}), body:JSON.stringify({preferences})});
  if (!res.ok) return showToast("Could not update preferences.", "error");
  currentUser = await res.json(); renderUserDashboard(); showToast("Preferences updated!");
}
async function loadStats() {
  const s = await fetch(api + "/admin/stats", {headers:authHeaders()}).then(r => r.json());
  document.getElementById("stats").innerHTML = `<div class="stat">Users<b>${s.totalUsers}</b></div><div class="stat">Content Items<b>${s.totalItems}</b></div><div class="stat">Interactions<b>${s.totalInteractions}</b></div><div class="stat">Average Rating<b>${Number(s.averageRating).toFixed(2)}</b></div>`;
}
async function loadSettings() {
  const s = await fetch(api + "/recommendations/settings", {headers:authHeaders()}).then(r => r.json());
  document.getElementById("preferenceWeight").value = s.preferenceWeight; document.getElementById("ratingWeight").value = s.ratingWeight; document.getElementById("popularityWeight").value = s.popularityWeight;
}
async function saveSettings() {
  const body = {preferenceWeight:Number(document.getElementById("preferenceWeight").value), ratingWeight:Number(document.getElementById("ratingWeight").value), popularityWeight:Number(document.getElementById("popularityWeight").value)};
  if (body.preferenceWeight + body.ratingWeight + body.popularityWeight !== 100) return showToast("Weights must add up to 100.", "error");
  await fetch(api + "/recommendations/settings", {method:"PUT", headers:authHeaders({"Content-Type":"application/json"}), body:JSON.stringify(body)});
  showToast("Settings updated!");
}
async function loadItems() {
  const items = await fetch(api + "/items", {headers:authHeaders()}).then(r => r.json());
  document.getElementById("items").innerHTML = items.map(i => `<div class="card"><div class="card-top"><span class="category">${i.category}</span><span class="match">${i.rating}/5</span></div><h3>${i.name}</h3><p>₹${i.price}</p>${i.tags.map(t => `<span class="tag">${t}</span>`).join("")}</div>`).join("");
}
checkSession();
