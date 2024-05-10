document.addEventListener('DOMContentLoaded', () => {
    const searchButton = document.getElementById('search-btn');
    searchButton.addEventListener('click', () => {
        const ticker = document.getElementById('search-input').value.trim().toUpperCase();
        if (!ticker) {
            alert('Please enter a stock ticker.');
            return;
        }
        fetchCompanyProfile(ticker);
        fetchStockQuote(ticker);
    });
});

function fetchCompanyProfile(tick) {
    const API = "https://finnhub.io/api/v1/stock/profile2?symbol=" + tick + "&token=cntp4mpr01qt3uhjkbh0cntp4mpr01qt3uhjkbhg"

   fetch(API)
        .then(response => {
            console.log(response); // Log the response to see what you are getting
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log(data); // Log the data to inspect the object
            if (data.error) {
                throw new Error(`API error! message: ${data.error}`);
            }
            updateCompanyInfo(data);
        })
        .catch(error => {
            console.error('Fetch error:', error);
            document.getElementById('stockInfo').textContent = 'Error loading company data: ' + error.message;
        });
}

function updateCompanyInfo(data) {
    // Ensure this ID matches the one in your HTML.
    const companyInfo = document.getElementById('stockInfo'); 
    
    // If stockInfoDiv is null, log an error message and return early.
    if (!companyInfo) {
        console.error('The "stockInfo" div cannot be found in the document.');
        return;
    }

    companyInfo.innerHTML = `
        <h2>${data.ticker} (${data.name})</h2>
        <p>Exchange: ${data.exchange}</p>
        <p>IPO Date: ${data.ipo}</p>
        <p>Market Cap: ${data.marketCapitalization}</p>
        <p>Shares Outstanding: ${data.shareOutstanding}</p>
        <p>Website: <a href="${data.weburl}" target="_blank">${data.weburl}</a></p>
        <p>Phone: ${data.phone}</p>
    `;
      const searchContainer = document.querySelector('.search-container');
    if (searchContainer) {
        searchContainer.style.display = 'none';
    }
}

function fetchStockQuote(ticker) {
    const quoteAPI = "https://finnhub.io/api/v1/quote?symbol=" + ticker + "&token=cntp4mpr01qt3uhjkbh0cntp4mpr01qt3uhjkbhg"

    fetch(quoteAPI)
        .then(response => response.json())
        .then(quoteData => {
            updateStockQuote(ticker, quoteData);
        })
        .catch(error => {
            console.error('Fetch error:', error);
            document.getElementById('stockQuote').textContent = 'Error loading stock quote.';
        });
}

function updateStockQuote(ticker, quote) {
    const stockQuoteDiv = document.getElementById('stockQuote');
    stockQuoteDiv.innerHTML = `
        <h3>${ticker} - Summary</h3>
        <p>High Price: ${quote.h}</p>
        <p>Low Price: ${quote.l}</p>
        <p>Open Price: ${quote.o}</p>
        <p>Close Price: ${quote.pc}</p>
    `;
      const searchContainer = document.querySelector('.search-container');
    if (searchContainer) {
        searchContainer.style.display = 'none';
    }
}
