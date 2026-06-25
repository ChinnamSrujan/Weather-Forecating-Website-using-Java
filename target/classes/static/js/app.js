// Weather Forecast Application JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Initialize the application
    initializeApp();
});

function initializeApp() {
    // Add event listeners
    const apiCityInput = document.getElementById('apiCity');
    if (apiCityInput) {
        apiCityInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                searchWeatherAPI();
            }
        });
    }

    // Add loading states to forms
    const searchForms = document.querySelectorAll('.search-form');
    searchForms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const submitBtn = form.querySelector('.search-btn');
            if (submitBtn) {
                showLoading(submitBtn);
            }
        });
    });

    // Add smooth scrolling for better UX
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth'
                });
            }
        });
    });
}

// API-based weather search function
async function searchWeatherAPI() {
    const cityInput = document.getElementById('apiCity');
    const resultsDiv = document.getElementById('apiResults');
    const searchBtn = document.querySelector('.api-btn');
    
    if (!cityInput || !resultsDiv) return;
    
    const city = cityInput.value.trim();
    if (!city) {
        showError(resultsDiv, 'Please enter a city name');
        return;
    }

    // Show loading state
    showLoading(searchBtn);
    resultsDiv.style.display = 'block';
    resultsDiv.innerHTML = '<div class="loading-message">🔄 Fetching weather data...</div>';

    try {
        const response = await fetch(`/api/weather/${encodeURIComponent(city)}`);
        
        if (!response.ok) {
            throw new Error(`Weather data not found for ${city}`);
        }

        const weatherData = await response.json();
        displayAPIResults(resultsDiv, weatherData, city);
        
    } catch (error) {
        showError(resultsDiv, error.message);
    } finally {
        hideLoading(searchBtn);
    }
}

function displayAPIResults(container, weatherData, city) {
    if (!weatherData || weatherData.length === 0) {
        showError(container, 'No weather data available');
        return;
    }

    // Take only first 8 forecasts for API display
    const limitedData = weatherData.slice(0, 8);
    
    let html = `
        <div class="api-results-header">
            <h3>🌤️ Weather Forecast for ${city}</h3>
            <p>Showing ${limitedData.length} upcoming forecasts</p>
        </div>
        <div class="api-forecast-grid">
    `;

    limitedData.forEach(forecast => {
        html += `
            <div class="api-forecast-item">
                <div class="api-weather-icon">${forecast.icon}</div>
                <div class="api-forecast-info">
                    <div class="api-date">${formatDate(forecast.date)}</div>
                    <div class="api-temp">${forecast.temperature}°C</div>
                    <div class="api-condition">${forecast.weatherCondition}</div>
                    <div class="api-humidity">💧 ${forecast.humidity}%</div>
                </div>
            </div>
        `;
    });

    html += `
        </div>
        <div class="api-actions">
            <a href="/weather?city=${encodeURIComponent(city)}" class="view-full-btn">
                View Full Forecast →
            </a>
        </div>
    `;

    container.innerHTML = html;
}

function formatDate(dateString) {
    const date = new Date(dateString);
    const options = { 
        month: 'short', 
        day: 'numeric', 
        hour: '2-digit', 
        minute: '2-digit' 
    };
    return date.toLocaleDateString('en-US', options);
}

function showError(container, message) {
    container.style.display = 'block';
    container.innerHTML = `
        <div class="api-error">
            <span class="error-icon">⚠️</span>
            <span class="error-text">${message}</span>
        </div>
    `;
}

function showLoading(button) {
    if (!button) return;
    
    const originalText = button.innerHTML;
    button.dataset.originalText = originalText;
    button.innerHTML = '<span class="loading"></span> Loading...';
    button.disabled = true;
}

function hideLoading(button) {
    if (!button) return;
    
    const originalText = button.dataset.originalText;
    if (originalText) {
        button.innerHTML = originalText;
    }
    button.disabled = false;
}

// Add CSS for API results dynamically
const apiResultsCSS = `
    .api-results-header {
        text-align: center;
        margin-bottom: 20px;
        padding-bottom: 15px;
        border-bottom: 2px solid #e1e5e9;
    }

    .api-results-header h3 {
        color: #333;
        margin-bottom: 5px;
    }

    .api-results-header p {
        color: #666;
        font-size: 0.9rem;
    }

    .api-forecast-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
        gap: 15px;
        margin-bottom: 20px;
    }

    .api-forecast-item {
        background: white;
        border: 1px solid #e1e5e9;
        border-radius: 10px;
        padding: 15px;
        text-align: center;
        transition: transform 0.2s ease;
    }

    .api-forecast-item:hover {
        transform: translateY(-2px);
        box-shadow: 0 5px 15px rgba(0,0,0,0.1);
    }

    .api-weather-icon {
        font-size: 2rem;
        margin-bottom: 8px;
    }

    .api-date {
        font-size: 0.8rem;
        color: #666;
        margin-bottom: 5px;
    }

    .api-temp {
        font-size: 1.2rem;
        font-weight: 600;
        color: #ff6b6b;
        margin-bottom: 3px;
    }

    .api-condition {
        font-size: 0.9rem;
        color: #333;
        margin-bottom: 3px;
    }

    .api-humidity {
        font-size: 0.8rem;
        color: #4ecdc4;
    }

    .api-error {
        background: #fee;
        color: #c33;
        padding: 15px;
        border-radius: 8px;
        text-align: center;
        border: 1px solid #fcc;
    }

    .api-actions {
        text-align: center;
        padding-top: 15px;
        border-top: 1px solid #e1e5e9;
    }

    .view-full-btn {
        display: inline-block;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        text-decoration: none;
        padding: 12px 25px;
        border-radius: 25px;
        font-weight: 600;
        transition: transform 0.2s ease;
    }

    .view-full-btn:hover {
        transform: translateY(-2px);
    }

    .loading-message {
        text-align: center;
        padding: 20px;
        color: #666;
        font-size: 1.1rem;
    }
`;

// Inject the CSS
const style = document.createElement('style');
style.textContent = apiResultsCSS;
document.head.appendChild(style);
