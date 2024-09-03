package UAM.user;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

public class User {
	public String userFirstName;
	public String userLastName;
	public String email;
	private String userPwd;
	private String cuserPwd;
	private String encryptedPwd;
	private String userName;
	public User(String first_name, String last_name, String email, String pwd, String cpwd) {
		this.userFirstName = first_name;
		this.userLastName = last_name;
		this.email = email;
		this.userPwd = pwd;
		this.cuserPwd = cpwd;
	}
	public User(String username, String password) {
		this.userName = username;
		userPwd = password;
	}
	public User() {
		// TODO Auto-generated constructor stub
	}
	
	public String login() throws ClassNotFoundException, SQLException, IOException {
	    // Encrypt the provided password using the custom encryption method
	    String encryptedPwd = encryptPassword();
	    
	    // SQL query to check if the provided username exists in the database
	    String userCheckQuery = "SELECT COUNT(*) FROM user_table WHERE user_name = ?";
	    
	    // SQL query to check if the username and password match a record in the database
	    String credentialsCheckQuery = "SELECT first_name, last_name, user_name, user_type FROM user_table WHERE password = ? AND user_name = ?";
	    
	    // Establish a database connection and prepare SQL statements
	    try (Connection con = ConnectDb.connectdb();
	         PreparedStatement userCheckStmt = con.prepareStatement(userCheckQuery);
	         PreparedStatement credentialsCheckStmt = con.prepareStatement(credentialsCheckQuery)) {
	        
	        // Set the username parameter for the user check query
	        userCheckStmt.setString(1, userName);
	        
	        // Execute the user check query
	        try (ResultSet rs = userCheckStmt.executeQuery()) {
	            if (rs.next()) {
	                // Retrieve the count of users with the provided username
	                int userCount = rs.getInt(1);
	                
	                // If no user exists with the provided username, return an error message
	                if (userCount == 0) {
	                    return "Username not found.";
	                }
	            }
	            
	            // Set the parameters for the credentials check query
	            credentialsCheckStmt.setString(1, encryptedPwd);
	            credentialsCheckStmt.setString(2, userName);
	            
	            // Execute the credentials check query
	            try (ResultSet rs2 = credentialsCheckStmt.executeQuery()) {
	                if (rs2.next()) {
	                    // Retrieve user details from the result set
	                    String storedUsername = rs2.getString("user_name");
	                    String userType = rs2.getString("user_type");
	                    String firstname = rs2.getString("first_name");
	                    String lastname = rs2.getString("last_name");
	                    
	                    // Check if the provided username matches the stored username
	                    if (userName.equals(storedUsername)) {
	                        // Validate the password with a combined encryption of first and last names
	                        if (encryptedPwd.equals(encryptPassword(firstname + lastname))) {
	                            // Redirect to the new user page if credentials are correct
	                            return "http://localhost:8542/user/newuser.jsp";
	                        } else if ("Admin".equals(userType) || "SuperAdmin".equals(userType)) {
	                            // Redirect to the admin page for Admin or SuperAdmin user types
	                            return "http://localhost:8542/user/admin.jsp";
	                        } else if ("Manager".equals(userType)) {
	                            // Redirect to the manager page for Manager user type
	                            return "http://localhost:8542/user/manager.jsp";
	                        } else if ("User".equals(userType)) {
	                            // Redirect to the user page for standard User type
	                            return "http://localhost:8542/user/user.jsp";
	                        } else {
	                            // Return an error message for unknown user types
	                            return "Unknown user type. ";
	                        }
	                    } else {
	                        // Return an error message if the username does not match
	                        return "Invalid credentials. ";
	                    }
	                } else {
	                    // Return an error message if credentials are invalid
	                    return "Invalid credentials. ";
	                }
	            }
	        } catch (SQLException e) {
	            // Print the stack trace and return a database error message
	            e.printStackTrace();
	            return "Database error. ";
	        }
	    } catch (SQLException e) {
	        // Print the stack trace and return a database error message
	        e.printStackTrace();
	        return "Database error. ";
	    } 
	}
	
	
	
