package at.webCrawler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseMaster {
    private static DataBaseMaster dbm = new DataBaseMaster();
    private Connection dbCon;

    private DataBaseMaster(){}

    public static DataBaseMaster getInstance(){
        return dbm;
    }

    /**
     * close connection to SQL DB
     */
    public void closeDatabase(){
        if (dbCon != null) {
            try {
                dbCon.close();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
            dbCon = null;
        }
    }

    /**
     * open connection to SQL DB
     * @return connection if successful
     */
    public Connection getDbCon() {
        if (dbCon == null){
            try {
                dbCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/webcrawler?" +
                        "serverTimezone=UTC", "khan", "123456");
            } catch (SQLException sqle){
                sqle.printStackTrace();
            }
        }
        return dbCon;
    }
}
