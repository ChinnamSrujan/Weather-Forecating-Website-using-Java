// app.js – shared across all pages

document.addEventListener('DOMContentLoaded', () => {
    // Enter key on instant search input
    const apiCityInput = document.getElementById('apiCity');
    if (apiCityInput) {
        apiCityInput.addEventListener('keypress', e => {
            if (e.key === 'Enter') searchWeatherAPI();
        });
    }

    // Show loading spinner on main form submit
    const searchForms = document.querySelectorAll('.search-form');
    searchForms.forEach(form => {
        form.addEventListener('submit', () => {
            const btn = form.querySelector('.search-btn:not(.geo-btn)');
            if (btn) showLoading(btn);
        });
    });
});

// ─── Instant API search ────────────────────────────────────────────────────────

async function searchWeatherAPI() {
    const cityInput  = document.getElementById('apiCity');
    const resultsDiv = document.getElementById('apiResults');
    const searchBtn  = document.querySelector('.api-btn');
    if (!cityInput || !resultsDiv) return;

    const city = cityInput.value.trim();
    if (!city) { showError(resultsDiv, 'Please enter a city name'); return; }

    showLoading(searchBtn);
    resultsDiv.style.display = 'block';
    // Skeleton placeholder
    resultsDiv.innerHTML = buildSkeleton(4);

    try {
        const resp = await fetch(`/api/weather/${encodeURIComponent(city)}`);
        if (!resp.ok) {
            const err = await resp.json().catch(() => ({}));
            throw new Error(err.error || `Weather data not found for "${city}"`);
        }
        const data = await resp.json();
        displayAPIResults(resultsDiv, data, city);
    } catch (err) {
        showError(resultsDiv, err.message);
    } finally {
        hideLoading(searchBtn);
    }
}

function buildSkeleton(count) {
    let html = '<div class="api-forecast-grid">';
    for (let i = 0; i < count; i++) {
        html += '<div class="skeleton skeleton-card"></div>';
    }
    html += '</div>';
    return html;
}

function displayAPIResults(container, weatherData, city) {
    if (!weatherData || weatherData.length === 0) {
        showError(container, 'No weather data available');
        return;
    }
    const limited = weatherData.slice(0, 8);
    let html = `
        <div class="api-results-header">
            <h3>🌤️ Weather for ${escHtml(city)}</h3>
            <p>Showing ${limited.length} upcoming forecasts</p>
        </div>
        <div class="api-forecast-grid">
    `;
    limited.forEach(f => {
        html += `
            <div class="api-forecast-item">
                <div class="api-weather-icon">${f.icon || '🌤️'}</div>
                <div class="api-date">${formatDate(f.date)}</div>
                <div class="api-temp">${f.temperature}°C</div>
                <div class="api-condition">${f.weatherCondition || ''}</div>
                <div class="api-humidity">💧 ${f.humidity}%</div>
            </div>
        `;
    });
    html += `
        </div>
        <div class="api-actions">
            <a href="/weather?city=${encodeURIComponent(city)}" class="view-full-btn">View Full Forecast →</a>
        </div>
    `;
    container.innerHTML = html;
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

function formatDate(dateString) {
    const d = new Date(dateString);
    return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
}

function showError(container, message) {
    container.style.display = 'block';
    container.innerHTML = `
        <div class="api-error">
            <span>⚠️</span> <span>${escHtml(message)}</span>
        </div>`;
}

function showLoading(button) {
    if (!button) return;
    button.dataset.originalHtml = button.innerHTML;
    button.innerHTML = '<span class="loading"></span> Loading...';
    button.disabled = true;
}

function hideLoading(button) {
    if (!button) return;
    if (button.dataset.originalHtml) button.innerHTML = button.dataset.originalHtml;
    button.disabled = false;
}

function escHtml(str) {
    if (!str) return '';
    return String(str).replace(/[&<>"']/g, m => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m]));
}