	public String signUp1(String userFirstName2, String userLastName2, String email2, @Context HttpServletResponse res) throws ClassNotFoundException, IOException {
	    String username; // Variable to store the generated unique username
	    
	    try (Connection con = ConnectDb.connectdb()) { // Establish a database connection
	        // Generate a unique username based on the user's first and last names
	        username = generateUniqueUsername(con, userFirstName2 + "." + userLastName2);
	        
	        // Encrypt the password using the user's first and last names
	       
	        
	        // Get the current date in the format "yyyy-MM-dd"
	        String dateString = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
	        
	        // SQL query to insert the new user's details into the `user_table`
	        String query = "INSERT INTO user_table (first_name, last_name, user_name, email, user_type, Date_of_joining, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
	        
	        try (PreparedStatement pst = con.prepareStatement(query)) {
	            // Set the parameters for the SQL query
	            pst.setString(1, userFirstName2); // Set the user's first name
	            pst.setString(2, userLastName2);  // Set the user's last name
	            pst.setString(3, username);        // Set the generated username
	            pst.setString(4, email2);          // Set the user's email
	            pst.setString(5, "User");          // Set the user's type as "User"
	            pst.setString(6, dateString);      // Set the date of joining
	            pst.setString(7, encryptPassword(userFirstName2 + userLastName2));    // Set the encrypted password
	            
	            // Execute the SQL query to insert the user's details into the database
	            pst.executeUpdate();
	        }
	    } catch (SQLException e) {
	        // Print the stack trace and return an error message if a SQL exception occurs
	        e.printStackTrace();
	        return "Database error: " + e.getMessage();
	    }
	    
	    // Return a success message indicating the account creation and the generated username
	    return "<h1>Account created successfully and the Username is:" + username + "</h1>";
	}
	
	
	
	public Response signUp(@Context HttpServletResponse res) throws ClassNotFoundException, IOException, ServletException {
	    String redirectUrl; // Variable to store the URL for redirection after successful registration
	    
	    // Check if the provided password matches the confirmed password
	    if (!userPwd.equals(cuserPwd)) {
	        // Return an UNAUTHORIZED response if passwords do not match
	        return Response.status(Response.Status.UNAUTHORIZED).entity("Password does not match").build();
	    }
	    
	    // Check if the provided password meets the strength requirements
	    
	    try (Connection con = ConnectDb.connectdb()) { // Establish a database connection
	        // Encrypt the password using the custom encryption method
	        encryptedPwd = encryptPassword();
	        
	        // Generate a unique username based on the user's first and last names
	        String username = generateUniqueUsername(con, userFirstName + "." + userLastName);
	        
	        // Determine the user's type (e.g., "User", "Admin", etc.)
	        String userType = getUserType();
	        
	        // Get the current date in the format "yyyy-MM-dd"
	        String dateString = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
	        
	        // SQL query to insert the new user's details into the `user_table`
	        String query = "INSERT INTO user_table (first_name, last_name, user_name, email, user_type, Date_of_joining, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
	        
	        try (PreparedStatement pst = con.prepareStatement(query)) {
	            // Set the parameters for the SQL query
	            pst.setString(1, userFirstName); // Set the user's first name
	            pst.setString(2, userLastName);  // Set the user's last name
	            pst.setString(3, username);        // Set the generated username
	            pst.setString(4, email);          // Set the user's email
	            pst.setString(5, userType);       // Set the user's type
	            pst.setString(6, dateString);     // Set the date of joining
	            pst.setString(7, encryptedPwd);   // Set the encrypted password
	            
	            // Execute the SQL query to insert the user's details into the database
	            pst.executeUpdate();
	        }
	        
	        // Create the URL for redirection after successful registration
	        redirectUrl = "http://localhost:8542/user/sucessful_login.html?username=" + URLEncoder.encode(username, "UTF-8");
	        
	    } catch (SQLException e) {
	        // Print the stack trace and return an INTERNAL_SERVER_ERROR response if a SQL exception occurs
	        e.printStackTrace();
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error: " + e.getMessage()).build();
	    }
	    
	    // Redirect the user to the success page
	    res.sendRedirect(redirectUrl);
	    return null; // Return null because the redirection is handled directly
	}
	
	
	
