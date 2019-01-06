create sequence hibernate_sequence start with 1 increment by 1
create table address (id bigint not null, city varchar(255), number integer, state varchar(255), street varchar(255), type varchar(255), unit varchar(255), zip_code varchar(255), identification_id bigint, primary key (id))
create table communication (id bigint not null, preferred boolean, type varchar(255), value varchar(255), identification_id bigint, primary key (id))
create table identification (id bigint not null, dob date, first_name varchar(255), gender integer, last_name varchar(255), title varchar(255), primary key (id))
alter table address add constraint FK4t17c42xmh7ekk1hyby5xe3j3 foreign key (identification_id) references identification
alter table communication add constraint FK816uukfmisewws7hlofgdhpfj foreign key (identification_id) references identification
