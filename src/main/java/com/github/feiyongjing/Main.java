package com.github.feiyongjing;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class Main {
    private static final String USER_WAME="root";
    private static final String PASSEORD="root";
    private static List<String> loadUrlsFromDatabase(Connection connection, String spl) throws SQLException {
        List<String> results = new ArrayList<>();
        ResultSet resultSet = null;
        try (PreparedStatement statement = connection.prepareStatement(spl)) {
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                results.add(resultSet.getNString(1));
            }
        }finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
        return results;
    }

    @SuppressFBWarnings("DMI_CONSTANT_DB_PASSWORD")
    public static void main(String[] args) throws IOException, SQLException {
        Connection connection = DriverManager.getConnection("jdbc:h2:file:E:\\Multithreaded-crawler/news", USER_WAME, PASSEORD);
        while (true) {
            //待处理的池子
            List<String> linkPool = loadUrlsFromDatabase(connection, "select link from LINKS_TO_BE_PROCESSED");
            if (linkPool.isEmpty()) {
                break;
            }

            String link = linkPool.remove(linkPool.size() - 1);
            insertLinkInToDatabase(connection, link, "delete from LINKS_TO_BE_PROCESSED where link = ?");

            if (isLinkProcessed(connection, link)) {
                continue;
            }

            if (IsItnecessary(link)) {

                System.out.println(link);
                Document doc = HttpGetAndParseHtml(URLExceptionHandling(link));

                parseUrisFromPageAndStoreInToDatabase(connection, linkPool, doc);

                storeInToDatabaseIfItIsNewspage(connection, doc);

                insertLinkInToDatabase(connection, link, "insert into LINKS_ALREADY_PROCESSFD (link) values (?)");
            }
        }
    }

    private static void parseUrisFromPageAndStoreInToDatabase(Connection connection, List<String> linkPool, Document doc) throws SQLException {
        for (Element aTag : doc.select("a")) {
            String href = aTag.attr("href");
            linkPool.add(href);
            insertLinkInToDatabase(connection, href, "insert into LINKS_TO_BE_PROCESSED (link) values (?)");
        }
    }

    private static boolean isLinkProcessed(Connection connection, String link) throws SQLException {
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

    private static void insertLinkInToDatabase(Connection connection, String href, String spl) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(spl)) {
            statement.setString(1, href);
            statement.executeUpdate();
        }
    }

    private static void storeInToDatabaseIfItIsNewspage(Connection connection, Document doc) {
        ArrayList<Element> articleTags = doc.select("article");
        if (!articleTags.isEmpty()) {
//            for (Element articleTag : articleTags) {
            String title = articleTags.get(0).child(0).text();
            System.out.println(title);
//            }
        }
    }

    private static Document HttpGetAndParseHtml(String urlExceptionHandling) throws IOException {
        String html;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(urlExceptionHandling);
        httpGet.addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.81 Safari/537.36");

        try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            html = EntityUtils.toString(entity1);
        }
        return Jsoup.parse(html);
    }

    private static String URLExceptionHandling(String link) {
        if (link.startsWith("//")) {
            link = "https:" + link;
            System.out.println(link);
        }
        if (link.contains("\\/")) {
            link = link.replace("\\/", "/");
        }
        return link;
    }

    private static boolean IsItnecessary(String link) {
        return link.contains("news.sina.cn") || "https://sina.cn".equals(link);
    }

}
