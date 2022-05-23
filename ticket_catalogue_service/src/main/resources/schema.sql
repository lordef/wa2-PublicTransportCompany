drop table if exists tickets;

create table if not exists  tickets (
    ticket_id BIGINT NOT NULL  PRIMARY KEY,
    price DECIMAL(10,2) NOT NULL ,
    type VARCHAR(255) NOT NULL
    );

INSERT INTO tickets ( ticket_id , price, type) VALUES ( 1, 3.92, 'daily');
INSERT INTO tickets ( ticket_id , price, type) VALUES ( 2, 2.17, 'pass');
INSERT INTO tickets ( ticket_id , price, type) VALUES ( 3, 2.17, 'ordinary');