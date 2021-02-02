package at.webCrawler;

import java.sql.*;

public class DataBaseFunction {
    /**
     * write a row into DB.target after getting all relevant information for all columns
     *
     * @param baseUrl currentURL as String
     * @return true if write was successfull
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
            ps.setString(5, "Methode searchAndCreateDescription");
            int rows = ps.executeUpdate();
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
        } finally {
            DataBaseMaster.getInstance().closeDatabase();
        }
    }


    /**
     * returns true if URL not found in DB
     *
     * @param baseUrl String with URL
     * @return baseUrl is a new entry
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
//                System.out.println(baseUrl + " exisitiert in db");
                return false;
            } else {
//                System.out.println(baseUrl + " exisitiert nicht in db");
                return true;
            }
        } catch (SQLException exc) {
            exc.printStackTrace();
            return false;
        } finally {
            DataBaseMaster.getInstance().closeDatabase();
        }
    }


    /**
     * returns one complete row from DB.target
     *
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
     * returns id of baseUrl from DB.target
     *
     * @param baseUrl currentUrl as String
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


    /**
     * writes baseUrl to DB.target(url)
     *
     * @param baseUrl currentUrl as String
     * @return successful write
     */
    public static boolean writeTargetUrl(String baseUrl) {
        boolean success = false;
        //Url prüfen, maximallänge überschritten
        if (baseUrl.length() > 2048) {
            baseUrl = baseUrl.substring(0, 2048);
            System.out.println("baseUrl ist länger als 2048 Zeichen. Siehe XML-Sitemaps: max 2048.");
            return false;
        }

        try {
            Connection con = DataBaseMaster.getInstance().getDbCon();
            String statement = "INSERT INTO webcrawler.target " +
                    "(url) VALUES (?);";
            PreparedStatement ps = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, baseUrl);
            int rows = ps.executeUpdate();
            success = true;
        } catch (SQLException exc) {
            exc.printStackTrace();
            success = false;
        } finally {
            DataBaseMaster.getInstance().closeDatabase();
        }
        return success;
    }


    /**
     * updates title for DB.target at given id
     *
     * @param titel title of website
     * @param targetId
     * @return
     */
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
        } finally {
            DataBaseMaster.getInstance().closeDatabase();
        }
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
        } finally {
            DataBaseMaster.getInstance().closeDatabase();
        }
    }


    public static boolean writeDB_Datum(int nextVisit, int targetId) {
        try {
            Connection con = DataBaseMaster.getInstance().getDbCon();
            String statement = "UPDATE webcrawler.target SET (lastupdate, nextvisit) VALUES (?, ?) WHERE id = ?;";
            PreparedStatement ps = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            ps.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setInt(2, nextVisit);
            ps.setInt(3, targetId);
            int rows = ps.executeUpdate();
            return true;
        } catch (SQLException exc) {
            exc.printStackTrace();
            return false;
        } finally {
            DataBaseMaster.getInstance().closeDatabase();
        }
    }

    public static String readDB_nextTarget() {
        String targetUrl = "https://htmlunit.sourceforge.io/gettingStarted.html";
        //String targetUrl = "https://www.laendlejob.at";
        //String targetUrl = "https://vol.at";
        //targetUrl = DataBaseFunction.readDbNextTarget();
        // TODO: 13.01.2021 url aus DB nicht aufrufbar / unterschiedliches Format ->
        //  Format anpassen oder writeUrl anpassen

        try {
            Connection con = DataBaseMaster.getInstance().getDbCon();
            String statement = "select url from target order by lastupdate IS NULL DESC, lastupdate  limit 1";
            PreparedStatement ps = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                targetUrl = rs.getString(1);
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            DataBaseMaster.getInstance().closeDatabase();
        }
        return targetUrl;
    }


    public static void clearKeywords(int targetId) {
        try {
            Connection con = DataBaseMaster.getInstance().getDbCon();
            String statement = "DELETE FROM webcrawler.searchresult WHERE FK_targetId = ?";
            PreparedStatement ps = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, targetId);
            int rows = ps.executeUpdate();
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            DataBaseMaster.getInstance().closeDatabase();
        }
    }


    public static void writeKeyword(String keyword, int relevanz, int targetId) {
        if (keyword.length() > 165) {
            keyword = keyword.substring(0, 165);
        }

        try {
            Connection con = DataBaseMaster.getInstance().getDbCon();
            String statement = "INSERT INTO webcrawler.searchresult (keyword, relevanz, FK_targetId) " +
                    "VALUES (?, ?, ? );";
            PreparedStatement ps = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, keyword);
            ps.setInt(2, relevanz);
            ps.setInt(3, targetId);
            int rows = ps.executeUpdate();
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            DataBaseMaster.getInstance().closeDatabase();
        }
    }

    public static void writeDB_Nextvisit(int targetId, String title, String description, int nextvisit) {
        if (description.length() > 2048) {
            description = description.substring(0, 2048);
        }
        if (title.length() > 512) {
            title = title.substring(0, 512);
        }
        try {
            Connection con = DataBaseMaster.getInstance().getDbCon();
            String statement = "UPDATE webcrawler.target SET (lastupdate, nextvisit, title, description) VALUES (?, ?, ?, ?) WHERE id = ?;";
            PreparedStatement ps = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            ps.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setInt(2, nextvisit);
            ps.setString(3, title);
            ps.setString(4, description);
            ps.setInt(5, targetId);
            int rows = ps.executeUpdate();
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            DataBaseMaster.getInstance().closeDatabase();
        }
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

            return true;
        } catch (SQLException exc) {
            exc.printStackTrace();
            return false;
        } finally {
            DataBaseMaster.getInstance().closeDatabase();
        }
    }


    public static boolean updateTargetNextVisit(int targetId, String title, String description) {
        int nextvisit = 1440;
        if (description.length() > 2048) {
            description = description.substring(0, 2048);
        }
        if (title.length() > 512) {
            title = title.substring(0, 512);
        }

        try {
            Connection con = DataBaseMaster.getInstance().getDbCon();
            String statement =
                    "UPDATE target " +
                            "SET title= ?, nextvisit= ?, description = ?, lastupdate = ?" +
                            "WHERE id= ?;";

            PreparedStatement ps = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, title);
            ps.setInt(2, nextvisit);
            ps.setString(3, description);
            ps.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setInt(5, targetId);
            int rows = ps.executeUpdate();
            return true;
        } catch (SQLException exc) {
            exc.printStackTrace();
            return false;
        } finally {
            DataBaseMaster.getInstance().closeDatabase();
        }
    }
}
