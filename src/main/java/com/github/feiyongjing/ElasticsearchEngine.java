package com.github.feiyongjing;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ElasticsearchEngine {
    /**
     * 命令行搜索数据
     */
    public static void main(String[] args) throws IOException {
        while(true){
            System.out.println("Please input a search keyword: ");
            BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
            String keyword = reader.readLine();
//            System.out.println(keyword);
            search(keyword);
        }
    }

    private static void search(String keyword) {
        try (RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost("localhost", 9200, "http"),
                new HttpHost("localhost", 9300, "http")))) {
            SearchRequest searchResult=new SearchRequest("news");
            searchResult.source(new SearchSourceBuilder().query(new MultiMatchQueryBuilder(keyword,"title","content")));
            SearchResponse response = client.search(searchResult, RequestOptions.DEFAULT);
            response.getHits().forEach(hit-> System.out.println(hit.getSourceAsString()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
