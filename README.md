### 实现多线程爬虫与ES新闻搜索引擎
启动数据库
```
docker run --name news -v 'E:\java-prbject\Multithreaded-crawler\mysql-date':/var/lib/mysql -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 -d mysql:8.0.18
create database news
```
启动elasticsearch
```
docker run --name news -v 'E:\java-prbject\Multithreaded-crawler\mysql-date':/var/lib/mysql -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 -d mysql:8.0.18
create database news
```
