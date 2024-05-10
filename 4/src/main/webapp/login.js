document.addEventListener("DOMContentLoaded", function() {
    const loginForm = document.getElementById('login-form');
    const signupForm = document.getElementById('signup-form');

    loginForm.addEventListener('submit', function(event) {
        event.preventDefault();
        const username = document.getElementById('login-username').value;
        const password = document.getElementById('login-password').value;

        const loginData = { username: username, password: password };
        
        fetch('/Assignment4/Login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(loginData)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
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
        fetch('/Assignment4/Register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(signupData)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
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