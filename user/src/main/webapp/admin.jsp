<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Admin Dashboard</title>
  <link rel="stylesheet" href="page.css">
  <style>
    /* Add some basic styling for the file upload section */
    .file-upload-section {
      display: none;
      margin-top: 20px;
    }
    .file-upload-section input[type="file"] {
      display: block;
      margin-bottom: 10px;
    }
    .file-upload-section .message {
      color: red;
      font-weight: bold;
    }
    .nav-bar a {
      margin-right: 15px;
      text-decoration: none;
    }
    .logout-button {
      background-color: #f44336;
      color: #fff;
      border: none;
      padding: 10px 20px;
      cursor: pointer;
    }
    .logout-button:hover {
      background-color: #c62828;
    }
  </style>
</head>
<body>
  <div class="container">
    <div class="header">
      <h1>Admin Dashboard</h1>
      <% String username = session.getAttribute("uname") != null ? session.getAttribute("uname").toString() : "Guest"; %>
      <p>Welcome, <strong><%= username %></strong></p>
    </div>

    <!-- Navigation Bar -->
    <div class="nav-bar">
      <a href="webapi/myresource/check_requests">Check Requests</a>
      <a href="webapi/myresource/requestlist">Request List</a>
      <a href="#add-resource">Add Resource</a>
      <a href="webapi/myresource/listofresources">List Of Resources</a>
      <a href="webapi/myresource/remove_resource">Remove Resource</a>
      <a href="webapi/myresource/listofmanagers">List Of Managers</a>
      <a href="webapi/myresource/listofusers">List Of Users</a>   
      <a href="webapi/myresource/adduser">Add User</a>
      <a href="webapi/myresource/removeuser">Remove User</a>
      <a href="webapi/myresource/removeresourcefromuser">Remove Resource from User</a>
      <a href="webapi/myresource/checkuserforresource">Check Resources for User</a>
      <a href="webapi/myresource/checkusersforresource">Check Users for Resource</a>
      <a href="#file-upload-section" id="load-csv-link">Load CSV</a>
      <a href="webapi/myresource/changepass">Change Password</a>
      
    </div>

    <!-- Add Resource Section -->
    <div id="add-resource" class="section">
      <h2>Add Resource</h2>
      <form id="add-resource-form" action="webapi/myresource/add_resource" method="post">
        <div class="form-group">
          <label for="resource-name">Resource Name</label>
          <input type="text" id="resource-name" name="resource_name" required>
        </div>
        <div class="form-group">
          <input type="submit" value="Add Resource">
        </div>
        <div id="messageContainer" class="message"></div>
        <div id="add-message" class="message"></div>
      </form>
    </div>

   <div id="file-upload-section" class="file-upload-section">
      <h2>Upload File</h2>
      <form id="file-upload-form">
        <div class="form-group">
          <input type="file" id="csv-file" name="csv_file" accept=".csv, .txt" required>
        </div>
        <div class="form-group">
          <input type="submit" value="Upload file">
        </div>
        <div id="file-upload-message" class="message"></div>
      </form>
    </div>
    <!-- Logout Button -->
    <form>
      <button type="submit" class="logout-button" id="logout-button">Logout</button>
    </form>
    
    <script>
      // Handle "Load CSV" link click
      document.getElementById('load-csv-link').addEventListener('click', function(event) {
        event.preventDefault();
        const uploadSection = document.getElementById('file-upload-section');
        uploadSection.style.display = uploadSection.style.display === 'none' || uploadSection.style.display === '' ? 'block' : 'none';
      });

      // Handle file upload form submission
      document.getElementById('file-upload-form').addEventListener('submit', function(event) {
        event.preventDefault(); // Prevent form submission

        const fileInput = document.getElementById('csv-file');
        const file = fileInput.files[0];
        const messageContainer = document.getElementById('file-upload-message');

        if (!file) {
          messageContainer.textContent = 'Please select a file.';
          return;
        }

        if (file.type !== 'text/plain' && file.type !== 'text/csv') {
          messageContainer.textContent = 'Please upload a valid file (CSV or text).';
          return;
        }

        const reader = new FileReader();
        reader.onload = function(e) {
          const fileContent = e.target.result;

          fetch('webapi/myresource/uploadcsv', {
            method: 'POST',
            headers: {
              'Content-Type': 'text/plain'
            },
            body: fileContent
          })
          .then(response => response.text())
          .then(data => {
            messageContainer.textContent = 'File uploaded successfully';
          })
          .catch(error => {
            messageContainer.textContent = 'Error uploading file: ' + error.message;
          });
        };

        reader.readAsText(file);
      });

      // Logout button click
      document.getElementById('logout-button').addEventListener('click', function() {
        // Optionally open the login page in a new tab
        window.close();
        window.open('index.jsp', '_blank');
        // Close the current window/tab
      });
    </script>
  </div>
</body>
</html>
