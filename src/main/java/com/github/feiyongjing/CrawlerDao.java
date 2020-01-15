package com.github.feiyongjing;

        import java.sql.SQLException;

public interface CrawlerDao {
    String getNextLinkThenDelete() throws SQLException;

    boolean isLinkProcessed(String link) throws SQLException;

    void insertNewsIntoDatabase(String link, String title, String content) throws SQLException;

    void insertProcessedLink(String link) throws SQLException;

    void insertLinkToBeprocessed(String href) throws SQLException;
}
