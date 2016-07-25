package raceattscan;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class RaceDataHandle implements Runnable {

	public String filename;
	private Connection conn = null;
	private String dbname = "jdbc:sqlite:datalog.db";
	private Statement stmt = null;

	public RaceDataHandle ( ) {
		try{
			openDB ( ) ;
			intiateDB ( );
		}catch(Exception e){
			//code to handle an Exception here
		}
	}

	public void run (  ) {
			try{
				//this.filename = local_filename.toString();
				pushDB( filename );
			}catch(Exception e){
				//code to handle an Exception here
			}

		//TODO: Create a script to report for replicated files (from DB) at startup
	}

	private void openDB ( ) throws Exception {
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection( dbname );
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}

	private void intiateDB() throws Exception {
		try {
			stmt = conn.createStatement();
		      	String sql = "CREATE TABLE IF NOT EXISTS log ( FILENAME CHAR(250), ISUNIQUE CHAR(10) );"; 
		      	stmt.executeUpdate( sql );
		} catch ( Exception e ) {
			conn.close();
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}

	private void pushDB( String pfilename ) throws Exception {
		try {
		      	String sql = "select * from log where FILENAME = '" + pfilename + "'";
		      	stmt.execute(sql);
			ResultSet rs = stmt.getResultSet();

			if (rs.next()) {//if there is such entry...
				sql = "UPDATE log set ISUNIQUE = 'FALSE' where FILENAME='"+pfilename+"';";
				stmt.executeUpdate(sql);
			} else {//no such entry add the asset normally...
				sql = "INSERT INTO log ( FILENAME, ISUNIQUE )"+
					"VALUES ( '"+pfilename+"','TRUE');";
				stmt.executeUpdate(sql);
			}
		} catch ( Exception e ) {
			stmt.close();
			conn.close();
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}

	public void testDB ( ) throws Exception {



		Statement stat = conn.createStatement();
		//stat.executeUpdate("drop table if exists people;");
		stat.executeUpdate("CREATE TABLE IF NOT EXISTS log (name, occupation);");
		//stat.executeUpdate("CREATE table log (name, occupation);");
		PreparedStatement prep = conn.prepareStatement(
		    "insert into people values (?, ?);");
/*
		prep.setString(1, "Gandhi");
		prep.setString(2, "politics");
		prep.addBatch();
		prep.setString(1, "Turing");
		prep.setString(2, "computers");
		prep.addBatch();
		prep.setString(1, "Wittgenstein");
		prep.setString(2, "smartypants");
		prep.addBatch();
*/
		conn.setAutoCommit(false);
		prep.executeBatch();
		conn.setAutoCommit(true);

		ResultSet rs = stat.executeQuery("select * from people;");

		rs.close();
		conn.close();
	    }

}

