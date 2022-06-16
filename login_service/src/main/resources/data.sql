INSERT INTO roles ( id , name) VALUES ( 1, 'CUSTOMER');
INSERT INTO roles ( id , name) VALUES ( 2, 'ADMIN');
INSERT INTO roles (id, name) VALUES (0, 'EMBEDDED_SYSTEM');

/* INSERT STARTUP ADMIN IN USER
    nickname: "startup_admin"
   password : "Password2022!"
*/
INSERT INTO users (id, active, email, nickname, password, enrolling_capabilities) VALUES (1, true, 'admin@gmail.com', 'startup_admin', '$2a$10$tF89eHwXEblVRSeeIUB79e8pcTr5euSFSq8MOz.u0jZmgcToGz8Ha', true);
INSERT INTO user_role (user_id, role_id) VALUES (1, 2);

/* INSERT STARTUP EMBEDDED IN USER
    nickname: "embedded_system"
   password : "Password2022!"
*/
INSERT INTO users (id, active, email, nickname, password) VALUES (0, true, 'embeddedsystem@gmail.com', 'embedded_system', '$2a$10$tF89eHwXEblVRSeeIUB79e8pcTr5euSFSq8MOz.u0jZmgcToGz8Ha');
INSERT INTO user_role (user_id, role_id) VALUES (0, 0);

/*TODO : check if tests have problems with the insertion of startup user*/


