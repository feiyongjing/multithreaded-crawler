package com.github.feiyongjing;

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
import java.util.stream.Collectors;


public class Crawler {
//    CrawlerDao dao = new JdbcCrawlerDao();
    CrawlerDao dao = new MyBatisCrawlerDao();

    public void run() throws SQLException, IOException {
        String link;
        while ((link = dao.getNextLinkThenDelete()) != null) {

            if (dao.isLinkProcessed(link)) {
                continue;
            }

            if (IsItnecessary(link)) {

                System.out.println(link);
                Document doc = HttpGetAndParseHtml(URLExceptionHandling(link));

                parseUrisFromPageAndStoreInToDatabase(doc);

                storeInToDatabaseIfItIsNewspage(doc, link);

                dao.insertProcessedLink(link);
//                dao.updateDatabase(link, "insert into LINKS_ALREADY_PROCESSFD (link) values (?)");
            }
        }
    }


    public static void main(String[] args) throws IOException, SQLException {
        new Crawler().run();
    }

    public void parseUrisFromPageAndStoreInToDatabase(Document doc) throws SQLException {
        for (Element aTag : doc.select("a")) {
            String href = aTag.attr("href");
            dao.insertLinkToBeprocessed(href);
//            dao.updateDatabase(href, "insert into LINKS_TO_BE_PROCESSED (link) values (?)");
        }
    }


    public void storeInToDatabaseIfItIsNewspage(Document doc, String link) throws SQLException {
        ArrayList<Element> articleTags = doc.select("article");
        if (!articleTags.isEmpty()) {
            for (Element articleTag : articleTags) {
                String title = articleTags.get(0).child(0).text();
                String content = articleTag.select("p").stream().map(Element::text).collect(Collectors.joining("\n"));
//                System.out.println(title);

                dao.insertNewsIntoDatabase(link, title, content);
            }
        }
    }


    private Document HttpGetAndParseHtml(String urlExceptionHandling) throws IOException {
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
