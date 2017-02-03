DROP TABLE t_sys_role;
CREATE TABLE t_sys_role (
  id varchar2(255) NOT NULL,
  name varchar2(255),
  remark varchar2(255),
  roleType varchar2(255)
);
alter table t_sys_role add constraint pk_t_sys_role primary key (ID)
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
comment on table t_sys_role is '½ÇÉ«±í';
comment on column t_sys_role.id is 'ID';