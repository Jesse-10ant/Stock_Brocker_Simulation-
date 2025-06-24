/**
 * 
 */
document.addEventListener("DOMContentLoaded", function() {
	console.log("Fetch User Stocks");
	fetchUserStocks();
});


function fetchUserStocks() {
	console.log("Fetch User Stocks");
	const user_ID = localStorage.getItem('userID');
	fetch('/H4/UserStocks', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({
			userID: user_ID
		})
	})
		.then(response => response.json())
		.then(data => {
			if (data.success && Array.isArray(data.stocks)) { 
				console.log(data);
				data.stocks.forEach(stock => {
					console.log(stock);
					
					createStockInfoBlock(stock);
				});
			} else {
				console.error('Failed to load stocks:', data.message);
			}
		})
		.catch(error => {
			console.error('ERROR: fetching stocks failed');
		});
}

function createStockInfoBlock(stock) {
	console.log("Create Stock Info Block");
	console.log(stock);


	const container = document.querySelector('.container');

	const stockDisplay = document.createElement('div');
	stockDisplay.className = 'entry';
	container.appendChild(stockDisplay);

	const stockHead = document.createElement('div');
	stockHead.className = 'stock-head';
	const stockTitle = document.createElement('h2');
	stockTitle.textContent = `${stock.ticker} - ${stock.name}`;
	stockHead.appendChild(stockTitle);
	stockDisplay.appendChild(stockTitle);

	const stockDetails = document.createElement('div');
	stockDetails.className = 'stock-details';
	const detailDisplay = document.createElement('p');
	detailDisplay.textContent = `Quantity: ${stock.quantity}`;
	stockDetails.appendChild(detailDisplay);


	const avgCostDisplay = document.createElement('p');
	avgCostDisplay.textContent = `Average Cost ${stock.avgCost}`;
	stockDisplay.appendChild(avgCostDisplay);

	const totalCost = document.createElement('p');
	totalCost.textContent = `Total Cost ${stock.totalCost}`;
	stockDetails.appendChild(totalCost);
	stockDisplay.appendChild(stockDetails);

	const trade_form = document.createElement('form');
	trade_form.className = 'trade-form';

	const input_ammount = document.createElement('input');
	input_ammount.type = 'number';
	input_ammount.min = '0';
	input_ammount.placeholder = 'Quantity';
	trade_form.appendChild(input_ammount);

	const buy_label = makeRadio('BUY', 'trade-option', 'buy');
	const sell_label = makeRadio('SELL', 'trade-option', 'sell');
	trade_form.appendChild(buy_label);
	trade_form.appendChild(sell_label);

	const tradeButton = document.createElement('button');
	tradeButton.type = 'submit';
	tradeButton.textContent = 'Submit';
	trade_form.appendChild(tradeButton);
	stockDisplay.appendChild(trade_form);

}
function fetchStockInfo(ticker) {
	console.log("Fetch Stock Info");

	const quoteAPI = "https://finnhub.io/api/v1/quote?symbol=" + ticker + "&token=cntp4mpr01qt3uhjkbh0cntp4mpr01qt3uhjkbhg"

	fetch(quoteAPI)
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
		})
}

function makeRadio(val, name, id) {
	console.log("Make Radio");

	const label = document.createElement('label');
	const radio = document.createElement('input');
	radio.type = 'radio';
	radio.id = id;
	radio.name = name;
	radio.value = val;
	label.appendChild(radio);
	label.appendChild(document.createTextNode(val));
	return label;
}