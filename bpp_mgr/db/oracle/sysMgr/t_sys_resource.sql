DROP TABLE t_sys_resource;
CREATE TABLE t_sys_resource (
  id varchar2(255) NOT NULL,
  action varchar2(255),
  appId varchar2(255),
  code varchar2(255),
  name varchar2(255),
  parentId varchar2(255),
  rank number(11) NOT NULL
);
alter table t_sys_resource add constraint pk_t_sys_resource primary key (ID)
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
comment on table t_sys_resource is '×ÊÔ´±í';
comment on column t_sys_resource.id is 'ID';