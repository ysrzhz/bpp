DROP TABLE t_sys_organization;
CREATE TABLE t_sys_organization (
  id varchar2(255) NOT NULL,
  code varchar2(255),
  manager varchar2(1) NOT NULL,
  name varchar2(255),
  remark varchar2(255)
);
alter table t_sys_organization add constraint pk_t_sys_organization primary key (ID)
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
comment on table t_sys_organization is '×éÖ¯±í';
comment on column t_sys_organization.id is 'ID';