package com.github.feiyongjing;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.sql.*;

public class JdbcCrawlerDao implements CrawlerDao {
    private static final String USER_WAME = "root";
    private static final String PASSWORD = "root";
    private final Connection connection;

    @SuppressFBWarnings("DMI_CONSTANT_DB_PASSWORD")
    public JdbcCrawlerDao() {
        try {
            this.connection = DriverManager.getConnection("jdbc:h2:file:E:\\Multithreaded-crawler/news", USER_WAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getNextLink(String spl) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(spl); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                return resultSet.getNString(1);
            }
        }
        return null;
    }

    public String getNextLinkThenDelete() throws SQLException {
        String link = getNextLink("select link from LINKS_TO_BE_PROCESSED limit 1");
        if (link != null) {
            updateDatabase(link, "delete from LINKS_TO_BE_PROCESSED where link = ?");
        }
        return link;
    }

    public void updateDatabase(String link, String spl) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(spl)) {
            statement.setString(1, link);
            statement.executeUpdate();
        }
    }

    public boolean isLinkProcessed(String link) throws SQLException {
        ResultSet resultSet = null;
        try (PreparedStatement statement = connection.prepareStatement("select link from LINKS_ALREADY_PROCESSFD where link = ?")) {
            statement.setString(1, link);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                return true;
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
        return false;
    }

    public void insertNewsIntoDatabase(String link, String title, String content) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("insert into NEWS (URL,TITLE,CONTENT,CREATED_AT,MODIFIED_AT) values (?,?,?,now(),now() )")) {
            statement.setString(1, link);
            statement.setString(2, title);
            statement.setString(3, content);
            statement.executeUpdate();
        }
    }

    @Override
    public void insertProcessedLink(String link) throws SQLException {
        updateDatabase(link, "insert into LINKS_ALREADY_PROCESSFD (link) values (?)");
    }

    @Override
    public void insertLinkToBeprocessed(String href) throws SQLException {
        updateDatabase(href, "insert into LINKS_TO_BE_PROCESSED (link) values (?)");
    }

}
