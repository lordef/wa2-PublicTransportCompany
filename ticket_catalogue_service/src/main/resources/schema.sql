drop table if exists orders;
drop table if exists tickets;

create table if not exists  tickets (
    ticket_id BIGINT GENERATED ALWAYS AS IDENTITY,
    price DECIMAL(10,2) NOT NULL ,
    type VARCHAR(255) NOT NULL,
    min_age INT,
    max_age INT,
    PRIMARY KEY (ticket_id)
    );





create table if not exists  orders (
                                        order_id BIGINT GENERATED ALWAYS AS IDENTITY,
                                        status VARCHAR(20) NOT NULL ,
                                        user_id VARCHAR(255) NOT NULL ,
                                        quantity INTEGER NOT NULL,
                                        total_price NUMERIC(7,2) NOT NULL,
                                        ticket_type BIGINT NOT NULL ,
                                        not_before VARCHAR(255) NOT NULL,
                                        zone_id VARCHAR(255) NOT NULL,
                                        PRIMARY KEY (order_id),
                                        CONSTRAINT fk_customer
                                            FOREIGN KEY(ticket_type)
                                                REFERENCES tickets(ticket_id)

);

INSERT INTO tickets ( price, type, min_age, max_age) VALUES ( 1.70, 'ordinary',NULL,NULL);             --1
INSERT INTO tickets ( price, type, min_age, max_age) VALUES ( 3.00, 'daily',NULL,NULL);                --2
INSERT INTO tickets ( price, type, min_age, max_age) VALUES ( 10.00, 'weekly',NULL,NULL);              --3
INSERT INTO tickets ( price, type, min_age, max_age) VALUES ( 24.00, 'monthly',NULL,NULL);             --4
INSERT INTO tickets ( price, type, min_age, max_age) VALUES ( 60.00, 'biannually',NULL,NULL);          --5
INSERT INTO tickets ( price, type, min_age, max_age) VALUES ( 110.00, 'yearly',NULL,NULL);             --6
INSERT INTO tickets ( price, type, min_age, max_age) VALUES ( 3.80, 'weekend_pass', NULL, 27);         --7
