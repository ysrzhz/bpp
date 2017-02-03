DROP TABLE t_sys_application;
CREATE TABLE t_sys_application (
  id varchar2(255) NOT NULL,
  code varchar2(255),
  name varchar2(255)
);
alter table t_sys_application add constraint pk_t_sys_application primary key (ID)
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
comment on table t_sys_application is '”¶”√±Ì';
comment on column t_sys_application.id is 'ID';