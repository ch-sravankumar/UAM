package UAM.user;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;


public class Requests {
	private String requestType;
	private String userName;
	private String status;
	private Date dateOfRequest;
	public Requests(String requestType, String userName, String status, Date dateOfRequest) {
		this.requestType = requestType;
		this.userName = userName;
		this.status = status;
		this.dateOfRequest = dateOfRequest;
	}
	public Requests() {
		
	}
	
	public String listOfRequests() throws SQLException, ClassNotFoundException {
		StringBuilder html = new StringBuilder();
		html.append("<!DOCTYPE html><html><head>");
		html.append("<style>");
		html.append("body { font-family: Arial, sans-serif; margin: 20px; }");
		html.append("table.requests-table { width: 100%; border-collapse: collapse; margin: 20px 0; font-size: 14px; }");
		html.append("table.requests-table th, table.requests-table td { padding: 12px; border: 1px solid #ddd; }");
		html.append("table.requests-table th { background-color: #4CAF50; color: #fff; text-align: center; }");
		html.append("table.requests-table td { text-align: left; }");
		html.append("table.requests-table tr:nth-child(even) { background-color: #f9f9f9; }");
		html.append("table.requests-table tr:hover { background-color: #f1f1f1; }");
		html.append("table.requests-table button { padding: 8px 16px; margin: 0 5px; border: none; border-radius: 4px; cursor: pointer; color: #fff; font-size: 14px; }");
		html.append("table.requests-table button.accept { background-color: #4CAF50; }");
		html.append("table.requests-table button.reject { background-color: #f44336; }");
		html.append("form { display: inline; }");
		html.append("</style>");
		html.append("</head><body>");
		try (Connection connection = ConnectDb.connectdb();
				PreparedStatement preparedStatement = connection.prepareStatement(
						"SELECT * FROM requests WHERE status NOT IN ('approved', 'rejected')");
				ResultSet resultSet = preparedStatement.executeQuery()) {

			if (!resultSet.isBeforeFirst()) {
				return "<p>There are no requests.</p>";
			}
			html.append("<table class='requests-table'>");
			html.append("<thead>");
			html.append("<tr>");
			html.append("<th>Request ID</th>");
			html.append("<th>Requested For</th>");
			html.append("<th>Requested From</th>");
			html.append("<th>Date Of Request</th>");
			html.append("<th>Status</th>");
			html.append("<th>Actions</th>");
			html.append("</tr>");
			html.append("</thead>");
			html.append("<tbody>");
			while (resultSet.next()) {
				int requestId = resultSet.getInt("request_id");
				String requestedFor = resultSet.getString("request_type");
				String requestedFrom = resultSet.getString("user_name");
				String dateOfRequesting = resultSet.getString("date_of_request");
				String status = resultSet.getString("status");
				html.append("<tr>");
				html.append("<td>").append(requestId).append("</td>");
				html.append("<td>").append(requestedFor).append("</td>");
				html.append("<td>").append(requestedFrom).append("</td>");
				html.append("<td>").append(dateOfRequesting).append("</td>");
				html.append("<td>").append(status).append("</td>");
				html.append("<td>");
				html.append("<form action='check_requests_action' method='post'>");
				html.append("<input type='hidden' name='requestId' value='").append(requestId).append("' />");
				html.append("<input type='hidden' name='action' value='approve' />");
				html.append("<input type='hidden' name='username' value='").append(requestedFrom).append("' />");
				html.append("<input type='hidden' name='resname' value='").append(requestedFor).append("' />");
				html.append("<button type='submit' class='accept'>Accept</button>");
				html.append("</form>");
				html.append("<form action='check_requests_action' method='post'>");
				html.append("<input type='hidden' name='requestId' value='").append(requestId).append("' />");
				html.append("<input type='hidden' name='action' value='reject' />");
				html.append("<input type='hidden' name='username' value='").append(requestedFrom).append("' />");
				html.append("<input type='hidden' name='resname' value='").append(requestedFor).append("' />");
				html.append("<button type='submit' class='reject'>Reject</button>");
				html.append("</form>");
				html.append("</td>");
				html.append("</tr>");
			}
			html.append("</tbody>");
			html.append("</table>");
		} catch (SQLException e) {
			e.printStackTrace();
			throw e; 
		}
		html.append("</body></html>");
		return html.toString();
	}
	
	
	public String requests() throws ClassNotFoundException, SQLException {
	    StringBuilder html = new StringBuilder();

	    // Start building the HTML content
	    html.append("<!DOCTYPE html><html><head>");
	    html.append("<style>");

	    // Basic styles for the page
	    html.append("body { font-family: Arial, sans-serif; margin: 20px; }");

	    // Styles for the table
	    html.append("table.requests-table { width: 100%; border-collapse: collapse; margin: 20px 0; font-size: 14px; }");
	    html.append("table.requests-table th, table.requests-table td { padding: 12px; border: 1px solid #ddd; }");
	    html.append("table.requests-table th { background-color: #4CAF50; color: #fff; text-align: center; }");
	    html.append("table.requests-table td { text-align: left; }");

	    // Zebra striping for table rows
	    html.append("table.requests-table tr:nth-child(even) { background-color: #f9f9f9; }");
	    html.append("table.requests-table tr:hover { background-color: #f1f1f1; }");

	    // Conditional styling for accepted and rejected rows
	    html.append("table.requests-table .accepted { background-color: #d4edda; color: #155724; }");
	    html.append("table.requests-table .rejected { background-color: #f8d7da; color: #721c24; }");

	    html.append("</style>");
	    html.append("</head><body>");

	    // Database query and HTML table generation
	    try (Connection connection = ConnectDb.connectdb();
	         PreparedStatement preparedStatement = connection.prepareStatement(
	                 "SELECT * FROM requests WHERE status NOT IN ('pending')");
	         ResultSet resultSet = preparedStatement.executeQuery()) {

	        // Check if there are no results
	        if (!resultSet.isBeforeFirst()) {
	            return "<p>There are no requests.</p>";
	        }

	        // Start building the HTML table
	        html.append("<table class='requests-table'>");
	        html.append("<thead>");
	        html.append("<tr>");
	        html.append("<th>Request ID</th>");
	        html.append("<th>Requested For</th>");
	        html.append("<th>Requested From</th>");
	        html.append("<th>Date Of Request</th>");
	        html.append("<th>Status</th>");
	        html.append("</tr>");
	        html.append("</thead>");
	        html.append("<tbody>");

	        // Process each row in the result set
	        while (resultSet.next()) {
	            int requestId = resultSet.getInt("request_id");
	            String requestedFor = resultSet.getString("request_type");
	            String requestedFrom = resultSet.getString("user_name");
	            String dateOfRequesting = resultSet.getString("date_of_request");
	            String status = resultSet.getString("status");

	            // Determine the row class based on the status value
	            String statusClass;
	            switch (status.toLowerCase()) {
	                case "accepted":
	                    statusClass = "accepted";
	                    break;
	                case "rejected":
	                    statusClass = "rejected";
	                    break;
	                default:
	                    statusClass = ""; // No special class if status is unknown or pending
	                    break;
	            }

	            // Build the HTML for each table row
	            html.append("<tr class='").append(statusClass).append("'>");
	            html.append("<td>").append(requestId).append("</td>");
	            html.append("<td>").append(requestedFor).append("</td>");
	            html.append("<td>").append(requestedFrom).append("</td>");
	            html.append("<td>").append(dateOfRequesting).append("</td>");
	            html.append("<td>").append(status).append("</td>");
	            html.append("</tr>");
	        }

	        html.append("</tbody>");
	        html.append("</table>");
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw e; // Re-throw exception for error handling
	    }

	    html.append("</body></html>");
	    return html.toString(); // Return the generated HTML content
	}



	



	
	
