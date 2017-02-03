DROP TABLE t_sys_relate;
CREATE TABLE t_sys_relate (
  id varchar2(255) NOT NULL,
  objectId varchar2(255),
  relatedId varchar2(255),
  typeId varchar2(255)
);
alter table t_sys_relate add constraint pk_t_sys_relate primary key (ID)
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
comment on table t_sys_relate is '¹ØÁª±í';
comment on column t_sys_relate.id is 'ID';
comment on column t_sys_relate.objectId is 'orgId/userId/roleId';
comment on column t_sys_relate.relatedId is 'roleId/roleId/resourceId';
comment on column t_sys_relate.typeId is 'orgRole×Ö·û´®/orgId/appId';