<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login Page</title>
    <link rel="stylesheet" href="index.css">
    <style>
        .error-message {
            color: red;
            font-size: 0.875rem;
            margin-top: 0.5rem;
        }
        .forgot-password-link {
            display: block;
            margin-top: 1rem;
            color: #007bff;
            text-decoration: none;
        }
        .forgot-password-link:hover {
            text-decoration: underline;
        }
    </style>
    <script>
        async function login(event) {
            event.preventDefault(); // Prevent default form submission

            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;

            try {
                const response = await fetch('webapi/myresource/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: new URLSearchParams({
                        'username': username,
                        'password': password
                    })
                });

                if (response.redirected) {
                    window.location.href = response.url;
                } else {
                    const message = await response.text();
                    showMessage(message);
                }
            } catch (error) {
                showMessage('Unexpected error occurred.');
            }
        }

        function showMessage(message) {
            const messageElement = document.getElementById('message');
            messageElement.textContent = message;
        }
        
    </script>
</head>
<body>
    <div class="background">
        <div class="container">
            <div class="form-container">
                <h1>Login</h1>
                <form id="loginForm" onsubmit="login(event)">
                    <div class="input-group">
                        <label for="username">Username</label>
                        <input type="text" id="username" name="username" required>
                    </div>
                    <div class="input-group">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" required>
                    </div>
                    <button type="submit">Login</button>
                    <div id="message" class="error-message"></div> <!-- Message area -->
                    <a href="forgetpassword.html" class="forgot-password-link">Forgot Password?</a>
                </form>
                <div class="new-user">
                    <center>
                        <p>Don't have an account?</p>
                        <a href="register.html" class="register-link">Create a new account</a>
                    </center>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
