drop table if exists tickets_acquired;
drop table if exists transits;
drop table if exists user_details;


create table if not exists  user_details (
                                        username VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    address VARCHAR(255) ,
    date_of_birth DATE,
    telephone_number VARCHAR(255),
    PRIMARY KEY (username)
    );





create table if not exists  tickets_acquired (
                                       ticket_id BIGINT GENERATED ALWAYS AS IDENTITY,
                                       valid_from VARCHAR(255) TIMESTAMP,
    issued_at VARCHAR(255) TIMESTAMP ,
    expiry VARCHAR(255) TIMESTAMP ,
    zone_id VARCHAR(255) NOT NULL ,
    type VARCHAR(255) NOT NULL ,
    jws VARCHAR(255) NOT NULL  ,
    user_id VARCHAR(255) NOT NULL
    PRIMARY KEY (ticket_id),
    CONSTRAINT fk_user_det
        FOREIGN KEY(user_id)
        REFERENCES user_details(username)

    );

create table if not exists  transits(
                                        transit_id BIGINT GENERATED ALWAYS AS IDENTITY,
    timestamp TIMESTAMP NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (transit_id),
    CONSTRAINT fk_user_det2
    FOREIGN KEY(user_id)
    REFERENCES user_details(username)
)