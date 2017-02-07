package edu.lehigh.cse216.phase0.db.fko218;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.ArrayList;

/**
 * A simple application that interacts with a database
 *
 */
public class App 
{
    public static void main( String[] argv)
    {
	// Get the MYSQL configuration from the environment
    	Map<String, String> env = System.getenv();
		String ip = env.get("MYSQL_IP");
		String port = env.get("MYSQL_PORT");
		String user = env.get("MYSQL_USER");
		String pass = env.get("MYSQL_PASS");
		String db = env.get("MYSQL_DB");

		// Use these to connect to the database and issue commands
		Connection conn = null;
		PreparedStatement stmt = null;

		// Connect to the database; fail if we can't
		System.out.println("Connecting to " + ip + ":" + port + "/" + db);
		try {
	    	//Open a connection; fail if we cannot get one
	    	conn = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + db, user, pass); 
	    
	    	if (conn == null) {
				System.out.println("Error: getConnection returned null object");
			return;
	    	}   
		}
		catch (SQLException e) {
	    	System.out.println("Error: getConnection threw an exception");
	    	e.printStackTrace();
	    	return;
		}
	
		//Create a table to store data. It matches the 'Datum' type from the previous
		// tutorial
		System.out.println("Attempting to create tblData");
		String createStatement = "CREATE TABLE tblData (id INT(64) NOT NULL " + "AUTO_INCREMENT, val1 VARCHAR(200), val2 VARCHAR(200), " + "PRIMARY KEY(id)";
		try {
	    	stmt = conn.prepareStatement(createStatement);
	    	stmt.execute();
	    	System.out.println("Table Created");
		}
		catch (SQLException e) {
	    	System.out.println("Table not created (it may already exist)");
		}

		//insert 10 rows of dummy data
		insertRows(conn, 10);
		// show the current table contents
		showAll(conn);
		// update the first row 
		updateRow(conn, 1);
		// delete the second row
		deleteRow(conn, 2); 
		// show the current table contents
		showAll(conn);

		try {
	    	conn.close(); //connection close
		}
		catch (SQLException e) {
	    	System.out.println("Error: unable to close database connection");
	    	e.printStackTrace();
		}
    }
    
    /** 
     * Show all data in the table
     */
    private static void showAll(Connection conn) {
		System.out.println("Current Database Contents:");
		// Query and display the data
		try {
	    	// get all the data into a ResultSet
	    	String getStmt = "SELECT * FROM tblData";
	    	PreparedStatement stmt = conn.prepareStatement(getStmt);
	    	ResultSet rs = stmt.executeQuery();

	   		// Let's iterate through the java ResultSet
	    	while (rs.next()) {
				String id = rs.getString("id");
				String val1 = rs.getString("val1");
				String val2 = rs.getString("val2");
				System.out.format("[%s] --> {%s, %s}\n", id, val1, val2);
	    	}

	    	stmt.close();
		}
		catch (SQLException e) {
	    	System.out.println("Error: query failed");
	    	e.printStackTrace();
		}
    }
    
    /**
     * Insert a few rows of data into the database
     */
    private static void insertRows(Connection conn, int num) {
		// create some phony data
		System.out.println("Attempting to insert some data");
		ArrayList<String> val1s = new ArrayList<>();
		ArrayList<String> val2s = new ArrayList<>();
		for (int i = 0; i < num; ++i) {
	    	val1s.add("The first value is " + i);
	    	val2s.add("The second value is " + (10 * i));
		}
		int count = 0;
		try {
	    	String insertStmt = "INSERT INTO tblData VALUES (default, ?, ?)";
	    	PreparedStatement stmt = conn.prepareStatement(insertStmt);
	    	for (int i = 0; i < 10; ++i) {
				stmt.setString(1, val1s.get(i));
				stmt.setString(2, val2s.get(i));
				count += stmt.executeUpdate();
	    	}
	    	stmt.close();
	    	System.out.println(count + " rows added");
		} catch (SQLException e) {
	    	System.out.println("Error: insertion failed after " + count
			      	 + " attempts");
	    	e.printStackTrace();
		} 
    }

    /**
     * Execute an UPDATE query to modify table contents
     */
    private static void updateRow(Connection conn, int which) {
		System.out.println("Updating val2 of row with " + which + " in val1");
		try {
	    	String updateStmt = "UPDATE tblData SET val2 = ? WHERE val1 = ?";
	    	PreparedStatement stmt = conn.prepareStatement(updateStmt);
	    	stmt.setString(1, "I love CSE216");
	    	stmt.setString(2, "The first value is " + which);
	    	int count = stmt.executeUpdate();
	    	stmt.close();
	    	System.out.println("Updated " + count + " rows");
		} catch (SQLException e) {
	    	System.out.println("Error: unable to update row");
	    	e.printStackTrace();
		}
    }
    
    /**
     * Delete contents from the table
     */
    private static void deleteRow(Connection conn, int which) {
		System.out.println("Deleting row with " + which + " in val1");
		try {
	    	String deleteStmt = "DELETE FROM tblData WHERE val1 = ?";
	    	PreparedStatement stmt = conn.prepareStatement(deleteStmt);
	    	stmt.setString(1, "The first value is " + which);
	    	int count = stmt.executeUpdate();
	    	stmt.close();
	    	System.out.println("Deleted " + count + " rows");
		} catch (SQLException e) {
	    	System.out.println("Error: unable to delete row");
	    	e.printStackTrace();
		}
    }
} 

