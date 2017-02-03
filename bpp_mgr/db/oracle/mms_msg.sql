DROP TABLE mms_msg;
CREATE TABLE mms_msg (
	id number(20) NOT NULL,
	createTime date,
	lastUpdateTime date,
	title VARCHAR2(255),
	sendTime date NOT NULL,
	content VARCHAR2(1000) NOT NULL,
	stbId clob,
	areaId clob,
	custId clob,
	cronExpr VARCHAR2(100),
	validTime date NOT NULL,
	scope VARCHAR2(2) NOT NULL,
	vtype VARCHAR2(2) NOT NULL,
	status VARCHAR2(2) NOT NULL,
	sysUserId VARCHAR2(20) NOT NULL,
	sysRoleId VARCHAR2(20) NOT NULL,
	dataSrc VARCHAR2(2) NOT NULL
);
alter table mms_msg add constraint pk_mms_msg primary key (ID)
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
comment on table mms_msg is 'ÏûÏ¢±í';
comment on column mms_msg.id is 'ID';

create index indx_mms_msg_status on mms_msg (status)
  tablespace system
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 1M
    next 1M
    minextents 1
    maxextents unlimited
    pctincrease 0
  );
  
create index indx_mms_msg_datastat on mms_msg (dataSrc, status)
  tablespace system
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 1M
    next 1M
    minextents 1
    maxextents unlimited
    pctincrease 0
  );