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
                                        PRIMARY KEY (order_id),
                                        CONSTRAINT fk_customer
                                            FOREIGN KEY(ticket_type)
                                                REFERENCES tickets(ticket_id)

);

INSERT INTO tickets ( price, type, min_age, max_age) VALUES ( 3.92, 'daily',NULL,NULL);
INSERT INTO tickets ( price, type, min_age, max_age) VALUES ( 2.17, 'pass',NULL,NULL);
INSERT INTO tickets ( price, type, min_age, max_age) VALUES ( 2.17, 'under_27',1,26);