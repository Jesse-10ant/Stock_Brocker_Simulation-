document.addEventListener("DOMContentLoaded", updateNavigationBar);


document.addEventListener('DOMContentLoaded', () => {
	const searchButton = document.getElementById('search-btn');
	const buyButton = document.getElementById('buy-button');
	searchButton.addEventListener('click', (event) => {
		event.preventDefault();
		const tick = document.getElementById('search-input').value.trim().toUpperCase();
		updateNavigationBar();

		if (!tick) {
			alert('Please enter a stock ticker.');
			return;
		}
		fetchCompanyProfile(tick);
		fetchStockQuote(tick);
	});

	buyButton.addEventListener('click', (event) => {
		event.preventDefault();
		fetchMakeTrade();
	});
});

function logoutUser() {
	localStorage.removeItem('userID');
	console.log('Logged out:', !localStorage.getItem('userID'));
	window.location.href = 'index.html';
}

function isUserLoggedIn() {
	console.log("help");
	console.log("localStorage.getItem : ", localStorage.getItem('userID'));
	const userName = localStorage.getItem('userID');
	if (userName != null) {
		return true;
	} else {
		return false;
	}
}
function updateNavigationBar() {
	const navBarLinks = document.querySelector('.navbar-links');
	console.log("in nav bar update");
	if (isUserLoggedIn()) {
		navBarLinks.innerHTML = `
      <a href="index.html">Home</a>
      <a href="index.html">Search</a>
      <a href="portfolio.html">Portfolio</a>
      <a href="#" id="logout">Log Out</a>
    `;
		const logoutLink = document.getElementById('logout');
		if (logoutLink) {
			logoutLink.addEventListener('click', function(event) {
				event.preventDefault();
				logoutUser();
			});
		}
	} else {
		navBarLinks.innerHTML = `
      <a href="index.html">Home</a>
      <a href="search.html">Search</a>
      <a href="login.html">Login</a>
      <a href="signup.html">Sign Up</a>
    `;
	}
}

