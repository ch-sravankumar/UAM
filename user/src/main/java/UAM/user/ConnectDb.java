package UAM.user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDb {
	public static Connection connectdb() throws ClassNotFoundException, SQLException {
    	String driver="com.mysql.cj.jdbc.Driver",url="jdbc:mysql://localhost:3306/UAM",u="root",p="2003";
    	Class.forName(driver);
    	Connection con=DriverManager.getConnection(url,u,p);
		return con;
    }
}
