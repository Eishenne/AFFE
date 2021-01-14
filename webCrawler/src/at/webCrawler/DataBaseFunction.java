package at.webCrawler;

import java.sql.*;

//import static at.webCrawler.DataBaseMaster.closeDatabase;

public class DataBaseFunction {

    /**
     * establishes and closes a connection to the DB
     * write a row into DB webcrawler.target
     * !!!shouldnt write an entire entry, instead write only the url
     * @return true if successfull
     * Todo: doesnt close the connection
     */
    public static boolean writeTargetToTargetlist(String baseUrl) {
        int nextvisit = 1440;

        try {
            Connection con = DataBaseMaster.getInstance().getDbCon();
            String statement = "INSERT INTO webcrawler.target " +
                    "(url, lastupdate, nextvisit, title, description) " +
                    "VALUES (?, ?, ?, ?, ?);";
            PreparedStatement ps = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, baseUrl);
            ps.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setInt(3, nextvisit);
            ps.setString(4, "Methode searchAndCreateTitel");
            //Todo: erstellen - createTitel()

            ps.setString(5, "Methode searchAndCreateDescription");
            //Todo: erstellen - createDescription()

            int rows = ps.executeUpdate();

            //close connection when row is written ????
            // or
            //each time we read/write                                                  ???????????????????????????
            //DataBaseMaster.closeDatabase();
            return true;

            //executeUpdate returns 1 if rows have been written
//            if (rows > 0) {
//                ResultSet resultSet = ps.getGeneratedKeys();
//                if (resultSet.next()) {
//                    this.id = resultSet.getLong(1);
//                    return true;
//                }
//            }
//            return false;
        } catch (SQLException exc) {
            exc.printStackTrace();
            return false;
        } /* finally {                                                                  ??????????????????????????
            con.closeDatabase();
            con.close();
        } */
    }

    /**
     * returns true if URL not found in DB
     * @param baseUrl String with URL
     * @return true if URL not found
     */
    public static boolean newTarget(String baseUrl) {
        ResultSet result = null;
        try {
            Connection con = DataBaseMaster.getInstance().getDbCon();
            String statement = "SELECT id FROM target\n" +
                    "WHERE url = ?";
            PreparedStatement ps = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, baseUrl);
            result = ps.executeQuery();

            if(result.next()) {
                System.out.println("URL exisitiert in db");
                return false;
            } else {
                System.out.println("URL exisitiert nicht in db");
                return true;
            }
        } catch (SQLException exc) {
            exc.printStackTrace();
            return false;
        }
    }

    //target row vollständig schreiben und searchtarget rows für alle keywords

    // beides soll während des durcharbeitens des body / header ermittelt/erzeugt werden
    public static void createTitel() {

    }

    public static void createDescription() {

    }


    public static void createSearchResult() {

    }
}
