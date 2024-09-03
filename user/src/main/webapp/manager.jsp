<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Manager Dashboard</title>
  <style>
/* General Styles */
body { 
  font-family: Arial, sans-serif; 
  margin: 0; 
  padding: 0; 
  background-color: #f4f7f9; 
  color: #333; 
}

.container { 
  max-width: 1300px; 
  margin: 30px auto; 
  background: #fff; 
  padding: 20px; 
  border-radius: 8px; 
  box-shadow: 0 4px 8px rgba(0,0,0,0.1); 
}

/* Header Styles */
.header { 
  margin-bottom: 20px; 
  text-align: center; 
  padding-bottom: 20px;
  border-bottom: 2px solid #007BFF; /* Adds a blue border below the header */
}

.header h1 { 
  font-size: 28px; 
  color: #007BFF; 
}

.header p {
  font-size: 18px;
  color: #333;
}

/* Navigation Bar Styles */
.nav-bar {
  margin-bottom: 20px;
  text-align: center;
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
}

.nav-bar a {
  margin: 0 15px;
  font-size: 18px;
  color: #007BFF;
  text-decoration: none;
  padding: 10px;
  border-radius: 6px;
  transition: background-color 0.3s, color 0.3s;
}

.nav-bar a:hover {
  background-color: #007BFF;
  color: #fff;
}

/* Section Styles */
.section {
  display: none; /* Hide all sections initially */
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 6px;
  margin-bottom: 20px;
}

.section.active {
  display: block; /* Show the active section */
}

.section h2 {
  font-size: 24px; 
  margin-bottom: 10px; 
  color: #007BFF; 
}

/* Form Group Styles */
.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-size: 16px;
  margin-bottom: 5px;
  color: #333;
}

.form-group input, .form-group select {
  width: 100%;
  padding: 10px;
  font-size: 16px;
  border: 1px solid #ddd;
  border-radius: 6px;
  box-sizing: border-box;
}

.form-group input[type="submit"] {
  background-color: #28a745;
  color: #fff;
  border: none;
  cursor: pointer;
  transition: background-color 0.3s;
}

.form-group input[type="submit"]:hover {
  background-color: #218838;
}

/* Button Styles */
.button {
  display: inline-block;
  padding: 12px 24px;
  font-size: 16px;
  color: #fff;
  background-color: #007BFF;
  border: none;
  border-radius: 6px;
  text-decoration: none;
  text-align: center;
  margin: 10px 0;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.2s;
}

.button:hover { 
  background-color: #0056b3; 
  transform: scale(1.05);
}

/* Table Styles */
.table {
  width: 100%;
  border-collapse: collapse;
}

.table th, .table td {
  padding: 12px;
  border: 1px solid #ddd;
  text-align: left;
}

.table th {
  background-color: #007BFF;
  color: #fff;
}

.table tr:nth-child(even) {
  background-color: #f2f2f2;
}

.table a {
  color: #007BFF;
  text-decoration: none;
  padding: 5px;
}

.table a:hover {
  text-decoration: underline;
}

/* Logout Button Styles */
.logout-button {
  display: block;
  margin: 20px auto;
  padding: 12px 24px;
  font-size: 16px;
  color: #fff;
  background-color: #dc3545;
  border: none;
  border-radius: 6px;
  text-align: center;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.2s;
}

.logout-button:hover { 
  background-color: #c82333; 
  transform: scale(1.05);
}

/* Section Visibility Based on Hash in URL */
:target {
  display: block;
}
</style>
  
</head>
<body>
  <div class="container">
    <div class="header">
      <h1>Manager Dashboard</h1>
      <%
      String username=session.getAttribute("uname").toString();
      %>
       <p>Welcome, <strong><%= username %></strong></p>
    </div>

    <!-- Navigation Bar -->
    <div class="nav-bar">
      <a href="webapi/myresource/showteam">Show Team</a>
      <a href="webapi/myresource/getteammember">Get a Team Member</a>
      <a href="webapi/myresource/requestforadmininmanager">Request for Admin</a>
      <a href="webapi/myresource/managerresources">Check Resources</a>
      <a href="webapi/myresource/requestformanagerresource">Request For Resource</a>
      <a href="webapi/myresource/checkmanagerapprovals">Check Approvals</a>
      <a href="webapi/myresource/removemanagerresource">Remove Own Resource</a>
      <a href="webapi/myresource/managerattestation">Remove resource from team member</a>
      <a href="webapi/myresource/changepass">Change Password</a>
    </div>

    
    
    
    
    
    
    
    

    <!-- Logout Button -->
    <form>
      <button type="submit" class="logout-button" id="logout-button">Logout</button>
    </form>
  </div>
  <script>
  // Function to fetch and display resources

  // Function to get query parameter from URL
  function getQueryParam(name) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(name);
  }
  document.getElementById('logout-button').addEventListener('click', function() {
      // Optionally open the login page in a new tab
      window.close();
      window.open('index.jsp', '_blank');
      // Close the current window/tab
  });
  // Set username from query parameter
  document.getElementById('username').textContent = getQueryParam('username');
</script>
</body>
</html>
