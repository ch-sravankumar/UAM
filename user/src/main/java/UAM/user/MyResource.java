package UAM.user;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("myresource")
public class MyResource {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }
    
    
    @POST
    @Path("Register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response signUp(@FormParam("firstname") String userFirstName,@FormParam("lastname") String userLastName,@FormParam("email") String email,@FormParam("password") String userPwd,@FormParam("confirmPassword") String cuserPwd,@Context HttpServletResponse res) throws ClassNotFoundException, IOException, ServletException, SQLException {

        if (!userPwd.equals(cuserPwd)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Password does not match").build();
        }
        User user = new User(userFirstName, userLastName, email, userPwd, cuserPwd);
		return user.signUp(res); 
    }
    
    
    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(@FormParam("username") String username, @FormParam("password") String password,@Context HttpServletRequest req) throws ClassNotFoundException, SQLException, IOException {
        User user = new User(username, password);
        HttpSession session=req.getSession();
		session.setAttribute("uname", username);
        String resultMessage = user.login(); 
        if (resultMessage.startsWith("http")) { 
            return Response.seeOther(URI.create(resultMessage)).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity(resultMessage).build();
        }
    }
    
    @POST
    @Path("resetpassword")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML) // Return HTML content
    public String resetPassword(@FormParam("email")String email,@FormParam("username") String username, @FormParam("password") String password,@Context HttpServletRequest req) throws ClassNotFoundException, SQLException, IOException {
        if (username == null || username.trim().isEmpty()) {
            return "<p>Username cannot be empty.</p>";
        }
        if (password == null || password.trim().isEmpty()) {
            return "<p>Password cannot be empty.</p>";
        }
        String resultMessage = User.resetPassword(email,username, password);
        if ("Password reset successfully.".equals(resultMessage)) {
            return "<p>Password reset has been done.</p>" ;
        } else {
            return "<p>" + resultMessage + "</p>";
        }
    }
    
    
    @POST
    @Path("changepassfornewuser")
    public String changepassfornewuser(@FormParam("current_password") String currpwd,
                                        @FormParam("new_password") String npass,
                                        @FormParam("confirm_password") String cpass,
                                        @Context HttpServletRequest req) throws ClassNotFoundException, SQLException, IOException {
        
        // Print input parameters for debugging purposes
        System.out.println("HI" + currpwd + npass + cpass + "HI");
        
        // Encrypt the current and new passwords for comparison and storage
        String encryptedCurrPwd = User.encryptPassword(currpwd);
        String encryptedNewPwd = User.encryptPassword(npass);
        
        // Retrieve the session to get the username of the current user
        HttpSession session = req.getSession();
        String username = session.getAttribute("uname").toString();
        
        // Establish a connection to the database
        Connection con = ConnectDb.connectdb();
        
        // Query to check the current stored password for the user
        String checkPasswordQuery = "SELECT password FROM user_table WHERE user_name = ?";
        PreparedStatement checkPasswordStmt = con.prepareStatement(checkPasswordQuery);
        checkPasswordStmt.setString(1, username);
        ResultSet rs = checkPasswordStmt.executeQuery();
        
        if (rs.next()) {
            // Retrieve the stored password from the result set
            String storedPassword = rs.getString("password");
            
            // Check if the provided current password matches the stored password
            if (!encryptedCurrPwd.equals(storedPassword)) {
                // Close the connection and return an error message if passwords do not match
                con.close();
                return "Current password is incorrect."
                    + "<form action='http://localhost:8542/user/newuser.jsp' method='get'>"
                    + "<button type='submit'>Go back</button>"
                    + "</form>";
            }
            
            // Query to update the user's password in the database
            String updatePasswordQuery = "UPDATE user_table SET password = ? WHERE user_name = ?";
            PreparedStatement updatePasswordStmt = con.prepareStatement(updatePasswordQuery);
            updatePasswordStmt.setString(1, encryptedNewPwd);
            updatePasswordStmt.setString(2, username);
            
            // Execute the update and check if it was successful
            int rowsUpdated = updatePasswordStmt.executeUpdate();
            con.close();
            
            if (rowsUpdated > 0) {
                // Return a success message and a button to redirect the user to the login page
                return "Password changed successfully."
                    + "<form action='http://localhost:8542/user/index.jsp' method='get'>"
                    + "<button type='submit'>Please Login Again</button>"
                    + "</form>";
            } else {
                // Return an error message if the password update failed
                return "Failed to update the password."
                    + "<form action='http://localhost:8542/user/newuser.jsp' method='get'>"
                    + "<button type='submit'>Go back</button>"
                    + "</form>";
            }
        } else {
            // Close the connection and return an error message if the user was not found
            con.close();
            return "User not found."
                + "<form action='http://localhost:8542/user/newuser.jsp' method='get'>"
                + "<button type='submit'>Go back</button>"
                + "</form>";
        }
    }



    
    ////////////////////////////////user//////////////////////////////////////
    ////////////////////////////////user//////////////////////////////////////
    ////////////////////////////////user//////////////////////////////////////
    
    @GET
    @Path("my_resources")
    public String myResources(@Context HttpServletRequest req) throws Exception {
    	
    	HttpSession session=req.getSession();
    	Resource r=new Resource();
    	String name=session.getAttribute("uname").toString();
    	String data=r.myresources(name);
    	FileUtils file=new FileUtils();
		return  file.addDataAfter(280, data, "webapp/userb.html", req);
    }
    
    
    @GET
    @Path("requestforresource")
    public String RequestForResource(@Context HttpServletRequest req) throws Exception {
    	
    	HttpSession session=req.getSession();
    	Resource r=new Resource();
    	String name=session.getAttribute("uname").toString();
    	String data=r.requestforresources(name);
    	FileUtils file=new FileUtils();
		return  file.addDataAfter(280, data, "webapp/userb.html", req);
    }
    
    
    @POST
    @Path("requestforresourceaction")
    public String requestForResourceAction(@FormParam("options")String option,@Context HttpServletRequest req) throws Exception {
    	
    	HttpSession session=req.getSession();
    	Resource r=new Resource();
    	String name=session.getAttribute("uname").toString();
    	return r.requestforresourcesaction(name, option);
    }
    
    
    @GET
    @Path("requestforadminormanager")
    public String requestForAdminOrManager(@Context HttpServletRequest req) throws Exception {
    	
    	HttpSession session=req.getSession();
    	Resource r=new Resource();
    	String name=session.getAttribute("uname").toString();
    	String data=r.requestforadminormanager(name);
    	FileUtils file=new FileUtils();
		return  file.addDataAfter(280, data, "webapp/userb.html", req);
    }
    
    
    @POST
    @Path("requestfor")
    public String requestFor(@FormParam("options")String option,@Context HttpServletRequest req) throws Exception {
    	
    	HttpSession session=req.getSession();
    	Resource r=new Resource();
    	String name=session.getAttribute("uname").toString();
    	return r.requestfor(name, option);
    
    }
    
    
    @GET
    @Path("removeownresource")
    public String removeOwnResource(@Context HttpServletRequest req) throws Exception {
    	
    	HttpSession session=req.getSession();
    	Resource r=new Resource();
    	String name=session.getAttribute("uname").toString();
    	String data=r.removeownresource(name);
    	FileUtils file=new FileUtils();
		return  file.addDataAfter(280, data, "webapp/userb.html", req);
    }
    
    
    @POST
    @Path("removeownresourceaction")
    public String removeOwnResourceAction(@FormParam("options")String option,@Context HttpServletRequest req) throws Exception {
    	HttpSession session1=req.getSession();
 		String var=(String)session1.getAttribute("uname");
 		
    	Resource r=new Resource();
    	return r.removeownresourceaction(var,option);
    }
    
    
    @GET
    @Path("checkapprovals")
    public String checkApprovals(@Context HttpServletRequest req) throws Exception {
    	
    	Requests reqe=new Requests();
    	HttpSession session=req.getSession();
    	String name=session.getAttribute("uname").toString();
    	String data=reqe.checkApprovals(name);
    	FileUtils file=new FileUtils();
		return  file.addDataAfter(280, data, "webapp/userb.html", req);
    }
    
    
    @GET
    @Path("knowyourmanager")
    public String knowYourManager(@Context HttpServletRequest req) throws ClassNotFoundException, SQLException {
    	
    	User user=new User();
    	HttpSession session=req.getSession();
    	String name=session.getAttribute("uname").toString();
    	String data=user.KnowYourManager(name);
    	FileUtils file=new FileUtils();
		return  file.addDataAfter(280, data, "webapp/userb.html", req);
    }
    
    
    
    
    /////////////////////////admin///////////////////////////////////
    /////////////////////////admin///////////////////////////////////
    /////////////////////////admin///////////////////////////////////
    
    
    
    @GET
    @Path("check_requests")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String checkRequest(@Context HttpServletRequest req) throws Exception {

        Requests request=new Requests();
        String data=request.listOfRequests();
        FileUtils file=new FileUtils();
		return  file.addDataAfter(280, data, "webapp/adminb.html", req);
    }
    @GET
    @Path("requestlist")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String requestList(@Context HttpServletRequest req) throws Exception {

        Requests request=new Requests();
        String data=request.requests();
        FileUtils file=new FileUtils();
		return  file.addDataAfter(280, data, "webapp/adminb.html", req);
    }
    
    
    @GET
    @Path("listofresources")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String listOfResources(@Context HttpServletRequest req) throws Exception {
    	
 		
        Resource r=new Resource();
        String data=r.listOfResource();
        FileUtils file=new FileUtils();
		return  file.addDataAfter(280, data, "webapp/adminb.html", req);
    }
    
    
    @GET
    @Path("listofmanagers")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String listOfManagers(@Context HttpServletRequest req) throws Exception {
    	
 		
        Resource r=new Resource();
        String data=r.listOfManagers();
        FileUtils file=new FileUtils();
		return  file.addDataAfter(280, data, "webapp/adminb.html", req);
    }
    
    
    @GET
    @Path("listofusers")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String listOfUsers(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	String name=session.getAttribute("uname").toString();
        User r=new User();
        String data=r.ListOfUsers(name);
        FileUtils file=new FileUtils();
		return  file.addDataAfter(280, data, "webapp/adminb.html", req);
    }   
    
    @POST
    @Path("updateuserdetails")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String UpdateUserDetails(@FormParam("username")String username,@FormParam("firstname")String firstname,@FormParam("lastname")String lastname,@FormParam("email")String email, @Context HttpServletRequest req) throws Exception {
    	
        User r=new User();
        return r.updateUserDetails(username,firstname,lastname,email);
    }
    
    @POST
    @Path("check_requests_action")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response checkRequestAction(@FormParam("requestId") int requestId,@FormParam("resname") String resname,@FormParam("username") String name,@FormParam("action") String action,@Context HttpServletRequest req) {
    	
        Requests request = new Requests();        
        try {
            if (requestId <= 0 || action == null || resname == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input parameters.").build();
            }
            String result = request.processRequestAction(requestId, resname, action, name,req);           
            return Response.ok(result).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid action: " + e.getMessage()).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error: " + e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An unexpected error occurred: " + e.getMessage()).build();
        }
    }
    
    
    @GET
    @Path("removeresourcefromuser")
    public String removeResourceFromUser(@Context HttpServletRequest req) throws ClassNotFoundException, SQLException {
    	
    	Resource resource=new Resource();
    	String data= resource.removeResourceFromUser();
        FileUtils file=new FileUtils();
		return  file.addDataAfter(280, data, "webapp/adminb.html", req);
    }
    
    
    @POST
    @Path("removeresourcefromauseraction1")
    public String removeResourceFromUserAction1(@FormParam("options1")String username,@Context HttpServletRequest req) throws ClassNotFoundException, SQLException {
    	
    	Resource resource=new Resource();
    	String data= resource.removeResourceFromUserAction1(username);
        FileUtils file=new FileUtils();
		return  file.addDataAfter(280, data, "webapp/adminb.html", req);
    }
    
    @POST
    @Path("removeresourcefromauseraction2")
    public String removeResourceFromUserAction2(@FormParam("userName")String username,@FormParam("resource")String resname,@Context HttpServletRequest req) throws ClassNotFoundException, SQLException {
    	
    	Resource resource=new Resource();
    	return  resource.removeResourceFromUserAction2(username,resname);
        
    }
    
    
    @POST
    @Path("add_resource")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addResource(@FormParam("resource_name") String resourceName,@Context HttpServletRequest req) throws Exception {
        
        Resource resource = new Resource();
        String result = resource.addResource(resourceName);
        return result;
    }
    
    
    @GET
    @Path("remove_resource")   
    public String removeResource(@Context HttpServletRequest req) throws Exception {
    	
    	Resource r=new Resource();
    	String data= r.RemoveResource();
        FileUtils file=new FileUtils();
		return  file.addDataAfter(280, data, "webapp/adminb.html", req);
    }
    
    
    @POST
    @Path("removeresourceaction")    
    public String removeResourceAction(@FormParam ("options")String resname,@Context HttpServletRequest req) throws Exception {
    	
    	Resource r=new Resource();
    	return r.removeResourceAction(resname);
    }
    
    
    @GET
    @Path("checkuserforresource")   
    public String checkUserForResource( @Context HttpServletRequest req) throws Exception {
    	
    	Resource resource = new Resource();
        String data = resource.checkuserforresource();

        FileUtils file = new FileUtils();
        return file.addDataAfter(280, data, "webapp/adminb.html", req);
    }
    
    
    @POST
    @Path("checkuserforresourceaction")   
    public String checkUserForResourceAction(@FormParam("user")String username, @Context HttpServletRequest req) throws Exception {
    	
    	Resource resource = new Resource();
        String data = resource.checkuserforresourceaction(username);

        FileUtils file = new FileUtils();
        return file.addDataAfter(280, data, "webapp/adminb.html", req);
    }
    
    
    @GET
    @Path("checkusersforresource")   
    public String checkUsersForResource(@Context HttpServletRequest req) throws Exception {
    	
    	Resource resource = new Resource();
        String data = resource.checkUsersForresource();
        FileUtils file = new FileUtils();
        return file.addDataAfter(280, data, "webapp/adminb.html", req);
    }
    
    
    @POST
    @Path("checkusersforresourceaction")  
    public String checkUsersForResourceAction(@FormParam("resource")String resName, @Context HttpServletRequest req) throws Exception {
    	
    	Resource resource = new Resource();
        String data = resource.checkUsersForresourceAction(resName);

        FileUtils file = new FileUtils();
        return file.addDataAfter(280, data, "webapp/adminb.html", req);
    }
    
    
    @GET
    @Path("adduser")
    public String addUser(@Context HttpServletRequest req) {
    	
    	User u=new User();
    	FileUtils file = new FileUtils();
    	String data= u.adduser();
        return file.addDataAfter(280, data, "webapp/adminb.html", req);
    	
    }
    
    @POST
    @Path("createaccountaction")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String createAccountAction(@FormParam("firstname") String userFirstName,@FormParam("lastname") String userLastName,@FormParam("email") String email,@Context HttpServletRequest req,@Context HttpServletResponse res) throws ClassNotFoundException, IOException, ServletException, SQLException {
    	
        User user = new User();
        String pqr=user.adduser();
		String abc=user.signUp1(userFirstName, userLastName, email, res);
		FileUtils fobj=new FileUtils();
		return fobj.addDataAfter(280, pqr+abc, "webapp/adminb.html", req);
    }
    
    
    @GET
    @Path("removeuser")   
    public String removeUsers( @Context HttpServletRequest req) throws Exception {
    	
    	User user=new User();
    	String data=user.removeuser();
    	FileUtils fobj=new FileUtils();
    	return fobj.addDataAfter(280, data, "webapp/adminb.html", req);
    }
    
    
    @POST
    @Path("removeusersaction")    
    public String removeUserAction(@FormParam("username")String username,@Context HttpServletRequest req) throws Exception {
    	
    	User user=new User();
    	return user.removeuseraction(username);
    }
    
    
    @POST
    @Path("uploadcsv")
    @Consumes("text/plain")
    public Response uploadCsv(@Context HttpServletRequest request) {
        StringBuilder messageBuilder = new StringBuilder();
        try (InputStream fileInputStream = request.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] columns = line.split(",");
                if (columns.length == 4) {
                    // Process user data
                    User.insertUser(columns[0].trim(), columns[1].trim(), columns[2].trim(), columns[3].trim());
                    
                } else if (columns.length == 1) {
                    // Process resource data
                    User.insertResource(columns[0].trim());
                } else {
                    // Invalid line format
                    messageBuilder.append("Invalid line format: ").append(line).append("\n");
                }
            }

            if (messageBuilder.length() > 0) {
                // If there were any format issues, return a warning message with details
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("File processed with warnings:\n" + messageBuilder.toString())
                        .build();
            }

            return Response.ok("File uploaded successfully").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Database error: " + e.getMessage())
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error reading the file: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error processing the file: " + e.getMessage())
                    .build();
        }
    }

    
    //////////////////////////////////////////////manager///////////////////////////////////////////////////////
    //////////////////////////////////////////////manager///////////////////////////////////////////////////////
    //////////////////////////////////////////////manager///////////////////////////////////////////////////////
    
    @GET
    @Path("showteam")
    public String showTeam(@Context HttpServletRequest req) throws Exception {
    	
    	HttpSession session=req.getSession();
    	Resource r=new Resource();
    	String name=session.getAttribute("uname").toString();
    	String data=r.showteam(name);
    	FileUtils file=new FileUtils();
		return  file.addDataAfter(280, data, "webapp/managerb.html", req);
    }
    
    
    @POST
    @Path("removeuserfromteam")   
    public String removeUserFromTeam(@FormParam("userName") String username,@FormParam("managerName") String managername, @Context HttpServletRequest req) throws Exception {
        Resource r = new Resource();
        String result = r.removeUser(managername, username);
        return result;
    }
    @GET
    @Path("getteammember")   
    public String getTeamMember(@Context HttpServletRequest req) throws Exception {
    	HttpSession session=req.getSession();
    	Resource r=new Resource();
    	String name=session.getAttribute("uname").toString();
    	String data=r.getTeamMember(name);
    	FileUtils file=new FileUtils();
		return  file.addDataAfter(280, data, "webapp/managerb.html", req);
    }
    
    @POST
    @Path("assignUserToTeam")   
    public String assignUserToTeam(@FormParam("username") String username, @Context HttpServletRequest req) throws Exception {
    	
    	HttpSession session = req.getSession();
        String managerName = (String) session.getAttribute("uname");
        Resource r = new Resource();
        String result = r.assignUserToTeam(username, managerName);
        return result;
    }
    
    @GET
    @Path("managerresources")   
    public String managerResources(@Context HttpServletRequest req) throws Exception {
    	
    	HttpSession session=req.getSession();
    	Resource r=new Resource();
    	String name=session.getAttribute("uname").toString();
    	String data=r.myresources(name);
    	FileUtils file=new FileUtils();
		return  file.addDataAfter(280, data, "webapp/managerb.html", req);
    }
    
    @GET
    @Path("removemanagerresource")  
    public String removeManagerOwnResource(@Context HttpServletRequest req) throws Exception {
    	
    	HttpSession session=req.getSession();
    	Resource r=new Resource();
    	String name=session.getAttribute("uname").toString();
    	String data=r.removeownresource(name);
    	FileUtils file=new FileUtils();
		return  file.addDataAfter(280, data, "webapp/managerb.html", req);
    }
    
    
    @GET
    @Path("checkmanagerapprovals")    
    public String checkManagerApprovals(@Context HttpServletRequest req) throws Exception {
    	
    	Requests reqe=new Requests();
    	HttpSession session=req.getSession();
    	String name=session.getAttribute("uname").toString();
    	String data=reqe.checkApprovals(name);
    	FileUtils file=new FileUtils();
		return  file.addDataAfter(280, data, "webapp/managerb.html", req);
    }
    
    
    @GET
    @Path("requestformanagerresource")  
    public String RequestForManagerResource(@Context HttpServletRequest req) throws Exception {
    	
    	HttpSession session=req.getSession();
    	Resource r=new Resource();
    	String name=session.getAttribute("uname").toString();
    	String data=r.requestforresources(name);
    	FileUtils file=new FileUtils();
		return  file.addDataAfter(280, data, "webapp/managerb.html", req);
    }
    
    @GET
    @Path("requestforadmininmanager")   
    public String requestForAdminInManager(@Context HttpServletRequest req) throws Exception {
    	
    	HttpSession session=req.getSession();
    	Resource r=new Resource();
    	String name=session.getAttribute("uname").toString();
    	String data=r.requestForAdminInManager(name);
    	FileUtils file=new FileUtils();
		return  file.addDataAfter(280, data, "webapp/managerb.html", req);
    }
    
    @POST
    @Path("assignmanagerto")   
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignManagerTo(@FormParam("username") String teammembername, @Context HttpServletRequest req) {
    	
    	HttpSession session = req.getSession(false); // Use false to avoid creating a new session if none exists
        if (session == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Session not found").build();
        }

        String name = (String) session.getAttribute("uname");
        if (name == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("User not found in session").build();
        }

        if (teammembername == null || teammembername.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Team member name is required").build();
        }
        session.setAttribute("teammembername", teammembername);

        Resource resource = new Resource();
        try {
            String result = resource.assignManagerTo(name);
            return Response.ok(result).build();
        } catch (Exception e) {
            e.printStackTrace(); 
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error assigning manager").build();
        }
    }
    
    
    @GET
    @Path("managerattestation")   
    public String managerAttestation(@Context HttpServletRequest req) throws Exception {
    	
    	HttpSession session=req.getSession();
    	Resource r=new Resource();
    	String name=session.getAttribute("uname").toString();
    	String data=r.managerAttestation(name);
    	FileUtils file=new FileUtils();
		return  file.addDataAfter(280, data, "webapp/managerb.html", req);
    }
    
    
    @POST
    @Path("removeresourcefromteam")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeResource(@FormParam("user_name") String userName,@FormParam("resource_name") String resourceName,@Context HttpServletRequest req) throws ClassNotFoundException, SQLException {
    	
        if (userName == null || userName.isEmpty() || resourceName == null || resourceName.isEmpty()) {
            return "Both user_name and resource_name must be provided.";
        }
        Resource r=new Resource();
		return r.removeResourceFromTeam(userName, resourceName);  
    }

    
    
    
    
    
    
    
    
    
    
    
    
    //////////////////////chanage pass///////////////////////////////
    @Path("changepass")
    @GET
    public String changepassword(@Context HttpServletRequest req) {
    	HttpSession session1=req.getSession();
 		String var=(String)session1.getAttribute("uname");
 		if(var==null) {
 			return "User logged Out! Please Login again "
 	 				+ "<form action='http://localhost:8542/user/index.jsp' method='get'>"
 			     	+ "<button type='submit'>Login Again</button>"
 			     	+ "</form>";
 		}
        return User.changePassword();
    }
    @POST
    @Path("changepassaction")
    public String changepasswordaction(@FormParam("new-password")String Pass,@FormParam("confirm-password")String cPass,@Context HttpServletRequest req) {
    	HttpSession session=req.getSession();
    	if(!Pass.equals(cPass)) {
    		return "Passwords are not matching ";
    	}
 		String var=(String)session.getAttribute("uname");
 		if(var==null) {
 			return "User logged Out! Please Login again "
 	 				+ "<form action='http://localhost:8542/user/index.jsp' method='get'>"
 			     	+ "<button type='submit'>Login Again</button>"
 			     	+ "</form>";
 		}
    	HttpSession session1=req.getSession();
 		String var1=(String)session1.getAttribute("uname");
    	return User.changePasswordaction(var1,Pass,cPass);
    }
    
    
    
    
    
    
    
    
    
    
}
