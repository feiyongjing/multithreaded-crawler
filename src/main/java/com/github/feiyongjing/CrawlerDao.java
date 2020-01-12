package com.github.feiyongjing;

        import java.sql.SQLException;

public interface CrawlerDao {
    String getNextLink(String spl) throws SQLException;

    String getNextLinkThenDelete() throws SQLException;

    void updateDatabase(String link, String spl) throws SQLException;

    boolean isLinkProcessed(String link) throws SQLException;

    void insertNewsIntoDatabase(String link, String title, String content) throws SQLException;

}