	public static String resetPassword(String email, String username, String newPassword) throws IOException, SQLException, ClassNotFoundException {
	    // Encrypt the new password
	    String encryptedPwd = encryptPassword(newPassword);
	    
	    // SQL query to check if the username exists
	    String usernameCheckQuery = "SELECT COUNT(*) FROM user_table WHERE user_name = ?";
	    
	    // SQL query to check if the email matches the username
	    String emailCheckQuery = "SELECT COUNT(*) FROM user_table WHERE user_name = ? AND email = ?";
	    
	    // SQL query to update the password for the specified username and email
	    String updateQuery = "UPDATE user_table SET password = ? WHERE user_name = ? AND email = ?";
	    
	    try (Connection con = ConnectDb.connectdb(); // Establish a database connection
	         PreparedStatement usernameCheckStmt = con.prepareStatement(usernameCheckQuery);
	         PreparedStatement emailCheckStmt = con.prepareStatement(emailCheckQuery);
	         PreparedStatement updateStmt = con.prepareStatement(updateQuery)) {
	        
	        // Check if the username exists in the database
	        usernameCheckStmt.setString(1, username);
	        ResultSet rs = usernameCheckStmt.executeQuery();
	        
	        if (rs.next()) {
	            int usernameCount = rs.getInt(1);
	            
	            if (usernameCount == 0) {
	                // Username is not found; return an error message with a form to try again
	                return "Username is not valid. " 
	                        + "<form action='http://localhost:8542/user/forgetpassword.html' method='get'>"
	                        + "<button type='submit'>Please try again</button>"
	                        + "</form>";
	            }
	            
	            // Check if the email matches the username
	            emailCheckStmt.setString(1, username);
	            emailCheckStmt.setString(2, email);
	            rs = emailCheckStmt.executeQuery();
	            
	            if (rs.next()) {
	                int emailCount = rs.getInt(1);
	                
	                if (emailCount == 0) {
	                    // Email does not match the username; return an error message with a form to try again
	                    return "Email is not valid for the provided username. " 
	                            + "<form action='http://localhost:8542/user/forgetpassword.html' method='get'>"
	                            + "<button type='submit'>Please Try Again</button>"
	                            + "</form>";
	                }
	                
	                // Update the password in the database
	                updateStmt.setString(1, encryptedPwd);
	                updateStmt.setString(2, username);
	                updateStmt.setString(3, email);
	                int rowsAffected = updateStmt.executeUpdate();
	                
	                if (rowsAffected > 0) {
	                    // Password was updated successfully; return a success message with a form to go back to login
	                    return "Password reset successfully."
	                            + "<form action='http://localhost:8542/user/index.jsp' method='get'>"
	                            + "<button type='submit'>Go back to login page</button>"
	                            + "</form>";
	                } else {
	                    // An error occurred while updating the password; return an error message with a form to try again
	                    return "An error occurred while resetting the password."
	                            + "<form action='http://localhost:8542/user/forgetpassword.html' method='get'>"
	                            + "<button type='submit'>Please try again</button>"
	                            + "</form>";
	                }
	            } else {
	                // Error occurred while verifying the email; return an error message with a form to try again
	                return "An error occurred while verifying email."
	                        + "<form action='http://localhost:8542/user/forgetpassword.html' method='get'>"
	                        + "<button type='submit'>Please try again</button>"
	                        + "</form>";
	            }
	        } else {
	            // Error occurred while verifying the username; return an error message with a form to go back
	            return "An error occurred while verifying username."
	                    + "<form action='http://localhost:8542/user/forgetpassword.html' method='get'>"
	                    + "<button type='submit'>Go back</button>"
	                    + "</form>";
	        }
	        
	    } catch (SQLException e) {
	        // Print the stack trace and return a generic error message if an SQL exception occurs
	        e.printStackTrace();
	        return "An error occurred while resetting the password.";
	    }
	}

