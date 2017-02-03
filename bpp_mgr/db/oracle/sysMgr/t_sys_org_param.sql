DROP TABLE t_sys_org_param;
CREATE TABLE t_sys_org_param (
  id varchar2(255) NOT NULL,
  name varchar2(255),
  orgId varchar2(255),
  remark varchar2(255),
  value varchar2(255)
);
alter table t_sys_org_param add constraint pk_t_sys_org_param primary key (ID)
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
comment on table t_sys_org_param is '组织参数表';
comment on column t_sys_org_param.id is 'ID';