	public String processRequestAction(int requestId, String resname, String action, String username, @Context HttpServletRequest req) throws SQLException, ClassNotFoundException {
	    // Establish a database connection
	    Connection connection1 = ConnectDb.connectdb();
	    String superadminname = null;

	    // Query to get the SuperAdmin's username
	    String query = "SELECT user_name FROM user_table WHERE user_type = 'SuperAdmin'";
	    try (Connection connection = ConnectDb.connectdb(); 
	         PreparedStatement statement = connection.prepareStatement(query);
	         ResultSet resultSet = statement.executeQuery()) {
	        // Retrieve the SuperAdmin's username
	        if (resultSet.next()) {
	            superadminname = resultSet.getString("user_name");
	        }
	    }

	    String status;

	    // Check if the action is to approve the request
	    if ("approve".equalsIgnoreCase(action)) {
	        status = "approved";

	        // Handle specific cases based on the resource name
	        if (resname.equals("ManagertoAdmin")) {
	            HttpSession session = req.getSession();
	            String teammembername = (String) session.getAttribute("teammembername");

	            // Check if the session contains the team member's name
	            if (teammembername == null) {
	                return "No value in session for team member.";
	            }

	            PreparedStatement updateStatement1 = null;
	            PreparedStatement updateStatement2 = null;
	            PreparedStatement updateStatement3 = null;
	            PreparedStatement updateStatement4 = null;

	            try {
	                // Update user roles and manager names
	                String update1 = "UPDATE user_table SET user_type = 'Admin' WHERE user_name = ?";
	                updateStatement1 = connection1.prepareStatement(update1);
	                updateStatement1.setString(1, username);
	                updateStatement1.executeUpdate();

	                String update3 = "UPDATE user_table SET manager_name = ? WHERE manager_name=?";
	                updateStatement3 = connection1.prepareStatement(update3);
	                updateStatement3.setString(1, teammembername);
	                updateStatement3.setString(2, username);
	                updateStatement3.executeUpdate();

	                String update2 = "UPDATE user_table SET user_type = 'Manager', manager_name = ? WHERE user_name = ?";
	                updateStatement2 = connection1.prepareStatement(update2);
	                updateStatement2.setString(1, superadminname);
	                updateStatement2.setString(2, teammembername);
	                updateStatement2.executeUpdate();

	                String update4 = "UPDATE user_table SET manager_name = ? WHERE user_name = ?";
	                updateStatement4 = connection1.prepareStatement(update4);
	                updateStatement4.setString(2, username);
	                updateStatement4.setString(1, superadminname);
	                updateStatement4.executeUpdate();

	                // Update request status to approved
	                String updateQuery = "UPDATE requests SET status = ? WHERE request_id = ?";
	                try (PreparedStatement updateStatement = connection1.prepareStatement(updateQuery)) {
	                    updateStatement.setString(1, status);
	                    updateStatement.setInt(2, requestId);
	                    updateStatement.executeUpdate();
	                }
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        } else if (resname.equals("Admin")) {
	            // Update user type to Admin and set manager name
	            String update = "UPDATE user_table SET user_type = 'Admin', manager_name= ? WHERE user_name=  ?";
	            PreparedStatement updateStatement = connection1.prepareStatement(update);
	            updateStatement.setString(2, username);
	            updateStatement.setString(1, superadminname);
	            updateStatement.executeUpdate();
	        } else if (resname.equals("Manager")) {
	            // Update user type to Manager and set manager name
	            String update = "UPDATE user_table SET user_type = 'Manager', manager_name= ? WHERE user_name=  ?";
	            PreparedStatement updateStatement = connection1.prepareStatement(update);
	            updateStatement.setString(2, username);
	            updateStatement.setString(1, superadminname);
	            updateStatement.executeUpdate();
	        } else if (!resname.equals("Admin") && !resname.equals("Manager")) {
	            // Insert new resource assignment for the user
	            String insertQuery = "INSERT INTO user_resources(user_name, resource_name) VALUES (?, ?)";
	            try (PreparedStatement insertStatement = connection1.prepareStatement(insertQuery)) {
	                insertStatement.setString(1, username);
	                insertStatement.setString(2, resname);
	                insertStatement.executeUpdate();
	            }
	            // Note: The following code for updating resource count is commented out
	            // String updateQuery1 = "UPDATE resource SET no_of_users = no_of_users + 1 WHERE resource_name = ?";
	            // try (PreparedStatement updateStatement1 = connection1.prepareStatement(updateQuery1)) {
	            //     updateStatement1.setString(1, resname); 
	            // }
	        }

	        // Update request status to approved
	        String updateQuery = "UPDATE requests SET status = ? WHERE request_id = ?";
	        try (PreparedStatement updateStatement = connection1.prepareStatement(updateQuery)) {
	            updateStatement.setString(1, status);
	            updateStatement.setInt(2, requestId);
	            updateStatement.executeUpdate();
	        }
	    } else if ("reject".equalsIgnoreCase(action)) {
	        // Handle request rejection
	        status = "rejected";
	        String updateQuery = "UPDATE requests SET status = ? WHERE request_id = ?";
	        try (PreparedStatement updateStatement = connection1.prepareStatement(updateQuery)) {
	            updateStatement.setString(1, status);
	            updateStatement.setInt(2, requestId);
	            updateStatement.executeUpdate();
	        }
	    } else {
	        // Handle invalid action
	        throw new IllegalArgumentException("Invalid action: " + action);
	    }

	    // Return the result message with a link to go back
	    return "Request successfully " + status + "."
	             + "<form action='http://localhost:8542/user/webapi/myresource/check_requests' method='get'>"
	             + "<button type='submit'>Go back</button>"
	             + "</form>";
	}

	public String checkApprovals(String uname) throws SQLException, ClassNotFoundException {
	    StringBuilder show = new StringBuilder();
	    
	    // Start HTML document
	    show.append("<!DOCTYPE html><html><head>");
	    
	    // Add CSS styles for the table
	    show.append("<style>");
	    show.append("table.check-approvals-table { width: 100%; border-collapse: collapse; margin: 20px 0; font-size: 16px; text-align: left; }");
	    show.append("table.check-approvals-table th, table.check-approvals-table td { padding: 12px; border: 1px solid #ddd; }");
	    show.append("table.check-approvals-table th { background-color: #f2f2f2; color: #333; }");
	    show.append("table.check-approvals-table tr:nth-of-type(even) { background-color: #f9f9f9; }");
	    show.append("table.check-approvals-table tr:hover { background-color: #e0e0e0; }");
	    show.append(".approved { background-color: #d4edda; color: #155724; }");
	    show.append(".rejected { background-color: #f8d7da; color: #721c24; }");
	    show.append(".pending { background-color: #fff3cd; color: #856404; }");
	    show.append("</style>");
	    
	    // End HTML head and start body
	    show.append("</head><body>");
	    
	    // Start the table and add table headers
	    show.append("<table class='check-approvals-table'><tr><th>RequestId</th><th>DateOfRequesting</th><th>RequestName</th><th>ApprovalStatus</th></tr>");
	    
	    // Establish a database connection and prepare a statement to retrieve requests
	    try (Connection c = ConnectDb.connectdb();
	         PreparedStatement pst = c.prepareStatement("SELECT * FROM requests WHERE user_name=?")) {
	        
	        // Set the username parameter
	        pst.setString(1, uname);
	        
	        // Execute the query and process the result set
	        try (ResultSet rs = pst.executeQuery()) {
	            while (rs.next()) {
	                String status = rs.getString("Status");
	                String statusClass;
	                
	                // Determine the CSS class based on the status
	                switch (status) {
	                    case "approved":
	                        statusClass = "approved";
	                        break;
	                    case "rejected":
	                        statusClass = "rejected";
	                        break;
	                    case "pending":
	                        statusClass = "pending";
	                        break;
	                    default:
	                        statusClass = ""; // No specific class for unknown statuses
	                        break;
	                }
	                
	                // Append table rows with request details and status
	                show.append("<tr>");
	                show.append("<td>").append(rs.getInt("Request_Id")).append("</td>");
	                show.append("<td>").append(rs.getString("Date_Of_Request")).append("</td>");
	                show.append("<td>").append(rs.getString("Request_type")).append("</td>");
	                show.append("<td class='").append(statusClass).append("'>").append(status).append("</td>");
	                show.append("</tr>");
	            }
	        }
	    } catch (SQLException e) {
	        // Print and rethrow the SQL exception
	        e.printStackTrace();
	        throw e; 
	    }
	    
	    // End the table and HTML document
	    show.append("</table>");
	    show.append("</body></html>");
	    
	    // Return the generated HTML string
	    return show.toString();
	}






}
