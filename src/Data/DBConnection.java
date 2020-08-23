package Data;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String DB_NAME = "U07Q3g";
    private static final String DB_URL = "jdbc:mysql://3.227.166.251/" + DB_NAME;
    private static final String USERNAME = "U07Q3g";
    private static final String PASSWORD = "53689098455";
    private static final String DRIVER = "com.mysql.jdbc.Driver";

    static Connection DB_CONNECTION;

    public static void connect() throws Exception {
        Class.forName(DRIVER);
        DB_CONNECTION = (Connection) DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        System.out.println("Database connected.");
    }

    public static void disconnect() throws Exception {
        DB_CONNECTION.close();
        System.out.println("Database disconnected.");
    }
}
