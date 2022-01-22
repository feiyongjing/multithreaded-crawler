package com.github.feiyongjing;

public class Main {
    /**
     * 爬取数据到数据库
     */
    public static void main(String[] args) {
//        CrawlerDao dao = new JdbcCrawlerDao();
        CrawlerDao dao = new MyBatisCrawlerDao();
        for (int i = 0; i <10; i++) {
            new Crawler(dao).start();
        }
    }
}
