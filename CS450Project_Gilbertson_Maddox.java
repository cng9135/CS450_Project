import java.sql.*;
import java.math.*;
import java.io.*;
import oracle.jdbc.driver.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class CS450Project_Gilbertson_Maddox 
{ 
    
  private String driver = "oracle.jdbc.driver.OracleDriver";
  private String jdbc_url = "jdbc:oracle:thin:@artemis.vsnet.gmu.edu:1521/vse18c.vsnet.gmu.edu";

 // we have to prompt user for this....
  private String username;
  private String password;
  private String optionSelected;

// constructor
  CS450Project_Gilbertson_Maddox () {}

  private Connection getConnection() {
    // register the JDBC driver
    try {
      Class.forName(driver);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    
    // create a connection
    Connection connection = null;
    try {
      connection = DriverManager.getConnection (jdbc_url, username, password);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return connection;
  }

public void viewTableContent (Connection conn) {
    try {
      Scanner scnr = new Scanner (System.in);
       String mySchema = username.toUpperCase();
       DatabaseMetaData dmd = conn.getMetaData();
       ResultSet rs = dmd.getTables(null, mySchema, "%", new String[] {"TABLE"});
       System.out.println ("Please choose one of the tables listed below:");
       while (rs.next()) { 
            System.out.println ( "         " + rs.getString(3));
       }
       String tablename = scnr.nextLine();
       //need to select * from this table
       System.out.println ("You chose " + tablename);
       String strSelect = "SELECT * FROM " + tablename;
       PreparedStatement stmt = conn.prepareStatement (strSelect);
       rs = stmt.executeQuery();
       while (rs.next()) {
          System.out.println (rs.getString(2));
       }
       System.out.println ("");
       }
    catch (SQLException sqle) {
       System.out.println (sqle); 
}      
}

public void mainMenu() {

  try { 
      String tableName;
      boolean done = false;
      Scanner myScanner = new Scanner(System.in);
      System.out.println ("Enter username");
      username = myScanner.nextLine();
      System.out.println ("Enter Password");
      password = myScanner.nextLine();
      Connection connection = getConnection();
      while (!done) {
          System.out.println("Main Menu");
          System.out.println("1 = View Table Content");
          System.out.println("2 = Add/Update/Delete Records");
          System.out.println("3 = Search");
          System.out.println("4 = Show Rental History");
          System.out.println("5 = Exit");
          optionSelected = myScanner.nextLine();
          if (optionSelected.equals("1")) { 
             viewTableContent (connection);  
	  }
	  else if (optionSelected.equals("5")) {
                System.out.println ("Bye, and see you at the movies.");
		done = true;
	   }
         else {
             System.out.println ("Please enter 1, 2, 3, 4 or 5");
          }
	}  
      close(connection);
  }
  catch (SQLException sqle) {
   
   System.out.println ("SQL Exception :" + sqle);
  } 
}

public static void main (String arg[]){
     CS450Project_Gilbertson_Maddox  myDB = new CS450Project_Gilbertson_Maddox();
     myDB.mainMenu();
}

  public void close(Connection connection) throws SQLException
  {
    try
    {
      connection.close();
    } catch (SQLException e)
    {
      throw e;
    }
  }
}
