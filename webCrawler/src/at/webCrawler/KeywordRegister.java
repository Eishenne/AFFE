package at.webCrawler;

import org.w3c.dom.Node;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class KeywordRegister {

    public static void clearAndRegisterKeywords(int targetId, HashMap<String, Integer> keywords) {
        // clear old keywords
        try {
            Connection con = DataBaseMaster.getInstance().getDbCon();
            String statement = "DELETE FROM webcrawler.searchresult WHERE FK_targetId = ?";
            PreparedStatement ps = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, targetId);
            int rows = ps.executeUpdate();
        } catch (SQLException exc) {
            exc.printStackTrace();
        }

        // Add all keywords
        for (String k : keywords.keySet()) {
            //--keyword, relevanz, targetId an SQL übergeben
            writeKeyword(k, keywords.get(k), targetId);
        }
    }

    public static boolean writeKeyword(String keyword, int relevanz, int targetId){
        try {
            Connection con = DataBaseMaster.getInstance().getDbCon();
            String statement = "INSERT INTO webcrawler.searchresult (keyword, relevanz, FK_targetId)\n" +
                    "VALUES (?, ?, ? );";
            PreparedStatement ps = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, keyword);
            ps.setInt(2, relevanz);
            ps.setInt(3, targetId);
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



}