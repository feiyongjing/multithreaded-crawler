package com.github.feiyongjing;

import org.apache.http.HttpHost;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElasticsearchDataGenerator extends Thread {

    /**
     * 从数据库获取数据灌入Elasticsearch
     */
    public static void main(String[] args) {
        SqlSessionFactory sqlSessionFactory;
        try {
            String resource = "db/mybatis/config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<News> newsFromMySQL = getNewsFromMySQL(sqlSessionFactory);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> wrideSingleThread(newsFromMySQL)).start();
        }
    }

    private static void wrideSingleThread(List<News> newsFromMySQL) {

        try (RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost("localhost", 9200, "http"),
                new HttpHost("localhost", 9300, "http")))) {
            BulkRequest bulkRequest=new BulkRequest();
            for (int i = 0; i < 5_0000; i++) {
                for (News news : newsFromMySQL) {
                    IndexRequest request = new IndexRequest("news");
                    Map<String, Object> data = new HashMap<>();
                    data.put("content", news.getContent());
                    data.put("url", news.getUrl());
                    data.put("title", news.getTitle());
                    data.put("createdAt", news.getCreatedAt());
                    data.put("modifiedAt", news.getModifiedAt());

                    request.source(data, XContentType.JSON);
                    bulkRequest.add(request);

//                    IndexResponse response = client.index(request, RequestOptions.DEFAULT);
//                response.status().getStatus();
//                    System.out.println(response.status().getStatus());
                }
                BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                System.out.println("Current thread: "+Thread.currentThread().getName()+"finishes"+i+": "+bulkResponse.status().getStatus());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
    }

    private static List<News> getNewsFromMySQL(SqlSessionFactory sqlSessionFactory) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            List<News> currentNews = session.selectList("com.github.feiyongjing.MockMapper.selectNews");
            return currentNews;
        }
    }
}
