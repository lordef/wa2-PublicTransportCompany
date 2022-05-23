drop table if exists tickets;

create table if not exists  tickets (
    ticket_id BIGINT NOT NULL  PRIMARY KEY,
    price DECIMAL(10,2) NOT NULL ,
    type VARCHAR(255) NOT NULL
    );