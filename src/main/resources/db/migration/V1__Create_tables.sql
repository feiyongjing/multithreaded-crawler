create table LINKS_TO_BE_PROCESSED(
    link varchar(2000)
);

create table LINKS_ALREADY_PROCESSFD(
    link varchar(2000)
);

create table news(
                     id bigint primary key auto_increment,
                     title text,
                     content text,
                     url varchar(5000),
                     created_at timestamp,
                     modified_at timestamp
);
