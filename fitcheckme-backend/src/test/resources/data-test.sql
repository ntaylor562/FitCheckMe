--Test data

--Users (# of users: 2)
insert into community.user (username, email, password, bio) values ('test_user', 'test_user@test.com', '$2a$10$kqqXbO62d0fIuzUsQtqLJ.jML3nawowcmvVcHSAWx0lzasSLGXCIO', 'test bio');
insert into community.user (username, email, password, bio) values ('test_super_admin', 'test_super_admin@test.com', '$2a$10$kqqXbO62d0fIuzUsQtqLJ.jML3nawowcmvVcHSAWx0lzasSLGXCIO', 'test bio');

--Roles (# of roles: 5)
insert into app.role (role_name) values ('USER');
insert into app.role (role_name) values ('OUTFIT_ADMIN');
insert into app.role (role_name) values ('GARMENT_ADMIN');
insert into app.role (role_name) values ('USER_ADMIN');
insert into app.role (role_name) values ('SUPER_ADMIN');

--User roles (# of user roles: 3)
insert into app.user_role (user_id, role_id) values (1, 1);

insert into app.user_role (user_id, role_id) values (2, 1);
insert into app.user_role (user_id, role_id) values (2, 5);
