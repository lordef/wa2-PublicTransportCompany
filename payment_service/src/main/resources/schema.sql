drop table if exists transactions;

create table if not exists  transactions (
                                        transaction_id BIGINT GENERATED ALWAYS AS IDENTITY,
                                        amount DECIMAL(10,2) NOT NULL ,
                                        customer VARCHAR(255) NOT NULL,
                                        order_id BIGINT NOT NULL,
                                        date DATE NOT NULL,
                                        status VARCHAR(255) NOT NULL,
                                        PRIMARY KEY (transaction_id)
                                        );