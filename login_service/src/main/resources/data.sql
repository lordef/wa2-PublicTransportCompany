INSERT INTO roles ( id , name) VALUES ( 1, 'CUSTOMER');
INSERT INTO roles ( id , name) VALUES ( 2, 'ADMIN');

/* INSERT DEFAULT CUSTOMER IN USER
    nickname: "testCustomer"
   password : "Password2022!"
*/
INSERT INTO users (id, active, email, nickname, password) VALUES (1, true, 'ferdinandomicco@outlook.it', 'testCustomer', '$2a$10$tF89eHwXEblVRSeeIUB79e8pcTr5euSFSq8MOz.u0jZmgcToGz8Ha');
INSERT INTO user_role (user_id, role_id) VALUES (1, 1);


