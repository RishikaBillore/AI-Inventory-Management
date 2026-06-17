/* ==============================================
   InventoryAI — app.js
   Connects to Spring Boot REST API at /items
   ============================================== */

const API = "/items";

let barChart    = null;
let doughnutChart = null;
let currentEditId = null;
let allItems    = [];

/* -----------------------------------------------
   INIT
----------------------------------------------- */
document.addEventListener("DOMContentLoaded", () => {
  startClock();
  showView("dashboard");
});

/* -----------------------------------------------
   CLOCK
----------------------------------------------- */
function startClock() {
  const el = document.getElementById("clock");
  function tick() {
    const now = new Date();
    el.textContent = now.toLocaleTimeString("en-IN", { hour: "2-digit", minute: "2-digit", second: "2-digit" });
  }
  tick();
  setInterval(tick, 1000);
}

/* -----------------------------------------------
   NAVIGATION
----------------------------------------------- */
function showView(name) {
  // hide all views
  document.querySelectorAll(".view").forEach(v => {
    v.classList.add("hidden");
    v.classList.remove("active");
  });

  // activate target
  const target = document.getElementById("view-" + name);
  if (target) {
    target.classList.remove("hidden");
    target.classList.add("active");
  }

  // update nav highlight
  document.querySelectorAll(".nav-item").forEach(a => {
    a.classList.toggle("active", a.dataset.view === name);
  });

  // update page title
  const titles = {
    dashboard: "Dashboard",
    inventory: "Inventory",
    analytics: "Analytics",
    ai:        "AI Insights",
    add:       "Add Item"
  };
  document.getElementById("pageTitle").textContent = titles[name] || name;

  // load data for the view
  switch (name) {
    case "dashboard": loadDashboard(); break;
    case "inventory": loadInventory(); break;
    case "analytics": loadAnalytics(); break;
    case "ai":        loadAI();        break;
    case "add":       resetForm();     break;
  }
}

/* -----------------------------------------------
   FETCH HELPERS
----------------------------------------------- */
async function fetchJSON(url) {
  try {
    const res = await fetch(url);
    if (!res.ok) throw new Error(res.status);
    return await res.json();
  } catch {
    return [];
  }
}

async function fetchText(url) {
  try {
    const res = await fetch(url);
    return await res.text();
  } catch {
    return "Unavailable";
  }
}

/* -----------------------------------------------
   DASHBOARD
----------------------------------------------- */
async function loadDashboard() {
  // Show loading state
  setLoading("summary");
  setLoading("health");
  setLoading("criticalCount");

  const [summary, health, critical, lowStock, items] = await Promise.all([
    fetchText(API + "/summary"),
    fetchText(API + "/health"),
    fetchJSON(API + "/critical"),
    fetchJSON(API + "/low-stock"),
    fetchJSON(API)
  ]);

  document.getElementById("summary").textContent = summary;
  document.getElementById("health").textContent  = health;
  document.getElementById("criticalCount").textContent = critical.length;

  // Low stock badge
  const badge = document.getElementById("lowStockBadge");
  if (lowStock.length > 0) {
    badge.textContent = `⚠ ${lowStock.length} Low Stock Item${lowStock.length > 1 ? "s" : ""}`;
    badge.classList.remove("hidden");
  } else {
    badge.classList.add("hidden");
  }

  // Dashboard table (top 8 items)
  const tbody = document.getElementById("dashboardTable");
  tbody.innerHTML = "";
  const display = items.slice(0, 8);
  if (!display.length) {
    tbody.innerHTML = `<tr><td colspan="5" style="text-align:center;color:var(--text-muted);padding:24px;">No inventory data found.</td></tr>`;
    return;
  }
  display.forEach(item => {
    const status = getStatus(item);
    tbody.innerHTML += `
      <tr>
        <td>${escHtml(item.name)}</td>
        <td style="color:var(--text-muted)">${escHtml(item.category)}</td>
        <td class="mono">${item.quantity}</td>
        <td class="mono">₹${item.price.toFixed(2)}</td>
        <td><span class="pill ${status.cls}">${status.label}</span></td>
      </tr>
    `;
  });
}

function setLoading(id) {
  const el = document.getElementById(id);
  if (el) {
    el.textContent = "Loading…";
    el.style.opacity = "0.5";
  }
}

function clearLoading(id) {
  const el = document.getElementById(id);
  if (el) el.style.opacity = "1";
}

/* -----------------------------------------------
   INVENTORY
----------------------------------------------- */
async function loadInventory() {
  allItems = await fetchJSON(API);
  populateCategoryFilter();
  renderInventoryTable(allItems);
}

