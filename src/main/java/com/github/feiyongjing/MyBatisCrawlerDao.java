package com.github.feiyongjing;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MyBatisCrawlerDao implements CrawlerDao {
    private SqlSessionFactory sqlSessionFactory;

    public MyBatisCrawlerDao() {
        try {
            String resource = "db/mybatis/config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String getNextLinkThenDelete() {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            String url = session.selectOne("com.github.feiyongjing.MyMapper.selectNextAvailableLink");
            if (url != null) {
                session.delete("com.github.feiyongjing.MyMapper.deleteLink", url);
            }
            return url;
        }
    }

    @Override
    public boolean isLinkProcessed(String link) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            Integer count = session.selectOne("com.github.feiyongjing.MyMapper.countLink", link);
            return count != 0;
        }
    }

    @Override
    public void insertNewsIntoDatabase(String link, String title, String content) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            session.insert("com.github.feiyongjing.MyMapper.insertNews", new News(link, content, title));
        }
    }

    @Override
    public void insertProcessedLink(String link) {
        Map<String, Object> param = new HashMap<>();
        param.put("tableName", "LINKS_ALREADY_PROCESSED");
        param.put("link", link);
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            session.insert("com.github.feiyongjing.MyMapper.insertLink", param);
        }
    }

    @Override
    public void insertLinkToBeprocessed(String href) {
        Map<String, Object> param = new HashMap<>();
        param.put("tableName", "LINKS_TO_BE_PROCESSED");
        param.put("link", href);
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            session.insert("com.github.feiyongjing.MyMapper.insertLink", param);
        }
    }
}
