alter session set container = XEPDB1;

ALTER SYSTEM SET db_create_file_dest = '/opt/oracle/oradata';

create tablespace SRM;

create user "SRM_OWNER" profile "DEFAULT" identified by "password" default tablespace "SRM" account unlock;

grant connect to SRM_OWNER;
grant unlimited tablespace to SRM_OWNER;

grant create view to SRM_OWNER;
grant create sequence to SRM_OWNER;
grant create table to SRM_OWNER;
grant create procedure to SRM_OWNER;
grant create type to SRM_OWNER;
grant create trigger to SRM_OWNER;