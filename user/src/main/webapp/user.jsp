<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>User Dashboard</title>
  <link rel="stylesheet" href="page.css">
  
</head>
<body>
  <div class="container">
    <div class="header">
      <h1>User Dashboard</h1>
      <%
      String username=session.getAttribute("uname").toString();
      %>
       <p>Welcome, <strong><%= username %></strong></p>
    </div>

    <!-- Navigation Bar -->
    <div class="nav-bar">
      <a href="webapi/myresource/my_resources">My Resources</a>
      <a href="webapi/myresource/requestforresource">Request For Resource</a>
      <a href="webapi/myresource/checkapprovals">Check Approvals</a>
      <a href="webapi/myresource/requestforadminormanager">Request for Manager/Admin</a>
      <a href="webapi/myresource/removeownresource">Remove Own Resource</a>
      <a href="webapi/myresource/knowyourmanager">Know Your Manager</a>
      <a href="webapi/myresource/changepass">Change Password</a>
      
    </div>

    <form id="check-resources-form" action="http://localhost:8542/user/myresource.html" method="post" class="section">
  	<h2>Check Resources</h2>
  
  <!-- Add any input fields you need here -->
  <!-- Example input field -->
  <label for="resource-id">Resource ID:</label>
  <input type="text" id="resource-id" name="resource-id" required>
  
  <!-- Example submit button -->
  <button type="submit">Submit</button>
	</form>


    <!-- Request New Resource Section -->
		   <div id="request-new-resource" class="section">
    <h2>Request for Resource</h2>
    <form action="webapi/myresource/request" method="post">
      <div class="form-group">
        <label for="new-resource">Resource Name</label>
        <input type="text" id="new-resource" name="resource_name" placeholder="Enter Resource Name" required>
      </div>
      <div class="form-group">
        <input type="submit" value="Request Resource">
      </div>
    </form>
  </div>
    

    <!-- Check Approvals Section -->
    <div id="check-approvals"   class="section">
      <h2>Check Approvals</h2>
      <ul>
        <li>Approval 1</li>
        <li>Approval 2</li>
        <li>Approval 3</li>
      </ul>
    </div>

    <!-- Request for Manager/Admin Section -->
	    <div id="request-manager" class="section">
    		<h2>Request for Manager/Admin</h2>
    		
    		
  		</div>
    

    <!-- Remove Own Resource Section -->
    <div id="remove-own-resource" class="section">
      <h2>Remove Own Resource</h2>
      <form action="/user/remove-own-resource" method="post">
        <div class="form-group">
          <label for="resource-to-remove">Resource Name</label>
          <input type="text" id="resource-to-remove" name="resource_name" placeholder="Enter Resource Name" required>
        </div>
        <div class="form-group">
          <input type="submit" value="Remove Resource">
        </div>
      </form>
    </div>

    <!-- Logout Button -->
    <form >
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