    static String encryptPassword(String password) {
        String check = "ABCDEFGHI,JKLMNOPQR,STUVWXYZa,bcdefghij,klmnopqrs,tuvwxyz01,23456789`,~!@#$%^&*,()-_=+[{],}|;:',<.>,/?";
        String[] arr = check.split(",");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);
            for (int j = 0; j < arr.length; j++) {
                if (arr[j].indexOf(ch) != -1) {
                    sb.append(j);
                    sb.append(arr[j].indexOf(ch));
                }
            }
        }
        return sb.toString();
    }
    
    
    public static String getUserType() throws ClassNotFoundException {
        try (Connection con = ConnectDb.connectdb(); // Establish a database connection
             PreparedStatement pst = con.prepareStatement("SELECT COUNT(*) FROM user_table"); // Prepare a statement to count records
             ResultSet rs = pst.executeQuery()) { // Execute the query
            if (rs.next()) { // Check if there are results
                int count = rs.getInt(1); // Get the count of records
                return count > 0 ? "User" : "SuperAdmin"; // Return "User" if records exist, otherwise "SuperAdmin"
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print stack trace if an SQL exception occurs
        }
        return "SuperAdmin"; // Default return value if an exception occurs
    }
	
	
    public static String generateUniqueUsername(Connection con, String baseUsername) throws SQLException {
        String query = "SELECT COUNT(*) FROM user_table WHERE user_name LIKE ?"; // Query to count existing usernames
        try (PreparedStatement pst = con.prepareStatement(query)) { // Prepare the statement
            pst.setString(1, baseUsername + "%"); // Set the base username with a wildcard to match existing usernames
            try (ResultSet rs = pst.executeQuery()) { // Execute the query
                int count = 0;
                if (rs.next()) {
                    count = rs.getInt(1); // Get the count of existing usernames
                }
                if (count == 0) {
                    return baseUsername; // Return baseUsername if no match is found
                }
                return baseUsername + (count); // Append the count to the baseUsername to make it unique
            }
        }
    }
	
	
    public static boolean isPasswordStrong(String pwd) {
        final String CAPITAL_LETTER_PATTERN = ".*[A-Z].*"; // Regex pattern for uppercase letters
        final String SMALL_LETTER_PATTERN = ".*[a-z].*"; // Regex pattern for lowercase letters
        final String SPECIAL_CHAR_PATTERN = ".*[!@#$%^&*(),.?\":{}|<>].*"; // Regex pattern for special characters
        final int MIN_LENGTH = 10; // Minimum password length
        return pwd.length() >= MIN_LENGTH && Pattern.matches(CAPITAL_LETTER_PATTERN, pwd) // Check length and uppercase
                && Pattern.matches(SMALL_LETTER_PATTERN, pwd) && Pattern.matches(SPECIAL_CHAR_PATTERN, pwd); // Check lowercase and special characters
    }
	
	
    private String encryptPassword() {
        String check = "ABCDEFGHI,JKLMNOPQR,STUVWXYZa,bcdefghij,klmnopqrs,tuvwxyz01,23456789`,~!@#$%^&*,()-_=+[{],}|;:',<.>,/?"; // Character set for encryption
        String[] arr = check.split(","); // Split the character set into arrays of characters
        StringBuilder sb = new StringBuilder(); // StringBuilder to build the encrypted password
        for (int i = 0; i < userPwd.length(); i++) { // Iterate over each character in the password
            char ch = userPwd.charAt(i); // Get the current character
            for (int j = 0; j < arr.length; j++) { // Iterate over the character arrays
                if (arr[j].indexOf(ch) != -1) { // Check if the character is in the current array
                    sb.append(j); // Append the index of the array
                    sb.append(arr[j].indexOf(ch)); // Append the index of the character within the array
                }
            }
        }
        return sb.toString(); // Return the encrypted password
    }
	
	
	/////////////////////////////admin////////////////////////////////////
	
	
    public String removeuser() throws Exception {
        // StringBuilder to construct the HTML content
        StringBuilder dropDown = new StringBuilder();
        dropDown.append("<!DOCTYPE html>") // Define the document type
                 .append("<html lang='en'>") // Start HTML document
                 .append("<head>") // Start head section
                 .append("<meta charset='UTF-8'>") // Character encoding
                 .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>") // Responsive design settings
                 .append("<title>Remove User</title>") // Title of the page
                 .append("<style>") // Start CSS styling
                 .append("body { font-family: Arial, sans-serif; margin: 20px; }") // Style for the body
                 .append("form { background-color: #f9f9f9; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }") // Style for the form
                 .append("label { font-weight: bold; }") // Style for labels
                 .append("select { padding: 8px; margin-right: 10px; }") // Style for the select element
                 .append("button { background-color: #4CAF50; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; }") // Style for buttons
                 .append("button:hover { background-color: #45a049; }") // Style for button hover state
                 .append("</style>") // End CSS styling
                 .append("</head>") // End head section
                 .append("<body>") // Start body section
                 .append("<h1>Remove User</h1>") // Header for the page
                 .append("<form action='removeusersaction' method='post'>") // Form action and method
                 .append("<label for='dropdown'>Select a User To Delete:</label>") // Label for the dropdown
                 .append("<select id='dropdown' name='username' required>"); // Start dropdown selection

        try (Connection c = ConnectDb.connectdb(); // Establish database connection
             PreparedStatement pst1 = c.prepareStatement("SELECT user_name FROM user_table WHERE user_type ='User'")) { // Prepare SQL statement to select usernames

            try (ResultSet rs = pst1.executeQuery()) { // Execute query
                boolean userFound = false; // Flag to check if any users are found
                while (rs.next()) { // Iterate through the result set
                    userFound = true; // Set flag to true if at least one user is found
                    String value = rs.getString("user_name"); // Get username from result set
                    dropDown.append("<option value='").append(value).append("'>").append(value).append("</option>"); // Add option to dropdown
                }
                if (!userFound) { // If no users were found
                    dropDown.append("<option value='' disabled selected>No Users Available</option>"); // Add a disabled option indicating no users are available
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Print stack trace if an SQL exception occurs
            throw new Exception("Database error occurred while fetching user list.", e); // Throw a new exception with a message
        }

        dropDown.append("</select>") // Close the select element
                .append("<button type='submit'>Submit</button>") // Add submit button
                .append("</form>") // Close the form
                .append("</body>") // End body section
                .append("</html>"); // End HTML document
        
        return dropDown.toString(); // Return the generated HTML as a string
    }
	
	
    public String removeuseraction(String username) throws SQLException, ClassNotFoundException {
        String resultMessage; // Variable to store the result message

        try (Connection c = ConnectDb.connectdb()) { // Establish a database connection
            // Query to delete the user from the user_table
            String query1 = "DELETE FROM user_table WHERE user_name = ?";
            try (PreparedStatement pst1 = c.prepareStatement(query1)) { // Prepare the statement for user deletion
                pst1.setString(1, username); // Set the username parameter
                pst1.executeUpdate(); // Execute the delete operation
                //System.out.println("Deleted user"); // Optional debugging line
            }

            // Query to delete associated requests for the user
            String query2 = "DELETE FROM requests WHERE user_name = ?";
            try (PreparedStatement pst2 = c.prepareStatement(query2)) { // Prepare the statement for request deletion
                pst2.setString(1, username); // Set the username parameter
                pst2.executeUpdate(); // Execute the delete operation
            }

            // Query to delete associated resources for the user
            String query3 = "DELETE FROM user_resources WHERE user_name = ?";
            try (PreparedStatement pst3 = c.prepareStatement(query3)) { // Prepare the statement for resource deletion
                pst3.setString(1, username); // Set the username parameter
                pst3.executeUpdate(); // Execute the delete operation
            }

            // Success message indicating the user has been deleted
            resultMessage = "<h1>User has been deleted successfully.</h1>"
                    + "<form action='http://localhost:8542/user/webapi/myresource/removeuser' method='get'>" // Correct the 'method' attribute
                    + "<button type='submit'>Go back</button>"
                    + "</form>";
        } catch (SQLException e) {
            // Error message if there is an issue during the deletion process
            resultMessage = "<h1 style='color: red;'>Error occurred while deleting user.</h1>"
                    + "<form action='http://localhost:8542/user/webapi/myresource/removeuser' method='get'>" // Correct the 'method' attribute
                    + "<button type='submit'>Go back</button>"
                    + "</form>";
            throw new SQLException("Error during user deletion", e); // Re-throw the exception with a descriptive message
        }

        return resultMessage; // Return the result message
    }
    
    
    public String adduser() {
        // HTML content for creating a new user account
        String htmlContent = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Create Account</title>\n" +
                "    <style>\n" +
                "        /* Basic styling for the body */\n" +
                "        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f4f4f4; }\n" +
                "        /* Container styling for centering the form */\n" +
                "        .container { max-width: 600px; margin: auto; padding: 20px; background: white; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }\n" +
                "        /* Styling for heading */\n" +
                "        h1 { margin-bottom: 20px; }\n" +
                "        /* Styling for input groups */\n" +
                "        .input-group { margin-bottom: 15px; }\n" +
                "        /* Styling for labels */\n" +
                "        label { display: block; margin-bottom: 5px; font-weight: bold; }\n" +
                "        /* Styling for inputs */\n" +
                "        input { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; }\n" +
                "        /* Styling for buttons */\n" +
                "        button { background-color: #4CAF50; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; }\n" +
                "        button:hover { background-color: #45a049; }\n" +
                "        /* Styling for error messages */\n" +
                "        .error-message { color: red; margin-top: 10px; }\n" +
                "    </style>\n" +
                "    <script src=\"scripts.js\" defer></script>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <h1>Create Account for User</h1>\n" +
                "        <form id=\"registrationForm\" action=\"createaccountaction\" method=\"POST\" onsubmit=\"return validateForm()\">\n" +
                "            <!-- Input field for first name -->\n" +
                "            <div class=\"input-group\">\n" +
                "                <label for=\"firstname\">First Name</label>\n" +
                "                <input type=\"text\" id=\"firstname\" name=\"firstname\" required>\n" +
                "            </div>\n" +
                "            <!-- Input field for last name -->\n" +
                "            <div class=\"input-group\">\n" +
                "                <label for=\"lastname\">Last Name</label>\n" +
                "                <input type=\"text\" id=\"lastname\" name=\"lastname\" required>\n" +
                "            </div>\n" +
                "            <!-- Input field for email address -->\n" +
                "            <div class=\"input-group\">\n" +
                "                <label for=\"email\">Email</label>\n" +
                "                <input type=\"email\" id=\"email\" name=\"email\" required>\n" +
                "            </div>\n" +
                "            <!-- Submit button to create the account -->\n" +
                "            <button type=\"submit\">Create Account</button>\n" +
                "        </form>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        // Return the HTML content
        return htmlContent;
    }


    public String ListOfUsers(String name) throws Exception {
        // StringBuilder to construct the HTML content
        StringBuilder htmlBuilder = new StringBuilder();
        
        // Start HTML document and page styling
        htmlBuilder.append("<!DOCTYPE html>")
                   .append("<html lang='en'>")
                   .append("<head>")
                   .append("<meta charset='UTF-8'>")
                   .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                   .append("<title>All User Details</title>")
                   .append("<style>")
                   .append("body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f7f9; color: #333; }")
                   .append(".container { max-width: 1300px; margin: 1px auto; background: #fff; padding: 15px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }")
                   .append(".header { margin-bottom: 15px; text-align: center; padding-bottom: 15px; border-bottom: 2px solid #007BFF; }")
                   .append(".header h1 { font-size: 24px; color: #007BFF; }")
                   .append(".header p { font-size: 16px; color: #333; }")
                   .append(".table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }")
                   .append(".table th, .table td { padding: 8px; border: 1px solid #ddd; text-align: left; }")
                   .append(".table th { background-color: #007BFF; color: #fff; }")
                   .append(".table tr:nth-child(even) { background-color: #f2f2f2; }")
                   .append(".form-group { margin-bottom: 15px; }")
                   .append(".form-group label { display: block; font-size: 14px; margin-bottom: 5px; color: #333; }")
                   .append(".form-group input { width: 100%; padding: 8px; font-size: 14px; border: 1px solid #ddd; border-radius: 6px; box-sizing: border-box; }")
                   .append(".form-group input[type='submit'] { background-color: #28a745; color: #fff; border: none; cursor: pointer; transition: background-color 0.3s; }")
                   .append(".form-group input[type='submit']:hover { background-color: #218838; }")
                   .append(".button:hover { background-color: #0056b3; transform: scale(1.05); }")
                   .append(".edit-form { display: none; margin-top: 10px; }")
                   .append(".edit-form.active { display: block; }")
                   .append(".edit-button { display: inline-block; padding: 8px 15px; font-size: 14px; color: #fff; background-color: #007BFF; border: none; border-radius: 6px; cursor: pointer; transition: background-color 0.3s; }")
                   .append(".edit-button:hover { background-color: #0056b3; }")
                   .append("</style>")
                   .append("</head>")
                   .append("<body>")
                   .append("<div class='container'>")
                   .append("<div class='header'>")
                   .append("<h1>All User Details</h1>")
                   .append("<p>View and edit user details below.</p>")
                   .append("</div>")
                   .append("<table class='table'>")
                   .append("<thead>")
                   .append("<tr>")
                   .append("<th>Username</th>")
                   .append("<th>First Name</th>")
                   .append("<th>Last Name</th>")
                   .append("<th>Email</th>")
                   .append("<th>Actions</th>")
                   .append("</tr>")
                   .append("</thead>")
                   .append("<tbody>");

        // Query the database to retrieve user details
        try (Connection c = ConnectDb.connectdb();
             PreparedStatement pst = c.prepareStatement("SELECT user_name, first_name, last_name, email FROM user_table WHERE user_type != 'SuperAdmin'");
             ResultSet rs = pst.executeQuery()) {

            // Iterate through the result set and build the table rows
            while (rs.next()) {
                String userName = rs.getString("user_name");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String email = rs.getString("email");

                // Append user details to the table
                htmlBuilder.append("<tr>")
                           .append("<td>").append(userName).append("</td>")
                           .append("<td>").append(firstName).append("</td>")
                           .append("<td>").append(lastName).append("</td>")
                           .append("<td>").append(email).append("</td>")
                           .append("<td>")
                           .append("<button type='button' class='edit-button' onclick='toggleEditForm(\"").append(userName).append("\")'>Edit</button>")
                           .append("</td>")
                           .append("</tr>")
                           .append("<tr>")
                           .append("<td colspan='5'>")
                           .append("<form action='updateuserdetails' method='post' class='edit-form' id='form-").append(userName).append("'>")
                           .append("<input type='hidden' name='username' value='").append(userName).append("'>")
                           .append("<div class='form-group'>")
                           .append("<label for='firstname-").append(userName).append("'>First Name:</label>")
                           .append("<input type='text' id='firstname-").append(userName).append("' name='firstname' value='").append(firstName).append("'>")
                           .append("</div>")
                           .append("<div class='form-group'>")
                           .append("<label for='lastname-").append(userName).append("'>Last Name:</label>")
                           .append("<input type='text' id='lastname-").append(userName).append("' name='lastname' value='").append(lastName).append("'>")
                           .append("</div>")
                           .append("<div class='form-group'>")
                           .append("<label for='email-").append(userName).append("'>Email:</label>")
                           .append("<input type='email' id='email-").append(userName).append("' name='email' value='").append(email).append("'>")
                           .append("</div>")
                           .append("<div class='form-group'>")
                           .append("<input type='submit' value='Update'>")
                           .append("</div>")
                           .append("</form>")
                           .append("</td>")
                           .append("</tr>");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Database error occurred while fetching user details.", e);
        }

        // End the table and HTML document
        htmlBuilder.append("</tbody>")
                   .append("</table>")
                   .append("</div>")
                   .append("<script>")
                   .append("function toggleEditForm(username) {")
                   .append("  var form = document.getElementById('form-' + username);")
                   .append("  if (form.classList.contains('active')) {")
                   .append("    form.classList.remove('active');")
                   .append("  } else {")
                   .append("    form.classList.add('active');")
                   .append("  }")
                   .append("}")
                   .append("</script>")
                   .append("</body>")
                   .append("</html>");

        // Return the constructed HTML content
        return htmlBuilder.toString();
    }
    
    
    public String updateUserDetails(String username, String firstName, String lastName, String email) 
            throws SQLException, ClassNotFoundException {
        // SQL query to update user details
        String sql = "UPDATE user_table SET first_name = ?, last_name = ?, email = ? WHERE user_name = ?";
        
        // Establish a database connection
        Connection connection = ConnectDb.connectdb();
        
        // Use a try-with-resources statement to ensure the PreparedStatement is closed automatically
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // Set parameters for the SQL query
            preparedStatement.setString(1, firstName);  // Set the new first name
            preparedStatement.setString(2, lastName);   // Set the new last name
            preparedStatement.setString(3, email);      // Set the new email
            preparedStatement.setString(4, username);   // Specify the username of the user to update

            // Execute the update operation
            preparedStatement.executeUpdate();          
        }

        // Return a confirmation message with a link to go back to the user list
        return "User Details Updated Successfully "
               + "<form action='http://localhost:8542/user/webapi/myresource/listofusers' method='get'>"
               + "<button type='submit'>Go back</button>"
               + "</form>";
    }



    public String KnowYourManager(String name) throws ClassNotFoundException, SQLException {
        // Create a StringBuilder to build the HTML content
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>")
                   .append("<html>")
                   .append("<head>")
                   .append("<style>")
                   .append("table { width: 100%; border-collapse: collapse; margin: 20px 0; }")
                   .append("th, td { padding: 12px; text-align: left; border: 1px solid #ddd; }")
                   .append("th { background-color: #f4f4f4; }")
                   .append("</style>")
                   .append("</head>")
                   .append("<body>")
                   .append("<h2>Manager Information for User: ").append(name).append("</h2>")
                   .append("<table>")
                   .append("<tr><th>Manager ID</th></tr>");
        
        // Initialize database resources
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            // Establish a connection to the database
            con = ConnectDb.connectdb();
            
            // Prepare the SQL query to get the manager name for the specified user
            String query = "SELECT manager_name FROM user_table WHERE user_name = ?";
            stmt = con.prepareStatement(query);
            stmt.setString(1, name);
            
            // Execute the query and obtain the result
            rs = stmt.executeQuery();
            
            // Check if a result was returned
            if (rs.next()) {
                String managerId = rs.getString("manager_name");
                // Display the manager ID or a message if no manager is assigned
                htmlBuilder.append("<tr><td>").append(managerId != null ? managerId : "No manager assigned").append("</td></tr>");
            } else {
                // If no user was found with the specified username
                htmlBuilder.append("<tr><td>No manager found for the specified user.</td></tr>");
            }
            
        } catch (SQLException e) {
            // Print the stack trace and display an error message in the HTML
            e.printStackTrace();
            htmlBuilder.append("<tr><td>Error occurred while retrieving manager information.</td></tr>");
        } finally {
            // Ensure resources are closed properly
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (con != null) try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        
        // Complete the HTML content
        htmlBuilder.append("</table>")
                   .append("</body>")
                   .append("</html>");
        
        // Return the generated HTML content
        return htmlBuilder.toString();
    }
	
    public static String changePassword() {
        // Start building the HTML content
        return "<!DOCTYPE html>" +
               "<html lang='en'>" +
               "<head>" +
               "<meta charset='UTF-8'>" +
               "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
               "<title>Change Password</title>" +
               "<style>" +
               // Basic styling for the body and container
               "body { font-family: Arial, sans-serif; margin: 20px; background-color: #f4f4f4; }" +
               ".container { max-width: 600px; margin: auto; padding: 20px; background: white; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }" +
               "h1 { margin-bottom: 20px; }" +
               ".input-group { margin-bottom: 15px; }" +
               "label { display: block; margin-bottom: 5px; font-weight: bold; }" +
               "input[type='password'] { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; }" +
               "button { background-color: #4CAF50; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; }" +
               "button:hover { background-color: #45a049; }" +
               ".error-message { color: red; margin-top: 10px; }" +
               "</style>" +
               "</head>" +
               "<body>" +
               "<div class='container'>" +
               "<h1>Change Password</h1>" +
               // Start of the form for changing the password
               "<form id='changePasswordForm' action='changepassaction' method='post'>" +
               "<div class='input-group'>" +
               "<label for='new-password'>New Password</label>" +
               // Input for the new password with validation pattern
               "<input type='password' id='new-password' name='new-password' required " +
               "pattern='(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}' " +
               "title='Password must be at least 8 characters long, and include at least one uppercase letter, one lowercase letter, one number, and one special character (@, $, !, %, *, ?, &).' />" +
               "</div>" +
               "<div class='input-group'>" +
               "<label for='confirm-password'>Confirm New Password</label>" +
               // Input for confirming the new password
               "<input type='password' id='confirm-password' name='confirm-password' required />" +
               // Placeholder for error messages
               "<div id='error-message' class='error-message'></div>" +
               "</div>" +
               // Submit button for the form
               "<button type='submit'>Change Password</button>" +
               "</form>" +
               // JavaScript for client-side validation
               "<script>" +
               "document.getElementById('changePasswordForm').onsubmit = function() {" +
               "    var password = document.getElementById('new-password').value;" +
               "    var confirmPassword = document.getElementById('confirm-password').value;" +
               "    var errorMessage = document.getElementById('error-message');" +
               // Check if the new password and confirm password match
               "    if (password !== confirmPassword) {" +
               "        errorMessage.textContent = 'Passwords do not match.';" +
               "        return false;" +
               "    }" +
               "    errorMessage.textContent = '';" +
               "    return true;" +
               "};" +
               "</script>" +
               "</div>" +
               "</body>" +
               "</html>";
    }


    public static String changePasswordaction(String username, String newPassword, String confirmPassword) {
        String resultMessage = "";

        // Validate the new password
        if (newPassword == null || newPassword.trim().isEmpty()) {
            resultMessage = "<p class='error-message'>New password cannot be empty.</p>";
        } else if (!newPassword.equals(confirmPassword)) {
            resultMessage = "<p class='error-message'>New passwords do not match.</p>";
        } else {
            try {
                // Encrypt the new password
                String encryptedPwd = encryptPassword(newPassword);

                // SQL query to update the password in the database
                String query = "UPDATE user_table SET password = ? WHERE user_name = ?";
                try (Connection con = ConnectDb.connectdb();
                     PreparedStatement pst = con.prepareStatement(query)) {
                    
                    // Set the parameters for the prepared statement
                    pst.setString(1, encryptedPwd);
                    pst.setString(2, username);

                    // Execute the update and check if the update was successful
                    int rowsAffected = pst.executeUpdate();

                    if (rowsAffected > 0) {
                        resultMessage = "<p>Password has been updated successfully.</p>"
                                + "<form action='http://localhost:8542/user/index.jsp' method='get'>"
                                + "<button type='submit'>You have to Login again</button>"
                                + "</form>";
                    } else {
                        resultMessage = "<p class='error-message'>User not found.</p>"
                                + "<form action='http://localhost:8542/user/webapi/myresource/changepass' method='get'>"
                                + "<button type='submit'>Go back</button>"
                                + "</form>";
                    }
                }
            } catch (Exception e) {
                resultMessage = "<p class='error-message'>An error occurred: " + e.getMessage() + "</p>";
            }
        }

        return resultMessage;
    }
	
	
    public static void insertUser(String firstName, String lastName, String userName, String email) throws SQLException, ClassNotFoundException {
        // Get the current date in 'yyyy-MM-dd' format
        String dateString = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

        // SQL query to insert a new user into the database
        String sql = "INSERT INTO user_table (First_Name, Last_Name, User_Name, Email, user_type, date_of_joining, password) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConnectDb.connectdb();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            // Set the parameters for the prepared statement
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, generateUniqueUsername(connection,firstName+"."+lastName));
            statement.setString(4, email);
            statement.setString(5, "User"); // Set user_type as "User"
            statement.setString(6, dateString); // Set current date as date_of_joining
            statement.setString(7, encryptPassword(firstName + lastName)); // Encrypt password using first and last name
            
            // Execute the update
            statement.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error inserting user into database: " + e.getMessage(), e);
        }
    }

    public static void insertResource(String resourceName) throws SQLException, ClassNotFoundException {
        // SQL query to insert a new resource into the database
        String sql = "INSERT INTO resource (Resource_Name) VALUES (?)";

        try (Connection connection = ConnectDb.connectdb();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Set the parameter for the prepared statement
            statement.setString(1, resourceName);

            // Execute the update
            statement.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error inserting resource into database: " + e.getMessage(), e);
        }
    }
	

}
