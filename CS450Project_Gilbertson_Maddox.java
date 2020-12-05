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

  // This method will determine if a user is attempting to update, delete, or add a new record, then call the appropriate void method to accomplish the goal.
  public void additionSubMenu(Connection conn){
    // Determine if we're adding, updating, or deleting, then call the appropriate sub-method.
    Scanner scanner = new Scanner(System.in);
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
          addRecord(conn);
          break;
        case 2:
          // Update.
          updateOrDelete(conn, true);
          break;
        case 3:
          // Delete.
          updateOrDelete(conn, false);
          break;
      }
    }
    catch (Exception e){
      System.out.println("Error when selecting option. Message: " + e.getMessage());
    }
    finally{
      scanner.close();
    }
  }

  public void updateOrDelete(Connection conn, boolean isUpdate){
    // If isUpdate, then update, otherwise delete.
    Scanner scanner = new Scanner(System.in);
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
      DatabaseMetaData databaseMetaData = conn.getMetaData();
      ResultSet tableColumns = databaseMetaData.getColumns(null, null, tableName, "%"); // Gets the names of all the columns in the appropriate table.
      ResultSet tableColumns2 = databaseMetaData.getColumns(null, null, tableName, "%");
      // Tell the user what the various columns are, and let them input conditions.
      HashMap<String, String> updateColumns = new HashMap<String, String>(); // Key by fieldname, value is new expression.
      System.out.println("In " + tableName + ", there are the following columns:");
      while(tableColumns.next()){
        System.out.print(tableColumns.getString("COLUMN_NAME") + "\t");
      }
      System.out.println("\nPlease type a valid SQL condition (sans terminating semicolon) you would like to " + (isUpdate ? "update" : "delete") + "by:");
      System.out.println("Note, if you are using more than one condition, please include all boolean operators (ANDs, ORs, etc.).");
      System.out.println("If you are trying to " + (isUpdate ? "update" : "delete") + " everything in this table, please type 'EVERYTHING'.");
      String condition = scanner.nextLine();
      // If delete, run statement. Else, it's update. We need the new values for update.
      if(!isUpdate){
        System.out.println("DELETE FROM " + tableName + (condition.equals("EVERYTHING") ? "" : (" WHERE " + condition)));
        conn.prepareStatement("DELETE FROM " + tableName + (condition.equals("EVERYTHING") ? "" : (" WHERE " + condition))).executeUpdate();
      }
      else{
        // We need to get the set statement. 
        String setStatement = "";
        System.out.println("For updating, please input a valid SQL expression for each given column name (or press enter for no expression):");
        while(tableColumns2.next()){
          System.out.println("Column name = " + tableColumns2.getString("COLUMN_NAME"));
          setStatement = scanner.nextLine();
          if(!(setStatement.equals(""))){
            updateColumns.put(tableColumns2.getString("COLUMN_NAME"), setStatement);
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
    finally{
      scanner.close();
    }
  }

  // This method will add a record into a table, returning void. 
  // The plan: Get the table from user input, tell the user the appropriate schema, and then get each required field.
  public void addRecord(Connection conn){
    Scanner scanner = new Scanner(System.in);
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
      DatabaseMetaData databaseMetaData = conn.getMetaData();
      ResultSet tableColumns = databaseMetaData.getColumns(null, null, tableName, "%"); // Gets the names of all the columns in the appropriate table.
      LinkedList<String> columnNames = new LinkedList<String>(); // Ordered queue of column names.
      HashMap<String, String> fieldValues = new HashMap<String, String>(); // Mapping from column names to the value for the new record.
      boolean isProfile = tableName.equals("Profile") ? true : false;

      // Get the values for each field.
      while(tableColumns.next()){
        // Now get the data for that field.
        String columnName = tableColumns.getString("COLUMN_NAME");
        System.out.println("Field name: " + columnName + " with datatype: " + tableColumns.getString("DATA_TYPE") + ".\nInsert value for field:");
        String fieldValue = scanner.nextLine();
        columnNames.add(columnName);
        fieldValues.put(columnName, fieldValue);
        if(isProfile && columnName.toLowerCase().equals("member_id")){
          // We need to check and see if this is valid. Count how many entries already have this member_id in Profiles table.
          ResultSet countOfProfiles = conn.prepareStatement("SELECT COUNT(*) AS number FROM Profile WHERE Member_ID = '" + fieldValue + "'", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery();
          countOfProfiles.beforeFirst();
          tableColumns.next();
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
        while(!(columnNames.isEmpty())){
          String currentColumn = columnNames.poll();
          queryString = queryString + currentColumn + ", ";
          valuesString = valuesString + fieldValues.get(currentColumn) + ", ";
        }
        valuesString = valuesString.substring(0, (valuesString.length() - 1)) + ")";
        queryString = queryString.substring(0, (queryString.length() - 1)) + ") VALUES " + valuesString;

        // Finally, execute the insert statement.
        System.out.println("Executed insert:\n" + queryString);
        conn.prepareStatement(queryString).executeUpdate();
      }
    }
    catch(Exception e){
      System.out.println("Exception occurred when adding record. Message: " + e.getMessage());
    }
    finally{
      scanner.close();
    }
  }

  // This method gets user input and searches for specific movies.
  public void searchDatabase(Connection conn){
    // We need to find out what part of the database we're searching.
    Scanner scanner = new Scanner(System.in);
    try{
      // First, get the field we're searching by.
      String response = "";
      do{
        System.out.println("Please indicate if you would like to search for movies by title or by actors.\nUse 'title' for title and 'actors' for actors.");
        response = scanner.next();
      }while(!(response.equals("title")) && !(response.equals("actors)")));
      if(response.equals("title")){
        // We're searching by title. Get the title substring and search for it.
        String titleFragment = "";
        while(titleFragment.equals("")){
          System.out.println("Please provide the title (or part of title) of the movie you are searching for.");
          titleFragment = scanner.nextLine();
        }
        // Now search.
        String queryString = "Executing search: SELECT title, year, average_rating FROM Movie WHERE title LIKE '%" + titleFragment + "%'";
        System.out.println(queryString);
        ResultSet matchingMovies = conn.prepareStatement(queryString, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery();
        // Now display.
        matchingMovies.beforeFirst();
        while(matchingMovies.next()){
          System.out.println("MOVIE: " + matchingMovies.getString("title") + "\nYEAR: " + matchingMovies.getString("year") + "\nAVERAGE RATING: " + matchingMovies.getString("average_rating") + "\n\n");
        }
      }
      else{
        // We're searching by an actor's name. Get the actor's name and find all associated movies, then select the appropriate movie info from that.
        System.out.println("Please provide the actor's first name, a fragment of their first name, or, if you do not know it, please leave the field blank and press enter.");
        String firstActorFragment = scanner.nextLine();
        System.out.println("Please provide the actor's last name, a fragment of their last name, or, if you do not know it, please leave the field blank and press enter.");
        String lastActorFragment = scanner.nextLine();
        String queryString = "SELECT title, year, average_rating FROM Movies WHERE movie_ID IN (SELECT movie_ID FROM Movie_Cast WHERE Actor_ID IN (SELECT Actor_ID FROM Actor WHERE ";
        // Now implement our searches. If we know a fragment, add a LIKE clause.
        boolean weHaveFirst = firstActorFragment.equals("") ? false : true;
        boolean weHaveLast = lastActorFragment.equals("") ? false : true;
        if(!weHaveFirst && !weHaveLast){
          System.out.println("I'm sorry, but we cannot search for an actor without either a first name or a last name (or a fragment of either).");
        }
        else{
          if(weHaveFirst){
            queryString = queryString + "first_name LIKE '" + firstActorFragment + "'";
            if(weHaveLast){
              queryString = queryString + " AND ";
            }
          }
          if(weHaveLast){
            queryString = queryString + "last_name LIKE '" + lastActorFragment + "'";
          }
          queryString = queryString + "))";
          // Finally, query and print results.
          System.out.println("Executing search: " + queryString);
          ResultSet matchingMovies = conn.prepareStatement(queryString, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery();
          matchingMovies.beforeFirst();
          while(matchingMovies.next()){
            System.out.println("MOVIE: " + matchingMovies.getString("title") + "\nYEAR: " + matchingMovies.getString("year") + "\nAVERAGE RATING: " + matchingMovies.getString("average_rating") + "\n\n");
          }
        }
      }
    }
    catch(Exception e){
      System.out.println("Exception occurred while searching movies. Message: " + e.getMessage());
    }
    finally{
      scanner.close();
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
        else if(optionSelected.equals("2")){
          additionSubMenu(connection);
        }
        else if(optionSelected.equals("3")){
          searchDatabase(connection);
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