function populateCategoryFilter() {
  const sel = document.getElementById("categoryFilter");
  const current = sel.value;
  const categories = [...new Set(allItems.map(i => i.category))].sort();
  sel.innerHTML = `<option value="">All Categories</option>`;
  categories.forEach(c => {
    sel.innerHTML += `<option value="${escHtml(c)}" ${c === current ? "selected" : ""}>${escHtml(c)}</option>`;
  });
}

function renderInventoryTable(items) {
  const tbody = document.getElementById("inventoryTable");
  tbody.innerHTML = "";

  if (!items.length) {
    tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;color:var(--text-muted);padding:24px;">No items found.</td></tr>`;
    return;
  }

  items.forEach(item => {
    const status = getStatus(item);
    tbody.innerHTML += `
      <tr>
        <td class="mono" style="color:var(--text-muted)">#${item.id}</td>
        <td style="font-weight:500">${escHtml(item.name)}</td>
        <td style="color:var(--text-muted)">${escHtml(item.category)}</td>
        <td class="mono">${item.quantity}</td>
        <td class="mono">₹${item.price.toFixed(2)}</td>
        <td class="mono" style="color:var(--text-muted)">${item.reorderLevel}</td>
        <td>
          <button class="action-btn" onclick="openModal(${item.id}, '${escHtml(item.name).replace(/'/g,"\\'")}')">Edit Qty</button>
          <button class="action-btn del" onclick="deleteItem(${item.id})">Delete</button>
        </td>
      </tr>
    `;
  });
}

function filterTable() {
  const query    = document.getElementById("searchBox").value.toLowerCase();
  const category = document.getElementById("categoryFilter").value;
  const filtered = allItems.filter(item => {
    const matchName = item.name.toLowerCase().includes(query) || item.category.toLowerCase().includes(query);
    const matchCat  = !category || item.category === category;
    return matchName && matchCat;
  });
  renderInventoryTable(filtered);
}

/* -----------------------------------------------
   ANALYTICS
----------------------------------------------- */
async function loadAnalytics() {
  const items = await fetchJSON(API);
  if (!items.length) return;

  // Bar chart — stock per product
  {
    const ctx = document.getElementById("chartBar").getContext("2d");
    if (barChart) barChart.destroy();
    barChart = new Chart(ctx, {
      type: "bar",
      data: {
        labels: items.map(i => i.name),
        datasets: [{
          label: "Stock Quantity",
          data: items.map(i => i.quantity),
          backgroundColor: items.map(i => {
            if (i.quantity === 0)               return "rgba(139,38,53,0.6)";
            if (i.quantity <= i.reorderLevel)   return "rgba(184,134,11,0.6)";
            return "rgba(74,144,217,0.55)";
          }),
          borderColor: items.map(i => {
            if (i.quantity === 0)               return "#8b2635";
            if (i.quantity <= i.reorderLevel)   return "#b8860b";
            return "#4a90d9";
          }),
          borderWidth: 1,
          borderRadius: 4,
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { display: false },
          tooltip: {
            backgroundColor: "#1a1e26",
            borderColor: "#252932",
            borderWidth: 1,
            titleColor: "#e4e8f0",
            bodyColor: "#6b7484",
            padding: 10,
          }
        },
        scales: {
          x: {
            ticks: { color: "#6b7484", font: { size: 11 } },
            grid:  { color: "#252932" }
          },
          y: {
            ticks: { color: "#6b7484", font: { size: 11 } },
            grid:  { color: "#252932" }
          }
        }
      }
    });
  }

  // Doughnut — category distribution
  {
    const ctx = document.getElementById("chartDoughnut").getContext("2d");
    if (doughnutChart) doughnutChart.destroy();

    const categoryCount = {};
    items.forEach(i => {
      categoryCount[i.category] = (categoryCount[i.category] || 0) + i.quantity;
    });
    const labels = Object.keys(categoryCount);
    const values = Object.values(categoryCount);

    const palette = ["#4a90d9","#5aad82","#d4a820","#e07070","#a07fd4","#60aec0","#c98060"];

    doughnutChart = new Chart(ctx, {
      type: "doughnut",
      data: {
        labels,
        datasets: [{
          data: values,
          backgroundColor: palette.slice(0, labels.length).map(c => c + "90"),
          borderColor:      palette.slice(0, labels.length),
          borderWidth: 1.5,
        }]
      },
      options: {
        responsive: true,
        cutout: "65%",
        plugins: {
          legend: {
            position: "bottom",
            labels: {
              color: "#6b7484",
              font: { size: 12 },
              padding: 14,
              boxWidth: 12,
            }
          },
          tooltip: {
            backgroundColor: "#1a1e26",
            borderColor: "#252932",
            borderWidth: 1,
            titleColor: "#e4e8f0",
            bodyColor: "#6b7484",
            padding: 10,
          }
        }
      }
    });
  }
}

/* -----------------------------------------------
   AI INSIGHTS
----------------------------------------------- */
async function loadAI() {
  const box = document.getElementById("aiBox");
  box.innerHTML = `<div style="color:var(--text-muted);font-size:13px;">Analyzing inventory…</div>`;

  const items = await fetchJSON(API);
  if (!items.length) {
    box.innerHTML = `<div style="color:var(--text-muted)">No items to analyze.</div>`;
    return;
  }

  // Fetch suggestions in parallel
  const suggestions = await Promise.all(
    items.map(item => fetchText(`${API}/${item.id}/suggestion`))
  );

  box.innerHTML = "";
  items.forEach((item, idx) => {
    const suggestion = suggestions[idx];
    const cls = getSuggestionClass(suggestion);
    box.innerHTML += `
      <div class="ai-card">
        <div class="ai-card-name">${escHtml(item.name)}</div>
        <div class="ai-card-cat">${escHtml(item.category)} · Qty: ${item.quantity}</div>
        <div class="ai-card-suggestion ${cls}">${escHtml(suggestion)}</div>
      </div>
    `;
  });
}

function getSuggestionClass(suggestion) {
  const s = suggestion.toUpperCase();
  if (s.includes("OUT OF STOCK") || s.includes("IMMEDIATELY")) return "danger";
  if (s.includes("LOW") || s.includes("REORDER"))               return "warn";
  if (s.includes("HEALTHY"))                                     return "ok";
  return "";
}

/* -----------------------------------------------
   ADD ITEM
----------------------------------------------- */
async function addItem() {
  const name        = document.getElementById("f-name").value.trim();
  const category    = document.getElementById("f-category").value.trim();
  const quantity    = parseInt(document.getElementById("f-quantity").value);
  const price       = parseFloat(document.getElementById("f-price").value);
  const reorderLevel= parseInt(document.getElementById("f-reorder").value);

  const msg = document.getElementById("formMsg");

  if (!name || !category || isNaN(quantity) || isNaN(price) || isNaN(reorderLevel)) {
    showMsg(msg, "All fields are required.", "error");
    return;
  }

  try {
    const res = await fetch(API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name, category, quantity, price, reorderLevel })
    });
    if (!res.ok) throw new Error();
    showMsg(msg, `"${name}" added to inventory.`, "success");
    resetForm();
  } catch {
    showMsg(msg, "Failed to add item. Is the server running?", "error");
  }
}

