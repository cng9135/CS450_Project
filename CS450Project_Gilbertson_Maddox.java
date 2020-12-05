import java.sql.*;
import java.math.*;
import java.io.*;
import oracle.jdbc.driver.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import javax.naming.spi.DirStateFactory.Result;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

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

  public void viewRentalHistory(Connection conn, Scanner scnr) {
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
      scnr.nextLine();
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
      String s = scnr.nextLine();
      System.out.println("");
    }
    catch (SQLException sqle) {
      System.out.println(sqle);
    }
  }

  public void viewTableContent(Connection conn, Scanner scnr) {
      try {
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
        String s = scnr.nextLine(); 
        System.out.println ("");
        }
      catch (SQLException sqle) {
        System.out.println (sqle); 
    }	      
  }

  // This method will determine if a user is attempting to update, delete, or add a new record, then call the appropriate void method to accomplish the goal.
  public void additionSubMenu(Connection conn, Scanner scanner){
    // Determine if we're adding, updating, or deleting, then call the appropriate sub-method.
    try{
      int userSelection = 1;
      System.out.println("Would you like to add a record, update a record, or delete a record?\nUse 1 for add, 2 for update, and 3 for delete.");
      do{
        userSelection = scanner.nextInt();
        if(userSelection < 1 || userSelection > 3){
          System.out.println("Please make a valid selection.\nUse 1 for add, 2 for update, and 3 for delete.");
        }
      }while(userSelection < 1 || userSelection > 3);
      switch(userSelection){
        case 1:
          // Add.
          addRecord(conn, scanner);
          break;
        case 2:
          // Update.
          updateOrDelete(conn, true, scanner);
          break;
        case 3:
          // Delete.
          updateOrDelete(conn, false, scanner);
          break;
      }
      System.out.println("");
      System.out.println("type any character to continue...");
      String returnFromUpdate = scanner.nextLine();
      System.out.println("");
    }
    catch (Exception e){
      System.out.println("Error when selecting option. Message: " + e.getMessage());
    }
  }

  public void updateOrDelete(Connection conn, boolean isUpdate, Scanner scanner){
    // If isUpdate, then update, otherwise delete.
    try{
      int tableSelection;
      String tableName = "";

      // First, get the table name.
      do{
        System.out.println("First, please indicate (using the given index) which table you wish to " + (isUpdate ? "update" : "delete") + " a record from.\nValid table names are:");
        System.out.println("1=Genre, 2=Movie, 3=Actor, 4=Movie_Cast, 5=Movie_Genre, 6=Movie_History, 7=Member, 8=Profile, 9=Fav_Genres");
        tableSelection = scanner.nextInt();
        switch (tableSelection) {
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
      }while(tableSelection < 1 || tableSelection > 9);

      // Now get the appropriate schema so we know what to find.
      ResultSet namesresult = conn.createStatement().executeQuery("SELECT * FROM " + tableName);
      ResultSetMetaData getColumnNames = namesresult.getMetaData();
      int columnCount = getColumnNames.getColumnCount();
      LinkedList<String> columnNames = new LinkedList<String>(); // Ordered queue of column names.
      LinkedList<String> columnNames2 = new LinkedList<String>();
      LinkedList<String> columnTypes = new LinkedList<String>();
      // Tell the user what the various columns are, and let them input conditions.
      HashMap<String, String> updateColumns = new HashMap<String, String>(); // Key by fieldname, value is new expression.
      for(int i = 1; i <= columnCount; i++){
        columnNames.add(getColumnNames.getColumnName(i));
        columnNames2.add(getColumnNames.getColumnName(i));
        columnTypes.add(getColumnNames.getColumnTypeName(i));
      }
      System.out.println("In " + tableName + ", there are the following columns:");
      while(columnNames.size() != 0){
        System.out.println(columnNames.poll() + " of type " + columnTypes.poll());
      }
      System.out.println("\nPlease type a valid SQL condition (sans terminating semicolon) you would like to " + (isUpdate ? "update" : "delete") + " by:");
      System.out.println("Note, if you are using more than one condition, please include all boolean operators (ANDs, ORs, etc.).");
      System.out.println("If you are trying to " + (isUpdate ? "update" : "delete") + " everything in this table, please type 'EVERYTHING'.");
      String condition = scanner.nextLine();
      while(condition.equals("")){
        condition = scanner.nextLine();
      }
      // If delete, run statement. Else, it's update. We need the new values for update.
      if(!isUpdate){
        System.out.println("DELETE FROM " + tableName + (condition.equals("EVERYTHING") ? "" : (" WHERE " + condition)));
        conn.prepareStatement("DELETE FROM " + tableName + (condition.equals("EVERYTHING") ? "" : (" WHERE " + condition))).executeUpdate();
      }
      else{
        // We need to get the set statement. 
        String setStatement = "";
        System.out.println("For updating, please input a valid SQL expression for each given column name (or press enter for no expression):");
        while(columnNames2.size() != 0){
          String columnName = columnNames2.poll();
          System.out.println("Column name = " + columnName);
          setStatement = scanner.nextLine();
          if(!(setStatement.equals(""))){
            updateColumns.put(columnName, setStatement);
          }
        }
        if(updateColumns.size() == 0){
          System.out.println("You attempted to update no columns. This is an invalid update.");
        }
        else{
          // Now we have all the sets. Create and run query.
          String queryString = "UPDATE " + tableName + " SET ";
          for(String columnKey : updateColumns.keySet()){
            queryString = queryString + columnKey + " = " + updateColumns.get(columnKey) + ", "; 
          }
          queryString = queryString.substring(0, (queryString.length() - 2)) + (condition.equals("EVERYTHING") ? "" : (" WHERE " + condition));
          System.out.println(queryString);
          conn.prepareStatement(queryString).executeUpdate();
        }
      }
    }
    catch(Exception e){
      System.out.println("Exception occurred when adding record. Message: " + e.getMessage());
    }
  }

  // This method will add a record into a table, returning void. 
  // The plan: Get the table from user input, tell the user the appropriate schema, and then get each required field.
  public void addRecord(Connection conn, Scanner scanner){
    try{
      int tableSelection;
      String tableName = "";

      // First, get the table name.
      do{
        System.out.println("First, please indicate (using the given index) which table you wish to add a record to.\nValid table names are:");
        System.out.println("1=Genre, 2=Movie, 3=Actor, 4=Movie_Cast, 5=Movie_Genre, 6=Movie_History, 7=Member, 8=Profile, 9=Fav_Genres");
        tableSelection = scanner.nextInt();
        switch (tableSelection) {
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
      }while(tableSelection < 1 || tableSelection > 9);

      // Now get the appropriate schema so we know what to fill.
      System.out.println("To add a record into the " + tableName + " table, please input the following fields:");
      ResultSet namesresult = conn.createStatement().executeQuery("SELECT * FROM " + tableName);
      ResultSetMetaData getColumnNames = namesresult.getMetaData();
      int columnCount = getColumnNames.getColumnCount();
      LinkedList<String> columnNames = new LinkedList<String>(); // Ordered queue of column names.
      LinkedList<String> columnNames2 = new LinkedList<String>();
      LinkedList<String> columnTypes = new LinkedList<String>();
      HashMap<String, String> fieldValues = new HashMap<String, String>(); // Mapping from column names to the value for the new record.
      boolean isProfile = tableName.equals("Profile") ? true : false;
      for(int i = 1; i <= columnCount; i++){
        columnNames.add(getColumnNames.getColumnName(i));
        columnTypes.add(getColumnNames.getColumnTypeName(i));
        columnNames2.add(getColumnNames.getColumnName(i));
      }

      // Get the values for each field.
      while(columnNames.size() > 0){
        // Now get the data for that field.
        String columnName = columnNames.poll();
        String columnType = columnTypes.poll();
        System.out.println("Field name: " + columnName + " with datatype: " + columnType + ".\nInsert value for field:");
        String fieldValue = scanner.nextLine();
        if(fieldValue.equals("")){
          fieldValue = scanner.nextLine();
        }
        else if(columnType.length() >= 7){ 
          if(columnType.substring(0, 7).toUpperCase().equals("VARCHAR")){
            fieldValue = "'" + fieldValue + "'";
          }
        }
        fieldValues.put(columnName, fieldValue);
        if(isProfile && columnName.toLowerCase().equals("member_id")){
          // We need to check and see if this is valid. Count how many entries already have this member_id in Profiles table.
          ResultSet countOfProfiles = conn.prepareStatement("SELECT COUNT(*) AS number FROM Profile WHERE Member_ID = '" + fieldValue + "'", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery();
          if(countOfProfiles.getInt("number") > 4){
            // Then this is invalid. Re-set isProfile to true.
            isProfile = true;
          }
          else{
            isProfile = false;
          }
        }
      }
      // Check if we can insert this profile or not. If not, then we need to exit immediately.
      if(isProfile){
        System.out.println("There are already a maximum number of profiles attached to this account. Please remove/update one before continuing.");
      }
      else{
        // Now construct the query string, using our linked list as a queue of the column names and the hash map to fetch the value.
        String queryString = "INSERT INTO " + tableName + " (";
        String valuesString = "(";
        while(!(columnNames2.size() == 0)){
          String currentColumn = columnNames2.poll();
          queryString = queryString + currentColumn + ", ";
          valuesString = valuesString + fieldValues.get(currentColumn) + ", ";
        }
        valuesString = valuesString.substring(0, (valuesString.length() - 2)) + ")";
        queryString = queryString.substring(0, (queryString.length() - 2)) + ") VALUES " + valuesString;

        // Finally, execute the insert statement.
        System.out.println("Executed insert:\n" + queryString);
        conn.prepareStatement(queryString).executeUpdate();
      }
    }
    catch(Exception e){
      System.out.println("Exception occurred when adding record. Message: " + e.getMessage());
    }
  }

  // This method gets user input and searches for specific movies.
  public void searchDatabase(Connection conn, Scanner scanner){
    // We need to find out what part of the database we're searching.
    String response = "";
    String queryString = "";
    String titleFragment = "";
    String firstActorFragment = "";
    String lastActorFragment = "";
    boolean weHaveFirst, weHaveLast;
    boolean datafound;
    boolean finishedSearching = false;
    ResultSet matchingMovies;
    try{
      while (!finishedSearching) {
         datafound = false; //initialize
         // First, get the field we're searching by.
          System.out.println("Please indicate if you would like to search for movies by title or by actors.");
         System.out.println(" Use 'title' for title and 'actors' for actors, or 'done' for finished searching");
       response = scanner.nextLine();
      if (response.equals("done")) {
         finishedSearching = true;
         }
      else if(response.equals("title")){
        // We're searching by title. Get the title substring and search for it.
        System.out.println ("enter the title to search by.  You may enter only part of the title.");
        titleFragment = scanner.nextLine();
        System.out.println ("");
        // Now search.
        queryString = "SELECT title, year, average_rating FROM Movie WHERE UPPER(title) LIKE '%" + titleFragment.toUpperCase() + "%'";
        System.out.println("Exwcuting search: " + queryString);
        matchingMovies = conn.prepareStatement(queryString, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery();
        // Now display.
        matchingMovies.beforeFirst();
        while(matchingMovies.next()){
          datafound = true;
          System.out.println("MOVIE: " + matchingMovies.getString("title") + "\nYEAR: " + matchingMovies.getString("year") + "\nAVERAGE RATING: " + matchingMovies.getString("average_rating") + "\n\n");
        }
      if (!datafound) {
        System.out.println ("No data foundi\n");
      }
      }
      else{
        // We're searching by an actor's name. Get the actor's name and find all associated movies, then select the appropriate movie info from that.
        System.out.println("Please provide the actor's first name, a fragment of their first name, or, if you do not know it, please leave the field blank and press enter.");
        firstActorFragment = scanner.nextLine().trim();
        System.out.println("Please provide the actor's last name, a fragment of their last name, or, if you do not know it, please leave the field blank and press enter.");
        lastActorFragment = scanner.nextLine().trim();
        queryString = "SELECT M.title, M.year, M.average_rating, A.first_name, A.last_name FROM Movie M NATURAL JOIN Movie_Cast MC NATURAL JOIN Actor A WHERE ";
        // Now implement our searches. If we know a fragment, add a LIKE clause.
        weHaveFirst = firstActorFragment.equals("") ? false : true;
        weHaveLast = lastActorFragment.equals("") ? false : true;
        if(!weHaveFirst && !weHaveLast){
          System.out.println("I'm sorry, but we cannot search for an actor without either a first name or a last name (or a fragment of either).");
        }
        else{
    
           queryString = queryString + "UPPER(A.first_name) LIKE '%" + firstActorFragment.toUpperCase() + "%' AND UPPER(A.last_name) LIKE '%" + lastActorFragment.toUpperCase() + "%'";
           
          // Finally, query and print results.
          System.out.println("Executing search: " + queryString + "\n");
          matchingMovies = conn.prepareStatement(queryString, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery();
          matchingMovies.beforeFirst();
        
          while(matchingMovies.next()){
            datafound = true;
            System.out.println("MOVIE: " + matchingMovies.getString("title") + "\nYEAR: " + matchingMovies.getString("year"));
            System.out.println ("AVERAGE RATING: " + matchingMovies.getString("average_rating"));
            System.out.println ("ACTOR: " + matchingMovies.getString("first_name") + " "  + matchingMovies.getString("last_name")+ "\n");
          }
         if (!datafound) {
            System.out.println ("No data found\n");
        }
      }
       }
    }
    System.out.println ("\nType any character to continue");
    response = scanner.nextLine();
    } // end of while loop
    catch(Exception e){
      System.out.println("Exception occurred while searching movies. Message: " + e.getMessage());
    }
    finally{
      //scanner.close();
    }
  }

  public void mainMenu() {

    Scanner myScanner = new Scanner(System.in);
    try { 
      String tableName;
      boolean done = false;
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
        try{
          optionSelected = myScanner.nextLine();
        }
        catch(NoSuchElementException nsee){
          optionSelected = "0";
        }
        if (optionSelected.equals("1")) { 
          viewTableContent(connection, myScanner);  
        }
        else if(optionSelected.equals("2")){
          additionSubMenu(connection, myScanner);
        }
        else if(optionSelected.equals("3")){
          searchDatabase(connection, myScanner);
        }
        else if (optionSelected.equals("4")) {
          viewRentalHistory(connection, myScanner);
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
    finally{
      myScanner.close();
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
