DROP TABLE t_sys_user;
CREATE TABLE t_sys_user (
  id varchar2(255) NOT NULL,
  loginName varchar2(255),
  manager varchar2(1) NOT NULL,
  name varchar2(255),
  orgId varchar2(255),
  password varchar2(255),
  created_on date,
  updated_on date,
  department varchar2(255),
  email varchar2(255),
  fax varchar2(255),
  mobile varchar2(255),
  phone varchar2(255),
  title varchar2(255),
  created_by number(11),
  owner number(11),
  updated_by number(11),
  area varchar2(255),
  areaName varchar2(255)
);
alter table t_sys_user add constraint pk_t_sys_user primary key (ID)
  using index 
  tablespace SYSTEM
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
comment on table t_sys_user is '”√ªß±Ì';
comment on column t_sys_user.id is 'ID';