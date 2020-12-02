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

public void viewRentalHistory(Connection conn) {
    Scanner scnr = new Scanner(System.in);
     
    try {
      String strSelect = "SELECT Profile_name FROM Profile ORDER BY 1";	
      PreparedStatement stmt = conn.prepareStatement(strSelect, ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_READ_ONLY);
      ResultSet rs = stmt.executeQuery();
      rs = stmt.executeQuery();
      int ctr = 1;
      System.out.println("Select a profile from the list:");
      while (rs.next()) {
	System.out.println(ctr + "-" + rs.getString(1));
	ctr++;
    } 
    int userSelection = scnr.nextInt();
    rs.absolute(userSelection);
    String profileName = rs.getString(1);
    System.out.println("Rental History for profile: " + profileName);
    System.out.println("");
    strSelect = "SELECT m.title, mh.rating FROM Movie m JOIN Movie_History mh ON m.Movie_ID = mh.Movie_ID WHERE mh.profile_name = ?";
    stmt = conn.prepareStatement (strSelect);
    stmt.setString(1, profileName);
    rs = stmt.executeQuery();
    System.out.println("Movie Rating");
    System.out.println("------------");
    while (rs.next()) {
       System.out.println(rs.getObject(1) + " " + rs.getObject(2));
      }
     System.out.println("");
     System.out.println("type any character to continue...");
     Scanner scnr2 = new Scanner(System.in);
     String s = scnr2.nextLine(); 

  }
      catch (SQLException sqle) {
	System.out.println(sqle);
	}	
   
}

public void viewTableContent(Connection conn) {
    try {
       Scanner scnr = new Scanner(System.in);
       String mySchema = username.toUpperCase();
       String col_type;
       String tableName = "";
       DatabaseMetaData dmd = conn.getMetaData();
       ResultSet rs;
       System.out.println ("Please choose one of the tables listed below:");
       System.out.println("1=Genre, 2=Movie, 3=Actor, 4=Movie_Cast, 5=Movie_Genre, 6=Movie_History, 7=Member, 8=Profile, 9=Fav_Genres");
       int userSelection = scnr.nextInt();
       switch (userSelection) {
	    case 1: tableName = "Genre";
		    break;
	    case 2: tableName = "Movie";
		    break;
	    case 3: tableName = "Actor";
		    break;
	    case 4: tableName = "Movie_Cast";
		    break;
	    case 5: tableName = "Movie_Genre";
		    break;
	    case 6: tableName = "Movie_History";
		    break;
	    case 7: tableName = "Member";
		    break;
	    case 8: tableName = "Profile";
		    break;
	    case 9: tableName = "Fav_Genres";
		    break;
	    default:
		    System.out.println("You didn't make a valid entry.");
	}
       //need to select * from this table
       String strSelect = "SELECT * FROM " + tableName;
       System.out.println (strSelect);
       PreparedStatement stmt = conn.prepareStatement (strSelect);
       rs = stmt.executeQuery();
       ResultSetMetaData rsmd = rs.getMetaData();
       int columnCount = rsmd.getColumnCount();
       for (int i = 1; i <= columnCount; i++) {
		System.out.print(rsmd.getColumnLabel(i) + " ");
		}
       System.out.print("\n");
       System.out.println("------------------------------------------");
       while (rs.next()) {
          for (int i = 1; i <= columnCount; i++) {
               System.out.print (rs.getObject(i) + " ");		
             }
             System.out.print("\n");
	}
       System.out.println("");
       System.out.println("type any character to continue...");
       Scanner scnr2 = new Scanner(System.in);
       String s = scnr2.nextLine(); 
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
             viewTableContent(connection);  
	  }
	  else if (optionSelected.equals("4")) {
		viewRentalHistory(connection);
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