function resetForm() {
  ["f-name","f-category","f-quantity","f-price","f-reorder"].forEach(id => {
    document.getElementById(id).value = "";
  });
  const msg = document.getElementById("formMsg");
  msg.textContent = "";
  msg.className = "form-msg hidden";
}

function showMsg(el, text, type) {
  el.textContent = text;
  el.className = `form-msg ${type}`;
}

/* -----------------------------------------------
   DELETE ITEM
----------------------------------------------- */
async function deleteItem(id) {
  if (!confirm("Delete this item from inventory?")) return;
  try {
    await fetch(`${API}/${id}`, { method: "DELETE" });
    allItems = allItems.filter(i => i.id !== id);
    renderInventoryTable(allItems);
  } catch {
    alert("Failed to delete item.");
  }
}

/* -----------------------------------------------
   UPDATE QTY MODAL
----------------------------------------------- */
function openModal(id, name) {
  currentEditId = id;
  document.getElementById("modalItemName").textContent = name;
  document.getElementById("modalQty").value = "";
  document.getElementById("modal").classList.remove("hidden");
  setTimeout(() => document.getElementById("modalQty").focus(), 50);
}

function closeModal() {
  document.getElementById("modal").classList.add("hidden");
  currentEditId = null;
}

async function saveQty() {
  const qty = parseInt(document.getElementById("modalQty").value);
  if (isNaN(qty) || qty < 0) { alert("Enter a valid quantity."); return; }
  try {
    await fetch(`${API}/${currentEditId}/quantity?quantity=${qty}`, { method: "PUT" });
    closeModal();
    loadInventory();
  } catch {
    alert("Failed to update quantity.");
  }
}

// Close modal on overlay click
document.getElementById("modal").addEventListener("click", function(e) {
  if (e.target === this) closeModal();
});

/* -----------------------------------------------
   HELPERS
----------------------------------------------- */
function getStatus(item) {
  if (item.quantity === 0)                     return { cls: "pill-critical", label: "Out of Stock" };
  if (item.quantity <= item.reorderLevel)      return { cls: "pill-low",      label: "Low Stock"    };
  return                                              { cls: "pill-ok",       label: "Healthy"      };
}

function escHtml(str) {
  return String(str)
    .replace(/&/g,"&amp;")
    .replace(/</g,"&lt;")
    .replace(/>/g,"&gt;")
    .replace(/"/g,"&quot;");
}