function fetchCompanyProfile(tick) {
	const API = "https://finnhub.io/api/v1/stock/profile2?symbol=" + tick + "&token=cntp4mpr01qt3uhjkbh0cntp4mpr01qt3uhjkbhg"
	fetch(API)
		.then(response => {
			if (!response.ok) {
				throw new Error(`HTTP error! status: ${response.status}`);
			}
			return response.json();
		})
		.then(data => {
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

function fetchStockQuote(tick) {
	const quoteAPI = "https://finnhub.io/api/v1/quote?symbol=" + tick + "&token=cntp4mpr01qt3uhjkbh0cntp4mpr01qt3uhjkbhg"

	fetch(quoteAPI)
		.then(response => response.json())
		.then(quoteData => {
			updateStockQuote(quoteData);
			if (isUserLoggedIn()) {
				updateStockMarketDetails(quoteData);
				document.getElementById('summary').textContent = "Summary";

				const flexContainerLogged = document.querySelector('.flex-container-logged-in');
				if (flexContainerLogged) {
					flexContainerLogged.style.display = 'flex';
				}
			}
		})
		.catch(error => {
			console.error('Fetch error:', error);
		});
	const flexContainer = document.querySelector('.flex-container');
	if (flexContainer) {
		flexContainer.style.display = 'block';
	}
	const searchContainer = document.querySelector('.search-container');
	if (searchContainer) {
		searchContainer.style.display = 'none';
	}
}

function updateCompanyInfo(data) {
	const companyInfo = document.getElementById('company');

	if (!companyInfo) {
		console.error('The "company" div cannot be found in the document.');
		return;
	}
	updateCompanySummary(data);
	companyInfo.innerHTML = `
        <p>IPO Date: ${data.ipo}</p>
        <p>Market Cap: ${data.marketCapitalization}</p>
        <p>Shares Outstanding: ${data.shareOutstanding}</p>
        <p>Website: ${data.weburl}</a></p>
        <p>Phone: ${data.phone}</p>
    `;
}

function updateStockQuote(quote) {
	const stockQuoteDiv = document.getElementById('quote');
	stockQuoteDiv.innerHTML = `
        <p>High Price: ${quote.h}</p>
        <p>Low Price: ${quote.l}</p>
        <p>Open Price: ${quote.o}</p>
        <p>Close Price: ${quote.pc}</p>
    `;
}

function updateCompanySummary(data) {

	if (!isUserLoggedIn()) {
		const stockSummaryDiv = document.getElementById('title');
		stockSummaryDiv.innerHTML = `
        <p>${data.ticker}</p>
        <p>${data.name}</p>
        <p>${data.exchange}</p>
    `;
	} else {
		updateStockMarketInformation(data);
	}
}
//Thing with the price indicator 
function updateStockMarketDetails(quote) {
	const tradeFloor = document.querySelector('.flex-container');
	tradeFloor.style.display = 'flex';
	document.getElementById('stock-price').textContent = `${quote.l}`;
	document.getElementById('stock-date-time').textContent = `${new Date().toLocaleString()}`;
	var time = new Date();
	if (time.getHours() < 13 && (time.getHours() > 6)) {
		document.getElementById('market-status').textContent = "Market is Open";
		localStorage.setItem('marketOpen', open);

	} else {
		document.getElementById('market-status').textContent = "Market is Closed";
		localStorage.removeItem('marketOpen')
	}

	const stockChangeIcon = document.getElementById('stock-change-icon');
	const price = document.getElementById('stock-price');
	const date = document.getElementById('stock-date-time');
	const change = document.getElementById('stock-change');

	if (quote.d >= 0) {
		stockChangeIcon.className = 'fa-solid fa-caret-up';
		stockChangeIcon.style.color = 'green';
		price.style.color = 'green';
		date.style.color = 'green;';
		change.style.color = 'green';
		stockChangeIcon.nextElementSibling.textContent = ` ${quote.d} (${quote.dp}%)`;
	} else {
		stockChangeIcon.className = 'fa-solid fa-caret-down';
		stockChangeIcon.nextElementSibling.textContent = ` ${quote.d} (${quote.dp}%)`;
		stockChangeIcon.style.color = 'red';
		price.style.color = 'red';
		date.style.color = 'red;'
		change.style.color = 'red';


	}
}

function fetchMakeTrade() {
	const now = new Date();
	const marketOpenTime = 6;
	const marketCloseTime = 13;
	const isWeekday = now.getDay() !== 0 && now.getDay() !== 6;
		const ticker = document.getElementById('stock-symbol').textContent;
		const price = document.getElementById('stock-price').textContent;
		const userID = localStorage.getItem('userID');
		const quantityInput = document.getElementById('stock-quantity');
		const quantity = parseInt(quantityInput.value, 10);
		if(quantity < 1)
		{
			alert('Please enter valid ammount');
		}
	if (isWeekday && now.getHours() >= marketOpenTime && now.getHours() < marketCloseTime) {
		fetch('/H4/Trade', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({
				ticker: ticker,
				price: price,
				quantity: quantity,
				userID: userID
			})
		})
			.then(response => response.json())
			.then(data => {
				if (data.success) {
					alert('SUCCESS: Executed purchase of ' + quantity + " shares of " + ticker + " for " + (price * quantity));
				} else {
					alert('Trade not possible');
				}
			})
			.catch(error => {
				console.error('Error:', error);
				alert('Failed to send trade data');
			});
	} else {
		alert('The market is currently closed. Please try again during market hours.');
	}
}

function updateStockMarketInformation(data) {
	document.getElementById('stock-symbol').textContent = `${data.ticker}`;
	document.getElementById('stock-company-name').textContent = `${data.name}`;
	document.getElementById('stock-exchange').textContent = `${data.exchange}`;
}


