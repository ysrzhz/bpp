DROP TABLE t_sys_log;
CREATE TABLE t_sys_log (
  id number(11) NOT NULL,
  app number(11),
  content clob,
  ip varchar2(255),
  lev number(11),
  operationtime date,
  u_account varchar2(255),
  u_id varchar2(255),
  u_name varchar2(255)
);
alter table t_sys_log add constraint pk_t_sys_log primary key (ID)
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
comment on table t_sys_log is '»’÷æ±Ì';
comment on column t_sys_log.id is 'ID';