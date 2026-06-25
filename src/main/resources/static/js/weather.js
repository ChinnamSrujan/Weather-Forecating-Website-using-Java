// weather.js – weather page: chart, sun arc, unit toggle, favourites

let currentUnit = 'C';
const FAVS_KEY = 'wf_favourites';

document.addEventListener('DOMContentLoaded', () => {
    initUnitToggle();
    initFavButton();
    buildTempChart();
    buildSunArc();
    updateFavButton();
});

// ─── Unit Toggle (°C / °F) ─────────────────────────────────────────────────────

function setUnit(unit) {
    currentUnit = unit;
    document.getElementById('unitC')?.classList.toggle('active', unit === 'C');
    document.getElementById('unitF')?.classList.toggle('active', unit === 'F');

    document.querySelectorAll('[data-celsius]').forEach(el => {
        const c = parseFloat(el.dataset.celsius);
        if (isNaN(c)) return;
        if (unit === 'F') {
            el.textContent = (c * 9 / 5 + 32).toFixed(1) + '°F';
        } else {
            el.textContent = c.toFixed(1) + '°C';
        }
    });

    // Re-render chart with new unit
    buildTempChart();
}

function initUnitToggle() {
    // Restore preference
    const saved = localStorage.getItem('wf_unit');
    if (saved && saved !== 'C') setUnit(saved);
}

// ─── Temperature Chart ─────────────────────────────────────────────────────────

function buildTempChart() {
    const canvas = document.getElementById('tempChart');
    if (!canvas || !FORECAST_DATA || FORECAST_DATA.length === 0) return;

    // Take every other entry so the chart isn't too dense (max ~20 points)
    const samples = FORECAST_DATA.filter((_, i) => i % 2 === 0);
    const labels  = samples.map(d => {
        const parts = d.date.split(' ');
        return parts[0].slice(5) + ' ' + parts[1].slice(0, 5);  // "MM-DD HH:mm"
    });
    const temps = samples.map(d => {
        const c = parseFloat(d.temperature);
        return currentUnit === 'F' ? +(c * 9 / 5 + 32).toFixed(1) : c;
    });

    // Destroy previous chart instance if any
    const existing = Chart.getChart(canvas);
    if (existing) existing.destroy();

    new Chart(canvas, {
        type: 'line',
        data: {
            labels,
            datasets: [{
                label: currentUnit === 'F' ? 'Temperature (°F)' : 'Temperature (°C)',
                data: temps,
                borderColor: '#667eea',
                backgroundColor: 'rgba(102,126,234,0.15)',
                fill: true,
                tension: 0.4,
                pointBackgroundColor: '#764ba2',
                pointRadius: 4,
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            interaction: { mode: 'index', intersect: false },
            plugins: {
                legend: { display: true, labels: { color: '#555' } },
                tooltip: {
                    callbacks: {
                        label: ctx => ` ${ctx.parsed.y}°${currentUnit}`
                    }
                }
            },
            scales: {
                x: {
                    ticks: { color: '#666', maxRotation: 45, minRotation: 30, font: { size: 11 } },
                    grid: { color: 'rgba(0,0,0,0.05)' }
                },
                y: {
                    ticks: {
                        color: '#666',
                        callback: v => v + '°' + currentUnit
                    },
                    grid: { color: 'rgba(0,0,0,0.05)' }
                }
            }
        }
    });
}

// ─── Sunrise / Sunset Arc ──────────────────────────────────────────────────────

function buildSunArc() {
    const canvas = document.getElementById('sunArcCanvas');
    if (!canvas || !CURRENT_DATA) return;

    const sunrise = CURRENT_DATA.sunrise;   // Unix seconds
    const sunset  = CURRENT_DATA.sunset;
    const tz      = CURRENT_DATA.timezone;  // offset in seconds

    if (!sunrise || !sunset) return;

    // Display times
    document.getElementById('sunriseTime').textContent = '🌅 ' + unixToLocalTime(sunrise, tz);
    document.getElementById('sunsetTime').textContent  = '🌇 ' + unixToLocalTime(sunset,  tz);

    const now    = Math.floor(Date.now() / 1000);
    const total  = sunset - sunrise;
    const elapsed = Math.max(0, Math.min(now - sunrise, total));
    const progress = elapsed / total;   // 0 to 1

    const ctx  = canvas.getContext('2d');
    const W    = canvas.width;
    const H    = canvas.height;
    const cx   = W / 2;
    const cy   = H - 20;
    const r    = H - 40;

    ctx.clearRect(0, 0, W, H);

    // Arc track
    ctx.beginPath();
    ctx.arc(cx, cy, r, Math.PI, 0, false);
    ctx.strokeStyle = 'rgba(200,200,200,0.4)';
    ctx.lineWidth = 6;
    ctx.stroke();

    // Progress arc
    const endAngle = Math.PI + progress * Math.PI;
    ctx.beginPath();
    ctx.arc(cx, cy, r, Math.PI, endAngle, false);
    ctx.strokeStyle = '#f9a825';
    ctx.lineWidth = 6;
    ctx.stroke();

    // Sun dot position
    const sunAngle = Math.PI + progress * Math.PI;
    const sx = cx + r * Math.cos(sunAngle);
    const sy = cy + r * Math.sin(sunAngle);

    ctx.beginPath();
    ctx.arc(sx, sy, 12, 0, Math.PI * 2);
    ctx.fillStyle = '#ffca28';
    ctx.fill();
    ctx.strokeStyle = '#f57f17';
    ctx.lineWidth = 2;
    ctx.stroke();

    // Sun emoji label
    ctx.font = '18px serif';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText('☀️', sx, sy);
}

function unixToLocalTime(unix, tzOffsetSeconds) {
    const d = new Date((unix + tzOffsetSeconds) * 1000);
    const h = String(d.getUTCHours()).padStart(2, '0');
    const m = String(d.getUTCMinutes()).padStart(2, '0');
    return `${h}:${m}`;
}

// ─── Favourites ────────────────────────────────────────────────────────────────

function getFavourites() {
    try { return JSON.parse(localStorage.getItem(FAVS_KEY)) || []; }
    catch { return []; }
}

function isFavourite(city) {
    return getFavourites().some(c => c.toLowerCase() === city.toLowerCase());
}

function updateFavButton() {
    const btn = document.getElementById('favBtn');
    if (!btn) return;
    const city = btn.dataset.city;
    if (isFavourite(city)) {
        btn.textContent = '★ Favourited';
        btn.classList.add('fav-active');
    } else {
        btn.textContent = '☆ Favourite';
        btn.classList.remove('fav-active');
    }
}

function toggleFavourite(btn) {
    const city = btn.dataset.city;
    let favs = getFavourites();
    if (isFavourite(city)) {
        favs = favs.filter(c => c.toLowerCase() !== city.toLowerCase());
    } else {
        favs.push(city);
    }
    localStorage.setItem(FAVS_KEY, JSON.stringify(favs));
    updateFavButton();
}

function initFavButton() {
    updateFavButton();
}

// ─── Hourly Carousel ───────────────────────────────────────────────────────────

function scrollCarousel(dir) {
    const carousel = document.getElementById('hourlyCarousel');
    if (!carousel) return;
    carousel.scrollBy({ left: dir * 200, behavior: 'smooth' });
}
