package utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.sqlite.*;

import static com.vftlite.core.VFT.*;
import static utils.Utils.*;

public class JDBC {

	public static final String URL = "jdbc:sqlite:dataset/db/database.db";  
	public static final String DRIVER = "org.sqlite.JDBC";
	
	public static void initializeDB() {
				
		String sqlDeviceModel = "CREATE TABLE IF NOT EXISTS DeviceModel (\n"
				+ " id integer PRIMARY KEY AUTOINCREMENT,\n"
				+ " name text NOT NULL,\n"
				+ " version text NOT NULL,\n"
				+ " UNIQUE(name, version)\n"
				+ ");";
		
		String sqlOperatingSystem = "CREATE TABLE IF NOT EXISTS OperatingSystem (\n"
				+ " id integer PRIMARY KEY AUTOINCREMENT,\n"
				+ " maker text NOT NULL,\n"
				+ " model text NOT NULL,\n"
				+ " UNIQUE(maker, model)\n"
				+ ");";
		
		String sqlVideoFile = "CREATE TABLE IF NOT EXISTS VideoFile (\n"
				+ " id integer PRIMARY KEY AUTOINCREMENT,\n"
				+ " title text,\n"
				+ " device_model integer NOT NULL,\n"
				+ " operating_system integer NOT NULL,\n"
				+ " pathtofile text,\n"
				+ " pathtoxml text,\n"
				+ " pathtoinfo text,\n"
				+ " UNIQUE(title),\n"
				+ " FOREIGN KEY(device_model) REFERENCES DeviceModel(id),\n"
				+ " FOREIGN KEY(operating_system) REFERENCES OperatingSystem(id)\n"
				+ ")";
		
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.execute(sqlDeviceModel);
			stmt.execute(sqlOperatingSystem);
			stmt.execute(sqlVideoFile);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	private static Connection getConnection() throws ClassNotFoundException {  
		Class.forName(DRIVER);  
	    Connection connection = null;  
	    try {  
	        SQLiteConfig config = new SQLiteConfig();  
	        config.enforceForeignKeys(true);  
	        connection = DriverManager.getConnection(URL, config.toProperties());
	    } catch (SQLException e) {
	    	System.out.println(e.getMessage());
	    }  
	    return connection;
	}
	
	public static void updateDB(String pathfolder) throws Exception {
		File folder = new File(pathfolder);
		if (!folder.exists() || !folder.isDirectory()) {
			System.err.println("Could not find the dataset folder at '" + pathfolder + "'");
		} else {
			update(folder);
		}
	}
	
	private static void update(File folder) throws Exception {
		File[] files = folder.listFiles();
		for (File f: files) {
			if (f.isFile() && !f.getName().matches(".*?\\.txt.*|.*?\\.xml.*") && !f.getName().startsWith(".")) {
				//parse file container
				parse(f.getAbsolutePath(), folder.getAbsolutePath()); //TODO don't if file already exists
				//parse info xml
				Info info = parseInfo(f.getAbsolutePath());
				//insert to db
				insertVideo(info);
			}
		}
	}
	
	public static void insertVideo(Info info) {
		
	}
	
}
