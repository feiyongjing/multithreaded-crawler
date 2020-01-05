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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {
        //待处理的池子
        List<String> linkPool = new ArrayList<>();
        //已经处理的池子
        Set<String> processedLinks = new HashSet<>();
        linkPool.add("https://sina.cn");
        while (true) {
            if (linkPool.isEmpty()) {
                break;
            }
            String link = linkPool.remove(linkPool.size() - 1);
            if (processedLinks.contains(link)) {
                continue;
            }
            if (IsItnecessary(link)){

                System.out.println(link);
                Document doc=HttpGetAndParseHtml(URLExceptionHandling(link));

                doc.select("a").stream().map(aTag->aTag.attr("href")).forEach(linkPool::add);

                storeIntoDatabaseIfItIsNewspage(doc);

                processedLinks.add(link);
            } else {
                continue;
            }
        }
    }

    private static void storeIntoDatabaseIfItIsNewspage(Document doc) {
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
        if (link.startsWith("//")){
            link="https:"+link;
            System.out.println(link);
        }
        if (link.contains("\\/")){
            link = link.replace("\\/", "/");
        }
        return link;
    }

    private static boolean IsItnecessary(String link) {
        return link.contains("news.sina.cn") || "https://sina.cn".equals(link);
    }

}
