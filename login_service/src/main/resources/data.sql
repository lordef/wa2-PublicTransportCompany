INSERT INTO roles (id, name) VALUES (0, 'EMBEDDED_SYSTEM');
INSERT INTO roles ( id , name) VALUES ( 1, 'CUSTOMER');
INSERT INTO roles ( id , name) VALUES ( 2, 'ADMIN');
INSERT INTO roles (id, name) VALUES (3, 'ADMIN_E');

/* INSERT STARTUP ADMIN IN USER
    nickname: "startup_admin"
   password : "Password2022!"
*/
INSERT INTO users (id, active, email, nickname, password) VALUES (1, true, ' ', 'startup_admin', '$2a$10$tF89eHwXEblVRSeeIUB79e8pcTr5euSFSq8MOz.u0jZmgcToGz8Ha');
INSERT INTO user_role (user_id, role_id) VALUES (1, 3);

/* INSERT STARTUP EMBEDDED IN USER
    nickname: "embedded_system"
   password : "Password2022!"
*/
INSERT INTO users (id, active, email, nickname, password) VALUES (0, true, ' ', 'embedded_system', '$2a$10$tF89eHwXEblVRSeeIUB79e8pcTr5euSFSq8MOz.u0jZmgcToGz8Ha');
INSERT INTO user_role (user_id, role_id) VALUES (0, 0);

/*TODO : check if tests have problems with the insertion of startup user*/


