<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Dashboard</title>
    <link rel="stylesheet" href="page.css">
    <style>
        /* Basic styling */
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f4f4f4;
        }
        .container {
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
            background: white;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        h1 {
            text-align: center;
            color: #333;
        }
        .section {
            display: none;
            margin-top: 20px;
        }
        .section.active {
            display: block;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input[type="password"] {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .error {
            color: red;
            font-size: 0.9em;
            margin-top: 5px;
        }
        button {
            width: 100%;
            padding: 10px;
            background-color: #007BFF;
            border: none;
            border-radius: 4px;
            color: white;
            font-size: 16px;
            cursor: pointer;
        }
        button:hover {
            background-color: #0056b3;
        }
        .password-constraints {
            margin-top: 10px;
            font-size: 0.9em;
            color: #555;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>User Dashboard</h1>
            <%
                String username = (String) session.getAttribute("uname");
                if (username == null) {
                    username = "Guest";
                }
            %>
            <p>Welcome, <strong><%= username %></strong></p>
        </div>
		<h1>Please change the password and login again.</h1>
        <!-- Navigation Bar -->
        <div class="nav-bar">
            <a href="#" id="change-password-link">Change Password</a>
        </div>

        <!-- Change Password Section -->
        <div id="change-password-section" class="section">
            <h2>Change Password</h2>
            <form action="webapi/myresource/changepassfornewuser" method="post">
                <div class="form-group">
                    <label for="current-password">Current Password</label>
                    <input type="password" id="current-password" name="current_password" required>
                </div>
                <div class="form-group">
                    <label for="new-password">New Password</label>
                    <input 
                        type="password" 
                        id="new-password" 
                        name="new_password" 
                        pattern="(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}" 
                        title="Password must be at least 8 characters long, and include at least one uppercase letter, one lowercase letter, one number, and one special character (@, $, !, %, *, ?, &)." 
                        required
                    >
                    <div id="new-password-error" class="error"></div>
                </div>
                <div class="form-group">
                    <label for="confirm-password">Confirm New Password</label>
                    <input 
                        type="password" 
                        id="confirm-password" 
                        name="confirm_password" 
                        pattern="(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}" 
                        title="Password must be at least 8 characters long, and include at least one uppercase letter, one lowercase letter, one number, and one special character (@, $, !, %, *, ?, &)." 
                        required
                    >
                    <div id="confirm-password-error" class="error"></div>
                </div>
                <button type="submit">Change Password</button>
            </form>
            <div class="password-constraints">
                <h3>Password Constraints:</h3>
                <ul>
                    <li>At least 8 characters long</li>
                    <li>At least one uppercase letter</li>
                    <li>At least one lowercase letter</li>
                    <li>At least one number</li>
                    <li>At least one special character (e.g., @, $, !, %, *, ?, &)</li>
                </ul>
            </div>
        </div>

        <!-- Logout Button -->
        <form action="webapi/myresource/logout" method="post">
            <button type="submit" class="logout-button">Logout</button>
        </form>
    </div>

    <script>
        // Function to show the Change Password section
        document.getElementById('change-password-link').addEventListener('click', function(event) {
            event.preventDefault();
            document.getElementById('change-password-section').classList.toggle('active');
        });
    </script>
</body>
</html>