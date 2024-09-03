package UAM.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Resource {
    
    private String resourceName;
    private int noOfUsers;

    public Resource(String resourceName, int noOfUsers) {
        //this.id = id;
        this.resourceName = resourceName;
        this.noOfUsers = noOfUsers;
    }
    public Resource(String resourceName) {
        //this.id = id;
        this.resourceName = resourceName;
    }

    public Resource() {
		
	}
	public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public int getNoOfUsers() {
        return noOfUsers;
    }

    public void setNoOfUsers(int noOfUsers) {
        this.noOfUsers = noOfUsers;
    }
    
    
    ////////////////////admin///////////////////////////
    
    
    public String addResource(String resourceName) throws Exception {
        try (Connection connection = ConnectDb.connectdb()) {
            // Query to check if the resource already exists
            String checkQuery = "SELECT resource_name FROM resource WHERE resource_name = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setString(1, resourceName);
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Resource already exists
                        return "<p style='color: red;'>Resource already exists</p>"
                               + "<form action='http://localhost:8542/user/admin.jsp?#add-resource' method='get'>"
                               + "<button type='submit'>Go back</button>"
                               + "</form>";
                    }
                }
            }

            // Query to insert the new resource
            String insertQuery = "INSERT INTO resource (resource_name) VALUES (?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setString(1, resourceName);
                insertStatement.executeUpdate();
            }
        }
        
        // Success message
        return "<p style='color: green;'>Resource added successfully</p>"
               + "<form action='http://localhost:8542/user/admin.jsp?#add-resource' method='get'>"
               + "<button type='submit'>Go back</button>"
               + "</form>";
    }
    
    
    public String listOfResource() throws ClassNotFoundException, SQLException {
        // Initialize a StringBuilder to construct the HTML content
        StringBuilder resourcesHtml = new StringBuilder();
        
        // Append the HTML header and CSS styles for the table
        resourcesHtml.append("<!DOCTYPE html><html><head>")
                     .append("<style>")
                     .append("table { width: 100%; border-collapse: collapse; margin: 20px 0; }")
                     .append("th, td { padding: 12px; text-align: left; border: 1px solid #ddd; }")
                     .append("th { background-color: #f4f4f4; }")
                     .append("</style>")
                     .append("</head><body>")
                     .append("<h2>List Of Resources: ").append("</h2>")
                     .append("<table>")
                     .append("<tr><th>Resource Name</th></tr>");
        
        // Establish a connection to the database
        try (Connection connection = ConnectDb.connectdb();
             // Prepare a statement to retrieve resource names
             PreparedStatement pst = connection.prepareStatement("SELECT resource_name FROM resource")) {
            
            // Execute the query and obtain the result set
            try (ResultSet rs = pst.executeQuery()) {
                boolean hasResources = false; // Flag to check if any resources are found
                
                // Iterate through the result set
                while (rs.next()) {
                    hasResources = true; // Set flag to true if resources are found
                    String resourceName = rs.getString("resource_name"); // Get resource name
                    // Append the resource name to the HTML table
                    resourcesHtml.append("<tr><td>").append(resourceName).append("</td></tr>");
                }
                
                // If no resources are found, append a message indicating so
                if (!hasResources) {
                    resourcesHtml.append("<tr><td>No Resources Found</td></tr>");
                }
            }
        }
        
        // Append the closing tags for the HTML table and page
        resourcesHtml.append("</table>")
                     .append("</body></html>");
        
        // Return the constructed HTML string
        return resourcesHtml.toString();
    }
    
    
    public String listOfManagers() throws ClassNotFoundException, SQLException {
        // Initialize a StringBuilder to construct the HTML content
        StringBuilder resourcesHtml = new StringBuilder();
        
        // Append the HTML header and CSS styles for the table
        resourcesHtml.append("<!DOCTYPE html><html><head>")
                     .append("<style>")
                     .append("table { width: 100%; border-collapse: collapse; margin: 20px 0; }")
                     .append("th, td { padding: 12px; text-align: left; border: 1px solid #ddd; }")
                     .append("th { background-color: #f4f4f4; }")
                     .append("</style>")
                     .append("</head><body>")
                     .append("<h2>List Of Managers: ").append("</h2>")
                     .append("<table>")
                     .append("<tr><th>Manager Name</th></tr>");
        
        // Establish a connection to the database
        try (Connection connection = ConnectDb.connectdb();
             // Prepare a statement to retrieve manager names from user_table
             PreparedStatement pst = connection.prepareStatement("SELECT user_name FROM user_table WHERE user_type='Manager'")) {
            
            // Execute the query and obtain the result set
            try (ResultSet rs = pst.executeQuery()) {
                boolean hasManagers = false; // Flag to check if any managers are found
                
                // Iterate through the result set
                while (rs.next()) {
                    hasManagers = true; // Set flag to true if managers are found
                    String managerName = rs.getString("user_name"); // Get manager name
                    // Append the manager name to the HTML table
                    resourcesHtml.append("<tr><td>").append(managerName).append("</td></tr>");
                }
                
                // If no managers are found, append a message indicating so
                if (!hasManagers) {
                    resourcesHtml.append("<tr><td>No Managers Found</td></tr>");
                }
            }
        }
        
        // Append the closing tags for the HTML table and page
        resourcesHtml.append("</table>")
                     .append("</body></html>");
        
        // Return the constructed HTML string
        return resourcesHtml.toString();
    }
    
    
    
    public String RemoveResource() throws Exception {
        // Initialize a StringBuilder to construct the HTML content
        StringBuilder dropdownHtml = new StringBuilder();
        
        // Append the HTML header and CSS styles for the form and dropdown
        dropdownHtml.append("<!DOCTYPE html><html><head>")
                    .append("<style>")
                    .append("form { margin: 20px; }")
                    .append("label { font-weight: bold; }")
                    .append("select { padding: 8px; margin-right: 10px; }")
                    .append("button { background-color: #4CAF50; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; }")
                    .append("button:hover { background-color: #45a049; }")
                    .append("</style>")
                    .append("</head><body>")
                    .append("<h2>Select from dropdown to remove Resource</h2>")
                    .append("<form action='removeresourceaction' method='post'>")
                    .append("<label for='dropdown'>Select resource name to delete:</label>")
                    .append("<select id='dropdown' name='options' required>")
                    .append("<option value='' disabled selected >Select Resource</option>");
        
        // Establish a connection to the database
        try (Connection connection = ConnectDb.connectdb();
             // Prepare a statement to retrieve resource names from the resource table
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT resource_name FROM resource");
             // Execute the query and obtain the result set
             ResultSet resultSet = preparedStatement.executeQuery()) {
            
            boolean hasResources = false; // Flag to check if any resources are found
            
            // Iterate through the result set
            while (resultSet.next()) {
                hasResources = true; // Set flag to true if resources are found
                String resourceName = resultSet.getString("resource_name"); // Get resource name
                // Append each resource name as an option in the dropdown
                dropdownHtml.append("<option value='").append(resourceName).append("'>").append(resourceName).append("</option>");
            }
            
            // If no resources are found, append a message indicating so
            if (!hasResources) {
                dropdownHtml.append("<option value='' disabled selected>No resources available</option>");
            }
        }
        
        // Append the closing tags for the form and HTML page
        dropdownHtml.append("</select>")
                    .append("<button type='submit'>Submit</button>")
                    .append("</form>")
                    .append("</body></html>");
        
        // Return the constructed HTML string
        return dropdownHtml.toString();
    }
    
    
    public String removeResourceAction(String resourceName) throws Exception {
        // Establish a connection to the database
        try (Connection connection = ConnectDb.connectdb()) {
            // SQL queries to delete resource and related data
            String deleteQuery1 = "DELETE FROM resource WHERE resource_name = ?";
            String deleteQuery2 = "DELETE FROM user_resources WHERE resource_name = ?";
            String deleteQuery3 = "DELETE FROM requests WHERE request_type = ?";

            // Prepare the statements for deletion
            try (PreparedStatement deleteStatement1 = connection.prepareStatement(deleteQuery1);
                 PreparedStatement deleteStatement2 = connection.prepareStatement(deleteQuery2);
                 PreparedStatement deleteStatement3 = connection.prepareStatement(deleteQuery3)) {

                // Set the resource name parameter for the first delete query
                deleteStatement1.setString(1, resourceName);
                
                // Execute the deletion of the resource
                int rowsAffected = deleteStatement1.executeUpdate();
                
                if (rowsAffected > 0) {
                    // If resource deletion was successful, proceed to delete related entries
                    
                    // Set the resource name parameter for the second delete query
                    deleteStatement2.setString(1, resourceName);
                    deleteStatement2.executeUpdate();
                    
                    // Set the resource name parameter for the third delete query
                    deleteStatement3.setString(1, resourceName);
                    deleteStatement3.executeUpdate();
                    
                    // Return a success message if the deletion was successful
                    return "<p style='color: green;'>Resource deleted successfully</p>"
                           + "<form action='http://localhost:8542/user/webapi/myresource/remove_resource' method='get'>"
                           + "<button type='submit'>Go back</button>"
                           + "</form>";
                } else {
                    // Return an error message if the resource was not found or could not be deleted
                    return "<p style='color: red;'>Failed to delete resource</p>"
                           + "<form action='http://localhost:8542/user/webapi/myresource/remove_resource' method='get'>"
                           + "<button type='submit'>Go back</button>"
                           + "</form>";
                }
            }
        }
    }
    
    
    public String removeResourceFromUser() throws ClassNotFoundException, SQLException {
        StringBuilder dropDown = new StringBuilder();
        
        // Start building the HTML content
        dropDown.append("<!DOCTYPE html><html><head>")
                .append("<style>")
                .append("form { margin: 20px; }")
                .append("label { font-weight: bold; }")
                .append("select { padding: 8px; margin-right: 10px; }")
                .append("button { background-color: #4CAF50; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; }")
                .append("button:hover { background-color: #45a049; }")
                .append("</style>")
                .append("</head><body>")
                .append("<form action='removeresourcefromauseraction1' method='post'>")
                .append("<label for='dropdown' disabled selected>Select a user:</label>")
                .append("<select id='dropdown' name='options1' required>");
        
        // Connect to the database and retrieve users
        try (Connection connection = ConnectDb.connectdb();
             PreparedStatement pst = connection.prepareStatement("SELECT user_name FROM user_table WHERE user_type NOT IN ('SuperAdmin', 'Admin', 'Manager')");
             ResultSet rs = pst.executeQuery()) {
            
            boolean hasUsers = false;
            
            // Populate the dropdown with user names
            while (rs.next()) {
                hasUsers = true;
                String value = rs.getString("user_name");
                dropDown.append("<option value='").append(value).append("'>").append(value).append("</option>");
            }
            
            // If no users are found, display a corresponding message
            if (!hasUsers) {
                dropDown.append("<option value='' disabled selected>No Users Available</option>");
            }
        }
        
        // Close the HTML tags and return the final HTML string
        dropDown.append("</select>")
                .append("<button type='submit'>Show Resources</button>")
                .append("</form>")
                .append("</body></html>");
        
        return dropDown.toString();
    }
    
    
    public String removeResourceFromUserAction1(String userName) throws ClassNotFoundException, SQLException {
        StringBuilder dropDown = new StringBuilder();
        
        // Start building the HTML content
        dropDown.append("<!DOCTYPE html><html><head>")
                .append("<style>")
                .append("form { margin: 20px; }")
                .append("label { font-weight: bold; }")
                .append("select { padding: 8px; margin-right: 10px; }")
                .append("button { background-color: #4CAF50; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; }")
                .append("button:hover { background-color: #45a049; }")
                .append("</style>")
                .append("</head><body>")
                .append("<form action='removeresourcefromauseraction2' method='post'>")
                .append("<label for='dropdown' disabled selected>Select a resource to remove:</label>")
                .append("<select id='dropdown' name='resource' required>");
        
        // Connect to the database and retrieve resources assigned to the specified user
        try (Connection connection = ConnectDb.connectdb();
             PreparedStatement pst = connection.prepareStatement("SELECT DISTINCT resource_name FROM user_resources WHERE user_name = ?")) {
            pst.setString(1, userName);
            
            // Execute query and populate the dropdown with resources
            try (ResultSet rs = pst.executeQuery()) {
                boolean hasResources = false;
                
                while (rs.next()) {
                    hasResources = true;
                    String resource = rs.getString("resource_name");
                    dropDown.append("<option value='").append(resource).append("'>").append(resource).append("</option>");
                }
                
                // If no resources are found, display a corresponding message
                if (!hasResources) {
                    dropDown.append("<option value='' disabled selected>No Resources Available</option>");
                }
            }
        }
        
        // Add a hidden field to pass the username and finalize the HTML form
        dropDown.append("</select>")
                .append("<input type='hidden' name='userName' value='").append(userName).append("'/>")
                .append("<button type='submit'>Remove Resource</button>")
                .append("</form>")
                .append("</body></html>");
        
        return dropDown.toString();
    }
    
    
    public String removeResourceFromUserAction2(String userName, String resName) throws ClassNotFoundException, SQLException {
        // Establish a database connection
        try (Connection connection = ConnectDb.connectdb();
             // Prepare SQL statement to delete the resource from the user's resource list
             PreparedStatement pst = connection.prepareStatement("DELETE FROM user_resources WHERE user_name = ? AND resource_name = ?")) {
            
            pst.setString(1, userName);
            pst.setString(2, resName);
            // Execute the deletion and get the number of rows affected
            int rowsAffected = pst.executeUpdate();

            // If the resource was successfully removed from the user's list
            if (rowsAffected > 0) {
                // Prepare another SQL statement to delete any related requests
                try (PreparedStatement pst1 = connection.prepareStatement("DELETE FROM requests WHERE user_name = ? AND request_type = ?")) {
                    pst1.setString(1, userName);
                    pst1.setString(2, resName);
                    // Execute the deletion of related requests
                    pst1.executeUpdate();
                }

                // Return a success message with a "Go back" button
                return "<p style='color: green;'>Resource removed successfully</p>"
                     + "<form action='http://localhost:8542/user/webapi/myresource/removeresourcefromauseraction1' method='post'>"
                     + "<button type='submit'>Go back</button>"
                     + "</form>";
            }
        }
        
        // Return an error message with a "Go back" button if the resource was not removed
        return "<p style='color: red;'>Some error occurred</p>"
             + "<form action='http://localhost:8542/user/webapi/myresource/removeresourcefromauseraction1' method='post'>"
             + "<button type='submit'>Go back</button>"
             + "</form>";
    }
    
    
    public String checkuserforresource() throws ClassNotFoundException, SQLException {
        // Initialize a StringBuilder to construct the HTML content
        StringBuilder dropDown = new StringBuilder();
        
        // Start building the HTML content
        dropDown.append("<!DOCTYPE html><html><head>")
                .append("<style>")
                .append("form { margin: 20px; }")  // Style for the form
                .append("label { font-weight: bold; }")  // Style for the label
                .append("select { padding: 8px; margin-right: 10px; }")  // Style for the select dropdown
                .append("button { background-color: #4CAF50; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; }")  // Style for the button
                .append("button:hover { background-color: #45a049; }")  // Style for button hover effect
                .append("</style>")
                .append("</head><body>")
                .append("<form action='checkuserforresourceaction' method='post'>")
                .append("<label for='dropdown' disabled selected>Select a user:</label>")
                .append("<select id='dropdown' name='user' required>");
        
        // Query the database to get the list of users
        try (Connection connection = ConnectDb.connectdb();
             PreparedStatement pst = connection.prepareStatement("SELECT user_name FROM user_table where user_type not in ('SuperAdmin','Admin','Manager')");
             ResultSet rs = pst.executeQuery()) {
            // Flag to check if any users are found
            boolean hasUsers = false;
            
            // Process the result set
            while (rs.next()) {
                hasUsers = true;  // Set the flag to true if users are found
                String value = rs.getString("user_name");
                dropDown.append("<option value='").append(value).append("'>").append(value).append("</option>");  // Add each user to the dropdown list
            }
            
            // If no users are found, display a message indicating this
            if (!hasUsers) {
                dropDown.append("<option value='' disabled selected>No Users Available</option>");
            }
        }
        
        // Complete the HTML content
        dropDown.append("</select>")
                .append("<button type='submit'>Check Resources</button>")
                .append("</form>")
                .append("</body></html>");

        // Return the complete HTML content
        return dropDown.toString();
    }
    
    
    
    public String checkuserforresourceaction(String userName) throws ClassNotFoundException, SQLException {
        // Initialize a StringBuilder to construct the HTML content
        StringBuilder resourcesHtml = new StringBuilder();
        
        // Start building the HTML content
        resourcesHtml.append("<!DOCTYPE html><html><head>")
                     .append("<style>")
                     .append("table { width: 100%; border-collapse: collapse; margin: 20px 0; }")  // Style for the table
                     .append("th, td { padding: 12px; text-align: left; border: 1px solid #ddd; }")  // Style for table cells
                     .append("th { background-color: #f4f4f4; }")  // Style for table header cells
                     .append("</style>")
                     .append("</head><body>")
                     .append("<h2>Resources for User: ").append(userName).append("</h2>")
                     .append("<table>")
                     .append("<tr><th>Resource Name</th></tr>");
        
        // Query the database to get the list of resources for the specified user
        try (Connection connection = ConnectDb.connectdb();
             PreparedStatement pst = connection.prepareStatement("SELECT distinct resource_name FROM user_resources WHERE user_name = ?")) {
            pst.setString(1, userName);  // Set the user name parameter for the query
            try (ResultSet rs = pst.executeQuery()) {
                // Flag to check if any resources are found
                boolean hasResources = false;
                
                // Process the result set
                while (rs.next()) {
                    hasResources = true;  // Set the flag to true if resources are found
                    String resourceName = rs.getString("resource_name");
                    resourcesHtml.append("<tr><td>").append(resourceName).append("</td></tr>");  // Add each resource to the table
                }
                
                // If no resources are found, display a message indicating this
                if (!hasResources) {
                    resourcesHtml.append("<tr><td>No Resources Found</td></tr>");
                }
            }
        }
        
        // Complete the HTML content
        resourcesHtml.append("</table>")
                     .append("</body></html>");
        
        // Return the complete HTML content
        return resourcesHtml.toString();
    }
    
    
    public String checkUsersForresource() throws ClassNotFoundException, SQLException {
        // Initialize a StringBuilder to construct the HTML content
        StringBuilder dropDown = new StringBuilder();
        
        // Start building the HTML content
        dropDown.append("<!DOCTYPE html><html><head>")
                .append("<style>")
                .append("form { margin: 20px; }")  // Style for the form
                .append("label { font-weight: bold; }")  // Style for the label
                .append("select { padding: 8px; margin-right: 10px; }")  // Style for the dropdown
                .append("button { background-color: #4CAF50; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; }")  // Style for the button
                .append("button:hover { background-color: #45a049; }")  // Button hover style
                .append("</style>")
                .append("</head><body>")
                .append("<form action='checkusersforresourceaction' method='post'>")  // Form action and method
                .append("<label for='dropdown' disabled selected>Select a Resource:</label>")  // Label for the dropdown
                .append("<select id='dropdown' name='resource' required>");  // Dropdown for selecting a resource
        
        // Query the database to get the list of resources
        try (Connection connection = ConnectDb.connectdb();
             PreparedStatement pst = connection.prepareStatement("SELECT Resource_name FROM resource");
             ResultSet rs = pst.executeQuery()) {
            // Flag to check if any resources are found
            boolean hasUsers = false;
            
            // Process the result set
            while (rs.next()) {
                hasUsers = true;  // Set the flag to true if resources are found
                String value = rs.getString("resource_name");
                dropDown.append("<option value='").append(value).append("'>").append(value).append("</option>");  // Add each resource to the dropdown
            }
            
            // If no resources are found, display a message indicating this
            if (!hasUsers) {
                dropDown.append("<option value='' disabled selected>No Resources Available</option>");
            }
        }
        
        // Complete the HTML content
        dropDown.append("</select>")
                .append("<button type='submit'>Check Users</button>")  // Submit button for the form
                .append("</form>")
                .append("</body></html>");
        
        // Return the complete HTML content
        return dropDown.toString();
    }
    
    
    public String checkUsersForresourceAction(String resName) throws ClassNotFoundException, SQLException {
        // Initialize a StringBuilder to construct the HTML content
        StringBuilder resourcesHtml = new StringBuilder();
        
        // Start building the HTML content
        resourcesHtml.append("<!DOCTYPE html><html><head>")
                     .append("<style>")
                     .append("table { width: 100%; border-collapse: collapse; margin: 20px 0; }")  // Style for the table
                     .append("th, td { padding: 12px; text-align: left; border: 1px solid #ddd; }")  // Style for table headers and cells
                     .append("th { background-color: #f4f4f4; }")  // Background color for table headers
                     .append("</style>")
                     .append("</head><body>")
                     .append("<h2> Users for Resource: ").append(resName).append("</h2>")  // Header with the resource name
                     .append("<table>")
                     .append("<tr><th>The Associated users are</th></tr>");  // Table header for user list
        
        // Query the database to get users associated with the specified resource
        try (Connection connection = ConnectDb.connectdb();
             PreparedStatement pst = connection.prepareStatement("SELECT user_name FROM user_resources WHERE resource_name = ?")) {
            pst.setString(1, resName);  // Set the resource name parameter
            try (ResultSet rs = pst.executeQuery()) {
                // Flag to check if any users are found
                boolean hasResources = false;
                
                // Process the result set
                while (rs.next()) {
                    hasResources = true;  // Set the flag to true if users are found
                    String resourceName = rs.getString("user_name");
                    resourcesHtml.append("<tr><td>").append(resourceName).append("</td></tr>");  // Add each user to the table
                }
                
                // If no users are found, display a message indicating this
                if (!hasResources) {
                    resourcesHtml.append("<tr><td>No Users Found</td></tr>");
                }
            }
        }
        
        // Complete the HTML content
        resourcesHtml.append("</table>")
                     .append("</body></html>");
        
        // Return the complete HTML content
        return resourcesHtml.toString();
    }
    
    
    //////////////////////////user///////////////////////////////////////////////
    
    
    
    public String myresources(String username) throws Exception {
        StringBuilder tableHtml = new StringBuilder();
        tableHtml.append("<!DOCTYPE html>")
                 .append("<html lang='en'>")
                 .append("<head>")
                 .append("<meta charset='UTF-8'>")
                 .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                 .append("<title>User Resources</title>")
                 .append("<style>")
                 .append("body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }")
                 .append("h1 { color: #333; text-align: center; margin-top: 20px; }")
                 .append("table { width: 80%; margin: 20px auto; border-collapse: collapse; background: #fff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }")
                 .append("th, td { padding: 12px; border: 1px solid #ddd; text-align: left; }")
                 .append("th { background-color: #007BFF; color: #fff; }")
                 .append("tr:nth-child(even) { background-color: #f2f2f2; }")
                 .append("td { color: #333; }")
                 .append("td[colspan='1'] { text-align: center; }")
                 .append("</style>")
                 .append("</head>")
                 .append("<body>")
                 .append("<h1>User Resources</h1>")
                 .append("<table>")
                 .append("<thead>")
                 .append("<tr>")
                 .append("<th>Resource Name</th>")
                 .append("</tr>")
                 .append("</thead>")
                 .append("<tbody>");
        String query = "SELECT distinct resource_name FROM user_resources WHERE user_name = ? ";
        try (Connection connection = ConnectDb.connectdb();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                boolean hasResults = false;
                while (resultSet.next()) {
                    hasResults = true;
                    String resourceName = resultSet.getString("resource_name");
                    tableHtml.append("<tr>")
                             .append("<td>").append(resourceName).append("</td>")
                             .append("</tr>");
                }
                if (!hasResults) {
                    tableHtml.append("<tr>")
                             .append("<td colspan='1'>No resources available</td>")
                             .append("</tr>");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Database error occurred while fetching user resources.", e);
        }
        tableHtml.append("</tbody>")
                 .append("</table>")
                 .append("</body>")
                 .append("</html>");
        return tableHtml.toString();
    }
    
    
    public String requestforresources(String username) throws Exception {
        StringBuilder dropdownHtml = new StringBuilder();
        dropdownHtml.append("<!DOCTYPE html>")
                    .append("<html lang='en'>")
                    .append("<head>")
                    .append("<meta charset='UTF-8'>")
                    .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                    .append("<title>Request Resources</title>")
                    .append("<style>")
                    .append("body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }")
                    .append("h1 { color: #333; text-align: center; margin-top: 20px; }")
                    .append("form { width: 80%; max-width: 600px; margin: 20px auto; padding: 20px; background: #fff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }")
                    .append("label { display: block; margin-bottom: 10px; color: #333; font-weight: bold; }")
                    .append("select, button { width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ddd; border-radius: 4px; }")
                    .append("button { background-color: #007BFF; color: #fff; border: none; cursor: pointer; }")
                    .append("button:hover { background-color: #0056b3; }")
                    .append("</style>")
                    .append("</head>")
                    .append("<body>")
                    .append("<h1>Request Resources</h1>")
                    .append("<form action='requestforresourceaction' method='post'>")
                    .append("<label for='dropdown' disabled selected>Select resource name to request:</label>")
                    .append("<select id='dropdown' name='options' required>");
        try (Connection connection = ConnectDb.connectdb()) {
            Set<String> ownedResources = new HashSet<>();
            try (PreparedStatement pstOwned = connection.prepareStatement("SELECT resource_name FROM user_resources WHERE user_name = ?")) {
                pstOwned.setString(1, username);
                try (ResultSet rsOwned = pstOwned.executeQuery()) {
                    while (rsOwned.next()) {
                        ownedResources.add(rsOwned.getString("resource_name"));
                    }
                }
            }
            Set<String> pendingRequests = new HashSet<>();
            try (PreparedStatement pstPending = connection.prepareStatement("SELECT request_type FROM requests WHERE user_name = ? AND status = 'pending'")) {
                pstPending.setString(1, username);
                try (ResultSet rsPending = pstPending.executeQuery()) {
                    while (rsPending.next()) {
                        pendingRequests.add(rsPending.getString("request_type"));
                    }
                }
            }
            boolean hasAvailableResources = false;
            try (PreparedStatement pstAll = connection.prepareStatement("SELECT resource_name FROM resource")) {
                try (ResultSet rsAll = pstAll.executeQuery()) {
                    while (rsAll.next()) {
                        String resourceName = rsAll.getString("resource_name");
                        if (!ownedResources.contains(resourceName) && !pendingRequests.contains(resourceName)) {
                            dropdownHtml.append("<option value='").append(resourceName).append("'>")
                                        .append(resourceName).append("</option>");
                            hasAvailableResources = true;
                        }
                    }
                }
            }
            if (!hasAvailableResources) {
                dropdownHtml.append("<option value='' disabled selected>No resources available</option>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Database error occurred while preparing resource request form.", e);
        }
        dropdownHtml.append("</select>")
                    .append("<button type='submit'>Submit</button>")
                    .append("</form>")
                    .append("</body>")
                    .append("</html>");
        return dropdownHtml.toString();
    }
    
    
    public String requestforresourcesaction(String username, String option) throws Exception {
        String resultMessage;
        String query = "INSERT INTO requests (request_type, user_name, date_of_request, status) VALUES (?, ?, ?, ?)";
        try (Connection con = ConnectDb.connectdb();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, option);
            pst.setString(2, username);
            pst.setString(3, LocalDate.now().toString()); // Current date
            pst.setString(4, "pending");
            int rowsAffected = pst.executeUpdate();
            if(rowsAffected > 0) {
            	resultMessage =  "Request submitted successfully." 
            			+ "<form action='http://localhost:8542/user/webapi/myresource/requestforresource' method='get'>"
            		     + "<button type='submit'>Go back</button>"
            		     + "</form>";
            }
            else {
            	resultMessage =  "Failed to submit request." 
            			+ "<form action='http://localhost:8542/user/webapi/myresource/requestforresource' method='get'>"
            		     + "<button type='submit'>Go back</button>"
            		     + "</form>";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Database error occurred while submitting request.", e);
        }
        return resultMessage;
    }
    
    
    public String requestforadminormanager(String uname) throws Exception {
        StringBuilder dropDown = new StringBuilder();
        dropDown.append("<!DOCTYPE html>")
                .append("<html lang='en'>")
                .append("<head>")
                .append("<meta charset='UTF-8'>")
                .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                .append("<title>Request Role</title>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }")
                .append("h1 { color: #333; text-align: center; margin-top: 20px; }")
                .append("form { width: 80%; max-width: 600px; margin: 20px auto; padding: 20px; background: #fff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }")
                .append("label { display: block; margin-bottom: 10px; color: #333; font-weight: bold; }")
                .append("select, button { width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ddd; border-radius: 4px; }")
                .append("button { background-color: #007BFF; color: #fff; border: none; cursor: pointer; }")
                .append("button:hover { background-color: #0056b3; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<h1>Request Role</h1>")
                .append("<form action='requestfor' method='post'>")
                .append("<label for='dropdown' disabled selected>Select role to request:</label>")
                .append("<select id='dropdown' name='options' required>");
        String query = "SELECT COUNT(*) FROM requests WHERE user_name = ? AND request_type = ?";
        try (Connection con = ConnectDb.connectdb();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, uname);
            pst.setString(2, "Admin");
            try (ResultSet rs = pst.executeQuery()) {
                boolean adminRequested = rs.next() && rs.getInt(1) > 0;
                pst.setString(2, "Manager");
                try (ResultSet rsManager = pst.executeQuery()) {
                    boolean managerRequested = rsManager.next() && rsManager.getInt(1) > 0;
                    if (adminRequested && managerRequested) {
                        dropDown.append("<option value= ''disabled selected>No options available</option>");
                    } else {
                        if (!adminRequested) {
                            dropDown.append("<option value='Admin'>Admin</option>");
                        }
                        if (!managerRequested) {
                            dropDown.append("<option value='Manager'>Manager</option>");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Database error occurred while preparing role request form.", e);
        }
        dropDown.append("</select>")
                .append("<button type='submit'>Submit</button>")
                .append("</form>")
                .append("</body>")
                .append("</html>");
        return dropDown.toString();
    }
    
    
    public String requestfor(String uname, String option) throws Exception {
        String resultMessage;
        String query = "INSERT INTO requests (request_type, user_name, date_of_request, status) VALUES (?, ?, ?, ?)";
        try (Connection con = ConnectDb.connectdb();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, option);
            pst.setString(2, uname);
            pst.setString(3, LocalDate.now().toString()); 
            pst.setString(4, "pending");
            int rowsAffected = pst.executeUpdate();
            if(rowsAffected > 0) {
            	resultMessage =  "Request submitted successfully." 
            			+ "<form action='http://localhost:8542/user/webapi/myresource/requestforadminormanager' method='get'>"
            		     + "<button type='submit'>Go back</button>"
            		     + "</form>";
            }
            else {
            	resultMessage =  "Failed to submit request." 
            			+ "<form action='http://localhost:8542/user/webapi/myresource/requestforadminormanager' method='get'>"
            		     + "<button type='submit'>Go back</button>"
            		     + "</form>";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Database error occurred while submitting role request.", e);
        }
        return resultMessage;
    }
    
    
    public String removeownresource(String userName) throws Exception {
        StringBuilder dropdownHtml = new StringBuilder();
        dropdownHtml.append("<!DOCTYPE html>")
                    .append("<html lang='en'>")
                    .append("<head>")
                    .append("<meta charset='UTF-8'>")
                    .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                    .append("<title>Remove Resource</title>")
                    .append("<style>")
                    .append("body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }")
                    .append("h1 { color: #333; text-align: center; margin-top: 20px; }")
                    .append("form { width: 80%; max-width: 600px; margin: 20px auto; padding: 20px; background: #fff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }")
                    .append("label { display: block; margin-bottom: 10px; color: #333; font-weight: bold; }")
                    .append("select, button { width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ddd; border-radius: 4px; }")
                    .append("button { background-color: #007BFF; color: #fff; border: none; cursor: pointer; }")
                    .append("button:hover { background-color: #0056b3; }")
                    .append("</style>")
                    .append("</head>")
                    .append("<body>")
                    .append("<h1>Remove Resource</h1>")
                    .append("<form action='removeownresourceaction' method='post'>")
                    .append("<label for='dropdown'disabled selected>Select the resource name to delete:</label>")
                    .append("<select id='dropdown' name='options' required>");
        String query = "SELECT distinct resource_name FROM user_resources WHERE user_name = ?";
        try (Connection connection = ConnectDb.connectdb();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, userName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                boolean hasResults = false;
                while (resultSet.next()) {
                    hasResults = true;
                    String resourceName = resultSet.getString("resource_name");
                    dropdownHtml.append("<option value='").append(resourceName).append("'>")
                                .append(resourceName).append("</option>");
                }
                if (!hasResults) {
                    dropdownHtml.append("<option value='' disabled selected>No options available</option>");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Database error occurred while preparing resource removal form.", e);
        }
        dropdownHtml.append("</select>")
                    .append("<button type='submit'>Submit</button>")
                    .append("</form>")
                    .append("</body>")
                    .append("</html>");
        return dropdownHtml.toString();
    }
    
    
    public String removeownresourceaction(String userName,String option) throws Exception {
        String resultMessage;
        Connection connection = ConnectDb.connectdb();
        String query = "DELETE FROM user_resources WHERE resource_name = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, option);
            int rowsAffected = pst.executeUpdate();
            resultMessage = (rowsAffected > 0) ? "Resource removed successfully."+ "<form action='http://localhost:8542/user/webapi/myresource/removeownresource' method='get'>"
        		     + "<button type='submit'>Go back</button>"
        		     + "</form>": "Resource not found."+ "<form action='http://localhost:8542/user/webapi/myresource/removeownresource' method='get'>"
                 		     + "<button type='submit'>Go back</button>"
                 		     + "</form>";
        } 
        try (PreparedStatement pst1 = connection.prepareStatement("DELETE FROM requests WHERE user_name = ? AND request_type = ?")) {
            pst1.setString(1, userName);
            pst1.setString(2, option);
            pst1.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Database error occurred while removing resource.", e);
        }
        return resultMessage;
    }
    
    
    
    
    //////////////////////manager//////////////////////////////////////////////////
    
    
    
    
    public String showteam(String managerName) throws Exception {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>")
                   .append("<html lang='en'>")
                   .append("<head>")
                   .append("<meta charset='UTF-8'>")
                   .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                   .append("<title>Team Members</title>")
                   .append("<style>")
                   .append("body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }")
                   .append("h1 { color: #333; text-align: center; margin-top: 20px; }")
                   .append("ul { width: 80%; margin: 20px auto; padding: 0; list-style-type: none; }")
                   .append("li { display: flex; justify-content: space-between; align-items: center; padding: 10px; margin-bottom: 5px; background: #fff; border-radius: 4px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }")
                   .append("li:nth-child(odd) { background-color: #f9f9f9; }")
                   .append(".remove-btn { background-color: #c00; color: #fff; border: none; border-radius: 4px; padding: 5px 10px; cursor: pointer; }")
                   .append(".remove-btn:hover { background-color: #a00; }")
                   .append("</style>")
                   .append("</head>")
                   .append("<body>")
                   .append("<h1>Team Members under Manager: ").append(managerName).append("</h1>")
                   .append("<ul>");
        String query = "SELECT User_Name FROM user_table WHERE manager_name =?";
        try (Connection connection = ConnectDb.connectdb();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, managerName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                boolean hasResults = false;

                while (resultSet.next()) {
                    hasResults = true;
                    String userName = resultSet.getString("User_Name");
                    htmlBuilder.append("<li>")
                               .append(userName)
                               .append("<form action='removeuserfromteam' method='post' style='margin: 0; display: inline;'>")
                               .append("<input type='hidden' name='managerName' value='").append(managerName).append("'/>")
                               .append("<input type='hidden' name='userName' value='").append(userName).append("'/>")
                               .append("<button type='submit' class='remove-btn'>Remove</button>")
                               .append("</form>")
                               .append("</li>");
                }

                if (!hasResults) {
                    htmlBuilder.append("<li>No team members are allocated</li>");
                }
            }
        } catch (SQLException e) {
            return e.getMessage();
            //throw new Exception("Database error occurred while fetching team members.", e);
        }
        htmlBuilder.append("</ul>")
                   .append("</body>")
                   .append("</html>");

        return htmlBuilder.toString();
    }
    
    
    public String removeUser(String managerName, String userName) throws Exception {
        String query = "UPDATE user_table SET manager_name = null WHERE user_name = ?";
        try (Connection connection = ConnectDb.connectdb();  
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            //preparedStatement.setString(1, managerName);
            preparedStatement.setString(1, userName);
            
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                return "User " + userName + " removed from the team successfully."
                		+ "<form action='http://localhost:8542/user/webapi/myresource/showteam' method='get'>"
             		     + "<button type='submit'>Go back</button>"
             		     + "</form>";
            } else {
                return "User " + userName + " not found."
                		+ "<form action='http://localhost:8542/user/webapi/myresource/showteam' method='get'>"
             		     + "<button type='submit'>Go back</button>"
             		     + "</form>";
            }
        } catch (SQLException e) {
            throw new Exception("Database error occurred while removing the user.", e);
        }
    }



    public String getTeamMember(String managerName) throws Exception {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>")
                   .append("<html lang='en'>")
                   .append("<head>")
                   .append("<meta charset='UTF-8'>")
                   .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                   .append("<title>Unassigned Team Members</title>")
                   .append("<style>")
                   .append("body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }")
                   .append("h1 { color: #333; text-align: center; margin-top: 20px; }")
                   .append("ul { width: 80%; margin: 20px auto; padding: 0; list-style-type: none; }")
                   .append("li { padding: 10px; margin-bottom: 5px; background: #fff; border-radius: 4px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); display: flex; justify-content: space-between; align-items: center; }")
                   .append("li:nth-child(odd) { background-color: #f9f9f9; }")
                   .append("button { padding: 10px 15px; border: none; background-color: #007bff; color: #fff; border-radius: 4px; cursor: pointer; }")
                   .append("button:hover { background-color: #0056b3; }")
                   .append("</style>")
                   .append("</head>")
                   .append("<body>")
                   .append("<h1>Unassigned Team Members</h1>")
                   .append("<form action='assignUserToTeam' method='post'>")
                   .append("<ul>");
        String query = "SELECT User_Name FROM user_table WHERE manager_name IS NULL and user_type != \"Manager\" and user_type != \"Admin\" and user_type != \"SuperAdmin\";";
        try (Connection connection = ConnectDb.connectdb();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {        	
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                boolean hasResults = false;
                while (resultSet.next()) {
                    hasResults = true;
                    String userName = resultSet.getString("User_Name");
                    htmlBuilder.append("<li>")
                               .append(userName)
                               .append("<form action='assignUserToTeam' method='post' style='margin: 0;'>")
                               .append("<button type='submit' name='username' value='").append(userName).append("'>Add to Team</button>")
                               .append("</form>")
                               .append("</li>");
                }
                if (!hasResults) {
                    htmlBuilder.append("<li>No unassigned team members found</li>");
                }
            }
        } catch (SQLException e) {
            return e.getMessage();
            //throw new Exception("Database error occurred while fetching unassigned team members.", e);
        }
        htmlBuilder.append("</ul>")
                   .append("</body>")
                   .append("</html>");
        return htmlBuilder.toString();
    }
    
    
    public String assignUserToTeam(String username, String managerName) throws Exception {
        String resultMessage;
        //System.out.println(username+""+managerName);
        String query = "UPDATE user_table SET manager_name = ? WHERE User_Name = ?";
        try (Connection connection = ConnectDb.connectdb();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, managerName);
            preparedStatement.setString(2, username);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                resultMessage = "User " + username + " has been successfully assigned to manager " + managerName + "."
                		+ "<form action='http://localhost:8542/user/webapi/myresource/getteammember' method='get'>"
           		     + "<button type='submit'>Go back</button>"
           		     + "</form>";
                } 
            else {
                resultMessage = "Failed to assign user " + username + "."
                		+ "<form action='http://localhost:8542/user/webapi/myresource/getteammember' method='get'>"
            		     + "<button type='submit'>Go back</button>"
            		     + "</form>";
;
            }
        } catch (SQLException e) {
            return e.getMessage();
            //throw new Exception("Database error occurred while assigning user to the team.", e);
        }
        return resultMessage;
    }
    
    
    public String requestForAdminInManager(String managerId) throws ClassNotFoundException, SQLException {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>")
                   .append("<html lang='en'>")
                   .append("<head>")
                   .append("<meta charset='UTF-8'>")
                   .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                   .append("<title>Team Members Dropdown</title>")
                   .append("<style>")
                   .append("body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }")
                   .append("h1 { color: #333; text-align: center; margin-top: 20px; }")
                   .append("form { width: 80%; margin: 20px auto; }")
                   .append("select { width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ddd; border-radius: 4px; font-size: 16px; }")
                   .append("button { padding: 10px 15px; border: none; background-color: #007bff; color: #fff; border-radius: 4px; cursor: pointer; font-size: 16px; }")
                   .append("button:hover { background-color: #0056b3; }")
                   .append("</style>")
                   .append("</head>")
                   .append("<body>")
                   .append("<h1>Select Team Member to assign as Manager</h1>")
                   .append("<form action='assignmanagerto' method='post'>")
                   .append("<select name='username'>");

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = ConnectDb.connectdb();
            String query = "SELECT user_name FROM user_table WHERE manager_name = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, managerId);
            resultSet = statement.executeQuery();
            
            boolean hasResults = false;
            while (resultSet.next()) {
                hasResults = true;
                String userName = resultSet.getString("user_name");
                htmlBuilder.append("<option value='").append(userName).append("'>")
                           .append(userName).append("</option>");
            }
            
            if (!hasResults) {
                htmlBuilder.append("<option value='' selected disabled>No team members found</option>");
            }
        } catch (SQLException e) {
            return "<html><body><h1>Error occurred</h1><p>" + e.getMessage() + "</p></body></html>";
        } 
        
        htmlBuilder.append("</select>")
                   .append("<button type='submit'>Submit</button>")
                   .append("</form>")
                   .append("</body>")
                   .append("</html>");
        
        return htmlBuilder.toString();
    }

    public String assignManagerTo(String managername) throws ClassNotFoundException, SQLException {
    	//System.out.println(teammembername);
    	Connection connection = ConnectDb.connectdb();
    	String query1 = "INSERT INTO requests (request_type, user_name, date_of_request, status) VALUES (?, ?, ?, ?)";
        PreparedStatement statement1 = connection.prepareStatement(query1);
        statement1.setString(1,"ManagertoAdmin");
        statement1.setString(2,managername);
        statement1.setString(3, LocalDate.now().toString()); // Current date
        statement1.setString(4, "pending");
        statement1.execute();
		return "Request Sucessfully sent";
    }
    public String managerAttestation(String managerName) throws ClassNotFoundException, SQLException {
        StringBuilder htmlBuilder = new StringBuilder();

        htmlBuilder.append("<!DOCTYPE html>")
                   .append("<html lang='en'>")
                   .append("<head>")
                   .append("<meta charset='UTF-8'>")
                   .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                   .append("<title>Manager Attestation</title>")
                   .append("<style>")
                   .append("body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f7f9; color: #333; }")
                   .append(".container { max-width: 1200px; margin: 20px auto; padding: 20px; background: #fff; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }")
                   .append(".header { margin-bottom: 20px; text-align: center; }")
                   .append(".header h1 { font-size: 24px; color: #007BFF; }")
                   .append(".table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }")
                   .append(".table th, .table td { padding: 10px; border: 1px solid #ddd; text-align: left; }")
                   .append(".table th { background-color: #007BFF; color: #fff; }")
                   .append(".table tr:nth-child(even) { background-color: #f9f9f9; }")
                   .append(".button { padding: 8px 15px; font-size: 14px; color: #fff; background-color: #dc3545; border: none; border-radius: 6px; cursor: pointer; transition: background-color 0.3s; }")
                   .append(".button:hover { background-color: #c82333; }")
                   .append("</style>")
                   .append("</head>")
                   .append("<body>")
                   .append("<div class='container'>")
                   .append("<div class='header'>")
                   .append("<h1>Team Members and Resources</h1>")
                   .append("</div>")
                   .append("<table class='table'>")
                   .append("<thead>")
                   .append("<tr>")
                   .append("<th>Username</th>")
                   .append("<th>Resources</th>")
                   .append("<th>Actions</th>")
                   .append("</tr>")
                   .append("</thead>")
                   .append("<tbody>");
        String sqlUsers = "SELECT user_name FROM user_table WHERE manager_name = ?";
        Map<String, StringBuilder> userResourcesMap = new HashMap<>();

        try (Connection connection = ConnectDb.connectdb();
             PreparedStatement userStatement = connection.prepareStatement(sqlUsers)) {
            userStatement.setString(1, managerName);
            ResultSet userResultSet = userStatement.executeQuery();

            while (userResultSet.next()) {
                String userName = userResultSet.getString("user_name");
                userResourcesMap.put(userName, new StringBuilder());
                String sqlResources = "SELECT resource_name FROM user_resources WHERE user_name = ?";
                try (PreparedStatement resourceStatement = connection.prepareStatement(sqlResources)) {
                    resourceStatement.setString(1, userName);
                    ResultSet resourceResultSet = resourceStatement.executeQuery();
                    boolean hasResults = false;
                    while (resourceResultSet.next()) {
                    	hasResults=true;
                        String resourceName = resourceResultSet.getString("resource_name");
                        userResourcesMap.get(userName).append(resourceName).append("<br>");
                    }
                    if (!hasResults) {
                    	userResourcesMap.get(userName).append("No Resources Found").append("<br>");
                    }
                    htmlBuilder.append("<tr>")
                               .append("<td>").append(userName).append("</td>")
                               .append("<td>").append(userResourcesMap.get(userName).toString()).append("</td>")
                               .append("<td>")
                               .append("<form action='removeresourcefromteam' method='post'>")
                               .append("<input type='hidden' name='user_name' value='").append(userName).append("'>")
                               .append("<input type='text' name='resource_name' placeholder='Resource to remove' required>")
                               .append("<input type='submit' class='button' value='Remove'>")
                               .append("</form>")
                               .append("</td>")
                               .append("</tr>");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Database error occurred while fetching team members and resources.", e);
        }
        htmlBuilder.append("</tbody>")
                   .append("</table>")
                   .append("</div>")
                   .append("</body>")
                   .append("</html>");

        return htmlBuilder.toString();
    }
    
    
    public String removeResourceFromTeam(String userName, String resourceName) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM user_resources WHERE user_name = ? AND resource_name = ?";
        
        try (Connection connection = ConnectDb.connectdb();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userName);
            statement.setString(2, resourceName);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                return "No resource found for the specified user."
                		+ "<form action='http://localhost:8542/user/webapi/myresource/managerattestation' method='get'>"
              		     + "<button type='submit'>Go back</button>"
              		     + "</form>";
            } else {
            	try (PreparedStatement pst = connection.prepareStatement("DELETE FROM requests WHERE user_name = ? AND request_type = ?")) {
                       pst.setString(1, userName);
                       pst.setString(2, resourceName);
                       pst.execute();
                   }
                return "Removed "+ resourceName +" sucessfully for the user "+userName
                		+ "<form action='http://localhost:8542/user/webapi/myresource/managerattestation' method='get'>"
              		     + "<button type='submit'>Go back</button>"
              		     + "</form>";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error removing the resource: " + e.getMessage(), e);
        }
    }
    
}