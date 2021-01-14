package at.webCrawler;

import java.sql.*;


public class DataBaseFunction {

    /**
     * establishes and closes a connection to the DB
     * write a row into DB webcrawler.target
     * !!!shouldnt write an entire entry, instead write only the url
     *
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
     *
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

            if (result.next()) {
                System.out.println(baseUrl + " exisitiert in db");
                return false;
            } else {
                System.out.println(baseUrl + " exisitiert nicht in db");
                return true;
            }
        } catch (SQLException exc) {
            exc.printStackTrace();
            return false;
        }
    }

    /**
     * returns one complete row from DB.target
     * @param baseUrl from DB.target
     * @return Target t
     */
    public static Target readTargetRow(String baseUrl) {
        Target t = null;

        try {
            Connection con = DataBaseMaster.getInstance().getDbCon();
            String statement = "SELECT id, url, lastupdate, nextvisit, title, description FROM target WHERE url = ?";
            PreparedStatement ps = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            ps.setString(2, baseUrl);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                t = new Target(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getTimestamp(3),
                        rs.getInt(4),
                        rs.getString(5),
                        rs.getString(6)
                );
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            DataBaseMaster.getInstance().closeDatabase();
        }
        return t;
    }

    /**
     * returns the ID row from DB.target
     * @param baseUrl from DB.target via method
     * @return int
     */
    public static int readTargetId(String baseUrl) {
        int targetId = -1;

        try {
            Connection con = DataBaseMaster.getInstance().getDbCon();
            String statement = "SELECT id FROM target WHERE url = ?";
            PreparedStatement ps = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, baseUrl);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                targetId = rs.getInt(1);
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            DataBaseMaster.getInstance().closeDatabase();
        }
        return targetId;
    }


    public static boolean writeTargetUrl(String baseUrl) {
        try {
            Connection con = DataBaseMaster.getInstance().getDbCon();
            String statement = "INSERT INTO webcrawler.target " +
                    "(url) VALUES (?);";
            PreparedStatement ps = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, baseUrl);
            int rows = ps.executeUpdate();
            return true;
        } catch (SQLException exc) {
            exc.printStackTrace();
            return false;
        } /* finally {                                                                  ??????????????????????????
            con.closeDatabase();
            con.close();
        } */
    }

    public static boolean writeDB_titel(String titel, int targetId) {
        try {
            Connection con = DataBaseMaster.getInstance().getDbCon();
            String statement = "UPDATE webcrawler.target SET title = ? WHERE id = ?;";
            PreparedStatement ps = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, titel);
            ps.setInt(2, targetId);
            int rows = ps.executeUpdate();
            return true;
        } catch (SQLException exc) {
            exc.printStackTrace();
            return false;
        } /* finally {                                                                  ??????????????????????????
            con.closeDatabase();
            con.close();
        } */
    }

    public static boolean writeDB_Description(String textDescription, int targetId) {
        try {
            Connection con = DataBaseMaster.getInstance().getDbCon();
            String statement = "UPDATE webcrawler.target SET description = ? WHERE id = ?;";
            PreparedStatement ps = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, textDescription);
            ps.setInt(2, targetId);
            int rows = ps.executeUpdate();
            return true;
        } catch (SQLException exc) {
            exc.printStackTrace();
            return false;
        } /* finally {                                                                  ??????????????????????????
            con.closeDatabase();
            con.close();
        } */
    }

    public static String readDbNextTarget() {
        String nextTarget = "";
        try {
            Connection con = DataBaseMaster.getInstance().getDbCon();
            String statement = "SELECT url FROM target WHERE lastupdate IS NULL";
            PreparedStatement ps = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                nextTarget = rs.getString(1);
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            DataBaseMaster.getInstance().closeDatabase();
        }
        return nextTarget;

    }

    public static boolean writeTargetRow(Target target) {
        int nextvisit = 1440;
        try {
            Connection con = DataBaseMaster.getInstance().getDbCon();
            String statement = "INSERT INTO webcrawler.target " +
                    "(url, lastupdate, nextvisit, title, description) " +
                    "VALUES (?, ?, ?, ?, ?) WHERE target.id = ?;";
            PreparedStatement ps = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, target.targetUrl);
            ps.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setInt(3, nextvisit);
            ps.setString(4, target.targetTitle);
            ps.setString(5, target.targetDescription);
            ps.setInt(6, target.targetId);
            int rows = ps.executeUpdate();

            //close connection when row is written ????
            // or
            //each time we read/write                                                  ???????????????????????????
            //DataBaseMaster.closeDatabase();
            return true;
        } catch (SQLException exc) {
            exc.printStackTrace();
            return false;
        } /* finally {                                                                  ??????????????????????????
            con.closeDatabase();
            con.close();
        } */
    }




}
