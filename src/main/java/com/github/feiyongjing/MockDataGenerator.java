package com.github.feiyongjing;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.Random;

public class MockDataGenerator{
    private static final int TARGET_ROW_COUNT = 100_0000;

    public static void main(String[] args) {
        SqlSessionFactory sqlSessionFactory;
        try {
            String resource = "db/mybatis/config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            List<News> currentNews = session.selectList("com.github.feiyongjing.MockMapper.selectNews");
            mockData(sqlSessionFactory,currentNews);
        }

    }

    public static void mockData(SqlSessionFactory sqlSessionFactory, List<News> currentNews) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            int count = TARGET_ROW_COUNT - currentNews.size();
            Random random = new Random();
            try {
                while (count-- > 0) {
                    int index = random.nextInt(currentNews.size());
                    News newsToBeInserted = new News(currentNews.get(index));

                    Instant currentTime = newsToBeInserted.getCreatedAt();
                    currentTime = currentTime.minusSeconds(random.nextInt(3600 * 24 * 365));
                    newsToBeInserted.setCreatedAtd(currentTime);
                    newsToBeInserted.setModifiedAt(currentTime);
                    session.insert("com.github.feiyongjing.MockMapper.insertNews", newsToBeInserted);
                    if (count % 2000 == 0) {
                        System.out.println(count);
                        session.flushStatements();
                    }
                }
            } catch (Exception e) {
                session.rollback();
            }
            session.commit();
        }
    }

}
