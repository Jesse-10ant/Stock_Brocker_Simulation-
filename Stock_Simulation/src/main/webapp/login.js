document.addEventListener("DOMContentLoaded", function() {
	const loginForm = document.getElementById('login-form');
	const signupForm = document.getElementById('signup-form');
	updateNavigationBar();
	//Logic for handling logging in 
	loginForm.addEventListener('submit', function(event) {
		event.preventDefault();
		const username = document.getElementById('login-username').value;
		const password = document.getElementById('login-password').value;
		const loginData = { username: username, password: password };

		fetch('/H4/Login', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify(loginData)
		})
			.then(response => response.json())
			.then(data => {
				if (data.success) {
				localStorage.setItem('userID', data.userID);
                console.log('Logged in userID:', data.userID);
                window.location.href = 'index.html';
				} else {
					alert('Failed to login: ' + data.message);
				}
			})
			.catch(error => {
				console.error('Error:', error);
				alert('Failed to send login data');
			});
	});


	//Logic for handling registration
	signupForm.addEventListener('submit', function(event) {
		event.preventDefault();
		const email = document.getElementById('signup-email').value;
		const username = document.getElementById('signup-username').value;
		const password = document.getElementById('signup-password').value;
		const confirmPassword = document.getElementById('confirm-password').value;

		if (password !== confirmPassword) {
			alert("Passwords do not match!");
			return;
		}

		const signupData = {
			email: email,
			username: username,
			password: password
		};

		fetch('/H4/Register', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify(signupData)
		})
			.then(response => response.json())
			.then(data => {
				if (data.success) {
					 localStorage.setItem('userID', data.userID);
                console.log('Logged in userID:', data.userID);
                window.location.href = 'index.html';
					alert('Registration successful');
					window.location.href = 'index.html';
				} else {
					alert('Failed to register: ' + data.message);
				}
			})
			.catch(error => {
				console.error('Error:', error);
				alert('Failed to send registration data');
			});
	});
});


function logoutUser() {
	localStorage.removeItem('userID');
	console.log('Logged out:', !localStorage.getItem('userID'));
	window.location.href = 'index.html';
}

function isUserLoggedIn() {
	const userName = localStorage.getItem('userID');
	if(userName != null){
		console.log("help");
		return true;
	}else{
		return false;
	}
}

function updateNavigationBar() {
	const navBarLinks = document.querySelector('.navbar-links');
	if (isUserLoggedIn()) {
		navBarLinks.innerHTML = `
      <a href="index.html">Home</a>
      <a href="index.html">Search</a>
      <a href="portfolio.html">Portfolio</a>
      <a href="#" id="logout">Log Out</a>
    `;
		const logoutLink = document.getElementById('logout');
        if(logoutLink) {
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