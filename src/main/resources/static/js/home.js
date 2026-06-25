// home.js – index page: search history, favourites, geolocation

const HISTORY_KEY  = 'wf_history';
const FAVS_KEY     = 'wf_favourites';
const MAX_HISTORY  = 5;

document.addEventListener('DOMContentLoaded', () => {
    renderHistory();
    renderFavourites();
    setupHistoryCapture();
});

// ─── Search history ────────────────────────────────────────────────────────────

function getHistory() {
    try { return JSON.parse(localStorage.getItem(HISTORY_KEY)) || []; }
    catch { return []; }
}

function addToHistory(city) {
    if (!city) return;
    let hist = getHistory().filter(c => c.toLowerCase() !== city.toLowerCase());
    hist.unshift(city);
    if (hist.length > MAX_HISTORY) hist = hist.slice(0, MAX_HISTORY);
    localStorage.setItem(HISTORY_KEY, JSON.stringify(hist));
}

function renderHistory() {
    const hist = getHistory();
    const section = document.getElementById('recentSearches');
    const list    = document.getElementById('recentList');
    if (!section || !list) return;
    if (hist.length === 0) { section.style.display = 'none'; return; }

    section.style.display = 'block';
    list.innerHTML = hist.map(city => `
        <button class="city-tag" onclick="searchCity('${escHtml(city)}')">${escHtml(city)}</button>
    `).join('');
}

function setupHistoryCapture() {
    const form = document.getElementById('mainSearchForm');
    if (!form) return;
    form.addEventListener('submit', () => {
        const city = document.getElementById('city')?.value?.trim();
        if (city) addToHistory(city);
    });
}

// ─── Favourites ────────────────────────────────────────────────────────────────

function getFavourites() {
    try { return JSON.parse(localStorage.getItem(FAVS_KEY)) || []; }
    catch { return []; }
}

function renderFavourites() {
    const favs    = getFavourites();
    const section = document.getElementById('favSection');
    const list    = document.getElementById('favList');
    if (!section || !list) return;
    if (favs.length === 0) { section.style.display = 'none'; return; }

    section.style.display = 'block';
    list.innerHTML = favs.map(city => `
        <button class="city-tag fav-tag" onclick="searchCity('${escHtml(city)}')">${escHtml(city)}
            <span class="remove-fav" onclick="removeFav(event, '${escHtml(city)}')">✕</span>
        </button>
    `).join('');
}

function removeFav(event, city) {
    event.stopPropagation();
    let favs = getFavourites().filter(c => c.toLowerCase() !== city.toLowerCase());
    localStorage.setItem(FAVS_KEY, JSON.stringify(favs));
    renderFavourites();
}

// ─── Geolocation ───────────────────────────────────────────────────────────────

async function locateMe() {
    if (!navigator.geolocation) {
        alert('Geolocation is not supported by your browser.');
        return;
    }
    const geoBtn = document.querySelector('.geo-btn');
    if (geoBtn) { geoBtn.textContent = '⏳'; geoBtn.disabled = true; }

    navigator.geolocation.getCurrentPosition(
        async (pos) => {
            const { latitude: lat, longitude: lon } = pos.coords;
            try {
                // Reverse-geocode using OpenWeatherMap's geo endpoint
                const resp = await fetch(
                    `/api/current-by-coords?lat=${lat}&lon=${lon}`
                );
                if (resp.ok) {
                    const data = await resp.json();
                    const city = data.city || data.name;
                    if (city) {
                        addToHistory(city);
                        window.location.href = `/weather?city=${encodeURIComponent(city)}`;
                        return;
                    }
                }
            } catch (e) { /* fall through */ }
            // Fallback: submit coords as city text won't work, show alert
            alert('Could not determine city from your location. Please type it manually.');
            if (geoBtn) { geoBtn.textContent = '📍'; geoBtn.disabled = false; }
        },
        (err) => {
            alert('Location access denied or unavailable.');
            if (geoBtn) { geoBtn.textContent = '📍'; geoBtn.disabled = false; }
        }
    );
}

// ─── Shared helpers ────────────────────────────────────────────────────────────

function searchCity(city) {
    addToHistory(city);
    document.getElementById('city').value = city;
    document.getElementById('mainSearchForm').submit();
}

function escHtml(str) {
    return str.replace(/[&<>"']/g, m => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m]));
}
