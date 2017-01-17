package utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.sqlite.*;

import static com.vftlite.core.VFT.*;

public class JDBC {

	public static final String URL = "jdbc:sqlite:" + System.getProperty("java.class.path").split("bin|target")[0] + "dataset/db/database.db";  
	public static final String DRIVER = "org.sqlite.JDBC";
	
	public static void initializeDB() throws Exception {

		String sqlDeviceModel = "CREATE TABLE IF NOT EXISTS DeviceModel (\n"
				+ " id integer PRIMARY KEY AUTOINCREMENT,\n"
				+ " maker text NOT NULL,\n"
				+ " model text NOT NULL,\n"
				+ " UNIQUE(maker, model)\n"
				+ " );";
		
		String sqlOperatingSystem = "CREATE TABLE IF NOT EXISTS OperatingSystem (\n"
				+ " id integer PRIMARY KEY AUTOINCREMENT,\n"
				+ " name text NOT NULL,\n"
				+ " version text NOT NULL,\n"
				+ " UNIQUE(name, version)\n"
				+ " );";
		
		String sqlVideoFile = "CREATE TABLE IF NOT EXISTS VideoFile (\n"
				+ " id integer PRIMARY KEY AUTOINCREMENT,\n"
				+ " title text NOT NULL,\n"
				+ " device_id text NOT NULL,\n"
				+ " device_model integer NOT NULL,\n"
				+ " operating_system integer NOT NULL,\n"
				+ " pathtofile text NOT NULL,\n"
				+ " pathtoxml text NOT NULL,\n"
				+ " pathtoinfo text NOT NULL,\n"
				+ " UNIQUE(title),\n"
				+ " FOREIGN KEY(device_model) REFERENCES DeviceModel(id),\n"
				+ " FOREIGN KEY(operating_system) REFERENCES OperatingSystem(id)\n"
				+ " )";
		
		executeQuery(sqlDeviceModel);
		executeQuery(sqlOperatingSystem);
		executeQuery(sqlVideoFile);
		
		System.out.println("Database initialized at " + URL.replace("jdbc:sqlite:", ""));
		
	}
	
	public static boolean executeQuery(String query) throws Exception {
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	    	conn = getConnection();
	        stmt = conn.createStatement();
	        stmt.execute(query);
	        return true;
	    } catch(SQLException e) {
	        throw new Exception("Exception during statement execution", e);
	    } finally {
	        stmt.close();
	        conn.close();
	    }
	}
	
	private static Connection getConnection() throws ClassNotFoundException {  
		Class.forName(DRIVER);  
	    Connection conn = null;  
	    try {  
	        SQLiteConfig config = new SQLiteConfig();  
	        config.enforceForeignKeys(true);  
	        conn = DriverManager.getConnection(URL, config.toProperties());
	    } catch (SQLException e) {
	    	System.out.println(e.getMessage());
	    }  
	    return conn;
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
				if (!new File(f.getAbsolutePath() + ".xml").exists()) {
					parse(f.getAbsolutePath(), folder.getAbsolutePath());
				}
				//parse info xml
				Info info = new Info(f.getAbsolutePath());
				//insert to db
				insertVideo(info);
				System.out.println("updated: " + f.getAbsolutePath());
			}
		}
	}
	
	public static void insertVideo(Info info) throws Exception {
		
		String sqlDeviceModel = "INSERT INTO DeviceModel(id, maker, model)\n"
				+ " SELECT NULL, '" + info.getManufacturer() + "', '" + info.getModel() + "'\n"
				+ " WHERE NOT EXISTS(SELECT 1 FROM DeviceModel\n"
				+ " WHERE maker = '"+ info.getManufacturer() +"' AND model = '" + info.getModel() + "');";
		
		String sqlOperatingSystem = "INSERT INTO OperatingSystem(id, name, version)\n"
				+ " SELECT NULL, '" + info.getOS() + "', '" + info.getVersion() + "'\n"
				+ " WHERE NOT EXISTS(SELECT 1 FROM OperatingSystem\n"
				+ " WHERE name = '"+ info.getOS() +"' AND version = '" + info.getVersion() + "');";
		
		executeQuery(sqlDeviceModel);
		executeQuery(sqlOperatingSystem);
		
		String sqlVideoFile = "INSERT OR IGNORE INTO VideoFile(id, title, device_id, device_model, operating_system, pathtofile, pathtoxml, pathtoinfo)\n"
				+ " VALUES (NULL,\n"
				+ " '" + info.getTitle() + "',\n"
				+ " '" + info.getDeviceID() + "',\n"
				+ " (SELECT id FROM DeviceModel WHERE maker = '" + info.getManufacturer() + "' AND model = '" + info.getModel() + "'),\n"
				+ " (SELECT id FROM OperatingSystem WHERE name = '" + info.getOS() + "' AND version = '" + info.getVersion() + "'),\n"
				+ " '" + info.getPathToFile() + "',\n"
				+ " '" + info.getPathToXml() + "',\n"
				+ " '" + info.getPathToInfo() + "'\n"
				+ " );";
		
		executeQuery(sqlVideoFile);
		
	}
	
}
